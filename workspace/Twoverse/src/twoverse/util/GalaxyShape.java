package twoverse.util;

import java.io.Serializable;

public class GalaxyShape implements Serializable {
    private static final long serialVersionUID = 6779708232529398026L;

    public GalaxyShape(int id, String name, String textureFile) {
        setId(id);
        setName(name);
        setTextureFile(textureFile);
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
