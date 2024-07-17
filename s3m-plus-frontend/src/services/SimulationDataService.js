import SIMULATION_API from "../constants/simulation_data_constant";
import CONS from './../constants/constant';
import commonService from './CommonService';

class SimlulationDataService {
    #type;
    #commonService

    constructor(){
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
    }

    listDataEP = (customerId, projectId,typeSystem) => {
        let url = SIMULATION_API.LIST_EP +"?customer="+customerId + "&project=" +projectId + "&system-type="+typeSystem;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }
    listDataMoney = (customerId, projectId,typeSystem) => {
        let url = SIMULATION_API.LIST_MONEY +"?customer="+customerId + "&project=" +projectId + "&system-type="+typeSystem;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    updateDataEp = (id, data) => {
        let url = SIMULATION_API.EDIT_EP + id;
        let type = this.#type.PUT;
        return this.#commonService.sendRequest(type, url, data);
    }
    updateDataMoney = (id, data) => {
        let url = SIMULATION_API.EDIT_MONEY + id;
        let type = this.#type.PUT;
        return this.#commonService.sendRequest(type, url, data);
    }
    addDataMoney = (data) => {
        let url = SIMULATION_API.ADD_MONEY;
        let type = this.#type.POST;
        return this.#commonService.sendRequest(type, url, data);
    }
    addDataEp = (data) => {
        let url = SIMULATION_API.ADD_EP;
        let type = this.#type.POST;
        return this.#commonService.sendRequest(type, url, data);
    }
}

export default new SimlulationDataService();