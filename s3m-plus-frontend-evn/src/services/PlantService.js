import BASE_API_PLANT from "../constants/plant_api";
import commonService from './CommonService';
import CONS from './../constants/constant';
class PlantService {

    #type;
    #commonService;

    constructor(){
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
    }
    getListPlant = async (typeScropTree, userName) => {
        let url = BASE_API_PLANT.PLANT.LIST;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {typeScropTree, userName});
    }

    searchPlant = async (data) => {
        let url = BASE_API_PLANT.PLANT.SEARCH;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, data);
    }
}

export default new PlantService();