package main.java.order;

import main.java.factory.ProductFactory;
import main.java.catalog.Product;
import main.java.pricing.PricingService;
import main.java.pricing.ReceiptPrinter;
import main.java.payment.PaymentStrategy;
import main.java.payment.PaymentStrategyFactory;

public final class CheckoutService {
    private final ProductFactory factory;
    private final PricingService pricing;
    private final ReceiptPrinter printer;
    private final PaymentStrategyFactory paymentFactory;
    private final int taxPercent;

    public CheckoutService(ProductFactory factory, PricingService pricing, 
                           ReceiptPrinter printer, PaymentStrategyFactory paymentFactory, 
                           int taxPercent) {
        this.factory = factory;
        this.pricing = pricing;
        this.printer = printer;
        this.paymentFactory = paymentFactory;
        this.taxPercent = taxPercent;
    }

    public String checkout(String recipe, int qty, String paymentType, String discountCode, boolean printReceipt) {
        Product product = factory.create(recipe);
        if (qty <= 0) qty = 1;
        
        Money unit = (product instanceof main.java.decorators.Priced p) ? p.price() : product.basePrice();
        Money subtotal = unit.multiply(qty);
        
        // Calculate discount using original logic
        Money discount = calculateDiscount(subtotal, discountCode);
        Money discounted = subtotal.subtract(discount);
        if (discounted.asBigDecimal().signum() < 0) discounted = Money.zero();
        
        // Use PricingService for tax calculation
        var result = pricing.price(discounted);
        
        String receipt = printer.format(recipe, qty, result, taxPercent);
        
        if (printReceipt) {
            printer.print(receipt);
        }
        
        // Adapt to your Week-3 signature; if your strategy expects an Order, pass the real one here.
        // If your strategy prints based on totals, wrap in a tiny adapter and call after pricing.
        PaymentStrategy payment = paymentFactory.fromType(paymentType);
        Order order = new Order(qty);
        payment.pay(order);
        
        return receipt;
    }
    
    private Money calculateDiscount(Money subtotal, String discountCode) {
        if (discountCode == null) return Money.zero();
        
        if (discountCode.equalsIgnoreCase("LOYAL5")) {
            return Money.of(subtotal.asBigDecimal()
                .multiply(java.math.BigDecimal.valueOf(5))
                .divide(java.math.BigDecimal.valueOf(100)));
        } else if (discountCode.equalsIgnoreCase("COUPON1")) {
            return Money.of(java.math.BigDecimal.valueOf(1.0));
        } else if (discountCode.equalsIgnoreCase("NONE")) {
            return Money.zero();
        } else {
            return Money.zero();
        }
    }
}
