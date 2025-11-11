package main.java.com.cafepos.checkout;

import java.math.BigDecimal; 
import main.java.com.cafepos.common.Money;

public final class FixedRateTaxPolicy implements TaxPolicy {

    private final int percent;

    public FixedRateTaxPolicy(int percent) {
        if (percent < 0) throw new IllegalArgumentException();
        this.percent = percent;
    }

    @Override
    public Money taxOn(Money amount) {
        BigDecimal t = amount.asBigDecimal()
                .multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100));
        return Money.of(t);
    }

    public int getPercent() { // for keeping printed label consistent
        return percent;
    }
}

