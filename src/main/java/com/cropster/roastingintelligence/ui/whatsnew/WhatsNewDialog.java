package com.cropster.roastingintelligence.ui.whatsnew;

import com.cropster.roastingintelligence.ui.JavaFxController;
import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.components.skin.NoTitlePopOverSkin;
import com.cropster.roastingintelligence.ui.util.VeilSupport;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.TreeMap;
import java.util.ServiceLoader.Provider;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Skin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;
import org.apache.commons.lang3.SystemUtils;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.action.ActionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WhatsNewDialog extends PopOver {
   public static final String WHATSNEW_STYLE_SHEET = WhatsNewDialog.class
      .getResource(SystemUtils.IS_OS_MAC_OSX ? "whatsnewdialog-mac.css" : "whatsnewdialog.css")
      .toExternalForm();
   private final Logger logger = LoggerFactory.getLogger(WhatsNewDialog.class);
   private final WhatsNewPane contentPane = new WhatsNewPane();
   private final boolean centered;
   private final IntSupplier lastSeenPageIdentifierGetter;
   private final IntConsumer lastSeenPageIdentifierSetter;
   private final Map<Integer, WhatsNewPage> pages = new TreeMap<>();
   private final VeilSupport veilSupport = new VeilSupport(false);
   private List<WhatsNewPage> pagesList = List.of();
   private boolean wasGotItCheckboxChecked = false;

   public static WhatsNewDialog.Builder builder() {
      return new WhatsNewDialog.Builder(null, null);
   }

   public static WhatsNewDialog.Builder builder(IntSupplier lastSeenPageGetter, IntConsumer lastSeenPageSetter) {
      return new WhatsNewDialog.Builder(lastSeenPageGetter, lastSeenPageSetter);
   }

   private WhatsNewDialog(WhatsNewDialog.Builder builder) {
      this.centered = builder.centered;
      this.lastSeenPageIdentifierGetter = builder.lastSeenPageIdentifierGetter;
      this.lastSeenPageIdentifierSetter = builder.lastSeenPageIdentifierSetter;
      this.setCloseButtonEnabled(false);
      this.setCornerRadius(builder.cornerRadius);
      this.setDetachable(true);
      this.setHeaderAlwaysVisible(true);
      this.getRoot().getStylesheets().add(JavaFxController.STYLE_SHEET);
      this.getRoot().getStylesheets().add(WHATSNEW_STYLE_SHEET);
      this.init(builder.title, builder.pagesPackage);
   }

   public WhatsNewDialog addPage(WhatsNewPage page) {
      if (page != null) {
         this.pages.put(page.identifier(), page);
      }

      return this;
   }

   public void centerAtOwner() {
      Node owner = this.getOwnerNode();
      Window window = owner.getScene().getWindow();
      double centerX = window.getX() + window.getWidth() / 2.0;
      double centerY = window.getY() + window.getHeight() / 2.0;
      this.setAnchorX(centerX - this.getSkin().getNode().getBoundsInParent().getWidth() / 2.0);
      this.setAnchorY(centerY - this.getSkin().getNode().getBoundsInParent().getHeight() / 2.0);
   }

   public int lastPageIdentifier() {
      return this.pages.values().stream().mapToInt(p -> p.identifier()).max().orElse(-1);
   }

   public WhatsNewDialog lookupPages() {
      ServiceLoader.load(WhatsNewPage.class).stream().map(Provider::get).filter(p -> !p.disabled()).forEach(this::addPage);
      return this;
   }

   public WhatsNewDialog lookupPages(String packageName) {
      ScanResult scanResult = new ClassGraph().enableClassInfo().acceptPackages(new String[]{packageName}).scan();

      try {
         for (ClassInfo classInfo : scanResult.getClassesImplementing(WhatsNewPage.class)) {
            if (!classInfo.isAbstract()) {
               try {
                  WhatsNewPage page = (WhatsNewPage)classInfo.loadClass().getDeclaredConstructor().newInstance();
                  if (!page.disabled()) {
                     this.addPage(page);
                  }
               } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException var7) {
                  this.logger.warn("Unable to add page of type: {}", classInfo.getSimpleName(), var7);
               }
            }
         }
      } catch (Throwable var8) {
         if (scanResult != null) {
            try {
               scanResult.close();
            } catch (Throwable var6) {
               var8.addSuppressed(var6);
            }
         }

         throw var8;
      }

      if (scanResult != null) {
         scanResult.close();
      }

      return this;
   }

   public WhatsNewDialog showAt(Node owner) {
      this.veilSupport.installHandler(this);
      this.veilSupport.setVeiledScene(owner, false);
      this.pagesList = this.pages.values().stream().collect(Collectors.toList());
      if (!this.pagesList.isEmpty()) {
         boolean setterExists = this.lastSeenPageIdentifierSetter != null;
         boolean getterExists = this.lastSeenPageIdentifierGetter != null;
         int lastSeenPageId = getterExists ? this.lastSeenPageIdentifierGetter.getAsInt() : -1;
         int firstPageIndex = lastSeenPageId >= 0 && lastSeenPageId < this.lastPageIdentifier()
            ? IntStream.range(0, this.pagesList.size()).filter(i -> this.pagesList.get(i).identifier() <= lastSeenPageId).max().orElse(-1) + 1
            : 0;
         this.wasGotItCheckboxChecked = setterExists && getterExists && lastSeenPageId == this.lastPageIdentifier();
         this.showPage(firstPageIndex);
      }

      this.setDetached(true);
      this.performShow(owner);
      return this;
   }

   public WhatsNewDialog showAt(Scene scene) {
      return this.showAt(scene.getRoot());
   }

   protected Skin<?> createDefaultSkin() {
      return new NoTitlePopOverSkin(this);
   }

   private void init(String title, String pagesPackage) {
      this.contentPane.getTitleLabel().setText(title);
      this.contentPane.getCloseButton().setOnAction(e -> {
         this.hide();
         boolean canBeSaved = this.lastSeenPageIdentifierSetter != null;
         boolean checked = this.contentPane.getGotitCheckbox().isSelected();
         if (canBeSaved) {
            if (checked) {
               this.lastSeenPageIdentifierSetter.accept(this.lastPageIdentifier());
            } else if (this.wasGotItCheckboxChecked) {
               this.lastSeenPageIdentifierSetter.accept(-1);
            }
         }
      });
      JavaFxUtil.setDefaultLineSpacing(this.contentPane);
      this.setContentNode(this.contentPane);
   }

   private void performShow(Node owner) {
      if (owner.localToScreen(owner.getBoundsInLocal()) == null) {
         Platform.runLater(() -> this.performShow(owner));
      } else {
         this.show(owner, 0.0);
         if (this.centered) {
            this.centerAtOwner();
         }
      }
   }

   private void showPage(int pageIndex) {
      WhatsNewPage page = this.pagesList.get(pageIndex);
      BorderPane imageContainer = this.contentPane.getImageContainer();
      if (!imageContainer.getChildren().isEmpty()) {
         imageContainer.getChildren().clear();
      }

      URL imageUrl = page.image();
      imageContainer.setVisible(imageUrl != null);
      if (imageUrl != null) {
         if (imageUrl.getPath().toLowerCase().endsWith(".svg")) {
            imageContainer.setCenter(JavaFxUtil.loadSvg(imageUrl).scaleTo(500.0));
         } else {
            Image image = new Image(imageUrl.toExternalForm());
            ImageView view = new ImageView(image);
            view.setFitWidth(500.0);
            view.setPreserveRatio(true);
            view.setSmooth(true);
            view.setCache(true);
            imageContainer.setCenter(view);
         }
      }

      this.contentPane.getBannerTitle().setText(page.title());
      this.contentPane.getBannerStatus().setVisible(page.needsAttention());
      this.contentPane.getContentFlow().getChildren().clear();
      this.contentPane.getContentFlow().getChildren().addAll(page.contentNodes());
      boolean showButtons = this.pagesList.size() > 1 || page.action() != null;
      this.contentPane.getButtonsContainer().setVisible(showButtons);
      this.contentPane.getActionButton().setVisible(page.action() != null);
      this.contentPane.getNotificationContainer().setVisible(this.pagesList.size() > 1);
      if (page.action() != null) {
         ActionUtils.unconfigureButton(this.contentPane.getActionButton());
         ActionUtils.configureButton(page.action(), this.contentPane.getActionButton());
      }

      if (this.pagesList.size() > 1) {
         this.contentPane
            .getNotificationLabel()
            .setText(org.cropster.roastingintelligence.util.Messages.m_("Notification {0,number,#0} of {1,number,#0}", pageIndex + 1, this.pagesList.size()));
         this.contentPane.getPreviousButton().setOnAction(e -> this.showPage(pageIndex == 0 ? this.pagesList.size() - 1 : pageIndex - 1));
         this.contentPane.getNextButton().setOnAction(e -> this.showPage(pageIndex >= this.pagesList.size() - 1 ? 0 : pageIndex + 1));
      }

      boolean canBeSaved = this.lastSeenPageIdentifierSetter != null && this.lastSeenPageIdentifierGetter != null;
      boolean checked = canBeSaved && this.lastSeenPageIdentifierGetter.getAsInt() == this.lastPageIdentifier();
      this.contentPane.getGotitCheckbox().setSelected(checked);
      this.contentPane.getGotitCheckbox().setDisable(!canBeSaved);
      if (canBeSaved && page.identifier() > this.lastSeenPageIdentifierGetter.getAsInt()) {
         this.lastSeenPageIdentifierSetter.accept(page.identifier());
      }

      JavaFxUtil.setDefaultLineSpacing(this.contentPane);
   }

   public static class Builder {
      private final IntSupplier lastSeenPageIdentifierGetter;
      private final IntConsumer lastSeenPageIdentifierSetter;
      private double cornerRadius = 11.0;
      private boolean centered = true;
      private String pagesPackage = null;
      private String title = org.cropster.roastingintelligence.util.Messages.m_("What's new?");

      public Builder(IntSupplier lastSeenPageIdentifierGetter, IntConsumer lastSeenPageIdentifierSetter) {
         this.lastSeenPageIdentifierGetter = lastSeenPageIdentifierGetter;
         this.lastSeenPageIdentifierSetter = lastSeenPageIdentifierSetter;
      }

      public WhatsNewDialog build() {
         return new WhatsNewDialog(this);
      }

      public WhatsNewDialog.Builder centered(boolean centered) {
         this.centered = centered;
         return this;
      }

      public WhatsNewDialog.Builder cornerRadius(double cornerRadius) {
         this.cornerRadius = cornerRadius;
         return this;
      }

      public WhatsNewDialog.Builder withPages(String pagesPackage) {
         this.pagesPackage = pagesPackage;
         return this;
      }

      public WhatsNewDialog.Builder withTitle(String title) {
         this.title = title;
         return this;
      }
   }
}
