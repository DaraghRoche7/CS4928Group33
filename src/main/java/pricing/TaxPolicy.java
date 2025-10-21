package main.java.pricing;

import main.java.order.Money;

public interface TaxPolicy {
    Money taxOn(Money amount);
}
