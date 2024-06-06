package ichigo.util;

import java.util.logging.*;

public class LogUtil {

  private static Logger logger = null;

  public static boolean init() {
    if (logger != null) {
      return true;
    }
    try {
      //System.setProperty("java.util.logging.SimpleFormatter.format",
      //"%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %4$s %2$s %5$s%6$s%n");
      System.setProperty("java.util.logging.SimpleFormatter.format",
      "%1$tY%1$tm%1$td%1$tH%1$tM%1$tS%4$s %5$s%6$s%n");

      logger = Logger.getLogger("logger");
      logger.setLevel(Level.INFO);
      Handler handler = new FileHandler("./logs/" + YmdUtil.getYmd() + "-" + YmdUtil.getHms() + ".log");
      handler.setFormatter(new SimpleFormatter());
      logger.addHandler(handler);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public static void log(String l) {
		Throwable th = new Throwable();
    String filePos = getFilePos(th.getStackTrace());
    // System.out.print(filePos);
		// System.out.println(l);
    info(filePos + l);
	}


  public static void info(String l) {
    if (init()) {
      logger.info(l);
    }
  }

  public static void redlog(String l) {
    Throwable th = new Throwable();
		System.out.print("\033[31m");
    String filePos = getFilePos(th.getStackTrace());
    info(filePos + l);
		System.out.print("\033[0m");
	}
  public static void red(String l) {
    Throwable th = new Throwable();
		System.out.print("\033[31m");
    System.out.flush();
    String filePos = getFilePos(th.getStackTrace());
    info(filePos + l);
    System.out.flush();
		System.out.print("\033[0m");
    System.out.flush();
	}
  public static void blue(String l) {
    Throwable th = new Throwable();
		System.out.print("\033[34m");
    String filePos = getFilePos(th.getStackTrace());
    info(filePos + l);
		System.out.print("\033[0m");
	}

  private static String getFilePos(StackTraceElement[] stackTrace) {
		if (stackTrace.length > 1) {
			StackTraceElement e = stackTrace[1];
      return "" + e.getFileName() + ":"
								+ e.getLineNumber() + "  ";
		}
    return "";
  }

  public static void log(Exception e) {
    info (e.getMessage());
    for (StackTraceElement element : e.getStackTrace()) {
      info(element.toString());     
    }
  }

}
