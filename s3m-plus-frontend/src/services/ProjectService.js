import PROJECT_API from "../constants/project_api";
import authService from './AuthService';
import commonService from './CommonService';
import CONS from './../constants/constant';

class ProjectService {
    #type;
    #commonService;
    #authService;

    constructor() {
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
        this.#authService = authService;
    }

    listProject = async () => {
        let url = PROJECT_API.LIST;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    searchProject = async (keyword) => {
        let url = PROJECT_API.SEARCH + keyword;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    addProject = async (project) => {
        let url = PROJECT_API.ADD;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, project);
    }

    getProject = async (projectId) => {
        let url = PROJECT_API.GET + projectId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    updateProject = async (project) => {
        let url = PROJECT_API.UPDATE;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, project);
    }

    deleteProject = async (projectId) => {
        let url = PROJECT_API.DELETE + projectId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    getProjectByCustomerIdAreaId = async (customerId, areaId) => {
        let url = PROJECT_API.LIST + "/" + areaId + "/" + customerId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    getProjectByCustomerId = async (customerId, ids) => {
        let url = PROJECT_API.LIST_TOOL + "/" + customerId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, { ids: ids });
    }

    getListProjectByCustomerIdAndCustomerName = async (customerId, projectName) => {
        let url = PROJECT_API.LIST_TOOL_SEARCH;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, { customerId, projectName });
    }

    updateImageProject = async (projectId, project) => {
        let url = PROJECT_API.UPDATE_IMAGE + projectId;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, project);
    }

    getProjectIds = async (userName) => {
        let url = PROJECT_API.LIST_IDS;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, { userName: userName });
    }

    getProIds = async (userName) => {
        let url = PROJECT_API.IDS;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, { userName: userName });
    }

}

export default new ProjectService();