import React, { useEffect, useState } from "react";
import { Route, Switch, useHistory, useParams } from "react-router-dom";
import EffectivePower from "./effective-power";
import PowerCircuit from "./power-circuit";
import Voltage from "./voltage";
import OperationInformationService from "../../../../../../services/OperationInformationService";
import { Calendar } from 'primereact/calendar';
import moment from "moment";
import './index.css';
import ElectricalPower from "./electrical-power";
import CONS from "../../../../../../constants/constant";
import { RadioButton } from "primereact/radiobutton";
import AuthService from "../../../../../../services/AuthService";
import Temperature from "./temperature";
import Efficiency from "./efficiency";

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

    const deviceType = parseInt(param.deviceType);

    const getDataChart = async e => {

        let fDate = moment(fromDate).format("YYYY-MM-DD");
        let tDate = moment(toDate).format("YYYY-MM-DD");

        let chartType;
        switch (e) {
            case CONS.CHART_TYPE_PV.DONG_DIEN:
                setTypeForm(CONS.CHART_TYPE_PV.DONG_DIEN);
                chartType = CONS.CHART_TYPE_PV.DONG_DIEN;
                history.push(`/home/solar/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/${fDate}/${tDate}/${chartType}`);
                break;
            case CONS.CHART_TYPE_PV.DIEN_AP:
                setTypeForm(CONS.CHART_TYPE_PV.DIEN_AP);
                chartType = CONS.CHART_TYPE_PV.DIEN_AP;
                history.push(`/home/solar/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/voltage/${fDate}/${tDate}/${chartType}`);
                break;
            case CONS.CHART_TYPE_PV.CONG_SUAT_TAC_DUNG:
                setTypeForm(CONS.CHART_TYPE_PV.CONG_SUAT_TAC_DUNG);
                chartType = CONS.CHART_TYPE_PV.CONG_SUAT_TAC_DUNG;
                history.push(`/home/solar/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/effective-power/${fDate}/${tDate}/${chartType}`);
                break;
            case CONS.CHART_TYPE_PV.DIEN_NANG:
                let fromDateView = null;
                if (typePqs === 3) {
                    fromDateView = moment(fromDate).format("YYYY");
                } else if (typePqs === 2) {
                    fromDateView = moment(fromDate).format("YYYY-MM");
                } else {
                    fromDateView = fDate;
                }
                setTypeForm(CONS.CHART_TYPE_PV.DIEN_NANG);
                chartType = CONS.CHART_TYPE_PV.DIEN_NANG;
                history.push(`/home/solar/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/electrical-power/${fromDateView}/${typePqs}/${chartType}`);
                break;
            case CONS.CHART_TYPE_PV.NHIET_DO:
                setTypeForm(CONS.CHART_TYPE_PV.NHIET_DO);
                chartType = CONS.CHART_TYPE_PV.NHIET_DO;
                history.push(`/home/solar/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/temperature/${fDate}/${tDate}/${chartType}`);
                break;
            case CONS.CHART_TYPE_PV.HIEU_SUAT:
                setTypeForm(CONS.CHART_TYPE_PV.HIEU_SUAT);
                chartType = CONS.CHART_TYPE_PV.HIEU_SUAT;
                history.push(`/home/solar/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/efficiency/${fDate}/${tDate}/${chartType}`);
                break;
            default:
                break;
        }
    }

    const downloadPVDataChart = async (e) => {
        setStatusDownload(true);
        let fDate = moment(fromDate).format("YYYY-MM-DD");
        let tDate = moment(toDate).format("YYYY-MM-DD");

        let chartType;
        let res;
        switch (e) {
            case CONS.CHART_TYPE_PV.DONG_DIEN:

                chartType = CONS.CHART_TYPE_PV.DONG_DIEN;

                switch (parseInt(param.deviceType)) {
                    case CONS.DEVICE_TYPE_PV.INVERTER:

                        res = await OperationInformationService.getDataChartInverterPV(param.customerId, param.deviceId, fDate, tDate);
                        if (res.status == 200 && res.data !== '') {
                            await OperationInformationService.downloadChartInverterPV(param.customerId, param.deviceId, fDate, tDate, chartType, userName);
                        } else {
                            history.push(`/home/solar/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/${fDate}/${tDate}/${chartType}`);
                        }
                        setStatusDownload(false);
                        break;

                    case CONS.DEVICE_TYPE_PV.COMBINER:

                        res = await OperationInformationService.getDataChartCombinerPV(param.customerId, param.deviceId, fDate, tDate);
                        if (res.status == 200 && res.data !== '') {
                            await OperationInformationService.downloadChartCombinerPV(param.customerId, param.deviceId, fDate, tDate, chartType, userName);
                        } else {
                            history.push(`/home/solar/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/${fDate}/${tDate}/${chartType}`);
                        }
                        setStatusDownload(false);

                        break;
                    case CONS.DEVICE_TYPE_PV.STRING:

                        res = await OperationInformationService.getDataChartStringPV(param.customerId, param.deviceId, fDate, tDate);
                        if (res.status == 200 && res.data !== '') {
                            await OperationInformationService.downloadChartStringPV(param.customerId, param.deviceId, fDate, tDate, chartType, userName);
                        } else {
                            history.push(`/home/solar/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/${fDate}/${tDate}/${chartType}`);
                        }
                        setStatusDownload(false);

                        break;
                    case CONS.DEVICE_TYPE_PV.PANEL:

                        res = await OperationInformationService.getDataChartPanelPV(param.customerId, param.deviceId, fDate, tDate);
                        if (res.status == 200 && res.data !== '') {
                            await OperationInformationService.downloadChartPanelPV(param.customerId, param.deviceId, fDate, tDate, chartType, userName);
                        } else {
                            history.push(`/home/solar/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/${fDate}/${tDate}/${chartType}`);
                        }
                        setStatusDownload(false);

                        break;
                    default:
                        break;
                }
                break;
            case CONS.CHART_TYPE_PV.DIEN_AP:

                chartType = CONS.CHART_TYPE_PV.DIEN_AP;
                switch (parseInt(param.deviceType)) {
                    case CONS.DEVICE_TYPE_PV.INVERTER:

                        res = await OperationInformationService.getDataChartInverterPV(param.customerId, param.deviceId, fDate, tDate);

                        if (res.status == 200 && res.data !== '') {
                            await OperationInformationService.downloadChartInverterPV(param.customerId, param.deviceId, fDate, tDate, chartType, userName);
                        } else {
                            history.push(`/home/solar/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/voltage/${fDate}/${tDate}/${chartType}`);
                        }
                        setStatusDownload(false);

                        break;
                    case CONS.DEVICE_TYPE_PV.COMBINER:

                        res = await OperationInformationService.getDataChartCombinerPV(param.customerId, param.deviceId, fDate, tDate);
                        if (res.status == 200 && res.data !== '') {
                            await OperationInformationService.downloadChartCombinerPV(param.customerId, param.deviceId, fDate, tDate, chartType, userName);
                        } else {
                            history.push(`/home/solar/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/${fDate}/${tDate}/${chartType}`);
                        }
                        setStatusDownload(false);

                        break;
                    case CONS.DEVICE_TYPE_PV.STRING:

                        res = await OperationInformationService.getDataChartStringPV(param.customerId, param.deviceId, fDate, tDate);
                        if (res.status == 200 && res.data !== '') {
                            await OperationInformationService.downloadChartStringPV(param.customerId, param.deviceId, fDate, tDate, chartType, userName);
                        } else {
                            history.push(`/home/solar/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/${fDate}/${tDate}/${chartType}`);
                        }
                        setStatusDownload(false);

                        break;
                    case CONS.DEVICE_TYPE_PV.PANEL:

                        res = await OperationInformationService.getDataChartPanelPV(param.customerId, param.deviceId, fDate, tDate);
                        if (res.status == 200 && res.data !== '') {
                            await OperationInformationService.downloadChartPanelPV(param.customerId, param.deviceId, fDate, tDate, chartType, userName);
                        } else {
                            history.push(`/home/solar/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/${fDate}/${tDate}/${chartType}`);
                        }
                        setStatusDownload(false);

                        break;
                    default:
                        break;
                }

                break;
            case CONS.CHART_TYPE_PV.CONG_SUAT_TAC_DUNG:

                chartType = CONS.CHART_TYPE_PV.CONG_SUAT_TAC_DUNG;

                res = await OperationInformationService.getDataChartInverterPV(param.customerId, param.deviceId, fDate, tDate, 0);
                if (res.status == 200 && res.data !== '') {
                    await OperationInformationService.downloadChartInverterPV(param.customerId, param.deviceId, fDate, tDate, chartType, userName);
                } else {
                    history.push(`/home/solar/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/effective-power/${fDate}/${tDate}/${chartType}`);
                }
                setStatusDownload(false);

                break;
            case CONS.CHART_TYPE_PV.DIEN_NANG:

                let fromDateView = null;
                if (typePqs === 3) {
                    fromDateView = moment(fromDate).format("YYYY");
                } else if (typePqs === 2) {
                    fromDateView = moment(fromDate).format("YYYY-MM");
                } else {
                    fromDateView = fDate;
                }
                await OperationInformationService.downloadChartElectricalPowerInverterPV(param.customerId, param.deviceId, fromDateView, typePqs, userName);
                setStatusDownload(false);

                break;
            case CONS.CHART_TYPE_PV.NHIET_DO:

                chartType = CONS.CHART_TYPE_PV.NHIET_DO;

                switch (parseInt(param.deviceType)) {
                    case CONS.DEVICE_TYPE_PV.INVERTER:

                        res = await OperationInformationService.getDataChartInverterPV(param.customerId, param.deviceId, fDate, tDate);

                        if (res.status == 200 && res.data !== '') {
                            await OperationInformationService.downloadChartInverterPV(param.customerId, param.deviceId, fDate, tDate, chartType, userName);
                        } else {
                            history.push(`/home/solar/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/${fDate}/${tDate}/${chartType}`);
                        }
                        setStatusDownload(false);

                        break;
                    case CONS.DEVICE_TYPE_PV.WEARTHER:

                        res = await OperationInformationService.getDataChartWeatherPV(param.customerId, param.deviceId, fDate, tDate);

                        if (res.status == 200 && res.data !== '') {
                            await OperationInformationService.downloadChartWeatherPV(param.customerId, param.deviceId, fDate, tDate, chartType, userName);
                        } else {
                            history.push(`/home/solar/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/temperature/${fDate}/${tDate}/${chartType}`);
                        }
                        setStatusDownload(false);

                        break;
                    case CONS.DEVICE_TYPE_PV.COMBINER:

                        res = await OperationInformationService.getDataChartCombinerPV(param.customerId, param.deviceId, fDate, tDate);
                        if (res.status == 200 && res.data !== '') {
                            await OperationInformationService.downloadChartCombinerPV(param.customerId, param.deviceId, fDate, tDate, chartType, userName);
                        } else {
                            history.push(`/home/solar/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/${fDate}/${tDate}/${chartType}`);
                        }
                        setStatusDownload(false);

                        break;
                    case CONS.DEVICE_TYPE_PV.STRING:

                        res = await OperationInformationService.getDataChartStringPV(param.customerId, param.deviceId, fDate, tDate);
                        if (res.status == 200 && res.data !== '') {
                            await OperationInformationService.downloadChartStringPV(param.customerId, param.deviceId, fDate, tDate, chartType, userName);
                        } else {
                            history.push(`/home/solar/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/chart-operation/${fDate}/${tDate}/${chartType}`);
                        }
                        setStatusDownload(false);

                        break;
                    default:
                        break;
                }

                break;
            case CONS.CHART_TYPE_PV.HIEU_SUAT:
                setStatusDownload(false);
                break;
            default:
                break;
        }

    }
    useEffect(() => {
        document.title = "Thông tin thiết bị - Biểu đồ";
        getDataChart(typeForm);
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
                        <select id="cbo-data" name="dataTypeElectrical" defaultValue={1} className="custom-select block custom-select-sm" onChange={(e) => { getDataChart(parseInt(e.target.value)) }}>

                            {
                                deviceType === CONS.DEVICE_TYPE_PV.INVERTER &&
                                <option value={1}>Thông số Inverter</option>
                            }
                            {
                                deviceType === CONS.DEVICE_TYPE_PV.WEARTHER &&
                                <option value={2}>Thông số cảm biến thời tiết</option>
                            }
                            {
                                deviceType === CONS.DEVICE_TYPE_PV.COMBINER &&
                                <option value={3}>Thông số Combiner box</option>
                            }
                            {
                                deviceType === CONS.DEVICE_TYPE_PV.STRING &&
                                <option value={4}>Thông số String</option>
                            }
                            {
                                deviceType === CONS.DEVICE_TYPE_PV.PANEL &&
                                <option value={5}>Thông số Panel</option>
                            }
                        </select>
                    </div>
                    <div className="d-flex flex-wrap gap-3">

                        <div className="flex align-items-center mr-3 mt-1">
                            <RadioButton inputId="ingredient1" name={CONS.CHART_TYPE_PV.DONG_DIEN} value={CONS.CHART_TYPE_PV.DONG_DIEN} onChange={(e) => getDataChart(e.value)} checked={typeForm === CONS.CHART_TYPE_PV.DONG_DIEN} />
                            <label htmlFor="ingredient1" className="ml-2 mb-0">Dòng điện</label>
                        </div>
                        <div className="flex align-items-center mr-3 mt-1">
                            <RadioButton inputId="ingredient2" name={CONS.CHART_TYPE_PV.DIEN_AP} value={CONS.CHART_TYPE_PV.DIEN_AP} onChange={(e) => getDataChart(e.value)} checked={typeForm === CONS.CHART_TYPE_PV.DIEN_AP} />
                            <label htmlFor="ingredient1" className="ml-2 mb-0">Điện áp</label>
                        </div>
                        <div className="flex align-items-center mr-3 mt-1">
                            <RadioButton inputId="ingredient3" name={CONS.CHART_TYPE_PV.CONG_SUAT_TAC_DUNG} value={CONS.CHART_TYPE_PV.CONG_SUAT_TAC_DUNG} onChange={(e) => getDataChart(e.value)} checked={typeForm === CONS.CHART_TYPE_PV.CONG_SUAT_TAC_DUNG} />
                            <label htmlFor="ingredient1" className="ml-2 mb-0">Công suất tác dụng</label>
                        </div>
                        <div className="flex align-items-center mr-3 mt-1">
                            <RadioButton inputId="ingredient4" name={CONS.CHART_TYPE_PV.DIEN_NANG} value={CONS.CHART_TYPE_PV.DIEN_NANG} onChange={(e) => getDataChart(e.value)} checked={typeForm === CONS.CHART_TYPE_PV.DIEN_NANG} />
                            <label htmlFor="ingredient1" className="ml-2 mb-0">Điện năng</label>
                        </div>
                        <div className="flex align-items-center mr-3 mt-1">
                            <RadioButton inputId="ingredient4" name={CONS.CHART_TYPE_PV.NHIET_DO} value={CONS.CHART_TYPE_PV.NHIET_DO} onChange={(e) => getDataChart(e.value)} checked={typeForm === CONS.CHART_TYPE_PV.NHIET_DO} />
                            <label htmlFor="ingredient1" className="ml-2 mb-0">Nhiệt độ</label>
                        </div>
                        <div className="flex align-items-center mr-3 mt-1">
                            <RadioButton inputId="ingredient4" name={CONS.CHART_TYPE_PV.HIEU_SUAT} value={CONS.CHART_TYPE_PV.HIEU_SUAT} onChange={(e) => getDataChart(e.value)} checked={typeForm === CONS.CHART_TYPE_PV.HIEU_SUAT} />
                            <label htmlFor="ingredient1" className="ml-2 mb-0">Hiệu suất</label>
                        </div>
                    </div>
                </div>
            </div>
            <div id="main-search" className="ml-1 mb-3" style={{ height: '32px' }}>
                <div className="form-group mt-2 mb-0 ml-2">
                    {
                        typeForm === 4 && <>
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
                    {(typeForm === 1 || typeForm === 2 || typeForm === 3 || typeForm === 5 || typeForm === 6 || typeForm === 7 || typeForm === 8) &&
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

                    <button type="button" className="btn btn-outline-secondary btn-sm btn-downPV-chart" style={{ overflow: "hidden" }} onClick={() => downloadPVDataChart(typeForm)}>
                        {
                            !statusDownload &&
                            <i className="fa-solid fa-download" />
                        }
                        {
                            statusDownload &&
                            <i className="fa-solid fa-down-long icon-aniamation-download" />
                        }
                    </button>
                </div>

            </div>
            <div id="main-content">
                {
                    <Switch>
                        <Route path={"/home/solar/:customerId/:projectId/device-information/:deviceId/:deviceType/chart-operation/effective-power/:fromDate/:toDate/:chartType"}><EffectivePower></EffectivePower></Route>
                        <Route path={"/home/solar/:customerId/:projectId/device-information/:deviceId/:deviceType/chart-operation/electrical-power/:date/:typePqs/:chartType"}><ElectricalPower></ElectricalPower></Route>
                        <Route path={"/home/solar/:customerId/:projectId/device-information/:deviceId/:deviceType/chart-operation/temperature/:fromDate/:toDate/:chartType"}><Temperature></Temperature></Route>
                        <Route path={"/home/solar/:customerId/:projectId/device-information/:deviceId/:deviceType/chart-operation/efficiency/:fromDate/:toDate/:chartType"}><Efficiency></Efficiency></Route>
                        <Route path={"/home/solar/:customerId/:projectId/device-information/:deviceId/:deviceType/chart-operation/voltage/:fromDate/:toDate/:chartType"}><Voltage></Voltage></Route>
                        <Route path={"/home/solar/:customerId/:projectId/device-information/:deviceId/:deviceType/chart-operation/:fromDate/:toDate/:chartType"}><PowerCircuit></PowerCircuit></Route>
                    </Switch>
                }
            </div>
        </>
    )
}

export default ChartOperation;