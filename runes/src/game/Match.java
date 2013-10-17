package game;

import java.util.ArrayList;
import java.util.Random;

import rooms.Room;
import utils.utils;

public class Match {
	
	Room room;
	Board board;
	Player first;
	Player second;
	int whoseTurn;
	
	public Match(Player first, Player second, Room room){
		board = new Board(utils.BOARD_SIZE);
		this.first = first;
		this.second = second;
		this.room = room;
	}
	
	public void start(){
		
		// randomize player spells, set player stats
		first.setOpponent(second);
		first.setBoard(board);
		first.setColor(utils.WHITE);
		first.setHp(20);
		first.setMp(100);
		first.setSpells(utils.getRandomSpellSet());
		
		second.setOpponent(first);
		second.setBoard(board);
		second.setColor(utils.BLACK);
		second.setHp(20);
		second.setMp(100);
		second.setSpells(utils.getRandomSpellSet());
		
		writeToFirst("spells "+room.getName()+" first "+first.getSpellString());
		writeToSecond("spells "+room.getName()+" second "+second.getSpellString());
		
		first.playing = true;
		second.playing = true;

		room.writeTextToAllPlayers(utils.capitalize(room.getName()), String.format(
				"The game between %s and %s has started!", first.name, second.name));
		room.writeToAllPlayers("start "+room.getName());
		whoseTurn = utils.WHITE;
	}
	
	public void writeToPlayingPlayers(String message){
		first.getHandler().write(message);
		second.getHandler().write(message);
	}
	
	public void writeToFirst(String message){
		first.getHandler().write(message);
	}
	
	public void writeToSecond(String message){
		second.getHandler().write(message);
	}
	
	public Board getBoard(){
		return board;
	}
	
	public void setTurn(int next){
		whoseTurn = next;
	}
	
	public int getTurn(){
		return whoseTurn;
	}
}
