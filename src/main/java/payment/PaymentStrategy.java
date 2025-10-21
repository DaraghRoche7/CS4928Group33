package main.java.payment;

import main.java.order.Order;

public interface PaymentStrategy {
    void pay(Order order);
}
