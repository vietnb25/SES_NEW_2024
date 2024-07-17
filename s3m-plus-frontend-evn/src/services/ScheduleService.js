import commonService from './CommonService';
import CONS from './../constants/constant';

class ScheduleService {
    #type;
    #commonService;

    constructor() {
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
    }

    getPopUp = (markerId) => {
        let url = CONS.LOCAL_HOST + "common/schedule/system/" + markerId
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getSchedule = () => {
        let url = CONS.LOCAL_HOST + "common/schedule/data"
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getScheduleLowerLevel = (data) => {
        let url = CONS.LOCAL_HOST + "common/schedule/data/lower-level"
        let type = this.#type.POST;
        return this.#commonService.sendRequest(type, url, data);
    }

    getScheduleSuperManagerId = (superManagerId) => {
        let url = CONS.LOCAL_HOST + "common/schedule/data/" + superManagerId
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getHistory = (data) => {
        let url = CONS.LOCAL_HOST + "common/schedule/history"
        let type = this.#type.POST;
        return this.#commonService.sendRequest(type, url, data);
    }

    checkSchedule = (time) => {
        let url = CONS.LOCAL_HOST + "common/schedule/check"
        let type = this.#type.POST;
        return this.#commonService.sendRequest(type, url, time);
    }

    addSchedule = (data) => {
        let url = CONS.LOCAL_HOST + "common/schedule/add"
        let type = this.#type.POST;
        return this.#commonService.sendRequest(type, url, data);
    }

    editSchedule = (data) => {
        let url = CONS.LOCAL_HOST + "common/schedule/history/show"
        let type = this.#type.POST;
        return this.#commonService.sendRequest(type, url, data);
    }

    historyDetail = (data) => {
        let url = CONS.LOCAL_HOST + "common/schedule/history/detail"
        let type = this.#type.POST;
        return this.#commonService.sendRequest(type, url, data);
    }

    delete = (id) => {
        let url = CONS.LOCAL_HOST + "common/schedule/history/delete/" + id
        let type = this.#type.POST;
        return this.#commonService.sendRequest(type, url, id);
    }
}

export default new ScheduleService()