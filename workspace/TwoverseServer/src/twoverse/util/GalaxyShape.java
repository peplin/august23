package twoverse.util;

public class GalaxyShape {
	public GalaxyShape() {
		
	}

	public void setId(int id) {
		mId = id;
	}
	public int getId() {
		return mId;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getName() {
		return mName;
	}

	public void setTextureFile(String textureFile) {
		mTextureFile = textureFile;
	}

	public String getTextureFile() {
		return mTextureFile;
	}

	private int mId;
	private String mName;
	private String mTextureFile;
}
