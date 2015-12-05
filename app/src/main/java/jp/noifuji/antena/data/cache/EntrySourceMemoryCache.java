package jp.noifuji.antena.data.cache;

import java.util.ArrayList;
import java.util.List;

import jp.noifuji.antena.data.entity.HtmlPage;

/**
 * Created by ryoma on 2015/12/05.
 */
public class EntrySourceMemoryCache {
    private List<HtmlPage> mHtmlPageCacheList;
    private static final int MAX_SIZE_HTML_PAGE_CACHE_SIZE = 20;

    EntrySourceMemoryCache(){
        mHtmlPageCacheList = new ArrayList<>();
    }

    /**
     * 指定したURLのページの情報を取得する。
     * @param url
     * @return
     */
    public HtmlPage get(String url) {
        if(url == null) {
            return null;
        }

        for (HtmlPage htmlPage : mHtmlPageCacheList) {
            if(url.equals(htmlPage.getmHtml())) {
                return htmlPage;
            }
        }
        return null;
    }

    /**
     * URLがキャッシュされているかをチェックする。
     * @param url
     * @return
     */
    public boolean isCached(String url) {
        if(url == null) {
            return false;
        }

        for (HtmlPage htmlPage : mHtmlPageCacheList) {
            if(url.equals(htmlPage.getmHtml())) {
                return true;
            }
        }
        return false;
    }

    public void add(HtmlPage htmlPage) {
        mHtmlPageCacheList.add(htmlPage);

        //最大保存サイズを超える場合は、最も古いキャッシュを消す。
        if(mHtmlPageCacheList.size() > MAX_SIZE_HTML_PAGE_CACHE_SIZE) {
            mHtmlPageCacheList.remove(0);
        }
    }
}
