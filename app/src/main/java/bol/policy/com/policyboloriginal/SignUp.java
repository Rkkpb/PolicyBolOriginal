package bol.policy.com.policyboloriginal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

public class SignUp extends AppCompatActivity implements View.OnClickListener {
    TextView title_signup;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        title_signup=(TextView)findViewById(R.id.title_signup);
        title_signup.setText(Html.fromHtml(getString(R.string.sign_up_title)));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                startActivity(new Intent(SignUp.this, LoginActivity.class));
                break;
        }
    }
}
