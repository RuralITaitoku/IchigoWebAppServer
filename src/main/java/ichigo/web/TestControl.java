package ichigo.web;

import ichigo.util.*;
import java.lang.reflect.*;

public class TestControl {

  @RequestMapping(value = "/index.html.*")
  public String test() {
    try {
      IchigoSession threadSession = IchigoSession.getThreadSession();
      return threadSession.getStringFile("html/test.html");
      /*
      LogUtil.log("---------------------- TestControl　呼ばれる");
      StringBuilder sb = new StringBuilder();
      Command req = threadSession.getRequest();
      String hellowWorld = HtmlUtil.escapeCRLF(req.getCommandString());


      String html1 = """
      <!doctype html>
      <html lang='ja'>
      <meta charset='utf-8' />
      <meta
      <head>
      </head>
        <body>
      """;
      String html2 = """
        </body>
      </html>
      """;
      return html1 + hellowWorld + html2;
      */
    } catch (Exception e) {
      e.printStackTrace();
      return e.getMessage();
    }
  }

  public String toString() {
    return "TestControl.toString メソッド";

  }

}
