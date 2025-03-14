package saka1029;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Common {

    static final Formatter MY_FORMATTER= new Formatter() {
        @Override public String format(LogRecord record){
            // String loggerName = record.getLoggerName();
            String loggerName = record.getLoggerName().replaceFirst("^.+\\.", "");
            Object[] parameters = record.getParameters();
            String mess = record.getMessage();
            String message = parameters == null ? mess : mess.formatted(parameters);
            return String.format("%1$tY-%1$tm-%1$td %1$tT.%1$tL %3$s %4$s: %5$s%6$s%n",
                new Date(record.getMillis()), record.getSourceClassName(), loggerName,
                record.getLevel(), message, record.getThrown()==null ? "" : " " + record.getThrown());
            }
        };

    public static Logger logger(Class<?> clazz) {
        initLogger();
        Logger logger = Logger.getLogger(clazz.getName());
        return logger;
    }

    static boolean INIT_LOGGER = false;

    /**
     * System.out, System.errの文字セットをUTF-8に変更する。
     * すべてのロガーのフォーマットをMY_FORMATに変更する。
     * 
     * System.out, System.errのデフォルト文字セットは
     * (1)コマンドプロンプトの場合はコードページに依存する。
     * （コードページ932のときMS932、65001のときUTF-8）
     * (2)Eclipseのコンソールの場合はwindows-31jとなる。
     * (3)vscodeのコンソールの場合はwindows-31jとなる。
     */
    public static void initLogger() {
        if (INIT_LOGGER)
            return;
        // if (!System.out.charset().equals(StandardCharsets.UTF_8))
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        // if (!System.err.charset().equals(StandardCharsets.UTF_8))
            System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
        for (Handler h : Logger.getLogger("").getHandlers())
            if (!h.getFormatter().equals(MY_FORMATTER))
                h.setFormatter(MY_FORMATTER);
    }

    public static void log(Logger logger, Level level, String message, Object... parameters) {
        logger.log(level, message, parameters);
    }

    public static void log(Logger logger, Level level, String message, Throwable thrown) {
        logger.log(level, message, thrown);
    }

    public static String methodName() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        return elements[2].getMethodName();
    }
}
