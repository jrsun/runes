package spells;

import game.Player;

import game.Spell;
import game.Square;

import utils.utils;
import java.util.Random;

public class Fireball extends Spell{

	static final int[][] PATTERN = {{1,1},{1,1}};
	static final int MANA_COST = 4;
	static final String COLOR = "red";
	
	public Fireball(){
		addPattern(PATTERN);
		setManaCost(MANA_COST);
		setName("Fireball");
		setColor(COLOR);
	}

	@Override
	public void effect(Player caster, Player target) {
		super.effect(caster, target);
		int damage = (int) (4+gen.nextInt(2));
		setMessage(String.format("%s blasts %s with fire for %s damage!",
				caster.name, target.name, damage));
		int state = 0;
		int counter = 0;
		while(counter <= 100){
			int x = gen.nextInt(utils.BOARD_SIZE);
			int y = gen.nextInt(utils.BOARD_SIZE);
			state = target.getBoard().board[x][y].getState();
			if(state == target.getColor()){
				target.getBoard().board[x][y].setState(state*utils.USED);
				break;
			}
			counter++;
		}
		if(state == target.getColor()){
			addMessage(" A stone is burnt out...");
		}
		target.changeHp(-damage);
		System.out.println(message); // later, output to each client
		writeEffect();
	}
}
