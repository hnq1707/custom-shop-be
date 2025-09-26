# Project Guidelines

🛠️ Coding Agent Guideline – Backend Bán Áo In Ảnh (Spring Boot + MongoDB)
1. Công nghệ chính

Java 17

Spring Boot 3.x

Spring Data MongoDB

Spring Security (cơ bản) – chỉ cần xác thực cơ bản để tích hợp với NextAuth (FE)

Lưu trữ file local: ảnh AI được lưu vào uploads/designs/

AI integration: OpenAI DALL·E hoặc HuggingFace Stable Diffusion

Translate: OpenAI GPT hoặc Google Translate API

2. Entities (MongoDB @Document)
   User
   @Document("users")
   class User {
   @Id String id;
   String name;
   String email;
   String password; // cho NextAuth FE
   String phone;
   String address;
   String role; // USER | ADMIN
   }

Product
@Document("products")
class Product {
@Id String id;
String type; // PLAIN | UPLOAD | AI
String size; // S, M, L, XL
String designUrl; // null nếu là áo trắng
Double price;
LocalDateTime createdAt;
}

Order
@Document("orders")
class Order {
@Id String id;
String userId;
List<OrderItem> items;
String status; // PENDING | SHIPPING | COMPLETED
Double totalPrice;
String shippingAddress;
PaymentInfo paymentInfo;
LocalDateTime createdAt;
}

class OrderItem {
String productId;
String size;
String type;
String designUrl;
Integer quantity;
Double price;
}

class PaymentInfo {
String method;   // COD, Paypal...
String txnId;
}

3. API Endpoints
   Auth

POST /api/auth/register → đăng ký user mới

POST /api/auth/login → login (đơn giản, FE NextAuth sẽ gọi)

User

GET /api/users/me → trả về thông tin user đang đăng nhập

Product

POST /api/products (ADMIN) → thêm sản phẩm mới

GET /api/products → danh sách sản phẩm

Design (AI)

POST /api/designs/generate

Input:

JSON { "description": "mô tả tiếng Việt" }

Optional file upload image (ảnh mẫu)

Flow:

Kiểm tra description (bắt buộc).

Dịch sang tiếng Anh.

Nếu có ảnh upload → gọi AI (img2img).

Nếu không → gọi AI (text2img).

Lưu ảnh PNG trong suốt vào uploads/designs/.

Trả về { "imageUrl": "/static/designs/{fileName}.png" }.

Orders

POST /api/orders → tạo đơn hàng (gắn userId)

GET /api/orders/me → lấy danh sách đơn hàng của user

Admin

GET /api/admin/orders → danh sách tất cả đơn hàng

PATCH /api/admin/orders/{id}/status → update trạng thái đơn hàng

GET /api/admin/stats → doanh thu tổng + doanh thu theo tháng

4. AI Integration

Translate: viết TranslationService dùng OpenAI GPT hoặc Google Translate API.

AI Generate: viết DesignService:

Nếu text-only → gọi OpenAI DALL·E hoặc HuggingFace Stable Diffusion text2img.

Nếu có ảnh upload → gọi API img2img của HuggingFace (Stable Diffusion).

Lưu ảnh bằng UUID để tránh trùng lặp.

Serve ảnh qua StaticResourceConfig.

5. Exception Handling

Tạo GlobalExceptionHandler để trả JSON { "error": "message" }.

Validate đầu vào: nếu description trống thì return 400.

6. Project Structure (gợi ý)
   src/main/java/com/example/app/
   ├─ config/            # cấu hình (Mongo, Security, StaticResourceConfig)
   ├─ controller/        # REST Controllers
   ├─ dto/               # request/response DTOs
   ├─ entity/            # MongoDB entities
   ├─ repository/        # MongoDB repositories
   ├─ service/           # business logic (UserService, OrderService, DesignService)
   └─ util/              # TranslationUtil, FileUtil

7. Ví dụ Curl API
   Generate design không có ảnh
   curl -X POST http://localhost:8080/api/designs/generate \
   -H "Content-Type: application/json" \
   -d '{"description":"Một chú mèo dễ thương đội mũ phi hành gia"}'

Generate design có ảnh mẫu
curl -X POST http://localhost:8080/api/designs/generate \
-F "description=Một chú mèo dễ thương đội mũ phi hành gia" \
-F "image=@/path/to/sample.png"


Response:

{
"imageUrl": "/static/designs/8fa2d21c-9b47-41c9-abc3-fb22d01a3d4e.png"
}
