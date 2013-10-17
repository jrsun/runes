package tests;

import static org.junit.Assert.*;
import game.Player;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.ClientHandler;
import server.GameServer;

/**
 * Test case to see if the new conversation is created properly with the user as its member. Also to check if sending message to this
 * conversation works properly along with the Conversation's history.
 * @category no_didit
 */
public class ClientServerTest extends GameServer{
	
	public ClientServerTest() throws IOException {
		super(1234);
	}
	
	@After
    public void tearDown() throws IOException {
		this.close();
    }

	@Test
	public void testJoin() throws IOException{ 
		//Some arbitrary socket
		Socket sock = new Socket("localhost",1234);
		//Create the client thread.
		ClientHandler client1 = new ClientHandler(sock, this);
		client1.handleRequest("Guest lobby signin Bob");
		client1.handleRequest("Bob lobby join abc");
		ArrayList<Player> players = ((ArrayList<Player>)this.rooms.get("abc").toMap().get("players"));
		assertEquals("Bob", players.get(0).name);
	}
	
	@Test
	public void testReady() throws IOException{ 
		Socket sock = new Socket("localhost",1234);
		ClientHandler client1 = new ClientHandler(sock, this);
		client1.handleRequest("Guest lobby signin Joe");
		client1.handleRequest("Joe lobby join abc");
		client1.handleRequest("Joe abc ready");
		String name = ((Player)this.rooms.get("abc").toMap().get("first")).name;
		assertEquals("Joe", name);
	}
	
	@Test
	public void testMultipleClients() throws IOException{ 
		Socket sock1 = new Socket("localhost",1234);
		ClientHandler client1 = new ClientHandler(sock1, this);
		client1.handleRequest("Guest lobby signin Joe");
		
		Socket sock2 = new Socket("localhost",1234);
		ClientHandler client2 = new ClientHandler(sock2, this);
		client2.handleRequest("Guest lobby signin Schmoe");
		
		ArrayList<Player> players = (ArrayList<Player>) this.rooms.get("lobby").toMap().get("players");
		assertEquals(2, players.size());
	}
	
	@Test
	public void testStartGame() throws IOException{
		Socket sock1 = new Socket("localhost",1234);
		ClientHandler client1 = new ClientHandler(sock1, this);
		client1.handleRequest("Guest lobby signin Joe");
		client1.handleRequest("Joe lobby join game");
		client1.handleRequest("Joe game ready");
		
		Socket sock2 = new Socket("localhost",1234);
		ClientHandler client2 = new ClientHandler(sock2, this);
		client2.handleRequest("Guest lobby signin Schmoe");
		client2.handleRequest("Schmoe lobby join game");
		client2.handleRequest("Schmoe game ready");
		
		assertEquals("true", this.rooms.get("game").toMap().get("playing"));
	}
}
