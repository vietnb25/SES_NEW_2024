import authService from './AuthService';
import AREA_API from './../constants/area_api';
import commonService from './CommonService';
import CONS from "../constants/constant";

class AreaService {
    #type;
    #auth;
    #commonService;

    constructor(){
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
        this.#auth = authService;
    }

    listArea = async () => {
        let url = AREA_API.LIST + "/" + this.#auth.getAuth().username;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    listAreaByCustomerAndManagerId = async (customerId, managerId) => {
        let url = AREA_API.LIST_BY_CUSTOMER_AND_MANAGER  +"/" + customerId + "/" + managerId;
        let type = this.#type.GET; 
        return await this.#commonService.sendRequest(type, url, null);
    }

    getByManagerId = async managerId => {
        let url = AREA_API.LIST + "/" + this.#auth.getAuth().username + "/" + managerId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }
    searchArea = async (keyword)=>{
        let url=AREA_API.SEARCH+keyword ;
        let type=this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }
    addArea = async(area) =>{
        let url=AREA_API.ADD ;
        let type=this.#type.POST ;
        return await this.#commonService.sendRequest(type, url, area);
    }
    getArea = async(areaId)=>{
        let url=AREA_API.GET + areaId;
        let type=this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null)
    }
    updateArea = async(areaId, area)=>{
        let url=AREA_API.UPDATE +areaId;
        let type = this.#type.PUT;
        return await this.#commonService.sendRequest(type,url,area)
    }
    deleteArea =async(areaId) =>{
        let url=AREA_API.DELETE +areaId;
        let type = this.#type.DELETE;
        return await this.#commonService.sendRequest(type,url,null)
    }
    getAreaSelectDevice = async (systemType, project) => {
        let url=AREA_API.AREA_SELECT_DEVICE;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type,url,{
            systemType: systemType,
            project : project
        })
    }
}

export default new AreaService();