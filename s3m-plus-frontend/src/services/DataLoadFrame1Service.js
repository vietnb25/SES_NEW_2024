import CONS from "../constants/constant";
import BASE_API_DATA_LOAD_FRAME1 from "../constants/data_load_frame1";
import commonService from './CommonService';

class DataLoadFrame1 {

    #type;
    #commonService
    
    constructor(){
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
    }

    getDateNewDevice = async (customerId) => {
        let url = BASE_API_DATA_LOAD_FRAME1.DATA_LOAD_FRAME1+customerId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }
}

export default new DataLoadFrame1();