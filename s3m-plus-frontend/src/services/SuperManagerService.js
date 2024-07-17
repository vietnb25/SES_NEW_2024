import BASE_API_SUPER_MANAGER from "../constants/super_manager_api_constant";
import CONS from './../constants/constant';
import commonService from './CommonService';

class SuperManagerService {
    #type;
    #commonService
    
    constructor(){
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
    }

    listSuperManager = () => {
        let url = BASE_API_SUPER_MANAGER.SUPERMANAGER.LIST;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    addSuperManager = (superManager) => {
        let url = BASE_API_SUPER_MANAGER.SUPERMANAGER.ADD;
        let type = this.#type.POST;
        return this.#commonService.sendRequest(type, url, superManager);
    }

    searchSuperManager = async (keyword) => {
        let url = BASE_API_SUPER_MANAGER.SUPERMANAGER.SEARCH + keyword;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    detailsSuperManager = async (superManagerId) => {
        let url = BASE_API_SUPER_MANAGER.SUPERMANAGER.DETAILS + superManagerId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    deleteSuperManager = async (supperManagerId) => {
        let url = BASE_API_SUPER_MANAGER.SUPERMANAGER.DELETE + supperManagerId;
        let type = this.#type.DELETE;
        return this.#commonService.sendRequest(type, url, null);
    }

    updateSuperManager = async (superManagerId, superManager) => {
        let url = BASE_API_SUPER_MANAGER.SUPERMANAGER.UPDATE + superManagerId;
        let type = this.#type.PUT;
        return this.#commonService.sendRequest(type, url, superManager);
    }
}

export default new SuperManagerService();
