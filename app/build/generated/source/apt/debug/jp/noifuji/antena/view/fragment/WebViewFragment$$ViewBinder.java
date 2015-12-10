// Generated code from Butter Knife. Do not modify!
package jp.noifuji.antena.view.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class WebViewFragment$$ViewBinder<T extends jp.noifuji.antena.view.fragment.WebViewFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624048, "field 'webView'");
    target.webView = finder.castView(view, 2131624048, "field 'webView'");
    view = finder.findRequiredView(source, 2131624049, "field 'mProgressBar'");
    target.mProgressBar = view;
    view = finder.findRequiredView(source, 2131624050, "field 'mBackButton'");
    target.mBackButton = finder.castView(view, 2131624050, "field 'mBackButton'");
  }

  @Override public void unbind(T target) {
    target.webView = null;
    target.mProgressBar = null;
    target.mBackButton = null;
  }
}
