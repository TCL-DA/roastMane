package org.girod.javafx.svgimage.xml.parsers;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import org.girod.javafx.svgimage.xml.specs.Viewport;

public class TransformUtils implements SVGTags {
   private static final Pattern TRANSFORM_PAT = Pattern.compile("\\w+\\s*\\((.*)\\)");

   private TransformUtils() {
   }

   public static List<Double> getTransformArgumentsForAnimation(String content, Viewport viewport) {
      List<Double> args = new ArrayList<>();
      StringTokenizer tok = new StringTokenizer(content, ", ");

      while (tok.hasMoreTokens()) {
         String argumentS = tok.nextToken();
         ParserUtils.parseLengthValue(args, argumentS, true, null, viewport);
      }

      return args;
   }

   private static List<Double> getTranslateTransformArguments(String transformTxt, Viewport viewport) {
      List<Double> args = new ArrayList<>();
      Matcher m = TRANSFORM_PAT.matcher(transformTxt);
      if (!m.matches()) {
         return null;
      } else {
         String content = m.group(1);
         StringTokenizer tok = new StringTokenizer(content, ", ");

         while (tok.hasMoreTokens()) {
            String argumentS = tok.nextToken();
            ParserUtils.parseLengthValue(args, argumentS, true, null, viewport);
         }

         return args;
      }
   }

   private static List<Double> getScaleTransformArguments(String transformTxt, Viewport viewport) {
      List<Double> args = new ArrayList<>();
      Matcher m = TRANSFORM_PAT.matcher(transformTxt);
      if (!m.matches()) {
         return null;
      } else {
         String content = m.group(1);
         StringTokenizer tok = new StringTokenizer(content, ", ");

         while (tok.hasMoreTokens()) {
            String argumentS = tok.nextToken();
            ParserUtils.parseLengthValue(args, argumentS, true, null, null);
         }

         return args;
      }
   }

   private static List<Double> getRotateTransformArguments(String transformTxt, Viewport viewport) {
      List<Double> args = new ArrayList<>();
      Matcher m = TRANSFORM_PAT.matcher(transformTxt);
      if (m.matches()) {
         String content = m.group(1);
         StringTokenizer tok = new StringTokenizer(content, ", ");
         int countTokens = tok.countTokens();
         if (countTokens == 1) {
            String argumentS = tok.nextToken();
            ParserUtils.parseLengthValue(args, argumentS, true, null, null);
         } else if (countTokens == 3) {
            String argumentS = tok.nextToken();
            ParserUtils.parseLengthValue(args, argumentS, true, null, null);
            argumentS = tok.nextToken();
            ParserUtils.parseLengthValue(args, argumentS, true, null, viewport);
            argumentS = tok.nextToken();
            ParserUtils.parseLengthValue(args, argumentS, true, null, viewport);
         }

         return args;
      } else {
         return null;
      }
   }

   private static List<Double> getSkewTransformArguments(String transformTxt, Viewport viewport) {
      List<Double> args = new ArrayList<>();
      Matcher m = TRANSFORM_PAT.matcher(transformTxt);
      if (!m.matches()) {
         return null;
      } else {
         String content = m.group(1);
         StringTokenizer tok = new StringTokenizer(content, ", ");

         while (tok.hasMoreTokens()) {
            String argumentS = tok.nextToken();
            ParserUtils.parseLengthValue(args, argumentS, true, null, null);
         }

         return args;
      }
   }

   private static List<Double> getMatrixTransformArguments(String transformTxt, Viewport viewport) {
      List<Double> args = new ArrayList<>();
      Matcher m = TRANSFORM_PAT.matcher(transformTxt);
      if (m.matches()) {
         String content = m.group(1);
         StringTokenizer tok = new StringTokenizer(content, ", ");
         int countTokens = tok.countTokens();
         if (countTokens == 6) {
            String argumentS = tok.nextToken();
            ParserUtils.parseLengthValue(args, argumentS, true, null, null);
            argumentS = tok.nextToken();
            ParserUtils.parseLengthValue(args, argumentS, true, null, null);
            argumentS = tok.nextToken();
            ParserUtils.parseLengthValue(args, argumentS, true, null, null);
            argumentS = tok.nextToken();
            ParserUtils.parseLengthValue(args, argumentS, true, null, null);
            argumentS = tok.nextToken();
            ParserUtils.parseLengthValue(args, argumentS, true, null, viewport);
            argumentS = tok.nextToken();
            ParserUtils.parseLengthValue(args, argumentS, true, null, viewport);
         }

         return args;
      } else {
         return null;
      }
   }

   public static void setTransforms(Node node, XMLNode xmlNode, Viewport viewport) {
      if (xmlNode.hasAttribute("transform")) {
         String transforms = xmlNode.getAttributeValue("transform");
         List<Transform> transformList = extractTransforms(transforms, viewport);
         if (!transformList.isEmpty()) {
            ObservableList<Transform> nodeTransforms = node.getTransforms();

            for (Transform theTransForm : transformList) {
               nodeTransforms.add(theTransForm);
            }
         }
      }
   }

   public static List<Transform> extractTransforms(String transforms, Viewport viewport) {
      List<Transform> transformList = new ArrayList<>();
      StringTokenizer tokenizer = new StringTokenizer(transforms, ")");

      while (tokenizer.hasMoreTokens()) {
         String transformTxt = tokenizer.nextToken() + ")";
         transformTxt = transformTxt.trim();
         if (transformTxt.startsWith("translateX")) {
            List<Double> args = getTranslateTransformArguments(transformTxt, viewport);
            if (args.size() == 1) {
               Transform transform = Transform.translate(args.get(0), 0.0);
               transformList.add(transform);
            }
         } else if (transformTxt.startsWith("translateY")) {
            List<Double> args = getTranslateTransformArguments(transformTxt, viewport);
            if (args.size() == 1) {
               Transform transform = Transform.translate(0.0, args.get(0));
               transformList.add(transform);
            }
         } else if (transformTxt.startsWith("translate")) {
            List<Double> args = getTranslateTransformArguments(transformTxt, viewport);
            if (args.size() == 2) {
               Transform transform = Transform.translate(args.get(0), args.get(1));
               transformList.add(transform);
            }
         } else if (transformTxt.startsWith("scaleX")) {
            List<Double> args = getScaleTransformArguments(transformTxt, viewport);
            if (args.size() == 1) {
               Transform transform = Transform.translate(args.get(0), 1.0);
               transformList.add(transform);
            }
         } else if (transformTxt.startsWith("scaleY")) {
            List<Double> args = getScaleTransformArguments(transformTxt, viewport);
            if (args.size() == 1) {
               Transform transform = Transform.translate(1.0, args.get(0));
               transformList.add(transform);
            }
         } else if (transformTxt.startsWith("scale")) {
            List<Double> args = getScaleTransformArguments(transformTxt, viewport);
            if (args.size() == 2) {
               Transform transform = Transform.scale(args.get(0), args.get(1));
               transformList.add(transform);
            } else if (args.size() == 1) {
               Transform transform = Transform.scale(args.get(0), args.get(0));
               transformList.add(transform);
            }
         } else if (transformTxt.startsWith("rotate")) {
            List<Double> args = getRotateTransformArguments(transformTxt, viewport);
            if (args.size() == 3) {
               Transform transform = Transform.rotate(args.get(0), args.get(1), args.get(2));
               transformList.add(transform);
            } else if (args.size() == 1) {
               Transform transform = Transform.rotate(args.get(0), 0.0, 0.0);
               transformList.add(transform);
            }
         } else if (transformTxt.startsWith("skewX")) {
            List<Double> args = getSkewTransformArguments(transformTxt, viewport);
            if (args.size() == 1) {
               Transform transform = Transform.shear(Math.tan(Math.toRadians(args.get(0))), 0.0);
               transformList.add(transform);
            }
         } else if (transformTxt.startsWith("skewY")) {
            List<Double> args = getSkewTransformArguments(transformTxt, viewport);
            if (args.size() == 1) {
               Transform transform = Transform.shear(0.0, Math.tan(Math.toRadians(args.get(0))));
               transformList.add(transform);
            }
         } else if (transformTxt.startsWith("matrix")) {
            List<Double> args = getMatrixTransformArguments(transformTxt, viewport);
            if (args.size() == 6) {
               Transform transform = Transform.affine(args.get(0), args.get(1), args.get(2), args.get(3), args.get(4), args.get(5));
               transformList.add(transform);
            }
         }
      }

      return transformList;
   }
}
