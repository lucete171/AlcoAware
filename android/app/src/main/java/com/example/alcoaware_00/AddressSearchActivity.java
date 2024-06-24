package com.example.alcoaware_00;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class AddressSearchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_search);

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true); // 수정된 부분
        webView.addJavascriptInterface(new BridgeInterface(), "Android");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Android javascript 함수 호출
                webView.loadUrl("javascript:sample2_execDaumPostcode();");
            }
        });
        // 최초 웹뷰 호출
        webView.loadUrl("https://alcoaware-cfbcb.web.app/");
    }

    private class BridgeInterface {
        @JavascriptInterface
        public void processDATA(String data){
            // API 결과값이 브릿지 통로를 통해 전달받는다
            Intent intent = new Intent();
            intent.putExtra("data",data);
            setResult(RESULT_OK, intent);
            finish(); // 수정된 부분
        }
    }
}
