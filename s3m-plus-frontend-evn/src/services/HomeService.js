import commonService from './CommonService';
import CONS from './../constants/constant';
import HOME_API from './../constants/home_api';
import authService from './AuthService';

class HomeService {
    #type;
    #commonService;
    #authService;
    
    constructor(){
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
        this.#authService = authService;
    }

    initData = () => {
        let url = HOME_API.HOME + "/" + this.#authService.getAuth().username;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getProjectLocations = () => {
        let url = HOME_API.HOME + "/marker";
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, {username: this.#authService.getAuth().username});
    }

    getProjectById = (projectId) => {
        let url = HOME_API.HOME + "/project/" + projectId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getProjectTree = () => {
        let url = HOME_API.HOME + "/" + this.#authService.getAuth().username + "/project-tree-evn";;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }
}

export default new HomeService();