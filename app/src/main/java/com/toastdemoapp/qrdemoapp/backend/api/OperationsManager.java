package com.toastdemoapp.qrdemoapp.backend.api;

import android.util.Log;

import  com.toastdemoapp.qrdemoapp.R;
import com.toastdemoapp.qrdemoapp.backend.models.Qr;
import com.toastdemoapp.qrdemoapp.backend.models.QrForm;
import com.toastdemoapp.qrdemoapp.backend.models.SignForm;
import com.toastdemoapp.qrdemoapp.backend.models.StatusResult;
import  com.toastdemoapp.qrdemoapp.backend.observers.CTHttpError;
import  com.toastdemoapp.qrdemoapp.utilities.BaseApplication;
import  com.toastdemoapp.qrdemoapp.utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class OperationsManager {

    private static final String TAG = "OperationsManager";
    private static OperationsManager _instance = null;

    public static OperationsManager getInstance() {
        if (_instance == null)
            _instance = new OperationsManager();
        return _instance;
    }

//    /**
//     * This method to get the new token after old is expired.
//     *
//     * @param refreshToken String
//     * @return the new token
//     */
//    public Token doRefreshToken(String refreshToken) throws IOException {
//        Log.v(TAG, "doRefreshToken");
//        Map<String, Object> data = new HashMap<>();
//        data.put("refreshtoken", refreshToken);
//        HashMap<String, String> headers = ApiClient.getDefaultHeaders();
//        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
//        Call<Token> call = apiService.doRefreshToken(headers, data);
//        Response<Token> response = call.execute();
//
//        ensureHttpSuccess(response);
//
//        return response.body();
//    }

    public StatusResult doUserSignIn(SignForm signForm) throws IOException {
        Log.v(TAG, "doUserSignIn");
        HashMap<String, String> headers = ApiClient.getDefaultHeaders();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Object> call = apiService.doUserSignIn(headers, signForm);
        Response<Object> response = call.execute();

        ensureHttpSuccess(response);
        Object object = response.body();
        StatusResult statusResult = (StatusResult) object;

        return statusResult;
    }

    public Qr doScanQr(QrForm qrForm) throws IOException {
        Log.v(TAG, "doUserSignIn");
        HashMap<String, String> headers = ApiClient.getDefaultHeaders();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Qr> call = apiService.doUserSignIn(headers, qrForm);
        Response<Qr> response = call.execute();

        ensureHttpSuccess(response);

        return response.body();
    }
    /**
     * Ensure http request has success
     *
     * @param response of the api
     * @throws IOException if an error found, then throw an exception with the error, and the above layer (Operation) will catch it.
     */
    private void ensureHttpSuccess(Response response) throws IOException {
        if (!response.isSuccessful() && response.errorBody() != null) {
            ResponseBody errorBody = response.errorBody();
            // assert errorBody != null;
            String errorMSG = errorBody.string();
            int code = response.code();
            if (code == 504 && Utilities.isNullString(errorMSG)) // Request timeout
                errorMSG = BaseApplication.getContext().getString(R.string.request_error);
            else if (!Utilities.isNullString(errorMSG) && errorMSG.trim().startsWith("{")
                    && errorMSG.trim().endsWith("}")) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(errorMSG);
                    errorMSG = jsonObject.getString("message");
                    if (jsonObject.has("code"))
                        code = jsonObject.optInt("code");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            throw new CTHttpError(errorMSG, code);
        }
    }
}
