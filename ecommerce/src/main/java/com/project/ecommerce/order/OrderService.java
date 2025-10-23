package com.project.ecommerce.order;

import com.project.ecommerce.order.OrderServiceImpl.OrderStatusUpdateDTO;
import com.project.ecommerce.orderitem.OrderItemDTO;
import com.project.ecommerce.util.PageDetails;

public interface OrderService
{
    OrderDTO placeOrder(OrderDTO dto);

    PageDetails<OrderDTO> fetchOrdersForUser(long userId, int pageNum, int pageSize, String sortBy, String sortOrder);

    OrderDTO updateOrderStatus(OrderStatusUpdateDTO statusUpdateDTO);

    OrderDTO cancelOrder(long orderId);

    Order getOrderObj(long orderId);

    Order saveOrderObj(Order order);

    OrderDTO getOrderData(long orderId);

    OrderDTO deleteOrder(long orderId);

    PageDetails<OrderDTO> listOrdersForMonth(int year, int month, int pageNum, int pageSize, String sortBy, String sortOrder);

    PageDetails<OrderItemDTO> fetchOrdersForSeller(long sellerId, int pageNum, int pageSize, String sortBy, String sortOrder);
}
