package jp.noifuji.antena.data.repository;

import jp.noifuji.antena.data.repository.datastore.DataStore;

/**
 * Created by ryoma on 2015/11/20.
 */
public interface HeadlineRepository {
    //TODO findAll find update とかにする。thumbnailは特殊な枠でつくる。
    public DataStore headlines();
}
