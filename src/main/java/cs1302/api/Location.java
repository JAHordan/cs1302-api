package cs1302.api;

/**
 * Class for the street of the crime.
 */
public class Location {
    Street street;

    /**
     * Location object for the location of the crime.
     * @param street for the street near the crime.
     */
    Location(Street street) {
        this.street = street;
    }

    /**
     * Turns the location object into a string.
     */
    @Override
    public String toString() {
        return "Street: "  + street;
    }
}
