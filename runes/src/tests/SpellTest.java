package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import game.Board;
import game.Player;
import game.Spell;
import game.Square;

import org.junit.Test;

import spells.MagicMissile;

public class SpellTest {

	@Test
	public void initializeSpell() {
		MagicMissile m = new MagicMissile();
		assertEquals(3, m.getManaCost());
		assertEquals("XXXX\n", m.getPatternStrings().get(0));
		assertEquals("X\nX\nX\nX\n", m.getPatternStrings().get(1));
	}
	
	@Test
	public void effectSpell() {
		MagicMissile m = new MagicMissile();
		ArrayList<Spell> empty = new ArrayList<Spell>();
		Board b = new Board(10);
		Player alice =  new Player("Alice");
		Player bob = new Player("Bob");
		m.effect(alice, bob);
		System.out.println("Bob's HP: " + bob.getHp());
		assert(bob.getHp() < 80);
	}
}
