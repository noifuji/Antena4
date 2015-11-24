package jp.noifuji.antena.data.repository;

import jp.noifuji.antena.data.repository.datastore.DataStore;
import jp.noifuji.antena.data.repository.datastore.HeadlineDataStore;

/**
 * Created by ryoma on 2015/11/20.
 */
public class HeadlineRepositoryImpl implements HeadlineRepository {
    @Override
    public DataStore headlines() {
        return new HeadlineDataStore();
    }
}
