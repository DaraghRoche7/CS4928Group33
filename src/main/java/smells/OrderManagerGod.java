package main.java.smells;

import main.java.order.CheckoutService;
import main.java.factory.ProductFactory;
import main.java.pricing.*;
import main.java.payment.*;

public class OrderManagerGod {

    // --- Main process method ---
    public static String process(String recipe, int qty, String paymentType, String discountCode, boolean printReceipt) {
        // Create dependencies
        ProductFactory factory = new ProductFactory();
        DiscountPolicy discountPolicy = new NoDiscountPolicy();
        TaxPolicy taxPolicy = new FixedRateTaxPolicy(10);
        PricingService pricing = new PricingService(discountPolicy, taxPolicy);
        ReceiptPrinter printer = new ReceiptPrinter();
        PaymentStrategyFactory paymentFactory = new DefaultPaymentStrategyFactory();
        
        // Create orchestrator
        CheckoutService checkout = new CheckoutService(factory, pricing, printer, paymentFactory, 10);
        
        // Delegate to orchestrator
        return checkout.checkout(recipe, qty, paymentType, discountCode, printReceipt);
    }
}