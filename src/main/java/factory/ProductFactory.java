package main.java.factory;

import main.java.catalog.Product;
import main.java.catalog.SimpleProduct;
import main.java.order.Money;
import main.java.decorators.*;

import java.util.Arrays;

public final class ProductFactory {

    public Product create(String recipe) {
        if (recipe == null || recipe.isBlank()) {
            throw new IllegalArgumentException("recipe required");
        }

        // Split by '+', trim spaces, uppercase tokens
        String[] parts = Arrays.stream(recipe.split("\\+"))
                .map(String::trim)
                .map(String::toUpperCase)
                .toArray(String[]::new);

        // Determine base product
        Product p = switch (parts[0]) {
            case "ESP" -> new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
            case "LAT" -> new SimpleProduct("P-LAT", "Latte", Money.of(3.20));
            case "CAP" -> new SimpleProduct("P-CAP", "Cappuccino", Money.of(3.00));
            default -> throw new IllegalArgumentException("Unknown base: " + parts[0]);
        };

        // Apply add-ons in order
        for (int i = 1; i < parts.length; i++) {
            String addon = parts[i];
            if (addon.startsWith("SYP:")) {
                String flavour = addon.substring(4); // e.g. "Caramel"
                p = new Syrup(p, flavour);
                continue;
            }
            p = switch (addon) {
                case "SHOT" -> new ExtraShot(p);
                case "OAT" -> new OatMilk(p);
                case "L" -> new SizeLarge(p);
                default -> throw new IllegalArgumentException("Unknown addon: " + addon);
            };
        }

        return p;
    }
}

