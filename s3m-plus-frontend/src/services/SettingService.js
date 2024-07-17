import CABLE_API from "../constants/cable_api_constant";
import SETTING_API from "../constants/setting_api_constant";
import CONS from './../constants/constant';
import commonService from './CommonService';

class SettingService {
    #type;
    #commonService

    constructor() {
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
    }

    listSetting = (customerId, projectId, typeSystem) => {
        let url = SETTING_API.LIST + "?customerId=" + customerId + "&projectId=" + projectId + "&type=" + typeSystem;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    listSettingHistory = async (customerId, projectId, fromDate, toDate) => {
        let url = SETTING_API.LIST_HISTORY;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId, projectId, fromDate, toDate
        });
    }

    getSettingById = (settingId) => {
        let url = SETTING_API.GETBYID + settingId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    updateSetting = (settingId, setting) => {
        let url = SETTING_API.EDIT + settingId;
        let type = this.#type.PUT;
        return this.#commonService.sendRequest(type, url, setting);
    }
    getSettingByDeviceIds = (devices) => {
        let url = SETTING_API.LIST_SETTING_BY_DEVICES;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, {
            devices: devices
        });
    }
    listByDeviceType = (customerId, projectId, typeSystem, deviceType, deviceId) => {
        let url = SETTING_API.LIST_SETTING_BY_DEVICE_TYPE;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, {
            customer: customerId,
            systemType: typeSystem,
            project: projectId,
            deviceType: deviceType,
            deviceId: deviceId,
        });
    }

    updateSettingByDevices = (devices, setting) => {
        let url = SETTING_API.EDIT_BY_DEVICES + "?devices=" + devices;
        let type = this.#type.PUT;
        return this.#commonService.sendRequest(type, url, setting);
    }

}

export default new SettingService();