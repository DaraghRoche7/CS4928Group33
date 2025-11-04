package main.java.demo;

import main.java.factory.ProductFactory;
import main.java.pricing.*;
import main.java.printing.LegacyPrinterAdapter;
import main.java.vendor.legacy.LegacyThermalPrinter;

public final class Week8Demo_Adapter {
    public static void main(String[] args) {
        System.out.println("=== Week 8 Demo: Adapter Pattern ===\n");

        // Create a receipt using ReceiptPrinter
        ProductFactory factory = new ProductFactory();
        PricingService pricing = new PricingService(
            new NoDiscountPolicy(), 
            new FixedRateTaxPolicy(10)
        );
        ReceiptPrinter receiptPrinter = new ReceiptPrinter();

        // Create a product and calculate pricing
        String recipe = "LAT+L";
        int qty = 2;
        var product = factory.create(recipe);
        var subtotal = product.basePrice().multiply(qty);
        var pricingResult = pricing.price(subtotal);

        // Format the receipt
        String receiptText = receiptPrinter.format(recipe, qty, pricingResult, 10);
        
        System.out.println("1. Printing receipt normally (System.out):");
        System.out.println("----------------------------------------");
        receiptPrinter.print(receiptText);
        System.out.println();

        // Now use the adapter to print via legacy printer
        System.out.println("2. Printing receipt via Legacy Printer (Adapter):");
        System.out.println("----------------------------------------");
        LegacyThermalPrinter legacyPrinter = new LegacyThermalPrinter();
        LegacyPrinterAdapter adapter = new LegacyPrinterAdapter(legacyPrinter);
        adapter.print(receiptText);
        System.out.println();

        System.out.println("3. Receipt content (for reference):");
        System.out.println("----------------------------------------");
        System.out.println(receiptText);
    }
}
