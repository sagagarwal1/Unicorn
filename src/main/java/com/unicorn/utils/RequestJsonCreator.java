package com.unicorn.utils;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.unicorn.base.logger.Logger;
import net.serenitybdd.core.Serenity;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
//import serenityrest.util.api.AttachmentDetail;


public class RequestJsonCreator {

    public static JSONObject createCaseSearchInputJson(String caseId, String paymentId,
                                                       String fromDate, String toDate, int[] status, String workGroup, String caseType,
                                                       String orderId, String customerId,
                                                       String confirmationNumber, String regionCode, String countryCode) {
        JSONObject searchJson = new JSONObject();
        searchJson.put("caseId", caseId);
        searchJson.put("paymentId", paymentId);
        searchJson.put("orderId", orderId);
        searchJson.put("customerId", customerId);
        searchJson.put("fromDate", fromDate);
        searchJson.put("toDate", toDate);
        searchJson.put("confirmationNumber", confirmationNumber);
        JSONObject Regionarray = new JSONObject();
        Regionarray.put("regionCode", regionCode);
        Regionarray.put("countryCode", countryCode);
        searchJson.put("regionCountry", Regionarray);
        JSONObject array = new JSONObject();
        array.put("status", status);
        array.put("workGroup", workGroup);
        array.put("caseType", caseType);
        searchJson.put("workgroupStatus", array);
        return searchJson;
    }

    public static JSONObject createPaymentSearchInputJson(String paymentId, String fromDate,
                                                          String toDate, String orderId, String customerId,
                                                          boolean searchGoodPaymentFlag, String confirmationNumber, String regionCode,
                                                          String countryCode) {
        JSONObject searchJson = new JSONObject();
        searchJson.put("paymentId", paymentId);
        searchJson.put("orderId", orderId);
        searchJson.put("customerId", customerId);
        searchJson.put("confirmationNumber", confirmationNumber);
        searchJson.put("fromDate", fromDate);
        searchJson.put("toDate", toDate);
        searchJson.put("searchGoodPayment", searchGoodPaymentFlag);
        JSONObject Regionarray = new JSONObject();
        Regionarray.put("regionCode", regionCode);
        Regionarray.put("countryCode", countryCode);
        searchJson.put("regionCountry", Regionarray);
        return searchJson;
    }

    public static JSONObject createHighPriorityJson(String caseKey, String HighPriorityFlag,
                                                    String caseLevel,
                                                    String loggedInAnalystLoginId, String loggedInAnalystUserId, String loggedInAnalystName,
                                                    String analystLevel,
                                                    String analystWorkGroup, String analystWorkLevel) {
        JSONObject createHighPriorityJson = new JSONObject();
        createHighPriorityJson.put("caseKey", caseKey);
        createHighPriorityJson.put("caseWeight", HighPriorityFlag);
        createHighPriorityJson.put("caseLevel", caseLevel);
        createHighPriorityJson.put("queueId", Serenity.getCurrentSession().get("QueueID").toString());
        createHighPriorityJson
                .put("regionCode", Serenity.getCurrentSession().get("RegionCode").toString());
        JSONObject array = new JSONObject();
        array.put("analystLoginId", loggedInAnalystLoginId);
        array.put("analystUserId", loggedInAnalystUserId);
        array.put("analystName", loggedInAnalystName);
        array.put("analystLevel", analystLevel);
        array.put("analystWorkGroup", analystWorkGroup);
        array.put("analystWorkLevel", analystWorkLevel);
        createHighPriorityJson.put("analystDetails", array);
        return createHighPriorityJson;
    }

    public static JSONObject createGetCaseJson(String queueID, String workgroupId,
                                               String analystLoginId, String userId, String analystName, String analystLevel,
                                               String analystWorkGroup, String analystWorkLevel) {
        JSONObject createGetCaseJson = new JSONObject();
        createGetCaseJson.put("queueId", queueID);
        createGetCaseJson.put("workgroupId", workgroupId);
        createGetCaseJson.put("sessionId", "abcd");
        //
        JSONObject array = new JSONObject();
        array.put("analystLoginId", analystLoginId);
        array.put("analystUserId", userId);
        array.put("analystName", analystName);
        array.put("analystLevel", analystLevel);
        array.put("analystWorkGroup", analystWorkGroup);
        array.put("analystWorkLevel", analystWorkLevel);
        createGetCaseJson.put("analystDetails", array);
        return createGetCaseJson;
    }

    public static JSONObject addCommentJSON(String AnalystLevel, String comment) throws IOException {
        JSONObject addCommentJson = new JSONObject();
        addCommentJson.put("caseKey", Serenity.getCurrentSession().get("CaseKey").toString());
        addCommentJson.put("comment", comment);

        String query =
                "SELECT processing_Stage from CaseManagement where caseKey='" + Serenity.getCurrentSession()
                        .get("CaseKey").toString() + "'";
        Map<String, List<String>> data = CouchBaseUtility.executeCouchBaseQuery(query);
        addCommentJson.put("caseProcessingStage",
                data.get("processing_Stage").toString().replace("[", "").replace("]", ""));
        if (AnalystLevel.contains("L3")) {
            AnalystLevel = "L3";
        }
        String[] UserDetail = Serenity.getCurrentSession().get(AnalystLevel + "UserDetails").toString()
                .split(";");
        JSONObject arrayElementOne = new JSONObject();
        arrayElementOne.put("queueId", Serenity.getCurrentSession().get("QueueID").toString());
        arrayElementOne.put("regionCode", Serenity.getCurrentSession().get("RegionCode").toString());
        JSONObject array = new JSONObject();
        array.put("analystLoginId", UserDetail[0]);
        array.put("analystUserId", UserDetail[1]);
        array.put("analystName", UserDetail[2]);
        array.put("analystLevel", UserDetail[6]);
        array.put("analystWorkGroup", UserDetail[3]);
        array.put("analystWorkLevel", UserDetail[4]);
        addCommentJson.put("analystDetails", array);
        addCommentJson.put("headerInfo", arrayElementOne);
        return addCommentJson;
    }

    public static JSONObject bulkDispositionJSON(String caseKey, String dispositionCommentsData,
                                                 String dispositionReasonData, String dispositionTypeData) throws IOException {
        String query =
                "SELECT caseDispo.dispositionDocId,caseAnalyst.lastAssignedUserName,caseAnalyst.lastAssignedUserId,processing_Stage,queue.queueName,queue.queueId "
                        + ",regionInfo.regionCode,regionInfo.regionName,regionInfo.countryName from CaseManagement where caseKey='"
                        + caseKey + "'";
        Map<String, List<String>> data = CouchBaseUtility.executeCouchBaseQuery(query);
        query =
                "SELECT * from CaseManagement where meta().id='" + data.get("dispositionDocId").toString()
                        .replace("[", "").replace("]", "") + "'";
        String dispositionDocDetail = CouchBaseUtility.executeCouchBaseQuery1(query).get(0).toString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(dispositionDocDetail);
        JsonNode CaseManagementNode = rootNode.path("CaseManagement");
        JsonNode HitsNode = CaseManagementNode.path("hits");
        Configuration config = Configuration.builder().jsonProvider(new JacksonJsonNodeJsonProvider())
                .mappingProvider(new JacksonMappingProvider()).build();
        DocumentContext UpdatedDispositionJson = null;
        String DispositionJson = rootNode.toString();
        for (int i = 0; i < HitsNode.size(); i++) {
            String dispositionComments = "CaseManagement.hits[" + i + "].disposition.comment";
            String dispositionReason = "CaseManagement.hits[" + i + "].disposition.reason";
            String dispositionType = "CaseManagement.hits[" + i + "].disposition.type";
            String dispositioned = "CaseManagement.hits[" + i + "].disposition.dispositioned";
            String dispositionedsimilarBulk = "CaseManagement.hits[" + i + "].disposition.similarBulk";
            String dispositioneddispositionSummarydata =
                    "CaseManagement.hits[" + i + "].disposition.dispositionSummarydata";
            UpdatedDispositionJson = JsonPath.using(config).parse(DispositionJson)
                    .set(dispositionComments, dispositionCommentsData);
            UpdatedDispositionJson = JsonPath.using(config).parse(UpdatedDispositionJson.jsonString())
                    .set(dispositionReason, dispositionReasonData);
            UpdatedDispositionJson = JsonPath.using(config).parse(UpdatedDispositionJson.jsonString())
                    .set(dispositionType, dispositionTypeData);
            UpdatedDispositionJson = JsonPath.using(config).parse(UpdatedDispositionJson.jsonString())
                    .add(dispositioned, true);
            UpdatedDispositionJson = JsonPath.using(config).parse(UpdatedDispositionJson.jsonString())
                    .add(dispositionedsimilarBulk, true);
            UpdatedDispositionJson = JsonPath.using(config).parse(UpdatedDispositionJson.jsonString())
                    .add(dispositioneddispositionSummarydata, "");
            DispositionJson = UpdatedDispositionJson.jsonString();

        }
        rootNode = mapper.readTree(UpdatedDispositionJson.jsonString());
        CaseManagementNode = rootNode.path("CaseManagement");
        JSONObject object1 = new JSONObject(CaseManagementNode.toString());
        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();
        JSONObject arrayElementOne = new JSONObject();
        arrayElementOne.put("analystName",
                data.get("lastAssignedUserName").toString().replace("[", "").replace("]", ""));
        arrayElementOne
                .put("queueName", data.get("queueName").toString().replace("[", "").replace("]", ""));
        arrayElementOne.put("unassignedCaseCount", 0);
        arrayElementOne.put("emailId", "abc@wu.com");
        arrayElementOne
                .put("userId", data.get("lastAssignedUserId").toString().replace("[", "").replace("]", ""));
        arrayElementOne.put("processingStage",
                data.get("processing_Stage").toString().replace("[", "").replace("]", ""));
        arrayElementOne.put("workLevel",
                data.get("processing_Stage").toString().replace("[", "").replace("]", ""));
        arrayElementOne
                .put("queueId", data.get("queueId").toString().replace("[", "").replace("]", ""));
        arrayElementOne
                .put("regionCode", data.get("regionCode").toString().replace("[", "").replace("]", ""));
        arrayElementOne
                .put("regionName", data.get("regionName").toString().replace("[", "").replace("]", ""));
        arrayElementOne
                .put("countryName", data.get("countryName").toString().replace("[", "").replace("]", ""));
        String[] L1UserDetail = Serenity.getCurrentSession().get("L1UserDetails").toString().split(";");
        arrayElementOne.put("loginId", L1UserDetail[0]);

        JSONObject arrayElementTwo = new JSONObject();
        arrayElementTwo.put("analystLoginId", L1UserDetail[0]);
        arrayElementTwo.put("analystUserId", L1UserDetail[1]);
        arrayElementTwo.put("analystName", L1UserDetail[2]);
        arrayElementTwo.put("analystLevel", L1UserDetail[4]);
        arrayElementTwo.put("analystWorkGroup", L1UserDetail[3]);
        arrayElementTwo.put("analystWorkLevel", L1UserDetail[4]);
        array.put(arrayElementOne);
        object.put("caseKey", caseKey);
        object.put("headerInfo", arrayElementOne);
        object.put("analystDetails", arrayElementTwo);
        JSONObject mainObject = new JSONObject(object.toString());
        mainObject.accumulate("dispositionedHitsModel", object1);
        //object.put("dispositionedHitsModel", nameNode6.toString().replace("\\", ""));
        System.out.println(mainObject.toString());
        return mainObject;
    }
	
	/*public static JSONObject addAttachmentJSON(String caseKey,ArrayList<AttachmentDetail> caseAttachmentDetails ){
		JSONObject addAttachmentJSON = new JSONObject();
		addAttachmentJSON.put("caseKey", caseKey);
	
		JSONObject caseAttachments = new JSONObject();		
		for (int i = 0; i < caseAttachmentDetails.size(); i++) {
			JSONObject attachmentDetailAPIJSON = new JSONObject();
			attachmentDetailAPIJSON.put("attachmentId",	caseAttachmentDetails.get(i).attachmentId);
			attachmentDetailAPIJSON.put("attachmentName",caseAttachmentDetails.get(i).attachmentName);
			attachmentDetailAPIJSON.put("attachmentType",caseAttachmentDetails.get(i).attachmentType);
			attachmentDetailAPIJSON.put("tag",caseAttachmentDetails.get(i).tag);
			attachmentDetailAPIJSON.put("userId",caseAttachmentDetails.get(i).userId);
			attachmentDetailAPIJSON.put("userName",caseAttachmentDetails.get(i).userName);
			attachmentDetailAPIJSON.put("size", caseAttachmentDetails.get(i).size);
			caseAttachments.put("", caseAttachments);
		}
		addAttachmentJSON.put("caseAttachments", caseAttachments);
		return addAttachmentJSON;
	}*/

    public static JSONObject CaseDispositionJSON(String caseKey, String comment,
                                                 String failSubStatus, String assignToUser, String subRegion, String escalatedProcessingStage,
                                                 String AnalystLevel) throws IOException {
        String query =
                "select processing_Stage,caseAnalyst.lastAssignedUserId,caseAnalyst.lastAssignedUserName,createTimestamp from CaseManagement where documentType='Case' and caseKey='"
                        + caseKey + "'";
        Map<String, List<String>> data = CouchBaseUtility.executeCouchBaseQuery(query);
        JSONObject createCaseDispositionJson = new JSONObject();
        createCaseDispositionJson.put("caseKey", caseKey);
        createCaseDispositionJson.put("comment", comment);
        createCaseDispositionJson.put("processingStage",
                data.get("processing_Stage").toString().replace("[", "").replace("]", ""));
        createCaseDispositionJson.put("analystName",
                data.get("lastAssignedUserName").toString().replace("[", "").replace("]", ""));
        createCaseDispositionJson.put("analystId",
                data.get("lastAssignedUserId").toString().replace("[", "").replace("]", ""));
        createCaseDispositionJson.put("caseFailCode", failSubStatus);
        createCaseDispositionJson.put("assignToUser", assignToUser);
        createCaseDispositionJson.put("subRegion", subRegion);
        createCaseDispositionJson.put("createTimestamp",
                data.get("createTimestamp").toString().replace("[", "").replace("]", ""));
        createCaseDispositionJson.put("escalatedProcessingStage", escalatedProcessingStage);
        createCaseDispositionJson.put("messageID", Serenity.getCurrentSession().get("messageID"));
        createCaseDispositionJson.put("platform", Integer.toString(CommonFunctions.getDataSourceID()));
        createCaseDispositionJson.put("queueId", Serenity.getCurrentSession().get("QueueID"));
        createCaseDispositionJson.put("regionCode", Serenity.getCurrentSession().get("RegionCode"));
        String[] UserDetail = null;
        UserDetail = Serenity.getCurrentSession().get(AnalystLevel + "UserDetails").toString().split(";");
        JSONObject array = new JSONObject();
        array.put("analystLoginId", UserDetail[0]);
        array.put("analystUserId", UserDetail[1]);
        array.put("analystName", UserDetail[2]);
        array.put("analystLevel", UserDetail[6]);
        array.put("analystWorkGroup", UserDetail[3]);
        array.put("analystWorkLevel", UserDetail[4]);
        createCaseDispositionJson.put("analystDetails", array);
        return createCaseDispositionJson;
    }

    public static JSONObject assignCaseAPI(String caseKey, String caseProcessingStage,

                                           String analystName, String emailId,
                                           String userId, String loginId, String workLevel,
                                           String analystLoginId, String assignedToUserID, String assignedToAnalystName,
                                           String assignedToAnalystLevel
            , String assignedToAnalystWorkLevel) throws IOException {
        String query =
                "SELECT caseDispo.dispositionDocId,caseAnalyst.lastAssignedUserName,caseAnalyst.lastAssignedUserId,processing_Stage,queue.queueName,queue.queueId "
                        + ",regionInfo.regionCode,regionInfo.regionName,regionInfo.countryName from CaseManagement where caseKey='"
                        + caseKey + "'";
        Map<String, List<String>> data = CouchBaseUtility.executeCouchBaseQuery(query);
        JSONObject assignCaseJSON = new JSONObject();
        assignCaseJSON.put("caseKey", caseKey);
        assignCaseJSON.put("caseProcessingStage", caseProcessingStage);
        //
        //String assignedToAnalystName,
        //assignCaseJSON.put("assignedToUserID", assignedToUserID);
        //assignCaseJSON.put("assignedToAnalystName",	assignedToAnalystName);

        JSONObject headerInfoJSON = new JSONObject();
        headerInfoJSON
                .put("queueName", data.get("queueName").toString().replace("[", "").replace("]", ""));
        headerInfoJSON.put("unassignedCaseCount", 0);
        headerInfoJSON.put("analystName", analystName);
        headerInfoJSON.put("emailId", emailId);
        headerInfoJSON.put("userId", userId);
        headerInfoJSON.put("processingStage", caseProcessingStage);
        headerInfoJSON
                .put("regionName", data.get("regionName").toString().replace("[", "").replace("]", ""));
        headerInfoJSON
                .put("countryName", data.get("countryName").toString().replace("[", "").replace("]", ""));
        headerInfoJSON.put("loginId", loginId);
        headerInfoJSON.put("workLevel", workLevel);
   /* headerInfoJSON.put("queueId", Serenity.getCurrentSession().get("QueueID").toString());
    headerInfoJSON.put("regionCode", Serenity.getCurrentSession().get("RegionCode").toString());*/
        headerInfoJSON.put("queueId", data.get("queueId").toString());
        headerInfoJSON.put("regionCode", data.get("regionCode").toString());

        JSONObject analystDetailsJSON = new JSONObject();
        analystDetailsJSON.put("analystLoginId", analystLoginId);
        analystDetailsJSON.put("analystUserId", assignedToUserID);
        analystDetailsJSON.put("analystName", assignedToAnalystName);
        analystDetailsJSON.put("analystLevel", assignedToAnalystLevel);
        analystDetailsJSON.put("analystWorkGroup", assignedToAnalystWorkLevel);
        analystDetailsJSON.put("analystWorkLevel", assignedToAnalystLevel);

        assignCaseJSON.put("headerInfo", headerInfoJSON);
        assignCaseJSON.put("analystDetails", analystDetailsJSON);
        return assignCaseJSON;

    }

    public static JSONObject caseWorkViewModeAPI(String caseKey, String mode,
                                                 String loggedInAnalystName, String loggedInAnalystUserId, String processingStage
            , String loggedInAnalystLognID, String assignedToAnalystWorkLevel, String AnalystWorkGroup,
                                                 String assignedToAnalystLevel) {
        //{"caseKey":"269091","mode":"work","loggedInAnalystName":"Anurag Agarwal","loggedInAnalystId":"1000001048","processingStage":"L1"}
        JSONObject caseWorkViewModeJSON = new JSONObject();
        caseWorkViewModeJSON.put("caseKey", caseKey);
        caseWorkViewModeJSON.put("mode", mode);
        caseWorkViewModeJSON.put("queueId", Serenity.getCurrentSession().get("QueueID").toString());
        caseWorkViewModeJSON
                .put("regionCode", Serenity.getCurrentSession().get("RegionCode").toString());
        JSONObject analystDetailsJSON = new JSONObject();
        analystDetailsJSON.put("analystLoginId", loggedInAnalystLognID);
        analystDetailsJSON.put("analystUserId", loggedInAnalystUserId);
        analystDetailsJSON.put("analystName", loggedInAnalystName);
        analystDetailsJSON.put("analystLevel", assignedToAnalystLevel);
        analystDetailsJSON.put("analystWorkGroup", AnalystWorkGroup);
        analystDetailsJSON.put("analystWorkLevel", assignedToAnalystWorkLevel);
        caseWorkViewModeJSON.put("analystDetails", analystDetailsJSON);

        return caseWorkViewModeJSON;
    }

    public static JSONObject decisionSyncAPI(String id, String name, String routingCode,
                                             String checksum, String dateTime, String comment,
                                             String operator, String statusName, String unit, String businessUnit, String messageID,
                                             String systemID, String comments, String Operator) {
        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();
        JSONObject arrayElementOne = new JSONObject();
        arrayElementOne.put("id", id);
        arrayElementOne.put("name", name);
        arrayElementOne.put("routingCode", routingCode);
        arrayElementOne.put("checksum", checksum);
        array.put(arrayElementOne);
        JSONObject arrayElementTwo = new JSONObject();
        arrayElementTwo.put("dateTime", dateTime);
        arrayElementTwo.put("comment", comment);
        arrayElementTwo.put("operator", operator);
        arrayElementTwo.put("statusName", statusName);
        array.put(arrayElementTwo);
        object.put("status", arrayElementOne);
        object.put("action", arrayElementTwo);
        object.put("unit", unit);
        object.put("businessUnit", businessUnit);
        object.put("messageID", messageID);
        object.put("systemID", systemID);
        object.put("comment", comments);
        object.put("operator", Operator);
        return object;
    }

    public static String updateJsonRequest(String JsonRequest, String fieldname, String fieldvalue) {
        Configuration config = Configuration.builder()
                .jsonProvider(new JacksonJsonNodeJsonProvider())
                .mappingProvider(new JacksonMappingProvider())
                .build();
        DocumentContext newJson = null;
        newJson = JsonPath.using(config).parse(JsonRequest).
                set(fieldname, fieldvalue);
        Object document = Configuration.defaultConfiguration().jsonProvider()
                .parse(newJson.jsonString());
        return newJson.jsonString();
    }

    public static String updateJson(String filename, String fieldname, String fieldvalue)
            throws IOException {
        Configuration config = Configuration.builder()
                .jsonProvider(new JacksonJsonNodeJsonProvider())
                .mappingProvider(new JacksonMappingProvider())
                .build();
        DocumentContext newJson = null;
        String JsonfileName = CommonFunctions.getFullPath(filename);
        newJson = JsonPath.using(config).parse(new File(JsonfileName)).
                set(fieldname, fieldvalue);
        Object document = Configuration.defaultConfiguration().jsonProvider()
                .parse(newJson.jsonString());
        return newJson.jsonString();
    }

    public static LinkedHashMap<String, String> readDataFromFileAndConvertIntoMap(String fileName) {

        BufferedReader br = null;
        FileReader fr = null;
        String datafileName = CommonFunctions.getFullPath(fileName);
        LinkedHashMap<String, String> DataMap = new LinkedHashMap<String, String>();
        try {
            fr = new FileReader(datafileName);
            br = new BufferedReader(fr);

            String sCurrentLine;
            String paramName;
            String paramValue;
            int tokens = 0;
            // String updatedJson;

            while ((sCurrentLine = br.readLine()) != null) {
                if (null != sCurrentLine && !sCurrentLine.equals("")) {
                    if (sCurrentLine.contains("=")) {
                        String[] arr = sCurrentLine.split("=");
                        //StringTokenizer st = new StringTokenizer(sCurrentLine,"=");
                        //tokens = st.countTokens();
                        //if (tokens == 2) {
                        paramName = arr[0]; //st.nextToken();
                        paramValue = arr[1]; //st.nextToken();
                        DataMap.put(paramName.trim(), paramValue.trim());
                        //}
                    } else if (sCurrentLine.contains("@SQL")) {
                        DataMap.put("@SQL", "@SQL");
                    }

                }
            }

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (br != null) {
                    br.close();
                }

                if (fr != null) {
                    fr.close();
                }

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }
        return DataMap;
    }

    public Map<String, String> updateTheGivenNodeInJson(String nodeToBeUpdated) {
        String jsonPathToTheNode = null;
        long startTime = System.currentTimeMillis();
        PropertyFileReader propertyFileReader = new PropertyFileReader();
        RequestJsonCreator jsonCreator = new RequestJsonCreator();

        HashMap<String, String> updatedJsonMap = new HashMap<String, String>();

        String propValueOFAC;
        if ((Serenity.getCurrentSession().get("GoodKeywordFlag")).equals(true)) {
            propValueOFAC = propertyFileReader
                    .getPropertyValue(CommonFunctions.getFullPath("json-api", "good_keywords.properties"),
                            "ofac_keyword");
            Serenity.getCurrentSession().put("GoodKeywordFlag", false);
        } else {
            propValueOFAC = propertyFileReader
                    .getPropertyValue(CommonFunctions.getFullPath("json-api", "ofac_keywords.properties"),
                            "ofac_keyword");
        }
        //System.out.println(propValueOFAC);
        jsonPathToTheNode = propertyFileReader.getPropertyValue(
                CommonFunctions.getFullPath("json-api", "test-field-and-json-path.properties"),
                nodeToBeUpdated);

        Serenity.getCurrentSession().put("jsonPathToTheNode", jsonPathToTheNode);
        jsonCreator.writeToParamsFile(jsonPathToTheNode + ": " + propValueOFAC);

        updatedJsonMap = jsonCreator.prepareTestData("json-api", "params.txt");

        //Iterator it = updatedJsonMap.entrySet().iterator();
        //while (it.hasNext()) {
        //	Map.Entry pair = (Map.Entry) it.next();
        //	System.out.println(pair.getKey() + " = " + pair.getValue());
        // it.remove(); // avoids a ConcurrentModificationException
        //}

        long endTime = System.currentTimeMillis();
        Logger.info("Job completed in " + ((endTime - startTime)) + " milli secs");
        return updatedJsonMap;
    }

    public HashMap<String, String> prepareTestData(String folder, String inputDataFile) {

        HashMap<String, String> updatedJsonMap = new HashMap<String, String>();
        File paramsFile = CommonFunctions.loadFile(folder, inputDataFile);
        updatedJsonMap = readDataFromFileAndCreateJsonMap(paramsFile);

        return updatedJsonMap;
    }

    public HashMap<String, String> prepareTestDataWithGivenJasonNodePath(String folder,
                                                                         String inputDataFile) {

        HashMap<String, String> updatedJsonMap = new HashMap<String, String>();
        File paramsFile = CommonFunctions.loadFile(folder, inputDataFile);
        updatedJsonMap = readDataFromFileAndCreateJsonMap(paramsFile);

        return updatedJsonMap;
    }

    public String updateJson(String paramName, String paramValue) {
        String fileName = null;
        Configuration config = Configuration.builder().jsonProvider(new JacksonJsonNodeJsonProvider())
                .mappingProvider(new JacksonMappingProvider()).build();
        if (Serenity.getCurrentSession().get("paymentSourcePlatform").toString().equals("CCT")) {
            fileName = CommonFunctions.getFullPath("json-api", "input_CCT.json");
        } else {
            fileName = CommonFunctions.getFullPath("json-api", "input.json");
        }
        DocumentContext newJson1 = null;
        DocumentContext newJson2 = null;
        DocumentContext newJson3 = null;
        DocumentContext newJson4 = null;
        DocumentContext newJson = null;
        String sourcePlatformID = null;
        try {
            /**
             * added the below piece of code  to dynamically handle the payment source
             * author - Anurag
             */
            newJson1 = JsonPath.using(config).parse(new File(fileName)).set(paramName, paramValue);
            //System.out.println("new : " + newJson1.jsonString());
            if (Serenity.getCurrentSession().get("paymentSourcePlatform").toString() == null) {
                Serenity.getCurrentSession().put("paymentSourcePlatform", "SPOT");
            }
            newJson2 = JsonPath.using(config).parse(newJson1.jsonString()).set("payment.platform",
                    Serenity.getCurrentSession().get("paymentSourcePlatform").toString());
            if (Serenity.getCurrentSession().get("paymentSourcePlatform").toString().equals("SPOT")) {
                sourcePlatformID = "1";
            } else if (Serenity.getCurrentSession().get("paymentSourcePlatform").toString()
                    .equals("MPS")) {
                sourcePlatformID = "13";
            } else if (Serenity.getCurrentSession().get("paymentSourcePlatform").toString()
                    .equals("H2H")) {
                sourcePlatformID = "28";
            } else if (Serenity.getCurrentSession().get("paymentSourcePlatform").toString()
                    .equals("CCT")) {
                sourcePlatformID = "10";
            }
            String pattern = "YYYYMMdd_hhmmss_SSS";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String messageID = simpleDateFormat.format(System.currentTimeMillis());
            pattern = "YYYYMMdd_hhmmss";
            String CustomerSourcePattern = "YYYYMMdd";

            String SourcePaymentId = null;
            if (Serenity.getCurrentSession().get("paymentSourcePlatform").toString().equals("CCT")) {
                simpleDateFormat = new SimpleDateFormat(pattern);
                SourcePaymentId = simpleDateFormat.format(System.currentTimeMillis());
            } else {
                SourcePaymentId = messageID;
            }
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(CustomerSourcePattern);
            String CustomerID = simpleDateFormat1.format(System.currentTimeMillis());
            if ((boolean) Serenity.getCurrentSession().get("PaymentSourcePlatformIDFieldValueIsBlank")) {

                if (Serenity.getCurrentSession().get("PaymentFieldValueIsBlank").toString()
                        .equals("messageID")) {
                    newJson3 = JsonPath.using(config).parse(newJson2.jsonString()).set("messageID", "");
                    newJson4 = JsonPath.using(config).parse(newJson2.jsonString())
                            .set("payment.sourcePlatformID", sourcePlatformID);
                    newJson = JsonPath.using(config).parse(newJson3.jsonString())
                            .set("payment.sourcePaymentID", SourcePaymentId);
                    //newJson =  JsonPath.using(config).parse(newJson3.jsonString()).set("payment.orderID", SourcePaymentId);

                } else if (Serenity.getCurrentSession().get("PaymentFieldValueIsBlank").toString()
                        .equals("sourcePaymentID") || Serenity.getCurrentSession()
                        .get("PaymentFieldValueIsBlank").toString().equals("systemID")) {
                    newJson3 = JsonPath.using(config).parse(newJson2.jsonString())
                            .set("payment.sourcePaymentID", "");
                    newJson4 = JsonPath.using(config).parse(newJson2.jsonString())
                            .set("payment.sourcePlatformID", sourcePlatformID);
                    newJson = JsonPath.using(config).parse(newJson3.jsonString()).set("messageID", messageID);
                    //newJson =  JsonPath.using(config).parse(newJson3.jsonString()).set("payment.orderID", messageID);
                } else if (Serenity.getCurrentSession().get("PaymentFieldValueIsBlank").toString()
                        .equals("sourcePlatformID")) {
                    newJson3 = JsonPath.using(config).parse(newJson2.jsonString())
                            .set("payment.sourcePlatformID", "");
                    newJson4 = JsonPath.using(config).parse(newJson3.jsonString())
                            .set("payment.sourcePaymentID", SourcePaymentId);
                    newJson = JsonPath.using(config).parse(newJson4.jsonString()).set("messageID", messageID);
                    //newJson =  JsonPath.using(config).parse(newJson3.jsonString()).set("payment.orderID", SourcePaymentId);
                }
            } else {
                newJson3 = JsonPath.using(config).parse(newJson2.jsonString())
                        .set("payment.sourcePlatformID", sourcePlatformID);
                newJson4 = JsonPath.using(config).parse(newJson3.jsonString())
                        .set("payment.sourcePaymentID", SourcePaymentId);
                newJson = JsonPath.using(config).parse(newJson4.jsonString()).set("messageID", messageID);
            }
            if ((boolean) Serenity.getCurrentSession().get("PaymentSourcePlatformIDFieldValueIsBlank")) {
                if (Serenity.getCurrentSession().get("PaymentFieldValueIsBlank").toString()
                        .equals("sourcePlatformID")) {
                    sourcePlatformID = "10";
                }
            }
            newJson = JsonPath.using(config).parse(newJson.jsonString())
                    .set("payment.confirmationNumber", "C" + messageID);
            newJson = JsonPath.using(config).parse(newJson.jsonString())
                    .set("payment.parties[0].misc.sourcePlatformID", sourcePlatformID);
            newJson = JsonPath.using(config).parse(newJson.jsonString())
                    .set("payment.parties[2].misc.sourcePlatformID", sourcePlatformID);
            newJson = JsonPath.using(config).parse(newJson.jsonString())
                    .set("payment.parties[3].misc.sourcePlatformID", sourcePlatformID);
            newJson = JsonPath.using(config).parse(newJson.jsonString())
                    .set("payment.parties[4].misc.sourcePlatformID", sourcePlatformID);
            newJson = JsonPath.using(config).parse(newJson.jsonString())
                    .set("payment.parties[5].misc.sourcePlatformID", sourcePlatformID);
            newJson = JsonPath.using(config).parse(newJson.jsonString())
                    .set("payment.bankDetails[0].sourcePlatformID", sourcePlatformID);
            newJson = JsonPath.using(config).parse(newJson.jsonString())
                    .set("payment.bankDetails[1].sourcePlatformID", sourcePlatformID);
            newJson = JsonPath.using(config).parse(newJson.jsonString())
                    .set("payment.bankDetails[2].sourcePlatformID", sourcePlatformID);
            newJson = JsonPath.using(config).parse(newJson.jsonString())
                    .set("payment.bankDetails[3].sourcePlatformID", sourcePlatformID);
            newJson = JsonPath.using(config).parse(newJson.jsonString())
                    .set("payment.orderID", SourcePaymentId);
            newJson = JsonPath.using(config).parse(newJson.jsonString())
                    .set("payment.parties[0].ID", CustomerID);
            newJson = JsonPath.using(config).parse(newJson.jsonString())
                    .set("payment.customerID", CustomerID);
            //newJson4 =  JsonPath.using(config).parse(newJson3.jsonString()).set("payment.sourcePaymentID", SourcePaymentId);
            //newJson =  JsonPath.using(config).parse(newJson4.jsonString()).set("messageID", messageID);
            Serenity.getCurrentSession().put("ConfirmationNumber", "C" + messageID);
            Serenity.getCurrentSession().put("customerID", CustomerID);
            Serenity.getCurrentSession().put("OrderID", SourcePaymentId);
            Serenity.getCurrentSession().put("OutgoingPaymentID", SourcePaymentId);
            Serenity.getCurrentSession().put("messageID", messageID);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // System.out.println("new : " + newJson.jsonString());

        // String json = "...";
        Object document = Configuration.defaultConfiguration().jsonProvider()
                .parse(newJson.jsonString());

        String val = JsonPath.read(document, paramName);
        // System.out.println(paramName + " : " + val);

        return newJson.jsonString();
    }

    public HashMap<String, String> readDataFromFileAndCreateJsonMap(File file) {

        BufferedReader br = null;
        FileReader fr = null;

        HashMap<String, String> updatedJsonMap = new HashMap<String, String>();
        try {

            // br = new BufferedReader(new FileReader(FILENAME));
            fr = new FileReader(file);
            br = new BufferedReader(fr);

            String sCurrentLine;
            String paramName;
            String paramValue;
            int tokens = 0;
            String updatedJson;

            while ((sCurrentLine = br.readLine()) != null) {
                // System.out.println(sCurrentLine);

                if (null != sCurrentLine && !sCurrentLine.equals("") && sCurrentLine.contains(":")) {
                    StringTokenizer st = new StringTokenizer(sCurrentLine, ":");

                    // this code expects that each line has
                    // param name : param value
                    // there are no extra colon on the line

                    tokens = st.countTokens();

                    if (tokens == 2) {
                        // System.out.println(st.nextToken() + " --> " +
                        // st.nextToken());
                        paramName = st.nextToken();
                        paramValue = st.nextToken();
                        updatedJson = updateJson(paramName.trim(), paramValue.trim());

                        // System.out.println(paramName + " --> " + paramValue);
                        // System.out.println(updatedJson);
                        updatedJsonMap.put(paramName, updatedJson);
                    }
                }
            }

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (br != null) {
                    br.close();
                }

                if (fr != null) {
                    fr.close();
                }

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }

        return updatedJsonMap;

    }

    public void writeToParamsFile(String contentToWrite) {

        String FILENAME = "params.txt";

        BufferedWriter bw = null;
        FileWriter fw = null;

        try {

            String content = contentToWrite;
            FILENAME = CommonFunctions.getFullPath("json-api", "params.txt");

            fw = new FileWriter(FILENAME);
            bw = new BufferedWriter(fw);
            bw.write(content);

            Logger.info("Done");

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (bw != null) {
                    bw.close();
                }

                if (fw != null) {
                    fw.close();
                }

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }

    }

    public String addSystemId(String systemId, String inputJson) {

        StringBuffer strBuff = new StringBuffer(inputJson);
        int startPos = inputJson.indexOf("payment");
        strBuff.insert(startPos, "systemID\": \"" + systemId + "\",\n" + "  \"");
        System.out.println(strBuff.toString());
        return strBuff.toString();
    }

    public HashMap<String, String> readDataFromFileAndCreateJsonMap(String fileName, String jsonFileName, String fieldName) {
        BufferedReader br = null;
        FileReader fr = null;
        int line = -1;
        String updatedJson = null;
        String fileName1 = CommonFunctions.getFullPath(fileName);
        HashMap<String, String> updatedJsonMap = new HashMap<String, String>();
        try {
            fr = new FileReader(fileName1);
            br = new BufferedReader(fr);

            String sCurrentLine;
            String paramName;
            String paramValue;
            int tokens = 0;
            // String updatedJson;

            while ((sCurrentLine = br.readLine()) != null) {
                if (null != sCurrentLine && !sCurrentLine.equals("") && sCurrentLine.contains("=")) {
                    line = line + 1;
                    StringTokenizer st = new StringTokenizer(sCurrentLine, "=");
                    tokens = st.countTokens();
                    if (tokens == 2) {
                        paramName = st.nextToken();
                        paramValue = st.nextToken();
                        if (line == 0) {
                            updatedJson = updateJson(jsonFileName, paramName.trim(), paramValue.trim());
//							System.out.println("Line : " + line +  "took from params.txt and updated the json" +"\n" + updatedJson );

                        } else {
                            updatedJson = updateJsonRequest(updatedJson, paramName, paramValue);
//							System.out.println("Line : " + line +  "took from params.txt and updated the json" +"\n" + updatedJson );

                        }
                        updatedJsonMap.put("updatedJson", updatedJson);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return updatedJsonMap;
    }

    /**
     * Get entity search input json
     *
     * @return
     */
    public static JSONObject getEntitySearchInputJson(Map<String, String> requestFieldMap) {
        String requestFields[] = {"customerId", "customerName", "salesforceAccountId", "fromDate", "toDate"};

        JSONObject entityJsonMap = new JSONObject();
        JSONObject workgroupStatus = new JSONObject();
        JSONArray status = new JSONArray();
        boolean isSearchCaseFieldfound = false;
        for (Map.Entry entry : requestFieldMap.entrySet()) {
            if (entry.getKey().toString().equals("status")) {
                status.put(entry.getValue());
                workgroupStatus.put("workgroupStatus", status);
            } else if (entry.getKey().toString().equals("workGroup")) {
                workgroupStatus.put("workGroup", entry.getValue());
            } else if (entry.getKey().toString().equals("caseType")) {
                workgroupStatus.put("caseType", entry.getValue());
            } else {
                entityJsonMap.put(entry.getKey().toString(), entry.getValue());
                if (!workgroupStatus.isEmpty())
                    entityJsonMap.put("workgroupStatus", workgroupStatus);
                isSearchCaseFieldfound = true;
            }
        }

        if (!isSearchCaseFieldfound)
            Logger.assertFail("No 'Search By' field found for creating entity search input json");
        System.out.println(entityJsonMap.toString());
        return entityJsonMap;
    }

    /**
     * Get entity search input json
     *
     * @return
     */
    public static JSONObject getEntityCaseSearchInputJson(Map<String, String> requestFieldMap) {
        String requestFields[] = {"entityId", "customerId", "customerName", "caseId", "salesforceAccountId", "fromDate", "toDate", "status", "workGroup", "caseType"};

        JSONObject entityCaseJsonMap = new JSONObject();
        JSONObject workgroupStatus = new JSONObject();
        JSONArray status = new JSONArray();
        boolean isSearchCaseFieldfound = false;
        for (Map.Entry entry : requestFieldMap.entrySet()) {
            if (entry.getKey().toString().equals("status")) {
                status.put(entry.getValue());
                workgroupStatus.put("workgroupStatus", status);
            } else if (entry.getKey().toString().equals("workGroup")) {
                workgroupStatus.put("workGroup", entry.getValue());
            } else if (entry.getKey().toString().equals("caseType")) {
                workgroupStatus.put("caseType", entry.getValue());
            } else {
                entityCaseJsonMap.put(entry.getKey().toString(), entry.getValue());
                if (!workgroupStatus.isEmpty())
                    entityCaseJsonMap.put("workgroupStatus", workgroupStatus);
                isSearchCaseFieldfound = true;
            }
        }

        if (!isSearchCaseFieldfound)
            Logger.assertFail("No 'Search By' field found for creating entity case search input json");
        System.out.println(entityCaseJsonMap.toString());
        return entityCaseJsonMap;
    }

    /**
     * Get entity case disposition input json
     *
     * @param dispositionDetails
     * @return
     */
    public static JSONObject getEntityCaseDispositionInputJson(HashMap<String, Object> dispositionDetails) {
        JSONObject disposition = new JSONObject();
        disposition.put("CaseKey", dispositionDetails.get("CaseKey"));
        disposition.put("Action", dispositionDetails.get("Action"));
        disposition.put("Reason", dispositionDetails.get("Reason"));
        if (((int) dispositionDetails.get("FailStatusID")) != -1) {
            disposition.put("FailStatusID", dispositionDetails.get("FailStatusID"));
        }
        return disposition;
    }
}
