package org.apereo.cas.authentication;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
//import java.io.Serializable;

/**
 * Handles both organization services and username and password.
 *
 * @author ComproDLS
 * @since 4.2.1
 *
 */

public class OrganizationUsernamePasswordCredential extends UsernamePasswordCredential implements OrganizationCredential {

    /** Unique Id for serialization. */
    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(min=1, message = "required.organization")
    private String organization;

    /** Default constructor. */
    public OrganizationUsernamePasswordCredential() {}

    /**
     * Creates a new instance with the given organization , username and password.
     *
     * @param userName Non-null user name.
     * @param password Non-null password.
     * @param organization Non-null organization.
     */
    public OrganizationUsernamePasswordCredential(final String organization , final String userName, final String password) {
        super(userName , password);
        this.organization = organization;
    }
    
    public final void setOrganization(final String organization) {
        this.organization = organization;
    }

    public final String getOrganization() {
        return this.organization;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(organization)
                .toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OrganizationUsernamePasswordCredential other = (OrganizationUsernamePasswordCredential) obj;

        if (organization != null ? !organization.equals(other.organization) : other.organization != null) {
            return false;
        }
        return true;
    }
}