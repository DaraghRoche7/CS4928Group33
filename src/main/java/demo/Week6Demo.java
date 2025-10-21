package main.java.demo;

import main.java.smells.OrderManagerGod;
import main.java.factory.ProductFactory;
import main.java.pricing.*;
import main.java.payment.*;
import main.java.order.CheckoutService;

public final class Week6Demo {
    public static void main(String[] args) {
        // Old behavior
        String oldReceipt = OrderManagerGod.process("LAT+L", 2, "CARD", "LOYAL5", false);
        
        // New behavior with equivalent result
        var pricing = new PricingService(new NoDiscountPolicy(), new FixedRateTaxPolicy(10));
        var printer = new ReceiptPrinter();
        var paymentFactory = new DefaultPaymentStrategyFactory();
        var checkout = new CheckoutService(new ProductFactory(), pricing, printer, paymentFactory, 10);
        String newReceipt = checkout.checkout("LAT+L", 2, "CARD", "LOYAL5", false);
        
        System.out.println("Old Receipt:\n" + oldReceipt);
        System.out.println("\nNew Receipt:\n" + newReceipt);
        System.out.println("\nMatch: " + oldReceipt.equals(newReceipt));
    }
}
