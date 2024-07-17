import BASE_API_DEVICE_TYPE from "../constants/device_type_api_constant";
import BASE_API_DEVICE_TYPE_MST from "../constants/device_type_mst_api_constant";
import CONS from './../constants/constant';
import commonService from './CommonService';

class DeviceTypeService {
    #type;
    #commonService
    
    constructor(){
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
    }

    listDeviceType = () => {
        let url = BASE_API_DEVICE_TYPE.DEVICETYPE.LIST;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    listDeviceTypeBySystemType = async (systemTypeId) => {
        let url = BASE_API_DEVICE_TYPE.DEVICETYPE.SYSTEMTYPE + systemTypeId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }
    listDeviceTypeMst = async (systemType, customer, project) => {
        let url = BASE_API_DEVICE_TYPE_MST.DEVICETYPE.LIST;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, {systemType, customer, project});
    }

    getListDeviceType = async () => {
        let url = BASE_API_DEVICE_TYPE_MST.DEVICETYPE.LIST_DEVICE_TYPE;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    searchDeviceType = async (keyword) => {
        let url = BASE_API_DEVICE_TYPE_MST.DEVICETYPE.SEARCH;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, {
            keyword
        });
    }

    addDeviceType = async (data) => {
        let url = BASE_API_DEVICE_TYPE_MST.DEVICETYPE.ADD_DEVICE_TYPE;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, data);
    }

    updateDeviceType = async (data) => {
        let url = BASE_API_DEVICE_TYPE_MST.DEVICETYPE.UPDATE_DEVICE_TYPE;
        let type = this.#type.PUT;
        return await this.#commonService.sendRequest(type, url, data);
    }

    deleteDeviceType= async (id) => {
        let url = BASE_API_DEVICE_TYPE_MST.DEVICETYPE.DELETE + id;
        let type = this.#type.DELETE;
        return await this.#commonService.sendRequest(type, url, null);
    }

    getDeviceTypeById = async (id) => {
        let url = BASE_API_DEVICE_TYPE_MST.DEVICETYPE.GET_BY_ID + id;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }
}

export default new DeviceTypeService();
