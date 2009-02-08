package twoverse;

import java.awt.Color;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.common.XmlRpcHttpRequestConfig;
import org.apache.xmlrpc.server.AbstractReflectiveHandlerMapping;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.webserver.XmlRpcServlet;

import twoverse.object.CelestialBody;
import twoverse.object.Galaxy;
import twoverse.object.ManmadeBody;
import twoverse.object.PlanetarySystem;
import twoverse.object.Star;
import twoverse.util.GalaxyShape;
import twoverse.util.PhysicsVector3d;
import twoverse.util.User;

public class RequestHandlerServer extends XmlRpcServlet implements
        TwoversePublicApi {
    private ObjectManagerServer objectManager;
    private SessionManager sessionManager;
    
    public RequestHandlerServer(ObjectManager objectManager,
            SessionManager sessionManager) {

    }

    public boolean login(String username, String password) {
        return false;
    }

    public boolean logout(String username, int session) {
        return false;
    }

    public void refreshUser(String username, int session) {

    }

    // TODO double all of these, one accepts serialized object
    public void addGalaxy() {

    }

    public void addPlanetarySystem() {

    }

    public void addManmadeBody() {

    }

    public void changeName(int objectId) {

    }

    public GalaxyShape[] getGalaxyShapes() {
        return null;

    }

    public Color[] getColors() {
        return null;

    }

    private boolean isAuthenticated(String username, String password) {
        return false;
    }

    protected XmlRpcHandlerMapping newXmlRpcHandlerMapping()
            throws XmlRpcException {
        PropertyHandlerMapping mapping = (PropertyHandlerMapping) super
                .newXmlRpcHandlerMapping();
        AbstractReflectiveHandlerMapping.AuthenticationHandler handler = new AbstractReflectiveHandlerMapping.AuthenticationHandler() {
            public boolean isAuthorized(XmlRpcRequest pRequest) {
                XmlRpcHttpRequestConfig config = (XmlRpcHttpRequestConfig) pRequest
                        .getConfig();
                return isAuthenticated(config.getBasicUserName(), config
                        .getBasicPassword());
            };
        };
        mapping.setAuthenticationHandler(handler);
        return mapping;
    }


    @Override
    public void addGalaxy(Galaxy galaxy) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addGalaxy(User owner, CelestialBody parent,
            PhysicsVector3d velocity, PhysicsVector3d acceleration,
            Color color, GalaxyShape shape) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addManmadeBody(ManmadeBody body) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addManmadeBody(User owner, CelestialBody parent,
            PhysicsVector3d velocity, PhysicsVector3d acceleration, Color color) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addPlanetarySystem(PlanetarySystem system) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addPlanetarySystem(User owner, CelestialBody parent,
            PhysicsVector3d velocity, PhysicsVector3d acceleration,
            Color color, Star center) {
        // TODO Auto-generated method stub
        
    }
}
