package ee.taltech.manufacturing.connector.camel.routes;

import aQute.bnd.annotation.component.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Quick and dirty Users service implementation.
 */
@Component
public class UsersImpl implements Users {

    private final static Logger LOG = LoggerFactory.getLogger(UsersImpl.class);

    private static List<User> USERS;

    static {
        final List<User> users = new ArrayList<>();
        users.add(new User("Jill"));
        users.add(new User("Jack"));
        users.add(new User("Joe"));

        USERS = users;
    }

    @Override
    public final List<User> listAllUsers() {
        LOG.info("Requested a list of all users.");
        return new ArrayList<>(USERS);
    }

    @Override
    public final User getUser(final String name) {

        LOG.info("Requested user '{}' details.", name);

        User user = null;

        for (User u : USERS) {
            if (u.getName().equals(name)) {
                user = u;
                break;
            }
        }

        if (null == user) {
            user = new User("");
        }

        return user;
    }

    @Override
    public void addUser(User user) {
        if (null == user) {
            throw new IllegalArgumentException("user must not be null");
        }

        LOG.info("Adding user named {} ({})", user.getName(), user);

        USERS.add(user);
    }

    @Override
    public void deleteUser(String name) {
        if (null == name) {
            throw new IllegalArgumentException("name must not be null");
        }

        LOG.info("Removing user {}.", name);

        USERS.removeIf(user -> user.getName().equals(name));
    }
}