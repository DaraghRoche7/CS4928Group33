package main.java.com.cafepos.order;

import main.java.com.cafepos.common.Money;
import main.java.com.cafepos.payment.PaymentStrategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public final class Order implements OrderPublisher {

    private final long id;
    private final List<LineItem> items = new ArrayList<>();
    private final List<OrderObserver> observers = new ArrayList<>();
    private final BigDecimal amount;

    public Order(long id) {
        this.id = id;
		this.amount = null;
    }

    public long id() {
        return id;
    }

    public List<LineItem> items() {
        return items;
    }

    @Override
    public void register(OrderObserver o) {
        if (o == null) throw new IllegalArgumentException("observer required");
        if (!observers.contains(o)) {
            observers.add(o);
        }
    }

    @Override
    public void unregister(OrderObserver o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(Order order, String eventType) {
        for (OrderObserver o : observers) {
            o.updated(order, eventType);
        }
    }

    public void addItem(LineItem li) {
        if (li == null) throw new IllegalArgumentException("line item required");
        items.add(li);
        notifyObservers(this, "itemAdded");
    }

    public Money subtotal() {
        return items.stream()
                .map(LineItem::lineTotal)
                .reduce(Money.zero(), Money::add);
    }

    public Money taxAtPercent(int percent) {
        return subtotal().multiply(percent / 100.0);
    }
    
    public Money multiply(double factor) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(factor)));
    }

    public Money totalWithTax(int percent) {
        return subtotal().add(taxAtPercent(percent));
    }

    public void pay(PaymentStrategy strategy) {
        if (strategy == null) throw new IllegalArgumentException("strategy required");
        strategy.pay(this);
        notifyObservers(this, "paid");
    }

    public void markReady() {
        notifyObservers(this, "ready");
    }
    
}

