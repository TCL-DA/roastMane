package com.cropster.roastingintelligence.ui.whatsnew.pages;

import com.cropster.roastingintelligence.ui.whatsnew.AbstractPage;

public class N05_CurvesValues extends AbstractPage {
   public N05_CurvesValues() {
      super(
         org.cropster.roastingintelligence.util.Messages.m_("Discover curves' values with your hand's precision"),
         org.cropster.roastingintelligence.util.Messages.m_(
            "Say goodbye to the old RI4 “Measurement values” window, where your curve values were displayed every 30 seconds.\n\nIn RI5, the power is in your hands: get axis values of your chart by simply using mouse coordinates for insights on potential trajectories, while also discovering current curves values and deviations from those of your reference curve."
         )
      );
   }
}
