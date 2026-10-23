# Nguồn gốc và thông báo pháp lý

Từ ngày 23/10/2026, repository có thêm cây ứng dụng được phục hồi tại `src/main/java/` và pipeline dựng main + overlay từ source. Đây là mã tái dựng từ bytecode, không phải source gốc do nhà cung cấp phát hành. Các thư viện bên thứ ba, JavaFX và runtime vẫn dùng binary dependency; quyền sở hữu và điều khoản dưới đây không thay đổi.

`roastMane` là lớp nhận diện và launcher cục bộ đặt trên ứng dụng Java/JAR có nguồn gốc Cropster Roasting Intelligence 5.13. Bản vá không chuyển quyền sở hữu mã gốc, không xóa thông báo bản quyền/giấy phép trong JAR và không tuyên bố được Cropster bảo trợ.

Các package `org.cropster.*`, `com.cropster.*`, tên một số JAR và định danh cấu hình nội bộ được giữ nguyên vì đổi chúng có thể phá vỡ liên kết nhị phân, ServiceLoader, dữ liệu JSON và cấu hình thiết bị. Chúng là dấu vết kỹ thuật về nguồn gốc, không phải nhãn hiển thị cho người dùng.

Thư viện bên thứ ba được giữ nguyên tên và nội dung. Giấy phép SIL Open Font License đi kèm font NotoMono vẫn nằm trong JAR chính tại `fonts/SIL Open Font License.txt`. Các điều khoản của từng thư viện và phần mềm gốc vẫn áp dụng.

Không có địa chỉ email, trang web hỗ trợ hoặc dịch vụ cloud giả mang tên roastMane. Các lối vào hỗ trợ, cập nhật và đồng bộ trực tuyến bị vô hiệu hóa trong bản ngoại tuyến.

Bộ nhận diện cá nhân v06 được sao chép nguyên vẹn từ thư mục đã được người dùng duyệt trong `F:\Kho-anh\99_Xuat-Photoshop\261022_Dau-rieng_Logo-v06_Tinh-chinh`. Build không sửa các file nguồn trong kho ảnh.
