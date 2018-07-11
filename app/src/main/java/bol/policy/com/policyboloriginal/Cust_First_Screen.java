package bol.policy.com.policyboloriginal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Cust_First_Screen extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_screen);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.existing_cust:
                startActivity(new Intent(getApplicationContext(), Register_Mobile.class));
                break;
        }
    }
}
