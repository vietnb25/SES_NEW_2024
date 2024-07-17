import CONS from "./constant";

const OPERATION_INFORMATION_HOST = CONS.LOCAL_HOST + "common/operation/";
const OPERATION_PV_HOST = CONS.LOCAL_HOST + "pv/operation/";
const OPERATION_GRID_HOST = CONS.LOCAL_HOST + "grid/operation/";

const BASE_API_OPERATION_INFORMATION = {
    OPERATION_INFORMATION: {

        // LOAD
        /**
         * 1. LOAD
         */
        LIST: OPERATION_INFORMATION_HOST,
        INSTANT: OPERATION_INFORMATION_HOST + "instant/",
        INSTANT_POWER_QUALITY: OPERATION_INFORMATION_HOST + "power-quality/instant/",
        POWER_QUALITIES: OPERATION_INFORMATION_HOST + "power-quality/",
        DOWNLOAD_ELECTRICAL_PARAM: OPERATION_INFORMATION_HOST + "download/electrical-param/",
        DOWNLOAD_TEMPERATURE_PARAM: OPERATION_INFORMATION_HOST + "download/temperature/",
        DOWNLOAD_POWER_QUALITY: OPERATION_INFORMATION_HOST + "download/power-quality/",
        OPERATING_WARNING: OPERATION_INFORMATION_HOST + "operating-warning/",
        UPDATE_OPERATING_WARNING: OPERATION_INFORMATION_HOST + "operating-warning/update",
        DOWNLOAD: OPERATION_INFORMATION_HOST + "operating-warning/download",
        DOWNLOAD_WARNING: OPERATION_INFORMATION_HOST + "operating-warning/downloadWarning",
        CHART: OPERATION_INFORMATION_HOST + "chart/",
        DOWNLOAD_CHART: OPERATION_INFORMATION_HOST + "chart/download/",
        HARMONIC: OPERATION_INFORMATION_HOST + "chart-harmonic/",
        HARMONIC_BY_DAY: OPERATION_INFORMATION_HOST + "chart-harmonic/day/",
        HARMONIC_PERIOD: OPERATION_INFORMATION_HOST + "chart-harmonic/period/",
        DEVICES_BY_PROJECT: OPERATION_INFORMATION_HOST + "devices/",

        // PV

        /**
         * Thông số cài đăt
         */
        SETTING_INVERTER_PV: OPERATION_PV_HOST + "setting/inverter/",

        /**
         * 4.INVERTER
         */
        LIST_INVERTER_PV: OPERATION_PV_HOST + "inverter/",
        INSTANT_INVERTER_PV: OPERATION_PV_HOST + "instant/inverter/",
        CHART_INVERTER: OPERATION_PV_HOST + "chart/inverter/",
        CHART_INVERTER_ELECTRICAL_POWER: OPERATION_PV_HOST + "chart/electrical-power/inverter/",
        DOWNLOAD_DEVICE_PARAMETER_INVERTER_PV: OPERATION_PV_HOST + "download/device-parameter/inverter/",
        DOWNLOAD_CHART_INVERTER_PV: OPERATION_PV_HOST + "download/chart/inverter/",
        DOWNLOAD_CHART_ELECTRICAL_POWER_INVERTER_PV: OPERATION_PV_HOST + "download/chart/inverter/electrical-power/",

        /**
         * 5. WEATHER
         */
        LIST_WEATHER_PV: OPERATION_PV_HOST + "weather/",
        INSTANT_WEATHER_PV: OPERATION_PV_HOST + "instant/weather/",
        CHART_WEATHER_PV: OPERATION_PV_HOST + "chart/weather/",
        DOWNLOAD_DEVICE_PARAMETER_WEATHER_PV: OPERATION_PV_HOST + "download/device-parameter/weather/",
        DOWNLOAD_CHART_WEATHER_PV: OPERATION_PV_HOST + "download/chart/weather/",

        /**
         * 6. COMBINER
         */
        LIST_COMBINER_PV: OPERATION_PV_HOST + "combiner/",
        INSTANT_COMBINER_PV: OPERATION_PV_HOST + "instant/combiner/",
        CHART_COMBINER_PV: OPERATION_PV_HOST + "chart/combiner/",
        DOWNLOAD_DEVICE_PARAMETER_COMBINER_PV: OPERATION_PV_HOST + "download/device-parameter/combiner/",
        DOWNLOAD_CHART_COMBINER_PV: OPERATION_PV_HOST + "download/chart/combiner/",

        /**
         * 7. STRING
         */
        LIST_STRING_PV: OPERATION_PV_HOST + "string/",
        INSTANT_STRING_PV: OPERATION_PV_HOST + "instant/string/",
        CHART_STRING_PV: OPERATION_PV_HOST + "chart/string/",
        DOWNLOAD_DEVICE_PARAMETER_STRING_PV: OPERATION_PV_HOST + "download/device-parameter/string/",
        DOWNLOAD_CHART_STRING_PV: OPERATION_PV_HOST + "download/chart/string/",

        /**
         * 8. PANEL
         */
        LIST_PANEL_PV: OPERATION_PV_HOST + "panel/",
        INSTANT_PANEL_PV: OPERATION_PV_HOST + "instant/panel/",
        CHART_PANEL_PV: OPERATION_PV_HOST + "chart/panel/",
        DOWNLOAD_DEVICE_PARAMETER_PANEL_PV: OPERATION_PV_HOST + "download/device-parameter/panel/",
        DOWNLOAD_CHART_PANEL_PV: OPERATION_PV_HOST + "download/chart/panel/",

        // GRID
        /**
         * 9. RmuDrawer
         */
        LIST_RMU_DRAWER_GRID: OPERATION_GRID_HOST + "rmu-drawer/",
        INSTANT_RMU_DRAWER_GRID: OPERATION_GRID_HOST + "instant/rmu-drawer/",
        DOWNLOAD_DEVICE_PARAMETER_RMU_DRAWER_GRID: OPERATION_GRID_HOST + "download/device-parameter/rmu-drawer/",
        CHART_RMU_DRAWER_GRID: OPERATION_GRID_HOST + "chart/rmu-drawer/",
        CHART_RMU_DRAWER_GRID_ELECTRICAL_POWER: OPERATION_GRID_HOST + "chart/electrical-power/rmu-drawer/",
        DOWNLOAD_CHART_RMU_DRAWER: OPERATION_GRID_HOST + "/chart/download/rmu-drawer/"
    }
}

export default BASE_API_OPERATION_INFORMATION;