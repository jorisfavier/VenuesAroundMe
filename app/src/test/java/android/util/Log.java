package android.util;

/**
 * Util Log class in order to being able to log in test
 */
public class Log {
    public static int w(String tag, Throwable t) {
        System.out.println("WARN: " + tag + ": " + t.getMessage());
        return 0;
    }
}
