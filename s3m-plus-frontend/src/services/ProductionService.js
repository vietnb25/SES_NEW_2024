import commonService from './CommonService';
import PRODUCTION_API from "../constants/production_api";
import CONS from "../constants/constant";

class ProductionService {
    #type;
    #commonService

    constructor(){
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
    }

    getListProduction = async (customerId, projectId) => {
        let url = `${PRODUCTION_API.GET_PRODUCTIONS}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            projectId
        });
    }

    getListProductionStep = async (customerId, productionId, projectId) => {
        let url = `${PRODUCTION_API.GET_PRODUCTION_STEPS}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            productionId,
            projectId
        });
    }

    addProduction = async (customerId, data) => {
        let url = `${PRODUCTION_API.ADD_PRODUCTION}/` + customerId;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, data);
    }

    addProductionStep = async (customerId, data) => {
        let url = `${PRODUCTION_API.ADD_PRODUCTION_STEP}/` + customerId;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, data);
    }
}

export default new ProductionService();