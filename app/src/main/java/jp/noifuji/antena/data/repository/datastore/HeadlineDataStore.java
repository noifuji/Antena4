package jp.noifuji.antena.data.repository.datastore;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.noifuji.antena.data.net.WebAPI;
import jp.noifuji.antena.data.cache.HeadlineListCache;
import jp.noifuji.antena.entity.Headline;
import jp.noifuji.antena.entity.HeadlineComparator;
import jp.noifuji.antena.util.Utils;

/**
 * 記事タイトルリストをWebAPIに問い合わせて取得する。
 * キャッシュが存在する場合は、差分をWebAPIから取得する。
 */
public class HeadlineDataStore implements DataStore {
    private static final String TAG = "HeadlineDataStore";

    @Override
    public List<Headline> headlineList(Context context, String time, String category) throws IOException, JSONException {
        Log.d(TAG, "headlineList called");
        HeadlineListCache cache = new HeadlineListCache(context);
        ArrayList<Headline> headlinesCache = null;
        WebAPI webAPI = new WebAPI();

        Log.d(TAG, cache.isCached() + "," + cache.isExpired());
        if (cache.isCached() && !cache.isExpired()) {
            headlinesCache = (ArrayList<Headline>) cache.get();
            for(Headline h :headlinesCache) {
                Log.d(TAG, h.getmTitle());
            }
            Headline newestHeadline = Collections.max(headlinesCache, new HeadlineComparator());
            ArrayList<Headline> newHeadlines = (ArrayList<Headline>) webAPI.getHeadlinesFromAPI(newestHeadline.getmPublicationDate(), category);

            //現在のキャッシュに追加する
            for (Headline headline : newHeadlines) {
                headlinesCache.add(headline);
            }

        } else {
            headlinesCache = (ArrayList<Headline>) webAPI.getHeadlinesFromAPI(String.valueOf(Utils.getDayInMonth(Utils.getNowDate()).getTime()), category);
        }
        cache.put(headlinesCache);
        return headlinesCache;
    }
}
