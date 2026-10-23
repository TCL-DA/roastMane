package com.cropster.roastingintelligence.model.livegoals;

import com.cropster.roastingintelligence.ui.components.MeasureEditor;
import java.util.Collection;
import java.util.Date;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.ControlState;
import org.cropster.roastingintelligence.model.CurveModel;
import org.cropster.roastingintelligence.model.MeasurementModel;
import org.cropster.roastingintelligence.model.Roast;

public class LiveGoalBetweenBatchModel implements MeasurementModel {
   private final MeasurementModel delegate;
   private final Roast roast = new Roast();
   private final ChangeListener<Number> startWeightListener;

   public LiveGoalBetweenBatchModel(MeasurementModel delegate, MeasureEditor startWeightEditor) {
      this.delegate = delegate;
      this.startWeightListener = (s, o, n) -> this.roast.setStartWeight(startWeightEditor.getMeasure());
      startWeightEditor.valueProperty().addListener(new WeakChangeListener(this.startWeightListener));
      this.roast.setStartWeight(startWeightEditor.getMeasure());
   }

   @Override
   public Channel getChannel(String channelName) {
      return this.delegate.getChannel(channelName);
   }

   @Override
   public Collection<Channel> getChannels() {
      return this.delegate.getChannels();
   }

   @Override
   public Collection<ControlState> getControlStates() {
      return this.delegate.getControlStates();
   }

   @Override
   public Roast getCurrentReference() {
      return this.delegate.getCurrentReference();
   }

   @Override
   public CurveModel getCurveModel(Channel channel) {
      return this.delegate.getCurveModel(channel);
   }

   @Override
   public Roast getRoast() {
      return this.roast;
   }

   @Override
   public CurveModel getRorModel(Channel channel) {
      return this.delegate.getRorModel(channel);
   }

   @Override
   public Date getStartDate() {
      return this.delegate.getStartDate();
   }

   @Override
   public Boolean getState(ControlState controlState) {
      return this.delegate.getState(controlState);
   }

   @Override
   public void put(Channel channel, Date date, Double value) {
      this.delegate.put(channel, date, value);
   }

   @Override
   public void putState(ControlState controlState, Boolean value) {
      this.delegate.putState(controlState, value);
   }
}
