import React, { useEffect } from "react";
import { Link, Route, Switch, useHistory, useParams } from "react-router-dom";
import ElectricalParam from "./electrical-param";
import TemperatureParam from "./temperature-param";
import PowerQuality from "./power-quality";
import Pickadate from 'pickadate/builds/react-dom';
import OperationInformationService from "../../../../../services/OperationInformationService";

const OperatingParam = () => {

    const param = useParams();

    const history = useHistory();

    const getOperationParam = async () => {

        let fromDate = document.getElementById("from-date").value;
        let toDate = document.getElementById("to-date").value;
        let typeParam = document.getElementById("cbo-data-type").value;

        if (parseInt(typeParam) === 3) {
            history.push(`/operation/${param.projectId}/${param.deviceId}/operating-param/${fromDate}/${toDate}/2`)
        } else if (parseInt(typeParam) === 2) {
            history.push(`/operation/${param.projectId}/${param.deviceId}/operating-param/${fromDate}/${toDate}/1`)
        } else {
            history.push(`/operation/${param.projectId}/${param.deviceId}/operating-param/${fromDate}/${toDate}`);
        }

    }

    const downloadOperationParam = async () => {

        let fromDate = document.getElementById("from-date").value;
        let toDate = document.getElementById("to-date").value;
        let typeParam = document.getElementById("cbo-data-type").value;

        if (parseInt(typeParam) === 3) {

            let res = await OperationInformationService.getPowerQuality(param.deviceId, fromDate, toDate);

            if (res.status === 200 && parseInt(res.data.data.length) > 0) {
                await OperationInformationService.downloadPowerQuality(param.deviceId, fromDate, toDate);
            } else {
                history.push(`/operation/${param.projectId}/${param.deviceId}/operating-param/${fromDate}/${toDate}/2`)
            }

        } else if (parseInt(typeParam) === 2) {

            let res = await OperationInformationService.getOperationInformation(param.deviceId, fromDate, toDate, 1);

            if (res.status === 200 && parseInt(res.data.data.length) > 0) {
                await OperationInformationService.downloadTemperatureParam(param.deviceId, fromDate, toDate);
            } else {
                history.push(`/operation/${param.projectId}/${param.deviceId}/operating-param/${fromDate}/${toDate}/1`)
            }

        } else {

            let res = await OperationInformationService.getOperationInformation(param.deviceId, fromDate, toDate, 1);

            if (res.status === 200 && parseInt(res.data.data.length) > 0) {

                await OperationInformationService.downloadElectricalParam(param.deviceId, fromDate, toDate);
            } else {
                history.push(`/operation/${param.projectId}/${param.deviceId}/operating-param/${fromDate}/${toDate}`)
            }

        }

    }

    document.title = "Thông tin thiết bị - Thông số vận hành";

    useEffect(() => {

    }, [])
    return (
        <>
            <div id="main-search" className="ml-1" style={{ height: '32px' }}>
                <div className="form-group mt-2 mb-0 ml-2">
                    <div className="input-group float-left mr-1" style={{ width: '270px' }}>
                        <div className="input-group-prepend">
                            <span className="input-group-text pickericon">
                                <span className="fas fa-circle-info" />
                            </span>
                        </div>
                        <select id="cbo-data-type" name="dataType" defaultValue={1} className="custom-select block custom-select-sm" onChange={() => getOperationParam()}>
                            <option value={1}>Thông số điện</option>
                            <option value={2}>Thông số nhiệt độ</option>
                            <option value={3}>Thông số chất lượng điện năng</option>
                        </select>
                    </div>
                    <div className="input-group float-left mr-1" style={{ width: '250px' }}>
                        <div className="input-group-prepend">
                            <span className="input-group-text pickericon">
                                <span className="far fa-calendar" />
                            </span>
                        </div>
                        <Pickadate.InputPicker id="from-date" value={new Date()} className="form-control pickadate form-control-sm" style={{ fontSize: "13px" }} readOnly={true} aria-haspopup="true"
                            aria-readonly="false" aria-owns="from-date_root" autoComplete="off" placeholder="Từ ngày"
                            initialState={{
                                selected: new Date(),
                                template: "YYYY-MM-DD"
                            }}
                        />
                    </div>
                    <div className="input-group float-left mr-1" style={{ width: '250px' }}>
                        <div className="input-group-prepend">
                            <span className="input-group-text pickericon">
                                <span className="far fa-calendar" />
                            </span>
                        </div>
                        <Pickadate.InputPicker id="to-date" value={new Date()} className="form-control pickadate form-control-sm" style={{ fontSize: "13px" }} readOnly={true} aria-haspopup="true"
                            aria-readonly="false" aria-owns="from-date_root" autoComplete="off" placeholder="Đến ngày"
                            initialState={{
                                selected: new Date(),
                                template: "YYYY-MM-DD"
                            }}
                        />
                    </div>
                </div>
                <div className="search-buttons float-left">
                    <button type="button" className="btn btn-outline-secondary btn-sm mr-1" onClick={() => getOperationParam()}>
                        <i className="fa-solid fa-search" />
                    </button>
                    <button type="button" className="btn btn-outline-secondary btn-sm" onClick={() => downloadOperationParam()}>
                        <i className="fa-solid fa-download" />
                    </button>
                </div>
            </div>
            <div id="main-content" style={{ overflow: "auto", height: "650px", position: "absolute", width: "-webkit-fill-available" }}>
                {
                    <Switch>
                        <Route path={"/operation/:projectId/:deviceId/operating-param/:fromDate/:toDate/1"}>
                            <TemperatureParam></TemperatureParam>
                        </Route>
                        <Route path={"/operation/:projectId/:deviceId/operating-param/:fromDate/:toDate/2"}>
                            <PowerQuality></PowerQuality>
                        </Route>
                        <Route path={"/operation/:projectId/:deviceId/operating-param/:fromDate/:toDate"}>
                            <ElectricalParam></ElectricalParam>
                        </Route>
                    </Switch>
                }
            </div>
        </>
    )
}

export default OperatingParam;