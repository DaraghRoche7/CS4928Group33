package test.java;

import main.java.catalog.SimpleProduct;
import main.java.decorators.*;
import main.java.order.Money;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DecoratorTests {

    @Test
    void extraShot_addsToPriceAndName() {
        var base = new SimpleProduct("ESP", "Espresso", Money.of(2.50));
        var shot = new ExtraShot(base);

        assertTrue(shot.name().contains("Extra Shot"));
        assertEquals(Money.of(3.30), shot.price()); // +0.80
    }

    @Test
    void oatMilk_addsToPriceAndName() {
        var base = new SimpleProduct("LAT", "Latte", Money.of(3.20));
        var oat = new OatMilk(base);

        assertTrue(oat.name().contains("Oat Milk"));
        assertEquals(Money.of(3.70), oat.price()); // +0.50
    }

    @Test
    void syrup_flavourVariations() {
        var base = new SimpleProduct("LAT", "Latte", Money.of(3.20));
        var caramel = new Syrup(base, "Caramel");
        var vanilla = new Syrup(base, "Vanilla");

        assertTrue(caramel.name().contains("Caramel Syrup"));
        assertTrue(vanilla.name().contains("Vanilla Syrup"));

        assertEquals(Money.of(3.60), caramel.price()); // +0.40
    }

    @Test
    void sizeLarge_increasesPrice() {
        var base = new SimpleProduct("CAP", "Cappuccino", Money.of(3.00));
        var large = new SizeLarge(base);

        assertTrue(large.name().contains("Large"));
        assertEquals(Money.of(3.70), large.price()); // +0.70
    }
}
