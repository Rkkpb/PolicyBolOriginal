package bol.policy.com.policyboloriginal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/* Existing Customer Information */
public class Customer_info extends AppCompatActivity {
    @BindView(R.id.customer_id)
    TextView cust_id;
    @BindView(R.id.c_name)
    TextView cust_name;
    @BindView(R.id.c_gender)
    TextView cust_gender;
    @BindView(R.id.c_dob)
    TextView cust_dob;
    @BindView(R.id.c_email)
    TextView cust_email;
    @BindView(R.id.c_mobile)
    TextView cust_mobile;
    @BindView(R.id.c_status)
    TextView cust_status;
    @BindView(R.id.c_address)
    TextView cust_address;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cust_info);
        ButterKnife.bind(this);
        setdata();
    }

    private void setdata() {
        SharedPreferences sharedPreferences = getSharedPreferences("PolicyBol", MODE_PRIVATE);
        String response = sharedPreferences.getString("custinfo", null);
        System.out.println("===customer info===" + response);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            String custid = jsonObject.getString("id");
            String custid_temp = jsonObject.getString("customerid");
            System.out.println("===customer===" + custid);
            cust_id.setText("" + custid_temp);
            try {
                String f_name = jsonObject.getString("firstname");
                String l_name = jsonObject.getString("lastname");
                String c_name = f_name.concat(l_name);
                cust_name.setText("" + c_name);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                String c_gender = jsonObject.getString("gender");
                cust_gender.setText("" + c_gender);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                String c_dob = jsonObject.getString("dob");
                cust_dob.setText("" + c_dob);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                String c_email = jsonObject.getString("emailaddress");
                cust_email.setText("" + c_email);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                String c_mobile = jsonObject.getString("mobileno");
                cust_mobile.setText("" + c_mobile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                String c_m_status = jsonObject.getString("maritalstatus");
                cust_status.setText("" + c_m_status);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                StringBuffer cust_Address = new StringBuffer();
                if (!jsonObject.isNull("housetype")) {
                    cust_Address.append(jsonObject.getString("housetype") + " ");
                }
                if (!jsonObject.isNull("houseno")) {
                    cust_Address.append(jsonObject.getString("houseno") + ",");
                }
                if (!jsonObject.isNull("streetname")) {
                    cust_Address.append(jsonObject.getString("streetname") + ",");
                }
                if (!jsonObject.isNull("city")) {
                    cust_Address.append(jsonObject.getString("city") + ",");
                }
                if (!jsonObject.isNull("state")) {
                    cust_Address.append(jsonObject.getString("state") + ",");
                }
                if (!jsonObject.isNull("country")) {
                    cust_Address.append(jsonObject.getString("country") + " ");
                }
                if (!jsonObject.isNull("pincode")) {
                    cust_Address.append(jsonObject.getString("pincode"));
                }
                cust_address.setText("" + cust_Address.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cust_info_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_profile:
                startActivity(new Intent(Customer_info.this, EditCustomerInfo.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
