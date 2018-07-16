package bol.policy.com.policyboloriginal;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.skyfishjy.library.RippleBackground;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fetchdata.RegisterResponse;
import fetchdata.UserManager;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.READ_CONTACTS;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    ArrayList<HashMap<String, String>> contactlist = new ArrayList<>();
    RippleBackground rippleBackground;
    Vibrator vibrator;
    String TAG_RETROFIT_GET_POST = "PolicyBol";
    long[] mVibratePattern = new long[]{0, 400, 800, 600};
    String cust_id, verifycode;
    boolean soscheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        rippleBackground = (RippleBackground) findViewById(R.id.content);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        contactlist.clear();
        cust_info_service();
    }

    /*Call to collect User Information*/
    private void cust_info_service() {
        SharedPreferences sharedPreferences = getSharedPreferences("PolicyBol", MODE_PRIVATE);
        cust_id = sharedPreferences.getString("custid", null);
        verifycode = sharedPreferences.getString("verifycode", null);
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
                                    callwebservice();
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

    /* SoS Hit call */
    private void callsos() {
        if (soscheck) {
            SharedPreferences sharedPreferences = getSharedPreferences("PolicyBol", MODE_PRIVATE);
            String cust_id = sharedPreferences.getString("custid", null);
            Call<ResponseBody> call = UserManager.getUserManagerService(null).soshit(cust_id);
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
                                        System.out.println("===result===" + jsonObject.toString());
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
        } else {
            Toast.makeText(this, "No Sos Contact! Please Add..", Toast.LENGTH_SHORT).show();
            daiologopen();
        }
    }

    /*Dialog open if there is no SoS Contact*/
    private void daiologopen() {
        if (contactlist.isEmpty() || contactlist.size() < 1) {
            final Dialog dialog = new Dialog(MainActivity.this, R.style.Theme_Dialog);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.first_time_dialog);
            dialog.setCancelable(false);
            Button btn_addnow = (Button) dialog.findViewById(R.id.btn_addnow);
            btn_addnow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, Add_contact.class));
                }
            });
            Button btn_goback = (Button) dialog.findViewById(R.id.btn_goback);
            btn_goback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            dialog.show();
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_customer) {
            startActivity(new Intent(MainActivity.this, Customer_info.class));
        } else if (id == R.id.nav_policies) {
            startActivity(new Intent(MainActivity.this, MyPolicy.class));
        } else if (id == R.id.nav_emergency_contact) {
            startActivity(new Intent(MainActivity.this, Emergency_contact.class));
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_referus) {
            startActivity(new Intent(MainActivity.this, Refer_us.class));
        } else if (id == R.id.logout) {
            SharedPreferences sharedPreferences = getSharedPreferences("PolicyBol", MODE_PRIVATE);
            sharedPreferences.edit().clear().commit();
            startActivity(new Intent(MainActivity.this, Cust_First_Screen.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            Intent intent = new Intent(MainActivity.this, Cust_First_Screen.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            } else {
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cust_info:
                startActivity(new Intent(MainActivity.this, Customer_info.class));
                break;
            case R.id.btn_my_policy:
                startActivity(new Intent(MainActivity.this, MyPolicy.class));
                break;
            case R.id.btn_refer:
                startActivity(new Intent(MainActivity.this, Refer_us.class));
                break;
            case R.id.btn_co_dis:
                break;
            case R.id.centerImage:
                rippleBackground.startRippleAnimation();
                vibrator.vibrate(mVibratePattern, 2);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (vibrator.hasVibrator()) {
                            vibrator.cancel();
                        }
                        rippleBackground.stopRippleAnimation();
                    }
                }, 15000);
                callsos();
                break;
        }
    }

    /*Call this method to check SoS list*/
    private void callwebservice() {
        SharedPreferences sharedPreferences = getSharedPreferences("PolicyBol", MODE_PRIVATE);
        String cust_id = sharedPreferences.getString("custid", null);
        Call call = UserManager.getUserManagerService(null).getemergencycontact(cust_id);
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
                                    contactlist.clear();
                                    System.out.println("===result===" + jsonObject.toString());
                                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                                    System.out.println("===clicked===" + jsonArray.length());
                                    if (jsonArray.length() > 0) {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                            String em_id = jsonObject1.getString("em_id");
                                            String contactperson = jsonObject1.getString("contactperson");
                                            String contactmob = jsonObject1.getString("contactmob");
                                            String relation = jsonObject1.getString("relation");

                                            HashMap<String, String> hashMap = new HashMap<>();
                                            hashMap.clear();
                                            hashMap.put("em_id", em_id);
                                            hashMap.put("contactperson", contactperson);
                                            hashMap.put("contactmob", contactmob);
                                            hashMap.put("relation", relation);

                                            contactlist.add(hashMap);
                                        }
                                        messageBuffer.append(response.message());
                                        soscheck = true;
                                    } else {
                                        soscheck = false;
                                    }
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
}
