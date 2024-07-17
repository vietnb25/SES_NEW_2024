import CABLE_API from "../constants/cable_api_constant";
import SETTING_API from "../constants/setting_api_constant";
import SETTING_COST_API from "../constants/setting_cost_api_contant";
import CONS from './../constants/constant';
import commonService from './CommonService';

class SettingCostService {
    #type;
    #commonService

    constructor() {
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
    }

    listSettingCostByProject = (projectId) => {
        let url = SETTING_COST_API.LIST;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, { project: projectId });
    }
    updateSettingCost = (data) => {
        let url = SETTING_COST_API.UPDATE;
        let type = this.#type.PUT;
        return this.#commonService.sendRequest(type, url, data);
    }


}

export default new SettingCostService();