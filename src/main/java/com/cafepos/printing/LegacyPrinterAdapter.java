package main.java.com.cafepos.printing;

import main.java.vendor.legacy.LegacyThermalPrinter;

public final class LegacyPrinterAdapter implements Printer {
    private final LegacyThermalPrinter legacyPrinter;

    public LegacyPrinterAdapter(LegacyThermalPrinter legacyPrinter) {
        this.legacyPrinter = legacyPrinter;
    }

    @Override
    public void print(String receiptText) {
        // Convert String receipt to byte[] for legacy printer
        byte[] payload = receiptText.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        legacyPrinter.legacyPrint(payload);
    }
}

