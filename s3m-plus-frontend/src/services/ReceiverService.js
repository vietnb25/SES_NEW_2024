
import commonService from './CommonService';
import CONS from './../constants/constant';
import RECEIVER_API from '../constants/receiver_api';
import authService from './AuthService';

class ReceiverService {
    #type;
    #commonService;
    #authService;

    constructor() {
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
        this.#authService = authService;
    }

    //PV
    listReceiverPV = async (projectId, systemType) => {
        let url = RECEIVER_API.PV.LIST;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            projectId: projectId,
            systemType: systemType
        });
    }

    saveWarningInforPV = async (warnings, systemType, customerId, projectId, deviceId, receiverId) => {
        let url = RECEIVER_API.PV.SAVE + "/" + systemType + "/" + customerId + "/" + projectId + "/" + deviceId + "/" + receiverId;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, warnings);
    }

    getWarningInforPV = async (receiverId, deviceId) => {
        let url = RECEIVER_API.PV.GET + "/" + receiverId + "/" + deviceId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    addNewReceiverPV = async (receiver, projectId, systemType) => {
        let url = RECEIVER_API.PV.ADD + "/" + projectId + "/" + systemType;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, receiver);
    }

    updateReceiverPV = async (receiver) => {
        let url = RECEIVER_API.PV.UPDATE;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, receiver);
    }

    deleteReceiverPV = async (receiverId) => {
        let url = RECEIVER_API.PV.DELETE;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            receiverId: receiverId
        })
    }

    //LOAD

    listReceiverLoad = async (projectId, systemType) => {
        let url = RECEIVER_API.LOAD.LIST;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            projectId: projectId,
            systemType: systemType
        });
    }

    saveWarningInforLoad = async (warnings, systemType, customerId, projectId, deviceId, receiverId) => {
        let url = RECEIVER_API.LOAD.SAVE + "/" + systemType + "/" + customerId + "/" + projectId + "/" + deviceId + "/" + receiverId;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, warnings);
    }

    getWarningInforLoad = async (receiverId, deviceId) => {
        let url = RECEIVER_API.LOAD.GET + "/" + receiverId + "/" + deviceId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    addNewReceiverLoad = async (receiver, projectId, systemType) => {
        let url = RECEIVER_API.LOAD.ADD + "/" + projectId + "/" + systemType;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, receiver);
    }

    updateReceiverLoad = async (receiver) => {
        let url = RECEIVER_API.LOAD.UPDATE;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, receiver);
    }

    deleteReceiverLoad = async (receiverId) => {
        let url = RECEIVER_API.LOAD.DELETE;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            receiverId: receiverId
        })
    }

    //GRID
    listReceiverGrid = async (projectId, systemType) => {
        let url = RECEIVER_API.GRID.LIST;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            projectId: projectId,
            systemType: systemType
        });
    }

    saveWarningInforGrid = async (warnings, systemType, customerId, projectId, deviceId, receiverId) => {
        let url = RECEIVER_API.GRID.SAVE + "/" + systemType + "/" + customerId + "/" + projectId + "/" + deviceId + "/" + receiverId;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, warnings);
    }

    getWarningInforGrid = async (receiverId, deviceId) => {
        let url = RECEIVER_API.GRID.GET + "/" + receiverId + "/" + deviceId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    addNewReceiverGrid = async (receiver, projectId, systemType) => {
        let url = RECEIVER_API.GRID.ADD + "/" + projectId + "/" + systemType;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, receiver);
    }

    updateReceiverGrid = async (receiver) => {
        let url = RECEIVER_API.GRID.UPDATE;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, receiver);
    }

    deleteReceiverGrid = async (receiverId) => {
        let url = RECEIVER_API.GRID.DELETE;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            receiverId: receiverId
        })
    }

    /**------------------------------HOME-PAGE ----------------------------------*/

    listReceiver = async (projectId, systemType) => {
        let url = RECEIVER_API.LIST;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            projectId: projectId,
            systemType: systemType
        });
    }

    saveWarningInfor = async (warnings, systemType, customerId, projectId, deviceId, receiverId) => {
        let url = RECEIVER_API.SAVE + "/" + systemType + "/" + customerId + "/" + projectId + "/" + deviceId + "/" + receiverId;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, warnings);
    }

    getWarningInfor = async (receiverId, deviceId) => {
        let url = RECEIVER_API.GET + "/" + receiverId + "/" + deviceId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    addNewReceiver = async (receiver, projectId, systemType) => {
        let url = RECEIVER_API.ADD + "/" + projectId + "/" + systemType;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, receiver);
    }

    updateReceiver = async (receiver) => {
        let url = RECEIVER_API.UPDATE;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, receiver);
    }

    deleteReceiver = async (receiverId) => {
        let url = RECEIVER_API.DELETE;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            receiverId: receiverId
        })
    }

    getWarningByDeviceId = async (receiverId, deviceId) => {
        let url = RECEIVER_API.GET + "/" + receiverId + "/" + deviceId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }


}

export default new ReceiverService();