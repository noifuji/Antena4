package jp.noifuji.antena.data.repository;

import java.util.ArrayList;
import java.util.List;

import jp.noifuji.antena.entity.Headline;

/**
 * Created by ryoma on 2015/11/20.
 */
public class OnlineHaedlineDataStore implements DataStore {//@sample
    @Override
    public List<Headline> headlineList(String time, String category) {
        ArrayList list = new ArrayList();
        Headline h = new Headline();
        h.setmTitle("test");
        list.add(h);
        return list;
    }
}
