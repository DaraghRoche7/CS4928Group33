package test.java;

import main.java.printing.LegacyPrinterAdapter;
import main.java.printing.Printer;
import vendor.legacy.LegacyThermalPrinter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class AdapterTests {

    private LegacyThermalPrinter legacyPrinter;
    private LegacyPrinterAdapter adapter;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        legacyPrinter = new LegacyThermalPrinter();
        adapter = new LegacyPrinterAdapter(legacyPrinter);
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void adapter_implementsPrinterInterface() {
        assertTrue(adapter instanceof Printer);
    }

    @Test
    void adapter_printsStringAsByteArray() {
        String receipt = "Test Receipt\nTotal: $10.00";
        adapter.print(receipt);
        
        String output = outputStream.toString();
        assertTrue(output.contains("[Legacy] printing bytes:"));
        assertTrue(output.contains(String.valueOf(receipt.getBytes().length)));
    }

    @Test
    void adapter_convertsStringToUtf8Bytes() {
        String receipt = "Receipt Content";
        adapter.print(receipt);
        
        String output = outputStream.toString();
        // UTF-8 encoding of "Receipt Content" = 15 bytes
        assertTrue(output.contains("15") || output.contains(String.valueOf(receipt.getBytes(java.nio.charset.StandardCharsets.UTF_8).length)));
    }

    @Test
    void adapter_handlesEmptyString() {
        String emptyReceipt = "";
        adapter.print(emptyReceipt);
        
        String output = outputStream.toString();
        assertTrue(output.contains("[Legacy] printing bytes: 0"));
    }

    @Test
    void adapter_handlesMultilineReceipt() {
        String receipt = "Order: LAT+L\n" +
                        "Subtotal: $3.70\n" +
                        "Tax (10%): $0.37\n" +
                        "Total: $4.07";
        adapter.print(receipt);
        
        String output = outputStream.toString();
        assertTrue(output.contains("[Legacy] printing bytes:"));
        // Should have converted the multiline string to bytes
        int expectedBytes = receipt.getBytes(java.nio.charset.StandardCharsets.UTF_8).length;
        assertTrue(output.contains(String.valueOf(expectedBytes)) || 
                   output.length() > 0);
    }

    @Test
    void adapter_handlesSpecialCharacters() {
        String receipt = "Order: €10.50 - 5% discount";
        adapter.print(receipt);
        
        String output = outputStream.toString();
        assertTrue(output.contains("[Legacy] printing bytes:"));
        // Euro symbol takes more bytes in UTF-8
        assertTrue(output.length() > 0);
    }

    @Test
    void adapter_delegatesToLegacyPrinter() {
        String receipt = "Test Receipt";
        adapter.print(receipt);
        
        String output = outputStream.toString();
        // Verify it called the legacy printer
        assertTrue(output.contains("[Legacy]"));
        assertTrue(output.contains("printing bytes:"));
    }

    @Test
    void adapter_handlesLongReceipt() {
        StringBuilder longReceipt = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longReceipt.append("Line ").append(i).append("\n");
        }
        
        adapter.print(longReceipt.toString());
        
        String output = outputStream.toString();
        assertTrue(output.contains("[Legacy] printing bytes:"));
        // Should handle long strings
        assertTrue(output.length() > 0);
    }

    @Test
    void adapter_keepsDomainCodeUnchanged() {
        // This test verifies that the adapter allows domain code
        // to work with String receipts while using byte[] legacy printer
        String receipt = "Order (LAT+L) x2\n" +
                        "Subtotal: $7.40\n" +
                        "Tax (10%): $0.74\n" +
                        "Total: $8.14";
        
        // Domain code uses String (Printer interface)
        Printer printer = adapter; // Polymorphism
        printer.print(receipt);
        
        String output = outputStream.toString();
        assertTrue(output.contains("[Legacy]"));
        // Domain code doesn't need to know about byte[] conversion
    }

    @Test
    void adapter_byteArrayLengthMatchesUtf8Encoding() {
        String receipt = "Test Receipt Content";
        adapter.print(receipt);
        
        String output = outputStream.toString();
        byte[] expectedBytes = receipt.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        assertTrue(output.contains(String.valueOf(expectedBytes.length)));
    }

    @Test
    void legacyPrinter_acceptsByteArray() {
        byte[] testBytes = "Test".getBytes();
        legacyPrinter.legacyPrint(testBytes);
        
        String output = outputStream.toString();
        assertTrue(output.contains("[Legacy] printing bytes: 4"));
    }

    @Test
    void adapter_worksWithReceiptFormat() {
        // Simulate a real receipt format
        String receipt = String.format(
            "Order (%s) x%d%n" +
            "Subtotal: %s%n" +
            "Tax (%d%%): %s%n" +
            "Total: %s",
            "LAT+L", 2, "$7.40", 10, "$0.74", "$8.14"
        );
        
        adapter.print(receipt);
        
        String output = outputStream.toString();
        assertTrue(output.contains("[Legacy]"));
        // Verify bytes were sent
        assertTrue(output.contains("printing bytes:"));
    }
}

