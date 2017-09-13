package cc.colorcat.netbird2;

/**
 * Created by cxx on 2017/2/24.
 * xx.ch@outlook.com
 */
final class Version {
    static String userAgent() {
        return "NetBird2/1.0.1";
    }

    private Version() {
        throw new AssertionError("no instance");
    }
}
