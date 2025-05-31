package domain.core.employee;

/**
 * Represents a site in the company's network.
 */
public class Site {
    private final int siteId;
    private final String address;
    private final String name;

    /**
     * Constructs a new Site.
     *
     * @param siteId Unique identifier of the site.
     * @param address Physical address of the site.
     * @param name Name of the site.
     */
    public Site(int siteId, String address, String name) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }

        this.siteId = siteId;
        this.address = address;
        this.name = name;
    }

    public int getSiteId() {
        return siteId;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Site{" +
                "siteId=" + siteId +
                ", address='" + address + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Site)) return false;
        Site site = (Site) o;
        return siteId == site.siteId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(siteId);
    }
} 