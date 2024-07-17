import CABLE_API from "../constants/cable_api_constant";
import SETTING_SHIFT_API from "../constants/setting_shift_api_contant";
import CONS from './../constants/constant';
import commonService from './CommonService';

class SettingShiftService {
    #type;
    #commonService

    constructor() {
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
    }

    listSetting = (projectId) => {
        let url = SETTING_SHIFT_API.LIST;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, { projectId: projectId });
    }
    listSettingByProjectAndStatus = (projectId) => {
        let url = SETTING_SHIFT_API.LIST_BY_PROJECT_AND_STATUS;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, { projectId: projectId, status: 1 });
    }

    getSettingById = (settingId) => {
        let url = SETTING_SHIFT_API.GETBYID + settingId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    addSettingShift = async (setting, projectId) => {
        let url = SETTING_SHIFT_API.ADD + "/" + projectId;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, setting);
    }

    getSettingByProjectId = (customerId, systemTypeId, projectId) => {
        let url = SETTING_SHIFT_API.GETBYPROJECTID;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url,
            {
                customerId, systemTypeId, projectId
            });
    }

    getSettingShiftByIds = async (id) => {
        let url = `${SETTING_SHIFT_API.GET_BY_ID}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            id
        });
    }

    updateSetting = (setting) => {
        let url = SETTING_SHIFT_API.EDIT;
        let type = this.#type.PUT;
        return this.#commonService.sendRequest(type, url, setting);
    }

    lockSetting = (settingId, setting) => {
        let url = SETTING_SHIFT_API.LOCK + settingId;
        let type = this.#type.PUT;
        return this.#commonService.sendRequest(type, url, setting);
    }

    deleteSetting = (settingId, setting) => {
        let url = SETTING_SHIFT_API.DELETE + settingId;
        let type = this.#type.PUT;
        return this.#commonService.sendRequest(type, url, setting);
    }

    unLockSetting = (settingId, setting) => {
        let url = SETTING_SHIFT_API.UNLOCK + settingId;
        let type = this.#type.PUT;
        return this.#commonService.sendRequest(type, url, setting);
    }
}

export default new SettingShiftService();