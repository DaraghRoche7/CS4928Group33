package main.java.com.cafepos.payment;

public interface PaymentStrategyFactory {
    PaymentStrategy fromType(String paymentType);
}




