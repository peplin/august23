package twoverse.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import nu.xom.Attribute;
import nu.xom.Element;
import twoverse.util.XmlExceptions.UnexpectedXmlElementException;

public class GalaxyShape implements Serializable {
    private static final long serialVersionUID = 6779708232529398026L;
    private Properties mConfigFile;
    private static Logger sLogger = Logger.getLogger(GalaxyShape.class
            .getName());
    private int mId;
    private String mName;
    private String mTextureFile;

    public GalaxyShape(int id, String name, String textureFile) {
        loadConfig();
        initialize(id, name, textureFile);
    }

    public GalaxyShape(Element element) {
        loadConfig();

        if (!element.getLocalName().equals(
                mConfigFile.getProperty("GALAXY_SHAPE_TAG"))) {
            throw new UnexpectedXmlElementException(
                    "Element is not a galaxy shape");
        }

        int id = Integer.valueOf(element.getAttribute(
                mConfigFile.getProperty("ID_ATTRIBUTE_TAG")).getValue());

        String name = element.getAttribute(
                mConfigFile.getProperty("NAME_ATTRIBUTE_TAG")).getValue();

        String textureFile = element.getAttribute(
                mConfigFile.getProperty("TEXTURE_FILE_ATTRIBUTE_TAG"))
                .getValue();

        initialize(id, name, textureFile);
    }

    private void initialize(int id, String name, String textureFile) {
        setId(id);
        setName(name);
        setTextureFile(textureFile);
    }

    private void loadConfig() {
        try {
            mConfigFile = new Properties();
            mConfigFile
                    .load(this.getClass().getClassLoader().getResourceAsStream(
                            "twoverse/conf/GalaxyShape.properties"));
        } catch (IOException e) {
            sLogger.log(Level.SEVERE, "Unable to laod config: "
                    + e.getMessage(), e);
        }
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
        Element root = new Element(mConfigFile.getProperty("GALAXY_SHAPE_TAG"));
        root.addAttribute(new Attribute(mConfigFile
                .getProperty("ID_ATTRIBUTE_TAG"), String.valueOf(mId)));
        root.addAttribute(new Attribute(mConfigFile
                .getProperty("NAME_ATTRIBUTE_TAG"), mName));
        root.addAttribute(new Attribute(mConfigFile.getProperty("TEXTURE_FILE_ATTRIBUTE_TAG"), mTextureFile));
        return root;
    }
}
