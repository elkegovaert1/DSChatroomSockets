package client;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import server.Server;

public class ClientScreen extends Application {
    private ArrayList<Thread> threads;
    private Client client;
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
    public Scene makeInitSceneWithError(Stage primaryStage, String error) {
    	GridPane rootPane = new GridPane();
        rootPane.setPadding(new Insets(20));
        rootPane.setVgap(10);
        rootPane.setHgap(10);
        rootPane.setAlignment(Pos.CENTER);

        TextField nameField = new TextField();

        Label nameLabel = new Label("Name");
        
        Label errorLabel = new Label(error);
        errorLabel.setTextFill(Color.RED);

        Button submitClientInfoButton = new Button("Done");

        submitClientInfoButton.setOnAction(Event -> {
            Client client;
            
            	try {
            		client = new Client(nameField.getText());
                    this.client = client;
                    Thread clientThread = new Thread(client);                    
                    clientThread.setDaemon(true);
                    clientThread.start();
                    threads.add(clientThread);
                    if(client.isDuplicate()) {
                    	makeInitSceneWithError(primaryStage, error);                    	
                    }else {
                    	/* Change the scene of the primaryStage */
                        primaryStage.close();
                        primaryStage.setScene(makeChatUI(primaryStage, client));
                        primaryStage.setTitle(client.getName());
                        primaryStage.show();
                    }                    
                }
                catch(ConnectException e){
                    errorLabel.setTextFill(Color.RED);
                    errorLabel.setText("Invalid host name, try again");
                }
                catch (NumberFormatException | IOException e) {
                    errorLabel.setTextFill(Color.RED);
                    errorLabel.setText("Invalid port number, try again");
                }catch(RuntimeException rte) {
                	errorLabel.setText("Username already connected");
                }            

        });

        rootPane.add(nameField, 0, 0);
        rootPane.add(nameLabel, 1, 0);
        rootPane.add(submitClientInfoButton, 0, 3, 2, 1);
        rootPane.add(errorLabel, 0, 4);

        return new Scene(rootPane, 400, 400);
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
                    this.client = client;
                    Thread clientThread = new Thread(client);                    
                    clientThread.setDaemon(true);
                    clientThread.start();
                    threads.add(clientThread);
                    
                    	/* Change the scene of the primaryStage */
                        primaryStage.close();
                        primaryStage.setScene(makeChatUI(primaryStage, client));
                        primaryStage.setTitle(client.getName());
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

    public Scene makeChatUI(Stage primaryStage, Client client) {    		
        GridPane rootPane = new GridPane();
        rootPane.setPadding(new Insets(20));
        rootPane.setAlignment(Pos.CENTER);
        rootPane.setHgap(10);
        rootPane.setVgap(10);
        
        
        ListView<String> chatListView = new ListView<String>();
        chatListView.setItems(client.chatLog);
        
        setupPriveListView(rootPane);
        
        TextField chatTextField = new TextField();
        chatTextField.setOnAction(event -> {
            client.writeToServer(chatTextField.getText());
            chatTextField.clear();
        });

        rootPane.add(chatListView, 0, 0);
        rootPane.add(chatTextField, 0, 1);
        if(this.client.isDuplicate()) {
        	return makeInitSceneWithError(primaryStage, "Username already connected");
        }else {
            return new Scene(rootPane, 600, 400);
        }
    }
    public void handleListClick(priveGesprek pg, GridPane rootPane) {
        ListView<String> priveListView = new ListView<String>();
        priveListView.setItems(pg.getBerichten());
        rootPane.add(priveListView, 1, 0);
        
        GridPane pane = new GridPane();
        
        TextField priveTextField = new TextField();
        priveTextField.setOnAction(event -> {
            client.writeToServer(Client.priveBerichtIdentifier + priveTextField.getText() + 
            		Client.priveBerichtIdentifier + client.getName()+Client.priveBerichtIdentifier+pg.getPartner());
            pg.getBerichten().add(client.getName() + " : " + priveTextField.getText());
            priveTextField.clear();
        });
        
        Button back = new Button("Back");
        back.setOnAction(Event -> {
        	rootPane.getChildren().remove(pane);
        	rootPane.getChildren().remove(priveListView);
        	setupPriveListView(rootPane);
        });
        
        pane.add(priveTextField, 0, 0);
        pane.add(back, 1, 0);
        
        rootPane.add(pane, 1, 1);
        
    }
    public void setupPriveListView(GridPane rootPane) {
    	ListView<priveGesprek> priveListView = new ListView<priveGesprek>(); 
        
        priveListView.setItems(client.getPriveBerichten());
        priveListView.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
            	
            	priveGesprek pg = priveListView.getSelectionModel().getSelectedItem();
                //System.out.println("clicked on " + pg.getPartner());
                rootPane.getChildren().remove(priveListView);
                handleListClick(pg, rootPane);
                
            }
        });
        rootPane.add(priveListView, 1, 0);
    }
}
