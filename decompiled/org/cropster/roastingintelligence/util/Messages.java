package org.cropster.roastingintelligence.util;

import com.cropster.roastingintelligence.model.RoastingPhase;
import com.cropster.roastingintelligence.model.livegoals.CheckResult;
import com.cropster.roastingintelligence.model.livegoals.LiveGoalDisplayMode;
import com.cropster.roastingintelligence.ui.components.RoastLabel;
import com.cropster.roastingintelligence.ui.util.KeyboardShortcut;
import com.cropster.util.model.Unit;
import com.phidget22.RTDWireSetup;
import gnu.gettext.GettextResource;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.AtillaSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.AutonicsTk4sSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.BellaTWSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.BertoSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.BescaAutoSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.BescaManualSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.BridgePhidgetSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.BuhlerPlcSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.CaparaoSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.CarmomaqNetworkSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.CoffedNetworkSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.CoffedSerialSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.CoffeeTechNetworkSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.CoffeeTechSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.CoffeetoolPlcSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.CogenPlcSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.CogenPlcV2SourceFactory;
import org.cropster.roastingintelligence.measurement.impl.ColorTrackLaserSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.DemoSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.DiedrichAutoSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.DiedrichGasAirflowSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.DongyiSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.DutchMasterSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.FabricaRoastersSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.GarantiSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.GenericModbusRtuSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.GenericModbusTcpSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.GenericModbusUdpSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.GenericSiemensS7SourceFactory;
import org.cropster.roastingintelligence.measurement.impl.GiesenPlcSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.ImfPlcSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.ImfPlcV2SourceFactory;
import org.cropster.roastingintelligence.measurement.impl.IpXenakisPlcSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.JoperNetworkSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.KapokSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.KirschUndMausserPlcSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.KraffePlcSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.LillaPlcSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.LoringPlcSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.MillCitySourceFactory;
import org.cropster.roastingintelligence.measurement.impl.NordicCoffeeSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.PetronciniSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.Phidget1132SourceFactory;
import org.cropster.roastingintelligence.measurement.impl.PhidgetTmp1100SourceFactory;
import org.cropster.roastingintelligence.measurement.impl.PhidgetTmp1101SourceFactory;
import org.cropster.roastingintelligence.measurement.impl.PhidgetTmp1200SourceFactory;
import org.cropster.roastingintelligence.measurement.impl.PressureSensorPercentSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.PressureSensorSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.PrimoSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.ProbatPlcSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.ProbatSampleRoasterSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.RceSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.SantokerSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.SchuilenburgPlcSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.SivetzPlcSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.TemperaturePhidgetSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.ToperSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.TyphoonSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.VortecsProSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.WatlowEzZoneSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.YangChiaSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.YoctopuceSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.simulation.DeviceSimulatorSourceFactory;
import org.cropster.roastingintelligence.model.WorkerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Messages {
   private static final Logger LOGGER = LoggerFactory.getLogger(org.cropster.roastingintelligence.util.Messages.class);
   public static final String TEXT_COMMENT = "TEXT_COMMENT";
   public static final String DOTS = " ...";
   public static final String DOT = ".";
   public static final String AUTHORIZATION_FAILED_MESSAGE;
   public static final String AUTO_START_DESCRIPTION = "AutoStartDescription";
   public static final String AUTO_START_CAPTION = "AutoStartCaption";
   public static final String AUTO_START_READY_MESSAGE = "AutoStartReadyMessage";
   public static final String AUTO_START_NOT_READY_MESSAGE = "AutoStartNotReadyMessage";
   public static final String MACHINE_NOT_CONNECTED = "MachineNotConnected";
   public static final String NO_MACHINE_ACCESS = "NoMachineAccess";
   private static ResourceBundle _bundle;
   private static ResourceBundle _aliasBundle;

   public static ResourceBundle getBundle() {
      return _aliasBundle;
   }

   public static ResourceBundle forEnum(Class<? extends Enum<?>> enumClass) {
      return ResourceBundle.getBundle(enumClass.getName());
   }

   public static String m_(String key) {
      if (_aliasBundle.containsKey(key)) {
         return _aliasBundle.getString(key);
      } else {
         if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Missing key: {}", key);
         }

         return key;
      }
   }

   public static String m_(Enum<?> e) {
      try {
         ResourceBundle enumBundle = ResourceBundle.getBundle(e.getClass().getName());
         if (enumBundle != null && enumBundle.containsKey(e.name())) {
            return enumBundle.getString(e.name());
         }
      } catch (MissingResourceException var2) {
      }

      if (_aliasBundle.containsKey(e.name())) {
         return _aliasBundle.getString(e.name());
      } else {
         if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Missing key for enum: {}.{}", e.getClass().getSimpleName(), e.name());
         }

         return e.toString();
      }
   }

   public static String m_(String key, Class<? extends MeasurementSourceFactory> sourceFactoryClass) {
      String compoundKey = key + sourceFactoryClass.getSimpleName();
      return _aliasBundle.containsKey(compoundKey) ? _aliasBundle.getString(compoundKey) : m_(key);
   }

   public static String m_(String key, Object... parameters) {
      return MessageFormat.format(m_(key), parameters);
   }

   public static String p_(String singularKey, String pluralKey, int num) {
      String message = GettextResource.ngettext(_bundle, singularKey, pluralKey, (long)num);
      return MessageFormat.format(message, num);
   }

   public static String c_(String context, String key) {
      return GettextResource.pgettext(_bundle, context, key);
   }

   private Messages() {
   }

   static {
      Locale locale = Locale.getDefault();
      if ("zh".equals(locale.getLanguage()) && "HK".equals(locale.getCountry())) {
         locale = Locale.TRADITIONAL_CHINESE;
      }

      LOGGER.info("Loading resource bundle for {}", locale);

      try {
         _bundle = ResourceBundle.getBundle("Messages", locale);
      } catch (MissingResourceException var2) {
         LOGGER.warn("Resource bundle not found", var2);
         _bundle = new EmptyResourceBundle();
      }

      AliasResourceBundle aliasBundle = new AliasResourceBundle(_bundle);
      aliasBundle.alias("firstCrack").m_("First crack");
      aliasBundle.alias("secondCrack").m_("Second crack");
      aliasBundle.alias("turningPoint").m_("Turning point");
      aliasBundle.alias("colorChange").m_("Color change");
      aliasBundle.alias("end").m_("End");
      aliasBundle.alias("GAS").m_("Gas");
      aliasBundle.alias("AIRFLOW").m_("Airflow");
      aliasBundle.alias("endTemperature").m_("End temperature");
      aliasBundle.alias("roastValue").m_("Roast value");
      aliasBundle.alias("roastValue2").m_("Roast value 2");
      aliasBundle.alias("duration").m_("Duration");
      aliasBundle.alias("developmentTime").m_("Development time");
      aliasBundle.alias("developmentTimeRatio").m_("Dev. time ratio");
      aliasBundle.alias("startWeight").m_("Start weight");
      aliasBundle.alias("endWeight").m_("End weight");
      aliasBundle.alias("weightLoss").m_("Weight loss");
      aliasBundle.alias("simpleRor10").m_("Bean temp. RoR/10s");
      aliasBundle.alias("simpleRor15").m_("Bean temp. RoR/15s");
      aliasBundle.alias("simpleRor30").m_("Bean temp. RoR/30s");
      aliasBundle.alias("simpleRor60").m_("Bean temp. RoR/60s");
      aliasBundle.alias("beanRoR").m_("Bean temp. RoR/30s (time framed)");
      aliasBundle.alias("constantRoR").m_("Constant RoR");
      aliasBundle.alias("constantBeanTemp").m_("Constant bean temp.");
      aliasBundle.alias("TEXT_COMMENT").m_("Text comments");
      aliasBundle.alias("ambientTemperature").m_("Ambient temp.");
      aliasBundle.alias("beanTemperature").m_("Bean temp.");
      aliasBundle.alias("environmentalTemperature").m_("Env. temp.");
      aliasBundle.alias("afterburnerTemperature").m_("Afterburner");
      aliasBundle.alias("exhaustTemperature").m_("Exhaust");
      aliasBundle.alias("otherTemperature").m_("Other temp.");
      aliasBundle.alias("burnerTemperature").m_("Burner temp.");
      aliasBundle.alias("afterburnerTemperature2").m_("Afterburner 2");
      aliasBundle.alias("stackTemperature").m_("Stack temp.");
      aliasBundle.alias("inletTemperature").m_("Inlet temp.");
      aliasBundle.alias("returnTemperature").m_("Return temp.");
      aliasBundle.alias("drumTemperature").m_("Drum temp.");
      aliasBundle.alias("drumPressure").m_("Drum pressure");
      aliasBundle.alias("drumPressureControl").m_("Drum pressure control");
      aliasBundle.alias("drumSpeed").m_("Drum speed");
      aliasBundle.alias("drumSpeedControl").m_("Drum speed control");
      aliasBundle.alias("fanSpeed").m_("Fan speed");
      aliasBundle.alias("fanSpeedControl").m_("Fan speed control");
      aliasBundle.alias(MillCitySourceFactory.MillCityVersion.VERSION1.name()).m_("Phidgets only");
      aliasBundle.alias(MillCitySourceFactory.MillCityVersion.VERSION2A.name()).m_("Phidgets/Shihlin controls (USB port on the back)");
      aliasBundle.alias(MillCitySourceFactory.MillCityVersion.VERSION2B.name()).m_("Phidgets/Delta controls (USB port on the back)");
      aliasBundle.alias(MillCitySourceFactory.MillCityVersion.VERSION3.name()).m_("Phidget/Delta controls (data port on control panel and Phidget)");
      aliasBundle.alias(MillCitySourceFactory.MillCityVersion.VERSION4.name()).m_("MCR Series (data port on control panel)");
      aliasBundle.alias(MillCitySourceFactory.MillCityVersion.VERSION5.name()).m_("Delta controls (Standard control panel)");
      aliasBundle.alias(MillCitySourceFactory.MillCityVersion.VERSION5B.name()).m_("Fotek controls (Standard control panel)");
      aliasBundle.alias("gas").m_("Gas");
      aliasBundle.alias("gasControl").m_("Gas control");
      aliasBundle.alias("gasPressure").m_("Gas pressure");
      aliasBundle.alias("airflow").m_("Airflow");
      aliasBundle.alias("airflowControl").m_("Airflow control");
      aliasBundle.alias("roastColor").m_("Roast color");
      aliasBundle.alias("setTemperatureControl").m_("Set temperature");
      aliasBundle.alias("custom").m_("Custom");
      aliasBundle.alias("customControl").m_("Custom control");
      aliasBundle.alias(Unit.CELSIUS.name()).m_("Celsius");
      aliasBundle.alias(Unit.FAHRENHEIT.name()).m_("Fahrenheit");
      aliasBundle.alias(WorkerTask.TaskStatus.TO_DO).m_("To do");
      aliasBundle.alias(WorkerTask.TaskStatus.IN_PROGRESS).m_("In progress");
      aliasBundle.alias(WorkerTask.TaskStatus.DONE).m_("Done");
      aliasBundle.alias(WorkerTask.TaskStatus.NOT_DONE).m_("Won't do");
      aliasBundle.alias(Storage.FLOAT.name()).m_("Floating-point (32-bit)");
      aliasBundle.alias(Storage.INTEGER.name()).m_("Integer (32-bit)");
      aliasBundle.alias(Storage.INTEGER_16.name()).m_("Integer (16-bit)");
      aliasBundle.alias(Storage.DECIMAL1.name()).m_("Decimal with 1 digit (32-bit)");
      aliasBundle.alias(Storage.DECIMAL1_16.name()).m_("Decimal with 1 digit (16-bit)");
      aliasBundle.alias(Storage.DECIMAL2.name()).m_("Decimal with 2 digits (32-bit)");
      aliasBundle.alias(Storage.DECIMAL2_16.name()).m_("Decimal with 2 digits (16-bit)");
      aliasBundle.alias(Storage.DECIMAL3.name()).m_("Decimal with 3 digits (32-bit)");
      aliasBundle.alias(DiedrichGasAirflowSourceFactory.DiedrichGas.VOLTAGE_DIVIDER.name()).m_("Voltage divider (All models since 2018)");
      aliasBundle.alias(DiedrichGasAirflowSourceFactory.DiedrichGas.WC6.name()).m_("Transducer 6 wc (IR-12 with natural gas)");
      aliasBundle.alias(DiedrichGasAirflowSourceFactory.DiedrichGas.WC7.name()).m_("Transducer 7 wc (IR series on natural gas)");
      aliasBundle.alias(DiedrichGasAirflowSourceFactory.DiedrichGas.WC11.name()).m_("Transducer 11 wc (IR series on LP gas)");
      aliasBundle.alias(RTDWireSetup.WIRES_2.name()).m_("Two-wire mode");
      aliasBundle.alias(RTDWireSetup.WIRES_3.name()).m_("Three-wire mode");
      aliasBundle.alias(RTDWireSetup.WIRES_4.name()).m_("Four-wire mode");
      aliasBundle.alias(WorkerTask.TaskFrequency.DAILY.name()).m_("Daily");
      aliasBundle.alias(WorkerTask.TaskFrequency.WEEKLY.name()).m_("Weekly");
      aliasBundle.alias(WorkerTask.TaskFrequency.WORKING_DAYS.name()).m_("Working days");
      aliasBundle.alias(WorkerTask.TaskFrequency.MONTHLY.name()).m_("Monthly");
      aliasBundle.alias(WorkerTask.TaskFrequency.HALF_YEARLY.name()).m_("Half-yearly");
      aliasBundle.alias(WorkerTask.TaskFrequency.YEARLY.name()).m_("Yearly");
      aliasBundle.alias(WorkerTask.TaskFrequency.ONE_TIME.name()).m_("One-off task");
      aliasBundle.alias(WorkerTask.TaskFrequency.ROAST_VOLUME.name()).m_("Roast volume");
      aliasBundle.alias(WorkerTask.TaskFrequency.ROAST_NUMBER.name()).m_("Number of roasts");
      aliasBundle.alias(WorkerTask.TaskFrequency.ROAST_DURATION.name()).m_("Roasting time");
      aliasBundle.alias("chargeState").m_("Charge");
      aliasBundle.alias("dischargeState").m_("Discharge");
      aliasBundle.alias("burnerState").m_("Burner");
      aliasBundle.alias("coolerState").m_("Cooler");
      aliasBundle.alias("stirrerState").m_("Stirrer");
      aliasBundle.alias("standbyState").m_("Standby");
      aliasBundle.alias("drumState").m_("Drum");
      aliasBundle.alias(RoastingPhase.BETWEEN_BATCHES).m_("Pre-roasting");
      aliasBundle.alias(RoastingPhase.ROASTING).m_("Roasting");
      aliasBundle.alias(RoastingPhase.ALWAYS).m_("Always");
      aliasBundle.alias(DemoSourceFactory.class.getSimpleName()).m_("Demo sensor (replaying a curve from a file)");
      aliasBundle.alias(DeviceSimulatorSourceFactory.class.getSimpleName()).m_("Demo control (fake control doing nothing)");
      aliasBundle.alias(TemperaturePhidgetSourceFactory.class.getSimpleName()).m_("Phidgets Thermocouple Sensor (1048)");
      aliasBundle.alias(PhidgetTmp1101SourceFactory.class.getSimpleName()).m_("Phidgets Thermocouple Sensor (TMP1101)");
      aliasBundle.alias(PhidgetTmp1100SourceFactory.class.getSimpleName()).m_("Phidgets Isolated Thermocouple Sensor (TMP1100)");
      aliasBundle.alias(BridgePhidgetSourceFactory.class.getSimpleName()).m_("Phidgets RTD Sensor (1046)");
      aliasBundle.alias(PhidgetTmp1200SourceFactory.class.getSimpleName()).m_("Phidgets RTD Sensor (TMP1200)");
      aliasBundle.alias(PressureSensorSourceFactory.class.getSimpleName()).m_("Phidgets Differential Air Pressure Sensor [Pa]");
      aliasBundle.alias(PressureSensorPercentSourceFactory.class.getSimpleName()).m_("Phidgets Differential Air Pressure Sensor [%]");
      aliasBundle.alias(Phidget1132SourceFactory.class.getSimpleName()).m_("Phidgets 4-20 mA Adapter");
      aliasBundle.alias(YoctopuceSourceFactory.class.getSimpleName()).m_("Yocto-Thermocouple");
      aliasBundle.alias(GenericModbusTcpSourceFactory.class.getSimpleName()).m_("Generic Modbus over Network (TCP)");
      aliasBundle.alias(GenericModbusUdpSourceFactory.class.getSimpleName()).m_("Generic Modbus over Network (UDP)");
      aliasBundle.alias(GenericModbusRtuSourceFactory.class.getSimpleName()).m_("Generic Modbus over serial port");
      aliasBundle.alias(GenericSiemensS7SourceFactory.class.getSimpleName()).m_("Generic Siemens S7 over Network");
      aliasBundle.alias(AtillaSourceFactory.class.getSimpleName()).m_("Atilla Gold Plus PLC");
      aliasBundle.alias(BellaTWSourceFactory.class.getSimpleName()).m_("BellaTW PIDs");
      aliasBundle.alias(BertoSourceFactory.class.getSimpleName()).m_("Berto PIDs");
      aliasBundle.alias(BescaAutoSourceFactory.class.getSimpleName()).m_("Besca Auto PLC");
      aliasBundle.alias(BescaManualSourceFactory.class.getSimpleName()).m_("Besca Manual PIDs");
      aliasBundle.alias(BuhlerPlcSourceFactory.class.getSimpleName()).m_("Bühler PLC");
      aliasBundle.alias(CaparaoSourceFactory.class.getSimpleName()).m_("Caparaó PLC");
      aliasBundle.alias(CarmomaqNetworkSourceFactory.class.getSimpleName()).m_("Carmomaq PLC (over Network)");
      aliasBundle.alias(CoffedNetworkSourceFactory.class.getSimpleName()).m_("Coffed Auto PLC");
      aliasBundle.alias(CoffedSerialSourceFactory.class.getSimpleName()).m_("Coffed Manual PIDs");
      aliasBundle.alias(CoffeeTechNetworkSourceFactory.class.getSimpleName()).m_("Coffee-Tech PLC");
      aliasBundle.alias(CoffeeTechSourceFactory.class.getSimpleName()).m_("Coffee-Tech PIDs");
      aliasBundle.alias(CoffeetoolPlcSourceFactory.class.getSimpleName()).m_("Coffeetool PLC");
      aliasBundle.alias(CogenPlcSourceFactory.class.getSimpleName()).m_("Cogen PLC (V1)");
      aliasBundle.alias(CogenPlcV2SourceFactory.class.getSimpleName()).m_("Cogen PLC (V2)");
      aliasBundle.alias(ColorTrackLaserSourceFactory.class.getSimpleName()).m_("Colortrack Real-time Laser");
      aliasBundle.alias(DiedrichAutoSourceFactory.class.getSimpleName()).m_("Diedrich PLC");
      aliasBundle.alias(DongyiSourceFactory.class.getSimpleName()).m_("Dongyi / Yoshan PIDs");
      aliasBundle.alias(DiedrichGasAirflowSourceFactory.class.getSimpleName()).m_("Diedrich Gas and Airflow (Phidgets)");
      aliasBundle.alias(DutchMasterSourceFactory.class.getSimpleName()).m_("Dutch Master Roaster PLC");
      aliasBundle.alias(FabricaRoastersSourceFactory.class.getSimpleName()).m_("Fabrica Roasters PIDs");
      aliasBundle.alias(GarantiSourceFactory.class.getSimpleName()).m_("Garanti PIDs");
      aliasBundle.alias(GiesenPlcSourceFactory.class.getSimpleName()).m_("Giesen PLC");
      aliasBundle.alias(ImfPlcSourceFactory.class.getSimpleName()).m_("IMF PLC");
      aliasBundle.alias(ImfPlcV2SourceFactory.class.getSimpleName()).m_("IMF PLC (with controls)");
      aliasBundle.alias(IpXenakisPlcSourceFactory.class.getSimpleName()).m_("IP-CC PLC");
      aliasBundle.alias(JoperNetworkSourceFactory.class.getSimpleName()).m_("Joper PLC");
      aliasBundle.alias(KapokSourceFactory.class.getSimpleName()).m_("Kapok PIDs");
      aliasBundle.alias(KirschUndMausserPlcSourceFactory.class.getSimpleName()).m_("Kirsch+Mausser PLC");
      aliasBundle.alias(KraffePlcSourceFactory.class.getSimpleName()).m_("Kraffe PLC");
      aliasBundle.alias(LillaPlcSourceFactory.class.getSimpleName()).m_("Lilla PLC");
      aliasBundle.alias(LoringPlcSourceFactory.class.getSimpleName()).m_("Loring PLC");
      aliasBundle.alias(MillCitySourceFactory.class.getSimpleName()).m_("Mill City (all versions - Phidgets and PIDs/PLC)");
      aliasBundle.alias(NordicCoffeeSourceFactory.class.getSimpleName()).m_("Nordic Coffee PIDs/PLC");
      aliasBundle.alias(PetronciniSourceFactory.class.getSimpleName()).m_("Petroncini PLC");
      aliasBundle.alias(PrimoSourceFactory.class.getSimpleName()).m_("Primo PID");
      aliasBundle.alias(AutonicsTk4sSourceFactory.class.getSimpleName()).m_("Proaster PIDs (Autonics TK4S)");
      aliasBundle.alias(ProbatPlcSourceFactory.class.getSimpleName()).m_("Probat PLC");
      aliasBundle.alias(ProbatSampleRoasterSourceFactory.class.getSimpleName()).m_("Probat WebSocket (P III Series)");
      aliasBundle.alias(RceSourceFactory.class.getSimpleName()).m_("RCE Automatyka PLC");
      aliasBundle.alias(WatlowEzZoneSourceFactory.class.getSimpleName()).m_("San Franciscan PID");
      aliasBundle.alias(SantokerSourceFactory.class.getSimpleName()).m_("Santoker (over Bluetooth)");
      aliasBundle.alias(SchuilenburgPlcSourceFactory.class.getSimpleName()).m_("Schuilenburg PLC");
      aliasBundle.alias(SivetzPlcSourceFactory.class.getSimpleName()).m_("Sivetz PLC");
      aliasBundle.alias(ToperSourceFactory.class.getSimpleName()).m_("Toper PLC");
      aliasBundle.alias(TyphoonSourceFactory.class.getSimpleName()).m_("Typhoon PLC");
      aliasBundle.alias(VortecsProSourceFactory.class.getSimpleName()).m_("Vortecs Pro PIDs");
      aliasBundle.alias(YangChiaSourceFactory.class.getSimpleName()).m_("Yang-Chia PIDs");
      aliasBundle.alias("roaster").m_("Machine");
      aliasBundle.alias("webservice").m_("Web service URL");
      aliasBundle.alias("host").m_("Hostname/IP address");
      aliasBundle.alias("hostOverride").m_("Hostname/IP address override");
      aliasBundle.alias("port").m_("Port");
      aliasBundle.alias("slot").m_("Slot");
      aliasBundle.alias("bigEndian").m_("Big-Endian word order (32-bit)");
      aliasBundle.alias("serialPort").m_("Serial port");
      aliasBundle.alias("millCityVersion").m_("Version");
      aliasBundle.alias("encoding").m_("Encoding");
      aliasBundle.alias("baudRate").m_("Baud rate");
      aliasBundle.alias("dataBits").m_("Data bits");
      aliasBundle.alias("parity").m_("Parity");
      aliasBundle.alias("stopBits").m_("Stop bits");
      aliasBundle.alias("modbusFunction").m_("Modbus function");
      aliasBundle.alias("temperatureUnit").m_("Temperature unit");
      aliasBundle.alias("database").m_("Database");
      aliasBundle.alias("legacyPlc").m_("PLC with legacy software version (no floating point support)");
      aliasBundle.alias("storageFormat").m_("Storage format");
      aliasBundle.alias("storageFormatTemperature").m_("Storage format temperatures");
      aliasBundle.alias("storageFormatGas").m_("Storage format gas");
      aliasBundle.alias("beanTemperatureRegister").m_("Register bean temp.");
      aliasBundle.alias("beanTemperatureSlave").m_("Slave Id bean temp.");
      aliasBundle.alias("exhaustTemperatureRegister").m_("Register exhaust temp.");
      aliasBundle.alias("exhaustTemperatureSlave").m_("Slave Id exhaust temp.");
      aliasBundle.alias("burnerTemperatureRegister").m_("Register burner temp.");
      aliasBundle.alias("gasRegister").m_("Register gas");
      aliasBundle.alias("diedrichGasPressure").m_("Gas pressure");
      aliasBundle.alias("lowerGasLimit").m_("Lower gas limit");
      aliasBundle.alias("upperGasLimit").m_("Upper gas limit");
      aliasBundle.alias("lowerFanSpeedLimit").m_("Lower fan speed limit");
      aliasBundle.alias("upperFanSpeedLimit").m_("Upper fan speed limit");
      aliasBundle.alias("lowerDrumSpeedLimit").m_("Lower drum speed limit");
      aliasBundle.alias("upperDrumSpeedLimit").m_("Upper drum speed limit");
      aliasBundle.alias("drumSpeedUnit").m_("Drum speed unit");
      aliasBundle.alias("version").m_("Version");
      aliasBundle.alias("minGas").m_("Minimum gas");
      aliasBundle.alias("maxGas").m_("Maximum gas");
      aliasBundle.alias("minAirflow").m_("Minimum airflow");
      aliasBundle.alias("maxAirflow").m_("Maximum airflow");
      aliasBundle.alias("minDrumSpeed").m_("Minimum drum speed");
      aliasBundle.alias("maxDrumSpeed").m_("Maximum drum speed");
      aliasBundle.alias("inletTemperatureSlave").m_("Slave Id inlet temp.");
      aliasBundle.alias("decimalCorrection").m_("Factor");
      aliasBundle.alias("phidgetHub").m_("Phidget hub");
      aliasBundle.alias("minPressure").m_("Pressure at 0% [Pa]");
      aliasBundle.alias("maxPressure").m_("Pressure at 100% [Pa]");
      aliasBundle.alias("minCurrent").m_("Current at 0% [mA]");
      aliasBundle.alias("maxCurrent").m_("Current at 100% [mA]");
      aliasBundle.alias("rtdWireSetup").m_("Wiring mode");
      aliasBundle.alias("hubPort").m_("Hub port");
      aliasBundle.alias("samplingInterval").m_("Sampling interval (seconds)");
      aliasBundle.alias(KeyboardShortcut.ADD).m_("Increase gas control by 1%");
      aliasBundle.alias(KeyboardShortcut.ARROW_DOWN).m_("Decrease gas control by 1%");
      aliasBundle.alias(KeyboardShortcut.ARROW_UP).m_("Increase gas control by 1%");
      aliasBundle.alias(KeyboardShortcut.ESC).m_("Dismiss alerts and close dialogs and popup windows");
      aliasBundle.alias(KeyboardShortcut.F1).m_("Open the Cropster Helpdesk in the browser");
      aliasBundle.alias(KeyboardShortcut.F11).m_("Toggle full-screen mode");
      aliasBundle.alias(KeyboardShortcut.SUBTRACT).m_("Decrease gas control by 1%");
      aliasBundle.alias(KeyboardShortcut.SHIFT_ENTER).m_("Stop roasting (twice with stop confirmation enabled)");
      aliasBundle.alias(KeyboardShortcut.CTRL_ARROW_LEFT).m_("Toggle slider controls (left)");
      aliasBundle.alias(KeyboardShortcut.CTRL_ARROW_RIGHT).m_("Toggle slider controls (right)");
      aliasBundle.alias(KeyboardShortcut.CTRL_COMMA).m_("Open the application preferences");
      aliasBundle.alias(KeyboardShortcut.CTRL_E).m_("Export the log files to share with the Cropster support team");
      aliasBundle.alias(KeyboardShortcut.CTRL_ENTER).m_("Start roasting (twice to force start)");
      aliasBundle.alias(KeyboardShortcut.CTRL_ESC).m_("Abort roasting and discard the current roast");
      aliasBundle.alias(KeyboardShortcut.CTRL_F11).m_("Toggle full-screen mode");
      aliasBundle.alias(KeyboardShortcut.CTRL_P).m_("Print selected roast label");
      aliasBundle.alias(KeyboardShortcut.CTRL_R).m_("Reset chart axes to the preferences defaults");
      aliasBundle.alias(KeyboardShortcut.CTRL_S).m_("Synchronize local data with the Cropster web platform");
      aliasBundle.alias(KeyboardShortcut.CTRL_WHEEL_CHART).m_("Zoom in/out roast chart");
      aliasBundle.alias(KeyboardShortcut.CTRL_WHEEL_SLIDER).m_("Increase/decrease slider values");
      aliasBundle.alias(KeyboardShortcut.CTRL_1).m_("Add first crack comment");
      aliasBundle.alias(KeyboardShortcut.CTRL_2).m_("Add second crack comment");
      aliasBundle.alias(KeyboardShortcut.CTRL_3).m_("Add color change comment");
      aliasBundle.alias(KeyboardShortcut.CTRL_4).m_("Add gas comment");
      aliasBundle.alias(KeyboardShortcut.CTRL_5).m_("Add airflow comment");
      aliasBundle.alias(KeyboardShortcut.SHIFT_CTRL_A).m_("Toggle assistant");
      aliasBundle.alias(KeyboardShortcut.SHIFT_CTRL_ENTER).m_("Finalize and upload the current roast");
      aliasBundle.alias(KeyboardShortcut.SHIFT_CTRL_COMMA).m_("Open keyboard shortcuts");
      aliasBundle.alias(RoastLabel.RoastLabelStyle.DEFAULT).m_("Detailed (up to 12 slots)");
      aliasBundle.alias(RoastLabel.RoastLabelStyle.CARD).m_("Card (up to 5 slots)");
      aliasBundle.alias(RoastLabel.QrCodePosition.TOP).m_("At the top");
      aliasBundle.alias(RoastLabel.QrCodePosition.BOTTOM).m_("At the bottom");
      aliasBundle.alias(RoastLabel.QrCodePosition.HIDDEN).m_("No QR-Code");
      aliasBundle.alias("time").m_("Time");
      aliasBundle.alias("MachineNotConnected").m_("The machine is not connected!");
      aliasBundle.alias("NoMachineAccess").m_("To roast, first select a machine");
      aliasBundle.alias("AutoStartDescription")
         .m_(
            "Machine events differ between manufacturers and machine types. Wondering which events can trigger the auto-start and end of a roast for your machine?"
         );
      aliasBundle.alias("AutoStartCaption").m_("Machine event-based auto-start and end of roast");
      aliasBundle.alias("AutoStartReadyMessage").m_("Auto-start ready");
      aliasBundle.alias("AutoStartNotReadyMessage").m_("Auto-start not ready");
      aliasBundle.alias("AutoStartDescription", LoringPlcSourceFactory.class)
         .m_("The roast will start automatically when the hopper is opened and end automatically when the emission door is opened and the beans are released.");
      aliasBundle.alias("AutoStartCaption", LoringPlcSourceFactory.class).m_("Auto-start roast when hopper is opened and stop when emission door is opened.");
      aliasBundle.alias("AutoStartReadyMessage", LoringPlcSourceFactory.class).m_("Waiting for the hopper to be opened!");
      aliasBundle.alias("custom", ImfPlcV2SourceFactory.class).m_("Vortex");
      aliasBundle.alias("customControl", ImfPlcV2SourceFactory.class).m_("Vortex control");
      aliasBundle.alias(LiveGoalDisplayMode.MEET_THE_REFERENCE).m_("“Meet the reference” relative smart goal");
      aliasBundle.alias(LiveGoalDisplayMode.RANGE).m_("Absolute smart goal");
      aliasBundle.alias(LiveGoalDisplayMode.TARGET).m_("Reference parameter");
      aliasBundle.alias(CheckResult.CheckStatus.FAILED).m_("Failed");
      aliasBundle.alias(CheckResult.CheckStatus.INVALID).m_("Unavailable");
      aliasBundle.alias(CheckResult.CheckStatus.MISSING).m_("Monitoring");
      aliasBundle.alias(CheckResult.CheckStatus.PASSED).m_("Met");
      _aliasBundle = aliasBundle;
      AUTHORIZATION_FAILED_MESSAGE = m_("Error syncing data!")
         + " "
         + m_("Authorization failed! Please try to login again.")
         + "\n\n"
         + m_("Hint: Credentials are the same as for the Cropster web platform.");
   }
}
