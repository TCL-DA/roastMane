package com.cropster.roastingintelligence.ui.whatsnew.pages;

import com.cropster.roastingintelligence.ui.whatsnew.AbstractPage;

public class N02_ProfileRatios extends AbstractPage {
   public N02_ProfileRatios() {
      super(
         org.cropster.roastingintelligence.util.Messages.m_("Pre-blends: More flexibility on profile ratios"),
         org.cropster.roastingintelligence.util.Messages.m_(
            "While diligent workflows may require one to have all pre-blend profiles defined in C-SAR, with specific ratios set for each of the green inventory components, they also require some degree of flexibility.\n\nIn RI5, when using pre-blend profiles, the “Next roast” values will be pre-filled but users will be able to adjust them if necessary."
         )
      );
   }
}
