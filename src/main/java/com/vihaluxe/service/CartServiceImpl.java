package com.vihaluxe.service;

import com.vihaluxe.model.Cart;
import com.vihaluxe.model.CartItem;
import com.vihaluxe.model.Product;
import com.vihaluxe.model.User;
import com.vihaluxe.repository.CartItemRepository;
import com.vihaluxe.repository.CartRepository;
import com.vihaluxe.repository.ProductRepository;
import com.vihaluxe.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.vihaluxe.model.CustomCandle;


@Service
public class CartServiceImpl implements CartService {



    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;

    }

    @Override
    public void addToCart(Long productId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Cart cart = cartRepository.findByUser(user).orElse(null);

        if (cart == null) {

            cart = new Cart();
            cart.setUser(user);

            cart = cartRepository.save(cart);
        }

        CartItem cartItem = cartItemRepository
                .findByCartAndProduct(cart, product)
                .orElse(null);

        if (cartItem == null) {

            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setCustomCandle(null);
            cartItem.setQuantity(1);

        } else {

            cartItem.setQuantity(cartItem.getQuantity() + 1);

        }

        cartItemRepository.save(cartItem);
    }

    @Override
    public void addCustomCandle(CustomCandle customCandle, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));



        Cart cart = cartRepository.findByUser(user).orElse(null);

        if (cart == null) {

            cart = new Cart();
            cart.setUser(user);
            cart = cartRepository.save(cart);

        }

        CartItem cartItem = cartItemRepository
                .findByCartAndCustomCandle(cart, customCandle)
                .orElse(null);

        if (cartItem == null) {

            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(null);
            cartItem.setCustomCandle(customCandle);
            cartItem.setQuantity(1);

        } else {

            cartItem.setQuantity(cartItem.getQuantity() + 1);

        }

        cartItemRepository.save(cartItem);
    }

    @Override
    public Cart getCart(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save(cart);
                });
    }

    @Override
    public void increaseQuantity(Long cartItemId) {

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart Item Not Found"));

        item.setQuantity(item.getQuantity() + 1);

        cartItemRepository.save(item);
    }

    @Override
    public void decreaseQuantity(Long cartItemId) {

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart Item Not Found"));

        if (item.getQuantity() > 1) {

            item.setQuantity(item.getQuantity() - 1);

            cartItemRepository.save(item);

        } else {

            cartItemRepository.delete(item);

        }
    }

    @Override
    public void removeItem(Long cartItemId) {

        cartItemRepository.deleteById(cartItemId);

    }
}