import commonService from './CommonService';
import CONS from './../constants/constant';
import HOME_API from './../constants/home_api';
import authService from './AuthService';

class HomeService {
    #type;
    #commonService;
    #authService;

    constructor() {
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
        this.#authService = authService;
    }

    initHomeMonitorData = () => {
        let url = HOME_API.INIT + this.#authService.getAuth().username;
        return this.#commonService.sendRequest(this.#type.GET, url, null);
    }

    initData = () => {
        let url = HOME_API.HOME + "/" + this.#authService.getAuth().username;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getProjectLocations = () => {
        let url = HOME_API.HOME + "/" + this.#authService.getAuth().username + "/project-marker";
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getProjectById = (customerId, projectId) => {
        let url = HOME_API.HOME + "/project/" + customerId + "/" + projectId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getProjectTree = () => {
        let url = HOME_API.HOME + "/" + this.#authService.getAuth().username + "/project-tree";
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getSuperManagerTree = (customerId) => {
        let url = HOME_API.SUPER_MANAGER + customerId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getManagerTree = (customerId, superManagerId) => {
        let url = HOME_API.MANAGER + customerId + "/" + superManagerId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getAreaTree = (customerId, superManagerId, managerId) => {
        let url = HOME_API.AREA + customerId + "/" + superManagerId + "/" + managerId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getProjectByTree = (customerId, superManagerId, managerId, areaId) => {
        let url = HOME_API.PROJECT + customerId + "/" + superManagerId + "/" + managerId + "/" + areaId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getAllCustomer = () => {
        let url = HOME_API.ALL;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getSystemTypeByProject = (customerId, projectId) => {
        let url = HOME_API.PROJECT_CHART + customerId + "/" + projectId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    drawChart = (data) => {
        let url = HOME_API.CHART;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, data);
    }

    chartCustomer = (data) => {
        let url = HOME_API.CHART_CUSTOMER;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, data);
    }

    chartSuperManager = (data) => {
        let url = HOME_API.CHART_SUPER_MANAGER;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, data);
    }

    chartManager = (data) => {
        let url = HOME_API.CHART_MANAGER;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, data);
    }

    chartArea = (data) => {
        let url = HOME_API.CHART_AREA;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, data);
    }
    downloadDataChart = (data) => {
        let url = HOME_API.DOWNLOAD;
        let type = this.#type.DOWNLOAD;
        return this.#commonService.sendRequest(type, url, data);
    }

    chartAll = (data) => {
        let url = HOME_API.CHART_ALL;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, data);
    }

    downloadChartCustomer = (data) => {
        let url = HOME_API.DOWNLOAD_CUSTOMER;
        let type = this.#type.DOWNLOAD;
        return this.#commonService.sendRequest(type, url, data);
    }
    downloadChartSuperManager = (data) => {
        let url = HOME_API.DOWNLOAD_SUPER_MANAGER;
        let type = this.#type.DOWNLOAD;
        return this.#commonService.sendRequest(type, url, data);
    }
    downloadChartManager = (data) => {
        let url = HOME_API.DOWNLOAD_MANAGER;
        let type = this.#type.DOWNLOAD;
        return this.#commonService.sendRequest(type, url, data);
    }
    downloadChartArea = (data) => {
        let url = HOME_API.DOWNLOAD_AREA;
        let type = this.#type.DOWNLOAD;
        return this.#commonService.sendRequest(type, url, data);
    }
    downloadChartAll = (data) => {
        let url = HOME_API.DOWNLOAD_ALL;
        let type = this.#type.DOWNLOAD;
        return this.#commonService.sendRequest(type, url, data);
    }

    getProjectLocationsHomePage = () => {
        let url = HOME_API.HOME_PAGE + "/" + this.#authService.getAuth().username + "/project-marker";
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getProjectLocationsHomePageByCustomerId = (customerId, value, fromDate, toDate, ids) => {
        let url = HOME_API.HOME_PAGE + "/project-marker-customer" + "/" + customerId + "/" + value;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, { fromDate: fromDate, toDate: toDate, ids: ids });
    }

    initHomeMonitorDataHomePage = () => {
        let url = HOME_API.INIT_HOME_PAGE + this.#authService.getAuth().username;
        return this.#commonService.sendRequest(this.#type.GET, url, null);
    }

    getSystemTypeByProjectId = (projectId) => {
        let url = HOME_API.GET_SYSTEM_TYPE_BY_SITE + projectId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }
    getSystemLoad = (customerId) => {
        let url = HOME_API.GET_SYSTEM_LOAD + "/" + customerId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }
    getSystemPv = (customerId) => {
        let url = HOME_API.GET_SYSTEM_SOLAR + "/" + customerId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }
    getSystemGrid = (customerId) => {
        let url = HOME_API.GET_SYSTEM_GRID + "/" + customerId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }
    getSiteBySystemType = (systemTypeId, customerId) => {
        let url = HOME_API.GET_SITE_BY_SYSTEM_TYPE + customerId + "/" + systemTypeId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getCustomerIdFirstTime = () => {
        let url = HOME_API.GET_CUSTOMER_ID;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getSystemLoadByCustomerId = (customerId) => {
        let url = HOME_API.GET_SYSTEM_LOAD_BY_CUSTOMER + "/" + customerId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getSystemSolarByCustomerId = (customerId) => {
        let url = HOME_API.GET_SYSTEM_SOLAR_BY_CUSTOMER + "/" + customerId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getSystemGridByCustomerId = (customerId) => {
        let url = HOME_API.GET_SYSTEM_GRID_BY_CUSTOMER + "/" + customerId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getWarningByProject = (projectId, customerId) => {
        let url = HOME_API.GET_WARNING_BY_PROJECT_ID;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, { projectId, customerId });
    }

    getDataProject = (customerId, projectId) => {
        let url = HOME_API.GET_DATA_BY_PROJECT_ID + "/" + customerId + "/" + projectId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getListData = (customerId, projectId, time, isActiveButton, ids) => {
        let url = HOME_API.GET_LIST_DATA;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, { customerId: customerId, projectId: projectId, time: time, type: isActiveButton, ids: ids });
    }

    getListDataPowerByTime = (customerId, projectId, time, isActiveButton) => {
        let url = HOME_API.GET_LIST_DATA_POWER_BY_TIME;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, { customerId: customerId, projectId: projectId, time: time, type: isActiveButton });
    }

    getDataTab1 = (customerId, projectId, time, typeModule, fDate, tDate, ids) => {
        let url = HOME_API.GET_DATA_TAB_1;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, { customerId: customerId, projectId: projectId, time: time, type: typeModule, fDate: fDate, tDate: tDate, ids: ids });
    }

    getDataTab2 = (customerId, projectId, time, typeModule, fDate, tDate, ids) => {
        let url = HOME_API.GET_DATA_TAB_2;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, { customerId: customerId, projectId: projectId, time: time, type: typeModule, fDate: fDate, tDate: tDate, ids: ids });
    }

    getDataTab3 = (customerId, projectId, time, fDate, ids) => {
        let url = HOME_API.GET_DATA_TAB_3;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, { customerId: customerId, projectId: projectId, time: time, fDate: fDate, ids: ids });
    }

    getDataTab4 = (customerId, projectId, time, typeModule, fDate, tDate, ids) => {
        let url = HOME_API.GET_DATA_TAB_4;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, { customerId: customerId, projectId: projectId, time: time, type: typeModule, fDate: fDate, tDate: tDate, ids: ids });
    }

    getDataTab5 = (customerId, projectId, typeFil, typeModule, ids) => {
        let url = HOME_API.GET_DATA_TAB_5;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, { customerId: customerId, projectId: projectId, typeFil: typeFil, type: typeModule, ids: ids });
    }
    getDataTab6 = async (customerId, projectId, time, typeModule, fromDate, toDate, ids) => {
        let url = HOME_API.GET_DATA_TAB_6;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, {
            customerId: customerId,
            projectId: projectId,
            time: time,
            type: typeModule,
            fromDate: fromDate,
            toDate: toDate,
            ids: ids
        });
    }
    getDataFlowSensor = (customerId, projectId, time, typeModule, fDate, tDate, ids, fuelFormId) => {
        let url = HOME_API.GET_DATA_TAB_7_8;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, { customerId: customerId, projectId: projectId, time: time, type: typeModule, fDate: fDate, tDate: tDate, ids: ids, fuelFormId: fuelFormId });
    }
    exportDataTab1 = (customerId, projectId, time, typeModule, fDate, tDate, ids) => {
        let url = HOME_API.EXPORT_DATA_TAB_1;
        let type = this.#type.DOWNLOAD_NEW;
        return this.#commonService.sendRequest(type, url, { customerId: customerId, projectId: projectId, time: time, type: typeModule, fDate: fDate, tDate: tDate, ids: ids });
    }

    exportDataTab6 = (customerId, projectId, time, typeModule, fDate, tDate, ids) => {
        let url = HOME_API.EXPORT_DATA_TAB_6;
        let type = this.#type.DOWNLOAD_NEW;
        return this.#commonService.sendRequest(type, url, { customerId: customerId, projectId: projectId, time: time, type: typeModule, fromDate: fDate, toDate: tDate, ids: ids });
    }
}

export default new HomeService();