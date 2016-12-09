package org.apereo.cas.authentication;

import org.apache.commons.codec.binary.StringUtils;
import org.apereo.cas.authentication.OrganizationUsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractOrgUsernamePasswordAuthenticationHandler;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apereo.cas.authentication.AuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Handler that contains a list of valid users and passwords. Useful if there is
 * a small list of users that we wish to allow. An example use case may be if
 * there are existing handlers that make calls to LDAP, etc. but there is a need
 * for additional users we don't want in LDAP. With the chain of command
 * processing of handlers, this handler could be added to check before LDAP and
 * provide the list of additional users. The list of acceptable users is stored
 * in a map. The key of the map is the username and the password is the object
 * retrieved from doing map.get(KEY).
 * <p>
 * Note that this class makes an unmodifiable copy of whatever map is provided
 * to it.
 *
 * @author Scott Battaglia
 * @author Marvin S. Addison
 * @author ComproDLS
 * @since 4.2.1
 */
@Component("acceptOrgUsersAuthenticationHandler")
public class AcceptOrgUsersAuthenticationHandler extends AbstractOrgUsernamePasswordAuthenticationHandler {
	
    /**
     * The list of users we will accept.
     */
    private Map<String, String> users;
    private AuthenticationToken authToken = new AuthenticationToken();

    /**
     * Instantiates a new Accept users authentication handler.
     */
    public AcceptOrgUsersAuthenticationHandler() {
        this(new HashMap<>());
    }

    /**
     * Instantiates a new Accept users authentication handler.
     *
     * @param users the users
     */
    public AcceptOrgUsersAuthenticationHandler(final Map<String, String> users) {
        this.users = users;
    }

    @Override
    protected HandlerResult authenticateUsernamePasswordInternal(final OrganizationUsernamePasswordCredential credential)
            throws GeneralSecurityException, PreventedException {
        if (this.users == null || this.users.isEmpty()) {
            throw new FailedLoginException("No user can be accepted because none is defined");
        }
        final String username = credential.getUsername();
        final String cachedPassword = this.users.get(username);

        if (cachedPassword == null) {
            logger.debug("{} was not found in the map.", username);
            throw new AccountNotFoundException(username + " not found in backing map.");
        }

        if (!StringUtils.equals(credential.getPassword(), cachedPassword)) {
            throw new FailedLoginException();
        }

        try{
        	String response = authToken.getAuthToken(credential);
        	final List<MessageDescriptor> list = new ArrayList<>();
            Map<String, Object> attributes = Collections.<String, Object>singletonMap("comproDLS_attributes", response);
            return createHandlerResult(credential, this.principalFactory.createPrincipal(username,attributes), list);    	
        }
        catch(Exception e){
            logger.debug("{} was not found in the database in org {}",credential.getUsername() , credential.getOrganization());
            throw new AccountNotFoundException("The credentials are invalid");     	
        }
       }
    
    /**
     * @param users The users to set.
     */
    public void setUsers(final Map<String, String> users) {
        this.users = new HashMap<>(users);
    }    
}