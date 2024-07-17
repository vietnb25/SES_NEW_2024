import React, { useEffect, useState } from "react";
import { Route, Switch, useHistory, useParams } from "react-router-dom";
import EffectivePower from "./effective-power";
import PowerCircuit from "./power-circuit";
import Voltage from "./voltage";
import Weather from "./temperature";
import $ from "jquery";
import OperationInformationService from "../../../../../../services/OperationInformationService";
import { Calendar } from 'primereact/calendar';
import moment from "moment";
import './index.css';
import ElectricalPower from "./electrical-power";
import CONS from "../../../../../../constants/constant";
import { RadioButton } from "primereact/radiobutton";
import AuthService from "../../../../../../services/AuthService";
import Temperature from "./temperature";
import ProbeAntenna from "./probe-antenna";

const ChartOperation = () => {
    const param = useParams();
    const history = useHistory();
    const [fromDate, setFromDate] = useState(new Date());
    const [toDate, setToDate] = useState(new Date());
    const [type, setType] = useState(1);
    const [typeForm, setTypeForm] = useState(1);
    const [typePqs, setTypePqs] = useState(1);
    let userName = AuthService.getUserName();
    const [statusDownload, setStatusDownload] = useState(false);

    const setDefaultChart = async e => {
        // Lấy dữ liệu lần đầu
        if (e === 1) {
            setType(1);
            getDataChart(CONS.CHART_TYPE_GRID.DONG_DIEN);
        }
        if (e === 2) {
            setType(2);
            getDataChart(CONS.CHART_TYPE_GRID.NHIET_DO_CUC);
        }
        if (e === 3) {
            setType(3);
            getDataChart(CONS.CHART_TYPE_GRID.LFB_RATIO);
        }
    }

    const getDataChart = async e => {

        let fDate = moment(fromDate).format("YYYY-MM-DD");
        let tDate = moment(toDate).format("YYYY-MM-DD");

        let chartType;
        switch (e) {
            case CONS.CHART_TYPE_GRID.DONG_DIEN:
                setTypeForm(CONS.CHART_TYPE_GRID.DONG_DIEN);
                chartType = CONS.CHART_TYPE_GRID.DONG_DIEN;
                history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/${fDate}/${tDate}/${chartType}`);
                break;
            case CONS.CHART_TYPE_GRID.DIEN_AP:
                setTypeForm(CONS.CHART_TYPE_GRID.DIEN_AP);
                chartType = CONS.CHART_TYPE_GRID.DIEN_AP;
                history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/voltage/${fDate}/${tDate}/${chartType}`);
                break;
            case CONS.CHART_TYPE_GRID.CONG_SUAT_TAC_DUNG:
                setTypeForm(CONS.CHART_TYPE_GRID.CONG_SUAT_TAC_DUNG);
                chartType = CONS.CHART_TYPE_GRID.CONG_SUAT_TAC_DUNG;
                history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/effective-power/${fDate}/${tDate}/${chartType}`);
                break;
            case CONS.CHART_TYPE_GRID.DIEN_NANG:
                let fromDateView = null;
                if (typePqs === 3) {
                    fromDateView = moment(fromDate).format("YYYY");
                } else if (typePqs === 2) {
                    fromDateView = moment(fromDate).format("YYYY-MM");
                } else {
                    fromDateView = fDate;
                }
                setTypeForm(CONS.CHART_TYPE_GRID.DIEN_NANG);
                chartType = CONS.CHART_TYPE_GRID.DIEN_NANG;
                history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/electrical-power/${fromDateView}/${typePqs}/${chartType}`);
                break;
            case CONS.CHART_TYPE_GRID.NHIET_DO_CUC:
                setTypeForm(CONS.CHART_TYPE_GRID.NHIET_DO_CUC)
                chartType = CONS.CHART_TYPE_GRID.NHIET_DO_CUC;
                history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/temperature/${fDate}/${tDate}/${chartType}`);
                break;
            case CONS.CHART_TYPE_GRID.NHIET_DO_KHOANG:
                setTypeForm(CONS.CHART_TYPE_GRID.NHIET_DO_KHOANG)
                chartType = CONS.CHART_TYPE_GRID.NHIET_DO_KHOANG;
                history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/temperature/${fDate}/${tDate}/${chartType}`);
                break;
            case CONS.CHART_TYPE_GRID.DO_AM:
                setTypeForm(CONS.CHART_TYPE_GRID.DO_AM)
                chartType = CONS.CHART_TYPE_GRID.DO_AM;
                history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/temperature/${fDate}/${tDate}/${chartType}`);
                break;
            case CONS.CHART_TYPE_GRID.LFB_RATIO:
                setTypeForm(CONS.CHART_TYPE_GRID.LFB_RATIO)
                chartType = CONS.CHART_TYPE_GRID.LFB_RATIO;
                history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/probe-antenna/${fDate}/${tDate}/${chartType}`);
                break;
            case CONS.CHART_TYPE_GRID.LFB_EPPC:
                setTypeForm(CONS.CHART_TYPE_GRID.LFB_EPPC)
                chartType = CONS.CHART_TYPE_GRID.LFB_EPPC;
                history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/probe-antenna/${fDate}/${tDate}/${chartType}`);
                break;
            case CONS.CHART_TYPE_GRID.MFB_RATIO:
                setTypeForm(CONS.CHART_TYPE_GRID.MFB_RATIO)
                chartType = CONS.CHART_TYPE_GRID.MFB_RATIO;
                history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/probe-antenna/${fDate}/${tDate}/${chartType}`);
                break;
            case CONS.CHART_TYPE_GRID.MLFB_EPPC:
                setTypeForm(CONS.CHART_TYPE_GRID.MLFB_EPPC)
                chartType = CONS.CHART_TYPE_GRID.MLFB_EPPC;
                history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/probe-antenna/${fDate}/${tDate}/${chartType}`);
                break;
            case CONS.CHART_TYPE_GRID.HLFB_RATIO:
                setTypeForm(CONS.CHART_TYPE_GRID.HLFB_RATIO)
                chartType = CONS.CHART_TYPE_GRID.HLFB_RATIO;
                history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/probe-antenna/${fDate}/${tDate}/${chartType}`);
                break;
            case CONS.CHART_TYPE_GRID.HLFB_EPPC:
                setTypeForm(CONS.CHART_TYPE_GRID.HLFB_EPPC)
                chartType = CONS.CHART_TYPE_GRID.HLFB_EPPC;
                history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/probe-antenna/${fDate}/${tDate}/${chartType}`);
                break;
            case CONS.CHART_TYPE_GRID.INDICATOR:
                setTypeForm(CONS.CHART_TYPE_GRID.INDICATOR)
                chartType = CONS.CHART_TYPE_GRID.INDICATOR;
                history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/probe-antenna/${fDate}/${tDate}/${chartType}`);
                break;
            default:
                break;
        }
    }

    const downloadDataChartRmuDrawer = async (e) => {
        setStatusDownload(true);
        let fDate = moment(fromDate).format("YYYY-MM-DD");
        let tDate = moment(toDate).format("YYYY-MM-DD");

        let res = await OperationInformationService.getDataChartRmuDrawerGrid(param.customerId, param.deviceId, fDate, tDate);

        let chartType;
        switch (e) {
            case CONS.CHART_TYPE_GRID.DONG_DIEN:
                chartType = CONS.CHART_TYPE_GRID.DONG_DIEN;
                if (res.status === 200 && parseInt(res.data.length) > 0) {
                    await OperationInformationService.downloadDataChartgRmuDrawerGrid(param.customerId, param.deviceId, fDate, tDate, 0, chartType, userName);
                } else {
                    history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/${fDate}/${tDate}/${chartType}`);
                }
                setStatusDownload(false);
                break;
            case CONS.CHART_TYPE_GRID.DIEN_AP:
                chartType = CONS.CHART_TYPE_GRID.DIEN_AP;
                if (res.status === 200 && parseInt(res.data.length) > 0) {
                    await OperationInformationService.downloadDataChartgRmuDrawerGrid(param.customerId, param.deviceId, fDate, tDate, 0, chartType, userName);
                } else {
                    history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/voltage/${fDate}/${tDate}/${chartType}`);
                }
                setStatusDownload(false);
                break;
            case CONS.CHART_TYPE_GRID.CONG_SUAT_TAC_DUNG:
                chartType = CONS.CHART_TYPE_GRID.CONG_SUAT_TAC_DUNG;
                if (res.status === 200 && parseInt(res.data.length) > 0) {
                    await OperationInformationService.downloadDataChartgRmuDrawerGrid(param.customerId, param.deviceId, fDate, tDate, 0, chartType, userName);
                } else {
                    history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/effective-power/${fDate}/${tDate}/${chartType}`);
                }
                setStatusDownload(false);
                break;
            case CONS.CHART_TYPE_GRID.DIEN_NANG:
                let fromDateView = null;
                if (typePqs === 3) {
                    fromDateView = moment(fromDate).format("YYYY");
                } else if (typePqs === 2) {
                    fromDateView = moment(fromDate).format("YYYY-MM");
                } else {
                    fromDateView = fDate;
                }
                chartType = CONS.CHART_TYPE_GRID.DIEN_NANG;
                await OperationInformationService.downloadDataChartgRmuDrawerGrid(param.customerId, param.deviceId, fDate, tDate, typePqs, chartType, userName);
                setStatusDownload(false);
                break;
            case CONS.CHART_TYPE_GRID.NHIET_DO_CUC:
                chartType = CONS.CHART_TYPE_GRID.NHIET_DO_CUC;
                if (res.status === 200 && parseInt(res.data.length) > 0) {
                    await OperationInformationService.downloadDataChartgRmuDrawerGrid(param.customerId, param.deviceId, fDate, tDate, 0, chartType, userName);
                } else {
                    history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/temperature/${fDate}/${tDate}/${chartType}`);
                }
                setStatusDownload(false);
                break;
            case CONS.CHART_TYPE_GRID.NHIET_DO_KHOANG:
                chartType = CONS.CHART_TYPE_GRID.NHIET_DO_KHOANG;
                if (res.status === 200 && parseInt(res.data.length) > 0) {
                    await OperationInformationService.downloadDataChartgRmuDrawerGrid(param.customerId, param.deviceId, fDate, tDate, 0, chartType, userName);
                } else {
                    history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/temperature/${fDate}/${tDate}/${chartType}`);
                }
                setStatusDownload(false);
                break;
            case CONS.CHART_TYPE_GRID.DO_AM:
                chartType = CONS.CHART_TYPE_GRID.DO_AM;
                if (res.status === 200 && parseInt(res.data.length) > 0) {
                    await OperationInformationService.downloadDataChartgRmuDrawerGrid(param.customerId, param.deviceId, fDate, tDate, 0, chartType, userName);
                } else {
                    history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/temperature/${fDate}/${tDate}/${chartType}`);
                }
                setStatusDownload(false);
                break;
            case CONS.CHART_TYPE_GRID.LFB_RATIO:
                chartType = CONS.CHART_TYPE_GRID.LFB_RATIO;
                if (res.status === 200 && parseInt(res.data.length) > 0) {
                    await OperationInformationService.downloadDataChartgRmuDrawerGrid(param.customerId, param.deviceId, fDate, tDate, 0, chartType, userName);
                } else {
                    history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/probe-antenna/${fDate}/${tDate}/${chartType}`); history.push(`/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/probe-antenna/${fDate}/${tDate}/${chartType}`);
                }
                setStatusDownload(false);
                break;
            case CONS.CHART_TYPE_GRID.LFB_EPPC:
                chartType = CONS.CHART_TYPE_GRID.LFB_EPPC;
                if (res.status === 200 && parseInt(res.data.length) > 0) {
                    await OperationInformationService.downloadDataChartgRmuDrawerGrid(param.customerId, param.deviceId, fDate, tDate, 0, chartType, userName);
                } else {
                    history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/probe-antenna/${fDate}/${tDate}/${chartType}`);
                }
                setStatusDownload(false);
                break;
            case CONS.CHART_TYPE_GRID.MFB_RATIO:
                chartType = CONS.CHART_TYPE_GRID.MFB_RATIO;
                if (res.status === 200 && parseInt(res.data.length) > 0) {
                    await OperationInformationService.downloadDataChartgRmuDrawerGrid(param.customerId, param.deviceId, fDate, tDate, 0, chartType, userName);
                } else {
                    history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/probe-antenna/${fDate}/${tDate}/${chartType}`);
                }
                setStatusDownload(false);
                break;
            case CONS.CHART_TYPE_GRID.MLFB_EPPC:
                chartType = CONS.CHART_TYPE_GRID.MLFB_EPPC;
                if (res.status === 200 && parseInt(res.data.length) > 0) {
                    await OperationInformationService.downloadDataChartgRmuDrawerGrid(param.customerId, param.deviceId, fDate, tDate, 0, chartType, userName);
                } else {
                    history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/probe-antenna/${fDate}/${tDate}/${chartType}`);
                }
                setStatusDownload(false);
                break;
            case CONS.CHART_TYPE_GRID.HLFB_RATIO:
                chartType = CONS.CHART_TYPE_GRID.HLFB_RATIO;
                if (res.status === 200 && parseInt(res.data.length) > 0) {
                    await OperationInformationService.downloadDataChartgRmuDrawerGrid(param.customerId, param.deviceId, fDate, tDate, 0, chartType, userName);
                } else {
                    history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/probe-antenna/${fDate}/${tDate}/${chartType}`);
                }
                setStatusDownload(false);
                break;
            case CONS.CHART_TYPE_GRID.HLFB_EPPC:
                chartType = CONS.CHART_TYPE_GRID.HLFB_EPPC;
                if (res.status === 200 && parseInt(res.data.length) > 0) {
                    await OperationInformationService.downloadDataChartgRmuDrawerGrid(param.customerId, param.deviceId, fDate, tDate, 0, chartType, userName);
                } else {
                    history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/probe-antenna/${fDate}/${tDate}/${chartType}`);
                }
                setStatusDownload(false);
                break;
            case CONS.CHART_TYPE_GRID.INDICATOR:
                chartType = CONS.CHART_TYPE_GRID.INDICATOR;
                if (res.status === 200 && parseInt(res.data.length) > 0) {
                    await OperationInformationService.downloadDataChartgRmuDrawerGrid(param.customerId, param.deviceId, fDate, tDate, 0, chartType, userName);
                } else {
                    history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/probe-antenna/${fDate}/${tDate}/${chartType}`);
                }
                setStatusDownload(false);
                break;
            default:
                break;
        }
    }
    useEffect(() => {
        document.title = "Thông tin thiết bị - Biểu đồ";
    }, [param.deviceId, param.chartTypeValue, type, param.customerId, typePqs]);
    return (
        <>
            <div id="main-search" className="ml-1 mb-3" style={{ height: '32px' }}>
                <div className="form-group mt-2 mb-0 ml-2">
                    <div className="input-group float-left mr-3" style={{ width: '250px' }}>
                        <div className="input-group-prepend">
                            <span className="input-group-text pickericon">
                                <span className="fas fa-calendar" />
                            </span>
                        </div>
                        <select id="cbo-data" name="dataTypeElectrical" defaultValue={1} className="custom-select block custom-select-sm" onChange={(e) => { setDefaultChart(parseInt(e.target.value)) }}>
                            <option value={1}>Thông số điện</option>
                            <option value={2}>Thông số nhiệt độ</option>
                            <option value={3}>Thông số phóng điện</option>
                        </select>
                    </div>
                    <div className="d-flex flex-wrap gap-3">
                        {
                            parseInt(type) === 1 && <>
                                <div className="flex align-items-center mr-3 mt-1">
                                    <RadioButton inputId="ingredient1" name={CONS.CHART_TYPE_GRID.DONG_DIEN} value={CONS.CHART_TYPE_GRID.DONG_DIEN} onChange={(e) => getDataChart(e.value)} checked={typeForm === CONS.CHART_TYPE_GRID.DONG_DIEN} />
                                    <label htmlFor="ingredient1" className="ml-2 mb-0">Dòng điện</label>
                                </div>
                                <div className="flex align-items-center mr-3 mt-1">
                                    <RadioButton inputId="ingredient2" name={CONS.CHART_TYPE_GRID.DIEN_AP} value={CONS.CHART_TYPE_GRID.DIEN_AP} onChange={(e) => getDataChart(e.value)} checked={typeForm === CONS.CHART_TYPE_GRID.DIEN_AP} />
                                    <label htmlFor="ingredient1" className="ml-2 mb-0">Điện áp</label>
                                </div>
                                <div className="flex align-items-center mr-3 mt-1">
                                    <RadioButton inputId="ingredient3" name={CONS.CHART_TYPE_GRID.CONG_SUAT_TAC_DUNG} value={CONS.CHART_TYPE_GRID.CONG_SUAT_TAC_DUNG} onChange={(e) => getDataChart(e.value)} checked={typeForm === CONS.CHART_TYPE_GRID.CONG_SUAT_TAC_DUNG} />
                                    <label htmlFor="ingredient1" className="ml-2 mb-0">Công suất tác dụng</label>
                                </div>
                                <div className="flex align-items-center mr-3 mt-1">
                                    <RadioButton inputId="ingredient4" name={CONS.CHART_TYPE_GRID.DIEN_NANG} value={CONS.CHART_TYPE_GRID.DIEN_NANG} onChange={(e) => getDataChart(e.value)} checked={typeForm === CONS.CHART_TYPE_GRID.DIEN_NANG} />
                                    <label htmlFor="ingredient1" className="ml-2 mb-0">Điện năng</label>
                                </div>
                            </>
                        }
                        {
                            parseInt(type) === 2 && <>
                                <div className="flex align-items-center mr-3 mt-1">
                                    <RadioButton inputId="ingredient4" name={CONS.CHART_TYPE_GRID.NHIET_DO_CUC} value={CONS.CHART_TYPE_GRID.NHIET_DO_CUC} onChange={(e) => getDataChart(e.value)} checked={typeForm === CONS.CHART_TYPE_GRID.NHIET_DO_CUC} />
                                    <label htmlFor="ingredient1" className="ml-2 mb-0">Nhiệt độ cực</label>
                                </div>
                                <div className="flex align-items-center mr-3 mt-1">
                                    <RadioButton inputId="ingredient4" name={CONS.CHART_TYPE_GRID.NHIET_DO_KHOANG} value={CONS.CHART_TYPE_GRID.NHIET_DO_KHOANG} onChange={(e) => getDataChart(e.value)} checked={typeForm === CONS.CHART_TYPE_GRID.NHIET_DO_KHOANG} />
                                    <label htmlFor="ingredient1" className="ml-2 mb-0">Nhiệt độ khoang</label>
                                </div>
                                <div className="flex align-items-center mr-3 mt-1">
                                    <RadioButton inputId="ingredient4" name={CONS.CHART_TYPE_GRID.DO_AM} value={CONS.CHART_TYPE_GRID.DO_AM} onChange={(e) => getDataChart(e.value)} checked={typeForm === CONS.CHART_TYPE_GRID.DO_AM} />
                                    <label htmlFor="ingredient1" className="ml-2 mb-0">Độ ẩm</label>
                                </div>
                            </>
                        }
                        {
                            parseInt(type) === 3 && <>
                                <div className="flex align-items-center mr-3 mt-1">
                                    <RadioButton inputId="ingredient4" name={CONS.CHART_TYPE_GRID.LFB_RATIO} value={CONS.CHART_TYPE_GRID.LFB_RATIO} onChange={(e) => getDataChart(e.value)} checked={typeForm === CONS.CHART_TYPE_GRID.LFB_RATIO} />
                                    <label htmlFor="ingredient1" className="ml-2 mb-0">LFB Ratio</label>
                                </div>
                                <div className="flex align-items-center mr-3 mt-1">
                                    <RadioButton inputId="ingredient4" name={CONS.CHART_TYPE_GRID.LFB_EPPC} value={CONS.CHART_TYPE_GRID.LFB_EPPC} onChange={(e) => getDataChart(e.value)} checked={typeForm === CONS.CHART_TYPE_GRID.LFB_EPPC} />
                                    <label htmlFor="ingredient1" className="ml-2 mb-0">LFB EPPC</label>
                                </div>
                                <div className="flex align-items-center mr-3 mt-1">
                                    <RadioButton inputId="ingredient4" name={CONS.CHART_TYPE_GRID.MFB_RATIO} value={CONS.CHART_TYPE_GRID.MFB_RATIO} onChange={(e) => getDataChart(e.value)} checked={typeForm === CONS.CHART_TYPE_GRID.MFB_RATIO} />
                                    <label htmlFor="ingredient1" className="ml-2 mb-0">MFB Ratio</label>
                                </div>
                                <div className="flex align-items-center mr-3 mt-1">
                                    <RadioButton inputId="ingredient4" name={CONS.CHART_TYPE_GRID.MLFB_EPPC} value={CONS.CHART_TYPE_GRID.MLFB_EPPC} onChange={(e) => getDataChart(e.value)} checked={typeForm === CONS.CHART_TYPE_GRID.MLFB_EPPC} />
                                    <label htmlFor="ingredient1" className="ml-2 mb-0">MFB EPPC</label>
                                </div>
                                <div className="flex align-items-center mr-3 mt-1">
                                    <RadioButton inputId="ingredient4" name={CONS.CHART_TYPE_GRID.HLFB_RATIO} value={CONS.CHART_TYPE_GRID.HLFB_RATIO} onChange={(e) => getDataChart(e.value)} checked={typeForm === CONS.CHART_TYPE_GRID.HLFB_RATIO} />
                                    <label htmlFor="ingredient1" className="ml-2 mb-0">HFB Ratio</label>
                                </div>
                                <div className="flex align-items-center mr-3 mt-1">
                                    <RadioButton inputId="ingredient4" name={CONS.CHART_TYPE_GRID.HLFB_EPPC} value={CONS.CHART_TYPE_GRID.HLFB_EPPC} onChange={(e) => getDataChart(e.value)} checked={typeForm === CONS.CHART_TYPE_GRID.HLFB_EPPC} />
                                    <label htmlFor="ingredient1" className="ml-2 mb-0">HFB EPPC</label>
                                </div>
                                <div className="flex align-items-center mr-3 mt-1">
                                    <RadioButton inputId="ingredient4" name={CONS.CHART_TYPE_GRID.INDICATOR} value={CONS.CHART_TYPE_GRID.INDICATOR} onChange={(e) => getDataChart(e.value)} checked={typeForm === CONS.CHART_TYPE_GRID.INDICATOR} />
                                    <label htmlFor="ingredient1" className="ml-2 mb-0">Mức chỉ thị</label>
                                </div>
                            </>
                        }
                    </div>
                </div>
            </div>
            <div id="main-search" className="ml-1 mb-3" style={{ height: '32px' }}>
                <div className="form-group mt-2 mb-0 ml-2">
                    {
                        typeForm === CONS.CHART_TYPE_GRID.DIEN_NANG && <>
                            <div className="input-group float-left mr-1 data-type-electrical" style={{ width: '250px' }}>
                                <div className="input-group-prepend">
                                    <span className="input-group-text pickericon">
                                        <span className="fas fa-calendar" />
                                    </span>
                                </div>
                                <select id="cbo-data-type-electrical" name="dataTypeElectrical" defaultValue={typePqs} className="custom-select block custom-select-sm" onChange={(e) => { setTypePqs(parseInt(e.target.value)) }}>
                                    <option value={1}>Ngày</option>
                                    <option value={2}>Tháng</option>
                                    <option value={3}>Năm</option>
                                </select>
                            </div>
                            {
                                typePqs === 1 &&
                                <div className="input-group float-left mr-1 from-date" style={{ width: '270px' }}>
                                    <div className="input-group-prepend">
                                        <span className="input-group-text pickericon">
                                            <span className="far fa-calendar" />
                                        </span>
                                    </div>
                                    <Calendar
                                        id="from-value"
                                        className="celendar-picker"
                                        dateFormat="yy-mm-dd"
                                        hourFormat="24"
                                        showButtonBar
                                        value={fromDate}
                                        onChange={e => setFromDate(e.value)}
                                    />
                                </div>
                            }
                            {
                                typePqs === 2 &&
                                <div className="input-group float-left mr-1 from-date" style={{ width: '270px' }}>
                                    <div className="input-group-prepend">
                                        <span className="input-group-text pickericon">
                                            <span className="far fa-calendar" />
                                        </span>
                                    </div>
                                    <Calendar
                                        id="from-value"
                                        className="celendar-picker"
                                        dateFormat="yy-mm"
                                        hourFormat="24"
                                        showButtonBar
                                        value={fromDate}
                                        onChange={e => setFromDate(e.value)}
                                    />
                                </div>
                            }
                            {
                                typePqs === 3 &&
                                <div className="input-group float-left mr-1 from-date" style={{ width: '270px' }}>
                                    <div className="input-group-prepend">
                                        <span className="input-group-text pickericon">
                                            <span className="far fa-calendar" />
                                        </span>
                                    </div>
                                    <Calendar
                                        id="from-value"
                                        className="celendar-picker"
                                        dateFormat="yy"
                                        hourFormat="24"
                                        showButtonBar
                                        value={fromDate}
                                        onChange={e => setFromDate(e.value)}
                                    />
                                </div>
                            }
                        </>
                    }
                    {(typeForm === CONS.CHART_TYPE_GRID.DONG_DIEN ||
                        typeForm === CONS.CHART_TYPE_GRID.DIEN_AP ||
                        typeForm === CONS.CHART_TYPE_GRID.CONG_SUAT_TAC_DUNG ||
                        typeForm === CONS.CHART_TYPE_GRID.NHIET_DO_CUC ||
                        typeForm === CONS.CHART_TYPE_GRID.NHIET_DO_KHOANG ||
                        typeForm === CONS.CHART_TYPE_GRID.DO_AM ||
                        typeForm === CONS.CHART_TYPE_GRID.LFB_RATIO ||
                        typeForm === CONS.CHART_TYPE_GRID.LFB_EPPC ||
                        typeForm === CONS.CHART_TYPE_GRID.MFB_RATIO ||
                        typeForm === CONS.CHART_TYPE_GRID.MLFB_EPPC ||
                        typeForm === CONS.CHART_TYPE_GRID.HLFB_RATIO ||
                        typeForm === CONS.CHART_TYPE_GRID.HLFB_EPPC ||
                        typeForm === CONS.CHART_TYPE_GRID.INDICATOR) &&
                        <>
                            <div className="input-group float-left mr-1 from-date" style={{ width: '270px' }}>
                                <div className="input-group-prepend">
                                    <span className="input-group-text pickericon">
                                        <span className="far fa-calendar" />
                                    </span>
                                </div>
                                <Calendar
                                    id="from-value"
                                    className="celendar-picker"
                                    dateFormat="yy-mm-dd"
                                    value={fromDate}
                                    onChange={e => setFromDate(e.value)}
                                />
                            </div>
                            <div className="input-group float-left mr-1 to-date" style={{ width: '270px' }}>
                                <div className="input-group-prepend">
                                    <span className="input-group-text pickericon">
                                        <span className="far fa-calendar" />
                                    </span>
                                </div>
                                <Calendar
                                    id="to-value"
                                    className=""
                                    dateFormat="yy-mm-dd"
                                    value={toDate}
                                    onChange={e => setToDate(e.value)}
                                />
                            </div>
                        </>
                    }
                </div>

                <div className="search-buttons float-left search">
                    <button type="button" className="btn btn-outline-secondary btn-sm mr-1" onClick={() => { getDataChart(typeForm) }}>
                        <i className="fa-solid fa-search" />
                    </button>

                    <button type="button" className="btn btn-outline-secondary btn-sm btn-downPV-chart" onClick={() => downloadDataChartRmuDrawer(typeForm)}>
                        <i className="fa-solid fa-download" />
                    </button>
                </div>

            </div>
            <div id="main-content">
                {
                    <Switch>
                        <Route path={"/home/grid/:customerId/:projectId/device-information/:deviceId/:deviceType/chart-operation/effective-power/:fromDate/:toDate/:chartType"}><EffectivePower></EffectivePower></Route>
                        <Route path={"/home/grid/:customerId/:projectId/device-information/:deviceId/:deviceType/chart-operation/electrical-power/:date/:typePqs/:chartType"}><ElectricalPower></ElectricalPower></Route>
                        <Route path={"/home/grid/:customerId/:projectId/device-information/:deviceId/:deviceType/chart-operation/temperature/:fromDate/:toDate/:chartType"}><Temperature></Temperature></Route>
                        <Route path={"/home/grid/:customerId/:projectId/device-information/:deviceId/:deviceType/chart-operation/probe-antenna/:fromDate/:toDate/:chartType"}><ProbeAntenna></ProbeAntenna></Route>
                        <Route path={"/home/grid/:customerId/:projectId/device-information/:deviceId/:deviceType/chart-operation/voltage/:fromDate/:toDate/:chartType"}><Voltage></Voltage></Route>
                        <Route path={"/home/grid/:customerId/:projectId/device-information/:deviceId/:deviceType/chart-operation/:fromDate/:toDate/:chartType"}><PowerCircuit></PowerCircuit></Route>
                    </Switch>
                }
            </div>
        </>
    )
}

export default ChartOperation;