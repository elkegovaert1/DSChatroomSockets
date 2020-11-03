package client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class priveGesprek {
	private ObservableList<String> berichten;
	private String partner;
	private Client client;
	
	public priveGesprek(Client c, String p) {
		client = c;
		partner = p;
		berichten = FXCollections.observableArrayList();
	}
	public String toString() {
		return partner;
	}
	public ObservableList<String> getBerichten() {
		return berichten;
	}
	public void setBerichten(ObservableList<String> berichten) {
		this.berichten = berichten;
	}
	public String getPartner() {
		return partner;
	}
	public void setPartner(String partner) {
		this.partner = partner;
	}
	public Client getClient() {
		return client;
	}
	public void setClient(Client client) {
		this.client = client;
	}
	
	

}
