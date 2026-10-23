# Danh mục chức năng roastMane

Danh mục hiển thị trực tiếp trong [README trang chính](../README.md#tinh-nang). Ngày đối chiếu: **24/10/2026**.

## Cách đọc phạm vi

Kiểm kê theo chức năng được tìm thấy trong source ứng dụng, controller, cấu hình, model và lớp offline. Một file Java có thể chứa nhiều hành vi; nhiều file cũng có thể cùng phục vụ một chức năng. Vì vậy số mục mô tả không phải số phép thử hay bằng chứng rằng đã tìm được mọi hành vi có thể có. Không thể xác nhận “100% tính năng hoạt động” chỉ bằng đọc source và compile.

README phân biệt phần đã có phép thử, phần có triển khai nhưng chưa kiểm thử đầu cuối, phần phụ thuộc thiết bị, phần bị giới hạn quyền/module và phần offline không hỗ trợ. Mỗi nhóm có liên kết về source để tiếp tục đối chiếu khi phát triển.

## Bằng chứng và giới hạn

- [Kết quả build và hồi quy](SOURCE-BUILD.md): 677 source ứng dụng + 7 source lớp vá; 0 lỗi compile, 11 cảnh báo deprecated; 4 bộ kiểm thử đạt ngày 23/10/2026.
- [Kiểm thử persistence](../offline-v06/tests/local/PatchedFilePersisterIntegrationTest.java): lưu/đọc lại sau khởi động, chỉnh sửa lỗi, timestamp trùng và ID; dùng dữ liệu mô phỏng.
- [Kiểm thử UI](../tests/local/RecoveredUiTest.java), [launcher](../tests/local/GuiSmokeTest.java) và [nhận diện](../offline-v06/tests/local/BrandRenderProbe.java): phạm vi chọn lọc, không bao phủ mọi màn hình.
- [Bootstrap offline](../offline-v06/patch-src/local/OfflineBootstrap.java), [kết nối local](../offline-v06/patch-src/local/OfflineConnection.java), [persistence](../offline-v06/patch-src/local/OfflinePersistence.java): nguồn quyết định khả năng thực tế của bản offline.
- [Quyền và module](../src/main/java/com/cropster/roastingintelligence/ui/PermissionNotificationController.java): catalog mặc định chỉ có module/quyền rang cơ bản; không mặc nhiên có Goals, Tasks, tạo blend hoặc đánh dấu reference.
- Không có phép thử máy rang thật, cảm biến thật, lệnh điều khiển, máy in thật hoặc dịch vụ cloud. Các connector được liệt kê theo source, không phải danh sách thiết bị đã chứng nhận tương thích.
- Ghi file tạm rồi thay thế giúp tránh nhiều trường hợp file dở dang; archive và outbox chưa có giao dịch nguyên tử chung nhiều file.

## Duy trì danh mục

Khi thêm chức năng: cập nhật mục tương ứng trong README, liên kết implementation, nêu điều kiện sử dụng và bằng chứng kiểm thử. Không nâng trạng thái từ “có source” sang “đã kiểm thử” chỉ vì build thành công. [Chỉ mục source](SOURCE-INDEX.md) giúp tra toàn bộ cây ứng dụng và lớp offline; không bao gồm source thư viện trong `extracted-all/`.
