package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import client.Client;
import client.ClientScreen;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


public class ServerScreen extends Application {
    public static List<Thread> threads;
    public static void main(String[] args){
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        threads = new ArrayList<>();
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

        Label clientLabel = new Label("Clients Connected");
        ListView<String> clientView = new ListView<String>();
        ObservableList<String> clientList = server.clientNames;
        clientView.setItems(clientList);

        rootPane.add(clientLabel, 0, 0);
        rootPane.add(clientView, 0, 1);

        primaryStage.show();

        return new Scene(rootPane, 400, 300);
    }
}
