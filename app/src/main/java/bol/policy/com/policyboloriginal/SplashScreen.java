package bol.policy.com.policyboloriginal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import fetchdata.RegisterResponse;
import fetchdata.UserManager;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends AppCompatActivity implements Animation.AnimationListener {
    Animation animation;
    String cust_id, verifycode;
    String TAG_RETROFIT_GET_POST = "PolicyBol";

    @BindView(R.id.sp_anim)
    ImageView sp_icon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        ButterKnife.bind(this);
        SharedPreferences sharedPreferences = getSharedPreferences("PolicyBol", MODE_PRIVATE);
        cust_id = sharedPreferences.getString("custid", null);
        verifycode = sharedPreferences.getString("verifycode", null);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoomin);
        animation.setAnimationListener(this);
        sp_icon.startAnimation(animation);
    }

    private void callwebservice() {
        System.out.println("===customer id===" + cust_id);
        Map<String, String> fieldmap = new HashMap<>();
        fieldmap.put("custid", cust_id);
        fieldmap.put("verifycode", verifycode);
        Call<ResponseBody> call = UserManager.getUserManagerService(null).customerinfo(fieldmap);
        Callback<ResponseBody> callback = new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                StringBuffer messageBuffer = new StringBuffer();

                int statusCode = response.code();
                if (statusCode == 200) {
                    try {
                        // Get return string.
                        String returnBodyText = response.body().string();
                        System.out.println("===click result==" + returnBodyText);
                        TypeToken<RegisterResponse> typeToken = new TypeToken<RegisterResponse>() {
                        };
                        // Because return text is a json format string, so we should parse it manually.
                        Gson gson = new Gson();
                        // Get the response data list from JSON string.
                        RegisterResponse registerResponseList = gson.fromJson(returnBodyText, typeToken.getType());

                        if (registerResponseList != null) {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(returnBodyText.toString());
                                boolean result = jsonObject.getBoolean("result");
                                System.out.println("===clicked first===");
                                if (result == true) {
                                    System.out.println("===result===" + jsonObject.toString());
                                    String jsonObject1 = jsonObject.getString("response");
                                    SharedPreferences sharedPreferences = getSharedPreferences("PolicyBol", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("custinfo", jsonObject1);
                                    editor.commit();
                                    messageBuffer.append(response.message());
                                    jobfinish(result);
                                } else {
                                    jobfinish(result);
                                    messageBuffer.append(response.message());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("===clicked outer===");
                        }
                    } catch (IOException ex) {
                        Log.e(TAG_RETROFIT_GET_POST, ex.getMessage());
                    }
                } else {
                    // If server return error.
                    messageBuffer.append("Server return error code is ");
                    messageBuffer.append(statusCode);
                    messageBuffer.append("\r\n\r\n");
                    messageBuffer.append("Error message is ");
                    messageBuffer.append(response.message());
                }

                // Show a Toast message.
                Toast.makeText(getApplicationContext(), messageBuffer.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        };
        call.enqueue(callback);
    }

    private void jobfinish(boolean result) {
        if (cust_id != null) {
            if (result == true) {
                startActivity(new Intent(SplashScreen.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                } else {
                    finish();
                }
            } else {
                startActivity(new Intent(SplashScreen.this, Cust_First_Screen.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                } else {
                    finish();
                }
            }
        } else {
            startActivity(new Intent(SplashScreen.this, Cust_First_Screen.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (TextUtils.isEmpty(cust_id)) {
            startActivity(new Intent(SplashScreen.this, Cust_First_Screen.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else {
            callwebservice();
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
