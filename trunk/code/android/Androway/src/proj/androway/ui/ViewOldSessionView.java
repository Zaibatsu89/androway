package proj.androway.ui;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import proj.androway.R;
import proj.androway.common.Constants;
import proj.androway.main.ActivityBase;

/**
 * The ViewOldSessionView class is the web view for watching previous sessions on the mobile website
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public class ViewOldSessionView extends ActivityBase
{
    private WebView _webView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.view_old_session);
        
        _webView = (WebView) findViewById(R.id.webview);
        _webView.getSettings().setJavaScriptEnabled(true);
        _webView.getSettings().setBuiltInZoomControls(true);
        _webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        _webView.setWebViewClient(new MyWebViewClient());
        _webView.setWebChromeClient(new MyWebChromeClient());
        _webView.loadUrl(Constants.WEB_VIEW_URL);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && _webView.canGoBack())
        {
            _webView.goBack();
            return true;
        }
        
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Used for overriding the url loading
     */
    private class MyWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            view.loadUrl(url);
            return true;
        }
    }

    /**
     * Used to update the custom progress bar
     */
    private class MyWebChromeClient extends WebChromeClient
    {
        @Override
        public void onProgressChanged(WebView view, int progress)
        {
            ProgressBar pb = (ProgressBar)findViewById(R.id.main_progress_bar);
            pb.setProgress(progress);

            if(progress == 100)
                pb.setProgress(0);
        }
    }
}