/**
 * Twoverse Galaxy Shape
 *
 * by Christopher Peplin (chris.peplin@rhubarbtech.com)
 * for August 23, 1966 (GROCS Project Group)
 * University of Michigan, 2009
 *
 * http://august231966.com
 * http://www.dc.umich.edu/grocs
 *
 * Copyright 2009 Christopher Peplin 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

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
    private static Properties sConfigFile;
    private static Logger sLogger =
            Logger.getLogger(GalaxyShape.class.getName());
    private int mId;
    private String mName;
    private String mTextureFile;

    /**
     * @param id
     * @param name
     * @param textureFile
     */
    public GalaxyShape(int id, String name, String textureFile) {
        loadConfig();
        initialize(id, name, textureFile);
    }

    /**
     * @param element
     */
    public GalaxyShape(Element element) {
        loadConfig();

        if(!element.getLocalName()
                .equals(sConfigFile.getProperty("GALAXY_SHAPE_TAG"))) {
            throw new UnexpectedXmlElementException("Element is not a galaxy shape");
        }

        int id =
                Integer.valueOf(element.getAttribute(sConfigFile.getProperty("ID_ATTRIBUTE_TAG"))
                        .getValue());

        String name =
                element.getAttribute(sConfigFile.getProperty("NAME_ATTRIBUTE_TAG"))
                        .getValue();

        String textureFile =
                element.getAttribute(sConfigFile.getProperty("TEXTURE_FILE_ATTRIBUTE_TAG"))
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
            sConfigFile = new Properties();
            sConfigFile.load(this.getClass()
                    .getClassLoader()
                    .getResourceAsStream("twoverse/conf/GalaxyShape.properties"));
        } catch(IOException e) {
            sLogger.log(Level.SEVERE, "Unable to laod config: "
                    + e.getMessage(), e);
        }
    }

    /**
     * @param id
     */
    public void setId(int id) {
        mId = id;
    }

    /**
     * @return
     */
    public int getId() {
        return mId;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        mName = name;
    }

    /**
     * @return
     */
    public String getName() {
        return mName;
    }

    /**
     * @param textureFile
     */
    public void setTextureFile(String textureFile) {
        mTextureFile = textureFile;
    }

    /**
     * @return
     */
    public String getTextureFile() {
        return mTextureFile;
    }

    /**
     * @return
     */
    public Element toXmlElement() {
        loadConfig();
        Element root = new Element(sConfigFile.getProperty("GALAXY_SHAPE_TAG"));
        root.addAttribute(new Attribute(sConfigFile.getProperty("ID_ATTRIBUTE_TAG"),
                String.valueOf(mId)));
        root.addAttribute(new Attribute(sConfigFile.getProperty("NAME_ATTRIBUTE_TAG"),
                mName));
        root.addAttribute(new Attribute(sConfigFile.getProperty("TEXTURE_FILE_ATTRIBUTE_TAG"),
                mTextureFile));
        return root;
    }
}
