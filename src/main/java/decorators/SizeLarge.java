package main.java.decorators;

import main.java.catalog.Product;
import main.java.order.Money;

public final class SizeLarge extends ProductDecorator implements Priced {
    private static final Money SURCHARGE = Money.of(0.70);

    public SizeLarge(Product base) { super(base); }

    @Override
    public String name() { return base.name() + " (Large)"; }

    @Override
    public Money price() {
        Money basePrice = (base instanceof Priced p ? p.price() : base.basePrice());
        return basePrice.add(SURCHARGE);
    }
}
