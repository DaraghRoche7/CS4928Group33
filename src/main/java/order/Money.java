package main.java.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Money implements Comparable<Money> {

    private final BigDecimal amount;

    public Money(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.amount = amount.setScale(2, RoundingMode.HALF_EVEN);
    }
    
    public Money changeFrom(Money cashGiven) {
        return cashGiven.subtract(this); 
    }
    
    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    public static Money of(double value) {
        return new Money(BigDecimal.valueOf(value));
    }
    
    public static Money of(BigDecimal value) {
        return new Money(value);
    }

    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    public Money subtract(Money other) {
        return new Money(this.amount.subtract(other.amount));
    }

    public Money multiply(double d) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(d)));
    }

    public Money doubleValue() {
        return this.multiply(2);
    }

    public BigDecimal asBigDecimal() {
        return amount;
    }

    @Override
    public int compareTo(Money other) {
        return this.amount.compareTo(other.amount);
    }

    @Override
    public String toString() {
        return "$" + amount.toPlainString();
    }
    
    public static BigDecimal toBigDecimal(Object value) {
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        } else if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        } else if (value instanceof String) {
            return new BigDecimal((String) value);
        } else {
            throw new IllegalArgumentException("Cannot convert to BigDecimal: " + value);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money)) return false;
        Money money = (Money) o;
        return amount.equals(money.amount);
    }

    public int hashCode() {
        return Objects.hash(amount);
    }
    
    public Money negate() {
        return new Money(amount.negate());
    }
}
