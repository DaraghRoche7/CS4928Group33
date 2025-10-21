package main.java.decorators;

import main.java.catalog.Product;
import main.java.order.Money;

public abstract class ProductDecorator implements Product {
    protected final Product base;

    protected ProductDecorator(Product base) {
        if (base == null) throw new IllegalArgumentException("base product required");
        this.base = base;
    }

    @Override
    public String id() {
        return base.id(); // id may remain the base product id
    }

    @Override
    public Money basePrice() {
        return base.basePrice(); // original price (not total)
    }

    // Concrete decorators will override name() and provide a finalPrice() helper if needed
}
