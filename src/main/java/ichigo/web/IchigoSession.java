package ichigo.web;

import ichigo.util.*;
import java.util.*;
import java.io.*;
public class IchigoSession {

  public static String topDir = null;
  private static Map<String, IchigoSession> sessionMap = null;

  public static IchigoSession getThreadSession() {
    if (sessionMap == null) {
      sessionMap = new HashMap<String, IchigoSession>();
    }
    Thread current = Thread.currentThread();
    String key = "Thread-" + current.getId();
    IchigoSession session = sessionMap.get(key);
    if (session == null) {
      session = new IchigoSession();
      sessionMap.put(key, session);
    }
    return session;
  }

  public String getRandomKey() {
    char[] keyElement = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
        , 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
        , 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    char[] key = new char[16];
    for (int i = 0; i < key.length; i++) {
      int rint = (int) ((double)keyElement.length * Math.random());
      key[i] = keyElement[rint % keyElement.length];
    }
    String strKey = new String(key);
    System.out.println("ランダムキー：" + strKey);
    return strKey;
  }
  public static void setTopDir(String dir) {
      IchigoSession.topDir = dir;
  }
  public static String getTopDir() {
    return IchigoSession.topDir;
  }


  Command requestCommand;
  Command responseCommand;

  public void setRequest(Command req) {
    this.requestCommand = req;
  }

  public Command getRequest() {
    return this.requestCommand;
  }

  public void setResponse(Command res) {
    this.responseCommand = res;
  }

  public Command getResponse() {
    return this.responseCommand;
  }

  public byte[] getFile() {
    return HttpUtil.getFile(this.requestCommand, IchigoSession.topDir);
  }
  public String getStringFile() {
    return HttpUtil.getStringFile(this.requestCommand, IchigoSession.topDir);
  }
  public String getStringFile(String name) {
    ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    LogUtil.log("name=" + name);
    InputStream in = classLoader.getResourceAsStream(name);
    LogUtil.log("in=" + in);
    String data = IOUtil.readString(in);
    return data;
  }

}
