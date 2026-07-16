# BÁO CÁO KỸ THUẬT: THÔNG SỐ CHI TIẾT LINH KIỆN VÀ TÀI LIỆU PHÁT TRIỂN HỆ THỐNG

Tài liệu này tổng hợp toàn bộ thông số kỹ thuật, sơ đồ tính năng và các lưu ý chuyên sâu dành cho đội ngũ lập trình phần mềm (Backend/Firmware) và bộ phận lắp ráp phần cứng phục vụ dự án hệ thống khóa cửa bảo mật tích hợp nhận diện hình ảnh, mã số và thẻ từ từ xa.

---

## I. DANH SÁCH CHI TIẾT VÀ THÔNG SỐ KỸ THUẬT PHẦN CỨNG

### 1. Khối Xử Lý Trung Tâm: Mạch Phát Triển ESP32 NodeMCU (30-Pin)

Bo mạch này đóng vai trò là trung tâm điều khiển (Master MCU), chịu trách nhiệm quét ma trận phím, đọc dữ liệu thẻ RFID, điều khiển màn hình LCD hiển thị và xuất xung PWM điều khiển động cơ khóa.

* **Vi điều khiển chính:** Chip SoC ESP-WROOM-32.
* **Kiến trúc CPU:** Xtensa® lõi kép (Dual-core) 32-bit LX6, xung nhịp cấu hình từ 160 MHz đến 240 MHz mang lại năng lực xử lý tác vụ song song (FreeRTOS) cực kỳ mượt mà.
* **Bộ nhớ:** 4MB Flash (lưu trữ chương trình và phân vùng SPIFFS/LittleFS) và 520KB SRAM.
* **Kết nối không dây:** * Wi-Fi: Chuẩn 802.11 b/g/n (tốc độ lên tới 150 Mbps), băng tần 2.4 GHz.
* Bluetooth: Phiên bản v4.2 BR/EDR và BLE (Bluetooth tiết kiệm năng lượng).


* **Giao tiếp ngoại vi:** Hỗ trợ đầy đủ các chuẩn giao tiếp phần cứng bao gồm SPI, I2C, UART, và các chân xuất xung PWM.
* **Giao tiếp lập trình:** Tích hợp sẵn cổng cắm Micro-USB kết nối với IC chuyển đổi giao tiếp (thường là CP2102 hoặc CH340) giúp nạp code trực tiếp và debug qua Serial Monitor dễ dàng. Có sẵn nút cứng **EN** (Reset) và **BOOT** (Flash mode).
* **Điện áp hoạt động:**
* Cấp nguồn qua cổng USB: 5V DC.
* Cấp nguồn qua chân **VIN**: Chấp nhận điện áp từ 5V đến 9V DC (mạch tích hợp IC ổn áp hạ xuống 3.3V).
* Điện áp logic của các chân I/O: **3.3V** (Lưu ý quan trọng cho phần cứng: Không cấp tín hiệu logic 5V trực tiếp vào các chân GPIO của ESP32 để tránh gây hỏng chip).



### 2. Khối Nhận Diện Hình Ảnh: Module ESP32-CAM

Module bổ trợ độc lập chuyên trách cho các tác vụ xử lý hình ảnh, truyền phát luồng video (Video Streaming) qua mạng Wi-Fi hoặc thực hiện nhận diện khuôn mặt cơ bản.

* **Lõi xử lý:** Chip ESP32-S công suất thấp kết hợp thêm bộ nhớ mở rộng **8MB PSRAM** để đảm bảo đủ không gian lưu trữ và xử lý các khung hình độ phân giải cao.
* **Cảm biến Camera đi kèm:** Cảm biến ảnh OV2640 độ phân giải 2 Megapixel kết nối qua cáp FPC dẹt (mã cáp trên hình: `HDF3M-811-V1 T`). Hỗ trợ xuất các định dạng ảnh JPEG, BMP, Grayscale.
* **Đèn trợ sáng:** Tích hợp sẵn một đèn LED Flash công suất lớn ở góc dưới vi mạch phục vụ chiếu sáng khi chụp ảnh hoặc ghi hình vào ban đêm.
* **Lưu trữ mở rộng:** Có khe cắm thẻ nhớ MicroSD ở mặt sau, hỗ trợ đọc/ghi dữ liệu hình ảnh cục bộ.
* **Lưu ý ràng buộc GPIO đối với phần mềm:** Phần lớn các chân GPIO của ESP32-CAM đã được gán cố định cho việc giao tiếp với Camera và PSRAM bên trong. Do đó, các chân trống lộ ra ngoài rất hạn chế (chỉ còn khoảng vài chân khả dụng như GPIO 4 dùng cho đèn flash, GPIO 0 dùng cấu hình boot). Module này nên được lập trình để hoạt động độc lập và giao tiếp với mạch NodeMCU qua chuẩn truyền thông UART hoặc qua giao thức mạng (HTTP/MQTT).

### 3. Khối Nhận Diện Thẻ Từ: Module RFID-RC522 & Thẻ Xác Thực

Thiết bị đọc thông tin thẻ không tiếp xúc phục vụ tính năng quẹt thẻ mở cửa.

* **Chip điều khiển:** MFRC522.
* **Tần số sóng vô tuyến:** Tần số ngắn 13.56 MHz.
* **Chuẩn giao tiếp với MCU:** Giao thức SPI (bao gồm các chân chức năng: `SDA/SS`, `SCK`, `MOSI`, `MISO`, `RST`).
* **Khoảng cách đọc tối ưu:** Từ 2 cm đến 5 cm tùy thuộc vào cấu trúc ăng-ten của loại thẻ từ.
* **Điện áp cấp nguồn:** **3.3V DC** (Tuyệt đối không nối vào đường nguồn 5V). Dòng tiêu thụ cực kỳ thấp, chỉ từ 13 – 26mA khi hoạt động.
* **Vật tư định danh kèm theo:** * 01 Thẻ từ móc khóa (Keyfob) màu xanh bằng nhựa ABS chống nước.
* 01 Thẻ từ trắng dạng thẻ nhựa ISO kích thước tiêu chuẩn ($85.6mm \times 54mm \times 0.8mm$).
* Cả hai thẻ đều sử dụng chip chuẩn Mifare 1K (S50) tần số 13.56 MHz, lưu trữ ID cố định duy nhất (UID) để backend thực hiện đối sánh phân quyền.



### 4. Giao Diện Nhập Liệu: Bàn Phím Ma Trận Phím Màng 4x4

Hệ thống nhập chuỗi ký tự phục vụ chức năng bảo mật bằng mật mã (Password/PIN).

* **Cấu trúc phím:** Gồm 16 phím bấm vật lý được sắp xếp theo cấu trúc ma trận gồm 4 Hàng (Rows) và 4 Cột (Columns). Các phím ký tự bao gồm: Từ `0` đến `9`, các chữ cái `A`, `B`, `C`, `D`, và ký tự đặc biệt `*`, `#`.
* **Dạng vật liệu:** Phím màng (Membrane Keypad) siêu mỏng, dẻo, có tích hợp sẵn keo xốp hai mặt ở mặt sau giúp dán trực tiếp lên bề mặt vỏ hộp hoặc cánh cửa.
* **Cơ chế kết nối:** Gồm một cáp bẹt mềm với đầu ra là thanh cắm cái 8 chân (khoảng cách chân tiêu chuẩn 2.54mm). 4 chân đầu đại diện cho các hàng, 4 chân sau đại diện cho các cột.
* **Thông số tải:** Điện áp tối đa 35V DC, dòng hoạt động tối đa 100mA.

### 5. Giao Diện Hiển Thị: Màn Hình LCD 1602 Tích Hợp Module Chuyển Đổi I2C

Khối hiển thị thông tin cục bộ hướng dẫn người dùng thao tác và thông báo trạng thái của khóa cửa.

* **Màn hình hiển thị LCD 1602:** Cho phép hiển thị đồng thời 2 dòng văn bản, mỗi dòng chứa tối đa 16 ký tự mã ASCII. Màn hình có tấm nền màu xanh lá với đèn nền tương phản cao.
* **Mạch chuyển đổi giao tiếp I2C đính kèm:** Mạch PCB màu đen được hàn cố định trực tiếp phía sau màn hình, sử dụng chip mở rộng IO mã **PCF8574**.
* Vai trò: Thu gọn số lượng dây kết nối từ tối thiểu 8 dây tín hiệu của màn hình gốc xuống chỉ còn duy nhất **2 dây tín hiệu giao tiếp I2C**.
* Địa chỉ I2C mặc định: Thường nằm trong dải `0x27` hoặc `0x3F` (Cần lưu ý cho lập trình viên quét địa chỉ I2C chính xác trong code).
* Các chân kết nối đầu ra (4 chân): `GND` (Nguồn âm), `VCC` (Cấp nguồn 5V DC để đảm bảo độ sáng màn hình), `SDA` (Đường truyền dữ liệu I2C), `SCL` (Đường cấp xung nhịp clock I2C).
* Tiện ích phần cứng: Có một biến trở màu xanh dương để điều chỉnh thủ công độ tương phản (Contrast) hiển thị chữ và một chân Jump cắm để bật/tắt đèn nền LCD bằng phần mềm.



### 6. Khối Cơ Cấu Chấp Hành: Động Cơ Servo TowerPro MG90S

Cơ cấu chuyển động cơ học để trực tiếp gạt chốt khóa hoặc đóng/mở lẫy cửa.

* **Vật liệu hệ thống truyền động:** Toàn bộ hệ bánh răng bên trong được làm bằng **kim loại** (Metal Gears), mang lại độ bền và khả năng chịu tải, chống mài mòn vượt trội so với phiên bản SG90 bánh răng nhựa.
* **Góc quay giới hạn:** Từ 0 độ đến 180 độ.
* **Lực kéo (Stall Torque):** Đạt 1.8 kg·cm (ở điện áp 4.8V) và tăng lên tới 2.2 kg·cm (ở điện áp 6.0V).
* **Tốc độ phản hồi chuyển động:** 0.1 giây / 60 độ (ở nguồn cấp 4.8V).
* **Quy ước màu dây tín hiệu:**
* **Dây màu Cam:** Dây nhận tín hiệu điều khiển (Cần nối vào chân có tính năng xuất xung PWM của vi điều khiển).
* **Dây màu Đỏ:** Dây cấp nguồn dương (VCC, khuyến nghị nối vào đường nguồn độc lập 5V để tránh sụt áp hệ thống khi động cơ kéo tải).
* **Dây màu Nâu:** Dây cấp nguồn âm (GND).



### 7. Vật Tư Phụ Trợ Lắp Ráp & Thử Nghiệm

* **Bo mạch phíp lỗ hàn thử nghiệm (Prototyping PCB):** Tấm phíp giấy màu cam chịu nhiệt với các lỗ đồng độc lập khoảng cách chân chuẩn 2.54mm. Trên bo mạch này đã được hàn sẵn hai cụm hàng rào pin cái (Header Female), đóng vai trò làm đế cắm cố định cho các mạch phát triển SoC. Thiết kế này giúp kỹ sư lắp ráp dễ dàng cắm ráp hoặc rút mạch ra để thay thế, bảo trì mà không cần rã mối hàn.
* **Túi dây cắm kết nối (Jumper Wires):** Tập hợp các dây cáp lõi đồng nhiều màu sắc, có trang bị sẵn các đầu cos cắm loại Cái - Cái (Female to Female) và Đực - Cái (Male to Female), chiều dài tiêu chuẩn khoảng 20cm, dùng để kết nối nhanh tín hiệu giữa các module ngoại vi trong giai đoạn chạy thử nghiệm phần mềm.

---

## II. HƯỚNG DẪN KẾT NỐI PHẦN CỨNG (Tham Chiếu Cho Đội Lắp Ráp)

Để đảm bảo hệ thống vận hành ổn định và không làm quá tải mạch ổn áp nội tại của ESP32 NodeMCU, bộ phận lắp ráp cần tuân thủ cấu trúc phân phối nguồn và kết nối tín hiệu như sau:

1. **Phân Phối Nguồn Điện (Power Rail):**
* Đường nguồn **5V Bus** (lấy từ chân VIN khi cấp nguồn ngoài hoặc chân 5V từ cổng USB): Cấp cho chân VCC của **LCD 1602** và dây Đỏ của **Động cơ Servo MG90S**. *(Lưu ý: Nếu động cơ hoạt động liên tục gây hiện tượng sụt nguồn làm reset ESP32, bắt buộc phải tách riêng một nguồn 5V rời cho động cơ và nối chung đất GND với ESP32).*
* Đường nguồn **3.3V Bus** (lấy từ chân 3V3 của NodeMCU): Chỉ cấp cho chân VCC của module **RFID-RC522**.


2. **Giao Tiếp Tín Hiệu Ngoại Vi:**
* **Bàn phím 4x4:** Kết nối 8 chân đầu ra vào 8 chân GPIO thông thường bất kỳ trên ESP32 NodeMCU (cấu hình trong code làm các chân quét Hàng và Cột).
* **LCD 1602 I2C:** Kết nối chân `SDA` và `SCL` vào cặp chân phần cứng I2C mặc định của ESP32 (Thông thường là GPIO 21 - SDA và GPIO 22 - SCL).
* **RFID-RC522:** Kết nối vào cụm chân giao tiếp SPI mặc định của ESP32 (Thông thường là GPIO 23 - MOSI, GPIO 19 - MISO, GPIO 18 - SCK, phối hợp cùng 2 chân GPIO bất kỳ làm chân Chọn chip `SDA/SS` và chân Reset `RST`).
* **Servo MG90S:** Dây tín hiệu màu cam kết nối vào một chân GPIO có khả năng xuất xung PWM trên ESP32.



---

## III. ĐỊNH HƯỚNG LẬP TRÌNH VÀ PHÁT TRIỂN PHẦN MỀM (Dành Cho Lập Trình Viên Backend/Firmware)

### 1. Thư Viện Khuyến Nghị Sử Dụng (Môi Trường Arduino IDE / VS Code PlatformIO)

* Lập trình Màn hình: Thư viện `LiquidCrystal_I2C`.
* Lập trình Bàn phím: Thư viện `Keypad` (Cấu hình ánh xạ mảng hai chiều $4 \times 4$ chứa các ký tự từ '0' -> 'D').
* Lập trình RFID: Thư viện `MFRC522`.
* Lập trình Servo: Thư viện `ESP32Servo` (Thư viện Servo tiêu chuẩn của Arduino cũ không tương thích với kiến trúc PWM của chip ESP32).

### 2. Quy Trình Xử Lý Logic Của Firmware (State Machine)

* **Trạng thái Chờ (Idle):** Màn hình LCD hiển thị thông báo yêu cầu quẹt thẻ hoặc nhập mã pin. Hệ thống liên tục quét tín hiệu từ module RFID và quét ma trận phím.
* **Xử lý Xác thực (Authentication):**
* *Trường hợp Quẹt thẻ:* Đọc chuỗi mã UID từ thẻ RFID qua bus SPI -> So khớp chuỗi UID này với danh sách thẻ hợp lệ được lưu trong bộ nhớ (hoặc gửi request kiểm tra về Database cục bộ/Server).
* *Trường hợp Nhập mã:* Lưu trữ các ký tự nhấn từ bàn phím màng vào một bộ đệm (Buffer). Khi người dùng nhấn phím kết thúc (ví dụ phím `#`), tiến hành mã hóa và so sánh chuỗi mật mã vừa nhập.


* **Cơ chế Chấp Hành (Action):** Nếu thông tin xác thực chính xác, kích hoạt chân PWM xuất xung dịch chuyển góc quay của Servo MG90S từ 0 độ sang 90/180 độ để mở chốt khóa, đồng thời hiển thị "Cửa Đã Mở" lên LCD trong một khoảng thời gian thiết lập sẵn (ví dụ 5 giây) trước khi điều khiển Servo quay về vị trí khóa ban đầu. Nếu sai, hiển thị cảnh báo và đếm số lần nhập sai để khóa hệ thống tạm thời nếu cần.

### 3. Giải Pháp Tích Hợp Hệ Thống Kết Hợp ESP32-CAM

Vì bo mạch ESP32 NodeMCU đóng vai trò làm bộ xử lý trung tâm cho toàn bộ thiết bị ngoại vi, mạch ESP32-CAM nên được cấu hình hoạt động theo mô hình phối hợp:

* **Phương án 1 (Giao tiếp có dây):** ESP32-CAM kết nối với NodeMCU qua chuẩn nối tiếp UART (hai dây Tx/Rx). Khi NodeMCU phát hiện có người nhập sai mật khẩu quá 3 lần hoặc bấm chuông, nó sẽ gửi lệnh qua UART yêu cầu ESP32-CAM chụp một bức ảnh, lưu vào thẻ nhớ MicroSD hoặc đẩy thẳng dữ liệu hình ảnh lên server.
* **Phương án 2 (Kết nối không dây qua Network):** Cả hai mạch độc lập cùng kết nối vào một mạng Wi-Fi nội bộ. Giao tiếp giữa hai mạch được thực hiện thông qua giao thức mạng nhẹ như **MQTT** hoặc thiết lập các lệnh gọi **HTTP POST/GET Request**. Mạch ESP32-CAM đóng vai trò là một Camera IP liên tục truyền phát luồng video về ứng dụng quản lý hoặc thực hiện xử lý ảnh khi nhận được tín hiệu trigger không dây từ mạch Master NodeMCU.