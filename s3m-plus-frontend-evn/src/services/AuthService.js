import BASE_API from "../constants/api.constant";
import axios from "axios";
import jwt_decode from "jwt-decode";
import AUTH_API from './../constants/auth_api';

class AuthService {
    #key;

    constructor(){
        this.#key = "access_token";
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
            console.log(error);
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
        sessionStorage.setItem(this.#key, value);
    }

    getAuth = () => {
        let access_token = sessionStorage.getItem(this.#key);
        let decode = jwt_decode(access_token);
        let auth = {
            username: decode.sub,
            roles: decode.roles,
            expire: decode.exp
        }
        return auth;
    }

    getToken = () => {
        let access_token = sessionStorage.getItem(this.#key);
        return access_token;
    }

    removeToken = () => {
        sessionStorage.removeItem(this.#key);
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

    getUserName = () => {
        let userName = this.getAuth().username;
        return userName;
    }
}

export default new AuthService();