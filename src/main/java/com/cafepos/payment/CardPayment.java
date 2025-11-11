package main.java.com.cafepos.payment;

import main.java.com.cafepos.order.Order;

public final class CardPayment implements PaymentStrategy {
    private final String cardNumber;
    private final String maskedCardNumber;

    public CardPayment(String cardNumber) {
        if (cardNumber == null) {
            throw new IllegalArgumentException("cardNumber required");
        }
        if (cardNumber.length() < 4) {
            throw new IllegalArgumentException("cardNumber must have at least 4 digits");
        }

        this.cardNumber = cardNumber;
        this.maskedCardNumber = maskCardNumber(this.cardNumber);

        // print masked card number once (constructor)
        System.out.println("Card: " + this.maskedCardNumber);
    }

    private static String maskCardNumber(String digits) {
        // always return "****" + last four digits
        String lastFour = digits.substring(digits.length() - 4);
        return "****" + lastFour;
    }

    @Override
    public void pay(Order order) {
        System.out.println(
                "[Card] Customer paid " + order.totalWithTax(10) +
                        " EUR using card " + maskedCardNumber
        );
    }
}


