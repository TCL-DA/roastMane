package com.digitalpetri.enip.cip.epath;

import com.digitalpetri.enip.cip.structs.ElectronicKey;
import io.netty.buffer.ByteBuf;

public abstract class LogicalSegment<T> extends EPathSegment {
   public static final int SEGMENT_TYPE = 1;
   private final T value;
   private final LogicalSegment.LogicalType type;
   private final LogicalSegment.LogicalFormat format;

   protected LogicalSegment(T value, LogicalSegment.LogicalType type, LogicalSegment.LogicalFormat format) {
      this.value = value;
      this.type = type;
      this.format = format;
   }

   public T getValue() {
      return this.value;
   }

   public LogicalSegment.LogicalType getType() {
      return this.type;
   }

   public LogicalSegment.LogicalFormat getFormat() {
      return this.format;
   }

   protected abstract ByteBuf encodeValue(ByteBuf var1);

   protected static LogicalSegment.LogicalFormat smallestFormat(int value) {
      if ((value & 0xFF) == value) {
         return LogicalSegment.LogicalFormat.Bits_8;
      } else {
         return (value & 65535) == value ? LogicalSegment.LogicalFormat.Bits_16 : LogicalSegment.LogicalFormat.Bits_32;
      }
   }

   public static ByteBuf encode(LogicalSegment<?> segment, boolean padded, ByteBuf buffer) {
      int segmentByte = 0;
      segmentByte |= 32;
      segmentByte |= segment.getType().getType() << 2;
      segmentByte |= segment.getFormat().getType();
      buffer.writeByte(segmentByte);
      if (padded && (segment.getFormat() == LogicalSegment.LogicalFormat.Bits_16 || segment.getFormat() == LogicalSegment.LogicalFormat.Bits_32)) {
         buffer.writeByte(0);
      }

      segment.encodeValue(buffer);
      return buffer;
   }

   private static ByteBuf encodeIntValue(LogicalSegment.LogicalFormat format, int value, ByteBuf buffer) {
      switch (format) {
         case Bits_8:
            return buffer.writeByte(value);
         case Bits_16:
            return buffer.writeShort(value);
         case Bits_32:
            return buffer.writeInt(value);
         case Reserved:
         default:
            throw new IllegalStateException("Reserved segment type not supported");
      }
   }

   private static ByteBuf encodeKeyValue(LogicalSegment.LogicalFormat format, ElectronicKey value, ByteBuf buffer) {
      return ElectronicKey.encode(value, buffer);
   }

   public static final class AttributeId extends LogicalSegment<Integer> {
      public AttributeId(Integer value) {
         super(value, LogicalSegment.LogicalType.AttributeId, smallestFormat(value));
      }

      public AttributeId(Integer value, LogicalSegment.LogicalFormat format) {
         super(value, LogicalSegment.LogicalType.AttributeId, format);
      }

      @Override
      protected ByteBuf encodeValue(ByteBuf buffer) {
         return LogicalSegment.encodeIntValue(this.getFormat(), (Integer)this.getValue(), buffer);
      }
   }

   public static final class ClassId extends LogicalSegment<Integer> {
      public ClassId(Integer value) {
         this(value, smallestFormat(value));
      }

      public ClassId(int value, LogicalSegment.LogicalFormat format) {
         super(value, LogicalSegment.LogicalType.ClassId, format);
      }

      @Override
      protected ByteBuf encodeValue(ByteBuf buffer) {
         return LogicalSegment.encodeIntValue(this.getFormat(), (Integer)this.getValue(), buffer);
      }
   }

   public static final class ConnectionPoint extends LogicalSegment<Integer> {
      public ConnectionPoint(Integer value) {
         this(value, smallestFormat(value));
      }

      public ConnectionPoint(Integer value, LogicalSegment.LogicalFormat format) {
         super(value, LogicalSegment.LogicalType.ConnectionPoint, format);
      }

      @Override
      protected ByteBuf encodeValue(ByteBuf buffer) {
         return LogicalSegment.encodeIntValue(this.getFormat(), (Integer)this.getValue(), buffer);
      }
   }

   public static final class InstanceId extends LogicalSegment<Integer> {
      public InstanceId(int value) {
         this(value, smallestFormat(value));
      }

      public InstanceId(int value, LogicalSegment.LogicalFormat format) {
         super(value, LogicalSegment.LogicalType.InstanceId, format);
      }

      @Override
      protected ByteBuf encodeValue(ByteBuf buffer) {
         return LogicalSegment.encodeIntValue(this.getFormat(), (Integer)this.getValue(), buffer);
      }
   }

   public static final class KeySegment extends LogicalSegment<ElectronicKey> {
      public KeySegment(ElectronicKey value, LogicalSegment.LogicalFormat format) {
         super(value, LogicalSegment.LogicalType.Special, format);
      }

      @Override
      protected ByteBuf encodeValue(ByteBuf buffer) {
         return LogicalSegment.encodeKeyValue(this.getFormat(), (ElectronicKey)this.getValue(), buffer);
      }
   }

   public static enum LogicalFormat {
      Bits_8(0),
      Bits_16(1),
      Bits_32(2),
      Reserved(3);

      private final int type;

      private LogicalFormat(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }
   }

   public static enum LogicalType {
      ClassId(0),
      InstanceId(1),
      MemberId(2),
      ConnectionPoint(3),
      AttributeId(4),
      Special(5),
      ServiceId(6),
      Reserved(7);

      private final int type;

      private LogicalType(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }
   }

   public static final class MemberId extends LogicalSegment<Integer> {
      public MemberId(Integer value) {
         this(value, smallestFormat(value));
      }

      public MemberId(Integer value, LogicalSegment.LogicalFormat format) {
         super(value, LogicalSegment.LogicalType.MemberId, format);
      }

      @Override
      protected ByteBuf encodeValue(ByteBuf buffer) {
         return LogicalSegment.encodeIntValue(this.getFormat(), (Integer)this.getValue(), buffer);
      }
   }

   public static final class ServiceId extends LogicalSegment<Integer> {
      public ServiceId(Integer value, LogicalSegment.LogicalFormat format) {
         super(value, LogicalSegment.LogicalType.ServiceId, format);
      }

      @Override
      protected ByteBuf encodeValue(ByteBuf buffer) {
         return LogicalSegment.encodeIntValue(this.getFormat(), (Integer)this.getValue(), buffer);
      }
   }
}
