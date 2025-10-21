package main.java.smells;

import main.java.order.Money;
import main.java.order.Order;
import main.java.payment.*;
import main.java.factory.ProductFactory;
import main.java.catalog.Product;

public class OrderManagerGod {
    public static int TAX_PERCENT = 10;
    public static String LAST_DISCOUNT_CODE = null;

    public static String process(String recipe, int qty, String paymentType, String discountCode, boolean printReceipt) {
        ProductFactory factory = new ProductFactory();
        Product product = factory.create(recipe);

        Money unitPrice;
        try {
            var priced = product instanceof main.java.decorators.Priced p ? p.price() : product.basePrice();
            unitPrice = priced;
        } catch (Exception e) {
            unitPrice = product.basePrice();
        }

        if (qty <= 0) qty = 1;

        Money subtotal = unitPrice.multiply(qty);
        Money discount = Money.zero();

        if (discountCode != null) {
            if (discountCode.equalsIgnoreCase("LOYAL5")) { 
                discount = Money.of(subtotal.asBigDecimal()
                    .multiply(java.math.BigDecimal.valueOf(5))
                    .divide(java.math.BigDecimal.valueOf(100)));
            } else if (discountCode.equalsIgnoreCase("COUPON1")) {
                discount = Money.of(1.00);
            } else if (discountCode.equalsIgnoreCase("NONE")) {
                discount = Money.zero();
            } else {
                discount = Money.zero();
            }
            LAST_DISCOUNT_CODE = discountCode;
        }

        Money discounted = Money.of(subtotal.asBigDecimal().subtract(discount.asBigDecimal()));
        if (discounted.asBigDecimal().signum() < 0) discounted = Money.zero();

        var tax = Money.of(discounted.asBigDecimal()
            .multiply(java.math.BigDecimal.valueOf(TAX_PERCENT))
            .divide(java.math.BigDecimal.valueOf(100)));
    
        var total = discounted.add(tax);
        PaymentStrategy payment = new CashPayment(); 
        return processPayment(recipe, qty, subtotal, discount, tax, total, payment, printReceipt);
    }
    	public static String processPayment(String recipe, int qty, Money subtotal, Money discount,
            Money tax, Money total, PaymentStrategy paymentStrategy,
            boolean printReceipt) {

		// --- Build receipt ---
		StringBuilder receipt = new StringBuilder();
		receipt.append("Order (").append(recipe).append(") x").append(qty).append("\n");
		receipt.append("Subtotal: ").append(String.format("%.2f", subtotal.asBigDecimal())).append("\n");
		
		if (discount.asBigDecimal().signum() > 0) {
			receipt.append("Discount: -").append(String.format("%.2f", discount.asBigDecimal())).append("\n");
		}
		
		receipt.append("Tax (").append(TAX_PERCENT).append("%): ").append(String.format("%.2f", tax.asBigDecimal())).append("\n");
		receipt.append("Total: ").append(String.format("%.2f", total.asBigDecimal()));
		
		String out = receipt.toString();
    	
		if (printReceipt) {
			System.out.println(out);
		}
		
		// --- Execute payment ---
		Order orderForPayment = new Order(qty);
		paymentStrategy.pay(orderForPayment);
		
		return out;
		}
    }