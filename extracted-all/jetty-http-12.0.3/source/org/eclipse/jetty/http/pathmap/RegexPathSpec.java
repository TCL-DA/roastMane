package org.eclipse.jetty.http.pathmap;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegexPathSpec extends AbstractPathSpec {
   private static final Logger LOG = LoggerFactory.getLogger(UriTemplatePathSpec.class);
   private static final Map<Character, String> FORBIDDEN_ESCAPED = new HashMap<>();
   private final String _declaration;
   private final PathSpecGroup _group;
   private final int _pathDepth;
   private final int _specLength;
   private final Pattern _pattern;

   public RegexPathSpec(String regex) {
      String declaration;
      if (regex.startsWith("regex|")) {
         declaration = regex.substring("regex|".length());
      } else {
         declaration = regex;
      }

      int specLength = declaration.length();
      boolean inCharacterClass = false;
      boolean inQuantifier = false;
      boolean inCaptureGroup = false;
      StringBuilder signature = new StringBuilder();
      int pathDepth = 0;
      char last = 0;

      for (int i = 0; i < declaration.length(); i++) {
         char c = declaration.charAt(i);
         switch (c) {
            case '$':
            case '\'':
            case '^':
               break;
            case '(':
               inCaptureGroup = true;
               break;
            case ')':
               inCaptureGroup = false;
               signature.append('g');
               break;
            case '*':
            case '+':
            case '.':
            case '?':
            case '|':
               signature.append('g');
               break;
            case '/':
               if (!inCharacterClass && !inQuantifier && !inCaptureGroup) {
                  pathDepth++;
               }
               break;
            case '[':
               inCharacterClass = true;
               break;
            case ']':
               inCharacterClass = false;
               signature.append('g');
               break;
            case '{':
               inQuantifier = true;
               break;
            case '}':
               inQuantifier = false;
               break;
            default:
               if (!inCharacterClass && !inQuantifier && !inCaptureGroup && Character.isLetterOrDigit(c)) {
                  if (last == '\\') {
                     String forbiddenReason = FORBIDDEN_ESCAPED.get(c);
                     if (forbiddenReason != null) {
                        throw new IllegalArgumentException(
                           String.format("%s does not support \\%c (%s) for \"%s\"", this.getClass().getSimpleName(), c, forbiddenReason, declaration)
                        );
                     }

                     switch (c) {
                        case 'D':
                        case 'S':
                        case 'W':
                        case 'd':
                        case 'w':
                           signature.append('g');
                           break;
                        default:
                           signature.append('l');
                     }
                  } else {
                     signature.append('l');
                  }
               }
         }

         last = c;
      }

      Pattern pattern = Pattern.compile(declaration);
      String sig = signature.toString();
      PathSpecGroup group;
      if (Pattern.matches("^l+$", sig)) {
         group = PathSpecGroup.EXACT;
      } else if (Pattern.matches("^l+g+", sig)) {
         group = PathSpecGroup.PREFIX_GLOB;
      } else if (Pattern.matches("^g+l+.*", sig)) {
         group = PathSpecGroup.SUFFIX_GLOB;
      } else {
         group = PathSpecGroup.MIDDLE_GLOB;
      }

      this._declaration = declaration;
      this._group = group;
      this._pathDepth = pathDepth;
      this._specLength = specLength;
      this._pattern = pattern;
      if (LOG.isDebugEnabled()) {
         LOG.debug("Creating RegexPathSpec[{}] (signature: [{}], group: {})", new Object[]{this._declaration, sig, this._group});
      }
   }

   protected Matcher getMatcher(String path) {
      int idx = path.indexOf(63);
      return idx >= 0 ? this._pattern.matcher(path.substring(0, idx)) : this._pattern.matcher(path);
   }

   @Override
   public int getSpecLength() {
      return this._specLength;
   }

   @Override
   public PathSpecGroup getGroup() {
      return this._group;
   }

   @Override
   public int getPathDepth() {
      return this._pathDepth;
   }

   @Override
   public String getPathInfo(String path) {
      MatchedPath matched = this.matched(path);
      return matched == null ? null : matched.getPathInfo();
   }

   @Override
   public String getPathMatch(String path) {
      MatchedPath matched = this.matched(path);
      return matched == null ? "" : matched.getPathMatch();
   }

   @Override
   public String getDeclaration() {
      return this._declaration;
   }

   @Override
   public String getPrefix() {
      return null;
   }

   @Override
   public String getSuffix() {
      return null;
   }

   public Pattern getPattern() {
      return this._pattern;
   }

   @Override
   public boolean matches(String path) {
      return this.getMatcher(path).matches();
   }

   @Override
   public MatchedPath matched(String path) {
      Matcher matcher = this.getMatcher(path);
      return matcher.matches() ? new RegexPathSpec.RegexMatchedPath(this, path, matcher) : null;
   }

   static {
      FORBIDDEN_ESCAPED.put('s', "any whitespace");
      FORBIDDEN_ESCAPED.put('n', "newline");
      FORBIDDEN_ESCAPED.put('r', "carriage return");
      FORBIDDEN_ESCAPED.put('t', "tab");
      FORBIDDEN_ESCAPED.put('f', "form-feed");
      FORBIDDEN_ESCAPED.put('b', "bell");
      FORBIDDEN_ESCAPED.put('e', "escape");
      FORBIDDEN_ESCAPED.put('c', "control char");
   }

   private class RegexMatchedPath implements MatchedPath {
      private final RegexPathSpec pathSpec;
      private final String path;
      private String pathMatch;
      private String pathInfo;

      public RegexMatchedPath(RegexPathSpec regexPathSpec, String path, Matcher matcher) {
         this.pathSpec = regexPathSpec;
         this.path = path;
         this.calcPathMatchInfo(matcher);
      }

      private void calcPathMatchInfo(Matcher matcher) {
         int groupCount = matcher.groupCount();
         if (groupCount == 0) {
            this.pathMatch = this.path;
            this.pathInfo = null;
         } else {
            if (groupCount == 1) {
               int idxNameEnd = this.endOf(matcher, "name");
               if (idxNameEnd >= 0) {
                  this.pathMatch = this.path.substring(0, idxNameEnd);
                  this.pathInfo = this.path.substring(idxNameEnd);
                  if (this.pathMatch.length() > 0 && this.pathMatch.charAt(this.pathMatch.length() - 1) == '/' && !this.pathInfo.startsWith("/")) {
                     this.pathMatch = this.pathMatch.substring(0, this.pathMatch.length() - 1);
                     this.pathInfo = "/" + this.pathInfo;
                  }

                  return;
               }

               int idx = matcher.start(1);
               if (idx >= 0) {
                  this.pathMatch = this.path.substring(0, idx);
                  this.pathInfo = this.path.substring(idx);
                  if (this.pathMatch.length() > 0 && this.pathMatch.charAt(this.pathMatch.length() - 1) == '/' && !this.pathInfo.startsWith("/")) {
                     this.pathMatch = this.pathMatch.substring(0, this.pathMatch.length() - 1);
                     this.pathInfo = "/" + this.pathInfo;
                  }

                  return;
               }
            }

            String gName = this.valueOf(matcher, "name");
            String gInfo = this.valueOf(matcher, "info");
            if (gName != null && gInfo != null) {
               this.pathMatch = gName;
               this.pathInfo = gInfo;
            } else {
               this.pathMatch = this.path;
               this.pathInfo = null;
            }
         }
      }

      private String valueOf(Matcher matcher, String groupName) {
         try {
            return matcher.group(groupName);
         } catch (IllegalArgumentException var4) {
            return null;
         }
      }

      private int endOf(Matcher matcher, String groupName) {
         try {
            return matcher.end(groupName);
         } catch (IllegalArgumentException var4) {
            return -2;
         }
      }

      @Override
      public String getPathMatch() {
         return this.pathMatch;
      }

      @Override
      public String getPathInfo() {
         return this.pathInfo;
      }

      @Override
      public String toString() {
         return this.getClass().getSimpleName() + "[pathSpec=" + this.pathSpec + ", path=\"" + this.path + "\"]";
      }
   }
}
