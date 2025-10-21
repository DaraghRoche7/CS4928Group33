package main.java.order;

public final class KitchenDisplay implements OrderObserver {
    @Override
    public void updated(Order order, String eventType) {
        if ("itemAdded".equals(eventType)) {
            if (!order.items().isEmpty()) {
                var lastItem = order.items().get(order.items().size() - 1);
                System.out.println("[Kitchen] Order #" + order.id() + ": "
                        + lastItem.quantity() + "x "
                        + lastItem.product().name() + " added");
            } else {
                System.out.println("[Kitchen] Order #" + order.id() + ": item added");
            }
        } else if ("paid".equals(eventType)) {
            System.out.println("[Kitchen] Order #" + order.id() + ": payment received");
        }
    }
}
