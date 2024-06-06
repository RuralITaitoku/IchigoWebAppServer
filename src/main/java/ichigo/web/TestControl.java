package ichigo.web;

import ichigo.util.LogUtil;

public class TestControl {

  @RequestMapping(value = "/index.html.*")
  public String test() {
    try {
      IchigoSession threadSession = IchigoSession.getThreadSession();
      IchigoBunch ib = new IchigoBunch("table_test");

      ib.add("id");
      ib.put("id", "TEXT PRIMARY KEY");
      ib.add("name");
      ib.createTable();
      ib.clear();
      ib.put("id", "001");
      ib.put("name", "久冨善雄");
      ib.insert();
      ib.put("id", "002");
      ib.put("name", "久冨善雄");
      ib.insert();
      ib.put("id", "003");
      ib.put("name", "久冨善雄");
      ib.insert();
      ib.put("id", "004");
      ib.put("name", "久冨善雄");
      ib.insert();
      ib.clear();
 
      IchigoBunch update = new IchigoBunch("table_test");
      update.put("id", "001");
      update.put("name", "名前更新テスト");
      update.updateWithKey("id");

      update.put("id", "002");
      update.deleteWithKey("id");

      ib.selectSql("select * from table_test");

      LogUtil.log("結果　　　：" + ib);
      LogUtil.log("結果配列　：" + ib.toArrayString());
      LogUtil.log("結果マップ：" + ib.toMapString());

      String t = threadSession.getStringFile("html/test.html");
      IchigoBunch temp = new IchigoBunch(t);
      temp.put("test", "置き換えテスト");
      return temp.replaceTemp();


    } catch (Exception e) {
      e.printStackTrace();
      return e.getMessage();
    }
  }

  public String toString() {
    return "TestControl.toString メソッド";

  }

}
