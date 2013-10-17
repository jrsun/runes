package game;

import utils.utils;

public class Square {
	int state;
	
	public Square(){
		this.state = utils.EMPTY;
	}
	
	public Square(int state){
		this.state = state;
	}
	
	public Square(int state, boolean hasMagic){
		this.state = state;
	}
	
	public int getState() {
		return state;
	}
	
	public String toString(){
		if(state == utils.EMPTY){
			return "-";
		} else if(state == utils.WHITE){
			return "W";
		} else if(state == utils.BLACK){
			return "B";
		} else if(state == utils.WHITE_USED){
			return "w";
		} else if(state == utils.BLACK_USED){
			return "b";
		} else {
			return "X";
		}
	}

	public void setState(int state) {
		this.state = state;
	}

}
