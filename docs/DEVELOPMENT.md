# Phát triển roastMane

## Trạng thái hiện tại

Cây phát triển chính nằm tại `src/main/java/` và `src/main/resources/`. Lượt dựng đã xác minh ngày 23/10/2026 biên dịch chung **684 file Java** gồm 677 source ứng dụng và 7 source overlay, đạt **0 lỗi** và còn **11 cảnh báo deprecated/removal**. Classpath biên dịch loại cả main JAR và overlay JAR cũ, vì vậy kết quả không được che bởi class ứng dụng đã biên dịch sẵn.

Main JAR được tạo từ class vừa biên dịch cùng **291 tài nguyên**. JavaFX, thư viện bên thứ ba, native và JDK runtime vẫn là dependency nhị phân. `decompiled/` và `extracted-all/` là kho lưu trữ để đối chiếu, không phải cây source chính.

Xem [SOURCE-BUILD.md](SOURCE-BUILD.md) và [báo cáo máy đọc được](../reports/source-build-20261023.json) để kiểm tra số liệu và giới hạn của lượt dựng.

## Cấu trúc cần biết

```text
src/main/java/          source ứng dụng chính
src/main/resources/     FXML, CSS, ảnh, font, âm thanh và metadata
offline-v06/patch-src/  overlay offline và công cụ nhận diện/đóng gói
tests/local/            UI/FXML và launcher smoke
offline-v06/tests/      persistence và SVG
decompiled/             bản dịch ngược nguyên trạng để đối chiếu
extracted-all/          bản trích xuất từng dependency để lưu trữ
reports/                bằng chứng compiler, build và test
```

Giữ nguyên namespace `org.cropster.*` và `com.cropster.*`. Chúng là định danh tương thích nhị phân; nhận diện hiển thị của ứng dụng là roastMane.

## Yêu cầu môi trường

- Windows.
- JDK 21.
- Python 3.
- Một bundle nền chứa JavaFX, JAR bên thứ ba, native và runtime cần thiết.

Các script có giá trị mặc định theo máy đã kiểm thử. Dùng `-JdkBin` và các tham số bundle/output để chạy ở vị trí khác.

## Biên dịch source

```powershell
.\compile-recovered.ps1 `
  -Target Recovered `
  -Bundle 'C:\path\to\dependency-bundle' `
  -JdkBin 'C:\path\to\jdk-21\bin'
```

Target `Recovered` biên dịch cây ứng dụng và overlay hiện hành. Kết quả đã xác minh là 684 source, 0 lỗi và 11 cảnh báo. Các báo cáo baseline 65 lỗi mô tả lịch sử phục hồi, không phải trạng thái hiện tại.

## Dựng ứng dụng từ source

Chọn một output chưa tồn tại; script không ghi đè bundle nền:

```powershell
.\build-from-source.ps1 `
  -BaseBundle 'C:\path\to\dependency-bundle' `
  -Output 'C:\Tools\_soi\roastMane-dev' `
  -JdkBin 'C:\path\to\jdk-21\bin'
```

Script thực hiện tuần tự:

1. Biên dịch main và overlay từ source.
2. Đóng gói class mới cùng `src/main/resources/` thành main JAR và `offline-local.jar`.
3. Chạy pipeline nhận diện v06 trên main JAR vừa tạo.
4. Tạo app-image bằng `jpackage`.
5. Chép cây phát triển cần thiết và ghi checksum/build metadata vào output.

Output đã xác minh:

```text
C:\Tools\_soi\roastMane-SourceBuild-20261023-01
```

## Chạy hồi quy

```powershell
.\test-recovered.ps1 `
  -Bundle 'C:\Tools\_soi\roastMane-SourceBuild-20261023-01' `
  -JdkBin 'C:\path\to\jdk-21\bin'
```

Script biên dịch test, tạo home/data/log tạm rồi chạy bốn chương trình:

- `local.PatchedFilePersisterIntegrationTest`: lưu mới, sửa, restart, archive, lỗi ghi sửa đổi và timestamp trùng.
- `local.BrandRenderProbe`: giới hạn vật lý của SVG mark và wordmark.
- `local.RecoveredUiTest`: các nhánh `TaskLabel`, FXML thật và `TextFormatter` khi focus; đồng thời xác nhận CodeSource là main JAR vừa dựng.
- `local.GuiSmokeTest`: launcher thật, tiêu đề, catalog/root tạm, chặn mạng và lưu mẻ mô phỏng.

Lượt kiểm tra ngày 23/10/2026 đạt cả bốn chương trình với exit `0`. Không dùng dữ liệu người dùng, cổng serial hoặc máy rang.

## Quy tắc khi sửa source phục hồi

- Đối chiếu method liên quan bằng `javap` hoặc CFR trước khi sửa lỗi do decompiler.
- Sửa hẹp theo bằng chứng; không dùng cast hoặc regex hàng loạt chỉ để compiler im lỗi.
- Chạy lại target `Recovered`, sau đó dựng bundle mới và chạy `test-recovered.ps1` khi thay đổi ảnh hưởng runtime.
- Không suy diễn compile/smoke thành tương thích mọi tính năng hoặc phần cứng.

11 cảnh báo API cũ hiện chưa chặn build. Việc thay API phải được xử lý riêng với kiểm thử hành vi tương ứng.
