package local;
import java.security.Permission;
public final class Launcher {
 @SuppressWarnings("removal") public static void main(String[] args){
  System.setProperty("org.cropster.roastingintelligence.instance","otloffline");
  System.setProperty("org.cropster.roastingintelligence.version","5.13");
  System.setProperty("com.cropster.roastingintelligence.segment.writeKey","");
  // Chặn kết nối mạng của JVM; Modbus RTU qua cổng serial không cần socket.
  System.setSecurityManager(new SecurityManager(){public void checkPermission(Permission p){} public void checkConnect(String host,int port){throw new SecurityException("Offline mode blocks network: "+host+":"+port);} public void checkConnect(String h,int p,Object c){checkConnect(h,p);}});
  org.cropster.roastingintelligence.RoastingIntelligenceApp.main(args);
 }
}
