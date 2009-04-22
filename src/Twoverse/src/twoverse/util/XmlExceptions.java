/**
 * Twoverse XML Parsing Exceptions
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

import nu.xom.XMLException;

/**
 * A general class to hold all XML exceptions.
 * 
 * I don't believe Twoverse currently uses this.
 * 
 * @author Christopher Peplin (chris.peplin@rhubarbtech.com)
 * @version 1.0, Copyright 2009 under Apache License
 */
public class XmlExceptions {
    static public class UnexpectedXmlAttributeException extends XMLException {
        /**
         * 
         */
        private static final long serialVersionUID = 4725053380196549085L;

        public UnexpectedXmlAttributeException(String e) {
            super(e);
        }
    }

    static public class MissingXmlAttributeException extends XMLException {
        /**
         * 
         */
        private static final long serialVersionUID = -5588020784596940049L;

        public MissingXmlAttributeException(String e) {
            super(e);
        }
    }

    static public class MissingXmlElementException extends XMLException {
        /**
         * 
         */
        private static final long serialVersionUID = -295553940988389065L;

        public MissingXmlElementException(String e) {
            super(e);
        }
    }

    static public class UnexpectedXmlElementException extends XMLException {
        /**
         * 
         */
        private static final long serialVersionUID = 645544342938972281L;

        public UnexpectedXmlElementException(String e) {
            super(e);
        }
    }

}
