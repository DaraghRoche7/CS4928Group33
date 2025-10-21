package main.java.decorators;

import main.java.catalog.Product;
import main.java.order.Money;

public final class OatMilk extends ProductDecorator implements Priced {
    private static final Money SURCHARGE = Money.of(0.50);

    public OatMilk(Product base) { super(base); }

    @Override
    public String name() { return base.name() + " + Oat Milk"; }

    @Override
    public Money price() {
        Money basePrice = (base instanceof Priced p ? p.price() : base.basePrice());
        return basePrice.add(SURCHARGE);
    }
}
