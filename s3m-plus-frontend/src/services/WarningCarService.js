import commonService from './CommonService';
import WARNING_CAR_API from "../constants/warning_car_api";
import CONS from "../constants/constant";

class WarningCarService {
    #type;
    #commonService

    constructor() {
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
    }

    getWarningCars = async (customerId, systemTypeId, projectId, fromDate, toDate, page, deviceId) => {
        let url = `${WARNING_CAR_API.GET_WARNING_CAR}`;
        let type = this.#type.GET;
        const requestData = {
            customerId,
            systemTypeId,
            projectId,
            page,
            deviceId
        };

        // Add fromDate and toDate to the request data if provided
        if (fromDate) {
            requestData.fromDate = fromDate;
        }

        if (toDate) {
            requestData.toDate = toDate;
        }

        return await this.#commonService.sendRequest(type, url, requestData);
    };

    getWarningCarById = async (customerId, warningCarId) => {
        let url = `${WARNING_CAR_API.GET_WARNING_CAR_BY_ID}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            warningCarId
        });
    }


    addWarningCar = async (customerId, data) => {
        let url = `${WARNING_CAR_API.ADD_WARNING_CAR}` + customerId;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, data);
    }

    updateWarningCar = async (customerId, data) => {
        let url = `${WARNING_CAR_API.UPDATE_WARNING_CAR}` + customerId;
        let type = this.#type.PUT;
        return await this.#commonService.sendRequest(type, url, data);
    }
}

export default new WarningCarService();