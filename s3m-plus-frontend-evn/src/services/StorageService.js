import CONS from "../constants/constant";
import STORE_API from "../constants/store_api";
import AuthService from "./AuthService";
import CommonService from "./CommonService";

class StorageService {
    #type;
    #auth;
    #commonService;
    constructor() {
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = CommonService;
        this.#auth = AuthService;
    }
    listStore = async () => {
        let url = STORE_API.LIST_ALL + "username=" + this.#auth.getAuth().username;
        let type = this.#type.GET;
        console.log(url);
        return await this.#commonService.sendRequest(type, url, null);
    }
    listStoreSearch = async (fromDate, toDate) => {
        let url = STORE_API.LIST_ALL + "username=" + this.#auth.getAuth().username+"&fromDate="+fromDate+"&toDate="+toDate;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }
    // ROLE_EVN (vung mien)
    // Lay Historys va Schedules theo tung vung mien
    listStoreBySuperManagerId = async (superManagerId) => {
        let url = STORE_API.LIST_BY_SUPERMANAGERID + "superManagerId=" + superManagerId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }
    listStoreBySuperManagerIdSearch = async (superManagerId,fromDate, toDate) => {
        let url = STORE_API.LIST_BY_SUPERMANAGERID + "superManagerId=" + superManagerId+"&fromDate="+fromDate+"&toDate="+toDate;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    //ROLE_KHUVUC
    // Lay Historys va Schedules theo tung managerID thuoc supperManagerID
    listStoreByManagerId = async (managerId) => {
        let url = STORE_API.LIST_BY_MANAGERID + "managerId=" + managerId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }
    listStoreByManagerIdSearch = async (managerId,fromDate, toDate) => {
        let url = STORE_API.LIST_BY_MANAGERID + "managerId=" + managerId+"&fromDate="+fromDate+"&toDate="+toDate;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }
    // ROLE_Tinh_Thanh
    // Lay historys va schedules khu vực tương ứng với vùng miền 
    listAllStoreByAreaID = async (areaId) => {
        let url = STORE_API.LIST_BY_AREAID + "areaId=" + areaId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }
    listAllStoreByAreaIDSearch = async (areaId,fromDate, toDate) => {
        let url = STORE_API.LIST_BY_AREAID + "areaId=" + areaId+"&fromDate="+fromDate+"&toDate="+toDate;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

}
export default new StorageService();