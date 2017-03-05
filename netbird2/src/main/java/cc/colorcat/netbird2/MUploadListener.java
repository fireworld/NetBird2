package cc.colorcat.netbird2;

/**
 * Created by cxx on 2017/3/5.
 * xx.ch@outlook.com
 */
final class MUploadListener implements UploadListener {

    static UploadListener wrap(UploadListener listener) {
        if (listener != null) {
            return new MUploadListener(listener);
        }
        return null;
    }

    private UploadListener listener;

    private MUploadListener(UploadListener listener) {
        this.listener = listener;
    }

    @Override
    public void onChanged(long written, long total, int percent) {
        HandlerUtils.postProgress(listener, written, total, percent);
    }
}
