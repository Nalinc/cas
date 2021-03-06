package org.apereo.cas.authentication.handler.support;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.HandlerResult;
import org.apereo.cas.authentication.OrganizationUsernamePasswordCredential;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.PrincipalNameTransformer;
import org.apereo.cas.authentication.support.PasswordPolicyConfiguration;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import java.security.GeneralSecurityException;


/**
 * Abstract class to override supports so that we don't need to duplicate the
 * check for OrganizationUsernamePasswordCredential.
 *
 * @author ComproDLS
 * @since 4.2.1
 */
public abstract class AbstractOrgUsernamePasswordAuthenticationHandler extends
    AbstractPreAndPostProcessingAuthenticationHandler {

    /**
     * PasswordEncoder to be used by subclasses to encode passwords for
     * comparing against a resource.
     */
    //@NotNull
    private PasswordEncoder passwordEncoder = NoOpPasswordEncoder.getInstance();

    //@NotNull
    private PrincipalNameTransformer principalNameTransformer = formUserId -> formUserId;
    
    private Predicate<Credential> credentialSelectionPredicate = Predicates.alwaysTrue();
    
    /** The password policy configuration to be used by extensions. */
    private PasswordPolicyConfiguration passwordPolicyConfiguration;
    
    /**
     * {@inheritDoc}
     **/
    @Override
    protected HandlerResult doAuthentication(final Credential credential) throws GeneralSecurityException, PreventedException {
  	
        final OrganizationUsernamePasswordCredential userPass = (OrganizationUsernamePasswordCredential) credential;

        if (StringUtils.isBlank(userPass.getUsername())) {
            throw new AccountNotFoundException("Username is null.");
        }
              
        final String transformedUsername = this.principalNameTransformer.transform(userPass.getUsername());
        if (StringUtils.isBlank(transformedUsername)) {
            throw new AccountNotFoundException("Transformed username is null.");
        }

        if (StringUtils.isBlank(userPass.getPassword())) {
            throw new FailedLoginException("Password is null.");
        }

        final String transformedPsw = this.passwordEncoder.encode(userPass.getPassword());
        if (StringUtils.isBlank(transformedPsw)) {
            throw new AccountNotFoundException("Encoded password is null.");
        }

        userPass.setUsername(transformedUsername);
        userPass.setPassword(this.passwordEncoder.encode(userPass.getPassword()));
        return authenticateUsernamePasswordInternal(userPass);
    }

    /**
     * Authenticates a username/password credential by an arbitrary strategy.
     *
     * @param transformedCredential the credential object bearing the transformed username and password.
     *
     * @return HandlerResult resolved from credential on authentication success or null if no principal could be resolved
     * from the credential.
     *
     * @throws GeneralSecurityException On authentication failure.
     * @throws PreventedException On the indeterminate case when authentication is prevented.
     */
    protected abstract HandlerResult authenticateUsernamePasswordInternal(OrganizationUsernamePasswordCredential transformedCredential)
            throws GeneralSecurityException, PreventedException;
    
    protected PasswordPolicyConfiguration getPasswordPolicyConfiguration() {
        return this.passwordPolicyConfiguration;
    }

    /**
     * Sets the PasswordEncoder to be used with this class.
     *
     * @param passwordEncoder the PasswordEncoder to use when encoding
     * passwords.
     */
    public void setPasswordEncoder(final PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void setCredentialSelectionPredicate(final Predicate<Credential> credentialSelectionPredicate) {
        this.credentialSelectionPredicate = credentialSelectionPredicate;
    }

    public void setPrincipalNameTransformer(final PrincipalNameTransformer principalNameTransformer) {
        this.principalNameTransformer = principalNameTransformer;
    }

    public void setPasswordPolicyConfiguration(final PasswordPolicyConfiguration passwordPolicyConfiguration) {
        this.passwordPolicyConfiguration = passwordPolicyConfiguration;
    }
    

    /**
     * Method to return the PasswordEncoder to be used to encode passwords.
     *
     * @return the PasswordEncoder associated with this class.
     */
    protected final PasswordEncoder getPasswordEncoder() {
        return this.passwordEncoder;
    }

    protected final PrincipalNameTransformer getPrincipalNameTransformer() {
        return this.principalNameTransformer;
    }
    
    @Override
    public boolean supports(final Credential credential) {

        if (credential instanceof OrganizationUsernamePasswordCredential) {
            if (this.credentialSelectionPredicate != null) {
                return this.credentialSelectionPredicate.apply(credential);
            }
            return true;
        }
        return false;    	
    }   
}
