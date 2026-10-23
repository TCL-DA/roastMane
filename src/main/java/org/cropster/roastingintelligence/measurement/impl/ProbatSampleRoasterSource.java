package org.cropster.roastingintelligence.measurement.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.commons.io.IOUtils;
import org.cropster.roastingintelligence.measurement.ControlStateSource;
import org.cropster.roastingintelligence.measurement.MeasurementReaderWriter;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.ControlState;
import org.cropster.roastingintelligence.util.NanoTime;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.Callback.Completable;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketOpen;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSocket
public class ProbatSampleRoasterSource extends AbstractMeasurementSource implements MeasurementReaderWriter, ControlStateSource {
   private final Logger logger = LoggerFactory.getLogger(ProbatSampleRoasterSource.class);
   private static final String BEAN_NODE = "beanTemp";
   private static final String EXHAUST_NODE = "exhaustAirTemp";
   private static final String BURNER_NODE = "burner";
   private static final String AIRPRESSURE_NODE = "airPressure";
   private static final String DRUM_NODE = "drum";
   private static final String EXHAUST_FAN_NODE = "exhaustFan";
   private static final String INTAKE_AIR_NODE = "intakeAirTemp";
   private static final String AMBIENT_NODE = "ambientTemp";
   private static final String COOLING_AIR_NODE = "coolingAirTemp";
   public static final String COOLING_FAN_STATE_NODE = "coolingFanState";
   public static final String STIRRER_STATE_NODE = "stirrerState";
   public static final String STANDBY_STATE_NODE = "standby";
   private static final int CONNECT_TIMEOUT = 4000;
   private static final int READ_TIMEOUT = 500;
   private static final int WRITE_TIMEOUT = 500;
   private static final int KEEP_ALIVE_INTERVAL = 25;
   private static final int MAX_ID = 999999;
   private static final String DATA_COMMAND = "{ \"id\": %d, \"roasterID\": %d, \"command\": \"getCurrentRoastingStep\" }";
   private static final String CONTROL_COMMAND = "{ \"id\": %d, \"roasterID\": %d, \"command\": \"setControlParams\", \"params\": { \"%s\": %s }}";
   private static final String KEEP_ALIVE_COMMAND = "{ \"id\": %d, \"roasterID\": %d, \"command\": \"keepAlive\"}";
   private static final String GET_SUPPORTED_FEATURES_COMMAND = "{ \"id\": %d, \"roasterID\": %d, \"command\": \"getSupportedFeatures\"}";
   private static final String CONTROL_PARAMS_COMMAND = "{ \"id\": %d, \"roasterID\": %d, \"command\": \"getControlParams\" }";
   private final ObjectMapper _objectMapper;
   private Set<String> _channelNames = Collections.emptySet();
   private final Long _roasterId = 0L;
   private final URI _uri;
   private final WebSocketClient _client = new WebSocketClient();
   private CountDownLatch _connectLatch;
   private final Map<Integer, CompletableFuture<JsonNode>> _messageQueue = new HashMap<>();
   private final Random _random = new Random();
   private volatile long _lastKeepAlive = 0L;
   private Session _session;
   private final Map<String, ProbatSampleRoasterSource.ProbatState> controlStates = new LinkedHashMap<>();
   private final Map<ControlState, Boolean> readControlStateCache = Collections.synchronizedMap(new HashMap<>());

   public ProbatSampleRoasterSource(ProbatSampleRoasterSourceFactory factory, InetAddress ipAddress) throws IOException {
      super(factory);
      if (ipAddress == null) {
         throw new IOException("No IP address specified");
      } else {
         this._objectMapper = new ObjectMapper();
         this._objectMapper.setFilters(new SimpleFilterProvider().setFailOnUnknownId(false));
         this._objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
         this._objectMapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
         this._objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

         try {
            this._uri = new URI("ws://" + ipAddress.getHostAddress() + "/WebSocket");
         } catch (URISyntaxException var5) {
            throw new IOException("Invalid host: " + ipAddress.toString());
         }

         Map<String, ProbatSampleRoasterSource.ProbatState> availableStates = new HashMap<>();
         availableStates.put("coolerState", new ProbatSampleRoasterSource.ProbatIntegerState("coolingFanState"));
         availableStates.put("stirrerState", new ProbatSampleRoasterSource.ProbatIntegerState("stirrerState"));
         availableStates.put("standbyState", new ProbatSampleRoasterSource.ProbatIntegerState("standby"));
         List<ControlState> supportedStates = List.of(factory.getSupportedStates(factory.getParameters()));
         supportedStates.forEach(controlState -> {
            ProbatSampleRoasterSource.ProbatState probatControlState = availableStates.get(controlState.getName());
            if (probatControlState != null) {
               probatControlState.setControlState(controlState);
               this.controlStates.put(controlState.getName(), probatControlState);
            }
         });
      }
   }

   @Override
   public void open() {
      try {
         this._client.start();
         ClientUpgradeRequest request = new ClientUpgradeRequest();
         request.setHeader("Origin", "http://" + InetAddress.getLocalHost().toString() + "\r\n");
         this.logger.info("Connecting to: {}", this._uri);
         this._client.connect(this, this._uri, request);
         this._connectLatch = new CountDownLatch(1);
         if (!this.awaitConnect(4000, TimeUnit.MILLISECONDS)) {
            throw new IOException("Timeout");
         }

         try {
            JsonNode supportedFeaturesNode = this.requestSupportedFeatures(this._roasterId);
            this.logger.info("Supported features: {}", supportedFeaturesNode);
         } catch (IOException var3) {
            this.logger.error("Error requesting supported features", var3);
         }
      } catch (Exception var4) {
         this.logger.error("Error connecting to Probat WebSocket: {}", var4.getMessage(), var4);
      }
   }

   @Override
   public boolean isReady() {
      return this._session != null && this._session.isOpen();
   }

   @Override
   public void close() throws IOException {
      this.readControlStateCache.clear();

      try {
         if (this._session != null) {
            this._session.close();
         }

         this._client.stop();
      } catch (Exception var2) {
         throw new IOException("Error closing Probat WebSocket", var2);
      }
   }

   @Override
   public void configure(Collection<Channel> channels) {
      this._channelNames = new LinkedHashSet<>();

      for (Channel channel : channels) {
         this._channelNames.add(channel.getName());
      }
   }

   @Override
   public Set<String> getConfiguredChannels() {
      return this._channelNames;
   }

   private void addValue(JsonNode parentNode, Map<String, Double> values, String measureName, String fieldName) {
      if (parentNode != null && parentNode.has(fieldName)) {
         JsonNode measureNode = parentNode.get(fieldName);
         if (!measureNode.isNull()) {
            values.put(measureName, measureNode.asDouble());
         }
      }
   }

   @Override
   public Double getValue(String channelName) {
      return this.getValues().get(channelName);
   }

   @Override
   public Map<String, Double> getValues() {
      if (!this.isReady()) {
         this.readControlStateCache.clear();
         return Collections.emptyMap();
      } else {
         Map<String, Double> values = new LinkedHashMap<>();
         Map<ControlState, Boolean> stateValues = new HashMap<>();

         try {
            JsonNode dataNode = this.requestData(this._roasterId).get("data");
            if (this.logger.isDebugEnabled()) {
               this.logger.debug("Parse: {}", dataNode);
            }

            this.addValue(dataNode, values, "beanTemperature", "beanTemp");
            this.addValue(dataNode, values, "exhaustTemperature", "exhaustAirTemp");
            this.addValue(dataNode, values, "inletTemperature", "intakeAirTemp");
            this.addValue(dataNode, values, "drumTemperature", "ambientTemp");
            this.addValue(dataNode, values, "otherTemperature", "coolingAirTemp");
            this.addValue(dataNode, values, "gas", "burner");
            if (values.containsKey("gas")) {
               values.put("gasControl", values.get("gas"));
            }

            this.addValue(dataNode, values, "airflow", "exhaustFan");
            if (values.containsKey("airflow")) {
               values.put("airflowControl", values.get("airflow"));
            }

            this.addValue(dataNode, values, "drumSpeed", "drum");
            if (values.containsKey("drumSpeed")) {
               values.put("drumSpeedControl", values.get("drumSpeed"));
            }

            this.addValue(dataNode, values, "drumPressure", "airPressure");
            if (values.containsKey("drumPressure")) {
               values.put("drumPressure", values.get("drumPressure") * 1000.0);
            }

            JsonNode controlParamsResponse = this.requestControlParams(this._roasterId);
            if (controlParamsResponse.has("data")) {
               JsonNode controlParamsNode = controlParamsResponse.get("data");

               for (ProbatSampleRoasterSource.ProbatState probatState : this.controlStates.values()) {
                  if (controlParamsNode.has(probatState.getFieldName())) {
                     Boolean value = probatState.getValue(controlParamsNode.get(probatState.getFieldName()));
                     if (value != null) {
                        stateValues.put(probatState.getControlState(), value);
                     }
                  }
               }
            }

            long now = NanoTime.currentTimeMillis();
            if (now - this._lastKeepAlive >= 25000L) {
               this.requestKeepAlive(this._roasterId);
               this._lastKeepAlive = NanoTime.currentTimeMillis();
            }
         } catch (IOException var9) {
            this.logger.error("Error requesting values", var9);
            if (var9.getCause() instanceof TimeoutException) {
               IOUtils.closeQuietly(this);
            }
         }

         this.readControlStateCache.putAll(stateValues);
         return values;
      }
   }

   @Override
   public void setValue(String field, Double value) {
      if (!"gasControl".equals(field) && !"airflowControl".equals(field) && !"drumSpeedControl".equals(field)) {
         throw new IllegalArgumentException("Field not supported: " + field);
      } else {
         if (!this.isReady()) {
            this.open();
            if (!this.isReady()) {
               this.logger.error("Failed to write {} value because connecting failed", field);
               return;
            }
         }

         try {
            this.logger.info("Will be writing {} value: {}", field, value.intValue());

            JsonNode response = this.requestSetControl(this._roasterId, switch (field) {
               case "gasControl" -> "burner";
               case "airflowControl" -> "exhaustFan";
               case "drumSpeedControl" -> "drum";
               default -> throw new IllegalArgumentException("Field not supported: " + field);
            }, String.valueOf(value.intValue()));
            JsonNode status = response.get("status");
            if (status == null) {
               this.logger.error("Error setting {}. Response: {}", field, response.asText());
            } else if (status.asText().equals("ok")) {
               this.logger.info("{} value {} written.", field, value);
            } else {
               this.logger.info("Error setting {}. Status: {}", field, response.get("status"));
            }
         } catch (Exception var6) {
            this.logger.error("Error writing to PLC", var6);
         }
      }
   }

   @Override
   public Double getMinValue(String field) {
      switch (field) {
         case "gasControl":
         case "airflowControl":
         case "drumSpeedControl":
            return 0.0;
         default:
            return null;
      }
   }

   @Override
   public Double getMaxValue(String field) {
      switch (field) {
         case "gasControl":
         case "airflowControl":
         case "drumSpeedControl":
            return 100.0;
         default:
            return null;
      }
   }

   @Override
   public Map<ControlState, Boolean> getStates() {
      return Collections.unmodifiableMap(this.readControlStateCache);
   }

   @Override
   public Boolean getState(ControlState state) {
      return state == null ? null : this.readControlStateCache.get(state);
   }

   @Override
   public void setState(ControlState state, Boolean value) {
      if (state == null) {
         this.logger.warn("Null state");
      } else {
         String stateName = state.getName();
         if (value == null) {
            this.logger.warn("Null value for state {}", stateName);
         } else if (this.notReady()) {
            this.logger.error("Failed to write {} state value because connecting failed", stateName);
         } else {
            try {
               ProbatSampleRoasterSource.ProbatState probatState = this.controlStates.get(stateName);
               if (probatState == null) {
                  this.logger.warn("Writing unavailable state {}", stateName);
                  return;
               }

               String controlNode = probatState.getFieldName();
               JsonNode response = this.requestSetControl(this._roasterId, controlNode, String.valueOf(value));
               JsonNode status = response.get("status");
               if (status == null) {
                  this.logger.error("Error setting {}. Response: {}", controlNode, response.asText());
               } else if (status.asText().equals("ok")) {
                  this.logger.info("{} state {} written.", controlNode, value ? "true" : "false");
               } else {
                  this.logger.info("Error setting {}. Status: {}", controlNode, response.get("status"));
               }
            } catch (Exception var8) {
               this.logger.error("Error writing to PLC", var8);
            }
         }
      }
   }

   private boolean notReady() {
      if (!this.isReady()) {
         this.open();
         return !this.isReady();
      } else {
         return false;
      }
   }

   private boolean awaitConnect(int duration, TimeUnit unit) throws InterruptedException {
      return this._connectLatch.await((long)duration, unit);
   }

   @OnWebSocketClose
   public void onClose(int statusCode, String reason) {
      this._session = null;
      this.logger.info("Connection closed: {} - {}", statusCode, reason);
      this.readControlStateCache.clear();
      this.fireStatusChanged();
   }

   @OnWebSocketOpen
   public void onConnect(Session session) {
      this.logger.info("Connection opened: {}", session.getRemoteSocketAddress());
      this._session = session;
      this._session.setMaxTextMessageSize(65536L);
      this._connectLatch.countDown();
      this.fireStatusChanged();
      this._lastKeepAlive = NanoTime.currentTimeMillis();
   }

   @OnWebSocketError
   public void onError(Throwable ex) {
      if (this.logger.isDebugEnabled()) {
         this.logger.error("Error: {} - {}", new Object[]{ex.getClass().getSimpleName(), ex.getMessage(), ex});
      } else {
         this.logger.error("Error: {} - {}", ex.getMessage(), ex.getClass().getSimpleName());
      }
   }

   @OnWebSocketMessage
   public void onMessage(String message) {
      if (this.logger.isDebugEnabled()) {
         this.logger.debug("Received message: {}", message);
      }

      String[] messages = message.split("\n");

      for (String msg : messages) {
         if (this.logger.isDebugEnabled()) {
            this.logger.debug("Handle message: {}", msg);
         }

         try {
            JsonNode json = this._objectMapper.readTree(msg);
            if (json.has("id")) {
               Integer msgId = json.get("id").asInt();
               if (this._messageQueue.containsKey(msgId) && !this._messageQueue.get(msgId).complete(json)) {
                  this.logger.error("Error completing request {}", msg);
               }
            }
         } catch (IOException var9) {
            this.logger.error("Error parsing message", var9);
         }
      }
   }

   private JsonNode request(String msg, int msgId) throws IOException {
      if (this._session == null) {
         throw new IOException("No session");
      } else {
         CompletableFuture<Void> requestFuture = Completable.with(completable -> this._session.sendText(msg, completable));

         try {
            requestFuture.get(500L, TimeUnit.MILLISECONDS);
            if (this.logger.isDebugEnabled()) {
               this.logger.debug("Message sent: {}", msg);
            }
         } catch (ExecutionException | TimeoutException | InterruptedException var8) {
            this.logger.error("Error sending message", var8);
            if (requestFuture != null) {
               requestFuture.cancel(true);
            }

            throw new IOException(var8);
         }

         CompletableFuture<JsonNode> responseFuture = new CompletableFuture<>();
         this._messageQueue.put(msgId, responseFuture);
         JsonNode response = null;

         try {
            response = responseFuture.get(500L, TimeUnit.MILLISECONDS);
         } catch (ExecutionException | TimeoutException | InterruptedException var7) {
            this._messageQueue.remove(msgId);
            throw new IOException(var7);
         }

         this._messageQueue.remove(msgId);
         return response;
      }
   }

   private int getRandomMessageId() {
      return this._random.nextInt(999999) + 1;
   }

   private JsonNode requestData(Long roasterId) throws IOException {
      int msgId = this.getRandomMessageId();
      return this.request(String.format(Locale.ENGLISH, "{ \"id\": %d, \"roasterID\": %d, \"command\": \"getCurrentRoastingStep\" }", msgId, roasterId), msgId);
   }

   private JsonNode requestSetControl(Long roasterId, String control, String value) throws IOException {
      int msgId = this.getRandomMessageId();
      return this.request(
         String.format(
            Locale.ENGLISH, "{ \"id\": %d, \"roasterID\": %d, \"command\": \"setControlParams\", \"params\": { \"%s\": %s }}", msgId, roasterId, control, value
         ),
         msgId
      );
   }

   private JsonNode requestKeepAlive(Long roasterId) throws IOException {
      int msgId = this.getRandomMessageId();
      return this.request(String.format(Locale.ENGLISH, "{ \"id\": %d, \"roasterID\": %d, \"command\": \"keepAlive\"}", msgId, roasterId), msgId);
   }

   private JsonNode requestSupportedFeatures(Long roasterId) throws IOException {
      int msgId = this.getRandomMessageId();
      JsonNode response = this.request(
         String.format(Locale.ENGLISH, "{ \"id\": %d, \"roasterID\": %d, \"command\": \"getSupportedFeatures\"}", msgId, roasterId), msgId
      );
      if (response == null) {
         throw new IOException("No response");
      } else {
         JsonNode status = response.get("status");
         if (status == null || !"ok".equals(status.asText())) {
            throw new IOException("Status: " + (status != null ? status.asText() : "null"));
         } else if (!response.has("data")) {
            throw new IOException("No data");
         } else {
            return response.get("data");
         }
      }
   }

   private JsonNode requestControlParams(Long roasterId) throws IOException {
      int msgId = this.getRandomMessageId();
      return this.request(String.format(Locale.ENGLISH, "{ \"id\": %d, \"roasterID\": %d, \"command\": \"getControlParams\" }", msgId, roasterId), msgId);
   }

   static class ProbatIntegerState extends ProbatSampleRoasterSource.ProbatState {
      ProbatIntegerState(String fieldName) {
         super(fieldName);
      }

      @Override
      Boolean getValue(JsonNode node) {
         if (node != null && node.isInt()) {
            int value = node.asInt();
            return value < 0 ? null : value >= 1;
         } else {
            return null;
         }
      }
   }

   abstract static class ProbatState {
      private ControlState controlState;
      private String fieldName;

      ProbatState(String fieldName, ControlState controlState) {
         this(fieldName);
         this.controlState = controlState;
      }

      ProbatState(String fieldName) {
         this.fieldName = fieldName;
      }

      abstract Boolean getValue(JsonNode var1);

      public String getFieldName() {
         return this.fieldName;
      }

      public void setFieldName(String fieldName) {
         this.fieldName = fieldName;
      }

      public ControlState getControlState() {
         return this.controlState;
      }

      public void setControlState(ControlState controlState) {
         this.controlState = controlState;
      }
   }
}
