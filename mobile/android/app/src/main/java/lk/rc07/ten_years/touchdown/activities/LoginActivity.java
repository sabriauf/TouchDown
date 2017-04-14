package lk.rc07.ten_years.touchdown.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;

import java.util.Arrays;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.config.Constant;

public class LoginActivity extends AppCompatActivity {

    // Instance
    private SharedPreferences sharedPref;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        this.callbackManager = CallbackManager.Factory.create();
        sharedPref = getSharedPreferences(Constant.MY_PREFERENCES, Context.MODE_PRIVATE);

        initializeViews();
    }

    private void initializeViews() {
        LoginButton authButton = (LoginButton) findViewById(R.id.fb_login_button_fragment_login);
        authButton.setReadPermissions(Arrays.asList(new String[]{"public_profile"}));

        authButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getFacebookUserId();
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "Authentication Cancel", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, "Authentication Error", Toast.LENGTH_LONG).show();
            }
        });

        setView();
    }

    private void getFacebookUserId() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        final String userId = accessToken.getUserId();
        final String GRAPH_PATH_FOR_USER = "/" + userId;
        final GraphRequest requestGetEmail = new GraphRequest(AccessToken.getCurrentAccessToken(),
                GRAPH_PATH_FOR_USER, null, HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        try {
                            String name = response.getJSONObject().getString("first_name") + " " + response.getJSONObject().getString("last_name");
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString(Constant.SHEARED_PREFEREANCE_KEY_USER_ID, userId);
                            editor.putString(Constant.SHEARED_PREFEREANCE_KEY_USER_NAME, name);
                            editor.apply();
                            setResult(Activity.RESULT_OK, null);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle param = new Bundle();
        param.putString("fields", "first_name, last_name, email, age_range");
        requestGetEmail.setParameters(param);
        requestGetEmail.executeAsync();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void setView() {

        ProfilePictureView profilePictureView = (ProfilePictureView) findViewById(R.id.image);
        profilePictureView.setCropped(true);

        TextView userNameView = (TextView) findViewById(R.id.txt_name);

        if (AccessToken.getCurrentAccessToken() == null) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Constant.SHEARED_PREFEREANCE_KEY_USER_ID, "");
            editor.putString(Constant.SHEARED_PREFEREANCE_KEY_USER_NAME, "");
            editor.apply();
        } else {
            profilePictureView.setProfileId(sharedPref.getString(Constant.SHEARED_PREFEREANCE_KEY_USER_ID, ""));
            userNameView.setText(sharedPref.getString(Constant.SHEARED_PREFEREANCE_KEY_USER_NAME, ""));
        }
    }
}
