import commonService from './CommonService';
import WARNING_API from "../constants/warning_api";
import CONS from "../constants/constant";

class WarningService {
    #type;
    #commonService

    constructor() {
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
    }

    getWarnings = async (fromDate, toDate, projectId, customerId, systemTypeId, dvId, ids) => {
        let url = `${WARNING_API.GET_WARNINGS}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            fromDate,
            toDate,
            projectId,
            customerId,
            systemTypeId,
            dvId,
            ids
        });
    }

    listDeviceLostSignal = async (customerId, projectId, systemTypeId) => {
        let url = `${WARNING_API.GET_WARNED_DEVICE_LOST_SIGNAL}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            projectId,
            systemTypeId
        });
    }


    showDataWarningByDevice = async (systemTypeId, warningType, fromDate, toDate, projectId, customerId, deviceId, category, page) => {
        let url = `${WARNING_API.GET_INFOR_BY_DEVICEID}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            systemTypeId,
            warningType,
            fromDate,
            toDate,
            projectId,
            customerId,
            deviceId,
            category,
            page
        });
    }

    /**2023-10-07 */
    getListWarnedDevice = async (fromDate, toDate, projectId, customerId, systemTypeId, warningType, ids) => {
        let url = `${WARNING_API.GET_LIST_WARNED_DEVICE}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            fromDate,
            toDate,
            projectId,
            customerId,
            systemTypeId,
            warningType,
            ids
        });
    }

    /**2023-10-09 */
    getInfoWarnedDevice = async (customerId, systemTypeId, deviceId, warningType, toDate) => {
        let url = `${WARNING_API.GET_INFOR_WARNED_DEVICE}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            systemTypeId,
            deviceId,
            warningType,
            toDate,

        });
    }

    getInfoWarnedDeviceFrame2 = async (customerId, systemTypeId, deviceId, warningType, toDate) => {
        let url = `${WARNING_API.GET_INFOR_WARNED_DEVICE_FRAME_2}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            systemTypeId,
            deviceId,
            warningType,
            toDate,
        });
    }

    /**2023-10-11 */
    getListDataWarning = async (customerId, systemTypeId, deviceId, warningType, fromDate, toDate) => {
        let url = `${WARNING_API.GET_LIST_DATA_WARNING}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            systemTypeId,
            deviceId,
            warningType,
            fromDate,
            toDate
        });
    }

    getListDataWarningFrame2 = async (customerId, systemTypeId, deviceId, warningType, fromDate, toDate) => {
        let url = `${WARNING_API.GET_LIST_DATA_WARNING_FRAME_2}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            systemTypeId,
            deviceId,
            warningType,
            fromDate,
            toDate
        });
    }

}

export default new WarningService();