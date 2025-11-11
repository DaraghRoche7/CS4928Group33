package main.java.com.cafepos.decorator;

import main.java.com.cafepos.catalog.Product;
import main.java.com.cafepos.common.Money;

public final class Syrup extends ProductDecorator implements Priced {
    private static final Money SURCHARGE = Money.of(0.40);
    private final String flavour;

    public Syrup(Product base, String flavour) {
        super(base);
        if (flavour == null || flavour.isBlank()) {
            throw new IllegalArgumentException("Syrup flavour required");
        }
        this.flavour = flavour;
    }

    @Override
    public String name() {
        return base.name() + " + " + flavour + " Syrup";
    }

    @Override
    public Money price() {
        Money basePrice = (base instanceof Priced p ? p.price() : base.basePrice());
        return basePrice.add(SURCHARGE);
    }
}

