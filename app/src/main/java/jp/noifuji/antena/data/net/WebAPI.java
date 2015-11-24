package jp.noifuji.antena.data.net;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.noifuji.antena.constants.ErrorMessage;
import jp.noifuji.antena.entity.Headline;

/**
 * Created by ryoma on 2015/11/25.
 */
public class WebAPI {
    private static final String TAG = "WebAPI";

    public WebAPI() {}

    public List<Headline> getHeadlinesFromAPI(String time, String category) throws IOException, JSONException {
        Log.d(TAG, "getHeadlinesFromAPI called");
        ArrayList<Headline> headlines = new ArrayList<>();

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("https://antena-noifuji.c9.io/entry" + "?time=" + time + "&category=" + category);
        //HttpGet httpGet = new HttpGet("http://183.181.0.117:8080/entry" + "?time=" + mLatestPublicationDate + "&category=" + mCategory);
        HttpResponse httpResponse = null;
        String str = null;
        try {
            httpResponse = httpClient.execute(httpGet);
            StatusLine statusline = httpResponse.getStatusLine();
            Log.d(TAG, "StatusCode" + statusline.getStatusCode());
            str = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
        } catch (IOException e) {
            throw new IOException(ErrorMessage.E001);
        }

        JSONArray jsonEntries = null;
        try {
            JSONObject jsonResponse = new JSONObject(str);

            jsonEntries = jsonResponse.getJSONArray("entries");
            Log.d(TAG, jsonEntries.length() + " entries received");

            for (int i = jsonEntries.length() - 1; i >= 0; i--) {
                JSONObject jsonEntry = jsonEntries.getJSONObject(i);
                Headline headline = new Headline(jsonEntry);
                Log.d(TAG, "title:" + headline.getmTitle() + " , category:" + headline.getmCategory() + ", time:" + headline.getFormedPublicationDate("MM/dd HH:mm:ss"));
                headlines.add(headline);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            throw new JSONException("");
        }

        return headlines;
    }
}
