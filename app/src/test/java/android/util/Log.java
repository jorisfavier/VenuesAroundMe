package android.util;

public class Log {
    public static int w(String tag, Throwable t) {
        System.out.println("WARN: " + tag + ": " + t.getMessage());
        return 0;
    }
}
