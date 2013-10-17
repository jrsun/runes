package spells;

import game.Player;
import game.Spell;

public class Bokken extends Spell{

	static final int[][] PATTERN_ONE = {{1,1,1,1,1}};
	static final int[][] PATTERN_TWO = {{1},{1},{1},{1},{1}};
	static final int[][] PATTERN_THREE = {{1,0,0,0,0},{0,1,0,0,0},{0,0,1,0,0},{0,0,0,1,0},{0,0,0,0,1}};
	static final int[][] PATTERN_FOUR = {{0,0,0,0,1},{0,0,0,1,0},{0,0,1,0,0},{0,1,0,0,0},{1,0,0,0,0}};
	static final int MANA_COST = 4;
	static final String COLOR = "orange";
	
	public Bokken(){
		addPattern(PATTERN_ONE);
		addPattern(PATTERN_TWO);
		addPattern(PATTERN_THREE);
		addPattern(PATTERN_FOUR);
		setManaCost(MANA_COST);
		setName("Bokken");
		setColor(COLOR);
	}

	@Override
	public void effect(Player caster, Player target) {
		super.effect(caster, target);
		int damage = (int) (6+gen.nextInt(4));
		setMessage(String.format("%s whacks %s with a %s-foot-long stick!", caster.name, target.name, damage));
		target.changeHp(-damage);
		System.out.println(message); // later, output to each client
		writeEffect();
	}
}
