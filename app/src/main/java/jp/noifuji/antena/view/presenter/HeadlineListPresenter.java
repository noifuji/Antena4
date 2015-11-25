package jp.noifuji.antena.view.presenter;

import android.util.Log;

import java.util.Collections;
import java.util.List;

import jp.noifuji.antena.data.repository.HeadlineRepositoryImpl;
import jp.noifuji.antena.domain.usecase.GetHeadlineListUseCase;
import jp.noifuji.antena.data.entity.Headline;
import jp.noifuji.antena.data.entity.HeadlineComparator;
import jp.noifuji.antena.model.HeadLineListModel;
import jp.noifuji.antena.view.fragment.HeadLineListFragment;

/**
 * Created by ryoma on 2015/11/19.
 */
public class HeadlineListPresenter implements Presenter, HeadLineListModel.HeadLineListModelListener, GetHeadlineListUseCase.GetHeadlineListUseCaseListener {

    private static final String TAG = "HeadlineListPresenter";
    private static HeadlineListPresenter instance = new HeadlineListPresenter();
    private HeadLineListFragment mHeadlineListFragment;//@フラグメントが死んだときやばくね??
    private GetHeadlineListUseCase g;

    private HeadlineListPresenter() {}

    public static HeadlineListPresenter getInstance()  {
        return instance;
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void destroy() {}

    public void initialize(String category) {
        if(mHeadlineListFragment != null) {
        }
    }

    public void setFragment(HeadLineListFragment fragment) {
        this.mHeadlineListFragment = fragment;
    }

    public void loadHeadlineList(String category) {
        this.showViewLoading();
        this.getHeadlineList(category);
    }

    public void refreshHeadlineList(String category) {
        this.getHeadlineList(category);
    }

    public void onHeadlineClicked(Headline headline, int position) {
        //headline.setIsRead(true);
        //this.mHeadlineListFragment.readHeadline(position);//既読機能
        this.mHeadlineListFragment.viewEntry(headline);
    }

    private void showViewLoading() {
        this.mHeadlineListFragment.showLoading();
    }

    private void hideViewLoading() {
        this.mHeadlineListFragment.hideLoading();
    }

    private void hideViewRefreshing() {
        this.mHeadlineListFragment.hideRefreshing();
    }

    private void getHeadlineList(String category) {
        //ヘッドラインのリストを取得する。
        g = new GetHeadlineListUseCase(this.mHeadlineListFragment.getActivity(), new HeadlineRepositoryImpl(), category);
        g.addListener(this);
        g.execute(this.mHeadlineListFragment.getLoaderManager());
    }

    @Override
    public void onHeadLineListUpdateError(String errorMessage) {
        this.hideViewLoading();
        mHeadlineListFragment.showError(errorMessage);
    }

    @Override
    public void onHeadLineListUpdated(List<Headline> headlineList, int updatedCount) {
        this.hideViewLoading();
        this.hideViewRefreshing();
        mHeadlineListFragment.renderHeadlineList(headlineList);
        if(headlineList.size() > 0) {
            this.mHeadlineListFragment.setNewestHeadline(headlineList.get(0));
        }
    }

    @Override
    public void onGetHeadlineListUseCaseError(String errorMessage) {
        this.hideViewLoading();
        mHeadlineListFragment.showError(errorMessage);
    }

    @Override
    public void onGetHeadlineListUseCaseCompleted(List<Headline> headlineList, int updatedCount) {
        if(headlineList == null) {
            return;
        }
        Log.d(TAG, "title is " + Collections.max(headlineList, new HeadlineComparator()).getmTitle());

        this.hideViewLoading();
        this.hideViewRefreshing();
        mHeadlineListFragment.renderHeadlineList(headlineList);
        if(headlineList.size() > 0) {
            this.mHeadlineListFragment.setNewestHeadline(Collections.max(headlineList, new HeadlineComparator()));
        }
    }
}
