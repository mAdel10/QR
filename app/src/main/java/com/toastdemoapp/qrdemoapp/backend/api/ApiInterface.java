package com.toastdemoapp.qrdemoapp.backend.api;

import com.toastdemoapp.qrdemoapp.backend.models.Qr;
import com.toastdemoapp.qrdemoapp.backend.models.QrForm;
import com.toastdemoapp.qrdemoapp.backend.models.SignForm;
import com.toastdemoapp.qrdemoapp.backend.models.StatusResult;
import com.toastdemoapp.qrdemoapp.helpers.Constants;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface ApiInterface {

    /**
     * ---------------------------------------------------------------------------------------------
     * -------------------------------------- USER -------------------------------------------------
     * ---------------------------------------------------------------------------------------------
     */

//    @POST(ApiClient.BASE_URL + Constants.SERVICES_PUSH_REFRESH_TOKEN)
//    Call<Token> doRefreshToken(@HeaderMap Map<String, String> headers,
//                               @Body Map<String, Object> params);

    @POST(ApiClient.BASE_URL + Constants.SERVICES_USER_SIGN_IN)
    Call<Object> doUserSignIn(@HeaderMap Map<String, String> headers,
                                    @Body SignForm signForm);

    @POST(ApiClient.BASE_URL + Constants.SERVICES_QR_SCANED)
    Call<Qr> doUserSignIn(@HeaderMap Map<String, String> headers,
                          @Body QrForm qrForm);


}

