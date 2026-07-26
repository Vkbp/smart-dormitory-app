# PHỤ LỤC A: HƯỚNG DẪN SỬ DỤNG HỆ THỐNG SDMS

Tài liệu này hướng dẫn chi tiết các bước vận hành hệ thống dành cho Ban Quản lý và Sinh viên.

---

## A.1. DÀNH CHO BAN QUẢN LÝ (WEB ADMIN)

### A.1.1. Quy trình Mở đợt đăng ký nội trú mới
Đây là bước quan trọng nhất để bắt đầu một kỳ tuyển sinh. Admin thực hiện theo 3 bước sau:

**Bước 1: Khởi tạo thông tin đợt đăng ký**
1. Truy cập menu **"Quản lý đợt đăng ký"** -> Nhấn **"Tạo mới"**.
2. Nhập các thông tin cơ bản:
   - **Tên đợt:** Ví dụ: "Đăng ký KTX Học kỳ 1 - 2024".
   - **Thời gian nộp đơn:** Chọn ngày bắt đầu và ngày kết thúc (Sau ngày này sinh viên không thể nộp đơn).
   - **Thời gian lưu trú:** Chọn ngày sinh viên bắt đầu vào ở và ngày kết thúc hợp đồng.
   - **Loại đăng ký:** Chọn 1 trong 3 loại (Tân sinh viên / SV đang nội trú / Đăng ký tự do).
3. Nhấn **"Lưu tạm"**.

**Bước 2: Chuẩn bị danh sách đủ điều kiện (Chỉ dành cho Tân sinh viên hoặc Đợt tự do)**
*Lưu ý: Nếu chọn loại "SV đang nội trú", bạn có thể bỏ qua bước này.*
1. Nhấn vào nút **"Danh sách đủ điều kiện"** tại đợt vừa tạo.
2. Chuẩn bị file Excel theo đúng cấu trúc quy định (Xem chi tiết tại mục **A.1.4**).
3. Nhấn **"Import Excel"** để tải file lên hệ thống.
4. Kiểm tra xem danh sách hiển thị đã đúng số lượng và thông tin chưa.

**Bước 3: Kích hoạt đợt đăng ký**
1. Quay lại danh sách các đợt đăng ký.
2. Nhấn nút **"Kích hoạt" (Activate)** tại đợt muốn mở.
3. **Kết quả:** Hệ thống sẽ tự động tắt các đợt đăng ký cũ và hiển thị đợt mới này lên trang web dành cho sinh viên.

---

### A.1.2. Quy trình Phê duyệt hồ sơ sinh viên chi tiết
Đây là quy trình thẩm định hồ sơ đa tầng giúp Ban quản lý kiểm soát tính chính xác của dữ liệu đầu vào.

**Bước 1: Tiếp nhận và xem thông tin tổng quan**
1. Truy cập menu **"Kiểm duyệt hồ sơ"**. Các hồ sơ mới nằm ở trạng thái **Chờ duyệt (Pending)**.
2. Nhấn vào một hồ sơ cụ thể để mở trang **Kiểm duyệt hồ sơ chi tiết**.
3. **Kiểm tra thông tin sinh viên:** Xem các thông tin định danh (Họ tên, MSSV, CCCD, Ngày sinh, Địa chỉ, Diện ưu tiên).
4. **Xem vị trí xếp phòng dự kiến:** Hệ thống hiển thị bảng "Xếp phòng dự kiến" (Tòa nhà, Tầng, Phòng, Giường). Đây là vị trí hệ thống đã tự động "giữ chỗ" tạm thời ngay khi sinh viên nộp đơn thành công.

**Bước 2: Đối chiếu tài liệu hệ thống tự động**
1. Tại khu vực **"Tài liệu sinh tự động"**, Admin nhấn "Xem toàn màn hình" hoặc "Tải tài liệu PDF" để kiểm tra:
   - **Phiếu đăng ký (PDF):** Chứa toàn bộ thông tin sinh viên đã khai báo.
   - **Bản cam kết (PDF):** Chứa nội dung cam kết điện tử mà sinh viên đã xác nhận.
2. Việc đối chiếu này giúp đảm bảo dữ liệu trong file văn bản khớp hoàn toàn với dữ liệu khai báo trên hệ thống.

**Bước 3: Thẩm định minh chứng đính kèm**
1. Tại khu vực **"Minh chứng đính kèm"**, hệ thống hiển thị các ảnh: `CCCD_FRONT`, `CCCD_BACK`, `PORTRAIT_PHOTO`.
2. Admin thực hiện kiểm tra từng ảnh:
   - Nếu ảnh rõ nét, đúng loại: Nhấn nút **"Hợp lệ"** (Nút sẽ chuyển màu xanh).
   - Nếu ảnh mờ, sai thông tin: Nhấn nút **"Lỗi sai"** (Nút sẽ chuyển màu đỏ).

**Bước 4: Quyết định xử lý hồ sơ**
Dựa trên kết quả thẩm định, Admin chọn một trong ba nút tác vụ ở góc trên bên phải:
1. **Yêu cầu bổ sung:** Sử dụng khi có ít nhất một tài liệu bị đánh dấu "Lỗi sai". Admin nhập nội dung hướng dẫn sửa đổi và thiết lập hạn chót cập nhật. Hệ thống sẽ gửi email yêu cầu sinh viên tải lại ảnh mới.
2. **Từ chối:** Sử dụng khi sinh viên không đủ điều kiện nội trú. Hệ thống sẽ giải phóng giường đang giữ chỗ và thông báo kết quả từ chối cho sinh viên.
3. **Duyệt hợp lệ:** Sử dụng khi tất cả thông tin và minh chứng đều hợp lệ. 
   - **Kết quả:** Trạng thái chuyển sang **Chờ đóng tiền (Waiting Payment)**. Hệ thống tự động sinh hóa đơn và gửi email thông báo cấp tài khoản App cùng hướng dẫn thanh toán cho sinh viên.

---

### A.1.3. Quy trình Nhận phòng (Check-in) và Quản lý định danh
Sau khi hoàn tất nghĩa vụ tài chính, sinh viên đến KTX để thực hiện thủ tục nhận phòng. Hệ thống hỗ trợ 02 phương thức tiếp nhận linh hoạt:

#### Cách 1: Check-in thủ công tại Web Admin (Dành cho Lễ tân)
1. **Tra cứu:** Cán bộ lễ tân truy cập menu **"Lễ tân check-in"**.
2. **Tìm kiếm:** Sử dụng thanh tìm kiếm theo Họ tên hoặc MSSV để lọc danh sách sinh viên đang ở trạng thái **Chờ nhận phòng**.
3. **Thực hiện:** Nhấn nút **"Check-in"** trên hệ thống. 
4. **Kết quả:** Trạng thái giường chuyển sang **Occupied** (Đang ở). Hệ thống chính thức kích hoạt quyền truy cập của sinh viên.

#### Cách 2: Check-in bằng App Admin (Dành cho Cán bộ di động)
Đây là quy trình hiện đại, sử dụng thiết bị di động để quét thông tin định danh:
1. **Quét mã định danh:** Admin sử dụng camera trên App quét mã QR trên thẻ CCCD của sinh viên hoặc nhập số CCCD thủ công.
2. **Đối soát thông tin:** App hiển thị **Bottom Sheet** chứa ảnh chân dung dung lớn và thông tin phòng/giường đã được xếp chính thức để Admin đối chiếu người thật với hồ sơ.
3. **Gán thẻ RFID:** 
   - Admin nhấn nút **"GÁN THẺ RFID"**.
   - Một hộp thoại hiện ra yêu cầu nhập mã Hex của thẻ từ. Admin thực hiện quẹt thẻ vào đầu đọc để lấy mã hoặc nhập mã thủ công vào hệ thống.
4. **Xác nhận & Giao khóa:** Admin nhấn **"XÁC NHẬN & GIAO KHÓA"**. 
   - **Nghiệp vụ ngầm:** Hệ thống ghi nhận mã RFID vào hồ sơ sinh viên, cho phép sinh viên quẹt thẻ mở cổng IoT ngay lập tức và cập nhật trạng thái cư trú thành công.

---

### A.1.4. Quản lý Hồ sơ sinh viên và Cập nhật định danh
Trong trường hợp cần thay đổi thông tin sau khi đã nhận phòng:
1. **Truy cập:** Menu **"Quản lý sinh viên"** trên Web Admin.
2. **Cập nhật:** Admin chọn sinh viên cụ thể để chỉnh sửa các thông tin cá nhân hoặc gia đình.
3. **Thông tin thẻ kiểm soát:** Admin có thể cập nhật hoặc ghi đè mã RFID mới tại đây nếu sinh viên làm mất thẻ hoặc đổi thẻ mới. Hệ thống sẽ tự động đồng bộ dữ liệu này xuống các đầu đọc cổng kiểm soát.

---

### A.1.5. Quản lý Thanh toán tiền mặt (tại quầy)
Trường hợp sinh viên không sử dụng thanh toán trực tuyến qua QR, Admin thực hiện các bước sau:
1. Truy cập menu **"Quản lý hóa đơn"** (hoặc tìm kiếm hồ sơ sinh viên tại mục Kiểm duyệt).
2. Kiểm tra hóa đơn tương ứng với `Mã hồ sơ` hoặc `MSSV`.
3. Thu tiền mặt trực tiếp từ sinh viên.
4. Nhấn nút **"Thanh toán tiền mặt"** (Approve Cash Payment) trên hệ thống.
5. **Kết quả:** Hệ thống ghi nhận giao dịch với mã `CASH-XXX`, chuyển trạng thái hóa đơn sang **Đã thanh toán** và kích hoạt trạng thái hồ sơ sang **Đã tiếp nhận**.

---

### A.1.6. Quy định về cấu hình file Excel nhập liệu
Để đảm bảo dữ liệu được nhập chính xác, file Excel cần tuân thủ các quy tắc sau:

1. **Định dạng file:** Phải là định dạng `.xlsx` (Excel Workbook).
2. **Cấu trúc cột (Bắt buộc theo thứ tự):**
   - **Cột A (CCCD):** Số căn cước công dân của sinh viên.
   - **Cột B (Full Name):** Họ và tên đầy đủ.
   - **Cột C (Student Code):** Mã số sinh viên (Dùng để đối soát đăng ký).
   - **Cột D (Email):** Email sinh viên (Hệ thống sẽ gửi mã OTP qua email này).
   - **Cột E (Target):** Đối tượng ưu tiên. Sử dụng giá trị `FRESHMAN` (Tân sinh viên) hoặc `ALL` (Các đối tượng khác).

3. **Lưu ý dữ liệu:**
   - Không được để trống cột **Student Code** và **Email**.
   - Email phải là email hợp lệ để nhận được mã xác thực OTP.
   - Hệ thống sẽ tự động bỏ qua (Skip) các dòng bị trùng Mã số sinh viên đã tồn tại trong đợt đó.

---

## A.2. DÀNH CHO SINH VIÊN (WEB PUBLIC)

### A.2.1. Quy trình Đăng ký nội trú trực tuyến
Sau khi Ban Quản lý kích hoạt đợt đăng ký, sinh viên thực hiện nộp đơn theo các bước sau:

**Bước 1: Xác thực danh tính (OTP Verification)**
1. Truy cập trang chủ Web Public.
2. Nhập Email sinh viên (Ví dụ: `dh52201789@student.stu.edu.vn`) và nhấn **"Nhận mã OTP"**.
3. Kiểm tra hộp thư đến của Email đã nhập, lấy mã xác thực gồm 6 chữ số.
4. Nhập mã OTP vào hệ thống để bắt đầu điền đơn.
   - *Lưu ý:* Nếu Email không nằm trong danh sách đủ điều kiện (đã import ở bước Admin), hệ thống sẽ thông báo từ chối quyền đăng ký.

**Bước 2: Khai báo thông tin cá nhân và Gia đình**
Sinh viên điền đầy đủ các thông tin vào biểu mẫu (Hệ thống sẽ tự điền sẵn một số thông tin nếu có trong danh sách đủ điều kiện):
- **Thông tin cơ bản:** Họ tên, Ngày sinh, Giới tính, Số CCCD/CMND (Ngày cấp, Nơi cấp).
- **Thông tin học tập:** Mã số sinh viên, Khoa, Khóa học.
- **Thông tin liên lạc:** Số điện thoại cá nhân, Địa chỉ thường trú, Địa chỉ liên lạc.
- **Thông tin gia đình:** Họ tên, năm sinh, nghề nghiệp và số điện thoại của Cha và Mẹ (Dùng trong trường hợp khẩn cấp).

**Bước 3: Tải lên minh chứng (Documents Upload)**
Sinh viên chụp ảnh và tải lên các giấy tờ bắt buộc (định dạng ảnh rõ nét):
1. Ảnh chân dung (Dùng cho hồ sơ).
2. Mặt trước và mặt sau thẻ CCCD/CMND.
3. Các giấy tờ ưu tiên (Giấy xác nhận hộ nghèo, con thương binh, vùng sâu vùng xa...) nếu có.

**Bước 4: Ký cam kết điện tử và Nộp đơn**
1. Đọc kỹ **Nội quy nội trú** và **Bản cam kết trách nhiệm**.
2. Tích chọn vào ô **"Tôi cam đoan thông tin trên là đúng sự thật và đồng ý tuân thủ nội quy"**.
3. Nhấn nút **"Nộp hồ sơ"**.
4. **Kết quả:** Hệ thống hiển thị thông báo "Nộp đơn thành công" kèm theo **Mã hồ sơ** (Ví dụ: `APP123456`). Sinh viên cần lưu lại mã này để tra cứu kết quả sau này.

---

### A.2.2. Tra cứu và Theo dõi tiến độ hồ sơ
Sau khi nộp đơn thành công, sinh viên có thể sử dụng chức năng "Tra cứu hồ sơ" để theo dõi trạng thái xử lý thời gian thực. Giao diện tra cứu cung cấp cái nhìn toàn diện về hồ sơ bao gồm:

**1. Trạng thái hồ sơ (Status Tracking):**
Hệ thống hiển thị rõ ràng trạng thái hiện tại của đơn:
- **Chờ xét duyệt:** Hồ sơ đã được tiếp nhận thành công và đang chờ Ban quản lý KTX kiểm tra.
- **Yêu cầu bổ sung:** (Xem chi tiết tại mục A.2.3 nếu có tài liệu bị từ chối).
- **Chờ đóng tiền (Waiting Payment):** Hồ sơ đã được duyệt hợp lệ. Sinh viên cần hoàn tất đóng phí nội trú đúng hạn để chính thức giữ chỗ.
- **Đã tiếp nhận (Approved):** Đã hoàn tất thanh toán, hệ thống chốt giữ giường chính thức.

**2. Thông tin xếp phòng nội trú và Quy tắc giữ chỗ:**
Hệ thống cung cấp thông tin "Xếp phòng dự kiến" (Tòa nhà, Tầng, Phòng, Giường). 
- **Quy tắc thời gian:** Sinh viên có **tối đa 03 ngày** kể từ khi hồ sơ được duyệt để hoàn tất thanh toán. 
- **Xử lý quá hạn:** Nếu quá thời hạn 03 ngày mà hệ thống chưa ghi nhận thanh toán, đơn giữ chỗ sẽ bị tự động hủy (`EXPIRED`) để giải phóng giường cho sinh viên khác.

**3. Thông tin thanh toán và Hóa đơn nội trú:**
Dựa trên thời gian lưu trú trong đợt đăng ký, hệ thống tự động tính toán và chia nhỏ hóa đơn (mặc định 03 tháng/đợt thanh toán):
- **Ví dụ:** Nếu thời gian lưu trú là 05 tháng, hệ thống sinh ra 02 hóa đơn (Hóa đơn 1: 03 tháng - 1.050.000 VNĐ; Hóa đơn 2: 02 tháng còn lại - 700.000 VNĐ).
- **Hình thức thanh toán:**
    - **Chuyển khoản (Online):** Quét mã QR SePay (Ngân hàng MBBank). Sinh viên phải sử dụng đúng mã QR hoặc nội dung chuyển khoản chuẩn (Ví dụ: `SDMS8FD5031A`) để hệ thống tự động gạch nợ.
    - **Tiền mặt (tại quầy):** Sinh viên đóng tiền trực tiếp cho Admin tại văn phòng KTX.

**4. Xác nhận Phê duyệt chính thức và Kích hoạt tài khoản:**
Sau khi hệ thống ghi nhận thanh toán thành công (Hóa đơn chuyển sang trạng thái **Đã thanh toán**):
- **Trạng thái hồ sơ:** Chuyển sang **Đã phê duyệt chính thức**.
- **Xếp phòng:** Vị trí giường được chốt giữ chính thức trên hệ thống.
- **Kích hoạt tài khoản:** Sinh viên nhấn nút **"Kích hoạt tài khoản cư dân ngay"** để thiết lập mật khẩu cho ứng dụng di động.

---

### A.2.3. Quy trình kích hoạt tài khoản cư dân nội trú
Đây là bước thiết lập định danh để sinh viên bắt đầu sử dụng App nội bộ.
1. **Thông tin mặc định:** Hệ thống tự động khởi tạo tài khoản với:
   - **Tên đăng nhập:** Mã số sinh viên (MSSV).
   - **Mật khẩu tạm thời:** Mặc định là Mã số sinh viên của bạn.
2. **Thiết lập mật khẩu mới:** Sinh viên nhập Mật khẩu mới (tối thiểu 8 ký tự) và xác nhận lại.
3. **Hoàn tất:** Sau khi kích hoạt, trạng thái tài khoản chuyển sang **ACTIVE**. Sinh viên có thể dùng mật khẩu này để đăng nhập vào **App Student**.

---

### A.2.4. Quy trình bổ sung hồ sơ (Nếu có yêu cầu)
Trong trường hợp hồ sơ có tài liệu không đạt yêu cầu (ảnh mờ, sai thông tin):
1. Sinh viên sẽ thấy trạng thái chuyển thành **"Yêu cầu bổ sung"**.
2. Các tài liệu bị sai sẽ hiển thị nhãn màu đỏ kèm theo **Ghi chú lý do** từ Admin.
3. Sinh viên thực hiện tải lại ảnh mới và nhấn "Cập nhật hồ sơ" để gửi duyệt lại.

---

## A.3. DÀNH CHO SINH VIÊN (APP STUDENT)

### A.3.1. Tải và Đăng nhập ứng dụng
Sau khi đã kích hoạt tài khoản thành công trên Web Public, sinh viên thực hiện các bước sau để sử dụng các tiện ích nội trú:

1. **Tải ứng dụng:** Sinh viên thực hiện cài đặt ứng dụng SDMS Student trên thiết bị di động cá nhân.
2. **Đăng nhập:** 
   - **Tên đăng nhập:** Mã số sinh viên của bạn.
   - **Mật khẩu:** Mật khẩu mới bạn vừa thiết lập tại trang kích hoạt tài khoản.
3. **Trải nghiệm dịch vụ:** Sau khi đăng nhập thành công, sinh viên có thể tiếp cận ngay giao diện trang chủ chứa đầy đủ các tiện ích như: Thông tin phòng ở, Lịch sử ra vào, Thanh toán hóa đơn và Quản lý định danh khuôn mặt AI.
