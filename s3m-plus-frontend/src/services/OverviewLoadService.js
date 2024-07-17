import OVERVIEW_LOAD_API from "../constants/overview_load";
import authService from "./AuthService";
import commonService from "./CommonService";
import CONS from './../constants/constant';

class OverviewLoadService {
    #type;
    #commonService;
    #authService;

    constructor() {
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
        this.#authService = authService;
    }

    getPower = async (customerId, projectId, keyword) => {
        let url = OVERVIEW_LOAD_API.OVERVIEW.GET_POWER + customerId + "/" + projectId + "?keyword=" + keyword;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    getDataChart = async (customerId, projectId, fromDate, toDate) => {
        let url = OVERVIEW_LOAD_API.OVERVIEW.GET_DATA_CHART + customerId + "/" + projectId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {fromDate: fromDate, toDate: toDate});
    }

    getForecast = async (customerId, projectId, systemTypeId) => {
        let url = OVERVIEW_LOAD_API.OVERVIEW.GET_FORECAST + "/" + customerId + "/" + projectId + "/" + systemTypeId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    getForecasts = async (customerId, projectId, systemTypeId, page) => {
        let url = OVERVIEW_LOAD_API.OVERVIEW.GET_FORECASTS + "/" + customerId + "/" + projectId + "/" + systemTypeId + "/" + page;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    saveForecast = async (customerId, forecast) => {
        let url = OVERVIEW_LOAD_API.OVERVIEW.SAVE_FORECAST + customerId;
        let type = this.#type.POST;
        return this.#commonService.sendRequest(type, url, forecast);
    }

    exportExcel = async (customerId, projectId, fromDate, toDate, userName) => {
        let url = OVERVIEW_LOAD_API.OVERVIEW.EXPORT_EXCEL + customerId + "/" + projectId ;
        let type = this.#type.DOWNLOAD;
        return await this.#commonService.sendRequest(type, url, {fromDate: fromDate, toDate: toDate, userName: userName});
    }
}
export default new OverviewLoadService();