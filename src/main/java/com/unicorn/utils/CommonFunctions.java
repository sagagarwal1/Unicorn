package com.unicorn.utils;

import com.github.wnameless.json.flattener.JsonFlattener;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.unicorn.base.logger.Logger;
import io.qameta.allure.Allure;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.response.Response;
import net.minidev.json.JSONArray;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.rest.SerenityRest;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.*;
import java.util.Map.Entry;


public class CommonFunctions {

    public static String getResponseviaPost(ApiEnvironment apiEnvironment, String jsonString, String fieldName) {
        String fileName = getFullPath(AutProperties.getExecutionEnvironmentConfigFileName());
        //SerenityRest.config().encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false));
        String sURL = apiEnvironment + readFile(fileName, fieldName);
        //SerenityRest.given().contentType("application/json").and().body(jsonString).when()
        //.post(sURL);

        EncoderConfig ec = new EncoderConfig();
        ec.appendDefaultContentCharsetToContentTypeIfUndefined(false);
        Response response = SerenityRest.given().filter(new AllureRestAssured())
                .contentType("application/json").config(RestAssured.config()
                        .encoderConfig(ec.appendDefaultContentCharsetToContentTypeIfUndefined(false)))
                .and()
                .body(jsonString).when().post(sURL).then().extract().response();
        Serenity.getCurrentSession().put("apiResponse", response);
        String body = response.body().asString();
        //Serenity.recordReportData().withTitle("CCTRealTimeComplianceURL").andContents(sURL);
        return body;
    }
    public static String getResponseviaPut(ApiEnvironment apiEnvironment, String jsonString, String fieldName) {
        String fileName = getFullPath(AutProperties.getExecutionEnvironmentConfigFileName());
        //SerenityRest.config().encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false));
        String sURL = apiEnvironment + readFile(fileName, fieldName);
        //SerenityRest.given().contentType("application/json").and().body(jsonString).when()
        //.post(sURL);

        EncoderConfig ec = new EncoderConfig();
        ec.appendDefaultContentCharsetToContentTypeIfUndefined(false);
        Response response = SerenityRest.given().filter(new AllureRestAssured())
                .contentType("application/json").config(RestAssured.config()
                        .encoderConfig(ec.appendDefaultContentCharsetToContentTypeIfUndefined(false)))
                .and()
                .body(jsonString).when().put(sURL)
                .then().extract().response();
        Serenity.getCurrentSession().put("apiResponse", response);
        String body = response.body().asString();
        if (fieldName.equalsIgnoreCase("CCTRealTimeComplianceURL")) {
            Serenity.recordReportData().withTitle("CCTRealTimeComplianceURL").andContents(sURL);
        }
        return body;
    }

    public static String getDataFromConfigFile(String fieldName) throws IOException {
        String fileName = getFullPath(AutProperties.getExecutionEnvironmentConfigFileName());
        //SerenityRest.config().encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false));
        return readFile(fileName, fieldName);
    }

    public static String getCaseDispositionStatus(String CaseDisposition) {
        String caseStatus = null;
        String caseStatusId = null;
        String caseDisposition = null;
        String finalDisposition = null;
        if (CaseDisposition.trim().equals("Escalate")) {
            caseStatus = "Open";
            caseStatusId = "48";
            caseDisposition = "Escalate";
            finalDisposition = "#";
            ;
        } else if (CaseDisposition.trim().equals("Pended")) {
            caseStatus = "Pended";
            caseStatusId = "4";
            caseDisposition = "#";
            finalDisposition = "#";
        } else if (CaseDisposition.trim().equals("RFI")) {
            caseStatus = "RFI";
            caseStatusId = "81";
            caseDisposition = "Request for Information";
            finalDisposition = "#";
        } else if (CaseDisposition.trim().equals("Release to Queue")) {
            caseStatus = "Skipped";
            caseStatusId = "5";
            caseDisposition = "#";
            finalDisposition = "#";
        } else if (CaseDisposition.trim().equals("Pass")) {
            caseStatus = "Completed";
            caseStatusId = "9";
            caseDisposition = CaseDisposition;
            finalDisposition = CaseDisposition;
        } else if (CaseDisposition.trim().equals("Fail")) {
            caseStatus = "Completed";
            caseStatusId = "9";
            caseDisposition = CaseDisposition;
            finalDisposition = CaseDisposition;
        } else if (CaseDisposition.trim().equals("Match")) {
            caseStatus = "Match";
            caseStatusId = "9";
            caseDisposition = CaseDisposition;
            finalDisposition = CaseDisposition;
        } else if (CaseDisposition.trim().equals("No Match")) {
            caseStatus = "No Match";
            caseStatusId = "9";
            caseDisposition = CaseDisposition;
            finalDisposition = CaseDisposition;
        }
        return caseStatus + ";" + caseStatusId + ";" + caseDisposition + ";" + finalDisposition;

    }

    public static void getResponseviaGet(ApiEnvironment apiEnvironment, String fieldName) {
        String sURL;
        String fileName = getFullPath(AutProperties.getExecutionEnvironmentConfigFileName());
        sURL = apiEnvironment + readFile(fileName, fieldName);
        SerenityRest.given().contentType("application/json").when()
                .get(sURL);
    }

    public static String getTagValue(String jsonPath) {
        // TODO Auto-generated method stub
        Response res = (Response) Serenity.getCurrentSession().get("apiResponse");
        return res.body().jsonPath().get(jsonPath).toString();
    }


    public static Integer getStatusCode() {
        // TODO Auto-generated method stub
        Response res = (Response) Serenity.getCurrentSession().get("apiResponse");
        return res.then().extract().statusCode();
        //return SerenityRest.then().extract().statusCode();
    }

    public static String readFile(String fileName, String paramName) {
        File file = new File(fileName);
        FileInputStream fileInput = null;
        try {
            fileInput = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Logger.assertFail("Exception while reading file: " + fileName);
        }
        Properties prop = new Properties();

        //load properties file
        try {
            prop.load(fileInput);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return prop.getProperty(paramName);

    }

    public static void writeToParamsFile(String FILENAME, String contentToWrite) {

        //String FILENAME = "params.txt";

        BufferedWriter bw = null;
        BufferedReader br = null;
        FileWriter fw = null;
        FileReader fr = null;

        try {

            String content = contentToWrite;
            bw = new BufferedWriter(new FileWriter(FILENAME, true));
            bw.write(System.getProperty("line.separator"));
            bw.write(content);
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

    public static void readAndReplaceDataInFile(String fileName, String textTobeReplaced,
                                                String newTextValue) throws IOException {
        String fileName1 = getFullPath(fileName);
        FileInputStream fis = new FileInputStream(fileName1);
        String content = IOUtils.toString(fis, Charset.defaultCharset());
        content = content.replaceAll(textTobeReplaced, newTextValue);
        FileOutputStream fos = new FileOutputStream(fileName);
        IOUtils.write(content, new FileOutputStream(fileName), Charset.defaultCharset());
        fis.close();
        fos.close();
    }

    public static void removeDataFromFile(String fileName) throws IOException {
        String fileName1 = getFullPath(fileName);
        FileOutputStream writer = new FileOutputStream(fileName1);
        writer.write(("").getBytes());
        writer.close();
    }

    public static void RemoveDataFromSQLDB(String entityName, String Filterkey)
            throws ClassNotFoundException, IOException {
        if (entityName == "Customer" && Filterkey != "") {
            executequery("delete from EAS.EntityCustomer where CustomerSKey='" + Filterkey + "'");
            executequery("delete from EAS.EntityCustomerDetail where CustomerSKey='" + Filterkey + "'");
        } else if (entityName == "Beneficary" && Filterkey != "") {
            executequery("delete from EAS.EntityBeneficiary where BeneficiarySKey='" + Filterkey + "'");
            executequery(
                    "delete from EAS.EntityBeneficiaryDetail where BeneficiarySKey='" + Filterkey + "'");
        } else if (entityName == "AuthorizedUser" && Filterkey != "") {
            executequery(
                    "delete from EAS.EntityAuthorizedUser where AuthorizedUserSKey='" + Filterkey + "'");
        } else if (entityName == "BankAccountDetail" && Filterkey != "") {
            executequery(
                    "delete from EAS.EntityBankAccountDetail where BankAccountSKey='" + Filterkey + "'");
        } else if (entityName == "BeneficiaryRemitter" && Filterkey != "") {
            executequery(
                    "delete from EAS.EntityBeneficiaryRemitter where DataSourceNKey='" + Filterkey + "'");
        } else if (entityName == "Branch" && Filterkey != "") {
            executequery("delete from EAS.EntityBranch where BranchSKey='" + Filterkey + "'");
        } else if (entityName == "ThirdParty" && Filterkey != "") {
            executequery("delete from EAS.EntityThirdParty where ThirdPartySKey='" + Filterkey + "'");
        }

    }

    public static boolean executequery(String query) throws ClassNotFoundException, IOException {
        Connection conn = null;
        String fileName = getFullPath(AutProperties.getExecutionEnvironmentConfigFileName());
        String SQLServerName = null;
        String sSQLDBName = null;
        String sPortNumber = null;
        String sSQLUserName = readFile(fileName, "SQLUserName");
        String sSQLPassword = readFile(fileName, "SQLPassword");
        if (query.contains("[CM_FOR_QA].[TremaHardStop].[PaymentMessage]")) {
            SQLServerName = readFile(fileName, "TremaServerName");
            sSQLDBName = readFile(fileName, "TremaDBName");
            sPortNumber = readFile(fileName, "TremaPortNumber");
        } else if (query.contains("caseweight")) {
            SQLServerName = readFile(fileName, "ACLAIMSQLServerName");
            sSQLUserName = readFile(fileName, "ACLAIMSQLUserName");
            sSQLPassword = readFile(fileName, "ACLAIMSQLPassword");
            sSQLDBName = readFile(fileName, "ACLAIMCCTSQLDBName");
        } else {
            SQLServerName = readFile(fileName, "IRISServerName");
            sSQLDBName = readFile(fileName, "IRISDBName");
            sPortNumber = readFile(fileName, "IRISPortNumber");
        }
        boolean isFlag = false;

        try {
            String dbURL;
            if (sPortNumber == null) {
                dbURL = "jdbc:sqlserver://" + SQLServerName + ";databasename=" + sSQLDBName + ";";
            } else {
                dbURL =
                        "jdbc:sqlserver://" + SQLServerName + ":" + sPortNumber + ";databasename=" + sSQLDBName
                                + ";";
            }
            System.out.println(dbURL);
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(dbURL, sSQLUserName, sSQLPassword);
            if (conn != null) {
                Statement sqlStatement = conn.createStatement();
                sqlStatement.executeUpdate(query);

                //close the database connection
                conn.close();
                isFlag = true;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            isFlag = false;
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                isFlag = false;
            }
        }
        return isFlag;


    }

    public static Map<String, String> executeSQLquery(String query) {
        Logger.info("Executing SQL Query: " + query);
        Connection conn = null;
        String fileName = getFullPath(AutProperties.getExecutionEnvironmentConfigFileName());
        String SQLServerName = null;
        String sSQLDBName = null;
        String sSQLUserName = readFile(fileName, "SQLUserName");
        String sSQLPassword = readFile(fileName, "SQLPassword");
        String sPortNumber = null;
        if (query.contains("[CM_FOR_QA].[TremaHardStop].[PaymentMessage]")) {
            SQLServerName = readFile(fileName, "TremaServerName");
            sSQLDBName = readFile(fileName, "TremaDBName");
            sPortNumber = readFile(fileName, "TremaPortNumber");
        } else if (query.toUpperCase().contains("CPCE")) {
            SQLServerName = readFile(fileName, "ACLAIMSQLServerName");
            sSQLUserName = readFile(fileName, "ACLAIMSQLUserName");
            sSQLPassword = readFile(fileName, "ACLAIMSQLPassword");
            sSQLDBName = readFile(fileName, "ACLAIMCCTSQLDBName");
        } else if (query.toUpperCase().contains("SYSTEM_ID")) {
            SQLServerName = readFile(fileName, "ACLAIMSQLServerName");
            sSQLUserName = readFile(fileName, "ACLAIMSQLUserName");
            sSQLPassword = readFile(fileName, "ACLAIMSQLPassword");
            sSQLDBName = readFile(fileName, "ACLAIMFIRCOSOFTDBName");
        } else if (query.toUpperCase().contains("UMF")) {
            try {
                SQLServerName = readFile(fileName, "UMFSQLServerName");
                sSQLUserName = readFile(fileName, "UMFSQLUserName");
                sSQLPassword = readFile(fileName, "UMFSQLPassword");
                sSQLDBName = readFile(fileName, "UMFDBName");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            SQLServerName = readFile(fileName, "IRISServerName");
            sSQLDBName = readFile(fileName, "IRISDBName");
            sPortNumber = readFile(fileName, "IRISPortNumber");
        }

        HashMap<String, String> SQLMap = null;
        List<String> columnNames = new LinkedList<>();
        Map<String, String> columnNameToValuesMap = new HashMap<String, String>();
        int k = 0;
        try {
            String dbURL = null;
            if (sPortNumber != null) {
                dbURL =
                        "jdbc:sqlserver://" + SQLServerName + ":" + sPortNumber + ";databasename=" + sSQLDBName
                                + ";";
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            } else {

                dbURL = "jdbc:sqlserver://" + SQLServerName + ";databasename=" + sSQLDBName + ";";
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            }

            conn = DriverManager.getConnection(dbURL, sSQLUserName, sSQLPassword);

            if (conn != null) {

                Statement sqlStatement = conn.createStatement();
                ResultSet rs = sqlStatement.executeQuery(query);
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = rsmd.getColumnName(i);
                    columnNames.add(columnName);

                    //Load the Map initially with keys(columnnames) and empty list
                    columnNameToValuesMap.put(columnName, "");
                }

                try {
                    while (rs.next()) { //Iterate the resultset for each row
                        k++;
                        for (String columnName : columnNames) {
                            //add the updated list of column data to the map now
                            columnNameToValuesMap.put(columnName, rs.getString(columnName));
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                //Serenity.getCurrentSession().put("SQLCaseCount", k);
                //close the database connection
                conn.close();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.info("Exception while executing SQL query", e);
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return columnNameToValuesMap;


    }

    public static Map<String, String> executeDB2query(String query)
            throws ClassNotFoundException, IOException {
        HashMap<String, String> SQLMap = null;
        List<String> columnNames = new LinkedList<>();
        Map<String, String> columnNameToValuesMap = new HashMap<String, String>();
        Connection conn = null;
        String fileName = getFullPath(AutProperties.getExecutionEnvironmentConfigFileName());
        String db2ServerURL = readFile(fileName, "DB2ServerURL");
        String db2DBName = readFile(fileName, "DB2DataBaseName");
        String db2UserName = readFile(fileName, "DB2UserName");
        String db2Password = readFile(fileName, "DB2Password");

        try {
            Class.forName("com.ibm.db2.jcc.DB2Driver");
            conn = DriverManager.getConnection(db2ServerURL, db2UserName, db2Password);
            conn.setAutoCommit(false);
            if (conn != null) {
                Statement db2Statement = conn.createStatement();
                ResultSet rs = db2Statement.executeQuery(query);
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = rsmd.getColumnName(i);
                    columnNames.add(columnName);
                    columnNameToValuesMap.put(columnName, "");
                }
                while (rs.next()) {
                    for (String columnName : columnNames) {
                        columnNameToValuesMap.put(columnName,
                                rs.getString(columnName));
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return columnNameToValuesMap;
    }

    public static void executeUpdateDB2Query() throws IOException, ClassNotFoundException {
        Connection conn = null;
        String fileName = getFullPath(AutProperties.getExecutionEnvironmentConfigFileName());
        String db2ServerURL = readFile(fileName, "DB2ServerURL");
        String db2DBName = readFile(fileName, "DB2DataBaseName");
        String db2UserName = readFile(fileName, "DB2UserName");
        String db2Password = readFile(fileName, "DB2Password");
        try {
            Class.forName("com.ibm.db2.jcc.DB2Driver");
            String db2URL = "jdbc:db2://" + db2ServerURL + "/" + db2DBName;
            conn = DriverManager.getConnection(db2URL, db2UserName, db2Password);
            if (conn != null) {
                PreparedStatement pstmt;
                int numUpd;
                pstmt = conn.prepareStatement(
                        "update cpce.case set case_weight=null where case_weight=1");
                // Create a PreparedStatement object   1
                numUpd = pstmt.executeUpdate();   // Perform the update                  3
                pstmt.close();

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static int getDataSourceID() {
        int DataSourceID = 0;
        String paymentSourcePlatform = (String) Serenity.getCurrentSession()
                .get("paymentSourcePlatform");
        if (paymentSourcePlatform.equals("SPOT")) {
            DataSourceID = 1;
        } else if (paymentSourcePlatform.equals("CCT")) {
            DataSourceID = 10;
        } else if (paymentSourcePlatform.equals("MPS")) {
            DataSourceID = 13;
        } else if (paymentSourcePlatform.equals("H2H")) {
            DataSourceID = 28;
        }
        return DataSourceID;

    }

    public static void getData(String columnName, String tablename, String EnvironmentValue)
            throws ClassNotFoundException, IOException {
        String sData = null;
        String dbColumnName = null;
        if (columnName.equals("BeneficiaryRemitterAccountSKey") || columnName
                .equals("ThirdPartyAccountSKey") || columnName.equals("ReceipentAccountSKey") || columnName
                .equals("SenderBankAccountSKey")) {
            dbColumnName = "BankAccountSKey";

        } else {
            dbColumnName = columnName;
        }
        String query =
                "select " + dbColumnName + " from " + tablename + " where DataSourceNkey='" + Serenity
                        .getCurrentSession().get(EnvironmentValue) + "'";
        Map<String, String> getData = executeSQLquery(query);
        for (Entry<String, String> entry : getData.entrySet()) {
            if (entry.getKey().equals(dbColumnName)) {
                sData = entry.getValue().toString();
            }
        }

        Serenity.getCurrentSession().put(columnName, sData);

    }

    public static String getFullPath(String filename) {

        String userHome = System.getProperty("user.dir");
//    System.out.println("userHome  : " + userHome);
        String fullPath = userHome + System.getProperty("file.separator") + "config";
        return fullPath + System.getProperty("file.separator") + filename;
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
        String JsonfileName = getFullPath(filename);
        newJson = JsonPath.using(config).parse(new File(JsonfileName)).
                set(fieldname, fieldvalue);
        Object document = Configuration.defaultConfiguration().jsonProvider()
                .parse(newJson.jsonString());
        return newJson.jsonString();
    }

    public static String readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));

        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }

    public static LinkedHashMap<String, String> readDataFromFileAndConvertIntoMap(String fileName) {

        BufferedReader br = null;
        FileReader fr = null;
        String datafileName = getFullPath(fileName);
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
                if (null != sCurrentLine && !sCurrentLine.equals("") && sCurrentLine.contains("=")) {
                    StringTokenizer st = new StringTokenizer(sCurrentLine, "=");
                    tokens = st.countTokens();
                    if (tokens == 2) {
                        paramName = st.nextToken();
                        paramValue = st.nextToken();
                        DataMap.put(paramName.trim(), paramValue.trim());
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

    public static String getFullPath(String folderName, String filename) {

        String userHome = System.getProperty("user.dir");
        //	logger.info("userHome  : " + userHome);
        String fullPath = userHome + System.getProperty("file.separator") + "config" + System
                .getProperty("file.separator") + folderName;

        return fullPath + System.getProperty("file.separator") + filename;
    }

    public static String getTheMatchResult(String searchType) throws IOException {
        // TODO Auto-generated method stub
        String matchResult = null;
        CouchBaseUtility bucketExtractor = new CouchBaseUtility();
        CouchBaseUtility.openBucket();
        String query = null;
        String array1[] = Serenity.getCurrentSession().get("SearchCategory").toString().split(":");
        if (searchType.equals("case")) {
            if (Serenity.getCurrentSession().get("SearchCategory").toString().equals("CaseId")) {
                query = "Select * from CaseManagement where documentType='Case' and caseId = '" + Serenity.getCurrentSession()
                        .get("CaseId").toString() + "'";
            } else if (Serenity.getCurrentSession().get("SearchCategory").toString()
                    .equals("PaymentId")) {
                query = "Select * from CaseManagement where paymentTransaction.txnPaymentId = '" + Serenity
                        .getCurrentSession().get("OutgoingPaymentID").toString() + "'";
            } else if (Serenity.getCurrentSession().get("SearchCategory").toString().equals("OrderId")) {
                query = "Select * from CaseManagement where paymentTransaction.txnOrderId = '" + Serenity
                        .getCurrentSession().get("OrderId").toString() + "'and documentType='Case'";
            } else if (Serenity.getCurrentSession().get("SearchCategory").toString()
                    .equals("CustomerId")) {
                query = "Select * from CaseManagement where customer.customerId = '" + Serenity
                        .getCurrentSession().get("customerId").toString() + "' and documentType='Case'";
            } else if (Serenity.getCurrentSession().get("SearchCategory").toString()
                    .equals("ConfirmationNumber")) {
                query = "Select * from CaseManagement where paymentTransaction.confirmationNumber = '"
                        + Serenity.getCurrentSession().get("ConfirmationNumber").toString()
                        + "' and documentType='Case'";
            } else if (Serenity.getCurrentSession().get("SearchCategory").toString()
                    .contains("Region & Country")) {
                query = "Select * from CaseManagement where regionInfo.regionCode='" + Serenity
                        .getCurrentSession().get("RegionCode").toString() + "' and regionInfo.countryCode='"
                        + Serenity.getCurrentSession().get("CountryCode").toString()
                        + "' and documentType='Case'";
            } else if (Serenity.getCurrentSession().get("SearchCategory").toString()
                    .contains("Assigned - Saved/Pended")) {
                query =
                        "Select * from CaseManagement where caseStatusId = '4' and classification='" + array1[2]
                                + "' and processing_Stage='" + array1[1] + "' and documentType='Case'";
            } else if (Serenity.getCurrentSession().get("SearchCategory").toString()
                    .contains("Assigned - Working")) {
                query =
                        "Select * from CaseManagement where caseStatusId = '1' and classification='" + array1[2]
                                + "' and processing_Stage='" + array1[1] + "' and documentType='Case'";
            } else if (Serenity.getCurrentSession().get("SearchCategory").toString()
                    .contains("Assigned - Not Picked Up")) {
                query =
                        "Select * from CaseManagement where caseStatusId = '2' and classification='" + array1[2]
                                + "' and processing_Stage='" + array1[1] + "' and documentType='Case'";
            } else if (Serenity.getCurrentSession().get("SearchCategory").toString()
                    .contains("Completed - Pass/Fail")) {
                query =
                        "Select * from CaseManagement where caseStatusId = '9' and classification='" + array1[2]
                                + "' and processing_Stage='" + array1[1] + "' and documentType='Case'";
            } else if (Serenity.getCurrentSession().get("SearchCategory").toString()
                    .contains("Escalated - In Queue")) {
                query = "Select * from CaseManagement where caseStatusId = '48' and classification='"
                        + array1[2] + "' and processing_Stage='" + array1[1] + "' and documentType='Case'";
            } else if (Serenity.getCurrentSession().get("SearchCategory").toString().contains("New")) {
                query = "Select * from CaseManagement where caseStatusId = '41' and classification='"
                        + array1[2] + "' and processing_Stage='" + array1[1] + "' and documentType='Case'";
            } else if (Serenity.getCurrentSession().get("SearchCategory").toString()
                    .contains("Release to Queue - Skipped")) {
                query =
                        "Select * from CaseManagement where caseStatusId = '5' and classification='" + array1[2]
                                + "' and processing_Stage='" + array1[1] + "' and documentType='Case'";
            }
        } else if ((searchType.equals("payment")) && (Serenity.getCurrentSession()
                .get("goodBadPaymentFlag").equals(false))) {
            if (Serenity.getCurrentSession().get("SearchCategory").toString().equals("PaymentId")) {
                query =
                        "SELECT cp.payment.platform, cp.payment.orderID as orderId, cp.payment.sourcePaymentID as paymentId, cc.caseId, "
                                + "cp.payment.customerID as customerId, party.name.name as customer, cp.payment.creationDate as created, cp.payment.expectedValueDate as valueDate, "
                                + "cp.payment.currency, cp.payment.amount, party.address.countryCode as country , cc.regionInfo.countryName as BranchCountryName,cc.regionInfo.regionName as RegionName  "
                                + " From CaseManagement cc Join CaseManagement cp ON KEYS(cc.paymentTransaction.paymentDocumentID) "
                                + "UNNEST cp.payment.parties AS party WHERE party.type = 'Customer' AND cc.paymentTransaction.txnPaymentId ='"
                                + Serenity.getCurrentSession().get("OutgoingPaymentID").toString() + "'";
            } else if (Serenity.getCurrentSession().get("SearchCategory").toString().equals("OrderId")) {
                query =
                        "SELECT cp.payment.platform, cp.payment.orderID as orderId, cp.payment.sourcePaymentID as paymentId, cc.caseId, "
                                + "cp.payment.customerID as customerId, party.name.name as customer, cp.payment.creationDate as created, cp.payment.expectedValueDate as valueDate, "
                                + "cp.payment.currency, cp.payment.amount, party.address.countryCode as country , cc.regionInfo.countryName as BranchCountryName,cc.regionInfo.regionName as RegionName  "
                                + " From CaseManagement cc Join CaseManagement cp ON KEYS(cc.paymentTransaction.paymentDocumentID) "
                                + "UNNEST cp.payment.parties AS party WHERE party.type = 'Customer' AND cc.paymentTransaction.txnOrderId  ='"
                                + Serenity.getCurrentSession().get("OrderId").toString() + "'";
            } else if (Serenity.getCurrentSession().get("SearchCategory").toString()
                    .equals("CustomerId")) {
                query =
                        "SELECT cp.payment.platform, cp.payment.orderID as orderId, cp.payment.sourcePaymentID as paymentId, cc.caseId, "
                                + "cp.payment.customerID as customerId, party.name.name as customer, cp.payment.creationDate as created, cp.payment.expectedValueDate as valueDate, "
                                + "cp.payment.currency, cp.payment.amount, party.address.countryCode as country , cc.regionInfo.countryName as BranchCountryName,cc.regionInfo.regionName  as RegionName  "
                                + " From CaseManagement cc Join CaseManagement cp ON KEYS(cc.paymentTransaction.paymentDocumentID) "
                                + "UNNEST cp.payment.parties AS party WHERE party.type = 'Customer' AND cp.payment.customerID   ='"
                                + Serenity.getCurrentSession().get("customerId").toString() + "'";
            } else if (Serenity.getCurrentSession().get("SearchCategory").toString()
                    .equals("ConfirmationNumber")) {
                query =
                        "SELECT cp.payment.platform, cp.payment.orderID as orderId, cp.payment.sourcePaymentID as paymentId, cc.caseId, "
                                + "cp.payment.customerID as customerId, party.name.name as customer, cp.payment.creationDate as created, cp.payment.expectedValueDate as valueDate, "
                                + "cp.payment.currency, cp.payment.amount, party.address.countryCode as country , cc.regionInfo.countryName as BranchCountryName,cc.regionInfo.regionName  as RegionName  "
                                + " From CaseManagement cc Join CaseManagement cp ON KEYS(cc.paymentTransaction.paymentDocumentID) "
                                + "UNNEST cp.payment.parties AS party WHERE party.type = 'Customer' AND cp.payment.customerID   ='"
                                + Serenity.getCurrentSession().get("customerId").toString() + "'";
            } else if (Serenity.getCurrentSession().get("SearchCategory").toString()
                    .equals("Region & Country")) {
                query =
                        "SELECT cp.payment.platform, cp.payment.orderID as orderId, cp.payment.sourcePaymentID as paymentId, cc.caseId, "
                                + "cp.payment.customerID as customerId, party.name.name as customer, cp.payment.creationDate as created, cp.payment.expectedValueDate as valueDate, "
                                + "cp.payment.currency, cp.payment.amount, party.address.countryCode as country , cc.regionInfo.countryName as BranchCountryName,cc.regionInfo.regionName  as RegionName  "
                                + " From CaseManagement cc Join CaseManagement cp ON KEYS(cc.paymentTransaction.paymentDocumentID) "
                                + "UNNEST cp.payment.parties AS party WHERE party.type = 'Customer' AND cp.payment.customerID   ='"
                                + Serenity.getCurrentSession().get("customerId").toString() + "'";
            }

        } else if (searchType.equals("payment") && (Serenity.getCurrentSession()
                .get("goodBadPaymentFlag").equals(true))) {
            if (Serenity.getCurrentSession().get("SearchCategory").toString().equals("PaymentId")) {
                query =
                        "select CaseManagement.payment.platform,CaseManagement.payment.orderID,CaseManagement.payment.sourcePaymentID,CaseManagement.payment.customerID,CaseManagement.payment.parties[0].name.name,CaseManagement.payment.parties[0].address.countryCode,CaseManagement.payment.creationDate,"
                                + "CaseManagement.payment.expectedValueDate,CaseManagement.payment.currency,CaseManagement.payment.amount,CaseManagement.payment.regionInfo.countryName,CaseManagement.payment.regionInfo.regionName "
                                + "from CaseManagement where  documentType='Payment' and CaseManagement.payment.sourcePaymentID='"
                                + Serenity.getCurrentSession().get("OutgoingPaymentID").toString() + "'";
            } else if (Serenity.getCurrentSession().get("SearchCategory").toString().equals("OrderId")) {
                query =
                        "select CaseManagement.payment.platform,CaseManagement.payment.orderID,CaseManagement.payment.sourcePaymentID,CaseManagement.payment.customerID,CaseManagement.payment.parties[0].name.name,CaseManagement.payment.parties[0].address.countryCode,CaseManagement.payment.creationDate,"
                                + "CaseManagement.payment.expectedValueDate,CaseManagement.payment.currency,CaseManagement.payment.amount,CaseManagement.payment.regionInfo.countryName,CaseManagement.payment.regionInfo.regionName "
                                + "from CaseManagement where  documentType='Payment' and CaseManagement.payment.orderID='"
                                + Serenity.getCurrentSession().get("OrderId").toString() + "'";
            } else if (Serenity.getCurrentSession().get("SearchCategory").toString()
                    .equals("CustomerId")) {
                query =
                        "select CaseManagement.payment.platform,CaseManagement.payment.orderID,CaseManagement.payment.sourcePaymentID,CaseManagement.payment.customerID,CaseManagement.payment.parties[0].name.name,CaseManagement.payment.parties[0].address.countryCode,CaseManagement.payment.creationDate,"
                                + "CaseManagement.payment.expectedValueDate,CaseManagement.payment.currency,CaseManagement.payment.amount,CaseManagement.payment.regionInfo.countryName,CaseManagement.payment.regionInfo.regionName "
                                + "from CaseManagement where  documentType='Payment' and CaseManagement.payment.customerID='"
                                + Serenity.getCurrentSession().get("customerId").toString() + "'";
            }
        }
        // bucketExtractor.fetchAndSaveDocuments();

        bucketExtractor.queryAndSaveDocumentIDsInSession(query, searchType);
        Response res = (Response) Serenity.getCurrentSession().get("apiResponse");
        //return res.then().extract().statusCode();
        String searchCaseAPIResponse = res.then().extract().response().getBody().asString();
        //JSONObject obj=(JSONObject)JSONValue.parse(searchCaseAPIResponse);
        //JSONArray arr=(JSONArray)obj.get("cases");
        JSONArray array;
        if (searchType.equals("payment")) {
            array = JsonPath.read(searchCaseAPIResponse, "payments[*]");
        } else {
            array = JsonPath.read(searchCaseAPIResponse, "cases[*]");
        }

        System.out.println("response body : " + searchCaseAPIResponse);
        try {
            if (compare(Serenity.getCurrentSession().get("caseDetailsFromQuery").toString(),
                    searchCaseAPIResponse, searchType, array.size())) {
                matchResult = "Pass";

            } else {
                matchResult = "Fail";

            }
        } catch (Exception e) {

        }

        String testData = Serenity.getCurrentSession().get("stringBuffResult").toString();
        Allure.addAttachment("Json Comparison Details", testData);
        //Serenity.recordReportData().withTitle("Json Comparison Details").andContents(testData);
        return matchResult;
    }

    public static File loadFile(String folderName, String filename) {

        String strFile = getFullPath(folderName, filename);

        File f = new File(strFile);

        if (f.exists()) {
            return f;
        }

        return null;

    }

    public static Boolean compare(String CBResponse, String APIResponse, String searchType,
                                  int ChildNodeLength) {

        String jsonLeft;
        String jsonRight;
        jsonLeft = CBResponse;
        jsonRight = APIResponse;
        String fileName = null;
        if (Serenity.getCurrentSession().get("goodBadPaymentFlag").equals(true) && searchType
                .equals("payment")) {
            fileName = "mapping_searchPayment_good.txt";
        } else if (Serenity.getCurrentSession().get("goodBadPaymentFlag").equals(false) && searchType
                .equals("payment")) {
            fileName = "mapping_searchPayment.txt";
        } else {
            fileName = "mapping_searchCase.txt";
        }
        Map<String, Object> leftFlatMap = JsonFlattener.flattenAsMap(jsonLeft);
        Map<String, Object> rightFlatMap = JsonFlattener.flattenAsMap(jsonRight);
        HashMap<String, String> couchbaseAndApiFieldMapper = new HashMap<String, String>();
        CommonFunctions fileRead = new CommonFunctions();
        File fieldMappingFile = CommonFunctions.loadFile("json-api", fileName);
        couchbaseAndApiFieldMapper = fileRead.readDataFromFileAndCreateJsonMap(fieldMappingFile);
        List<List<String>> comparisonResultList = new ArrayList<List<String>>();
        List<String> helperList = new ArrayList<String>();
        for (int k = 0; k < ChildNodeLength; k++) {
            for (Map.Entry e : couchbaseAndApiFieldMapper.entrySet()) {
                String apiResponseFieldJsonPathKey = e.getKey().toString().trim();
                String couchbaseFieldJsonPathKey = e.getValue().toString().trim();
                apiResponseFieldJsonPathKey = apiResponseFieldJsonPathKey
                        .replace("[i]", "[" + Integer.toString(k) + "]");
                String valueToApiResponseFieldJsonPathKey = rightFlatMap.get(apiResponseFieldJsonPathKey)
                        .toString();
                String valueToCouchbaseFieldJsonPathKey = leftFlatMap.get(couchbaseFieldJsonPathKey)
                        .toString();
                if (valueToApiResponseFieldJsonPathKey.equals(valueToCouchbaseFieldJsonPathKey)) {
                    helperList.add(apiResponseFieldJsonPathKey);
                    helperList.add(valueToApiResponseFieldJsonPathKey);
                    helperList.add(couchbaseFieldJsonPathKey);
                    helperList.add(valueToCouchbaseFieldJsonPathKey);
                    helperList.add("Matched");
                    helperList.add("\n");
                    comparisonResultList.clear();
                    comparisonResultList.add(helperList);
                } else {
                    helperList.add(apiResponseFieldJsonPathKey);
                    helperList.add(valueToApiResponseFieldJsonPathKey);
                    helperList.add(couchbaseFieldJsonPathKey);
                    helperList.add(valueToCouchbaseFieldJsonPathKey);
                    helperList.add("Unmatched");
                    comparisonResultList.clear();
                    comparisonResultList.add(helperList);
                }
            }
        }
        StringBuffer stringBuffResult = new StringBuffer();
        Iterator it = comparisonResultList.iterator();
        while (it.hasNext()) {
            stringBuffResult.append(it.next());
            stringBuffResult.append("\n");
        }
        Serenity.getCurrentSession().put("stringBuffResult", stringBuffResult);
        System.out.println("stringBuffResult" + stringBuffResult);
        System.out.println(comparisonResultList);
        return !comparisonResultList.contains("Unmatched");

    }

    public void write(String filename, String content) {

        BufferedWriter bw = null;
        FileWriter fw = null;
        String FILENAME = null;
        try {

            FILENAME = getFullPath("json-api", filename + ".json");
            fw = new FileWriter(FILENAME);
            bw = new BufferedWriter(fw);
            bw.write(content);

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

    public HashMap<String, String> readDataFromFileAndCreateJsonMap(String fileName,
                                                                    String jsonFileName, String fieldName) {

        BufferedReader br = null;
        FileReader fr = null;
        int line = -1;
        String updatedJson = null;
        String fileName1 = getFullPath(fileName);
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
                        //	updatedJson = updateJson(paramName.trim(), paramValue.trim());

                        // System.out.println(paramName + " --> " + paramValue);
                        // System.out.println(updatedJson);
                        updatedJsonMap.put(paramName, paramValue);
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

    public void write(String content) {
        String FILENAME = "response.json";
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {

            FILENAME = getFullPath("json-api", "response.json");

            fw = new FileWriter(FILENAME);
            bw = new BufferedWriter(fw);
            bw.write(content);

            Logger.info("Json has been written");

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

}
