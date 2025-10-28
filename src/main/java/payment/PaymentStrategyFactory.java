package main.java.payment;

public interface PaymentStrategyFactory {
    PaymentStrategy fromType(String paymentType);
}



