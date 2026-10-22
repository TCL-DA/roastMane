package nonapi.io.github.classgraph.scanspec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import nonapi.io.github.classgraph.utils.CollectionUtils;
import nonapi.io.github.classgraph.utils.FastPathResolver;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.JarUtils;

public abstract class AcceptReject {
   protected Set<String> accept;
   protected Set<String> reject;
   protected Set<String> acceptPrefixesSet;
   protected List<String> acceptPrefixes;
   protected List<String> rejectPrefixes;
   protected Set<String> acceptGlobs;
   protected Set<String> rejectGlobs;
   protected transient List<Pattern> acceptPatterns;
   protected transient List<Pattern> rejectPatterns;
   protected char separatorChar;

   public AcceptReject() {
   }

   public AcceptReject(char separatorChar) {
      this.separatorChar = separatorChar;
   }

   public abstract void addToAccept(String var1);

   public abstract void addToReject(String var1);

   public abstract boolean isAcceptedAndNotRejected(String var1);

   public abstract boolean isAccepted(String var1);

   public abstract boolean acceptHasPrefix(String var1);

   public abstract boolean isRejected(String var1);

   public static String normalizePath(String path) {
      String pathResolved = FastPathResolver.resolve(path);

      while (pathResolved.startsWith("/")) {
         pathResolved = pathResolved.substring(1);
      }

      return pathResolved;
   }

   public static String normalizePackageOrClassName(String packageOrClassName) {
      return normalizePath(packageOrClassName.replace('.', '/')).replace('/', '.');
   }

   public static String pathToPackageName(String path) {
      return path.replace('/', '.');
   }

   public static String packageNameToPath(String packageName) {
      return packageName.replace('.', '/');
   }

   public static String classNameToClassfilePath(String className) {
      return JarUtils.classNameToClassfilePath(className);
   }

   public static Pattern globToPattern(String glob, boolean simpleGlob) {
      return Pattern.compile(
         "^"
            + (
               simpleGlob
                  ? glob.replace(".", "\\.").replace("*", ".*")
                  : glob.replace(".", "\\.").replace("*", "[^/]*").replace("[^/]*[^/]*", ".*").replace('?', '.')
            )
            + "$"
      );
   }

   private static boolean matchesPatternList(String str, List<Pattern> patterns) {
      if (patterns != null) {
         for (Pattern pattern : patterns) {
            if (pattern.matcher(str).matches()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean acceptIsEmpty() {
      return this.accept == null && this.acceptPrefixes == null && this.acceptGlobs == null;
   }

   public boolean rejectIsEmpty() {
      return this.reject == null && this.rejectPrefixes == null && this.rejectGlobs == null;
   }

   public boolean acceptAndRejectAreEmpty() {
      return this.acceptIsEmpty() && this.rejectIsEmpty();
   }

   public boolean isSpecificallyAcceptedAndNotRejected(String str) {
      return !this.acceptIsEmpty() && this.isAcceptedAndNotRejected(str);
   }

   public boolean isSpecificallyAccepted(String str) {
      return !this.acceptIsEmpty() && this.isAccepted(str);
   }

   void sortPrefixes() {
      if (this.acceptPrefixesSet != null) {
         this.acceptPrefixes = new ArrayList<>(this.acceptPrefixesSet);
      }

      if (this.acceptPrefixes != null) {
         CollectionUtils.sortIfNotEmpty(this.acceptPrefixes);
      }

      if (this.rejectPrefixes != null) {
         CollectionUtils.sortIfNotEmpty(this.rejectPrefixes);
      }
   }

   private static void quoteList(Collection<String> coll, StringBuilder buf) {
      buf.append('[');
      boolean first = true;

      for (String item : coll) {
         if (first) {
            first = false;
         } else {
            buf.append(", ");
         }

         buf.append('"');

         for (int i = 0; i < item.length(); i++) {
            char c = item.charAt(i);
            if (c == '"') {
               buf.append("\\\"");
            } else {
               buf.append(c);
            }
         }

         buf.append('"');
      }

      buf.append(']');
   }

   @Override
   public String toString() {
      StringBuilder buf = new StringBuilder();
      if (this.accept != null) {
         buf.append("accept: ");
         quoteList(this.accept, buf);
      }

      if (this.acceptPrefixes != null) {
         if (buf.length() > 0) {
            buf.append("; ");
         }

         buf.append("acceptPrefixes: ");
         quoteList(this.acceptPrefixes, buf);
      }

      if (this.acceptGlobs != null) {
         if (buf.length() > 0) {
            buf.append("; ");
         }

         buf.append("acceptGlobs: ");
         quoteList(this.acceptGlobs, buf);
      }

      if (this.reject != null) {
         if (buf.length() > 0) {
            buf.append("; ");
         }

         buf.append("reject: ");
         quoteList(this.reject, buf);
      }

      if (this.rejectPrefixes != null) {
         if (buf.length() > 0) {
            buf.append("; ");
         }

         buf.append("rejectPrefixes: ");
         quoteList(this.rejectPrefixes, buf);
      }

      if (this.rejectGlobs != null) {
         if (buf.length() > 0) {
            buf.append("; ");
         }

         buf.append("rejectGlobs: ");
         quoteList(this.rejectGlobs, buf);
      }

      return buf.toString();
   }

   public static class AcceptRejectLeafname extends AcceptReject.AcceptRejectWholeString {
      public AcceptRejectLeafname() {
      }

      public AcceptRejectLeafname(char separatorChar) {
         super(separatorChar);
      }

      @Override
      public void addToAccept(String str) {
         super.addToAccept(JarUtils.leafName(str));
      }

      @Override
      public void addToReject(String str) {
         super.addToReject(JarUtils.leafName(str));
      }

      @Override
      public boolean isAcceptedAndNotRejected(String str) {
         return super.isAcceptedAndNotRejected(JarUtils.leafName(str));
      }

      @Override
      public boolean isAccepted(String str) {
         return super.isAccepted(JarUtils.leafName(str));
      }

      @Override
      public boolean acceptHasPrefix(String str) {
         throw new IllegalArgumentException("Can only find prefixes of whole strings");
      }

      @Override
      public boolean isRejected(String str) {
         return super.isRejected(JarUtils.leafName(str));
      }
   }

   public static class AcceptRejectPrefix extends AcceptReject {
      public AcceptRejectPrefix() {
      }

      public AcceptRejectPrefix(char separatorChar) {
         super(separatorChar);
      }

      @Override
      public void addToAccept(String str) {
         if (str.contains("*")) {
            throw new IllegalArgumentException("Cannot use a glob wildcard here: " + str);
         } else {
            if (this.acceptPrefixesSet == null) {
               this.acceptPrefixesSet = new HashSet<>();
            }

            this.acceptPrefixesSet.add(str);
         }
      }

      @Override
      public void addToReject(String str) {
         if (str.contains("*")) {
            throw new IllegalArgumentException("Cannot use a glob wildcard here: " + str);
         } else {
            if (this.rejectPrefixes == null) {
               this.rejectPrefixes = new ArrayList<>();
            }

            this.rejectPrefixes.add(str);
         }
      }

      @Override
      public boolean isAcceptedAndNotRejected(String str) {
         boolean isAccepted = this.acceptPrefixes == null;
         if (!isAccepted) {
            for (String prefix : this.acceptPrefixes) {
               if (str.startsWith(prefix)) {
                  isAccepted = true;
                  break;
               }
            }
         }

         if (!isAccepted) {
            return false;
         } else {
            if (this.rejectPrefixes != null) {
               for (String prefixx : this.rejectPrefixes) {
                  if (str.startsWith(prefixx)) {
                     return false;
                  }
               }
            }

            return true;
         }
      }

      @Override
      public boolean isAccepted(String str) {
         boolean isAccepted = this.acceptPrefixes == null;
         if (!isAccepted) {
            for (String prefix : this.acceptPrefixes) {
               if (str.startsWith(prefix)) {
                  isAccepted = true;
                  break;
               }
            }
         }

         return isAccepted;
      }

      @Override
      public boolean acceptHasPrefix(String str) {
         throw new IllegalArgumentException("Can only find prefixes of whole strings");
      }

      @Override
      public boolean isRejected(String str) {
         if (this.rejectPrefixes != null) {
            for (String prefix : this.rejectPrefixes) {
               if (str.startsWith(prefix)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public static class AcceptRejectWholeString extends AcceptReject {
      public AcceptRejectWholeString() {
      }

      public AcceptRejectWholeString(char separatorChar) {
         super(separatorChar);
      }

      @Override
      public void addToAccept(String str) {
         if (str.contains("*")) {
            if (this.acceptGlobs == null) {
               this.acceptGlobs = new HashSet<>();
               this.acceptPatterns = new ArrayList<>();
            }

            this.acceptGlobs.add(str);
            this.acceptPatterns.add(globToPattern(str, true));
         } else {
            if (this.accept == null) {
               this.accept = new HashSet<>();
            }

            this.accept.add(str);
         }

         if (this.acceptPrefixesSet == null) {
            this.acceptPrefixesSet = new HashSet<>();
            this.acceptPrefixesSet.add("");
            this.acceptPrefixesSet.add("/");
         }

         String separator = Character.toString(this.separatorChar);
         String prefix = str;
         if (str.contains("*")) {
            prefix = str.substring(0, str.indexOf(42));
            int sepIdx = prefix.lastIndexOf(this.separatorChar);
            if (sepIdx < 0) {
               prefix = "";
            } else {
               prefix = prefix.substring(0, prefix.lastIndexOf(this.separatorChar));
            }
         }

         while (prefix.endsWith(separator)) {
            prefix = prefix.substring(0, prefix.length() - 1);
         }

         while (!prefix.isEmpty()) {
            this.acceptPrefixesSet.add(prefix + this.separatorChar);
            prefix = FileUtils.getParentDirPath(prefix, this.separatorChar);
         }
      }

      @Override
      public void addToReject(String str) {
         if (str.contains("*")) {
            if (this.rejectGlobs == null) {
               this.rejectGlobs = new HashSet<>();
               this.rejectPatterns = new ArrayList<>();
            }

            this.rejectGlobs.add(str);
            this.rejectPatterns.add(globToPattern(str, true));
         } else {
            if (this.reject == null) {
               this.reject = new HashSet<>();
            }

            this.reject.add(str);
         }
      }

      @Override
      public boolean isAcceptedAndNotRejected(String str) {
         return this.isAccepted(str) && !this.isRejected(str);
      }

      @Override
      public boolean isAccepted(String str) {
         return this.accept == null && this.acceptPatterns == null
            || this.accept != null && this.accept.contains(str)
            || AcceptReject.matchesPatternList(str, this.acceptPatterns);
      }

      @Override
      public boolean acceptHasPrefix(String str) {
         return this.acceptPrefixesSet == null ? false : this.acceptPrefixesSet.contains(str);
      }

      @Override
      public boolean isRejected(String str) {
         return this.reject != null && this.reject.contains(str) || AcceptReject.matchesPatternList(str, this.rejectPatterns);
      }
   }
}
