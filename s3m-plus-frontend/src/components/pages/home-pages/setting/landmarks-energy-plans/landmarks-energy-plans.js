import React, { useEffect, useState } from 'react';
import SettingLink from '../setting-link';
import FormLanmarks from './form-lanmarks';
import landmarkAndPlanService from '../../../../../services/landmark-and-plan-service';
import { useParams } from 'react-router-dom/cjs/react-router-dom.min';
import { ToastContainer } from "react-toastify";
import moment from 'moment';
import { NotficationError, NotficationSuscces, NotficationWarning } from '../../notification/notification';
import ProjectService from '../../../../../services/ProjectService';
import { useTranslation } from 'react-i18next';
import DataEmpty from '../../../access-denied/data-empty';
import SelectDevice from '../../select-device-component/select-device-component';
import { Calendar } from 'primereact/calendar';
import DeviceTypeService from '../../../../../services/DeviceTypeService';
import { param } from 'jquery';
import SelectDeviceSetting from '../../setting-warning/select-device/select-device';
import DeviceService from '../../../../../services/DeviceService';
import SettingService from '../../../../../services/SettingService';


const LandmarksEnergyPlans = (props) => {
    const { t } = useTranslation();
    const params = useParams();
    const [systemTypeId, setSystemTypeId] = useState(1);
    const [plan, setPlan] = useState(1);
    const [listPlan, setListPlan] = useState([]);
    const [listLandmark, setListLandmark] = useState([]);
    const [projectId, setProjectId] = useState();
    const [projectIds, setProjectIds] = useState();
    const [render, setRender] = useState(false);
    const [projects, setProjects] = useState([]);
    const [loading, setLoading] = useState(false);
    const callbackFunction = (stt) => {
        if (stt == 200) {
            NotficationSuscces(t('content.update_success'))
        } else {
            NotficationError(t('content.update_fail'))
        }
    }
    const convertArrayId = (ids) => {
        let strId = "";
        if (ids.length <= 0) {
            NotficationWarning(t('content.home_page.report.validate.device_null'));
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
    const [settings, setSettings] = useState([]);

    const [deviceIds, setDeviceIds] = useState("");
    const callbackFunction1 = (childData, arr) => {
        setSelectedDevicesId(childData)
        setSelectedDevices(arr);
    }

    const insertData = async (data, customer, project, system, date, deviceId) => {

        if (plan == 1) {
            let res = await landmarkAndPlanService.insertLandmarks(data, customer, system, project, date, deviceId);
            if (res.status == 200) {
                NotficationSuscces(t('content.add_success'));
                getListData(project);
            } else {
                NotficationWarning(t('content.data_exist'));
            }
        } else {
            let res = await landmarkAndPlanService.insertPlans(data, customer, system, project, date, deviceId);
            if (res.status == 200) {
                NotficationSuscces(t('content.add_success'));
                getListData(project);
            } else {
                NotficationWarning(t('content.data_exist'));
            }
        }
    }
    const changeLandmarkAndPlan = (value) => {
        // setLoading(true)
        setPlan(value);

    }

    const getListData = async (pro) => {
        let list = []
        for (var i = 0; i < selectedDevices.length; i++) {
            var counter = selectedDevices[i];
            list.push(counter.deviceId)
        }
        list.sort();
        let devicesInfor = list.toString()

        if (plan == 1) {
            let res = await landmarkAndPlanService.getLandmarks(params.customerId, pro, props.typeSystem, date, devicesInfor)
            if (res.status == 200) {
                setLoading(false)
                setListLandmark(res.data);
                if (res.data == 0) {
                    NotficationError(t('content.no_data'))
                }

            }

        }
        else {
            let res = await landmarkAndPlanService.getPlans(params.customerId, pro, props.typeSystem, date, devicesInfor)
            if (res.status == 200) {
                setLoading(false)
                setListPlan(res.data);
                if (res.data == 0) {
                    NotficationError(t('content.no_data'))
                }
            }
        }
    }
    const changeProject = async (projectId) => {
        setLoading(false)
        // getListData(projectId);
        setProjectIds(projectId);
        setSelectedDevices([])
    }

    const getListProject = async () => {

        let res = await ProjectService.getProjectByCustomerId(param.customerId);
        if (res.status == 200) {
            if (res.data.length > 0) {
                setProjects(res.data)
                setProjectId(res.data[0].projectId)
                // getListData(res.data[0].projectId)
            }
        }
    }

    const getProject = async (id) => {
        let res = await ProjectService.getProject(id);
        setProjects([res.data]);
        setProjectId(id)

    }
    const getDataByDate = () => {

        if (selectedDevicesId.length <= 0) {
            NotficationWarning(t('content.home_page.report.validate.device_null'));
            setLoading(false);
            return;
        }
        let list = []
        for (var i = 0; i < selectedDevices.length; i++) {
            var counter = selectedDevices[i];
            list.push(counter.deviceId)
        }
        list.sort();
        let devicesInfor = list.toString();
        setDeviceIds(devicesInfor)

        getListData(projectIds == undefined ? projectId : projectIds, moment(date).format("YYYY"));

    }
    useEffect(() => {

        if (param.projectId != undefined) {
            getProject(param.projectId)
            setProjectId(param.projectId)
        } else {
            getListProject();
        }
        setDate(moment(new Date()).format("YYYY"))


    }, [props.typeSystem, params.customerId, params.projectId, projectId])
    const reRender = () => {
        <div>
            <div className="loading" style={{ marginTop: "10%", marginLeft: "40%" }}>
                <img height="60px" src="/resources/image/loading2.gif" alt="loading" />
            </div>
            <div className="text-center loading-chart mt-1">{t('content.home_page.warning_setting.no_data')}</div>
        </div>
        if (plan == 2) {
            return (
                <tbody>
                    {listPlan.length > 0 ? listPlan?.map((e, index) => {
                        return (
                            <FormLanmarks key={index} parentCallback={callbackFunction} data={e} date={e.dateOfWeek} type={2} year={date} />
                        )
                    }) : <tr height="30px">
                        <td colSpan={15} className="text-center">{t('content.no_data')}</td>
                    </tr>}
                </tbody>
            )
        } else {
            return (
                <tbody>
                    {listLandmark.length > 0 ? listLandmark?.map((e, index) => {
                        return (
                            <FormLanmarks key={index} parentCallback={callbackFunction} data={e} date={e.dateOfWeek} type={1} year={date} />
                        )
                    }) : <tr height="30px">
                        <td colSpan={15} className="text-center">{t('content.no_data')}</td>
                    </tr>}
                </tbody>)

        }
    }
    const renderLoading = () => {
        return (
            <tbody>
                <tr>
                    <td>
                        <div style={{ marginLeft: '750px' }}>
                            <div className="loading" style={{ marginTop: "10%", marginLeft: "25%" }}>
                                <img height="60px" src="/resources/image/loading2.gif" alt="loading" />
                            </div>
                        </div>
                    </td>
                </tr>
            </tbody>
        )
    }
    const [selectedDevicesId, setSelectedDevicesId] = useState([]);
    const [selectedDevices, setSelectedDevices] = useState([]);
    const renderSelectedDevices = () => {
        if (selectedDevices.length > 0) {
            return (
                selectedDevices.map((dv, index) => {
                    return (
                        <div id="ic-eyes" key={index} className="mt-2" >
                            <i className="fa-solid fa-circle fa-2xs" style={{ color: "#ff23a4" }}> &nbsp;</i>
                            <span style={{ fontWeight: 'bold', color: "black", fontSize: '16px' }}>{dv.deviceName}</span>
                            <i
                                id="ic-eye"
                                style={{ fontWeight: 'bold', marginLeft: '5%', fontSize: '20px', }}
                                onClick={() => {
                                    setSelectedDevices(selectedDevices.filter((d) => d.deviceId != dv.deviceId))
                                    setSelectedDevicesId(selectedDevicesId.filter((a) => a != dv.deviceId))
                                }
                                }
                                className="fa-solid fa-circle-xmark"></i>
                        </div>
                    )
                })
            )
        }
    }
    const [date, setDate] = useState(moment(new Date()).format("YYYY"));
    const [type, setType] = useState(1);
    const param = useParams();


    return (<>
        <ToastContainer />
        <div className='content-btn-sim' style={{ width: '100%' }}>
            <div style={{ width: '50%' }}>
                <label className="content-btn-radio-sim" >
                    <input type="radio" name="radio-content" value={1} className="content-btn-input-sim" defaultChecked onChange={(event) => changeLandmarkAndPlan(event.target.value)} />
                    <span className="content-btn-text-sim" style={plan != 2 ? { backgroundColor: 'var(--ses-orange-100-color)' } : {}}>{t('content.home_page.setting.landmark_energy')}</span>
                </label>


                <label className="content-btn-radio-sim">
                    <input type="radio" name="radio-content" value={2} className="content-btn-input-sim" onChange={(event) => changeLandmarkAndPlan(event.target.value)} />
                    <span className="content-btn-text-sim" style={plan == 2 ? { backgroundColor: 'var(--ses-orange-100-color)' } : {}}>{t('content.home_page.setting.plan_energy')}</span>
                </label>
            </div>

            <div style={{ width: '43%' }}>
                <div className="float-right mr-1" style={{ position: 'relative', left: '14.5%' }} data-toggle="modal" data-target={"#new-receiver"} onClick={() => insertData({}, params.customerId, projectIds == undefined ? projectId : projectIds, props.typeSystem, date, deviceIds)} ><i className="fas fa-solid fa-circle-plus fa-3x float-right add-user"></i>
                </div>
            </div>

        </div>
        <div className="div-layout-selected-devices">
            <div className="div-frame-selected-devices">
                <div className="zone-layout">
                    <div className="zone">

                        <div className="polygon-setting-warning">
                            <label className="p-1 text-uppercase text-white pl-2">
                                <i className="fa-solid fa-circle-plus mr-1" style={{ color: "#FFF" }}></i>{t('content.home_page.choose_device')}
                            </label>
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
                                <select id={"dv"} className='form-select' style={{ width: '100%' }} onChange={(event) => changeProject(event.target.value)} >
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
                                    {t('content.year')}
                                </p>
                                <Calendar
                                    className="celendar-picker"
                                    id="from-value"
                                    // value={date}
                                    view={"year"}
                                    // maxDate={(new Date())}
                                    dateFormat="yy"
                                    onChange={(e) => setDate(moment(e.value).format("YYYY"))}
                                />
                            </div>
                        </div>

                        <div className="mt-2 mb-2" >
                            <SelectDevice
                                style={{ marginLeft: '2%', width: '200px' }}
                                projectId={projectIds == undefined ? projectId : projectIds}
                                systemTypeId={systemTypeId}
                                parentCallback={callbackFunction1}
                                titleName={t('content.home_page.choose_device')}
                                limit={1}
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
                            </div>
                        </div>
                    </div>
                </div>

            </div>
        </div>
        <div className='mt-2 mb-2 float-right'>
            <button
                type="button"
                className="btn btn-outline-secondary mr-1"
                onClick={() => getDataByDate()}
                style={{
                    marginLeft: '2%',
                    width: '200px',
                    height: '42px',
                    fontSize: "18px",
                }}
            >
                {t('content.view')}
            </button>
        </div>
        <div className='table-container'>
            <table className="table">
                <thead height="30px">
                    <tr>
                        <th>{t('content.day')}</th>
                        <th>T1</th>
                        <th>T2</th>
                        <th>T3</th>
                        <th>T4</th>
                        <th>T5</th>
                        <th>T6</th>
                        <th>T7</th>
                        <th>T8</th>
                        <th>T9</th>
                        <th>T10</th>
                        <th>T11</th>
                        <th>T12</th>
                        <th width="170px">{t('content.update_date')}</th>
                        <th width="60px"><i className="fa-regular fa-hand"></i></th>
                    </tr>
                </thead>
                {loading == false ? reRender() :
                    renderLoading()
                }
            </table>
        </div>
    </>
    );
};

export default LandmarksEnergyPlans;
