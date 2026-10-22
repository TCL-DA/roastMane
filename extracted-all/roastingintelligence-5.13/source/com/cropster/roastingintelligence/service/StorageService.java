package com.cropster.roastingintelligence.service;

import com.cropster.roastingintelligence.Data;
import com.cropster.roastingintelligence.Preferences;
import com.cropster.roastingintelligence.model.alarms.Alarm;
import com.cropster.roastingintelligence.model.livegoals.LiveGoal;
import java.io.IOException;
import java.util.List;

public interface StorageService {
   boolean isPreferencesAvailable();

   Preferences loadPreferences() throws IOException;

   Preferences loadMachinePreferences() throws IOException;

   void savePreferences(Preferences var1) throws IOException;

   Data loadDataCache();

   void saveDataCache(Data var1);

   List<Alarm> loadAlarms();

   void saveAlarms(List<Alarm> var1) throws IOException;

   List<LiveGoal> loadLiveGoals();

   void saveLiveGoals(List<LiveGoal> var1) throws IOException;
}
