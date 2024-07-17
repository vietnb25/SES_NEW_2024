import CONS from "./constant";

const OVERVIEWPV_HOST = CONS.LOCAL_HOST + "pv";

const OVERVIEW_PV_API = {
    OVERVIEW : {
        GET_POWER : OVERVIEWPV_HOST + "/power/",
        GET_DATA_CHART : OVERVIEWPV_HOST + "/powerTotal/",
        EXPORT_EXCEL : OVERVIEWPV_HOST + "/powerTotal/download/",
        GET_FORECAST : OVERVIEWPV_HOST + "/powerTotal/forecast/",
        GET_FORECASTS : OVERVIEWPV_HOST + "/forecasts/",
        SAVE_FORECAST : OVERVIEWPV_HOST + "/powerTotal/forecast/save",
        GET_TOTALENERGY: OVERVIEWPV_HOST + "/energy/",
    }
}
export default OVERVIEW_PV_API;