package org.cropster.roastingintelligence.tasks;

import jakarta.ws.rs.NotAuthorizedException;
import java.util.Collections;
import java.util.Map;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.listener.AbstractMessagePublisher;
import org.cropster.roastingintelligence.listener.MessageListener;
import org.cropster.roastingintelligence.model.Machine;
import org.cropster.roastingintelligence.remote.CsarConnection;
import org.cropster.roastingintelligence.util.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MachineLoader extends AbstractMessagePublisher implements Runnable {
   private final Logger logger = LoggerFactory.getLogger(MachineLoader.class);
   private final CsarConnection _connection;
   private final AppModel _appModel;

   public MachineLoader(CsarConnection connection, AppModel appModel) {
      this._connection = connection;
      this._appModel = appModel;
   }

   @Override
   public void run() {
      Map<Long, Machine> machines;
      try {
         machines = Collections.synchronizedMap(CollectionUtil.toMap(this._connection.getMachines()));
      } catch (NotAuthorizedException var4) {
         this.publishMessage(MessageListener.MessageType.WARN, org.cropster.roastingintelligence.util.Messages.AUTHORIZATION_FAILED_MESSAGE);
         throw var4;
      }

      try {
         Machine selectedMachine = this._appModel.getSelectedMachine();
         if (selectedMachine != null && this._connection.isGroupOnHold(selectedMachine.getGroup().getCode())) {
            machines.put(selectedMachine.getId(), selectedMachine);
         }
      } catch (Exception var3) {
         this.logger.error("Error checking hold", var3);
      }

      this._appModel.getData().setMachines(machines);
      this.logger.info("Machines loaded");
   }
}
