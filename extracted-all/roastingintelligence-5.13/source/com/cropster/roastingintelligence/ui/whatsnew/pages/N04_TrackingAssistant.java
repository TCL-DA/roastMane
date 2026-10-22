package com.cropster.roastingintelligence.ui.whatsnew.pages;

import com.cropster.roastingintelligence.ui.whatsnew.AbstractPage;

public class N04_TrackingAssistant extends AbstractPage {
   public N04_TrackingAssistant() {
      super(
         org.cropster.roastingintelligence.util.Messages.m_("A new powerful AI tool: the Tracking Assistant"),
         org.cropster.roastingintelligence.util.Messages.m_(
            "We keep building smart tools to support you while roasting! How does the tracking assistant work? First, it analyzes a profile’s reference for up to 2 minutes and then makes the necessary gas and airflow changes to track the reference as close as possible.\n\nImportant: The tool doesn’t automate roasting and a roasters oversight is always required."
         )
      );
   }

   @Override
   public boolean disabled() {
      return true;
   }
}
