package org.girod.javafx.svgimage;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.animation.Animation;
import javafx.scene.paint.Paint;
import org.girod.javafx.svgimage.xml.parsers.ClippingFactory;
import org.girod.javafx.svgimage.xml.parsers.XMLNode;
import org.girod.javafx.svgimage.xml.specs.FilterSpec;
import org.girod.javafx.svgimage.xml.specs.GradientSpec;
import org.girod.javafx.svgimage.xml.specs.MarkerSpec;
import org.girod.javafx.svgimage.xml.specs.Styles;
import org.girod.javafx.svgimage.xml.specs.SymbolSpec;
import org.girod.javafx.svgimage.xml.specs.Viewport;

public class LoaderContext {
   public final SVGImage root;
   public final LoaderParameters params;
   public Viewport viewport = null;
   public Styles svgStyle = null;
   public final URL url;
   public final ClippingFactory clippingFactory = new ClippingFactory();
   public final Map<String, GradientSpec> gradientSpecs = new HashMap<>();
   public final Map<String, FilterSpec> filterSpecs = new HashMap<>();
   public final Map<String, Paint> gradients = new HashMap<>();
   public final Map<String, MarkerSpec> markers = new HashMap<>();
   private final Map<String, XMLNode> namedNodes = new HashMap<>();
   private final Map<String, SymbolSpec> symbols = new HashMap<>();
   List<Animation> animations = new ArrayList<>();
   public boolean effectsSupported = false;

   public LoaderContext(SVGImage root, LoaderParameters params, URL url) {
      this.root = root;
      this.params = params;
      this.url = url;
   }

   public void addNamedNode(String id, XMLNode xmlNode) {
      this.namedNodes.put(id, xmlNode);
   }

   public void addMarker(String id, MarkerSpec xmlNode) {
      this.markers.put(id, xmlNode);
   }

   public boolean hasMarkers() {
      return !this.markers.isEmpty();
   }

   public boolean hasMarker(String id) {
      return this.markers.containsKey(id);
   }

   public MarkerSpec getMarker(String id) {
      return this.markers.get(id);
   }

   public boolean hasReifiedNamedNode(String id) {
      return this.namedNodes.containsKey(id);
   }

   public boolean hasNamedNode(String id) {
      return this.namedNodes.containsKey(id) || this.symbols.containsKey(id);
   }

   public XMLNode getReifiedNamedNode(String id) {
      return this.namedNodes.get(id);
   }

   public XMLNode getNamedNode(String id) {
      return this.namedNodes.containsKey(id) ? this.namedNodes.get(id) : this.symbols.get(id).getXMLNode();
   }

   public void addSymbol(String id, SymbolSpec symbol) {
      this.symbols.put(id, symbol);
   }

   public boolean hasSymbol(String id) {
      return this.symbols.containsKey(id);
   }

   public SymbolSpec getSymbol(String id) {
      return this.symbols.get(id);
   }

   public void addTransition(Animation animation) {
      this.animations.add(animation);
   }

   public void addAnimations(List<Animation> theAnimations) {
      this.animations.addAll(theAnimations);
   }

   public void playAnimations() {
      if (!this.animations.isEmpty()) {
         for (Animation tr : this.animations) {
            tr.play();
         }
      }
   }
}
