import CONTROL_API from "../constants/control_pv";
import CONS from "../constants/constant";
import commonService from "./CommonService";

class ControlService {
    #type;
    #commonService;

    constructor() {
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
    };

    getListSystemMap = (projectId) => {
        let url = CONTROL_API.CONTROL.GETSYSTEM + "/" + projectId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type , url, null);
    };

    getControls = (projectId) => {
        let url = CONTROL_API.CONTROL.CONTROL_PROJECT + "/" + projectId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    };

    getControlDevice = (control) => {
        let url = CONTROL_API.CONTROL.CONTROL_DEVICE;
        let type = this.#type.POST;
        return this.#commonService.sendRequest(type, url, control);
    };

    saveControl = (controls) => {
        let url = CONTROL_API.CONTROL.CONTROL_SAVE;
        let type = this.#type.POST;
        return this.#commonService.sendRequest(type, url, controls);
    };

    getControlSystem = (systemMapId) => {
        let url = CONTROL_API.CONTROL.CONTROL_SYSTEM + systemMapId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }


}
export default new ControlService();