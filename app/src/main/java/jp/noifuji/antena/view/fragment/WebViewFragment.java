package jp.noifuji.antena.view.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.noifuji.antena.R;
import jp.noifuji.antena.data.entity.HtmlHistory;
import jp.noifuji.antena.data.entity.HtmlPage;
import jp.noifuji.antena.util.Utils;
import jp.noifuji.antena.view.presenter.WebViewPresenter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WebViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class WebViewFragment extends Fragment {
    private static final String TAG = "WebViewFragment";
    private OnFragmentInteractionListener mListener;
    private HtmlHistory mHtmlPageStack;
    private boolean isGoBack = false;
    private HtmlPage mCurrentHtmlPage;

    private WebViewPresenter mWebViewPresenter;

    @Bind(R.id.webView)
    WebView webView;
    @Bind(R.id.progress_view)
    View mProgressBar;
    @Bind(R.id.back_button)
    FloatingActionButton mBackButton;

    public WebViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        mHtmlPageStack = new HtmlHistory();

        mWebViewPresenter = WebViewPresenter.getInstance();
        mWebViewPresenter.setFragment(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_web_view, container, false);
        ButterKnife.bind(this, view);

        //ズーム可
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mHtmlPageStack == null) {
                    return;
                }
                WebViewFragment.this.mProgressBar.setVisibility(View.GONE);
                if (isGoBack) {
                    //バックキーが押されている場合、ヒストリーからポップする。
                    isGoBack = false;
                    mCurrentHtmlPage = mHtmlPageStack.pop();
                    //時間をずらしてスクロールさせないと動かない
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "Load position X:" + mCurrentHtmlPage.getmScrollX() + ", Y:" + mCurrentHtmlPage.getmScrollY());
                            webView.setScrollX(0);
                            webView.setScrollY(mCurrentHtmlPage.getmScrollY());
                        }
                        // Delay the scrollTo to make it work
                    }, 100);
                }

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "Clicked URL =  " + url);
                mCurrentHtmlPage.setmScrollX(webView.getScrollX());
                mCurrentHtmlPage.setmScrollY(webView.getScrollY());
                mHtmlPageStack.add(mCurrentHtmlPage);

                if (Utils.isPictureUrl(url)) {
                    //画像URLであれば、htmlをパースせずにそのまま表示する。
                    webView.loadUrl(url);
                } else {
                    mWebViewPresenter.loadEntry(url);
                }
                mProgressBar.setVisibility(View.VISIBLE);
                return true;
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHtmlPageStack = null;
                mListener.onFinishActivity();
            }
        });

        mProgressBar.setVisibility(View.VISIBLE);
        Intent intent = this.getActivity().getIntent();
        //フラグメントが再生成された場合には、前回表示していたHTMLが残っているため、それを表示する。
        if (mCurrentHtmlPage == null) {
            mWebViewPresenter.loadEntry(intent.getStringExtra("URI"));
        } else {
            webView.loadDataWithBaseURL(null, mCurrentHtmlPage.getmHtml(), "text/html; charset=utf-8", "UTF-8", null);
        }

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach");
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach()");
        //Activityに渡していたWevViewへの参照を消しておく。
        mListener = null;
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView()");
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public void onBackPressed(Activity activity) {
        Log.d(TAG, "onBackPressed mHtmlPageStack = " + mHtmlPageStack.size());
        if (mHtmlPageStack.size() > 0) {
            this.isGoBack = true;
            //前ページをロードする
            webView.loadDataWithBaseURL(null, mHtmlPageStack.getLatestHistory().getmHtml(), "text/html; charset=utf-8", "UTF-8", null);
        } else {
            //ヒストリがなくなったら、リスト画面に戻る
            mHtmlPageStack = null;
            activity.finish();
        }
    }

    public void renderEntry(String html) {
        //TODO 実装する
        Log.d(TAG, "Render Entry.");
        webView.loadDataWithBaseURL(null, html, "text/html; charset=utf-8", "UTF-8", null);//webviewのhistoryにためない
        mCurrentHtmlPage = new HtmlPage(html);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onShowTextMessage(String message);
        void onFinishActivity();
    }

}
