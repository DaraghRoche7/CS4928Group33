package main.java.payment;

import main.java.order.Order;

public final class WalletPayment implements PaymentStrategy {
    private final String walletId;

    public WalletPayment(String walletId) {
        if (walletId == null) {
            throw new IllegalArgumentException("walletId required");
        }
        this.walletId = walletId;
    }

    @Override
    public void pay(Order order) {
        System.out.println(
                "[Wallet] Customer paid " + order.totalWithTax(10) +
                        " EUR using wallet " + walletId
        );
    }
}

