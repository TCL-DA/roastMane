package org.girod.javafx.svgimage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import javafx.animation.Animation;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.Effect;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.girod.javafx.svgimage.xml.builders.AnimationBuilder;
import org.girod.javafx.svgimage.xml.builders.MarkerBuilder;
import org.girod.javafx.svgimage.xml.builders.SVGShapeBuilder;
import org.girod.javafx.svgimage.xml.builders.SVGStyleBuilder;
import org.girod.javafx.svgimage.xml.parsers.ParserUtils;
import org.girod.javafx.svgimage.xml.parsers.SVGParsingException;
import org.girod.javafx.svgimage.xml.parsers.SVGTags;
import org.girod.javafx.svgimage.xml.parsers.TransformUtils;
import org.girod.javafx.svgimage.xml.parsers.XMLNode;
import org.girod.javafx.svgimage.xml.parsers.XMLRoot;
import org.girod.javafx.svgimage.xml.parsers.XMLTreeHandler;
import org.girod.javafx.svgimage.xml.specs.FilterSpec;
import org.girod.javafx.svgimage.xml.specs.GradientSpec;
import org.girod.javafx.svgimage.xml.specs.MarkerContext;
import org.girod.javafx.svgimage.xml.specs.MarkerSpec;
import org.girod.javafx.svgimage.xml.specs.SpanGroup;
import org.girod.javafx.svgimage.xml.specs.SymbolSpec;
import org.girod.javafx.svgimage.xml.specs.Viewbox;
import org.girod.javafx.svgimage.xml.specs.Viewport;
import org.xml.sax.SAXException;

public class SVGLoader implements SVGTags {
   private final SVGContent content;
   private final SVGImage root;
   private Viewport viewport = null;
   private final LoaderContext context;

   private SVGLoader(URL url, LoaderParameters params) {
      this.content = new SVGContent(url, params);
      this.root = new SVGImage(this.content);
      this.context = new LoaderContext(this.root, params, url);
   }

   private SVGLoader(String content, LoaderParameters params) {
      this.content = new SVGContent(content, params);
      this.root = new SVGImage(this.content);
      this.context = new LoaderContext(this.root, params, this.content.url);
   }

   public static SVGImage load(File file) throws SVGParsingException {
      try {
         URL url = file.toURI().toURL();
         return load(url);
      } catch (MalformedURLException var2) {
         throw new SVGParsingException(var2);
      }
   }

   public static SVGImage load(URL url) throws SVGParsingException {
      SVGLoader loader = new SVGLoader(url, new LoaderParameters());
      return loader.loadImpl();
   }

   public static SVGImage load(String content) throws SVGParsingException {
      SVGLoader loader = new SVGLoader(content, new LoaderParameters());
      return loader.loadImpl();
   }

   public static SVGImage load(File file, String styleSheets) throws SVGParsingException {
      try {
         URL url = file.toURI().toURL();
         return load(url, styleSheets);
      } catch (MalformedURLException var3) {
         throw new SVGParsingException(var3);
      }
   }

   public static SVGImage load(File file, LoaderParameters params) throws SVGParsingException {
      try {
         URL url = file.toURI().toURL();
         return load(url, params);
      } catch (MalformedURLException var3) {
         throw new SVGParsingException(var3);
      }
   }

   public static SVGImage load(URL url, String styleSheets) throws SVGParsingException {
      LoaderParameters params = new LoaderParameters();
      params.styleSheets = styleSheets;
      return load(url, params);
   }

   public static SVGImage load(String content, String styleSheets) throws SVGParsingException {
      LoaderParameters params = new LoaderParameters();
      params.styleSheets = styleSheets;
      return load(content, params);
   }

   public static SVGImage loadScaled(File file, double scale) throws SVGParsingException {
      try {
         URL url = file.toURI().toURL();
         return loadScaled(url, scale);
      } catch (MalformedURLException var4) {
         throw new SVGParsingException(var4);
      }
   }

   public static SVGImage loadScaled(URL url, double scale) throws SVGParsingException {
      LoaderParameters params = new LoaderParameters();
      params.scale = scale;
      return load(url, params);
   }

   public static SVGImage loadScaled(String content, double scale) throws SVGParsingException {
      LoaderParameters params = new LoaderParameters();
      params.scale = scale;
      return load(content, params);
   }

   public static SVGImage load(File file, double width) throws SVGParsingException {
      try {
         URL url = file.toURI().toURL();
         return load(url, width);
      } catch (MalformedURLException var4) {
         throw new SVGParsingException(var4);
      }
   }

   public static SVGImage load(URL url, double width) throws SVGParsingException {
      LoaderParameters params = new LoaderParameters();
      params.width = width;
      return load(url, params);
   }

   public static SVGImage load(String content, double width) throws SVGParsingException {
      LoaderParameters params = new LoaderParameters();
      params.width = width;
      return load(content, params);
   }

   public static SVGImage load(File file, double width, String styleSheets) throws SVGParsingException {
      try {
         URL url = file.toURI().toURL();
         return load(url, width, styleSheets);
      } catch (MalformedURLException var5) {
         throw new SVGParsingException(var5);
      }
   }

   public static SVGImage load(URL url, double width, String styleSheets) throws SVGParsingException {
      LoaderParameters params = new LoaderParameters();
      params.styleSheets = styleSheets;
      params.width = width;
      return load(url, params);
   }

   public static SVGImage load(String content, double width, String styleSheets) throws SVGParsingException {
      LoaderParameters params = new LoaderParameters();
      params.styleSheets = styleSheets;
      params.width = width;
      return load(content, params);
   }

   public static SVGImage loadScaled(File file, double scale, String styleSheets) throws SVGParsingException {
      try {
         URL url = file.toURI().toURL();
         return loadScaled(url, scale, styleSheets);
      } catch (MalformedURLException var5) {
         throw new SVGParsingException(var5);
      }
   }

   public static SVGImage loadScaled(URL url, double scale, String styleSheets) throws SVGParsingException {
      LoaderParameters params = new LoaderParameters();
      params.styleSheets = styleSheets;
      params.scale = scale;
      return load(url, params);
   }

   public static SVGImage loadScaled(String content, double scale, String styleSheets) throws SVGParsingException {
      LoaderParameters params = new LoaderParameters();
      params.styleSheets = styleSheets;
      params.scale = scale;
      return load(content, params);
   }

   public static SVGImage load(URL url, LoaderParameters params) throws SVGParsingException {
      SVGLoader loader = new SVGLoader(url, params);
      SVGImage img = loader.loadImpl();
      if (params.centerImage) {
         double theWidth = img.getLayoutBounds().getWidth();
         double theHeight = img.getLayoutBounds().getHeight();
         img.setTranslateX(-theWidth / 2.0);
         img.setTranslateY(-theHeight / 2.0);
      }

      if (params.styleSheets != null) {
         img.getStylesheets().add(params.styleSheets);
      }

      return img;
   }

   private void setViewportScaleImpl(Viewport viewport, LoaderParameters params) {
      if (params.scale > 0.0) {
         viewport.setScale(params.scale, params.scaleLineWidth);
      } else if (params.width > 0.0) {
         double initialWidth = viewport.getBestWidth();
         double scale = params.width / initialWidth;
         viewport.setScale(scale, params.scaleLineWidth);
      }
   }

   public static SVGImage load(String content, LoaderParameters params) throws SVGParsingException {
      SVGLoader loader = new SVGLoader(content, params);
      SVGImage img = loader.loadImpl();
      if (params.centerImage) {
         double theWidth = img.getLayoutBounds().getWidth();
         double theHeight = img.getLayoutBounds().getHeight();
         img.setTranslateX(-theWidth / 2.0);
         img.setTranslateY(-theHeight / 2.0);
      }

      if (params.styleSheets != null) {
         img.getStylesheets().add(params.styleSheets);
      }

      return img;
   }

   private SVGImage loadImpl() throws SVGParsingException {
      if (Platform.isFxApplicationThread()) {
         try {
            return this.loadImplInJFX();
         } catch (Exception var4) {
            GlobalConfig.getInstance().handleParsingException(var4);
            return null;
         }
      } else {
         new JFXPanel();
         FutureTask<SVGImage> future = new FutureTask<>(new Callable<SVGImage>() {
            public SVGImage call() throws Exception {
               return SVGLoader.this.loadImplInJFX();
            }
         });
         Platform.runLater(future);

         try {
            return future.get();
         } catch (InterruptedException var5) {
            return null;
         } catch (ExecutionException var6) {
            Throwable th = var6.getCause();
            GlobalConfig.getInstance().handleParsingException(th);
            return null;
         }
      }
   }

   private SVGImage loadImplInJFX() throws IOException {
      this.context.effectsSupported = Platform.isSupported(ConditionalFeature.EFFECT);
      SAXParserFactory saxfactory = SAXParserFactory.newInstance();

      try {
         saxfactory.setFeature("http://xml.org/sax/features/resolve-dtd-uris", false);
         saxfactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
         saxfactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
         SAXParser parser = saxfactory.newSAXParser();
         XMLTreeHandler handler = new XMLTreeHandler();
         if (this.content.url != null) {
            parser.parse(this.content.url.openStream(), handler);
         } else {
            InputStream stream = new ByteArrayInputStream(this.content.content.getBytes());
            parser.parse(stream, handler);
         }

         SVGImage img = this.walk(handler.getRoot());
         if (img != null) {
            if (!this.context.animations.isEmpty()) {
               img.setAnimations(this.context.animations);
               if (this.context.params.autoStartAnimations) {
                  this.context.playAnimations();
               }
            }

            if (this.context.params.applyViewportPosition) {
               Transform transform = Transform.translate(-this.viewport.getViewboxX(), -this.viewport.getViewboxY());
               img.getTransforms().add(transform);
            }
         }

         return img;
      } catch (SAXException | ParserConfigurationException var6) {
         GlobalConfig.getInstance().handleParsingException(var6);
         return null;
      }
   }

   private SVGImage walk(XMLRoot xmlRoot) {
      String name = xmlRoot.getName();
      if (name.equals("svg") && this.viewport == null) {
         this.viewport = ParserUtils.parseViewport(xmlRoot);
         this.setViewportScaleImpl(this.viewport, this.context.params);
         this.context.viewport = this.viewport;
         if (this.viewport != null) {
            this.viewport.scaleNode(this.root);
         }
      }

      this.buildNode(xmlRoot, this.root);
      return this.root;
   }

   private void buildNode(XMLNode xmlNode, Group group) {
      this.buildNode(xmlNode, group, false);
   }

   private void addMarker(XMLNode xmlNode) {
      if (xmlNode.hasAttribute("id")) {
         String id = xmlNode.getAttributeValue("id");
         MarkerSpec marker = new MarkerSpec(xmlNode);
         Viewbox viewbox = ParserUtils.parseMarkerViewbox(xmlNode, this.viewport);
         marker.computeRefPosition(this.viewport);
         marker.setViewbox(viewbox);
         this.context.addMarker(id, marker);
      }
   }

   private void addSymbol(XMLNode xmlNode) {
      if (xmlNode.hasAttribute("id")) {
         String id = xmlNode.getAttributeValue("id");
         SymbolSpec symbol = new SymbolSpec(xmlNode);
         Viewbox viewbox = ParserUtils.parseViewbox(xmlNode, this.viewport);
         symbol.setViewbox(viewbox);
         if (xmlNode.hasAttribute("preserveAspectRatio")) {
            boolean preserve = ParserUtils.getPreserveAspectRatio(xmlNode.getAttributeValue("preserveAspectRatio"));
            viewbox.setPreserveAspectRatio(preserve);
         }

         this.context.addSymbol(id, symbol);
      }
   }

   private void addNamedNode(XMLNode xmlNode, Node node) {
      if (node != null && xmlNode.hasAttribute("id")) {
         String id = xmlNode.getAttributeValue("id");
         this.context.addNamedNode(id, xmlNode);
      }
   }

   private List<XMLNode> lookForAnimations(XMLNode xmlNode, Node node, Viewport viewport) {
      if (node == null) {
         return new ArrayList<>();
      } else {
         List<XMLNode> animations = new ArrayList<>();

         for (XMLNode childNode : xmlNode.getChildren()) {
            String name = childNode.getName();
            switch (name) {
               case "animate":
               case "animateMotion":
               case "animateTransform":
               case "set":
                  animations.add(childNode);
            }
         }

         return animations;
      }
   }

   private void buildNode(XMLNode xmlNode, Group group, boolean acceptDefs) {
      if (group == null) {
         group = new Group();
      }

      List<XMLNode> animations = new ArrayList<>();

      for (XMLNode childNode : xmlNode.getChildren()) {
         List<? extends Node> nodes = null;
         SpanGroup spanGroup = null;
         String name = childNode.getName();
         switch (name) {
            case "style":
               this.manageSVGStyle(childNode);
               break;
            case "rect": {
               Node node = SVGShapeBuilder.buildRect(childNode, null, null, this.viewport);
               this.addNamedNode(childNode, node);
               animations = this.lookForAnimations(childNode, node, this.viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            }
            case "circle": {
               Node node = SVGShapeBuilder.buildCircle(childNode, null, null, this.viewport);
               this.addNamedNode(childNode, node);
               animations = this.lookForAnimations(childNode, node, this.viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            }
            case "ellipse": {
               Node node = SVGShapeBuilder.buildEllipse(childNode, null, null, this.viewport);
               this.addNamedNode(childNode, node);
               animations = this.lookForAnimations(childNode, node, this.viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            }
            case "path":
               boolean hasFill = SVGStyleBuilder.hasFill(childNode);
               nodes = SVGShapeBuilder.buildPath(childNode, null, null, this.viewport, hasFill);
               if (nodes != null) {
                  for (Node nodex : nodes) {
                     this.addNamedNode(childNode, nodex);
                     animations = this.lookForAnimations(childNode, nodex, this.viewport);
                  }
               }
               break;
            case "polygon": {
               Node node = SVGShapeBuilder.buildPolygon(childNode, null, null, this.viewport);
               this.addNamedNode(childNode, node);
               animations = this.lookForAnimations(childNode, node, this.viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            }
            case "line": {
               Node node = SVGShapeBuilder.buildLine(childNode, null, null, this.viewport);
               this.addNamedNode(childNode, node);
               animations = this.lookForAnimations(childNode, node, this.viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            }
            case "polyline": {
               Node node = SVGShapeBuilder.buildPolyline(childNode, null, null, this.viewport);
               this.addNamedNode(childNode, node);
               animations = this.lookForAnimations(childNode, node, this.viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            }
            case "use":
               nodes = SVGShapeBuilder.buildUse(childNode, this.context, null, this.viewport);
               break;
            case "text": {
               Node node = SVGShapeBuilder.buildText(childNode, null, null, this.viewport);
               if (node == null) {
                  spanGroup = SVGShapeBuilder.buildTSpanGroup(childNode, null, null, this.viewport);
                  this.addNamedNode(childNode, spanGroup.getTextGroup());
                  animations = this.lookForAnimations(childNode, spanGroup.getTextGroup(), this.viewport);
               } else {
                  this.addNamedNode(childNode, node);
                  animations = this.lookForAnimations(childNode, node, this.viewport);
               }

               nodes = ParserUtils.createNodeList(node);
               break;
            }
            case "image": {
               Node node = SVGShapeBuilder.buildImage(childNode, this.content.url, null, null, this.viewport);
               this.addNamedNode(childNode, node);
               animations = this.lookForAnimations(childNode, node, this.viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            }
            case "svg": {
               if (this.viewport == null) {
                  this.viewport = ParserUtils.parseViewport(childNode);
                  this.context.viewport = this.viewport;
               }

               Node node = this.buildGroup(childNode);
               nodes = ParserUtils.createNodeList(node);
               break;
            }
            case "g": {
               Node node = this.buildGroup(childNode);
               this.addNamedNode(childNode, node);
               animations = this.lookForAnimations(childNode, node, this.viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            }
            case "symbol":
               this.addSymbol(childNode);
               break;
            case "marker":
               this.addMarker(childNode);
               break;
            case "defs":
               if (!acceptDefs) {
                  this.buildDefs(childNode);
                  break;
               }
            case "clipPath":
               this.buildClipPath(childNode);
               break;
            case "linearGradient":
               if (acceptDefs) {
                  SVGShapeBuilder.buildLinearGradient(this.context.gradientSpecs, this.context.gradients, childNode, this.viewport);
                  break;
               }
            case "radialGradient":
               if (acceptDefs) {
                  SVGShapeBuilder.buildRadialGradient(this.context.gradientSpecs, this.context.gradients, childNode, this.viewport);
                  break;
               }
            case "filter":
               this.buildFilter(childNode);
         }

         if (nodes != null) {
            for (Node node : nodes) {
               group.getChildren().add(node);
               this.addStyles(group, node, childNode, false);
               if (!animations.isEmpty()) {
                  List<Animation> animationsList = AnimationBuilder.buildAnimations(childNode, node, animations, this.viewport);
                  if (animationsList != null) {
                     this.context.addAnimations(animationsList);
                  }
               }
            }
         } else if (spanGroup != null) {
            TransformUtils.setTransforms(spanGroup.getTextGroup(), childNode, this.viewport);
            Map<String, String> theStylesMap = ParserUtils.getStyles(childNode);
            Iterator<SpanGroup.TSpan> it2 = spanGroup.getSpans().iterator();
            SpanGroup.TSpan previous = null;

            while (it2.hasNext()) {
               SpanGroup.TSpan tspan = it2.next();
               Text tspanText = tspan.text;
               String theStyles = ParserUtils.mergeStyles(theStylesMap, tspan.node);
               tspan.node.addAttribute("style", theStyles);
               this.addStyles(group, tspanText, tspan.node, true);
               if (tspan.node.hasAttribute("baseline-shift")) {
                  String shiftValue = tspan.node.getAttributeValue("baseline-shift");
                  ParserUtils.setBaselineShift(tspanText, shiftValue);
               }

               if (!ParserUtils.hasXPosition(tspan.node) && previous != null) {
                  double width = previous.text.getLayoutBounds().getWidth();
                  tspanText.setLayoutX(width + previous.text.getLayoutX());
               }

               previous = tspan;
            }

            group.getChildren().add(spanGroup.getTextGroup());
         }
      }
   }

   private void addStyles(Group parent, Node node, XMLNode xmlNode, boolean isTextSpan) {
      MarkerContext markerContext = this.setNodeStyle(node, xmlNode);
      boolean visible = ParserUtils.setVisibility(node, xmlNode);
      ParserUtils.setOpacity(node, xmlNode);
      this.setFilter(node, xmlNode);
      if (!isTextSpan) {
         TransformUtils.setTransforms(node, xmlNode, this.viewport);
      }

      if (markerContext != null) {
         MarkerBuilder.buildMarkers(parent, node, xmlNode, markerContext, this.context, this.viewport, visible);
      }
   }

   private void manageSVGStyle(XMLNode xmlNode) {
      if (this.context.svgStyle == null) {
         String cdata = xmlNode.getCDATA();
         if (cdata != null) {
            this.context.svgStyle = SVGStyleBuilder.parseStyle(cdata, this.viewport);
         }
      }
   }

   private void buildDefs(XMLNode xmlNode) {
      this.buildNode(xmlNode, null, true);
      if (!this.context.gradientSpecs.isEmpty()) {
         Map<String, GradientSpec> specs = this.context.gradientSpecs;

         for (GradientSpec spec : specs.values()) {
            spec.resolve(specs, this.viewport);
         }

         for (Entry<String, GradientSpec> entry : specs.entrySet()) {
            GradientSpec spec = entry.getValue();
            this.context.gradients.put(entry.getKey(), spec.getPaint());
         }
      }
   }

   private Group buildGroup(XMLNode xmlNode) {
      Group group = new Group();
      this.buildNode(xmlNode, group);
      return group;
   }

   private void buildFilter(XMLNode xmlNode) {
      if (xmlNode.hasAttribute("id")) {
         String id = xmlNode.getAttributeValue("id");
         FilterSpec spec = new FilterSpec();
         this.context.filterSpecs.put(id, spec);
         this.buildFilterEffects(spec, xmlNode);
      }
   }

   private void buildFilterEffects(FilterSpec spec, XMLNode xmlNode) {
      for (XMLNode childNode : xmlNode.getChildren()) {
         String name = childNode.getName();
         switch (name) {
            case "feGaussianBlur":
               SVGShapeBuilder.buildFEGaussianBlur(spec, childNode);
               break;
            case "feDropShadow":
               SVGShapeBuilder.buildFEDropShadow(spec, childNode, this.viewport);
               break;
            case "feFlood":
               SVGShapeBuilder.buildFEFlood(spec, childNode, this.viewport);
               break;
            case "feImage":
               SVGShapeBuilder.buildFEImage(spec, this.content.url, childNode, this.viewport);
               break;
            case "feOffset":
               SVGShapeBuilder.buildFEOffset(spec, childNode, this.viewport);
               break;
            case "feComposite":
               SVGShapeBuilder.buildFEComposite(spec, childNode);
            case "feMerge":
               SVGShapeBuilder.buildFEMerge(spec, childNode);
               break;
            case "feSpecularLighting":
               SVGShapeBuilder.buildFESpecularLighting(spec, childNode, this.viewport);
               break;
            case "feDiffuseLighting":
               SVGShapeBuilder.buildFEDiffuseLighting(spec, childNode, this.viewport);
         }
      }
   }

   private void buildClipPath(XMLNode xmlNode) {
      if (xmlNode.hasAttribute("id")) {
         String id = xmlNode.getAttributeValue("id");
         this.context.clippingFactory.addClipSpec(id, xmlNode);
      }
   }

   private void setFilter(Node node, XMLNode xmlNode) {
      if (this.context.effectsSupported && xmlNode.hasAttribute("filter")) {
         Effect effect = this.expressFilter(node, xmlNode.getAttributeValue("filter"));
         if (effect != null) {
            node.setEffect(effect);
         }
      }
   }

   private Effect expressFilter(Node node, String value) {
      return ParserUtils.expressFilter(this.context.filterSpecs, node, value);
   }

   private MarkerContext setNodeStyle(Node node, XMLNode xmlNode) {
      return SVGStyleBuilder.setNodeStyle(node, xmlNode, this.context, this.viewport);
   }
}
