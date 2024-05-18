package com.perfumes.perfumeswebapp.Services;

import com.perfumes.perfumeswebapp.model.Cart;
import com.perfumes.perfumeswebapp.model.CartItem;
import com.perfumes.perfumeswebapp.model.Product;

import jakarta.servlet.http.HttpSession;

import com.perfumes.perfumeswebapp.Repositories.CartRepository;
import com.perfumes.perfumeswebapp.Repositories.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    public Cart findByUserId(String userId) {
        return cartRepository.findByUserId(userId);
    }

    @Autowired
    private HttpSession httpSession; // Autowire HttpSession to access session attributes

    public Cart getOrCreateCart(HttpSession session) {
        // Check if a cart ID is stored in the session
        String cartId = (String) httpSession.getAttribute("cartId");
        if (cartId != null) {
            // If cart ID exists, retrieve the cart from the database
            return cartRepository.findById(cartId).orElseGet(this::createCart);
        } else {
            // If cart ID doesn't exist in the session, create a new cart and store its ID

            Cart cart = createCart();
            httpSession.setAttribute("cartId", cart.getId());
            return cart;
        }
    }

    public Cart createCart() {
        Cart cart = new Cart();

        return cartRepository.save(cart);
    }

    public void addItemToCart(String cartId, String productId, double productPrice, String productImage, int quantity) {
        Optional<Cart> optionalCart = cartRepository.findById(cartId);
        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            List<CartItem> items = cart.getItems();

            if (items == null) {
                items = new ArrayList<>();
                cart.setItems(items);
            }
            // Create a new CartItem with all necessary product information
            Product product = productRepository.findById(productId).orElseThrow();
            CartItem cartItem = new CartItem();
            cartItem.setProductName(product.getProductName());
            cartItem.setPrice(product.getPrice());
            cartItem.setImage(product.getImage());
            cartItem.setQuantity(quantity);
            items.add(cartItem);
            cart.setTotalPrice(calculateTotalPrice(items));
            cartRepository.save(cart);
        } else {
            // Handle error: Cart not found
        }
    }

    public void removeItemFromCart(String cartId, String itemId) {
        Optional<Cart> optionalCart = cartRepository.findById(cartId);
        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            List<CartItem> items = cart.getItems();

            // Remove the item with the specified item ID
            items.removeIf(item -> item.getId().equals(itemId));

            // Update the total price of the cart
            cart.setTotalPrice(calculateTotalPrice(items));

            // Save the updated cart
            cartRepository.save(cart);
        } else {
            // Handle error: Cart not found
        }
    }

    private double calculateTotalPrice(List<CartItem> items) {
        double totalPrice = 0;
        for (CartItem item : items) {
            totalPrice += item.getPrice() * item.getQuantity();
        }
        return totalPrice;
    }

    public List<CartItem> getCartItems(String cartId) {
        Optional<Cart> optionalCart = cartRepository.findById(cartId);
        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            return cart.getItems(); // Return cart items directly
        } else {
            // Handle error: Cart not found
            return Collections.emptyList();
        }
    }

}
