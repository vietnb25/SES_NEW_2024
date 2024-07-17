import commonService from './CommonService';
import WARNING_API from "../constants/warning_load_api";
import CONS from "../constants/constant";

class WarningLoadService {
    #type;
    #commonService

    constructor(){
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
    }

    getWarnings = async (fromDate, toDate, projectId, customerId) => {
        let url = `${WARNING_API.GET_WARNINGS}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            fromDate,
            toDate,
            projectId,
            customerId
        });
    }

    getWarningsByType = async (fromDate, toDate, projectId, customerId, warningType, page) => {
        let url = WARNING_API.GET_WARNINGS_BY_WARNING_TYPE + warningType;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            fromDate,
            toDate,
            projectId,
            customerId,
            page
        });
    }

    showDataWarningByDevice = async (warningType, fromDate, toDate, deviceId, customerId, page) => {
        let url = `${WARNING_API.GET_DETAIL_BY_DEVICEID}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            warningType,
            fromDate,
            toDate,
            deviceId,
            customerId,
            page
        });
    }

    download = async (warningType, fromDate, toDate, deviceId, customerId, userName) => {
        let url = `${WARNING_API.DOWNLOAD}`;
        let type = this.#type.DOWNLOAD;
        return await this.#commonService.sendRequest(type, url, {
            warningType,
            fromDate,
            toDate,
            deviceId,
            customerId,
            userName
        });
    }

    getDetailWarningCache = async (warningCacheId, customerId) => {
        let url = `${WARNING_API.UPDATE}/${warningCacheId}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId
        });
    }

    updateWarningCache = async (data, customerId) => {
        let url = `${WARNING_API.UPDATE}/${data.id}/${customerId}`;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, data);
    }

}

export default new WarningLoadService();