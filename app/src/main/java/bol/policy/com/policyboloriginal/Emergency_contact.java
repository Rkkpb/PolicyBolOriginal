package bol.policy.com.policyboloriginal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import Adaptor.EmergencyAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import fetchdata.RegisterResponse;
import fetchdata.UserManager;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*THis class shown list of all SoS Customers*/
public class Emergency_contact extends AppCompatActivity {
    @BindView(R.id.all_emerg_contact)
    RecyclerView all_contacts_list;

    private static EmergencyAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<HashMap<String, String>> emergency_list = new ArrayList<>();
    String TAG_RETROFIT_GET_POST = "PolicyBol";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_emergency);
        ButterKnife.bind(this);

        all_contacts_list.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        all_contacts_list.setLayoutManager(layoutManager);
//        all_contacts_list.setItemAnimator(new DefaultItemAnimator());

        emergency_list.clear();

        adapter = new EmergencyAdapter(emergency_list);
        all_contacts_list.setAdapter(adapter);
        SharedPreferences sharedPreferences = getSharedPreferences("PolicyBol", MODE_PRIVATE);
        String cust_id = sharedPreferences.getString("custid", null);
        callwebservice(cust_id);
    }

    private void callwebservice(String cust_id) {
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
                                    emergency_list.clear();
                                    System.out.println("===result===" + jsonObject.toString());
                                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                                    System.out.println("===clicked===" + jsonArray.length());
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

                                        emergency_list.add(hashMap);

                                        adapter.notifyDataSetChanged();
                                        System.out.println("===array size===" + emergency_list.size());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.emergency_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_member:
                startActivity(new Intent(Emergency_contact.this, Add_contact.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences sharedPreferences = getSharedPreferences("PolicyBol", MODE_PRIVATE);
        String cust_id = sharedPreferences.getString("custid", null);
        callwebservice(cust_id);
    }
}
