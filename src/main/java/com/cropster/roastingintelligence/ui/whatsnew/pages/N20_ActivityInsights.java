package com.cropster.roastingintelligence.ui.whatsnew.pages;

import com.cropster.roastingintelligence.ui.whatsnew.AbstractPage;

public class N20_ActivityInsights extends AbstractPage {
   public N20_ActivityInsights() {
      super(
         org.cropster.roastingintelligence.util.Messages.m_("Activity insights about roasting data"),
         org.cropster.roastingintelligence.util.Messages.m_(
            "We’ve introduced a new section in the System sidebar called “Activity insights”, where you can track key metrics like roasted and aborted batches, as well as weight outliers since your last login. We’ll continue expanding these insights to provide greater value and clarity as to how the application is used."
         )
      );
   }
}
