package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.roastingintelligence.ui.util.WindowUtil;
import com.fazecast.jSerialComm.SerialPort;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javafx.scene.control.Alert.AlertType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.measurement.RefreshableSourceParameter;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SerialSourceFactory implements MeasurementSourceFactory {
   private final Logger logger = LoggerFactory.getLogger(SerialSourceFactory.class);
   protected static final String PARAM_SERIAL_PORT = "serialPort";
   protected String _serialPort;
   protected String _serialPortName;

   @Override
   public int getChannelCount() {
      return 0;
   }

   @Override
   public String[] getTypes() {
      return new String[]{"Default"};
   }

   protected void checkLinuxGroupMembership(String group) {
      if (SystemUtils.IS_OS_LINUX) {
         try {
            Process groupsCommand = Runtime.getRuntime().exec("groups");

            try (BufferedReader outputReader = new BufferedReader(new InputStreamReader(groupsCommand.getInputStream()))) {
               if (!outputReader.readLine().contains(group)) {
                  WindowUtil.showAlert(
                     AlertType.WARNING,
                     null,
                     org.cropster.roastingintelligence.util.Messages.m_("System setup"),
                     org.cropster.roastingintelligence.util.Messages.m_(
                           "Execute the following command on the terminal:\n{0}", String.format("sudo usermod -a -G %s $(whoami)", group)
                        )
                        + "\n\n"
                        + org.cropster.roastingintelligence.util.Messages.m_(
                           "For the change to take effect you have to log out of your operating system account!"
                        )
                  );
               }
            }
         } catch (Exception var8) {
            this.logger.warn("Error checking for group membership: {}", group, var8);
         }
      }
   }

   @Override
   public Set<SourceParameter> getParameters() {
      SourceParameter serialPortParameter = new RefreshableSourceParameter("serialPort", String.class, this._serialPort) {
         @Override
         public String[] refresh(Collection<SourceParameter> currentParameters) {
            for (SourceParameter parameter : currentParameters) {
               if ("serialPort".equals(parameter.getName())) {
                  Set<String> portNames = new LinkedHashSet<>();

                  try {
                     for (SerialPort serialPort : SerialPort.getCommPorts()) {
                        String systemPortName = serialPort.getSystemPortName();
                        if (!SystemUtils.IS_OS_WINDOWS) {
                           systemPortName = "/dev/" + systemPortName;
                        }

                        String displayName = serialPort.getDescriptivePortName();
                        if (StringUtils.isNotBlank(serialPort.getPortDescription()) && !serialPort.getPortDescription().equalsIgnoreCase(displayName)) {
                           displayName = displayName + " - " + serialPort.getPortDescription();
                        }

                        this.setOptionDisplayName(systemPortName, displayName);
                        portNames.add(systemPortName);
                     }

                     SerialSourceFactory.this.logger.info("Serial ports found: {}", portNames);
                     if (SerialSourceFactory.this._serialPort != null && !portNames.contains(SerialSourceFactory.this._serialPort)) {
                        portNames.add(SerialSourceFactory.this._serialPort);
                     }
                  } catch (LinkageError var11) {
                     SerialSourceFactory.this.logger.error("Error searching for serial ports", var11);
                  }

                  return portNames.toArray(new String[portNames.size()]);
               }
            }

            return new String[0];
         }
      };
      serialPortParameter.setOptions(new String[0]);
      Set<SourceParameter> parameters = new LinkedHashSet<>();
      parameters.add(serialPortParameter);
      return Collections.unmodifiableSet(parameters);
   }

   @Override
   public void setup(Set<SourceParameter> parameters) throws Exception {
      this.logger.info("Parameters: {}", StringUtils.strip(parameters.toString(), "{}"));

      for (SourceParameter parameter : parameters) {
         if ("serialPort".equals(parameter.getName())) {
            this.checkLinuxGroupMembership("dialout");
            this._serialPort = parameter.getValue();
            if (this._serialPort == null || this._serialPort.trim().length() == 0) {
               throw new IllegalArgumentException("Serial port required!");
            }

            this._serialPortName = this._serialPort.startsWith("/dev/") ? this._serialPort.substring(5) : this._serialPort;
         }
      }
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName();
   }
}
