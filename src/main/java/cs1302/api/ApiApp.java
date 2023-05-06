package cs1302.api;

import cs1302.api.CopResponse;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.control.TabPane;
import javafx.scene.text.Font;
import javafx.scene.control.ProgressBar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.net.URL;
import java.net.URI;
import java.net.http.HttpResponse.BodyHandlers;
import java.io.InputStream;
import java.util.Scanner;
import javafx.geometry.Pos;

import java.net.http.HttpRequest;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import com.google.gson.annotations.SerializedName;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.IOException;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;


/**
 * REPLACE WITH NON-SHOUTING DESCRIPTION OF YOUR APP.
 */
public class ApiApp extends Application {

    /** HTTP client. */
    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)        // uses HTTP protocol version 2 where possible
        .followRedirects(HttpClient.Redirect.NORMAL)  // always redirects, except from HTTPS to HTTP
        .build();

    /** Google {@code Gson} object for parsing JSON-formatted strings. */
    public static Gson GSON = new GsonBuilder()
        .setPrettyPrinting()                          // enable nice output when printing
        .create();

    private static final String BUSAPI = "https://transportapi.com/v3/uk/bus/route/FLDS/" +
        "40/outbound/450010688/2023-04-28/12:50/timetable.json?";
    private static final String APPKEY = "229e0cf6d01f4973246b27908273d479";
    private static final String APPID = "20610448";

    private Stage stage;
    private Scene scene;
    private VBox root;

    //STUFF UNDER ROOT
    private HBox header;
    private HBox secondRow;
    private HBox buttonRow;
    private HBox endStartRow;
    private TabPane report;
    private HBox thirdRow;
    private HBox bottom;


    //STUFF UNDER header
    private Label title;

    //STUFF UNDER secondRow
    private Label  town;
    private ComboBox<String> townDropDown;
    private Label busStop;
    private ComboBox<String> busStopDropDown;


    //STUFF UNDER buttonRow
    private Button getResult;
    private Button nextButton;
    private Button backButton;

    //STUFF UNDER endStartRow
    private Button startButton;
    private Button endButton;
    private Label searchLabel;
    private TextField increment;

    //STUFF UNDER report
    //N/A

    //STUFF UNDER thirdrow
    private Label crimeTitle;
    private Label crimeNum;

    //STUFF UNDER bottom
    private Label riskTitle;
    private Label riskNum;
    private ProgressBar progressBar;

    //Other Variables
    private String selectedNumber = null;

    //Variables for TabPane
    int displayCounter = 0;
    int reportCounter = 0;
    Tab[] displayedTabs = new Tab [10];
    String[] storageReportstr = new String [200];
    String[] displayedReportstr = new String [10];
    Label[] displayedLabels = new Label [10];

    //Variables for Bus API
    private String selectedValue = null;
    private String[] downloadedBusStops = new String [37];
    private double longi = 0.0;
    private double lati = 0.0;

    //Variables for Cop API
    private int[] downloadedCopIDs = new int [10];
    private String[] downloadedCopCategory = new String [10];
    private String[] downloadedCopContext = new String [10];
    private String[] downloadedCopOutcome = new String [10];
    private CopResponse[] downloadCopReport = new CopResponse [50];
    CopResponse[] storageReport = new CopResponse[200];
    CopResponse[] displayedReport = new CopResponse[10];
    //STUFF UNDER


    /**
     * Constructs an {@code ApiApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public ApiApp() {
        this.stage = null;
        this.scene = null;
        root = new VBox();

        //STUFF UNDER root
        header = new HBox (4);
        secondRow = new HBox (2);
        buttonRow = new HBox (2);
        endStartRow = new HBox (2);
        report = new TabPane ();
        thirdRow = new HBox (2);
        bottom = new HBox (2);

        //STUFF UNDER header
        title = new Label ("Safety on Bus Stops in Leeds, England");

        //STUFF UNDER secondRow
        town = new Label ("Town: ");
        townDropDown = new ComboBox<String>();
        busStop = new Label ("Bus Stop: ");
        busStopDropDown = new ComboBox<String>();


        //STUFF UNDER buttonRow
        getResult = new Button("Load Crime Reports");
        nextButton = new Button("Next 10 Reports");
        backButton = new Button("Previous 10 Reports");

        //STUFF UNDER endStartRow
        startButton = new Button("Go to Start");
        endButton = new Button("Go to End");
        searchLabel = new Label("Go to Report: # ");
        increment = new TextField();

        //STUFF UNDER thirdRow
        crimeTitle = new Label ("Number of Crimes at Stop");
        crimeNum = new Label ("0-10");

        //STUFF UNDER bottom
        riskTitle = new Label ("Completed Reports From March 2023");
        riskNum = new Label ("0-10");
        progressBar = new ProgressBar();
    } // ApiApp

    /** {@inheritDoc} */
    @Override
    public void init() {
        System.out.println("init() called");
        System.out.println( makeBusURI());
        root.getChildren().addAll(header, secondRow, buttonRow,
            endStartRow, report, bottom);
        header.getChildren().addAll(title);
        secondRow.getChildren().addAll(town, townDropDown, busStop, busStopDropDown);
        buttonRow.getChildren().addAll(getResult, nextButton, backButton);
        endStartRow.getChildren().addAll(startButton, endButton, searchLabel, increment);
        bottom.getChildren().addAll(riskTitle, riskNum, progressBar);
        for (int i = 0; i < 10; i++) {
            displayedTabs[i] = new Tab("INFO");
            displayedLabels[i] = new Label ("Select a town and bus stop, then press Enter to load" +
            "a 200 reports of crimes within a 1 mile radius of that Bus Stop. The reports will" +
            "be from the prior month in alphabetical order based on category of crime.");
            displayedLabels[i].setWrapText(true);
            displayedTabs[i].setContent(displayedLabels[i]);
            report.getTabs().add(displayedTabs[i]);
            displayedTabs[i].setClosable(false);
        }
        Runnable thread = () -> {
            loadLocations();
            loadCrimes();
        };
        this.townDropDown.setOnAction(event -> {
            selectedValue = this.townDropDown.getValue();
            dropDownSelection();
        });
        this.busStopDropDown.setOnAction(event -> getResult.setDisable(false));
        this.getResult.setOnAction(event -> {
            displayCounter = 0;
            reportCounter = 0;
            progressBar.setProgress(0.0);
            nextButton.setDisable(false);
            backButton.setDisable(true);
            startButton.setDisable(true);
            endButton.setDisable(false);
            getResult.setDisable(true);
            increment.setDisable(false);
            threadSmasher(thread);
        });
        this.increment.setOnAction(event -> {
            selectedNumber = this.increment.getText();
            incrementSelection();
            report.getSelectionModel().select(9);
        });
        this.nextButton.setOnAction(event -> loadReports());
        this.backButton.setOnAction(event -> previousReports());
        this.endButton.setOnAction(event -> endReports());
        this.startButton.setOnAction(event -> startReports());
    } //init

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {

        this.stage = stage;

        townDropDown.setPrefWidth(250);
        busStopDropDown.setPrefWidth(200);
        this.townDropDown.getItems().addAll("Leeds City Centre, Leeds","Quarry Hill, Leeds" ,
            "Richmond Hill, Leeds","East End Park, Leeds","Harehills, Leeds" ,"Osmondthorpe, Leeds",
            "Halton, Leeds" , "Cross Gates, Leeds" , "Swarcliffe, Leeds" , "Seacroft, Leeds");

        buttonRow.setAlignment(Pos.CENTER);
        header.setAlignment(Pos.CENTER);
        secondRow.setAlignment(Pos.CENTER);
        endStartRow.setAlignment(Pos.CENTER);
        report.setTabMinWidth(44);

        buttonRow.setSpacing(30);
        secondRow.setSpacing(5);
        report.setPrefSize(600,400);
        Font font = new Font ("Arial", 24);
        title.setFont(font);
        town.setFont(new Font(14));
        busStop.setFont(new Font(14));

        getResult.setDisable(true);
        nextButton.setDisable(true);
        backButton.setDisable(true);
        startButton.setDisable(true);
        endButton.setDisable(true);
        increment.setDisable(true);

        this.progressBar.setProgress(0.0);
        this.progressBar.setPrefWidth(330);
        scene = new Scene(root);

        // setup stage
        stage.setTitle("ApiApp!");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.sizeToScene();
        stage.show();

    } // start

    /**
     * Makes the string for the bus API.
     * @return The bus link.
     */
    private String makeBusURI() {
        return BUSAPI + "&app_id=" + APPID  + "&app_key=" + APPKEY
            + "&edge_geometry=true";
    } //makeBusURI

    /**
     * Organizes the dropdown for the town and bus stop location.
     */
    private void dropDownSelection() {

        if (selectedValue.equals("Leeds City Centre, Leeds")) {
            this.busStopDropDown.getItems().setAll("Trinity O", "Corn Exchange H" , "Cultural A");
        } else if (selectedValue.equals("Quarry Hill, Leeds")) {
            this.busStopDropDown.getItems().setAll("Cultural D", "Woodpecker Junction");
        } else if (selectedValue.equals("Richmond Hill, Leeds")) {
            this.busStopDropDown.getItems().setAll("Pontefract Lane", "Berking Avenue");
        } else if (selectedValue.equals("East End Park, Leeds")) {
            this.busStopDropDown.getItems().setAll("Raincliffe Road", "Torre Road",
                "Dawlish Terrace");
        } else if (selectedValue.equals("Harehills, Leeds")) {
            this.busStopDropDown.getItems().setAll("Shaftesbury Jct C");
        } else if (selectedValue.equals("Osmondthorpe, Leeds")) {
            this.busStopDropDown.getItems().setAll("Gipton Approach", "Halton Dial");
        } else if (selectedValue.equals("Halton, Leeds")) {
            this.busStopDropDown.getItems().setAll("Dunhill Rise", "Carden Avenue","Portage Avenue",
                "Halton Lidl", "Halton Library", "Sycamore Avenue", "Temple Walk");
        } else if (selectedValue.equals("Cross Gates, Leeds")) {
            this.busStopDropDown.getItems().setAll("Clapham Dene Road","Greenway",
                "Cross Gates Ctr A", "Cross Gates Ctr D", "Manston Park", "Pendas Drive",
                "Pendas Grove","Kelmscott Green", "Barkwick Road");
        } else if (selectedValue.equals("Swarcliffe, Leeds")) {
            this.busStopDropDown.getItems().setAll("Stanks Cross", "Stanks Rise", "Swarcliffe",
                "Grimes Dyke School", "Farndale Garth");
        } else if (selectedValue.equals("Seacroft, Leeds")) {
            this.busStopDropDown.getItems().setAll("Sherburn Approach", "Seacroft Ring Road",
                "Seacroft Bus Stn B");
        }

    } //dropDownSelection

    /**
     * Creates the HTTP request, and sends it to the HTTP Client.
     * Gets the HTTP Response in JSON and turns it into GSON.
     * Prints the Json String and calls the printBusResponse method.
     */
    private void loadLocations() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(makeBusURI()))
                .build();
            HttpResponse<String> response = HTTP_CLIENT
                .send(request, BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String jsonString = response.body();
                BusResponse busResponse = GSON
                    .fromJson(jsonString,BusResponse.class);
                printBusResponse(busResponse);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println(e);
            e.printStackTrace();
        }
    } //loadReports

    /**
     * Gets the GSON response and checks to see if the selected
     * bus stop by the user matches the any bus stops in the
     * JSON response. If there is a matching bus stop, then,
     * it set that bus stops's longitude and latitude to the
     * logi and lati double values repsectively.
     * @param busResponse from the loadLocations method.
     */
    private void printBusResponse(BusResponse busResponse) {
        for (int i = 0; i < busResponse.stops.length; i++) {
            BusResult result = busResponse.stops[i];
        }
        for (int i = 0; i < busResponse.stops.length; i++) {
            downloadedBusStops[i] = busResponse.stops[i].stopName;
            if (downloadedBusStops[i].equals(this.busStopDropDown.getValue())) {
                longi = busResponse.stops[i].longitude;
                lati = busResponse.stops[i].latitude;
            }

        }
        System.out.println(longi);
        System.out.println(lati);
    }

    /**
     * Creates the URL for the UK Police API.
     * @return the Cop Api URL.
     */
    private String makeCopURI() {
        return "https://data.police.uk/api/crimes-street/all-crime?lat=" + lati
            + "&lng=" + longi + "&date=2023-03";
    }

    /**
     * Creates the HTTP request and sends it to the HTTP Client.
     * Retrieves the Json Response, and parses it with GSON.
     * Checks the status code of the response, and if it is
     * 200, it will call the printCopResponse method.
     */
    private void loadCrimes() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(makeCopURI()))
                .build();
            HttpResponse<String> response = HTTP_CLIENT
                .send(request, BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String jsonString = response.body();
                CopResponse[] copResponse = GSON
                    .fromJson(jsonString,CopResponse[].class);
                printCopResponse(copResponse);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println(e);
            e.printStackTrace();
        } //loadCrimes
    }

    /**
     * Gets the GSON response for the Cop API. Then, create
     * 200 Cop Response objects.
     * @param copResponse from the Cop API.
     */
    private void printCopResponse(CopResponse[] copResponse) {
        progressBar.setProgress(0.0);
        System.out.println(makeCopURI());
        double downloadCounter = 0.0;
        double length = 200.0;
        for (int i = 0; i < 200; i++) {
            storageReport[i] = new CopResponse(copResponse[i].id,
            copResponse[i].category, copResponse[i].location,
            copResponse[i].month, copResponse[i].outcomeStatus);
            progressBar.setProgress(downloadCounter / length);
            System.out.println(progressBar.getProgress());
            downloadCounter++;
        }
        for (int i = 0; i < storageReport.length; i++) {
            storageReportstr[i] = storageReport[i].toString();
        }
        displayCounter = 0;
        reportCounter = 0;
        loadReports();
    }

    /**
     * Clear the TabPane then loops through to set ten cop responses.
     * Inside the loop, it will increase the displayCounter and reportCounter.
     * Then, it will create a string to turn the reportCounter into a string
     * to display as a tab.
     */
    private void loadReports() {
        if (displayCounter == 0) {
            backButton.setDisable(true);
            startButton.setDisable(true);
        } else {
            backButton.setDisable(false);
            startButton.setDisable(false);
        }
        if (displayCounter == 190) {
            nextButton.setDisable(true);
            endButton.setDisable(true);
        } else {
            nextButton.setDisable(false);
            endButton.setDisable(false);
        }

        Platform.runLater(() -> report.getTabs().clear());
        System.out.println(displayCounter);
        for (int i = 0; i < 10; i++) {
            final int index = i;
            displayedReportstr[i] = storageReportstr[displayCounter];
            Platform.runLater(() -> displayedLabels[index].setText(displayedReportstr[index]));
            displayedTabs[index].setContent(displayedLabels[index]);
            Platform.runLater(() -> report.getTabs().add(displayedTabs[index]));
            displayCounter++;
            reportCounter++;
            String str = String.valueOf(reportCounter);
            Platform.runLater(() -> displayedTabs[index].setText("#" + str));
        }
    }

    /**
     * Subtracts 20 from the displayCounter and reportCounter
     * then calls the loadReports method.
     */
    private void previousReports () {
        displayCounter -= 20;
        reportCounter -= 20;
        System.out.println(reportCounter);
        loadReports();
    }

    /**
     * Sets the displayCounter and reportCounter to zero
     * then calls the loadReports method.
     */
    private void startReports () {
        displayCounter = 0;
        reportCounter = 0;
        loadReports();
    }

    /**
     * Sets the displayCounter and reportCounter to 190
     * then calls the loadReports method.
     */
    private void endReports () {
        displayCounter = 190;
        reportCounter = 190;
        loadReports();
    }

    /**
     * Gets the value from the increment textfield.
     * Checks if the string is a int value between 1 - 200
     * inclusive, and if so, parses it and turns it into
     * a String value, and goes to the page with that value.
     */
    private void incrementSelection() {
        try {
            int num = Integer.parseInt(selectedNumber);
            int actualNumber = Integer.parseInt(selectedNumber);
            if (num <= 200 && num > 0) {
                if (num % 10 == 0) {
                    displayCounter = num - 10;
                    reportCounter = num - 10;
                    loadReports();
                    report.getSelectionModel().select(displayedTabs[9]);
                    return;
                }
                num =  (num / 10) * 10;
                displayCounter = num;
                reportCounter = num;
                int lastDigit = actualNumber % 10;
                loadReports();
                System.out.println(lastDigit);
                report.getSelectionModel().select(displayedTabs[lastDigit - 1]);
            } else {
                alertUser();
            }

        } catch (NumberFormatException e) {
            alertUser();
        }
    }

    /**
     * Alerts the user if the number they entered is
     * in the incorrect format or it is not in the specified range.
     */
    private void alertUser() {
        Alert alert = new Alert (Alert.AlertType.ERROR);
        String alertMessage = "Ensure that you have entered an whole number in numerical"
            + " format from 1 - 200 (inclusive).";
        alert.setContentText(alertMessage);
        alert.showAndWait();
    }

     /**
      * Creates and sets the daemon status of the thread.
      * @param thread is the extra thread for things that taje long.
      */
    private static void threadSmasher (Runnable thread) {
        Thread taskThread = new Thread (thread);
        taskThread.setDaemon(true);
        taskThread.start();
    }

} // ApiApp
