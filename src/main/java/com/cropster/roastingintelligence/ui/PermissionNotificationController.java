package com.cropster.roastingintelligence.ui;

import com.cropster.roastingintelligence.Data;
import com.cropster.roastingintelligence.model.notification.Notification;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.model.Group;
import org.cropster.roastingintelligence.model.GroupMember;
import org.cropster.roastingintelligence.model.Machine;
import org.cropster.roastingintelligence.remote.CsarConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PermissionNotificationController extends AbstractNotificationController {
   private static final Logger LOGGER = LoggerFactory.getLogger(PermissionNotificationController.class);
   private final PermissionNotificationController.NotificationHandler handler = new PermissionNotificationController.NotificationHandler();
   private final BooleanProperty riPermissionGranted = new SimpleBooleanProperty(true);
   private final BooleanProperty markReferencePermissionGranted = new SimpleBooleanProperty(true);
   private final BooleanProperty blendPermissionGranted = new SimpleBooleanProperty(true);
   private final BooleanProperty goalsModuleAvailable = new SimpleBooleanProperty(false);
   private final BooleanProperty taskModuleAvailable = new SimpleBooleanProperty(true);

   public static boolean isRiPermissionGrantedForCurrentMachine(AppModel appModel) {
      if (appModel != null) {
         String currentMachineGroupCode = null;
         Machine selectedMachine = appModel.getSelectedMachine();
         if (selectedMachine != null && selectedMachine.getGroup() != null) {
            currentMachineGroupCode = selectedMachine.getGroup().getCode();
         }

         GroupMember groupMember = currentMachineGroupCode != null ? appModel.getData().getGroupMembers().get(currentMachineGroupCode) : null;
         if (groupMember != null) {
            return groupMember.getPermissions().contains("roastRoastingIntelligence");
         }
      }

      return false;
   }

   public static boolean isAnyRiPermissionGranted(AppModel appModel) {
      Map<String, GroupMember> groupMembers = appModel.getData().getGroupMembers();
      return groupMembers != null && groupMembers.values().stream().anyMatch(gm -> gm.getPermissions().contains("roastRoastingIntelligence"));
   }

   public static boolean updateGroupsMembers(AppModel appModel, CsarConnection connection) {
      try {
         Collection<Group> groups = connection.getGroups();
         if (groups != null && !groups.isEmpty()) {
            if (appModel.getData() != null) {
               appModel.getData()
                  .setGroupMembers(groups.stream().map(Group::getCode).collect(Collectors.toMap(c -> (String)c, connection::getCurrentUserGroupMember)));
               return true;
            }
         } else {
            LOGGER.warn("User does not belong to any group!");
         }
      } catch (Exception var3) {
         LOGGER.warn("Problems syncing group members", var3);
      }

      return false;
   }

   public static void updateSingleGroupMember(Data data, String groupCode, CsarConnection connection) {
      Map<String, GroupMember> groupMembers = data.getGroupMembers();
      Map<String, GroupMember> var4 = groupMembers == null ? new HashMap() : new HashMap<>(data.getGroupMembers());
      var4.put(groupCode, connection.getCurrentUserGroupMember(groupCode));
      data.setGroupMembers(var4);
   }

   @Override
   protected Consumer<Notification> createConsumer(String topic) {
      byte var4 = -1;
      switch (topic.hashCode()) {
         case 1645487847:
            if (topic.equals("system-status-data-received")) {
               var4 = 0;
            }
         default:
            switch (var4) {
               case 0:
                  Consumer<Notification> consumer = this.handler::updatePermittedFeatures;
                  return n -> Platform.runLater(() -> consumer.accept(n));
               default:
                  throw new IllegalArgumentException("Unknown topic: " + topic);
            }
      }
   }

   public void init(AppModel appModel, CsarConnection connection, Consumer<Boolean> onPermissionChanged) {
      this.handler.init(appModel, connection, onPermissionChanged);
   }

   public ReadOnlyBooleanProperty blendPermissionGrantedProperty() {
      return this.blendPermissionGranted;
   }

   public ReadOnlyBooleanProperty goalsModuleAvailableProperty() {
      return this.goalsModuleAvailable;
   }

   public ReadOnlyBooleanProperty markReferencePermissionGrantedProperty() {
      return this.markReferencePermissionGranted;
   }

   public ReadOnlyBooleanProperty riPermissionGrantedProperty() {
      return this.riPermissionGranted;
   }

   public ReadOnlyBooleanProperty taskModuleAvailableProperty() {
      return this.taskModuleAvailable;
   }

   public PermissionNotificationController.NotificationHandler getHandler() {
      return this.handler;
   }

   public final class NotificationHandler {
      private AppModel appModel;
      private CsarConnection connection;
      private Consumer<Boolean> onPermissionChanged;
      private boolean initialized = false;

      public void updatePermittedFeatures(Notification notification) {
         if (this.appModel == null) {
            throw new IllegalStateException("PermissionNotificationController was not initialized");
         } else {
            String currentMachineGroupCode = null;
            Machine selectedMachine = this.appModel.getSelectedMachine();
            if (selectedMachine != null && selectedMachine.getGroup() != null) {
               currentMachineGroupCode = selectedMachine.getGroup().getCode();
            }

            boolean riPermitted = true;
            boolean markRoastAsReferencePermitted = false;
            boolean blendPermitted = false;
            boolean goalsPermitted = false;
            boolean tasksPermitted = false;
            GroupMember groupMember = currentMachineGroupCode != null ? this.appModel.getData().getGroupMembers().get(currentMachineGroupCode) : null;
            if (groupMember != null) {
               Collection<String> permissions = groupMember.getPermissions();
               riPermitted = permissions.contains("roastRoastingIntelligence");
               if (riPermitted) {
                  markRoastAsReferencePermitted = permissions.contains("roastRoastingIntelligenceMarkReference");
                  blendPermitted = permissions.contains("roastRoastingIntelligenceBlend");
                  goalsPermitted = groupMember.getModules().contains("roast.goals");
                  tasksPermitted = groupMember.getModules().contains("task");
               }
            }

            boolean riPermissionChanged = PermissionNotificationController.this.riPermissionGranted.get() != riPermitted;
            boolean markReferencePermissionChanged = PermissionNotificationController.this.markReferencePermissionGranted.get()
               != markRoastAsReferencePermitted;
            boolean blendPermissionChanged = PermissionNotificationController.this.blendPermissionGranted.get() != blendPermitted;
            boolean goalsModuleChanged = PermissionNotificationController.this.goalsModuleAvailable.get() != goalsPermitted;
            boolean taskModuleChanged = PermissionNotificationController.this.taskModuleAvailable.get() != tasksPermitted;
            PermissionNotificationController.this.riPermissionGranted.set(riPermitted);
            PermissionNotificationController.this.markReferencePermissionGranted.set(markRoastAsReferencePermitted);
            PermissionNotificationController.this.blendPermissionGranted.set(blendPermitted);
            PermissionNotificationController.this.goalsModuleAvailable.set(goalsPermitted);
            PermissionNotificationController.this.taskModuleAvailable.set(tasksPermitted);
            if (!this.initialized || riPermissionChanged || markReferencePermissionChanged || blendPermissionChanged || goalsModuleChanged || taskModuleChanged
               )
             {
               PermissionNotificationController.LOGGER
                  .info(
                     "Permissions for group {} ({}):\n\t     RI Access: {}\n\tMark Reference: {}\n\tBlend Creation: {}\n\t  Goals Module: {}\n\t   Task Module: {}",
                     new Object[]{
                        currentMachineGroupCode,
                        groupMember != null ? groupMember.getUsername() : "unknown",
                        riPermitted ? "granted" : "denied",
                        markRoastAsReferencePermitted ? "granted" : "denied",
                        blendPermitted ? "granted" : "denied",
                        goalsPermitted ? "available" : "not available",
                        tasksPermitted ? "available" : "not available"
                     }
                  );
            }

            if (this.onPermissionChanged != null) {
               this.onPermissionChanged.accept(riPermitted);
            }

            this.initialized = true;
         }
      }

      private void init(AppModel appModel, CsarConnection connection, Consumer<Boolean> onPermissionChanged) {
         this.appModel = appModel;
         this.connection = connection;
         this.onPermissionChanged = onPermissionChanged;
      }
   }
}
