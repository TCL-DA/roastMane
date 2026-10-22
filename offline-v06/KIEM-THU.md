# Kiểm thử roastMane v06 offline — 22/10/2026

## Kết quả

- Dựng patch Java bằng JDK 21 và đóng gói `app-image` bằng jpackage: **PASS**.
- Khởi động JavaFX với root dữ liệu tạm, tiêu đề `roastMane — Local Offline`: **PASS**.
- Quét chữ đang hiển thị trong scene graph, số chuỗi `Cropster` hoặc `Roasting Intelligence`: **0**.
- Catalog và thư mục dữ liệu dùng chung một root: **PASS**.
- Chặn socket mạng ở lớp JVM: **PASS**.
- Lưu mẻ thử 6,0 kg với đường cong nhiệt 3 điểm rồi đọc lại: **PASS**.
- Hồi quy lưu/sửa/khởi động lại: ID ổn định, đường cong 4 điểm còn nguyên, archive khớp outbox: **PASS**.
- Nhánh lỗi outbox: lưu thất bại đúng cách, không tiêu thụ ID, `verifySaved` từ chối: **PASS**.
- Tài nguyên logo cũ được thay bằng monogram v06 đã duyệt; bí danh tên cũ chỉ được giữ cho enum/tên đường dẫn động và có cùng nội dung byte với tài nguyên mới: **PASS**.
- SVG biểu tượng và wordmark được nạp bằng chính `JavaFxUtil.loadSvg`; kích thước render lần lượt là `220,0 x 242,1` và `1223,2 x 250,3`, scene graph có đủ 2 node: **PASS**.
- Icon EXE đa kích thước có đủ 6 lớp `16, 32, 48, 64, 128, 256 px`, dùng avatar xanh/be v06: **PASS**.
- Monogram SVG và avatar 1024 px trong JAR khớp byte với file đã duyệt; SHA-256 lần lượt là `DE59BEEC61D8EBBFE8BDC6A01925CF12B724699393351846055A387A1E8917AF` và `E2288BBFBECDFBD1ADD6EB030D7F12E3A143D47DC6F9E75E2FE208828CCA5D52`: **PASS**.
- `fonts/SIL Open Font License.txt` còn trong JAR: **PASS**.

## Giới hạn đã biết

- Đây là bản vá JAR đã biên dịch, không phải build lại từ toàn bộ mã nguồn.
- Package `org.cropster.*`, `com.cropster.*`, tên một số JAR, enum và bí danh tài nguyên nội bộ vẫn còn để bảo toàn tương thích nhị phân và nguồn gốc pháp lý. Chúng không còn là nhận diện hiển thị.
- Log kỹ thuật vẫn có thể nêu class, package hoặc endpoint gốc. Kết nối socket bị chặn; UI hỗ trợ, cập nhật và đồng bộ trực tuyến đã bị vô hiệu hóa.
- Chưa chọn cổng serial, chưa chạy với máy rang thật và không flash firmware.
- Dữ liệu mặc định mới là `%USERPROFILE%\roastMane\otl-offline`; không tự động di chuyển dữ liệu cũ.

## Cách chạy lại

Script dựng: `source\build.ps1`. Test hồi quy: `source\tests\local\PatchedFilePersisterIntegrationTest.java`. Kiểm tra render SVG: `source\tests\local\BrandRenderProbe.java`. Smoke GUI được bật bằng `-Doffline.smoke=true` và bắt buộc dùng `-Doffline.data` trỏ vào thư mục tạm.

Kết quả smoke cuối trên bundle `roastMane-Offline-OTL-20261022-02`:

```text
PASS title=roastMane visibleLegacyBranding=0 catalogIdentity=true rootUnified=true batchInputKg=6.0 networkBlocked=true previousBatches=0 savedId=2 points=3
PASS mark=220.0x242.1 wordmark=1223.2x250.3 nodes=2
PASS restartCurvePoints=4 archiveCurvePoints=4
PASS saveFailure=true verifyRejected=true
PASS rosterSize=1 savedOutputLotInRoster=false failedOutputLotInRoster=false
PASS failedId=null archiveCount=1
```
