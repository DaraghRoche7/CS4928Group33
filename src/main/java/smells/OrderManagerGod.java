package main.java.smells;

import main.java.order.Money;
import main.java.order.CheckoutService;
import main.java.factory.ProductFactory;
import main.java.pricing.*;
import main.java.payment.*;

public class OrderManagerGod {

    // --- Main process method (legacy signature) ---
    public static String process(String recipe, int qty, String paymentType, String discountCode, boolean printReceipt) {
        return processWithTax(recipe, qty, paymentType, discountCode, 10, printReceipt);
    }

    // --- Overload to allow variable tax for demo ---
    public static String processWithTax(String recipe, int qty, String paymentType, String discountCode, int taxPercent, boolean printReceipt) {
        // Create dependencies
        ProductFactory factory = new ProductFactory();
        DiscountPolicy discountPolicy = new NoDiscountPolicy();
        TaxPolicy taxPolicy = new FixedRateTaxPolicy(taxPercent);
        PricingService pricing = new PricingService(discountPolicy, taxPolicy);
        ReceiptPrinter printer = new ReceiptPrinter();
        PaymentStrategyFactory paymentFactory = new DefaultPaymentStrategyFactory();
        
        // Create orchestrator
        CheckoutService checkout = new CheckoutService(factory, pricing, printer, paymentFactory, taxPercent);
        
        // Delegate to orchestrator
        return checkout.checkout(recipe, qty, paymentType, discountCode, printReceipt);
    }
}