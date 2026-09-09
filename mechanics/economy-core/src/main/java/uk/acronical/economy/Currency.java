package uk.acronical.economy;

import java.math.BigDecimal;

public interface Currency {

    /**
     * The internal identifier (e.g., "gems", "coins").
     */
    String getId();

    /**
     * Formats the amount for display (e.g., "£10.50" or "100 Gems").
     */
    String format(BigDecimal amount);

    /**
     * Whether this currency allows negative balances (e.g., for loans).
     */
    default boolean allowsDebt() {
        return false;
    }
}
