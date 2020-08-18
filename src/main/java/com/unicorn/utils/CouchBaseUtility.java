package com.unicorn.utils;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.unicorn.base.logger.Logger;
import net.serenitybdd.core.Serenity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public class CouchBaseUtility {

    public static CouchbaseCluster couchbaseCluster = null;
    public static Bucket couchbaseBucket = null;
    static CouchbaseEnvironment env = null;
    static String bucketName = null;

    public static void openBucket() {
        if (couchbaseBucket == null || couchbaseBucket.isClosed()) {
            String fileName = CommonFunctions.getFullPath(AutProperties.getExecutionEnvironmentConfigFileName());
            bucketName = CommonFunctions.readFile(fileName, "CouchBaseBucketName");
            String sCouchbaseUserName = CommonFunctions.readFile(fileName, "CouchbaseUserName");
            String sCouchBasePassword = CommonFunctions.readFile(fileName, "CouchBasePassword");
            String sCouchbaseServerIP = CommonFunctions.readFile(fileName, "CouchbaseServerIP");
            env = DefaultCouchbaseEnvironment.builder().connectTimeout(3000000)
                    .keepAliveTimeout(100000000)
                    .socketConnectTimeout(100000000)
                    .kvTimeout(100000000)
                    .build();
            couchbaseCluster = CouchbaseCluster.create(env, sCouchbaseServerIP);
            couchbaseCluster.authenticate(sCouchbaseUserName, sCouchBasePassword);
            couchbaseBucket = couchbaseCluster.openBucket(bucketName);
            Serenity.getCurrentSession().put("CBActiveSession", true);
        }
    }

    public static void closeBucket() {
        if (couchbaseBucket != null) {
            couchbaseCluster.disconnect();
            couchbaseBucket.close();
            Serenity.getCurrentSession().put("CBActiveSession", false);
            Serenity.getCurrentSession().put("CBEntityActiveSession", false);
            couchbaseCluster = null;
            couchbaseBucket = null;
        }

    }

    public static void openEntityCaseManagementBucket() {
        if (couchbaseBucket == null) {
           //String fileName = CommonFunctions.getFullPath("Config.properties");
            String fileName = CommonFunctions.getFullPath(AutProperties.getExecutionEnvironmentConfigFileName());
            bucketName = CommonFunctions.readFile(fileName, "CouchBaseEntityCaseManagementBucketName");
            String sCouchbaseUserName = CommonFunctions.readFile(fileName, "EntityCouchbaseUserName");
            String sCouchBasePassword = CommonFunctions.readFile(fileName, "EntityCouchBasePassword");
            String sCouchbaseServerIP = CommonFunctions.readFile(fileName, "EntityCouchbaseServerIP");
            env = DefaultCouchbaseEnvironment.builder()
                    .connectTimeout(3000000)
                    .keepAliveTimeout(100000000)
                    .socketConnectTimeout(100000000)
                    .kvTimeout(100000000)
                    .build();
            couchbaseCluster = CouchbaseCluster.create(env, sCouchbaseServerIP);
            couchbaseCluster.authenticate(sCouchbaseUserName, sCouchBasePassword);
            couchbaseBucket = couchbaseCluster.openBucket(bucketName);
            Serenity.getCurrentSession().put("CBEntityActiveSession", true);
        }
    }

    public static JsonDocument insertDocument(JsonDocument doc) {
        return couchbaseBucket.insert(doc);
    }

    /**
     * This will execute couchbase query for CaseManagement Bucket
     *
     * @param query
     * @return
     * @throws IOException
     */
    public static N1qlQueryResult executeCBQuery(String query) throws IOException {
        return executeCBQuery(query, false);
    }

    /**
     * This will execute couchbase query
     *
     * @param query                        query
     * @param isEntityCaseManagementBucket set true to execute EntityCaseManagement bucket query
     * @return
     * @throws IOException
     */
    public static N1qlQueryResult executeCBQuery(String query, boolean isEntityCaseManagementBucket) {
        Logger.info("Executing CB: " + query);
        N1qlQueryResult caseInformationResult = null;
        try {
            if (isEntityCaseManagementBucket)
                openEntityCaseManagementBucket();
            else
                openBucket();
            caseInformationResult = couchbaseBucket.query(N1qlQuery.simple(query));
        } catch (Exception e) {
            e.printStackTrace();
            Logger.info("Exception while executing CouchBase query");
        }
        return caseInformationResult;
    }

    public static Map<String, List<String>> executeCouchBaseQuery(String query) throws IOException {
        N1qlQueryResult caseInformationResult = executeCBQuery(query);
        int rowcount = caseInformationResult.allRows().size();
        List<N1qlQueryRow> result = caseInformationResult.allRows();
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        for (int i = 0; i < rowcount; i++) {
            JsonObject CouchbaseQueryDocument = result.get(i).value();
            Map<String, Object> flattenMap_CouchbaseQuery = JsonFlattener
                    .flattenAsMap(CouchbaseQueryDocument.toString());
            ArrayList<String> list;
            for (Entry<String, Object> entry : flattenMap_CouchbaseQuery.entrySet()) {
                Logger.info("Key = " + entry.getKey() + ", Value = " + entry.getValue().toString());
                if (map.containsKey(entry.getKey())) {
                    list = (ArrayList<String>) map.get(entry.getKey());
                    list.add((String) entry.getValue().toString().replace("[", "").replace("]", ""));
                } else {
                    list = new ArrayList<String>();
                    list.add((String) entry.getValue().toString().replace("[", "").replace("]", ""));
                    map.put(entry.getKey(), list);
                }
            }
        }
      /*  if (Serenity.getCurrentSession().get("CBActiveSession").equals(true)) {
            couchbaseCluster.disconnect();
            couchbaseBucket.close();
            couchbaseBucket = null;
            Serenity.getCurrentSession().put("CBActiveSession", false);
        }*/
        return map;
    }

    public static void getRowCountCouchBaseQuery(String query) throws IOException {
        N1qlQueryResult caseInformationResult = executeCBQuery(query);
        int rowcount = caseInformationResult.allRows().size();
        List<N1qlQueryRow> result = caseInformationResult.allRows();
        for (int i = 0; i < rowcount; i++) {
            JsonObject CouchbaseQueryDocument = result.get(i).value();
            Map<String, Object> flattenMap_CouchbaseQuery = JsonFlattener
                    .flattenAsMap(CouchbaseQueryDocument.toString());
            ArrayList<String> list;
            for (Entry<String, Object> entry : flattenMap_CouchbaseQuery.entrySet()) {
                Logger.info("Key = " + entry.getKey() + ", Value = " + entry.getValue().toString());
                Serenity.getCurrentSession().put("CBRowCount", entry.getValue().toString());
            }
        }
        if (Serenity.getCurrentSession().get("CBActiveSession").equals(true)) {
        /*    couchbaseCluster.disconnect();
            couchbaseBucket.close();
            couchbaseBucket = null;
            Serenity.getCurrentSession().put("CBActiveSession", false);*/
        }
    }


    public static List<N1qlQueryRow> executeCouchBaseQuery1(String query) throws IOException {
        N1qlQueryResult caseInformationResult = executeCBQuery(query);
        List<N1qlQueryRow> result = caseInformationResult.allRows();
       /* couchbaseBucket.close();
        couchbaseBucket = null;*/
        return result;
    }


    public static void executeUpdateCouchBaseQuery() throws IOException {
        executeCBQuery("update CaseManagement set caseWeight=\"\" where  caseWeight=1 and documentType='Case'");
       // couchbaseBucket.close();

    }

    public void queryAndSaveDocumentIDsInSession(String Query, String searchType) {
        //Logger.info("Select * from CaseManagement where caseId = '" + caseID + "'");
        N1qlQueryResult caseInformationResult = couchbaseBucket
                .query(N1qlQuery.simple(Query), 5, TimeUnit.MINUTES);
        //.query(N1qlQuery.simple(Query),5,"minutes");

        List<N1qlQueryRow> row = caseInformationResult.allRows();
        JsonObject caseDetailsFromQuery = row.get(0).value();
        Map<String, Object> flattenMap_caseDetailsFromQuery = JsonFlattener
                .flattenAsMap(caseDetailsFromQuery.toString());
        Logger.info("flattenMap_caseDetailsFromQuery       " + flattenMap_caseDetailsFromQuery);
        if (searchType.equals("case")) {
            String caseJsonDocumentID = row.get(0).value().getObject("CaseManagement").get("id")
                    .toString();
            String paymentJsonDocumentID = row.get(0).value().getObject("CaseManagement")
                    .getObject("paymentTransaction")
                    .get("paymentDocumentID").toString();// paymentTransaction.paymentDocumentID
            Serenity.getCurrentSession().put("caseJsonDocumentID", caseJsonDocumentID);
            Serenity.getCurrentSession().put("paymentJsonDocumentID", paymentJsonDocumentID);
        }
        Serenity.getCurrentSession()
                .put("flattenMap_caseDetailsFromQuery", flattenMap_caseDetailsFromQuery);
        Serenity.getCurrentSession().put("caseDetailsFromQuery", caseDetailsFromQuery);
    }
}

