package utils;

import game.Spell;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

import spells.Bokken;
import spells.Crystal;
import spells.Cure;
import spells.Fireball;
import spells.MagicMissile;
import spells.Reaper;
import spells.TornadoSpin;

public class utils {

	public static final int BOARD_SIZE = 10;
	public static final int USED = 2;
	
	// square status constants
	public static final int EMPTY = 0;
	public static final int WHITE = 1;
	public static final int BLACK = -1;
	public static final int WHITE_USED = 2;
	public static final int BLACK_USED = -2;

    public static final Border blueBorder = BorderFactory.createLineBorder(Color.BLUE,3);
	
	public static final String SERVER = "Server";
	public static final Color GREEN = Color.getHSBColor(100f/360, .59f, .92f);
	public static final Color DARK_GREEN = Color.getHSBColor(127f/360, .87f, .80f);
	
	public static final MagicMissile MAGICMISSILE = new MagicMissile();
	public static final Cure CURE = new Cure();
	public static final Fireball FIREBALL = new Fireball();
	public static final Crystal CRYSTAL = new Crystal();
	public static final Bokken BOKKEN = new Bokken();
	public static final TornadoSpin TORNADOSPIN = new TornadoSpin();
	public static final Reaper REAPER = new Reaper();
	
	public static final Map<String, Spell> SPELLS = new HashMap<String, Spell>();
	
	static
	{
		SPELLS.put("MAGICMISSILE", MAGICMISSILE);
		SPELLS.put("CURE", CURE);
		SPELLS.put("FIREBALL", FIREBALL);
		SPELLS.put("CRYSTAL", CRYSTAL);
		SPELLS.put("BOKKEN", BOKKEN);
		SPELLS.put("TORNADOSPIN", TORNADOSPIN);
		SPELLS.put("REAPER", REAPER);
	}
	
	public static ArrayList<Spell> getRandomSpellSet(){
		ArrayList<Spell> randomSpells = new ArrayList<Spell>();
		Random gen = new Random();
		while(randomSpells.size() < 2){
			Object[] spells = SPELLS.values().toArray();
			Spell randomSpell = (Spell) spells[gen.nextInt(spells.length)];
			if(!randomSpells.contains(randomSpell)){
				randomSpells.add(randomSpell);
			}
		}
		return randomSpells;
	}
	
	public static ArrayList<Integer> pair(int row, int col){
		ArrayList<Integer> result = new ArrayList<Integer>();
		result.add(row);
		result.add(col);
		return result;
	}
	
	public static String capitalize(String s){
		String first = String.valueOf(s.charAt(0));
		return first.toUpperCase() + s.substring(1);
	}
	
	public static String join(String[] tokens, String delim) {
	    StringBuilder sb = new StringBuilder();
	    String loopDelim = "";
	    for(String s : tokens) {
	        sb.append(loopDelim);
	        sb.append(s);            
	        loopDelim = delim;
	    }
	    return sb.toString();
	}

	public static String join(ArrayList<String> tokens, String delim) {
		 StringBuilder sb = new StringBuilder();
		    String loopDelim = "";
		    for(String s : tokens) {
		        sb.append(loopDelim);
		        sb.append(s);            
		        loopDelim = delim;
		    }
		    return sb.toString();
	}
}
