import { useState } from "react";
import { useEffect } from "react";
import { useRef } from "react";
import { Link, Route, Switch, useHistory, useLocation, useParams } from "react-router-dom/cjs/react-router-dom.min";
import { Calendar } from 'primereact/calendar';
import ProjectService from "../../../../../services/ProjectService";
import DeviceService from "../../../../../services/DeviceService";
import moment from "moment";
import CONS from "../../../../../constants/constant";
import "./../index.css"
import { t } from "i18next";


const ObjectOneLevel = () => {

    const history = useHistory();
    const [routeType, setRouteType] = useState("");
    const [objectTypeName, setObjectTypeName] = useState("")
    const [devices, setListDevices] = useState([])
    const [devicesDefault, setListDevicesDefault] = useState([])
    const param = useParams();
    const [status, setStatus] = useState(true)

    const getObjectType = async (objectTypeId) => {
        let res = await DeviceService.getObjectType(objectTypeId)
        if (res.status == 200) {
            setObjectTypeName(res.data)
        }
    }

    const getListDevice = async (customerId, projectId, objectTypeId) => {
        if (projectId == 0) {
            let res = await DeviceService.getListDeviceOneLevelByCusSys(customerId, objectTypeId);
            if (res.status === 200) {
                setListDevices(res.data)
                setListDevicesDefault(res.data)
                setRouteType("ALL SITE")
            }
        } else {
            let respone = await ProjectService.getProject(projectId)
            if (respone.status == 200) {
                setRouteType(respone.data.projectName)
            }
            let res = await DeviceService.getListDeviceOneLevelByProSys(customerId, projectId, objectTypeId)
            if (res.status === 200) {
                setListDevices(res.data)
                setListDevicesDefault(res.data)
            }
        }
    }

    const onFilterByStatusDevice = (type) => {
        if (type == 0) {
            setListDevices(devicesDefault)
        }
        if (type == 1) {
            let list = devicesDefault.filter(device => device.operatingStatus == "active")
            setListDevices(list);
        }
        if (type == 2) {
            let list = devicesDefault.filter(device => device.operatingStatus == "warning")
            setListDevices(list);
        }
        if (type == 3) {
            let list = devicesDefault.filter(device => device.operatingStatus == "offline")
            setListDevices(list);
        }

    }

    const onFilterDeviceByName = (e) => {
        let name = e.target.value
        if (name == "") {
            setListDevices(devicesDefault)
        } else {
            let deviceFilter = devicesDefault.filter(device => device.deviceName.toLowerCase().includes(name.toLowerCase()))
            setListDevices(deviceFilter)
        }
    }

    useEffect(() => {
        getObjectType(param.objectTypeId)
        getListDevice(param.customerId, param.projectId, param.objectTypeId)
        document.title = "Thông tin thiết bị";
    }, [param.objectTypeId]);


    return (
        <>
            <div className="position-block">
                <div className="position-block">
                    <div className="float-left ml-2"><strong className="font-route">{routeType}/ {objectTypeName}</strong></div>
                    <div className="float-right">
                        <button className="button-back" onClick={() => history.goBack()}>
                            <img src="/resources/image/icon-back.svg" height="30px"></img>
                        </button>
                    </div>
                </div>
            </div>
            <div className="div-list-equipment">
                <div className="mt-2" style={{ width: "100%", height: "5px", backgroundColor: "#0a1a5c" }} ></div>
                <div className="position-relative mt-1"><input type="text" className="" name="equipment-name" placeholder={t('content.home_page.search')} onChange={(e) => onFilterDeviceByName(e)} />
                    <i className="fa fa-search position-absolute" style={{ left: "160px", top: "10px" }}></i>
                    <div className="type-status">
                        <div className="status">
                            <button><div className="all" onClick={() => onFilterByStatusDevice(0)}></div></button>
                            <span>TẤT CẢ</span>
                        </div>
                        <div className="status">
                            <button><div className="online" onClick={() => onFilterByStatusDevice(1)}></div></button>
                            <span>BÌNH THƯỜNG</span>
                        </div>
                        <div className="status">
                            <button><div className="warning" onClick={() => onFilterByStatusDevice(2)}></div></button>
                            <span>CẢNH BÁO</span>
                        </div>
                        <div className="status">
                            <button><div className="offline" onClick={() => onFilterByStatusDevice(3)}></div></button>
                            <span>MẤT TÍN HIỆU</span>
                        </div>
                    </div>
                </div>
                <div className="div-list-equipment">
                    <>
                        {devices?.map((device, index) => (
                            <span key={index}>
                                <Link className="button-icon" to={`/${param.customerId}/device-information/${device.deviceId}`} title="Thông tin chi tiết" key={index}>
                                    <div className="item-equipment position-relative">
                                        <div className="header-item-equipment mb-4">
                                            <div className={device.operatingStatus}></div>
                                            <div className="text-center number text-color-ses-2">{device.deviceName}</div>
                                        </div>
                                        {device.systemTypeId == 1 &&
                                            <div className="body-equipment ml-5 d-flex">
                                                <div className="float-left">
                                                    <p>{t('content.project')}</p>
                                                    <p>{t('content.home_page.device.total_active_power')}</p>
                                                    <p>{t('content.home_page.device.total_reactive_power')}</p>
                                                    <p>{t('content.home_page.device.voltage')}</p>
                                                    <p>{t('content.home_page.device.current')}</p>
                                                    <p>{t('content.home_page.device.cosphi')}</p>
                                                </div>
                                                {device.operatingStatus !== "offline" ?
                                                    <div className="float-left ml-2">
                                                        <p className="text-color-ses-1">{device.projectName}</p>
                                                        <p className="text-color-ses-1">{device.ptotal != null ? device.ptotal : "-"} kW</p>
                                                        <p className="text-color-ses-1">{device.qtotal >= 0 ? device.qtotal : "-"} kVAr</p>
                                                        <p className="text-color-ses-1">{device.uab >= 0 ? device.uab : "-"} V</p>
                                                        <p className="text-color-ses-1">{device.ia >= 0 ? device.ia : "-"} A</p>
                                                        <p className="text-color-ses-1">{device.pfa >= 0 ? device.pfa : "-"}</p>
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
                                            </div>
                                        }
                                        {(device.deviceType === 1 && device.systemTypeId === 2) &&
                                            <div className=" d-flex">
                                                <div className="float-left">
                                                    <p>{t('content.project')}</p>
                                                    <p>{t('content.home_page.device.total_active_power')}</p>
                                                    <p>{t('content.home_page.device.total_reactive_power')}</p>
                                                    <p>{t('content.home_page.device.voltage')}</p>
                                                    <p>{t('content.home_page.device.current')}</p>
                                                    <p>{t('content.home_page.device.cosphi')}</p>
                                                </div>
                                                {device.operatingStatus !== "offline" ?
                                                    <div className="float-left ml-2">
                                                        <p className="text-color-ses-1">{device.projectName}</p>
                                                        <p className="text-color-ses-1">{device.ptotal != null ? device.ptotal : "-"} kW</p>
                                                        <p className="text-color-ses-1">{device.qtotal > 0 ? device.qtotal : "-"} kVAr</p>
                                                        <p className="text-color-ses-1">{device.uab > 0 ? device.uab : "-"} V</p>
                                                        <p className="text-color-ses-1">{device.ia > 0 ? device.ia : "-"} A</p>
                                                        <p className="text-color-ses-1">{device.pfa > 0 ? device.pfa : "-"}</p>
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
                                            </div>
                                        }
                                        {(device.deviceType === 3 && device.systemTypeId === 2) &&
                                            <div className="body-equipment ml-5 d-flex">
                                                <div className="float-left">
                                                    <p>{t('content.project')}</p>
                                                    <p>{t('content.home_page.device.total_active_power')}</p>
                                                    <p>{t('content.home_page.device.current')}</p>
                                                    <p>{t('content.home_page.device.voltage')}</p>

                                                </div>
                                                {device.operatingStatus !== "offline" ?
                                                    <div className="float-left ml-2">
                                                        <p className="text-color-ses-1">{device.projectName}</p>
                                                        <p className="text-color-ses-1">{device.pdcCombiner != null ? device.pdcCombiner : "-"} kW</p>
                                                        <p className="text-color-ses-1">{device.idcCombiner > 0 ? device.idcCombiner : "-"} A</p>
                                                        <p className="text-color-ses-1">{device.vdcCombiner > 0 ? device.vdcCombiner : "-"} V</p>
                                                    </div>
                                                    :
                                                    <div className="float-left ml-2">
                                                        <p className="text-color-ses-1">{device.projectName}</p>
                                                        <p className="text-color-ses-1">- kW</p>
                                                        <p className="text-color-ses-1">- A</p>
                                                        <p className="text-color-ses-1">- V</p>
                                                    </div>
                                                }
                                            </div>
                                        }
                                        {(device.deviceType === 4 && device.systemTypeId === 2) &&
                                            <div className="body-equipment ml-5 d-flex">
                                                <div className="float-left">
                                                    <p>{t('content.project')}</p>
                                                    <p>{t('content.home_page.device.total_active_power')}</p>
                                                    <p>{t('content.home_page.device.current')}</p>
                                                    <p>{t('content.home_page.device.voltage')}</p>
                                                    <p>T</p>
                                                </div>
                                                {device.operatingStatus !== "offline" ?
                                                    <div className="float-left ml-2">
                                                        <p className="text-color-ses-1">{device.projectName}</p>
                                                        <p className="text-color-ses-1">{device.pdcStr != null ? device.pdcStr : "-"} kW</p>
                                                        <p className="text-color-ses-1">{device.idcStr > 0 ? device.idcStr : "-"} A</p>
                                                        <p className="text-color-ses-1">{device.vdcStr > 0 ? device.vdcStr : "-"} V</p>
                                                        <p className="text-color-ses-1">{device.t > 0 ? device.t : "-"} °C</p>
                                                    </div>
                                                    :
                                                    <div className="float-left ml-2">
                                                        <p className="text-color-ses-1">{device.projectName}</p>
                                                        <p className="text-color-ses-1">- kW</p>
                                                        <p className="text-color-ses-1">- A</p>
                                                        <p className="text-color-ses-1">- V</p>
                                                        <p className="text-color-ses-1">- °C</p>
                                                    </div>
                                                }
                                            </div>
                                        }
                                        <div className="position-absolute" style={{ top: "70%", left: "80%" }}>
                                            <img src={device.image} height="50px"></img>
                                        </div>
                                    </div>
                                </Link>
                            </span>
                        ))

                        }
                    </>
                </div>
            </div>
        </>
    )


}

export default ObjectOneLevel;