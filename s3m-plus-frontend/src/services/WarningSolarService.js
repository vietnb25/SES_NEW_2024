import commonService from './CommonService';
import WARNING_API from "../constants/warning_solar_api";
import CONS from "../constants/constant";

class WarningSolarService {
    #type;
    #commonService

    constructor(){
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
    }

    getWarnings = async (fromDate, toDate, customerId, projectId) => {
        let url = `${WARNING_API.GET_WARNINGS}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            fromDate,
            toDate,
            customerId,
            projectId
        });
    }

    getWarningsByType = async (fromDate, toDate, customerId, projectId, warningType, page) => {
        let url = WARNING_API.GET_WARNINGS_BY_WARNING_TYPE + warningType;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            fromDate,
            toDate,
            customerId,
            projectId,
            page
        });
    }

    getWarningsOperationInformationByType = async (fromDate, toDate, customerId, deviceId, warningType, page) => {
        let url = WARNING_API.GET_WARNINGS_DEVICE_BY_WARNING_TYPE + warningType;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            fromDate,
            toDate,
            customerId,
            deviceId,
            page
        });
    }

    showDataWarningByDevice = async (warningType, fromDate, toDate, customerId, deviceId, page) => {
        let url = `${WARNING_API.GET_INFOR_BY_DEVICEID}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            warningType,
            fromDate,
            toDate,
            customerId,
            deviceId,
            page
        });
    }

    getDetailWarningCache = async (warningType, deviceId, fromDate, toDate, customerId) => {
        let url = `${WARNING_API.UPDATE}/${warningType}/${deviceId}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            fromDate,
            toDate,
            customerId
        });
    }

    updateWarningCache = async (data) => {
        let url = `${WARNING_API.UPDATE}/${data.id}`;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, data);
    }

    download = async (warningType, fromDate, toDate, customerId, deviceId, userName) => {
        let url = `${WARNING_API.DOWNLOAD}`;
        let type = this.#type.DOWNLOAD;
        return await this.#commonService.sendRequest(type, url, {
            warningType,
            fromDate,
            toDate,
            customerId,
            deviceId,
            userName
        });
    }

}

export default new WarningSolarService();