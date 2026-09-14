package uk.acronical.economy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The core service for managing currencies, accounts, and financial transactions.
 * <p>
 * This service acts as the central API for the framework's economy, ensuring
 * transactions are safely delegated to the atomic operations within {@link EconomyAccount}
 * to prevent race conditions and thread deadlocks.
 *
 * @author Acronical
 * @since 1.0.6
 */
public class EconomyService {

    private final Map<String, Currency> currencies = new ConcurrentHashMap<>();
    private final Map<UUID, EconomyAccount> activeAccounts = new ConcurrentHashMap<>();

    /**
     * Registers a new currency into the economy system.
     *
     * @param currency The currency to register.
     */
    public void registerCurrency(@NotNull Currency currency) {
        currencies.put(currency.getId().toLowerCase(), currency);
    }

    /**
     * Retrieves a registered currency by its identifier.
     *
     * @param id The identifier (e.g., "coins").
     * @return The currency, or null if it does not exist.
     */
    @Nullable
    public Currency getCurrency(@NotNull String id) {
        return currencies.get(id.toLowerCase());
    }

    /**
     * Retrieves an active economy account, creating one if it is missing.
     *
     * @param uuid The unique identifier of the account holder.
     * @return The active economy account.
     */
    @NotNull
    public EconomyAccount getAccount(@NotNull UUID uuid) {
        return activeAccounts.computeIfAbsent(uuid, EconomyAccount::new);
    }

    /**
     * Deposits an amount into a target's account.
     */
    @NotNull
    public TransactionResponse deposit(@NotNull UUID target, @NotNull String currencyId, @NotNull BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) return TransactionResponse.ERROR;

        Currency currency = getCurrency(currencyId);
        if (currency == null) return TransactionResponse.CURRENCY_NOT_FOUND;

        EconomyAccount account = getAccount(target);

        synchronized (account) {
            BigDecimal current = account.getBalance(currencyId);
            account.setBalance(currencyId, current.add(amount));
        }

        return TransactionResponse.SUCCESS;
    }

    /**
     * Withdraws an amount from a target's account.
     */
    @NotNull
    public TransactionResponse withdraw(@NotNull UUID target, @NotNull String currencyId, @NotNull BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) return TransactionResponse.ERROR;

        Currency currency = getCurrency(currencyId);
        if (currency == null) return TransactionResponse.CURRENCY_NOT_FOUND;

        EconomyAccount account = getAccount(target);

        synchronized (account) {
            BigDecimal current = account.getBalance(currencyId);
            if (!currency.allowsDebt() && current.compareTo(amount) < 0) return TransactionResponse.INSUFFICIENT_FUNDS;
            account.setBalance(currencyId, current.subtract(amount));
        }

        return TransactionResponse.SUCCESS;
    }

    /**
     * Transfers an amount from one account to another securely.
     */
    @NotNull
    public TransactionResponse transfer(@NotNull UUID sender, @NotNull UUID receiver, @NotNull String currencyId, @NotNull BigDecimal amount) {
        EconomyAccount senderAccount = getAccount(sender);

        synchronized (senderAccount) {
            TransactionResponse withdrawResult = withdraw(sender, currencyId, amount);
            if (withdrawResult != TransactionResponse.SUCCESS) return withdrawResult;

            TransactionResponse depositResult = deposit(receiver, currencyId, amount);
            if (depositResult != TransactionResponse.SUCCESS) {
                deposit(sender, currencyId, amount);
                return depositResult;
            }
        }

        return TransactionResponse.SUCCESS;
    }
}
