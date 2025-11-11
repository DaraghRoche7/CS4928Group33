package main.java.com.cafepos.checkout;

import main.java.com.cafepos.common.Money;

public interface TaxPolicy {
    Money taxOn(Money amount);
}

