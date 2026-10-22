package org.cropster.roastingintelligence;

import com.cropster.roastingintelligence.ui.components.RoastLabel;
import com.cropster.util.model.Unit;
import java.awt.Color;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
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
import org.cropster.roastingintelligence.util.MathUtil;
import org.cropster.roastingintelligence.util.Smoothing;

public final class Defaults {
   public static final String DEFAULT_INSTANCE = "default";
   public static final String ENCODING = "UTF-8";
   public static final Charset CHARSET = Charset.forName("UTF-8");
   public static final String RI_PACKAGE = "org.cropster.roastingintelligence.";
   public static final String HOST = "c-sar.cropster.com";
   public static final String BASE_URL = "https://c-sar.cropster.com";
   public static final String OAUTH2_CALLBACK_URL = "http://127.0.0.1:31815";
   public static final String HOMEPAGE_URL = "https://www.cropster.com";
   public static final String HELP_URL = "https://help.cropster.com";
   public static final String DOWNLOAD_URL = "https://download.cropster.com";
   public static final String RELEASE_NOTES_URL = "https://www.cropster.com/product-updates/ri-release-notes/";
   public static final String SYSTEM_PROPERTY_CODEBASE = "com.cropster.roastingintelligence.codebase";
   public static final String SYSTEM_PROPERTY_BASEURL = "org.cropster.roastingintelligence.baseUrl";
   public static final String SYSTEM_PROPERTY_SENSOR = "org.cropster.roastingintelligence.sensorfactory";
   public static final Class<?>[] SOURCE_FACTORIES = new Class[]{
      DemoSourceFactory.class,
      DeviceSimulatorSourceFactory.class,
      TemperaturePhidgetSourceFactory.class,
      PhidgetTmp1101SourceFactory.class,
      PhidgetTmp1100SourceFactory.class,
      BridgePhidgetSourceFactory.class,
      PhidgetTmp1200SourceFactory.class,
      PressureSensorSourceFactory.class,
      PressureSensorPercentSourceFactory.class,
      Phidget1132SourceFactory.class,
      YoctopuceSourceFactory.class,
      GenericModbusTcpSourceFactory.class,
      GenericModbusUdpSourceFactory.class,
      GenericModbusRtuSourceFactory.class,
      GenericSiemensS7SourceFactory.class,
      AtillaSourceFactory.class,
      BellaTWSourceFactory.class,
      BertoSourceFactory.class,
      BescaAutoSourceFactory.class,
      BescaManualSourceFactory.class,
      BuhlerPlcSourceFactory.class,
      CaparaoSourceFactory.class,
      CarmomaqNetworkSourceFactory.class,
      CoffedNetworkSourceFactory.class,
      CoffedSerialSourceFactory.class,
      CoffeeTechNetworkSourceFactory.class,
      CoffeeTechSourceFactory.class,
      CoffeetoolPlcSourceFactory.class,
      CogenPlcSourceFactory.class,
      CogenPlcV2SourceFactory.class,
      ColorTrackLaserSourceFactory.class,
      DiedrichGasAirflowSourceFactory.class,
      DiedrichAutoSourceFactory.class,
      DongyiSourceFactory.class,
      DutchMasterSourceFactory.class,
      FabricaRoastersSourceFactory.class,
      GarantiSourceFactory.class,
      GiesenPlcSourceFactory.class,
      ImfPlcSourceFactory.class,
      ImfPlcV2SourceFactory.class,
      IpXenakisPlcSourceFactory.class,
      JoperNetworkSourceFactory.class,
      KapokSourceFactory.class,
      KirschUndMausserPlcSourceFactory.class,
      KraffePlcSourceFactory.class,
      LillaPlcSourceFactory.class,
      LoringPlcSourceFactory.class,
      MillCitySourceFactory.class,
      NordicCoffeeSourceFactory.class,
      PetronciniSourceFactory.class,
      PrimoSourceFactory.class,
      AutonicsTk4sSourceFactory.class,
      ProbatPlcSourceFactory.class,
      ProbatSampleRoasterSourceFactory.class,
      RceSourceFactory.class,
      WatlowEzZoneSourceFactory.class,
      SantokerSourceFactory.class,
      SchuilenburgPlcSourceFactory.class,
      SivetzPlcSourceFactory.class,
      ToperSourceFactory.class,
      TyphoonSourceFactory.class,
      VortecsProSourceFactory.class,
      YangChiaSourceFactory.class
   };
   public static final Integer[] SOURCE_FACTORY_GROUPS = new Integer[]{
      ArrayUtils.indexOf(SOURCE_FACTORIES, TemperaturePhidgetSourceFactory.class),
      ArrayUtils.indexOf(SOURCE_FACTORIES, GenericModbusTcpSourceFactory.class),
      ArrayUtils.indexOf(SOURCE_FACTORIES, AtillaSourceFactory.class)
   };
   public static final Set<Class<?>> HUB_PORT_SOURCE_FACTORIES = new HashSet<>(
      Arrays.asList(PhidgetTmp1200SourceFactory.class, PhidgetTmp1100SourceFactory.class)
   );
   public static final int CONNECTION_TIMEOUT = 9000;
   public static final long CONNECT_TIMEOUT = 20000L;
   public static final long SYNC_TIME = -1L;
   public static final long SCORE_THRESHOLD = 2592000000L;
   public static final long COUNT_THRESHOLD = 90L;
   public static final int SAMPLING_INTERVAL = 1000;
   public static final double FACTOR_LBS2KG = 0.4536;
   public static final double FACTOR_KG2LBS = 2.2046;
   public static final String FILE_EXTENSION = "json";
   public static final String FILE_NAME_PATTERN = "%1$tY%1$tm%1$td_%1$tH%1$tM%1$tS_%1$tL.json";
   public static final String FILE_NAME_PARSE_PATTERN = "yyyyMMdd_HHmmss";
   public static final String ENCODING_DELIMITER = "___";
   public static final String VALUE_DELIMITER = "\t";
   public static final String LOG_DIR_NAME = "log";
   public static final String CACHE_DIR_NAME = "cache";
   public static final String OUTBOX_DIR_NAME = "outbox";
   public static final String SENT_DIR = "sent";
   public static final long MIN_TIME = 0L;
   public static final long MIN_TIME_AUTO = -60L;
   public static final long MAX_TIME = 1020L;
   public static final long BETWEEN_BATCH_TIME = 240L;
   public static final long MAX_BETWEEN_BATCH_MILLIS = 900000L;
   public static final double MIN_TEMPERATURE = 45.0;
   public static final double MAX_TEMPERATURE = 275.0;
   public static final Integer[] RISE_RATES = new Integer[]{5, 10, 15, 30, 60};
   public static final int RISE_RATE = 60;
   public static final double MIN_RISE_RATE = 0.0;
   public static final double MAX_RISE_RATE_FACTOR = 0.94;
   public static final double MAX_RISE_RATE = MathUtil.roundHalfToEven(56.4, 0);
   public static final boolean REPLAY_ENABLED = true;
   public static final int GAS_CURVE_AS_COMMENTS_OFF = -1;
   public static final int GAS_CURVE_AS_COMMENTS_THRESHOLD = 25;
   public static final int RESET_GAS_THRESHOLD = -1;
   public static final boolean COMMENTING_BY_TEMP = true;
   public static final boolean EVENT_AUTO_MARKING = false;
   public static final boolean REFERENCE_COMMENT_HISTORY_HIDDEN = true;
   public static final boolean SOUND_ALARM_ON_EVENTS = false;
   public static final int ALARMS_SOUND_REPETITIONS = 1;
   public static final double ALARMS_SOUND_VOLUME = 0.7;
   public static final boolean ALARMS_TOAST_NOTIFICATIONS = true;
   public static final boolean SHOW_REFERENCE_VALUES = true;
   public static final boolean HIDE_LIVE_GOAL_CURRENT_VALUE = false;
   public static final boolean HIDE_GOAL_STATISTICS = false;
   public static final boolean STOP_ROASTING_CONFIRMATION_ENABLED = true;
   public static final boolean CURVE_PREDICTION_ENABLED = true;
   public static final boolean CONDENSED_CHANNEL_STATUS = false;
   public static final boolean FIRSTCRACK_PREDICTION_ENABLED = true;
   public static final boolean FLICK_PREDICTION_ENABLED = true;
   public static final boolean BETWEEN_BATCH_PROTOCOL_ENABLED = true;
   public static final boolean DEVELOPMENT_TIME_ENABLED = true;
   public static final boolean AUTO_DETECTION_ENABLED = true;
   public static final boolean AUTO_START_ENABLED = false;
   public static final AppModel.TimerType TIMER_TYPE = AppModel.TimerType.MODULATION;
   public static final int MAX_CACHED_ROASTS = 100;
   public static final int MAX_ROAST_COUNT = 20;
   public static final int PROBE_STATUS_READY = 0;
   public static final int PROBE_STATUS_DISCONNECTED = 1;
   public static final Smoothing ROR_SMOOTHING = Smoothing.STANDARD;
   public static final int SMA_WINDOW_SIZE = 4;
   public static final double PHIDGETS_TEMPERATURE_CHANGE_THRESHOLD = 0.1;
   public static final boolean SLIDER_CONTROL_BUTTONS_ON_TOP = true;
   private static final DecimalFormatSymbols FORMAT_SYMBOLS = DecimalFormatSymbols.getInstance();
   public static final String GROUPING_SEPARATOR = String.valueOf(FORMAT_SYMBOLS.getGroupingSeparator());
   public static final String DECIMAL_SEPARATOR = String.valueOf(FORMAT_SYMBOLS.getDecimalSeparator());
   public static final String NUMBER_PATTERN = String.format("\\d{1,3}(?:%s?\\d{3})*(?:%s\\d+)?", GROUPING_SEPARATOR, DECIMAL_SEPARATOR);
   public static final DecimalFormat DEC_FORMAT = new DecimalFormat("0.0");
   public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
   public static final String HTML_LINK_PATTERN1 = "<a href=\"%1$s\">%1$s</a>";
   public static final String HTML_LINK_PATTERN2 = "<a href=\"%s\">%s</a>";
   public static final String URL_CROPSTER = "https://c-sar.cropster.com";
   public static final String URL_JNLP_FILE = "/roastingintelligence.jnlp";
   public static final String URL_VERSION_FILE = "/version5.txt";
   private static final String URL_REDIRECT = "/processing/roastredirect";
   public static final String URL_PASSWORD_FORGOTTEN = "/registration/passwordforgotten";
   public static final String URL_GREEN = "/processing/roastredirect/green";
   public static final String URL_ROASTS = "/processing/roastredirect/roasts";
   public static final String URL_PROFILES = "/processing/roastredirect/profiles";
   public static final String URL_WORKERS = "/processing/roastredirect/workers";
   public static final String[] LINK_URLS = new String[]{
      "https://c-sar.cropster.com",
      "/processing/roastredirect/green",
      "/processing/roastredirect/roasts",
      "/processing/roastredirect/profiles",
      "/processing/roastredirect/workers"
   };
   public static final String[] LINK_LABELS = new String[]{
      org.cropster.roastingintelligence.util.Messages.m_("Dashboard"),
      org.cropster.roastingintelligence.util.Messages.m_("Green coffee"),
      org.cropster.roastingintelligence.util.Messages.m_("Roasts"),
      org.cropster.roastingintelligence.util.Messages.m_("Profiles"),
      org.cropster.roastingintelligence.util.Messages.m_("Team members")
   };
   public static final String[] LINK_ICONS = new String[]{"\uf0c9", "\uf0c9", "\uf0c9", "\uf201", "\uf0c0"};
   public static final String[] SHORTCUT_LABELS = new String[]{
      org.cropster.roastingintelligence.util.Messages.m_("Start roasting"),
      org.cropster.roastingintelligence.util.Messages.m_("Force start"),
      org.cropster.roastingintelligence.util.Messages.m_("Abort roasting"),
      org.cropster.roastingintelligence.util.Messages.m_("Stop roasting (twice to confirm)"),
      org.cropster.roastingintelligence.util.Messages.m_("Finish roasting"),
      org.cropster.roastingintelligence.util.Messages.m_("Reset axes"),
      org.cropster.roastingintelligence.util.Messages.m_("First crack"),
      org.cropster.roastingintelligence.util.Messages.m_("Second crack"),
      org.cropster.roastingintelligence.util.Messages.m_("Color change"),
      org.cropster.roastingintelligence.util.Messages.m_("Increase/decrease gas control (during roasting)"),
      org.cropster.roastingintelligence.util.Messages.m_("Increase/decrease gas control (always)")
   };
   public static final String[] SHORTCUT_KEYS = new String[]{
      "%s + S",
      "%s + F",
      "%s + X",
      "%s + S",
      "%s + F",
      "%s + R",
      "%s + 1",
      "%s + 2",
      "%s + 3",
      "↑↓",
      org.cropster.roastingintelligence.util.Messages.m_("+- (Numpad)")
   };
   public static final Color BLUE = new Color(51, 102, 204);
   public static final Color RED = new Color(220, 57, 18);
   public static final Color ORANGE = new Color(255, 153, 0);
   public static final Color GREEN = new Color(16, 150, 24);
   public static final Color PURPLE = new Color(153, 0, 153);
   public static final Color[] COLORS = new Color[]{
      BLUE, RED, ORANGE, GREEN, PURPLE, new Color(0, 153, 198), new Color(221, 68, 119), new Color(102, 170, 0), new Color(184, 46, 46), new Color(49, 99, 149)
   };
   public static final Color COLOR_BACKGROUND = new Color(238, 238, 238);
   public static final Color COLOR_COMMENTS = new Color(86, 86, 86);
   public static final Color COLOR_DRYING = new Color(173, 209, 143);
   public static final Color COLOR_MAILLARD = new Color(245, 224, 148);
   public static final Color COLOR_DEVELOPMENT = new Color(206, 173, 120);
   public static final boolean VISIBLE = true;
   public static final double OFFSET = 0.0;
   public static final long DATE_PRECISION = 3L;
   public static final String EVENT_FIRST_CRACK = "firstCrack";
   public static final String EVENT_SECOND_CRACK = "secondCrack";
   public static final String EVENT_TURNING_POINT = "turningPoint";
   public static final String EVENT_COLORCHANGE = "colorChange";
   public static final String EVENT_START = "start";
   public static final String EVENT_END = "end";
   public static final String[] EVENTS = new String[]{"colorChange", "firstCrack", "secondCrack", "turningPoint"};
   public static final String[] EVENTS_AUTO_MARK = new String[]{"colorChange", "firstCrack", "secondCrack"};
   public static final String PHASE_DRYING = "drying";
   public static final String PHASE_MAILLARD = "maillard";
   public static final String PHASE_DEVELOPMENT = "development";
   public static final String[] ENABLED_EVENTS = new String[]{"firstCrack", "secondCrack", "colorChange", "gas", "airflow", "TEXT_COMMENT"};
   public static final long SEARCH_INTERVAL_START = 30000L;
   public static final long SEARCH_INTERVAL_TURNINGPOINT = 30000L;
   public static final long SEARCH_INTERVAL_END = 5000L;
   public static final BigDecimal TEMPERATURE_DROP_START = new BigDecimal(-7);
   public static final BigDecimal TEMPERATURE_RISE_TURNINGPOINT = new BigDecimal(3);
   public static final BigDecimal TEMPERATURE_DROP_END = new BigDecimal(-3);
   public static final int CURVE_PREDICTION_REQUEST_INTERVAL = 1;
   public static final Unit ROAST_VALUE_UNIT = Unit.AGTRON_GOURMET;
   public static final Unit[] ROAST_VALUES = new Unit[]{
      Unit.AGTRON_GOURMET,
      Unit.AGTRON_COMMERCIAL,
      Unit.COLORTRACK,
      Unit.DIPPER,
      Unit.ESPRESSO_VISION,
      Unit.HUNTER_L,
      Unit.JAVALYTICS,
      Unit.KONICA_MINOLTA,
      Unit.LIGHTTELLS,
      Unit.NEUHAUS_NEOTEC,
      Unit.PROBAT_COLORETTE,
      Unit.ROAMI,
      Unit.ROASTRITE,
      Unit.TONINO
   };
   public static final Locale LOCALE_ARABIC = new Locale("ar");
   public static final Locale[] LOCALES = new Locale[]{
      Locale.ENGLISH,
      Locale.GERMAN,
      Locale.FRENCH,
      new Locale("es"),
      new Locale("pt"),
      Locale.ITALIAN,
      new Locale("cs"),
      new Locale("ru"),
      Locale.JAPANESE,
      Locale.KOREAN,
      Locale.SIMPLIFIED_CHINESE,
      Locale.TRADITIONAL_CHINESE
   };
   public static final String PARAM_FILTER = "filter";
   public static final Map<String, String> PRINT_LABEL_MAPPING;
   public static final Integer[] PRINT_FONT_SIZES = new Integer[]{4, 5, 6, 8, 10, 12, 16, 20, 24, 28, 36};
   public static final Integer PRINT_FONT_SIZE = 12;
   public static final RoastLabel.QrCodePosition PRINT_QR_CODE_POSITION = RoastLabel.QrCodePosition.TOP;
   public static final boolean PRINT_CAPTION_VISIBLE = true;
   public static final RoastLabel.DateFormatter PRINT_DATE_FORMATTER = RoastLabel.DateFormatter.ISO_LOCAL_DATE_TIME;

   public static URI getBaseUri() {
      String urlProperty = System.getProperty("org.cropster.roastingintelligence.baseUrl");

      try {
         return new URI(urlProperty);
      } catch (URISyntaxException var2) {
         throw new IllegalStateException(String.format("Error parsing URI from %s=%s", "org.cropster.roastingintelligence.baseUrl", urlProperty), var2);
      }
   }

   private Defaults() {
   }

   static {
      LinkedHashMap<String, String> labelMapping = new LinkedHashMap<>();
      labelMapping.put("A", RoastLabel.IDTAG_NAME.getId());
      labelMapping.put("B.1", RoastLabel.DATE.getId());
      labelMapping.put("B.2", RoastLabel.MACHINE.getId());
      labelMapping.put("B.3", RoastLabel.WORKER.getId());
      labelMapping.put("C", RoastLabel.PROFILE.getId());
      labelMapping.put("D.1", RoastLabel.DURATION.getId());
      labelMapping.put("D.2", RoastLabel.WEIGHTS.getId());
      labelMapping.put("D.3", RoastLabel.ROAST_VALUE_1.getId());
      labelMapping.put("E.1", RoastLabel.NOTES.getId());
      PRINT_LABEL_MAPPING = Collections.unmodifiableMap(labelMapping);
   }
}
