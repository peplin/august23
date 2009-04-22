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

/**
 * The Object Manager Client handles adding and update client universe objects,
 * as well as updating via an XML feed.
 * 
 * All universe updates are handled locally as well as being passed to a
 * RequestHandler for sending to the server.
 * 
 * This class is thread-safe.
 * 
 * @author Christopher Peplin (chris.peplin@rhubarbtech.com)
 * @version 1.0, Copyright 2009 under Apache License
 */
public class ObjectManagerClient extends ObjectManager {
    private Builder mParser;
    private RequestHandlerClient mRequestHandler;
    private String mFeedUrl;

    /**
     * Construct an object manager for the client.
     * 
     * @param requestHandler
     *            the handler for server requests (eg. update, insert)
     * @param feedUrl
     *            the URL of the XML feed to watch for universe updates
     */
    public ObjectManagerClient(RequestHandlerClient requestHandler,
            String feedUrl) {
        super();
        mParser = new Builder();
        mRequestHandler = requestHandler;
        mFeedUrl = feedUrl;
    }

    /**
     * Run any tasks that are scheduled to run periodically. At the moment, this
     * includes only updating via the XML feed.
     */
    @Override
    public void run() {
        pullFeed();
    }

    /**
     * Pulls a new copy of the XML and updates the universe with its contents.
     * 
     * This method handles parsing the feed as well as updating the local
     * universe. It currently eagerly updates, rather than checking each object
     * to see if there are actually any changes. This maybe introduce problems
     * with simultaneous updates across clients or between client and server
     * simulation.
     * 
     * There is currently no way to know whether an object was deleted, short of
     * occassionally scanning our local universe to see if there are objects NOT
     * in the XML feed.
     */
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

    /**
     * Returns all bodies in their applet form, suitable for drawing to the
     * screen.
     * 
     * @param parent
     *            the parent applet this applet-style object belongs to
     * @return list of appet-style objects
     */
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
     * Add an object to the universe and requests the server to add.
     * 
     * The object is added locally even if the server request fails.
     * 
     * @param body
     *            the object to insert. The ID and birth time of the object are
     *            set after the server request returns.
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

    /**
     * Update an object locally and request the server to update.
     * 
     * The object is updated locally even if the server request fails.
     * 
     * @param body
     *            the object to update. The ID must be set, and it must
     *            match an ID known on the server.
     * 
     * @throws UnhandledCelestialBodyException
     */
    @Override
    public void update(CelestialBody body) {
        sLogger.log(Level.INFO, "Attemping to update body: " + body);
        mLock.writeLock().lock();
        mRequestHandler.update(body);
        super.update(body);
        mLock.writeLock().unlock();
    }

    /**
     * Add a link to the universe and request the server to add it.
     * 
     * The object is added locally even if the server request fails.
     * 
     * @param link
     *            the object to insert. The ID of the object is set after the
     *            server request returns.
     * 
     */
    @Override
    public void add(Link link) {
        sLogger.log(Level.INFO, "Attemping to update link: " + link);
        mLock.writeLock().lock();
        mRequestHandler.add(link);
        super.update(link);
        mLock.writeLock().unlock();
    }
}
