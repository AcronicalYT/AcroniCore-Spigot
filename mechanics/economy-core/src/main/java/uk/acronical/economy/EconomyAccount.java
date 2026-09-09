package uk.acronical.economy;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a multi-currency economic profile for a specific entity.
 * <p>
 * This model utilises {@link BigDecimal} to guarantee precision in financial
 * calculations and employs atomic operations to ensure thread-safe transactions
 * across asynchronous tasks.
 *
 * @author Acronical
 * @since 1.0.6
 */
public class EconomyAccount {

    private final UUID owner;
    private final Map<String, BigDecimal> balances = new ConcurrentHashMap<>();

    /**
     * Initialises a new economy account.
     *
     * @param owner The unique identifier of the account holder (usually a Player UUID).
     */
    public EconomyAccount(@NotNull UUID owner) {
        this.owner = owner;
    }

    @NotNull
    public UUID getOwner() {
        return owner;
    }

    /**
     * Retrieves the current balance for a specific currency.
     *
     * @param currencyId The identifier of the currency (e.g., "coins", "gems").
     * @return The current balance, defaulting to ZERO if no record exists.
     */
    @NotNull
    public BigDecimal getBalance(@NotNull String currencyId) {
        return balances.getOrDefault(currencyId.toLowerCase(), BigDecimal.ZERO);
    }

    /**
     * Hard-sets the balance of a specific currency.
     *
     * @param currencyId The identifier of the currency.
     * @param balance    The exact balance to set.
     */
    public void setBalance(@NotNull String currencyId, @NotNull BigDecimal balance) {
        balances.put(currencyId.toLowerCase(), balance);
    }

    /**
     * Retrieves an unmodifiable view of the raw balances for saving.
     *
     * @return A read-only map of all active balances.
     */
    @NotNull
    public Map<String, BigDecimal> getRawBalances() {
        return Collections.unmodifiableMap(balances);
    }
}
