package twoverse.util;

import nu.xom.XMLException;

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
