package cs1302.api;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a result in a response from the Transport API. This is used
 * by Gson to create an object from the JSON response body. This class is
 * not provided with the project's starter code, and the instance variables
 * are intentionally set t package private visibility.
 * @see <a href="https://developer.transportapi.com/examples?example=bus-route-geometry"
 >Understanding Search Results</a>
 */
public class BusResult {
    double latitude;
    double longitude;
    String locality;
    @SerializedName ("stop_name") String stopName;
    // the rest of the result is intentionally omitted since I did not use them
} //BusResult
