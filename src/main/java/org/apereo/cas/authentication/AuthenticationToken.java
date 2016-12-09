package org.apereo.cas.authentication;

import java.net.URL;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.apereo.cas.authentication.OrganizationUsernamePasswordCredential;
/**
 * Get the auth token from ComproDLS Auth 
 * @author ComproDLS
 * @since 4.2.1
 */

@Component("AuthenticationToken")
public class AuthenticationToken {
	
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public final String getAuthToken(final OrganizationUsernamePasswordCredential credential) throws Exception {
        final String organization = credential.getOrganization();
        final String username = credential.getUsername();
        final String password = credential.getPassword();
        System.out.println("ORGANIZATION");
        System.out.println(organization);
        String requestUrl = "http://auth.comprodls.com/auth/" + organization + "/token";
        System.out.println("REQUEST URL________");
        System.out.println(requestUrl);
        URL url = new URL(requestUrl);

        //Making Request Body Params
        JSONObject credentials   = new JSONObject();
        credentials.put("username", username);
        credentials.put("password", password);
        
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        //Adding Request Headers
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        
        // Set it to true as we have to send a request body
        connection.setDoOutput(true);

        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
        wr.write(credentials.toString());
        wr.flush();
        wr.close();

        //int responseCode = connection.getResponseCode();

        InputStream in = new BufferedInputStream(connection.getInputStream());
        String result = org.apache.commons.io.IOUtils.toString(in, "UTF-8");

        in.close();
        connection.disconnect();

        return result;
    }
}