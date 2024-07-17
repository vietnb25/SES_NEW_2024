import CONS from "../constants/constant";
import FORECAST_SOLAR_API from "../constants/forecast_solar_api";
import commonService from "./CommonService";
import authService from "./AuthService";
class ForecastSolarService {
    #type;
    #commonService;
    #authService;
    constructor() {
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
        this.#authService = authService;
    }
    getForecasts = async (customerId, projectId, systemTypeId, page) => {
        let url = FORECAST_SOLAR_API.FORECAST.GET_FORECASTS + customerId + "/" + projectId + "/" + systemTypeId + "/" + page;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }
    getForecast = async (customerId, projectId, systemTypeId) => {
        let url = FORECAST_SOLAR_API.FORECAST.GET_FORECAST + customerId + "/" + projectId + "/" + systemTypeId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }
    saveForecast = async (customerId, forecast) => {
        let url = FORECAST_SOLAR_API.FORECAST.SAVE_FORECAST + customerId + "/";
        let type = this.#type.POST;
        return this.#commonService.sendRequest(type, url, forecast);
    }
}
export default new ForecastSolarService();