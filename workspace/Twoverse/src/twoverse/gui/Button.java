package twoverse.gui;

import processing.core.PApplet;
import twoverse.util.Point;
import java.lang.Math;
import java.util.ArrayList;

public abstract class Button {
	private Point mCenter;
	private int mBaseColor;
	private int mHighlightColor;
	private boolean mVisible = false;
	private boolean mLocked = false;
	private String mName;
	private ArrayList<Button> mChildren;
	protected PApplet mParentApplet;

	//TODO push matrix before doing any display, pop after
	public Button(PApplet parentApplet, Point center, int baseColor,
			int highlightColor, String name) {
		setCenter(center);
		setParentApplet(parentApplet);
		setBaseColor(baseColor);
		setHighlightColor(highlightColor);
		setName(name);
		setVisible(true);
		mChildren = new ArrayList<Button>();
	}

	private void setParentApplet(PApplet parentApplet) {
		mParentApplet = parentApplet;
	}

	public void update(Point cursor) {
		if(isPressed(cursor)) {
			if(isLocked()) {
				setLocked(false);
			} else {
				setLocked(true);
				//TODO clear all others
			}
		}
	}

	public abstract boolean isPressed(Point cursor);

	boolean overRect(Point cursor, Point center, int width,
			int height) {
		if (cursor.getX() >= center.getX() && cursor.getX() <= center.getX() + width
				&& cursor.getY() >= center.getY()
				&& cursor.getY() <= center.getY() + height) {
			return true;
		} else {
			return false;
		}
	}

	boolean overCircle(Point cursor, Point center, int diameter) {
		double disX = center.getX() - cursor.getX();
		double disY = center.getY() - cursor.getY();
		if (java.lang.Math.sqrt(java.lang.Math.pow(disX, 2.0)
				+ java.lang.Math.pow(disY, 2)) < diameter / 2) {
			return true;
		} else {
			return false;
		}
	}
	
	public void setVisible(boolean visible) {
		mVisible = visible;
	}
	
	public boolean isVisible() {
		return mVisible;
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
		child.setVisible(false);
		mChildren.add(child);
	}
}