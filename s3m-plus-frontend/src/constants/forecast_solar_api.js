import CONS from "./constant";

const FORECAST_SOLAR_HOST=CONS.LOCAL_HOST+"pv";
const FORECAST_SOLAR_API={
    FORECAST:{
        GET_FORECASTS:FORECAST_SOLAR_HOST+"/forecasts/",
        GET_FORECAST:FORECAST_SOLAR_HOST+"/forecast/",
        SAVE_FORECAST:FORECAST_SOLAR_HOST+"/forecast/save/",
    }
}
export default FORECAST_SOLAR_API;