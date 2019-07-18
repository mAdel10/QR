package com.toastdemoapp.qrdemoapp.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.toastdemoapp.qrdemoapp.R;
import com.toastdemoapp.qrdemoapp.backend.models.SignForm;
import com.toastdemoapp.qrdemoapp.backend.models.User;
import com.toastdemoapp.qrdemoapp.backend.observers.CTHttpError;
import com.toastdemoapp.qrdemoapp.backend.observers.RequestObserver;
import com.toastdemoapp.qrdemoapp.backend.operations.UserSignInOperation;
import com.toastdemoapp.qrdemoapp.dialogs.ErrorDialog;
import com.toastdemoapp.qrdemoapp.managers.UserManager;
import com.toastdemoapp.qrdemoapp.utilities.InputValidator;
import com.toastdemoapp.qrdemoapp.utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginActivity extends BaseActivity
{
    @BindView(R.id.signIn_user_name_editText)
    EditText signInUserNameEditText;
    @BindView(R.id.signIn_password_editText)
    EditText signInPasswordEditText;
    @BindView(R.id.signIn_sign_in_button)
    Button signInSignInButton;

    private String username;
    private String password;

    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    String login_url = "http://newgizaqr.com/loginAPI.php?login";


    public LoginActivity() {
        super(R.layout.activity_login, false);
    }

    @Override
    protected void doOnCreate(Bundle bundle) {
        ButterKnife.bind(this);
    }

    @OnClick(R.id.signIn_sign_in_button)
    public void onViewClicked()
    {
        if (Utilities.isConnected(this))
        {
            getInputData();
        } else {
            Toast.makeText(this, "No Internet Connection !", Toast.LENGTH_SHORT).show();
        }

    }

    private void getInputData()
    {
        if (!InputValidator.loginValidation(this, signInUserNameEditText, signInPasswordEditText))
        {
            return;
        }

        username = signInUserNameEditText.getText().toString().trim();
        password = signInPasswordEditText.getText().toString().trim();

        login(username, password);
    }



    public static class MySingleton
    {
        private static MySingleton mInstance;
        private RequestQueue mRequestQueue;
        private static Context mCtx;

        private MySingleton(Context context)
        {
            mCtx = context;
            mRequestQueue = getRequestQueue();
        }

        public static synchronized MySingleton getInstance(Context context)
        {
            if (mInstance == null)
            {
                mInstance = new MySingleton(context);
            }
            return mInstance;
        }

        private RequestQueue getRequestQueue()
        {
            if (mRequestQueue == null)
            {
                // getApplicationContext() is key, it keeps you from leaking the
                // Activity or BroadcastReceiver if someone passes one in.
                mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
            }
            return mRequestQueue;
        }

        <T> void addToRequestQueue(Request<T> req)
        {
            getRequestQueue().add(req);
        }
    }

    private void login(String username, String password)
    {
        JSONObject request = new JSONObject();
        try
        {
            request.put(KEY_USERNAME, username);
            request.put(KEY_PASSWORD, password);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, login_url, request, new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try {

                            if (response.getBoolean("status"))
                            {
                                JSONObject  user = response.getJSONObject("User");
                                User currentUser = new User();
                                currentUser.setUserName(user.getString("username"));
                                UserManager.getInstance().saveUser(currentUser);
                                Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Wrong User Name or Password ", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }
}
