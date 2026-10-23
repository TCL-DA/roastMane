# roastMane v06 — bản cục bộ ngoại tuyến

Chạy `roastMane.exe` trong thư mục này và giữ nguyên hai thư mục `app`, `runtime`. Ứng dụng lưu dữ liệu mặc định tại `%USERPROFILE%\roastMane\otl-offline`.

Đây là bản vá nhị phân phục vụ thử nghiệm nội bộ, được tạo từ bundle ngoại tuyến đã có. Không phải bản build lại từ toàn bộ mã nguồn. Không tự động di chuyển dữ liệu từ thư mục dữ liệu cũ; muốn thử dữ liệu cũ, hãy sao lưu rồi dùng tùy chọn JVM `-Doffline.data=<đường-dẫn>` trong môi trường kiểm thử.

Các chức năng cloud, đồng bộ, hỗ trợ trực tuyến và cập nhật đã bị vô hiệu hóa. Chưa chọn cổng serial và chưa xác minh trên máy rang thật. Không flash firmware hay tự kết nối phần cứng.

Mã patch, script dựng lại và bộ nhận diện v06 nằm trong `source`. Chạy `source\build.ps1` từ PowerShell với bundle offline gốc làm đầu vào. Script kiểm tra SHA-256 của hai asset chính trước khi dựng và từ chối ghi đè đầu ra nếu không truyền `-Force`.

Biểu tượng cửa sổ và EXE dùng avatar xanh/be 1024 px đã duyệt. Logo trong giao diện dùng đúng hình học `monogram_xanh.svg`, ghép với chữ roastMane. Bộ `source\brand-kit` giữ đủ sáu màu SVG/PNG/AI/PDF, ba avatar và hình học nguồn để dựng lại có thể kiểm tra được.

Package Java và một số tên file thư viện nội bộ vẫn giữ tên kỹ thuật cũ để bảo toàn liên kết nhị phân.
