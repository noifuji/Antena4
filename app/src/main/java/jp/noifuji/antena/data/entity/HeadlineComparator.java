package jp.noifuji.antena.data.entity;

import java.util.Comparator;

/**
 * Created by ryoma on 2015/11/24.
 */
public class HeadlineComparator implements Comparator<Headline> {
    @Override
    public int compare(Headline headline, Headline t1) {
        return Long.valueOf(headline.getmPublicationDate()) < Long.valueOf(t1.getmPublicationDate()) ? -1 : 1;
    }
}
