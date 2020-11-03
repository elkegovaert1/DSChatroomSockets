package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import server.Server;

public class Client implements Runnable {
	
	public static final String priveBerichtIdentifier = "mb54fea";
	public static final String connectedClientsIdentifier = "µ(&56fz";
	public static final String connectedClientsSplitter = "g5ef68zfz3";
	public static final String disconnectedClientsIdentifier = "a&p7!3";
	
    private Socket clientSocket;
    private BufferedReader serverToClientReader;
    private PrintWriter clientToServerWriter;
    private String name;
    public ObservableList<String> chatLog;
    public ObservableList<priveGesprek> priveBerichten;

    public Client(String name) throws IOException {

        clientSocket = new Socket("localhost", 5050);

        serverToClientReader = new BufferedReader(new InputStreamReader(
                clientSocket.getInputStream()));
        clientToServerWriter = new PrintWriter(
                clientSocket.getOutputStream(), true);
        chatLog = FXCollections.observableArrayList();
        priveBerichten = FXCollections.observableArrayList();

        this.name = name;
        clientToServerWriter.println(name);
    }

    public void writeToServer(String input) {
    	if(input.startsWith(priveBerichtIdentifier)) {
    		clientToServerWriter.println(input);
    	}
    	else {
            clientToServerWriter.println(name + " : " + input);
    	}
    }

    public void run() {
        while (true) {
            try {
                final String inputFromServer = serverToClientReader.readLine();
                
                //new client is connected
                if(inputFromServer.startsWith(connectedClientsIdentifier)) {  
                	
                	String temp = inputFromServer.substring(connectedClientsIdentifier.length(), inputFromServer.length());
                	String[] strarray = temp.split(connectedClientsSplitter);
                	
                	for(int i=0;i<strarray.length;i++) {
                		if(!strarray[i].equals(this.name)) {
                			boolean isAlAanwezig = false;
                			for(priveGesprek p : priveBerichten) {
                				if(p.getPartner().equals(strarray[i])) {
                					isAlAanwezig = true;
                					break;
                				}
                			}
                			if(!isAlAanwezig) {
                				priveGesprek pg = new priveGesprek(this, strarray[i]);
                    			priveBerichten.add(pg);                    			
                			}                			
                		}
                	}
                }
                //client is disconnected from server
                else if (inputFromServer.startsWith(disconnectedClientsIdentifier)) {
                	String disconnectedClient = inputFromServer.substring(disconnectedClientsIdentifier.length(), inputFromServer.length());
                	for(priveGesprek p : priveBerichten) {
                		if(p.getPartner().equals(disconnectedClient)) {
                			priveBerichten.remove(p);
                			break;
                		}
                	}
                }
                //receive a pm from server
                else if(inputFromServer.startsWith(priveBerichtIdentifier)) {
                	String temp = inputFromServer.substring(priveBerichtIdentifier.length(), inputFromServer.length());
                	String[] arr = temp.split(priveBerichtIdentifier);
                	for(priveGesprek pg : priveBerichten) {
                		if(pg.getPartner().equals(arr[1])) {
                			pg.getBerichten().add(arr[1] + " : " + arr[0]);
                		}
                	}
                }
                //receive a groupmessage from server
                else {
                    Platform.runLater(() -> chatLog.add(inputFromServer));
                }

            } catch (SocketException e) {
                Platform.runLater(() -> chatLog.add("Error in server"));
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public ObservableList<priveGesprek> getPriveBerichten(){
    	return priveBerichten;
    }
    public String getName() {
    	return name;
    }
}
