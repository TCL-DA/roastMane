package com.cropster.roastingintelligence.ui.whatsnew.pages;

import com.cropster.roastingintelligence.ui.whatsnew.AbstractPage;

public class N11_CustomizableAlarms extends AbstractPage {
   public N11_CustomizableAlarms() {
      super(
         org.cropster.roastingintelligence.util.Messages.m_("Enhanced feature: Customizable alerts"),
         org.cropster.roastingintelligence.util.Messages.m_(
            "Old alerts - 30 seconds before first crack and end of profile - have been fully enhanced!\n\nNow create as many alerts as you'd like, defining:\n• What is monitored (bean temperature, time, or profile events).\n• Target value and direction to be approached from.\n• Applicable roast phase.\n• Whether the alert should be visual, audible, or both, as well as the preferred sound and color."
         )
      );
   }
}
