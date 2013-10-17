package game;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import utils.utils;

public class Board {
	public int size;
	public Square[][] board;
	public Player p1;
	public Player p2;
	
	public Board(int size){
		this.size = size;
		this.board = new Square[size][size];
		for(int j=0; j<size; j++){
			for(int i=0; i<size; i++){
				this.board[j][i] = new Square();
			}
		}
	}
	
	public boolean placeStone(Player player, int row, int col){
		boolean canPlaceStone = (this.board[row][col].getState() == 0);
		if(canPlaceStone){
			board[row][col].setState(player.color);
		}
		return canPlaceStone;
	}
	
	public int sumOfSubArray(int color, int startRow, int startCol, int endRow, int endCol){
		int sum = 0;
		for(int j=startRow; j<endRow; j++){
			for(int i=endRow; i<endCol; i++){
				if (this.board[j][i].getState() == color){
					sum++;
				}
			}
		}
		return sum;
	}
	
	public String toString(){
		String s = "";
		for(int j=0; j<size; j++){
			for(int i=0; i<size; i++){
				s += this.board[j][i].toString();
			}
		}
		return s;
	}
}
