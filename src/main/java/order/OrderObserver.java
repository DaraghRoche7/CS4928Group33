package main.java.order;

public interface OrderObserver {
    void updated(Order order, String eventType);
}


