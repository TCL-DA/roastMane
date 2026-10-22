package org.eclipse.jetty.http;

import java.nio.ByteBuffer;
import java.util.EnumMap;
import java.util.ServiceLoader;
import org.eclipse.jetty.util.TypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PreEncodedHttpField extends HttpField {
   private static final Logger LOG = LoggerFactory.getLogger(PreEncodedHttpField.class);
   private static final EnumMap<HttpVersion, HttpFieldPreEncoder> __encoders = new EnumMap<>(HttpVersion.class);
   private final EnumMap<HttpVersion, byte[]> _encodedFields = new EnumMap<>(HttpVersion.class);
   private final long _longValue;

   private PreEncodedHttpField(HttpHeader header, String name, String value, long longValue) {
      super(header, name, value);
      this._longValue = longValue;

      for (HttpFieldPreEncoder encoder : __encoders.values()) {
         HttpVersion version = encoder.getHttpVersion();
         this._encodedFields
            .put(
               encoder.getHttpVersion(),
               version == HttpVersion.HTTP_1_1 ? this._encodedFields.get(HttpVersion.HTTP_1_0) : encoder.getEncodedField(header, name, value)
            );
      }
   }

   public PreEncodedHttpField(HttpHeader header, String name, String value) {
      this(header, name, value, Long.MIN_VALUE);
   }

   public PreEncodedHttpField(HttpHeader header, String value) {
      this(header, header.asString(), value);
   }

   public PreEncodedHttpField(HttpHeader header, long value) {
      this(header, header.asString(), Long.toString(value), value);
   }

   public PreEncodedHttpField(String name, String value) {
      this(null, name, value);
   }

   public PreEncodedHttpField(String name, long value) {
      this(null, name, Long.toString(value), value);
   }

   public void putTo(ByteBuffer bufferInFillMode, HttpVersion version) {
      bufferInFillMode.put(this._encodedFields.get(version));
   }

   public int getEncodedLength(HttpVersion version) {
      return this._encodedFields.get(version).length;
   }

   @Override
   public boolean contains(String search) {
      return super.contains(search);
   }

   @Override
   public int getIntValue() {
      return this._longValue == Long.MIN_VALUE ? super.getIntValue() : Math.toIntExact(this._longValue);
   }

   @Override
   public long getLongValue() {
      return this._longValue == Long.MIN_VALUE ? (long)super.getIntValue() : this._longValue;
   }

   static {
      TypeUtil.serviceProviderStream(ServiceLoader.load(HttpFieldPreEncoder.class)).forEach(provider -> {
         try {
            HttpFieldPreEncoder encoder = (HttpFieldPreEncoder)provider.get();
            HttpFieldPreEncoder existing = __encoders.put(encoder.getHttpVersion(), encoder);
            if (existing != null) {
               LOG.warn("multiple {} for {}", HttpFieldPreEncoder.class.getSimpleName(), encoder.getHttpVersion());
            }
         } catch (RuntimeException | Error var3) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("unable to add {}", HttpFieldPreEncoder.class.getSimpleName(), var3);
            }
         }
      });
      if (LOG.isDebugEnabled()) {
         LOG.debug("loaded {} {}s", __encoders.size(), HttpFieldPreEncoder.class.getSimpleName());
      }

      if (!__encoders.containsKey(HttpVersion.HTTP_1_0)) {
         __encoders.put(HttpVersion.HTTP_1_0, new Http10FieldPreEncoder());
      }

      if (!__encoders.containsKey(HttpVersion.HTTP_1_1)) {
         __encoders.put(HttpVersion.HTTP_1_1, new Http11FieldPreEncoder());
      }
   }
}
