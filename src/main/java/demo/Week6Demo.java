package main.java.demo;

import java.util.Scanner;
import main.java.com.cafepos.smells.OrderManagerGod;
import main.java.com.cafepos.factory.ProductFactory;
import main.java.com.cafepos.checkout.*;
import main.java.com.cafepos.payment.*;
import main.java.com.cafepos.order.CheckoutService;

public final class Week6Demo {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Week 6 CLI Demo (Old vs New) ===");

        System.out.print("Enter recipe code (e.g., LAT+L, ESP, CAP+SHOT): ");
        String recipe = scanner.nextLine().trim();

        System.out.print("Enter quantity: ");
        int qty = 1;
        try { qty = Integer.parseInt(scanner.nextLine().trim()); } catch (Exception ignored) {}

        System.out.print("Enter payment type (CASH/CARD/WALLET): ");
        String paymentType = scanner.nextLine().trim();
        String cardDigits = null;
        if (paymentType.equalsIgnoreCase("CARD")) {
            // Validate 16-digit card number
            while (true) {
                System.out.print("Enter 16-digit card number: ");
                cardDigits = scanner.nextLine().trim();
                if (cardDigits.matches("\\d{16}")) break;
                System.out.println("Invalid card number. Please enter exactly 16 digits.");
            }
        }

        System.out.print("Enter discount code (LOYAL5/COUPON1/NONE or blank): ");
        String discountCode = scanner.nextLine().trim();
        if (discountCode.isEmpty()) discountCode = null;
        
        // Validate discount code immediately
        if (discountCode != null) {
            boolean isValidCode = discountCode.equalsIgnoreCase("LOYAL5") || 
                                discountCode.equalsIgnoreCase("COUPON1") || 
                                discountCode.equalsIgnoreCase("COUPON!") ||
                                discountCode.equalsIgnoreCase("NONE");
            if (!isValidCode) {
                System.out.println("Invalid discount code '" + discountCode + "' - Please try again or leave blank.");
                System.out.print("Enter discount code (LOYAL5/COUPON1/NONE or blank): ");
                discountCode = scanner.nextLine().trim();
                if (discountCode.isEmpty()) discountCode = null;
            }
        }

        System.out.print("Enter tax percent (e.g., 10): ");
        int taxPercent = 10;
        try { taxPercent = Integer.parseInt(scanner.nextLine().trim()); } catch (Exception ignored) {}

        // Suppress payment strategy output during receipt generation
        java.io.PrintStream originalOut = System.out;
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.PrintStream suppressedOut = new java.io.PrintStream(baos);
        
        try {
            System.setOut(suppressedOut);
            
            // Old behavior (smelly)
            String oldType = paymentType;
            if (paymentType.equalsIgnoreCase("CARD") && cardDigits != null && !cardDigits.isEmpty()) {
                oldType = cardDigits; // legacy CardPayment expects number inside type in this project
            }
            String oldReceipt = OrderManagerGod.processWithTax(recipe, qty, oldType, discountCode, taxPercent, false);

            // New behavior (clean, orchestrated)
            var pricing = new PricingService(new NoDiscountPolicy(), new FixedRateTaxPolicy(taxPercent));
            var printer = new ReceiptPrinter();
            var paymentFactory = new DefaultPaymentStrategyFactory();
            var checkout = new CheckoutService(new ProductFactory(), pricing, printer, paymentFactory, taxPercent);
            String newType = paymentType;
            if (paymentType.equalsIgnoreCase("CARD") && cardDigits != null && !cardDigits.isEmpty()) {
                newType = cardDigits; // pass through so strategies can use it
            }
            String newReceipt = checkout.checkout(recipe, qty, newType, discountCode, false);
            
            // Restore original output
            System.setOut(originalOut);
            
            // Display receipts
            System.out.println("\n=== Old Receipt ===\n" + oldReceipt);
            System.out.println("\n=== New Receipt ===\n" + newReceipt);
            boolean match = oldReceipt.equals(newReceipt);
            System.out.println("\nMatch: " + match);

            // Payment summary and cash change calculation
            if (paymentType.equalsIgnoreCase("CARD") && cardDigits != null) {
                String lastFour = cardDigits.substring(cardDigits.length() - 4);
                System.out.println("\nPaid with card ****" + lastFour);
            } else if (paymentType.equalsIgnoreCase("CASH")) {
                System.out.println("\nPaid with cash");
                // Parse total and calculate change
                java.util.regex.Matcher m = java.util.regex.Pattern
                    .compile("Total: \\$(\\d+\\.\\d{2})")
                    .matcher(newReceipt);
                if (m.find()) {
                    java.math.BigDecimal total = new java.math.BigDecimal(m.group(1));
                    java.math.BigDecimal paid = java.math.BigDecimal.ZERO;
                    while (paid.compareTo(total) < 0) {
                        System.out.print("Enter cash amount given: ");
                        String amtStr = scanner.nextLine().trim();
                        try {
                            java.math.BigDecimal given = new java.math.BigDecimal(amtStr);
                            if (given.compareTo(java.math.BigDecimal.ZERO) < 0) {
                                System.out.println("Amount must be non-negative.");
                                continue;
                            }
                            paid = paid.add(given);
                            if (paid.compareTo(total) < 0) {
                                System.out.println("You have paid so far: $" + paid.toPlainString());
                            }
                        } catch (Exception e) {
                            System.out.println("Invalid amount. Please enter a number like 5 or 5.00");
                        }
                    }
                    java.math.BigDecimal change = paid.subtract(total);
                    if (change.compareTo(java.math.BigDecimal.ZERO) > 0) {
                        System.out.println("Change due: $" + change.toPlainString());
                    } else {
                        System.out.println("Exact amount received. No change due.");
                    }
                } else {
                    System.out.println("Could not parse total from receipt.");
                }
            } else if (paymentType.equalsIgnoreCase("WALLET")) {
                System.out.println("\nPaid with wallet");
            }
            
        } finally {
            // Ensure original output is restored even if exception occurs
            System.setOut(originalOut);
        }
    }
}
