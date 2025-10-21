package main.java.order;

//CustomerNotifier.java — You will complete missing parts

public final class CustomerNotifier implements OrderObserver {
 @Override
 public void updated(Order order, String eventType) {
     System.out.println("[Customer] Dear customer, your Order #"
             + order.id() + " has been updated: " + eventType + ".");
 }
}


