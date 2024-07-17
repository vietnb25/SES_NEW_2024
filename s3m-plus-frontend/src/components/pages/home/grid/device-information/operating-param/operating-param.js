import React, { useEffect, useState } from "react";
import { useHistory, useParams, Switch, Route } from 'react-router-dom';
import OperationInformationService from "../../../../../../services/OperationInformationService";
import { Calendar } from 'primereact/calendar';
import moment from "moment";
import AuthService from "../../../../../../services/AuthService";
import ElectricalParam from "./electrical-param";
import DischargeParam from "./discharge-param";
import TemperatureParam from "./temperature-param";
import CONS from "../../../../../../constants/constant";

const OperatingParam = () => {
    const param = useParams();

    const history = useHistory();

    const [fromDate, setFromDate] = useState(new Date());
    const [toDate, setToDate] = useState(new Date());
    const [typeData, setTypeData] = useState(1);
    let userName = AuthService.getUserName();
    const [page, setPage] = useState(1);
    const [statusDownload, setStatusDownload] = useState(false);

    const getOperationParam = async () => {

        let fDate = moment(fromDate).format("YYYY-MM-DD");
        let tDate = moment(toDate).format("YYYY-MM-DD");
        let deviceType = parseInt(param.deviceType);

        if (deviceType === CONS.DEVICE_TYPE_GRID.RMU_DRAWER) {
            switch (parseInt(typeData)) {
                case 1:
                    history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/operating-param/${fDate}/${tDate}`);
                    break;
                case 2:
                    history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/operating-param/temperature/${fDate}/${tDate}`);
                    break;
                case 3:
                    history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/operating-param/discharge/${fDate}/${tDate}`);
                    break;
                default:
                    break;
            }
        }
    }

    const downloadOperationParam = async () => {
        setStatusDownload(true);
        let fDate = moment(fromDate).format("YYYY-MM-DD");
        let tDate = moment(toDate).format("YYYY-MM-DD");
        let deviceType = parseInt(param.deviceType);

        if (deviceType === CONS.DEVICE_TYPE_GRID.RMU_DRAWER) {
            let res = await OperationInformationService.getOperationRmuDrawerGrid(param.customerId, param.deviceId, fDate, tDate, page);
            switch (parseInt(typeData)) {
                case 1:
                    if (res.status === 200 && res.data !== '') {
                        await OperationInformationService.downloadDeviceParameterRmuDrawerGrid(param.customerId, param.deviceId, fDate, tDate, typeData, userName);
                    } else {
                        history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/operating-param/${fDate}/${tDate}`);
                    }
                    setStatusDownload(false);
                    break;
                case 2:
                    if (res.status === 200 && res.data !== '') {
                        await OperationInformationService.downloadDeviceParameterRmuDrawerGrid(param.customerId, param.deviceId, fDate, tDate, typeData, userName);
                    } else {
                        history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/operating-param/temperature/${fDate}/${tDate}`);
                    }
                    setStatusDownload(false);
                    break;
                case 3:
                    if (res.status === 200 && res.data !== '') {
                        await OperationInformationService.downloadDeviceParameterRmuDrawerGrid(param.customerId, param.deviceId, fDate, tDate, typeData, userName);
                    } else {
                        history.push(`/home/grid/${param.customerId}/${param.projectId}/device-information/${param.deviceId}/${param.deviceType}/operating-param/discharge/${fDate}/${tDate}`);
                    }
                    setStatusDownload(false);
                    break;
                default:
                    break;
            }
        }

    }

    useEffect(() => {
        document.title = "Thông tin thiết bị - Thông số vận hành";
        getOperationParam();
    }, [param.customerId, param.deviceId, param.deviceType, typeData]);

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

                        <select id="cbo-data-type" name="dataType" defaultValue={1} className="custom-select block custom-select-sm" onChange={(e) => setTypeData(e.target.value)}>
                            <option value={1}>Thông số điện</option>
                            <option value={2}>Thông số nhiệt độ</option>
                            <option value={3}>Thông số phóng điện</option>
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
                        <Route path={"/home/grid/:customerId/:projectId/device-information/:deviceId/:deviceType/operating-param/discharge/:fromDate/:toDate"}>
                            <DischargeParam />
                        </Route>
                        <Route path={"/home/grid/:customerId/:projectId/device-information/:deviceId/:deviceType/operating-param/temperature/:fromDate/:toDate"}>
                            <TemperatureParam />
                        </Route>
                        <Route path={"/home/grid/:customerId/:projectId/device-information/:deviceId/:deviceType/operating-param/:fromDate/:toDate"}>
                            <ElectricalParam />
                        </Route>
                    </Switch>
                }
            </div>
        </>
    )
}

export default OperatingParam;