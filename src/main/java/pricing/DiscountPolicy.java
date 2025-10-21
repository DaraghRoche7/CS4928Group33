package main.java.pricing;

import main.java.order.Money;

public interface DiscountPolicy {
    Money discountOf(Money subtotal);
}

