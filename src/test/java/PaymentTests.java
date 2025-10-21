package test.java;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

import main.java.catalog.SimpleProduct;
import main.java.order.*;
import main.java.payment.*;

class PaymentTests {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUpStreams() {
        outContent.reset();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void cardPayment_validCard_printsMasked() {
        var o = new Order(10);
        o.addItem(new LineItem(new SimpleProduct("X", "X", Money.of(5.00)), 2));

        PaymentStrategy payment = new CardPayment("1234567890123456");
        payment.pay(o);

        String output = outContent.toString().trim();
        assertTrue(output.contains("[Card]"));
        assertTrue(output.contains("****3456"));
        assertTrue(output.contains("11.00 EUR")); // subtotal 10.00 + tax 1.00
    }

    @Test
    void cardPayment_invalidCard_throws() {
        assertThrows(IllegalArgumentException.class, () -> new CardPayment("12"));
        assertThrows(IllegalArgumentException.class, () -> new CardPayment(null));
    }

    @Test
    void cashPayment_printsCorrectAmount() {
        var o = new Order(11);
        o.addItem(new LineItem(new SimpleProduct("Y", "Y", Money.of(3.00)), 3));

        PaymentStrategy payment = new CashPayment();
        payment.pay(o);

        String output = outContent.toString().trim();
        assertTrue(output.startsWith("[Cash]"));
        assertTrue(output.contains("9.90 EUR")); // subtotal 9.00 + tax 0.90
    }

    @Test
    void payment_emptyOrder() {
        var o = new Order(12);

        new CashPayment().pay(o);
        String cashOutput = outContent.toString().trim();
        assertTrue(cashOutput.contains("0.00 EUR"));

        outContent.reset();
        new CardPayment("9876").pay(o);
        String cardOutput = outContent.toString().trim();
        assertTrue(cardOutput.contains("0.00 EUR"));
        assertTrue(cardOutput.contains("****9876"));
    }
    @Test
    void partialCashThenCardCompletesPayment() {
        Order order = new Order(100);
        order.addItem(new LineItem(new SimpleProduct("LAT", "Latte", Money.of(3.00)), 2)); // $6.00
        Money total = order.totalWithTax(10); // $6.60

        // Simulate paying $5 cash first
        Money amountPaid = Money.of(5.00);
        Money remaining = total.subtract(amountPaid);
        assertEquals("$1.60", remaining.toString());

        // Card must pay remaining balance exactly
        amountPaid = amountPaid.add(remaining);
        assertEquals(total, amountPaid, "Final payment should equal total amount");
    }

    @Test
    void overpayCashGivesChange() {
        Order order = new Order(101);
        order.addItem(new LineItem(new SimpleProduct("ESP", "Espresso", Money.of(2.50)), 1)); // $2.50
        Money total = order.totalWithTax(10); // $2.75

        Money cashGiven = Money.of(5.00);
        Money change = cashGiven.subtract(total);
        assertEquals("$2.25", change.toString());
    }
}
