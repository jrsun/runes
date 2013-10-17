package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rooms.Room;

public class GameServer {
	
	ServerSocket serverSocket;
	public Map<String, Room> rooms;
	public ArrayList<ClientHandler> clients;
	
	public GameServer(int port) throws IOException{

		this.serverSocket = new ServerSocket(port);
		
		clients = new ArrayList<ClientHandler>();
		rooms = new HashMap<String, Room>();
		
		rooms.put("lobby", new Room("lobby", this));
		writeRoomList();
	}
	
	public void serve() throws IOException{
		while (true) {
			// block until a client connects
			Socket socket = serverSocket.accept();
			//Create a new thread for client.           
			ClientHandler c = new ClientHandler(socket,this);
			clients.add(c);
			Thread t = new Thread(c);            
			t.start();
		}
	}
	
	public void writeToAllClients(String message){
		if(clients.size() > 0){
			for(ClientHandler c: clients){
				c.write(message);
			}
		}
	}
	
	public void writeRoomList(){
		writeToAllClients("rooms "+getAllRooms().replaceAll("lobby", ""));
	}
	
	public String getAllRooms(){
		String result = "";
		for(String name: rooms.keySet()){
			result += name + " ";
		}
		result.trim();
		return result;
	}
	
	public void createRoom(String name){
		rooms.put(name, new Room(name, this));
		writeRoomList();
	}
	
	public void close() throws IOException{
		this.serverSocket.close();
	}
}