import { async } from "q";
import BASE_API_OBJECT from "../constants/object_type_api_constant"
import CONS from './../constants/constant';
import authService from './AuthService';
import commonService from './CommonService';

class ObjectService {
    #type;
    #commonService;
    #authService;

    constructor() {
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
        this.#authService = authService;
    }

    listObject = async (systemTypeId) => {
        let url = BASE_API_OBJECT.OBJECT.LIST + '/' + systemTypeId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    getListObjectTypeBySystemTypeIdAndProjectId = async (systemTypeId, projectId) => {
        let url = BASE_API_OBJECT.OBJECT_TYPE.LIST;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            systemTypeId: systemTypeId, 
            projectId: projectId});
    }

    getListAreaBySystemTypeIdAndProjectId = async (systemTypeId, projectId) => {
        let url = BASE_API_OBJECT.OBJECT_TYPE.LIST_AREA;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            systemTypeId: systemTypeId, 
            projectId: projectId});
    }

    getListObjectByDeviceTypeId = async (projectId, deviceTypeId) => {
        let url = BASE_API_OBJECT.OBJECT.BY_DEVICE_TYPE + "?projectId=" + projectId + "&deviceTypeId=" + deviceTypeId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    getListObjectMst = async () => {
        let url = BASE_API_OBJECT.OBJECT_TYPE.LIST_MST;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }
}

export default new ObjectService();