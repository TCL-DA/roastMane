package local;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import org.girod.javafx.svgimage.SVGImage;

/** Kiểm tra hai SVG thương hiệu bằng chính bộ nạp SVG của ứng dụng. */
public final class BrandRenderProbe {
    private BrandRenderProbe() {
    }

    public static void main(String[] args) throws Exception {
        CountDownLatch completed = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();

        Platform.startup(() -> {
            try {
                SVGImage mark = load(resolve(args, 0, "/icons/logo-roastmane.svg"));
                SVGImage wordmark = load(resolve(args, 1, "/images/roastmane-logo.svg"));
                VBox root = new VBox(mark, wordmark);
                new Scene(root);
                root.applyCss();
                root.layout();
                verifyFits("mark", mark, 16.0, 16.0);
                verifyFits("wordmark", wordmark, 298.0, 50.0);
                System.out.printf(
                    "PASS mark=%.1fx%.1f wordmark=%.1fx%.1f nodes=%d%n",
                    mark.getWidth(), mark.getHeight(),
                    wordmark.getWidth(), wordmark.getHeight(),
                    root.getChildren().size()
                );
            } catch (Throwable throwable) {
                failure.set(throwable);
            } finally {
                completed.countDown();
            }
        });

        if (!completed.await(20, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Timed out while rendering brand SVGs");
        }
        Platform.exit();
        if (failure.get() != null) {
            throw new AssertionError("Brand SVG render failed", failure.get());
        }
    }

    private static URL resolve(String[] args, int index, String resource) throws Exception {
        URL url = args.length > index ? Path.of(args[index]).toUri().toURL() : BrandRenderProbe.class.getResource(resource);
        if (url == null) {
            throw new IllegalStateException("Missing resource: " + resource);
        }
        return url;
    }

    private static SVGImage load(URL url) {
        return JavaFxUtil.loadSvg(url);
    }

    private static void verifyFits(String name, SVGImage image, double maxWidth, double maxHeight) {
        if (image.getWidth() <= 0.0 || image.getHeight() <= 0.0) {
            throw new AssertionError(name + " has empty bounds");
        }
        if (image.getWidth() > maxWidth || image.getHeight() > maxHeight) {
            throw new AssertionError(name + " exceeds layout box: " + image.getWidth() + "x" + image.getHeight());
        }
    }
}
