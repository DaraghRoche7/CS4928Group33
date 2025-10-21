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
}
