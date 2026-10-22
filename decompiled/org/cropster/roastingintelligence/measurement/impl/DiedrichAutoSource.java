package org.cropster.roastingintelligence.measurement.impl;

import com.digitalpetri.enip.EtherNetIpClientConfig;
import com.digitalpetri.enip.EtherNetIpShared;
import com.digitalpetri.enip.cip.CipClient;
import com.digitalpetri.enip.cip.CipResponseException;
import com.digitalpetri.enip.cip.epath.EPathSegment;
import com.digitalpetri.enip.cip.epath.PortSegment;
import com.digitalpetri.enip.cip.epath.EPath.PaddedEPath;
import com.digitalpetri.enip.cip.epath.LogicalSegment.ClassId;
import com.digitalpetri.enip.cip.epath.LogicalSegment.InstanceId;
import com.digitalpetri.enip.cip.services.CipService;
import com.digitalpetri.enip.cip.services.CipService.PartialResponseException;
import com.digitalpetri.enip.cip.structs.MessageRouterRequest;
import com.digitalpetri.enip.cip.structs.MessageRouterResponse;
import com.digitalpetri.netty.fsm.State;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;
import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import org.cropster.roastingintelligence.measurement.MeasurementReaderWriter;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiedrichAutoSource extends AbstractMeasurementSource implements MeasurementReaderWriter {
   private final Logger logger = LoggerFactory.getLogger(DiedrichAutoSource.class);
   private static final long TIMEOUT = 500L;
   private static final int RETRIES_WRITE = 3;
   private final AtomicInteger _tnsNumber = new AtomicInteger(0);
   private final CipClient _client;
   private boolean _isReady = false;
   private Set<String> _channelNames = Collections.emptySet();

   public DiedrichAutoSource(MeasurementSourceFactory factory, InetAddress ipAddress) {
      super(factory);
      EtherNetIpClientConfig config = EtherNetIpClientConfig.builder(ipAddress.getHostAddress())
         .setSerialNumber(7079450)
         .setVendorId(1)
         .setTimeout(Duration.ofMillis(500L))
         .build();
      PaddedEPath connectionPath = new PaddedEPath(new EPathSegment[]{new PortSegment(1, new byte[]{0})});
      this._client = new CipClient(config, connectionPath);
   }

   @Override
   public void open() {
      try {
         this._client.connect().get();
         this.logger.info("Connection opened: {}", this._client.getState());
         this._isReady = true;
         this.fireStatusChanged();
      } catch (Exception var2) {
         this.logger.error("Error opening Ethernet/IP connection", var2);
      }
   }

   @Override
   public synchronized boolean isReady() {
      boolean connected = State.Connected.toString().equals(this._client.getState());
      if (this.logger.isTraceEnabled()) {
         this.logger.debug("Connected: {}, State: {}", connected, this._client.getState());
      }

      if (connected != this._isReady) {
         this._isReady = connected;
         this.fireStatusChanged();
      }

      return this._isReady;
   }

   @Override
   public void close() throws IOException {
      try {
         this._isReady = false;
         this._client.disconnect().get();
      } catch (Exception var2) {
         this.logger.error("Error closing Ethernet/IP connection: {}", var2.getMessage(), var2);
      }

      EtherNetIpShared.releaseSharedResources();
   }

   @Override
   public synchronized void configure(Collection<Channel> channels) {
      this._channelNames = new LinkedHashSet<>();

      for (Channel channel : channels) {
         this._channelNames.add(channel.getName());
      }
   }

   @Override
   public Set<String> getConfiguredChannels() {
      return this._channelNames;
   }

   @Override
   public Double getValue(String channelName) {
      return this.getValues().get(channelName);
   }

   @Override
   public synchronized Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         Map<String, Double> values = new LinkedHashMap<>();
         if (this.getConfiguredChannels().contains("beanTemperature")) {
            DiedrichAutoSource.ExecutePCCCService<Float[]> serviceFloat = new DiedrichAutoSource.ExecutePCCCService<>(
               new DiedrichAutoSource.ProtectedTypedLogicalReadFloat(35, 2, 1), this.nextTnsNumber(), this._client.getConfig()
            );
            CompletableFuture<Float[]> futureFloat = this._client.invoke(serviceFloat);

            try {
               Float[] responseData = futureFloat.get();
               if (responseData.length != 1) {
                  throw new IOException("invalid length of response");
               }

               values.put("beanTemperature", (double)responseData[0].floatValue());
            } catch (InterruptedException | ExecutionException | IOException var7) {
               this.logger.error("Error processing Ethernet/IP request", var7);
            }
         }

         if (this.getConfiguredChannels().contains("gas") || this.getConfiguredChannels().contains("airflow")) {
            DiedrichAutoSource.ExecutePCCCService<short[]> serviceShort = new DiedrichAutoSource.ExecutePCCCService<>(
               new DiedrichAutoSource.ProtectedTypedLogicalReadInt(33, 5, 2), this.nextTnsNumber(), this._client.getConfig()
            );
            CompletableFuture<short[]> futureShort = this._client.invoke(serviceShort);

            try {
               short[] responseData = futureShort.get();
               if (responseData.length != 2) {
                  throw new IOException("invalid length of response");
               }

               if (this.getConfiguredChannels().contains("gas")) {
                  values.put("gas", (double)responseData[0]);
               }

               if (this.getConfiguredChannels().contains("airflow")) {
                  values.put("airflow", (double)responseData[1]);
               }
            } catch (InterruptedException | ExecutionException | IOException var6) {
               this.logger.error("Error processing Ethernet/IP request", var6);
            }
         }

         if (this.getConfiguredChannels().contains("gasControl") || this.getConfiguredChannels().contains("airflowControl")) {
            DiedrichAutoSource.ExecutePCCCService<short[]> serviceShortControls = new DiedrichAutoSource.ExecutePCCCService<>(
               new DiedrichAutoSource.ProtectedTypedLogicalReadInt(32, 5, 2), this.nextTnsNumber(), this._client.getConfig()
            );
            CompletableFuture<short[]> futureShortControls = this._client.invoke(serviceShortControls);

            try {
               short[] responseDatax = futureShortControls.get();
               if (responseDatax.length != 2) {
                  throw new IOException("invalid length of response");
               }

               if (this.getConfiguredChannels().contains("gasControl")) {
                  values.put("gasControl", (double)responseDatax[0]);
               }

               if (this.getConfiguredChannels().contains("airflowControl")) {
                  values.put("airflowControl", (double)responseDatax[1]);
               }
            } catch (InterruptedException | ExecutionException | IOException var5) {
               this.logger.error("Error processing Ethernet/IP request", var5);
            }
         }

         return values;
      }
   }

   @Override
   public synchronized void setValue(String field, Double value) {
      if (!"gasControl".equals(field) && !"airflowControl".equals(field)) {
         throw new IllegalArgumentException("Field not supported!");
      } else {
         if (!this.isReady()) {
            this.open();
            if (!this.isReady()) {
               this.logger.error("Failed to write {} value because connecting failed", field);
               return;
            }
         }

         boolean writeSuccessful = false;

         for (int i = 0; !writeSuccessful && i < 3; i++) {
            try {
               this.logger.info("Write {} value: {}", field, value);
               DiedrichAutoSource.ExecutePCCCService<short[]> service = new DiedrichAutoSource.ExecutePCCCService<>(
                  new DiedrichAutoSource.ProtectedTypedLogicalWriteInt(32, "gasControl".equals(field) ? 5 : 6, 1, new short[]{value.shortValue()}),
                  this.nextTnsNumber(),
                  this._client.getConfig()
               );
               CompletableFuture<short[]> future = this._client.invoke(service);
               future.get();
               writeSuccessful = true;
            } catch (Exception var7) {
               this.logger.error("Error writing to PLC (try {})", i + 1, var7);
            }
         }
      }
   }

   @Override
   public Double getMinValue(String field) {
      return !"gasControl".equals(field) && !"airflowControl".equals(field) ? null : 0.0;
   }

   @Override
   public Double getMaxValue(String field) {
      return !"gasControl".equals(field) && !"airflowControl".equals(field) ? null : 100.0;
   }

   private short nextTnsNumber() {
      return (short)this._tnsNumber.getAndIncrement();
   }

   public class ExecutePCCCService<T> implements CipService<T> {
      private static final int SERVICE_CODE = 75;
      private static final int REQUEST_ID_SIZE = 7;
      private static final int PCCC_TYPED_CMD = 15;
      private static final int PCCC_STATUS = 0;
      private final Logger innerLogger = LoggerFactory.getLogger(DiedrichAutoSource.ExecutePCCCService.class);
      private final PaddedEPath _pcccObjectPath = new PaddedEPath(new EPathSegment[]{new ClassId(103), new InstanceId(1)});
      private final CipService<T> _service;
      private final short _sequenceNumber;
      private final EtherNetIpClientConfig _clientConfig;

      public ExecutePCCCService(CipService<T> service, short sequenceNumber, EtherNetIpClientConfig clientConfig) {
         this._service = service;
         this._sequenceNumber = sequenceNumber;
         this._clientConfig = clientConfig;
      }

      public void encodeRequest(ByteBuf buffer) {
         MessageRouterRequest request = new MessageRouterRequest(75, this._pcccObjectPath, this::encode);
         MessageRouterRequest.encode(request, buffer);
      }

      private ByteBuf encode(ByteBuf buffer) {
         buffer.writeByte(7);
         buffer.writeShort(this._clientConfig.getVendorId());
         buffer.writeInt(this._clientConfig.getSerialNumber());
         int bytesWritten = this.encodeEmbeddedService(buffer);
         if (bytesWritten % 2 != 0) {
            buffer.writeByte(0);
         }

         if (this.innerLogger.isTraceEnabled()) {
            this.innerLogger.debug("Request: {}", ByteBufUtil.hexDump(buffer));
         }

         return buffer;
      }

      private int encodeEmbeddedService(ByteBuf buffer) {
         buffer.writeByte(15);
         buffer.writeByte(0);
         buffer.writeShort(this._sequenceNumber);
         int messageStartIndex = buffer.writerIndex();
         this._service.encodeRequest(buffer);
         return buffer.writerIndex() - messageStartIndex;
      }

      public T decodeResponse(ByteBuf buffer) throws CipResponseException, PartialResponseException {
         MessageRouterResponse response = MessageRouterResponse.decode(buffer);
         int generalStatus = response.getGeneralStatus();

         Object var4;
         try {
            if (generalStatus != 0) {
               throw new CipResponseException(generalStatus, response.getAdditionalStatus());
            }

            var4 = this.decode(response);
         } finally {
            ReferenceCountUtil.release(response.getData());
         }

         return (T)var4;
      }

      private T decode(MessageRouterResponse response) throws CipResponseException, PartialResponseException {
         ByteBuf buffer = response.getData();
         buffer.readUnsignedByte();
         buffer.readUnsignedShort();
         buffer.readInt();
         return this.decodeEmbeddedservice(buffer);
      }

      private T decodeEmbeddedservice(ByteBuf buffer) throws CipResponseException, PartialResponseException {
         buffer.readUnsignedByte();
         int pcccStatus = buffer.readUnsignedByte();
         buffer.readUnsignedShort();
         if (this.innerLogger.isTraceEnabled()) {
            this.innerLogger.debug("Response: {}", ByteBufUtil.hexDump(buffer));
         }

         if (pcccStatus == 0) {
            ByteBuf data = buffer.isReadable() ? buffer.readSlice(buffer.readableBytes()) : Unpooled.EMPTY_BUFFER;
            return (T)this._service.decodeResponse(data);
         } else {
            int extendedStatus = -1;
            if (pcccStatus == 240 && buffer.isReadable()) {
               extendedStatus = buffer.readUnsignedByte();
            }

            throw new CipResponseException(208, new int[]{pcccStatus, extendedStatus});
         }
      }
   }

   public abstract class ProtectedTypedLogical {
      protected int _fileNumber;
      protected int _elementStart;
      protected int _numElements;
      protected int _pcccFnc;
      protected int _numBytes;
      protected int _fileType;

      public ProtectedTypedLogical(int fileNumber, int elementStart, int numElements, int pcccFnc, int fileType, int numBytes) {
         this._fileNumber = fileNumber;
         this._elementStart = elementStart;
         this._numElements = numElements;
         this._fileType = fileType;
         this._pcccFnc = pcccFnc;
         this._numBytes = numBytes;
      }

      public void encodeRequest(ByteBuf buffer) {
         buffer.writeByte(this._pcccFnc);
         buffer.writeByte(this._numBytes);
         buffer.writeByte(this._fileNumber);
         buffer.writeByte(this._fileType);
         buffer.writeByte(this._elementStart);
         buffer.writeByte(0);
      }
   }

   public abstract class ProtectedTypedLogicalRead extends DiedrichAutoSource.ProtectedTypedLogical {
      protected static final int PCCC_FNC = 162;

      public ProtectedTypedLogicalRead(int fileNumber, int elementStart, int numElements, int fileType, int numBytes) {
         super(fileNumber, elementStart, numElements, 162, fileType, numBytes);
      }
   }

   public class ProtectedTypedLogicalReadFloat extends DiedrichAutoSource.ProtectedTypedLogicalRead implements CipService<Float[]> {
      private static final int BYTES = 4;
      private static final byte FILE_TYPE = -118;

      public ProtectedTypedLogicalReadFloat(int fileNumber, int elementStart, int numElements) {
         super(fileNumber, elementStart, numElements, -118, numElements * 4);
      }

      @Override
      public void encodeRequest(ByteBuf buffer) {
         super.encodeRequest(buffer);
      }

      public Float[] decodeResponse(ByteBuf buffer) throws CipResponseException, PartialResponseException {
         if (buffer.readableBytes() < this._numBytes) {
            throw new CipResponseException(34, new int[0]);
         } else {
            Float[] values = new Float[this._numElements];

            for (int i = 0; i < this._numElements; i++) {
               values[i] = buffer.readFloat();
            }

            return values;
         }
      }
   }

   public class ProtectedTypedLogicalReadInt extends DiedrichAutoSource.ProtectedTypedLogicalRead implements CipService<short[]> {
      private static final int BYTES = 2;
      private static final byte FILE_TYPE = -119;

      public ProtectedTypedLogicalReadInt(int fileNumber, int elementStart, int numElements) {
         super(fileNumber, elementStart, numElements, -119, numElements * 2);
      }

      @Override
      public void encodeRequest(ByteBuf buffer) {
         super.encodeRequest(buffer);
      }

      public short[] decodeResponse(ByteBuf buffer) throws CipResponseException, PartialResponseException {
         if (buffer.readableBytes() < this._numBytes) {
            throw new CipResponseException(34, new int[0]);
         } else {
            short[] values = new short[this._numElements];

            for (int i = 0; i < this._numElements; i++) {
               values[i] = buffer.readShort();
            }

            return values;
         }
      }
   }

   public abstract class ProtectedTypedLogicalWrite extends DiedrichAutoSource.ProtectedTypedLogical {
      private static final int PCCC_FNC = 170;

      public ProtectedTypedLogicalWrite(int fileNumber, int elementStart, int numElements, int fileType, int numBytes) {
         super(fileNumber, elementStart, numElements, 170, fileType, numBytes);
      }
   }

   public class ProtectedTypedLogicalWriteInt extends DiedrichAutoSource.ProtectedTypedLogicalWrite implements CipService<short[]> {
      private static final int BYTES = 2;
      private static final byte FILE_TYPE = -119;
      private final short[] _data;

      public ProtectedTypedLogicalWriteInt(int fileNumber, int elementStart, int numElements, short[] data) {
         super(fileNumber, elementStart, numElements, -119, numElements * 2);
         this._data = data;
      }

      @Override
      public void encodeRequest(ByteBuf buffer) {
         super.encodeRequest(buffer);

         for (short value : this._data) {
            buffer.writeShort(value);
         }
      }

      public short[] decodeResponse(ByteBuf buffer) throws CipResponseException, PartialResponseException {
         return null;
      }
   }
}
