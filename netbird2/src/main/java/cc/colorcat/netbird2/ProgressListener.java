package cc.colorcat.netbird2;

/**
 * Created by cxx on 2016/12/12.
 * xx.ch@outlook.com
 */
public interface ProgressListener {

    void onChanged(long finished, long total, int percent);
}
