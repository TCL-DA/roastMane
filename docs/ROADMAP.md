# Lộ trình phục hồi roastMane

Lộ trình này ưu tiên khả năng tái dựng và bằng chứng tương đương hành vi. Nó không cam kết tính năng mới hoặc mốc phát hành khi main source chưa biên dịch sạch.

## 1. Khép 65 lỗi của main source

Baseline hiện tại: 677 source, `javac exit 1`, 65 lỗi và 10 cảnh báo trong 23 file.

Thứ tự xử lý:

1. Sửa các biến/local type bị decompiler tái sử dụng sai, bắt đầu từ `CurveChart.java`.
2. Khôi phục biến synthetic mất scope trong `PreferencesWindowController.java`, `JavaFxUtil.java` và `PreferencesStore.java` bằng cách đối chiếu `javap -c -p`.
3. Gắn kiểu listener tường minh cho các lambda JavaFX đang khớp nhiều overload.
4. Sửa generic capture và method reference theo signature bytecode, không dùng raw cast chỉ để làm compiler im lỗi.
5. Chạy lại `compile-recovered.ps1 -Target Main` sau từng nhóm file.

Tiêu chí hoàn thành: 677 source main biên dịch với `exit 0` khi `roastingintelligence-5.13.jar` bị loại khỏi classpath. Cảnh báo còn lại phải được phân loại, không bị che bằng cách đưa JAR cũ trở lại classpath.

## 2. Tạo build main JAR có thể lặp lại

Sau khi main source đạt `javac exit 0`:

- Chốt build tool và version JDK/dependency.
- Đóng gói class cùng CSS, FXML, properties, font và tài nguyên đúng đường dẫn gốc.
- Giữ nguyên license/provenance và các entry `META-INF` cần thiết.
- So sánh danh sách class, public signature và resource với main JAR nền.
- Dùng `offline-v06/patch-src/local/` làm overlay duy nhất; không dùng `decompiled/offline-patch/` lịch sử.

Tiêu chí hoàn thành: tạo được main JAR và `offline-local.jar` từ source bằng một lệnh trong checkout sạch, không phụ thuộc class main đã biên dịch sẵn.

## 3. Xác minh tương đương hành vi bằng dữ liệu tạm

Các gate bắt buộc trước khi gọi là source sẵn sàng phát triển:

- JavaFX mở được với tiêu đề roastMane và không có nhận diện cũ trong text đang hiển thị.
- Catalog, directories và connection dùng cùng root tạm.
- Lưu mới, sửa, restart và archive giữ nguyên ID cùng toàn bộ curve point.
- Nhánh outbox lỗi không tiêu thụ ID và `verifySaved` từ chối dữ liệu chưa được ghi.
- Icon, SVG mark/wordmark và brand-kit khớp asset v06 đã duyệt.
- Socket mạng vẫn bị chặn trong chế độ offline.

Mọi gate trên chạy với `-Doffline.data` trỏ vào `%TEMP%`. Không dùng dữ liệu cá nhân, không chọn serial và không nối máy rang.

## 4. Đối chiếu toàn bộ dependency source

Kho đã trích đủ 117 JAR, nhưng 14.223 file Java dependency chưa được build chung. Công việc còn lại:

- Ưu tiên source upstream đúng version khi thư viện công khai cung cấp, thay vì sửa decompiler output.
- Đối chiếu 24 file Vineflower từng báo lỗi với bản CFR/upstream đã lưu.
- Thiết lập build theo từng dependency hoặc dùng binary dependency có checksum cố định khi mục tiêu chỉ là build ứng dụng.
- Không diễn đạt độ phủ trích xuất là tính đúng đắn của từng method.

Tiêu chí hoàn thành: mỗi dependency có nguồn, version, license và phương án build/binary rõ ràng; `audit.json` tiếp tục khớp hash JAR nguồn.

## 5. Kiểm thử phần cứng là một giai đoạn riêng

Hiện chưa có kiểm thử máy rang, serial hoặc Modbus RTU. Việc này chỉ thực hiện khi người dùng yêu cầu và chuẩn bị đúng máy, cổng, slave/register và quy trình an toàn.

Tiêu chí tối thiểu trước thử máy: full-source build đã qua các gate dữ liệu tạm, backup cấu hình, xác nhận không tự chọn cổng và có kế hoạch dừng thử an toàn.

## Chưa nên làm

- Chưa đổi package nội bộ hoặc tên JAR tương thích.
- Chưa refactor diện rộng UI/model khi bytecode parity chưa có.
- Chưa tự động di chuyển dữ liệu từ root cũ.
- Chưa công bố full-source build, hỗ trợ cloud hoặc tương thích phần cứng khi chưa có phép thử tương ứng.
