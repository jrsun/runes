package spells;

import game.Player;
import game.Spell;

public class Crystal extends Spell{

	static final int[][] PATTERN = {{0,1,0},{1,0,1},{0,1,0}};
	static final int MANA_COST = 4;
	static final String COLOR = "blue";
	
	public Crystal(){
		addPattern(PATTERN);
		setManaCost(MANA_COST);
		setName("Crystal");
		setColor(COLOR);
	}

	public void effect(Player caster, Player target) {
		super.effect(caster, target);
		int damage = (int) (5+gen.nextInt(5));
		setMessage(String.format("%s freezes %s, rendering him unable to move!", caster.name, target.name));
		target.changeHp(-damage);
		target.getRoom().getMatch().setTurn(caster.getColor());
		writeEffect();
	}
}
