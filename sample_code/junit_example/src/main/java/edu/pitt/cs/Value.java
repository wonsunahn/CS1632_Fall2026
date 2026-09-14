package edu.pitt.cs;
public class Value {
	private int val = 0;

	private void setVal(int val) {
		this.val = val;
	}

	public void incVal() {
		setVal(getVal() + 1);
	}

	public int getVal() {
		return val;
	}
}