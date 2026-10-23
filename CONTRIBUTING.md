# Đóng góp và phát triển roastMane

Bắt đầu từ [DEVELOPMENT](docs/DEVELOPMENT.md), [ARCHITECTURE](docs/ARCHITECTURE.md) và [ROADMAP](docs/ROADMAP.md). Kho đang ở giai đoạn phục hồi source, chưa có pipeline build độc lập toàn bộ ứng dụng.

## Một thay đổi có thể review được

1. Tạo nhánh theo một mục tiêu cụ thể; giữ nguyên raw source trong `extracted-all/` để đối chiếu.
2. Ghi rõ thay đổi thuộc lớp vá hiện hành hay mã dịch ngược. Với source chính, đối chiếu bytecode trước khi sửa artefact decompiler.
3. Dùng dữ liệu tạm và ghi kết quả kiểm tra thực tế. Không xem compile thành công là bằng chứng tương đương hành vi.
4. Với giao diện, kiểm tra cách nạp FXML/CSS, độ tương phản, chữ có dấu và kích thước cửa sổ. Chỉ chụp dữ liệu thử khi cần minh họa.
5. Mô tả trước/sau, các file thay đổi, lệnh kiểm tra và giới hạn trong pull request.

## Quy ước

- Tài liệu và chú thích mới dùng tiếng Việt có dấu, UTF-8; identifier và chuỗi log kỹ thuật giữ tiếng Anh.
- Không đổi package hoặc tên tài nguyên nội bộ hàng loạt: chúng có thể liên quan enum, reflection và ServiceLoader.
- Không commit dữ liệu mẻ thật, token, thông tin đăng nhập, runtime hay binary phát hành vào source.
- Giữ thông báo bản quyền và giấy phép. Bộ nguồn không được tự động coi là mã nguồn mở có thể phân phối lại.
- Không mở kết nối phần cứng trong kiểm thử tự động khi chưa được giao rõ.

## Báo lỗi hoặc đề xuất

Dùng mẫu trong [Issues](https://github.com/TCL-DA/roastMane/issues/new/choose). Ghi bản bundle/commit, cách tái hiện và dữ liệu mô phỏng tối thiểu. Với lỗi biên dịch, kèm tên file, dòng lỗi và JDK; tránh chép toàn bộ log chứa đường dẫn riêng tư hoặc thông tin thiết bị.
