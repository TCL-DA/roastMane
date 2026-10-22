package com.sun.webkit.network;

import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Map.Entry;

final class CookieStore {
   private static final PlatformLogger logger = PlatformLogger.getLogger(CookieStore.class.getName());
   private static final int MAX_BUCKET_SIZE = 50;
   private static final int TOTAL_COUNT_LOWER_THRESHOLD = 3000;
   private static final int TOTAL_COUNT_UPPER_THRESHOLD = 4000;
   private final Map<String, Map<Cookie, Cookie>> buckets = new HashMap<>();
   private int totalCount = 0;

   Cookie get(Cookie var1) {
      Map var2 = this.buckets.get(var1.getDomain());
      if (var2 == null) {
         return null;
      } else {
         Cookie var3 = (Cookie)var2.get(var1);
         if (var3 == null) {
            return null;
         } else if (var3.hasExpired()) {
            var2.remove(var3);
            this.totalCount--;
            this.log("Expired cookie removed by get", var3, var2);
            return null;
         } else {
            return var3;
         }
      }
   }

   List<Cookie> get(String var1, String var2, boolean var3, boolean var4) {
      if (logger.isLoggable(Level.FINEST)) {
         logger.finest("hostname: [{0}], path: [{1}], secureProtocol: [{2}], httpApi: [{3}]", new Object[]{var1, var2, var3, var4});
      }

      ArrayList var5 = new ArrayList();
      String var6 = var1;

      while (var6.length() > 0) {
         Map var7 = this.buckets.get(var6);
         if (var7 != null) {
            this.find(var5, var7, var1, var2, var3, var4);
         }

         int var8 = var6.indexOf(46);
         if (var8 == -1) {
            break;
         }

         var6 = var6.substring(var8 + 1);
      }

      Collections.sort(var5, new CookieStore.GetComparator());
      long var11 = System.currentTimeMillis();

      for (Cookie var10 : var5) {
         var10.setLastAccessTime(var11);
      }

      logger.finest("result: {0}", new Object[]{var5});
      return var5;
   }

   private void find(List<Cookie> var1, Map<Cookie, Cookie> var2, String var3, String var4, boolean var5, boolean var6) {
      Iterator var7 = var2.values().iterator();

      while (var7.hasNext()) {
         Cookie var8 = (Cookie)var7.next();
         if (var8.hasExpired()) {
            var7.remove();
            this.totalCount--;
            this.log("Expired cookie removed by find", var8, var2);
         } else if ((var8.getHostOnly() ? var3.equalsIgnoreCase(var8.getDomain()) : Cookie.domainMatches(var3, var8.getDomain()))
            && Cookie.pathMatches(var4, var8.getPath())
            && (!var8.getSecureOnly() || var5)
            && (!var8.getHttpOnly() || var6)) {
            var1.add(var8);
         }
      }
   }

   void put(Cookie var1) {
      Object var2 = this.buckets.get(var1.getDomain());
      if (var2 == null) {
         var2 = new LinkedHashMap(20);
         this.buckets.put(var1.getDomain(), (Map<Cookie, Cookie>)var2);
      }

      if (var1.hasExpired()) {
         this.log("Cookie expired", var1, (Map<Cookie, Cookie>)var2);
         if (var2.remove(var1) != null) {
            this.totalCount--;
            this.log("Expired cookie removed by put", var1, (Map<Cookie, Cookie>)var2);
         }
      } else if (var2.put(var1, var1) == null) {
         this.totalCount++;
         this.log("Cookie added", var1, (Map<Cookie, Cookie>)var2);
         if (var2.size() > 50) {
            this.purge((Map<Cookie, Cookie>)var2);
         }

         if (this.totalCount > 4000) {
            this.purge();
         }
      } else {
         this.log("Cookie updated", var1, (Map<Cookie, Cookie>)var2);
      }
   }

   private void purge(Map<Cookie, Cookie> var1) {
      logger.finest("Purging bucket: {0}", new Object[]{var1.values()});
      Cookie var2 = null;
      Iterator var3 = var1.values().iterator();

      while (var3.hasNext()) {
         Cookie var4 = (Cookie)var3.next();
         if (var4.hasExpired()) {
            var3.remove();
            this.totalCount--;
            this.log("Expired cookie removed", var4, var1);
         } else if (var2 == null || var4.getLastAccessTime() < var2.getLastAccessTime()) {
            var2 = var4;
         }
      }

      if (var1.size() > 50) {
         var1.remove(var2);
         this.totalCount--;
         this.log("Excess cookie removed", var2, var1);
      }
   }

   private void purge() {
      logger.finest("Purging store");
      PriorityQueue var1 = new PriorityQueue<>(this.totalCount / 2, new CookieStore.RemovalComparator());

      for (Entry var3 : this.buckets.entrySet()) {
         Map var4 = (Map)var3.getValue();
         Iterator var5 = var4.values().iterator();

         while (var5.hasNext()) {
            Cookie var6 = (Cookie)var5.next();
            if (var6.hasExpired()) {
               var5.remove();
               this.totalCount--;
               this.log("Expired cookie removed", var6, var4);
            } else {
               var1.add(var6);
            }
         }
      }

      while (this.totalCount > 3000) {
         Cookie var7 = (Cookie)var1.remove();
         Map var8 = this.buckets.get(var7.getDomain());
         if (var8 != null) {
            var8.remove(var7);
            this.totalCount--;
            this.log("Excess cookie removed", var7, var8);
         }
      }
   }

   private void log(String var1, Cookie var2, Map<Cookie, Cookie> var3) {
      if (logger.isLoggable(Level.FINEST)) {
         logger.finest("{0}: {1}, bucket size: {2}, total count: {3}", new Object[]{var1, var2, var3.size(), this.totalCount});
      }
   }

   private static final class GetComparator implements Comparator<Cookie> {
      public int compare(Cookie var1, Cookie var2) {
         int var3 = var2.getPath().length() - var1.getPath().length();
         return var3 != 0 ? var3 : var1.getCreationTime().compareTo(var2.getCreationTime());
      }
   }

   private static final class RemovalComparator implements Comparator<Cookie> {
      public int compare(Cookie var1, Cookie var2) {
         return (int)(var1.getLastAccessTime() - var2.getLastAccessTime());
      }
   }
}
