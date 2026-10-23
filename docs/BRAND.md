# Nhận diện và hình ảnh GitHub

Logo v06 được duyệt và tích hợp vào bản roastMane `-02`. Bộ logo gốc dùng một hình liên tục; không tách chữ hoặc thay bằng monogram tự dựng khác.

| Màu | HEX |
|---|---|
| Xanh | `#254C3B` |
| Be | `#EAE2D2` |
| Đen | `#191D19` |
| Xám | `#737A70` |
| Nâu | `#785440` |
| Trắng | `#FFFFFF` |

- [Logo, avatar và vector](../offline-v06/brand-kit/exports/).
- [Banner README](assets/hero.png): 1600 × 760 px.
- [Thẻ trạng thái thực tế](assets/status.png): 1600 × 606 px, thay bảng "Phần việc" trong README (bảng chữ giữ trong mục gập).
- [Bốn mốc phục hồi source](assets/source-journey.png): 1600 × 580 px, thay bảng "Mốc kiểm chứng".
- [Ảnh chia sẻ liên kết](assets/social-preview.png): 1280 × 640 px. File đã sẵn sàng; việc gắn vào mục **Settings → Social preview** chưa được thực hiện vì phiên trình duyệt không đăng nhập GitHub.

## Phong cách trình bày: Steep, đổi theo v06 (từ 23/10/2026)

Hình GitHub theo kiểu [Steep — serif analytics on warm paper](https://styles.refero.design/style/75fdb89f-ca64-41b3-af36-7a78bd09448e): tiêu đề chữ có chân cỡ lớn, đậm thường (400), một cụm in nghiêng; nền trắng; thẻ bo 20–24 px; nút dạng viên thuốc; nhiều khoảng thở. Bảng màu gốc của Steep được thay bằng màu v06:

| Vai trò | Steep gốc | roastMane |
|---|---|---|
| Chữ chính | `#17191c` | `#44403C` xám than (không dùng chữ đen tuyền) |
| Chữ phụ / nhãn | `#777b86` / `#979799` | `#737A70` / `#8F948C` |
| Nền | `#ffffff` | `#FFFFFF` |
| Thẻ trung tính | `#f2f2f3` | `#F3F3F1` |
| Thẻ nhấn (tối đa một thẻ mỗi hình) | `#fbe1d1` | be `#EAE2D2` |
| Chữ trên thẻ nhấn, nút chính | `#5d2a1a` / `#17191c` | xanh `#254C3B` |
| Nét cong minh họa | `#5d2a1a` | nâu `#785440`, độ mờ 55 % |
| Chữ | Signifier + Söhne | Noto Serif + Segoe UI (Georgia vỡ dấu tiếng Việt) |

Khác Steep có chủ ý: **không đổ bóng** (thẻ nổi dùng viền mảnh `#E6E3DC`), không gradient, không mảng đen. Ảnh README có góc bo trong suốt để không thành tảng vuông trên giao diện tối của GitHub. Số liệu trên hình lấy nguyên từ README; nét cong chỉ là minh họa, không phải dữ liệu mẻ rang. Đổi số liệu thì sửa `make_steep.py`, dựng lại rồi cập nhật cả bảng trong mục gập của README.

Script dựng (`make_steep.py`, HTML → PNG bằng Chrome) lưu tại `F:\Kho-anh\99_Xuat-Photoshop\261023_roastMane_GitHub-Steep`. Bộ cũ nền xanh tối (Illustrator) giữ tại `F:\Kho-anh\99_Xuat-Photoshop\261022_roastMane_GitHub`. Repo chỉ giữ PNG nhỏ cần cho tài liệu; các bản PNG cũng có trong thư mục tập hợp ảnh xuất của kho.

Khi thay nội dung thống kê trên hình, cập nhật đồng thời README và báo cáo tương ứng. Không dùng badge để khẳng định CI, build toàn bộ hoặc kiểm thử phần cứng khi chưa có bằng chứng.
