package jp.noifuji.antena.domain.usecase;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import jp.noifuji.antena.constants.ErrorMessage;
import jp.noifuji.antena.data.repository.EntrySourceRepository;
import jp.noifuji.antena.exception.AntenaSystemException;

/**
 * ロジックを書く<br>
 * 指定されたアドレスからHTMLソースを取得する。<br>
 * 取得したHTMLから不要なタグを整理する。<br>
 */
public class GetEntrySourceUseCase extends AsyncTaskLoader<AsyncResult<Document>> implements LoaderManager.LoaderCallbacks<AsyncResult<Document>> {
    private static final String TAG = "GetEntrySourceUseCase";
    private static final int LOADER_ID = 2;
    private String mUrl;
    private Loader mLoader;
    private GetEntrySourceUseCaseListener mUseCaseListener;
    private EntrySourceRepository mEntrySourceRepository;

    public GetEntrySourceUseCase(Context context,EntrySourceRepository entrySourceRepository,  String url) {
        super(context);
        this.mUrl = url;
        this.mEntrySourceRepository = entrySourceRepository;
    }

    @Override
    public AsyncResult<Document> loadInBackground() {
        AsyncResult<Document> result = new AsyncResult<>();
        Document doc = null;

        try {
            Log.e(TAG, "url is " + this.mUrl);
            doc = Jsoup.connect(this.mUrl).get();
        } catch (IOException e) {
            e.printStackTrace();
            result.setException(e, ErrorMessage.E001);
            return result;
        }

        doc = removeTagFromHtml(doc, "aside");
        doc = removeTagFromHtml(doc, "script");
        doc = removeTagFromHtml(doc, "nav");
        doc = removeTagFromHtml(doc, "ul");

        result.setData(doc);
        return result;
    }

    @Override
    public Loader<AsyncResult<Document>> onCreateLoader(int i, Bundle bundle) {
        return this;
    }

    @Override
    public void onLoadFinished(Loader<AsyncResult<Document>> loader, AsyncResult<Document> data) {
        Exception exception = data.getException();
        if (data.getException() != null) {
            //Presenterへのエラー通知を行う
            if(mUseCaseListener != null) {
                mUseCaseListener.onGetEntrySourceUseCaseError(data.getErrorMessage());
            }
            return;
        }

        mUseCaseListener.onGetEntrySourceUseCaseCompleted(data.getData());
    }

    @Override
    public void onLoaderReset(Loader<AsyncResult<Document>> loader) {

    }

    public void execute(LoaderManager lm) {
        Bundle data = new Bundle();
        mLoader = lm.restartLoader(LOADER_ID, data, this);
        mLoader.forceLoad();
    }

    /**
     * このクラスから通知を受け取るクラスを登録します。
     * @param listener リスナとして登録するクラスのインスタンス
     */
    public void addListener(GetEntrySourceUseCaseListener listener) {
        this.mUseCaseListener = listener;
    }

    /**
     * このクラスの通知を受け取るクラスはこのインターフェースを実装する。
     */
    public interface GetEntrySourceUseCaseListener {
        /**
         * 記事のHMLソースの取得に失敗した場合に呼び出されます。
         * @param errorMessage
         */
        void onGetEntrySourceUseCaseError(String errorMessage);

        /**
         * 記事のHTMLソースが取得された際に呼び出されます。
         * @param entrySource 記事のHTMLソース
         *
         */
        void onGetEntrySourceUseCaseCompleted(Document entrySource);
    }

    public static Document removeTagFromHtml(Document doc, String tagName) {
        if(doc == null || tagName == null) {
            throw new AntenaSystemException();
        }
        Elements tags = doc.getElementsByTag(tagName);
        for (Element tag : tags) {
            tag.remove();
        }
        return doc;
    }
}
