package org.cropster.roastingintelligence.remote;

import com.cropster.roastingintelligence.exception.BetaAccessDeniedException;
import com.cropster.roastingintelligence.remote.Authenticator;
import com.cropster.roastingintelligence.util.json.ObjectMapperProvider;
import com.cropster.util.model.Unit;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.Response.StatusType;
import jakarta.ws.rs.core.Response.Status.Family;
import jakarta.ws.rs.ext.Provider;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.CookieHandler;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Map.Entry;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.cropster.roastingintelligence.model.Analysis;
import org.cropster.roastingintelligence.model.BetweenBatchLog;
import org.cropster.roastingintelligence.model.BlendProfile;
import org.cropster.roastingintelligence.model.Goal;
import org.cropster.roastingintelligence.model.Group;
import org.cropster.roastingintelligence.model.GroupMember;
import org.cropster.roastingintelligence.model.Lot;
import org.cropster.roastingintelligence.model.LotConsistencyCheck;
import org.cropster.roastingintelligence.model.LotTransaction;
import org.cropster.roastingintelligence.model.Machine;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.Profile;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.model.Scheduled;
import org.cropster.roastingintelligence.model.UnsavedBetweenBatchLog;
import org.cropster.roastingintelligence.model.User;
import org.cropster.roastingintelligence.model.WorkerTask;
import org.cropster.roastingintelligence.service.impl.JsonWriter;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.filter.EncodingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.message.GZipEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsarConnectionImpl implements CsarConnection {
   private static final GenericType<Collection<Group>> TYPE_GROUPS = new GenericType<Collection<Group>>() {
   };
   private static final GenericType<Collection<Machine>> TYPE_MACHINES = new GenericType<Collection<Machine>>() {
   };
   private static final GenericType<Collection<Profile>> TYPE_PROFILES = new GenericType<Collection<Profile>>() {
   };
   private static final GenericType<Collection<Lot>> TYPE_LOTS = new GenericType<Collection<Lot>>() {
   };
   private static final GenericType<Collection<BlendProfile>> TYPE_BLEND_PROFILES = new GenericType<Collection<BlendProfile>>() {
   };
   private static final GenericType<Collection<BetweenBatchLog>> TYPE_BETWEEN_BATCH_LOGS = new GenericType<Collection<BetweenBatchLog>>() {
   };
   private static final GenericType<Collection<Scheduled>> TYPE_SCHEDULES = new GenericType<Collection<Scheduled>>() {
   };
   private static final GenericType<Collection<WorkerTask>> TYPE_TASKS = new GenericType<Collection<WorkerTask>>() {
   };
   private static final GenericType<Collection<Goal>> TYPE_GOALS = new GenericType<Collection<Goal>>() {
   };
   private static final GenericType<Collection<LotConsistencyCheck>> TYPE_CHECKS = new GenericType<Collection<LotConsistencyCheck>>() {
   };
   private static final String API_PATH = "/api/rest/v3";
   private static final String PREDICTION_URL = "https://prediction.cropster.com";
   private static final String PREDICTION_ENDOINT_URL = "https://prediction.cropster.com/v1/models/curve_prediction_v2:predict";
   private static final String FIRSTCRACK_PREDICTION_ENDOINT_URL = "https://prediction.cropster.com/v1/models/first_crack_prediction_v2:predict";
   private static final String FLICK_PREDICTION_ENDOINT_URL = "https://prediction.cropster.com/v1/models/flick_prediction_v1:predict";
   private static final String COFFEE_GREEN = "coffee.green";
   private static final String COFFEE_DECAFFEINATING = "coffee.decaffeinating";
   private static final String COFFEE_BLENDING = "coffee.blending";
   private static final String COCOA_ROASTING = "cocoa.roasting";
   private static final String COCOA_DRYING = "cocoa.drying";
   private static final String FEATURE_FLAG_RI_PATTERN = "#[0-9]+-RI-.*";
   private static final String FEATURE_FLAG_RMM_PATTERN = "#[0-9]+-RMM-.*";
   private static final long LAST_MODIFIED_THRESHOLD = 15552000000L;
   private static final String[] MEASURES = new String[]{"roastValue", "roastValue2"};
   private static final List<String> NO_WORKERS = Collections.emptyList();
   private static final Logger LOGGER = LoggerFactory.getLogger(CsarConnectionImpl.class);
   private final Version _clientVersion;
   private final URI _baseUri;
   private final URI _apiUri;
   private Client _restClient;
   private final ObjectMapper _objectMapper;
   private final ClientConfig _clientConfig;
   private Authenticator _authenticator;
   private static final Comparator<Lot> NULL_LOCATION_COMPARATOR = new Comparator<Lot>() {
      public int compare(Lot o1, Lot o2) {
         int result = 0;
         if (o1.getLocation() != null && o2.getLocation() != null) {
            result = o1.getLocation().getName().compareTo(o2.getLocation().getName());
         } else {
            if (o1.getLocation() == null && o2.getLocation() != null) {
               return 1;
            }

            if (o1.getLocation() != null && o2.getLocation() == null) {
               return -1;
            }
         }

         if (result == 0) {
            if (o1.getProject() != null && o2.getProject() != null) {
               result = o1.getProject().getName().compareTo(o2.getProject().getName());
            } else {
               if (o1.getProject() == null && o2.getProject() != null) {
                  return 1;
               }

               if (o1.getProject() != null && o2.getProject() == null) {
                  return -1;
               }
            }
         }

         return result;
      }
   };

   public CsarConnectionImpl(Version clientVersion, URI baseUri) {
      this._clientVersion = clientVersion;
      this._baseUri = baseUri;
      this._apiUri = this._baseUri.resolve("/api/rest/v3");
      LOGGER.info("Using API {}", this._apiUri);
      CookieHandler.setDefault(null);
      java.net.Authenticator.setDefault(null);
      this._objectMapper = new ObjectMapper();
      this._objectMapper
         .setFilterProvider(
            new SimpleFilterProvider()
               .setFailOnUnknownId(false)
               .addFilter(
                  "unsavedBetweenBatchLog",
                  SimpleBeanPropertyFilter.filterOutAllExcept(new String[]{"previousBatch", "nextBatch", "startDate", "curves", "comments"})
               )
         );
      this._objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
      this._objectMapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
      this._objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
      this._clientConfig = new ClientConfig();
      this._clientConfig.property("jersey.config.client.followRedirects", false);
      this._clientConfig.property("jersey.config.client.connectTimeout", 10000);
      this._clientConfig.property("jersey.config.client.readTimeout", 20000);
      this._clientConfig.register(JacksonFeature.class);
      this._clientConfig.register(new ObjectMapperProvider(this._objectMapper));
      this._clientConfig.register(EncodingFilter.class);
      this._clientConfig.register(GZipEncoder.class);
      this._clientConfig.register(MultiPartFeature.class);
      this._clientConfig.register(new CsarConnectionImpl.UserAgentFilter(clientVersion));
      this._restClient = ClientBuilder.newClient(this._clientConfig);
   }

   private void handleResponse(Response response) {
      int statusCode = response.getStatus();
      if (!Family.SUCCESSFUL.equals(Family.familyOf(statusCode))) {
         Status status = Status.fromStatusCode(statusCode);
         switch (status) {
            case UNAUTHORIZED:
               throw new NotAuthorizedException(response);
            case BAD_REQUEST:
               throw new WebApplicationException((String)response.readEntity(String.class));
            default:
               throw new WebApplicationException(response);
         }
      }
   }

   @Override
   public synchronized void init(Authenticator authenticator) {
      this._authenticator = authenticator;
      this._restClient = ClientBuilder.newClient(this._clientConfig);
   }

   public URI getAppPath() {
      return this._apiUri;
   }

   @Override
   public void open() {
      this._authenticator.open(this._restClient);
   }

   @Override
   public void invalidate() {
      this._authenticator.invalidate();
   }

   @Override
   public void log() throws BetaAccessDeniedException {
      LOGGER.info("Logging on");
      Builder request = this._restClient.target(this._apiUri).path("log").path("on").request();
      Response response = request.head();
      this.handleResponse(response);
      if ("beta".equals(this._clientVersion.getClassifier())) {
         request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
            .path("property")
            .path("user.featureFlags")
            .path("value")
            .queryParam("username", new Object[]{this.getCurrentUser().getUsername()})
            .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE});
         boolean validFlagFound = false;
         String[] featureFlags = this.get(request, String[].class);
         if (featureFlags != null) {
            for (String featureFlag : featureFlags) {
               if (featureFlag.matches("#[0-9]+-RI-.*") || featureFlag.matches("#[0-9]+-RMM-.*")) {
                  LOGGER.info("Valid feature flag found: {}", Arrays.toString((Object[])featureFlags));
                  validFlagFound = true;
                  break;
               }
            }
         }

         if (!validFlagFound && !this.getCurrentUser().getUsername().matches(".*@cropster.com")) {
            LOGGER.error("No valid feature flag found: {}", Arrays.toString((Object[])featureFlags));
            throw new BetaAccessDeniedException("No valid feature flag found!");
         }
      }
   }

   private boolean isUnauthorizedResponse(Response response) {
      int statusCode = response.getStatus();
      Status status = Status.fromStatusCode(statusCode);
      return Status.UNAUTHORIZED.equals(status);
   }

   private <T> T get(Builder request, Class<T> type) {
      try {
         return (T)request.get(type);
      } catch (NotAuthorizedException var4) {
         LOGGER.warn("Not authorized! Retrying authentication ...");
         this.open();
         return (T)request.get(type);
      }
   }

   private <T> T get(Builder request, GenericType<T> genericType) {
      try {
         return (T)request.get(genericType);
      } catch (NotAuthorizedException var4) {
         LOGGER.warn("Not authorized! Retrying authentication ...");
         this.open();
         return (T)request.get(genericType);
      }
   }

   private Response post(Builder request, Entity<?> entity) {
      Response response = request.post(entity);
      if (this.isUnauthorizedResponse(response)) {
         LOGGER.warn("Not authorized! Retrying authentication ...");
         this.open();
         response = request.post(entity);
      }

      return response;
   }

   private Response put(Builder request, Entity<?> entity) {
      Response response = request.put(entity);
      if (this.isUnauthorizedResponse(response)) {
         LOGGER.warn("Not authorized! Retrying authentication ...");
         this.open();
         response = request.put(entity);
      }

      return response;
   }

   private Response delete(Builder request) {
      Response response = request.delete();
      if (this.isUnauthorizedResponse(response)) {
         LOGGER.warn("Not authorized! Retrying authentication ...");
         this.open();
         response = request.delete();
      }

      return response;
   }

   @Override
   public URL createSupportTicket(String subject, String description, File attachment) {
      FormDataMultiPart multiPart = new FormDataMultiPart();
      multiPart.field("subject", subject).field("description", description).bodyPart(new FileDataBodyPart("attachments[]", attachment));
      Response response = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("helpdesk")
         .path("ticket")
         .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE})
         .post(Entity.entity(multiPart, multiPart.getMediaType()));
      IOUtils.closeQuietly(multiPart);
      StatusType statusInfo = response.getStatusInfo();
      if (Family.SUCCESSFUL.equals(statusInfo.getFamily())) {
         try {
            return new URL(response.getHeaderString("Location"));
         } catch (MalformedURLException var8) {
            throw new RuntimeException("Error constructing URL", var8);
         }
      } else {
         LOGGER.error("Error creating support ticket: {} - {}", statusInfo.getStatusCode(), statusInfo.getReasonPhrase());
         return null;
      }
   }

   @Override
   public User getUser(String username) {
      Builder request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("user")
         .path(username)
         .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE});
      return this.get(request, User.class);
   }

   @Override
   public User getCurrentUser() {
      Builder request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("user")
         .path("current")
         .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE});
      return this.get(request, User.class);
   }

   @Override
   public GroupMember getCurrentUserGroupMember(String groupCode) {
      Builder request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("group")
         .path(groupCode)
         .path("groupMember")
         .path("current")
         .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE});
      return this.get(request, GroupMember.class);
   }

   @Override
   public Collection<Group> getGroups() {
      Builder request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("group")
         .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE});
      return this.get(request, TYPE_GROUPS);
   }

   @Override
   public String getGroupAccountManager(String groupCode) {
      Builder request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("group")
         .path(groupCode)
         .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE});
      JsonNode json = this.get(request, JsonNode.class);
      String accountManager = "support@cropster.com";
      if (json.has("accountManager") && json.get("accountManager").has("nameEmail")) {
         accountManager = json.get("accountManager").get("nameEmail").asText();
      }

      return accountManager;
   }

   @Override
   public boolean isGroupOnHold(String groupCode) {
      Builder request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("group")
         .path(groupCode)
         .path("contract")
         .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE});
      JsonNode json = this.get(request, JsonNode.class);
      String status = json.get("status").asText();
      return "HOLD".equalsIgnoreCase(status);
   }

   @Override
   public synchronized Machine getMachine(Long machineId) {
      return (Machine)((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("machine")
         .path(String.valueOf(machineId))
         .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE})
         .get(Machine.class);
   }

   @Override
   public synchronized Collection<Machine> getMachines() {
      return (Collection<Machine>)((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("machine")
         .path("full")
         .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE})
         .get(TYPE_MACHINES);
   }

   @Override
   public synchronized Collection<String> getRoastPersons(Machine machine) {
      Builder request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("property")
         .path("processing.workers")
         .path("value")
         .queryParam("groupCode", new Object[]{machine.getGroup().getCode()})
         .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE});
      String[] workers = this.get(request, String[].class);
      return workers != null ? Arrays.asList(workers) : NO_WORKERS;
   }

   @Override
   public synchronized Collection<Profile> getProfiles(Long machineId) {
      Builder request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("profile")
         .queryParam("machineId", new Object[]{machineId})
         .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE});
      return this.get(request, TYPE_PROFILES);
   }

   @Override
   public synchronized Lot getLot(Long lotId) {
      return (Lot)((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("lot")
         .path(String.valueOf(lotId))
         .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE})
         .get(Lot.class);
   }

   @Override
   public synchronized Collection<Lot> getLots(Machine machine, boolean samplesOnly) {
      WebTarget target = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("lot")
         .queryParam("perPage", new Object[]{300})
         .queryParam("lastModifiedSince", new Object[]{System.currentTimeMillis() - 15552000000L})
         .queryParam("sort", new Object[]{"lastModifiedDate"})
         .queryParam("sortOrder", new Object[]{"DESC"});
      if ("cocoa.roasting".equals(machine.getProcessingStep())) {
         target = target.queryParam("processingStep", new Object[]{"cocoa.drying"});
      } else {
         target = target.queryParam("processingStep", new Object[]{"coffee.green"}).queryParam("processingStep", new Object[]{"coffee.decaffeinating"});
      }

      if (machine.getLocation() != null) {
         target = target.queryParam("locationId", new Object[]{machine.getLocation().getId()});
      } else {
         target = target.queryParam("groupCode", new Object[]{machine.getGroup().getCode()});
      }

      if (samplesOnly) {
         target = target.queryParam("inventory", new Object[]{"false"});
      }

      Builder request = target.request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE});
      List<Lot> lots = new ArrayList<>(this.get(request, TYPE_LOTS));
      if (machine.getLocation() != null) {
         Collections.sort(lots, NULL_LOCATION_COMPARATOR);
      }

      return lots;
   }

   @Override
   public synchronized void updateLot(Lot lot) {
      Builder request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("lot")
         .path(lot.getId().toString())
         .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE});
      String json = this.get(request, String.class);

      try {
         ObjectReader reader = this._objectMapper.readerForUpdating(lot);
         reader.readValue(json);
      } catch (Exception var5) {
         throw new RuntimeException("Error updating lot", var5);
      }
   }

   @Override
   public synchronized Roast getProcessingRecord(Long lotId, Unit temperatureUnit) {
      Builder request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("processing")
         .path(lotId.toString())
         .queryParam("unit", new Object[]{temperatureUnit.name()})
         .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE});
      Roast roast = this.get(request, Roast.class);
      roast.getLot().setId(lotId);
      roast.setSynced(true);
      return roast;
   }

   @Override
   public Collection<BetweenBatchLog> getBetweenBatchLogs(Collection<Long> nextBatchIds) {
      WebTarget path = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class)).path("betweenBatchLog");

      for (Long nextBatchId : nextBatchIds) {
         path = path.queryParam("nextBatches[]", new Object[]{nextBatchId});
      }

      Builder request = path.request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE});
      return this.get(request, TYPE_BETWEEN_BATCH_LOGS);
   }

   private Analysis getLatestAnalysis(Long lotId, String type) {
      Builder request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("lot")
         .path(lotId.toString())
         .path("latestAnalysis")
         .queryParam("resultType", new Object[]{type})
         .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE});
      return this.get(request, Analysis.class);
   }

   @Override
   public Analysis getLatestCuppingAnalysis(Long lotId) {
      return this.getLatestAnalysis(lotId, "cuppingType");
   }

   @Override
   public Analysis getLatestPhysicalAnalysis(Long lotId) {
      return this.getLatestAnalysis(lotId, "physicalType");
   }

   @Override
   public synchronized Collection<Goal> getGoals(String groupCode) {
      Builder request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("processing")
         .path("goal")
         .queryParam("groupCode", new Object[]{groupCode})
         .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE});
      return this.get(request, TYPE_GOALS);
   }

   @Override
   public synchronized Collection<LotConsistencyCheck> getLotConsistencyChecks(String groupCode, Collection<Long> lotIds) {
      Builder request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("processing")
         .path("check")
         .queryParam("groupCode", new Object[]{groupCode})
         .queryParam("ids", lotIds != null ? lotIds.toArray() : null)
         .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE});
      return this.get(request, TYPE_CHECKS);
   }

   @Override
   public synchronized Collection<Scheduled> getSchedules(Long machineId) {
      Builder request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("schedule")
         .queryParam("machineId", new Object[]{machineId})
         .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE});
      return this.get(request, TYPE_SCHEDULES);
   }

   @Override
   public void markReference(Long profileId, Long lotId) {
      Map<String, Long> refernce = new HashMap<>();
      refernce.put("profile", profileId);
      refernce.put("lot", lotId);
      Builder request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("profile")
         .path(String.valueOf(profileId))
         .path("reference")
         .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE});
      Response response = this.post(request, Entity.json(refernce));
      this.handleResponse(response);
   }

   @Override
   public synchronized boolean updateScheduled(Scheduled scheduled, Lot roastedLot) {
      String status = scheduled.isDone() ? "DONE" : "SYNCED";
      Map<String, Object> payload = new HashMap<>();
      payload.put("id", scheduled.getId());
      payload.put("status", status);
      if (roastedLot != null) {
         payload.put("roastedLot", roastedLot.getId());
      }

      Builder request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("schedule")
         .path(scheduled.getId().toString())
         .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE});
      Response response = this.put(request, Entity.json(payload));
      boolean success = Family.SUCCESSFUL.equals(Family.familyOf(response.getStatus()));
      boolean serverError = Family.SERVER_ERROR.equals(Family.familyOf(response.getStatus()));
      if (success) {
         LOGGER.info("Updated scheduled {}: status={}", scheduled.getId(), status);
      } else {
         if (serverError) {
            throw new RuntimeException(
               String.format(
                  "%s %s on updating scheduled %s: status=%s", response.getStatus(), response.getStatusInfo().getReasonPhrase(), scheduled.getId(), status
               )
            );
         }

         LOGGER.warn("Failed to update scheduled {}: status={}", scheduled.getId(), status);
      }

      return success;
   }

   @Override
   public synchronized boolean updateScheduled(Scheduled scheduled) {
      return this.updateScheduled(scheduled, null);
   }

   @Override
   public synchronized Lot startRoast(Roast roast) {
      WebTarget target = this._restClient
         .target(this._apiUri)
         .path("processing")
         .queryParam("machine", new Object[]{roast.getMachine()})
         .queryParam("startDate", new Object[]{roast.getStartDate().getTime()});
      if (roast.getProfile() != null) {
         target = target.queryParam("profile", new Object[]{roast.getProfile()});
      }

      if (roast.getWorker() != null) {
         target = target.queryParam("worker", new Object[]{roast.getWorker()});
      }

      Builder request = target.request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE}).header("Content-Encoding", "gzip");
      Response response = this.post(request, Entity.json(roast.getLot().getSources()));
      this.handleResponse(response);

      try {
         ObjectReader reader = this._objectMapper.readerForUpdating(roast.getLot());
         reader.readValue((String)response.readEntity(String.class));
         return roast.getLot();
      } catch (Exception var6) {
         throw new RuntimeException("Error updating lot", var6);
      }
   }

   @Override
   public void finishRoast(Roast roast) {
      if (roast == null) {
         throw new IllegalArgumentException("Parameter roast must not be null");
      } else {
         try {
            Builder request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
               .path("processing")
               .path(roast.getLot().getId().toString())
               .request()
               .header("Content-Encoding", "gzip");
            StringWriter roastBuffer = new StringWriter();
            FilterProvider filters = new SimpleFilterProvider()
               .addFilter("roast", SimpleBeanPropertyFilter.serializeAllExcept(new String[]{"scheduledId", "markedAsReferenceDate"}));
            this._objectMapper.writer(filters).writeValue(roastBuffer, roast);
            Response response = this.put(request, Entity.json(roastBuffer.toString()));
            this.handleResponse(response);
         } catch (Exception var6) {
            throw new RuntimeException("Error finishing roast", var6);
         }

         Builder request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
            .path("processing")
            .path(roast.getLot().getId().toString())
            .path("endWeight")
            .request()
            .header("Content-Encoding", "gzip");
         Response response = this.post(request, Entity.json(roast.getEndWeight()));
         this.handleResponse(response);
      }
   }

   @Override
   public void update(Roast roast) {
      if (roast == null) {
         throw new IllegalArgumentException("Parameter roast must not be null");
      } else {
         Measure endWeight = roast.getEndWeight();
         if (endWeight == null) {
            throw new IllegalArgumentException("roast.endWeight must not be null");
         } else {
            try {
               Builder request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
                  .path("processing")
                  .path(roast.getLot().getId().toString())
                  .request()
                  .header("Content-Encoding", "gzip");
               StringWriter roastBuffer = new StringWriter();
               JsonWriter jsonWriter = new JsonWriter(this._clientVersion);
               jsonWriter.writeShort(roastBuffer, roast);
               Response response = this.put(request, Entity.json(roastBuffer.toString()));
               this.handleResponse(response);
            } catch (IOException var12) {
               throw new RuntimeException("Error serializing roast", var12);
            }

            for (String measureName : MEASURES) {
               Measure measureValue = roast.getMeasures().get(measureName);
               WebTarget target = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
                  .path("processing")
                  .path(roast.getLot().getId().toString())
                  .path("measures");
               if (measureValue != null) {
                  Builder request = target.request().header("Content-Encoding", "gzip");
                  Map<String, Object> measureMap = new HashMap<>();
                  measureMap.put("name", measureName);
                  measureMap.put("value", measureValue);
                  Response response = this.post(request, Entity.json(measureMap));
                  this.handleResponse(response);
               } else if (roast.getMeasures().containsKey(measureName)) {
                  Builder request = target.path(measureName).request();
                  this.handleResponse(this.delete(request));
               }
            }

            Builder request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
               .path("processing")
               .path(roast.getLot().getId().toString())
               .path("endWeight")
               .request()
               .header("Content-Encoding", "gzip");
            Response response = this.put(request, Entity.json(roast.getEndWeight()));
            this.handleResponse(response);
         }
      }
   }

   @Override
   public void update(WorkerTask task) {
      if (task == null) {
         throw new IllegalArgumentException("Parameter task must not be null");
      } else {
         try {
            Builder request = this._restClient.target(this._apiUri).path("taskInstance").path(task.getId().toString()).request();
            StringWriter buffer = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(this._clientVersion);
            jsonWriter.write(buffer, task);
            Response response = this.put(request, Entity.json(buffer.toString()));
            this.handleResponse(response);
         } catch (IOException var6) {
            throw new RuntimeException("Error serializing task", var6);
         }
      }
   }

   @Override
   public void storeBetweenBatchLog(UnsavedBetweenBatchLog betweenBatchLog) {
      Builder request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("betweenBatchLog")
         .request()
         .header("Content-Encoding", "gzip");
      Response response = this.post(request, Entity.json(betweenBatchLog));
      String responseMessage = (String)response.readEntity(String.class);
      if (response.getStatus() == Status.BAD_REQUEST.getStatusCode() && responseMessage.contains("must not be null")) {
         LOGGER.info("Skipping upload of between batch log: {} - {}", response.getStatusInfo().getReasonPhrase(), responseMessage);
      } else {
         this.handleResponse(response);
      }
   }

   @Override
   public Collection<WorkerTask> getTasks(Long machineId) {
      Builder request = this._restClient
         .target(this._apiUri)
         .path("taskInstance")
         .queryParam("machineId", new Object[]{machineId})
         .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE});
      return this.get(request, TYPE_TASKS);
   }

   @Override
   public String loadPreferences(long machineId) {
      Builder request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("preferences")
         .path("machine")
         .path(String.valueOf(machineId))
         .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE});
      Response response = request.get();
      return response.getStatus() == 200 ? (String)response.readEntity(String.class) : null;
   }

   @Override
   public String loadAlarmPreferences() {
      Builder request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("preferences")
         .path("alarms")
         .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE});
      Response response = request.get();
      return response.getStatus() == 200 ? (String)response.readEntity(String.class) : null;
   }

   @Override
   public String loadLiveGoalPreferences() {
      Builder request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("preferences")
         .path("liveGoals")
         .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE});
      Response response = request.get();
      return response.getStatus() == 200 ? (String)response.readEntity(String.class) : null;
   }

   @Override
   public int getCurvePredictionInterval() {
      Builder request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("property")
         .path("curvePrediction.requestInterval")
         .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE});
      Response response = request.get();
      if (response.getStatus() == 200) {
         JsonNode rootNode = (JsonNode)response.readEntity(JsonNode.class);
         return rootNode.findValue("value").asInt();
      } else {
         return -1;
      }
   }

   private double[] expandGasCurve(SortedMap<Long, Double> gasCurve, int length) {
      double[] gasValues = new double[length];
      if (gasCurve != null && !gasCurve.isEmpty()) {
         Iterator<Entry<Long, Double>> it = gasCurve.entrySet().iterator();
         Entry<Long, Double> currentEntry = null;
         double currentValue = 0.0;

         for (int i = 0; i < gasValues.length; i++) {
            if (it.hasNext() && (currentEntry == null || currentEntry.getKey() < (long)i * 1000L)) {
               currentEntry = it.next();
               currentValue = currentEntry.getValue();
            }

            gasValues[i] = currentValue;
         }
      }

      return gasValues;
   }

   @Override
   public double[] getCurvePrediction(
      Machine machine, Measure startWeight, Unit temperatureUnit, SortedMap<Long, Double> beanCurve, SortedMap<Long, Double> gasCurve
   ) {
      if (beanCurve.isEmpty()) {
         return null;
      } else {
         Double[] beanTempValues = beanCurve.values().toArray(new Double[0]);
         double[] gasValues = this.expandGasCurve(gasCurve, beanTempValues.length);
         Map<String, Object> inputs = new HashMap<>();
         inputs.put("curve_bean_temp", beanTempValues);
         inputs.put("unit_bean_temp", temperatureUnit);
         inputs.put("machine_type", machine.getType() != null ? machine.getType() : "");
         inputs.put("curve_gas", gasValues);
         inputs.put("start_weight", startWeight.toUnit(Unit.KG).getAmount());
         Map<String, Object> payload = new HashMap<>();
         payload.put("instances", new Object[]{inputs});
         if (LOGGER.isTraceEnabled()) {
            try {
               LOGGER.trace("Request: {}", this._objectMapper.writeValueAsString(payload));
            } catch (Exception var17) {
               LOGGER.error("Error logging JSON payload", var17);
            }
         }

         Builder request = ((WebTarget)this._restClient
               .target("https://prediction.cropster.com/v1/models/curve_prediction_v2:predict")
               .register(GZipEncoder.class))
            .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE});
         Response response = this.post(request, Entity.json(payload));
         this.handleResponse(response);
         JsonNode rootNode = (JsonNode)response.readEntity(JsonNode.class);
         JsonNode predictionNode = rootNode.findValue("predictions");
         if (predictionNode.size() != 1) {
            return null;
         } else {
            JsonNode predictionArrayNode = predictionNode.get(0);
            double[] predictions = new double[predictionArrayNode.size()];

            for (int j = 0; j < predictionArrayNode.size(); j++) {
               predictions[j] = predictionArrayNode.get(j).asDouble();
            }

            if (LOGGER.isTraceEnabled()) {
               LOGGER.trace("Response: {}", rootNode);
            }

            return predictions;
         }
      }
   }

   @Override
   public long[] getFirstCrackPrediction(Machine machine, Measure startWeight, Unit temperatureUnit, SortedMap<Long, Double> beanCurve) {
      Double[] beanTempValues = beanCurve.values().toArray(new Double[0]);
      Map<String, Object> inputs = new HashMap<>();
      inputs.put("curve_bean_temp", beanTempValues);
      inputs.put("unit_bean_temp", temperatureUnit);
      inputs.put("machine_type", machine != null && machine.getType() != null ? machine.getType() : "nan");
      inputs.put("machine_id", machine != null && machine.getId() != null ? machine.getId() : "");
      inputs.put("start_weight", startWeight.toUnit(Unit.KG).getAmount());
      inputs.put(
         "machine_capacity",
         machine != null && machine.getCapacity() != null ? machine.getCapacity().toUnit(Unit.KG).getAmount() : startWeight.toUnit(Unit.KG).getAmount()
      );
      Map<String, Object> payload = new HashMap<>();
      payload.put("instances", new Object[]{inputs});
      if (LOGGER.isTraceEnabled()) {
         try {
            LOGGER.debug("Request: {}", this._objectMapper.writeValueAsString(payload));
         } catch (Exception var17) {
            LOGGER.error("Error logging JSON payload", var17);
         }
      }

      Builder request = ((WebTarget)this._restClient
            .target("https://prediction.cropster.com/v1/models/first_crack_prediction_v2:predict")
            .register(GZipEncoder.class))
         .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE});
      Response response = this.post(request, Entity.json(payload));
      this.handleResponse(response);
      JsonNode rootNode = (JsonNode)response.readEntity(JsonNode.class);
      if (LOGGER.isTraceEnabled()) {
         try {
            LOGGER.debug("Response: {}", this._objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode));
         } catch (JsonProcessingException var16) {
            LOGGER.error("Error logging JSON response", var16);
         }
      }

      JsonNode predictionNode = rootNode.findValue("predictions");
      if (predictionNode.size() != 1) {
         return null;
      } else {
         JsonNode predictionArrayNode = predictionNode.get(0);
         JsonNode percentile10 = predictionArrayNode.findValue("prediction_10_percentile");
         JsonNode percentile50 = predictionArrayNode.findValue("prediction_50_percentile");
         JsonNode percentile90 = predictionArrayNode.findValue("prediction_90_percentile");
         return new long[]{(long)(percentile10.asDouble() * 1000.0), (long)(percentile50.asDouble() * 1000.0), (long)(percentile90.asDouble() * 1000.0)};
      }
   }

   @Override
   public Double getFlickPrediction(
      Machine machine, Measure startWeight, Unit temperatureUnit, SortedMap<Long, Double> beanCurve, SortedMap<Long, Double> gasCurve
   ) {
      Double[] beanTempValues = beanCurve.values().toArray(new Double[0]);
      double[] gasValues = this.expandGasCurve(gasCurve, beanTempValues.length);
      Map<String, Object> inputs = new HashMap<>();
      inputs.put("bean_temp", beanTempValues);
      inputs.put("unit_bean_temp", temperatureUnit);
      inputs.put("gas", gasValues);
      inputs.put("start_weight", startWeight.toUnit(Unit.KG).getAmount());
      inputs.put(
         "machine_capacity",
         machine != null && machine.getCapacity() != null ? machine.getCapacity().toUnit(Unit.KG).getAmount() : startWeight.toUnit(Unit.KG).getAmount()
      );
      Map<String, Object> payload = new HashMap<>();
      payload.put("instances", new Object[]{inputs});
      if (LOGGER.isTraceEnabled()) {
         try {
            LOGGER.debug("Request: {}", this._objectMapper.writeValueAsString(payload));
         } catch (Exception var15) {
            LOGGER.error("Error logging JSON payload", var15);
         }
      }

      Builder request = ((WebTarget)this._restClient
            .target("https://prediction.cropster.com/v1/models/flick_prediction_v1:predict")
            .register(GZipEncoder.class))
         .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE});
      Response response = this.post(request, Entity.json(payload));
      this.handleResponse(response);
      JsonNode rootNode = (JsonNode)response.readEntity(JsonNode.class);
      if (LOGGER.isTraceEnabled()) {
         try {
            LOGGER.debug("Response: {}", this._objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode));
         } catch (JsonProcessingException var14) {
            LOGGER.error("Error logging JSON response", var14);
         }
      }

      JsonNode predictionNode = rootNode.findValue("predictions");
      if (predictionNode != null && predictionNode.size() == 1) {
         predictionNode = predictionNode.get(0);
         if (predictionNode != null && predictionNode.size() == 1) {
            return predictionNode.get(0).asDouble();
         } else {
            LOGGER.error("Error parsing flick prediction response");
            return null;
         }
      } else {
         LOGGER.error("Error parsing flick prediction response");
         return null;
      }
   }

   @Override
   public void storeUserPreferences(String json) {
      Builder request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("preferences")
         .path("user")
         .request()
         .header("Content-Encoding", "gzip");
      Response response = this.post(request, Entity.json(json));
      this.handleResponse(response);
   }

   @Override
   public void storeMachinePreferences(long machineId, String json) {
      Builder request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("preferences")
         .path("machine")
         .path(String.valueOf(machineId))
         .request()
         .header("Content-Encoding", "gzip");
      Response response = this.post(request, Entity.json(json));
      this.handleResponse(response);
   }

   @Override
   public void storeAlarmPreferences(String json) {
      Builder request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("preferences")
         .path("alarms")
         .request()
         .header("Content-Encoding", "gzip");
      Response response = this.post(request, Entity.json(json));
      this.handleResponse(response);
   }

   @Override
   public void storeLiveGoalPreferences(String json) {
      Builder request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("preferences")
         .path("liveGoals")
         .request()
         .header("Content-Encoding", "gzip");
      Response response = this.post(request, Entity.json(json));
      this.handleResponse(response);
   }

   @Override
   public synchronized void close() {
      LOGGER.info("Logging off");
      Builder request = this._restClient.target(this._apiUri).path("log").path("off").request();
      Response response = request.head();
      this.handleResponse(response);
      this._authenticator.close();
   }

   @Override
   public Lot createManualBlend(String name, String notes, List<LotTransaction> components) {
      Validate.notBlank(name, "Null or empty 'name' parameter", new Object[0]);
      Validate.notEmpty(components, "Null or empty 'components' parameter", new Object[0]);
      WebTarget target = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("blend")
         .queryParam("name", new Object[]{name.trim()});
      if (StringUtils.isNotBlank(notes)) {
         target = target.queryParam("notes", new Object[]{notes.trim()});
      }

      Builder request = target.request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE}).header("Content-Encoding", "gzip");
      Response response = this.post(request, Entity.json(components));
      this.handleResponse(response);

      try {
         ObjectReader reader = this._objectMapper.readerFor(Lot.class);
         return (Lot)reader.readValue((String)response.readEntity(String.class));
      } catch (Exception var8) {
         throw new RuntimeException("Error getting the lot for the manually created blend", var8);
      }
   }

   @Override
   public Lot createBlendWithProfile(long profileId, String notes, List<LotTransaction> components) {
      Validate.notEmpty(components, "Null or empty 'components' parameter", new Object[0]);
      WebTarget target = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("blend")
         .queryParam("blendProfile", new Object[]{profileId});
      if (StringUtils.isNotBlank(notes)) {
         target = target.queryParam("notes", new Object[]{notes.trim()});
      }

      Builder request = target.request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE}).header("Content-Encoding", "gzip");
      Response response = this.post(request, Entity.json(components));
      this.handleResponse(response);

      try {
         ObjectReader reader = this._objectMapper.readerFor(Lot.class);
         return (Lot)reader.readValue((String)response.readEntity(String.class));
      } catch (Exception var9) {
         throw new RuntimeException("Error getting the lot for the manually created blend", var9);
      }
   }

   @Override
   public Collection<Lot> getBlends(String groupCode, String user) {
      Validate.notBlank(groupCode, "Null or empty 'groupCode' parameter", new Object[0]);
      Validate.notBlank(user, "Null or empty 'user' parameter", new Object[0]);
      Builder request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("lot")
         .queryParam("perPage", new Object[]{300})
         .queryParam("groupCode", new Object[]{groupCode})
         .queryParam("processingStep", new Object[]{"coffee.blending"})
         .queryParam("since", new Object[]{LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)})
         .queryParam("until", new Object[]{LocalDate.now().plusDays(1L).format(DateTimeFormatter.ISO_LOCAL_DATE)})
         .queryParam("createdBy", new Object[]{user})
         .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE});
      return new ArrayList<>(this.get(request, TYPE_LOTS));
   }

   @Override
   public Collection<BlendProfile> getBlendProfiles(String groupCode) {
      Validate.notBlank(groupCode, "Null or empty 'groupCode' parameter", new Object[0]);
      Builder request = ((WebTarget)this._restClient.target(this._apiUri).register(GZipEncoder.class))
         .path("blendProfile")
         .queryParam("groupCode", new Object[]{groupCode})
         .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE});
      return new ArrayList<>(this.get(request, TYPE_BLEND_PROFILES));
   }

   @Provider
   private static class UserAgentFilter implements ClientRequestFilter {
      private final Version _clientVersion;
      private String _userAgent;

      UserAgentFilter(Version clientVersion) {
         this._clientVersion = clientVersion;
      }

      public void filter(ClientRequestContext requestContext) throws IOException {
         if (this._userAgent == null) {
            this._userAgent = String.format(
               "RI/%s (%s %s; %s) Jersey/%s (HttpUrlConnection %s)",
               this._clientVersion,
               System.getProperty("os.name"),
               System.getProperty("os.version"),
               System.getProperty("os.arch"),
               System.getProperty("org.glassfish.jersey.version"),
               System.getProperty("java.version")
            );
            CsarConnectionImpl.LOGGER.info("User-Agent: {}", this._userAgent);
         }

         requestContext.getHeaders().add("User-Agent", this._userAgent);
         requestContext.getHeaders().add("Client-Version", "cropster-ri_" + this._clientVersion);
      }
   }
}
