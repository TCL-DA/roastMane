package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.roastingintelligence.annotation.NotMutuallyExclusiveSource;
import java.io.IOException;
import java.util.Collection;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.model.Channel;

@NotMutuallyExclusiveSource
public class GenericModbusUdpSourceFactory extends GenericModbusTcpSourceFactory {
   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      return new MeasurementSource[]{new GenericModbusUdpSource(this, this._inetAddress, this._port, this._registers, this._bigEndian)};
   }
}
