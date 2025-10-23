package com.project.ecommerce.payment;

import com.project.ecommerce.exceptionhandler.ResourceNotFoundException;
import com.project.ecommerce.order.Order;
import com.project.ecommerce.order.OrderDTO;
import com.project.ecommerce.order.OrderService;
import com.project.ecommerce.payment.Payment.PaymentStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentServiceImpl implements PaymentService
{
    private final PaymentRepo paymentRepo;
//    private final OrderClient orderClient;
    private final OrderService orderService;

    public PaymentServiceImpl(PaymentRepo paymentRepo, OrderService orderService) {
        this.paymentRepo = paymentRepo;
//        this.orderClient = orderClient;
        this.orderService = orderService;
    }


//    @Transactional
    @Override
    public PaymentDTO makePayment(PaymentInputDTO dto)
    {
        Order order = orderService.getOrderObj(dto.orderId);

        if (order == null || order.getUserId() != dto.userId) {
            throw new RuntimeException("Invalid order or user");
        }

        if (order.getTotalAmount() != dto.amount) {
            throw new RuntimeException("Payment amount does not match order total");
        }

        Payment payment = new Payment();

        payment.setOrderId(dto.orderId);
        payment.setAmount(dto.amount);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(PaymentStatus.COMPLETED);

//        order.setStatus(Order.OrderStatus.PAID);
//        orderService.saveOrderObj(order);
        return convertEntityToDTO(paymentRepo.save(payment));
    }

    private PaymentDTO convertEntityToDTO(Payment payment)
    {
        PaymentDTO dto = new PaymentDTO();
        dto.paymentId = payment.getId();
        dto.orderId = payment.getOrderId();
        dto.amount = payment.getAmount();
        dto.paymentDate = payment.getPaymentDate().toString();
        dto.paymentStatus = payment.getStatus().name();

        return dto;
    }

    public PaymentDTO getPaymentDetails(long orderId) {
        Payment payment = paymentRepo.findTopByOrderIdOrderByPaymentDateDesc(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "order id", orderId));

        return convertEntityToDTO(payment);
    }

    @Override
    public PaymentDTO refundPayment(long paymentId) {
        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setStatus(PaymentStatus.REFUNDED);
        return convertEntityToDTO(paymentRepo.save(payment));
    }

}
