package com.wogebi.android;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BaseWebActivity extends BaseActivity
{
    private View loading;
    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_web_base);
        initViews();
        initEvents();
    }

    @Override
    protected void initViews()
    {
        loading = findViewById(R.id.baseweb_loading);
        web = (WebView) findViewById(R.id.baseweb_web);
        web.getSettings().setJavaScriptEnabled(true);
    }

    @Override
    protected void initEvents()
    {
        web.setWebChromeClient(new WebChromeClient()
        {

            @Override
            public void onProgressChanged(WebView view, int newProgress)
            {
                super.onProgressChanged(view, newProgress);
            }

        });

        web.setWebViewClient(new WebViewClient()
        {
            private void showProgress()
            {
                loading.setVisibility(View.VISIBLE);
            }

            private void dismissProgress()
            {
                loading.setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                web.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, android.net.http.SslError error)
            {
                handler.proceed();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                super.onPageStarted(view, url, favicon);
                showProgress();
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                super.onPageFinished(view, url);
                dismissProgress();
            }
        });
    }
}
