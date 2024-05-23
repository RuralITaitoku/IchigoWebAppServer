package ichigo.util;

import java.util.Calendar;

public class YmdUtil {

  public static Calendar getCalendar(int ymd) {
    Calendar c = Calendar.getInstance();
    int y = ymd / 10000;
    int m = (ymd / 100) % 100 - 1;
    int d = ymd % 100;
    c.set(y, m, d);
    return c;
  }
  public static int getYmd() {
    return getYmd(Calendar.getInstance());
  }
  public static int getHms() {
    return getHms(Calendar.getInstance());
  }
  public static int getYmd(Calendar c) {
    int y = c.get(Calendar.YEAR);
    int m = c.get(Calendar.MONTH);
    int d = c.get(Calendar.DAY_OF_MONTH);
    return y * 10000 + (m + 1) * 100 + d;
  }
  public static int getHms(Calendar c) {
    int h = c.get(Calendar.HOUR_OF_DAY);
    int m = c.get(Calendar.MINUTE);
    int s = c.get(Calendar.SECOND);
    return h * 10000 + m * 100 + s;
  }
  public static int addDate(int ymd, int date) {
    Calendar c = getCalendar(ymd);
    c.add(Calendar.DAY_OF_MONTH, date);
    return getYmd(c);
  }
  public static int getDiffDays(int ymd1, int ymd2) {
    Calendar c1 = getCalendar(ymd1);
    Calendar c2 = getCalendar(ymd2);
    long diffTime = c2.getTimeInMillis() - c1.getTimeInMillis();
    int MILLIS_OF_DAYS = 1000 * 60 * 60 * 24;
    int diffDays = (int)(diffTime / MILLIS_OF_DAYS);
    return diffDays;
  }
  public static String getJpString(int ymd, int date) {
    return getJpString(addDate(ymd, date));
  }
  public static String getJpString(int ymd) {
    int y = ymd / 10000;
    int m = (ymd / 100) % 100;
    int d = ymd % 100;
    return String.format("%d年%d月%02d日(%s)", y, m, d, getJpWeek(ymd));
  }
  public static String getJpWeek(int ymd) {
    return getJpWeek(getCalendar(ymd));
  }
  public static String getJpWeek(Calendar c) {
    switch (c.get(Calendar.DAY_OF_WEEK)) {
      case Calendar.SUNDAY:     // Calendar.SUNDAY:1 （値。意味はない）
          //日曜日
          return "日";
      case Calendar.MONDAY:     // Calendar.MONDAY:2
          //月曜日
          return "月";
      case Calendar.TUESDAY:    // Calendar.TUESDAY:3
          //火曜日
          return "火";
      case Calendar.WEDNESDAY:  // Calendar.WEDNESDAY:4
          //水曜日
          return "水";
      case Calendar.THURSDAY:   // Calendar.THURSDAY:5
          //木曜日
          return "木";
      case Calendar.FRIDAY:     // Calendar.FRIDAY:6
          //金曜日
          return "金";
      case Calendar.SATURDAY:   // Calendar.SATURDAY:7
          //土曜日
          return "土";
    }
    return "不明";
  }
}
