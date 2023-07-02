package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Attack;

public class Input {
	private Attack[] attacks;
	private int R2D2;
	private int Lando;
	private int Ewoks;
	
	public int getEwoks() {
		return Ewoks;
	}
	public int getLando() {
		return Lando;
	}
	public int getR2D2() {
		return R2D2;
	}
	public Attack[] getAttacks() {
		return attacks;
	}
}
