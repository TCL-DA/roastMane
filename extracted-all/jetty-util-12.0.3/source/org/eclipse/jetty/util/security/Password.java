package org.eclipse.jetty.util.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.eclipse.jetty.util.StringUtil;

public class Password extends Credential {
   private static final long serialVersionUID = 5062906681431569445L;
   public static final String __OBFUSCATE = "OBF:";
   private String _pw;

   public Password(String password) {
      this._pw = password;

      while (this._pw != null && this._pw.startsWith("OBF:")) {
         this._pw = deobfuscate(this._pw);
      }
   }

   @Override
   public String toString() {
      return this._pw;
   }

   public String toStarString() {
      return "*****************************************************".substring(0, this._pw.length());
   }

   @Override
   public boolean check(Object credentials) {
      if (this == credentials) {
         return true;
      } else if (credentials instanceof Password) {
         return credentials.equals(this._pw);
      } else if (credentials instanceof String) {
         return stringEquals(this._pw, (String)credentials);
      } else if (credentials instanceof char[]) {
         return stringEquals(this._pw, new String((char[])credentials));
      } else {
         return credentials instanceof Credential ? ((Credential)credentials).check(this._pw) : false;
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (null == o) {
         return false;
      } else if (o instanceof Password) {
         return stringEquals(this._pw, ((Password)o)._pw);
      } else {
         return o instanceof String ? stringEquals(this._pw, (String)o) : false;
      }
   }

   @Override
   public int hashCode() {
      return null == this._pw ? super.hashCode() : this._pw.hashCode();
   }

   public static String obfuscate(String s) {
      StringBuilder buf = new StringBuilder();
      byte[] b = s.getBytes(StandardCharsets.UTF_8);
      buf.append("OBF:");

      for (int i = 0; i < b.length; i++) {
         byte b1 = b[i];
         byte b2 = b[b.length - (i + 1)];
         if (b1 >= 0 && b2 >= 0) {
            int i1 = 127 + b1 + b2;
            int i2 = 127 + b1 - b2;
            int i0 = i1 * 256 + i2;
            String x = Integer.toString(i0, 36).toLowerCase(Locale.ENGLISH);
            int j0 = Integer.parseInt(x, 36);
            int j1 = i0 / 256;
            int j2 = i0 % 256;
            byte bx = (byte)((j1 + j2 - 254) / 2);
            buf.append("000", 0, 4 - x.length());
            buf.append(x);
         } else {
            int i0 = (255 & b1) * 256 + (255 & b2);
            String x = Integer.toString(i0, 36).toLowerCase(Locale.ENGLISH);
            buf.append("U0000", 0, 5 - x.length());
            buf.append(x);
         }
      }

      return buf.toString();
   }

   public static String deobfuscate(String s) {
      if (s.startsWith("OBF:")) {
         s = s.substring(4);
      }

      byte[] b = new byte[s.length() / 2];
      int l = 0;

      for (int i = 0; i < s.length(); i += 4) {
         if (s.charAt(i) == 'U') {
            String x = s.substring(++i, i + 4);
            int i0 = Integer.parseInt(x, 36);
            byte bx = (byte)(i0 >> 8);
            b[l++] = bx;
         } else {
            String x = s.substring(i, i + 4);
            int i0 = Integer.parseInt(x, 36);
            int i1 = i0 / 256;
            int i2 = i0 % 256;
            byte bx = (byte)((i1 + i2 - 254) / 2);
            b[l++] = bx;
         }
      }

      return new String(b, 0, l, StandardCharsets.UTF_8);
   }

   public static Password getPassword(String realm, String dft, String promptDft) {
      String passwd = System.getProperty(realm, dft);
      if (passwd == null || passwd.length() == 0) {
         try {
            System.out.print(realm + (promptDft != null && promptDft.length() > 0 ? " [dft]" : "") + " : ");
            System.out.flush();
            byte[] buf = new byte[512];
            int len = System.in.read(buf);
            if (len > 0) {
               passwd = new String(buf, 0, len).trim();
            }
         } catch (IOException var6) {
            System.err.println("ERROR: Bad/Invalid password.");
            var6.printStackTrace();
         }

         if (passwd == null || passwd.length() == 0) {
            passwd = promptDft;
         }
      }

      return new Password(passwd);
   }

   public static void main(String[] args) throws IOException {
      boolean promptArgs = false;
      String argUser = null;
      String argPassword = null;

      for (String arg : args) {
         if (arg.equals("--prompt")) {
            promptArgs = true;
            break;
         }

         if (argUser == null) {
            argUser = arg;
            promptArgs = true;
         } else {
            if (!arg.equals("?")) {
               promptArgs = false;
            }

            argPassword = arg;
         }
      }

      if (promptArgs) {
         System.out.print("Username");
         if (StringUtil.isNotBlank(argUser)) {
            System.out.printf("[%s]", argUser);
         }

         System.out.print(": ");
         BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
         String inputUser = input.readLine();
         if (StringUtil.isNotBlank(inputUser)) {
            argUser = inputUser;
         }

         System.out.print("Password: ");
         argPassword = input.readLine();
         if (StringUtil.isBlank(argPassword)) {
            System.err.println("ERROR: blank passwords not supported");
            System.exit(1);
         }
      } else if (StringUtil.isBlank(argUser)) {
         System.err.printf("Usage - java %s [<username>] [<password>] --prompt%n", Password.class.getName());
         System.err.printf("Argument options:%n");
         System.err.printf("  %s%n", Password.class.getName());
         System.err.printf("     No arguments, will show this help%n");
         System.err.printf("  %s <username>%n", Password.class.getName());
         System.err.printf("     username only, will prompt for arguments%n");
         System.err.printf("  %s <username> ?%n", Password.class.getName());
         System.err.printf("     username with question mark password, will prompt for arguments%n");
         System.err.printf("  %s <username> <password>%n", Password.class.getName());
         System.err.printf("     username with password, will produce obfuscation results%n");
         System.err.printf("  %s --prompt%n", Password.class.getName());
         System.err.printf("     will prompt for arguments%n");
         System.exit(1);
      }

      Password pw = new Password(argPassword);
      System.err.println(obfuscate(pw.toString()));
      System.err.println(Credential.MD5.digest(argPassword));
      if (StringUtil.isNotBlank(argUser)) {
         System.err.println(Credential.Crypt.crypt(argUser, pw.toString()));
      }

      System.exit(0);
   }
}
