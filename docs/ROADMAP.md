# Lộ trình roastMane

Lộ trình này tách rõ phần đã có bằng chứng và phần còn cần kiểm thử. Trạng thái chi tiết của lượt dựng hiện hành nằm tại [SOURCE-BUILD.md](SOURCE-BUILD.md).

## Đã hoàn thành ngày 23/10/2026

### Source ứng dụng biên dịch sạch

- [x] Chuyển cây phát triển chính sang `src/main/java/` và `src/main/resources/`.
- [x] Phục hồi 677 source ứng dụng và dùng 7 source overlay hiện hành.
- [x] Biên dịch chung 684 source với `0` lỗi, còn 11 cảnh báo deprecated/removal.
- [x] Loại main JAR và overlay JAR cũ khỏi classpath compile.
- [x] Đối chiếu các sửa lỗi decompiler quan trọng bằng `javap`/CFR.

### Đóng gói từ source

- [x] Tạo main JAR từ class mới cùng 291 resource.
- [x] Tạo `offline-local.jar` từ overlay source.
- [x] Áp dụng nhận diện v06 và tạo icon bằng pipeline hiện có.
- [x] Tạo app-image bằng `jpackage` qua `build-from-source.ps1`.
- [x] Cho phép truyền `-JdkBin`; không buộc duy nhất đường dẫn JDK của máy phát triển.

Output đã xác minh:

```text
C:\Tools\_soi\roastMane-SourceBuild-20261023-01
```

### Hồi quy đã chạy

- [x] Persistence: lưu mới, sửa, restart, archive và giữ đủ điểm đường cong.
- [x] Lỗi ghi khi sửa: giữ file cũ và không chấp nhận xác nhận lưu đã lỗi thời.
- [x] Timestamp trùng: từ chối trước khi cấp ID hoặc thay đổi sequence/archive.
- [x] SVG: mark và wordmark nằm trong khung vật lý quy định.
- [x] UI/FXML: các nhánh `TaskLabel` và `TextFormatter` khi focus.
- [x] Launcher thật: tiêu đề, catalog/root tạm, chặn mạng và lưu mẻ mô phỏng.

Bốn chương trình test trong `test-recovered.ps1` đều đạt exit `0`. Đây là bốn nhóm test thực tế; dự án chưa tuyên bố có CI hoặc độ phủ toàn bộ tính năng.

## Việc tiếp theo

### Mở rộng kiểm thử tính năng

- [ ] Bổ sung test theo hành vi cho các workflow production, preparation, preferences, import/export và phục hồi lỗi.
- [ ] Kiểm tra nhiều vòng lưu/restart với tập dữ liệu lớn hơn và các trường hợp archive/outbox lỗi từng phần.
- [ ] Kiểm tra thêm cửa sổ, FXML, thao tác bàn phím và các nhánh locale/đơn vị.
- [ ] Theo dõi 11 cảnh báo API cũ; chỉ thay API khi có test chứng minh hành vi tương đương.

### Chiến lược dependency

- [ ] Ghi rõ version, license, checksum và nguồn cho từng binary dependency cần thiết.
- [ ] Ưu tiên source upstream đúng version khi cần dựng dependency; chỉ dùng source decompiler để đối chiếu khi không có upstream phù hợp.
- [ ] Quyết định dependency nào tiếp tục được khóa bằng binary và dependency nào cần build lặp lại từ source.
- [ ] Không diễn đạt `decompiled/` hoặc `extracted-all/` là dependency đã được xác minh build.

### Persistence và migration

- [ ] Đánh giá transaction/khôi phục khi outbox đã ghi nhưng archive thất bại.
- [ ] Thiết kế migration riêng nếu cần chuyển dữ liệu từ root cũ; không tự động di chuyển dữ liệu trong build hiện tại.
- [ ] Bổ sung kiểm tra tương thích dữ liệu trước khi thay đổi schema hoặc định dạng file.

### Kiểm thử phần cứng

- [ ] Kiểm thử serial, nguồn đo và máy rang trong một đợt có thiết bị, cấu hình cổng và quy trình an toàn riêng.
- [ ] Xác nhận không tự chọn cổng, có backup cấu hình và có cách dừng thử an toàn.
- [ ] Ghi nhận rõ model máy, firmware, register/protocol và kết quả từng ca thử.

Hiện chưa có kiểm thử phần cứng. Kết quả compile, package và smoke không được dùng để tuyên bố tương thích với mọi máy rang.

## Nguyên tắc duy trì

- Dùng `compile-recovered.ps1 -Target Recovered`, `build-from-source.ps1` và `test-recovered.ps1` làm đường kiểm tra mặc định.
- Giữ sửa đổi source hẹp và có bằng chứng bytecode khi nguyên nhân đến từ decompiler.
Giữ nguyên package và định danh nội bộ để bảo toàn tương thích.
- Không refactor diện rộng hoặc đổi định dạng dữ liệu trong cùng thay đổi với sửa lỗi phục hồi.
- Không kết luận “full source” cho toàn bộ hệ dependency khi các dependency nhị phân chưa được dựng lại.

Số liệu kiểm chứng được lưu trong [source-build-20261023.json](../reports/source-build-20261023.json); các báo cáo baseline cũ chỉ phục vụ lịch sử phục hồi.
