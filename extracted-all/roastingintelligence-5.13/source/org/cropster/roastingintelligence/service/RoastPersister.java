package org.cropster.roastingintelligence.service;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;
import org.cropster.roastingintelligence.model.Curve;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.model.UnsavedBetweenBatchLog;

public interface RoastPersister {
   File[] getFilesToUpload(FileFilter var1);

   Roast load(File var1) throws IOException;

   List<Roast> loadRecentRoasts(int var1) throws Exception;

   File save(Roast var1) throws IOException;

   File saveNew(Roast var1) throws IOException;

   File saveAsDemoRoast(Curve var1, String var2, long var3) throws IOException;

   File move(Roast var1) throws IOException;

   boolean compact(Roast var1) throws IOException;

   UnsavedBetweenBatchLog loadBetweenBatchLog(File var1) throws IOException;

   File saveBetweenBatchLog(UnsavedBetweenBatchLog var1) throws IOException;
}
