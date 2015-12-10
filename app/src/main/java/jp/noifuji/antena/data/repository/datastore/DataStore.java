package jp.noifuji.antena.data.repository.datastore;

import android.content.Context;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import jp.noifuji.antena.data.entity.Headline;

/**
 * Created by ryoma on 2015/11/20.
 */
public interface DataStore {
    //TODO Repositoryの下にはMemoryCache DiskCache Cloud...にする。
    public List<Headline> headlineList(Context context, String category) throws IOException, JSONException;
    public Headline thumbnail(Context context, Headline headline) throws IOException, JSONException;
}
