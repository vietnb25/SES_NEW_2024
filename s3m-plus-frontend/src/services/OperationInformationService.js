import BASE_API_OPERATION_INFORMATION from '../constants/operation_information_constant';
import CONS from './../constants/constant';
import commonService from './CommonService';

class OperationInformationService {
    #type;
    #commonService

    constructor() {
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
    }


    // Service xử lý Load

    getInstantOperationInformation = (customerId, deviceId) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.INSTANT + customerId + "/" + deviceId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getDevices = (projectId, systemType) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.DEVICES_BY_PROJECT + projectId + "/" + systemType;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getOperationInformation = (customerId, deviceId, fromDate, toDate, page) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.LIST + customerId + "/" + deviceId + `/${page}`;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, { fromDate, toDate });
    }

    downloadElectricalParam = (customerId, deviceId, fromDate, toDate, userName) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.DOWNLOAD_ELECTRICAL_PARAM + customerId+ "/" + deviceId;
        let type = this.#type.DOWNLOAD;
        return this.#commonService.sendRequest(type, url, {
            fromDate: fromDate,
            toDate: toDate,
            userName: userName
        });
    }

    downloadTemperatureParam = (customerId, deviceId, fromDate, toDate, userName) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.DOWNLOAD_TEMPERATURE_PARAM + customerId+ "/" + deviceId;
        let type = this.#type.DOWNLOAD;
        return this.#commonService.sendRequest(type, url, {
            fromDate: fromDate,
            toDate: toDate,
            userName: userName
        });
    }

    downloadPowerQuality = (customerId, deviceId, fromDate, toDate, userName) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.DOWNLOAD_POWER_QUALITY + customerId+ "/" + deviceId;
        let type = this.#type.DOWNLOAD;
        return this.#commonService.sendRequest(type, url, {
            fromDate: fromDate,
            toDate: toDate,
            userName: userName
        });
    }

    getInstantPowerQuality = (customerId, deviceId) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.INSTANT_POWER_QUALITY + customerId + "/" + deviceId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getPowerQuality = (customerId, deviceId, fromDate, toDate, page) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.POWER_QUALITIES + customerId+ "/" + deviceId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, { fromDate, toDate, page });
    }

    getWarningOperation = (customerId, deviceId, warningType, fromDate, toDate) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.OPERATING_WARNING + customerId+ "/" + deviceId + "?warningType=" + warningType + "&fromDate=" + fromDate + "&toDate=" + toDate;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getStatusWarning = (customerId, deviceId, warningType, fromDate, toDate) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.STATUS_WARNING + customerId+ "/" + deviceId + "?warningType=" + warningType + "&fromDate=" + fromDate + "&toDate=" + toDate;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getDataChart = (customerId, deviceId, fromDate, toDate, pqsViewType, chartType) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.CHART + customerId+ "/" + deviceId + "?fromDate=" + fromDate + "&toDate=" + toDate + "&pqsViewType=" + pqsViewType + "&chartType=" + chartType;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    downloadDataChart = (customerId, deviceId, fromDate, toDate, pqsViewType, chartType, userName) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.DOWNLOAD_CHART + customerId+ "/" + deviceId;
        let type = this.#type.DOWNLOAD;
        return this.#commonService.sendRequest(type, url, {
            fromDate : fromDate,
            toDate : toDate,
            pqsViewType: pqsViewType,
            chartType: chartType,
            userName: userName
        });
    }

    getDataChartHarmonic = (customerId, deviceId, params) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.HARMONIC + customerId+ "/" + deviceId;
        let type = this.#type.POST;
        return this.#commonService.sendRequest(type, url, params);
    }

    getDataChartHarmonicByDay = (customerId, deviceId, params, day) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.HARMONIC_BY_DAY + customerId+ "/" + deviceId + "?day=" + day;
        let type = this.#type.POST;
        return this.#commonService.sendRequest(type, url, params);
    }

    getDataChartHarmonicPeriod = (customerId, deviceId, fromDate, toDate) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.HARMONIC_PERIOD + customerId+ "/" + deviceId + "?fromDate=" + fromDate + "&toDate=" + toDate;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    showDataWarning = async (customerId, warningType, fromDate, toDate, deviceId, page) => {
        let url = `${BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.OPERATING_WARNING + "detail/" + customerId}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            warningType,
            fromDate,
            toDate,
            deviceId,
            page
        });
    }

    getDetailOperatingWarningCache = async (customerId, warningCacheId) => {
        let url = `${BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.UPDATE_OPERATING_WARNING}/${customerId}/${warningCacheId}`;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    updateOperatingWarningCache = async (customerId, data) => {
        let url = `${BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.UPDATE_OPERATING_WARNING}/${customerId}/${data.id}`;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, data);
    }

    download = async (customerId, warningType, fromDate, toDate, deviceId, userName) => {
        let url = `${BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.DOWNLOAD}/${customerId}`;
        let type = this.#type.DOWNLOAD;
        return await this.#commonService.sendRequest(type, url, {
            warningType,
            fromDate,
            toDate,
            deviceId,
            userName
        });
    }

    downloadWarning = async (customerId, deviceId, warningType, fromDate, toDate, userName) => {
        let url = `${BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.DOWNLOAD_WARNING}/${customerId}`;
        let type = this.#type.DOWNLOAD;
        return await this.#commonService.sendRequest(type, url, {
            warningType,
            fromDate,
            toDate,
            deviceId,
            userName
        });
    }

    // Service xử lý PV

    /**
     * Thông số cài đặt
     */
    getOperationSettingInverter = (customerId, deviceId) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.SETTING_INVERTER_PV + customerId + "/" + deviceId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }


    /**
     * 4. INVERTER
     */
    getInstantOperationInverterPV = (customerId, deviceId) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.INSTANT_INVERTER_PV + customerId + "/" + deviceId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getOperationInverterPV = (customerId, deviceId, fromDate, toDate, page) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.LIST_INVERTER_PV + customerId + "/" + deviceId + `/${page}` + `?fromDate=${fromDate}` + `&toDate=${toDate}`;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getDataChartInverterPV = (customerId, deviceId, fromDate, toDate) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.CHART_INVERTER + customerId + "/" + deviceId + `?fromDate=${fromDate}` + `&toDate=${toDate}`;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getDataChartElectricalPowerInverterPV = (customerId, deviceId, date, typeData) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.CHART_INVERTER_ELECTRICAL_POWER + customerId + "/" + deviceId + `?date=${date}` + `&type=${typeData}`;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    downloadDeviceParameterInverterPV = (customerId, deviceId, fromDate, toDate, userName) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.DOWNLOAD_DEVICE_PARAMETER_INVERTER_PV + customerId + "/" + deviceId + "?fromDate=" + fromDate + "&toDate=" + toDate + "&userName=" + userName;
        let type = this.#type.DOWNLOAD;
        return this.#commonService.sendRequest(type, url, null);
    }

    downloadChartInverterPV = (customerId, deviceId, fromDate, toDate, typeData, userName) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.DOWNLOAD_CHART_INVERTER_PV + customerId + "/" + deviceId + "/" + typeData + "?fromDate=" + fromDate + "&toDate=" + toDate + "&userName=" + userName;
        let type = this.#type.DOWNLOAD;
        return this.#commonService.sendRequest(type, url, null);
    }

    downloadChartElectricalPowerInverterPV = (customerId, deviceId, date, typePQS, userName) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.DOWNLOAD_CHART_ELECTRICAL_POWER_INVERTER_PV + customerId + "/" + deviceId + "/" + typePQS + "?date=" + date + "&userName=" + userName;
        let type = this.#type.DOWNLOAD;
        return this.#commonService.sendRequest(type, url, null);
    }

    /**
     * 5. WEATHER
     */
    getInstantOperationWeatherPV = (customerId, deviceId) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.INSTANT_WEATHER_PV + customerId + "/" + deviceId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getOperationWeatherPV = (customerId, deviceId, fromDate, toDate, page) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.LIST_WEATHER_PV + customerId + "/" + deviceId + `/${page}` + `?fromDate=${fromDate}` + `&toDate=${toDate}`;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getDataChartWeatherPV = (customerId, deviceId, fromDate, toDate) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.CHART_WEATHER_PV + customerId + "/" + deviceId + `?fromDate=${fromDate}` + `&toDate=${toDate}`;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    downloadDeviceParameterWeatherPV = (customerId, deviceId, fromDate, toDate, userName) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.DOWNLOAD_DEVICE_PARAMETER_WEATHER_PV + customerId + "/" + deviceId + "?fromDate=" + fromDate + "&toDate=" + toDate + "&userName=" + userName;
        let type = this.#type.DOWNLOAD;
        return this.#commonService.sendRequest(type, url, null);
    }

    downloadChartWeatherPV = (customerId, deviceId, fromDate, toDate, typeData, userName) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.DOWNLOAD_CHART_WEATHER_PV + customerId + "/" + deviceId + "/" + typeData + "?fromDate=" + fromDate + "&toDate=" + toDate + "&userName=" + userName;
        let type = this.#type.DOWNLOAD;
        return this.#commonService.sendRequest(type, url, null);
    }

    /**
    * 6. COMBINER
    */
    getInstantOperationCombinerPV = (customerId, deviceId) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.INSTANT_COMBINER_PV + customerId + "/" + deviceId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getOperationCombinerPV = (customerId, deviceId, fromDate, toDate, page) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.LIST_COMBINER_PV + customerId + "/" + deviceId + `/${page}` + `?fromDate=${fromDate}` + `&toDate=${toDate}`;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getDataChartCombinerPV = (customerId, deviceId, fromDate, toDate) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.CHART_COMBINER_PV + customerId + "/" + deviceId + `?fromDate=${fromDate}` + `&toDate=${toDate}`;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    downloadDeviceParameterCombinerPV = (customerId, deviceId, fromDate, toDate, userName) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.DOWNLOAD_DEVICE_PARAMETER_COMBINER_PV + customerId + "/" + deviceId + "?fromDate=" + fromDate + "&toDate=" + toDate + "&userName=" + userName;
        let type = this.#type.DOWNLOAD;
        return this.#commonService.sendRequest(type, url, null);
    }

    downloadChartCombinerPV = (customerId, deviceId, fromDate, toDate, typeData, userName) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.DOWNLOAD_CHART_COMBINER_PV + customerId + "/" + deviceId + "/" + typeData + "?fromDate=" + fromDate + "&toDate=" + toDate + "&userName=" + userName;
        let type = this.#type.DOWNLOAD;
        return this.#commonService.sendRequest(type, url, null);
    }

    /**
    * 7. STRING
    */
    getInstantOperationStringPV = (customerId, deviceId) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.INSTANT_STRING_PV + customerId + "/" + deviceId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getOperationStringPV = (customerId, deviceId, fromDate, toDate, page) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.LIST_STRING_PV + customerId + "/" + deviceId + `/${page}` + `?fromDate=${fromDate}` + `&toDate=${toDate}`;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getDataChartStringPV = (customerId, deviceId, fromDate, toDate) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.CHART_STRING_PV + customerId + "/" + deviceId + `?fromDate=${fromDate}` + `&toDate=${toDate}`;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    downloadDeviceParameterStringPV = (customerId, deviceId, fromDate, toDate, userName) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.DOWNLOAD_DEVICE_PARAMETER_STRING_PV + customerId + "/" + deviceId + "?fromDate=" + fromDate + "&toDate=" + toDate + "&userName=" + userName;
        let type = this.#type.DOWNLOAD;
        return this.#commonService.sendRequest(type, url, null);
    }

    downloadChartStringPV = (customerId, deviceId, fromDate, toDate, typeData, userName) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.DOWNLOAD_CHART_STRING_PV + customerId + "/" + deviceId + "/" + typeData + "?fromDate=" + fromDate + "&toDate=" + toDate + "&userName=" + userName;
        let type = this.#type.DOWNLOAD;
        return this.#commonService.sendRequest(type, url, null);
    }

    /**
    * 8. PANEL
    */
    getInstantOperationPanelPV = (customerId, deviceId) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.INSTANT_PANEL_PV + customerId + "/" + deviceId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getOperationPanelPV = (customerId, deviceId, fromDate, toDate, page) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.LIST_PANEL_PV + customerId + "/" + deviceId + `/${page}` + `?fromDate=${fromDate}` + `&toDate=${toDate}`;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getDataChartPanelPV = (customerId, deviceId, fromDate, toDate) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.CHART_PANEL_PV + customerId + "/" + deviceId + `?fromDate=${fromDate}` + `&toDate=${toDate}`;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    downloadDeviceParameterPanelPV = (customerId, deviceId, fromDate, toDate, userName) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.DOWNLOAD_DEVICE_PARAMETER_PANEL_PV + customerId + "/" + deviceId + "?fromDate=" + fromDate + "&toDate=" + toDate + "&userName=" + userName;
        let type = this.#type.DOWNLOAD;
        return this.#commonService.sendRequest(type, url, null);
    }

    downloadChartPanelPV = (customerId, deviceId, fromDate, toDate, typeData, userName) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.DOWNLOAD_CHART_PANEL_PV + customerId + "/" + deviceId + "/" + typeData + "?fromDate=" + fromDate + "&toDate=" + toDate + "&userName=" + userName;
        let type = this.#type.DOWNLOAD;
        return this.#commonService.sendRequest(type, url, null);
    }

    /**
     * 9. RmuDrawer
     */
    getInstantOperationRmuDrawerGrid = (customerId, deviceId) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.INSTANT_RMU_DRAWER_GRID + customerId + "/" + deviceId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getOperationRmuDrawerGrid = (customerId, deviceId, fromDate, toDate, page) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.LIST_RMU_DRAWER_GRID + customerId + "/" + deviceId + `/${page}` + `?fromDate=${fromDate}` + `&toDate=${toDate}`;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }
    
    downloadDeviceParameterRmuDrawerGrid = (customerId, deviceId, fromDate, toDate, typeData, userName) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.DOWNLOAD_DEVICE_PARAMETER_RMU_DRAWER_GRID + customerId + "/" + deviceId + "/" + typeData + "?fromDate=" + fromDate + "&toDate=" + toDate + "&userName=" + userName;
        let type = this.#type.DOWNLOAD;
        return this.#commonService.sendRequest(type, url, null);
    }

    getDataChartRmuDrawerGrid = (customerId, deviceId, fromDate, toDate) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.CHART_RMU_DRAWER_GRID + customerId + "/" + deviceId  + `?fromDate=${fromDate}` + `&toDate=${toDate}`;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getDataChartElectricalPowerRmuDrawerGrid = (customerId, deviceId, date, typeData) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.CHART_RMU_DRAWER_GRID_ELECTRICAL_POWER + customerId + "/" + deviceId + `?date=${date}` + `&type=${typeData}`;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    downloadDataChartgRmuDrawerGrid = (customerId, deviceId, fromDate, toDate, pqsViewType, chartType, userName) => {
        let url = BASE_API_OPERATION_INFORMATION.OPERATION_INFORMATION.DOWNLOAD_CHART_RMU_DRAWER + customerId+ "/" + deviceId;
        let type = this.#type.DOWNLOAD;
        return this.#commonService.sendRequest(type, url, {
            fromDate : fromDate,
            toDate : toDate,
            pqsViewType: pqsViewType,
            chartType: chartType,
            userName: userName
        });
    }

}

export default new OperationInformationService();
