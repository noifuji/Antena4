package jp.noifuji.antena.data.repository.datastore;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.noifuji.antena.data.cache.HeadlineListCache;
import jp.noifuji.antena.data.entity.Headline;
import jp.noifuji.antena.data.entity.HeadlineComparator;
import jp.noifuji.antena.data.net.WebAPI;
import jp.noifuji.antena.util.Utils;

/**
 * 記事タイトルリストをWebAPIに問い合わせて取得する。
 * キャッシュが存在する場合は、差分をWebAPIから取得する。
 */
public class HeadlineDataStore implements DataStore {
    private static final String TAG = "HeadlineDataStore";

    @Override
    public List<Headline> headlineList(Context context, String category) throws IOException, JSONException {
        Log.e(TAG, "headlineList:" + System.currentTimeMillis());
        Log.d(TAG, "headlineList called");
        HeadlineListCache cache = new HeadlineListCache(context);
        ArrayList<Headline> headlinesCache = null;
        WebAPI webAPI = new WebAPI();


        if (cache.isCached() && !cache.isExpired()) {
            //本日分のキャッシュが存在する場合
            headlinesCache = (ArrayList<Headline>) cache.get();
            for (Headline h : headlinesCache) {
                Log.d(TAG, h.getmTitle());
            }
            Headline newestHeadline = Collections.max(headlinesCache, new HeadlineComparator());
            ArrayList<Headline> newHeadlines = (ArrayList<Headline>) webAPI.getHeadlinesFromAPI(newestHeadline.getmPublicationDate(), category);


            for (Headline headline : newHeadlines) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap bmp = webAPI.getThumbnail(headline.getmThumbnailFileName());
                if (bmp == null) {
                    continue;
                }
                bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] bytes = baos.toByteArray();
                headline.setmThumbnail(bytes);
            }


            //現在のキャッシュに追加する
            for (Headline headline : newHeadlines) {
                headlinesCache.add(headline);
            }

        } else {
            headlinesCache = (ArrayList<Headline>) webAPI.getHeadlinesFromAPI(String.valueOf(Utils.getDayInMonth(Utils.getNowDate()).getTime()), category);

            for (Headline headline : headlinesCache) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap bmp = null;
                //bmp = webAPI.getThumbnail(headline.getmThumbnailFileName());

                if (bmp == null) {
                    Log.e(TAG, "getThumbnail No:" + System.currentTimeMillis());
                    continue;
                }

                Log.e(TAG, "getThumbnail End:" + System.currentTimeMillis());

                bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] bytes = baos.toByteArray();
                headline.setmThumbnail(bytes);
            }
        }
        cache.put(headlinesCache);
        return headlinesCache;
    }

    @Override
    public Headline thumbnail(Context context, Headline headline) throws IOException, JSONException {
        WebAPI webAPI = new WebAPI();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bmp = null;
        bmp = webAPI.getThumbnail(headline.getmThumbnailFileName());
        HeadlineListCache cache = new HeadlineListCache(context);
        ArrayList<Headline> headlinesCache = null;

        if (bmp == null) {
            Log.e(TAG, "getThumbnail No:" + System.currentTimeMillis());
            //TODO nullじゃないなんか適当なデータをつっこんでるけどどうにかする
            byte[] bytes = {1, 2, 3};
            headline.setmThumbnail(bytes);
            return headline;
        }

        Log.e(TAG, "getThumbnail End:" + System.currentTimeMillis());

        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        headline.setmThumbnail(bytes);

        //キャッシュにつっこむ
        //TODO たぶん死ぬほど遅いからなおす キャッシュへのかきこみは別スレッドでやればどうか?
        headlinesCache = (ArrayList<Headline>) cache.get();
        if (headlinesCache.size() > 0) {
            for (Headline h : headlinesCache) {
                if (h.getmSysId().equals(headline.getmSysId())) {
                    h = headline;
                }
            }
            cache.put(headlinesCache);
        }

        return headline;
    }
}
