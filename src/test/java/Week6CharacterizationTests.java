package test.java;

import main.java.smells.OrderManagerGod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderManagerGodTests {

    @BeforeEach
    void resetDiscountCode() {
        // no global state to reset anymore
    }

    @Test
    void process_withPositiveQty_calculatesTotalCorrectly() {
        String receipt = OrderManagerGod.process("ESP", 2, "CASH", null, false);
        assertTrue(receipt.contains("Order (ESP) x2"));
        assertTrue(receipt.contains("Tax (10%):"));
        assertTrue(receipt.contains("Total:"));
    }

    @Test
    void process_withZeroQty_defaultsToOne() {
        String receipt = OrderManagerGod.process("LAT", 0, "CARD", null, false);
        assertTrue(receipt.contains("x1")); // qty adjusted to 1
    }

    @Test
    void process_withLoyal5Discount_appliesPercentageDiscount() {
        String receipt = OrderManagerGod.process("CAP", 2, null, "LOYAL5", false);
        assertTrue(receipt.contains("Discount: -"));
    }

    @Test
    void process_withCoupon1Discount_appliesFixedDiscount() {
        String receipt = OrderManagerGod.process("LAT", 1, null, "COUPON1", false);
        assertTrue(receipt.contains("Discount: -"));
    }

    @Test
    void process_withUnknownDiscount_appliesNoDiscount() {
        String receipt = OrderManagerGod.process("ESP", 1, null, "RANDOMCODE", false);
        assertFalse(receipt.contains("Discount: -"));
    }

    @Test
    void process_withNoneDiscount_appliesNoDiscount() {
        String receipt = OrderManagerGod.process("LAT", 1, null, "NONE", false);
        assertFalse(receipt.contains("Discount: -"));
    }

    @Test
    void process_withVariousPayments_printsExpectedText() {
        // Just checking that it returns a receipt even with paymentType
        String cash = OrderManagerGod.process("ESP", 1, "CASH", null, false);
        String card = OrderManagerGod.process("ESP", 1, "CARD", null, false);
        String wallet = OrderManagerGod.process("ESP", 1, "WALLET", null, false);
        String unknown = OrderManagerGod.process("ESP", 1, "BITCOIN", null, false);

        assertTrue(cash.contains("Total:"));
        assertTrue(card.contains("Total:"));
        assertTrue(wallet.contains("Total:"));
        assertTrue(unknown.contains("Total:"));
    }

    @Test
    void process_discountDoesNotMakeTotalNegative() {
        // Simulate discount greater than subtotal
        String receipt = OrderManagerGod.process("ESP", 1, null, "COUPON1", false);
        assertFalse(receipt.contains("Total: -")); // total cannot be negative
    }

    @Test
    void cardPayment_accepts16Digits_andPrintsMaskedLastFour() {
        // Arrange
        String digits = "1234567890123456";
        var card = new main.java.payment.CardPayment(digits);
        var order = new main.java.order.Order(1);

        // Capture stdout
        java.io.PrintStream original = System.out;
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(baos));
        try {
            card.pay(order);
        } finally {
            System.setOut(original);
        }
        String out = baos.toString();
        assertTrue(out.contains("****3456"));
    }

    @Test
    void cash_changeCalculation_usesMoneyChangeFrom() {
        main.java.order.Money total = main.java.order.Money.of(8.15);
        main.java.order.Money cash = main.java.order.Money.of(10.00);
        main.java.order.Money change = total.changeFrom(cash);
        assertEquals(main.java.order.Money.of(1.85), change);
    }

    @Test
    void cardPayment_rejectsNullCardNumber() {
        assertThrows(IllegalArgumentException.class, () -> new main.java.payment.CardPayment(null));
    }

    @Test
    void cardPayment_rejectsTooShortCardNumber() {
        assertThrows(IllegalArgumentException.class, () -> new main.java.payment.CardPayment("123"));
    }

    // --- New tests added in Week 6 ---
    @Test
    void processWithTax_usesProvidedTaxPercent() {
        // ESP ($2.50) x2 => $5.00, tax 13% => $0.65
        String receipt = OrderManagerGod.processWithTax("ESP", 2, "CASH", null, 13, false);
        assertTrue(receipt.contains("Tax (13%): $0.65"));
        assertTrue(receipt.contains("Total: $5.65"));
    }

    @Test
    void process_couponBangAlias_appliesFixedDiscount() {
        // COUPON! should behave like COUPON1 with $1.00 off
        String receipt = OrderManagerGod.process("LAT", 2, "CASH", "COUPON!", false);
        assertTrue(receipt.contains("Discount: -$1.00"));
    }

    @Test
    void process_loyal5_showsExpectedDiscountAmount() {
        // ESP ($2.50) x2 => $5.00, LOYAL5 => $0.25 off
        String receipt = OrderManagerGod.process("ESP", 2, "CASH", "LOYAL5", false);
        assertTrue(receipt.contains("Discount: -$0.25"));
    }
}


