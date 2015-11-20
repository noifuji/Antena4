package jp.noifuji.antena.data.repository;

/**
 * Created by ryoma on 2015/11/20.
 */
public class HeadlineRepositoryImpl implements HeadlineRepository {
    @Override
    public DataStore headlines() {
        return new OnlineHaedlineDataStore();
    }
}
