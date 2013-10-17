package spells;

import game.Player;
import game.Spell;

public class Reaper extends Spell{

	static final int[][] PATTERN_ONE = {{1,1,1,1}, {0,0,0,1}};
	static final int[][] PATTERN_TWO = {{1,1,1,1}, {1,0,0,0}};
	static final int[][] PATTERN_THREE = {{0,0,0,1}, {1,1,1,1}};
	static final int[][] PATTERN_FOUR = {{1,0,0,0}, {1,1,1,1}};
	static final int[][] PATTERN_FIVE = {{1,1},{1,0},{1,0},{1,0}};
	static final int[][] PATTERN_SIX = {{1,1},{0,1},{0,1},{0,1}};
	static final int[][] PATTERN_SEVEN = {{1,0},{1,0},{1,0},{1,1}};
	static final int[][] PATTERN_EIGHT = {{0,1},{0,1},{0,1},{1,1}};
	static final int MANA_COST = 5;
	static final String COLOR = "silver";
	
	public Reaper(){
		addPattern(PATTERN_ONE);
		addPattern(PATTERN_TWO);
		addPattern(PATTERN_THREE);
		addPattern(PATTERN_FOUR);
		addPattern(PATTERN_FIVE);
		addPattern(PATTERN_SIX);
		addPattern(PATTERN_SEVEN);
		addPattern(PATTERN_EIGHT);
		setManaCost(MANA_COST);
		setName("Reaper");
		setColor(COLOR);
	}

	@Override
	public void effect(Player caster, Player target) {
		super.effect(caster, target);
		int damage = (int) (3+gen.nextInt(3));
		setMessage(String.format("%s drains %s points of %s's life!", caster.name, damage, target.name));
		target.changeHp(-damage);
		caster.changeHp(damage);
		System.out.println(message); // later, output to each client
		writeEffect();
	}
}
