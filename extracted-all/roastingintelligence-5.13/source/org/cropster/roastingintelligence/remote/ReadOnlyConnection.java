package org.cropster.roastingintelligence.remote;

import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.cropster.roastingintelligence.model.Lot;
import org.cropster.roastingintelligence.model.LotTransaction;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.model.Scheduled;
import org.cropster.roastingintelligence.model.UnsavedBetweenBatchLog;

public class ReadOnlyConnection extends CsarConnectionImpl {
   private final AtomicLong _ids = new AtomicLong(1L);

   public ReadOnlyConnection(Version clientVersion, URI baseUri) {
      super(clientVersion, baseUri);
   }

   @Override
   public void markReference(Long profileId, Long lotId) {
   }

   @Override
   public synchronized boolean updateScheduled(Scheduled scheduled) {
      return true;
   }

   @Override
   public synchronized boolean updateScheduled(Scheduled scheduled, Lot roastedLot) {
      return true;
   }

   @Override
   public synchronized Lot startRoast(Roast roast) {
      long id = this._ids.getAndIncrement();
      Lot roastedLot = roast.getLot();
      roastedLot.setId(id);
      roastedLot.setName("Roast #" + id);
      roastedLot.setWeight(roast.getEndWeight());
      roastedLot.setIdTag(String.format("PR-%04d", id));
      return roastedLot;
   }

   @Override
   public synchronized void finishRoast(Roast roast) {
      roast.setSynced(true);
   }

   @Override
   public synchronized void update(Roast roast) {
   }

   @Override
   public synchronized void updateLot(Lot lot) {
   }

   @Override
   public Lot createBlendWithProfile(long profileId, String notes, List<LotTransaction> components) {
      return null;
   }

   @Override
   public Lot createManualBlend(String name, String notes, List<LotTransaction> components) {
      return null;
   }

   @Override
   public void storeBetweenBatchLog(UnsavedBetweenBatchLog betweenBatchLog) {
   }
}
