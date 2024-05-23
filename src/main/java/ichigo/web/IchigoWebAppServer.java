package ichigo.web;

import ichigo.util.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;
import java.io.*;
import java.awt.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class IchigoWebAppServer {

  private static final String PACKAGE_SEPARATOR = ".";

  private static final String CLASS_SUFFIX = ".class";

  private IchigoWebAppMapping webAppMap;

  public static void main(String[] args) {
    IchigoWebAppServer webAppServer = new IchigoWebAppServer(1581, "/Library/WebServer/Documents");
    webAppServer.start();
  }

  int portNo;
  String topDir;

  public IchigoWebAppServer(int portNo, String dir) {
    this.portNo = portNo;
    webAppMap = new IchigoWebAppMapping(null);
    this.topDir = dir;
  }

  public void start() {


    try {
      this.getResourcesList(null);

      ServerSocket server = new ServerSocket(portNo);
      LogUtil.log("サーバーが起動しました。");
      LogUtil.log("ポート番号        ：" + portNo);

      (new Thread(() -> openApp())).start();


      for(;;) {
        Socket socket = server.accept();
        new Thread(() -> {recv(socket);}).start();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  public void openApp() {

    try {
      Thread.sleep(2000);
      Desktop.getDesktop().browse(new URI("http://localhost:1581/index.html?test=test"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public void recv(Socket socket) {
    LogUtil.red("受信" + socket);
    IchigoSession threadSession = IchigoSession.getThreadSession();
    String sessionKey = null;
    try {
      ByteArrayOutputStream lineBytes = new ByteArrayOutputStream();
      ByteArrayOutputStream reqBytes = new ByteArrayOutputStream();
      BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
      OutputStream out = socket.getOutputStream();
      for(;;) {
        int b = in.read();
        if (b < 0) {
          LogUtil.red("要求終了");
          break;
        }
        lineBytes.write(b);
        reqBytes.write(b);
        if (b == (int)'\n') {
          String line = lineBytes.toString("UTF-8");
          LogUtil.red("C->S  " + line.length() + ":" + line.trim());
          if (line.length() < 3) {
            LogUtil.red("-------空行！");
            Command cmd = new Command(reqBytes.toString("UTF-8"));
            threadSession.setRequest(cmd);
            reqBytes.reset();
            String httpCmd = cmd.getArgs(0);
            String httpPath = cmd.getRequestPath();
            if ("GET".equals(httpCmd)) {
              LogUtil.red("コマンド：" + httpCmd);
              LogUtil.red("ファイル：" + httpPath);
            } else if ("POST".equals(httpCmd)) {
              LogUtil.red("コマンド：" + httpCmd);
              LogUtil.red("ファイル：" + httpPath);
              String clen = cmd.getProp("Content-Length");
              int iClen = Integer.parseInt(clen.trim());
              LogUtil.red("Content-Length：" + iClen);
              for (int pbi = 0; pbi < iClen;pbi++) {
                int pb = in.read();
                reqBytes.write(pb);
              }
              LogUtil.red("データ部分：" + reqBytes.toString("UTF-8"));
              cmd.setData(reqBytes.toByteArray());
              reqBytes.reset();
            }


            String result = webAppMap.invoke(httpPath);
            LogUtil.log("WebApp呼び出結果" + result);
            Command res = null;
            if (result == null) {
              res = HttpUtil.getFileResponse(cmd, topDir);
            } else {
              res = HttpUtil.getOkResponse(cmd, result);
            }
            out.write(res.getCommand());
            out.write(res.getData());
        }
          lineBytes.reset();
        }
      }
    } catch(Exception e) {
      LogUtil.red("ソケット切断！");
      try {
        socket.close();
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }
      e.printStackTrace();
    }
  }


  public ArrayList<String> getResourcesList(String packageName) throws Exception {
      ArrayList<String> classNameList = new ArrayList<String>();

      // クラスローダを利用して、パッケージ配下のリソースを取得する。
      if (packageName == null) {
        packageName = this.getClass().getPackage().getName();
      }
      System.out.println("packageName:" + packageName);
      String rootPackageName = packageName.replace(PACKAGE_SEPARATOR, File.separator);
      ClassLoader classLoader = ClassLoader.getSystemClassLoader();
      Enumeration<URL> rootUrls = classLoader.getResources(rootPackageName);
      URL rootURL = null;
      if (rootUrls.hasMoreElements()) {
        rootURL = rootUrls.nextElement();
      }
      System.out.println("rootURL: " + rootURL);
      String fileName = rootURL.toString();
      // .replaceAll("!/" + rootPackageName);
      fileName = fileName.substring(9);
      fileName = fileName.replaceAll("!/" + packageName, "");
      System.out.println("fileName: " + fileName);

      JarFile jarFile = null;

      try {
          jarFile = new JarFile(new File(fileName));

          for (Enumeration e = jarFile.entries(); e.hasMoreElements();) {
              JarEntry entry = (JarEntry)e.nextElement();
              // 結果を出力
              String name = entry.getName();
              if (name.indexOf("$") >= 0) continue;
              if (name.indexOf(".class") < 0) continue;
              String className = name.replace(File.separator, PACKAGE_SEPARATOR);
              classNameList.add(className);
              //System.out.println(className);
          }
      } catch (IOException e) {
          // エラー処理
      } finally {
          try {
              if (jarFile != null) {
                jarFile.close();
              }
          } catch (IOException e) {
              e.printStackTrace();
          }
      }
      return classNameList;

  }



}
