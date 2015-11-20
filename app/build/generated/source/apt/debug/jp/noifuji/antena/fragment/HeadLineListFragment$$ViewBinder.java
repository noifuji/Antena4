// Generated code from Butter Knife. Do not modify!
package jp.noifuji.antena.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import jp.noifuji.antena.view.fragment.HeadLineListFragment;

public class HeadLineListFragment$$ViewBinder<T extends HeadLineListFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624046, "field 'mListView'");
    target.mListView = finder.castView(view, 2131624046, "field 'mListView'");
    view = finder.findRequiredView(source, 2131624045, "field 'mSwipeRefreshLayout'");
    target.mSwipeRefreshLayout = finder.castView(view, 2131624045, "field 'mSwipeRefreshLayout'");
    view = finder.findRequiredView(source, 2131624047, "field 'mUpButton'");
    target.mUpButton = finder.castView(view, 2131624047, "field 'mUpButton'");
    view = finder.findRequiredView(source, 2131624044, "field 'mProgressBar'");
    target.mProgressBar = finder.castView(view, 2131624044, "field 'mProgressBar'");
  }

  @Override public void unbind(T target) {
    target.mListView = null;
    target.mSwipeRefreshLayout = null;
    target.mUpButton = null;
    target.mProgressBar = null;
  }
}
