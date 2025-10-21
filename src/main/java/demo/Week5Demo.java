package main.java.demo;

import main.java.factory.ProductFactory;
import main.java.catalog.Product;
import main.java.order.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.math.BigDecimal;

public final class Week5Demo {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ProductFactory factory = new ProductFactory();
        Order order = new Order(OrderIds.next());

        System.out.println("=== Welcome to the Coffee Shop CLI ===");

        boolean moreDrinks = true;

        while (moreDrinks) {
            System.out.println("\nChoose a base drink:");
            System.out.println("1. Espresso");
            System.out.println("2. Latte");
            System.out.println("3. Cappuccino");
            System.out.print("Your choice: ");
            String baseChoice = scanner.nextLine().trim();

            String baseCode = switch (baseChoice) {
                case "1" -> "ESP";
                case "2" -> "LAT";
                case "3" -> "CAP";
                default -> {
                    System.out.println("Invalid choice, defaulting to Espresso.");
                    yield "ESP";
                }
            };

            List<String> addons = new ArrayList<>();

            System.out.print("Add extra shot? (a: yes, b: no): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("a")) addons.add("SHOT");

            System.out.print("Add oat milk? (a: yes, b: no): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("a")) addons.add("OAT");

            System.out.print("Add syrup? (a: yes, b: no): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("a")) {
                System.out.println("Choose syrup flavour:");
                System.out.println("1. Caramel");
                System.out.println("2. Vanilla");
                System.out.println("3. White Chocolate");
                System.out.print("Your choice: ");
                String syrupChoice = scanner.nextLine().trim();
                String flavour = switch (syrupChoice) {
                    case "1" -> "Caramel";
                    case "2" -> "Vanilla";
                    case "3" -> "White Chocolate";
                    default -> "Vanilla";
                };
                addons.add("SYP:" + flavour);
            }

            System.out.print("Make it large? (a: yes, b: no): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("a")) addons.add("L");

            String recipe = baseCode + (addons.isEmpty() ? "" : "+" + String.join("+", addons));
            Product product = factory.create(recipe);

            System.out.print("Quantity: ");
            int qty = 1;
            try {
                qty = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException ignored) {}

            order.addItem(new LineItem(product, qty));

            System.out.print("Add another drink? (a: yes, b: no): ");
            moreDrinks = scanner.nextLine().trim().equalsIgnoreCase("a");
        }

        // === Print receipt ===
        System.out.println("\n=== RECEIPT ===");
        System.out.println("Order #" + order.id());
        for (LineItem li : order.items()) {
            System.out.println(" - " + li.product().name() + " x" + li.quantity() + " = " + li.lineTotal());
        }
        System.out.println("-----------------------------");
        System.out.println("Subtotal: " + order.subtotal());
        System.out.println("Tax (10%): " + order.taxAtPercent(10));
        Money total = order.totalWithTax(10);
        System.out.println("Total: " + total);
        System.out.println("-----------------------------");

     // === Payment Loop ===
        Money amountPaid = Money.zero();

        while (amountPaid.asBigDecimal().compareTo(total.asBigDecimal()) < 0) {
            Money remaining = total.subtract(amountPaid);
            System.out.println("Amount still remaining: " + remaining);
            System.out.print("Choose payment method (1: Cash, 2: Card): ");
            String method = scanner.nextLine().trim();

            switch (method) {
                case "1" -> { // CASH
                    System.out.print("Enter cash amount given: ");
                    try {
                        double cash = Double.parseDouble(scanner.nextLine().trim());
                        Money cashGiven = Money.of(cash);
                        amountPaid = amountPaid.add(cashGiven);

                        if (amountPaid.asBigDecimal().compareTo(total.asBigDecimal()) >= 0) {
                            Money change = amountPaid.subtract(total);
                            if (change.asBigDecimal().compareTo(BigDecimal.ZERO) > 0) {
                                System.out.println("Change due: " + change);
                            }
                            break;
                        } else {
                            System.out.println("You have paid so far: " + amountPaid);
                        }
                    } catch (Exception e) {
                        System.out.println("Invalid amount entered. Please try again.");
                    }
                }

                case "2" -> { // CARD
                    // Must pay the full remaining balance
                    System.out.println("Charging remaining balance of " + remaining + " to your card...");
                    amountPaid = amountPaid.add(remaining);
                }

                default -> System.out.println("Invalid payment method. Please choose 1 (Cash) or 2 (Card).");
            }

            // Stop once fully paid
            if (amountPaid.asBigDecimal().compareTo(total.asBigDecimal()) >= 0) {
                break;
            }
        }

        // === Final Payment Summary ===
        System.out.println("=============================");
        System.out.println("Payment complete!");
        System.out.println("Total Paid: " + amountPaid);
        if (amountPaid.asBigDecimal().compareTo(total.asBigDecimal()) > 0) {
            Money change = amountPaid.subtract(total);
            System.out.println("Change Given: " + change);
        }
        System.out.println("Thank you for visiting!\n");


    }
}
