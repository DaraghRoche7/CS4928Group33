package main.java.com.cafepos.payment;

import main.java.com.cafepos.order.Order;

public final class CashPayment implements PaymentStrategy {
    @Override
    public void pay(Order order) {
        System.out.println("[Cash] Customer paid " + order.totalWithTax(10) + " EUR");
    }
}


