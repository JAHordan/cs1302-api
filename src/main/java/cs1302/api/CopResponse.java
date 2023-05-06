package cs1302.api;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a response from the UK Police Search API. THis is used by Gson to create
 * an object from the JSON response body. THis class is provided with project's starter
 * code, and the instance variables are intentionally set to package private visibility.
 */
public class CopResponse {
    int id;
    String category;
    Location location;
    @SerializedName("outcome_status") OutcomeStatus outcomeStatus;
    String month;

    /**
     * Makes the cop response object.
     * @param id for the id of the crime
     * @param category for the category of the crime
     * @param location for the location of the crime
     * @param month of the crime.
     * @param outcomeStatus for the outcome of the crime
     */
    CopResponse(int id, String category, Location location, String month,
        OutcomeStatus outcomeStatus) {
        this.id = id;
        this.category = category;
        this.location = location;
        this.month = month;
        this.outcomeStatus = outcomeStatus;
    }

    /**
     * Convert response to string.
     */
    @Override
    public String toString() {
        String outcomeStat = outcomeStatus != null ? outcomeStatus.toString() : "Not Reported";
        return "\nCrime Report  \n\n{" +
            "\n\nID: " + id +
            "\n\nCategory: " + category +
            "\n\n" + location +
            "\n\nMonth of Incident: " + month +
            "\n\nOutcome Status: " + outcomeStat + "\n\n}";
    }
} //CopResponse
