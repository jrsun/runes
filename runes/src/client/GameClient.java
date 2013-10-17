package client;

import game.Spell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import clientGUI.MainGUI;
import clientGUI.RoomGUI;

public class GameClient {
	Socket socket;
	PrintWriter out;
	public MainGUI gui;
	public Map<String, RoomGUI> activeRooms;
	private Object lock = new Object();
	
	public GameClient(final Socket socket) throws IOException{
		this.socket = socket;
		socket.setKeepAlive(true);
		activeRooms = new HashMap<String, RoomGUI>();
		out = new PrintWriter(socket.getOutputStream(), true);
		
		Runnable listenerForServer = new Runnable(){
			public void run(){
				try {
					BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				    String message;
				    while ((message = in.readLine()) != null) {
				        handleServerResponse(message);
				    }
				} catch (IOException e) {
					e.printStackTrace();
					notifyEnd(e);
				} finally {
					try {
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		//Create a thread for server listener and start the thread.
		Thread serverThread = new Thread(listenerForServer,"serverThread");
		serverThread.start();
	}
	
	protected void notifyEnd(IOException e) {
		// TODO Auto-generated method stub
		String disc = "/red Disconnected! :(";
		for(RoomGUI rGUI: activeRooms.values()){
			rGUI.addToChatWindow("Server", disc);
		}
		gui.addToChatWindow("Server", disc);
	}

	private synchronized void handleServerResponse(String message){
		if(!message.equals("ok")){
			System.out.println("Received response: "+message); //TODO: parse server result
		}
		
		String[] tokens = message.split(" ");
		synchronized(lock){
			if(tokens[0].equals("text")){
				String roomName = tokens[1];
				String sender = tokens[2];
				String text = utils.utils.join(
						Arrays.copyOfRange(tokens, 3, tokens.length), " ");
				if(roomName.equals("lobby")){
					gui.addToChatWindow(sender, text);
				} else {
					activeRooms.get(roomName).addToChatWindow(sender, text);
				}
			} else if(tokens[0].equals("rooms")){
				String[] roomList = Arrays.copyOfRange(tokens, 1, tokens.length);
				gui.getRoomModel().clear();
				for(String name: roomList){
					gui.getRoomModel().addElement(name);
				}
			} else if(tokens[0].equals("players")){
				String roomName = tokens[1];
				String[] playerList = Arrays.copyOfRange(tokens, 2, tokens.length);
				if(roomName.equals("lobby")){
					gui.getPlayerModel().clear();
					for(String name: playerList){
						gui.getPlayerModel().addElement(name);
					}
				} else {
					RoomGUI rGUI = activeRooms.get(roomName);
					rGUI.getPlayerModel().clear();
					for(String name: playerList){
						rGUI.getPlayerModel().addElement(name);
					}
				}
			} else if(tokens[0].equals("first")){
				String roomName = tokens[1];
				String firstName = tokens[2].equals("<NONE>") ? " " : tokens[2];
				RoomGUI rGUI = activeRooms.get(roomName);
				rGUI.setFirst(firstName);
			} else if(tokens[0].equals("second")){
				String roomName = tokens[1];
				String secondName = tokens[2].equals("<NONE>") ? " " : tokens[2];
				RoomGUI rGUI = activeRooms.get(roomName);
				rGUI.setSecond(secondName);
			} else if(tokens[0].equals("start")){
				String roomName = tokens[1];
				RoomGUI rGUI = activeRooms.get(roomName);
				rGUI.removeReady();
				rGUI.setPlaying(true);
				rGUI.repaint();
			} else if(tokens[0].equals("board")){
				String roomName = tokens[1];
				String boardRepr = tokens[2];
				RoomGUI rGUI = activeRooms.get(roomName);
				rGUI.updateBoard(boardRepr);
			} else if(tokens[0].equals("newest")){
				String roomName = tokens[1];
				int x = Integer.valueOf(tokens[2]);
				int y = Integer.valueOf(tokens[3]);
				RoomGUI rGUI = activeRooms.get(roomName);
				rGUI.paintBoard(x, y);
			} else if(tokens[0].equals("status")){
				String roomName = tokens[1];
				String p1Name = tokens[2];
				String p1HP = tokens[3];
				String p1MP = tokens[4];
				String p2Name = tokens[5];
				String p2HP = tokens[6];
				String p2MP = tokens[7];
				RoomGUI rGUI = activeRooms.get(roomName);
				rGUI.updatePlayerInfo(p1HP, p1MP, p2HP, p2MP);
			} else if(tokens[0].equals("spells")){
				String roomName = tokens[1];
				String whichPlayer = tokens[2];
				RoomGUI rGUI = activeRooms.get(roomName);
				if(whichPlayer.equals("first")){
					rGUI.populateFirstSpellsPanel(Arrays.copyOfRange(tokens, 3, tokens.length));
				} else if(whichPlayer.equals("second")){
					rGUI.populateSecondSpellsPanel(Arrays.copyOfRange(tokens, 3, tokens.length));
				}
			} else if(tokens[0].equals("end")){
				String roomName = tokens[1];
				RoomGUI rGUI = activeRooms.get(roomName);
				rGUI.end();
			} else if(tokens[0].equals("repaint")){
				String roomName = tokens[1];
				RoomGUI rGUI = activeRooms.get(roomName);
				rGUI.repaint();
			}
		}
	}
	
	public void sendRequest(String message){
		System.out.println("Request: "+message);
		synchronized(lock){
			out.println(message);
		}
	}
	
	public Socket getSocket(){
		return socket;
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException{
		Socket s = new Socket("localhost", 1234);
		GameClient g = new GameClient(s);
	}
}