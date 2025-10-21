package test.java;

import java.util.ArrayList;
import java.util.List;

import main.java.catalog.SimpleProduct;
import main.java.order.*;
import main.java.payment.PaymentStrategy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Week04Tests {
	@Test
    void observers_notified_on_item_add() {
        var sp = new SimpleProduct("A", "A", Money.of(2));
        var o = new Order(1);
        o.addItem(new LineItem(sp, 1)); // baseline
        List<String> events = new ArrayList<>();
        o.register((order, evt) -> events.add(evt));
        o.addItem(new LineItem(sp, 1));
        assertTrue(events.contains("itemAdded"));
    }

    @Test
    void multiple_observers_receive_ready_event() {
        var o = new Order(2);

        List<String> observer1Events = new ArrayList<>();
        List<String> observer2Events = new ArrayList<>();

        o.register((order, evt) -> observer1Events.add(evt));
        o.register((order, evt) -> observer2Events.add(evt));

        o.markReady();

        assertTrue(observer1Events.contains("ready"), "Observer 1 should receive 'ready'");
        assertTrue(observer2Events.contains("ready"), "Observer 2 should receive 'ready'");
    }
    @Test
    void payment_strategy_called_with_fake_class() {
        var p = new SimpleProduct("A", "A", Money.of(5));
        var order = new Order(42);
        order.addItem(new LineItem(p, 1));

        var fake = new FakePaymentStrategy();
        order.pay(fake);

        assertTrue(fake.isCalled(), "Payment strategy should be called");
    }

    @Test
    void payment_strategy_called_with_lambda() {
        var p = new SimpleProduct("A", "A", Money.of(5));
        var order = new Order(42);
        order.addItem(new LineItem(p, 1));

        final boolean[] called = {false};
        PaymentStrategy fake = o -> called[0] = true;

        order.pay(fake);

        assertTrue(called[0], "Payment strategy should be called");
    }
//Actual test
    @Test
    void payment_strategy_called() {
        var p = new SimpleProduct("A", "A", Money.of(5));
        var order = new Order(42);
        order.addItem(new LineItem(p, 1));
        final boolean[] called = {false};
        PaymentStrategy fake = o -> called[0] = true;
        order.pay(fake);
        assertTrue(called[0], "Payment strategy should be called");
    }

}
