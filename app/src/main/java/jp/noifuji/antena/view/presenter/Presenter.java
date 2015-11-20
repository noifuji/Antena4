package jp.noifuji.antena.view.presenter;

/**
 * Viewへの描画指示を行う。
 * UseCaseを用いてUIスレッド以外のスレッドで処理を実行する。
 */
public interface Presenter {
    /**
     * Presenterを保持するActivityやFragmentはonResume()時にこのメソッドをコールする。
     */
    void resume();
    /**
     * Presenterを保持するActivityやFragmentはonPause()時にこのメソッドをコールする。
     */
    void pause();
    /**
     * Presenterを保持するActivityやFragmentはonDestroy()時にこのメソッドをコールする。
     */
    void destroy();
}
