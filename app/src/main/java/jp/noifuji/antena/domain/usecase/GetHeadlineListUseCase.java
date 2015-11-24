package jp.noifuji.antena.domain.usecase;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import jp.noifuji.antena.entity.Headline;
import jp.noifuji.antena.loader.AsyncResult;
import jp.noifuji.antena.data.repository.HeadlineRepository;
import jp.noifuji.antena.util.Utils;

/**
 * Created by ryoma on 2015/11/20.
 */
public class GetHeadlineListUseCase extends AsyncTaskLoader<AsyncResult<List<Headline>>> implements LoaderManager.LoaderCallbacks<AsyncResult<List<Headline>>> {
    private static final int LOADER_ID = 1;
    private static final String TAG = "GetHeadlineListUseCase";
    private Loader mLoader;
    private HeadlineRepository mHeadlineRepository;
    private String mLatestPublicationDate;
    private String mCategory;
    private Context mContext;
    private GetHeadlineListUseCaseListener mUseCaseListener;

    public GetHeadlineListUseCase(Context context, HeadlineRepository headlineRepository, String latestPublicationDate, String category) {
        super(context);
        this.mHeadlineRepository = headlineRepository;
        mLatestPublicationDate = latestPublicationDate;
        mCategory = category;
        mContext = context;
    }

    @Override
    public AsyncResult<List<Headline>> loadInBackground() {
        AsyncResult<List<Headline>> result = new AsyncResult<List<Headline>>();
        List<Headline> headlineList = null;
        try {
            headlineList = mHeadlineRepository.headlines().headlineList(mContext, mLatestPublicationDate, mCategory);
        } catch (IOException e) {
            e.printStackTrace();
            result.setException(e, "");
        } catch (JSONException e) {
            e.printStackTrace();
            result.setException(e, "");
        }
        result.setData(headlineList);
        return result;
    }

    @Override
    public Loader<AsyncResult<List<Headline>>> onCreateLoader(int i, Bundle bundle) {
        return this;
    }

    @Override
    public void onLoadFinished(Loader<AsyncResult<List<Headline>>> loader, AsyncResult<List<Headline>> data) {
        Exception exception = data.getException();
        if (data.getException() != null) {
            //Presenterへのエラー通知を行う
            if(mUseCaseListener != null) {
                mUseCaseListener.onGetHeadlineListUseCaseError(data.getErrorMessage());
            }
            return;
        }
        mUseCaseListener.onGetHeadlineListUseCaseCompleted(Utils.filterHeadlineListByCategory(data.getData(), mCategory), 1);//@
    }

    @Override
    public void onLoaderReset(Loader<AsyncResult<List<Headline>>> loader) {

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
    public void addListener(GetHeadlineListUseCaseListener listener) {
        this.mUseCaseListener = listener;
    }

    /**
     * このクラスの通知を受け取るクラスはこのインターフェースを実装する。
     */
    public interface GetHeadlineListUseCaseListener {
        /**
         * 記事のヘッドラインの更新確認に失敗した場合に呼び出されます。
         * @param errorMessage
         */
        void onGetHeadlineListUseCaseError(String errorMessage);

        /**
         * 記事のヘッドラインの更新確認が完了した場合に呼び出されます。
         * @param headlineList モデルが保持しているヘッドライン情報
         * @param updatedCount 更新された件数
         */
        void onGetHeadlineListUseCaseCompleted(List<Headline> headlineList, int updatedCount);
    }
}
