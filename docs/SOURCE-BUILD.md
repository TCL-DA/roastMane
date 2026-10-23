# Source build và sửa lỗi — 23/10/2026

## Kết quả

Ứng dụng và lớp offline biên dịch cùng nhau từ **684 file**: 677 main + 7 overlay, **0 lỗi, 11 cảnh báo deprecated**. Compile classpath loại cả `roastingintelligence-5.13.jar` và `offline-local.jar` cũ. Main JAR được đóng gói từ class vừa biên dịch cùng 291 tài nguyên trong `src/main/resources`; không lấy class thiếu từ JAR cũ.

JavaFX, thư viện bên thứ ba, native và JDK runtime vẫn là binary dependency. Không có tuyên bố build lại toàn bộ 117 thư viện hoặc đã kiểm thử mọi tính năng.

## Lỗi đã sửa

- 65 lỗi baseline: generic bị mất, kiểu listener JavaFX mơ hồ, biến synthetic mất phạm vi, `NumberFormat` bị tái sử dụng thành plot và nhánh stop-handler sai cú pháp.
- 8 lỗi ở giai đoạn compiler tiếp theo: `TaskLabel` dùng switch expression thiếu giá trị và nhánh unreachable; `ToastNotificationController` trả tọa độ trước khi cộng offset lần hiển thị tiếp theo.
- Đối chiếu receiver, listener overload, kiểu plot, collection operation và nhánh điều kiện bằng `javap`/CFR của binary nền; không thay đổi nguồn trích xuất nguyên bản.
- `OfflinePersistence`: lần lưu sửa đổi thất bại từng có thể được `verifySaved` chấp nhận nhờ cache lần lưu trước. Nay hủy xác nhận trước lần ghi mới và chỉ xác nhận sau khi hoàn thành.
- Trùng timestamp từng được phát hiện sau khi đã cấp ID và ghi archive. Nay kiểm tra trước các side effect; ID và archive không đổi khi từ chối trùng.
- Pipeline đóng gói từng sao chép `roastMane.cfg` cũ vào input, làm jpackage báo `FileAlreadyExistsException`. Nay bỏ config launcher cũ để jpackage tạo lại.
- Script build nhận `-JdkBin` thay vì buộc đường dẫn JDK của một máy.

## Cây phát triển

```text
src/main/java/          677 source ứng dụng có thể biên dịch
src/main/resources/     FXML, CSS, ảnh, font, âm thanh và metadata
offline-v06/patch-src/  lớp offline và công cụ nhận diện
tests/local/           UI/FXML và launcher smoke
offline-v06/tests/     persistence và SVG
```

`decompiled/` và `extracted-all/` giữ nguyên để đối chiếu. Các số liệu 65 lỗi trong `reports/BASELINE.md` là lịch sử, không phải trạng thái cây `src/` hiện tại. Trường `full_source_build_verified=false` trong kiểm toán kho 117 JAR vẫn đúng cho phạm vi toàn bộ thư viện trích xuất.

## Chạy lại

```powershell
.\compile-recovered.ps1 -Target Recovered -Bundle 'C:\path\to\dependencies'
.\build-from-source.ps1 -BaseBundle 'C:\path\to\dependencies' -Output 'C:\Tools\_soi\roastMane-dev'
.\test-recovered.ps1 -Bundle 'C:\Tools\_soi\roastMane-dev'
```

Cả ba script hỗ trợ `-JdkBin`. Cần Windows, JDK 21 và Python 3; dùng output chưa tồn tại. Đường dẫn mặc định là cấu hình máy đã thử, không phải dependency tự tải về.

## Bằng chứng kiểm thử

| Kiểm tra trên JAR dựng từ source | Kết quả |
|---|---|
| Lưu, sửa, khởi động lại, archive, giữ 4/4 điểm | PASS |
| Ép lỗi JsonWriter khi sửa: giữ nguyên file cũ, từ chối xác nhận lưu | PASS |
| Trùng timestamp: ID null, sequence/archive không đổi; retry ID liên tục | PASS |
| SVG vật lý vừa khung 16 × 16 và 298 × 50 | PASS |
| TaskLabel: null/0/nonzero, volume/duration/count/one-time; TextFormatter giới hạn khi focus | PASS |
| Launcher thật: title, catalog/root, chặn mạng, lưu mẻ mô phỏng | PASS |

Các test dùng root và log tạm; UI test xác nhận CodeSource đúng JAR được dựng mới. Xem [kết quả máy đọc được](../reports/source-build-20261023.json), [compiler](../reports/javac-recovered-current.txt) và bốn log `reports/source-build-*.txt`.

## Giới hạn còn lại

- 11 cảnh báo API deprecated/removal của ứng dụng cũ vẫn còn; runtime đang dùng JDK 21.
- Outbox và archive chưa phải một transaction đa file. Lỗi xảy ra sau khi đã bắt đầu cấp ID vẫn có thể để ID bị bỏ trống; lỗi archive sau khi ghi outbox khiến phép lưu bị từ chối xác nhận. Chưa áp migration hay đổi định dạng dữ liệu.
- Chưa kiểm thử máy rang, serial hoặc mọi cửa sổ/tính năng. Không suy ra tính đúng đắn toàn bộ ứng dụng từ compile và smoke test.
