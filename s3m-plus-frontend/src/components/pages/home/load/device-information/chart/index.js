import React, { useEffect, useState } from "react";
import { Route, Switch, useHistory, useParams } from "react-router-dom";
import EffectivePower from "./effective-power";
import PowerCircuit from "./power-circuit";
import Voltage from "./voltage";
import Temperature from "./temperature";
import ElectricalPower from "./electrical-power";
import Harmonic from "./harmonic";
import $ from "jquery";
import OperationInformationService from "../../../../../../services/OperationInformationService";
import { Calendar } from 'primereact/calendar';
import moment from "moment";
import authService from "../../../../../../services/AuthService";
import { RadioButton } from 'primereact/radiobutton';
import './index.css';
import CONS from "../../../../../../constants/constant";

const ChartOperation = () => {
    const param = useParams();
    const history = useHistory();
    const [fromDate, setFromDate] = useState(new Date());
    const [toDate, setToDate] = useState(new Date());
    const [type, setType] = useState(1);
    const [typeForm, setTypeForm] = useState(1);
    const [typePqs, setTypePqs] = useState(1);
    const [statusDownload, setStatusDownload] = useState(false);

    const setDefaultChart = async e => {
        // Lấy dữ liệu lần đầu
        if (e === 1) {
            setType(1);
            getDataChart(CONS.CHART_TYPE_LOAD.DONG_DIEN);
        }
        if (e === 2) {
            setType(2);
            getDataChart(CONS.CHART_TYPE_LOAD.NHIET_DO_VI_TRI_1);
        }
        if (e === 3) {
            setType(3);
            getDataChart(CONS.CHART_TYPE_LOAD.SONG_HAI_TUC_THOI);
        }
    }

    const getDataChart = async e => {

        let fDate = moment(fromDate).format("YYYY-MM-DD");
        let tDate = moment(toDate).format("YYYY-MM-DD");

        if (e === CONS.CHART_TYPE_LOAD.DONG_DIEN) {
            setTypeForm(CONS.CHART_TYPE_LOAD.DONG_DIEN);
            let chartType = CONS.CHART_TYPE_LOAD.DONG_DIEN;
            history.push(`/home/load/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/chart-operation/${fDate}/${tDate}/${chartType}`);
        }
        if (e === CONS.CHART_TYPE_LOAD.DIEN_AP) {
            setTypeForm(CONS.CHART_TYPE_LOAD.DIEN_AP);
            let chartType = CONS.CHART_TYPE_LOAD.DIEN_AP;
            history.push(`/home/load/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/chart-operation/voltage/${fDate}/${tDate}/${chartType}`);
        }
        if (e === CONS.CHART_TYPE_LOAD.CONG_SUAT_TAC_DUNG) {
            setTypeForm(CONS.CHART_TYPE_LOAD.CONG_SUAT_TAC_DUNG);
            let chartType = CONS.CHART_TYPE_LOAD.CONG_SUAT_TAC_DUNG;
            history.push(`/home/load/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/chart-operation/effective-power/${fDate}/${tDate}/${chartType}`);
        }
        if (e === CONS.CHART_TYPE_LOAD.DIEN_NANG) {
            let fromDateView = null;
            if (typePqs === 3) {
                fromDateView = moment(fromDate).format("YYYY");
            } else if (typePqs === 2) {
                fromDateView = moment(fromDate).format("YYYY-MM");
            } else {
                fromDateView = fDate;
            }
            setTypeForm(CONS.CHART_TYPE_LOAD.DIEN_NANG);
            let chartType = CONS.CHART_TYPE_LOAD.DIEN_NANG;
            history.push(`/home/load/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/chart-operation/electrical-power/${fromDateView}/${typePqs}/${chartType}`);
        }
        if (e === CONS.CHART_TYPE_LOAD.NHIET_DO_VI_TRI_1) {
            setTypeForm(CONS.CHART_TYPE_LOAD.NHIET_DO_VI_TRI_1);
            let chartType = CONS.CHART_TYPE_LOAD.NHIET_DO_VI_TRI_1;
            history.push(`/home/load/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/chart-operation/temperature/${fDate}/${tDate}/${chartType}`);
        }
        if (e === CONS.CHART_TYPE_LOAD.NHIET_DO_VI_TRI_2) {
            setTypeForm(CONS.CHART_TYPE_LOAD.NHIET_DO_VI_TRI_2);
            let chartType = CONS.CHART_TYPE_LOAD.NHIET_DO_VI_TRI_2;
            history.push(`/home/load/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/chart-operation/temperature/${fDate}/${tDate}/${chartType}`);
        }
        if (e === CONS.CHART_TYPE_LOAD.NHIET_DO_VI_TRI_3) {
            setTypeForm(CONS.CHART_TYPE_LOAD.NHIET_DO_VI_TRI_3);
            let chartType = CONS.CHART_TYPE_LOAD.NHIET_DO_VI_TRI_3;
            history.push(`/home/load/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/chart-operation/temperature/${fDate}/${tDate}/${chartType}`);
        }
        if (e === CONS.CHART_TYPE_LOAD.SONG_HAI_TUC_THOI) {
            setTypeForm(CONS.CHART_TYPE_LOAD.SONG_HAI_TUC_THOI);
            let chartType = CONS.CHART_TYPE_LOAD.SONG_HAI_TUC_THOI;
            history.push(`/home/load/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/chart-operation/harmonic/${fDate}/${tDate}/${chartType}`);
        }
        if (e === CONS.CHART_TYPE_LOAD.SONG_HAI_THOI_DIEM) {
            setTypeForm(CONS.CHART_TYPE_LOAD.SONG_HAI_THOI_DIEM);
            let chartType = CONS.CHART_TYPE_LOAD.SONG_HAI_THOI_DIEM;
            history.push(`/home/load/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/chart-operation/harmonic/${fDate}/${tDate}/${chartType}`);
        }
        if (e === CONS.CHART_TYPE_LOAD.SONG_HAI_KHOANG_THOI_GIAN) {
            setTypeForm(CONS.CHART_TYPE_LOAD.SONG_HAI_KHOANG_THOI_GIAN);
            let chartType = CONS.CHART_TYPE_LOAD.SONG_HAI_KHOANG_THOI_GIAN;
            history.push(`/home/load/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/chart-operation/harmonic/${fDate}/${tDate}/${chartType}`);
        }
    }

    const downloadDataChart = async (e) => {
        setStatusDownload(true);
        let fDate = moment(fromDate).format("YYYY-MM-DD");
        let tDate = moment(toDate).format("YYYY-MM-DD");

        if (e === CONS.CHART_TYPE_LOAD.DONG_DIEN) {
            let chartType = CONS.CHART_TYPE_LOAD.DONG_DIEN;

            let res = await OperationInformationService.getDataChart(param.customerId, param.deviceId, fDate, tDate, 0, chartType);

            if (res.status === 200 && parseInt(res.data.length) > 0) {
                await OperationInformationService.downloadDataChart(param.customerId, param.deviceId, fDate, tDate, 0, chartType, authService.getUserName());
            } else {
                history.push(`/home/load/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/chart-operation/${fDate}/${tDate}/${chartType}`);
            }
            setStatusDownload(false);
        }
        if (e === CONS.CHART_TYPE_LOAD.DIEN_AP) {
            let chartType = CONS.CHART_TYPE_LOAD.DIEN_AP;
            let res = await OperationInformationService.getDataChart(param.customerId, param.deviceId, fDate, tDate, 0, chartType);

            if (res.status === 200 && parseInt(res.data.length) > 0) {
                await OperationInformationService.downloadDataChart(param.customerId, param.deviceId, fDate, tDate, 0, chartType, authService.getUserName());
            } else {
                history.push(`/home/load/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/chart-operation/voltage/${fDate}/${tDate}/${chartType}`);
            }
            setStatusDownload(false);
        }
        if (e === CONS.CHART_TYPE_LOAD.CONG_SUAT_TAC_DUNG) {
            let chartType = CONS.CHART_TYPE_LOAD.CONG_SUAT_TAC_DUNG;
            let res = await OperationInformationService.getDataChart(param.customerId, param.deviceId, fDate, tDate, 0, chartType);
            if (res.status === 200 && parseInt(res.data.length) > 0) {
                await OperationInformationService.downloadDataChart(param.customerId, param.deviceId, fDate, tDate, 0, chartType, authService.getUserName());
            } else {
                history.push(`/home/load/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/chart-operation/effective-power/${fDate}/${tDate}/${chartType}`);
            }
            setStatusDownload(false);
        }
        if (e === CONS.CHART_TYPE_LOAD.DIEN_NANG) {
            let chartType = CONS.CHART_TYPE_LOAD.DIEN_NANG;
            let fromDateView = null;
            if (typePqs === 3) {
                fromDateView = moment(fromDate).format("YYYY");
            } else if (typePqs === 2) {
                fromDateView = moment(fromDate).format("YYYY-MM");
            } else {
                fromDateView = fDate;
            }

            let res = await OperationInformationService.getDataChart(param.customerId, param.deviceId, fromDateView, null, typePqs, chartType);

            if (res.status === 200 && parseInt(res.data.length) > 0) {
                await OperationInformationService.downloadDataChart(param.customerId, param.deviceId, fromDateView, tDate, typePqs, chartType, authService.getUserName());
            } else {
                history.push(`/home/load/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/chart-operation/electrical-power/${fromDateView}/${typePqs}/${chartType}`);
            }
            setStatusDownload(false);
        }
        if (e === CONS.CHART_TYPE_LOAD.NHIET_DO_VI_TRI_1) {
            let chartType = CONS.CHART_TYPE_LOAD.NHIET_DO_VI_TRI_1;
            let res = await OperationInformationService.getDataChart(param.customerId, param.deviceId, fDate, tDate, 0, 5);

            if (res.status === 200 && parseInt(res.data.length) > 0) {
                await OperationInformationService.downloadDataChart(param.customerId, param.deviceId, fDate, tDate, 0, 5, authService.getUserName());
            } else {
                history.push(`/home/load/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/chart-operation/temperature/${fDate}/${tDate}/${chartType}`);
            }
            setStatusDownload(false);
        }
        if (e === CONS.CHART_TYPE_LOAD.NHIET_DO_VI_TRI_2) {
            let chartType = CONS.CHART_TYPE_LOAD.NHIET_DO_VI_TRI_2;
            let res = await OperationInformationService.getDataChart(param.customerId, param.deviceId, fDate, tDate, 0, 5);

            if (res.status === 200 && parseInt(res.data.length) > 0) {
                await OperationInformationService.downloadDataChart(param.customerId, param.deviceId, fDate, tDate, 0, 5, authService.getUserName());
            } else {
                history.push(`/home/load/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/chart-operation/temperature/${fDate}/${tDate}/${chartType}`);
            }
            setStatusDownload(false);
        }
        if (e === CONS.CHART_TYPE_LOAD.NHIET_DO_VI_TRI_3) {
            let chartType = CONS.CHART_TYPE_LOAD.NHIET_DO_VI_TRI_3;
            let res = await OperationInformationService.getDataChart(param.customerId, param.deviceId, fDate, tDate, 0, 5);

            if (res.status === 200 && parseInt(res.data.length) > 0) {
                await OperationInformationService.downloadDataChart(param.customerId, param.deviceId, fDate, tDate, 0, 5, authService.getUserName());
            } else {
                history.push(`/home/load/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/chart-operation/temperature/${fDate}/${tDate}/${chartType}`);
            }
            setStatusDownload(false);
        }
        if (e === CONS.CHART_TYPE_LOAD.SONG_HAI_TUC_THOI) {
            setStatusDownload(false);
        }
        if (e === CONS.CHART_TYPE_LOAD.SONG_HAI_THOI_DIEM) {
            setStatusDownload(false);
        }
        if (e === CONS.CHART_TYPE_LOAD.SONG_HAI_KHOANG_THOI_GIAN) {
            let chartType = CONS.CHART_TYPE_LOAD.SONG_HAI_KHOANG_THOI_GIAN;
            let res = await OperationInformationService.getDataChartHarmonicPeriod(param.customerId, param.deviceId, fDate, tDate);

            if (res.status === 200 && parseInt(res.data.length) > 0) {
                await OperationInformationService.downloadDataChart(param.customerId, param.deviceId, fDate, tDate, 0, 6, authService.getUserName());
            } else {
                history.push(`/home/load/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/chart-operation/harmonic/${fDate}/${tDate}/${chartType}`);
            }
            setStatusDownload(false);
        }
    }


    useEffect(() => {
        document.title = "Thông tin thiết bị - Biểu đồ";
    }, [type, param.customerId, typeForm, typePqs, param.deviceId]);
    return (
        <>
            <div id="main-search" className="ml-1 mb-3" style={{ height: '32px' }}>
                <div className="form-group mt-2 mb-0 ml-2">
                    <div className="input-group float-left mr-3" style={{ width: '200px' }}>
                        <div className="input-group-prepend">
                            <span className="input-group-text pickericon">
                                <span className="fas fa-circle-info" />
                            </span>
                        </div>
                        <select id="cbo-data" name="dataType" defaultValue={1} className="custom-select block custom-select-sm" onChange={(e) => { setDefaultChart(parseInt(e.target.value)) }}>
                            <option value={1}>Thông số điện</option>
                            <option value={2}>Thông số nhiệt độ</option>
                            <option value={3}>Thông số sóng hài</option>
                        </select>
                    </div>
                </div>
                <div className="d-flex flex-wrap gap-3">
                    {
                        parseInt(type) === 1 && <>
                            <div className="flex align-items-center mr-3 mt-1">
                                <RadioButton inputId="ingredient1" name={CONS.CHART_TYPE_LOAD.DONG_DIEN} value={CONS.CHART_TYPE_LOAD.DONG_DIEN} onChange={(e) => getDataChart(e.value)} checked={typeForm === CONS.CHART_TYPE_LOAD.DONG_DIEN} />
                                <label htmlFor="ingredient1" className="ml-2 mb-0">Dòng điện</label>
                            </div>
                            <div className="flex align-items-center mr-3 mt-1">
                                <RadioButton inputId="ingredient2" name={CONS.CHART_TYPE_LOAD.DIEN_AP} value={CONS.CHART_TYPE_LOAD.DIEN_AP} onChange={(e) => getDataChart(e.value)} checked={typeForm === CONS.CHART_TYPE_LOAD.DIEN_AP} />
                                <label htmlFor="ingredient1" className="ml-2 mb-0">Điện áp</label>
                            </div>
                            <div className="flex align-items-center mr-3 mt-1">
                                <RadioButton inputId="ingredient3" name={CONS.CHART_TYPE_LOAD.CONG_SUAT_TAC_DUNG} value={CONS.CHART_TYPE_LOAD.CONG_SUAT_TAC_DUNG} onChange={(e) => getDataChart(e.value)} checked={typeForm === CONS.CHART_TYPE_LOAD.CONG_SUAT_TAC_DUNG} />
                                <label htmlFor="ingredient1" className="ml-2 mb-0">Công suất tác dụng</label>
                            </div>
                            <div className="flex align-items-center mr-3 mt-1">
                                <RadioButton inputId="ingredient4" name={CONS.CHART_TYPE_LOAD.DIEN_NANG} value={CONS.CHART_TYPE_LOAD.DIEN_NANG} onChange={(e) => getDataChart(e.value)} checked={typeForm === CONS.CHART_TYPE_LOAD.DIEN_NANG} />
                                <label htmlFor="ingredient1" className="ml-2 mb-0">Điện năng</label>
                            </div>
                        </>
                    }
                    {
                        parseInt(type) === 2 && <>
                            <div className="flex align-items-center mr-3 mt-1">
                                <RadioButton inputId="ingredient4" name={CONS.CHART_TYPE_LOAD.NHIET_DO_VI_TRI_1} value={CONS.CHART_TYPE_LOAD.NHIET_DO_VI_TRI_1} onChange={(e) => getDataChart(e.value)} checked={typeForm === CONS.CHART_TYPE_LOAD.NHIET_DO_VI_TRI_1} />
                                <label htmlFor="ingredient1" className="ml-2 mb-0">Vị trí 1</label>
                            </div>
                            <div className="flex align-items-center mr-3 mt-1">
                                <RadioButton inputId="ingredient4" name={CONS.CHART_TYPE_LOAD.NHIET_DO_VI_TRI_2} value={CONS.CHART_TYPE_LOAD.NHIET_DO_VI_TRI_2} onChange={(e) => getDataChart(e.value)} checked={typeForm === CONS.CHART_TYPE_LOAD.NHIET_DO_VI_TRI_2} />
                                <label htmlFor="ingredient1" className="ml-2 mb-0">Vị trí 2</label>
                            </div>
                            <div className="flex align-items-center mr-3 mt-1">
                                <RadioButton inputId="ingredient4" name={CONS.CHART_TYPE_LOAD.NHIET_DO_VI_TRI_3} value={CONS.CHART_TYPE_LOAD.NHIET_DO_VI_TRI_3} onChange={(e) => getDataChart(e.value)} checked={typeForm === CONS.CHART_TYPE_LOAD.NHIET_DO_VI_TRI_3} />
                                <label htmlFor="ingredient1" className="ml-2 mb-0">Vị trí 3</label>
                            </div>
                        </>
                    }
                    {
                        parseInt(type) === 3 && <>
                            <div className="flex align-items-center mr-3 mt-1">
                                <RadioButton inputId="ingredient4" name={CONS.CHART_TYPE_LOAD.SONG_HAI_TUC_THOI} value={CONS.CHART_TYPE_LOAD.SONG_HAI_TUC_THOI} onChange={(e) => getDataChart(e.value)} checked={typeForm === CONS.CHART_TYPE_LOAD.SONG_HAI_TUC_THOI} />
                                <label htmlFor="ingredient1" className="ml-2 mb-0">Tức thời</label>
                            </div>
                            <div className="flex align-items-center mr-3 mt-1">
                                <RadioButton inputId="ingredient4" name={CONS.CHART_TYPE_LOAD.SONG_HAI_THOI_DIEM} value={CONS.CHART_TYPE_LOAD.SONG_HAI_THOI_DIEM} onChange={(e) => getDataChart(e.value)} checked={typeForm === CONS.CHART_TYPE_LOAD.SONG_HAI_THOI_DIEM} />
                                <label htmlFor="ingredient1" className="ml-2 mb-0">Chọn thời điểm</label>
                            </div>
                            <div className="flex align-items-center mr-3 mt-1">
                                <RadioButton inputId="ingredient4" name={CONS.CHART_TYPE_LOAD.SONG_HAI_KHOANG_THOI_GIAN} value={CONS.CHART_TYPE_LOAD.SONG_HAI_KHOANG_THOI_GIAN} onChange={(e) => getDataChart(e.value)} checked={typeForm === CONS.CHART_TYPE_LOAD.SONG_HAI_KHOANG_THOI_GIAN} />
                                <label htmlFor="ingredient1" className="ml-2 mb-0">Chọn khoảng thời gian</label>
                            </div>
                        </>
                    }
                </div>
            </div>
            {
                (typeForm === 1 || typeForm === 2 || typeForm === 3 || typeForm === 4 || typeForm === 5 || typeForm === 6 || typeForm === 7 || typeForm === 9 || typeForm === 10) &&
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
                        {(typeForm === 1 || typeForm === 2 || typeForm === 3 || typeForm === 5 || typeForm === 6 || typeForm === 7 || typeForm === 10) &&
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
                        {
                            typeForm === 9 && <div className="input-group float-left mr-1 from-date" style={{ width: '270px' }}>
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
                                    showTime
                                    hourFormat="24"
                                    onChange={e => setFromDate(e.value)}
                                />
                            </div>
                        }
                    </div>
                    {(typeForm === 1 || typeForm === 2 || typeForm === 3 || typeForm === 4 || typeForm === 5 || typeForm === 6 || typeForm === 7 || typeForm == 10) &&
                        <>
                            <div className="search-buttons float-left search">
                                <button type="button" className="btn btn-outline-secondary btn-sm mr-1" onClick={() => { getDataChart(typeForm) }}>
                                    <i className="fa-solid fa-search" />
                                </button>

                                <button type="button" className="btn btn-outline-secondary btn-sm btn-download-chart" style={{ overflow: "hidden" }} onClick={() => downloadDataChart(typeForm)}>
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
                        </>
                    }
                    {
                        typeForm === 9 &&
                        <div className="search-buttons float-left search">
                            <button type="button" className="btn btn-outline-secondary btn-sm" style={{ overflow: "hidden" }} onClick={() => { getDataChart(typeForm) }}>
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
                    }
                </div>
            }
            <div id="main-content">
                {
                    <Switch>
                        <Route path={"/home/load/:customerId/:projectId/device-information/:deviceId/chart-operation/harmonic/:fromDate/:toDate/:chartType"}><Harmonic></Harmonic></Route>
                        <Route path={"/home/load/:customerId/:projectId/device-information/:deviceId/chart-operation/electrical-power/:fromDate/:pqsViewType/:chartType"}><ElectricalPower></ElectricalPower></Route>
                        <Route path={"/home/load/:customerId/:projectId/device-information/:deviceId/chart-operation/effective-power/:fromDate/:toDate/:chartType"}><EffectivePower></EffectivePower></Route>
                        <Route path={"/home/load/:customerId/:projectId/device-information/:deviceId/chart-operation/temperature/:fromDate/:toDate/:chartType"}><Temperature></Temperature></Route>
                        <Route path={"/home/load/:customerId/:projectId/device-information/:deviceId/chart-operation/voltage/:fromDate/:toDate/:chartType"}><Voltage></Voltage></Route>
                        <Route path={"/home/load/:customerId/:projectId/device-information/:deviceId/chart-operation/:fromDate/:toDate/:chartType"}><PowerCircuit></PowerCircuit></Route>
                    </Switch>
                }
            </div>
        </>
    )
}

export default ChartOperation;