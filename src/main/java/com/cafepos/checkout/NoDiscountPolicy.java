package main.java.com.cafepos.checkout;

import main.java.com.cafepos.common.Money;

public final class NoDiscountPolicy implements DiscountPolicy {

    @Override
    public Money discountOf(Money subtotal) {
        return Money.zero();
    }
}


