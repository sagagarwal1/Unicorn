package com.unicorn.base.jiraintegration;

import com.unicorn.base.logger.Logger;
import com.unicorn.utils.AutProperties;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.testng.Assert;

import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JiraServices {
    private RequestSpecification httpRequest;
    private Properties properties;
    private String createTestCycleApi = "rest/zapi/latest/cycle";
    private String addTestToCycleApi = "rest/zapi/latest/execution/addTestsToCycle";
    private String getTicketIdApi = "rest/api/latest/issue/";
    private String getExecutionIdApi = "rest/zapi/latest/execution?projectId=%s&versionId=%s&cycleId=%s";
    private String updateTicketExecutionStatusApi = "rest/zapi/latest/execution/%s/execute";
    private String getVersionIdApi = "rest/api/2/project/%s/versions";
    private String getCycleIdApi = "rest/zapi/latest/cycle?projectId=%s&versionId=%s";

    public JiraServices(String aut) {
        properties = readJiraProperties(aut);
        RestAssured.baseURI = properties.getProperty("jira.base.url");
        httpRequest = RestAssured.given().auth().preemptive().basic(properties.getProperty("jira.user.name"), properties.getProperty("jira.user.passwd"));
        httpRequest.contentType("application/json");

        if (properties.getProperty("jira.set.proxy").equalsIgnoreCase("true")) {
            httpRequest.proxy(properties.getProperty("jira.base.proxy.url"), Integer.parseInt(properties.getProperty("jira.base.proxy.port")));
        }
    }

    /**
     * This will add test to test cycle
     *
     * @param projectId  project id
     * @param versionId  version id
     * @param cycleId    cycle id
     * @param jiraTicket amd test ticket number
     */
    public void addTestToCycle(String projectId, String versionId, String cycleId, String jiraTicket) throws Exception {
        JSONObject ticketDetails = new JSONObject();
        JSONArray issues = new JSONArray();
        issues.add(jiraTicket);
        ticketDetails.put("issues", issues);
        ticketDetails.put("versionId", Integer.parseInt(versionId));
        ticketDetails.put("cycleId", Integer.parseInt(cycleId));
        ticketDetails.put("projectId", Integer.parseInt(projectId));
        httpRequest.body(ticketDetails.toString());

        Response response = httpRequest.post(addTestToCycleApi);
        if (response.getStatusCode() != 200) {
            Logger.assertFail(String.format("Exception while adding %s test to %s cycle", jiraTicket, cycleId));
        }
    }

    /**
     * Create Test Execution Cycle
     *
     * @throws Exception
     */
    public void createTestCycle() {
        JSONObject cycleDetails = new JSONObject();
        cycleDetails.put("projectId", properties.getProperty("jira.project.id"));
        cycleDetails.put("name", AutProperties.getJiraTestCycleName());
        cycleDetails.put("versionId", getVersionId(properties.getProperty("jira.project.key"), properties.getProperty("jira.version.name")));
        httpRequest.body(cycleDetails.toString());
        Response response = httpRequest.post(createTestCycleApi);
        if (response.getStatusCode() != 200) {
            Logger.debug("Status Line:" + response.getStatusLine());
            Logger.assertFail(String.format("Exception while creating test cycle: %s ", AutProperties.getJiraTestCycleName()));
        }
    }

    /**
     * Add test case to test execution cycle
     *
     * @param jiraTicket jira ticket name
     * @throws Exception
     */
    public void addTestToCycle(String jiraTicket) {
        JSONObject ticketDetails = new JSONObject();
        JSONArray issues = new JSONArray();
        issues.add(jiraTicket);
        ticketDetails.put("issues", issues);
        ticketDetails.put("versionId", Integer.parseInt(getVersionId(properties.getProperty("jira.project.key"), properties.getProperty("jira.version.name"))));
        ticketDetails.put("cycleId", Integer.parseInt(getCycleId(properties.getProperty("jira.project.id"), getVersionId(properties.getProperty("jira.project.key"), properties.getProperty("jira.version.name")), AutProperties.getJiraTestCycleName())));
        ticketDetails.put("projectId", Integer.parseInt(properties.getProperty("jira.project.id")));
        httpRequest.body(ticketDetails.toString());

        Response response = httpRequest.post(addTestToCycleApi);
        if (response.getStatusCode() != 200) {
            Logger.assertFail(String.format("Exception while adding %s test to %s cycle", jiraTicket, AutProperties.getJiraTestCycleName()));
        }
    }

    /**
     * This will return the jira ticket id
     *
     * @param ticketKey jira ticket key
     * @return ticket id
     * @throws Exception
     */
    public String getTicketId(String ticketKey) {
        Response response = httpRequest.get(getTicketIdApi + ticketKey);
        if (response.getStatusCode() != 200) {
            Logger.assertFail(String.format("Exception while getting jira ticket's %s id", ticketKey));
        }
        JsonPath jsonPathEvaluator = response.jsonPath();
        return jsonPathEvaluator.get("id").toString();
    }

    /**
     * This will return the jira ticket execution id base on version and cycle name
     *
     * @param projectId   project id
     * @param versionName version name
     * @param cycleName   cycle name
     * @param jiraTicket  jira ticket name
     * @return execution id
     * @throws Exception
     */
    public String getExecutionId(String projectId, String versionName, String cycleName, String jiraTicket) throws Exception {
        String versionId = getVersionId(properties.getProperty("jira.project.key"), versionName);
        Response response = httpRequest.get(String.format(getExecutionIdApi, projectId, versionId, getCycleId(projectId, versionId, cycleName)));
        if (response.getStatusCode() != 200) {
            Logger.assertFail("Failed to get execution Id, status returned: " + response.getStatusCode());
        }
        JsonPath jsonPath = response.jsonPath();
        ArrayList arrayList = jsonPath.get("executions");
        HashMap<String, String> dataMap = new HashMap<>();

        for (Object object : arrayList) {
            if (object.toString().contains(jiraTicket)) {
                String[] pairs = object.toString().replaceAll("\\[|\\]", "").split(",");
                for (String pair : pairs) {
                    if (pair.split("=").length > 1) {
                        dataMap.put(pair.split("=")[0].trim(), pair.split("=")[1].trim());

                    }
                }
            }
        }
        return dataMap.get("id");
    }

    /**
     * This will return the jira ticket execution id mentioned version and cycle name in jira.properties name
     *
     * @param jiraTicket
     * @return execution id
     * @throws Exception
     */
    public String getExecutionId(String jiraTicket) {
        String versionId = getVersionId(properties.getProperty("jira.project.key"), properties.getProperty("jira.version.name"));
        Response response = httpRequest.get(String.format(getExecutionIdApi, properties.getProperty("jira.project.id"), versionId, getCycleId(properties.getProperty("jira.project.id"), versionId, AutProperties.getJiraTestCycleName())));
        if (response.getStatusCode() != 200) {
            Logger.info("Failed to get execution Id, status returned: " + response.getStatusCode());
        }
        JsonPath jsonPath = response.jsonPath();
        ArrayList arrayList = jsonPath.get("executions");
        HashMap<String, String> dataMap = new HashMap<>();

        for (Object object : arrayList) {
            if (object.toString().contains(jiraTicket)) {
                String[] pairs = object.toString().replaceAll("\\[|\\]", "").split(",");
                for (String pair : pairs) {
                    if (pair.split("=").length > 1) {
                        dataMap.put(pair.split("=")[0].trim(), pair.split("=")[1].trim());
                    }
                }
            }
        }
        return dataMap.get("id");
    }

    /**
     * @param executionId
     * @param status
     * @throws Exception
     */
    public void updateTicketExecutionStatus(String executionId, String status) {
        JSONObject executionDetails = new JSONObject();
        executionDetails.put("status", status);
        executionDetails.put("changeAssignee", "false");
        httpRequest.body(executionDetails.toString());

        // String id = executionId.split(",")[0];
        Response response = httpRequest.put(String.format(updateTicketExecutionStatusApi, executionId));
        if (response.getStatusCode() != 200) {
            Logger.assertFail("Failed to update execution status, status returned: " + response.getStatusCode());
        }
    }

    private static boolean isContain(String source, String subItem) {
        String pattern = "\\b" + subItem + "\\b";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(source);
        return m.find();
    }

    /**
     * Get version id
     *
     * @param projectKey  project name
     * @param versionName version name
     * @return version id
     * @throws Exception
     */
    public String getVersionId(String projectKey, String versionName) {
        try {
            Response response = httpRequest.get(String.format(getVersionIdApi, projectKey));
            if (response.getStatusCode() != 200) {
                Logger.assertFail("Failed to get version Id, status returned: " + response.getStatusCode());
            }
            JsonPath jsonPath = response.getBody().jsonPath();
            ArrayList array = jsonPath.get();
            HashMap<String, String> dataMap = new HashMap();
            for (Object obj : array) {
                if (isContain(obj.toString(), "name=" + versionName)) {
                    String pairs[] = obj.toString().replaceAll("\\{|\\}", "").split(",");
                    for (String pair : pairs) {
                        if (pair.split("=").length > 1)
                            dataMap.put(pair.split("=")[0].trim(), pair.split("=")[1].trim());
                    }
                }
            }
            return dataMap.get("id");
        } catch (Exception e) {
            Logger.assertFail("Failed to get version id", e);
        }
        return null;
    }

    /**
     * get execution cycle id
     *
     * @param projectId project id
     * @param versionId version id
     * @param cycleName cycle name
     * @return cycle id
     * @throws Exception
     */
    public String getCycleId(String projectId, String versionId, String cycleName) {
        try {
            Response response = httpRequest.get(String.format(getCycleIdApi, projectId, versionId));
            if (response.getStatusCode() != 200) {
                Logger.assertFail("Failed to get cycle Id, status returned: " + response.getStatusCode());
            }
            JsonPath jsonPath = response.getBody().jsonPath();
            HashMap<String, String> map = jsonPath.get();
            for (Map.Entry entry : map.entrySet()) {
                {
                    if (isContain(entry.getValue().toString(), "name=" + cycleName))
                        return entry.getKey().toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.info("Exception while getting cycle id for Cycle: " + cycleName, e);
        }
        return null;
    }

    public static URI appendUri(String uri, String appendQuery) throws URISyntaxException {
        URI oldUri = new URI(uri);

        String newQuery = oldUri.getQuery();
        if (newQuery == null) {
            newQuery = appendQuery;
        } else {
            newQuery += "&" + appendQuery;
        }

        URI newUri = new URI(oldUri.getScheme(), oldUri.getAuthority(),
                oldUri.getPath(), newQuery, oldUri.getFragment());

        return newUri;
    }

    /**
     * Append parameters to given url
     *
     * @param url
     * @param parameters
     * @return new String url with given parameters
     * @throws URISyntaxException
     */
    public String appendToUrl(String url, HashMap<String, String> parameters) {
        try {
            URI uri = new URI(url);
            String query = uri.getQuery();

            StringBuilder builder = new StringBuilder();

            if (query != null)
                builder.append(query);

            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                String keyValueParam = entry.getKey() + "=" + entry.getValue();
                if (!builder.toString().isEmpty())
                    builder.append("&");

                builder.append(keyValueParam);
            }

            URI newUri = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), builder.toString(), uri.getFragment());
            return newUri.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This will read the jira.properties file
     *
     * @return Properties
     * @throws Exception
     */
    public Properties readJiraProperties(String aut) {

        Properties properties = null;
        File jiraPropertiesFile = new File(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "properties" + File.separator + aut + File.separator + aut + ".properties");
        if (!jiraPropertiesFile.exists())
            Assert.fail("File: " + jiraPropertiesFile.getAbsolutePath() + " is not found!");

        try (FileReader reader = new FileReader(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "properties" + File.separator + aut + File.separator + aut + ".properties")) {
            properties = new Properties();
            properties.load(reader);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Exception in reading '" + aut + ".properties' file");
        }
        return properties;
    }

}
