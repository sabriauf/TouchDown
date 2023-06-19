package lk.rc07.ten_years.touchdown.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.config.Constant;

public class LoginActivity extends AppCompatActivity {

    //constants
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final String PARAM_METHOD_NAME = "method";
    private static final String PARAM_USER_ID = "user_id";
    private static final String PARAM_USER_NAME = "user_name";
    private static final String PARAM_USER_EMAIL = "user_email";
    private static final String PARAM_USER_GENDER = "user_gender";
    private static final String PARAM_USER_BIRTHDAY = "user_birthday";
    private static final String PARAM_USER_TOWN = "user_town";
    private static final String LOGIN_METHOD = "addUsers";

    // Instances
    private SharedPreferences sharedPref;
//    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

//        this.callbackManager = CallbackManager.Factory.create();
        sharedPref = getSharedPreferences(Constant.MY_PREFERENCES, Context.MODE_PRIVATE);

//        initializeViews();
    }

//    private void initializeViews() {
//        LoginButton authButton = (LoginButton) findViewById(R.id.fb_login_button_fragment_login);
//        authButton.setReadPermissions(Arrays.asList(new String[]{"public_profile", "email"}));
//
//        authButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                getFacebookUserId();
//            }
//
//            @Override
//            public void onCancel() {
//                Toast.makeText(LoginActivity.this, "Authentication Cancel", Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Toast.makeText(LoginActivity.this, "Authentication Error : " + error.toString(), Toast.LENGTH_LONG).show();
//            }
//        });
//
//        setView();
//    }
//
//    private void getFacebookUserId() {
//        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//        final String userId = accessToken.getUserId();
//        final String GRAPH_PATH_FOR_USER = "/" + userId;
//        final GraphRequest requestGetEmail = new GraphRequest(AccessToken.getCurrentAccessToken(),
//                GRAPH_PATH_FOR_USER, null, HttpMethod.GET,
//                new GraphRequest.Callback() {
//                    @Override
//                    public void onCompleted(GraphResponse response) {
//                        try {
//
//                            Log.d(TAG, "Facebook Login API result : " + response.getJSONObject().toString());
//                            String name = response.getJSONObject().getString("first_name") + " " + response.getJSONObject().getString("last_name");
//                            SharedPreferences.Editor editor = sharedPref.edit();
//                            editor.putString(Constant.SHEARED_PREFEREANCE_KEY_USER_ID, userId);
//                            editor.putString(Constant.SHEARED_PREFEREANCE_KEY_USER_NAME, name);
//                            editor.apply();
//
//                            String birthday = "";
//                            if (response.getJSONObject().has("birthday"))
//                                birthday = response.getJSONObject().getString("birthday");
//
//                            String hometown = "";
//                            if (response.getJSONObject().has("hometown"))
//                                hometown = response.getJSONObject().getString("hometown");
//
//                            String gender = "";
//                            if (response.getJSONObject().has("gender"))
//                                gender = response.getJSONObject().getString("gender");
//
//                            String email = "";
//                            if(response.getJSONObject().has("email"))
//                                email = response.getJSONObject().getString("email");
//
//                            serverLogin(userId, name, email, birthday, gender, hometown);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//
//        Bundle param = new Bundle();
//        param.putString("fields", "first_name, last_name, email, birthday, gender, hometown");
//        requestGetEmail.setParameters(param);
//        requestGetEmail.executeAsync();
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        callbackManager.onActivityResult(requestCode, resultCode, data);
//    }
//
//    private void setView() {
//
//        ProfilePictureView profilePictureView = (ProfilePictureView) findViewById(R.id.image);
//        profilePictureView.setCropped(true);
//
//        TextView userNameView = (TextView) findViewById(R.id.txt_name);
//
//        if (AccessToken.getCurrentAccessToken() == null) {
//            SharedPreferences.Editor editor = sharedPref.edit();
//            editor.putString(Constant.SHEARED_PREFEREANCE_KEY_USER_ID, "");
//            editor.putString(Constant.SHEARED_PREFEREANCE_KEY_USER_NAME, "");
//            editor.apply();
//        } else {
//            profilePictureView.setProfileId(sharedPref.getString(Constant.SHEARED_PREFEREANCE_KEY_USER_ID, ""));
//            userNameView.setText(sharedPref.getString(Constant.SHEARED_PREFEREANCE_KEY_USER_NAME, ""));
//        }
//    }
//
//    private void serverLogin(String userId, String name, String email, String birthday, String gender, String hometown) {
//        SynchronizeData syncData = new SynchronizeData(this);
//        syncData.setOnDownloadListener(LoginActivity.class.getSimpleName(), new SynchronizeData.DownloadListener() {
//            @Override
//            public void onDownloadSuccess(final String response, DownloadMeta meta, int code) {
//                Log.d(LoginActivity.class.getSimpleName(), response);
//                setResult(Activity.RESULT_OK, null);
//                finish();
//            }
//
//            @Override
//            public void onDownloadFailed(String errorMessage, DownloadMeta meta, int code) {
//                Log.e(LoginActivity.class.getSimpleName(), String.format(Constant.SERVER_ERROR_MESSAGE, code, errorMessage));
//                LoginManager.getInstance().logOut();
//                setResult(Activity.RESULT_CANCELED, null);
//                finish();
//            }
//        });
//
//        HashMap<String, String> params = new HashMap<>();
//        params.put(PARAM_METHOD_NAME, LOGIN_METHOD);
//        params.put(PARAM_USER_ID, userId);
//        params.put(PARAM_USER_NAME, name);
//        params.put(PARAM_USER_EMAIL, email);
//        params.put(PARAM_USER_GENDER, gender);
//        params.put(PARAM_USER_BIRTHDAY, birthday);
//        params.put(PARAM_USER_TOWN, hometown);
//
//        DownloadMeta meta = new DownloadMeta();
//        meta.setUrl(BuildConfig.DEFAULT_URL + AppConfig.LOGIN_URL);
//        meta.setRequestMethod(DownloadManager.POST_REQUEST);
//        meta.setParams(params);
//
//        syncData.execute(meta);
//    }
}
