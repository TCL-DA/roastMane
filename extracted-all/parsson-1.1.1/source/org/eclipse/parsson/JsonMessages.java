package org.eclipse.parsson;

import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;
import jakarta.json.stream.JsonLocation;
import jakarta.json.stream.JsonParser.Event;
import java.text.MessageFormat;
import java.util.ResourceBundle;

final class JsonMessages {
   private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("org.eclipse.parsson.messages");

   static String INTERNAL_ERROR() {
      return localize("internal.error");
   }

   static String TOKENIZER_UNEXPECTED_CHAR(int unexpected, JsonLocation location) {
      return localize("tokenizer.unexpected.char", unexpected, location);
   }

   static String TOKENIZER_EXPECTED_CHAR(int unexpected, JsonLocation location, char expected) {
      return localize("tokenizer.expected.char", unexpected, location, expected);
   }

   static String TOKENIZER_IO_ERR() {
      return localize("tokenizer.io.err");
   }

   static String PARSER_GETSTRING_ERR(Event event) {
      return localize("parser.getString.err", event);
   }

   static String PARSER_ISINTEGRALNUMBER_ERR(Event event) {
      return localize("parser.isIntegralNumber.err", event);
   }

   static String PARSER_GETINT_ERR(Event event) {
      return localize("parser.getInt.err", event);
   }

   static String PARSER_GETLONG_ERR(Event event) {
      return localize("parser.getLong.err", event);
   }

   static String PARSER_GETBIGDECIMAL_ERR(Event event) {
      return localize("parser.getBigDecimal.err", event);
   }

   static String PARSER_GETARRAY_ERR(Event event) {
      return localize("parser.getArray.err", event);
   }

   static String PARSER_GETOBJECT_ERR(Event event) {
      return localize("parser.getObject.err", event);
   }

   static String PARSER_GETVALUE_ERR(Event event) {
      return localize("parser.getValue.err", event);
   }

   static String PARSER_GETVALUESTREAM_ERR() {
      return localize("parser.getValueStream.err");
   }

   static String PARSER_EXPECTED_EOF(JsonTokenizer.JsonToken token) {
      return localize("parser.expected.eof", token);
   }

   static String PARSER_TOKENIZER_CLOSE_IO() {
      return localize("parser.tokenizer.close.io");
   }

   static String PARSER_INVALID_TOKEN(JsonTokenizer.JsonToken token, JsonLocation location, String expectedTokens) {
      return localize("parser.invalid.token", token, location, expectedTokens);
   }

   static String PARSER_STATE_ERR(ValueType type) {
      return localize("parser.state.err", type);
   }

   static String PARSER_SCOPE_ERR(JsonValue value) {
      return localize("parser.scope.err", value);
   }

   static String PARSER_INPUT_ENC_DETECT_FAILED() {
      return localize("parser.input.enc.detect.failed");
   }

   static String PARSER_INPUT_ENC_DETECT_IOERR() {
      return localize("parser.input.enc.detect.ioerr");
   }

   static String DUPLICATE_KEY(String name) {
      return localize("parser.duplicate.key", name);
   }

   static String GENERATOR_FLUSH_IO_ERR() {
      return localize("generator.flush.io.err");
   }

   static String GENERATOR_CLOSE_IO_ERR() {
      return localize("generator.close.io.err");
   }

   static String GENERATOR_WRITE_IO_ERR() {
      return localize("generator.write.io.err");
   }

   static String GENERATOR_ILLEGAL_METHOD(Object scope) {
      return localize("generator.illegal.method", scope);
   }

   static String GENERATOR_DOUBLE_INFINITE_NAN() {
      return localize("generator.double.infinite.nan");
   }

   static String GENERATOR_INCOMPLETE_JSON() {
      return localize("generator.incomplete.json");
   }

   static String GENERATOR_ILLEGAL_MULTIPLE_TEXT() {
      return localize("generator.illegal.multiple.text");
   }

   static String WRITER_WRITE_ALREADY_CALLED() {
      return localize("writer.write.already.called");
   }

   static String READER_READ_ALREADY_CALLED() {
      return localize("reader.read.already.called");
   }

   static String OBJBUILDER_NAME_NULL() {
      return localize("objbuilder.name.null");
   }

   static String OBJBUILDER_VALUE_NULL() {
      return localize("objbuilder.value.null");
   }

   static String OBJBUILDER_OBJECT_BUILDER_NULL() {
      return localize("objbuilder.object.builder.null");
   }

   static String OBJBUILDER_ARRAY_BUILDER_NULL() {
      return localize("objbuilder.array.builder.null");
   }

   static String ARRBUILDER_VALUE_NULL() {
      return localize("arrbuilder.value.null");
   }

   static String ARRBUILDER_OBJECT_BUILDER_NULL() {
      return localize("arrbuilder.object.builder.null");
   }

   static String ARRBUILDER_ARRAY_BUILDER_NULL() {
      return localize("arrbuilder.array.builder.null");
   }

   static String ARRBUILDER_VALUELIST_NULL(int index, int size) {
      return localize("arrbuilder.valuelist.null", index, size);
   }

   static String POINTER_FORMAT_INVALID() {
      return localize("pointer.format.invalid");
   }

   static String POINTER_MAPPING_MISSING(JsonObject object, String key) {
      return localize("pointer.mapping.missing", object, key);
   }

   static String POINTER_REFERENCE_INVALID(ValueType type) {
      return localize("pointer.reference.invalid", type.name());
   }

   static String POINTER_ARRAY_INDEX_ERR(String token) {
      return localize("pointer.array.index.err", token);
   }

   static String POINTER_ARRAY_INDEX_ILLEGAL(String token) {
      return localize("pointer.array.index.illegal", token);
   }

   static String NODEREF_VALUE_ADD_ERR() {
      return localize("noderef.value.add.err");
   }

   static String NODEREF_VALUE_CANNOT_REMOVE() {
      return localize("noderef.value.cannot.remove");
   }

   static String NODEREF_OBJECT_MISSING(String key) {
      return localize("noderef.object.missing", key);
   }

   static String NODEREF_ARRAY_INDEX_ERR(int index, int size) {
      return localize("noderef.array.index.err", index, size);
   }

   static String PATCH_MUST_BE_ARRAY() {
      return localize("patch.must.be.array");
   }

   static String PATCH_MOVE_PROPER_PREFIX(String from, String path) {
      return localize("patch.move.proper.prefix", from, path);
   }

   static String PATCH_MOVE_TARGET_NULL(String from) {
      return localize("patch.move.target.null", from);
   }

   static String PATCH_TEST_FAILED(String path, String value) {
      return localize("patch.test.failed", path, value);
   }

   static String PATCH_ILLEGAL_OPERATION(String operation) {
      return localize("patch.illegal.operation", operation);
   }

   static String PATCH_MEMBER_MISSING(String operation, String member) {
      return localize("patch.member.missing", operation, member);
   }

   static String PATCH_OPERATION_MISSING() {
      return localize("patch.operation.missing");
   }

   private static String localize(String key, Object... args) {
      try {
         String msg = BUNDLE.getString(key);
         return MessageFormat.format(msg, args);
      } catch (Exception var3) {
         return getDefaultMessage(key, args);
      }
   }

   private static String getDefaultMessage(String key, Object... args) {
      StringBuilder sb = new StringBuilder();
      sb.append("[failed to localize] ");
      sb.append(key);
      if (args != null) {
         sb.append('(');

         for (int i = 0; i < args.length; i++) {
            if (i != 0) {
               sb.append(", ");
            }

            sb.append(String.valueOf(args[i]));
         }

         sb.append(')');
      }

      return sb.toString();
   }
}
