package com.perfumes.perfumeswebapp.Services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.perfumes.perfumeswebapp.Repositories.OrderRepository;

import com.perfumes.perfumeswebapp.model.Cart;
import com.perfumes.perfumeswebapp.model.CartItem;
import com.perfumes.perfumeswebapp.model.Order;
import com.perfumes.perfumeswebapp.model.OrderItem;
import com.perfumes.perfumeswebapp.model.OrderStatus;
import com.perfumes.perfumeswebapp.model.User;

import jakarta.servlet.http.HttpSession;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemService orderItemService;

    public List<Order> findOrdersWithUser(User user, Order order, HttpSession session) {
        session.setAttribute("user", user);
        return orderRepository.findByUser(user);
    }

    // Method to create an order from a cart
    public Order createOrderFromCart(Cart cart, User user, HttpSession session) {

        // Calculate total price from cart items
        double total = calculateTotal(cart);

        // Create order object

        Order order = new Order();
        order.setCart(cart);
        order.setUser(user);
        order.setOrderStatus(OrderStatus.PENDING); // Assuming initial status is PENDING
        order.setCreatedDate(new Date());
        order.setTotal(total);

        // Save order to database
        Order savedOrder = orderRepository.save(order);

        // Create order items from cart items and associate them with the order
        createOrderItemsFromCart(cart, savedOrder, user);

        // Clear the cart after creating the order
        cart.clearCart();

        return savedOrder;
    }

    // Method to calculate total price from cart items
    private double calculateTotal(Cart cart) {
        double total = 0.0;
        for (CartItem cartItem : cart.getItems()) {
            total += cartItem.getPrice() * cartItem.getQuantity();
        }
        return total;
    }

    // Method to create order items from cart items and associate them with the
    // order
    private void createOrderItemsFromCart(Cart cart, Order order, User user) {

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductName(cartItem.getProductName());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setImage(cartItem.getImage());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setId(order.getOrderId()); // Set order ID for association
            orderItems.add(orderItem);
        }
        orderItemService.createOrderItems(orderItems);
    }

    public void updateOrderStatus(String orderId, OrderStatus orderStatus) {
        Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
        order.setOrderStatus(orderStatus);
        orderRepository.save(order);
    }

    public class OrderNotFoundException extends RuntimeException {
        public OrderNotFoundException() {
            super("Order not found");
        }
    }
}
