package jp.noifuji.antena.data.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import jp.noifuji.antena.data.entity.Headline;

/**
 * Created by ryoma on 2015/11/25.
 */
public class WebAPI {
    private static final String TAG = "WebAPI";
    private static final String API_SERVER_URL = "https://antena-noifuji.c9.io/";
    //private static final String API_SERVER_URL = "http://183.181.0.117:8080/";
    private static final String API_COMMAND_ENTRY = "entry";
    private static final String ENTRY_ARG_TIME = "time";
    private static final String ENTRY_ARG_CATEGORY = "category";

    public WebAPI() {
    }

    public List<Headline> getHeadlinesFromAPI(String time, String category) throws IOException, JSONException {
        Log.e(TAG, "getHeadlinesFromAPI:" + System.currentTimeMillis());
        Log.d(TAG, "getHeadlinesFromAPI called");
        ArrayList<Headline> headlines = new ArrayList<>();

        StringBuilder result = new StringBuilder();
        URL url = new URL(API_SERVER_URL + API_COMMAND_ENTRY + "?" + ENTRY_ARG_TIME + "=" + time + "&" + ENTRY_ARG_CATEGORY + "=" + category);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        InputStream in = new BufferedInputStream(urlConnection.getInputStream());

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }

 /*       HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(API_SERVER_URL + API_COMMAND_ENTRY + "?" + ENTRY_ARG_TIME + "=" + time + "&" + ENTRY_ARG_CATEGORY + "=" + category);
        HttpResponse httpResponse = httpClient.execute(httpGet);
        StatusLine statusline = httpResponse.getStatusLine();
        Log.d(TAG, "StatusCode" + statusline.getStatusCode());
        String str = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");*/

        //JSONObject jsonResponse = new JSONObject(str);
        JSONObject jsonResponse = new JSONObject(result.toString());

        JSONArray jsonEntries = jsonResponse.getJSONArray("entries");
        Log.d(TAG, jsonEntries.length() + " entries received");

        for (int i = jsonEntries.length() - 1; i >= 0; i--) {
            JSONObject jsonEntry = jsonEntries.getJSONObject(i);
            Headline headline = new Headline(jsonEntry);
            Log.d(TAG, "title:" + headline.getmTitle() + " , category:" + headline.getmCategory() + ", time:" + headline.getFormedPublicationDate("MM/dd HH:mm:ss"));
            headlines.add(headline);
        }

        return headlines;
    }

    public Bitmap getThumbnail(String thumbnail) throws IOException {
        Log.e(TAG, "getThumbnail:" + System.currentTimeMillis());
        Bitmap bmp = null;
        URL url = new URL(API_SERVER_URL+"thumbnail?name=" + URLEncoder.encode(thumbnail, "UTF-8"));
        /** Creating an http connection to communicate with url */
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        /** Connecting to url */
        urlConnection.connect();

        InputStream is = urlConnection.getInputStream();

        /** Creating a bitmap from the stream returned from the url */
        bmp = BitmapFactory.decodeStream(is);

        is.close();
        return bmp;
    }
}
