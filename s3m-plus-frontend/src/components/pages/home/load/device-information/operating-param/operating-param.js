import React, { useEffect, useState } from "react";
import { useHistory, useParams, Switch, Route } from 'react-router-dom';
import ElectricalParam from "./electrical-param";
import TemperatureParam from "./temperature-param";
import PowerQuality from "./power-quality";
import OperationInformationService from "../../../../../../services/OperationInformationService";
import { Calendar } from 'primereact/calendar';
import moment from "moment";
import './index.css';
import authService from "../../../../../../services/AuthService";

const OperatingParam = () => {

    let userName = authService.getUserName();

    const param = useParams();

    const history = useHistory();

    const [fromDate, setFromDate] = useState(new Date());
    const [toDate, setToDate] = useState(new Date());
    const [statusDownload, setStatusDownload] = useState(false);

    const getOperationParam = async () => {

        let fDate = moment(fromDate).format("YYYY-MM-DD");
        let tDate = moment(toDate).format("YYYY-MM-DD");

        let typeParam = document.getElementById("cbo-data-type").value;

        if (parseInt(typeParam) === 3) {
            history.push(`/home/load/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/operating-param/${fDate}/${tDate}/2`)
        } else if (parseInt(typeParam) === 2) {
            history.push(`/home/load/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/operating-param/${fDate}/${tDate}/1`)
        } else {
            history.push(`/home/load/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/operating-param/${fDate}/${tDate}`);
        }
    }

    const downloadOperationParam = async () => {
        setStatusDownload(true);
        let fDate = moment(fromDate).format("YYYY-MM-DD");
        let tDate = moment(toDate).format("YYYY-MM-DD");

        let typeParam = document.getElementById("cbo-data-type").value;

        if (parseInt(typeParam) === 3) {

            let res = await OperationInformationService.getPowerQuality(param.customerId, param.deviceId, fDate, tDate, 1);

            if (res.status === 200 && res.data !== '') {
                await OperationInformationService.downloadPowerQuality(param.customerId, param.deviceId, fDate, tDate, userName);
            } else {
                history.push(`/home/load/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/operating-param/${fDate}/${tDate}/2`)
            }
            setStatusDownload(false);
        } else if (parseInt(typeParam) === 2) {

            let res = await OperationInformationService.getOperationInformation(param.customerId, param.deviceId, fDate, tDate, 1);

            if (res.status === 200 && res.data !== '') {
                await OperationInformationService.downloadTemperatureParam(param.customerId, param.deviceId, fDate, tDate, userName);
            } else {
                history.push(`/home/load/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/operating-param/${fDate}/${tDate}/1`)
            }
            setStatusDownload(false);
        } else {

            let res = await OperationInformationService.getOperationInformation(param.customerId, param.deviceId, fDate, tDate, 1);

            if (res.status === 200 && res.data !== '') {

                await OperationInformationService.downloadElectricalParam(param.customerId, param.deviceId, fDate, tDate, userName);
            } else {
                history.push(`/home/load/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/operating-param/${fDate}/${tDate}`)
            }
            setStatusDownload(false);
        }
    }



    useEffect(() => {
        document.title = "Thông tin thiết bị - Thông số vận hành";
    }, [param.customerId, param.projectId, param.deviceId, statusDownload])

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
                        <select id="cbo-data-type" name="dataType" defaultValue={1} className="custom-select block custom-select-sm" onChange={() => getOperationParam()}>
                            <option value={1}>Thông số điện</option>
                            <option value={2}>Thông số nhiệt độ</option>
                            <option value={3}>Sóng hài</option>
                        </select>
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
                        <Route path={"/home/load/:customerId/:projectId/device-information/:deviceId/operating-param/:fromDate/:toDate/1"}>
                            <TemperatureParam />
                        </Route>
                        <Route path={"/home/load/:customerId/:projectId/device-information/:deviceId/operating-param/:fromDate/:toDate/2"}>
                            <PowerQuality />
                        </Route>
                        <Route path={"/home/load/:customerId/:projectId/device-information/:deviceId/operating-param/:fromDate/:toDate"}>
                            <ElectricalParam />
                        </Route>
                    </Switch>
                }
            </div>
        </>
    )
}

export default OperatingParam;