package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

public class Main extends Application {
    private String start = "Start listening";
    private String stop = "Stop listening";

    private Text serverPortText = new Text("Port:");
    private TextField serverPortInput = new TextField();
    private Button listeningButton = new Button(start);

    private Text tcpText = new Text("TCP");
    private Text udpText = new Text("UDP");

    private TextField udpDataSize = new TextField();
    private TextField tcpDataSize = new TextField();
    private SimpleDoubleProperty udpDataSizeProperty = new SimpleDoubleProperty();
    private SimpleDoubleProperty tcpDataSizeProperty = new SimpleDoubleProperty();
    private TextField udpSpeed = new TextField();
    private TextField tcpSpeed = new TextField();
    private SimpleLongProperty udpTimeElapsedProperty = new SimpleLongProperty();
    private SimpleLongProperty tcpTimeElapsedProperty = new SimpleLongProperty();

    private TCPServer tcpServer;
    private UDPServer udpServer;
    @Override
    public void start(Stage primaryStage) {
        Main self = this;
        serverPortInput.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        serverPortInput.setText("3301");
        serverPortInput.setPrefWidth(80);

        listeningButton.setPrefWidth(300);
        listeningButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (listeningButton.getText().contains(start)){
                    listeningButton.setText(stop);
                    tcpServer = new TCPServer(Integer.parseInt(serverPortInput.getText()), self);
                    udpServer = new UDPServer(Integer.parseInt(serverPortInput.getText()), self);
                } else {
                    listeningButton.setText(start);
                    tcpServer.disconnect();
                    udpServer.disconnect();
                }
            }
        });

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        GridPane.setHalignment(tcpText, HPos.RIGHT);
        GridPane.setHalignment(udpText, HPos.RIGHT);

        udpDataSize.setEditable(false);
        tcpDataSize.setEditable(false);
        udpSpeed.setEditable(false);
        tcpSpeed.setEditable(false);

        // Row 1
        grid.add(serverPortText, 0, 0);
        grid.add(serverPortInput, 1, 0);
        grid.add(listeningButton, 2, 0, 2, 1);
        // Row 2
        grid.add(new Text("Statistics:"), 0, 1);
        // Row 3
        grid.add(udpText, 2, 2);
        grid.add(tcpText, 3, 2);
        // Row 4
        grid.add(new Text("Data received [KB]"), 0, 3);
        grid.add(udpDataSize, 2, 3);
        grid.add(tcpDataSize, 3, 3);
        // Row 5
        grid.add(new Text("Speed [KB/s]"), 0, 4);
        grid.add(udpSpeed, 2, 4);
        grid.add(tcpSpeed, 3, 4);




        Scene scene = new Scene(grid, 500, 250);

        primaryStage.setTitle("Speed Test Server");
        primaryStage.setScene(scene);
        primaryStage.show();
        setListeners();
    }
    private void setListeners() {
        tcpDataSizeProperty.addListener((observableValue, number, newVal) -> Platform.runLater(() -> {
            tcpDataSize.setText(Long.toString(newVal.longValue()));
        }));
        tcpTimeElapsedProperty.addListener((observableValue, number, newVal) -> Platform.runLater(() -> {
            tcpSpeed.setText(Double.toString(Math.round(getTcpDataSizeProperty() * 100.0 / newVal.longValue()) / 100.0));
        }));
        udpDataSizeProperty.addListener((observableValue, number, newVal) -> Platform.runLater(() -> {
            udpDataSize.setText(Long.toString(newVal.longValue()));
        }));
        udpTimeElapsedProperty.addListener(((observableValue, number, newVal) -> Platform.runLater(() -> {
            udpSpeed.setText(Double.toString(Math.round(getUdpDataSizeProperty() * 100.0 / newVal.longValue()) / 100.0));
        })));

    }
    private void resetValues() {
        setTcpTimeElapsedProperty(0);
        setTcpDataSizeProperty(0);
        setUdpDataSizeProperty(0);
        setUdpTimeElapsedProperty(0);
    }
    public static void main(String[] args) {
        launch(args);
    }

    public double getUdpDataSizeProperty() {
        return udpDataSizeProperty.get();
    }

    public DoubleProperty udpDataSizePropertyProperty() {
        return udpDataSizeProperty;
    }

    public void setUdpDataSizeProperty(double udpDataSizeProperty) {
        this.udpDataSizeProperty.set(udpDataSizeProperty);
    }

    public double getTcpDataSizeProperty() {
        return tcpDataSizeProperty.get();
    }

    public SimpleDoubleProperty tcpDataSizePropertyProperty() {
        return tcpDataSizeProperty;
    }

    void setTcpDataSizeProperty(double tcpDataSizeProperty) {
        this.tcpDataSizeProperty.set(tcpDataSizeProperty);
    }

    public double getUdpTimeElapsedProperty() {
        return udpTimeElapsedProperty.get();
    }

    public SimpleLongProperty udpTimeElapsedPropertyProperty() {
        return udpTimeElapsedProperty;
    }

    public void setUdpTimeElapsedProperty(long udpTimeElapsedProperty) {
        this.udpTimeElapsedProperty.set(udpTimeElapsedProperty);
    }

    public double getTcpTimeElapsedProperty() {
        return tcpTimeElapsedProperty.get();
    }

    public SimpleLongProperty tcpTimeElapsedPropertyProperty() {
        return tcpTimeElapsedProperty;
    }

    public void setTcpTimeElapsedProperty(long tcpTimeElapsedProperty) {
        this.tcpTimeElapsedProperty.set(tcpTimeElapsedProperty);
    }
}
