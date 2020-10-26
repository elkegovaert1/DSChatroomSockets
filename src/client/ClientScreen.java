package client;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;

import javafx.application.Application;
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
import javafx.stage.Stage;

public class ClientScreen extends Application {
    private ArrayList<Thread> threads;
    public static void main(String[] args){

        launch();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        for (Thread thread: threads){
            thread.interrupt();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        threads = new ArrayList<Thread>();
        primaryStage.setTitle("Chat Client");
        primaryStage.setScene(makeInitScene(primaryStage));
        primaryStage.show();
    }

    public Scene makeInitScene(Stage primaryStage) {
        GridPane rootPane = new GridPane();
        rootPane.setPadding(new Insets(20));
        rootPane.setVgap(10);
        rootPane.setHgap(10);
        rootPane.setAlignment(Pos.CENTER);

        TextField nameField = new TextField();

        Label nameLabel = new Label("Name");
        Label errorLabel = new Label();

        Button submitClientInfoButton = new Button("Done");

        submitClientInfoButton.setOnAction(Event -> {
            Client client;
            try {
                client = new Client(nameField.getText());
                Thread clientThread = new Thread(client);
                clientThread.setDaemon(true);
                clientThread.start();
                threads.add(clientThread);

                /* Change the scene of the primaryStage */
                primaryStage.close();
                primaryStage.setScene(makeChatUI(client));
                primaryStage.show();
            }
            catch(ConnectException e){
                errorLabel.setTextFill(Color.RED);
                errorLabel.setText("Invalid host name, try again");
            }
            catch (NumberFormatException | IOException e) {
                errorLabel.setTextFill(Color.RED);
                errorLabel.setText("Invalid port number, try again");
            }

        });

        rootPane.add(nameField, 0, 0);
        rootPane.add(nameLabel, 1, 0);
        rootPane.add(submitClientInfoButton, 0, 3, 2, 1);
        rootPane.add(errorLabel, 0, 4);

        return new Scene(rootPane, 400, 400);
    }

    public Scene makeChatUI(Client client) {
        GridPane rootPane = new GridPane();
        rootPane.setPadding(new Insets(20));
        rootPane.setAlignment(Pos.CENTER);
        rootPane.setHgap(10);
        rootPane.setVgap(10);

        ListView<String> chatListView = new ListView<String>();
        chatListView.setItems(client.chatLog);

        TextField chatTextField = new TextField();
        chatTextField.setOnAction(event -> {
            client.writeToServer(chatTextField.getText());
            chatTextField.clear();
        });

        rootPane.add(chatListView, 0, 0);
        rootPane.add(chatTextField, 0, 1);

        return new Scene(rootPane, 400, 400);

    }
}
