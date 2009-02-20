package twoverse.gui;

import processing.core.PApplet;
import twoverse.gui.Button;
import twoverse.util.Point;

public class RectButton extends Button {
	private int mWidth;
	private int mHeight;

	public RectButton(PApplet parentApplet, Point center, int baseColor,
			int highlightColor, String name, int width, int height) {
		super(parentApplet, center, baseColor, highlightColor, name);
		setWidth(width);
		setHeight(height);
	}

	boolean over(int cursorX, int cursorY) {
		if (overRect(cursorX, cursorY, getCenter(), mWidth, mHeight)) {
			setPressed(true);
			return true;
		} else {
			setPressed(false);
			return false;
		}
	}

	void display() {
		mParentApplet.stroke(180);
		mParentApplet.fill(130);
		mParentApplet.rect((float) getCenter().getX(), (float) getCenter()
				.getY(), (float) getWidth(), (float) getHeight());
		// mParentApplet.textFont(font, 8);
		mParentApplet.fill(255);
		mParentApplet.text(getName(),
				(float) (getCenter().getX() + getWidth() / 4),
				(float) (getCenter().getY() + getHeight() / 2));
	}

	void highlight() {
		mParentApplet.stroke(180);
		mParentApplet.fill(130);
		mParentApplet.rect((float) getCenter().getX(), (float) getCenter()
				.getY(), (float) getWidth(), (float) getHeight());
		// mParentApplet.textFont(font, 8);
		mParentApplet.fill(255);
		mParentApplet.text(getName(),
				(float) (getCenter().getX() + getWidth() / 4),
				(float) (getCenter().getY() + getHeight() / 2));
	}

	public void setWidth(int width) {
		mWidth = width;
	}

	public int getWidth() {
		return mWidth;
	}

	public void setHeight(int height) {
		mHeight = height;
	}

	public int getHeight() {
		return mHeight;
	}
}
