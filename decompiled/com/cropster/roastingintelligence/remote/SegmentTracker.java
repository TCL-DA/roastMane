package com.cropster.roastingintelligence.remote;

import com.cropster.roastingintelligence.Data;
import com.cropster.roastingintelligence.ui.util.PlatformScheduler;
import com.segment.analytics.Analytics;
import com.segment.analytics.messages.TrackMessage;
import com.segment.analytics.messages.TrackMessage.Builder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.cropster.roastingintelligence.RoastingModel;
import org.cropster.roastingintelligence.listener.CsarDataListener;
import org.cropster.roastingintelligence.listener.RoastingListener;
import org.cropster.roastingintelligence.model.WorkerTask;

public class SegmentTracker implements RoastingListener, CsarDataListener {
   private static final String MAIN_USER_ID = "mainUserId";
   private static final String VERSION = "appVersion";
   private static final String DURATION = "roastDuration";
   private final Analytics analytics;
   private final String clientId;
   private final String userId;
   private volatile Data data;

   public static void appOpened(Analytics analytics, String clientId, String userId) {
      PlatformScheduler.executeBackgroundTask(() -> {
         Map<String, String> properties = new HashMap<>();
         properties.put("appVersion", System.getProperty("org.cropster.roastingintelligence.version"));
         if (userId != null) {
            properties.put("mainUserId", userId);
         }

         analytics.enqueue(((Builder)TrackMessage.builder("appOpened").anonymousId(clientId)).properties(properties));
      });
   }

   public SegmentTracker(Analytics analytics, String clientId, String userId, Data data) {
      Objects.requireNonNull(analytics, "analytics must not be null");
      Objects.requireNonNull(clientId, "clientId must not be null");
      Objects.requireNonNull(userId, "userId must not be null");
      Objects.requireNonNull(data, "data must not be null");
      this.analytics = analytics;
      this.clientId = clientId;
      this.userId = userId;
      this.data = data;
   }

   private Map<String, Object> extractRoastProperties(RoastingModel roastingModel) {
      Map<String, Object> properties = new HashMap<>();
      properties.put("mainUserId", this.userId);
      return properties;
   }

   private Map<String, Object> extractRoastProgressProperties(RoastingModel roastingModel) {
      Map<String, Object> properties = this.extractRoastProperties(roastingModel);
      properties.put("roastDuration", roastingModel.getRoast().getDuration() / 1000L);
      return properties;
   }

   @Override
   public void roastingStarted(RoastingModel roastingModel) {
      PlatformScheduler.executeBackgroundTask(() -> {
         Map<String, Object> properties = this.extractRoastProperties(roastingModel);
         this.analytics.enqueue(((Builder)TrackMessage.builder("roastStarted").anonymousId(this.clientId)).properties(properties));
      });
   }

   @Override
   public void roastingForced(RoastingModel roastingModel) {
   }

   @Override
   public void roastingAborted(RoastingModel roastingModel) {
      PlatformScheduler.executeBackgroundTask(() -> {
         Map<String, Object> properties = this.extractRoastProgressProperties(roastingModel);
         this.analytics.enqueue(((Builder)TrackMessage.builder("roastAborted").anonymousId(this.clientId)).properties(properties));
      });
   }

   @Override
   public void roastingStopped(RoastingModel roastingModel) {
   }

   @Override
   public void roastingFinalized(RoastingModel roastingModel) {
      PlatformScheduler.executeBackgroundTask(() -> {
         Map<String, Object> properties = this.extractRoastProgressProperties(roastingModel);
         this.analytics.enqueue(((Builder)TrackMessage.builder("roastFinished").anonymousId(this.clientId)).properties(properties));
      });
   }

   @Override
   public void dataReceived(Data data) {
      this.data = data;
   }

   @Override
   public void roastsUploaded(int count, Throwable exception) {
   }

   @Override
   public void tasksReceived(Collection<WorkerTask> tasks) {
   }
}
