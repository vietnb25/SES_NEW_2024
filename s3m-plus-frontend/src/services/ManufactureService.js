import commonService from './CommonService';
import MANUFACTURE_API from "../constants/manufacture_api";
import CONS from "../constants/constant";

class ManufactureService {
    #type;
    #commonService

    constructor() {
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
    }

    getManufactures = async (customerId, systemTypeId, projectId, fromDate, toDate, deviceIds, productionId, productionStepId) => {
        let url = `${MANUFACTURE_API.GET_MANUFACTURES}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            systemTypeId,
            projectId,
            fromDate,
            toDate,
            deviceIds,
            productionId,
            productionStepId
        });
    }

    getDataPqsManufactures = async (customerId, fromDate, toDate, deviceId) => {
        let url = `${MANUFACTURE_API.GET_DATA_PQS_MANUFACTURES}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            fromDate,
            toDate,
            deviceId,
        });
    }

    addManufactures = async (customerId, data) => {
        let url = `${MANUFACTURE_API.ADD_MANUFACTURES}` + customerId;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, data);
    }

    exportManufactures = async (prefix, devices, time) => {
        let url = MANUFACTURE_API.EXPORT_REPORT;
        let type = this.#type.DOWNLOAD_NEW;
        return await this.#commonService.sendRequest(type, url, {
            prefix, devices, time
        });
    }

    getListManufacturesShift = async (customer, project, production, productionStep) => {
        let url = MANUFACTURE_API.GET_LIST_MANUFACTURE_SHIFT;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url,
            {
                customer: customer,
                project: project,
                production: production,
                productionStep: productionStep,
            }
        );
    }

    getDataByProductionStep = async (customer, project, production, productionStep) => {
        let url = MANUFACTURE_API.GET_LIST_MANUFACTURE_PRODUCTION;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url,
            {
                customer: customer,
                project: project,
                production: production,
                productionStep: productionStep
            }
        );
    }

    addManufacturesAndProdutionStep = async (customerId, data) => {
        let url = `${MANUFACTURE_API.INSERT_MANUFACTURE_PRODUCTION}` +"/"+ customerId;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, data);
    }

    getDataEpByShiftAndViewTime = async (customer, project, devices,shift,fromDate,toDate ) => {
        let url = MANUFACTURE_API.GET_LIST_DATA_EP_BY_SHIFT_AND_VIEW_TIME;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url,
            {
                customer: customer,
                project: project,
                viewTime: null,
                devices: devices,
                shift: shift,
                fromDate: fromDate,
                toDate: toDate
            }
        );
    }
    getDataManufactureDetailByManufactureAndViewTime = async (customer, manufacture,fromDate,toDate ) => {
        let url = MANUFACTURE_API.GET_LIST_MANUFACTURE_DETAIL_BY_MANUFACTURE_AND_VIEW_TIME;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url,
            {
                customer: customer,
                manufacture: manufacture,
                fromDate: fromDate,
                toDate: toDate,
            }
        );
    }
    addOrUpdateManufactureDetail = async (customer, data) => {
        let url = MANUFACTURE_API.ADD_OR_UPDATE_MANUFACTURE_DETAIL + '?customer=' + customer;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url,data);
    }
    updateOrUpdateManufactureDetailRevenue = async (customer, data) => {
        let url = MANUFACTURE_API.UPDATE_OR_UPDATE_MANUFACTURE_DETAIL_REVENUE + '?customer=' + customer;
        let type = this.#type.PUT;
        return await this.#commonService.sendRequest(type, url,data);
    }
    deleteManufacture = async (customer, id) => {
        let url = MANUFACTURE_API.DELETE_MANUFACTURE + '?customer=' + customer + "&id=" + id;
        let type = this.#type.DELETE;
        return await this.#commonService.sendRequest(type, url,{});
    }

    dowloadMaufactureDetail = async (prefix, time,production) => {
        let url = MANUFACTURE_API.DOWLOAD_MANUFACTURE_DETTAIL;
        let type = this.#type.DOWNLOAD_NEW;
        return await this.#commonService.sendRequest(type, url,
            {
                prefix: prefix,
                time: time,
                production:production,
            }
        );
    }
}

export default new ManufactureService();