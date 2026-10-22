package com.cropster.roastingintelligence.ui;

import com.cropster.roastingintelligence.model.notification.Notification;
import com.cropster.roastingintelligence.ui.layout.ProductionSidebar;
import java.util.Collection;
import java.util.function.Consumer;
import javafx.application.Platform;
import org.cropster.roastingintelligence.model.Lot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlendNotificationController extends AbstractNotificationController {
   private static final Logger LOGGER = LoggerFactory.getLogger(BlendNotificationController.class);
   private final BlendNotificationController.NotificationHandler handler = new BlendNotificationController.NotificationHandler();

   @Override
   protected Consumer<Notification> createConsumer(String topic) {
      Consumer<Notification> consumer = switch (topic) {
         case "blend-created" -> this.handler::blendCreated;
         case "blend-collection-downloaded" -> this.handler::blendDownloaded;
         default -> throw new IllegalArgumentException("Unknown topic: " + topic);
      };
      return n -> Platform.runLater(() -> consumer.accept(n));
   }

   public void control(ProductionSidebar productionSidebar) {
      this.handler.init(productionSidebar);
   }

   public BlendNotificationController.NotificationHandler getHandler() {
      return this.handler;
   }

   public static final class NotificationHandler {
      private ProductionSidebar productionSidebar;

      public void blendCreated(Notification notification) {
         Lot blendLot = notification.getProperty(Lot.class, "blend-lot");
         if (blendLot != null) {
            this.productionSidebar.addLotToBlendList(blendLot);
         } else {
            BlendNotificationController.LOGGER.warn("Received blend created notification without blend lot: {}", notification);
         }
      }

      public void blendDownloaded(Notification notification) {
         Collection<Lot> blendLots = notification.getProperty(Collection.class, "blend-lot-collection");
         if (blendLots != null) {
            this.productionSidebar.setLotsToBlendList(blendLots);
         } else {
            BlendNotificationController.LOGGER.warn("Received blends downloaded notification without blend lot list: {}", notification);
         }
      }

      public void init(ProductionSidebar productionSidebar) {
         this.productionSidebar = productionSidebar;
      }
   }
}
