package main;

import game.Board;

import java.io.IOException;

import server.GameServer;

public class Server {

	/**
	 * Start a server on the port 1234.
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {			
		
		System.out.println("Starting server...");
		GameServer gameServer = new GameServer(4321);
		gameServer.serve();

	}

}
