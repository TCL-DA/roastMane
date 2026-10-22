package org.cropster.roastingintelligence.measurement.impl;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.remote.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Phidget22SourceFactory implements MeasurementSourceFactory {
   private static final String OS_LINUX = "Linux";
   private static final String OS_WINDOWS = "64-bit Windows";
   private static final String OS_WINDOWS_X86 = "32-bit Windows";
   private static final String OS_MAC_OS_X = "Mac OS X";
   private static final String URL_PHIDGET_LINUX = htmlEncode("https://www.phidgets.com/docs/OS_-_Linux");
   private static final String URL_PHIDGET_WINDOWS = htmlEncode("https://www.phidgets.com/downloads/phidget22/libraries/windows/Phidget22-x64.exe");
   private static final String URL_PHIDGET_WINDOWS_X86 = htmlEncode("https://www.phidgets.com/downloads/phidget22/libraries/windows/Phidget22-x86.exe");
   private static final String URL_PHIDGET_MAC = htmlEncode("https://www.phidgets.com/downloads/phidget22/libraries/macos/Phidget22.dmg");
   private static final String URL_PHIDGET_DRIVERS = htmlEncode("https://www.phidgets.com/docs/Operating_System_Support");
   private static final String URL_NOW_TO = htmlEncode("https://help.cropster.com/en/knowledge/how-to-install-the-phidget-driver");
   private static final String URL_MULTI_BARREL = "https://help.cropster.com/sample-multi-barrel-roasters/multi-barrel-lab-roasters-roasting-intelligence-ri-setup";
   private final Logger logger = LoggerFactory.getLogger(Phidget22SourceFactory.class);
   private static Boolean phidget22LibraryFound;
   private static PhidgetDeviceDetector _detector;

   private static String htmlEncode(String url) {
      return "<a href=\"" + url + "\">" + url + "</a>";
   }

   protected String createOsErrorMessage() {
      String os = org.cropster.roastingintelligence.util.Messages.m_("your operating system");
      String url = URL_PHIDGET_DRIVERS;
      String hints = "";
      String arch = System.getProperty("os.arch");
      if (SystemUtils.IS_OS_WINDOWS) {
         if (!arch.contains("64") && System.getenv("ProgramW6432") == null) {
            os = "32-bit Windows";
            url = URL_PHIDGET_WINDOWS_X86;
         } else {
            os = "64-bit Windows";
            url = URL_PHIDGET_WINDOWS;
         }
      } else if (SystemUtils.IS_OS_MAC_OSX) {
         os = "Mac OS X";
         url = URL_PHIDGET_MAC;
      } else if (SystemUtils.IS_OS_LINUX) {
         os = "Linux";
         url = URL_PHIDGET_LINUX;
         String hints1 = org.cropster.roastingintelligence.util.Messages.m_(
            "Roasting Intelligence requires the <i>{0}</i> and <i>{1}</i> packages to be installed.", "libphidget22", "libphidget22java"
         );
         String hints2 = org.cropster.roastingintelligence.util.Messages.m_(
            "<a href=\"{0}\">Multi-barrel setups</a> additionally require <i>{1}</i> and <i>{2}</i>.",
            "https://help.cropster.com/sample-multi-barrel-roasters/multi-barrel-lab-roasters-roasting-intelligence-ri-setup",
            "phidget22networkserver",
            "libphidget22extra"
         );
         hints = hints + "\n\n" + hints1 + "\n" + hints2;
      }

      hints = hints + "\n\n" + org.cropster.roastingintelligence.util.Messages.m_("Find help on how to install the driver here:") + "\n" + URL_NOW_TO;
      return org.cropster.roastingintelligence.util.Messages.m_("Download and install the temperature sensor driver for {0}:\n{1}", os, url) + hints;
   }

   String getPhidget22LibraryVersion() {
      Phidget22SourceFactory.Phidget22Library phidget22Library = (Phidget22SourceFactory.Phidget22Library)Native.loadLibrary(
         "phidget22", Phidget22SourceFactory.Phidget22Library.class
      );
      if (phidget22Library != null) {
         PointerByReference pointerByReference = new PointerByReference();
         phidget22Library.Phidget_getLibraryVersion(pointerByReference);
         Pointer pointer = pointerByReference.getValue();
         return pointer.getString(0L);
      } else {
         return null;
      }
   }

   @Override
   public Set<SourceParameter> getParameters() {
      return Collections.emptySet();
   }

   @Override
   public void setup(Set<SourceParameter> parameters) throws Exception {
      if (phidget22LibraryFound == null) {
         String libraryVersion = null;

         try {
            libraryVersion = this.getPhidget22LibraryVersion();
            phidget22LibraryFound = true;
            this.logger.info("Installed Phidget 22 driver version: {}", libraryVersion);
         } catch (Throwable var5) {
            phidget22LibraryFound = false;
            LinkageError linkageError = new LinkageError(
               org.cropster.roastingintelligence.util.Messages.m_("Error loading Phidget driver!")
                  + "\n"
                  + StringUtils.abbreviate(var5.getLocalizedMessage(), 150)
                  + "\n\n"
                  + this.createOsErrorMessage()
            );
            linkageError.initCause(var5);
            throw linkageError;
         }
      }
   }

   @Override
   public List<String> getSetupWarnings() {
      List<String> warnings = new ArrayList<>();

      try {
         if (phidget22LibraryFound != null && phidget22LibraryFound) {
            Version version = null;
            Pattern versionPattern = Pattern.compile("Phidget.* - Version (\\d.\\d(?:.\\d)?) - Built (\\w{3}\\s{1,2}\\d{1,2}) (\\d{4}) .*");
            Matcher matcher = versionPattern.matcher(this.getPhidget22LibraryVersion());
            if (matcher.matches()) {
               DateFormat dateFormatUs = DateFormat.getDateInstance(2, Locale.US);
               Date buildDate = dateFormatUs.parse(matcher.group(2) + ", " + matcher.group(3));
               DateFormat dateFormatVersion = new SimpleDateFormat("yyyyMMdd");
               String versionString = matcher.group(1) + "." + dateFormatVersion.format(buildDate);
               version = new Version(versionString);
            }

            Version recommendedVersion = new Version("1.7.20210706");
            if (version != null && version.compareTo(recommendedVersion) < 0) {
               warnings.add(
                  org.cropster.roastingintelligence.util.Messages.m_(
                     "The installed Phidgets driver ({0}) is older than the minimum recommended version ({1})!", version, recommendedVersion
                  )
               );
               warnings.add(this.createOsErrorMessage());
            }
         }
      } catch (Exception var9) {
         this.logger.warn("Error checking Phidgets driver version", var9);
      }

      return warnings;
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName();
   }

   public synchronized PhidgetDeviceDetector getDeviceDetector() {
      if (_detector == null) {
         _detector = new PhidgetDeviceDetector();
      }

      return _detector;
   }

   interface Phidget22Library extends Library {
      int Phidget_getLibraryVersion(PointerByReference var1);
   }
}
