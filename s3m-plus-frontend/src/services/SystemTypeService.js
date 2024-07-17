import BASE_API_SYSTEM_TYPE from "../constants/system_type_api_constant";
import CONS from './../constants/constant';
import commonService from './CommonService';

class SystemTypeService {
    #type;
    #commonService
    
    constructor(){
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
    }

    listSystemType = () => {
        let url = BASE_API_SYSTEM_TYPE.SYSTEMTYPE.LIST;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    detailsSystemType = async (systemTypeId) => {
        let url = BASE_API_SYSTEM_TYPE.SYSTEMTYPE.DETAILS + systemTypeId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }
}

export default new SystemTypeService();
