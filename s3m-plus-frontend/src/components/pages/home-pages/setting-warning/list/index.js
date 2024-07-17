import React, { useEffect, useState, useRef } from "react";
import { useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import "./index.css"
import DeviceTypeService from "../../../../../services/DeviceTypeService";
import DeviceService from "../../../../../services/DeviceService";
import { NotficationError, NotficationInfo, NotficationSuscces, NotficationWarning } from "../../notification/notification";
import { ToastContainer } from "react-toastify";
import SettingService from "../../../../../services/SettingService";
import moment from "moment";
import CONS from "../../../../../constants/constant";
import ProjectService from "../../../../../services/ProjectService";
import SelectDeviceSetting from "../select-device/select-device";
import FormUpdate from "../edit";
import ReactModal from "react-modal";
//import SelectDevice from "../../select-device-component/select-device-component";

const ListSetting = (props) => {

    const param = useParams();
    const { t } = useTranslation();
    // const [typeSystem, setTypeSystem] = useState(1);
    const [loadding, setLoading] = useState(true);
    const [selectedDevices, setSelectedDevices] = useState([]);
    const [settings, setSettings] = useState([]);
    const [lsSetting, setLsSetting] = useState([]);
    const [projectId, setProjectId] = useState();
    const [projects, setProjects] = useState();
    const [deviceTypes, setDeviceTypes] = useState([]);
    const [deviceTypeId, setDeviceTypeId] = useState();
    const [deviceTypeName, setDeviceTypeName] = useState();
    const [devicesUpdate, setDevicesUpdate] = useState();
    const [openModal, setOpenModal] = useState(false);
    const [deviceIds, setDeviceIds] = useState("");
    const [status, setStatus] = useState(true);

    const callbackFunction = (chillData) => {
        setLoading(true)
        setDevicesUpdate(convertArrayId(chillData));
        setDeviceIds(chillData);
        if (chillData.length > 0) {
            getDeviceByIds(convertArrayId(chillData))
        } else {
            setSelectedDevices(chillData)
        }
        getSettings(param.customerId, projectId, props.typeSystem, deviceTypeId, convertArrayId(chillData));

    }
    const callbackFunctionStt = (status) => {
        if (status == 200) {
            NotficationSuscces("Cập nhật thành công!")
            getSettings(param.customerId, param.projectId, props.typeSystem, deviceTypeId, convertArrayId(deviceIds));
        }
    }
    const convertArrayId = (ids) => {
        let strId = "";
        if (ids.length <= 0) {
            NotficationWarning("Không có điểm đo được chọn");
        } else {
            for (let i = 0; i < ids.length; i++) {
                if (i == 0) {
                    strId += ids[i];
                } else {
                    strId += "," + ids[i];
                }
            }
        }
        return strId;
    }
    const getDeviceByIds = async (ids) => {
        let res = await DeviceService.listDeviceByIds(ids);
        if (res.status == 200) {
            setTimeout(() => {
                setLoading(false)
            }, 300)
            setSelectedDevices(res.data)
        }


    }
    const clearSelectedDevice = () => {
        setSelectedDevices([]);
    }
    const getListDeviceTypeMst = async (projectId) => {
        setProjectId(projectId)
        setLoading(true)
        let res = await DeviceTypeService.listDeviceTypeMst(props.typeSystem, param.customerId, projectId);
        if (res.status == 200 && res.data.length > 0) {
            setDeviceTypes(res.data)
            setDeviceTypeId(res.data[0].id)
            setDeviceTypeName(res.data[0].name)
            // getSettings(param.customerId, param.projectId, typeSystem, res.data[0].id, deviceIds)
        } else {
            setDeviceTypes([])
            setDeviceTypeId("")
            setDeviceTypeName("")
        }
        clearSelectedDevice();
        setLoading(false)
    }
    const selectDeviceType = (event) => {
        setDeviceTypeName(event.target.options[event.target.selectedIndex].text);
        setDeviceTypeId(event.target.value)
        clearSelectedDevice()
    }
    const getSettings = async (customer, project, typeSystem, deviceType, deviceIds) => {
        setLoading(true)
        let res = await SettingService.listByDeviceType(customer, project, typeSystem, deviceType, deviceIds);
        if (res.status == 200 && res.data.length > 0) {
            setSettings(res.data);
        } else {
            setSettings([]);
        }
        setLoading(false)
    }
    const clickDeviceSelected = async (id) => {
        let resp = await SettingService.getSettingByDeviceIds(id);
        if (resp.status == 200) {
            setLsSetting(resp.data);
            setOpenModal(true);
        } else {
            NotficationInfo("Chức năng đang bảo trì!")
        }

    }
    const renderSelectedDevices = () => {
        if (selectedDevices.length > 0) {
            return (
                selectedDevices.map((dv, index) => {
                    return (
                        <div id="ic-eyes" key={index} className="mt-2" >
                            <i className="fa-solid fa-circle fa-2xs" style={{ color: "#ff23a4" }}> &nbsp;</i>
                            <span onClick={() => clickDeviceSelected(dv.deviceId)} style={{ fontWeight: 'bold', color: "black", fontSize: '16px' }}>{dv.deviceName}</span>

                            <i
                                id="ic-eye"
                                style={{ fontWeight: 'bold', marginLeft: '5%', fontSize: '20px', }}
                                onClick={() => closeDevice(dv)}
                                className="fa-solid fa-circle-xmark"></i>
                        </div>
                    )
                })
            )
        }
    }
    const renderListSetting = () => {
        if (loadding == true) {
            return (
                <>
                    <div className="loading" style={{ marginTop: "10%", marginLeft: "700px" }}>
                        <img height="60px" src="/resources/image/loading2.gif" alt="loading" />
                    </div>
                </>
            )
        } else {
            return settings.map((st, index) => {
                return (
                    <FormUpdate
                        key={index}
                        stt={index + 1}
                        data={st}
                        lengthData={selectedDevices.length}
                        devices={devicesUpdate}
                        parentCallback={callbackFunctionStt}
                    />
                )
            })
        }
    }
    const renderListSettingByDevice = () => {
        if (loadding == true) {
            return (
                <tr>
                    <div className="loading" style={{ marginTop: "10%", marginLeft: "700px" }}>
                        <img height="60px" src="/resources/image/loading2.gif" alt="loading" />
                    </div>
                </tr>
            )
        } else {
            return lsSetting.map((st, index) => {
                return (
                    <tr height="25px">
                        <td className="text-center">{index + 1}</td>
                        <td style={{ wordWrap: "break-word" }}>{st.warningTypeName}</td>
                        <td style={{ wordWrap: "break-word" }}>
                            <div className={"level" + st.warningLevel}>
                            </div>
                        </td>
                        <td style={{ wordWrap: "break-word" }}>
                            {st.settingValue}
                        </td>
                        <td style={{ wordWrap: "break-word" }}>{st.description}</td>
                        <td className="text-center">{moment(st.updateDate).format(CONS.DATE_FORMAT)}</td>
                    </tr>
                )
            })
        }
    }
    const customStyles = {
        content: {
            inset: '200px',
            padding: "15px",
            paddingTop: "0px"
        },
    };
    const closeDevice = async (dv) => {
        setSelectedDevices(selectedDevices.filter((d) => d.deviceId != dv.deviceId));
        setDeviceIds(deviceIds.filter((d) => d != dv.deviceId));
        let a = deviceIds.filter((d) => d != dv.deviceId);
        if (a.length > 0) {
            getSettings(param.customerId, projectId, props.typeSystem, deviceTypeId, convertArrayId(a))
        }

    }
    const getListProject = async () => {
        let res = await ProjectService.getProjectByCustomerId(param.customerId);
        if (res.status == 200) {
            if (res.data.length > 0) {
                setProjects(res.data)
                setProjectId(res.data[0].projectId)
                getListDeviceTypeMst(res.data[0].projectId)
            }
        }

    }
    const getProject = async (id) => {
        let res = await ProjectService.getProject(id);
        setProjects([res.data]);
        setProjectId(id)
        getListDeviceTypeMst(id)

    }

    useEffect(() => {
        document.title = "Danh sách cài đặt";
        if (param.projectId != undefined) {
            getProject(param.projectId)
            setProjectId(param.projectId)
        } else {
            getListProject();
        }
    }, [param.customerId, param.projectId, props.typeSystem]);

    return (

        <div>
            <ToastContainer />
            <div className="div-layout-selected-devices">
                <div className="div-frame-selected-devices">
                    <div className="zone-layout">
                        <div className="zone">
                            <div className="polygon-setting-warning">
                                <label className="p-1 text-uppercase text-white pl-2">
                                    <i className="fa-solid fa-circle-plus mr-1" style={{ color: "#FFF" }}></i>{t('content.home_page.choose_device')}</label>
                            </div>
                            <div className="d-flex justify-content-around">
                                <div className='mt-3' style={{ width: '50%' }}>
                                    <p
                                        style={{
                                            fontWeight: 'bold',
                                            fontFamily: 'Arial, Helvetica, sans-serif',
                                            fontSize: '14px',
                                            marginBottom: '1%'
                                        }}

                                    >
                                        {t('content.project')}
                                    </p>
                                    <select id={"dv"} disabled={param.projectId != undefined} className='form-select' style={{ width: '100%' }} onChange={(event) => getListDeviceTypeMst(event.target.value)} >
                                        {projects?.map((pro, index) => {
                                            return <option key={index} value={pro.projectId}>{pro.projectName}</option>
                                        })}
                                    </select>
                                </div>
                                <div className='mt-3' style={{ width: '44%' }}>
                                    <p
                                        style={{
                                            fontWeight: 'bold',
                                            fontFamily: 'Arial, Helvetica, sans-serif',
                                            fontSize: '14px',
                                            marginBottom: '1%'
                                        }}

                                    >
                                        {t('content.device_type')}
                                    </p>
                                    <select id={"dv"} className='form-select' style={{ width: '100%' }} onChange={(event) => selectDeviceType(event)} >
                                        {deviceTypes?.map((pro, index) => {
                                            return <option key={index} value={pro.id}>{pro.name}</option>
                                        })}
                                    </select>
                                </div>
                            </div>
                            <div className="mt-2 mb-2">
                                <SelectDeviceSetting
                                    systemType={props.typeSystem}
                                    project={param.projectId != undefined ? param.projectId : projectId}
                                    style={{ width: '44%', marginLeft: '1.7%' }}
                                    titleName={t('content.home_page.choose_device')}
                                    deviceTypeId={deviceTypeId != null || deviceTypeId != '' ? deviceTypeId : null}
                                    deviceTypeName={deviceTypeName}
                                    parentCallback={callbackFunction}
                                />
                            </div>
                        </div>
                    </div>
                    <div className="zone-layout">
                        <div className="zone">
                            <div className="polygon-setting-warning">
                                <label className="p-1 text-uppercase text-white pl-2">
                                    <i className="fa-solid fa-list mr-1" style={{ color: "#FFF" }}></i>{t('content.list_device')}</label>
                            </div>
                            <div className="div-padding" style={{ overflowY: "scroll", maxHeight: "200px" }} >
                                <div className="grid-container">
                                    {renderSelectedDevices()}

                                    <ReactModal isOpen={openModal}
                                        style={customStyles}
                                    >
                                        <button className="btn-modal-sel" onClick={() => setOpenModal(false)}><i style={{ fontSize: '20px', fontWeight: 'bold' }} className="fa-solid fa-xmark"></i></button>
                                        <div className="mt-2">
                                            <table className="table table-setting-warning">
                                                <thead height="30px">
                                                    <tr>
                                                        <th width="40px">TT</th>
                                                        <th width="300px">{t('content.home_page.warning_setting.setting')}</th>
                                                        <th width="130px">{t('content.home_page.warning_setting.warning_level')}</th>
                                                        <th width="130px">{t('content.home_page.warning_setting.value')}</th>
                                                        <th>{t('content.home_page.warning_setting.description')}</th>
                                                        <th width="150px">{t('content.home_page.warning_setting.update_date')}</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    {renderListSettingByDevice()}
                                                </tbody>
                                            </table>
                                        </div>
                                    </ReactModal>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div className="div-setting-warning">

                {loadding == false ? <div className="frame-table-setting" hidden={selectedDevices.length <= 0}>
                    <div className="zone-layout">
                        <table className="table table-setting-warning">
                            <thead height="30px">
                                <tr >
                                    <th width="40px">TT</th>
                                    <th width="300px">{t('content.home_page.warning_setting.setting')}</th>
                                    <th width="300px">{t('content.home_page.warning_setting.warning_level')}</th>
                                    <th width="130px">{t('content.home_page.warning_setting.value')}</th>
                                    <th>{t('content.home_page.warning_setting.description')}</th>
                                    <th width="150px">{t('content.home_page.warning_setting.update_date')}</th>
                                    <th width="40px"><i className="fa-regular fa-hand"></i></th>
                                </tr>
                            </thead>
                            <tbody>
                                {renderListSetting()}
                            </tbody>
                        </table>
                    </div>
                </div>
                    :
                    <>
                        <div className="loading" style={{ marginTop: "10%", marginLeft: "700px" }}>
                            <img height="60px" src="/resources/image/loading2.gif" alt="loading" />
                        </div>
                    </>
                }
            </div>
        </div>

    )
}

export default ListSetting;