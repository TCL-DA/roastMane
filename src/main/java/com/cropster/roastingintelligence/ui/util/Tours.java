package com.cropster.roastingintelligence.ui.util;

import com.cropster.roastingintelligence.Preferences;
import com.cropster.roastingintelligence.ui.components.PopOverTour;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import org.controlsfx.control.PopOver.ArrowLocation;

public final class Tours {
   public static PopOverTour onboardingTour(Preferences preferences) {
      PopOverTour tour = new PopOverTour("onboarding", 380);
      tour.addPage(
         "sidebars",
         ArrowLocation.LEFT_CENTER,
         org.cropster.roastingintelligence.util.Messages.m_("Menu sidebars and shortcuts"),
         org.cropster.roastingintelligence.util.Messages.m_(
            "On the upper section of the main menu (below the Cropster logo) you’ll find the RI sidebars: key elements to help you handle your production, task prompts, comments created while roasting a batch, and more. You can detach and minimize them as needed.\n\nOn the lower section, you’ll find quick access to preferences or to perform a sync at any time."
         )
      );
      tour.addDetachedPage(
         "pre-roast",
         org.cropster.roastingintelligence.util.Messages.m_("Two-columns pre-roast screen layout"),
         org.cropster.roastingintelligence.util.Messages.m_(
            "Adjust the split layout to your needs:\n\n• With Between Batch Protocol (BBP) enabled on the left side, you can control your machine's conditions while you prepare the next batch on the right.\n• Without BBP, prepare the next batch on the left and observe the profile reference information on the right."
         )
      );
      String nextBatchTitle = org.cropster.roastingintelligence.util.Messages.m_("Setting up the next roast (and its reference)");
      String nextBatchContent = org.cropster.roastingintelligence.util.Messages.m_(
         "Select the profile and batch size for the upcoming roast, as well as the green inventory lots involved. If the available weight of the green lots is less than the start weight for the batch, RI will inform you without interfering if you wish to go ahead.\n\nCustomize the information to be shown about the reference set for the selected profile."
      );
      if (preferences.isBetweenBatchProtocolEnabled()) {
         tour.addPage("next-batch", ArrowLocation.RIGHT_CENTER, nextBatchTitle, nextBatchContent);
      } else {
         tour.addDetachedPage("next-batch-split", nextBatchTitle, nextBatchContent);
      }

      tour.addPage(
         "readouts",
         ArrowLocation.RIGHT_CENTER,
         org.cropster.roastingintelligence.util.Messages.m_("Customizing readouts"),
         org.cropster.roastingintelligence.util.Messages.m_(
            "The measurement readouts are vital to control the conditions of your roast. Enable your measurement readouts, define their size and rearrange them to match your needs in the program's Preferences."
         )
      );
      tour.addPage(
         "playerbar",
         ArrowLocation.BOTTOM_CENTER,
         org.cropster.roastingintelligence.util.Messages.m_("Roast playerbar"),
         org.cropster.roastingintelligence.util.Messages.m_(
            "One of the essential parts of Roasting Intelligence:\n\n• Control your roast progress with the Start, Abort, Stop and Upload command buttons.\n• Turn any enabled Smart assistant On or Off.\n• Observe your roast KPIs like duration, development, between batch timer, and more."
         )
      );
      tour.addDetachedPage(
         org.cropster.roastingintelligence.util.Messages.m_("New keyboard shortcuts"),
         new String[]{
            org.cropster.roastingintelligence.util.Messages.m_(
               "We have made changes to RI keyboard shortcuts to ensure they are easier to discover, memorize and don't conflict with those of other programs, actions within RI, or potential new shortcuts added in the future.\n\nIf you are used to old shortcuts, we suggest reviewing the new list, which can be found under \"Help -> Keyboard shortcuts\"."
            )
         }
      );
      tour.addPage(
         "dividers",
         ArrowLocation.RIGHT_CENTER,
         org.cropster.roastingintelligence.util.Messages.m_("Adjust your interface using RI's dividers"),
         (Runnable)null,
         Tours::getSplitterDivider,
         org.cropster.roastingintelligence.util.Messages.m_(
            "Some sections of the interface can be resized. Drag dividers to resize the interface to your needs. If needed, double-click to reset. Adjust the space for your Next roast section, measurement readout values, and reference box during roasting. Discover the dividers by hovering over them as they change color, making them easy to locate."
         )
      );
      tour.addPage(
         "system-sidebar",
         ArrowLocation.LEFT_TOP,
         org.cropster.roastingintelligence.util.Messages.m_("The System sidebar"),
         org.cropster.roastingintelligence.util.Messages.m_(
            "At the top of the main menu, you’ll find the <b>Cropster button</b>. Clicking it gives you access to key system information, including the logged-in  user, last synchronization time, system version, and available updates."
         )
      );
      return tour;
   }

   private static Node getSplitterDivider(Node splitter) {
      if (splitter != null && splitter instanceof SplitPane splitPane) {
         if (splitPane.getItems().isEmpty()) {
            return splitter;
         } else {
            Node divider = splitter.lookup(".split-pane-divider");
            return divider != null ? divider : splitter;
         }
      } else {
         return splitter;
      }
   }

   private Tours() {
   }
}
