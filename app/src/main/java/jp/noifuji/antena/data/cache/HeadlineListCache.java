package jp.noifuji.antena.data.cache;

import android.content.Context;

import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jp.noifuji.antena.data.entity.Headline;
import jp.noifuji.antena.data.entity.HeadlineComparator;
import jp.noifuji.antena.util.Utils;

/**
 * Headlineリストの情報がいつ作られたのかを返す。
 * Headlineリストのキャッシュが存在するのかを返す。
 * Headlineリストの取得、保存を行う。
 */
public class HeadlineListCache {
    private static final String TAG = "HeadlineListCache";
    private static final String CACHE_FILE_NAME = "HeadlineList.dat";
    private Context mContext;

    public HeadlineListCache(Context context) {
        this.mContext = context;
    }

    /**
     * キャッシュからリストを取得する。
     * @return
     */
    public List<Headline> get() {
        return (List<Headline>)Utils.deserialize(Utils.getSDCardDirectory(mContext), CACHE_FILE_NAME);
    }

    /**
     * キャッシュへ登録する。
     */
    public void put(List<Headline> headlines) {//@
        Utils.serialize((Serializable) headlines, Utils.getSDCardDirectory(mContext), CACHE_FILE_NAME);
    }

    public boolean isCached() {//@
        String path = Utils.getSDCardDirectory(mContext) + File.separator + CACHE_FILE_NAME;
        File file = new File(path);
        return file.exists();
    }

    /**
     * キャッシュ内のHeadline情報が本日の記事のものかを返す。
     * @return 記事の発行時刻が本日であればtrueを返す。
     */
    public boolean isExpired() {//@
        List<Headline> headlines = get();
        Headline newestHeadline = Collections.max(headlines, new HeadlineComparator());
        Date today = Utils.getDayInMonth(new Date());
        return today.getTime() > Long.valueOf(newestHeadline.getmPublicationDate());
    }
}
