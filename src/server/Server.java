package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import client.Client;
import client.ClientScreen;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class Server implements Runnable {
    private int portNumber;
    private ServerSocket socket;
    private ArrayList<Socket> clients;
    private ArrayList<ClientThread> clientThreads;
    public static ObservableList<String> clientNames;
    public Server(int portNumber) throws IOException {
        this.portNumber = portNumber;
        clientNames = FXCollections.observableArrayList();
        clients = new ArrayList<>();
        clientThreads = new ArrayList<>();
        socket = new ServerSocket(portNumber);
        

    }

    public void startServer() {

        try {
            socket = new ServerSocket(this.portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        try {
            while (true) {

                final Socket clientSocket = socket.accept();

                clients.add(clientSocket);
                ClientThread clientThreadHolderClass = new ClientThread(clientSocket, this);
                Thread clientThread = new Thread(clientThreadHolderClass);
                clientThreads.add(clientThreadHolderClass);
                clientThread.setDaemon(true);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void clientDisconnected(ClientThread client) {

        Platform.runLater(() -> {
        	String s = Client.disconnectedClientsIdentifier + clientNames.get(clientThreads.indexOf(client));
            clients.remove(clientThreads.indexOf(client));
            clientNames.remove(clientThreads.indexOf(client));
            clientThreads.remove(clientThreads.indexOf(client));            
            writeToAllSockets(s);
        });
        


    }

    public void writeToAllSockets(String input) {
        for (ClientThread clientThread : clientThreads) {
            clientThread.writeToServer(input);
        }
    }
    public void writeToSingleSocket(String input, String sender, String receiver){
    	for(ClientThread clientThread : clientThreads) {
    		if(clientThread.getClientName().equals(receiver)) {
    			clientThread.writeToServer(Client.priveBerichtIdentifier + input + Client.priveBerichtIdentifier + sender);
    		}
    	}
    
    	
    }
    public String getOnlineClientsString() {
    	String s = Client.connectedClientsIdentifier;
    	if(clientNames.size()>0) {
    		s = s + clientNames.get(0);
    		if(clientNames.size()>1) {
        		for(int i=1; i<clientNames.size();i++) {
            		s = s + Client.connectedClientsSplitter + clientNames.get(i);
            	}
    		}     		
    	}    	
    	return s;
    }
    public ObservableList<String> getClientNames(){
    	return clientNames;
    }
}
