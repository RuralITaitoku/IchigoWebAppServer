package ichigo.util;

import java.io.*;

public class HttpUtil {

  String topDir;


  public HttpUtil(String top) {
    this.topDir = top;

  }

  public void http11(Command reqCmd, InputStream in, OutputStream out) {
    String httpCmd = reqCmd.getArgs(0);
    if ("GET".equals(httpCmd)) {
      LogUtil.blue("--");
      http11GetFile(reqCmd, in, out);
    }
  }
  public void http11GetFile(Command reqCmd, InputStream in, OutputStream out) {
    String path = reqCmd.getArgs(1);
    LogUtil.blue("path = " + path);
    File file = new File(topDir + path);
    if (file.isDirectory() == true) {
      int l = path.length();
      if (l > 0) {
        char last = path.charAt(l - 1);
        if (last == '/') {
          file = new File(topDir + path + "index.html");
        } else {
          file = new File(topDir + path + "/index.html");
        }
      } else {
        file = new File(topDir + path + "index.html");
      }
    }
    LogUtil.blue("" + file);
    if (file.exists() == false) {
      // 404 NotFound
      http11NotFound(reqCmd, in, out);
    } else {
      // http11NotFound(reqCmd, in, out);
      http11File(reqCmd, in, out, file);
    }
  }
  public void http11NotFound(Command reqCmd, InputStream in, OutputStream out) {
    Command resCmd = new Command("HTTP/1.1 404 Not Found\r\n");
    resCmd.putProp("Content-Type", "text/html");
    resCmd.putProp("Connection", "Keep-Alive");
    StringBuilder sb = new StringBuilder();
    sb.append("<html><head>");
    sb.append("<title>404 Not Found</title>");
    sb.append("</head><body>");
    sb.append("<h1>Not Found</h1>");
    sb.append("</body></html>");
    resCmd.setData(sb.toString());
    resCmd.putProp("Content-Length", "" + resCmd.getDataLength());
    try {
      LogUtil.blue(resCmd.toString());
      out.write(resCmd.getCommand());
      out.write(resCmd.getData());
    } catch(Exception e){
      e.printStackTrace();
    }
  }
  public void http11File(Command cmd, InputStream in, OutputStream out, File file) {
    try {
      LogUtil.blue("要求コマンド：" + cmd.getCmdList());
      String host = cmd.getProp("Host");
      LogUtil.blue("Host:" + host);
      Command resCmd = new Command("HTTP/1.1 200 OK\r\n");
      String contentType = getContentType(file);
      LogUtil.blue("ファイル拡張子:" + contentType);
      resCmd.putProp("Content-Type", contentType);
      resCmd.putProp("Connection", "Keep-Alive");
      resCmd.putProp("Vary", "negotiate");
      resCmd.putProp("TCN", "choice");
      byte[] data = IOUtil.loadBytes(file);
      resCmd.setData(data);
      resCmd.putProp("Content-Length", "" + resCmd.getDataLength());
      LogUtil.blue("TopDir:" + this.topDir);
      String location = file.getAbsolutePath().substring(this.topDir.length());
      resCmd.putProp("Location", "http://" + host.trim() +"/" + location);
      resCmd.putProp("Content-Location", "http://" + host.trim() +"/" + location);
      LogUtil.blue(resCmd.toString());
      out.write(resCmd.getCommand());
      out.write(resCmd.getData());
    } catch(Exception e){
      e.printStackTrace();
    }
  }
  public static String getContentType(File f) {
    String fileName = f.getName();
    LogUtil.blue("ファイル名:" + fileName);
    String ext = fileName.substring(fileName.lastIndexOf("."));
    if (".jpg".equals(ext)) {
      return "image/jpeg";
    } else if (".png".equals(ext)) {
      return "image/png";
    } else if (".mp4".equals(ext)) {
      return "video/mp4";
    } else {
      return "text/html";
    }
  }
  public static Command getOkResponse(Command req, String html) throws Exception {

    byte[] data = html.getBytes("UTF-8");

    return getOkResponse(req, data);
  }

  public static Command getOkResponse(Command req, byte[] data) {
    String path = req.getRequestPath();
    String host = req.getProp("Host");
    Command resCmd = new Command("HTTP/1.1 200 OK\r\n");
    resCmd.putProp("Connection", "Keep-Alive");
    resCmd.putProp("Vary", "negotiate");
    resCmd.putProp("TCN", "choice");
    resCmd.putProp("Content-Location", "http://" + host.trim() + path);
    resCmd.putProp("Content-Type", "text/html");
    resCmd.putProp("Content-Length", "" + data.length);

    String contentType = req.getProp("Res:Content-Type");
    if (contentType == null) {
      resCmd.putProp("Content-Type", "text/html");
    } else {
      resCmd.putProp("Content-Type", contentType);
      LogUtil.log("Content-Type:" + contentType);
    }
    String location = req.getProp("Res:Location");
    if (location == null){
      resCmd.putProp("Location", "http://" + host.trim() + path);
    } else {
      resCmd.putProp("Location", location);
      resCmd.putProp("Content-Location", location);
      LogUtil.log("Location:" + location);
    }
    resCmd.setData(data);
    return resCmd;
  }

  public static Command getNotFoundResponse(Command reqCmd) {
    Command resCmd = new Command("HTTP/1.1 404 Not Found\r\n");
    resCmd.putProp("Content-Type", "text/html");
    resCmd.putProp("Connection", "Keep-Alive");
    StringBuilder sb = new StringBuilder();
    sb.append("<html><head>");
    sb.append("<title>404 Not Found</title>");
    sb.append("</head><body>");
    sb.append("<h1>Not Found</h1>");
    sb.append("</body></html>");
    resCmd.setData(sb.toString());
    resCmd.putProp("Content-Length", "" + resCmd.getDataLength());
    return resCmd;
  }

  public static byte[] getFile(Command req, String topDir) {
    String path = req.getRequestPath();
    LogUtil.blue("path = " + path);
    File file = new File(topDir + path);
    LogUtil.log("ファイル：" + file);
    if (file.isDirectory() == true) {
      int l = path.length();
      if (l > 0) {
        char last = path.charAt(l - 1);
        if (last == '/') {
          file = new File(topDir + path + "index.html");
        } else {
          file = new File(topDir + path + "/index.html");
        }
      } else {
        file = new File(topDir + path + "index.html");
      }
    }
    try {
      LogUtil.log("ファイル：" + file);
      String contentType = getContentType(file);
      req.putProp("Res:Content-Type", contentType);
      String host = req.getProp("Host");
      String location = file.getAbsolutePath().substring(topDir.length());
      if (location.startsWith("/")) {
        req.putProp("Res:Location", "http://" + host.trim() + location);
      } else {
        req.putProp("Res:Location", "http://" + host.trim() +"/" + location);
      }
      byte[] data = IOUtil.loadBytes(file);
      return data;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static String getStringFile(Command req, String topDir) {
    try {
      byte[] data = getFile(req, topDir);
      if (data != null) {
        return new String(data, "UTF-8");
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static Command getFileResponse(Command req, String topDir) {
    byte[] data = getFile(req, topDir);
    if (data == null) {
      return getNotFoundResponse(req);
    } else {
      Command res = getOkResponse(req, data);

      return res;
    }
  }
}
