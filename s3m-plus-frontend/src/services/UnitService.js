import commonService from './CommonService';
import UNIT_API from "../constants/unit_api";
import CONS from "../constants/constant";

class UnitService {
    #type;
    #commonService

    constructor(){
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
    }

    getUnit = async () => {
        let url = `${UNIT_API.GET_UNITS}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }
}

export default new UnitService();