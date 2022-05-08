package me.saiintbrisson.minecraft;

final class SlotFindResult {

	private final int value;
	private final int moveTo;
	private final boolean stacked;

	public SlotFindResult(int value, int moveTo, boolean stacked) {
		this.value = value;
		this.moveTo = moveTo;
		this.stacked = stacked;
	}

	public int getValue() {
		return value;
	}

	public int getMoveTo() {
		return moveTo;
	}

	public boolean isStacked() {
		return stacked;
	}

	public boolean isAvailable() {
		return value != -1;
	}

	public boolean shouldBeMoved() {
		return moveTo != -1;
	}

}