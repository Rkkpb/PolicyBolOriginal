package fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import Adaptor.EmergencyAdapter;
import Adaptor.MyAdapter;
import bol.policy.com.policyboloriginal.R;
import fetchdata.RegisterResponse;
import fetchdata.UserManager;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/*My Policy Tab Motor Fragment*/
public class MotorFragment extends Fragment implements SearchView.OnQueryTextListener {
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    SearchView searchView;
    ArrayList<HashMap<String, String>> myDataSet = new ArrayList<>();
    ArrayList<HashMap<String, String>> org_data = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.motor_view, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.motor_list);
        searchView = (SearchView) view.findViewById(R.id.search_view);

        myDataSet = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setHasFixedSize(true);
        myAdapter = new MyAdapter(myDataSet);
        recyclerView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();

        searchView.setOnQueryTextListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        myDataSet.clear();
        myAdapter = new MyAdapter(myDataSet);
        recyclerView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("PolicyBol", MODE_PRIVATE);
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
                                    myDataSet.clear();
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
                                        myDataSet.add(hashMap);
                                        myAdapter.notifyDataSetChanged();
                                        System.out.println("===array size===" + myDataSet.size());
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
                        Log.e("PolicyBol.com", ex.getMessage());
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
                Toast.makeText(getContext(), messageBuffer.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        };
        call.enqueue(callback);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        query = query.toLowerCase();
        ArrayList<HashMap<String, String>> newList = new ArrayList<>();
        if (TextUtils.isEmpty(query)) {
            newList.clear();
            newList = myDataSet;
        } else {
            for (HashMap<String, String> entry : myDataSet) {
                if (entry.get("contactperson").toLowerCase(Locale.getDefault()).contains(query)) {
                    newList.add(entry);
                }
                if (entry.get("contactmob").toLowerCase(Locale.getDefault()).contains(query)) {
                    newList.add(entry);
                }
                if (entry.get("relation").toLowerCase(Locale.getDefault()).contains(query)) {
                    newList.add(entry);
                }
                if (entry.get("em_id").toLowerCase(Locale.getDefault()).contains(query)) {
                    newList.add(entry);
                }
            }
        }
        myAdapter.setFilter(newList);
        return true;
    }
}
