package org.cropster.roastingintelligence.service.impl;

import com.cropster.roastingintelligence.Data;
import com.cropster.roastingintelligence.Preferences;
import com.cropster.roastingintelligence.model.alarms.Alarm;
import com.cropster.roastingintelligence.model.livegoals.LiveGoal;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.awt.Color;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.Defaults;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.model.UnsavedBetweenBatchLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonReader {
   private final Logger logger = LoggerFactory.getLogger(JsonReader.class);
   private final ObjectMapper _mapper;

   public JsonReader() {
      SimpleModule module = new SimpleModule();
      module.addDeserializer(Color.class, new JsonReader.ColorDeserializer());
      this._mapper = new ObjectMapper();
      this._mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
      this._mapper.registerModule(module);
   }

   public Roast read(Reader reader) throws IOException {
      ObjectNode rootNode = (ObjectNode)this._mapper.readValue(reader, ObjectNode.class);
      JsonNode versionNode = rootNode.remove("version");
      if (versionNode != null) {
         this.logger.debug("Loading roast file version {}", versionNode.asText());
      }

      JsonNode roastValueNode = rootNode.remove("roastValue");
      if (roastValueNode != null) {
         String roastValue = roastValueNode.asText();
         if (roastValue != null && roastValue.trim().length() > 0 && !roastValue.trim().equals("null")) {
            ObjectNode measureNode = (ObjectNode)rootNode.findValue("measures");
            if (measureNode == null) {
               measureNode = this._mapper.createObjectNode();
            }

            ObjectNode newNode = this._mapper.createObjectNode();
            newNode.put("amount", roastValue);
            newNode.put("unit", Defaults.ROAST_VALUE_UNIT.name());
            measureNode.put("roastValue", newNode);
            rootNode.put("measures", measureNode);
         }
      }

      JsonNode measureNode = rootNode.get("measures");
      if (measureNode != null && measureNode.isObject()) {
         ArrayNode measureArray = this._mapper.createArrayNode();
         Iterator<Entry<String, JsonNode>> it2 = measureNode.fields();

         while (it2.hasNext()) {
            Entry<String, JsonNode> entry = it2.next();
            ObjectNode newNode = this._mapper.createObjectNode();
            newNode.put("name", entry.getKey());
            newNode.put("value", entry.getValue());
            measureArray.add(newNode);
         }

         rootNode.put("measures", measureArray);
      }

      if (rootNode.has("schedule")) {
         rootNode.remove("schedule");
      }

      return (Roast)this._mapper.readValue(new TreeTraversingParser(rootNode), Roast.class);
   }

   public UnsavedBetweenBatchLog readBetweenBatchLog(Reader reader) throws IOException {
      return (UnsavedBetweenBatchLog)this._mapper.readValue(reader, UnsavedBetweenBatchLog.class);
   }

   public Preferences readPreferences(Reader reader) throws IOException {
      ObjectNode rootNode = (ObjectNode)this._mapper.readValue(reader, ObjectNode.class);

      try {
         JsonNode channelsNode = rootNode.get("channels");
         if (channelsNode != null) {
            for (JsonNode jsonNode : channelsNode) {
               ObjectNode channelNode = (ObjectNode)jsonNode;
               JsonNode nameNode = channelNode.get("name");
               if (nameNode != null && nameNode.asText() != null && nameNode.asText().matches(".+Temperature")) {
                  JsonNode rorNode = channelNode.get("riseRate");
                  if (rorNode != null) {
                     channelNode.put("ror", rorNode.asInt() > 0);
                  }
               }
            }
         }
      } catch (Exception var9) {
         this.logger.error("Error migrating AppModel", var9);
      }

      return (Preferences)this._mapper.readValue(new TreeTraversingParser(rootNode), Preferences.class);
   }

   public Data readData(Reader reader) throws IOException {
      return (Data)this._mapper.readValue(reader, Data.class);
   }

   public List<Alarm> readAlarms(Reader reader) throws IOException {
      ObjectNode rootNode = (ObjectNode)this._mapper.readValue(reader, ObjectNode.class);
      JsonNode versionNode = rootNode.remove("version");
      if (versionNode != null) {
         this.logger.debug("Loading alarms file version {}", versionNode.asText());
         JsonNode alarmsNode = rootNode.get("alarms");
         if (alarmsNode != null) {
            return (List<Alarm>)this._mapper.treeToValue(alarmsNode, TypeFactory.defaultInstance().constructCollectionType(List.class, Alarm.class));
         } else {
            throw new IOException("Error reading alarms file: missing alarms data.");
         }
      } else {
         throw new IOException("Error reading alarms file: missing version.");
      }
   }

   public List<LiveGoal> readLiveGoals(Reader reader) throws IOException {
      ObjectNode rootNode = (ObjectNode)this._mapper.readValue(reader, ObjectNode.class);
      JsonNode versionNode = rootNode.remove("version");
      if (versionNode != null) {
         this.logger.debug("Loading live goals file version {}", versionNode.asText());
         JsonNode liveGoalsNode = rootNode.get("liveGoals");
         if (liveGoalsNode != null) {
            return (List<LiveGoal>)this._mapper.treeToValue(liveGoalsNode, TypeFactory.defaultInstance().constructCollectionType(List.class, LiveGoal.class));
         } else {
            throw new IOException("Error reading live goals file: missing live goals data.");
         }
      } else {
         throw new IOException("Error reading live goals file: missing version.");
      }
   }

   public class ColorDeserializer extends JsonDeserializer<Color> {
      public Color deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
         String colorHexString = jp.getText();
         return StringUtils.isNotBlank(colorHexString) ? new Color(Integer.parseInt(colorHexString, 16)) : null;
      }
   }
}
