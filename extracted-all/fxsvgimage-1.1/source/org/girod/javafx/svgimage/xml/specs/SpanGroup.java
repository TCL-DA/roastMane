package org.girod.javafx.svgimage.xml.specs;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.text.Text;
import org.girod.javafx.svgimage.xml.parsers.XMLNode;

public class SpanGroup {
   private Group textGroup;
   private List<SpanGroup.TSpan> tspans = new ArrayList<>();

   public SpanGroup(Group textGroup) {
      this.textGroup = textGroup;
   }

   public Group getTextGroup() {
      return this.textGroup;
   }

   public List<SpanGroup.TSpan> getSpans() {
      return this.tspans;
   }

   public void addTSpan(XMLNode node, Text text) {
      SpanGroup.TSpan tspan = new SpanGroup.TSpan(node, text);
      this.tspans.add(tspan);
   }

   public class TSpan {
      public final XMLNode node;
      public final Text text;

      private TSpan(XMLNode node, Text text) {
         this.node = node;
         this.text = text;
      }
   }
}
