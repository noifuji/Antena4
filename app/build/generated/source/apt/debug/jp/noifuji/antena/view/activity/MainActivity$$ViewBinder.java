// Generated code from Butter Knife. Do not modify!
package jp.noifuji.antena.view.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MainActivity$$ViewBinder<T extends jp.noifuji.antena.view.activity.MainActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624038, "field 'mDrawer'");
    target.mDrawer = finder.castView(view, 2131624038, "field 'mDrawer'");
  }

  @Override public void unbind(T target) {
    target.mDrawer = null;
  }
}
