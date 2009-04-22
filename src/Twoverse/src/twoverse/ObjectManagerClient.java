/**
 * Twoverse Object Manager Client
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

package twoverse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import processing.core.PApplet;
import twoverse.object.CelestialBody;
import twoverse.object.Link;
import twoverse.object.Star;
import twoverse.object.applet.AppletBodyInterface;

public class ObjectManagerClient extends ObjectManager {
    private Builder mParser;
    private RequestHandlerClient mRequestHandler;
    private String mFeedUrl;

    public ObjectManagerClient(RequestHandlerClient requestHandler,
            String feedUrl) {
        super();
        mParser = new Builder();
        mRequestHandler = requestHandler;
        mFeedUrl = feedUrl;
    }

    @Override
    public void run() {
        pullFeed();
    }

    private void pullFeed() {
        try {
            Document doc = mParser.build(mFeedUrl);
            // TODO how do we pick up deleted objects?

            Element universe =
                    doc.getRootElement()
                            .getFirstChildElement(mConfigFile.getProperty("CELESTIAL_BODY_TAG"));
            update(new CelestialBody(universe));

            Elements stars =
                    doc.getRootElement()
                            .getChildElements(mConfigFile.getProperty("STAR_TAG"));
            for(int i = 0; i < stars.size(); i++) {
                Star star = new Star(stars.get(i));
                update(star);
            }

            Elements links =
                    doc.getRootElement()
                            .getChildElements(mConfigFile.getProperty("LINK_TAG"));
            for(int i = 0; i < links.size(); i++) {
                Link link = new Link(links.get(i));
                update(link);
            }
        } catch(ParsingException e) {
            sLogger.log(Level.WARNING, "Feed may be malformed", e);
        } catch(IOException e) {
            sLogger.log(Level.WARNING, "Unable to connect to feed", e);
        }
    }

    public ArrayList<AppletBodyInterface> getAllBodiesAsApplets(PApplet parent) {
        mLock.readLock().lock();
        ArrayList<AppletBodyInterface> allBodies =
                new ArrayList<AppletBodyInterface>();
        Collection<CelestialBody> bodies = mCelestialBodies.values();
        for(CelestialBody body : bodies) {
            allBodies.add(body.getAsApplet(parent));
        }
        mLock.readLock().unlock();
        return allBodies;
    }

    /**
     * Modifies body, sets ID and birth time
     * 
     * @throws UnhandledCelestialBodyException
     */
    @Override
    public void add(CelestialBody body) {
        sLogger.log(Level.INFO, "Attemping to add body: " + body);
        mLock.writeLock().lock();
        mRequestHandler.add(body);
        try {
            super.add(body);
        } catch(UnhandledCelestialBodyException e) {
            sLogger.log(Level.WARNING, "Unable to add", e);
        }
        mLock.writeLock().unlock();
    }

    @Override
    public void update(CelestialBody body) {
        sLogger.log(Level.INFO, "Attemping to update body: " + body);
        mLock.writeLock().lock();
        mRequestHandler.update(body);
        super.update(body);
        mLock.writeLock().unlock();
    }

    @Override
    public void add(Link link) {
        sLogger.log(Level.INFO, "Attemping to update link: " + link);
        mLock.writeLock().lock();
        mRequestHandler.add(link);
        super.update(link);
        mLock.writeLock().unlock();
    }
}
