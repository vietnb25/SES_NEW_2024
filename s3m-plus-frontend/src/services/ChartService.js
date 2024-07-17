import commonService from './CommonService';
import CHART_API from "../constants/chart_api";
import CONS from "../constants/constant";

class ChartService {
    #type;
    #commonService

    constructor() {
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
    }

    getChartLoad = async (customerId, projectId, typeTime, fromDate, toDate, deviceId, systemTypeId, ids) => {
        let url = `${CHART_API.GET_CHART_LOAD}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            projectId,
            typeTime,
            fromDate,
            toDate,
            deviceId,
            systemTypeId,
            ids
        });
    }

    getChartLoadPower = async (customerId, projectId, typeTime, fromDate, toDate, deviceId, systemTypeId, ids) => {
        let url = `${CHART_API.GET_CHART_LOAD_POWER}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            projectId,
            typeTime,
            fromDate,
            toDate,
            deviceId,
            systemTypeId,
            ids
        });
    }

    getChartLoadCost = async (customerId, projectId, typeTime, fromDate, toDate, deviceId, systemTypeId, ids) => {
        let url = `${CHART_API.GET_CHART_LOAD_COST}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            projectId,
            typeTime,
            fromDate,
            toDate,
            deviceId,
            systemTypeId,
            ids
        });
    }

    getChartLoadHeat = async (customerId, projectId, typeTime, fromDate, toDate, deviceId, systemTypeId, ids) => {
        let url = `${CHART_API.GET_CHART_LOAD_HEAT}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            projectId,
            typeTime,
            fromDate,
            toDate,
            deviceId,
            systemTypeId,
            ids
        });
    }

    getChartSolar = async (customerId, projectId, typeTime, fromDate, toDate, deviceId) => {
        let url = `${CHART_API.GET_CHART_SOLAR}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            projectId,
            typeTime,
            fromDate,
            toDate,
            deviceId
        });
    }

    getChartSolarPower = async (customerId, projectId, typeTime, fromDate, toDate, deviceId) => {
        let url = `${CHART_API.GET_CHART_SOLAR_POWER}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            projectId,
            typeTime,
            fromDate,
            toDate,
            deviceId
        });
    }

    getChartSolarCost = async (customerId, projectId, typeTime, fromDate, toDate, deviceId) => {
        let url = `${CHART_API.GET_CHART_SOLAR_COST}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            projectId,
            typeTime,
            fromDate,
            toDate,
            deviceId
        });
    }

    getChartSolarHeat = async (customerId, projectId, typeTime, fromDate, toDate, deviceId) => {
        let url = `${CHART_API.GET_CHART_SOLAR_HEAT}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            projectId,
            typeTime,
            fromDate,
            toDate,
            deviceId
        });
    }

    getChartGrid = async (customerId, projectId, typeTime, fromDate, toDate, deviceId) => {
        let url = `${CHART_API.GET_CHART_GRID}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            projectId,
            typeTime,
            fromDate,
            toDate,
            deviceId
        });
    }

    getChartGridPower = async (customerId, projectId, typeTime, fromDate, toDate, deviceId) => {
        let url = `${CHART_API.GET_CHART_GRID_POWER}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            projectId,
            typeTime,
            fromDate,
            toDate,
            deviceId
        });
    }

    getChartGridCost = async (customerId, projectId, typeTime, fromDate, toDate, deviceId) => {
        let url = `${CHART_API.GET_CHART_GRID_COST}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            projectId,
            typeTime,
            fromDate,
            toDate,
            deviceId
        });
    }

    getChartGridHeat = async (customerId, projectId, typeTime, fromDate, toDate, deviceId) => {
        let url = `${CHART_API.GET_CHART_GRID_HEAT}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            projectId,
            typeTime,
            fromDate,
            toDate,
            deviceId
        });
    }

    exportDataChartLoad = async (customerId, projectId, typeTime, fromDate, toDate, deviceId, systemTypeId, ids) => {
        let url = `${CHART_API.EXPORT_DATA_CHART_LOAD}`;
        let type = this.#type.DOWNLOAD_NEW;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            projectId,
            typeTime,
            fromDate,
            toDate,
            deviceId,
            systemTypeId,
            ids
        });
    }

    exportDataChartPower = async (customerId, projectId, typeTime, fromDate, toDate, deviceId, systemTypeId, ids) => {
        let url = `${CHART_API.EXPORT_DATA_CHART_POWER}`;
        let type = this.#type.DOWNLOAD_NEW;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            projectId,
            typeTime,
            fromDate,
            toDate,
            deviceId,
            systemTypeId,
            ids
        });
    }

    exportDataChartCost = async (customerId, projectId, typeTime, fromDate, toDate, deviceId, systemTypeId, ids) => {
        let url = `${CHART_API.EXPORT_DATA_CHART_COST}`;
        let type = this.#type.DOWNLOAD_NEW;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            projectId,
            typeTime,
            fromDate,
            toDate,
            deviceId,
            systemTypeId,
            ids
        });
    }

    getDataTemperature = async (customerId, projectId, typeTime, fromDate, toDate, deviceId, systemTypeId, ids) => {
        let url = `${CHART_API.GET_CHART_TEMPERATURE}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            projectId,
            typeTime,
            fromDate,
            toDate,
            deviceId,
            systemTypeId,
            ids
        });
    }

    getChartSankey = async (customerId, projectId, typeTime, fromDate, toDate, deviceId, systemTypeId, ids) => {
        let url = `${CHART_API.GET_CHART_SANKEY}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            projectId,
            typeTime,
            fromDate,
            toDate,
            deviceId,
            systemTypeId,
            ids
        });
    }

    getDataDischargeIndicator = async (customerId, projectId, typeTime, fromDate, toDate, deviceId, systemTypeId, ids) => {
        let url = `${CHART_API.GET_CHART_DISCHARGE_INDICATOR}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            projectId,
            typeTime,
            fromDate,
            toDate,
            deviceId,
            systemTypeId,
            ids,
        });
    }

    getChartLoadCompare = async (customerId, projectId, typeTime, fromDate, toDate, deviceId, systemTypeId, ids) => {
        let url = `${CHART_API.GET_CHART_LOAD_COMPARE}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            projectId,
            typeTime,
            fromDate,
            toDate,
            deviceId,
            systemTypeId,
            ids
        });
    }

    getChartEnergyPlan = async (customerId, projectId, time, typeModule, fDate, tDate, deviceId, ids) => {
        let url = `${CHART_API.GET_CHART_ENERGY_PLAN}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId,
            projectId,
            time,
            typeModule,
            fDate,
            tDate,
            deviceId,
            ids
        });
    }
}

export default new ChartService();