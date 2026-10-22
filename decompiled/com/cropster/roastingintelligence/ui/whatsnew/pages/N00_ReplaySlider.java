package com.cropster.roastingintelligence.ui.whatsnew.pages;

import com.cropster.roastingintelligence.ui.whatsnew.AbstractPage;

public class N00_ReplaySlider extends AbstractPage {
   public N00_ReplaySlider() {
      super(
         org.cropster.roastingintelligence.util.Messages.m_("A known feature is now more robust: Replay Sliders"),
         org.cropster.roastingintelligence.util.Messages.m_(
            "Previously, when replaying a reference via the slider controls, all changes replayed were based on temperature. This resulted in slider control increases not being made at the ideal time. We looked in depth at these cases and improved the feature, basing gas control increases on time and, as before, control decreases on temperature."
         )
      );
   }
}
