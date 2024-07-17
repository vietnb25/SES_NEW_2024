import commonService from './CommonService';
import CONS from "../constants/constant";
import LANDMARK_AND_PLAN_API from '../constants/landmark-and-plan-api';

class LandmarksAndPlansService {
    #type;
    #commonService

    constructor() {
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
    }

    getPlans = async (customer, project, systemType, date, deviceId) => {
        let url = `${LANDMARK_AND_PLAN_API.LIST_PLANS}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, { customer, project, systemType, date, deviceId });
    }
    getLandmarks = async (customer, project, systemType, date, deviceId) => {
        let url = `${LANDMARK_AND_PLAN_API.LIST_LANDMARK}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, { customer, project, systemType, date, deviceId });
    }
    updateLandmark = async (data, customer) => {
        let url = `${LANDMARK_AND_PLAN_API.UPDATE_LANDMARK}` + customer;
        let type = this.#type.PUT;
        return await this.#commonService.sendRequest(type, url, data);
    }
    updatePlan = async (data, customer) => {
        let url = `${LANDMARK_AND_PLAN_API.UPDATE_PLAN}` + customer;
        let type = this.#type.PUT;
        return await this.#commonService.sendRequest(type, url, data);
    }
    insertLandmarks = async (data, customer, systemType, project, date, deviceId) => {
        let url = `${LANDMARK_AND_PLAN_API.INSERT_LANDMARK}` + "customer=" + customer + "&systemType=" + systemType + "&project=" + project + "&date=" + date + "&deviceId=" + deviceId;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, data);
    }
    insertPlans = async (data, customer, systemType, project, date, deviceId) => {
        let url = `${LANDMARK_AND_PLAN_API.INSERT_PLAN}` + "customer=" + customer + "&systemType=" + systemType + "&project=" + project + "&date=" + date + "&deviceId=" + deviceId;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, data);
    }


}
export default new LandmarksAndPlansService();