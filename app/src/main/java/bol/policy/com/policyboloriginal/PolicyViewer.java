package bol.policy.com.policyboloriginal;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/*To Show Policy in Pdf*/
public class PolicyViewer extends AppCompatActivity {
    WebView mWebview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.policyview);
        mWebview = new WebView(this);
        mWebview.getSettings().setJavaScriptEnabled(true);

        final Activity viewer = this;
        mWebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                viewer.setTitle("Loading...");
                viewer.setProgress(newProgress * 100);
                if (newProgress == 100)
                    viewer.setTitle(R.string.app_name);
            }
        });
        mWebview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(viewer, description, Toast.LENGTH_SHORT).show();
            }
        });
        String myPdfUrl = "http://45.35.62.217/crm/uploads/Covernote/CoverNote18/Voucher-15304592421.pdf";
        String url = "http://docs.google.com/gview?embedded=true&url=" + myPdfUrl;
        mWebview.loadUrl(url);
        setContentView(mWebview);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.webview_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_share:
                opendialog();
                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return true;
    }

    private void opendialog() {
        final Dialog dialog = new Dialog(PolicyViewer.this, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.share_view);
        Button btn_email = (Button) dialog.findViewById(R.id.btn_email);
        btn_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PolicyViewer.this, "Via Email", Toast.LENGTH_SHORT).show();
                opensharedialog(true);
                dialog.dismiss();
            }
        });
        Button btn_sms = (Button) dialog.findViewById(R.id.btn_sms);
        btn_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PolicyViewer.this, "Via Sms", Toast.LENGTH_SHORT).show();
                opensharedialog(false);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void opensharedialog(final boolean selection) {
        final Dialog dialog = new Dialog(PolicyViewer.this, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.share_dialog);
        TextView textView2 = (TextView) dialog.findViewById(R.id.title_view);
        final TextInputEditText enter_text = (TextInputEditText) dialog.findViewById(R.id.enter_text);
        if (selection) {
            textView2.setText(getString(R.string.enter_email));
            enter_text.setHint(getString(R.string.enter_email));
            enter_text.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        } else {
            textView2.setText(getString(R.string.enter_mobile_no));
            enter_text.setHint(getString(R.string.enter_mobile_no));
            enter_text.setInputType(InputType.TYPE_CLASS_PHONE);
            enter_text.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        }
        Button btn_share = (Button) dialog.findViewById(R.id.btn_share);
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String en_otp = enter_text.getText().toString();
                if (TextUtils.isEmpty(en_otp) || en_otp.length() == 0) {
                    if (selection) {
                        Toast.makeText(PolicyViewer.this, "Please enter Email", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PolicyViewer.this, "Please enter Mobile Number", Toast.LENGTH_SHORT).show();
                    }
                } else {
//                    callwebservice(true, en_otp);
                    dialog.dismiss();
                }

            }
        });

        dialog.show();
    }
}
