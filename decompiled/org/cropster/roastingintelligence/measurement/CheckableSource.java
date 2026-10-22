package org.cropster.roastingintelligence.measurement;

import java.util.Collection;
import org.cropster.roastingintelligence.model.Channel;

public interface CheckableSource {
   String checkVersion(Collection<Channel> var1);
}
