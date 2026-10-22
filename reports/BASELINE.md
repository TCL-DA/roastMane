# Baseline biên dịch source dịch ngược — 22/10/2026

## Phạm vi đúng của main application

- Nguồn: bản sao `C:\Tools\_soi\roastMane-github\decompiled`.
- Main JAR nguồn có SHA-256 `65EBB160886A2EE4E33B77449A6A411B9A0ECF90AD968ED82DAAE88BCE4D2D2C`.
- JDK: Eclipse Adoptium 21.0.12.101.
- Dependency: các JAR của `roastMane-Offline-OTL-20261022-02\app`, nhưng loại chính `roastingintelligence-5.13.jar` khỏi classpath để class cũ không che lỗi source.
- JavaFX được nạp bằng module path và các `--add-exports` giống launcher phát hành.
- Loại `src/offline-patch/` khỏi main target vì đây là source lịch sử được nhúng như tài nguyên, không phải class của main JAR.

Kết quả main baseline:

```text
source files: 677
javac exit: 1
errors: 65
warnings: 10
files with errors: 23
```

Log baseline đầy đủ: `javac-app-baseline.txt`. Lượt tái lập bằng script bàn giao: `javac-main-current.txt`, `javac-main-current.args` và `javac-main-current.exitcode.txt`. Bảng đếm theo file: `errors-by-file.csv`.

## Nhóm lỗi

| Nhóm chẩn đoán đầu tiên | Số lỗi |
|---|---:|
| Biến hoặc method synthetic bị mất (`cannot find symbol`) | 29 |
| Kiểu generic/local variable bị suy luận sai (`incompatible types`) | 22 |
| Lambda khớp nhiều overload JavaFX (`ambiguous`) | 11 |
| Lời gọi collection sai kiểu (`no suitable method`) | 2 |
| Lambda bị tái dựng sai cú pháp/ngữ cảnh | 1 |

Các file tập trung nhiều lỗi nhất là `CurveChart.java` (12), `PreferencesWindowController.java` (12), `JavaFxUtil.java` (9), `PreferencesStore.java` (4) và `PreparationPanel.java` (3). Ví dụ rõ nhất là biến `NumberFormat durationFormat` bị decompiler tái sử dụng cho một `CombinedDomainXYPlot`, biến synthetic `rec$`/`channel` mất khai báo, và lambda `addListener` mất cast kiểu listener.

Đây là artefact tái dựng source, không phải 65 dependency bị thiếu: classpath và JavaFX module access đã được giải quyết trước khi lấy số liệu này.

## Source offline v06 hiện hành

`offline-v06/` được sao chép từ `roastMane-Offline-Source-20261022-v06\patch-src\local`, không lấy bộ `offline-patch` cũ trong main JAR.

```text
source files: 7
javac exit: 0
class files: 15
warnings: 1 (AppModel.getDirectories() đã deprecated)
```

## Kết luận giới hạn

Main source dịch ngược chưa đạt full-source build. Việc sửa 65 lỗi cần đối chiếu bytecode từng method để tránh thay đổi hành vi; không nên áp dụng thay thế regex hàng loạt. Runtime bundle `-02` đã được kiểm thử từ bytecode phát hành, nhưng kết quả đó không chứng minh source dịch ngược có thể tái dựng tương đương.
