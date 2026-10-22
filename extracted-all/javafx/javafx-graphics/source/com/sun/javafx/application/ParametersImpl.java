package com.sun.javafx.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javafx.application.Application;

public class ParametersImpl extends Application.Parameters {
   private List<String> rawArgs = new ArrayList<>();
   private Map<String, String> namedParams = new HashMap<>();
   private List<String> unnamedParams = new ArrayList<>();
   private List<String> readonlyRawArgs = null;
   private Map<String, String> readonlyNamedParams = null;
   private List<String> readonlyUnnamedParams = null;
   private static Map<Application, Application.Parameters> params = new HashMap<>();

   public ParametersImpl() {
   }

   public ParametersImpl(List<String> var1) {
      if (var1 != null) {
         this.init(var1);
      }
   }

   public ParametersImpl(String[] var1) {
      if (var1 != null) {
         this.init(Arrays.asList(var1));
      }
   }

   public ParametersImpl(Map var1, String[] var2) {
      this.init(var1, var2);
   }

   private void init(List<String> var1) {
      for (String var3 : var1) {
         if (var3 != null) {
            this.rawArgs.add(var3);
         }
      }

      this.computeNamedParams();
      this.computeUnnamedParams();
   }

   private void init(Map var1, String[] var2) {
      for (Object var4 : var1.entrySet()) {
         Object var5 = ((Entry)var4).getKey();
         if (this.validKey(var5)) {
            Object var6 = var1.get(var5);
            if (var6 instanceof String) {
               this.namedParams.put((String)var5, (String)var6);
            }
         }
      }

      this.computeRawArgs();
      if (var2 != null) {
         for (String var10 : var2) {
            this.unnamedParams.add(var10);
            this.rawArgs.add(var10);
         }
      }
   }

   private boolean validFirstChar(char var1) {
      return Character.isLetter(var1) || var1 == '_';
   }

   private boolean validKey(Object var1) {
      if (var1 instanceof String) {
         String var2 = (String)var1;
         if (var2.length() > 0 && var2.indexOf(61) < 0) {
            return this.validFirstChar(var2.charAt(0));
         }
      }

      return false;
   }

   private boolean isNamedParam(String var1) {
      return !var1.startsWith("--") ? false : var1.indexOf(61) > 2 && this.validFirstChar(var1.charAt(2));
   }

   private void computeUnnamedParams() {
      for (String var2 : this.rawArgs) {
         if (!this.isNamedParam(var2)) {
            this.unnamedParams.add(var2);
         }
      }
   }

   private void computeNamedParams() {
      for (String var2 : this.rawArgs) {
         if (this.isNamedParam(var2)) {
            int var3 = var2.indexOf(61);
            String var4 = var2.substring(2, var3);
            String var5 = var2.substring(var3 + 1);
            this.namedParams.put(var4, var5);
         }
      }
   }

   private void computeRawArgs() {
      ArrayList var1 = new ArrayList();
      var1.addAll(this.namedParams.keySet());
      Collections.sort(var1);

      for (String var3 : var1) {
         this.rawArgs.add("--" + var3 + "=" + this.namedParams.get(var3));
      }
   }

   @Override
   public List<String> getRaw() {
      if (this.readonlyRawArgs == null) {
         this.readonlyRawArgs = Collections.unmodifiableList(this.rawArgs);
      }

      return this.readonlyRawArgs;
   }

   @Override
   public Map<String, String> getNamed() {
      if (this.readonlyNamedParams == null) {
         this.readonlyNamedParams = Collections.unmodifiableMap(this.namedParams);
      }

      return this.readonlyNamedParams;
   }

   @Override
   public List<String> getUnnamed() {
      if (this.readonlyUnnamedParams == null) {
         this.readonlyUnnamedParams = Collections.unmodifiableList(this.unnamedParams);
      }

      return this.readonlyUnnamedParams;
   }

   public static Application.Parameters getParameters(Application var0) {
      return params.get(var0);
   }

   public static void registerParameters(Application var0, Application.Parameters var1) {
      params.put(var0, var1);
   }
}
