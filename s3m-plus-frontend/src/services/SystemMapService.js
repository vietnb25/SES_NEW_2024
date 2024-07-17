import BASE_API_SYSTEM_MAP from "../constants/system_map_api_constant";
import CONS from './../constants/constant';
import commonService from './CommonService';

class SystemMapService {
    #type;
    #commonService
    
    constructor(){
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
    }

    getAll = () => {
        let url = BASE_API_SYSTEM_MAP.ALL;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    listSystemMap = () => {
        let url = BASE_API_SYSTEM_MAP.LIST;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getSystemMapByProjectIdAndSystemTypeId = async (projectId, systemTypeId) => {
        let url = BASE_API_SYSTEM_MAP.LIST + "/" + projectId + "/" + systemTypeId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    addSystemMap = async systemMap => {
        let url = BASE_API_SYSTEM_MAP.ADD;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, systemMap);
    }

    getDevicesEmpty = async (projectId, systemTypeId) => {
        let url = BASE_API_SYSTEM_MAP.DEVICE_EMPTY + "/" + projectId + "/" + systemTypeId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    updateSystemMap = async (systemMap, listDeviceUpdate, deviceJsonList) => {
        let url = BASE_API_SYSTEM_MAP.UPDATE;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, {systemMap, listDeviceUpdate, deviceJsonList});
    }

    updateSystemDevice = async (systemMap) => {
        let url = BASE_API_SYSTEM_MAP.UPDATE_SYSTEM_DEVICE;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, {systemMap});
    }

    getSystemMap = async (systemMapId) => {
        let url = BASE_API_SYSTEM_MAP.DETAILS + "/" + systemMapId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getDevicesAlready = async (projectId, systemTypeId) => {
        let url = BASE_API_SYSTEM_MAP.DEVICE_ALREADY + "/" + projectId + "/" + systemTypeId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    deleteSystemMap = async (systemMapId) => {
        let url = BASE_API_SYSTEM_MAP.DELETE + "/" + systemMapId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    getDataJson = async (idList, customerId) => {
        let url = BASE_API_SYSTEM_MAP.DATAJSON;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {idList, customerId});
    }

    getSystemInfoTime = async (idList, customerId) => {
        let url = BASE_API_SYSTEM_MAP.SYSTEM_INFO_TIME;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {idList, customerId});
    }

    getSystemMapByProjectId = async () => {
        let url = BASE_API_SYSTEM_MAP.SYSTEM_MAP_BY_PROJECT;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }
}

export default new SystemMapService();
