# roastMane — mã khảo sát và bản vá offline

Kho này lưu mã Java dịch ngược để khảo sát cùng mã vá offline và nhận diện roastMane v06. **Chưa phải bộ mã nguồn có thể biên dịch độc lập toàn bộ ứng dụng.**

- `decompiled/`: mã dịch ngược từ JAR Roasting Intelligence 5.13 đã vá, tài nguyên, bản kiểm kê và bản vá trước đây.
- `offline-v06/`: mã launcher, bản vá offline/nhận diện, kiểm thử, script đóng gói và bộ logo v06 đã duyệt.
- `offline-v06/build.ps1`: dựng bản vá và đóng gói từ bundle offline nền có sẵn; không build toàn bộ mã dịch ngược. Cần Windows, JDK 21, bundle nền và các đường dẫn được mô tả trong script.
- `offline-v06/KIEM-THU.md`: kết quả kiểm tra bản chạy v06; chưa kiểm chứng trên máy rang thật.

Mã dịch ngược có thể chứa lỗi hoặc cấu trúc không biên dịch được. Bước khôi phục dự án build độc lập chưa hoàn tất. Không kèm dữ liệu mẻ rang cá nhân, runtime Java hoặc bộ cài ứng dụng.

## Nguồn gốc

Mã gốc và thư viện vẫn thuộc chủ sở hữu tương ứng. Việc dịch ngược, đổi nhận diện và lưu kho không chuyển quyền sở hữu hay cấp quyền phân phối. Giữ nguyên thông báo bản quyền/giấy phép và các định danh nội bộ cần cho tương thích. Xem `offline-v06/PROVENANCE.md` và giấy phép đi kèm tài nguyên.

Kho được tạo riêng tư để lưu và tiếp tục công việc.
