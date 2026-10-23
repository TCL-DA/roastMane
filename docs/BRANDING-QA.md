# Kiểm tra nhận diện và ảnh giao diện

## Lỗi tìm thấy khi chụp ảnh thực tế

Bản `-02` dùng SVG gốc có tọa độ trên canvas 300 px. Bộ nạp `fxsvgimage` có thể scale bằng transform nhưng trả layout bounds chưa scale; vì vậy một phép kiểm tra chỉ xác nhận SVG nạp thành công đã không phát hiện biểu tượng bị phóng lớn trong thanh bên.

## Sửa trong bản -03

- Giữ nguyên SVG/PNG gốc đã duyệt trong `brand-kit/exports`.
- Tạo SVG dùng lúc chạy bằng Illustrator, scale trực tiếp tọa độ của toàn bộ compound path vào canvas 16 × 16 px và wordmark 298 × 50 px; chữ được outline.
- `PatchJar` đọc các file `brand-kit/runtime/mark-16.svg` và `wordmark-298.svg` cho tài nguyên hiển thị và alias tương thích.
- `BrandRenderProbe` kiểm tra bounds thực tế không vượt khung 16 × 16 và 298 × 50. Kết quả: mark **11,8 × 13,0**, wordmark **212,8 × 44,0**.

Icon EXE và avatar không đổi. Không đổi logic persistence hoặc kết nối thiết bị.

## Kiểm chứng

- Probe dùng chính `JavaFxUtil.loadSvg`: đạt giới hạn kích thước mới.
- GUI smoke trên bundle `-03`, root dữ liệu tạm: tiêu đề roastMane, không có nhận diện cũ trong chữ hiển thị, catalog/root thống nhất, socket bị chặn, lưu và đọc lại mẻ mô phỏng 6 kg với 3 điểm: **PASS**.
- Chụp `Scene.snapshot` ở cửa sổ 1600 × 1000; xem trực quan xác nhận thanh bên có bề rộng bình thường và không bị SVG khổng lồ đẩy bố cục. Ảnh không dựng lại bằng HTML hoặc ảnh sinh giả UI.
- Hồi quy persistence đầy đủ đã đạt ở bản `-02`; không chạy lại toàn bộ vì lần này chỉ đổi tài nguyên nhận diện và phép kiểm tra bounds.

Ảnh nguồn và bản Illustrator lưu trong kho ảnh; README chỉ dùng PNG nhỏ tại `docs/assets/app-preview.png`. Chưa thử với máy rang thật.
