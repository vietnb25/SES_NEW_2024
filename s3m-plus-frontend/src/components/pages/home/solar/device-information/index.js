import React, { useEffect, useState } from "react";
import { Link, Route, Switch, useHistory, useParams, useLocation } from "react-router-dom";
import OperationInformationService from "../../../../../services/OperationInformationService";
import Instance from "./instance";
import './index.css';
import DeviceService from "./../../../../../services/DeviceService";
import OperatingParam from "./operating-param/operating-param";
import ChartOperation from "./chart/index";
import OperationWarning from "./operation-warning/index";
import OperationSetting from "./setting";
import CONS from "../../../../../constants/constant";

const DeviceInformation = ({ projectInfo }) => {

    const param = useParams();
    const [devices, setDevices] = useState([]);
    const [deviceSearch, setDeviceSearch] = useState([]);
    const [deviceId, setDeviceId] = useState(0);
    const [deviceType, setDeviceType] = useState(0);
    const [selectedInputDevice, setSelectedInputDevice] = useState(true);
    const [deviceName, setDeviceName] = useState("");
    const history = useHistory();
    const location = useLocation();
    const getDevices = async () => {
        let res = await OperationInformationService.getDevices(param.projectId, CONS.SYSTEM_TYPE.PV);
        if (res.status === 200 && res.data !== '') {
            let devices = res.data;
            setDevices(devices);
            if (parseInt(param.deviceId) !== 0) {
                setDeviceId(param.deviceId);
                onLoadDevice(param.deviceId);
            } else {
                setDeviceId(devices[0].deviceId);
                onLoadDevice(devices[0].deviceId);
            }
        }
    }

    const searchDevice = (e) => {
        let device_name = document.getElementById("keyword").value;
        if (device_name === "") {
            setDeviceSearch([]);
        } else {
            let deviceSearch = devices?.filter(d => d.deviceName.includes(device_name));
            setDeviceSearch(deviceSearch);
        }
    }

    const setLine = (data) => {
        let a = document.getElementById("setLine");
        a.style.left = `${data}%`;
    }

    const onLoadDevice = async (_deviceId) => {
        setDeviceId(_deviceId);
        setDeviceType();

        let deiceInfo = await DeviceService.detailsDevice(_deviceId);
        let deviceType = deiceInfo.data.deviceType;

        setDeviceType(parseInt(deviceType));

        let customerId = param.customerId;
        let deviceId = _deviceId;
        let projectId = param.projectId;
        let pathname = window.location.pathname;
        let arrPathName = pathname.split("/");

        let url = "/";
        arrPathName[3] = customerId;
        arrPathName[4] = projectId;
        arrPathName[6] = deviceId;
        arrPathName[7] = deviceType;

        for (let index = 0; index < arrPathName.length; index++) {
            if (arrPathName[index] !== "") {
                if (index === 6) {
                    arrPathName[index] = _deviceId;
                }
                url += arrPathName[index] + "/";
            }
            else {
                arrPathName[2] = "solar";
                url += arrPathName[index];
            }
        }
        history.push(url);
    }

    const changeSearch = () => {
        setSelectedInputDevice(!selectedInputDevice);
        setDeviceSearch([]);
    }

    const changeDevice = (deviceId, deviceName) => {
        document.getElementById("keyword").value = deviceName;
        onLoadDevice(deviceId);
        setDeviceSearch([]);
    }
    useEffect(() => {
        getDevices();
        document.title = "Thông tin thiết bị";
    }, [param.projectId, param.customerId]);

    return (
        <div id="project-info" style={{marginTop: "auto", width: "100%", height: "795px"}}>
            <div className="tab-content">
                <div className="tab-title">

                    <div className="project-infor" style={{ padding: "0px 10px", display: "block", marginTop: "10px" }}>
                        <span className="project-tree">{projectInfo}</span>
                    </div>
                    <div className="form-group mt-2 mb-0 ml-2 mr-2">
                        <div className="input-group mr-1">
                            {
                                selectedInputDevice ?
                                    <>
                                        <div className="input-group-prepend" onClick={() => changeSearch()}>
                                            <span className="input-group-text pickericon">
                                                <span className="fas fa-list-check"></span>
                                            </span>
                                        </div>
                                        <select id="cbo-device-id" name="deviceId" value={param.deviceId} className="custom-select block custom-select-sm lable-device-info" onChange={(e) => onLoadDevice(e.target.value)}>
                                            {
                                                devices?.map((item, index) => (
                                                    <option value={item.deviceId} key={index}>{item.deviceName}</option>
                                                ))
                                            }
                                        </select>
                                    </>
                                    :
                                    <>
                                        <div className="input-group-prepend" onClick={() => changeSearch()}>
                                            <span className="input-group-text pickericon">
                                                <span className="fas fa-magnifying-glass"></span>
                                            </span>
                                        </div>
                                        <input type="text" id="keyword" className="form-control lable-device-info" aria-label="Tìm kiếm" aria-describedby="inputGroup-sizing-sm" placeholder="Nhập tên thiết bị" onChange={() => searchDevice()} />
                                    </>
                            }
                        </div>
                        <div style={{ position: "relative", zIndex: "99", border: "none" }}>
                            {
                                deviceSearch?.map((m, index) => {
                                    return <div className="autocomplete" key={index} style={{ border: "none" }}>
                                        <div className="form-control hover-device" style={{ border: "none" }} onClick={() => changeDevice(m.deviceId, m.deviceName)}><i className="fas fa-server pr-3 pl-1"></i>{m.deviceName}</div>
                                    </div>
                                })
                            }
                        </div>
                    </div>
                </div>

                <div className="tab-container mb-0 ml-2 mt-4">
                    {
                        parseInt(deviceType) === CONS.DEVICE_TYPE_PV.INVERTER && <ul className="menu-inverter mr-3">
                            <li className="active backgroud-m">
                                <Link to={`/home/solar/${param.customerId}/${param.projectId}/device-information/${deviceId}/${deviceType}`} onClick={() => setLine(0)}><i className="fas fa-rss"></i>&nbsp; <span>Tức thời</span></Link>
                            </li>
                            <li className="backgroud-m">
                                <Link to={`/home/solar/${param.customerId}/${param.projectId}/device-information/${deviceId}/${deviceType}/operating-param`} onClick={() => setLine(20)}><i className="fas fa-tasks"></i>&nbsp; <span>Thông số</span></Link>
                            </li>
                            <li className="backgroud-m">
                                <Link to={`/home/solar/${param.customerId}/${param.projectId}/device-information/${deviceId}/${deviceType}/chart-operation`} onClick={() => setLine(40)}><i className="fas fa-area-chart"></i>&nbsp; <span>Biểu đồ <h1>{param.deviceType}</h1></span></Link>
                            </li>
                            <li className="backgroud-m">
                                <Link to={`/home/solar/${param.customerId}/${param.projectId}/device-information/${deviceId}/${deviceType}/operating-warning`} onClick={() => setLine(60)}><i className="fas fa-exclamation-triangle"></i>&nbsp; <span>Cảnh báo</span></Link>
                            </li>
                            <li className="backgroud-m">
                                <Link to={`/home/solar/${param.customerId}/${param.projectId}/device-information/${deviceId}/${deviceType}/operating-setting`} onClick={() => setLine(80)}><i className="fas fa-gear"></i>&nbsp; <span>Thông số cài đặt</span></Link>
                            </li>
                            <div className="line-inverter" id="setLine"></div>
                        </ul>
                    }
                    {
                        (parseInt(deviceType) === CONS.DEVICE_TYPE_PV.WEARTHER || parseInt(deviceType) === CONS.DEVICE_TYPE_PV.COMBINER || parseInt(deviceType) === CONS.DEVICE_TYPE_PV.STRING || parseInt(deviceType) === CONS.DEVICE_TYPE_PV.PANEL) && <ul className="menu mr-3">
                            <li className="active backgroud-m">
                                <Link to={`/home/solar/${param.customerId}/${param.projectId}/device-information/${deviceId}/${deviceType}`} onClick={() => setLine(0)}><i className="fas fa-rss"></i>&nbsp; <span>Tức thời</span></Link>
                            </li>
                            <li className="backgroud-m">
                                <Link to={`/home/solar/${param.customerId}/${param.projectId}/device-information/${deviceId}/${deviceType}/operating-param`} onClick={() => setLine(25)}><i className="fas fa-tasks"></i>&nbsp; <span>Thông số</span></Link>
                            </li>
                            <li className="backgroud-m">
                                <Link to={`/home/solar/${param.customerId}/${param.projectId}/device-information/${deviceId}/${deviceType}/chart-operation`} onClick={() => setLine(50)}><i className="fas fa-area-chart"></i>&nbsp; <span>Biểu đồ</span></Link>
                            </li>
                            <li className="backgroud-m">
                                <Link to={`/home/solar/${param.customerId}/${param.projectId}/device-information/${deviceId}/${deviceType}/operating-warning`} onClick={() => setLine(75)}><i className="fas fa-exclamation-triangle"></i>&nbsp; <span>Cảnh báo</span></Link>
                            </li>
                            <div className="lineOperation" id="setLine"></div>
                        </ul>
                    }


                </div>
                <div className="tab-content">
                    <Switch>
                        <Route path={"/home/solar/:customerId/:projectId/device-information/:deviceId/:deviceType/operating-param"}>
                            <OperatingParam />
                        </Route>
                        <Route path={"/home/solar/:customerId/:projectId/device-information/:deviceId/:deviceType/chart-operation"}>
                            <ChartOperation />
                        </Route>
                        <Route path={"/home/solar/:customerId/:projectId/device-information/:deviceId/:deviceType/operating-warning"}>
                            <OperationWarning />
                        </Route>
                        <Route path={"/home/solar/:customerId/:projectId/device-information/:deviceId/:deviceType/operating-setting"}>
                            <OperationSetting />
                        </Route>
                        <Route path={"/home/solar/:customerId/:projectId/device-information/:deviceId/:deviceType"}>
                            <Instance />
                        </Route>
                    </Switch>
                </div>
            </div>
        </div>
    )
}

export default DeviceInformation;