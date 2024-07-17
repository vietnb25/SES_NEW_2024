import React, { useEffect, useState } from "react";
import { Link, Route, Switch, useHistory, useParams } from "react-router-dom";
import Instant from "./instant";
import OperatingParam from "./operating-param";
import ChartOperation from "./chart";
import OperationWarning from "./operation-warning";
import "./index.css";
import OperationInformationService from "../../../../services/OperationInformationService";


const OperationInformation = () => {

    const param = useParams();

    const [devices, setDevices] = useState([]);
    const [deviceId, setDeviceId] = useState(param.deviceId);
    const history = useHistory();

    const getDevices = async () => {
        let res = await OperationInformationService.getDevices(param.projectId);
        if (res.status === 200) {
            let devices = res.data;
            setDevices(devices);
        }
    }

    const setLine = (data) => {
        let a = document.getElementById("setLine");
        a.style.left = `${data}%`;
    }

    const onchangeDevice = () => {
        let deviceId = document.getElementById("cbo-device-id").value;
        setDeviceId(deviceId);
        let url = window.location.pathname;
        let deviceStr = "" + param.deviceId;
        let projectStr = "" + param.projectId;

        let href = url.slice(0, 0) + url.slice(12 + deviceStr.length + projectStr.length);

        history.push(`/operation/` + param.projectId + `/` + deviceId + href);
    }

    document.title = "Thông tin thiết bị";

    useEffect(() => {
        getDevices();
    }, [deviceId])

    return (
        <div id="project-info" style={{ marginTop: "auto" }}>
            <div className="tab-container">
                <ul className="menu">
                    <li>
                        <a href="#"><i className="fas fa-server"></i>&nbsp; <span>Thông tin Thiết bị</span></a>
                        <div id="main-button" className="text-left" style={{ bottom: 0, right: "11px", top: "-12px", position: "absolute" }}>
                            <button type="button" className="btn btn-light btn-s3m w-100px" style={{background: "#FFA87D", padding: 0, height: "27px", fontSize: "13px"}} onClick={() => history.push(`/load/${param.projectId}`)}>
                            <i className="fa-solid fa-circle-left"  style={{color: "#ffffff"}}></i>&nbsp;Quay lại
                            </button>
                        </div>
                    </li>
                </ul>
            </div>

            <div className="tab-content">
                <div className="tab-title">
                    <div className="form-group mt-2 mb-0 ml-2 mr-2">
                        <div className="input-group mr-1">
                            <div className="input-group-prepend">
                                <span className="input-group-text pickericon">
                                    <span className="fas fa-list-check"></span>
                                </span>
                            </div>
                            <select id="cbo-device-id" name="deviceId" defaultValue={param.deviceId} className="custom-select block custom-select-sm" onChange={() => onchangeDevice()}>
                                {
                                    devices.map((item, index) => (
                                        <option value={item.deviceId} key={index}>{item.deviceName}</option>
                                    ))
                                }
                            </select>
                        </div>
                    </div>
                </div>

                <div className="tab-container mb-0 ml-2">
                    <ul className="menu mr-3">
                        <li className="active backgroud-m">
                            <Link to={`/operation/${param.projectId}/${deviceId}`} onClick={() => setLine(0)}><i className="fas fa-rss"></i>&nbsp; <span>Tức thời</span></Link>
                        </li>
                        <li className="backgroud-m">
                            <Link to={`/operation/${param.projectId}/${deviceId}/operating-param`} onClick={() => setLine(25)}><i className="fas fa-tasks"></i>&nbsp; <span>Thông số</span></Link>
                        </li>
                        <li className="backgroud-m">
                            <Link to={`/operation/${param.projectId}/${deviceId}/chart-operation/1`} onClick={() => setLine(50)}><i className="fas fa-area-chart"></i>&nbsp; <span>Biểu đồ</span></Link>
                        </li>
                        <li className="backgroud-m">
                            <Link to={`/operation/${param.projectId}/${deviceId}/operating-warning`} onClick={() => setLine(75)}><i className="fas fa-exclamation-triangle"></i>&nbsp; <span>Cảnh báo</span></Link>
                        </li>
                        <div className="lineOperation" id="setLine"></div>
                    </ul>
                </div>
                <div className="tab-content">
                    <Switch>
                        <Route path={"/operation/:projectId/:deviceId/operating-param"}>
                            <OperatingParam></OperatingParam>
                        </Route>
                        <Route path={"/operation/:projectId/:deviceId/chart-operation/:chartType"}>
                            <ChartOperation></ChartOperation>
                        </Route>
                        <Route path={"/operation/:projectId/:deviceId/operating-warning"}>
                            <OperationWarning></OperationWarning>
                        </Route>
                        <Route path={"/operation/:projectId/:deviceId"}>
                            <Instant></Instant>
                        </Route>
                    </Switch>
                </div>
            </div>
        </div>
    )
};
export default OperationInformation;
