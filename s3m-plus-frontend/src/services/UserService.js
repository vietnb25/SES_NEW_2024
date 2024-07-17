import USER_API from "../constants/user_api";
import commonService from './CommonService';
import CONS from "../constants/constant";
import authService from './AuthService';

class UserService {
    #type;
    #auth;
    #commonService

    constructor() {
        this.#type = CONS.METHOD_TYPE;
        this.#auth = authService;
        this.#commonService = commonService;
    }

    listUser = async () => {
        let url = USER_API.LIST;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    userByCustomerIds = async (customerIds, userId) => {
        let url = USER_API.USER_BY_CUSTOMER_IDS;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, { customerIds, userId });
    }

    addUser = async userData => {
        let url = USER_API.ADD;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, userData);
    }

    searchUser = async (keyword, customerIds, userId) => {
        let url = USER_API.SEARCH;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            keyword,
            customerIds,
            userId
        });
    }

    deleteUser = async userId => {
        let url = USER_API.DELETE + userId;
        let type = this.#type.DELETE;
        return await this.#commonService.sendRequest(type, url, null);
    }

    getInfoInsert = async () => {
        let url = USER_API.ADD;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    getUser = async userId => {
        let url = USER_API.UPDATE + userId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    updateUser = async user => {
        let url = USER_API.UPDATE + user.id;
        let type = this.#type.PUT;
        return await this.#commonService.sendRequest(type, url, user);
    }

    unlockUser = async userId => {
        let url = USER_API.UNLOCK_USER + userId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    lockUser = async userId => {
        let url = USER_API.LOCK_USER + userId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    getUserByUsername = async () => {
        let url = `${USER_API.GET_USER}` + "/" + this.#auth.getAuth().username;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null)
    }

    updatePriorityIngredients = async (user) => {
        let url = USER_API.UPDATE_PRIORITY;
        let type = this.#type.PUT;
        return await this.#commonService.sendRequest(type, url, user);
    }
}

export default new UserService();