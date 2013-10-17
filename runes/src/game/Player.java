package game;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import rooms.Room;
import server.ClientHandler;
import utils.utils;

public class Player {
	
	ClientHandler handler;
	int hp;
	int mp;
	int color; // + or - 1
	Board board;
	public String name;
	Player opponent;
	Room room;
	
	public boolean playing = false;
	ArrayList<Spell> spells = new ArrayList<Spell>();
	ArrayList<Spell> castedSpells = new ArrayList<Spell>();

	public Player(String name){ // this constructor for testing only
		this.name = name;
	}
	
	public Player(String name, Room room, ClientHandler handler){
		this.name = name;
		this.room = room;
		this.handler = handler;
	}
	
	public boolean go(int row, int col){
		boolean canPlaceStone = board.placeStone(this, row, col);
		if(canPlaceStone){
			room.getMatch().setTurn(-color);
			checkPlayerSpells(row, col);
			
			int stones = 0;
			for(int j=0; j<board.size; j++){
				for(int i=0; i<board.size; i++){
					if(board.board[i][j].getState() != 0){stones++;}
				}
			}
			// win conditions
			if(stones == Math.pow(board.size, 2) || opponent.getHp() <= 0 || getHp() <= 0){
				room.end();
			}
		} else {
			System.out.println("Cannot place there!");
		}
		return canPlaceStone;
	}

	public ArrayList<Spell> checkPlayerSpells(int placedRow, int placedCol){
		ArrayList<Spell> activatedSpells = new ArrayList<Spell>();
		ArrayList<ArrayList<Integer>> coordsToKill = new ArrayList<ArrayList<Integer>>();
		for(Spell spell: spells){
			if(getMp() >= spell.getManaCost()){
				ArrayList<ArrayList<Integer>> spellCoords = new ArrayList<ArrayList<Integer>>();
				spellCoords.addAll(checkForSpell(spell, placedRow, placedCol));
				if(spellCoords.size() > 0){
					activatedSpells.add(spell);
					coordsToKill.addAll(spellCoords);
				}
			}
		}
		killStones(coordsToKill);
		return activatedSpells;
	}
	
	public ArrayList<ArrayList<Integer>> checkForSpell(Spell spell, int placedRow, int placedCol){
		for(int[][] pattern : spell.getPatternArrays()){
			for(int j=Math.max(0, placedRow-pattern.length+1); j<=placedRow; j++){
				for(int i=Math.max(0, placedCol-pattern[0].length+1); i<=placedCol; i++){
					if(checkForPatternAtXY(pattern, j, i)){ // later, implement multiple spells, return array of spells with positions maybe
						ArrayList<Integer> coords = new ArrayList<Integer>();
						coords.add(j);
						coords.add(i);
						cast(spell);
						return getStonesToKill(pattern, j, i);
					}	
				}
			}
		}
		return new ArrayList<ArrayList<Integer>>();
	}
	
	public boolean checkForPatternAtXY(int[][] pattern, int row, int col){
		for(int r=0; r<pattern.length; r++){
			for(int c=0; c<pattern[0].length; c++){
				if(row+r>=board.size || col+c>=board.size){ // test if pattern would not fit on board at this XY
					return false;
				}
				if(pattern[r][c] == 1 && board.board[row+r][col+c].getState() != color){
					return false;
				}
			}
		}
		return true;
	}
	
	public ArrayList<ArrayList<Integer>> getStonesToKill(int[][] pattern, int row, int col){
		ArrayList<ArrayList<Integer>> coordsList = new ArrayList<ArrayList<Integer>>();
		for(int r=0; r<pattern.length; r++){
			for(int c=0; c<pattern[0].length; c++){
				if(pattern[r][c] == 1){
					assertEquals(color, board.board[row+r][col+c].getState());
					// TODO: better empty or used??
					//board.board[row+r][col+c].setState(utils.EMPTY); 
					ArrayList<Integer> coords = new ArrayList<Integer>();
					coords.add(row+r);
					coords.add(col+c);
					coordsList.add(coords);
				}
			}
		}
		return coordsList;
	}
	
	public void killStones(ArrayList<ArrayList<Integer>> coordsToKill){
		for(ArrayList<Integer> pair : coordsToKill){
			board.board[pair.get(0)][pair.get(1)].setState(color*utils.USED);
		}
	}

	public boolean cast(Spell spell){
		boolean canCastSpell = (getMp() >= spell.getManaCost());
		if(canCastSpell){
			changeMp(-spell.getManaCost());
			spell.effect(this, opponent);
			if(!castedSpells.contains(spell)){
				castedSpells.add(spell);
			}
		} else {
			System.out.println("Failed to cast " + spell.getName());
		}
		return canCastSpell;
	}
	
	public Player getOpponent() {
		return opponent;
	}
	public void setOpponent(Player opponent) {
		this.opponent = opponent;
	}
	public int getHp() {
		return hp;
	}
	public void setHp(int hp) {
		this.hp = hp;
	}
	public void changeHp(int val){
		this.hp += val;
	}
	public int getMp() {
		return mp;
	}
	public void setMp(int mp) {
		this.mp = mp;
	}

	public void changeMp(int val){
		this.mp += val;
	}
	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}
	
	public ArrayList<Spell> getSpells(){
		return this.spells;
	}

	public void setSpells(ArrayList<Spell> spells) {
		this.spells = spells;
	}
	
	public String getSpellString(){
		String result = "";
		for(Spell s: spells){
			result += s.getName() + " ";
		}
		return result.trim();
	}
	
	public String getCastedSpellsString(){
		String result = "";
		for(Spell s: castedSpells){
			result += s.getName() + " ";
		}
		return result.trim();
	}

	public ClientHandler getHandler() {
		return handler;
	}

	public void setHandler(ClientHandler handler) {
		this.handler = handler;
	}
	
	public Room getRoom() {
		return room;
	}
}
