import CONS from './../constants/constant';
import authService from './AuthService';
import commonService from './CommonService';
import BASE_API_LOAD_TYPE from '../constants/load_type_api_constant'

class LoadTypeService {
    #type;
    #commonService;
    #authService;

    constructor() {
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
        this.#authService = authService;
    }

    listLoadType = async () => {
        let url = BASE_API_LOAD_TYPE.LIST_LOAD_TYPE.LIST;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    getListLoadBySystemTypeIdAndProjectId = async (systemTypeId, projectId) => {
        let url = BASE_API_LOAD_TYPE.LIST_LOAD_TYPE.LIST_LOAD_BY_PROJECT_ID + projectId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            systemTypeId: systemTypeId
        });
    }

    addLoadType = async (customerId, data) => {
        let url = `${BASE_API_LOAD_TYPE.LIST_LOAD_TYPE.ADD_LOAD_TYPE}` + customerId;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, data);
    }

    getLoadTypeById = async (customerId, loadTypeId) => {
        let url = `${BASE_API_LOAD_TYPE.LIST_LOAD_TYPE.GET_LOAD_TYPE_BY_ID}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            loadTypeId
        });
    }

    updateLoadType = async (customerId, data) => {
        let url = `${BASE_API_LOAD_TYPE.LIST_LOAD_TYPE.UPDATE_LOAD_TYPE}` + customerId;
        let type = this.#type.PUT;
        return await this.#commonService.sendRequest(type, url, data);
    }

    searchLoadType = async (keyword) => {
        let url = BASE_API_LOAD_TYPE.LIST_LOAD_TYPE.SEARCH + keyword;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    listLoadTypeMst = async () => {
        let url = BASE_API_LOAD_TYPE.LIST_LOAD_TYPE.LIST_MST;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    deleteLoadTypeById = async (id) => {
        let url = `${BASE_API_LOAD_TYPE.LIST_LOAD_TYPE.DELETE_LOAD_TYPE_BY_ID}` + `${id}`;
        let type = this.#type.DELETE;
        return await this.#commonService.sendRequest(type, url, {
            id
        });
    }

    checkLoadTypeDevice = async (id) => {
        let url = BASE_API_LOAD_TYPE.LIST_LOAD_TYPE.CHECK_LOAD_TYPE_DEVICE + id;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getLoadTypeByProjectAndSystemType = async (customer, project, systemType) => {
        let url = BASE_API_LOAD_TYPE.LIST_LOAD_TYPE.LIST_LOAD_TYPE_BY_PROJECT_AND_SYSTEM_TYPE + "?customer=" + customer + "&project=" + project +"&typeSystem="+systemType;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }
}

export default new LoadTypeService();