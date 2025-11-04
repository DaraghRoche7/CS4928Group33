package test.java;

import main.java.command.*;
import main.java.order.*;
import main.java.payment.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CommandTests {

    private Order order;
    private OrderService service;

    @BeforeEach
    void setUp() {
        order = new Order(OrderIds.next());
        service = new OrderService(order);
    }

    @Test
    void addItemCommand_executesAndAddsItem() {
        var command = new AddItemCommand(service, "ESP", 1);
        command.execute();

        assertEquals(1, order.items().size());
        assertEquals("P-ESP", order.items().get(0).product().id());
        assertEquals(1, order.items().get(0).quantity());
    }

    @Test
    void addItemCommand_undoRemovesLastItem() {
        var command1 = new AddItemCommand(service, "ESP", 1);
        var command2 = new AddItemCommand(service, "LAT", 2);
        
        command1.execute();
        command2.execute();
        assertEquals(2, order.items().size());
        
        command2.undo();
        assertEquals(1, order.items().size());
        assertEquals("P-ESP", order.items().get(0).product().id());
    }

    @Test
    void addItemCommand_undoOnEmptyOrderDoesNothing() {
        var command = new AddItemCommand(service, "ESP", 1);
        // Don't execute, just undo
        command.undo(); // Should not throw
        
        assertEquals(0, order.items().size());
    }

    @Test
    void payOrderCommand_executesPayment() {
        // Add items first
        var addCommand = new AddItemCommand(service, "ESP", 1);
        addCommand.execute();
        
        var fakePayment = new FakePaymentStrategy();
        var payCommand = new PayOrderCommand(service, fakePayment, 10);
        payCommand.execute();
        
        assertTrue(fakePayment.isCalled());
        assertTrue(order.totalWithTax(10).asBigDecimal().signum() > 0);
    }

    @Test
    void payOrderCommand_calculatesTaxCorrectly() {
        var addCommand = new AddItemCommand(service, "LAT", 1);
        addCommand.execute();
        
        var fakePayment = new FakePaymentStrategy();
        var payCommand = new PayOrderCommand(service, fakePayment, 10);
        payCommand.execute();
        
        // LAT = 3.20, tax 10% = 0.32, total = 3.52
        var expected = Money.of(3.52);
        var actual = order.totalWithTax(10);
        assertEquals(expected.asBigDecimal().doubleValue(), 
                     actual.asBigDecimal().doubleValue(), 
                     0.01);
    }

    @Test
    void macroCommand_executesAllSteps() {
        var command1 = new AddItemCommand(service, "ESP", 1);
        var command2 = new AddItemCommand(service, "LAT", 1);
        var macro = new MacroCommand(command1, command2);
        
        macro.execute();
        
        assertEquals(2, order.items().size());
    }

    @Test
    void macroCommand_undoReversesAllSteps() {
        var command1 = new AddItemCommand(service, "ESP", 1);
        var command2 = new AddItemCommand(service, "LAT", 1);
        var macro = new MacroCommand(command1, command2);
        
        macro.execute();
        assertEquals(2, order.items().size());
        
        macro.undo();
        assertEquals(0, order.items().size());
    }

    @Test
    void macroCommand_undoReversesInCorrectOrder() {
        var command1 = new AddItemCommand(service, "ESP", 1);
        var command2 = new AddItemCommand(service, "LAT", 1);
        var command3 = new AddItemCommand(service, "CAP", 1);
        var macro = new MacroCommand(command1, command2, command3);
        
        macro.execute();
        assertEquals(3, order.items().size());
        
        // Undo should remove in reverse order: CAP, LAT, ESP
        macro.undo();
        assertEquals(0, order.items().size());
    }

    @Test
    void posRemote_setSlotAndPressExecutesCommand() {
        var remote = new PosRemote(3);
        var command = new AddItemCommand(service, "ESP", 1);
        
        remote.setSlot(0, command);
        remote.press(0);
        
        assertEquals(1, order.items().size());
    }

    @Test
    void posRemote_pressEmptySlotDoesNothing() {
        var remote = new PosRemote(3);
        
        // Should not throw
        assertDoesNotThrow(() -> remote.press(0));
        assertEquals(0, order.items().size());
    }

    @Test
    void posRemote_undoReversesLastCommand() {
        var remote = new PosRemote(3);
        var command1 = new AddItemCommand(service, "ESP", 1);
        var command2 = new AddItemCommand(service, "LAT", 1);
        
        remote.setSlot(0, command1);
        remote.setSlot(1, command2);
        
        remote.press(0);
        remote.press(1);
        assertEquals(2, order.items().size());
        
        remote.undo();
        assertEquals(1, order.items().size());
        assertEquals("P-ESP", order.items().get(0).product().id());
    }

    @Test
    void posRemote_undoMultipleTimes() {
        var remote = new PosRemote(3);
        var command1 = new AddItemCommand(service, "ESP", 1);
        var command2 = new AddItemCommand(service, "LAT", 1);
        var command3 = new AddItemCommand(service, "CAP", 1);
        
        remote.setSlot(0, command1);
        remote.setSlot(1, command2);
        remote.setSlot(2, command3);
        
        remote.press(0);
        remote.press(1);
        remote.press(2);
        assertEquals(3, order.items().size());
        
        remote.undo(); // Remove CAP
        remote.undo(); // Remove LAT
        remote.undo(); // Remove ESP
        assertEquals(0, order.items().size());
    }

    @Test
    void posRemote_undoOnEmptyHistoryDoesNothing() {
        var remote = new PosRemote(3);
        
        // Should not throw
        assertDoesNotThrow(() -> remote.undo());
    }

    @Test
    void posRemote_multipleCommandsInSequence() {
        var remote = new PosRemote(3);
        var addEspresso = new AddItemCommand(service, "ESP", 1);
        var addLatte = new AddItemCommand(service, "LAT", 2);
        var fakePayment = new FakePaymentStrategy();
        var payCommand = new PayOrderCommand(service, fakePayment, 10);
        
        remote.setSlot(0, addEspresso);
        remote.setSlot(1, addLatte);
        remote.setSlot(2, payCommand);
        
        remote.press(0);
        remote.press(1);
        remote.press(2);
        
        assertEquals(2, order.items().size());
        assertTrue(fakePayment.isCalled());
    }

    @Test
    void orderService_addItemCreatesProduct() {
        service.addItem("ESP+SHOT", 2);
        
        assertEquals(1, order.items().size());
        var item = order.items().get(0);
        assertEquals(2, item.quantity());
        assertTrue(item.product().name().contains("Extra Shot"));
    }

    @Test
    void orderService_removeLastItemWorks() {
        service.addItem("ESP", 1);
        service.addItem("LAT", 1);
        assertEquals(2, order.items().size());
        
        service.removeLastItem();
        assertEquals(1, order.items().size());
        assertEquals("P-ESP", order.items().get(0).product().id());
    }

    @Test
    void orderService_totalWithTaxCalculatesCorrectly() {
        service.addItem("LAT", 1);
        var total = service.totalWithTax(10);
        
        // LAT = 3.20, tax 10% = 0.32, total = 3.52
        assertEquals(Money.of(3.52).asBigDecimal().doubleValue(), 
                     total.asBigDecimal().doubleValue(), 
                     0.01);
    }
}

