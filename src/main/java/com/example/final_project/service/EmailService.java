package com.example.final_project.service;

import com.example.final_project.entity.CartItem;
import com.example.final_project.entity.Order;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void send(String toEmail, String subject, String body) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(toEmail);
                message.setSubject(subject);
                message.setText(body);
                javaMailSender.send(message);
            } catch (Exception e) {
                e.printStackTrace(); // hoặc log lỗi
            }
    }

    /**
     * Gửi email khi tạo tài khoản cho khách mới chưa đăng nhập
     */
    public void sendAccountCreated(String toEmail, String plainPassword) {
        String subject = "Tài khoản của bạn đã được tạo tại SpringCommerce!";
        String body = "Xin chào,\n\n"
                + "Bạn vừa được tạo tài khoản tự động khi đặt hàng tại SpringCommerce.\n"
                + "Email đăng nhập: " + toEmail + "\n"
                + "Mật khẩu tạm thời: " + plainPassword + "\n\n"
                + "Bạn có thể đăng nhập và đổi mật khẩu bất kỳ lúc nào.\n"
                + "Trân trọng,\nSpringCommerce Team";
        send(toEmail, subject, body);
    }

    /**
     * Gửi email khi khôi phục password
     */
    public void sendOTPEmailRecoveryPassword(String toEmail, String otp) {
        String subject = "Mã OTP khôi phục mật khẩu";
        String content = "Chào " + toEmail + ",\n\n"
                + "Mã OTP để khôi phục mật khẩu của bạn là: " + otp + "\n\n"
                + "Mã OTP có hiệu lực trong 5 phút.\n"
                + "Nếu bạn không yêu cầu, vui lòng bỏ qua email này.\n\n"
                + "Cảm ơn bạn.";

        send(toEmail, subject, content);
    }

    /*
     * Gửi email xác nhận đơn hàng
     */

    public void sendOrderConfirmation(String toEmail, Order order, List<CartItem> items) {
        // Currency formatter for VND
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        // Date formatter
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Build plain text content
        StringBuilder content = new StringBuilder();
        content.append("🛒 ĐƠN HÀNG CỦA BẠN ĐÃ ĐƯỢC XÁC NHẬN!\n")
                .append("========================================\n\n")
                .append("Mã đơn hàng: ").append(order.getId()).append("\n")
                .append("Ngày đặt hàng: ").append(order.getPurchaseDate().format(dateFormatter)).append("\n")
                .append("Địa chỉ giao hàng: ")
                .append(order.getAddressDetail()).append(", ")
                .append(order.getWard()).append(", ")
                .append(order.getDistrict()).append(", ")
                .append(order.getProvince()).append("\n\n")
                .append("📦 DANH SÁCH SẢN PHẨM\n")
                .append("----------------------------------------\n");

        // Format product list as a table-like structure
        content.append(String.format("%-30s %-10s %-15s\n", "Sản phẩm", "Số lượng", "Giá"));
        content.append("----------------------------------------\n");
        for (CartItem item : items) {
            String productName = item.getVariant().getVariantName();
            // Truncate product name if too long to avoid breaking alignment
            if (productName.length() > 27) {
                productName = productName.substring(0, 24) + "...";
            }
            content.append(String.format("%-30s %-10d %-15s\n",
                    productName,
                    item.getQuantity(),
                    currencyFormat.format(item.getPrice())));
        }

        content.append("----------------------------------------\n")
                .append(String.format("Tổng cộng: %s\n\n", currencyFormat.format(order.getTotalAmount())))
                .append("Cảm ơn bạn đã mua sắm tại SpringCommerce!\n")
                .append("Trân trọng,\n")
                .append("SpringCommerce Team\n");

        // Send the email
        send(toEmail, "Xác nhận đơn hàng #" + order.getId(), content.toString());
    }
}
