package org.girod.javafx.svgimage.xml.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.StrokeTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.WritableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import org.girod.javafx.svgimage.xml.parsers.ParserUtils;
import org.girod.javafx.svgimage.xml.parsers.PathParser;
import org.girod.javafx.svgimage.xml.parsers.SVGTags;
import org.girod.javafx.svgimage.xml.parsers.TransformUtils;
import org.girod.javafx.svgimage.xml.parsers.XMLNode;
import org.girod.javafx.svgimage.xml.specs.Viewport;

public class AnimationBuilder implements SVGTags {
   private static final short TYPE_TRANSLATE = 0;
   private static final short TYPE_SCALE = 1;
   private static final short TYPE_ROTATE = 2;
   private static final short TYPE_SKEW_X = 3;
   private static final short TYPE_SKEW_Y = 4;
   private static final short ANIMATE_DEFAULT = 0;
   private static final short ANIMATE_VISIBILITY = 1;
   private static final short ANIMATE_STROKE = 2;
   private static final short ANIMATE_FILL = 3;
   private static final Pattern DURATION_PAT = Pattern.compile("(\\d+)(\\w+)");

   private AnimationBuilder() {
   }

   public static List<Animation> buildAnimations(XMLNode xmlNode, Node node, List<XMLNode> xmlAnims, Viewport viewport) {
      ParallelTransition parallel = null;
      List<Animation> transitionsList = new ArrayList<>();
      if (xmlAnims.size() > 1) {
         parallel = new ParallelTransition(node);
         transitionsList.add(parallel);
      }

      for (XMLNode xmlAnim : xmlAnims) {
         String name = xmlAnim.getName();
         switch (name) {
            case "animate":
               Animation animate = buildAnimate(xmlNode, xmlAnim, node, parallel, viewport);
               if (animate != null) {
                  transitionsList.add(animate);
               }
               break;
            case "animateMotion":
               Animation motionTransition = buildAnimateMotion(xmlNode, xmlAnim, node, parallel, viewport);
               if (motionTransition != null) {
                  transitionsList.add(motionTransition);
               }
               break;
            case "animateTransform":
               Animation transition = buildAnimateTransform(xmlNode, xmlAnim, node, parallel, viewport);
               if (transition != null) {
                  transitionsList.add(transition);
               }
            case "set":
         }
      }

      return transitionsList;
   }

   private static Duration parseDuration(String attrValue) {
      if (attrValue.equals("indefinite")) {
         return Duration.INDEFINITE;
      } else {
         Matcher m = DURATION_PAT.matcher(attrValue);
         if (m.matches()) {
            int time = Integer.parseInt(m.group(1));
            String type = m.group(2);
            if (type.equals("s")) {
               return Duration.seconds((double)time);
            } else if (type.equals("ms")) {
               return Duration.millis((double)time);
            } else {
               return type.equals("min") ? Duration.minutes((double)time) : Duration.ZERO;
            }
         } else {
            return Duration.ZERO;
         }
      }
   }

   private static Animation buildAnimate(XMLNode xmlNode, XMLNode xmlAnim, Node node, ParallelTransition parallel, Viewport viewport) {
      if (!xmlAnim.hasAttribute("attributeName")) {
         return null;
      } else {
         WritableValue value;
         short animateType;
         value = null;
         animateType = 0;
         String attrName = xmlAnim.getAttributeValue("attributeName");
         String nodeName = xmlNode.getName();
         label358:
         switch (nodeName) {
            case "g":
               Group group = (Group)node;
               switch (attrName) {
                  case "x":
                     value = group.translateXProperty();
                     break label358;
                  case "y":
                     value = group.translateYProperty();
                     break label358;
                  case "opacity":
                     value = group.opacityProperty();
                     break label358;
                  case "visibility":
                     value = group.opacityProperty();
                     animateType = 1;
                  default:
                     break label358;
               }
            case "rect":
               Rectangle rect = (Rectangle)node;
               switch (attrName) {
                  case "x":
                     value = rect.xProperty();
                     break label358;
                  case "y":
                     value = rect.yProperty();
                     break label358;
                  case "width":
                     value = rect.widthProperty();
                     break label358;
                  case "height":
                     value = rect.heightProperty();
                     break label358;
                  case "opacity":
                     value = rect.opacityProperty();
                     break label358;
                  case "visibility":
                     value = rect.opacityProperty();
                     animateType = 1;
                     break label358;
                  case "fill":
                     value = rect.fillProperty();
                     animateType = 3;
                     break label358;
                  case "stroke":
                     value = rect.strokeProperty();
                     animateType = 2;
                  default:
                     break label358;
               }
            case "image":
               ImageView imgView = (ImageView)node;
               switch (attrName) {
                  case "x":
                     value = imgView.xProperty();
                     break label358;
                  case "y":
                     value = imgView.yProperty();
                     break label358;
                  case "width":
                     value = imgView.fitWidthProperty();
                     break label358;
                  case "height":
                     value = imgView.fitHeightProperty();
                     break label358;
                  case "opacity":
                     value = imgView.opacityProperty();
                     break label358;
                  case "visibility":
                     value = imgView.opacityProperty();
                     animateType = 1;
                  default:
                     break label358;
               }
            case "circle":
               Circle circle = (Circle)node;
               switch (attrName) {
                  case "cx":
                     value = circle.centerXProperty();
                     break label358;
                  case "cy":
                     value = circle.centerYProperty();
                     break label358;
                  case "r":
                     value = circle.radiusProperty();
                     break label358;
                  case "opacity":
                     value = circle.opacityProperty();
                     break label358;
                  case "visibility":
                     value = circle.opacityProperty();
                     animateType = 1;
                     break label358;
                  case "fill":
                     value = circle.fillProperty();
                     animateType = 3;
                     break label358;
                  case "stroke":
                     value = circle.strokeProperty();
                     animateType = 2;
                  default:
                     break label358;
               }
            case "ellipse":
               Ellipse ellipse = (Ellipse)node;
               switch (attrName) {
                  case "cx":
                     value = ellipse.centerXProperty();
                     break label358;
                  case "cy":
                     value = ellipse.centerYProperty();
                     break label358;
                  case "rx":
                     value = ellipse.radiusXProperty();
                     break label358;
                  case "ry":
                     value = ellipse.radiusYProperty();
                     break label358;
                  case "opacity":
                     value = ellipse.opacityProperty();
                     break label358;
                  case "visibility":
                     value = ellipse.opacityProperty();
                     animateType = 1;
                     break label358;
                  case "fill":
                     value = ellipse.fillProperty();
                     animateType = 3;
                     break label358;
                  case "stroke":
                     value = ellipse.strokeProperty();
                     animateType = 2;
                  default:
                     break label358;
               }
            case "line":
               Line line = (Line)node;
               switch (attrName) {
                  case "x1":
                     value = line.startXProperty();
                     break label358;
                  case "y1":
                     value = line.startYProperty();
                     break label358;
                  case "x2":
                     value = line.endXProperty();
                     break label358;
                  case "y2":
                     value = line.endYProperty();
                     break label358;
                  case "opacity":
                     value = line.opacityProperty();
                     break label358;
                  case "visibility":
                     value = line.opacityProperty();
                     animateType = 1;
                     break label358;
                  case "stroke":
                     value = line.strokeProperty();
                     animateType = 2;
                  default:
                     break label358;
               }
            case "polygon":
               Polygon polygon = (Polygon)node;
               switch (attrName) {
                  case "opacity":
                     value = polygon.opacityProperty();
                     break label358;
                  case "visibility":
                     value = polygon.opacityProperty();
                     animateType = 1;
                     break label358;
                  case "fill":
                     value = polygon.fillProperty();
                     animateType = 3;
                     break label358;
                  case "stroke":
                     value = polygon.strokeProperty();
                     animateType = 2;
                  default:
                     break label358;
               }
            case "polyline":
               Polyline polyline = (Polyline)node;
               switch (attrName) {
                  case "opacity":
                     value = polyline.opacityProperty();
                     break;
                  case "visibility":
                     value = polyline.opacityProperty();
                     animateType = 1;
                     break;
                  case "stroke":
                     value = polyline.strokeProperty();
                     animateType = 2;
               }
            case "path":
               SVGPath thePath = (SVGPath)node;
               switch (attrName) {
                  case "opacity":
                     value = thePath.opacityProperty();
                     break;
                  case "visibility":
                     value = thePath.opacityProperty();
                     animateType = 1;
                     break;
                  case "stroke":
                     value = thePath.strokeProperty();
                     animateType = 2;
               }
         }

         List<Double> fromArgs = null;
         List<Double> toArgs = null;
         if (animateType == 0) {
            fromArgs = getFromArguments(xmlAnim, viewport);
            toArgs = getToArguments(xmlAnim, viewport);
         }

         if (animateType != 0 || fromArgs != null && toArgs != null && !fromArgs.isEmpty() && !toArgs.isEmpty()) {
            if (value == null) {
               return null;
            } else {
               Duration beginDur = Duration.ZERO;
               if (xmlAnim.hasAttribute("begin")) {
                  String begin = xmlAnim.getAttributeValue("begin");
                  beginDur = parseDuration(begin);
               }

               Duration duration = Duration.ZERO;
               if (xmlAnim.hasAttribute("dur")) {
                  duration = parseDuration(xmlAnim.getAttributeValue("dur"));
               }

               Animation animation;
               if (animateType == 1) {
                  boolean fromVisible = getFromVisibilityArgument(xmlAnim);
                  boolean toVisible = getToVisibilityArgument(xmlAnim);
                  KeyValue fromValue = new KeyValue(value, fromVisible ? 1 : 0, Interpolator.DISCRETE);
                  KeyValue toValue = new KeyValue(value, toVisible ? 1 : 0, Interpolator.DISCRETE);
                  KeyFrame fromFrame = new KeyFrame(beginDur, new KeyValue[]{fromValue});
                  KeyFrame toFrame = new KeyFrame(duration, new KeyValue[]{toValue});
                  Timeline timeline = new Timeline(new KeyFrame[]{fromFrame, toFrame});
                  animation = timeline;
               } else if (animateType == 2) {
                  Color fromColor = getFromColorArgument(xmlAnim);
                  Color toColor = getToColorArgument(xmlAnim);
                  if (fromColor == null || toColor == null) {
                     return null;
                  }

                  StrokeTransition transition;
                  if (parallel != null) {
                     transition = new StrokeTransition(duration);
                  } else {
                     transition = new StrokeTransition(duration, (Shape)node);
                  }

                  transition.setFromValue(fromColor);
                  transition.setToValue(toColor);
                  animation = transition;
               } else if (animateType == 3) {
                  Color fromColorx = getFromColorArgument(xmlAnim);
                  Color toColorx = getToColorArgument(xmlAnim);
                  if (fromColorx == null || toColorx == null) {
                     return null;
                  }

                  FillTransition transition;
                  if (parallel != null) {
                     transition = new FillTransition(duration);
                  } else {
                     transition = new FillTransition(duration, (Shape)node);
                  }

                  transition.setFromValue(fromColorx);
                  transition.setToValue(toColorx);
                  animation = transition;
               } else {
                  Timeline timeline = new Timeline();
                  animation = timeline;
                  KeyValue fromValue = new KeyValue(value, fromArgs.get(0));
                  KeyValue toValue = new KeyValue(value, toArgs.get(0));
                  KeyFrame fromFrame = new KeyFrame(beginDur, new KeyValue[]{fromValue});
                  KeyFrame toFrame = new KeyFrame(duration, new KeyValue[]{toValue});
                  timeline.getKeyFrames().addAll(new KeyFrame[]{fromFrame, toFrame});
               }

               if (xmlAnim.hasAttribute("repeatCount")) {
                  String repeatValue = xmlAnim.getAttributeValue("repeatCount");
                  if (repeatValue.equals("indefinite")) {
                     animation.setCycleCount(-1);
                  } else {
                     int count = ParserUtils.parseIntProtected(repeatValue);
                     animation.setCycleCount(count);
                  }
               }

               if (parallel != null) {
                  parallel.getChildren().add(animation);
                  return null;
               } else {
                  return animation;
               }
            }
         } else {
            return null;
         }
      }
   }

   private static Transition buildAnimateMotion(XMLNode xmlNode, XMLNode xmlAnim, Node node, ParallelTransition parallel, Viewport viewport) {
      if (!xmlAnim.hasAttribute("path")) {
         return null;
      } else {
         String content = xmlAnim.getAttributeValue("path");
         content = content.replace('−', '-');
         PathParser pathParser = new PathParser();
         List<SVGPath> list = pathParser.parsePathContent(content, viewport, false);
         Duration duration = Duration.ZERO;
         if (xmlAnim.hasAttribute("dur")) {
            duration = parseDuration(xmlAnim.getAttributeValue("dur"));
         }

         SVGPath path = list.get(0);
         PathTransition transition;
         if (parallel != null) {
            transition = new PathTransition(duration, path);
         } else {
            transition = new PathTransition(duration, path, node);
         }

         transition.setDuration(duration);
         if (xmlAnim.hasAttribute("repeatCount")) {
            String repeatValue = xmlAnim.getAttributeValue("repeatCount");
            if (repeatValue.equals("indefinite")) {
               transition.setCycleCount(-1);
            } else {
               int count = ParserUtils.parseIntProtected(repeatValue);
               transition.setCycleCount(count);
            }
         }

         if (xmlAnim.hasAttribute("begin")) {
            String begin = xmlAnim.getAttributeValue("begin");
            Duration beginDur = parseDuration(begin);
            if (beginDur != null) {
               transition.setDelay(beginDur);
            }
         }

         return transition;
      }
   }

   private static Transition buildAnimateTransform(XMLNode xmlNode, XMLNode xmlAnim, Node node, ParallelTransition parallel, Viewport viewport) {
      List<Double> fromArgs = getFromArguments(xmlAnim, viewport);
      List<Double> toArgs = getToArguments(xmlAnim, viewport);
      if (fromArgs != null && toArgs != null) {
         short type = 0;
         if (xmlAnim.hasAttribute("type")) {
            String duration = xmlAnim.getAttributeValue("type");
            switch (duration) {
               case "scale":
                  type = 1;
                  break;
               case "rotate":
                  type = 2;
                  break;
               case "skewX":
                  type = 3;
                  break;
               case "skewY":
                  type = 4;
            }
         }

         Duration duration = Duration.ZERO;
         if (xmlAnim.hasAttribute("dur")) {
            duration = parseDuration(xmlAnim.getAttributeValue("dur"));
         }

         Transition transition = null;
         switch (type) {
            case 0:
               TranslateTransition translate;
               if (parallel != null) {
                  translate = new TranslateTransition(duration);
               } else {
                  translate = new TranslateTransition(duration, node);
               }

               translate.setDuration(duration);
               transition = translate;
               if (fromArgs.size() == 2) {
                  translate.setFromX(fromArgs.get(0));
                  translate.setFromY(fromArgs.get(1));
               } else if (fromArgs.size() == 1) {
                  translate.setFromX(fromArgs.get(0));
               }

               if (toArgs.size() == 2) {
                  translate.setToX(toArgs.get(0));
                  translate.setToY(toArgs.get(1));
               } else if (toArgs.size() == 1) {
                  translate.setToX(toArgs.get(0));
               }
               break;
            case 1:
               ScaleTransition scale;
               if (parallel != null) {
                  scale = new ScaleTransition(duration);
               } else {
                  scale = new ScaleTransition(duration, node);
               }

               scale.setDuration(duration);
               transition = scale;
               if (fromArgs.size() == 1) {
                  scale.setFromX(fromArgs.get(0));
                  scale.setFromY(fromArgs.get(0));
               } else if (fromArgs.size() == 2) {
                  scale.setFromX(fromArgs.get(0));
                  scale.setFromY(fromArgs.get(1));
               }

               if (toArgs.size() == 1) {
                  scale.setToX(toArgs.get(0));
                  scale.setToY(toArgs.get(0));
               } else if (toArgs.size() == 2) {
                  scale.setToX(toArgs.get(0));
                  scale.setToY(toArgs.get(1));
               }
               break;
            case 2:
               RotateTransition rotate;
               if (parallel != null) {
                  rotate = new RotateTransition(duration);
               } else {
                  rotate = new RotateTransition(duration, node);
               }

               rotate.setDuration(duration);
               transition = rotate;
               if (fromArgs.size() == 1) {
                  rotate.setFromAngle(fromArgs.get(0));
               } else if (fromArgs.size() == 3) {
                  rotate.setFromAngle(fromArgs.get(0));
               }

               if (toArgs.size() >= 1) {
                  rotate.setToAngle(toArgs.get(0));
               }
         }

         if (transition != null) {
            if (xmlAnim.hasAttribute("repeatCount")) {
               String repeatValue = xmlAnim.getAttributeValue("repeatCount");
               if (repeatValue.equals("indefinite")) {
                  transition.setCycleCount(-1);
               } else {
                  int count = ParserUtils.parseIntProtected(repeatValue);
                  transition.setCycleCount(count);
               }
            }

            if (xmlAnim.hasAttribute("begin")) {
               String begin = xmlAnim.getAttributeValue("begin");
               Duration beginDur = parseDuration(begin);
               if (beginDur != null) {
                  transition.setDelay(beginDur);
               }
            }

            if (parallel != null) {
               parallel.getChildren().add(transition);
               return null;
            } else {
               return transition;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private static Color getFromColorArgument(XMLNode xmlAnim) {
      if (xmlAnim.hasAttribute("from")) {
         String content = xmlAnim.getAttributeValue("from");
         return ParserUtils.getColor(content);
      } else {
         return null;
      }
   }

   private static Color getToColorArgument(XMLNode xmlAnim) {
      if (xmlAnim.hasAttribute("to")) {
         String content = xmlAnim.getAttributeValue("to");
         return ParserUtils.getColor(content);
      } else {
         return null;
      }
   }

   private static boolean getFromVisibilityArgument(XMLNode xmlAnim) {
      if (xmlAnim.hasAttribute("from")) {
         String content = xmlAnim.getAttributeValue("from");
         return ParserUtils.parseVisibility(content);
      } else {
         return true;
      }
   }

   private static boolean getToVisibilityArgument(XMLNode xmlAnim) {
      if (xmlAnim.hasAttribute("to")) {
         String content = xmlAnim.getAttributeValue("to");
         return ParserUtils.parseVisibility(content);
      } else {
         return true;
      }
   }

   private static List<Double> getFromArguments(XMLNode xmlAnim, Viewport viewport) {
      if (xmlAnim.hasAttribute("from")) {
         String content = xmlAnim.getAttributeValue("from");
         return TransformUtils.getTransformArgumentsForAnimation(content, viewport);
      } else {
         return null;
      }
   }

   private static List<Double> getToArguments(XMLNode xmlAnim, Viewport viewport) {
      if (xmlAnim.hasAttribute("to")) {
         String content = xmlAnim.getAttributeValue("to");
         return TransformUtils.getTransformArgumentsForAnimation(content, viewport);
      } else {
         return null;
      }
   }
}
