package org.cropster.roastingintelligence.tasks;

import com.cropster.roastingintelligence.ui.PermissionNotificationController;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.listener.AbstractMessagePublisher;
import org.cropster.roastingintelligence.model.Machine;
import org.cropster.roastingintelligence.remote.CsarConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupMemberLoader extends AbstractMessagePublisher implements Runnable {
   private final Logger logger = LoggerFactory.getLogger(GroupMemberLoader.class);
   private final AppModel appModel;
   private final CsarConnection connection;

   public GroupMemberLoader(AppModel appModel, CsarConnection connection) {
      this.appModel = appModel;
      this.connection = connection;
   }

   @Override
   public void run() {
      Machine machine = this.appModel.getData().getMachines().get(this.appModel.getSelectedMachine().getId());
      PermissionNotificationController.updateSingleGroupMember(this.appModel.getData(), machine.getGroup().getCode(), this.connection);
      this.logger.info("Group members synced from server");
      this.appModel.fireCsarDataReceived();
   }
}
