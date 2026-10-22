package org.cropster.roastingintelligence.service.impl;

import com.cropster.roastingintelligence.Data;
import com.cropster.roastingintelligence.Preferences;
import com.cropster.roastingintelligence.model.alarms.Alarm;
import com.cropster.roastingintelligence.model.livegoals.LiveGoal;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import java.awt.Color;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.model.UnsavedBetweenBatchLog;
import org.cropster.roastingintelligence.model.WorkerTask;
import org.cropster.roastingintelligence.remote.Version;

public class JsonWriter {
   private final Version _version;
   private final ObjectMapper _mapper;

   public JsonWriter(Version version) {
      this._version = version;
      SimpleModule module = new SimpleModule();
      module.addSerializer(Color.class, new JsonWriter.ColorSerializer());
      this._mapper = new ObjectMapper();
      this._mapper.setFilters(new SimpleFilterProvider().setFailOnUnknownId(false));
      this._mapper.registerModule(module);
      this._mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
      this._mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
   }

   public void write(Writer writer, Preferences preferences) throws IOException {
      FilterProvider filters = new SimpleFilterProvider()
         .addFilter("preferences", SimpleBeanPropertyFilter.serializeAllExcept(new String[]{"sources", "channels", "states"}));
      this._mapper.setFilterProvider(filters);
      ObjectNode rootNode = (ObjectNode)this._mapper.convertValue(preferences, ObjectNode.class);
      rootNode.put("version", this._version.toString());
      this._mapper.writeValue(writer, rootNode);
   }

   public void writeMachinePreferences(Writer writer, Preferences preferences) throws IOException {
      FilterProvider filters = new SimpleFilterProvider()
         .addFilter("preferences", SimpleBeanPropertyFilter.filterOutAllExcept(new String[]{"selectedMachineId", "sources", "channels", "states"}));
      this._mapper.setFilterProvider(filters);
      ObjectNode rootNode = (ObjectNode)this._mapper.convertValue(preferences, ObjectNode.class);
      this._mapper.setFilterProvider(null);
      rootNode.put("version", this._version.toString());
      this._mapper.writeValue(writer, rootNode);
   }

   public void write(Writer writer, Data data) throws IOException {
      this._mapper.setFilterProvider(new SimpleFilterProvider().addFilter("roast", SimpleBeanPropertyFilter.serializeAll()));
      ObjectNode rootNode = (ObjectNode)this._mapper.convertValue(data, ObjectNode.class);
      rootNode.put("version", this._version.toString());
      this._mapper.writeValue(writer, rootNode);
   }

   public void write(Writer writer, Roast roast) throws IOException {
      ObjectNode rootNode = (ObjectNode)this._mapper.convertValue(roast, ObjectNode.class);
      rootNode.put("version", this._version.toString());
      this._mapper.writeValue(writer, rootNode);
   }

   public void writeShort(Writer writer, Roast roast) throws IOException {
      FilterProvider filters = new SimpleFilterProvider().addFilter("roast", SimpleBeanPropertyFilter.filterOutAllExcept(new String[]{"notes"}));
      this._mapper.writer(filters).writeValue(writer, roast);
   }

   public void writeBetweenBatchLog(Writer writer, UnsavedBetweenBatchLog betweenBatchLog) throws IOException {
      FilterProvider filters = new SimpleFilterProvider()
         .addFilter("unsavedBetweenBatchLog", SimpleBeanPropertyFilter.serializeAllExcept(new String[]{"duration", "previousBatch", "nextBatch"}));
      this._mapper.writer(filters).writeValue(writer, betweenBatchLog);
   }

   public void write(Writer writer, WorkerTask task) throws IOException {
      this._mapper.writer().writeValue(writer, task);
   }

   public void writeAlarms(Writer writer, List<Alarm> alarms) throws IOException {
      ObjectNode rootNode = this._mapper.createObjectNode();
      rootNode.put("alarms", this._mapper.valueToTree(alarms));
      rootNode.put("version", this._version.toString());
      this._mapper.writeValue(writer, rootNode);
   }

   public void writeLiveGoals(Writer writer, List<LiveGoal> liveGoals) throws IOException {
      ObjectNode rootNode = this._mapper.createObjectNode();
      rootNode.put("liveGoals", this._mapper.valueToTree(liveGoals));
      rootNode.put("version", this._version.toString());
      this._mapper.writeValue(writer, rootNode);
   }

   public class ColorSerializer extends JsonSerializer<Color> {
      public void serialize(Color value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
         String colorHexString = StringUtils.leftPad(Integer.toHexString(value.getRGB() & 16777215), 6, "0");
         jgen.writeString(colorHexString);
      }
   }
}
