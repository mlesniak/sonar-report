package com.mlesniak.sonar.report;

import com.google.gson.*;
import org.apache.http.Header;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * HTTP paths to necessary sonar requests.
 *
 * @author Michael Lesniak (mlesniak@micromata.de)
 */
public class SonarConnection {
    public static String GET_ISSUE_SEARCH = "/api/issues/search";
    public static String GET_AUTHORIZE_VALIDATE = "/api/authentication/validate";

    private Gson gson = new Gson();

    private class Auth {
        private boolean valid;

        public boolean isValid() {
            return valid;
        }
    }

    public class Issue {
        private String key;
        private String component;
        private String componentId;
        private String project;
        private String rule;
        private String status;
        private String resolution;
        private String severity;
        private String message;
        private String line;
        private String debt;
        private String creationDate;
        private String updateDate;
        private String fUpdateAge;
        private String closeDate;

        public String getKey() {
            return key;
        }

        public String getComponent() {
            return component;
        }

        public String getComponentId() {
            return componentId;
        }

        public String getProject() {
            return project;
        }

        public String getRule() {
            return rule;
        }

        public String getStatus() {
            return status;
        }

        public String getResolution() {
            return resolution;
        }

        public String getSeverity() {
            return severity;
        }

        public String getMessage() {
            return message;
        }

        public String getLine() {
            return line;
        }

        public String getDebt() {
            return debt;
        }

        public String getCreationDate() {
            return creationDate;
        }

        public String getUpdateDate() {
            return updateDate;
        }

        public String getfUpdateAge() {
            return fUpdateAge;
        }

        public String getCloseDate() {
            return closeDate;
        }
    }

    public void login() {
        try {
            String sonarResponse = getSonarResponse(GET_AUTHORIZE_VALIDATE);
            Auth authJSON = gson.fromJson(sonarResponse, Auth.class);
            if (!authJSON.isValid()) {
                System.out.println("Logging: error, not valid!");
            }
        } catch (IOException | AuthenticationException e) {
            e.printStackTrace();
        }
    }

    public List<Issue> getIssues() {
        try {
            String sonarResponse = getSonarResponse(GET_ISSUE_SEARCH);
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(sonarResponse);
            JsonArray issueArray = ((JsonObject) element).get("issues").getAsJsonArray();
            List<Issue> issues = new LinkedList<>();
            for (JsonElement issue : issueArray) {
                Issue issueInstance = gson.fromJson(issue, Issue.class);
                issues.add(issueInstance);
            }
            return issues;
        } catch (IOException | AuthenticationException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getSonarResponse(String suffix) throws IOException, AuthenticationException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(Configuration.get().getServer() + suffix);
        Header header = new BasicScheme().authenticate(getHttpClient(), httpGet, new HttpClientContext());
        httpGet.addHeader(header);
        System.out.println(httpGet.toString());
        CloseableHttpResponse response = httpclient.execute(httpGet);
        return EntityUtils.toString(response.getEntity());
    }

    private UsernamePasswordCredentials getHttpClient() {
        return new UsernamePasswordCredentials(Configuration.get().getUser(), Configuration.get().getPassword());
    }
}
