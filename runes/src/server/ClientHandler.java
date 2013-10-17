package server;

import game.Match;
import game.Player;
import game.Spell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import rooms.Room;
import utils.utils;

public class ClientHandler implements Runnable {
	Socket socket;
	GameServer server;
	BufferedReader in;
	PrintWriter out;
	public String name;
	public Map<String, Player> players;
	
	public ClientHandler(Socket socket, GameServer server) throws IOException{
		this.socket = socket;
		this.server = server;
		players = new HashMap<String, Player>();

		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
	}
	
	public void run() {
		try {
			handleConnection(socket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void handleConnection(Socket socket) throws IOException{
		try{
			for(String line = in.readLine(); line != null; line = in.readLine()){
				String response = "";
				try{
					response = handleRequest(line);
				} catch(Exception e){
					out.println("Invalid: " + line + "\r\n");
					e.printStackTrace();
				}
				out.println(response);
			}
		} catch (SocketException se){
			se.printStackTrace();
		} finally {
			this.destroy();
		}
	}
	
	public String handleRequest(String request){
		System.out.println("Request: "+request);
		String[] tokens = request.split(" ");
		synchronized(server){
			if(tokens[0].equals("text")){
				Room room = server.rooms.get(tokens[1]);
				String message = utils.join(Arrays.copyOfRange(tokens, 2, tokens.length), " ");
				room.writeTextToAllPlayers(name, message);
				return "ok";
			} else if(tokens[0].equals("signin")){
				name = tokens[1];
				return "ok";
			} else if(tokens[0].equals("join")){
				String newRoom = tokens[1];
				if(!server.rooms.containsKey(newRoom)){
					server.createRoom(newRoom);
				}
				for(String activeRooms: players.keySet()){
					if(activeRooms.equals(newRoom)){
						return "already in";
					}
				}
				Player newPlayer = new Player(name, server.rooms.get(newRoom), this);
				players.put(newRoom, newPlayer);
				server.rooms.get(newRoom).join(newPlayer);
				return "ok";
			} else if(tokens[0].equals("leave")){
				String leaveRoom = tokens[1];
				server.rooms.get(leaveRoom).unready(players.get(leaveRoom));
				server.rooms.get(leaveRoom).leave(players.get(leaveRoom));
				players.remove(leaveRoom);
				return "ok";
			} else if(tokens[0].equals("ready")){
				Room room = server.rooms.get(tokens[1]);
				if(room.ready(players.get(room.getName()))){
					return "ok";
				} else {
					return "failed to ready";
				}
			} else if(tokens[0].equals("unready")){
				String unreadyRoom = tokens[1];
				(server.rooms.get(unreadyRoom)).unready(players.get(unreadyRoom));
				return "ok";
			} else if(tokens[0].equals("place")){
				Room room = server.rooms.get(tokens[1]);
				Match match = room.getMatch();
				int row = Integer.parseInt(tokens[2]);
				int col = Integer.parseInt(tokens[3]);
				if(match.getTurn() == players.get(room.getName()).getColor()){
					if(players.get(room.getName()).go(row, col)){ // player goes
						room.writeToAllPlayers("board "+room.getName()+" "+
					match.getBoard().toString()); // update board
						room.writeToAllPlayers("newest "+room.getName()+" "+
					String.valueOf(row)+" "+String.valueOf(col)); // update newest to highlight
						 // switches color
						if(room.isPlaying()){
							Player first = room.getFirst();
							Player second = room.getSecond();
							match.writeToFirst("spells "+room.getName()+" second "+second.getCastedSpellsString());
							match.writeToSecond("spells "+room.getName()+" first "+first.getCastedSpellsString());
							room.writeToAllPlayers(String.format("status %s %s %s %s %s %s %s",
								room.getName(), first.name, first.getHp(), first.getMp(),
								second.name, second.getHp(), second.getMp())); // update players
						}
					}
				} else {
					return "not your turn!";
				}
				return "ok";
			} else if(tokens[0].equals("list_rooms")){
				write("rooms "+server.getAllRooms().replace("lobby", "")); // remove lobby from room list so clients don't see
				return "ok";
			} else if(tokens[0].equals("list_players")){
				Room room = server.rooms.get(tokens[1]);
				String playerList = "";
				ArrayList<Player> players = (ArrayList<Player>) room.toMap().get("players");
				for(Player p: players){
					playerList += p.name + " ";
				}
				write("players "+room.getName()+" "+playerList.trim());
				return "ok";
			} else if(tokens[0].equals("bye")){
				this.destroy();
				return "ok";
			}
			return "Echoing unknown request: "+request;
		}
	}
	
	public synchronized void write(String message){
		out.println(message);
	}
	
	private void destroy(){
		for(String roomName: players.keySet()){
			server.rooms.get(roomName).unready(players.get(roomName));
			server.rooms.get(roomName).leave(players.get(roomName));
		}
		server.clients.remove(this);
	}
	
	public static void main(String[] args) throws IOException{
		GameServer g = new GameServer(1234);
		g.serve();
	}
	
}