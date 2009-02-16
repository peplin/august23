package twoverse;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.common.XmlRpcHttpRequestConfig;
import org.apache.xmlrpc.server.AbstractReflectiveHandlerMapping;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.webserver.XmlRpcServlet;

import twoverse.Database.InvalidUserException;
import twoverse.ObjectManager.UnhandledCelestialBodyException;
import twoverse.SessionManager.ExistingUserException;
import twoverse.object.Galaxy;
import twoverse.object.ManmadeBody;
import twoverse.object.PlanetarySystem;
import twoverse.util.User;

@SuppressWarnings("serial")
public class RequestHandlerServer extends XmlRpcServlet implements
		TwoversePublicApi {
	private static ObjectManagerServer sObjectManager;
	private static SessionManager sSessionManager;
	private static Logger sLogger = Logger.getLogger(RequestHandlerServer.class
			.getName());
	private static HashMap<String, Boolean> sMethodAuthorization = new HashMap<String, Boolean>();

	public RequestHandlerServer() {
	}

	/*private static class RequestHandlerServerHolder {
		private final static RequestHandlerServer INSTANCE = new RequestHandlerServer();
	}

	public static RequestHandlerServer getInstance() {
		return RequestHandlerServerHolder.INSTANCE;
	}*/

	public static void init(ObjectManagerServer objectManager,
			SessionManager sessionManager) {
		sObjectManager = objectManager;
		sSessionManager = sessionManager;

		sMethodAuthorization.put("RequestHandlerServer.logout", true);
		sMethodAuthorization.put("RequestHandlerServer.createAccount", false);
		sMethodAuthorization.put("RequestHandlerServer.changeName", true);
		sMethodAuthorization.put("RequestHandlerServer.addGalaxy", true);
		sMethodAuthorization.put("RequestHandlerServer.addManmadeBody", true);
		sMethodAuthorization.put("RequestHandlerServer.addPlanetarySystem",
				true);
		sMethodAuthorization
				.put("RequestHandlerServer.getHashedPassword", true);
	}

	@Override
	public void logout(String username, int session) {
		sSessionManager.logout(username, session);
	}

	@Override
	public int createAccount(User user) throws ExistingUserException {
		return sSessionManager.createAccount(user);
	}

	@Override
	public void changeName(int objectId, String newName) {
		try {
			sObjectManager.getCelestialBody(objectId).setName(newName);
		} catch (UnhandledCelestialBodyException e) {
			sLogger.log(Level.WARNING, e.getMessage(), e);
		}
	}

	@Override
	public Galaxy addGalaxy(Galaxy galaxy) {
		sObjectManager.add(galaxy);
		return galaxy;
	}

	@Override
	public ManmadeBody addManmadeBody(ManmadeBody body) {
		sObjectManager.add(body);
		return body;
	}

	@Override
	public PlanetarySystem addPlanetarySystem(PlanetarySystem system) {
		sObjectManager.add(system);
		return system;
	}

	public String getHashedPassword(String username) {
		return sSessionManager.getUser(username).getHashedPassword();
	}

	/**
	 * Check that a user exists, confirm the password is correct. If so, create
	 * a new session and return true to the client. TODO this will not work.
	 * Need to hash password on client, so it's not sent plaintext Need actual
	 * correct hashed password in order to hash the candidate use
	 * unauthenticated login method that returns the hashed actual, allowing
	 * correct hash to be generated. then it can be set for the config.
	 */
	private boolean isAuthenticated(String username, String hashedPassword) {
		// return (mSessionManager.login(username, hashedPassword) != -1);
		return true;
	}

	@Override
	protected XmlRpcHandlerMapping newXmlRpcHandlerMapping()
			throws XmlRpcException {
		PropertyHandlerMapping mapping = (PropertyHandlerMapping) super
				.newXmlRpcHandlerMapping();
		AbstractReflectiveHandlerMapping.AuthenticationHandler handler = new AbstractReflectiveHandlerMapping.AuthenticationHandler() {
			public boolean isAuthorized(XmlRpcRequest pRequest) {
				// if(mMethodAuthorization.get(pRequest.getMethodName())) {
				XmlRpcHttpRequestConfig config = (XmlRpcHttpRequestConfig) pRequest
						.getConfig();
				return isAuthenticated(config.getBasicUserName(), config
						.getBasicPassword());
				// } else {
				// return true;
				// }
			};
		};
		mapping.setAuthenticationHandler(handler);
		return mapping;
	}
}
