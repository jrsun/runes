package spells;

import game.Player;
import game.Spell;

public class MagicMissile extends Spell{

	static final int[][] PATTERN_ONE = {{1,1,1,1}};
	static final int[][] PATTERN_TWO = {{1},{1},{1},{1}};
	static final int[][] PATTERN_THREE = {{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}};
	static final int[][] PATTERN_FOUR = {{0,0,0,1},{0,0,1,0},{0,1,0,0},{1,0,0,0}};
	static final int MANA_COST = 3;
	static final String COLOR = "aqua";
	
	public MagicMissile(){
		addPattern(PATTERN_ONE);
		addPattern(PATTERN_TWO);
		addPattern(PATTERN_THREE);
		addPattern(PATTERN_FOUR);
		setManaCost(MANA_COST);
		setName("MagicMissile");
		setColor(COLOR);
	}

	@Override
	public void effect(Player caster, Player target) {
		super.effect(caster, target);
		int damage = (int) (4+gen.nextInt(3));
		setMessage(String.format("%s fires %s energy bolts at %s!", caster.name, damage, target.name));
		target.changeHp(-damage);
		System.out.println(message); // later, output to each client
		writeEffect();
	}
}
