# Phát triển roastMane

## Trạng thái source hiện tại

| Phần | Trạng thái đã kiểm tra |
|---|---|
| Bundle `roastMane-Offline-OTL-20261022-02` | Chạy JavaFX, lưu/đọc lại mẻ QA và chặn mạng: đạt |
| Source overlay `offline-v06/patch-src/local` | 7 file Java biên dịch thành 15 class; 1 cảnh báo deprecated |
| Main source `decompiled/` | 677 file thuộc target ứng dụng; còn 65 lỗi và 10 cảnh báo trong 23 file |
| Toàn bộ dependency `extracted-all/` | Đã trích 117 JAR; 25.208 class entry ánh xạ về 14.223 file Java; chưa xác minh full-source build |

Bundle chạy được hiện nay được tạo bằng cách vá bytecode và tài nguyên của JAR nền. Nó **không** được build từ toàn bộ source trong `decompiled/` hay `extracted-all/`.

## Thư mục cần biết

- `decompiled/org/cropster/roastingintelligence/`: vòng đời ứng dụng, model vận hành, quản lý rang và preferences.
- `decompiled/com/cropster/roastingintelligence/ui/`: JavaFX controller, trạng thái cửa sổ và tiện ích UI.
- `decompiled/com/cropster/roastingintelligence/ui/layout/`: production, preparation, preferences và các panel chính.
- `decompiled/org/cropster/roastingintelligence/measurement/`: nguồn đo và luồng dữ liệu đo.
- `decompiled/org/cropster/roastingintelligence/model/` và `decompiled/com/cropster/roastingintelligence/model/`: model mẻ rang, profile, lot và cấu hình liên quan.
- `decompiled/org/cropster/roastingintelligence/service/` và `decompiled/com/cropster/roastingintelligence/service/`: persistence và service nội bộ.
- `offline-v06/patch-src/local/`: launcher offline, bootstrap catalog cục bộ, connection/persistence, smoke probe và công cụ vá JAR.
- `offline-v06/tests/local/`: kiểm tra persistence restart và render SVG bằng JavaFX.
- `reports/`: log `javac`, response file, mã thoát và bảng lỗi theo file.
- `extracted-all/<ten-jar>/source/`: source dịch ngược tách theo từng JAR để tránh ghi đè package hoặc tài nguyên trùng tên.

Giữ nguyên package `org.cropster.*` và `com.cropster.*` trong giai đoạn phục hồi. Đây là định danh tương thích nhị phân và nguồn gốc, không phải nhận diện hiển thị roastMane.

## Môi trường đã dùng

- Windows.
- Eclipse Adoptium JDK `21.0.12.101`.
- Bundle dependency: `C:\Tools\_soi\roastMane-Offline-OTL-20261022-02\app`.
- Bundle nền để vá: `C:\Tools\_soi\Cropster-Offline-OTL-20261021-01`.

Các đường dẫn trên là đường dẫn đã kiểm thử trên máy phát triển hiện tại. Có thể truyền đường dẫn khác qua tham số của script.

## Kiểm tra khả năng biên dịch source

Lệnh sau đã chạy thực tế:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass `
  -File .\compile-recovered.ps1 -Target All
```

Kết quả baseline phải được đọc đúng:

```text
javac target=main-current exit=1 errors=65 warnings=10
javac target=offline-v06-current exit=0 errors=0 warnings=1
```

`compile-recovered.ps1` loại `roastingintelligence-5.13.jar` khỏi classpath của main target để class đã biên dịch không che lỗi source. Nó cũng loại `decompiled/offline-patch/` khỏi target vì đó là source patch lịch sử được nhúng như tài nguyên; overlay đúng là `offline-v06/patch-src/local/`.

## Dựng bundle vá vào thư mục tạm

Không ghi đè bundle phát hành khi chỉ thử build. Chọn output mới trong `%TEMP%`:

```powershell
$trial = Join-Path $env:TEMP 'roastmane-v06-trial'
powershell.exe -NoProfile -ExecutionPolicy Bypass `
  -File .\offline-v06\build.ps1 `
  -BaseBundle 'C:\Tools\_soi\Cropster-Offline-OTL-20261021-01' `
  -Output $trial
```

Script này biên dịch overlay, vá JAR nền, tạo icon đa kích thước và đóng gói `app-image` bằng `jpackage`. Nó không biên dịch 677 file main source.

## Chạy lại kiểm tra persistence

Đây là quy trình đã dùng cho bundle `-02`; mọi dữ liệu được tạo trong `%TEMP%`:

```powershell
$bundle = 'C:\Tools\_soi\roastMane-Offline-OTL-20261022-02'
$app = Join-Path $bundle 'app'
$jdk = 'C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot\bin'
$classes = Join-Path $env:TEMP 'roastmane-persistence-test'
New-Item -ItemType Directory -Path $classes -Force | Out-Null
$cp = ((Get-ChildItem $app -Filter '*.jar').FullName +
       (Get-ChildItem (Join-Path $app 'javafx') -Filter '*.jar').FullName) -join ';'

& "$jdk\javac.exe" -encoding UTF-8 -proc:none -cp $cp -d $classes `
  .\offline-v06\tests\local\PatchedFilePersisterIntegrationTest.java
& "$jdk\java.exe" -cp "$classes;$cp" local.PatchedFilePersisterIntegrationTest
```

Kết quả đã xác minh gồm ID ổn định sau restart, đường cong `4/4`, archive khớp outbox, nhánh lỗi không tiêu thụ ID và không đưa mẻ rang thành phẩm vào danh mục cà phê xanh.

## Quy tắc test an toàn

- GUI smoke chỉ được chạy với `-Doffline.smoke=true` **và** `-Doffline.data=<thu-muc-tam>`.
- Không trỏ test vào `%USERPROFILE%\roastMane\otl-offline` hoặc dữ liệu thật của người dùng.
- Không chọn cổng serial, không kết nối máy rang và không dùng kết quả này để tuyên bố đã kiểm thử phần cứng.
- Ảnh preview phải lấy từ scene JavaFX thật với catalog QA tạm; không dựng ảnh giả giao diện.
- Giữ giấy phép, thông báo bản quyền và provenance của thư viện/JAR khi sửa hoặc đóng gói.
- Mỗi sửa lỗi source dịch ngược phải đối chiếu bytecode method tương ứng và chạy lại `compile-recovered.ps1`; không sửa hàng loạt bằng regex khi chưa chứng minh tương đương hành vi.

Chi tiết kết quả phát hành nằm trong [offline-v06/KIEM-THU.md](../offline-v06/KIEM-THU.md). Chẩn đoán source gồm [reports/BASELINE.md](../reports/BASELINE.md), [errors-by-file.csv](../reports/errors-by-file.csv) và [javac-main-current.txt](../reports/javac-main-current.txt).
