package game;

import java.util.ArrayList;
import java.util.Random;

import rooms.Room;

public abstract class Spell {
	private ArrayList<Square[][]> patterns = new ArrayList<Square[][]>();
	private int manaCost;
	private String color;
	protected Random gen = new Random();
	
	protected String message;
	protected String name;
	protected Player caster;
	protected Player target;

	public void effect(Player caster, Player target){
		this.caster = caster;
		this.target = target;
	}
	
	protected void writeEffect(){
		caster.room.writeTextToAllPlayers("Game", "/"+color+" "+message);
	}
	
	protected Square[][] makeSquares(int[][] patternArray){
		Square[][] pattern = new Square[patternArray.length][patternArray[0].length];
		for(int j=0;j<patternArray.length;j++){
			for(int i=0;i<patternArray[0].length;i++){
				pattern[j][i] = new Square(patternArray[j][i]);
			}
		}
		return pattern;
	}
	
	public ArrayList<int[][]> getPatternArrays(){
		ArrayList<int[][]> patternArrays = new ArrayList<int[][]>();
		for(Square[][] pattern : patterns){
			int[][] patternArray = new int[pattern.length][pattern[0].length];
			for(int j=0;j<pattern.length;j++){
				for(int i=0;i<pattern[0].length;i++){
					patternArray[j][i] = pattern[j][i].getState();
				}
			}
			patternArrays.add(patternArray);
		}
		
		return patternArrays;
	}
	
	public ArrayList<String> getPatternStrings(){
		ArrayList<String> patternStrings = new ArrayList<String>();
		for(int[][] patternArray : getPatternArrays()){
			String s = "<html><font size='5' face='courier'>";
			for(int[] row: patternArray){
				for(int state: row){
					if(state==0){
						s += ("-");
					} else if(state==1){
						s += ("X");
					}
				}
				s += ("<br>");
			}
			s += "</font></html>";
			patternStrings.add(s);
		}
		return patternStrings;
	}
	
	// getters and setters
	public ArrayList<Square[][]> getPatterns(){ return patterns; }
	
	public void addPattern(int[][] patternArray){
		this.patterns.add(makeSquares(patternArray));
	}

	public int getManaCost(){ return manaCost; }
	
	public void setManaCost(int manaCost){ this.manaCost = manaCost; }

	public String getMessage() { return message; }

	public void setMessage(String message) { this.message = message; }
	
	public void addMessage(String toAdd) { this.message += toAdd; }

	public String getName() { return name; }

	public void setName(String name) { this.name = name; }
	
	public void setColor(String color){ this.color = color; }
}
