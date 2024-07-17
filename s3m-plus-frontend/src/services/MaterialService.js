import commonService from './CommonService';
import CONS from "../constants/constant";
import MATERIAL_API from '../constants/material-api';

class MaterialService {
    #type;
    #commonService

    constructor() {
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
    }

    getListMaterialType = async () => {
        let url = `${MATERIAL_API.LIST_MATERIAL_TYPE}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {});
    }

    getListMaterialValue = async (project, material) => {
        let url = `${MATERIAL_API.LIST_MATERIAL_VALUE}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            project: project,
            material: material
        });
    }
    addOrUpdateMaterialValue = async (materialValue) => {
        let url = `${MATERIAL_API.ADD_OR_UPDATE_MATERIAL_VALUE}`;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, materialValue);
    }

    
}
export default new  MaterialService();