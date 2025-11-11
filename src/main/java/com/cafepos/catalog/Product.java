package main.java.com.cafepos.catalog;

import main.java.com.cafepos.common.Money;

public interface Product {
    String id();
    String name();
    Money basePrice();
}

