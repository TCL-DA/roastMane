package org.apache.logging.log4j.core.async;

import com.lmax.disruptor.LifecycleAware;
import com.lmax.disruptor.SequenceReportingEventHandler;

@Deprecated
public class RingBufferLogEventHandler extends RingBufferLogEventHandler4 implements SequenceReportingEventHandler<RingBufferLogEvent>, LifecycleAware {
}
