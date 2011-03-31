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
 *
 * @author Tymen
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

    private class MyWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            view.loadUrl(url);
            return true;
        }
    }

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