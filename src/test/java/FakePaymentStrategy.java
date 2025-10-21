package test.java;

import main.java.payment.PaymentStrategy;
import main.java.order.*;

public class FakePaymentStrategy implements PaymentStrategy {
    private boolean called;

    @Override
    public void pay(Order order) {
        called = true;
    }

    public boolean isCalled() {
        return called;
    }

}

