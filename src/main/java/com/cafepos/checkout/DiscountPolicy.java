package main.java.com.cafepos.checkout;

import main.java.com.cafepos.common.Money;

public interface DiscountPolicy {
    Money discountOf(Money subtotal);
}

