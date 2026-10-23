# Kiểm thử roastMane offline — bản -03

## Kiểm tra bản -03

- Biên dịch lớp vá bằng JDK 21, tạo app-image bằng jpackage: **PASS**.
- Nạp SVG bằng chính `JavaFxUtil.loadSvg`, kiểm tra bounds vật lý: mark **11,8 × 13,0**, wordmark **212,8 × 44,0**; nằm trong khung 16 × 16 và 298 × 50: **PASS**.
- JavaFX smoke dùng root tạm: tiêu đề roastMane, không có nhận diện cũ trong chữ hiển thị, catalog/root thống nhất, socket bị chặn: **PASS**.
- Lưu mẻ mô phỏng 6 kg, đọc lại đủ đường cong 3 điểm: **PASS**.
- Chụp scene JavaFX thật và xem trực quan: biểu tượng thanh bên không còn phóng quá lớn; bố cục mở đủ ở cửa sổ 1600 × 1000: **PASS**.
- Avatar/icon EXE không đổi so với bản -02. Tài nguyên SVG hiển thị dùng bản scale tọa độ trực tiếp do Illustrator xuất; các alias tương thích nhận cùng nội dung.

## Kết quả kế thừa, không chạy lại trong đợt sửa tài nguyên

Ở bản -02, kiểm thử persistence đầy đủ đã đạt: ID ổn định sau restart, đường cong 4/4 điểm, archive khớp outbox, nhánh ghi lỗi không tiêu thụ ID và `verifySaved` từ chối dữ liệu chưa ghi. Bản -03 không thay logic persistence. Icon ICO đa kích thước và giấy phép font đã được kiểm tra ở đợt trước.

## Lỗi bố cục đã sửa

Kiểm tra cũ chỉ xác nhận SVG nạp được, đã bỏ sót bounds 220 × 242 và 1223 × 250 gây logo quá lớn trong UI. Bản -03 dùng SVG có tọa độ vật lý đúng kích thước, không phụ thuộc scale của `viewBox`, và bổ sung assertion giới hạn bounds trong `BrandRenderProbe`.

## Giới hạn

- Đây là bản vá JAR nền, chưa phải full-source build. Main source còn 65 lỗi compiler; lớp vá biên dịch được.
- Package và alias nội bộ cũ được giữ để tương thích và bảo toàn nguồn gốc pháp lý.
- Không chọn cổng serial, không thử máy rang thật, không flash firmware và không dùng dữ liệu cá nhân.
- Root mặc định vẫn là `%USERPROFILE%\roastMane\otl-offline`; các phép thử chỉ dùng thư mục tạm.

Source kiểm tra: `tests/local/BrandRenderProbe.java`, `patch-src/local/SmokeProbe.java` và `tests/local/PatchedFilePersisterIntegrationTest.java`. Script dựng: `build.ps1`. Hồ sơ source build độc lập nằm trong `reports/BASELINE.md` ở gốc repository.
