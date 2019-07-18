package com.toastdemoapp.qrdemoapp.backend.api;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import  com.toastdemoapp.qrdemoapp.helpers.Constants;
import  com.toastdemoapp.qrdemoapp.helpers.Logger;
import  com.toastdemoapp.qrdemoapp.utilities.BaseApplication;
import  com.toastdemoapp.qrdemoapp.utilities.Utilities;

import java.io.IOException;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String BASE_URL = Constants.BASE_URL;
    private static Retrofit retrofit = null;

    private static final long CACHE_SIZE = 20 * 1024 * 1024; // 10 MB
    private static OkHttpClient.Builder clientBuilder;

    /**
     * Get Default API Headers
     *
     * @return HashMap<String>
     */
    public static HashMap<String, String> getDefaultHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        headers.put("locale", Utilities.getLanguage()); // en: English, ar: Arabic
        headers.put("time-zone", TimeZone.getDefault().getID());

        return headers;
    }

    static {
        clientBuilder = new OkHttpClient
                .Builder()
                .connectTimeout(40, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS)
                .cache(new Cache(BaseApplication.getContext().getCacheDir(), CACHE_SIZE)) // 10 MB

                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {
                        Request request = chain.request();
                        okhttp3.Headers headers = request.headers();

                        Logger.instance().v("Request URL", request.url());
                        Logger.instance().v("Request Headers", (headers != null) ? headers.toString() : "Null headers");
                        Logger.instance().v("Request Body", (request.body() != null) ? bodyToString(request.body()) : "NULL/Empty");

                        if (Utilities.isConnected(BaseApplication.getApplication())) {
                            request = request.newBuilder().header("Cache-Control", "public, max-age=" + 50000).build();
                        } else {
                            request = request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build();
                        }
                        return chain.proceed(request);
                    }
                });
    }

    private static String bodyToString(final RequestBody request) {
        try {
            Buffer buffer = new Buffer();
            request.writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "BodyToString: " + e.getMessage();
        }
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            retrofit = new Retrofit.Builder()
                    .addConverterFactory(LenientGsonConverterFactory.create(gson))
                    .baseUrl(BASE_URL)
                    .client(clientBuilder.build())
                    .build();
        }
        return retrofit;
    }
}
