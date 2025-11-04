package main.java.command;

import main.java.catalog.Product;
import main.java.order.Money;
import main.java.factory.ProductFactory;
import main.java.order.LineItem;
import main.java.order.Order;
import main.java.payment.PaymentStrategy;

public final class OrderService {
    private final ProductFactory factory = new ProductFactory();
    private final Order order;

    public OrderService(Order order) {
        this.order = order;
    }

    public void addItem(String recipe, int qty) {
        Product p = factory.create(recipe);
        order.addItem(new LineItem(p, qty));
        System.out.println("[Service] Added " + p.name() + " x" + qty);
    }

    public void removeLastItem() {
        var items = new java.util.ArrayList<>(order.items());
        if (!items.isEmpty()) {
            var last = items.get(items.size() - 1);
            items.remove(items.size() - 1);

            try {
                var field = Order.class.getDeclaredField("items");
                field.setAccessible(true);
                field.set(order, items);
            } catch (Exception e) {
                throw new IllegalStateException("Could not remove item reflectively; adapt your Order API.");
            }
            System.out.println("[Service] Removed last item");
        }
    }

    public Money totalWithTax(int percent) {
        return order.totalWithTax(percent);
    }

    public void pay(PaymentStrategy strategy, int taxPercent) {
        var total = order.totalWithTax(taxPercent);
        strategy.pay(order);
        System.out.println("[Service] Payment processed for total " + total);
    }

    public Order order() {
        return order;
    }
}

