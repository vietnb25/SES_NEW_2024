import React, { useEffect, useState } from "react";
import { useHistory, useParams, Switch, Route } from 'react-router-dom';
import WeatherParam from "./weather-param";
import OperationInformationService from "../../../../../../services/OperationInformationService";
import { Calendar } from 'primereact/calendar';
import moment from "moment";
import InverterParam from "./inverter-param";
import AuthService from "../../../../../../services/AuthService";
import CONS from "../../../../../../constants/constant";
import CombinerParam from "./combiner-param";
import StringParam from "./string-param";
import PanelParam from "./penel-param";

const OperatingParam = () => {
    const param = useParams();

    const history = useHistory();

    const [fromDate, setFromDate] = useState(new Date());
    const [toDate, setToDate] = useState(new Date());
    let userName = AuthService.getUserName();
    const [statusDownload, setStatusDownload] = useState(false);

    const getOperationParam = async () => {

        let fDate = moment(fromDate).format("YYYY-MM-DD");
        let tDate = moment(toDate).format("YYYY-MM-DD");

        let deviceType = parseInt(param.deviceType);

        switch (deviceType) {
            case CONS.DEVICE_TYPE_PV.INVERTER:
                history.push(`/home/solar/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/operating-param/${fDate}/${tDate}`);
                break;
            case CONS.DEVICE_TYPE_PV.WEARTHER:
                history.push(`/home/solar/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/operating-param/weather/${fDate}/${tDate}`);
                break;
            case CONS.DEVICE_TYPE_PV.COMBINER:
                history.push(`/home/solar/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/operating-param/combiner/${fDate}/${tDate}`);
                break;
            case CONS.DEVICE_TYPE_PV.STRING:
                history.push(`/home/solar/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/operating-param/string/${fDate}/${tDate}`);
                break;
            case CONS.DEVICE_TYPE_PV.PANEL:
                history.push(`/home/solar/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/operating-param/panel/${fDate}/${tDate}`);
                break;
            default:
                break;
        }
    }

    const downloadOperationParam = async () => {
        setStatusDownload(true);
        let fDate = moment(fromDate).format("YYYY-MM-DD");
        let tDate = moment(toDate).format("YYYY-MM-DD");

        let typeParam = document.getElementById("cbo-data-type").value;

        let deviceType = parseInt(param.deviceType);

        let res;
        switch (deviceType) {
            case CONS.DEVICE_TYPE_PV.INVERTER:

                res = await OperationInformationService.getOperationInverterPV(param.customerId, param.deviceId, fDate, tDate, 1);

                if (res.status === 200 && res.data !== '') {

                    await OperationInformationService.downloadDeviceParameterInverterPV(param.customerId, param.deviceId, fDate, tDate, userName);
                } else {
                    history.push(`/home/solar/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/operating-param/${fDate}/${tDate}`);
                }
                setStatusDownload(false);

                break;
            case CONS.DEVICE_TYPE_PV.WEARTHER:

                res = await OperationInformationService.getOperationWeatherPV(param.customerId, param.deviceId, fDate, tDate, 1);

                if (res.status === 200 && res.data !== '') {
                    await OperationInformationService.downloadDeviceParameterWeatherPV(param.customerId, param.deviceId, fDate, tDate, userName);
                } else {
                    history.push(`/home/solar/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/operating-param/weather/${fDate}/${tDate}`)
                }
                setStatusDownload(false);

                break;
            case CONS.DEVICE_TYPE_PV.COMBINER:

                res = await OperationInformationService.getOperationCombinerPV(param.customerId, param.deviceId, fDate, tDate, 1);

                if (res.status === 200 && res.data !== '') {
                    await OperationInformationService.downloadDeviceParameterCombinerPV(param.customerId, param.deviceId, fDate, tDate, userName);
                } else {
                    history.push(`/home/solar/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/operating-param/combiner/${fDate}/${tDate}`)
                }
                setStatusDownload(false);

                break;
            case CONS.DEVICE_TYPE_PV.STRING:

                res = await OperationInformationService.getOperationStringPV(param.customerId, param.deviceId, fDate, tDate, 1);

                if (res.status === 200 && res.data !== '') {
                    await OperationInformationService.downloadDeviceParameterStringPV(param.customerId, param.deviceId, fDate, tDate, userName);
                } else {
                    history.push(`/home/solar/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/operating-param/string/${fDate}/${tDate}`)
                }
                setStatusDownload(false);

                break;
            case CONS.DEVICE_TYPE_PV.PANEL:

                res = await OperationInformationService.getOperationPanelPV(param.customerId, param.deviceId, fDate, tDate, 1);

                if (res.status === 200 && res.data !== '') {
                    await OperationInformationService.downloadDeviceParameterPanelPV(param.customerId, param.deviceId, fDate, tDate, userName);
                } else {
                    history.push(`/home/solar/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/operating-param/panel/${fDate}/${tDate}`)
                }
                setStatusDownload(false);

                break;
            default:
                break;
        }
    }

    useEffect(() => {
        document.title = "Thông tin thiết bị - Thông số vận hành";
        getOperationParam();
    }, [param.customerId, param.deviceId, param.deviceType]);

    return (
        <>
            <div id="main-search" className="ml-1" style={{ height: '32px' }}>
                <div className="form-group mt-2 mb-0 ml-2">
                    <div className="input-group float-left mr-1" style={{ width: '200px' }}>
                        <div className="input-group-prepend">
                            <span className="input-group-text pickericon">
                                <span className="fas fa-circle-info" />
                            </span>
                        </div>

                        {
                            parseInt(param.deviceType) === CONS.DEVICE_TYPE_PV.INVERTER && <select id="cbo-data-type" name="dataType" defaultValue={1} className="custom-select block custom-select-sm" onChange={() => getOperationParam()}>
                                <option value={1}>Thông số Inverter</option>
                            </select>
                        }
                        {
                            parseInt(param.deviceType) === CONS.DEVICE_TYPE_PV.WEARTHER && <select id="cbo-data-type" name="dataType" defaultValue={2} className="custom-select block custom-select-sm" onChange={() => getOperationParam()}>
                                <option value={2}>Thông số thời tiết</option>
                            </select>
                        }
                        {
                            parseInt(param.deviceType) === CONS.DEVICE_TYPE_PV.COMBINER && <select id="cbo-data-type" name="dataType" defaultValue={3} className="custom-select block custom-select-sm" onChange={() => getOperationParam()}>
                                <option value={3}>Thông số Combiner BOX</option>
                            </select>
                        }
                        {
                            parseInt(param.deviceType) === CONS.DEVICE_TYPE_PV.STRING &&
                            <select id="cbo-data-type" name="dataType" defaultValue={4} className="custom-select block custom-select-sm" onChange={() => getOperationParam()}>
                                <option value={4}>Thông số String</option>
                            </select>
                        }

                        {
                            parseInt(param.deviceType) === CONS.DEVICE_TYPE_PV.PANEL &&
                            <select id="cbo-data-type" name="dataType" defaultValue={5} className="custom-select block custom-select-sm" onChange={() => getOperationParam()}>
                                <option value={5}>Thông số Panel</option>
                            </select>
                        }

                    </div>
                    <div className="input-group float-left" style={{ width: "270px" }}>
                        <div className="input-group-prepend">
                            <span className="input-group-text pickericon">
                                <span className="far fa-calendar"></span>
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
                    <div className="input-group float-left" style={{ width: "270px" }}>
                        <div className="input-group-prepend">
                            <span className="input-group-text pickericon">
                                <span className="far fa-calendar"></span>
                            </span>
                        </div>
                        <Calendar
                            id="to-value"
                            className="celendar-picker"
                            dateFormat="yy-mm-dd"
                            value={toDate}
                            onChange={e => setToDate(e.value)}
                        />
                    </div>
                </div>
                <div className="search-buttons float-left">
                    <button type="button" className="btn btn-outline-secondary btn-sm mr-1" onClick={() => getOperationParam()}>
                        <i className="fa-solid fa-search" />
                    </button>
                    <button type="button" className="btn btn-outline-secondary btn-sm" style={{ overflow: "hidden" }} onClick={() => downloadOperationParam()}>
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
            <div id="main-content" style={{ overflow: "auto", height: "650px", position: "absolute", width: "-webkit-fill-available" }}>
                {
                    <Switch>
                        <Route path={"/home/solar/:customerId/:projectId/device-information/:deviceId/:deviceType/operating-param/weather/:fromDate/:toDate"}>
                            <WeatherParam />
                        </Route>
                        <Route path={"/home/solar/:customerId/:projectId/device-information/:deviceId/:deviceType/operating-param/combiner/:fromDate/:toDate"}>
                            <CombinerParam />
                        </Route>
                        <Route path={"/home/solar/:customerId/:projectId/device-information/:deviceId/:deviceType/operating-param/string/:fromDate/:toDate"}>
                            <StringParam />
                        </Route>
                        <Route path={"/home/solar/:customerId/:projectId/device-information/:deviceId/:deviceType/operating-param/panel/:fromDate/:toDate"}>
                            <PanelParam />
                        </Route>
                        <Route path={"/home/solar/:customerId/:projectId/device-information/:deviceId/:deviceType/operating-param/:fromDate/:toDate"}>
                            <InverterParam />
                        </Route>
                    </Switch>
                }
            </div>
        </>
    )
}

export default OperatingParam;