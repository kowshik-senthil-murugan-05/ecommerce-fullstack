package com.project.ecommerce.payment;

public interface PaymentService
{
    PaymentDTO makePayment(PaymentInputDTO dto);

    PaymentDTO getPaymentDetails(long orderId);

    PaymentDTO refundPayment(long paymentId);
}
