package com.toastdemoapp.qrdemoapp.utilities;

import android.content.Context;

import  com.toastdemoapp.qrdemoapp.backend.models.User;
import  com.toastdemoapp.qrdemoapp.helpers.Logger;
import  com.toastdemoapp.qrdemoapp.views.UIEngine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

public class CachingManager {

    private Context context;
    protected static CachingManager self;

    public static CachingManager getInstance() {
        if (self == null) {
            self = new CachingManager();
        }
        return self;
    }

    private CachingManager() {
        context = BaseApplication.getContext();
    }

    /**
     * Check if  Object Cached And Not Expired
     *
     * @param expireInHours long
     * @param objectFile    File
     * @return boolean
     */
    protected boolean isObjectCachedAndNotExpired(long expireInHours, File objectFile) {
        boolean exist, expired = false;
        exist = objectFile.exists();

        if (exist) {
            Date now = new Date();
            Date expireDate = new Date(objectFile.lastModified() + expireInHours * 60 * 60 * 1000);

            if (now.after(expireDate)) {
                expired = true;
            }
        }
        return exist && !expired;
    }

    /**
     * Save Object
     *
     * @param object     Serializable
     * @param objectFile File
     * @throws IOException
     */
    public static void saveObject(Serializable object, File objectFile) throws IOException {
        if (!objectFile.exists()) {
            objectFile.createNewFile();
        }
        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(objectFile));
        outputStream.writeObject(object);
        objectFile.setLastModified(new Date().getTime());
        outputStream.close();

        objectFile.setLastModified(new Date().getTime());
    }

    /**
     * Load Object
     *
     * @param objectFile File
     * @return Serializable
     * @throws Exception
     */
    public static Serializable loadObject(File objectFile) throws Exception {
        Object cachedObject = null;
        if (objectFile.exists()) {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(objectFile));
            cachedObject = inputStream.readObject();
            inputStream.close();
            objectFile.setLastModified(new Date().getTime());
        }

        return (Serializable) cachedObject;
    }

    /**
     * Save App Configuration
     *
     * @param appConfig AppConfiguration
     */
    public void saveAppConfiguration(AppConfiguration appConfig) {
        File saveToFile = new File(Engine.DataFolder.APP_DATA, Engine.FileName.APP_CONFIGURATION);
        try {
            saveObject(appConfig, saveToFile);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Load App Configuration
     *
     * @return AppConfiguration
     */
    public AppConfiguration loadAppConfiguration() {
        AppConfiguration appConfig = null;
        try {
            appConfig = (AppConfiguration) loadObject(new File(Engine.DataFolder.APP_DATA, Engine.FileName.APP_CONFIGURATION));
        } catch (Throwable t) {
            Logger.instance().v("CachingManager", "loadAppConfiguration - failed to load cached app configuration" + t.getClass().getSimpleName(), false);
        }

        return appConfig;
    }

    /**
     * Delete contents of cache folder
     *
     * @param context Context
     */
    public void clearCachingFolder(Context context) {
        try {
            // Get sub-folders inside cache folder
            File cacheFolder = Engine.getCacheRootDir(context);
            File[] subFolders = Engine.getSubFolders(cacheFolder);
            if (subFolders != null && subFolders.length > 0) {
                for (File folder : subFolders) {
                    try {
                        Engine.deleteFileRecursive(folder);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * USER PART
     */
    public void saveUser(User user) {
        if (user == null)
            return;
        String itemFileName = Engine.FileName.APP_USER;
        File containerFolder = Engine.DataFolder.USER_DATA;
        saveObject(itemFileName, containerFolder, user, BaseApplication.getContext());

    }

    public User loadUser() {
        String itemFileName = Engine.FileName.APP_USER;
        File containerFolder = Engine.DataFolder.USER_DATA;
        Serializable object = loadObject(itemFileName, containerFolder, BaseApplication.getContext(), Engine.ExpiryInfo.NO_EXPIRY);
        return (User) object;
    }

    public void deleteUser() {
        Engine.deleteFileRecursive(Engine.DataFolder.USER_DATA);
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * TOKEN PART
     */


    /**
     * ---------------------------------------------------------------------------------------------
     * FCM
     */
    public String getDeviceId() {
        String deviceId = null;
        String itemFileName = Engine.FileName.FILE_DEVICE_TOKEN;
        File containerFolder = Engine.DataFolder.APP_DATA;
        Serializable object = loadObject(itemFileName, containerFolder, context, Engine.ExpiryInfo.NO_EXPIRY);
        deviceId = (String) object;
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        if (!Utilities.isNullString(deviceId)) {
            String itemFileName = Engine.FileName.FILE_DEVICE_TOKEN;
            File containerFolder = Engine.DataFolder.APP_DATA;
            saveObject(itemFileName, containerFolder, deviceId, context);

        }
    }


    /**
     * SAVE OBJECT
     * 1- Get language to cache the list in both languages
     * 2- Create file containing the list (if not exists)
     * 3- Create folder containing lists files (if not exists)
     * 4- Save list file in the folder
     *
     * @param listFileName    String
     * @param containerFolder File
     * @param object          Serializable
     * @param context         Context
     */
    private void saveObject(String listFileName, File containerFolder, Serializable object, Context context) {
        String language = UIEngine.getCurrentAppLanguage(context);

        if (!Utilities.isNullString(language))
            listFileName = listFileName + "_" + language.toUpperCase() + Engine.FileName.APP_FILES_EXT;

        File folder = Engine.getCacheFile(containerFolder, listFileName, context);

        try {
            saveObject(object, folder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * LOAD OBJECT
     * 1- Get language to get cached list of the current language
     * 2- Get the file containing the list
     * 3- Get the folder containing lists files
     * 4- Check if the file isn't expired yet
     * 5- load list from  list file which is in the folder
     *
     * @param listFileName    String
     * @param containerFolder File
     * @param context         Context
     * @param expireHours     int
     * @return Serializable
     */
    private Serializable loadObject(String listFileName, File containerFolder, Context context, int expireHours) {
        String language = UIEngine.getCurrentAppLanguage(context);

        if (language != null)
            listFileName = listFileName + "_" + language.toUpperCase() + Engine.FileName.APP_FILES_EXT;

        // Check that the file is already existing
        // If it's not exiting  don't continue
        if (!Engine.isExistingFile(containerFolder, listFileName)) {
            return null;
        }
        File folder = Engine.getCacheFile(containerFolder, listFileName, context);
        Serializable object = null;
        // If expire hours == 0 this means don't care expiry
        try {
            boolean isNotExpired;
            if (expireHours == Engine.ExpiryInfo.NO_EXPIRY) {
                isNotExpired = true;
            } else {
                isNotExpired = isObjectCachedAndNotExpired(expireHours, folder);
            }

            if (isNotExpired) {
                object = loadObject(folder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }
}
