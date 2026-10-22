# roastMane — mã khảo sát và bản vá offline

Kho này lưu mã Java dịch ngược để khảo sát cùng mã vá offline và nhận diện roastMane v06. **Chưa phải bộ mã nguồn có thể biên dịch độc lập toàn bộ ứng dụng.**

- `decompiled/`: mã dịch ngược từ JAR Roasting Intelligence 5.13 đã vá, tài nguyên, bản kiểm kê và bản vá trước đây.
- `offline-v06/`: mã launcher, bản vá offline/nhận diện, kiểm thử, script đóng gói và bộ logo v06 đã duyệt.
- `extracted-all/`: trích xuất đủ 117 JAR của bản roastMane v06, gồm thư viện và JavaFX, tách theo từng JAR để tránh ghi đè class/tài nguyên trùng tên. Bao gồm biến thể `META-INF/versions`, giấy phép, bản kiểm kê SHA-256 và bản đối chiếu CFR cho 24 file Vineflower báo lỗi. OkHttp/Okio có thêm source upstream đúng phiên bản trong `upstream-sources/`.
- `tools/`: script trích xuất, xử lý biến thể theo phiên bản Java, đối chiếu CFR và kiểm toán độ phủ. Cần cung cấp đường dẫn JDK, bộ dịch ngược và bundle nền.
- `compile-recovered.ps1` và `reports/`: phép thử biên dịch tái lập. Main có 677 source, còn 65 lỗi/10 cảnh báo trong 23 file; phần vá v06 biên dịch được 7 source thành 15 class.
- `offline-v06/build.ps1`: dựng bản vá và đóng gói từ bundle offline nền có sẵn; không build toàn bộ mã dịch ngược. Cần Windows, JDK 21, bundle nền và các đường dẫn được mô tả trong script.
- `offline-v06/KIEM-THU.md`: kết quả kiểm tra bản chạy v06; chưa kiểm chứng trên máy rang thật.

Mã dịch ngược có thể chứa lỗi hoặc cấu trúc không biên dịch được. Bước khôi phục dự án build độc lập chưa hoàn tất. Không kèm dữ liệu mẻ rang cá nhân, runtime Java hoặc bộ cài ứng dụng.

Kiểm toán `extracted-all/audit.json` đối chiếu mọi class với file Java bao ngoài, không coi class lồng nhau là file nguồn riêng. Có file nguồn tương ứng không đồng nghĩa mọi phương thức đã được khôi phục chính xác. Xem `fallback-report.json` cho lỗi dịch ngược và bản đối chiếu; một đoạn `okhttp3/internal/Util` vẫn báo lỗi trong CFR nhưng có bản Kotlin upstream trong bộ nguồn bổ sung. Native DLL/SO không thể khôi phục thành Java: các tệp đó được kiểm kê và không đưa binary vào Git. `runtime` JDK và launcher EXE không phải mã nguồn Java của ứng dụng và không nằm trong phạm vi dịch ngược này.

## Nguồn gốc

Mã gốc và thư viện vẫn thuộc chủ sở hữu tương ứng. Việc dịch ngược, đổi nhận diện và lưu kho không chuyển quyền sở hữu hay cấp quyền phân phối. Giữ nguyên thông báo bản quyền/giấy phép và các định danh nội bộ cần cho tương thích. Xem `offline-v06/PROVENANCE.md` và giấy phép đi kèm tài nguyên.

Kho được tạo riêng tư để lưu và tiếp tục công việc.
