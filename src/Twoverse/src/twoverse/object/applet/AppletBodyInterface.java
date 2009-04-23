/**
 * Twoverse Applet-style Object Interface
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

package twoverse.object.applet;

import twoverse.util.Point.TwoDimensionalException;

/**
 * All bodies that want to be drawn to the screen must have a version of
 * themselves that implement this interface. They should also provide an easy
 * way to convert from the parent type to the applet type, altohugh there is no
 * way to enforce that.
 * 
 * @author Christopher Peplin (chris.peplin@rhubarbtech.com)
 * @version 1.0, Copyright 2009 under Apache License
 */
public interface AppletBodyInterface {
    /**
     * Displays the body to the parent applet screen.
     * 
     * @throws TwoDimensionalException
     *             if 2D/3D conversion improperly handled
     */
    public abstract void display() throws TwoDimensionalException;
}
