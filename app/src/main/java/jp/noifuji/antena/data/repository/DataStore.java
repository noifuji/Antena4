package jp.noifuji.antena.data.repository;

import java.util.List;

import jp.noifuji.antena.entity.Headline;

/**
 * Created by ryoma on 2015/11/20.
 */
public interface DataStore {
    public List<Headline> headlineList(String time, String category);
}
