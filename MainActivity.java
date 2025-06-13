package com.chitechma;

import android.app.Activity; 
import android.app.AlertDialog; 
import android.content.DialogInterface;
import android.content.Intent; 
import android.graphics.Color;
import android.net.Uri; 
import android.os.Build;
import android.os.Bundle; 
import android.os.Handler; 
import android.view.LayoutInflater;
import android.view.View; 
import android.view.animation.Animation;
import android.view.animation.AnimationUtils; 
import android.webkit.JavascriptInterface; 
import android.webkit.ValueCallback; 
import android.webkit.WebChromeClient;
import android.webkit.WebView; 
import android.webkit.WebViewClient; 
import android.webkit.WebSettings; 
import android.widget.Button;
import android.widget.Toast;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
public class MainActivity extends Activity {

private WebView webView;  
private ValueCallback<Uri[]> filePathCallback;  
private static final int FILE_CHOOSER_REQUEST_CODE = 100;  
private boolean doubleBackToExitPressedOnce = false;  

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    webView = (WebView) findViewById(R.id.webView);
    WebSettings webSettings = webView.getSettings();
    webSettings.setJavaScriptEnabled(true);
    webSettings.setAllowFileAccess(true);
    webSettings.setDomStorageEnabled(true);
    webSettings.setLoadWithOverviewMode(true);
    webSettings.setUseWideViewPort(true);

    webView.addJavascriptInterface(new WebAppInterface(), "Android");
    webView.setWebViewClient(new CustomWebViewClient());
    webView.setWebChromeClient(new WebChromeClient() {
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            MainActivity.this.filePathCallback = filePathCallback;
            Intent intent = fileChooserParams.createIntent();
            try {
                startActivityForResult(intent, FILE_CHOOSER_REQUEST_CODE);
            } catch (Exception e) {
                filePathCallback = null;
                return false;
            }
            return true;
        }
    });

    webView.loadUrl("file:///android_asset/index.html");
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == FILE_CHOOSER_REQUEST_CODE && filePathCallback != null) {
        filePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
        filePathCallback = null;
    }
}

@Override
public void onBackPressed() {
    if (webView.canGoBack()) {
        webView.goBack();
    } else if (doubleBackToExitPressedOnce) {
        super.onBackPressed();
    } else {
        doubleBackToExitPressedOnce = true;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

        showExitDialog();  
    }  
}  

private void showExitDialog() {  
    LayoutInflater inflater = LayoutInflater.from(this);  
    View dialogView = inflater.inflate(R.layout.dialog_exit, null);  

    Button btnYes = (Button) dialogView.findViewById(R.id.btn_yes);  
    Button btnNo = (Button) dialogView.findViewById(R.id.btn_no);  

    final AlertDialog dialog = new AlertDialog.Builder(this)  
            .setView(dialogView)  
            .setCancelable(false)  
            .create();  

    Animation dialogAnimation = AnimationUtils.loadAnimation(this, R.anim.dialog_slide_in);  
    dialogView.startAnimation(dialogAnimation);  

    btnYes.setOnClickListener(new View.OnClickListener() {  
        @Override  
        public void onClick(View v) {  
            dialog.dismiss();  
            MainActivity.super.onBackPressed();  
        }  
    });  

    btnNo.setOnClickListener(new View.OnClickListener() {  
        @Override  
        public void onClick(View v) {  
            dialog.dismiss();  
        }  
    });  

    dialog.show();  
}  

private class CustomWebViewClient extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            // Let WhatsApp links open externally
            if (url.contains("wa.me") || url.contains("api.whatsapp.com")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                view.getContext().startActivity(intent);
                return true;
            }

            // Load all other HTTP/HTTPS links inside WebView
            view.loadUrl(url);
            return true;
        } else if (url.startsWith("tel:") || url.startsWith("mailto:") || url.startsWith("sms:")) {
            // Handle other intents externally
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            view.getContext().startActivity(intent);
            return true;
        }
        return false;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        view.loadUrl("javascript:(function() { " +
                "var metaTag = document.querySelector('meta[name=\"theme-color\"]');" +
                "if (metaTag) {" +
                "    var color = metaTag.getAttribute('content');" +
                "    if (color) {" +
                "        window.Android.updateStatusBarColor(color);" +
                "    }" +
                "}" +
                "})();");
    }

    // For older Android versions (API < 21)
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        // Show the custom error page
        view.loadUrl("file:///android_asset/error.html");
    }

    // For newer Android versions (API >= 21)
    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
        // Show the custom error page for HTTP errors
        view.loadUrl("file:///android_asset/error.html");
    }
}

public class WebAppInterface {
    @JavascriptInterface
    public void updateStatusBarColor(String colorString) {
        try {
            final int color = Color.parseColor(colorString);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getWindow().setStatusBarColor(color);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

}

