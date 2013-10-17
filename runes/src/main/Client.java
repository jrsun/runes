package main;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import client.GameClient;

public class Client {

	/**
	 * Run a GuiConnector
	 * @param args
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public static void main(final String[] args) throws UnknownHostException, IOException {    	
		Socket socket = new Socket("localhost", 1234);
		GameClient c = new GameClient(socket);
	}
}
