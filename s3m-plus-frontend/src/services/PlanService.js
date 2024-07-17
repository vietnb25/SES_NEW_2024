import commonService from './CommonService';
import CONS from "../constants/constant";
import PLAN_API from '../constants/plan_api';

class PlanService {
    #type;
    #commonService

    constructor() {
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
    }

    getPlan = async (customerId, projectId, startDate, endDate, systemTypeId) => {
        let url = `${PLAN_API.GET_PLAN}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId, projectId, startDate, endDate, systemTypeId
        });
    }

    addPlan = async (customerId, data) => {
        let url = `${PLAN_API.ADD_PLAN}` + customerId;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, data);
    }

    getPlanById = async (customerId, planId) => {
        let url = `${PLAN_API.GET_PLAN_BY_ID}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            planId
        });
    }

    deletePlanById = async (customerId, planId) => {
        let url = `${PLAN_API.DELETE_PLAN_BY_ID}` + `?customerId=${1}&planId=${planId}`;
        let type = this.#type.DELETE;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            planId
        });
    }

    updatePlan = async (customerId, data) => {
        let url = `${PLAN_API.UPDATE_PLAN}` + customerId;
        let type = this.#type.PUT;
        return await this.#commonService.sendRequest(type, url, data);
    }


}
export default new PlanService();