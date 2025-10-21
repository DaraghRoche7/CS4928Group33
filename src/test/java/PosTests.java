package test.java;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

import main.java.catalog.*;
import main.java.order.*;


class PosTests {

    @Test
    void money_addition() {
        Money m1 = Money.of(5.00);
        Money m2 = Money.of(3.50);
        assertEquals(Money.of(8.50), m1.add(m2));
    }

    @Test
    void money_multiplication() {
        Money m = Money.of(2.50);
        assertEquals(Money.of(7.50), m.multiply(3));
    }

    @Test
    void money_doubleValue() {
        Money m = Money.of(6.25);
        Money doubled = m.doubleValue();
        assertEquals(Money.of(12.50), doubled);
    }
    
    @Test
    void order_totals() {
        var p1 = new SimpleProduct("A", "A", Money.of(2.50));
        var p2 = new SimpleProduct("B", "B", Money.of(3.50));
        var o = new Order(1);
        o.addItem(new LineItem(p1, 2));
        o.addItem(new LineItem(p2, 1));

        assertEquals(Money.of(8.50), o.subtotal());
        assertEquals(Money.of(0.85), o.taxAtPercent(10));
        assertEquals(Money.of(9.35), o.totalWithTax(10));
    }

    @Test
    void order_empty() {
        var o = new Order(2);
        assertEquals(Money.of(0.00), o.subtotal());
        assertEquals(Money.of(0.00), o.taxAtPercent(10));
        assertEquals(Money.of(0.00), o.totalWithTax(10));
    }

    @Test
    void order_item_quantity() {
        var p = new SimpleProduct("C", "C", Money.of(4.00));
        var o = new Order(3);
        o.addItem(new LineItem(p, 3));
        assertEquals(Money.of(12.00), o.subtotal());
    }
}
