import authService from './AuthService';
import REPORT_API from '../constants/report_api';
import CONS from "../constants/constant";
import commonService from "./CommonService";
import { data } from 'jquery';

class ReportService {
    #type;
    #commonService;
    #auth;
    constructor() {
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
    }

    getReport = async (userName, projectId) => {
        let url = REPORT_API.LIST + userName + "/" + projectId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    getReportPV = async (userName, customerId, projectId) => {
        let url = REPORT_API.LISTPV + customerId + "/" + userName + "/" + projectId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    getReportGrid = async (userName, projectId) => {
        let url = REPORT_API.LISTGRID + userName + "/" + projectId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    generateReports = async (data, userName, customerId, user) => {
        let url = REPORT_API.GENERATEREPORT + customerId + "/" + data.deviceId + "/" + data.reportType + "/" + data.date + "/" + data.dateType + "/" + userName + "/" + data.projectId;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, user);
    }

    generateReportsPV = async (data, userName, customerId, user) => {
        let url = REPORT_API.GENERATEREPORTPV + customerId + "/" + data.reportType + "/" + data.date + "/" + userName + "/" + data.projectId + "/" + data.deviceId;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, user);
    }

    generateReportsGrid = async (data, userName, customerId, user) => {
        let url = REPORT_API.GENERATEREPORTGRID + customerId + "/" + data.deviceId + "/" + data.reportType + "/" + data.date + "/" + data.dateType + "/" + userName + "/" + data.projectId;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, user);
    }

    downloadReport = async (path) => {
        let url = REPORT_API.DOWNLOAD_REPORT;
        let type = this.#type.DOWNLOAD;
        return await this.#commonService.sendRequest(type, url, { path });
    }
    downloadReportPV = async (path) => {
        let url = REPORT_API.DOWNLOAD_REPORTPV;
        let type = this.#type.DOWNLOAD;
        return await this.#commonService.sendRequest(type, url, { path });
    }

    downloadReportGrid = async (path) => {
        let url = REPORT_API.DOWNLOAD_REPORTGRID;
        let type = this.#type.DOWNLOAD;
        return await this.#commonService.sendRequest(type, url, { path });
    }

    deleteReport = async (id) => {
        let url = REPORT_API.DELETE_REPORT + id;
        let type = this.#type.DELETE;
        return await this.#commonService.sendRequest(type, url, null);
    }

    deleteReportPV = async (id) => {
        let url = REPORT_API.DELETE_REPORTPV + id;
        let type = this.#type.DELETE;
        return await this.#commonService.sendRequest(type, url, null);
    }

    deleteReportGrid = async (id) => {
        let url = REPORT_API.DELETE_REPORTGRID + id;
        let type = this.#type.DELETE;
        return await this.#commonService.sendRequest(type, url, null);
    }

    addReport = async (data, userName, customerId) => {
        let url = REPORT_API.ADDREPORT + customerId + "/" + data.deviceId + "/" + data.reportType + "/" + data.date + "/" + userName + "/" + data.projectId + "/" + data.dateType;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    addReportPV = async (data, userName, customerId) => {
        let url = REPORT_API.ADDREPORTPV + customerId + "/" + data.reportType + "/" + data.date + "/" + userName + "/" + data.projectId + "/" + data.deviceId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    addReportGrid = async (data, userName, customerId) => {
        console.log("data:", data);
        let url = REPORT_API.ADDREPORTGRID + customerId + "/" + data.deviceId + "/" + data.reportType + "/" + data.date + "/" + userName + "/" + data.projectId + "/" + data.dateType;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    downloadNewReport = async (typeInfor, reportInfor, customerInfor, devicesInfor, timeInfor, siteModuleInfor, categoryInfor, shiftInfor, unitInfor, listInfor) => {
        let url = REPORT_API.DOWNLOAD_NEW_REPORT;
        let type = this.#type.DOWNLOAD;
        return await this.#commonService.sendRequest(type, url, {typeInfor, reportInfor, customerInfor, devicesInfor, timeInfor, siteModuleInfor, categoryInfor, shiftInfor, unitInfor, listInfor});
    }
    exportReport = async (prefix, devices,time,loadType) => {
        let url = REPORT_API.EXPORT_REPORT;
        let type = this.#type.DOWNLOAD_NEW;
        return await this.#commonService.sendRequest(type, url, {prefix, devices, time,loadType});
    }


}
export default new ReportService();