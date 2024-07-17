import { useState } from "react";
import { useEffect } from "react";
import { useRef } from "react";
import { Link, Route, Switch, useHistory, useLocation, useParams } from "react-router-dom/cjs/react-router-dom.min";
import { Calendar } from 'primereact/calendar';
import ProjectService from "../../../../../services/ProjectService";
import DeviceService from "../../../../../services/DeviceService";
import AuthService from "../../../../../services/AuthService";
import CustomerService from "../../../../../services/CustomerService";
import AccessDenied from "../../../access-denied/AccessDenied";
import moment from "moment";
import CONS from "../../../../../constants/constant";
import "./../index.css"
import { color } from "d3";
import { t } from "i18next";

const ObjectTwoLevel1 = () => {

    const [userName] = useState(AuthService.getUserName());
    const [role] = useState(AuthService.getRoleName());
    const history = useHistory();
    const [routeType, setRouteType] = useState("");
    const [objectTypeName, setObjectTypeName] = useState("")
    const [devices, setListDevices] = useState([])
    const param = useParams();
    const [devicesDefault, setListDevicesDefault] = useState([])
    const [type, setType] = useState(0);
    const [customerName, setCustomerName] = useState()
    const [typeName, setTypeName] = useState();
    const [accessDenied, setAccessDenied] = useState(false)

    const getObjectType = async (objectTypeId) => {
        let res = await DeviceService.getObjectType(objectTypeId)
        if (res.status == 200) {
            setObjectTypeName(res.data)
        }
    }

    const getListDevice = async (customerId, projectId, objectTypeId) => {
        if (projectId == 0) {
            let res = await DeviceService.getListDeviceTwoLevelByCusSys(customerId, objectTypeId);
            if (res.status === 200) {
                setListDevices(res.data)
                setListDevicesDefault(res.data)
                setRouteType("Tất cả dự án")
            }
        } else {
            let respone = await ProjectService.getProject(projectId)
            if (respone.status == 200) {
                setRouteType(respone.data.projectName)
            }
            let res = await DeviceService.getListDeviceTwoLevelByProSys(customerId, projectId, objectTypeId)
            if (res.status === 200) {
                setListDevices(res.data)
                setListDevicesDefault(res.data)
            }
        }
    }

    const onFilterDeviceByName = (e) => {
        let name = e.target.value
        if (name == "") {
            setListDevices(devicesDefault)
        } else {
            let deviceFilter = devicesDefault.filter(device => device.objectTypeName.toLowerCase().includes(name.toLowerCase()))
            setListDevices(deviceFilter)
        }
    }

    const onFilterByStatusDevice = (type) => {
        if (type == 0) {
            setListDevices(devicesDefault)
        }
        if (type == 1) {
            let list = devicesDefault.filter(device => device.status == "active")
            setListDevices(list);
        }
        if (type == 2) {
            let list = devicesDefault.filter(device => device.status == "warning")
            setListDevices(list);
        }
        if (type == 3) {
            let list = devicesDefault.filter(device => device.status == "offline")
            setListDevices(list);
        }

    }

    const getListObject = async (customerId, projectIds) => {
        let res = await DeviceService.getListObject(param.customerId, param.projectId, param.objectTypeId, param.type, projectIds)
        setType(param.type);
        if (param.type == 1) {
            setTypeName("Tải điện");
        } else if (param.type == 2) {
            setTypeName("Điện mặt trời");
        } else if (param.type == 5) {
            setTypeName("Lưới điện");
        } else if (param.type == 3) {
            setTypeName("Pin lưu trữ");
        } else if (param.type == 4) {
            setTypeName("Điện gió");
        } else {
            setTypeName("Tất cả")
        }
        if (res.status == 200) {
            setListDevices(res.data)
            setListDevicesDefault(res.data)
            if (param.projectId == 0) {
                setRouteType("Tất cả dự án")
                setObjectTypeName(res.data[0].objectName)
                setCustomerName(res.data[0].customerName)
            } else {
                setRouteType(res.data[0].projectName)
                setObjectTypeName(res.data[0].objectName)
                setCustomerName(res.data[0].customerName)
            }
        }
    }

    const funcSetType = async (e) => {
        if (e.target.value == 0) {
            setType(() => e.target.value)
            setTypeName("Tất cả");
            setListDevices(devicesDefault)
        } else {
            setType(() => e.target.value)
            if (e.target.value == 1) {
                setTypeName("Tải điện");
            } else if (e.target.value == 2) {
                setTypeName("Điện mặt trời");
            } else if (e.target.value == 5) {
                setTypeName("Lưới điện");
            } else if (e.target.value == 3) {
                setTypeName("Pin lưu trữ");
            } else if (e.target.value == 4) {
                setTypeName("Điện gió");
            }
            if (param.type == 0) {
                let proJId = param.projectId != undefined ? param.projectId : null
                let res = await DeviceService.getListObject(param.customerId, proJId, param.objectTypeId, e.target.value)
                if (res.status == 200) {
                    if (res.data[0] === null) {
                        setListDevices()
                    } else {
                        setListDevices(res.data)
                    }
                }
            } else {
                if (param.type == e.target.value) {
                    setListDevices(devicesDefault)
                } else {
                    setListDevices()
                }
            }
        }
    }

    const checkAuthorization = async () => {
        let cusId = 0;
        if (param.customerId !== undefined) {
            cusId = param.customerId;
        }
        let check = true;
        let resCheckProjectInCus = await ProjectService.getProjectByCustomerId(cusId);
        if (param.projectId != 0) {
            check = resCheckProjectInCus.data.some(project => parseInt(project.projectId) === parseInt(param.projectId));
            if (!check) {
                setAccessDenied(true);
            }
        }
        if (check) {
            if (role === "ROLE_ADMIN") {
                setAccessDenied(false)
                getListObject(param.customerId, "")
            }
            if (role === "ROLE_MOD") {
                let customerIds = ""

                let res = await CustomerService.getCustomerIds(userName)
                if (res.status === 200 && res.data !== '') {
                    for (let i = 0; i < res.data.length; i++) {
                        customerIds += res.data[i].customerId + ","
                    }
                }

                if (param.customerId != undefined) {
                    if (!customerIds.includes(param.customerId)) {
                        setAccessDenied(true)
                    } else {
                        setAccessDenied(false)
                        getListObject(param.customerId, "")
                    }
                }
            }
            if (role == "ROLE_USER") {
                let customerIds = ""
                let projIds = ""

                let res = await CustomerService.getCustomerIds(userName)
                if (res.status === 200 && res.data !== '') {
                    for (let i = 0; i < res.data.length; i++) {
                        customerIds += res.data[i].customerId + ","
                    }
                }

                if (param.customerId != undefined) {
                    if (!customerIds.includes(param.customerId)) {
                        setAccessDenied(true)
                    } else {
                        let re = await ProjectService.getProIds(userName)
                        if (re.status === 200 && re.data !== "") {
                            projIds = "" + re.data
                        }
                        if (param.projectId != null && param.projectId != 0) {
                            if (!projIds.includes(param.projectId)) {
                                setAccessDenied(true)
                            } else {
                                setAccessDenied(false)
                                getListObject(param.customerId, param.projectId)
                            }
                        } else {
                            setAccessDenied(false)
                            getListObject(param.customerId, projIds)
                        }
                    }
                }
            }
        }
    }

    useEffect(() => {
        checkAuthorization()
        // getListObject()
        document.title = "Thông tin thiết bị";
    }, [param.objectTypeId]);

    return (
        <>
            {!accessDenied ?
                <>
                    <div className="position-block">
                        <div className="position-block">
                            <div className="float-left ml-2"><strong className="font-route">{customerName} / {routeType} / {typeName} / {objectTypeName}</strong></div>
                            <div className="float-right" style={{ marginRight: "1%" }}>
                                <button className="button-back" onClick={() => history.goBack()}>
                                    <img src="/resources/image/icon-back.svg" height="30px"></img>
                                </button>
                            </div>
                        </div>
                    </div>
                    <div className="div-list-equipment">
                        <div className="mt-2" style={{ width: "100%", height: "5px", backgroundColor: "#0a1a5c" }} ></div>
                        <div className="header-infor mt-1">
                            <div className="system-type">
                                <div className="radio-tabs">
                                    {param.type == 0 ? <>
                                        <label className="radio-tabs__field text-uppercase">
                                            <input type="radio" name="radio-tabs" value={0} className="radio-tabs__input" checked={type == 0 ? true : false} onChange={(e) => funcSetType(e)} />
                                            <span className="radio-tabs__text">
                                                <img src="/resources/image/icon-load-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                                {t('content.all')}</span>
                                        </label>
                                    </> : <></>}
                                    <label className="radio-tabs__field text-uppercase">
                                        <input type="radio" name="radio-tabs" value={1} className="radio-tabs__input" checked={type == 1 ? true : false} onChange={(e) => funcSetType(e)} />
                                        <span className="radio-tabs__text">
                                            <img src="/resources/image/icon-load-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                            {t('content.home_page.load')}</span>
                                    </label>
                                    <label className="radio-tabs__field text-uppercase" >
                                        <input type="radio" name="radio-tabs" value={2} className="radio-tabs__input" checked={type == 2 ? true : false} onChange={(e) => funcSetType(e)} />
                                        <span className="radio-tabs__text">
                                            <img src="/resources/image/icon-solar-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                            {t('content.home_page.solar')}</span>
                                    </label>
                                    <label className="radio-tabs__field text-uppercase" >
                                        <input type="radio" name="radio-tabs" value={5} className="radio-tabs__input" checked={type == 5 ? true : false} onChange={(e) => funcSetType(e)} />
                                        <span className="radio-tabs__text">
                                            <img src="/resources/image/icon-grid-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                            {t('content.home_page.grid')}</span>
                                    </label>
                                    <label className="radio-tabs__field text-uppercase" >
                                        <input type="radio" name="radio-tabs" value={3} className="radio-tabs__input" checked={type == 3 ? true : false} onChange={(e) => funcSetType(e)} />
                                        <span className="radio-tabs__text">
                                            <img src="/resources/image/icon-battery-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                            {t('content.home_page.battery')}</span>
                                    </label>
                                    <label className="radio-tabs__field text-uppercase" >
                                        <input type="radio" name="radio-tabs" value={4} className="radio-tabs__input" checked={type == 4 ? true : false} onChange={(e) => funcSetType(e)} />
                                        <span className="radio-tabs__text">
                                            <img src="/resources/image/icon-wind-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                            {t('content.home_page.wind')}</span>
                                    </label>
                                </div>
                                <div className="position-relative mt-1"><input type="text" className="" name="equipment-name" placeholder="   Tìm kiếm...." onChange={(e) => onFilterDeviceByName(e)} />
                                    <i className="fa fa-search position-absolute" style={{ left: "160px", top: "10px" }}></i>
                                    <div className="type-status">
                                        <div className="status">
                                            <button><div className="all" onClick={() => onFilterByStatusDevice(0)}><i className="icon fa-solid fa-arrows-up-down-left-right"></i></div></button>
                                            <span>{t('content.all')}</span>

                                        </div>
                                        <div className="status">
                                            <button><div className="online" onClick={() => onFilterByStatusDevice(1)}><i className="icon fa-solid fa-check" style={{ color: "white" }}></i></div></button>
                                            <span>{t('content.home_page.normal')}</span>
                                        </div>
                                        <div className="status">

                                            <button><div className="warning" onClick={() => onFilterByStatusDevice(2)}><i className="icon fa-solid fa-triangle-exclamation" style={{ color: "white" }}></i></div></button>
                                            <span>{t('content.warning')}</span>
                                        </div>
                                        <div className="status">
                                            <button><div className="offline" onClick={() => onFilterByStatusDevice(3)}><i className="icon fa-solid fa-xmark" style={{ color: "white" }}></i></div></button>
                                            <span>{t('content.home_page.lost_signal')}</span>
                                        </div>
                                    </div>
                                </div>
                            </div>

                        </div>

                        <div className="div-list-equipment">
                            <div className="mt-2" style={{ borderBottom: "solid 2px" }}>

                            </div>

                            {devices?.map((device, index) => (
                                <div className="list-equipment-object-grid mt-1 mb-1" key={index}>
                                    <Link className="button-icon" to={`/${param.customerId}/${param.projectId}/device-information/${device.objectTypeId}/${device.objectId}/${type}`} title="Thông tin chi tiết" key={index}>
                                        <div className="list-equipment">
                                            <div className="header-equipment mb-4">
                                                <div className="float-left">
                                                    <div className={device.status}></div>
                                                </div>
                                                <div className="float-right number" style={{ width: "180px" }}>{t('content.home_page.device.no_measure_point')}: {device.countDevice}</div>
                                            </div>
                                            <div className="body-equipment">
                                                <img src={device.img != null ? device.img : "/resources/image/icon-khac.svg"} height="200px"></img>
                                            </div>
                                            <div className="footer-equipment">
                                                <p>{device.objectTypeName}</p>
                                            </div>
                                        </div>
                                    </Link>
                                </div>
                            ))
                            }

                        </div>
                    </div>
                </>
                :
                <AccessDenied></AccessDenied>
            }
        </>
    )
}

export default ObjectTwoLevel1;