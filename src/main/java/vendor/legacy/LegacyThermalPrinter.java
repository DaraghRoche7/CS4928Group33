package main.java.vendor.legacy;

/**
 * Legacy vendor thermal printer.
 * This is vendor code that cannot be modified.
 * It only accepts byte[] payloads for printing.
 */
public final class LegacyThermalPrinter {
    
    /**
     * Prints the given byte array payload to the thermal printer.
     * This is the legacy interface that only accepts byte arrays.
     * 
     * @param payload the byte array to print
     */
    public void legacyPrint(byte[] payload) {
        // Simulate printing to thermal printer
        // In a real implementation, this would send bytes to hardware
        System.out.println("[Legacy Printer] Printing " + payload.length + " bytes");
    }
}
