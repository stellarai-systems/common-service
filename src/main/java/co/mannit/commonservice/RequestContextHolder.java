package co.mannit.commonservice;

//RequestContextHolder.java


public class RequestContextHolder {
 private static final ThreadLocal<String> xxxIdHolder = new ThreadLocal<>();

 public static void set(String xxxid) {
     xxxIdHolder.set(xxxid);
 }

 public static String get() {
     return xxxIdHolder.get();
 }

 public static void clear() {
     xxxIdHolder.remove();
 }
}

