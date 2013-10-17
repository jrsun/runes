package spells;

import game.Player;
import game.Spell;
import utils.utils;

public class TornadoSpin extends Spell{

	static final int[][] PATTERN_ONE = {{1,1,1},{0,1,0}};
	static final int[][] PATTERN_TWO = {{1,0},{1,1},{1,0}};
	static final int[][] PATTERN_THREE = {{0,1,0},{1,1,1}};
	static final int[][] PATTERN_FOUR = {{0,1},{1,1},{0,1}};
	static final int MANA_COST = 4;
	static final String COLOR = "gray";
	
	public TornadoSpin(){
		addPattern(PATTERN_ONE);
		addPattern(PATTERN_TWO);
		addPattern(PATTERN_THREE);
		addPattern(PATTERN_FOUR);
		setManaCost(MANA_COST);
		setName("TornadoSpin");
		setColor(COLOR);
	}

	@Override
	public void effect(Player caster, Player target) {
		super.effect(caster, target);
		int damage = (int) (3+gen.nextInt(3));
		setMessage(String.format("%s spins %s round for %s damage! A chunk of debris lands elsewhere!", caster.name, target.name, damage));
		target.changeHp(-damage);
		int x = gen.nextInt(utils.BOARD_SIZE);
		int y = gen.nextInt(utils.BOARD_SIZE);
		while(!caster.getBoard().placeStone(caster, x, y)){}
		System.out.println(message); // later, output to each client
		writeEffect();
	}
}
