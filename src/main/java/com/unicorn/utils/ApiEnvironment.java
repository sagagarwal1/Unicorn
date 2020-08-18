package com.unicorn.utils;

/**
 * @author Vivek Lande
 */
public enum ApiEnvironment {
    FIRCO_DASHBOARD {
        @Override
        public String toString() {
            return CommonUtils.readPropertiesFile(CommonFunctions.getFullPath(AutProperties.getExecutionEnvironmentConfigFileName())).getProperty("fircoDashboard_QA");
        }
    }, ACLAIM_CLIENT_IWATCH {
        @Override
        public String toString() {
            return CommonUtils.readPropertiesFile(CommonFunctions.getFullPath(AutProperties.getExecutionEnvironmentConfigFileName())).getProperty("aclaimClientIWatch_QA");
        }
    }, PSS {
        @Override
        public String toString() {
            return CommonUtils.readPropertiesFile(CommonFunctions.getFullPath(AutProperties.getExecutionEnvironmentConfigFileName())).getProperty("PSS_QA");
        }
    }, ENTITY {
        @Override
        public String toString() {
            return CommonUtils.readPropertiesFile(CommonFunctions.getFullPath(AutProperties.getExecutionEnvironmentConfigFileName())).getProperty("ENTITY_QA");
        }
    },
    COMPLIANCE_WEB_API {
        @Override
        public String toString() {
            return CommonUtils.readPropertiesFile(CommonFunctions.getFullPath(AutProperties.getExecutionEnvironmentConfigFileName())).getProperty("complianceWebApi_QA");
        }
    },
    RTCC {
        @Override
        public String toString() {
            return CommonUtils.readPropertiesFile(CommonFunctions.getFullPath(AutProperties.getExecutionEnvironmentConfigFileName())).getProperty("RTCC_QA");
        }
    }
}
