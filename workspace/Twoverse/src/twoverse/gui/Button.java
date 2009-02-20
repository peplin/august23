package twoverse.gui;

import processing.core.PApplet;
import twoverse.TwoverseClient.Mode;
import twoverse.util.Point;
import java.lang.Math;
import java.util.ArrayList;

public abstract class Button {
	private Point mCenter;
	private int mBaseColor;
	private int mHighlightColor;
	private boolean mPressed = false;
	private boolean mLocked = false;
	private String mName;
	private ArrayList<Button> mChildren;
	protected PApplet mParentApplet;

	public Button(PApplet parentApplet, Point center, int baseColor,
			int highlightColor, String name) {

	}

	public void update(Point cursor) {
		
	}

	boolean pressed() {
		if (mPressed) {
			setLocked(true);
			return true;
		}
		return false;
	}

	boolean over(int cursorX, int cursorY) {
		return false;
	}

	boolean overRect(int cursorX, int cursorY, Point center, int width,
			int height) {
		if (cursorX >= center.getX() && cursorX <= center.getX() + width
				&& cursorY >= center.getY()
				&& cursorY <= center.getY() + height) {
			return true;
		} else {
			return false;
		}
	}

	boolean overCircle(int cursorX, int cursorY, Point center, int diameter) {
		double disX = center.getX() - cursorX;
		double disY = center.getY() - cursorY;
		if (java.lang.Math.sqrt(java.lang.Math.pow(disX, 2.0)
				+ java.lang.Math.pow(disY, 2)) < diameter / 2) {
			return true;
		} else {
			return false;
		}
	}
	
	public void setPressed(boolean pressed) {
		mPressed = pressed;
	}
	
	public boolean getPressed() {
		return mPressed;
	}

	public void setCenter(Point center) {
		mCenter = center;
	}

	public Point getCenter() {
		return mCenter;
	}

	public void setBaseColor(int baseColor) {
		mBaseColor = baseColor;
	}

	public int getBaseColor() {
		return mBaseColor;
	}

	public void setHighlightColor(int highlightColor) {
		mHighlightColor = highlightColor;
	}

	public int getHighlightColor() {
		return mHighlightColor;
	}

	public void setLocked(boolean locked) {
		mLocked = locked;
	}

	public boolean isLocked() {
		return mLocked;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getName() {
		return mName;
	}

	public void addChild(Button child) {
		mChildren.add(child);
	}
}