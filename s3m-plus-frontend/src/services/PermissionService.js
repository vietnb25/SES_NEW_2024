import CONS from './../constants/constant';
import commonService from './CommonService';
import PERMISSION_API from './../constants/permission/permission.api';

class PermissionService {
    #type;
    #commonService

    constructor(){
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
    }

    add = async (data) => {
        let type = this.#type.POST;
        let url = PERMISSION_API.ADD;
        return await this.#commonService.sendRequest(type, url, data);
    }

    getPermissionByUserId = async (userId) => {
        let type = this.#type.GET;
        let url = PERMISSION_API.GET_USER_PERMISSION + userId;
        return await this.#commonService.sendRequest(type, url, null);
    }

    addCategoryPermission = async (data) => {
        let type = this.#type.POST;
        let url = PERMISSION_API.CATEGORY;
        return await this.#commonService.sendRequest(type, url, data);
    }

    getCategoryPermission = async (userId) => {
        let type = this.#type.GET;
        let url = PERMISSION_API.CATEGORY + userId;
        return await this.#commonService.sendRequest(type, url, null);
    }
}

export default new PermissionService();