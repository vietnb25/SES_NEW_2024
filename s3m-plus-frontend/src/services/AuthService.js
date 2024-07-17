import BASE_API from "../constants/api.constant";
import axios from "axios";
import jwt_decode from "jwt-decode";
import AUTH_API from './../constants/auth_api';
import Cookies from 'js-cookie';




class AuthService {
    #key;
    #store;

    constructor() {
        this.#key = "access_token";
        this.#store = "user-storage";
    }

    

    forgotPassword = async data => {
        return await axios.post(AUTH_API.FORGOT_PASSWORD, data, {
            headers: {
                "Content-Type": "application/json"
            }
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
    }

    login = async data => {
        return await axios.post(BASE_API.LOGIN, data, {
            headers: {
                "Content-Type": "application/json"
            }
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
    }

    logout = () => {
        this.removeToken();
        window.location.href = "/login";
    }

    saveToken = (value) => {
        Cookies.set(this.#key, value);
    }

    getAuth = () => {
        let access_token = Cookies.get(this.#key);
        let decode = jwt_decode(access_token);
        let auth = {
            username: decode.sub,
            roles: decode.roles,
            expire: decode.exp
        }
        return auth;
    }

    getToken = () => {
        let access_token = Cookies.get(this.#key);
        return access_token;
    }

    removeToken = () => {
        Cookies.remove(this.#key);
        sessionStorage.removeItem(this.#store);
    }

    checkExpireToken = () => {
        let date = new Date();
        let timeMilisecond = date.getTime() / 1000;
        let timeExpire = this.getAuth().expire;
        return timeMilisecond > timeExpire;
    }

    getRoleName = () => {
        let roleName = this.getAuth().roles;
        return roleName[0];
    }

    getUserName= () => {
        let userName = this.getAuth().username;
        return userName;
    }
}

export default new AuthService();