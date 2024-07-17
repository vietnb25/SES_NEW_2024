import CONS from "./constant";

const OVERVIEWGRID_HOST = CONS.LOCAL_HOST + "grid";

const OVERVIEW_GRID_API = {
    OVERVIEW : {
        GET_POWER : OVERVIEWGRID_HOST + "/power/",
        GET_DATA_CHART : OVERVIEWGRID_HOST + "/powerTotal/",
        EXPORT_EXCEL : OVERVIEWGRID_HOST + "/powerTotal/download/",
        GET_FORECAST : OVERVIEWGRID_HOST + "/powerTotal/forecast/",
        GET_FORECASTS : OVERVIEWGRID_HOST + "/forecasts/",
        SAVE_FORECAST : OVERVIEWGRID_HOST + "/powerTotal/forecast/save",
        GET_TOTALENERGY: OVERVIEWGRID_HOST + "/energy/",
    }
}
export default OVERVIEW_GRID_API;