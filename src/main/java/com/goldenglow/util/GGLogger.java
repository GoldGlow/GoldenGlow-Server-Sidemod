package com.goldenglow.util;

import java.io.*;
import java.util.Date;
import java.util.logging.*;

public class GGLogger
{
    private static final Logger logger = Logger.getLogger("GoldenGlow");
    private static Handler handler;

    public static void teamInfo(Object msg)
    {
        msg="[TEAM] "+msg;
        info(msg);
    }

    public static void info(Object msg)
    {
        logger.log(Level.FINE, msg.toString());
        handler.flush();
    }

    public static void error(Object msg) {
        logger.log(Level.SEVERE, msg.toString());
        handler.flush();
    }

    public static void error(Object msg, Exception e) {
        logger.log(Level.SEVERE, msg.toString(), e);
        handler.flush();
    }

    public static void except(Exception e) {
        logger.log(Level.SEVERE, e.getMessage(), e);
        handler.flush();
    }

    static {
        try {
            File logDir = new File("logs/GoldenGlow");
            if (!logDir.exists())
                logDir.mkdirs();
            File latestLog = new File(logDir, "GoldenGlow-Latest.log");
            File logLock = new File(logDir, "GoldenGlow-Latest.log.lck");
            File log1 = new File(logDir, "GoldenGlow-1.log");
            File log2 = new File(logDir, "GoldenGlow-2.log");
            File log3 = new File(logDir, "GoldenGlow-3.log");

            if (logLock.exists())
                logLock.delete();
            if (log3.exists())
                log3.delete();
            if (log2.exists())
                log2.renameTo(log3);
            if (log1.exists())
                log1.renameTo(log2);
            if (latestLog.exists())
                latestLog.renameTo(log1);

            handler = new StreamHandler(new FileOutputStream(latestLog), new Formatter() {
                public String format(LogRecord record) {
                    String time = "[" + new Date(record.getMillis()) + "][" + "GoldenGlow" + "/" + record.getLevel() + "] ";
                    if (record.getThrown() != null) {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        record.getThrown().printStackTrace(pw);
                        return time + sw.toString();
                    }
                    return time + record.getMessage() + "\n";
                }
            });
            handler.setLevel(Level.ALL);
            logger.addHandler(handler);
            logger.setUseParentHandlers(false);
            Handler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(handler.getFormatter());
            consoleHandler.setLevel(Level.ALL);
            logger.addHandler(consoleHandler);

            logger.setLevel(Level.ALL);
            info(new Date().toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
