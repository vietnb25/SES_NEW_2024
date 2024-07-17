package vn.ses.s3m.plus.batch.common;

public class Constants {

    public static final Integer YEAR = 1;
    public static final Integer MONTH = 2;
    public static final Integer DAY = 3;
    public static final Integer HOUR = 4;
    public static final Integer MINUTE_15 = 5;
    public static final Integer MINUTE_05 = 6;
    public static final String YEAR_MONTH_DAY_FORMAT = "yyyy-MM-dd";
    public static final String YEAR_MONTH_FORMAT = "yyyy-MM";
    public static final String YEAR_FORMAT = "yyyy";

    public static class ES {

        public static final String DATETIME_FORMAT = "yyyy/MM/dd HH:mm:ss";
        public static final String DATETIME_FORMAT_DMYHM = "dd-MM-yyyy HH:mm";
        public static final String DATETIME_FORMAT_DMYHMS = "dd/MM/yyyy HH:mm:ss";
        public static final String DATETIME_FORMAT_DMYHHMM = "dd/MM/yyyy HH:mm";
        public static final String DATETIME_FORMAT_YMDHMS = "yyyy-MM-dd HH:mm:ss";
        public static final String DATETIME_FORMAT_YMDHM = "yyyy-MM-dd HH:mm";
        public static final String DATETIME_FORMAT_YMDH = "yyyy-MM-dd HH";
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

    }

    public static class Setting {

        public static final Integer GIO_CAO_DIEM = 1;
        public static final Integer GIO_BINH_THUONG = 2;
        public static final Integer GIO_THAP_DIEM = 3;
    }

    public static class System_type {
    	public static final Integer NUMBER_0 = 0;
        public static final Integer LOAD = 1;
        public static final Integer SOLAR = 2;
        public static final Integer BATTERY = 3;
        public static final Integer WIND = 4;
        public static final Integer GRID = 5;

        public static class LOAD_TYPE {
            public static final Integer METER = 1;
            public static final Integer STMV = 2;
            public static final Integer SGMV = 3;
        }

        public static class SOLAR_TYPE {
            public static final Integer INVERTER = 1;
            public static final Integer WEATHER = 2;
            public static final Integer COMBINER = 3;
            public static final Integer STRING = 4;
            public static final Integer PANEL = 5;
        }

        public static class GRID_TYPE {
            public static final Integer RMU = 1;
        }

    }
    
    public static class Device_type {
        public static final Integer METER = 1;
        public static final Integer INVERTER = 2;
        public static final Integer CAM_BIEN_NHIET_DO_DO_AM = 3;
        public static final Integer CAM_BIEN_TRANG_THAI = 4;
        public static final Integer CAM_BIEN_PHONG_DIEN_HTR02 = 5;
        public static final Integer CAM_BIEN_PHONG_DIEN_AMS01 = 6;
        public static final Integer CAM_BIEN_AP_SUAT = 7;
        public static final Integer RELAY = 8;
        public static final Integer GATEWAY = 9;
        public static final Integer CAM_BIEN_LUU_LUONG = 10;
        public static final Integer CAM_BIEN_BUC_XA = 11;
        public static final Integer CAM_BIEN_MUC_NUOC = 12;
        public static final Integer CAM_BIEN_PH = 13;
        public static final Integer CAM_BIEN_TOC_DO_GIO = 14;
        public static final Integer CAM_BIEN_DUNG_LUONG_PIN = 15;
        public static final Integer STRING = 16;
        public static final Integer COMBINER = 17;
        public static final Integer PANEL = 18;
        public static final Integer CAM_BIEN_MUC_NHIEN_LIEU = 19;
    }
}
