package main.java.smells;

import main.java.order.Money;
import main.java.factory.ProductFactory;
import main.java.catalog.Product;

public class OrderManagerGod {

    public static int TAX_PERCENT = 10; // Global/Static State + Primitive Obsession
    public static String LAST_DISCOUNT_CODE = null; // Global/Static State

    public static String process(String recipe, int qty, String paymentType, String discountCode, boolean printReceipt) {
        // God Class & Long Method: performs product creation, pricing, discounting, tax, payment I/O, and printing
        ProductFactory factory = new ProductFactory();
        Product product = factory.create(recipe);

        Money unitPrice;
        try {
            var priced = product instanceof main.java.decorators.Priced p ? p.price() : product.basePrice();
            unitPrice = priced;
        } catch (Exception e) {
            unitPrice = product.basePrice();
        }

        if (qty <= 0) qty = 1; // Primitive Obsession: bare primitive for quantity

        Money subtotal = unitPrice.multiply(qty);
        Money discount = Money.zero();

        if (discountCode != null) { // Primitive Obsession: raw String discount codes
            if (discountCode.equalsIgnoreCase("LOYAL5")) {
                discount = Money.of(subtotal.asBigDecimal()
                        .multiply(java.math.BigDecimal.valueOf(5))
                        .divide(java.math.BigDecimal.valueOf(100))); // Duplicated Logic + Magic Number (5%)
            } else if (discountCode.equalsIgnoreCase("COUPON1")) {
                discount = Money.of(1.00); // Primitive Obsession + Magic Number
            } else if (discountCode.equalsIgnoreCase("NONE")) {
                discount = Money.zero();
            } else {
                discount = Money.zero();
            }
            LAST_DISCOUNT_CODE = discountCode; // Global/Static State
        }

        Money discounted = Money.of(subtotal.asBigDecimal().subtract(discount.asBigDecimal())); // Duplicated Logic
        if (discounted.asBigDecimal().signum() < 0) discounted = Money.zero();

        var tax = Money.of(discounted.asBigDecimal()
                .multiply(java.math.BigDecimal.valueOf(TAX_PERCENT))
                .divide(java.math.BigDecimal.valueOf(100))); // Feature Envy / Shotgun Surgery + Primitive Obsession
        var total = discounted.add(tax);

        if (paymentType != null) {
            // God Class: payment handling logic mixed in
            if (paymentType.equalsIgnoreCase("CASH")) {
                System.out.println("[Cash] Customer paid " + total + " EUR");
            } else if (paymentType.equalsIgnoreCase("CARD")) {
                System.out.println("[Card] Customer paid " + total + " EUR with card ****1234");
            } else if (paymentType.equalsIgnoreCase("WALLET")) {
                System.out.println("[Wallet] Customer paid " + total + " EUR via wallet user-wallet-789");
            } else {
                System.out.println("[UnknownPayment] " + total);
            }
        }

        // Long Method + Duplicated Logic: builds receipt inline instead of delegating
        StringBuilder receipt = new StringBuilder();
        receipt.append("Order (").append(recipe).append(") x").append(qty).append("\n");
        receipt.append("Subtotal: ").append(subtotal).append("\n");
        if (discount.asBigDecimal().signum() > 0) {
            receipt.append("Discount: -").append(discount).append("\n");
        }
        receipt.append("Tax (").append(TAX_PERCENT).append("%): ").append(tax).append("\n");
        receipt.append("Total: ").append(total);

        String out = receipt.toString();

        if (printReceipt) {
            System.out.println(out); // God Class: presentation logic in business method
        }

        return out;
    }
}

