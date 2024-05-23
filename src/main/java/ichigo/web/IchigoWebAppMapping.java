package ichigo.web;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;

import ichigo.util.*;
import java.lang.reflect.*;

public class IchigoWebAppMapping {
  private static final String PACKAGE_SEPARATOR = ".";
  private static final String CLASS_SUFFIX = ".class";

  ArrayList<MapData> mapList = new ArrayList<MapData>();

  public IchigoWebAppMapping(String packageName) {
    try {
      init(packageName);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private class MapData {
    public String key;
    public Method method;
    public Object object;

    public MapData(String k, Object obj, Method m) {
      this.key = k;
      this.object = obj;
      this.method = m;
    }

    public Object invoke(String path) {
      if (path.matches(key)) {
        LogUtil.log("invoke " + key + ", " + path);
        try {
          return this.method.invoke(object);
        } catch(Exception e) {
          e.printStackTrace();
        }
      }
      return null;
    }
  }


  public void init(String packageName) throws Exception {

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
              if (name.indexOf(CLASS_SUFFIX) < 0) continue;
              String className = name.replace(CLASS_SUFFIX, "").replace(File.separator, PACKAGE_SEPARATOR);
              initMapping(className);
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
  }

  public void initMapping(String className) {
    try {
      ClassLoader classLoader = ClassLoader.getSystemClassLoader();
      Class cls = classLoader.loadClass(className);

      Method[] mm = cls.getMethods();
      for (Method m : mm) {
        RequestMapping map = m.getAnnotation(RequestMapping.class);
        if (map != null){
          LogUtil.log(map.value() + " -------> " + "("  + cls.getName() + ")." + m.getName());
          //@SuppressWarnin
          @SuppressWarnings("unchecked")
          Constructor<?> constructor = cls.getDeclaredConstructor();
          Object obj = constructor.newInstance();
          MapData data = new MapData(map.value(), obj, m);
          mapList.add(data);
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String invoke(String path) {
    LogUtil.log("----------- invoke::" + path + ":");
    for (MapData data: mapList) {
      Object result = data.invoke(path);
      if (result != null) {
        return result.toString();
      }
    }
    return null;
  }

}
