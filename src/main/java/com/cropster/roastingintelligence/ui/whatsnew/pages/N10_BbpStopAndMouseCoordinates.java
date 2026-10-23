package com.cropster.roastingintelligence.ui.whatsnew.pages;

import com.cropster.roastingintelligence.ui.whatsnew.AbstractPage;

public class N10_BbpStopAndMouseCoordinates extends AbstractPage {
   public N10_BbpStopAndMouseCoordinates() {
      super(
         org.cropster.roastingintelligence.util.Messages.m_("A gradually more robust Between Batch Protocol!"),
         org.cropster.roastingintelligence.util.Messages.m_(
            "Until now, the Between Batch Protocol recording would start once a batch was roasted. While you could record up to 15 minutes and restart if needed, it was not possible to stop the recording.\n\nWe have now changed that, allowing you (and your computer!) to take production breaks. Plus, we added curve values in real time to between batches as well."
         )
      );
   }
}
