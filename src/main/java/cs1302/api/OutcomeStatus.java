package cs1302.api;

/**
 * Outcome class for the outcome of the crime from the crime reports.
 */
public class OutcomeStatus {
    String category;
    String date;


    /**
     * Outcome class that makes the category and date of the outcome report.
     * @param category for the category of the crime
     * @param date of the outcome status.
     */
    OutcomeStatus(String category, String date) {
        this.category = category;
        this.date = date;
    }

    /**
     * Turns the outcome object into a string.
     */
    @Override
    public String toString() {
        return  "\ncategory: " + category +
            "\nOutcome Date: " + date;
    }
} //toString
