package main.java.catalog;

import main.java.order.Money;

public interface Product {
    String id();
    String name();
    Money basePrice();
}

