package main.java.com.cafepos.payment;

public final class DefaultPaymentStrategyFactory implements PaymentStrategyFactory {

    @Override
    public PaymentStrategy fromType(String paymentType) {
        if (paymentType == null) return new CashPayment();
        return switch (paymentType.toUpperCase()) {
            case "CASH" -> new CashPayment();
            case "CARD" -> new CardPayment(paymentType);
            case "WALLET" -> new WalletPayment(paymentType);
            default -> new CashPayment();
        };
    }
}




