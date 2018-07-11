package bol.policy.com.policyboloriginal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

import static android.Manifest.permission.READ_CONTACTS;

public class Refer_us extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    LinearLayout mSnackbar;
    @BindView(R.id.contactname)
    EditText mContactname;
    @BindView(R.id.email_id)
    TextInputEditText email_id;

    @BindView(R.id.contactno)
    EditText mContactno;
    @BindView(R.id.c_relation)
    Spinner cust_relation;
    @BindView(R.id.checkid)
    CheckBox checkid;
    String[] relation = {"Father", "Mother", "Brother", "Sister", "Son", "Daughter", "Friend", "Relative"};
    String TAG_RETROFIT_GET_POST = "PolicyBol.com";
    String cust_id, sel_relation, emailid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refer_us);
        ButterKnife.bind(this);
        mSnackbar = (LinearLayout) findViewById(R.id.main_layout);
        SharedPreferences sharedPreferences = getSharedPreferences("PolicyBol", MODE_PRIVATE);
        cust_id = sharedPreferences.getString("custid", null);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, relation);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cust_relation.setAdapter(arrayAdapter);
        cust_relation.setOnItemSelectedListener(this);
        populateAutoComplete();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.phonebook:
                Intent getcontact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(getcontact, 7);
                break;
            case R.id.btn_submit:
                if (checkid.isChecked()) {
                    Toast.makeText(this, "Ok", Toast.LENGTH_SHORT).show();
                    String contactname = mContactname.getText().toString();
                    String contactno = mContactno.getText().toString();
                    emailid = email_id.getText().toString();
                    if (TextUtils.isEmpty(contactname) || contactname.length() == 0) {
                        Toast.makeText(this, "Please enter Contact name", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(contactno) || contactno.length() == 0) {
                        Toast.makeText(this, "Please enter Contact number", Toast.LENGTH_SHORT).show();
                    } else {
                        callwebservice(contactname, contactno);
                    }
                } else {
                    Toast.makeText(this, "Please Accept Terms & Conditions", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mSnackbar, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    }).show();
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    @Override
    public void onActivityResult(int RequestCode, int ResultCode, Intent ResultIntent) {

        super.onActivityResult(RequestCode, ResultCode, ResultIntent);

        switch (RequestCode) {

            case (7):
                if (ResultCode == Activity.RESULT_OK) {

                    Uri uri;
                    Cursor cursor1, cursor2;
                    String TempNameHolder, TempNumberHolder, TempContactID, IDresult = "";
                    int IDresultHolder;

                    uri = ResultIntent.getData();

                    cursor1 = getContentResolver().query(uri, null, null, null, null);

                    if (cursor1.moveToFirst()) {

                        TempNameHolder = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                        TempContactID = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts._ID));

                        IDresult = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        IDresultHolder = Integer.valueOf(IDresult);

                        if (IDresultHolder == 1) {

                            cursor2 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + TempContactID, null, null);

                            while (cursor2.moveToNext()) {

                                TempNumberHolder = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                System.out.println("===Refer name===" + TempNameHolder + "====" + TempNumberHolder);
                                mContactname.setText("" + TempNameHolder);
                                mContactno.setText("" + TempNumberHolder);

                            }
                        }

                    }
                }
                break;
        }
    }

    private void callwebservice(String contactname, String contactno) {
        System.out.println("===customer id===" + contactname);
        Map<String, String> fieldmap = new HashMap<>();
        fieldmap.put("custid", cust_id);
        fieldmap.put("customername", contactname);
        fieldmap.put("mobile", contactno);
        fieldmap.put("relation", sel_relation);
        if (TextUtils.isEmpty(emailid)) {
        } else {
            fieldmap.put("email", emailid);
        }
        Call<ResponseBody> call = UserManager.getUserManagerService(null).referus(fieldmap);
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
                                    Toast.makeText(Refer_us.this, "Success", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
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
                    } catch (Exception e) {
                        Toast.makeText(Refer_us.this, "Connection Error", Toast.LENGTH_SHORT).show();
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
        sel_relation = relation[pos];
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
