package jp.noifuji.antena.view.presenter;

import android.util.Log;

import org.jsoup.nodes.Document;

import jp.noifuji.antena.data.repository.EntrySourceRepositoryImpl;
import jp.noifuji.antena.domain.usecase.GetEntrySourceUseCase;
import jp.noifuji.antena.view.fragment.WebViewFragment;

/**
 * Created by ryoma on 2015/11/26.
 */
public class WebViewPresenter implements Presenter, GetEntrySourceUseCase.GetEntrySourceUseCaseListener{
    private static final String TAG = WebViewPresenter.class.getName();
    private static WebViewPresenter instance = new WebViewPresenter();
    private WebViewFragment mWebViewFragment;//@フラグメントが死んだときやばくね??
    private GetEntrySourceUseCase g;

    private WebViewPresenter() {}

    public static WebViewPresenter getInstance()  {
        return instance;
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }

    public  void setFragment(WebViewFragment webViewFragment) {
        this.mWebViewFragment = webViewFragment;
    }
    public void loadEntry(String url) {
        this.getEntrySource(url);
    }

    private void getEntrySource(String url) {
        //ヘッドラインのリストを取得する。
        //TODO リポジトリはPresenterの寿命に合わせるほうがよさそう
        g = new GetEntrySourceUseCase(this.mWebViewFragment.getActivity(), new EntrySourceRepositoryImpl(), url);
        g.addListener(this);
        g.execute(this.mWebViewFragment.getLoaderManager());
    }

    @Override
    public void onGetEntrySourceUseCaseError(String errorMessage) {
        Log.d(TAG, "");
    }

    @Override
    public void onGetEntrySourceUseCaseCompleted(Document entrySource) {
        mWebViewFragment.renderEntry(entrySource.toString());
    }
}
