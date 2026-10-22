package org.cropster.roastingintelligence.listener;

import com.cropster.roastingintelligence.Data;
import java.util.Collection;
import org.cropster.roastingintelligence.model.WorkerTask;

public interface CsarDataListener {
   void tasksReceived(Collection<WorkerTask> var1);

   void dataReceived(Data var1);

   void roastsUploaded(int var1, Throwable var2);
}
