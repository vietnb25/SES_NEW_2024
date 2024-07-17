import MANAGER_API from "../constants/manager_api";
import authService from './AuthService';
import CONS from './../constants/constant';
import commonService from './CommonService';

class ManagerService {
    #type;
    #auth;
    #commonService;

    constructor(){
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
        this.#auth = authService;
    }
    
    listManager =  () => {
        let url = MANAGER_API.LIST
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getManagerBySuperManager = superManagerId => {
        let url = MANAGER_API.LIST_BY_SUPER_MANAGER + this.#auth.getAuth().username + "/" +  superManagerId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    searchManager = keyword => {
        let url = MANAGER_API.SEARCH +keyword;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    addManager = managerData => {
        let url = MANAGER_API.ADD;
        let type = this.#type.POST;
        return this.#commonService.sendRequest(type,url,managerData);
    }

    detailManager = async (managerId) => {
        let url = MANAGER_API.DETAIL + managerId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type,url,null);
    }

    updateManager = async (managerId, managerData) => {
        let url = MANAGER_API.UPDATE+ managerId;
        let type = this.#type.PUT;
        return this.#commonService.sendRequest(type,url,managerData);
    }

    deleteManager = async (managerId) => {
        let url = MANAGER_API.DELETE + managerId;
        let type = this.#type.DELETE;
        return this.#commonService.sendRequest(type,url,null);
    }

    getManagerByCustomer = async (customerId) => {
        let url = MANAGER_API.LIST_BY_CUSTOMER + customerId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type,url,null);
    }
}

export default new ManagerService();