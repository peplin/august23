package twoverse.util;

import java.io.Serializable;

import nu.xom.Attribute;
import nu.xom.Element;

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

    public Element toXmlElement() {
        Element root = new Element("galaxy_shape");
        root.addAttribute(new Attribute("id", String.valueOf(mId)));
        root.addAttribute(new Attribute("name", mName));
        root.addAttribute(new Attribute("texture", mTextureFile));
        return root;
    }

    private int mId;
    private String mName;
    private String mTextureFile;
}
