package main.java.smells;

import main.java.order.Money;
import main.java.order.Order;
import main.java.payment.*;
import main.java.factory.ProductFactory;
import main.java.catalog.Product;
import main.java.pricing.DiscountPolicy;
import main.java.pricing.TaxPolicy;
import main.java.pricing.ReceiptPrinter;
import main.java.pricing.PricingService;
import main.java.pricing.FixedRateTaxPolicy;

public class OrderManagerGod {

    public static int TAX_PERCENT = 10;
    public static String LAST_DISCOUNT_CODE = null;

    // --- Main process method ---
    public static String process(String recipe, int qty, String paymentType, String discountCode, boolean printReceipt) {
        ProductFactory factory = new ProductFactory();
        Product product = factory.create(recipe);

        // Determine unit price
        Money unitPrice = product instanceof main.java.decorators.Priced p ? p.price() : product.basePrice();

        if (qty <= 0) qty = 1;

        Money subtotal = unitPrice.multiply(qty);

        // Calculate discount using original logic
        Money discount = calculateDiscount(subtotal, discountCode);
        Money discounted = subtotal.subtract(discount);
        if (discounted.asBigDecimal().signum() < 0) discounted = Money.zero();

        // Calculate tax using TaxPolicy
        TaxPolicy taxPolicy = new FixedRateTaxPolicy(TAX_PERCENT);
        Money tax = taxPolicy.taxOn(discounted);
        Money total = discounted.add(tax);

        // Build receipt using ReceiptPrinter
        ReceiptPrinter printer = new ReceiptPrinter();
        PricingService.PricingResult pr = new PricingService.PricingResult(subtotal, discount, tax, total);
        String receipt = printer.format(recipe, qty, pr, TAX_PERCENT);
        
        if (printReceipt) {
            printer.print(receipt);
        }

        // Execute payment using PaymentStrategy (via factory)
        PaymentStrategyFactory paymentFactory = new DefaultPaymentStrategyFactory();
        PaymentStrategy paymentStrategy = paymentFactory.fromType(paymentType);
        Order order = new Order(qty);
        paymentStrategy.pay(order);

        return receipt;
    }

    private static Money calculateDiscount(Money subtotal, String discountCode) {
        if (discountCode == null) return Money.zero();
        
        LAST_DISCOUNT_CODE = discountCode;
        
        if (discountCode.equalsIgnoreCase("LOYAL5")) {
            return Money.of(subtotal.asBigDecimal()
                .multiply(java.math.BigDecimal.valueOf(5))
                .divide(java.math.BigDecimal.valueOf(100)));
        } else if (discountCode.equalsIgnoreCase("COUPON1")) {
            return Money.of(1.00);
        } else if (discountCode.equalsIgnoreCase("NONE")) {
            return Money.zero();
        } else {
            return Money.zero();
        }
    }

    // Old factory method retained for backward compatibility if needed elsewhere
    private static PaymentStrategy createPaymentStrategy(String paymentType) {
        PaymentStrategyFactory paymentFactory = new DefaultPaymentStrategyFactory();
        return paymentFactory.fromType(paymentType);
    }
}
