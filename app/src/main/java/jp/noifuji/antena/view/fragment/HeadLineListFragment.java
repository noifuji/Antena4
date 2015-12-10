package jp.noifuji.antena.view.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.noifuji.antena.R;
import jp.noifuji.antena.constants.Category;
import jp.noifuji.antena.data.entity.Headline;
import jp.noifuji.antena.view.EntryAdapter;
import jp.noifuji.antena.view.presenter.HeadlineListPresenter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class HeadLineListFragment extends Fragment implements EntryAdapter.OnHeadlineDisplayedListener {
    private static final String TAG = "HeadLineListFragment";
    private static final String CATEGORY = "category";
    private OnFragmentInteractionListener mListener;
    private String mCategory = Category.ALL; //@presenterの管轄か

    private HeadlineListPresenter mHeadlineListPresenter;

    @Bind(R.id.entry_list)
    ListView mListView;
    @Bind(R.id.swipe_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.up_button)
    FloatingActionButton mUpButton;
    @Bind(R.id.headline_list_progress)
    CircularProgressView mProgressBar;

    ImageView imageView;//TODO 消す 実験用


    public HeadLineListFragment() {
        // Required empty public constructor
        //
    }

    public static HeadLineListFragment newInstance(String category) {
        HeadLineListFragment fragment = new HeadLineListFragment();
        Bundle args = new Bundle();
        args.putString(CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCategory = getArguments().getString(CATEGORY);
        }

        mHeadlineListPresenter = HeadlineListPresenter.getInstance();
        mHeadlineListPresenter.setFragment(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.mHeadlineListPresenter.resume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment   kl
        View view = inflater.inflate(R.layout.fragment_entry_list, container, false);
        ButterKnife.bind(this, view);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.swipe_color_1,
                R.color.swipe_color_2, R.color.swipe_color_3, R.color.swipe_color_4);

        mUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mListView.smoothScrollToPosition(0);
                ((EntryAdapter) mListView.getAdapter()).notifyDataSetChanged();
            }
        });



        this.loadHeadlineList();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        mListener.onSetTitle(mCategory);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "onRefresh" + mCategory);
                HeadLineListFragment.this.refreshHeadlineList();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Headline headline = (Headline) parent.getAdapter().getItem(position);
                //headline.setIsRead(true);
                //parent.getAdapter().getView(position, view, parent);//@これはろじっくでは??

                HeadLineListFragment.this.mHeadlineListPresenter.onHeadlineClicked(headline, position);
            }
        });
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
        Log.d(TAG, "entered onDetach()");
        mListener = null;
        super.onDetach();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.mHeadlineListPresenter.pause();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView()");
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mHeadlineListPresenter.destroy();
    }

    @Override
    public void onHeadlineDisplayed(Headline headline) {
        mHeadlineListPresenter.getThumbnailImage(headline);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onStartWebView(String uri);

        void onShowTextMessage(String message);

        void onSetTitle(String category);

        void onSetNewestEntryTitle(Headline headline);
    }

    public void showLoading() {
        this.mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        this.mProgressBar.setVisibility(View.GONE);
    }

    public void hideRefreshing() {
        //更新ダイアログを停止する。
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void renderHeadlineList(List<Headline> headlineList) {
        if(headlineList.size() == 0) {
            return;
        }
        EntryAdapter adapter = new EntryAdapter(this.getActivity().getApplication(), R.layout.list_item, headlineList, this);
        mListView.setAdapter(adapter);
    }

    public void viewEntry(Headline headline) {
        //WebViewを開く
        mListener.onStartWebView(headline.getmUrl());
    }

    public void setNewestHeadline(Headline headline) {
        //メニューの上に最新エントリの情報を表示する。
        mListener.onSetNewestEntryTitle(headline);
    }

    public void showError(String message) {
        mListener.onShowTextMessage(message);
    }

    public void loadHeadlineList() {
        this.mHeadlineListPresenter.loadHeadlineList(mCategory);
    }

    public void refreshHeadlineList() {
        this.mHeadlineListPresenter.refreshHeadlineList(mCategory);
    }

    /**
     * 記事を既読状態にする
     * @param position
     */
    public void readHeadline(int position) {//@
        Headline headline = (Headline) mListView.getAdapter().getItem(position);
        headline.setIsRead(true);
        mListView.getAdapter().getView(position, null, null);
    }

    /**
     * サムネイル画像の遅延ロードが完了したら呼ばれる。
     * @param updatedHeadline
     */
    public void onReceivedThumbnail(Headline updatedHeadline) {
        Log.d(TAG, updatedHeadline.getmTitle());
        ((EntryAdapter)mListView.getAdapter()).setItem(updatedHeadline);
        ((EntryAdapter)mListView.getAdapter()).notifyDataSetChanged();
    }

}
