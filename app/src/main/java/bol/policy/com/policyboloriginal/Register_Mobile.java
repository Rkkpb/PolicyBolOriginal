package bol.policy.com.policyboloriginal;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fetchdata.RegisterResponse;
import fetchdata.UserManager;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*For Existing Regtister Mobile*/
public class Register_Mobile extends AppCompatActivity implements View.OnClickListener {
    TextView reg_mob_title;
    TextInputEditText mobile_text;
    String TAG_RETROFIT_GET_POST = "PolicyBol";
    String otp_key, mobileno;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_mobile);
        reg_mob_title = (TextView) findViewById(R.id.title_mobile);
        mobile_text = (TextInputEditText) findViewById(R.id.mobile_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            reg_mob_title.setText(Html.fromHtml(getString(R.string.register_data), Html.FROM_HTML_MODE_COMPACT));
        } else {
            reg_mob_title.setText(Html.fromHtml(getString(R.string.register_data)));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                callservice();
                break;
        }
    }

    private void callservice() {
        {
            mobileno = "";
            mobileno = mobile_text.getText().toString();
            if (TextUtils.isEmpty(mobileno) || mobileno.length() == 0) {
                Toast.makeText(this, "Please enter mobile no", Toast.LENGTH_SHORT).show();
            } else {
                callwebservice(false, mobileno);
            }
        }
    }

    private void callwebservice(final boolean press_key, String mobileno) {
        Call<ResponseBody> call;
        if (press_key == false) {
            call = UserManager.getUserManagerService(null).registerUser(this.mobileno);
        } else {
            System.out.println("===otp===" + this.mobileno + "====" + mobileno);
            Map<String, String> fieldmap = new HashMap<>();
            fieldmap.put("mobileno", this.mobileno);
            fieldmap.put("otp", mobileno);
            call = UserManager.getUserManagerService(null).registermobileno(fieldmap);
        }

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
                                    System.out.println("===clicked===");
                                    if (press_key == false) {
                                        otp_key = jsonObject.getString("otp");
                                        opendialog();
                                    } else {
                                        System.out.println("===result===" + jsonObject.toString());
                                        JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                                        String cust_id = jsonObject1.getString("cust_id");
                                        String verifycode = jsonObject1.getString("verfiycode");
                                        SharedPreferences sharedPreferences = getSharedPreferences("PolicyBol", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("custid", cust_id);
                                        editor.putString("verifycode", verifycode);
                                        editor.commit();
                                        startActivity(new Intent(Register_Mobile.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                            finishAffinity();
                                        } else {
                                            finish();
                                        }
                                    }
                                    messageBuffer.append(response.message());
                                } else {
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

    /*Dialog for OTP Verification*/
    private void opendialog() {
        final Dialog dialog = new Dialog(Register_Mobile.this, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.otp_dialog);
        final TextInputEditText otp_filed = (TextInputEditText) dialog.findViewById(R.id.otp_text);
        Button btn_submit = (Button) dialog.findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String en_otp = otp_filed.getText().toString();
                if (TextUtils.isEmpty(en_otp) || en_otp.length() == 0) {
                    Toast.makeText(Register_Mobile.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                } else {
                    callwebservice(true, en_otp);
                }
                dialog.dismiss();
            }
        });
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        dialog.show();
    }
}