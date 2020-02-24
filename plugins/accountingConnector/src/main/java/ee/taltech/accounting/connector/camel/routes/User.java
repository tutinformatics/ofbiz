package ee.taltech.accounting.connector.camel.routes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Immutable User.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public final class User {

    private final String name;

    /**
     * Empty constructor for Jackson.
     */
    protected User() {
        name = null;
    }

    public User(final String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name; // Serialize to json
    }
}