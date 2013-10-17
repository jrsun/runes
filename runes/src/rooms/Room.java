package rooms;

import game.Match;
import game.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import server.GameServer;

public class Room {
	
	GameServer server;
	String name;
	ArrayList<Player> players;
	Player first;
	Player second;
	Match match;
	boolean playing = false;

	public Room(String name, GameServer server) {
		this.name = name;
		this.server = server;
		players = new ArrayList<Player>();
	}
	
	public void join(Player player){

		writeTextToAllPlayers("Server", player.name + " has joined the room.");
		players.add(player);
		player.getHandler().players.put(name, player);
		writeTextToPlayer(player, String.format("Welcome to %s", name));
		updatePlayers();
		if(first != null){
			writeToPlayer(player, "first "+getName()+" "+first.name);	
		}
		if(second != null){
			writeToPlayer(player, "second "+getName()+" "+second != null ? second.name : "<NONE>");
		}
	}

	public boolean ready(Player player){
		boolean success = false;
		if(first != player && second != player){ // if player is not already first or second
			if(first == null){
				first = player;
				success = true;
				writeToAllPlayers("first "+getName()+" "+player.name);
			} else if(second == null){
				second = player;
				success = true;
				writeToAllPlayers("second "+getName()+" "+player.name);
			}
			if(first != null && second != null){ // both players are ready
				start();
			}
		}
		return success;
	}
	
	public void unready(Player player) throws IllegalStateException{
		if(first == player){
			first = null;
			writeToAllPlayers("first "+getName()+" <NONE>");
		} else if(second == player){
			second = null;
			writeToAllPlayers("second "+getName()+" <NONE>");
		}
	}
	
	public void leave(Player player){

		writeTextToAllPlayers("Server", player.name + " has left the room.");
		players.remove(player);
		player.getHandler().players.remove(name);
		
		updatePlayers();
		
		if(getPlayers().size() <= 0){
			server.rooms.remove(getName());
			server.writeRoomList();
		}
	}
	
	public void start(){
		match = new Match(first, second, this);
		match.start();
		playing = true;
	}
	
	public Map<String, Object> toMap(){
		Map<String, Object> info = new HashMap<String, Object>();
		String playing = ((Boolean)this.playing).toString();
		info.put("players", players);
		info.put("first", first);
		info.put("second", second);
		info.put("playing", playing);
		return info;
	}
	
	private void updatePlayers(){
		// update all players in joined room with new playerList
		String playerList = "";
		ArrayList<Player> players = getPlayers();
		for(Player p: players){
			playerList += p.name + " ";
		}
		playerList = playerList.trim();
		for(Player p: players){
			p.getHandler().write("players "+name+" "+playerList);
		}
	}

	public Match getMatch() {
		return match;
	}

	public void setMatch(Match match) {
		this.match = match;
	}

	public Player getFirst() {
		return first;
	}

	public void setFirst(Player first) {
		this.first = first;
	}

	public Player getSecond() {
		return second;
	}

	public void setSecond(Player second) {
		this.second = second;
	}
	
	
	public void writeTextToPlayer(Player player, String message){
		player.getHandler().write("text "+name+" Server "+message);
	}
	
	public void writeTextToAllPlayers(String sender, String message){
		for(Player p: players){
			p.getHandler().write("text "+name+" "+sender+" "+message);
		}
	}
	
	public void writeToPlayer(Player player, String message){
		player.getHandler().write(message);
	}
	
	public void writeToAllPlayers(String message){
		for(Player p: players){
			p.getHandler().write(message);
		}
	}
	
	public String getName(){
		return this.name;
	}
	
	
	public ArrayList<Player> getPlayers(){
		return players;
	}

	public boolean isPlaying() {
		return playing;
	}

	public void end(){
		playing = false;
		first.playing = false;
		second.playing = false;
		if(first.getHp() < second.getHp()){
			writeTextToAllPlayers(name, second.name + " has won the game!");
		} else if(first.getHp() > second.getHp()){
			writeTextToAllPlayers(name, first.name + " has won the game!");
		} else {
			writeTextToAllPlayers(name, "The game has ended in a draw!");
		}
		setFirst(null);
		setSecond(null);
		writeToAllPlayers("end "+name);
		writeToAllPlayers("repaint "+name);
	}
}
