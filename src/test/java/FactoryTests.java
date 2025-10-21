package test.java;

import main.java.factory.ProductFactory;
import main.java.order.LineItem;
import main.java.order.Money;
import main.java.order.Order;
import main.java.catalog.Product;
import main.java.catalog.SimpleProduct;
import main.java.decorators.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FactoryTests {

    private final ProductFactory factory = new ProductFactory();

    @Test
    void espressoWithExtras_buildsCorrectProduct() {
        Product p = factory.create("ESP+SHOT+OAT");

        // Check that the name contains all components in order
        String name = p.name();
        assertTrue(name.contains("Espresso"), "Name should contain base product");
        assertTrue(name.contains("Extra Shot"), "Name should contain Extra Shot");
        assertTrue(name.contains("Oat Milk"), "Name should contain Oat Milk");
    }

    @Test
    void largeLatte_buildsCorrectProduct() {
        Product p = factory.create("LAT+L");

        String name = p.name();
        assertTrue(name.contains("Latte"), "Name should contain base product");
        assertTrue(name.contains("Large"), "Name should contain size modifier");
    }



    @Test
    void invalidRecipe_throws() {
        assertThrows(IllegalArgumentException.class, () -> factory.create("INVALID"));
    }

    @Test
    void nullOrBlankRecipe_throws() {
        assertThrows(IllegalArgumentException.class, () -> factory.create(null));
        assertThrows(IllegalArgumentException.class, () -> factory.create(""));
    }
    
    @Test
    void factoryVsManualConstruction() {
        Product viaFactory = new ProductFactory().create("ESP+SHOT+OAT+L");
        Product viaManual = new SizeLarge(
                                new OatMilk(
                                    new ExtraShot(
                                        new SimpleProduct("P-ESP", "Espresso", Money.of(2.50))
                                    )
                                )
                            );

        assertEquals(viaManual.name(), viaFactory.name(), "Factory and manual names should match");

        Priced factoryPriced = (Priced) viaFactory;
        Priced manualPriced = (Priced) viaManual;

        assertEquals(manualPriced.price(), factoryPriced.price(), "Factory and manual prices should match");

        Order order1 = new Order(1L);
        order1.addItem(new LineItem(viaFactory, 1));

        Order order2 = new Order(2L);
        order2.addItem(new LineItem(viaManual, 1));

        assertEquals(order1.subtotal(), order2.subtotal(), "Subtotals should match");
        assertEquals(order1.totalWithTax(10), order2.totalWithTax(10), "Total with tax should match");
    }

}
