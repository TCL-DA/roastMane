package local;

import java.nio.file.Files;
import java.nio.file.Path;

/** Chạy launcher thật với dữ liệu tạm và dừng JVM kiểm thử khi có kết quả. */
public final class GuiSmokeTest {
    public static void main(String[] args) throws Exception {
        Path root = Files.createTempDirectory("roastmane-source-gui-").toAbsolutePath();
        System.setProperty("offline.data", root.toString());
        System.setProperty("offline.smoke", "true");
        System.setProperty("org.cropster.roastingintelligence.logging.path", root.toString().replace('\\', '/') + "/");
        Thread monitor = new Thread(() -> {
            long deadline = System.nanoTime() + 45_000_000_000L;
            try {
                while (System.nanoTime() < deadline) {
                    Path failure = root.resolve("smoke-failure.txt");
                    if (Files.exists(failure)) {
                        System.err.println(Files.readString(failure));
                        System.exit(1);
                    }
                    Path result = root.resolve("smoke-result.txt");
                    if (Files.exists(result)) {
                        String report = Files.readString(result);
                        if (!report.startsWith("PASS ")) throw new AssertionError(report);
                        System.out.println(report);
                        System.exit(0);
                    }
                    Thread.sleep(100);
                }
                throw new AssertionError("Timed out waiting for the real application smoke test: " + root);
            } catch (Throwable error) {
                error.printStackTrace();
                System.exit(2);
            }
        }, "roastmane-smoke-monitor");
        monitor.setDaemon(true);
        monitor.start();
        Launcher.main(args);
    }
}
