import CABLE_API from "../constants/cable_api_constant";
import CONS from './../constants/constant';
import commonService from './CommonService';

class CableService {
    #type;
    #commonService

    constructor(){
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
    }

    listCable = () => {
        let url = CABLE_API.CABLE.LIST;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    addCable =  cableData => {
        let url = CABLE_API.CABLE.ADD;
        let type = this.#type.POST;
        return this.#commonService.sendRequest(type, url, cableData);
    }

    detailCable = async (cableId) => {
        let url = CABLE_API.CABLE.GETBYID + cableId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    updateCable = async (cableId, cable) => {
        let url = CABLE_API.CABLE.EDIT + cableId;
        let type = this.#type.PUT;
        return this.#commonService.sendRequest(type, url, cable);
    }

    deleteCable = cableId => {
        let url = CABLE_API.CABLE.DELETE + cableId;
        let type = this.#type.DELETE;
        return this.#commonService.sendRequest(type, url, null);
    }

    searchCable = keyword => {
        let url = CABLE_API.CABLE.SEARCH + keyword;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }
}

export default new CableService();