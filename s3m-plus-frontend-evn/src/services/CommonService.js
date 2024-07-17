import axios from "axios";
import CONS from "../constants/constant";
import authService from './AuthService';

class CommonService {
    #type;

    constructor() {
        this.#type = CONS.METHOD_TYPE;
    }

    sendRequest = async (type, url, data) => {
        let isExpired = authService.checkExpireToken();
        if (isExpired) {
            let path = window.location.pathname;
            authService.removeToken();
            window.location.href = "/login?next=" + path;
            return;
        }

        let token = authService.getToken();
        let headers = {
            "Content-Type": "application/json",
            "Authorization": token
        }
        switch (type) {
            case this.#type.GET:
                return await axios.get(url, {
                    headers,
                    params: data
                }).then(response => {
                    return {
                        data: response.data,
                        status: response.status
                    }
                }).catch(error => {
                    return {
                        data: error.response.data,
                        status: error.response.status
                    }
                });
            case this.#type.POST:
                return await axios.post(url, data, {
                    headers
                }).then(response => {
                    return {
                        data: response.data,
                        status: response.status
                    }
                }).catch(error => {
                    return {
                        data: error.response.data,
                        status: error.response.status
                    }
                });
            case this.#type.PUT:
                return await axios.put(url, data, {
                    headers
                }).then(response => {
                    return {
                        data: response.data,
                        status: response.status
                    }
                }).catch(error => {
                    return {
                        data: error.response.data,
                        status: error.response.status
                    }
                });
            case this.#type.DELETE:
                return await axios.delete(url, {
                    headers
                }).then(response => {
                    return {
                        data: response.data,
                        status: response.status
                    }
                }).catch(error => {
                    return {
                        data: error.response.data,
                        status: error.response.status
                    }
                });
            default:
                break;
        }
    }
}

export default new CommonService();