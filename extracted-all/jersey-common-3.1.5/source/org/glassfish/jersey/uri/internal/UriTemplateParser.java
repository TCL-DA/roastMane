package org.glassfish.jersey.uri.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.glassfish.jersey.internal.LocalizationMessages;
import org.glassfish.jersey.uri.UriComponent;

public class UriTemplateParser {
   static final int[] EMPTY_INT_ARRAY = new int[0];
   private static final Set<Character> RESERVED_REGEX_CHARACTERS = initReserved();
   private static final String[] HEX_TO_UPPERCASE_REGEX = initHexToUpperCaseRegex();
   public static final Pattern TEMPLATE_VALUE_PATTERN = Pattern.compile("[^/]+");
   public static final Pattern TEMPLATE_VALUE_PATTERN_MULTI = Pattern.compile("[^,/]+");
   public static final Pattern MATCH_NUMBER_OF_MAX_LENGTH_4 = Pattern.compile("[1-9][0-9]{0,3}");
   private final String template;
   private final StringBuffer regex = new StringBuffer();
   private final StringBuffer normalizedTemplate = new StringBuffer();
   private final StringBuffer literalCharactersBuffer = new StringBuffer();
   private final Pattern pattern;
   private final List<UriPart> names = new ArrayList<>();
   private final List<UriPart> parts = new ArrayList<>();
   private final List<Integer> groupCounts = new ArrayList<>();
   private final Map<String, Pattern> nameToPattern = new HashMap<>();
   private int numOfExplicitRegexes;
   private int skipGroup;
   private int literalCharacters;

   private static Set<Character> initReserved() {
      char[] reserved = new char[]{'.', '^', '&', '!', '?', '-', ':', '<', '(', '[', '$', '=', ')', ']', ',', '>', '*', '+', '|'};
      Set<Character> s = new HashSet<>(reserved.length);

      for (char c : reserved) {
         s.add(c);
      }

      return s;
   }

   public UriTemplateParser(String template) throws IllegalArgumentException {
      if (template != null && !template.isEmpty()) {
         this.template = template;
         this.parse(new CharacterIterator(template));

         try {
            this.pattern = Pattern.compile(this.regex.toString());
         } catch (PatternSyntaxException var3) {
            throw new IllegalArgumentException("Invalid syntax for the template expression '" + this.regex + "'", var3);
         }
      } else {
         throw new IllegalArgumentException("Template is null or has zero length");
      }
   }

   public final String getTemplate() {
      return this.template;
   }

   public final Pattern getPattern() {
      return this.pattern;
   }

   public final String getNormalizedTemplate() {
      return this.normalizedTemplate.toString();
   }

   public final Map<String, Pattern> getNameToPattern() {
      return this.nameToPattern;
   }

   public final List<UriPart> getNames() {
      return this.names;
   }

   public List<UriPart> getUriParts() {
      return this.parts;
   }

   public final List<Integer> getGroupCounts() {
      return this.groupCounts;
   }

   public final int[] getGroupIndexes() {
      if (this.names.isEmpty()) {
         return EMPTY_INT_ARRAY;
      } else {
         int[] indexes = new int[this.names.size()];
         indexes[0] = 0 + this.groupCounts.get(0);

         for (int i = 1; i < indexes.length; i++) {
            indexes[i] = indexes[i - 1] + this.groupCounts.get(i);
         }

         return indexes;
      }
   }

   public final int getNumberOfExplicitRegexes() {
      return this.numOfExplicitRegexes;
   }

   public final int getNumberOfRegexGroups() {
      if (this.groupCounts.isEmpty()) {
         return 0;
      } else {
         int[] groupIndex = this.getGroupIndexes();
         return groupIndex[groupIndex.length - 1] + this.skipGroup;
      }
   }

   public final int getNumberOfLiteralCharacters() {
      return this.literalCharacters;
   }

   protected String encodeLiteralCharacters(String characters) {
      return characters;
   }

   private void parse(CharacterIterator ci) {
      try {
         while (ci.hasNext()) {
            char c = ci.next();
            if (c == '{') {
               this.processLiteralCharacters();
               this.skipGroup = this.parseName(ci, this.skipGroup);
            } else {
               this.literalCharactersBuffer.append(c);
            }
         }

         this.processLiteralCharacters();
      } catch (NoSuchElementException var3) {
         throw new IllegalArgumentException(LocalizationMessages.ERROR_TEMPLATE_PARSER_INVALID_SYNTAX_TERMINATED(this.template), var3);
      }
   }

   private void processLiteralCharacters() {
      if (this.literalCharactersBuffer.length() > 0) {
         this.literalCharacters = this.literalCharacters + this.literalCharactersBuffer.length();
         String s = this.encodeLiteralCharacters(this.literalCharactersBuffer.toString());
         this.normalizedTemplate.append(s);
         this.parts.add(new UriPart(s));

         for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (RESERVED_REGEX_CHARACTERS.contains(c)) {
               this.regex.append("\\");
               this.regex.append(c);
            } else if (c == '%') {
               char c1 = s.charAt(i + 1);
               char c2 = s.charAt(i + 2);
               if (UriComponent.isHexCharacter(c1) && UriComponent.isHexCharacter(c2)) {
                  this.regex.append("%").append(HEX_TO_UPPERCASE_REGEX[c1]).append(HEX_TO_UPPERCASE_REGEX[c2]);
                  i += 2;
               }
            } else {
               this.regex.append(c);
            }
         }

         this.literalCharactersBuffer.setLength(0);
      }
   }

   private static String[] initHexToUpperCaseRegex() {
      String[] table = new String[128];

      for (int i = 0; i < table.length; i++) {
         table[i] = String.valueOf((char)i);
      }

      for (char c = 'a'; c <= 'f'; c++) {
         table[c] = "[" + c + (char)(c - 'a' + 65) + "]";
      }

      for (char c = 'A'; c <= 'F'; c++) {
         table[c] = "[" + (char)(c - 'A' + 97) + c + "]";
      }

      return table;
   }

   private int parseName(CharacterIterator ci, int skipGroup) {
      UriTemplateParser.Variables variables = new UriTemplateParser.Variables();
      variables.parse(ci, this.template);
      String name = variables.getName();
      int argIndex = 0;

      try {
         Pattern namePattern;
         switch (variables.paramType) {
            case '&':
            case ';':
            case '?':
               StringBuilder regexBuilder = new StringBuilder();
               String separator = null;
               switch (variables.paramType) {
                  case '&':
                     separator = "\\&";
                     regexBuilder.append("\\&");
                     break;
                  case ';':
                     separator = ";/\\?";
                     regexBuilder.append(";");
                     break;
                  case '?':
                     separator = "\\&";
                     regexBuilder.append("\\?");
               }

               regexBuilder.append('(');

               for (String subName : variables.names) {
                  TemplateVariable.Position positionx = determinePosition(variables.separatorCount, argIndex);
                  TemplateVariable templateVariablexx = TemplateVariable.createTemplateVariable(variables.paramType, subName, positionx);
                  templateVariablexx.setStar(variables.explodes(argIndex));
                  regexBuilder.append("(&?");
                  regexBuilder.append(subName);
                  regexBuilder.append("(=([^");
                  regexBuilder.append(separator);
                  regexBuilder.append(']');
                  if (variables.hasLength(argIndex)) {
                     regexBuilder.append('{').append(variables.getLength(argIndex)).append('}');
                     templateVariablexx.setLength(variables.getLength(argIndex));
                  } else {
                     regexBuilder.append('*');
                  }

                  regexBuilder.append("))?");
                  regexBuilder.append(')');
                  if (argIndex != 0) {
                     regexBuilder.append('|');
                  }

                  this.names.add(templateVariablexx);
                  this.parts.add(templateVariablexx);
                  this.groupCounts.add(argIndex == 0 ? 5 : 3);
                  argIndex++;
               }

               skipGroup = 1;
               regexBuilder.append(")*");
               namePattern = Pattern.compile(regexBuilder.toString());
               break;
            default:
               if (variables.separatorCount == 0) {
                  if (variables.hasRegexp(0)) {
                     this.numOfExplicitRegexes++;
                  }

                  TemplateVariable templateVariable = TemplateVariable.createTemplateVariable(
                     variables.paramType, variables.getName(0), TemplateVariable.Position.SINGLE
                  );
                  templateVariable.setStar(variables.explodes(0));
                  this.names.add(templateVariable);
                  this.parts.add(templateVariable);
                  if (variables.hasLength(0)) {
                     if (variables.getLength(0) != 0) {
                        int len = TEMPLATE_VALUE_PATTERN.pattern().length() - 1;
                        String pattern = TEMPLATE_VALUE_PATTERN.pattern().substring(0, len) + '{' + variables.getLength(0) + '}';
                        namePattern = Pattern.compile(pattern);
                     } else {
                        namePattern = TEMPLATE_VALUE_PATTERN;
                     }

                     templateVariable.setLength(variables.getLength(0));
                  } else {
                     namePattern = !variables.hasRegexp(0) ? TEMPLATE_VALUE_PATTERN : Pattern.compile(variables.regexp(0));
                  }

                  if (this.nameToPattern.containsKey(name)) {
                     if (!this.nameToPattern.get(name).equals(namePattern)) {
                        throw new IllegalArgumentException(LocalizationMessages.ERROR_TEMPLATE_PARSER_NAME_MORE_THAN_ONCE(name, this.template));
                     }
                  } else {
                     this.nameToPattern.put(name, namePattern);
                  }

                  Matcher m = namePattern.matcher("");
                  int g = m.groupCount();
                  this.groupCounts.add(1 + skipGroup);
                  skipGroup = g;
               } else {
                  argIndex = 0;
                  StringBuilder regexBuilder = new StringBuilder();

                  for (String subName : variables.names) {
                     if (argIndex != 0) {
                        regexBuilder.append('(').append(',');
                     }

                     TemplateVariable.Position position = determinePosition(variables.separatorCount, argIndex);
                     TemplateVariable templateVariablex = TemplateVariable.createTemplateVariable(variables.paramType, subName, position);
                     templateVariablex.setStar(variables.explodes(argIndex));
                     this.names.add(templateVariablex);
                     this.parts.add(templateVariablex);
                     if (variables.hasLength(argIndex)) {
                        int len = TEMPLATE_VALUE_PATTERN_MULTI.pattern().length() - 1;
                        String pattern = TEMPLATE_VALUE_PATTERN_MULTI.pattern().substring(0, len) + '{' + variables.getLength(argIndex) + '}';
                        namePattern = Pattern.compile(pattern);
                        templateVariablex.setLength(variables.getLength(argIndex));
                     } else {
                        namePattern = !variables.hasRegexp(argIndex) ? TEMPLATE_VALUE_PATTERN_MULTI : Pattern.compile(variables.regexp(argIndex));
                     }

                     if (this.nameToPattern.containsKey(subName) && variables.paramType == 'p') {
                        if (!this.nameToPattern.get(subName).equals(namePattern)) {
                           throw new IllegalArgumentException(LocalizationMessages.ERROR_TEMPLATE_PARSER_NAME_MORE_THAN_ONCE(name, this.template));
                        }
                     } else {
                        this.nameToPattern.put(subName, namePattern);
                     }

                     regexBuilder.append('(').append(namePattern).append(')');
                     if (argIndex != 0) {
                        regexBuilder.append(")");
                     }

                     if (!variables.hasRegexp(argIndex)) {
                        regexBuilder.append("{0,1}");
                     }

                     argIndex++;
                     this.groupCounts.add(2);
                  }

                  namePattern = Pattern.compile(regexBuilder.toString());
               }
         }

         this.regex.append('(').append(namePattern).append(')');
         this.normalizedTemplate.append('{').append(name).append('}');
         return skipGroup;
      } catch (PatternSyntaxException var15) {
         throw new IllegalArgumentException(
            LocalizationMessages.ERROR_TEMPLATE_PARSER_INVALID_SYNTAX(variables.regexp(argIndex), variables.name, this.template), var15
         );
      }
   }

   private static TemplateVariable.Position determinePosition(int separatorCount, int argIndex) {
      return separatorCount == 0
         ? TemplateVariable.Position.SINGLE
         : (argIndex == 0 ? TemplateVariable.Position.FIRST : (argIndex == separatorCount ? TemplateVariable.Position.LAST : TemplateVariable.Position.MIDDLE));
   }

   private static class Variables {
      private char paramType = 'p';
      private List<String> names = new ArrayList<>();
      private List<Boolean> explodes = new ArrayList<>();
      private List<String> regexps = new ArrayList<>();
      private List<Integer> lengths = new ArrayList<>();
      private int separatorCount = 0;
      private StringBuilder name = new StringBuilder();

      private Variables() {
      }

      private int getCount() {
         return this.names.size();
      }

      private boolean explodes(int index) {
         return !this.explodes.isEmpty() && this.explodes.get(index);
      }

      private boolean hasRegexp(int index) {
         return !this.regexps.isEmpty() && this.regexps.get(index) != null;
      }

      private String regexp(int index) {
         return this.regexps.get(index);
      }

      private boolean hasLength(int index) {
         return !this.lengths.isEmpty() && this.lengths.get(index) != null;
      }

      private Integer getLength(int index) {
         return this.lengths.get(index);
      }

      private char getParamType() {
         return this.paramType;
      }

      private int getSeparatorCount() {
         return this.separatorCount;
      }

      private String getName() {
         return this.name.toString();
      }

      private String getName(int index) {
         return this.names.get(index);
      }

      private void parse(CharacterIterator ci, String template) {
         this.name.append('{');
         char c = consumeWhiteSpace(ci);
         StringBuilder nameBuilder = new StringBuilder();
         if (c == '?' || c == ';' || c == '.' || c == '+' || c == '#' || c == '/' || c == '&') {
            this.paramType = c;
            c = ci.next();
            this.name.append(this.paramType);
         }

         if (!Character.isLetterOrDigit(c) && c != '_') {
            throw new IllegalArgumentException(LocalizationMessages.ERROR_TEMPLATE_PARSER_ILLEGAL_CHAR_START_NAME(c, ci.pos(), template));
         } else {
            nameBuilder.append(c);
            this.name.append(c);
            StringBuilder regexBuilder = new StringBuilder();
            UriTemplateParser.Variables.State state = UriTemplateParser.Variables.State.TEMPLATE;
            boolean star = false;
            boolean whiteSpace = false;
            boolean ignoredLastComma = false;
            int bracketDepth = 1;
            int regExpBracket = 0;
            int regExpRound = 0;
            boolean reqExpSlash = false;

            while ((state.value & (UriTemplateParser.Variables.State.ERROR.value | UriTemplateParser.Variables.State.EXIT.value)) == 0) {
               UriTemplateParser.Variables.State previousState = state;
               c = ci.next();
               if (Character.isLetterOrDigit(c)) {
                  append(c, state, nameBuilder, regexBuilder);
                  state = state.transition(UriTemplateParser.Variables.State.TEMPLATE.value | UriTemplateParser.Variables.State.REGEXP.value);
               } else {
                  switch (c) {
                     case '*':
                        state = state.transition(UriTemplateParser.Variables.State.TEMPLATE.value | UriTemplateParser.Variables.State.REGEXP.value);
                        if (state == UriTemplateParser.Variables.State.TEMPLATE) {
                           star = true;
                           state = UriTemplateParser.Variables.State.STAR;
                        } else if (state == UriTemplateParser.Variables.State.REGEXP) {
                           regexBuilder.append(c);
                        }
                        break;
                     case ',':
                        switch (state) {
                           case REGEXP:
                              if (bracketDepth == 1 && !reqExpSlash && regExpBracket == 0 && regExpRound == 0) {
                                 state = UriTemplateParser.Variables.State.COMMA;
                                 break;
                              }

                              regexBuilder.append(c);
                              break;
                           case TEMPLATE:
                           case STAR:
                              state = UriTemplateParser.Variables.State.COMMA;
                        }

                        this.separatorCount++;
                        break;
                     case '-':
                     case '.':
                     case '_':
                        append(c, state, nameBuilder, regexBuilder);
                        state = state.transition(UriTemplateParser.Variables.State.TEMPLATE.value | UriTemplateParser.Variables.State.REGEXP.value);
                        break;
                     case ':':
                        if (state == UriTemplateParser.Variables.State.REGEXP) {
                           regexBuilder.append(c);
                        }

                        state = state.transition(
                           UriTemplateParser.Variables.State.TEMPLATE.value
                              | UriTemplateParser.Variables.State.REGEXP.value
                              | UriTemplateParser.Variables.State.STAR.value,
                           UriTemplateParser.Variables.State.REGEXP
                        );
                        break;
                     case '{':
                        if (state == UriTemplateParser.Variables.State.REGEXP) {
                           bracketDepth++;
                           regexBuilder.append(c);
                        } else {
                           state = UriTemplateParser.Variables.State.ERROR;
                        }
                        break;
                     case '}':
                        if (--bracketDepth == 0) {
                           state = UriTemplateParser.Variables.State.BRACKET;
                        } else {
                           regexBuilder.append(c);
                        }
                        break;
                     default:
                        if (!Character.isWhitespace(c)) {
                           if (state != UriTemplateParser.Variables.State.REGEXP) {
                              state = UriTemplateParser.Variables.State.ERROR;
                           } else {
                              switch (c) {
                                 case '(':
                                    regExpRound++;
                                    break;
                                 case ')':
                                    regExpRound--;
                                    break;
                                 case '[':
                                    regExpBracket++;
                                    break;
                                 case ']':
                                    regExpBracket--;
                              }

                              if (c == '\\') {
                                 reqExpSlash = true;
                              } else {
                                 reqExpSlash = false;
                              }

                              regexBuilder.append(c);
                           }
                        }

                        whiteSpace = true;
                  }
               }

               switch (state) {
                  case COMMA:
                  case BRACKET:
                     if (nameBuilder.length() == 0 && regexBuilder.length() == 0 && !star && this.name.charAt(this.name.length() - 1) == ',') {
                        if (ignoredLastComma) {
                           state = UriTemplateParser.Variables.State.ERROR;
                        } else {
                           this.name.setLength(this.name.length() - 1);
                           ignoredLastComma = true;
                        }
                     } else {
                        label166: {
                           if (regexBuilder.length() != 0) {
                              String regex = regexBuilder.toString();
                              Matcher matcher = UriTemplateParser.MATCH_NUMBER_OF_MAX_LENGTH_4.matcher(regex);
                              if (matcher.matches()) {
                                 this.lengths.add(Integer.parseInt(regex));
                                 this.regexps.add(null);
                              } else {
                                 if (this.paramType != 'p') {
                                    state = UriTemplateParser.Variables.State.ERROR;
                                    c = regex.charAt(0);
                                    ci.setPosition(ci.pos() - regex.length());
                                    break label166;
                                 }

                                 this.lengths.add(null);
                                 this.regexps.add(regex);
                              }
                           } else {
                              this.regexps.add(previousState == UriTemplateParser.Variables.State.REGEXP ? "" : null);
                              this.lengths.add(previousState == UriTemplateParser.Variables.State.REGEXP ? 0 : null);
                           }

                           this.names.add(nameBuilder.toString());
                           this.explodes.add(star);
                           nameBuilder.setLength(0);
                           regexBuilder.setLength(0);
                           star = false;
                           ignoredLastComma = false;
                        }
                     }
                  default:
                     if (!whiteSpace) {
                        this.name.append(c);
                     }

                     whiteSpace = false;
                     switch (state) {
                        case COMMA:
                           state = UriTemplateParser.Variables.State.TEMPLATE;
                           break;
                        case BRACKET:
                           state = UriTemplateParser.Variables.State.EXIT;
                     }
               }
            }

            if (state == UriTemplateParser.Variables.State.ERROR) {
               throw new IllegalArgumentException(LocalizationMessages.ERROR_TEMPLATE_PARSER_ILLEGAL_CHAR_AFTER_NAME(c, ci.pos(), template));
            }
         }
      }

      private static void append(char c, UriTemplateParser.Variables.State state, StringBuilder templateSb, StringBuilder regexpSb) {
         if (state == UriTemplateParser.Variables.State.TEMPLATE) {
            templateSb.append(c);
         } else {
            regexpSb.append(c);
         }
      }

      private static char consumeWhiteSpace(CharacterIterator ci) {
         char c;
         do {
            c = ci.next();
         } while (Character.isWhitespace(c));

         return c;
      }

      private static enum State {
         TEMPLATE(1),
         REGEXP(2),
         STAR(4),
         COMMA(8),
         BRACKET(16),
         EXIT(64),
         ERROR(256);

         private final int value;

         private State(int value) {
            this.value = value;
         }

         UriTemplateParser.Variables.State transition(int allowed) {
            return (this.value & allowed) != 0 ? this : ERROR;
         }

         UriTemplateParser.Variables.State transition(int allowed, UriTemplateParser.Variables.State next) {
            return (this.value & allowed) != 0 ? next : ERROR;
         }
      }
   }
}
