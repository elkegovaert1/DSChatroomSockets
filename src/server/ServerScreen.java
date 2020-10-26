package server;

import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class ServerScreen extends Application {
    public static ArrayList<Thread> threads;
    public static void main(String[] args){
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        threads = new ArrayList<Thread>();
        primaryStage.setTitle("Chat Server");
        primaryStage.setScene(makeUI(primaryStage));
        primaryStage.show();

    }

    public Scene makeUI(Stage primaryStage) throws IOException {
        GridPane rootPane = new GridPane();
        rootPane.setAlignment(Pos.CENTER);
        rootPane.setPadding(new Insets(20));
        rootPane.setVgap(10);
        rootPane.setHgap(10);


        Server server = new Server(5050);
        Thread serverThread = (new Thread(server));
        serverThread.setName("Server Thread");
        serverThread.setDaemon(true);
        serverThread.start();
        threads.add(serverThread);

        /* Make the server log ListView */
        Label logLabel = new Label("Server Log");
        ListView<String> logView = new ListView<String>();
        ObservableList<String> logList = server.serverLog;
        logView.setItems(logList);

        /* Make the client list ListView */
        Label clientLabel = new Label("Clients Connected");
        ListView<String> clientView = new ListView<String>();
        ObservableList<String> clientList = server.clientNames;
        clientView.setItems(clientList);

        /* Add the view to the pane */
        rootPane.add(logLabel, 0, 0);
        rootPane.add(logView, 0, 1);
        rootPane.add(clientLabel, 0, 2);
        rootPane.add(clientView, 0, 3);

        primaryStage.show();

        return new Scene(rootPane, 400, 300);
    }
}
