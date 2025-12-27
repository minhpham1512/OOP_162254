QuanLyNganHang - Hệ Thống Quản Lý Ngân Hàng (OOP Demo)
Dự án QuanLyNganHang là một ứng dụng Desktop được xây dựng bằng Java và thư viện Swing, minh họa các nguyên lý Lập trình Hướng đối tượng (OOP) và kiến trúc phần mềm phân lớp (Layered Architecture). Ứng dụng mô phỏng các hoạt động ngân hàng cơ bản cho khách hàng và công tác quản trị cho admin.
🚀 Tính Năng ChínhHệ thống phân chia quyền hạn rõ ràng giữa Khách hàng (Customer) và Quản trị viên (Admin).
👤 Dành Cho Khách Hàng (Customer)
- Đăng ký & Đăng nhập: Tạo tài khoản mới và đăng nhập vào hệ thống.
- Quản lý Tài khoản: Xem danh sách tài khoản, số dư hiện tại và thông tin thẻ liên kết (Credit/Debit).
- Chuyển tiền: Chuyển tiền từ tài khoản này sang tài khoản khác trong hệ thống.
- Nạp tiền: Nạp tiền vào tài khoản thông qua nhiều phương thức (Chuyển khoản, Thẻ, Tiền mặt...).
- Vay tiền:Tạo đơn vay mới (Vay tín chấp, mua nhà, mua xe...).
- Xem danh sách khoản vay và trạng thái.
- Thanh toán khoản vay (một phần hoặc toàn bộ).
- Lịch sử Giao dịch: Xem lại chi tiết các giao dịch nạp, rút, chuyển tiền.
- 👨‍💼 Dành Cho Quản Trị Viên (Admin)
- Dashboard Tổng quan: Xem thống kê tổng số khách hàng, tổng số tài khoản và tổng số dư toàn hệ thống.
- Quản lý Khách hàng: Xem danh sách toàn bộ khách hàng và thông tin chi tiết.
- Quản lý Tài khoản & Giao dịch: Giám sát toàn bộ tài khoản và nhật ký giao dịch của hệ thống.
🏗️ Kiến Trúc Hệ Thống
Dự án áp dụng mô hình phân lớp để tách biệt logic nghiệp vụ và giao diện:
- View (Giao diện): Các lớp trong com.bank.view (như BankGUI, AdminDashboard) xây dựng giao diện người dùng bằng Java Swing.
- Controller/Service (Nghiệp vụ): Các lớp trong com.bank.service (như AccountService, AuthService) xử lý logic chuyển tiền, tính toán lãi suất, xác thực.
- Model (Dữ liệu): Các lớp POJO trong com.bank.model (như User, Account, Loan) định nghĩa cấu trúc dữ liệu.
- Repository (Lưu trữ): Lớp DatabaseSimulator trong com.bank.repository giả lập cơ sở dữ liệu bằng cách lưu trữ dữ liệu trong bộ nhớ (In-memory HashMap).
🛠️ Cài Đặt & Chạy Ứng Dụng
- Yêu cầuJava Development Kit (JDK) 8 trở lên.IDE (IntelliJ IDEA, Eclipse, NetBeans) hoặc Terminal.
- Hướng dẫnClone repository hoặc tải mã nguồn về máy.Mở dự án trong IDE của bạn.
- Tìm đến file src/com/bank/Main.java.Chạy phương thức main() để khởi động ứng dụng.
🔑 Tài Khoản Demo
- Hệ thống đã được nạp sẵn dữ liệu mẫu để bạn trải nghiệm ngay lập tức:Vai TròEmailMật KhẩuGhi ChúAdminadmin@bank.comadminQuản trị viên hệ thốngUseralice@bank.com123Khách hàng có sẵn tiền & thẻUserbob@bank.com123Khách hàng khác để test chuyển khoản📂 Cấu Trúc Thư MụcPlaintextsrc/com/bank/
├── controller/        # (Optional) Các lớp điều khiển
├── model/             # Các thực thể: User, Account, Card, Loan...
├── repository/        # Giả lập Database (DatabaseSimulator)
├── service/           # Logic nghiệp vụ: AuthService, AccountService...
├── view/              # Giao diện Swing: BankGUI, AdminDashboard...
└── Main.java          # Điểm khởi chạy ứng dụng
