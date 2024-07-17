import React, { useState, useEffect } from "react";
import { useFormik } from "formik";
import DeviceService from "../../../../../../services/DeviceService";
import { Calendar } from "primereact/calendar";
import moment from "moment";
import { Switch, useHistory } from "react-router-dom";
import * as Yup from "yup";
import { useTranslation } from "react-i18next";
import CustomerService from "../../../../../../services/CustomerService";
import SystemTypeService from "../../../../../../services/SystemTypeService";
import DeviceTypeService from "../../../../../../services/DeviceTypeService";
import AreaService from "../../../../../../services/AreaService";
import ProjectService from "../../../../../../services/ProjectService";
import CONS from "../../../../../../constants/constant";
import ObjectService from "../../../../../../services/ObjectService";
import $ from "jquery";
import LoadTypeService from "../../../../../../services/LoadTypeService";

const MeterAny = (props) => {

    const initialValues = {
        deviceCode: props.data.deviceCode,
        deviceName: props.data.deviceName,
        deviceTypeId: props.data.deviceType,
        systemTypeId: props.data.systemTypeId,
        address: props.data.address,
        latitude: props.data.latitude,
        longitude: props.data.longitude,
        projectId: props.data.projectId,
        customerId: props.data.customerId,
        managerId: props.data.managerId,
        areaId: props.data.areaId,
        objectId: props.data.objectId,
        location: props.data.location,
        model: '',
        manufacturer: '',
        load_type_id: '',
        vn: 400,
        in: '',
        cable_length: 500,
        rho: 0.03,
        reference_device_id: null,
        description: "",
        work_date: moment(new Date()).format("YYYY-MM-DD"),
        pdm: 0,
        priority_flag: 1,
    }

    const [systemTypes, setSystemTypes] = useState([]);
    const [deviceTypes, setDeviceTypes] = useState([]);
    const [cutomers, setCustomers] = useState([]);
    const [deviceGateway, setDeviceGateway] = useState([]);
    const [areas, setAreas] = useState([]);
    const [managers, setManagers] = useState([]);
    const [projects, setProjects] = useState([]);
    const { t } = useTranslation();
    const [errorsValidate, setErrorsValidate] = useState([]);
    const [activeIndex, setActiveIndex] = useState(props.isActive);
    const [error, setError] = useState(null);
    const [customer, setCustomer] = useState([]);
    const [objects, setObjects] = useState([]);
    const [loadTypes, setLoadTypes] = useState([]);

    const history = useHistory();

    const getInfoAdd = async () => {
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
        let resProjects = await ProjectService.listProject();
        setProjects(resProjects.data);
        let resObject = await ObjectService.getListObjectMst();
        setObjects(resObject.data);
        let resLoadTypes = await LoadTypeService.listLoadTypeMst();
        setLoadTypes(resLoadTypes.data);
        let resDevice = await DeviceService.getDeviceGateway();
        setDeviceGateway(resDevice.data);
    }

    const closeModal = () => {
        props.parentCallback(false)
    }

    const addDevice = () => {
        $.confirm({
            type: 'green',
            typeAnimated: true,
            icon: 'fa fa-success',
            title: t('content.notification'),
            content: t('content.category.device.list.add_success'),
            buttons: {
                confirm: {
                    text: "<i class=\"fa-solid fa-arrow-rotate-left\"></i>" + t('content.category.device.list.title'),
                    action: async function () {
                        history.push({
                            pathname: "/category/device",
                            state: {
                                status: 200,
                                message: "INSERT_SUCCESS"
                            }
                        });
                        closeModal();
                    }
                }
            }
        });
    }

    const formik = useFormik({
        enableReinitialize: true,
        initialValues,
        onSubmit: async (data) => {
            let res = await DeviceService.addDeviceMst(data);
            if (res.status === 200) {
                addDevice();
            } else if (res.status === 400) {
                setErrorsValidate(res.data.errors);
                console.log("errors");
            } else if (res.status === 500) {
                setError(t('validate.customer.INSERT_FAILED'));
                console.log("errors");
            }
        }
    })


    const stepBack = () => {
        setActiveIndex(activeIndex - 1);
    }

    const stepNext = () => {
        setActiveIndex(activeIndex + 1);
    }

    const keepValueForm = () => {
        formik.setFieldValue("load_type_id", $('#load_type_id').val());
        formik.setFieldValue("manufacturer", $('#manufacturer').val());
        formik.setFieldValue("model", $('#model').val());
        formik.setFieldValue("vn", $('#vn').val());
        formik.setFieldValue("in", $('#in').val());
        formik.setFieldValue("cable_length", $('#cable_length').val());
        formik.setFieldValue("rho", $('#rho').val());
        formik.setFieldValue("reference_device_id", $('#reference_device_id').val());
        formik.setFieldValue("description", $('#description').val());
        formik.setFieldValue("work_date", $('#work_date').val());
        formik.setFieldValue("pdm", $('#pdm').val());
        formik.setFieldValue("priority_flag", $('#priority_flag').val());
    }


    useEffect(() => {
        getInfoAdd();
        keepValueForm();
        console.log("Object type: ", props.data.objectId);
    }, [props.isActive])

    return (
        <>
            {
                (error != null && props.isActive == 4) &&
                <div className="alert alert-danger" role="alert">
                    <p className="m-0 p-0">{t('content.category.device.add.error_add')}</p>
                </div>
            }
            {
                (errorsValidate.length > 0 && props.isActive == 4) &&
                <div className="alert alert-warning" role="alert">
                    {
                        errorsValidate.map((error, index) => {
                            return (<p key={index + 1} className="m-0 p-0">{error}</p>)
                        })
                    }
                </div>
            }
            <div id="formValue" className={`relative flex flex-col items-center justify-center mt-5 p-2 
                        bg-white rounded-md text-sm ${props.isActive != 3 && "hidden"} `}>
                <table className={`table table-input`}>
                    <tbody>
                        <tr className="deviceCode">
                            <th width="300px">{t('content.category.device.lable_device_id')} (SID)</th>
                            <td>
                                <input type="text" className="form-control" name="deviceCode" id="device-code" defaultValue={props.data.deviceCode} disabled />
                            </td>
                        </tr>
                        <tr>
                            <th>{t('content.load_type')}</th>
                            <td>
                                <select className="custom-select  system-type-id" name="load_type_id" id="load_type_id" onChange={formik.handleChange}>
                                    {
                                        loadTypes?.length > 0 && loadTypes?.map((lt, index) => {
                                            return <option key={index + 1} value={lt.loadTypeId}>{lt.loadTypeName}</option>
                                        })
                                    }
                                </select>
                            </td>
                        </tr>
                        <tr>
                            <th>{t('content.category.device.lable_manufacture')}</th>
                            <td>
                                <input type="text" className="form-control" name="manufacturer" id="manufacturer" onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="model">
                            <th>Model</th>
                            <td>
                                <input type="text" className="form-control" name="model" id="model" onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="vn">
                            <th id="lb-imccb">{t('content.category.device.rated_voltage')} [V]</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="vn" id="vn" defaultValue={formik.values.vn} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="in">
                            <th id="lb-imccb">{t('content.current')} [A]</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="in" id="in" defaultValue={formik.values.in} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="cable_length">
                            <th>{t('content.category.device.cable_length')} [m]</th>
                            <td>
                                <input type="number" className="form-control" name="cable_length" id="cable_length" defaultValue={formik.values.cable_length} onChange={formik.handleChange} />
                            </td>
                        </tr>

                        <tr className="rho">
                            <th>{t('content.category.device.conductor_resistivity')} [Ohm/m]</th>
                            <td>
                                <input type="number" className="form-control" name="rho" id="rho" defaultValue={formik.values.rho} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="pdm">
                            <th>{t('content.category.device.rated_power')} [kW]</th>
                            <td>
                                <input type="number" className="form-control" name="pdm" id="pdm" defaultValue={formik.values.pdm} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="reference_device_id">
                            <th>{t('content.category.device.connect_device')}</th>
                            <td>
                                <select className="custom-select  system-type-id" name="reference_device_id" id="reference_device_id" onChange={formik.handleChange}>
                                    <option value=""></option>
                                    {
                                        deviceGateway?.length > 0 && deviceGateway?.map((device, index) => {
                                            return <option key={index + 1} value={device.deviceId}>{device.deviceName}</option>
                                        })
                                    }
                                </select>
                            </td>
                        </tr>
                        <tr className="priority_flag">
                            <th>{t('content.category.device.priority_flag')}</th>
                            <td>
                                <select className="custom-select  system-type-id" name="priority_flag" id="priority_flag" onChange={formik.handleChange}>
                                    <option value={1}>{t('content.category.device.low')}</option>
                                    <option value={2}>{t('content.category.device.normal')}</option>
                                    <option value={3}>{t('content.category.device.high')}</option>
                                </select>
                            </td>
                        </tr>
                        <tr>
                            <th>{t('content.description')}</th>
                            <td>
                                <input type="text" className="form-control" name="description" id="description" onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="work_date string">
                            <th>{t('content.start_time')}</th>
                            <td>
                                <input type="date" className="form-control" name="work_date" id="work_date" defaultValue={formik.values.work_date} onChange={formik.handleChange}></input>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            {
                props.isActive == 4 ?
                    <div className={`relative flex flex-col items-center justify-center mt-5 p-2
                bg-white rounded-md text-sm  ${props.isActive != 4 && "hidden"}`}>
                        <form onSubmit={formik.handleSubmit} id="form5">
                            <table className={`table table-input`}>
                                <tbody>
                                    <tr>
                                        <th width="180px">{t('content.customer')} </th>
                                        <td>
                                            <select className="custom-select  customer-id" id="customer-id" name="customerId" value={props.data.customerId} disabled>
                                                {customer?.length > 0 && customer?.map((c, index) => {
                                                    return <option key={index + 1} value={c.customerId}>{c.customerName}</option>
                                                })}
                                            </select>
                                        </td>
                                    </tr>

                                    <tr>
                                        <th width="180px">{t('content.project')} </th>
                                        <td>
                                            <select className="custom-select  project-id" id="project-id" name="projectId" value={props.data.projectId} disabled>
                                                {projects?.length > 0 && projects?.map((p, index) => {
                                                    return <option key={index + 1} value={p.projectId}>{p.projectName}</option>
                                                })}
                                            </select>
                                        </td>
                                    </tr>

                                    <tr>
                                        <th width="230px">{t('content.system')} </th>
                                        <td>
                                            <select className="custom-select  system-type-id" id="system-type-id" name="systemTypeId" value={props.data.systemTypeId} disabled>
                                                {
                                                    systemTypes?.length > 0 && systemTypes?.map((systemType, index) => {
                                                        return <option key={index + 1} value={systemType.systemTypeId}>{systemType.systemTypeName}</option>
                                                    })
                                                }
                                            </select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th width="180px">{t('content.country')} </th>
                                        <td>
                                            <select className="custom-select  country-id" id="country-id" disabled>
                                                <option value="84">Việt Nam</option>
                                            </select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th width="180px">{t('content.manager')} </th>
                                        <td>
                                            <select className="custom-select  manager-id" id="manager-id" name="managerId" value={props.data.managerId} disabled>
                                                {
                                                    managers?.length > 0 && managers?.map((manager, index) => {
                                                        return <option key={index + 1} value={manager.managerId}>{manager.managerName}</option>
                                                    })
                                                }
                                            </select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th width="180px">{t('content.area')} </th>
                                        <td>
                                            <select className="custom-select  area-id" id="area-id" name="areaId" value={props.data.areaId} disabled>
                                                {areas?.length > 0 && areas?.map((area, index) => {
                                                    return <option key={index + 1} value={area.areaId}>{area.areaName}</option>
                                                })}
                                            </select>
                                        </td>
                                    </tr>
                                    <tr className="address">
                                        <th>{t('content.address')}</th>
                                        <td>
                                            <input type="text" className="form-control" name="address" id="address" value={props.data.address} disabled />
                                        </td>
                                    </tr>
                                    <tr>
                                        <th width="300px">{t('content.super_manager')}</th>
                                        <td>
                                            <input type="text" className="form-control area" name="location" value={props.data.location} disabled />
                                        </td>
                                    </tr>
                                    <tr className="longitude">
                                        <th>{t('content.longitude')}</th>
                                        <td>
                                            <input type="number" step="0.0000000001" className="form-control input-number-m" name="longitude" id="longitude" value={props.data.longitude} disabled />
                                        </td>
                                    </tr>
                                    <tr className="latitude">
                                        <th>{t('content.latitude')}</th>
                                        <td>
                                            <input type="number" step="0.0000000001" className="form-control input-number-m" name="latitude" id="latitude" value={props.data.latitude} disabled />
                                        </td>
                                    </tr>
                                    <tr className="device-name" >
                                        <th width="180px">{t('content.category.device.lable_device_name')}<span className="required">※</span></th>
                                        <td>
                                            <input type="text" className="form-control" name="deviceName" id="deviceName" value={props.data.deviceName} disabled />
                                        </td>
                                    </tr>
                                    <tr>
                                        <th width="180px">{t('content.category.device.lable_device_type')}</th>
                                        <td>
                                            <select className="custom-select  device-type-id" id="device-type-id" name="deviceTypeId" value={props.data.deviceType} disabled>
                                                <option value=""></option>
                                                {
                                                    deviceTypes?.length > 0 && deviceTypes?.map((dt, index) => {
                                                        return <option key={index + 1} value={dt.id}>{dt.name}</option>
                                                    })
                                                }
                                            </select>
                                        </td>
                                    </tr>
                                    <tr className="object-type">
                                        <th width="180px">{t('content.object')}</th>
                                        <td>
                                            <select className="custom-select  object-type-id" id="object-type-id" name="objectId" value={props.data.objectId} disabled>
                                                <option value=""></option>
                                                {
                                                    objects?.length > 0 && objects?.map((obj, index) => {
                                                        return <option key={index + 1} value={obj.id}>{obj.name}</option>
                                                    })
                                                }
                                            </select>
                                        </td>
                                    </tr>
                                    <tr className="deviceCode">
                                        <th width="300px">{t('content.category.device.lable_device_id')} (SID)</th>
                                        <td>
                                            <input type="text" className="form-control" name="deviceCode" id="device-code" value={props.data.deviceCode} disabled />
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>{t('content.load_type')}</th>
                                        <td>
                                            <select className="custom-select  system-type-id" name="load_type_id" id="load_type_id" defaultValue={formik.values.load_type_id} disabled>
                                                {
                                                    loadTypes?.length > 0 && loadTypes?.map((lt, index) => {
                                                        return <option key={index + 1} value={lt.loadTypeId}>{lt.loadTypeName}</option>
                                                    })
                                                }
                                            </select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>{t('content.category.device.lable_manufacture')}</th>
                                        <td>
                                            <input type="text" className="form-control" name="manufacturer" id="manufacturer" value={formik.values.manufacturer} disabled />
                                        </td>
                                    </tr>
                                    <tr className="model">
                                        <th>Model</th>
                                        <td>
                                            <input type="text" className="form-control" name="model" id="model" value={formik.values.model} disabled />
                                        </td>
                                    </tr>
                                    <tr className="vn">
                                        <th id="lb-imccb">{t('content.category.device.rated_voltage')} [V]</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="vn" id="vn" value={formik.values.vn} disabled />
                                        </td>
                                    </tr>
                                    <tr className="in">
                                        <th id="lb-imccb">{t('content.current')} [A]</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="in" id="in" value={formik.values.in} disabled />
                                        </td>
                                    </tr>
                                    <tr className="cable_length">
                                        <th>{t('content.category.device.cable_length')} [m]</th>
                                        <td>
                                            <input type="number" className="form-control" name="cable_length" id="cable_length" value={formik.values.cable_length} disabled />
                                        </td>
                                    </tr>

                                    <tr className="rho">
                                        <th>{t('content.category.device.conductor_resistivity')} [Ohm/m]</th>
                                        <td>
                                            <input type="number" className="form-control" name="rho" id="rho" value={formik.values.rho} disabled />
                                        </td>
                                    </tr>
                                    <tr className="pdm">
                                        <th>{t('content.category.device.rated_power')} [kW]</th>
                                        <td>
                                            <input type="number" className="form-control" name="pdm" id="pdm" value={formik.values.pdm} disabled />
                                        </td>
                                    </tr>
                                    <tr className="reference_device_id">
                                        <th>{t('content.category.device.connect_device')}</th>
                                        <td>
                                            <select className="custom-select  system-type-id" name="reference_device_id" id="reference_device_id" value={formik.values.reference_device_id} disabled>
                                                <option value=""></option>
                                                {
                                                    deviceGateway?.length > 0 && deviceGateway?.map((device, index) => {
                                                        return <option key={index + 1} value={device.deviceId}>{device.deviceName}</option>
                                                    })
                                                }
                                            </select>
                                        </td>
                                    </tr>
                                    <tr className="priority_flag">
                                        <th>{t('content.category.device.priority_flag')}</th>
                                        <td>
                                            <select className="custom-select  system-type-id" name="priority_flag" id="priority_flag" value={formik.values.priority_flag} disabled>
                                                <option value={1}>{t('content.category.device.low')}</option>
                                                <option value={2}>{t('content.category.device.normal')}</option>
                                                <option value={3}>{t('content.category.device.high')}</option>
                                            </select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>{t('content.description')}</th>
                                        <td>
                                            <input type="text" className="form-control" name="description" value={formik.values.description} disabled />
                                        </td>
                                    </tr>
                                    <tr className="work_date string">
                                        <th>{t('content.category.device.start_time')}</th>
                                        <td>
                                            <input type="date" className="form-control" name="work_date" value={moment(formik.values.work_date).format("YYYY-MM-DD")} disabled />
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </form>
                    </div> : <></>
            }

        </>
    )
}

export default MeterAny;