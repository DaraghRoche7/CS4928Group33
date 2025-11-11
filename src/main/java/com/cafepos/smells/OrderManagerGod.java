package main.java.com.cafepos.smells;

import main.java.com.cafepos.common.Money;
import main.java.com.cafepos.order.CheckoutService;
import main.java.com.cafepos.factory.ProductFactory;
import main.java.com.cafepos.checkout.*;
import main.java.com.cafepos.payment.*;

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

