package ichigo.util;

import java.util.*;

public class HtmlUtil {

  public static String escapeCRLF(String text) {
    StringBuilder sb = new StringBuilder();
    Scanner scan = new Scanner(text);
    while (scan.hasNext()) {
      String line = scan.nextLine();
      sb.append(line);
      sb.append("<br />\r\n");
    }
    scan.close();
    return sb.toString();
  }


}
