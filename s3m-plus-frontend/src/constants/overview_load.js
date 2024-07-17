import CONS from "./constant";

const OVERVIEWLOAD_HOST = CONS.LOCAL_HOST + "load";

const OVERVIEW_LOAD_API = {
    OVERVIEW : {
        GET_POWER : OVERVIEWLOAD_HOST + "/power/",
        GET_DATA_CHART : OVERVIEWLOAD_HOST + "/powerTotal/",
        EXPORT_EXCEL : OVERVIEWLOAD_HOST + "/powerTotal/download/",
        GET_FORECAST : OVERVIEWLOAD_HOST + "/powerTotal/forecast/",
        GET_FORECASTS : OVERVIEWLOAD_HOST + "/forecasts/",
        SAVE_FORECAST : OVERVIEWLOAD_HOST + "/powerTotal/forecast/save/",
    }
}
export default OVERVIEW_LOAD_API;