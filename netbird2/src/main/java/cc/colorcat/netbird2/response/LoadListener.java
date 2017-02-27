package cc.colorcat.netbird2.response;

import cc.colorcat.netbird2.ProgressListener;

/**
 * Created by cxx on 17-2-27.
 * xx.ch@outlook.com
 */
public interface LoadListener extends ProgressListener {

    @Override
    void onChanged(long read, long total, int percent);
}
