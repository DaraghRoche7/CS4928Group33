package main.java.pricing;

import main.java.order.Money;

public final class NoDiscountPolicy implements DiscountPolicy {

    @Override
    public Money discountOf(Money subtotal) {
        return Money.zero();
    }
}


