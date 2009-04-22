/**
** Mode Interface for Twoverse Client
**
** by Christopher Peplin (chris.peplin@rhubarbtech.com)
** for August 23, 1966 (GROCS Project Group)
** University of Michigan, 2009
**
** http://august231966.com
** http://www.dc.umich.edu/grocs
**
** Copyright 2009 Christopher Peplin 
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at 
** http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
** See the License for the specific language governing permissions and
** limitations under the License. 
*/

/**
The Twoverse clients use a "mode" for each view they show. Every mode must
implement this interface so that the client can properly switch between them and
also so it can pass on input events.

   @author Christopher Peplin (chris.peplin@rhubarbtech.com)
   @version 1.0, Copyright 2009 under Apache License
*/
public interface ModeInterface {
    void display();
    void cursorPressed(Point cursor);
    void cursorDragged(Point cursor);
    void disable();
    boolean canDisable();
}
