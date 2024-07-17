package vn.ses.s3m.plus.common;

import java.util.Date;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.collections.map.MultiKeyMap;

public class Constants {

    /** Config Path */
    public static final String CONFIG_PATH = "config";

    /** Slash Character */
    public static final String SLASH_CHARACTER = "/";

    /** Application resources properties file name */
    public static final String S3M_RESOURCES_FILE = "resources.properties";

    /** Đường dẫn SMS base URL */
    public static final String SMS_BASE_URL = "SMS_BASE_URL";

    public static int SMS_THREAD_POOL_NO = 5;

    /** Nội dung tin nhắn */
    public static final String SMS_MESSAGE = "SMS_MESSAGE";

    public static final String PARAMETER_01 = "{1}";

    public static final String PARAMETER_02 = "{2}";

    /** PHONE TEST */
    public static final String SMS_PHONE_TEST = "SMS_PHONE_TEST";

    /** S3M Queue */
    public static class S3mQueue {
        public static final String LOAD_SERVER_URI = "tcp://localhost:1883";
        public static final String LOAD_CLIENT_ID = "client_pub";
        public static final String LOAD_CLIENT_ID_SUB = "client_sub";
        public static final String QUEUE_NAME_SETTING = "MQTT_UPDATE_SETTING";
        public static final String QUICK_QUEUE_NAME_LOAD = "s3m-quick-queue-load";
        public static final String HOST_NAME = "localhost";
        public static final Integer NUMBER_5672 = 5672;
        public static final String LOAD_USER_NAME = "mqtt-test";
        public static final String LOAD_PASSWORD = "mqtt-test";
        public static final String LOAD_TOPIC = "loadTopic";
        public static final String LOAD_QOS = "1";
        public static final String EXCHANGE_NAME = "amq.topic";
        public static final String TOPIC_SETTING_NAME = "MQTT_UPDATE_SETTING";
    }

    public static class ResponseMessage {
        public static final String SUCCESS = "SUCCESS";
        public static final String ERROR = "ERROR";
        public static final String ERROR_EXPIRED_TOKEN = "ERROR_EXPIRED_TOKEN";
        public static final String ACCOUNT_IS_LOCKED_BY_ADMIN = "ACCOUNT_IS_LOCKED_BY_ADMIN";
        public static final String ACCOUNT_IS_LOCKED = "ACCOUNT_IS_LOCKED";
        public static final String BAD_CREDITIAL = "BAD_CREDITIAL";
        public static final String NOT_FOUND_EMAIL = "NOT_FOUND_EMAIL";
        public static final String NEW_PASSWORD_SAME_CURRENT_PASSWORD = "NEW_PASSWORD_SAME_CURRENT_PASSWORD";
        public static final String ERROR_UPDATE_PASSWORD = "ERROR_UPDATE_PASSWORD";
        public static final String CURRENT_PASSWORD_NOT_MATCH = "CURRENT_PASSWORD_NOT_MATCH";
    }

    public static class FirstLoginValidation {
        public static final String CURRENT_PASSWORD_NOT_BLANK = "CURRENT_PASSWORD_NOT_BLANK";
        public static final String PASSWORD_PATTERN = "^(?:(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*)";
        public static final String PASSWORD_PATTERN_ERROR = "PASSWORD_PATTERN_ERROR";
        public static final int PASSWORD_MIN_SIZE = 8;
        public static final int PASSWORD_MAX_SIZE = 255;
        public static final String PASSWORD_MIN_SIZE_ERROR = "PASSWORD_MIN_SIZE_ERROR";
        public static final String PASSWORD_MAX_SIZE_ERROR = "PASSWORD_MAX_SIZE_ERROR";
        public static final String PASSWORD_NOT_BLANK = "PASSWORD_NOT_BLANK";
    }

    public static class UserValidation {
        public static final String USERNAME_NOT_BLANK = "USERNAME_NOT_BLANK";
        public static final int USERNAME_MAX_SIZE = 20;
        public static final String USERNAME_MAX_SIZE_ERROR = "USERNAME_MAX_SIZE_ERROR";
        public static final String PASSWORD_PATTERN = "^(?:(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*)";
        public static final String PASSWORD_PATTERN_ERROR = "PASSWORD_PATTERN_ERROR";
        public static final String PASSWORD_NOT_BLANK = "PASSWORD_NOT_BLANK";
        public static final int PASSWORD_MIN_SIZE = 8;
        public static final int PASSWORD_MAX_SIZE = 255;
        public static final String PASSWORD_MIN_SIZE_ERROR = "PASSWORD_MIN_SIZE_ERROR";
        public static final String PASSWORD_MAX_SIZE_ERROR = "PASSWORD_MAX_SIZE_ERROR";
        public static final String STAFF_NAME_NOT_BLANK = "STAFF_NAME_NOT_BLANK";
        public static final int STAFF_NAME_MAX_SIZE = 100;
        public static final String STAFF_NAME_MAX_SIZE_ERROR = "STAFF_NAME_MAX_SIZE_ERROR";
        public static final String EMAIL_NOT_BLANK = "EMAIL_NOT_BLANK";
        public static final int EMAIL_MAX_SIZE = 100;
        public static final String EMAIL_MAX_SIZE_ERROR = "EMAIL_MAX_SIZE_ERROR";
        public static final String EMAIL_IS_INVALID = "EMAIL_IS_INVALID";
        public static final String USERNAME_IS_EXIST = "USERNAME_IS_EXIST";
        public static final String EMAIL_IS_EXIST = "EMAIL_IS_EXIST";
    }

    public static class CableValidation {
        // validate function
        public static final String CABLE_NAME_NOT_BLANK = "Tên cáp không được bỏ trống";
        public static final int CABLE_NAME_SIZE = 255;
        public static final String CABLE_NAME_MAX_LENGTH = "Tên cáp chỉ có tối đa 255 kí tự";
        public static final String CURRENT_NOT_BLANK = "Biến dòng không được bỏ trống";
        public static final int CURRENT_SIZE = 8388607;
        public static final String CURRENT_MAX_VALUE = "Biến dòng phải nhỏ hơn 8388607";
        public static final int DESCRIPTION_SIZE = 100;
        public static final String DESCRIPTION_MAX_LENGTH = "Mô tả chỉ có tối đa 100 kí tự";

        // validate error
        public static final String CABLE_NAME_EXIST = "Tên cáp đã tồn tại";
    }

    public static class ManagerValidation {
        public static final String MANAGER_NAME_NOT_BLANK = "Tên tỉnh thành không được bỏ trống";
        public static final int MANAGER_NAME_SIZE = 100;
        public static final String MANAGER_NAME_MAX_LENTH = "Tên tỉnh thành chỉ có tối đa 100 kí tự";

        public static final String LONGTITUDE_NOT_BLANK = "Kinh độ không được bỏ trống";
        public static final double LONGTITUDE_MIN_SIZE = -180.00000000;
        public static final String LONGTITUDE_MIN_VALUE = "Kinh độ phải lớn hơn -180.00000000";
        public static final double LONGTITUDE_MAX_SIZE = 180.00000000;
        public static final String LONGTITUDE_MAX_VALUE = "Kinh độ phải nhỏ hơn 180.00000000;";

        public static final String LATITUDE_NOT_BLANK = "Vĩ độ không được bỏ trống";
        public static final double LATITUDE_MIN_SIZE = -85.05112878;
        public static final String LATITUDE_MIN_VALUE = "Vĩ đô phải lớn hơn -85.05112878;";
        public static final double LATITUDE_MAX_SIZE = 85.05112878;
        public static final String LATITUDE_MAX_VALUE = "Vĩ độ phải nhỏ hơn 85.05112878;";
        public static final int DESCRIPTION_SIZE = 100;
        public static final String DESCRIPTION_MAX_LENGTH = "Mô tả chỉ có tối đa 100 kí tự";

        // validate error
        public static final String MANAGER_NAME_EXIST = "Tên tỉnh thành đã tồn tại";
        public static final String MANAGER_NAME_DEPENDENT = "Tên tỉnh thành đang sử dụng";
    }

    public static class SuperManagerValidation {
        public static final String SUPER_MANAGER_NAME_EXIST = "Tên khu vực đã được sử dụng!";
        public static final String SUPER_MANAGER_NAME_NOT_BLANK = "Tên khu vực không được để trống!";
        public static final String NOT_BLANK_LONGITUDE = "Kinh độ không được để trống!";
        public static final String NOT_BLANK_LATITUDE = "Vĩ độ không được để trống!";
        public static final int MAX_SIZE = 100;
        public static final long LATITUDE_MIN_VALUE = (long) -85.05112878;
        public static final long LATITUDE_MAX_VALUE = (long) 85.05112878;
        public static final long LONGITUDE_MIN_VALUE = (long) -180.00000000;
        public static final long LONGITUDE_MAX_VALUE = (long) 180.00000000;
        public static final String MAX_SIZE_ERROR_SUPERMANAGERNAME = "Tên khu vực chỉ có tối đa 100 ký tự!";
        public static final String MIN_VALUE_ERROR_LONGITUDE = "Kinh độ phải lớn hơn -180.00000000!";
        public static final String MAX_VALUE_ERROR_LONGITUDE = "Kinh độ phải nhỏ hơn 180.000000008!";
        public static final String MIN_VALUE_ERROR_LATITUDE = "Vĩ phải lớn hơn -85.05112878!";
        public static final String MAX_VALUE_ERROR_LATITUDE = "Vĩ độ phải nhỏ hơn 85.05112878!";
    }

    public static class DeviceValidation {
        public static final String DEVICE_CODE_EXIST = "Mã thiết bị đã được sử dụng!";
        public static final String DEVICE_NAME_NOT_BLANK = "Tên thiết bị không được để trống!";
        public static final String SIM_NO_NOT_BLANK = "Số sim không được để trống!";
        public static final String LONGITUDE_NOT_BLANK = "Kinh độ không được để trống!";
        public static final String LATITUDE_NOT_BLANK = "Vĩ độ không được để trống!";
        public static final String UID_NOT_BLANK = "UID không được để trống!";
        public static final String IP_NOT_BLANK = "IP không được để trống!";
        public static final String IP_PARTERN = "^[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}$";
        public static final String IP_PARTERN_ERROR = "IP không đúng định dạng!";
        public static final int SIM_NO_MAX_SIZE = 20;
        public static final int DEVICE_NAME_MAX_SIZE = 100;
        public static final int MODEL_MAX_SIZE = 255;
        public static final long UID_MIN_VALUE = 1;
        public static final long UID_MAX_VALUE = 255;
        public static final long LATITUDE_MIN_VALUE = (long) -85.05112878;
        public static final long LATITUDE_MAX_VALUE = (long) 85.05112878;
        public static final long LONGITUDE_MIN_VALUE = (long) -180.00000000;
        public static final long LONGITUDE_MAX_VALUE = (long) 180.00000000;
        public static final String SIM_NO_MAX_SIZE_ERROR = "Số sim chỉ có tối đa 20 ký tự!";
        public static final String MODEL_MAX_SIZE_ERROR = "Số sim chỉ có tối đa 255 ký tự!";
        public static final String DEVICE_NAME_MAX_SIZE_ERROR = "Tên thiết bị chỉ có tối đa 100 ký tự.";
        public static final String LONGITUDE_MIN_VALUE_ERROR = "Kinh độ phải lớn hơn -180.00000000.";
        public static final String LONGITUDE_MAX_VALUE_ERROR = "Kinh độ phải nhỏ hơn 180.00000000.";
        public static final String LATITUDE_MIN_VALUE_ERROR = "Vĩ phải lớn hơn -85.05112878.";
        public static final String LATITUDE_MAX_VALUE_ERROR = "Vĩ độ phải nhỏ hơn 85.05112878.";
        public static final String UID_MIN_VALUE_ERROR = "UID phải lớn hơn 1.";
        public static final String UID_MAX_VALUE_ERROR = "UID phải nhỏ hơn 255.";
        public static final String WORK_DATE_NOT_BLANK = "Thời gian bắt đầu hoạt động không được để trống!";
        public static final String DEVICE_TYPE_NAME_EXIST = "Loại thiết bị đã tồn tại!";
    }

    public static class AreaValidate {
        public static final String AREA_NAME_NOT_BLANK = "Tên quận huyện không được để trống!";
        public static final String AREA_NAME_MAX = "Tên quận huyện chỉ có tối đa 100 kí tự!";
        public static final int AREA_NAME_MAX_SIZE = 100;
        public static final String MANAGER_ID_NOT_BLANK = "Không được để trống trực thuộc!";
        public static final String LONGITUDE_MAX = "Kinh độ phải nhỏ hơn 180!";
        public static final double LONGITUDE_MAX_SIZE = 180;
        public static final String LONGITUDE_MIN = "Kinh độ phải lớn hơn -180!";
        public static final double LONGITUDE_MIN_SIZE = -180;
        public static final String LONGITUDE_NOT_BLANK = "Kinh độ không được để trống!";
        public static final String LATITUDE_NOT_BLANK = "Vĩ độ không được để trống!";
        public static final String LATITUDE_MAX = "Vĩ độ phải nhỏ hơn 85.05112878!";
        public static final double LATITUDE_MAX_SIZE = 85.05112878;
        public static final String LATIUDE_MIN = "Vĩ độ phải lớn hơn -85.05112878!";
        public static final double LATITUDE_MIN_SIZE = -85.05112878;
        public static final String DESCRIPTION_MAX = "Mô tả không được quá 1000 kí tự!";
        public static final int DESCRIPTION_MAX_SIZE = 1000;
    }

    public static class CustomerValidate {
        public static final String CUSTOMER_CODE_NOT_BLANK = "CUSTOMER_CODE_NOT_BLANK";
        public static final String CUSTOMER_CODE_MAX_LENGTH = "CUSTOMER_CODE_MAX_SIZE_ERROR";
        public static final String CUSTOMER_NAME_NOT_BLANK = "CUSTOMER_NAME_NOT_BLANK";
        public static final String CUSTOMER_NAME_MAX_LENGTH = "CUSTOMER_NAME_MAX_SIZE_ERROR";
        public static final String CUSTOMER_CODE_EXISTED = "CUSTOMER_CODE_EXISTED";
        public static final int CUSTOMER_NAME_MAX = 100;
        public static final int CUSTOMER_CODE_MAX = 8;
    }

    public static class ObjectValidate {
        public static final String OBJECT_NAME_EXIST = "Tên đối tượng giám sát đã tồn tại";
    }

    public static class ProjectValidate {
        public static final String PROJECT_NAME_NOT_BLANK = "PROJECT_NAME_NOT_BLANK";
        public static final String PROJECT_NAME_MAX_LENGTH = "PROJECT_NAME_MAX_SIZE_ERROR";
        public static final int PROJECT_NAME_MAX = 100;
        public static final String PROJECT_LONGITUDE_MAX_ERROR = "PROJECT_LONGITUDE_MAX_ERROR";
        public static final String PROJECT_LONGITUDE_MIN_ERROR = "PROJECT_LONGITUDE_MIN_ERROR";
        public static final String PROJECT_LATITUDE_MAX_ERROR = "PROJECT_LATITUDE_MAX_ERROR";
        public static final String PROJECT_LATITUDE_MIN_ERROR = "PROJECT_LATITUDE_MIN_ERROR";
        public static final String PROJECT_LATITUDE_NOT_BLANK = "PROJECT_LATITUDE_NOT_BLANK";
        public static final String PROJECT_LONGITUDE_NOT_BLANK = "PROJECT_LONGITUDE_NOT_BLANK";
        public static final long PROJECT_LONGITUDE_MAX = (long) 180.00000000;
        public static final long PROJECT_LONGITUDE_MIN = (long) -180.00000000;
        public static final long PROJECT_LATITUDE_MAX = (long) 85.05112878;
        public static final long PROJECT_LATITUDE_MIN = (long) -85.05112878;
    }

    public static class WarningCarValidate {
        public static final String ORGANIZATION_CREATE_NOT_BLANK = "Đơn vị lập không được để trống!";
        public static final String ORGANIZATION_CREATE_MAX = "Đơn vị lập không được quá 1000 kí tự!";
        public static final int ORGANIZATION_CREATE_MAX_SIZE = 1000;
        public static final String CONTENT_NOT_BLANK = "Nội dung không được để trống!";
        public static final String CONTENT_MAX = "Nội dung không được quá 1000 kí tự!";
        public static final int CONTENT_MAX_SIZE = 1000;
        public static final String REASON_METHOD_NOT_BLANK = "Nguyên nhân và biện pháp không được để trống!";
        public static final String REASON_METHOD_MAX = "Nguyên nhân và biện pháp không được quá 1000 kí tự!";
        public static final int REASON_METHOD_MAX_SIZE = 1000;
        public static final String ORGANIZATION_EXECUTION_NOT_BLANK = "Đơn vị thực hiện không được để trống!";
        public static final String ORGANIZATION_EXECUTION_MAX = "Đơn vị thực hiện không được quá 1000 kí tự!";
        public static final int ORGANIZATION_EXECUTION_MAX_SIZE = 1000;
        public static final String RESULT_EXECUTION_NOT_BLANK = "Kết quả thực hiện không được để trống!";
        public static final String RESULT_EXECUTION_MAX = "Kết quả thực hiện không được quá 1000 kí tự!";
        public static final int RESULT_EXECUTION_SIZE = 1000;
        public static final String ORGANIZATION_TEST_NOT_BLANK = "Đơn vị kiểm tra không được để trống!";
        public static final String ORGANIZATION_TEST_MAX = "Đơn vị kiểm tra không được quá 1000 kí tự!";
        public static final int ORGANIZATION_TEST_SIZE = 1000;
    }

    public class WarningType {

        public static final int NGUONG_AP_CAO = 101;
        public static final int NGUONG_AP_THAP = 102;

        public static final int NHIET_DO_TIEP_XUC = 103;
        public static final int COS_THAP_TONG = 104;

        public static final int QUA_TAI = 105;

        public static final int TAN_SO_THAP = 106;
        public static final int TAN_SO_CAO = 107;

        public static final int MAT_NGUON_PHA = 108;
        public static final int LECH_PHA = 109;
        public static final int NGUOC_PHA = 110;

        public static final int NGUONG_HAI_BAC_N = 111;
        public static final int NGUONG_TONG_HAI = 112;

        public static final int DONG_TRUNG_TINH = 113;
        public static final int DONG_TIEP_DIA = 114;

        public static final int CANH_BAO_1 = 115;
        public static final int CANH_BAO_2 = 116;
        public static final int LECH_AP_PHA = 117;

        public static final int DONG_MO_CUA = 120;

        public static final int STATUS_WARNING = 0;

        public static final int STATUS_WARNING_1 = 1;

        public static final int STATUS_WARNING_2 = 2;

        public static final int STATUS_WARNING_3 = 3;

        public static final int STATUS_WARNING_4 = 4;
    }

    public class WarningTypeMeter {

        public static final int NGUONG_AP_CAO = 101;
        public static final int NGUONG_AP_THAP = 102;
        public static final int QUA_TAI = 103;
        public static final int HE_SO_CONG_SUAT_THAP = 104;
        public static final int TAN_SO_CAO = 105;
        public static final int TAN_SO_THAP = 106;
        public static final int LECH_PHA = 107;
        public static final int SONG_HAI_DONG_DIEN_BAC_N = 108;
        public static final int SONG_HAI_DIEN_AP_BAC_N = 109;
        public static final int TONG_MEO_SONG_HAI_DIEN_AP = 110;
        public static final int TONG_MEO_SONG_HAI_DONG_DIEN = 111;
        public static final int NGUOC_PHA = 112;
        public static final int MAT_DIEN_TONG = 113;
    }

    public static class WarningTypeInverter {
        // PV constants
        public static final int CHAM_DAT = 201; // 0: GROUND FAULT
        public static final int DIEN_AP_CAO_DC = 202; // 1: DC_OVER_VOLT
        public static final int MAT_KET_NOI_AC = 203; // 2: AC_DISCONNECT
        public static final int MAT_KET_NOI_DC = 204; // 3: DC_DISCONNECT
        public static final int MAT_NGUON_LUOI = 205; // 4: GRID_DISCONNECT
        public static final int DONG_MO_CUA = 206; // 5: CABINET_OPEN
        public static final int NGAT_THU_CONG = 207; // 6: MANUAL_SHUTDOWN
        public static final int NHIET_DO_CAO = 208; // 7: OVER_TEMP
        public static final int TAN_SO_CAO = 209; // 8: OVER_FREQUENCY
        public static final int TAN_SO_THAP = 210; // 9: UNDER_FREQUENCY
        public static final int DIEN_AP_CAO_AC = 211; // 10: AC_OVER_VOLT
        public static final int DIEN_AP_THAP_AC = 212; // 11: AC_UNDER_VOLT
        public static final int HONG_CAU_CHI = 213; // 12: BLOWN_STRING_FUSE
        public static final int NHIET_DO_THAP = 214; // 13: UNDER_TEMP
        public static final int MEMORY_LOSS = 215; // 14: MEMORY_LOSS
        public static final int HW_TEST_FAILURE = 216; // 15: HW_TEST_FAILURE
    }

    public static class WarningTypeTempHumidity {
        // PV constants
        public static final int NHIET_DO_CAO = 301;
        public static final int NHIET_DO_THAP = 302;
        public static final int DO_AM_CAO = 303;
        public static final int DO_AM_THAP = 304;
    }

    public static class WarningTypeStatus {
        public static final int FI_TU_RMU = 401;
        public static final int KHOANG_TON_THAT = 402;
        public static final int DONG_MO_CUA = 403;
        public static final int MUC_DAU_THAP = 404;
        public static final int ROLE_GAS = 405;
        public static final int CHAM_VO = 406;
        public static final int MUC_DAU_CAO = 407;
        public static final int CAM_BIEN_HONG_NGOAI = 408;
        public static final int AP_SUAT_NOI_BO_MBA = 409;
        public static final int ROLE_NHIET_DO_DAU = 410;
        public static final int NHIET_DO_CUON_DAY = 411;
        public static final int KHI_GAS_MBA = 412;
    }

    public static class WARNING_RMU {
        public static final int DO_AM = 501;
        public static final int PHONG_DIEN = 502;
        public static final int TAN_SO_THAP = 503;
        public static final int TAN_SO_CAO = 504;
        public static final int SONG_HAI = 505;
        public static final int QUA_TAI_TONG = 506;
        public static final int QUA_TAI_NHANH = 507;
        public static final int LECH_PHA_TONG = 508;
        public static final int LECH_PHA_NHANH = 509;
        public static final int FI_TU_RMU = 510;
        public static final int KHOANG_TON_THAT = 511;
        public static final int DONG_MO_CUA = 512;
        public static final int MUC_DAU_THAP = 513;
        public static final int NHIET_DO = 514;
        public static final int NHIET_DO_DAU = 515;
        public static final int MAT_DIEN_TONG = 516;
        public static final int MAT_DIEN_NHANH = 517;
        public static final int ROLE_GAS = 518;
        public static final int CHAM_VO = 519;
        public static final int MUC_DAU_CAO = 520;
        public static final int CAM_BIEN_HONG_NGOAI = 521;
        public static final int DIEN_AP_CAO = 522;
        public static final int DIEN_AP_THAP = 523;
        public static final int COS_TONG_THAP = 524;
        public static final int COS_NHANH_THAP = 525;
        public static final int AP_SUAT_NOI_BO_MBA = 526;
        public static final int ROLE_NHIET_DO_DAU = 527;
        public static final int NHIET_DO_CUON_DAY = 528;
        public static final int KHI_GAS_MBA = 529;
        public static final int HE_SO_CON_SUAT_THAP = 530;
    }

    public static class WarningTypeDischarge {
        // PV constants
        public static final int PHONG_DIEN_HTR = 501;
        public static final int PHONG_DIEN_AMS = 601;
    }

    public static class DeviceType {

        public static final int INVERTER = 1;
        public static final int COMBINER = 3;
        public static final int STRING = 4;
    }

    public static class ES {

        public static final String DATETIME_FORMAT = "yyyy/MM/dd HH:mm:ss";
        public static final String DATETIME_FORMAT_DMYHM = "dd-MM-yyyy HH:mm";
        public static final String DATETIME_FORMAT_DMYHMS = "dd/MM/yyyy HH:mm:ss";
        public static final String DATETIME_FORMAT_DMYHHMM = "dd/MM/yyyy HH:mm";
        public static final String DATETIME_FORMAT_YMDHMS = "yyyy-MM-dd HH:mm:ss";
        public static final String DATETIME_FORMAT_YMDHM = "yyyy-MM-dd HH:mm";
        public static final String DATE_FORMAT_YMD = "yyyy-MM-dd";
        public static final String DATE_FORMAT_DMY = "dd-MM-yyyy";
        public static final String DATE_FORMAT_DMY_02 = "dd/MM/yyyy";
        public static final String DATE_FORMAT_YM_01 = "yyyyMM";
        public static final String DATE_FORMAT_YM_02 = "yyyy-MM";

        // -----------------------------------------------------------------------------------------------------------
        // CHARACTER CONSTANT
        // -----------------------------------------------------------------------------------------------------------

        /** Blank Character */
        public static final String BLANK_CHARACTER = " ";

        /** DOT Character */
        public static final String DOT_CHARACTER = ".";

        /** Comma Character */
        public static final String COMMA_CHARACTER = ",";

        /** Colon Character */
        public static final String COLON_CHARACTER = ":";

        /** Tilde Character */
        public static final String TILDE_CHARACTER = "~";

        /** Hyphen Character */
        public static final String HYPHEN_CHARACTER = "-";

        /** Zero Character */
        public static final String ZERO_CHARACTER = "0";

        /** Underscore Character */
        public static final String UNDERSCORE_CHARACTER = "_";

        /** Slash Character */
        public static final String SLASH_CHARACTER = "/";

    }

    public static class DATA {

        /** Loại chart */
        public static final class CHART {

            public static final int DONG_DIEN = 1;
            public static final int DIEN_AP = 2;
            public static final int CONG_SUAT_TAC_DUNG = 3;
            public static final int DIEN_NANG_PQS = 4;
            public static final int NHIET_DO = 5;
            public static final int SONG_HAI = 6;
            public static final int THOI_TIET = 7;

        }

        public static final class CHART_PV {

            public static final int DONG_DIEN = 1;
            public static final int DIEN_AP = 2;
            public static final int CONG_SUAT_TAC_DUNG = 3;
            public static final int DIEN_NANG_PQS = 4;
            public static final int NHIET_DO = 5;
            public static final int HIEU_SUAT = 6;

        }

        /** Loại chart */
        public static final class CHART_GRID {

            public static final int DONG_DIEN = 1;
            public static final int DIEN_AP = 2;
            public static final int CONG_SUAT_TAC_DUNG = 3;
            public static final int DIEN_NANG = 4;
            public static final int NHIET_DO_CUC = 5;
            public static final int NHIET_DO_KHOANG = 6;
            public static final int DO_AM = 7;
            public static final int LFB_RATIO = 8;
            public static final int LFB_EPPC = 9;
            public static final int MFB_RATIO = 10;
            public static final int MLFB_EPPC = 11;
            public static final int HLFB_RATIO = 12;
            public static final int HLFB_EPPC = 13;
            public static final int INDICATOR = 14;

        }

        // truy vấn theo thời gian

        /** Message Labels */
        public static interface MESSAGE {

            /** Message type */
            static int FRAME1 = 1;
            static int FRAME2 = 2;
            static int INVERTER1 = 4;
            static int WEATHER1 = 5;
            static int COMBINER1 = 6;
            static int STRING1 = 7;
            static int PANEL1 = 8;
            static int GRID_RMU_DRAWER1 = 9;
        }

        public static Date IN_2020 = DateUtils.toDate("2020-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2021 = DateUtils.toDate("2021-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2022 = DateUtils.toDate("2022-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2023 = DateUtils.toDate("2023-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2024 = DateUtils.toDate("2024-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2025 = DateUtils.toDate("2025-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2026 = DateUtils.toDate("2026-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2027 = DateUtils.toDate("2027-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2028 = DateUtils.toDate("2028-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2029 = DateUtils.toDate("2029-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2030 = DateUtils.toDate("2030-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2031 = DateUtils.toDate("2031-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2032 = DateUtils.toDate("2032-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2033 = DateUtils.toDate("2033-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2034 = DateUtils.toDate("2034-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2035 = DateUtils.toDate("2035-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2036 = DateUtils.toDate("2036-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2037 = DateUtils.toDate("2037-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2038 = DateUtils.toDate("2038-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2039 = DateUtils.toDate("2039-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2040 = DateUtils.toDate("2040-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2041 = DateUtils.toDate("2041-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2042 = DateUtils.toDate("2042-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2043 = DateUtils.toDate("2043-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2044 = DateUtils.toDate("2044-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2045 = DateUtils.toDate("2045-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2046 = DateUtils.toDate("2046-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2047 = DateUtils.toDate("2047-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2048 = DateUtils.toDate("2048-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2049 = DateUtils.toDate("2049-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2050 = DateUtils.toDate("2050-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2051 = DateUtils.toDate("2051-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2052 = DateUtils.toDate("2052-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2053 = DateUtils.toDate("2053-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2054 = DateUtils.toDate("2054-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2055 = DateUtils.toDate("2055-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2056 = DateUtils.toDate("2056-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2057 = DateUtils.toDate("2057-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2058 = DateUtils.toDate("2058-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2059 = DateUtils.toDate("2059-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);
        public static Date IN_2060 = DateUtils.toDate("2060-12-31 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS);

        public static Date[] tables = {IN_2021, IN_2022, IN_2023, IN_2024, IN_2025, IN_2026, IN_2027, IN_2028, IN_2029,
            IN_2030, IN_2031, IN_2032, IN_2033, IN_2034, IN_2035, IN_2036, IN_2037, IN_2038, IN_2039, IN_2040, IN_2041,
            IN_2042, IN_2043, IN_2044, IN_2045, IN_2046, IN_2047, IN_2048, IN_2049, IN_2050, IN_2051, IN_2052, IN_2053,
            IN_2054, IN_2055, IN_2056, IN_2057, IN_2058, IN_2059, IN_2060};

        /** Nhãn tên bảng dữ liệu vận hành */
        public static String DEFAULT_DATA_TABLE = "s3m_data_load_frame_1_2022";
        public static MultiKeyMap DATA_TABLES = new MultiKeyMap();
        static {
            DATA_TABLES.put(new MultiKey(IN_2021, MESSAGE.FRAME1), "s3m_data_load_frame_1_2021");
            DATA_TABLES.put(new MultiKey(IN_2022, MESSAGE.FRAME1), "s3m_data_load_frame_1_2022");
            DATA_TABLES.put(new MultiKey(IN_2023, MESSAGE.FRAME1), "s3m_data_load_frame_1_2023");
            DATA_TABLES.put(new MultiKey(IN_2024, MESSAGE.FRAME1), "s3m_data_load_frame_1_2024");
            DATA_TABLES.put(new MultiKey(IN_2025, MESSAGE.FRAME1), "s3m_data_load_frame_1_2025");
            DATA_TABLES.put(new MultiKey(IN_2026, MESSAGE.FRAME1), "s3m_data_load_frame_1_2026");
            DATA_TABLES.put(new MultiKey(IN_2027, MESSAGE.FRAME1), "s3m_data_load_frame_1_2027");
            DATA_TABLES.put(new MultiKey(IN_2028, MESSAGE.FRAME1), "s3m_data_load_frame_1_2028");
            DATA_TABLES.put(new MultiKey(IN_2029, MESSAGE.FRAME1), "s3m_data_load_frame_1_2029");
            DATA_TABLES.put(new MultiKey(IN_2030, MESSAGE.FRAME1), "s3m_data_load_frame_1_2030");
            DATA_TABLES.put(new MultiKey(IN_2031, MESSAGE.FRAME1), "s3m_data_load_frame_1_2031");
            DATA_TABLES.put(new MultiKey(IN_2032, MESSAGE.FRAME1), "s3m_data_load_frame_1_2032");
            DATA_TABLES.put(new MultiKey(IN_2033, MESSAGE.FRAME1), "s3m_data_load_frame_1_2033");
            DATA_TABLES.put(new MultiKey(IN_2034, MESSAGE.FRAME1), "s3m_data_load_frame_1_2034");
            DATA_TABLES.put(new MultiKey(IN_2035, MESSAGE.FRAME1), "s3m_data_load_frame_1_2035");
            DATA_TABLES.put(new MultiKey(IN_2036, MESSAGE.FRAME1), "s3m_data_load_frame_1_2036");
            DATA_TABLES.put(new MultiKey(IN_2037, MESSAGE.FRAME1), "s3m_data_load_frame_1_2037");
            DATA_TABLES.put(new MultiKey(IN_2038, MESSAGE.FRAME1), "s3m_data_load_frame_1_2038");
            DATA_TABLES.put(new MultiKey(IN_2039, MESSAGE.FRAME1), "s3m_data_load_frame_1_2039");
            DATA_TABLES.put(new MultiKey(IN_2040, MESSAGE.FRAME1), "s3m_data_load_frame_1_2040");
            DATA_TABLES.put(new MultiKey(IN_2041, MESSAGE.FRAME1), "s3m_data_load_frame_1_2041");
            DATA_TABLES.put(new MultiKey(IN_2042, MESSAGE.FRAME1), "s3m_data_load_frame_1_2042");
            DATA_TABLES.put(new MultiKey(IN_2043, MESSAGE.FRAME1), "s3m_data_load_frame_1_2043");
            DATA_TABLES.put(new MultiKey(IN_2044, MESSAGE.FRAME1), "s3m_data_load_frame_1_2044");
            DATA_TABLES.put(new MultiKey(IN_2045, MESSAGE.FRAME1), "s3m_data_load_frame_1_2045");
            DATA_TABLES.put(new MultiKey(IN_2046, MESSAGE.FRAME1), "s3m_data_load_frame_1_2046");
            DATA_TABLES.put(new MultiKey(IN_2047, MESSAGE.FRAME1), "s3m_data_load_frame_1_2047");
            DATA_TABLES.put(new MultiKey(IN_2048, MESSAGE.FRAME1), "s3m_data_load_frame_1_2048");
            DATA_TABLES.put(new MultiKey(IN_2049, MESSAGE.FRAME1), "s3m_data_load_frame_1_2049");
            DATA_TABLES.put(new MultiKey(IN_2050, MESSAGE.FRAME1), "s3m_data_load_frame_1_2050");
            DATA_TABLES.put(new MultiKey(IN_2051, MESSAGE.FRAME1), "s3m_data_load_frame_1_2051");
            DATA_TABLES.put(new MultiKey(IN_2052, MESSAGE.FRAME1), "s3m_data_load_frame_1_2052");
            DATA_TABLES.put(new MultiKey(IN_2053, MESSAGE.FRAME1), "s3m_data_load_frame_1_2053");
            DATA_TABLES.put(new MultiKey(IN_2054, MESSAGE.FRAME1), "s3m_data_load_frame_1_2054");
            DATA_TABLES.put(new MultiKey(IN_2055, MESSAGE.FRAME1), "s3m_data_load_frame_1_2055");
            DATA_TABLES.put(new MultiKey(IN_2056, MESSAGE.FRAME1), "s3m_data_load_frame_1_2056");
            DATA_TABLES.put(new MultiKey(IN_2057, MESSAGE.FRAME1), "s3m_data_load_frame_1_2057");
            DATA_TABLES.put(new MultiKey(IN_2058, MESSAGE.FRAME1), "s3m_data_load_frame_1_2058");
            DATA_TABLES.put(new MultiKey(IN_2059, MESSAGE.FRAME1), "s3m_data_load_frame_1_2059");
            DATA_TABLES.put(new MultiKey(IN_2060, MESSAGE.FRAME1), "s3m_data_load_frame_1_2060");

            DATA_TABLES.put(new MultiKey(IN_2020, MESSAGE.FRAME2), "s3m_data_load_frame_2_2020");
            DATA_TABLES.put(new MultiKey(IN_2021, MESSAGE.FRAME2), "s3m_data_load_frame_2_2021");
            DATA_TABLES.put(new MultiKey(IN_2022, MESSAGE.FRAME2), "s3m_data_load_frame_2_2022");
            DATA_TABLES.put(new MultiKey(IN_2024, MESSAGE.FRAME2), "s3m_data_load_frame_2_2023");
            DATA_TABLES.put(new MultiKey(IN_2023, MESSAGE.FRAME2), "s3m_data_load_frame_2_2024");
            DATA_TABLES.put(new MultiKey(IN_2025, MESSAGE.FRAME2), "s3m_data_load_frame_2_2025");
            DATA_TABLES.put(new MultiKey(IN_2026, MESSAGE.FRAME2), "s3m_data_load_frame_2_2026");
            DATA_TABLES.put(new MultiKey(IN_2027, MESSAGE.FRAME2), "s3m_data_load_frame_2_2027");
            DATA_TABLES.put(new MultiKey(IN_2028, MESSAGE.FRAME2), "s3m_data_load_frame_2_2028");
            DATA_TABLES.put(new MultiKey(IN_2029, MESSAGE.FRAME2), "s3m_data_load_frame_2_2029");
            DATA_TABLES.put(new MultiKey(IN_2030, MESSAGE.FRAME2), "s3m_data_load_frame_2_2030");
            DATA_TABLES.put(new MultiKey(IN_2031, MESSAGE.FRAME2), "s3m_data_load_frame_2_2031");
            DATA_TABLES.put(new MultiKey(IN_2032, MESSAGE.FRAME2), "s3m_data_load_frame_2_2032");
            DATA_TABLES.put(new MultiKey(IN_2033, MESSAGE.FRAME2), "s3m_data_load_frame_2_2033");
            DATA_TABLES.put(new MultiKey(IN_2034, MESSAGE.FRAME2), "s3m_data_load_frame_2_2034");
            DATA_TABLES.put(new MultiKey(IN_2035, MESSAGE.FRAME2), "s3m_data_load_frame_2_2035");
            DATA_TABLES.put(new MultiKey(IN_2036, MESSAGE.FRAME2), "s3m_data_load_frame_2_2036");
            DATA_TABLES.put(new MultiKey(IN_2037, MESSAGE.FRAME2), "s3m_data_load_frame_2_2037");
            DATA_TABLES.put(new MultiKey(IN_2038, MESSAGE.FRAME2), "s3m_data_load_frame_2_2038");
            DATA_TABLES.put(new MultiKey(IN_2039, MESSAGE.FRAME2), "s3m_data_load_frame_2_2039");
            DATA_TABLES.put(new MultiKey(IN_2040, MESSAGE.FRAME2), "s3m_data_load_frame_2_2040");
            DATA_TABLES.put(new MultiKey(IN_2041, MESSAGE.FRAME2), "s3m_data_load_frame_2_2041");
            DATA_TABLES.put(new MultiKey(IN_2042, MESSAGE.FRAME2), "s3m_data_load_frame_2_2042");
            DATA_TABLES.put(new MultiKey(IN_2043, MESSAGE.FRAME2), "s3m_data_load_frame_2_2043");
            DATA_TABLES.put(new MultiKey(IN_2044, MESSAGE.FRAME2), "s3m_data_load_frame_2_2044");
            DATA_TABLES.put(new MultiKey(IN_2045, MESSAGE.FRAME2), "s3m_data_load_frame_2_2045");
            DATA_TABLES.put(new MultiKey(IN_2046, MESSAGE.FRAME2), "s3m_data_load_frame_2_2046");
            DATA_TABLES.put(new MultiKey(IN_2047, MESSAGE.FRAME2), "s3m_data_load_frame_2_2047");
            DATA_TABLES.put(new MultiKey(IN_2048, MESSAGE.FRAME2), "s3m_data_load_frame_2_2048");
            DATA_TABLES.put(new MultiKey(IN_2049, MESSAGE.FRAME2), "s3m_data_load_frame_2_2049");
            DATA_TABLES.put(new MultiKey(IN_2050, MESSAGE.FRAME2), "s3m_data_load_frame_2_2050");
            DATA_TABLES.put(new MultiKey(IN_2051, MESSAGE.FRAME2), "s3m_data_load_frame_2_2051");
            DATA_TABLES.put(new MultiKey(IN_2052, MESSAGE.FRAME2), "s3m_data_load_frame_2_2052");
            DATA_TABLES.put(new MultiKey(IN_2053, MESSAGE.FRAME2), "s3m_data_load_frame_2_2053");
            DATA_TABLES.put(new MultiKey(IN_2054, MESSAGE.FRAME2), "s3m_data_load_frame_2_2054");
            DATA_TABLES.put(new MultiKey(IN_2055, MESSAGE.FRAME2), "s3m_data_load_frame_2_2055");
            DATA_TABLES.put(new MultiKey(IN_2056, MESSAGE.FRAME2), "s3m_data_load_frame_2_2056");
            DATA_TABLES.put(new MultiKey(IN_2057, MESSAGE.FRAME2), "s3m_data_load_frame_2_2057");
            DATA_TABLES.put(new MultiKey(IN_2058, MESSAGE.FRAME2), "s3m_data_load_frame_2_2058");
            DATA_TABLES.put(new MultiKey(IN_2059, MESSAGE.FRAME2), "s3m_data_load_frame_2_2059");
            DATA_TABLES.put(new MultiKey(IN_2060, MESSAGE.FRAME2), "s3m_data_load_frame_2_2060");

            DATA_TABLES.put(new MultiKey(IN_2020, MESSAGE.INVERTER1), "s3m_data_inverter_1_2020");
            DATA_TABLES.put(new MultiKey(IN_2021, MESSAGE.INVERTER1), "s3m_data_inverter_1_2021");
            DATA_TABLES.put(new MultiKey(IN_2022, MESSAGE.INVERTER1), "s3m_data_inverter_1_2022");
            DATA_TABLES.put(new MultiKey(IN_2023, MESSAGE.INVERTER1), "s3m_data_inverter_1_2023");
            DATA_TABLES.put(new MultiKey(IN_2024, MESSAGE.INVERTER1), "s3m_data_inverter_1_2024");
            DATA_TABLES.put(new MultiKey(IN_2025, MESSAGE.INVERTER1), "s3m_data_inverter_1_2025");
            DATA_TABLES.put(new MultiKey(IN_2026, MESSAGE.INVERTER1), "s3m_data_inverter_1_2026");
            DATA_TABLES.put(new MultiKey(IN_2027, MESSAGE.INVERTER1), "s3m_data_inverter_1_2027");
            DATA_TABLES.put(new MultiKey(IN_2028, MESSAGE.INVERTER1), "s3m_data_inverter_1_2028");
            DATA_TABLES.put(new MultiKey(IN_2029, MESSAGE.INVERTER1), "s3m_data_inverter_1_2029");
            DATA_TABLES.put(new MultiKey(IN_2030, MESSAGE.INVERTER1), "s3m_data_inverter_1_2030");
            DATA_TABLES.put(new MultiKey(IN_2031, MESSAGE.INVERTER1), "s3m_data_inverter_1_2031");
            DATA_TABLES.put(new MultiKey(IN_2032, MESSAGE.INVERTER1), "s3m_data_inverter_1_2032");
            DATA_TABLES.put(new MultiKey(IN_2033, MESSAGE.INVERTER1), "s3m_data_inverter_1_2033");
            DATA_TABLES.put(new MultiKey(IN_2034, MESSAGE.INVERTER1), "s3m_data_inverter_1_2034");
            DATA_TABLES.put(new MultiKey(IN_2035, MESSAGE.INVERTER1), "s3m_data_inverter_1_2035");
            DATA_TABLES.put(new MultiKey(IN_2036, MESSAGE.INVERTER1), "s3m_data_inverter_1_2036");
            DATA_TABLES.put(new MultiKey(IN_2037, MESSAGE.INVERTER1), "s3m_data_inverter_1_2037");
            DATA_TABLES.put(new MultiKey(IN_2038, MESSAGE.INVERTER1), "s3m_data_inverter_1_2038");
            DATA_TABLES.put(new MultiKey(IN_2039, MESSAGE.INVERTER1), "s3m_data_inverter_1_2039");
            DATA_TABLES.put(new MultiKey(IN_2040, MESSAGE.INVERTER1), "s3m_data_inverter_1_2040");
            DATA_TABLES.put(new MultiKey(IN_2041, MESSAGE.INVERTER1), "s3m_data_inverter_1_2041");
            DATA_TABLES.put(new MultiKey(IN_2042, MESSAGE.INVERTER1), "s3m_data_inverter_1_2042");
            DATA_TABLES.put(new MultiKey(IN_2043, MESSAGE.INVERTER1), "s3m_data_inverter_1_2043");
            DATA_TABLES.put(new MultiKey(IN_2044, MESSAGE.INVERTER1), "s3m_data_inverter_1_2044");
            DATA_TABLES.put(new MultiKey(IN_2045, MESSAGE.INVERTER1), "s3m_data_inverter_1_2045");
            DATA_TABLES.put(new MultiKey(IN_2046, MESSAGE.INVERTER1), "s3m_data_inverter_1_2046");
            DATA_TABLES.put(new MultiKey(IN_2047, MESSAGE.INVERTER1), "s3m_data_inverter_1_2047");
            DATA_TABLES.put(new MultiKey(IN_2048, MESSAGE.INVERTER1), "s3m_data_inverter_1_2048");
            DATA_TABLES.put(new MultiKey(IN_2049, MESSAGE.INVERTER1), "s3m_data_inverter_1_2049");
            DATA_TABLES.put(new MultiKey(IN_2050, MESSAGE.INVERTER1), "s3m_data_inverter_1_2050");
            DATA_TABLES.put(new MultiKey(IN_2051, MESSAGE.INVERTER1), "s3m_data_inverter_1_2051");
            DATA_TABLES.put(new MultiKey(IN_2052, MESSAGE.INVERTER1), "s3m_data_inverter_1_2052");
            DATA_TABLES.put(new MultiKey(IN_2053, MESSAGE.INVERTER1), "s3m_data_inverter_1_2053");
            DATA_TABLES.put(new MultiKey(IN_2054, MESSAGE.INVERTER1), "s3m_data_inverter_1_2054");
            DATA_TABLES.put(new MultiKey(IN_2055, MESSAGE.INVERTER1), "s3m_data_inverter_1_2055");
            DATA_TABLES.put(new MultiKey(IN_2056, MESSAGE.INVERTER1), "s3m_data_inverter_1_2056");
            DATA_TABLES.put(new MultiKey(IN_2057, MESSAGE.INVERTER1), "s3m_data_inverter_1_2057");
            DATA_TABLES.put(new MultiKey(IN_2058, MESSAGE.INVERTER1), "s3m_data_inverter_1_2058");
            DATA_TABLES.put(new MultiKey(IN_2059, MESSAGE.INVERTER1), "s3m_data_inverter_1_2059");
            DATA_TABLES.put(new MultiKey(IN_2060, MESSAGE.INVERTER1), "s3m_data_inverter_1_2060");

            DATA_TABLES.put(new MultiKey(IN_2020, MESSAGE.WEATHER1), "s3m_data_weather_1_2020");
            DATA_TABLES.put(new MultiKey(IN_2021, MESSAGE.WEATHER1), "s3m_data_weather_1_2021");
            DATA_TABLES.put(new MultiKey(IN_2022, MESSAGE.WEATHER1), "s3m_data_weather_1_2022");
            DATA_TABLES.put(new MultiKey(IN_2023, MESSAGE.WEATHER1), "s3m_data_weather_1_2023");
            DATA_TABLES.put(new MultiKey(IN_2024, MESSAGE.WEATHER1), "s3m_data_weather_1_2024");
            DATA_TABLES.put(new MultiKey(IN_2025, MESSAGE.WEATHER1), "s3m_data_weather_1_2025");
            DATA_TABLES.put(new MultiKey(IN_2026, MESSAGE.WEATHER1), "s3m_data_weather_1_2026");
            DATA_TABLES.put(new MultiKey(IN_2027, MESSAGE.WEATHER1), "s3m_data_weather_1_2027");
            DATA_TABLES.put(new MultiKey(IN_2028, MESSAGE.WEATHER1), "s3m_data_weather_1_2028");
            DATA_TABLES.put(new MultiKey(IN_2029, MESSAGE.WEATHER1), "s3m_data_weather_1_2029");
            DATA_TABLES.put(new MultiKey(IN_2030, MESSAGE.WEATHER1), "s3m_data_weather_1_2030");
            DATA_TABLES.put(new MultiKey(IN_2031, MESSAGE.WEATHER1), "s3m_data_weather_1_2031");
            DATA_TABLES.put(new MultiKey(IN_2032, MESSAGE.WEATHER1), "s3m_data_weather_1_2032");
            DATA_TABLES.put(new MultiKey(IN_2033, MESSAGE.WEATHER1), "s3m_data_weather_1_2033");
            DATA_TABLES.put(new MultiKey(IN_2034, MESSAGE.WEATHER1), "s3m_data_weather_1_2034");
            DATA_TABLES.put(new MultiKey(IN_2035, MESSAGE.WEATHER1), "s3m_data_weather_1_2035");
            DATA_TABLES.put(new MultiKey(IN_2036, MESSAGE.WEATHER1), "s3m_data_weather_1_2036");
            DATA_TABLES.put(new MultiKey(IN_2037, MESSAGE.WEATHER1), "s3m_data_weather_1_2037");
            DATA_TABLES.put(new MultiKey(IN_2038, MESSAGE.WEATHER1), "s3m_data_weather_1_2038");
            DATA_TABLES.put(new MultiKey(IN_2039, MESSAGE.WEATHER1), "s3m_data_weather_1_2039");
            DATA_TABLES.put(new MultiKey(IN_2040, MESSAGE.WEATHER1), "s3m_data_weather_1_2040");
            DATA_TABLES.put(new MultiKey(IN_2041, MESSAGE.WEATHER1), "s3m_data_weather_1_2041");
            DATA_TABLES.put(new MultiKey(IN_2042, MESSAGE.WEATHER1), "s3m_data_weather_1_2042");
            DATA_TABLES.put(new MultiKey(IN_2043, MESSAGE.WEATHER1), "s3m_data_weather_1_2043");
            DATA_TABLES.put(new MultiKey(IN_2044, MESSAGE.WEATHER1), "s3m_data_weather_1_2044");
            DATA_TABLES.put(new MultiKey(IN_2045, MESSAGE.WEATHER1), "s3m_data_weather_1_2045");
            DATA_TABLES.put(new MultiKey(IN_2046, MESSAGE.WEATHER1), "s3m_data_weather_1_2046");
            DATA_TABLES.put(new MultiKey(IN_2047, MESSAGE.WEATHER1), "s3m_data_weather_1_2047");
            DATA_TABLES.put(new MultiKey(IN_2048, MESSAGE.WEATHER1), "s3m_data_weather_1_2048");
            DATA_TABLES.put(new MultiKey(IN_2049, MESSAGE.WEATHER1), "s3m_data_weather_1_2049");
            DATA_TABLES.put(new MultiKey(IN_2050, MESSAGE.WEATHER1), "s3m_data_weather_1_2050");
            DATA_TABLES.put(new MultiKey(IN_2051, MESSAGE.WEATHER1), "s3m_data_weather_1_2051");
            DATA_TABLES.put(new MultiKey(IN_2052, MESSAGE.WEATHER1), "s3m_data_weather_1_2052");
            DATA_TABLES.put(new MultiKey(IN_2053, MESSAGE.WEATHER1), "s3m_data_weather_1_2053");
            DATA_TABLES.put(new MultiKey(IN_2054, MESSAGE.WEATHER1), "s3m_data_weather_1_2054");
            DATA_TABLES.put(new MultiKey(IN_2055, MESSAGE.WEATHER1), "s3m_data_weather_1_2055");
            DATA_TABLES.put(new MultiKey(IN_2056, MESSAGE.WEATHER1), "s3m_data_weather_1_2056");
            DATA_TABLES.put(new MultiKey(IN_2057, MESSAGE.WEATHER1), "s3m_data_weather_1_2057");
            DATA_TABLES.put(new MultiKey(IN_2058, MESSAGE.WEATHER1), "s3m_data_weather_1_2058");
            DATA_TABLES.put(new MultiKey(IN_2059, MESSAGE.WEATHER1), "s3m_data_weather_1_2059");
            DATA_TABLES.put(new MultiKey(IN_2060, MESSAGE.WEATHER1), "s3m_data_weather_1_2060");

            DATA_TABLES.put(new MultiKey(IN_2020, MESSAGE.COMBINER1), "s3m_data_combiner_1_2020");
            DATA_TABLES.put(new MultiKey(IN_2021, MESSAGE.COMBINER1), "s3m_data_combiner_1_2021");
            DATA_TABLES.put(new MultiKey(IN_2022, MESSAGE.COMBINER1), "s3m_data_combiner_1_2022");
            DATA_TABLES.put(new MultiKey(IN_2023, MESSAGE.COMBINER1), "s3m_data_combiner_1_2023");
            DATA_TABLES.put(new MultiKey(IN_2024, MESSAGE.COMBINER1), "s3m_data_combiner_1_2024");
            DATA_TABLES.put(new MultiKey(IN_2025, MESSAGE.COMBINER1), "s3m_data_combiner_1_2025");
            DATA_TABLES.put(new MultiKey(IN_2026, MESSAGE.COMBINER1), "s3m_data_combiner_1_2026");
            DATA_TABLES.put(new MultiKey(IN_2027, MESSAGE.COMBINER1), "s3m_data_combiner_1_2027");
            DATA_TABLES.put(new MultiKey(IN_2028, MESSAGE.COMBINER1), "s3m_data_combiner_1_2028");
            DATA_TABLES.put(new MultiKey(IN_2029, MESSAGE.COMBINER1), "s3m_data_combiner_1_2029");
            DATA_TABLES.put(new MultiKey(IN_2030, MESSAGE.COMBINER1), "s3m_data_combiner_1_2030");
            DATA_TABLES.put(new MultiKey(IN_2031, MESSAGE.COMBINER1), "s3m_data_combiner_1_2031");
            DATA_TABLES.put(new MultiKey(IN_2032, MESSAGE.COMBINER1), "s3m_data_combiner_1_2032");
            DATA_TABLES.put(new MultiKey(IN_2033, MESSAGE.COMBINER1), "s3m_data_combiner_1_2033");
            DATA_TABLES.put(new MultiKey(IN_2034, MESSAGE.COMBINER1), "s3m_data_combiner_1_2034");
            DATA_TABLES.put(new MultiKey(IN_2035, MESSAGE.COMBINER1), "s3m_data_combiner_1_2035");
            DATA_TABLES.put(new MultiKey(IN_2036, MESSAGE.COMBINER1), "s3m_data_combiner_1_2036");
            DATA_TABLES.put(new MultiKey(IN_2037, MESSAGE.COMBINER1), "s3m_data_combiner_1_2037");
            DATA_TABLES.put(new MultiKey(IN_2038, MESSAGE.COMBINER1), "s3m_data_combiner_1_2038");
            DATA_TABLES.put(new MultiKey(IN_2039, MESSAGE.COMBINER1), "s3m_data_combiner_1_2039");
            DATA_TABLES.put(new MultiKey(IN_2040, MESSAGE.COMBINER1), "s3m_data_combiner_1_2040");
            DATA_TABLES.put(new MultiKey(IN_2041, MESSAGE.COMBINER1), "s3m_data_combiner_1_2041");
            DATA_TABLES.put(new MultiKey(IN_2042, MESSAGE.COMBINER1), "s3m_data_combiner_1_2042");
            DATA_TABLES.put(new MultiKey(IN_2043, MESSAGE.COMBINER1), "s3m_data_combiner_1_2043");
            DATA_TABLES.put(new MultiKey(IN_2044, MESSAGE.COMBINER1), "s3m_data_combiner_1_2044");
            DATA_TABLES.put(new MultiKey(IN_2045, MESSAGE.COMBINER1), "s3m_data_combiner_1_2045");
            DATA_TABLES.put(new MultiKey(IN_2046, MESSAGE.COMBINER1), "s3m_data_combiner_1_2046");
            DATA_TABLES.put(new MultiKey(IN_2047, MESSAGE.COMBINER1), "s3m_data_combiner_1_2047");
            DATA_TABLES.put(new MultiKey(IN_2048, MESSAGE.COMBINER1), "s3m_data_combiner_1_2048");
            DATA_TABLES.put(new MultiKey(IN_2049, MESSAGE.COMBINER1), "s3m_data_combiner_1_2049");
            DATA_TABLES.put(new MultiKey(IN_2050, MESSAGE.COMBINER1), "s3m_data_combiner_1_2050");
            DATA_TABLES.put(new MultiKey(IN_2051, MESSAGE.COMBINER1), "s3m_data_combiner_1_2051");
            DATA_TABLES.put(new MultiKey(IN_2052, MESSAGE.COMBINER1), "s3m_data_combiner_1_2052");
            DATA_TABLES.put(new MultiKey(IN_2053, MESSAGE.COMBINER1), "s3m_data_combiner_1_2053");
            DATA_TABLES.put(new MultiKey(IN_2054, MESSAGE.COMBINER1), "s3m_data_combiner_1_2054");
            DATA_TABLES.put(new MultiKey(IN_2055, MESSAGE.COMBINER1), "s3m_data_combiner_1_2055");
            DATA_TABLES.put(new MultiKey(IN_2056, MESSAGE.COMBINER1), "s3m_data_combiner_1_2056");
            DATA_TABLES.put(new MultiKey(IN_2057, MESSAGE.COMBINER1), "s3m_data_combiner_1_2057");
            DATA_TABLES.put(new MultiKey(IN_2058, MESSAGE.COMBINER1), "s3m_data_combiner_1_2058");
            DATA_TABLES.put(new MultiKey(IN_2059, MESSAGE.COMBINER1), "s3m_data_combiner_1_2059");
            DATA_TABLES.put(new MultiKey(IN_2060, MESSAGE.COMBINER1), "s3m_data_combiner_1_2060");

            DATA_TABLES.put(new MultiKey(IN_2020, MESSAGE.STRING1), "s3m_data_string_1_2020");
            DATA_TABLES.put(new MultiKey(IN_2021, MESSAGE.STRING1), "s3m_data_string_1_2021");
            DATA_TABLES.put(new MultiKey(IN_2022, MESSAGE.STRING1), "s3m_data_string_1_2022");
            DATA_TABLES.put(new MultiKey(IN_2023, MESSAGE.STRING1), "s3m_data_string_1_2023");
            DATA_TABLES.put(new MultiKey(IN_2024, MESSAGE.STRING1), "s3m_data_string_1_2024");
            DATA_TABLES.put(new MultiKey(IN_2025, MESSAGE.STRING1), "s3m_data_string_1_2025");
            DATA_TABLES.put(new MultiKey(IN_2026, MESSAGE.STRING1), "s3m_data_string_1_2026");
            DATA_TABLES.put(new MultiKey(IN_2027, MESSAGE.STRING1), "s3m_data_string_1_2027");
            DATA_TABLES.put(new MultiKey(IN_2028, MESSAGE.STRING1), "s3m_data_string_1_2028");
            DATA_TABLES.put(new MultiKey(IN_2029, MESSAGE.STRING1), "s3m_data_string_1_2029");
            DATA_TABLES.put(new MultiKey(IN_2030, MESSAGE.STRING1), "s3m_data_string_1_2030");
            DATA_TABLES.put(new MultiKey(IN_2031, MESSAGE.STRING1), "s3m_data_string_1_2031");
            DATA_TABLES.put(new MultiKey(IN_2032, MESSAGE.STRING1), "s3m_data_string_1_2032");
            DATA_TABLES.put(new MultiKey(IN_2033, MESSAGE.STRING1), "s3m_data_string_1_2033");
            DATA_TABLES.put(new MultiKey(IN_2034, MESSAGE.STRING1), "s3m_data_string_1_2034");
            DATA_TABLES.put(new MultiKey(IN_2035, MESSAGE.STRING1), "s3m_data_string_1_2035");
            DATA_TABLES.put(new MultiKey(IN_2036, MESSAGE.STRING1), "s3m_data_string_1_2036");
            DATA_TABLES.put(new MultiKey(IN_2037, MESSAGE.STRING1), "s3m_data_string_1_2037");
            DATA_TABLES.put(new MultiKey(IN_2038, MESSAGE.STRING1), "s3m_data_string_1_2038");
            DATA_TABLES.put(new MultiKey(IN_2039, MESSAGE.STRING1), "s3m_data_string_1_2039");
            DATA_TABLES.put(new MultiKey(IN_2040, MESSAGE.STRING1), "s3m_data_string_1_2040");
            DATA_TABLES.put(new MultiKey(IN_2041, MESSAGE.STRING1), "s3m_data_string_1_2041");
            DATA_TABLES.put(new MultiKey(IN_2042, MESSAGE.STRING1), "s3m_data_string_1_2042");
            DATA_TABLES.put(new MultiKey(IN_2043, MESSAGE.STRING1), "s3m_data_string_1_2043");
            DATA_TABLES.put(new MultiKey(IN_2044, MESSAGE.STRING1), "s3m_data_string_1_2044");
            DATA_TABLES.put(new MultiKey(IN_2045, MESSAGE.STRING1), "s3m_data_string_1_2045");
            DATA_TABLES.put(new MultiKey(IN_2046, MESSAGE.STRING1), "s3m_data_string_1_2046");
            DATA_TABLES.put(new MultiKey(IN_2047, MESSAGE.STRING1), "s3m_data_string_1_2047");
            DATA_TABLES.put(new MultiKey(IN_2048, MESSAGE.STRING1), "s3m_data_string_1_2048");
            DATA_TABLES.put(new MultiKey(IN_2049, MESSAGE.STRING1), "s3m_data_string_1_2049");
            DATA_TABLES.put(new MultiKey(IN_2050, MESSAGE.STRING1), "s3m_data_string_1_2050");
            DATA_TABLES.put(new MultiKey(IN_2051, MESSAGE.STRING1), "s3m_data_string_1_2051");
            DATA_TABLES.put(new MultiKey(IN_2052, MESSAGE.STRING1), "s3m_data_string_1_2052");
            DATA_TABLES.put(new MultiKey(IN_2053, MESSAGE.STRING1), "s3m_data_string_1_2053");
            DATA_TABLES.put(new MultiKey(IN_2054, MESSAGE.STRING1), "s3m_data_string_1_2054");
            DATA_TABLES.put(new MultiKey(IN_2055, MESSAGE.STRING1), "s3m_data_string_1_2055");
            DATA_TABLES.put(new MultiKey(IN_2056, MESSAGE.STRING1), "s3m_data_string_1_2056");
            DATA_TABLES.put(new MultiKey(IN_2057, MESSAGE.STRING1), "s3m_data_string_1_2057");
            DATA_TABLES.put(new MultiKey(IN_2058, MESSAGE.STRING1), "s3m_data_string_1_2058");
            DATA_TABLES.put(new MultiKey(IN_2059, MESSAGE.STRING1), "s3m_data_string_1_2059");
            DATA_TABLES.put(new MultiKey(IN_2060, MESSAGE.STRING1), "s3m_data_string_1_2060");

            DATA_TABLES.put(new MultiKey(IN_2020, MESSAGE.PANEL1), "s3m_data_panel_1_2020");
            DATA_TABLES.put(new MultiKey(IN_2021, MESSAGE.PANEL1), "s3m_data_panel_1_2021");
            DATA_TABLES.put(new MultiKey(IN_2022, MESSAGE.PANEL1), "s3m_data_panel_1_2022");
            DATA_TABLES.put(new MultiKey(IN_2023, MESSAGE.PANEL1), "s3m_data_panel_1_2023");
            DATA_TABLES.put(new MultiKey(IN_2024, MESSAGE.PANEL1), "s3m_data_panel_1_2024");
            DATA_TABLES.put(new MultiKey(IN_2025, MESSAGE.PANEL1), "s3m_data_panel_1_2025");
            DATA_TABLES.put(new MultiKey(IN_2026, MESSAGE.PANEL1), "s3m_data_panel_1_2026");
            DATA_TABLES.put(new MultiKey(IN_2027, MESSAGE.PANEL1), "s3m_data_panel_1_2027");
            DATA_TABLES.put(new MultiKey(IN_2028, MESSAGE.PANEL1), "s3m_data_panel_1_2028");
            DATA_TABLES.put(new MultiKey(IN_2029, MESSAGE.PANEL1), "s3m_data_panel_1_2029");
            DATA_TABLES.put(new MultiKey(IN_2030, MESSAGE.PANEL1), "s3m_data_panel_1_2030");
            DATA_TABLES.put(new MultiKey(IN_2031, MESSAGE.PANEL1), "s3m_data_panel_1_2031");
            DATA_TABLES.put(new MultiKey(IN_2032, MESSAGE.PANEL1), "s3m_data_panel_1_2032");
            DATA_TABLES.put(new MultiKey(IN_2033, MESSAGE.PANEL1), "s3m_data_panel_1_2033");
            DATA_TABLES.put(new MultiKey(IN_2034, MESSAGE.PANEL1), "s3m_data_panel_1_2034");
            DATA_TABLES.put(new MultiKey(IN_2035, MESSAGE.PANEL1), "s3m_data_panel_1_2035");
            DATA_TABLES.put(new MultiKey(IN_2036, MESSAGE.PANEL1), "s3m_data_panel_1_2036");
            DATA_TABLES.put(new MultiKey(IN_2037, MESSAGE.PANEL1), "s3m_data_panel_1_2037");
            DATA_TABLES.put(new MultiKey(IN_2038, MESSAGE.PANEL1), "s3m_data_panel_1_2038");
            DATA_TABLES.put(new MultiKey(IN_2039, MESSAGE.PANEL1), "s3m_data_panel_1_2039");
            DATA_TABLES.put(new MultiKey(IN_2040, MESSAGE.PANEL1), "s3m_data_panel_1_2040");
            DATA_TABLES.put(new MultiKey(IN_2041, MESSAGE.PANEL1), "s3m_data_panel_1_2041");
            DATA_TABLES.put(new MultiKey(IN_2042, MESSAGE.PANEL1), "s3m_data_panel_1_2042");
            DATA_TABLES.put(new MultiKey(IN_2043, MESSAGE.PANEL1), "s3m_data_panel_1_2043");
            DATA_TABLES.put(new MultiKey(IN_2044, MESSAGE.PANEL1), "s3m_data_panel_1_2044");
            DATA_TABLES.put(new MultiKey(IN_2045, MESSAGE.PANEL1), "s3m_data_panel_1_2045");
            DATA_TABLES.put(new MultiKey(IN_2046, MESSAGE.PANEL1), "s3m_data_panel_1_2046");
            DATA_TABLES.put(new MultiKey(IN_2047, MESSAGE.PANEL1), "s3m_data_panel_1_2047");
            DATA_TABLES.put(new MultiKey(IN_2048, MESSAGE.PANEL1), "s3m_data_panel_1_2048");
            DATA_TABLES.put(new MultiKey(IN_2049, MESSAGE.PANEL1), "s3m_data_panel_1_2049");
            DATA_TABLES.put(new MultiKey(IN_2050, MESSAGE.PANEL1), "s3m_data_panel_1_2050");
            DATA_TABLES.put(new MultiKey(IN_2051, MESSAGE.PANEL1), "s3m_data_panel_1_2051");
            DATA_TABLES.put(new MultiKey(IN_2052, MESSAGE.PANEL1), "s3m_data_panel_1_2052");
            DATA_TABLES.put(new MultiKey(IN_2053, MESSAGE.PANEL1), "s3m_data_panel_1_2053");
            DATA_TABLES.put(new MultiKey(IN_2054, MESSAGE.PANEL1), "s3m_data_panel_1_2054");
            DATA_TABLES.put(new MultiKey(IN_2055, MESSAGE.PANEL1), "s3m_data_panel_1_2055");
            DATA_TABLES.put(new MultiKey(IN_2056, MESSAGE.PANEL1), "s3m_data_panel_1_2056");
            DATA_TABLES.put(new MultiKey(IN_2057, MESSAGE.PANEL1), "s3m_data_panel_1_2057");
            DATA_TABLES.put(new MultiKey(IN_2058, MESSAGE.PANEL1), "s3m_data_panel_1_2058");
            DATA_TABLES.put(new MultiKey(IN_2059, MESSAGE.PANEL1), "s3m_data_panel_1_2059");
            DATA_TABLES.put(new MultiKey(IN_2060, MESSAGE.PANEL1), "s3m_data_panel_1_2060");

            DATA_TABLES.put(new MultiKey(IN_2020, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2020");
            DATA_TABLES.put(new MultiKey(IN_2021, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2021");
            DATA_TABLES.put(new MultiKey(IN_2022, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2022");
            DATA_TABLES.put(new MultiKey(IN_2023, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2023");
            DATA_TABLES.put(new MultiKey(IN_2024, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2024");
            DATA_TABLES.put(new MultiKey(IN_2025, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2025");
            DATA_TABLES.put(new MultiKey(IN_2026, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2026");
            DATA_TABLES.put(new MultiKey(IN_2027, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2027");
            DATA_TABLES.put(new MultiKey(IN_2028, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2028");
            DATA_TABLES.put(new MultiKey(IN_2029, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2029");
            DATA_TABLES.put(new MultiKey(IN_2030, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2030");
            DATA_TABLES.put(new MultiKey(IN_2031, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2031");
            DATA_TABLES.put(new MultiKey(IN_2032, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2032");
            DATA_TABLES.put(new MultiKey(IN_2033, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2034");
            DATA_TABLES.put(new MultiKey(IN_2034, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2035");
            DATA_TABLES.put(new MultiKey(IN_2035, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2036");
            DATA_TABLES.put(new MultiKey(IN_2036, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2037");
            DATA_TABLES.put(new MultiKey(IN_2037, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2038");
            DATA_TABLES.put(new MultiKey(IN_2038, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2039");
            DATA_TABLES.put(new MultiKey(IN_2039, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2040");
            DATA_TABLES.put(new MultiKey(IN_2040, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2041");
            DATA_TABLES.put(new MultiKey(IN_2041, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2042");
            DATA_TABLES.put(new MultiKey(IN_2042, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2043");
            DATA_TABLES.put(new MultiKey(IN_2043, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2044");
            DATA_TABLES.put(new MultiKey(IN_2044, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2045");
            DATA_TABLES.put(new MultiKey(IN_2045, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2046");
            DATA_TABLES.put(new MultiKey(IN_2046, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2047");
            DATA_TABLES.put(new MultiKey(IN_2047, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2048");
            DATA_TABLES.put(new MultiKey(IN_2048, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2049");
            DATA_TABLES.put(new MultiKey(IN_2049, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2050");
            DATA_TABLES.put(new MultiKey(IN_2050, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2051");
            DATA_TABLES.put(new MultiKey(IN_2051, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2052");
            DATA_TABLES.put(new MultiKey(IN_2052, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2053");
            DATA_TABLES.put(new MultiKey(IN_2053, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2054");
            DATA_TABLES.put(new MultiKey(IN_2054, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2055");
            DATA_TABLES.put(new MultiKey(IN_2055, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2056");
            DATA_TABLES.put(new MultiKey(IN_2056, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2057");
            DATA_TABLES.put(new MultiKey(IN_2057, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2058");
            DATA_TABLES.put(new MultiKey(IN_2058, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2059");
            DATA_TABLES.put(new MultiKey(IN_2059, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2060");
            DATA_TABLES.put(new MultiKey(IN_2060, MESSAGE.GRID_RMU_DRAWER1), "s3m_data_rmu_drawer_1_2023");

        }

        public static long[] times = {IN_2021.getTime() / 1000, IN_2022.getTime() / 1000, IN_2023.getTime() / 1000};

    }

    public static class ModuleStatus {
        public static final String WARNING = "warning";
        public static final String IN_ACTIVE = "inactive";
        public static final String ACTIVE = "active";
        public static final String OFFLINE = "offline";
        public static final String OFF = "off";
        public static final String ON = "on";
    }

    public static final class settingCostEnergy {
        // mã setting_mst load
        public static final Integer LOAD_LOW_COST_IN = 49;
        public static final Integer LOAD_MEDIUM_COST_IN = 50;
        public static final Integer LOAD_HIGH_COST_IN = 51;

        // mã setting_mst solar
        public static final Integer SOLAR_LOW_COST_OUT = 52;
        public static final Integer SOLAR_MEDIUM_COST_OUT = 53;
        public static final Integer SOLAR_HIGH_COST_OUT = 54;

        // mã setting_mst grid
        public static final Integer GRID_LOW_COST_IN = 55;
        public static final Integer GRID_MEDIUM_COST_IN = 56;
        public static final Integer GRID_HIGH_COST_IN = 57;

        public static final Integer GRID_LOW_COST_OUT = 58;
        public static final Integer GRID_MEDIUM_COST_OUT = 59;
        public static final Integer GRID_HIGH_COST_OUT = 60;
    }

    public static class System_type {
        public static final Integer LOAD = 1;
        public static final Integer SOLAR = 2;
        public static final Integer BATTERY = 3;
        public static final Integer WIND = 4;
        public static final Integer GRID = 5;
    }
    
    public static class ANOTHER_MAP {
    	 public static final Integer TONG_QUAN = 1;
    	 public static final Integer TRUYEN_THONG = 2;
    	 public static final Integer MAT_BANG = 3;
    	 public static final Integer DIEU_KHIEN = 4;
    	}

}
