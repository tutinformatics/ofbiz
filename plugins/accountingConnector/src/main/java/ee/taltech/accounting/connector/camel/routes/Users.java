package ee.taltech.accounting.connector.camel.routes;

import java.util.List;

/**
 * Sample User repository interface.
 */
public interface Users {


    /**
     * Produces a collection of all known users.
     *
     * @return Collection of all known users.
     */
    List<User> listAllUsers();

    /**
     * Produces a specific user by name.
     *
     * @param name User name.
     * @return The user with name.
     */
    User getUser(String name);

    /**
     * Adds a new user.
     * <p>
     *     Caution: Side effects.
     * </p>
     * @param user User to add.
     */
    void addUser(User user);

    /**
     * Delete an user by name.
     * <p>
     *     Caution: Side effects.
     * </p>
     * @param name User name to be deleted.
     */
    void deleteUser(String name);
}