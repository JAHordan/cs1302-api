package cs1302.api;

/**
 * Class that gets the street name for the crime.
 */
public class Street {
    String name;

    /**
     * Gets the street object for the crime.
     * @param name for the name of the street.
     */
    Street(String name) {
        this.name = name;
    }

    /**
     * Turns the street object into a String.
     */
    @Override
    public String toString() {
        String nameChecker = name != "On or near" ? name.toString() : "Not Reported";
        return  nameChecker;
    }
}
