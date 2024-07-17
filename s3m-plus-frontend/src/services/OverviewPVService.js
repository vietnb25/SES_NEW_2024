import OVERVIEW_PV_API from "../constants/overview_pv";
import authService from "./AuthService";
import commonService from "./CommonService";
import CONS from './../constants/constant';

class OverViewPVService {
    #type;
    #commonService;
    #authService;

    constructor() {
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
        this.#authService = authService;
    }

    getPower = async (customerId, projectId, keyword) => {
        let url = OVERVIEW_PV_API.OVERVIEW.GET_POWER + customerId + "/" + projectId + "?keyword=" + keyword;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    getDataChart = async (customerId, projectId, fromDate, toDate) => {
        let url = OVERVIEW_PV_API.OVERVIEW.GET_DATA_CHART + customerId + "/" + projectId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, { fromDate: fromDate, toDate: toDate });
    }

    getTotalEnergy = async (customerId, projectId) => {
        let url = OVERVIEW_PV_API.OVERVIEW.GET_TOTALENERGY + customerId + "/" + projectId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    exportExcel = async (customerId, projectId, fromDate, toDate, userName) => {
        let url = OVERVIEW_PV_API.OVERVIEW.EXPORT_EXCEL + customerId + "/" + projectId;
        let type = this.#type.DOWNLOAD;
        return await this.#commonService.sendRequest(type, url, { fromDate: fromDate, toDate: toDate, userName: userName });
    }
}
export default new OverViewPVService();