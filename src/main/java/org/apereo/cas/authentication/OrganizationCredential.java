package org.apereo.cas.authentication;

/**
 * Credential that wish to handle organization Id scenarios need
 * to implement this class.
 *
 * @author ComproDLS
 * @since 4.2.1
 *
 */
public interface OrganizationCredential extends Credential {

    /** Authentication attribute name for organization. **/
    String AUTHENTICATION_ATTRIBUTE_ORGANIZATION = "org.apereo.cas.authentication.principal.ORGANIZATION";

    /** Request parameter name. **/
    String REQUEST_PARAMETER_ORGANIZATION = "organization";

    /**
     * Checks if organization is enabled.
     *
     * @return true, if organization
     */
    String getOrganization();

    /**
     * Sets the organization
     *
     * @param organization the new organization
     */
    void setOrganization(String organization);
}
