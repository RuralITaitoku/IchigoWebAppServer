package ichigo.util;

import java.net.*;
import java.io.*;
import java.util.*;

public class CoordinateUtil {

	public static double EQUATORIAL_RADIUS = 6378137;

  public static double rad(double angle) {
    return angle * Math.PI / 180;
  }

  public static double calcDistance(double lat1, double lon1
                            , double lat2, double lon2) {
    double a = Math.sin(rad(lat1)) * Math.sin(rad(lat2))
              + Math.cos(rad(lat1)) * Math.cos(rad(lat2))
                * Math.cos(rad(lon2) - rad(lon1));
    double distance = EQUATORIAL_RADIUS * Math.acos(a);
    return distance;
  }

  public static double calcAreaOfTriangle(double a, double b, double c) {
    double s = (a + b + c) / 2;
    return Math.sqrt(s * (s-a) * (s - b) * (s - c));
  }


}
