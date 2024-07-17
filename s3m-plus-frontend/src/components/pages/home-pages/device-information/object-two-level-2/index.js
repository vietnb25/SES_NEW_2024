import { useState } from "react";
import { useEffect } from "react";
import { Link, Route, Switch, useHistory, useLocation, useParams } from "react-router-dom/cjs/react-router-dom.min";
import DeviceService from "../../../../../services/DeviceService";
import AccessDenied from "../../../access-denied/AccessDenied";
import { useTranslation } from "react-i18next";
import "./../index.css"
import { t } from "i18next";

const ObjectTwoLevel2 = () => {

    const history = useHistory();
    const [routeType, setRouteType] = useState("");
    const [objectTypeName, setObjectTypeName] = useState("")
    const [objectName, setObjectName] = useState("")
    const [devices, setListDevices] = useState([])
    const param = useParams();
    const [status, setStatus] = useState(true)
    const [devicesDefault, setListDevicesDefault] = useState([])
    const [type, setType] = useState(0);
    const [customerName, setCustomerName] = useState()
    const [typeName, setTypeName] = useState();
    const [accessDenied, setAccessDenied] = useState(false)
    const { t } = useTranslation();
    const [nameFil, setNameFil] = useState({
        loadType: [],
        deviceType: [],
        location: []
    });

    const onFilterByStatusDevice = (type) => {
        if (type == 0) {
            setListDevices(devicesDefault)
        }
        if (type == 1) {
            let list = devicesDefault.filter(device => device.statusDevice == "active")
            setListDevices(list);
        }
        if (type == 2) {
            let list = devicesDefault.filter(device => device.statusDevice == "warning")
            setListDevices(list);
        }
        if (type == 3) {
            let list = devicesDefault.filter(device => device.statusDevice == "offline")
            setListDevices(list);
        }

    }

    const getListDevice = async () => {
        let nameFilter = {
            loadType: [],
            deviceType: [],
            location: []
        }
        let res = await DeviceService.getListDevice(param.customerId, param.projectId, param.objectId, param.type)
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
            if (res.data.length > 0) {
                setAccessDenied(false)
                setCustomerName(res.data[0].customerName)
                setObjectName(res.data[0].objectName)
                setObjectTypeName(res.data[0].objectTypeName)
                if (param.projectId == 0) {
                    setRouteType("Tất cả dự án")
                } else {
                    setRouteType(res.data[0].projectName)
                }
                setListDevices(res.data)
                for (let i = 0; i < res.data.length; i++) {
                    if (res.data[i].loadTypeName != null) {
                        nameFilter.loadType.push(res.data[i].loadTypeName)
                    }
                    if (res.data[i].deviceTypeName != null) {
                        nameFilter.deviceType.push(res.data[i].deviceTypeName)
                    }
                    if (res.data[i].location != null) {
                        nameFilter.location.push(res.data[i].location)
                    }
                }
                nameFilter.loadType = Array.from(new Set(nameFilter.loadType));
                nameFilter.deviceType = Array.from(new Set(nameFilter.deviceType));
                nameFilter.location = Array.from(new Set(nameFilter.location));

                setNameFil(nameFilter);

                setListDevicesDefault(res.data)
            } else {
                setAccessDenied(true)
            }
        }
    }
    const funcSetType = async (e) => {
        if (e.target.value == 0) {
            setType(() => e.target.value)
            setListDevices(devicesDefault)
            setTypeName("Tất cả");
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
                let res = await DeviceService.getListDevice(param.customerId, proJId, param.objectId, e.target.value)

                if (res.status == 200) {
                    setListDevices(res.data)
                } else {
                    setListDevices()
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

    const onFilterDeviceByName = () => {
        let name = document.getElementById("inputName").value
        let location = document.getElementById("inputLocation").value
        let loadType = document.getElementById("inputLoadType").value
        let deviceType = document.getElementById("inputDeviceType").value
        let list = devicesDefault
        if (name == "" && location == "" && deviceType == "" && location == "") {
            setListDevices(devicesDefault)
        }
        if (name != "") {
            list = list.filter(device => device.deviceName.toLowerCase().includes(name.toLowerCase()))
            setListDevices(list)
        }
        if (location != "") {
            list = list.filter(device => device.location != null ? device.location.toLowerCase().includes(location.toLowerCase()) : 0)
            setListDevices(list)
        }
        if (loadType != "") {
            list = list.filter(device => device.loadTypeName != null ? loadType.toLowerCase().includes(device.loadTypeName.toLowerCase()) : 0)
            setListDevices(list)
        }
        if (deviceType != "") {
            list = list.filter(device => device.deviceTypeName.toLowerCase().includes(deviceType.toLowerCase()))
            setListDevices(list)
        }

    }

    useEffect(() => {
        getListDevice()
        document.title = "Thông tin thiết bị";
    }, [param.objectTypeId]);

    return (
        <>
            {!accessDenied ?
                <>
                    <div className="position-block">
                        <div className="position-block">
                            <div className="float-left ml-2"><strong className="font-route">{customerName} / {routeType} / {typeName} / {objectName} / {objectTypeName}</strong></div>
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
                                        <label className="radio-tabs__field text-uppercase" >
                                            <input type="radio" name="radio-tabs" value={0} className="radio-tabs__input" checked={type == 0 ? true : false} onChange={(e) => funcSetType(e)} />
                                            <span className="radio-tabs__text">
                                                <img src="/resources/image/icon-load-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                                {t('content.all')}</span>
                                        </label>
                                    </> : <></>}
                                    <label className="radio-tabs__field text-uppercase" >
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
                                <div className="position-relative mt-1"><input id="inputName" type="text" className="" name="equipment-name" placeholder={t('content.home_page.search')} onChange={(e) => onFilterDeviceByName(e)} />
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
                                            <span>{t('content.home_page.warning')}</span>
                                        </div>
                                        <div className="status">
                                            <button><div className="offline" onClick={() => onFilterByStatusDevice(3)}><i className="icon fa-solid fa-xmark" style={{ color: "white" }}></i></div></button>
                                            <span>{t('content.home_page.lost_signal')}</span>
                                        </div>
                                    </div>
                                </div>
                                <div className="option-time">
                                    <select id="inputLocation" className="button-time-block" onChange={(e) => onFilterDeviceByName(e)}>
                                        <option value="">{t('content.home_page.area')}</option>
                                        {nameFil.location != null &&
                                            <>
                                                {nameFil.location?.map((data, index) => (
                                                    <option key={index} value={data}>{data}</option>
                                                ))
                                                }
                                            </>
                                        }
                                    </select>
                                    <select id="inputLoadType" className="button-time-block" onChange={(e) => onFilterDeviceByName(e)}>
                                        <option value="">{t('content.home_page.load_type')}</option>
                                        {nameFil.loadType != null &&
                                            <>
                                                {nameFil.loadType?.map((data, index) => (
                                                    <option key={index} value={data}>{data}</option>
                                                ))
                                                }
                                            </>
                                        }
                                    </select>
                                    <select id="inputDeviceType" className="button-time-block" onChange={(e) => onFilterDeviceByName(e)}>
                                        <option value="">{t('content.home_page.device_type')}</option>
                                        {nameFil.deviceType != null &&
                                            <>
                                                {nameFil.deviceType?.map((data, index) => (
                                                    <option key={index} value={data}>{data}</option>
                                                ))
                                                }
                                            </>
                                        }
                                    </select>
                                </div>
                            </div>

                        </div>

                        <div className="div-list-equipment">
                            <div className="mt-2" style={{ borderBottom: "solid 2px" }}>

                            </div>
                            <>
                                {devices?.map((device, index) => (
                                    <span key={index}>
                                        <Link className="button-icon" to={`/${param.customerId}/device-information/${device.deviceId}`} title="Thông tin chi tiết">
                                            <div className="item-equipment position-relative"
                                                key={index}>

                                                <div className="header-item-equipment mb-4">
                                                    <div className={device.statusDevice}></div>
                                                    <div className="text-center number text-color-ses-2">{device.deviceName}</div>
                                                </div>
                                                {device.deviceType == 1 &&
                                                    <div className="body-equipment ml-5 d-flex">
                                                        <div className="float-left">
                                                            <p>{t('content.project')}</p>
                                                            <p>{t('content.home_page.device.total_active_power')}</p>
                                                            <p>{t('content.home_page.device.total_reactive_power')}</p>
                                                            <p>{t('content.home_page.device.average_volt')}</p>
                                                            <p>{t('content.home_page.device.average_current')}</p>
                                                            <p>{t('content.home_page.device.cosphi')}</p>
                                                        </div>
                                                        {device.statusDevice !== "offline" ?
                                                            <div className="float-left ml-2">
                                                                <p className="text-color-ses-1 tagName">{device.projectName}</p>
                                                                <p className="text-color-ses-1 tagName" style={{ position: "absolute", top: "98px" }}>{device.ptotal != null ? device.ptotal : "-"} kW</p>
                                                                <p className="text-color-ses-1 tagName" style={{ position: "absolute", top: "127px" }}>{device.qtotal >= 0 ? device.qtotal : "-"} kVAr</p>
                                                                <p className="text-color-ses-1 tagName" style={{ position: "absolute", top: "156px" }}>{device.uab >= 0 ? ((device.uab + device.ubc + device.uca) / 3).toFixed(2) : "-"} V</p>
                                                                <p className="text-color-ses-1 tagName" style={{ position: "absolute", top: "183px" }}>{device.ia >= 0 ? ((device.ia + device.ib + device.ic) / 3).toFixed(2) : "-"} A</p>
                                                                <p className="text-color-ses-1 tagName" style={{ position: "absolute", top: "211px" }}>{device.pfa !== 0 ? ((Math.abs(device.pfa) + Math.abs(device.pfb) + Math.abs(device.pfc)) / 3).toFixed(2) : "-"}</p>
                                                            </div>
                                                            :
                                                            <div className="float-left ml-2">
                                                                <p className="text-color-ses-1 tagName">{device.projectName}</p>
                                                                <p className="text-color-ses-1 tagName" style={{ position: "absolute", top: "98px" }}>- kW</p>
                                                                <p className="text-color-ses-1 tagName" style={{ position: "absolute", top: "127px" }}>- kVAr</p>
                                                                <p className="text-color-ses-1 tagName" style={{ position: "absolute", top: "156px" }}>- V</p>
                                                                <p className="text-color-ses-1 tagName" style={{ position: "absolute", top: "183px" }}>- A</p>
                                                                <p className="text-color-ses-1 tagName" style={{ position: "absolute", top: "211px" }}>-</p>
                                                            </div>
                                                        }
                                                        <div className="position-absolute" style={{ top: "70%", left: "80%" }}>
                                                            <img src={device.img != null ? device.img : "/resources/image/icon-khac.svg"} alt="Ảnh" height="50px"></img>
                                                        </div>
                                                    </div>
                                                }
                                                {device.deviceType == 3 &&
                                                    <>
                                                        <div className="body-equipment ml-5 d-flex">
                                                            <div className="float-left">
                                                                <p>{t('content.project')}</p>
                                                                <p>{t('content.temperature')}</p>
                                                                <p>{t('content.humidity')}</p>
                                                            </div>
                                                            {device.statusDevice !== "offline" ?
                                                                <div className="float-left ml-2">
                                                                    <p className="text-color-ses-1">{device.projectName}</p>
                                                                    <p className="text-color-ses-1">{device.tsensor != null ? device.tsensor : "-"} °C</p>
                                                                    <p className="text-color-ses-1">{device.h >= 0 ? device.h : "-"} %</p>
                                                                </div>
                                                                :
                                                                <div className="float-left ml-2">
                                                                    <p className="text-color-ses-1">{device.projectName}</p>
                                                                    <p className="text-color-ses-1">- °C</p>
                                                                    <p className="text-color-ses-1">- %</p>
                                                                </div>
                                                            }
                                                            <div className="position-absolute" style={{ top: "70%", left: "80%" }}>
                                                                <img src={device.img != null ? device.img : "/resources/image/icon-khac.svg"} alt="Ảnh" height="50px"></img>
                                                            </div>
                                                        </div>
                                                    </>
                                                }
                                                {device.deviceType == 4 &&
                                                    <>
                                                        <div className="body-equipment ml-5 d-flex">
                                                            <div className="float-left">
                                                                <p>{t('content.project')}</p>
                                                                <p>{t('content.status')}</p>
                                                            </div>
                                                            {device.statusDevice !== "offline" ?
                                                                <div className="float-left ml-2">
                                                                    <p className="text-color-ses-1">{device.projectName}</p>
                                                                    <p className="text-color-ses-1">{device.status == 1 ? "Hoạt động" : "Không hoạt động"} </p>
                                                                </div>
                                                                :
                                                                <div className="float-left ml-2">
                                                                    <p className="text-color-ses-1">{device.projectName}</p>
                                                                    <p className="text-color-ses-1">-</p>
                                                                </div>
                                                            }
                                                            <div className="position-absolute" style={{ top: "70%", left: "80%" }}>
                                                                <img src={device.img != null ? device.img : "/resources/image/icon-khac.svg"} alt="Ảnh" height="50px"></img>
                                                            </div>
                                                        </div>
                                                    </>
                                                }
                                                {device.deviceType == 2 &&
                                                    <div className="body-equipment ml-5">
                                                        <div className="float-left">
                                                            <p>{t('content.project')}</p>
                                                            <p>{t('content.home_page.device.total_active_power')}</p>
                                                            <p>{t('content.home_page.device.total_reactive_power')}</p>
                                                            <p>{t('content.home_page.device.average_volt')}</p>
                                                            <p>{t('content.home_page.device.average_current')}</p>
                                                            <p>{t('content.home_page.device.cosphi')}</p>
                                                        </div>
                                                        {device.statusDevice !== "offline" ?
                                                            <div className="float-left ml-2">
                                                                <p className="text-color-ses-1">{device.projectName}</p>
                                                                <p className="text-color-ses-1">{device.ptotal != null ? device.ptotal : "-"} kW</p>
                                                                <p className="text-color-ses-1">{device.qtotal >= 0 ? device.qtotal : "-"} kVAr</p>
                                                                <p className="text-color-ses-1">{device.uab >= 0 ? ((device.uab + device.ubc + device.uca) / 3).toFixed(2) : "-"} V</p>
                                                                <p className="text-color-ses-1">{device.ia >= 0 ? ((device.ia + device.ib + device.ic) / 3).toFixed(2) : "-"} A</p>
                                                                <p className="text-color-ses-1">{device.pfa !== 0 && device.pfa != null ? ((Math.abs(device.pfa) + Math.abs(device.pfb) + Math.abs(device.pfc)) / 3).toFixed(2) : "-"}</p>
                                                            </div>

                                                            :
                                                            <div className="float-left ml-2">
                                                                <p className="text-color-ses-1">{device.projectName}</p>
                                                                <p className="text-color-ses-1">- kW</p>
                                                                <p className="text-color-ses-1">- kVAr</p>
                                                                <p className="text-color-ses-1">- V</p>
                                                                <p className="text-color-ses-1">- A</p>
                                                                <p className="text-color-ses-1">-</p>
                                                            </div>
                                                        }
                                                        <div className="position-absolute" style={{ top: "70%", left: "80%" }}>
                                                            <img src={device.img != null ? device.img : "/resources/image/icon-khac.svg"} alt="Ảnh" height="50px"></img>
                                                        </div>
                                                    </div>
                                                }
                                                {(device.deviceType == 9 && device.systemTypeId === 1) &&
                                                    <div className="body-equipment ml-5 d-flex">
                                                        <div className="float-left">
                                                            <p>{t('content.project')}</p>
                                                            <p>{t('content.home_page.device.total_active_power')}</p>
                                                            <p>{t('content.home_page.device.total_reactive_power')}</p>
                                                            <p>{t('content.home_page.device.average_volt')}</p>
                                                            <p>{t('content.home_page.device.average_current')}</p>
                                                            <p>{t('content.home_page.device.cosphi')}</p>
                                                        </div>
                                                        {device.operatingStatus !== "offline" ?
                                                            <div className="float-left ml-2">
                                                                <p className="text-color-ses-1">{device.projectName}</p>
                                                                <p className="text-color-ses-1"> - </p>
                                                                <p className="text-color-ses-1"> - </p>
                                                                <p className="text-color-ses-1"> - </p>
                                                                <p className="text-color-ses-1"> - </p>
                                                                <p className="text-color-ses-1"> - </p>
                                                            </div>
                                                            :
                                                            <div className="float-left ml-2">
                                                                <p className="text-color-ses-1">{device.projectName}</p>
                                                                <p className="text-color-ses-1"> - </p>
                                                                <p className="text-color-ses-1"> - </p>
                                                                <p className="text-color-ses-1"> - </p>
                                                                <p className="text-color-ses-1"> - </p>
                                                                <p className="text-color-ses-1"> - </p>
                                                            </div>
                                                        }
                                                    </div>
                                                }
                                                {device.deviceType == 5 &&
                                                    <>
                                                        <div className="body-equipment ml-5 d-flex">
                                                            <div className="float-left">
                                                                <p>{t('content.project')}</p>
                                                                <p>{t('content.home_page.chart.discharge_indicator')}</p>
                                                            </div>
                                                            {device.statusDevice !== "offline" ?
                                                                <div className="float-left ml-2">
                                                                    <p className="text-color-ses-1">{device.projectName}</p>
                                                                    <p className="text-color-ses-1">{device.indicator != null ? device.indicator : "-"} </p>
                                                                </div>
                                                                :
                                                                <div className="float-left ml-2">
                                                                    <p className="text-color-ses-1">{device.projectName}</p>
                                                                    <p className="text-color-ses-1">-</p>
                                                                </div>
                                                            }
                                                            <div className="position-absolute" style={{ top: "70%", left: "80%" }}>
                                                                <img src={device.img != null ? device.img : "/resources/image/icon-khac.svg"} alt="Ảnh" height="50px"></img>
                                                            </div>
                                                        </div>
                                                    </>
                                                }
                                                {device.deviceType == 6 &&
                                                    <>
                                                        <div className="body-equipment ml-5 d-flex">
                                                            <div className="float-left">
                                                                <p>{t('content.project')}</p>
                                                                <p>{t('content.home_page.chart.discharge_indicator')}</p>
                                                            </div>
                                                            {device.statusDevice !== "offline" ?
                                                                <div className="float-left ml-2">
                                                                    <p className="text-color-ses-1">{device.projectName}</p>
                                                                    <p className="text-color-ses-1">{device.indicator != null ? device.indicator : "-"} </p>
                                                                </div>
                                                                :
                                                                <div className="float-left ml-2">
                                                                    <p className="text-color-ses-1">{device.projectName}</p>
                                                                    <p className="text-color-ses-1">-</p>
                                                                </div>
                                                            }
                                                            <div className="position-absolute" style={{ top: "70%", left: "80%" }}>
                                                                <img src={device.img != null ? device.img : "/resources/image/icon-khac.svg"} alt="Ảnh" height="50px"></img>
                                                            </div>
                                                        </div>
                                                    </>
                                                }
                                                {device.deviceType == 7 &&
                                                    <>
                                                        <div className="body-equipment ml-5 d-flex">
                                                            <div className="float-left">
                                                                <p>{t('content.project')}</p>
                                                                <p>Áp suất không khí</p>
                                                            </div>
                                                            {device.statusDevice !== "offline" ?
                                                                <div className="float-left ml-2">
                                                                    <p className="text-color-ses-1">{device.projectName}</p>
                                                                    <p className="text-color-ses-1">{device.p != null ? device.p + " Kpa" : "- Kpa"} </p>
                                                                </div>
                                                                :
                                                                <div className="float-left ml-2">
                                                                    <p className="text-color-ses-1">{device.projectName}</p>
                                                                    <p className="text-color-ses-1">- Kpa</p>
                                                                </div>
                                                            }
                                                            <div className="position-absolute" style={{ top: "70%", left: "80%" }}>
                                                                <img src={device.img != null ? device.img : "/resources/image/icon-khac.svg"} alt="Ảnh" height="50px"></img>
                                                            </div>
                                                        </div>
                                                    </>
                                                }
                                                {device.deviceType == 10 &&
                                                    <>
                                                        <div className="body-equipment ml-5 d-flex">
                                                            <div className="float-left">
                                                                <p>{t('content.project')}</p>
                                                                <p>Lưu lượng / giây</p>
                                                            </div>
                                                            {device.statusDevice !== "offline" ?
                                                                <div className="float-left ml-2">
                                                                    <p className="text-color-ses-1">{device.projectName}</p>
                                                                    <p className="text-color-ses-1">{device.fs != null ? device.fs + " m³/s" : "- m³/s"} </p>
                                                                </div>
                                                                :
                                                                <div className="float-left ml-2">
                                                                    <p className="text-color-ses-1">{device.projectName}</p>
                                                                    <p className="text-color-ses-1">- m³/s</p>
                                                                </div>
                                                            }
                                                            <div className="position-absolute" style={{ top: "70%", left: "80%" }}>
                                                                <img src={device.img != null ? device.img : "/resources/image/icon-khac.svg"} alt="Ảnh" height="50px"></img>
                                                            </div>
                                                        </div>
                                                    </>
                                                }
                                            </div>
                                        </Link>
                                    </span>
                                ))

                                }
                            </>
                        </div>
                    </div>
                </>
                :
                <AccessDenied></AccessDenied>
            }
        </>
    )
}

export default ObjectTwoLevel2;