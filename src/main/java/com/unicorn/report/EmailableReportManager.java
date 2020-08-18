package com.unicorn.report;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.unicorn.base.logger.Logger;
import com.unicorn.utils.AutProperties;
import com.unicorn.utils.CommonUtils;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class EmailableReportManager {

    private PrintWriter writer;
    private int total_tests_passed = 0;
    private int total_tests_failed = 0;
    private int total_tests_skipped = 0;
    private String execution_start_time = "";
    private String execution_end_time = "";
    private String execution_total_time = "";

    private String reportTitle = AutProperties.getEmailTitle();
    private String outputDir = System.getProperty("user.dir") + File.separator + "target";
    List<ScenarioData> scenarioDataList;

    public EmailableReportManager() {
        scenarioDataList = new ArrayList<>();
        execution_start_time = System.getProperty("startTime");
        execution_end_time = System.getProperty("endTime");
        execution_total_time = findDuration(execution_start_time, execution_end_time);
    }

    class ScenarioData {
        String feature;
        String scenario;
        String status;

        public ScenarioData(String feature, String scenario, String status) {
            this.feature = feature;
            this.scenario = scenario;
            this.status = status;
        }

        public String getFeature() {
            return feature;
        }

        public String getScenario() {
            return scenario;
        }

        public String getStatus() {
            return status;
        }
    }

    private PrintWriter createWriter(String outputdir, String reportFileName) throws IOException {
        new File(outputdir).mkdirs();
        return new PrintWriter(new BufferedWriter(new FileWriter(new File(outputdir, reportFileName))));
    }

    public void generateReport(String reportFileName) {
        extractScenarioExecutionInfo();
        startReport(reportFileName);
        generateExecutionDetails();
        endReport();
    }

    public void startReport(String reportFileName) {
        try {
            writer = createWriter(outputDir, reportFileName);
        } catch (IOException e) {
            System.err.println("Unable to create output file");
            e.printStackTrace();
            return;
        }

        startHtml();
        writeReportTitle(reportTitle);
        writer.println("<div class=\"container\" align=\"center\"  style=\"padding: 0px\" >");
        writer.println("<div align=\"center\" style=\"background-color:#404040; padding: 0px;\">");
        writer.println("<h2 align=\"center\" style=\"color:#FFFFFF;\"> <b>Summary</b></h2>");
        writer.println("</div>");
        generateEnvironmentDetails();
        generateOverallExecutionReport();
        writer.println("</div> <br>");


    }

    public void endTable() {
        writer.println("</div></table><hr></div>");
    }


    private String timeConversion(long seconds) {

        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        int minutes = (int) (seconds / SECONDS_IN_A_MINUTE);
        seconds -= minutes * SECONDS_IN_A_MINUTE;

        int hours = minutes / MINUTES_IN_AN_HOUR;
        minutes -= hours * MINUTES_IN_AN_HOUR;

        return prefixZeroToDigit(hours) + ":" + prefixZeroToDigit(minutes) + ":" + prefixZeroToDigit((int) seconds);
    }

    private String findDuration(String date1, String date2) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime dateTime1 = LocalDateTime.parse(date1, formatter);
        LocalDateTime dateTime2 = LocalDateTime.parse(date2, formatter);

        long diffInSeconds = java.time.Duration.between(dateTime1, dateTime2).getSeconds();

        long seconds = diffInSeconds % 60;
        long minutes = diffInSeconds / 60;
        long hours = diffInSeconds / 3600;
        long days = diffInSeconds / (3600 * 24);
        String difference = (days != 0 ? days + " Days, " : "")
                + (hours != 0 ? hours + " Hours, " : "")
                + (minutes != 0 ? minutes + " Minutes, " : "")
                + (seconds != 0 ? seconds + " Seconds" : "");
        return difference;
    }

    private String prefixZeroToDigit(int num) {
        int number = num;
        if (number <= 9) {
            String sNumber = "0" + number;
            return sNumber;
        } else
            return "" + number;

    }


    private void summaryCell(String[] val) {
        StringBuffer b = new StringBuffer();
        for (String v : val) {
            b.append(v + " ");
        }
        summaryCell(b.toString(), true);
    }

    private void summaryCell(String v, boolean isgood) {
        writer.println("<td class=\"numi" + (isgood ? "" : "_attn") + "\">" + v
                + "</td>");
    }


    public void summaryCellInfo(String cellValue, String bgColor) {
//        writer.println("<td class=\"numi_info\" bgcolor=\"" + bgColor + "\">" + cellValue + "</td>");
        writer.println("<td class=\"numi_info\" style=\"background-color:" + bgColor + "\">" + cellValue + "</td>");
    }

    private void summaryCellInfo(String v) {
        writer.print("<td class=\"numi_info" + "\">" + v
                + "</td>");
    }


    private void summaryCell(int v, int maxexpected) {
        summaryCell(String.valueOf(v), v <= maxexpected);
    }

    public void tableStart(String cssclass, String id) {
        writer.println("<table align=\"center\" cellspacing=\"0\" cellpadding=\"0\"" + (cssclass != null ? " class=\"" + cssclass + "\"" : " style=\"padding-bottom:2em\"")
                + (id != null ? " id=\"" + id + "\"" : "") + ">");
    }

    public void tableColumnStart(String label, String bgColor) {
        writer.println("<th bgcolor=\"" + bgColor + "\">" + label + "</th>");
    }

    private void titleRow(String label, int cq) {
        titleRow(label, cq, null);
    }

    private void titleRow(String label, int cq, String id) {
        writer.println("<tr");
        if (id != null) {
            writer.println(" id=\"" + id + "\"");
        }
        writer.println("><th colspan=\"" + cq + "\">" + label + "</th></tr>");
    }

    private void writeReportTitle(String title) {
        String logoPath = CommonUtils.getResourcePath("img") + System.getProperty("file.separator") + "WULogo.png";
        writer.println("<div style=\"background-color:#1a1a1a; padding: 0px\">");
        writer.println(" <div align=\"center\" style=\"padding-top: 5px; padding-bottom: 5px;\">");
        writer.println("<center><h1 style=\"color:#FFFFFF;\">" + title + "</h1></center>");
        writer.println("<center><h2 style=\"color:#FFFFFF;\">( " + getDateAsString() + " )</h2></center>");
        writer.println("</div></div>");
    }

    private String getDateAsString() {
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * Starts HTML stream
     */
    private void startHtml() {
        writer.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
        writer.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        writer.println("<head>");
        writer.println("<title>Test Execution Report</title>");
        writer.println("<style type=\"text/css\">");
        writer.println("table {margin-bottom:10px;border-collapse:collapse;empty-cells:show}");
        writer.println("td,th {border:1px solid #009;padding:.25em .5em}");
        writer.println(".result th {vertical-align:bottom}");
        writer.println(".param th {padding-left:1em;padding-right:1em}");
        writer.println(".param td {padding-left:.5em;padding-right:2em}");
        writer.println(".stripe td,.stripe th {background-color: #E6EBF9}");
        writer.println(".numi,.numi_attn {text-align:right}");
        writer.println(".numi_info,.numi_attn_info {letter-spacing: normal; text-align:left; font-size: 10.0pt; font-family: \"Arial\", sans-serif}");
        writer.println(".total td {font-weight:bold}");
        writer.println(".passedodd td {background-color: #0A0}");
        writer.println(".passedeven td {background-color: #3F3}");
        writer.println(".skippedodd td {background-color: #CCC}");
        writer.println(".skippedodd td {background-color: #DDD}");
        writer.println(".failedodd td,.numi_attn {background-color: #F33}");
        writer.println(".failedeven td,.stripe .numi_attn {background-color: #D00}");
        writer.println(".stacktrace {white-space:pre;font-family:monospace}");
        writer.println(".totop {font-size:85%;text-align:center;border-bottom:2px solid #000}");
        writer.println(".floatLeft { width: 30%; float: left; }");
        writer.println(".floatRight {width: 50%; float: right; }");
        writer.println(".container {" + "overflow-x: auto;" + "white-space: nowrap;" + "}");
        writer.println(".containerfloatleft {float: left; width: 10%; white-space: nowrap;}");
        writer.println(".containerfloatright {float: right; width: 60%; white-space: nowrap; }");
        writer.println(".normal { letter-spacing: normal;margin: 0; font-size: 10.0pt; font-family: \"Arial\", sans-serif; color: black;}");
        //out.println(".container { overflow: hidden; }");
        writer.println("</style>");
        writer.println("</head>");
        writer.println("<body bgcolor='WhiteSmoke'>");

    }

    /**
     * Finishes HTML stream
     */
    public void endReport() {
        writer.println("<hr/>");
        writer.println("<p class=\"normal\">&nbsp;</p>\n" +
                "<p class=\"normal\">\n" +
                "<b>This is a system generated email please DO-NOT reply.</b>\n" +
                "</p>\n" +
                "<p class=\"normal\">&nbsp;<br></p>\n" +
                "<p class=\"normal\">\n" +
                "<b><span style=\"color: #595959\">"+AutProperties.getEmailSignatureName()+"</span></b>\n" +
                "</p>\n" +
                "<p class=\"normal\">\n" +
                "<span style=\"color: #595959\">"+AutProperties.getEmailSignatureId()+"</span>\n" +
                "</p>\n" +
                "<p class=\"normal\">&nbsp;</p>\n" +
                "<p class=\"normal\">\n" +
                "<span style=\"color: #595959\">This is an automatically generated e-mail for Test Execution status</span>\n" +
                "</p>\n" +
                "<p class=\"normal\">&nbsp;</p>\n" +
                "<p class=\"normal\">\n" +
                "<b><span style=\"color: #595959\">IMPORTANT NOTICE:</span></b>\n" +
                "</p>\n" +
                "<p class=\"normal\">\n" +
                "<span style=\"color: #595959\">\n" +
                "The information transmitted, including any content in this communication is confidential, is intended only for the use of the intended recipient and is the\tproperty of the Western Union Company or its affiliates and\tsubsidiaries." +
                "If you are not the intended recipient, you are hereby notified that any use of the information contained in or transmitted with the communication or dissemination, distribution, or copying of\tthis communication is strictly prohibited." +
                "If you have received this communication in error, please immediately delete the original and any copies or printouts permanently thereof." +
                "</span>\n" +
                "</p><br>");
        writer.println("<p><div align=\"left\" style=\"display:inline-block;width:10%;\"><img src=\"cid:WULogo.png\" alt=\"WU Logo\" width=\"300\" height=\"70\" style=\"width:300px;height:70px;\" data-inline=\"true\" /></div></p>");
        writer.println("</body></html>");
        writer.flush();
        writer.close();
    }

    private void generateEnvironmentDetails() {
        try {
            String name = AutProperties.getAppName();

            writer.println("<div class=\"containerfloatleft\" align=\"left\" style=\"padding-left: 10px; width:30%;\">");
            writer.println("<h2 align=\"center\"> <b>Testing Environment</b></h2>");
            tableStart("Testing Environment", null);
            writer.println("<tr>");
            summaryCellInfo("Execution Environment");
            summaryCellInfo(AutProperties.getExecutionEnvironment());
            writer.println("</tr>");

            writer.println("<tr>");
            summaryCellInfo("App Name");
            summaryCellInfo(name);
            writer.println("</tr>");


            writer.println("<tr>");
            summaryCellInfo("Operating System");
            summaryCellInfo(System.getProperty("os.name"));
            writer.println("</tr>");

            writer.println("<tr>");
            summaryCellInfo("Executor");
            summaryCellInfo(AutProperties.getEmailSignatureName());
            writer.println("</tr>");

            writer.println("<tr>");
            summaryCellInfo("Build Stability");
            if ((total_tests_passed + total_tests_skipped + total_tests_failed) == total_tests_passed)
                summaryCellInfo("STABLE", "#009900");
            else
                summaryCellInfo("UNSTABLE", "#ff3333");
            writer.println("</tr>");

            writer.println("</table>");
            writer.println("</div>");
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error("Exception while generating environment details");
        }
    }

    private void extractScenarioExecutionInfo() {
        try {
            String csvFile = System.getProperty("user.dir") + File.separator + "target" + File.separator + "allure-report" + File.separator + "data" + File.separator + "behaviors.csv";
            Logger.info("Reading results from: "+csvFile);
            FileReader filereader = new FileReader(csvFile);
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            List<String[]> allRows = csvReader.readAll();

            for (String[] row : allRows) {
                String status = null;
                if (Integer.parseInt(row[3]) > 0) {
                    status = "Failed";
                    total_tests_failed += 1;
                } else if (Integer.parseInt(row[4]) > 0) {
                    status = "Broken";
                    total_tests_failed += 1;
                } else if (Integer.parseInt(row[5]) > 0) {
                    status = "Passed";
                    total_tests_passed += 1;
                } else {
                    status = "Skipped";
                    total_tests_skipped += 1;
                }
                scenarioDataList.add(new ScenarioData(row[1], row[2], status));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error("Exception while extracting Scenario execution information");
        }
    }

    private void generateOverallExecutionReport() {
        try {
            writer.println("<div class=\"containerfloatright\" align=\"center\" style=\"padding-left:0px; margin: 10px;\">");
            writer.println("<div align=\"center\" style=\"padding-left:197px;\">");
            writer.println("<h2><b>Overall Execution Information</b></h2>");
            writer.println("</div>");
            tableStart("Overall Execution Information", null);
            writer.println("<tr>");
            tableColumnStart("Total Tests", "#787878");
            tableColumnStart("Total Passed", "#787878");
            tableColumnStart("Total Skipped", "#787878");
            tableColumnStart("Total Failed", "#787878");
            tableColumnStart("Success %", "#787878");
            tableColumnStart("Start Time<br/>(yyyy-mm-dd hh:mm:ss)", "#787878");
            tableColumnStart("End Time<br/>(yyyy-mm-dd hh:mm:ss)", "#787878");
//            tableColumnStart("Total Time<br/>(yyyy-mm-dd hh:mm:ss)", "#787878");
            tableColumnStart("Total Duration<br/>", "#787878");
            writer.println("</tr>");
            writer.println("<tr>");
            summaryCellInfo(Integer.toString(total_tests_passed + total_tests_skipped + total_tests_failed));
            summaryCellInfo(Integer.toString(total_tests_passed));
            summaryCellInfo(Integer.toString(total_tests_skipped));
            summaryCellInfo(Integer.toString(total_tests_failed));
            double pass_per = (total_tests_passed * 100) / (total_tests_passed + total_tests_skipped + total_tests_failed);
            summaryCellInfo(Double.toString(pass_per) + "  %");
            summaryCellInfo(execution_start_time);
            summaryCellInfo(execution_end_time);
            summaryCellInfo(execution_total_time);
            writer.println("</tr>");
            writer.println("</table>");
            writer.println("</div>");
        } catch (Exception e) {
            Logger.assertFail("Exception while generating overall execution report", e);
        }
    }

    private void generateExecutionDetails() {
        try {

            HashMap<String, Integer> scenarioDetails = new HashMap<>();
            scenarioDetails.put("Epic", 0);
            scenarioDetails.put("Feature", 1);
            scenarioDetails.put("Story", 2);
            scenarioDetails.put("Failed", 3);
            scenarioDetails.put("Broken", 4);
            scenarioDetails.put("Passed", 5);
            scenarioDetails.put("Skipped", 6);
            scenarioDetails.put("Unknown", 7);

            writer.println("<div class=\"container\" style=\"padding: 0px; overflow:scroll;\">");
            writer.println("<div style=\"background-color:#404040\" align=\"center\">");
            writer.println("<h2 style=\"color:#FFFFFF;\"> <b>Execution Details</b></h2>");
            writer.println("</div>");

            writer.println("<div align=\"center\">");
            tableStart("Execution Details", null);
            writer.println("<tr>");
            tableColumnStart("S.No", "#787878");
            tableColumnStart("Feature", "#787878");
            tableColumnStart("Scenario", "#787878");
            tableColumnStart("Status", "#787878");
            writer.println("</tr>");
            int scenarioCount = 1;
            for (ScenarioData scenarioData : scenarioDataList) {
                writer.println("<tr>");
                writer.println("<td>" + scenarioCount + "</td>");
                writer.println("<td>" + scenarioData.getFeature() + "</td>");
                writer.println("<td>" + scenarioData.getScenario() + "</td>");

                if (scenarioData.getStatus().equalsIgnoreCase("Failed")) {
                    summaryCellInfo("Failed", "#ff471a");  //red #FF5733
                } else if (scenarioData.getStatus().equalsIgnoreCase("Passed")) {
                    summaryCellInfo("Passed", "#99ff99"); //green #99ff99
                } else if (scenarioData.getStatus().equalsIgnoreCase("Broken")) {
                    summaryCellInfo("Broken", "#ff3333"); //green #99ff99
                } else {
                    summaryCellInfo("Skipped", "#F9D168"); //yellow #F9D168
                }

                writer.println("</tr>");
                ++scenarioCount;
            }

            writer.println("</tr>");
            writer.println("</table>");
            writer.println("</div></div><br>");
        } catch (
                Exception e) {
            e.printStackTrace();
            Logger.error("Exception while generation execution details");
        }
    }

}
