package nonapi.io.github.classgraph.types;

public class ParseException extends Exception {
   static final long serialVersionUID = 1L;

   public ParseException(Parser parser, String msg) {
      super(parser == null ? msg : msg + " (" + parser.getPositionInfo() + ")");
   }
}
