package org.cropster.roastingintelligence.listener;

import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;

public interface ConnectorConfigurationListener {
   void settingsChanged(MeasurementSourceFactory var1);
}
