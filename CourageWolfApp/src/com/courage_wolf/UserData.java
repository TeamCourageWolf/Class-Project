package com.courage_wolf;

import java.util.Date;

public class UserData {
	public boolean containsData;
	private int energy;
	private double cash;
	private int rep;
	private Date lastPlayed;
	public UserData()
	{
		containsData = false;
		energy = 0;
		cash = 0.0;
		rep = 0;
		lastPlayed = null;
	}
	public void setEnergy(int energy) {
		this.energy = energy;
	}
	public int getEnergy() {
		return energy;
	}
	public void setCash(double cash) {
		this.cash = cash;
	}
	public double getCash() {
		return cash;
	}
	public void setRep(int rep) {
		this.rep = rep;
	}
	public int getRep() {
		return rep;
	}
	public void setLastPlayed(Date lastPlayed) {
		this.lastPlayed = lastPlayed;
	}
	public Date getLastPlayed() {
		return lastPlayed;
	}
}
