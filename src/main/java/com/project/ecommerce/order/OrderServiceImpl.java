package com.project.ecommerce.order;

import com.project.ecommerce.exceptionhandler.APIException;
import com.project.ecommerce.exceptionhandler.ResourceNotFoundException;
import com.project.ecommerce.order.Order.OrderStatus;
import com.project.ecommerce.orderitem.OrderItem;
import com.project.ecommerce.orderitem.OrderItemDTO;
import com.project.ecommerce.orderitem.OrderItemRepo;
import com.project.ecommerce.product.ProductDTO;
import com.project.ecommerce.product.ProductService;
import com.project.ecommerce.util.PageDetails;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService{
    private final OrderRepo orderRepo;
    private final OrderItemRepo orderItemRepo;
//    private final ProductClient productClient;
    private final ProductService productService;

    public OrderServiceImpl(OrderRepo orderRepo, OrderItemRepo orderItemRepo, ProductService productService)
    {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.productService = productService;
    }

    //@Transactional is a Spring annotation that wraps a method (or class) in a database transaction.
    //It ensures that all database operations within the method either succeed together or fail together.
    //If anything throws an exception, everything gets rolled back automatically — like an undo.
    @Transactional
    public OrderDTO placeOrder(OrderDTO dto) {

        if (dto.items == null || dto.items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item.");
        }

        Order order = new Order();
        order.setUserId(dto.userId);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(dto.totalAmount);

        List<OrderItem> items = dto.items.stream().map(i -> {

            ProductDTO productDTO = productService.getProductObjForProductId(i.productId);

            OrderItem item = new OrderItem();

            item.setProductId(productDTO.id);
            item.setQuantity(i.quantity);
            item.setPrice(productDTO.finalPrice);
            item.setOrder(order);

            long sellerId = productDTO.sellerId;
            item.setSellerId(sellerId);

            return item;
        }).collect(Collectors.toList());

        order.setItems(items);

        double total = items.stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();
        order.setTotalAmount(total);

        Order savedOrder = orderRepo.save(order);

        return convertToDTO(savedOrder);
    }

    private OrderDTO convertToDTO(Order order)
    {
        OrderDTO dto = new OrderDTO();

        dto.orderId = order.getId();
        dto.userId = order.getUserId();
        dto.totalAmount = order.getTotalAmount();
        dto.items = order.getItems().stream().map(i -> {
            OrderItemDTO orderItemDTO = new OrderItemDTO();

            orderItemDTO.orderItemId = i.getId();
            orderItemDTO.productId = i.getProductId();
            orderItemDTO.price = i.getPrice();
            orderItemDTO.quantity = i.getQuantity();
            orderItemDTO.sellerId = i.getSellerId();

            return orderItemDTO;
        }).collect(Collectors.toList());

        return dto;
    }

    //orders for user (user's order history)
    public PageDetails<OrderDTO> fetchOrdersForUser(long userId, int pageNum, int pageSize, String sortBy, String sortOrder)
    {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNum, pageSize, sortByAndOrder);
        Page<Order> orderPage = orderRepo.findAllByUserId(userId, pageDetails);

        return getOrderDTOPageDetails(orderPage);
    }

    private PageDetails<OrderDTO> getOrderDTOPageDetails(Page<Order> orderPage)
    {
        List<Order> orders = orderPage.getContent();

        if(orders.isEmpty())
        {
            throw new APIException("No orders available!!");
        }

        List<OrderDTO> orderDTOS = orders.stream().map(this::convertToDTO).toList();

        return new PageDetails<>(
                orderDTOS,
                orderPage.getNumber(),
                orderPage.getSize(),
                orderPage.getTotalElements(),
                orderPage.getTotalPages(),
                orderPage.isLast()
        );
    }

    public static class OrderStatusUpdateDTO
    {
        public long orderId;
        public OrderStatus orderStatus;
    }

    public OrderDTO updateOrderStatus(OrderStatusUpdateDTO orderStatusUpdateDTO) {
        Order orderUpdate = getOrderObj(orderStatusUpdateDTO.orderId);
        orderUpdate.setStatus(orderStatusUpdateDTO.orderStatus);

        return convertToDTO(orderRepo.save(orderUpdate));
    }

    public OrderDTO cancelOrder(long orderId) {
        Order orderUpdate = getOrderObj(orderId);
        orderUpdate.setStatus(OrderStatus.CANCELLED);

        return convertToDTO(orderRepo.save(orderUpdate));
    }

    public Order getOrderObj(long orderId) {
         return orderRepo.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public Order saveOrderObj(Order order)
    {
        return orderRepo.save(order);
    }

    public OrderDTO getOrderData(long orderId)
    {
        return convertToDTO(orderRepo.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException("Order", "order id", orderId))
        );
    }

    public OrderDTO deleteOrder(long orderId) {
        Order order = orderRepo.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException("Order", "orderId", orderId));

        orderRepo.delete(order);

        return convertToDTO(order);
    }

    public PageDetails<OrderDTO> listOrdersForMonth(int year, int month, int pageNum, int pageSize, String sortBy, String sortOrder)
    {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                                ? Sort.by(sortBy).ascending()
                                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNum, pageSize, sortByAndOrder);

        OffsetDateTime start = OffsetDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime end = start.plusMonths(1);

        Page<Order> orderPage = orderRepo.findByOrderDateBetween(start, end, pageDetails);

        return getOrderDTOPageDetails(orderPage);
    }

    public PageDetails<OrderItemDTO> fetchOrdersForSeller(long sellerId, int pageNum, int pageSize, String sortBy, String sortOrder)
    {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNum, pageSize, sortByAndOrder);

        Page<OrderItem> orderItems = orderItemRepo.findAllBySellerId(sellerId, pageDetails);

        List<OrderItemDTO> orderItemDTOS = orderItems.getContent().stream().map(oi -> {
            OrderItemDTO dto = new OrderItemDTO();

            dto.productId = oi.getProductId();
            dto.orderItemId = oi.getId();
            dto.sellerId = oi.getSellerId();
            dto.quantity = oi.getQuantity();
            dto.price = oi.getPrice();

            return dto;
        }).toList();

        return new PageDetails<>(
                orderItemDTOS,
                orderItems.getNumber(),
                orderItems.getSize(),
                orderItems.getTotalElements(),
                orderItems.getTotalPages(),
                orderItems.isLast()
        );
    }

}
