package spells;

import game.Player;
import game.Spell;

public class Cure extends Spell{

	static final int[][] PATTERN_ONE = {{0,1,0},{1,1,1},{0,1,0}};
	static final int[][] PATTERN_TWO = {{1,0,1},{0,1,0},{1,0,1}};
	static final int MANA_COST = 5;
	static final String COLOR = "green";
	
	public Cure(){
		addPattern(PATTERN_ONE);
		addPattern(PATTERN_TWO);
		setManaCost(MANA_COST);
		setName("Cure");
		setColor(COLOR);
	}

	@Override
	public void effect(Player caster, Player target) {
		super.effect(caster, target);
		int health = (int) (6 + gen.nextInt(3));
		setMessage(String.format("%s cures himself of %s points of damage!", caster.name, health));
		caster.changeHp(health);
		System.out.println(message); // later, output to each client
		writeEffect();
	}
}
