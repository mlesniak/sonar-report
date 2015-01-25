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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * HTTP paths to necessary sonar requests.
 *
 * @author Michael Lesniak (mlesniak@micromata.de)
 */
public class SonarConnection {
    private static Logger LOG = LoggerFactory.getLogger(SonarConnection.class);
    private static String GET_ISSUE_SEARCH = "/api/issues/search";
    private static String GET_AUTHORIZE_VALIDATE = "/api/authentication/validate";
    private Configuration config;
    private Gson gson = new Gson();
    private final CloseableHttpClient httpclient;

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

    public SonarConnection() {
        httpclient = HttpClients.createDefault();
        config = Configuration.get();
    }

    public void login() {
        LOG.debug("Authenticating at sonar server.");
        String sonarResponse = getSonarResponse(GET_AUTHORIZE_VALIDATE);
        Auth authJSON = gson.fromJson(sonarResponse, Auth.class);
        if (!authJSON.isValid()) {
            LOG.error("Unable to log into server. Aborting");
            System.exit(1);
        }
    }

    /**
     * Filter for property names defined in the configuration.
     */
    public List<Issue> getIssues() {
        LOG.debug("Retriving issues from server");
        String sonarResponse = getSonarResponse(GET_ISSUE_SEARCH);
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(sonarResponse);
        JsonArray issueArray = ((JsonObject) element).get("issues").getAsJsonArray();
        List<Issue> issues = new LinkedList<>();
        for (JsonElement issue : issueArray) {
            Issue issueInstance = gson.fromJson(issue, Issue.class);
            issues.add(issueInstance);
        }
        filterIssues(issues);
        return issues;
    }

    private void filterIssues(List<Issue> issues) {
        class Wrapper {
            public boolean value = false;
        }

        Iterator<Issue> it = issues.iterator();
        while (it.hasNext()) {
            Issue issue = it.next();
            Wrapper removeIt = new Wrapper();
            // Remember if the current item should be removed:
            BeanUtils.forEachField(issue, field -> {
                String filterValue = Configuration.get().get(field.getName());
                if (filterValue == null) {
                    return;
                }

                if (!field.get(issue).equals(filterValue)) {
                    removeIt.value = true;
                }
            });
            if (removeIt.value) {
                it.remove();
            }
        }
    }

    private String getSonarResponse(String suffix) {
        try {
            HttpGet httpGet = new HttpGet(Configuration.get().getServer() + suffix);
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials(config.getUser(), config.getPassword());
            Header header = new BasicScheme().authenticate(creds, httpGet, new HttpClientContext());
            httpGet.addHeader(header);
            LOG.debug("Sonar communuication: {}", httpGet.toString());
            CloseableHttpResponse response = httpclient.execute(httpGet);
            return EntityUtils.toString(response.getEntity());
        } catch (IOException | AuthenticationException e) {
            LOG.error("Error in sonar communication: {}.", e.getMessage());
            System.exit(1);
            return null;
        }
    }

}
