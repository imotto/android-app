package net.imotto.imottoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.utils.Constants;

/**
 * Created by sunht on 2016/12/21.
 *
 */

public class ShowWebActivity extends BaseBackableActivity {

    private WebView webView;
    private SwipeRefreshLayout mRefresh;
    private String mUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        mRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        webView = (WebView) findViewById(R.id.web_view);

        Intent intent = getIntent();
        String title = intent.getStringExtra(Constants.BUNDLE_ACT_TITLE);
        mUrl = intent.getStringExtra(Constants.BUNDLE_URL);

        setupToolBar(title);

        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mRefresh.setRefreshing(false);
            }
        });

        mRefresh.setColorSchemeResources(R.color.colorPrimary);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                beginLoadUrl();
            }
        });

        mRefresh.post(new Runnable() {
            @Override
            public void run() {
                mRefresh.setRefreshing(true);
                beginLoadUrl();
            }
        });

    }

    private void beginLoadUrl(){
        webView.loadUrl(mUrl);
    }
}
