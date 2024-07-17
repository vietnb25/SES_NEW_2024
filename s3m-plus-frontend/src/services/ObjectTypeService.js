import commonService from './CommonService';
import CONS from "../constants/constant";
import OBJECT_TYPE_API from '../constants/opject_type_api';
class ObjectTypeService {
    #type;
    #commonService

    constructor() {
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
    }

    getObjectType = async () => {
        let url = `${OBJECT_TYPE_API.GET_OBJECT_TYPE}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    getObjectTypeSelect = async (customerId) => {
        let url = `${OBJECT_TYPE_API.GET_OBJECT_TYPE_SELECT}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId
        });
    }

    checkObjectIdLinkToDevice = async (id) => {
        let url = `${OBJECT_TYPE_API.CHECK_OBJECT_ID}` + id;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    addObjectType = async (data) => {
        let url = `${OBJECT_TYPE_API.ADD_OBJECT_TYPE}`;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, data);
    }

    getObjectById = async (id) => {
        let url = `${OBJECT_TYPE_API.GET_OBJECT_TYPE_BY_ID}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            id
        });
    }

    updateObjectType = async ( data) => {
        let url = `${OBJECT_TYPE_API.UPDATE_OBJECT_TYPE}`;
        let type = this.#type.PUT;
        return await this.#commonService.sendRequest(type, url, data);
    }

    deleteObjectTypeById = async (customerId, id) => {
        let url = `${OBJECT_TYPE_API.DELETE_OBJECT_TYPE_BY_ID}` + `?customerId=${customerId}&id=${id}`;
        let type = this.#type.DELETE;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            id
        });
    }

    getObjectTypeIdById = async (id) => {
        let url = `${OBJECT_TYPE_API.GET_OBJECT_TYPE_ID}` + id;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    getObjectId = async () => {
        let url = `${OBJECT_TYPE_API.GET_OBJECT_ID}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    listObjectType = async () => {
        let url = `${OBJECT_TYPE_API.LIST}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    searchObjectType = async (keyword) => {
        let url = `${OBJECT_TYPE_API.SEARCH_OBJECT_TYPE}` + keyword;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    deleteObjectType = async (id) => {
        let url = `${OBJECT_TYPE_API.DELETE_OBJECT_TYPE}` + id;
        let type = this.#type.DELETE;
        return this.#commonService.sendRequest(type, url, null);
    }

    getObjectTypeById = async (id) => {
        let url = `${OBJECT_TYPE_API.GET_BY_ID}` + id;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    insertObjectType = async (data) => {
        let url = `${OBJECT_TYPE_API.INSERT_OBJECT_TYPE}`;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, data);
    }

    editObjectType = async (data) => {
        let url = `${OBJECT_TYPE_API.EDIT_OBJECT_TYPE}`;
        let type = this.#type.PUT;
        return await this.#commonService.sendRequest(type, url, data);
    }

    getObjectTypeByIds = async (objectTypeIds) => {
        let url = `${OBJECT_TYPE_API.GET_OBJECT_TYPE_BY_IDS}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            objectTypeIds
        });
    }

    getObjectTypeSelectDevice = async (systemType, project) => {
        let url = `${OBJECT_TYPE_API.GET_OBJECT_TYPE_SELECT_DEVICE}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {systemType, project});
    }
}
export default new ObjectTypeService();