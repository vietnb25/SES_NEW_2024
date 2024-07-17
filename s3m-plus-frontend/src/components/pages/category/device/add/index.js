import React, { useState, useEffect } from "react";
import { Switch, useHistory } from "react-router-dom";
import { useFormik } from 'formik';
import DeviceService from "../../../../../services/DeviceService";
import SystemTypeService from "../../../../../services/SystemTypeService";
import CustomerService from "../../../../../services/CustomerService";
import ManagerService from "../../../../../services/ManagerService";
import AreaService from "../../../../../services/AreaService";
import ProjectService from "../../../../../services/ProjectService";
import DeviceTypeService from "../../../../../services/DeviceTypeService";
import CablesService from "../../../../../services/CablesService";
import ObjectService from "../../../../../services/ObjectService";
import LoadTypeService from "../../../../../services/LoadTypeService";
import $ from "jquery";

import * as Yup from "yup";
import { useTranslation } from 'react-i18next';
import CONS from "../../../../../constants/constant";
import { MultiSelect } from 'primereact/multiselect';
import { Calendar } from 'primereact/calendar';
import moment from 'moment';
import Stepper from 'react-stepper-horizontal';
import MeterMBA from "./meter/mba";
import MeterTDHT from "./meter/tdht";
import MeterAny from "./meter/any";
import Gateway from "./gateway";
import CambienAny from "./cambien";
import MeterTTT from "./meter/ttt";
import MeterInverter from "./meter/inverter";
import StringPV from "./string";
import "./index.css"
import CBTTAny from "./cbtt";
import ObjectTypeService from "../../../../../services/ObjectTypeService";
import Modal from 'react-modal';
import { useParams } from "react-router-dom/cjs/react-router-dom.min";
import fuel from "../fuel_data";
import MaterialService from "../../../../../services/MaterialService";

const passValuesForm = {
    projectId: "",
    customerId: "",
    deviceCode: "",
    deviceType: "",
    systemTypeId: "",
    objectId: "",
    areaId: "",
    managerId: "",
    address: "",
    location: "",
    longitude: "",
    latitude: "",
    deviceName: "",
    fuelTypeId: "",
    fuelFormId: "",
}


const AddDevice = (props) => {
    const param = useParams();
    const { t } = useTranslation();
    const [activeIndex, setActiveIndex] = useState(0);
    const [stepIndex, setStepIndex] = useState(1)
    const [materialTypes, setMaterialTypes] = useState([]);
    const [materialTypesOld, setMaterialTypesOld] = useState([]);
    const items = [
        {
            title: t('content.system'),
        },
        {
            title: t('content.device_type'),
        },
        {
            title: t('content.location'),
        },
        {
            title: t('content.device_info')
        },
        {
            title: t('content.end'),
        }
    ];

    const initialValues = {
        objectName: "",
        objectTypeId: "",
        projectId: "",
        fuelTypeId: "",
        fuelFormId: "",
    }

    const stepBack = () => {
        setActiveIndex(activeIndex - 1);
    }

    const stepNext = () => {
        setActiveIndex(activeIndex + 1);
    }

    const history = useHistory();
    const dataId = {
        customer: props === undefined ? undefined : props.customerId,
        project: props === undefined ? undefined : props.projectId,
    };
    const sendData = (data) => {
        props.parentCallback(data);
    }
    const [systemTypes, setSystemTypes] = useState([]);
    const [deviceTypes, setDeviceTypes] = useState([]);
    const [cutomers, setCustomers] = useState([]);
    const [areas, setAreas] = useState([]);
    const [managers, setManagers] = useState([]);
    const [projects, setProjects] = useState([]);
    const [cables, setCables] = useState([]);

    const [errorsValidate, setErrorsValidate] = useState([]);
    const [error, setError] = useState(null);
    const [changeProjectId, setChangeProjectId] = useState(props.projectId);
    const [customerId, setCustomerId] = useState(props.customerId);
    const [systemType, setSystemType] = useState(1);
    const [weatherDevice, setWeatherDevice] = useState();
    const [weatherDeviceSelect, setWeatherDeviceSelect] = useState();
    const [inverterDevice, setInverterDevice] = useState([]);
    const [inverterDeviceSelect, setInverterDeviceSelect] = useState([]);
    const [objects, setObjects] = useState([]);
    const [loadTypes, setLoadTypes] = useState([]);
    const [project, setProject] = useState([])
    const [project1, setProject1] = useState([])
    const [projectId, setProjectId] = useState([])
    const [projectName, setProjectName] = useState([])
    const [customer, setCustomer] = useState([])
    const [checkForm, setCheckForm] = useState(false);
    const [objectTypeId, setObjectTypeId] = useState(0);
    const [fuelTypeId, setFuelTypeId] = useState(0);
    const [isOpen, setIsOpen] = useState(false);
    const [dataSelect, setDataSelect] = useState([]);
    const [objectId, setObjectId] = useState(0);
    const getInfoAdd = async () => {
        if (dataId != undefined) {
            let responseProject = await ProjectService.getProjectByCustomerId(dataId.customer);
            setProject(responseProject.data)
        }

        let responseCustomer = await CustomerService.getListCustomer()
        setCustomer(responseCustomer.data)

        let resSystemTypes = await SystemTypeService.listSystemType();
        setSystemTypes(resSystemTypes.data);
        let resDeviceTypes = await DeviceTypeService.listDeviceType();
        setDeviceTypes(resDeviceTypes.data);
        let resCustomers = await CustomerService.getListCustomer();
        setCustomers(resCustomers.data);
        let resAreas = await AreaService.listArea();
        setAreas(resAreas.data.areas);
        setManagers(resAreas.data.managers);
        const resProjects = await ProjectService.listProject();
        if (resProjects.status === 200) {
            setProject1(resProjects.data);
        }

        let resCables = await CablesService.listCable();
        setCables(resCables.status === 200 && resCables.data);
        setValueSystemType(CONS.SYSTEM_TYPE.LOAD);
        let resLoadTypes = await LoadTypeService.listLoadType();
        setLoadTypes(resLoadTypes.data)

    }

    const getDeviceTypes = async () => {
        let systemTypeId = $('#system-type-id').val();
        if (systemTypeId != null) {
            let res = await DeviceTypeService.listDeviceTypeBySystemType(systemTypeId);
            $('#device-type-id').html("");
            if (res.data.length > 0) {
                $('#device-type-id').prop('disabled', false);
                let data = res.data;
                $.each(data, function (index, value) {
                    $('#device-type-id').append('<option value="' + value.id + '">' + value.name + '</option>');
                });
            } else {
                $('#device-type-id').prop('disabled', true);
            }
        }
        getObjectType();
    }

    const getObjectType = async () => {
        let projectId = $('#project-id').val();
        let deviceTypeId = $('#device-type-id').val();
        if (projectId != null && deviceTypeId != null) {
            let res = await ObjectService.getListObjectByDeviceTypeId(projectId, deviceTypeId);
            $('#object-type-id').html("");
            if (res.data.length > 0 && res.data[0] != null) {
                $('#object-type-id').prop('disabled', false);
                let data = res.data;
                $.each(data, function (index, value) {
                    $('#object-type-id').append('<option value="' + value.id + '">' + value.name + '</option>');
                });
            } else if (res.data[0] == null) {
                $('#object-type-id').prop('disabled', true);
            } else {
                $('#object-type-id').prop('disabled', true);
            }
        }
        initialValues.projectId = projectId;
    }


    const getManagers = async () => {
        let customerId = $('#customer-id').val();
        if (customerId != null) {
            let res = await ManagerService.getManagerByCustomer(customerId);
            $('#manager-id').html("");
            if (res.data.length > 0) {
                $('#manager-id').prop('disabled', false);
                let data = res.data;
                $.each(data, function (index, value) {
                    $('#manager-id').append('<option value="' + value.managerId + '">' + value.managerName + '</option>');
                });
            } else {
                $('#manager-id').prop('disabled', true);
            }
        }
        getAreas();
    }


    const getAreas = async () => {
        let customerId = $('#customer-id').val();
        let managerId = $('#manager-id').val();
        if (customerId != null) {
            let res = await AreaService.listAreaByCustomerAndManagerId(customerId, managerId);
            $('#area-id').html("");
            if (res.data.length > 0) {
                $('#area-id').prop('disabled', false);
                $.each(res.data, function (index, value) {
                    $('#area-id').append('<option value="' + value.areaId + '">' + value.areaName + '</option>');
                });
            } else {
                $('#area-id').prop('disabled', true);
            }
        }
        // getProjects();
        getListWeatherDevice();
        getListInverterDevice();
    }

    const getProjects = async () => {
        let customerId = $('#customer-id').val();
        if (customerId != null) {
            let res = await ProjectService.getProjectByCustomerId(customerId);
            if (res.data.length > 0) {
                setProjectId(res.data[0].projectId)
            }
            $('#project-id').html("");
            if (res.data.length > 0) {
                $('#project-id').prop('disabled', false);
                let data = res.data;
                $('#project-id').append('<option value="">Chọn dự án</option>')
                $.each(data, function (index, value) {
                    $('#project-id').append('<option value="' + value.projectId + '">' + value.projectName + '</option>')
                });
            } else {
                $('#project-id').prop('disabled', true);
            }
        }
        getListWeatherDevice();
        getListInverterDevice();
    };

    const getListWeatherDevice = async () => {
        let projectId = $('#project-id').val();
        let systemTypeId = $('#system-type-id').val();
        if (projectId != null && systemTypeId == 2) {
            let res = await DeviceService.listDevice(projectId, systemTypeId, 2);
            if (res.status === 200) {
                setWeatherDevice(res.data);
            }
        }
    };

    const getListInverterDevice = async () => {
        let projectId = $('#project-id').val();
        let systemTypeId = $('#system-type-id').val();
        if (projectId != null && systemTypeId == 2) {
            let res = await DeviceService.listDevice(projectId, systemTypeId, 1);
            if (res.status === 200) {
                setInverterDevice(res.data);
            }
        }
    };

    const genDeviceCode = async () => {
        let pad = "0000";
        let customerId = $('#customer-id').val();
        let customerCode = customerId === null || parseInt(customerId) === 0 ? pad : pad.substring(0, pad.length - customerId.length) + customerId;

        let projectId = $('#project-id').val();
        let projectCode = projectId === null || parseInt(projectId) === 0 ? pad : pad.substring(0, pad.length - projectId.length) + projectId;

        let deviceLastest = await DeviceService.getId();
        let deviceId;
        if (deviceLastest.status === 200) {
            deviceId = deviceLastest.data != null ? deviceLastest.data.deviceId + 1 : 1;
            if (deviceId == deviceLastest.data.deviceId) {
                deviceId = deviceId + 1
            }
        } else {
            deviceId = 1
        }
        let deviceid = deviceId === null || parseInt(deviceId) === 0 ? pad : pad.substring(0, pad.length - deviceId.toString().length) + deviceId



        let deviceCode = customerCode + projectCode + deviceid;



        passValuesForm.customerId = $('#customer-id').val();
        passValuesForm.projectId = projectId;
        passValuesForm.systemTypeId = $('#system-type-id').val();
        passValuesForm.deviceCode = deviceCode;
        passValuesForm.managerId = $('#manager-id').val();
        passValuesForm.areaId = $('#area-id').val();
        passValuesForm.address = $('#address').val();
        passValuesForm.location = $('#location').val();
        passValuesForm.longitude = $('#longitude').val();
        passValuesForm.latitude = $('#latitude').val();
        passValuesForm.deviceName = $('#deviceName').val();
        passValuesForm.deviceType = $('#device-type-id').val();

        passValuesForm.objectId = $('#object-type-id').find(":selected").val();
        passValuesForm.fuelTypeId = $('#fuelTypeId').val();
        passValuesForm.fuelFormId = $('#fuelFormId').val();
    }


    /**
     * Hiển thị form theo loại hệ thống
     * 
     * @param {loại hệ thống} systemType 
     */
    const setValueSystemType = (systemType) => {
        getDeviceTypes(systemType);
        setSystemType(systemType);
        let type = parseInt(systemType);
        switch (type) {
            case CONS.SYSTEM_TYPE.LOAD:
                setValueDeviceType(CONS.DEVICE_TYPE_LOAD.METER);
                break;
            case CONS.SYSTEM_TYPE.PV:
                setValueDeviceType(CONS.DEVICE_TYPE_PV.METER);
                break;
            case CONS.SYSTEM_TYPE.GRID:
                setValueDeviceType(CONS.DEVICE_TYPE_GRID.METER);
                break;
            default:
                break;
        }
    }

    const setValueDeviceType = (deviceType) => {
        let typeDv = parseInt(deviceType);
        let systemType = parseInt($('#system-type-id').val());
        let typeObject = parseInt($('#object-type-id').val());

    }
    const funcGetObjectTypeSelect = async () => {
        let res = await ObjectTypeService.getObjectTypeSelect(1);
        if (res.status === 200) {
            setDataSelect(() => res.data);
        }
    }

    const getObjectId = async () => {
        let resObjectId = await ObjectTypeService.getObjectId();
        if (resObjectId.status === 200) {
            setObjectId(resObjectId.data.id);
        }
    }

    const formik = useFormik({
        initialValues,
        onSubmit: async (data) => {
            let datas;
            if (data.objectTypeId == "") {
                datas = {
                    objectName: data.objectName,
                    objectTypeId: dataSelect[0].objectTypeId,
                    projectId: changeProjectId == undefined ? projectId : changeProjectId
                }
            } else {
                datas = {
                    objectName: data.objectName,
                    objectTypeId: data.objectTypeId,
                    projectId: changeProjectId == undefined ? projectId : changeProjectId,
                    fuelTypeId: data.fuelTypeId,
                    fuelFormId: data.fuelFormId,
                }
            }
            let res = await ObjectTypeService.addObjectType(datas);
            if (res.status === 400) {
                setErrorsValidate(res.data.errors);
                return
            }
            if (res.status === 200) {
                let objectIdAdded = objectId + 1;
                $.alert({
                    title: t('content.title_notify'),
                    content: t('content.category.object_type.list.add_success')
                });
                setIsOpen(false);
                setErrorsValidate([]);
                $('#object-type-id').append('<option value="' + objectIdAdded + '" selected>' + data.objectName + '</option>');
            } else {
                setError(t('validate.cable.INSERT_FAILED'))
            }

        }
    });

    const showStep3 = () => {
        setActiveIndex(2);
    }

    const getObjectTypeIdById = async (id) => {
        let res = await ObjectTypeService.getObjectTypeIdById(id);
        if (res.status === 200) {
            setObjectTypeId(res.data.objectTypeId);
        }
    }

    const callbackFunction = (isCloseModal) => {
        let customerId = $('#customer-id').val();
        let projectId = $('#project-id').val();
        props.parentCloseModal(isCloseModal, customerId, projectId)
    }


    const changeFormByCondition = (data, isActive) => {
        let systemType = parseInt($('#system-type-id').val());
        let deviceType = parseInt($('#device-type-id').val());
        let typeObject = parseInt($('#object-type-id').val());
        // getObjectTypeIdById(typeObject);
        if (systemType == 1 || systemType == 3 || systemType == 4 || systemType == 6) {
            if (deviceType == 1) {
                if (objectTypeId == 1) {
                    // return props.isActive != undefined ? <MeterMBA data={data}></MeterMBA> : <></>
                    return (
                        <>
                            <MeterMBA parentCallback={callbackFunction} data={data} isActive={isActive}></MeterMBA>
                            {
                                activeIndex == 4 ?
                                    <div id="main-button">

                                        <button type="button" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} onClick={() => {
                                            stepBack();
                                        }}>
                                            <i className="fa-solid fa-caret-left"></i>
                                        </button>
                                        <button type="submit" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} form="form1">
                                            <i className="fa-solid fa-floppy-disk"></i>
                                        </button>
                                    </div> : <></>
                            }

                        </>

                    )
                } else if (objectTypeId == 2) {
                    return (
                        <>
                            <MeterTDHT parentCallback={callbackFunction} data={data} isActive={isActive}></MeterTDHT>
                            <div id="main-button" className={`${(activeIndex != 4) && "hidden"}`}>

                                <button type="button" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} onClick={() => {
                                    stepBack();
                                }}>
                                    <i className="fa-solid fa-caret-left"></i>
                                </button>
                                <button type="submit" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} form="form2">
                                    <i className="fa-solid fa-floppy-disk"></i>
                                </button>
                            </div>
                        </>
                    )

                } else {
                    return (
                        <>
                            <MeterAny parentCallback={callbackFunction} data={data} isActive={isActive}></MeterAny>
                            <div id="main-button" className={`${(activeIndex != 4) && "hidden"}`}>

                                <button type="button" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} onClick={() => {
                                    stepBack();
                                }}>
                                    <i className="fa-solid fa-caret-left"></i>
                                </button>
                                <button type="submit" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} form="form5">
                                    <i className="fa-solid fa-floppy-disk"></i>
                                </button>
                            </div>
                        </>
                    )

                }
            } else if (deviceType == 4) {
                return (
                    <>
                        <CBTTAny parentCallback={callbackFunction} data={data} isActive={isActive}></CBTTAny>
                        <div id="main-button" className={`${(activeIndex != 4) && "hidden"}`}>

                            <button type="button" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} onClick={() => {
                                stepBack();
                            }}>
                                <i className="fa-solid fa-caret-left"></i>
                            </button>
                            <button type="submit" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} form="form7">
                                <i className="fa-solid fa-floppy-disk"></i>
                            </button>
                        </div>
                    </>
                )

            }
            else if (deviceType == 9) {
                return (
                    <>
                        <Gateway parentCallback={callbackFunction} data={data} isActive={isActive}></Gateway>
                        <div id="main-button" className={`${(activeIndex != 4) && "hidden"}`}>

                            <button type="button" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} onClick={() => {
                                stepBack();
                            }}>
                                <i className="fa-solid fa-caret-left"></i>
                            </button>
                            <button type="submit" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} form="form6">
                                <i className="fa-solid fa-floppy-disk"></i>
                            </button>
                        </div>
                    </>
                )

            } else {
                return (
                    <>
                        <CambienAny parentCallback={callbackFunction} data={data} isActive={isActive}></CambienAny>
                        <div id="main-button" className={`${(activeIndex != 4) && "hidden"}`}>

                            <button type="button" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} onClick={() => {
                                stepBack();
                            }}>
                                <i className="fa-solid fa-caret-left"></i>
                            </button>
                            <button type="submit" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} form="form8">
                                <i className="fa-solid fa-floppy-disk"></i>
                            </button>
                        </div>
                    </>
                )

            }
        } else if (systemType == 2) {
            if (deviceType == 1) {
                if (objectTypeId == 1) {
                    return (
                        <>
                            <MeterMBA parentCallback={callbackFunction} data={data} isActive={isActive}></MeterMBA>
                            {
                                activeIndex == 4 ?
                                    <div id="main-button">

                                        <button type="button" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} onClick={() => {
                                            stepBack();
                                        }}>
                                            <i className="fa-solid fa-caret-left"></i>
                                        </button>
                                        <button type="submit" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} form="form1">
                                            <i className="fa-solid fa-floppy-disk"></i>
                                        </button>
                                    </div> : <></>
                            }

                        </>

                    )
                } else if (objectTypeId == 2) {
                    return (
                        <>
                            <MeterTDHT parentCallback={callbackFunction} data={data} isActive={isActive}></MeterTDHT>
                            <div id="main-button" className={`${(activeIndex != 4) && "hidden"}`}>

                                <button type="button" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} onClick={() => {
                                    stepBack();
                                }}>
                                    <i className="fa-solid fa-caret-left"></i>
                                </button>
                                <button type="submit" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} form="form2">
                                    <i className="fa-solid fa-floppy-disk"></i>
                                </button>
                            </div>
                        </>
                    )
                } else if (objectTypeId == 21) {
                    return (
                        <>
                            <MeterInverter parentCallback={callbackFunction} data={data} isActive={isActive}></MeterInverter>
                            {
                                activeIndex == 4 ?
                                    <div id="main-button" >

                                        <button type="button" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} onClick={() => {
                                            stepBack();
                                        }}>
                                            <i className="fa-solid fa-caret-left"></i>
                                        </button>
                                        <button type="submit" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} form="form3">
                                            <i className="fa-solid fa-floppy-disk"></i>
                                        </button>
                                    </div> : <></>
                            }

                        </>
                    )

                } else {
                    return (
                        <>
                            <MeterAny parentCallback={callbackFunction} data={data} isActive={isActive}></MeterAny>
                            <div id="main-button" className={`${(activeIndex != 4) && "hidden"}`}>

                                <button type="button" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} onClick={() => {
                                    stepBack();
                                }}>
                                    <i className="fa-solid fa-caret-left"></i>
                                </button>
                                <button type="submit" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} form="form5">
                                    <i className="fa-solid fa-floppy-disk"></i>
                                </button>
                            </div>
                        </>
                    )

                }
            } else if (deviceType == 2) {
                return (
                    <>
                        <MeterInverter parentCallback={callbackFunction} data={data} isActive={isActive}></MeterInverter>
                        {
                            activeIndex == 4 ?
                                <div id="main-button" >

                                    <button type="button" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} onClick={() => {
                                        stepBack();
                                    }}>
                                        <i className="fa-solid fa-caret-left"></i>
                                    </button>
                                    <button type="submit" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} form="form3">
                                        <i className="fa-solid fa-floppy-disk"></i>
                                    </button>
                                </div> : <></>
                        }

                    </>
                )
            } else if (deviceType == 4) {
                return (
                    <>
                        <CBTTAny parentCallback={callbackFunction} data={data} isActive={isActive}></CBTTAny>
                        <div id="main-button" className={`${(activeIndex != 4) && "hidden"}`}>

                            <button type="button" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} onClick={() => {
                                stepBack();
                            }}>
                                <i className="fa-solid fa-caret-left"></i>
                            </button>
                            <button type="submit" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} form="form7">
                                <i className="fa-solid fa-floppy-disk"></i>
                            </button>
                        </div>
                    </>
                )
            } else if (deviceType == 9) {
                return (
                    <>
                        <Gateway parentCallback={callbackFunction} data={data} isActive={isActive}></Gateway>
                        <div id="main-button" className={`${(activeIndex != 4) && "hidden"}`}>

                            <button type="button" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} onClick={() => {
                                stepBack();
                            }}>
                                <i className="fa-solid fa-caret-left"></i>
                            </button>
                            <button type="submit" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} form="form6">
                                <i className="fa-solid fa-floppy-disk"></i>
                            </button>
                        </div>
                    </>
                )
            } else if (deviceType == 16) {
                return (
                    <>
                        <StringPV parentCallback={callbackFunction} data={data} isActive={isActive}></StringPV>
                        {
                            activeIndex == 4 ?
                                <div id="main-button" className={`${(activeIndex != 4) && "hidden"}`}>

                                    <button type="button" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} onClick={() => {
                                        stepBack();
                                    }}>
                                        <i className="fa-solid fa-caret-left"></i>
                                    </button>
                                    <button type="submit" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} form="form9">
                                        <i className="fa-solid fa-floppy-disk"></i>
                                    </button>
                                </div> : <></>
                        }

                    </>
                )

            } else {
                return (
                    <>
                        <CambienAny parentCallback={callbackFunction} data={data} isActive={isActive}></CambienAny>
                        <div id="main-button" className={`${(activeIndex != 4) && "hidden"}`}>

                            <button type="button" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} onClick={() => {
                                stepBack();
                            }}>
                                <i className="fa-solid fa-caret-left"></i>
                            </button>
                            <button type="submit" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} form="form8">
                                <i className="fa-solid fa-floppy-disk"></i>
                            </button>
                        </div>
                    </>
                )
            }
        } else if (systemType == 5) {
            if (deviceType == 1) {
                if (objectTypeId == 1) {
                    // return props.isActive != undefined ? <MeterMBA data={data}></MeterMBA> : <></>
                    return (
                        <>
                            <MeterMBA parentCallback={callbackFunction} data={data} isActive={isActive}></MeterMBA>
                            {
                                activeIndex == 4 ?
                                    <div id="main-button">

                                        <button type="button" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} onClick={() => {
                                            stepBack();
                                        }}>
                                            <i className="fa-solid fa-caret-left"></i>
                                        </button>
                                        <button type="submit" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} form="form1">
                                            <i className="fa-solid fa-floppy-disk"></i>
                                        </button>
                                    </div> : <></>
                            }

                        </>

                    )
                } else if (objectTypeId == 2) {
                    return (
                        <>
                            <MeterTDHT parentCallback={callbackFunction} data={data} isActive={isActive}></MeterTDHT>
                            <div id="main-button" className={`${(activeIndex != 4) && "hidden"}`}>

                                <button type="button" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} onClick={() => {
                                    stepBack();
                                }}>
                                    <i className="fa-solid fa-caret-left"></i>
                                </button>
                                <button type="submit" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} form="form2">
                                    <i className="fa-solid fa-floppy-disk"></i>
                                </button>
                            </div>
                        </>
                    )

                } else if (objectTypeId == 20) {
                    return (
                        <>
                            <MeterTTT parentCallback={callbackFunction} data={data} isActive={isActive}></MeterTTT>
                            <div id="main-button" className={`${(activeIndex != 4) && "hidden"}`}>

                                <button type="button" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} onClick={() => {
                                    stepBack();
                                }}>
                                    <i className="fa-solid fa-caret-left"></i>
                                </button>
                                <button type="submit" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} form="form4">
                                    <i className="fa-solid fa-floppy-disk"></i>
                                </button>
                            </div>
                        </>
                    )

                } else {
                    return (
                        <>
                            <MeterAny parentCallback={callbackFunction} data={data} isActive={isActive}></MeterAny>
                            <div id="main-button" className={`${(activeIndex != 4) && "hidden"}`}>

                                <button type="button" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} onClick={() => {
                                    stepBack();
                                }}>
                                    <i className="fa-solid fa-caret-left"></i>
                                </button>
                                <button type="submit" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} form="form5">
                                    <i className="fa-solid fa-floppy-disk"></i>
                                </button>
                            </div>
                        </>
                    )
                }
            } else if (deviceType == 4) {
                return (
                    <>
                        <CBTTAny parentCallback={callbackFunction} data={data} isActive={isActive}></CBTTAny>
                        <div id="main-button" className={`${(activeIndex != 4) && "hidden"}`}>

                            <button type="button" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} onClick={() => {
                                stepBack();
                            }}>
                                <i className="fa-solid fa-caret-left"></i>
                            </button>
                            <button type="submit" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} form="form7">
                                <i className="fa-solid fa-floppy-disk"></i>
                            </button>
                        </div>
                    </>
                )

            }
            else if (deviceType == 9) {
                return (
                    <>
                        <Gateway parentCallback={callbackFunction} data={data} isActive={isActive}></Gateway>
                        <div id="main-button" className={`${(activeIndex != 4) && "hidden"}`}>

                            <button type="button" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} onClick={() => {
                                stepBack();
                            }}>
                                <i className="fa-solid fa-caret-left"></i>
                            </button>
                            <button type="submit" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} form="form6">
                                <i className="fa-solid fa-floppy-disk"></i>
                            </button>
                        </div>
                    </>
                )

            } else {
                return (
                    <>
                        <CambienAny parentCallback={callbackFunction} data={data} isActive={isActive}></CambienAny>
                        <div id="main-button" className={`${(activeIndex != 4) && "hidden"}`}>

                            <button type="button" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} onClick={() => {
                                stepBack();
                            }}>
                                <i className="fa-solid fa-caret-left"></i>
                            </button>
                            <button type="submit" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} form="form8">
                                <i className="fa-solid fa-floppy-disk"></i>
                            </button>
                        </div>
                    </>
                )
            }
        }

    }

    const changeProject = (e) => {
        setChangeProjectId(parseInt(e.target.value));
        getListWeatherDevice();
        getListInverterDevice();

    }

    const customModalStyles = {
        overlay: {
            backgroundColor: 'rgba(0, 0, 0, 0.6)'
        },
        content: {
            width: '550px',
            overflow: 'hidden',
            top: '50%',
            left: '50%',
            right: 'auto',
            bottom: 'auto',
            marginRight: '-50%',
            transform: 'translate(-50%, -50%)'
        }
    }

    const closeModal = () => {
        let customerId = $('#customer-id').val();
        let projectId = $('#project-id').val();
        props.parentCloseModal(false, customerId, projectId)
    }
    const [deviceTypeId, setDeviceTypeId] = useState("");
    const handleDeviceTypeChange = (event) => {
        const selectedDeviceTypeId = event.target.value;
        setDeviceTypeId(selectedDeviceTypeId);
        getObjectType();
    };

    const handleChangeFuelType = (event) => {
        const selectedFuelType = event.target.value;
        setFuelTypeId(selectedFuelType);
    };
    const handleChangeMaterialForm = (value) => {
        let arr = materialTypesOld.filter(item => item.materialForm == value);
        if (arr.length > 0) {
            setMaterialTypes(arr);
        } else {
            setMaterialTypes(materialTypesOld);
        }


    };
    useEffect(() => {
        getInfoAdd();
        Modal.setAppElement('#modals');
        getListMaterialType();
        document.title = t('content.category.device.add.title');

    }, []);
    const getListMaterialType = async () => {
        let res = await MaterialService.getListMaterialType();
        if (res.status == 200) {
            setMaterialTypes(res.data.filter(item => item.materialForm == 1))
            setMaterialTypesOld(res.data)
        }
    }
    return (
        <>
            <div id="page-body">
                <div id="main-title">
                    <h5 className="d-block mb-0 float-left text-uppercase"><i className="fas fa-server"></i>&nbsp; {t('content.category.device.add.title')}</h5>
                </div>
                {
                    (error != null) &&
                    <div className="alert alert-danger" role="alert">
                        <p className="m-0 p-0">{t('content.category.device.add.error_add')}</p>
                    </div>
                }
                {
                    (errorsValidate.length > 0) &&
                    <div className="alert alert-warning" role="alert">
                        {
                            errorsValidate.map((error, index) => {
                                return (<p key={index + 1} className="m-0 p-0">{error}</p>)
                            })
                        }
                    </div>
                }

                <div id="main-content w-full flex flex-col items-center justify-center">
                    <div id="stepper">
                        <Stepper
                            onStepClick={setActiveIndex}
                            activeColor="#F36F21"
                            completeColor="#0a1a5c"
                            completeTitle="bold"
                            completeTitleColor="#000"
                            steps={items.map((step, index) => ({
                                ...step,
                                onClick: (event) => {
                                    step.onClick?.(event);
                                    setActiveIndex(index);
                                }
                            }))}
                            activeStep={activeIndex}
                        />
                    </div>


                    <div className={`relative flex flex-col items-center justify-center mt-5 p-2
                        bg-white rounded-md text-sm ${activeIndex != 0 && "hidden"} `}>
                        <table className={`table table-input `}>
                            <tbody>
                                <tr>
                                    <th width="180px">{t('content.customer')} </th>
                                    <td>
                                        <select className="custom-select block customer-id" id="customer-id" onChange={() => { getManagers(); getProjects(); }}>
                                            <option value=""></option>
                                            {customer?.length > 0 && customer?.map((c, index) => {
                                                return <option key={index + 1} value={c.customerId}>{c.customerName}</option>
                                            })}
                                        </select>
                                    </td>
                                </tr>

                                <tr>
                                    <th width="180px">{t('content.project')} </th>
                                    <td>
                                        <select className="custom-select block project-id" id="project-id" onChange={(e) => { changeProject(e) }}>
                                            <option value=""></option>
                                            {projects?.length > 0 && projects?.map((p, index) => {
                                                return <option key={index + 1} value={p.projectId}>{p.projectName}</option>
                                            })}
                                        </select>
                                    </td>
                                </tr>

                                <tr>
                                    <th width="230px">{t('content.system_type')} </th>
                                    <td>
                                        <select className="custom-select block system-type-id" id="system-type-id" onChange={e => {
                                            setValueSystemType(e.target.value);
                                        }}>
                                            {
                                                systemTypes?.length > 0 && systemTypes?.map((systemType, index) => {
                                                    return <option key={index + 1} value={systemType.systemTypeId}>{systemType.systemTypeName}</option>
                                                })
                                            }
                                        </select>
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <div id="main-button" >
                            <button type="button" className="btn btn-outline-secondary btn-cancel mr-1 mb-3" onClick={() => {
                                // history.push({
                                //     pathname: `/category/device`,
                                //     state: {
                                //         status: -1,
                                //         message: "NO CREATE"
                                //     }
                                // });
                                closeModal();
                            }}>
                                <i className="fa-solid fa-xmark"></i>
                            </button>
                            <button type="button" className="btn btn-outline-secondary btn-agree mb-3" onClick={() => {
                                getObjectType(); setActiveIndex(1);
                            }}>
                                <i className="fa-solid fa-caret-right"></i>
                            </button>
                        </div>
                    </div>

                    <div id="formValue" className={`relative flex flex-col items-center justify-center mt-5 p-2 
                        bg-white rounded-md text-sm ${activeIndex != 1 && "hidden"} `}>
                        <table className={`table table-input `}>
                            <tbody>
                                <tr className="device-name" >
                                    <th width="225px">{t('content.category.device.lable_device_name')}<span className="required">※</span></th>
                                    <td>
                                        <input type="text" className="form-control" name="deviceName" id="deviceName" onChange={formik.handleChange} />
                                    </td>
                                </tr>
                                <tr>
                                    <th>{t('content.category.device.lable_device_type')}</th>
                                    <td>
                                        <select className="custom-select block device-type-id" id="device-type-id" onChange={handleDeviceTypeChange} >
                                            <option value={""}>Chọn thiết bị đo lường</option>
                                            {
                                                deviceTypes?.length > 0 && deviceTypes?.map((dt, index) => {
                                                    return <option key={index + 1} value={dt.id}>{dt.name}</option>
                                                })
                                            }
                                        </select>
                                    </td>
                                </tr>
                                <tr className="object-type">

                                    <th>{t('content.object')}
                                        <button type="button" className="btn btn-primary btn-rounded btn-cancel ml-2" onClick={() => { setIsOpen(true); getObjectId(); funcGetObjectTypeSelect(); }}>
                                            <i className="fa-solid fa-plus"></i>
                                        </button>
                                    </th>
                                    <td>
                                        <select className="custom-select block object-type-id" id="object-type-id" name="objectTypeId">
                                            <option value={""}>{t('content.choose_object')}</option>
                                            {
                                                objects?.length > 0 && objects?.map((obj, index) => {
                                                    return <option key={index + 1} value={obj.id}>{obj.name}</option>
                                                })
                                            }
                                        </select>
                                    </td>
                                </tr>

                                {deviceTypeId == 10 && (
                                    <>
                                        <tr>
                                            <th>Dạng nhiên liệu</th>
                                            <td>
                                                <select className="custom-select block " id="fuelFormId" onChange={(e) => handleChangeMaterialForm(e.target.value)} name="fuelFormId">
                                                    <option value={1}>Rắn</option>
                                                    <option value={2}>Lỏng</option>
                                                    <option value={3}>Khí</option>
                                                </select>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th>Loại nhiên liệu</th>
                                            <td>
                                                <select className="custom-select block fuelTypeId" id="fuelTypeId" onChange={handleChangeFuelType} name="fuelTypeId">
                                                    {/* <option value={""}>Chọn loại nhiên liệu</option> */}
                                                    {materialTypes.map(item => (
                                                        <option key={item.id} value={item.id}>{item.materialName}</option>
                                                    ))}
                                                </select>
                                            </td>
                                        </tr>
                                    </>
                                )}


                            </tbody>
                        </table>
                        <div id="main-button">

                            <button type="button" className="btn btn-outline-secondary btn-agree mr-1 mb-3" onClick={() => {
                                stepBack();
                            }}>
                                <i className="fa-solid fa-caret-left"></i>
                            </button>
                            <button type="button" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} disabled={!(formik.dirty && formik.isValid)} onClick={() => {
                                stepNext();
                            }}>
                                <i className="fa-solid fa-caret-right"></i>
                            </button>
                        </div>

                    </div>

                    <div className={`relative flex flex-col items-center justify-center mt-5 p-2 
                        bg-white rounded-md text-sm ${activeIndex != 2 && "hidden"} `}>
                        <table className={`table table-input `}>
                            <tbody>
                                <tr>
                                    <th width="180px">{t('content.country')}</th>
                                    <td>
                                        <select className="custom-select block country-id" id="country-id">
                                            <option value="84">Việt Nam</option>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <th width="180px">{t('content.manager')}</th>
                                    <td>
                                        <select className="custom-select block manager-id" id="manager-id" onChange={() => { getAreas() }}>
                                            {
                                                managers?.length > 0 && managers?.map((manager, index) => {
                                                    return <option key={index + 1} value={manager.managerId}>{manager.managerName}</option>
                                                })
                                            }
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <th width="180px">{t('content.area')}</th>
                                    <td>
                                        <select className="custom-select block area-id" id="area-id">
                                            {areas?.length > 0 && areas?.map((area, index) => {
                                                return <option key={index + 1} value={area.areaId}>{area.areaName}</option>
                                            })}
                                        </select>
                                    </td>
                                </tr>
                                <tr className="address">
                                    <th>{t('content.address')}</th>
                                    <td>
                                        <input type="text" className="form-control" name="address" id="address" />
                                    </td>
                                </tr>
                                <tr>
                                    <th width="300px">{t('content.super_manager')}</th>
                                    <td>
                                        <input type="text" className="form-control area" name="area" id="location" />
                                    </td>
                                </tr>
                                <tr className="longitude">
                                    <th>{t('content.longitude')}</th>
                                    <td>
                                        <input type="number" step="0.0000000001" className="form-control input-number-m" name="longitude" id="longitude" />
                                    </td>
                                </tr>
                                <tr className="latitude">
                                    <th>{t('content.latitude')}</th>
                                    <td>
                                        <input type="number" step="0.0000000001" className="form-control input-number-m" name="latitude" id="latitude" />
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <div id="main-button">

                            <button type="button" className="btn btn-outline-secondary btn-agree mr-1 mb-3" onClick={() => {
                                stepBack();
                            }}>
                                <i className="fa-solid fa-caret-left"></i>
                            </button>
                            <button type="button" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} onClick={() => {
                                genDeviceCode(); stepNext();
                            }}>
                                <i className="fa-solid fa-caret-right"></i>
                            </button>
                        </div>

                    </div>


                    {changeFormByCondition(passValuesForm, activeIndex)}

                    {
                        activeIndex == 3 ?
                            <div id="main-button">

                                <button type="button" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} onClick={() => {
                                    stepBack();
                                }}>
                                    <i className="fa-solid fa-caret-left"></i>
                                </button>
                                <button type="button" className={`btn btn-outline-secondary btn-agree mr-1 mb-3 `} onClick={() => {
                                    stepNext();
                                }}>
                                    <i className="fa-solid fa-caret-right"></i>
                                </button>


                            </div> : <></>
                    }

                </div>

            </div>

            <div id="modals" />
            <Modal
                isOpen={isOpen}
                ariaHideApp={false}
                onRequestClose={() => setIsOpen(false)}
                style={customModalStyles}
            >
                <div id="main-title">
                    <h5 className="d-block mb-4 float-left"><i className="far fa-clone"></i> &nbsp;{t('content.category.object_type.add.title')}</h5>
                </div>

                <div hidden={error == null || errorsValidate == null} className="mt-2 mb-2 bg-red-100 border border-red-400 text-red-700 px-4 py-2 rounded relative text-sm" >
                    {
                        (error != null) &&
                        <div className="alert alert-danger" role="alert">
                            <p className="m-0 p-0">{t('content.category.device.add.error_add')}</p>
                        </div>
                    }
                    {
                        (errorsValidate.length > 0) &&
                        <div className="alert alert-warning" role="alert">
                            {
                                errorsValidate.map((error, index) => {
                                    return (<p key={index + 1} className="m-0 p-0">{error}</p>)
                                })
                            }
                        </div>
                    }
                </div>

                <form onSubmit={formik.handleSubmit}>

                    <table className="table table-input ">
                        <tbody>
                            <tr>
                                <th width="210px">{t('content.category.object_type.lable_object_name')} <span className="required">※</span></th>
                                <td>
                                    <input type="text" onChange={formik.handleChange} name="objectName" className="form-control" />
                                </td>
                            </tr>
                            <tr>
                                <th width="210px">{t('content.category.object_type.lable_object_type_name')} <span className="required">※</span></th>
                                <td>

                                    <select className="form-select" onChange={formik.handleChange} name="objectTypeId" required>
                                        {dataSelect.map((m, index) => {
                                            return <option key={index} value={m.objectTypeId}> {m.objectTypeName}</option>
                                        })}
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <th width="220px">{t('content.project')} <span className="required">※</span></th>
                                <td>
                                    <select

                                        defaultValue={changeProjectId == 0 ? projectId : changeProjectId}
                                        className="form-select"
                                        name="projectId"
                                        onChange={formik.handleChange}
                                        required
                                        style={{ overflow: "hidden", textOverflow: "ellipsis", maxWidth: "100%", pointerEvents: "none" }}
                                    >
                                        {project1.map((p, index) => (
                                            <option
                                                key={index}
                                                style={{ overflow: "hidden", textOverflow: "ellipsis", width: "100%" }}
                                                value={p.projectId}
                                                title={p.projectName.length > 35 ? p.projectName : null}
                                            >
                                                {p.projectName.length > 35 ? `${p.projectName.slice(0, 35)}...` : p.projectName}
                                            </option>
                                        ))}
                                    </select>
                                </td>
                            </tr>


                        </tbody>
                    </table>

                    <div id="mt-3 justify-between">
                        <button type="button" className="btn btn-outline-secondary btn-cancel" onClick={() => {
                            setIsOpen(false);
                        }}>
                            <i className="fa-solid fa-xmark"></i>
                        </button>
                        <button type="submit" id="submitButton" className="btn btn-outline-secondary btn-agree mr-1 float-right" >
                            <i className="fa-solid fa-check"></i>
                        </button>
                    </div>
                </form>
            </Modal>
        </>
    )
}


export default AddDevice;