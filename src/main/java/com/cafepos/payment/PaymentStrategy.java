package main.java.com.cafepos.payment;

import main.java.com.cafepos.order.Order;

public interface PaymentStrategy {
    void pay(Order order);
}

