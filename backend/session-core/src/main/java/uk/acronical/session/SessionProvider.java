package uk.acronical.session;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * A functional contract for loading and persisting player session data.
 * <p>
 * This interface facilitates the decoupling of session logic from specific
 * storage implementations, such as SQL databases, Flat-file systems, or NoSQL solutions.
 *
 * @param <T> The type of session object managed by this provider.
 * @author Acronical
 * @since 1.0.4
 */
public interface SessionProvider<T> {

    /**
     * Retrieves the session data associated with a specific unique identifier.
     * <p>
     * Implementation of this method should handle the initialisation of new
     * session objects if no existing data is found.
     *
     * @param uuid The {@link UUID} of the player.
     * @return The loaded session object of type {@code T}.
     * @throws Exception If an error occurs during the retrieval process (e.g., SQL failure).
     */
    @NotNull
    T load(@NotNull UUID uuid) throws Exception;

    /**
     * Persists the provided session data to the underlying storage medium.
     *
     * @param uuid    The {@link UUID} of the player.
     * @param session The session object to be saved.
     * @throws Exception If an error occurs during the persistence process.
     */
    void save(@NotNull UUID uuid, @NotNull T session) throws Exception;
}
