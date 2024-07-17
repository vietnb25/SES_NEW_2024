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


const StringPV = (props) => {
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
        uid: "",
        model: "",
        dbId: "",
        description: "",
        manufacturer: '',
        p_max: 550,
        vmp: 41,
        imp: 13.2,
        voc: 50,
        isc: 14,
        gstc: 1000,
        tstc: 25,
        gnoct: 800,
        tnoct: 45,
        cp_max: -0.34,
        cvoc: -0.26,
        cisc: 0.05,
        ns: 10,
        sensor_radiation_id: 0,
        sensor_temperature_id: 0,
        eff: 21,
        reference_device_id: 0,
        work_date: moment(new Date()).format("YYYY-MM-DD"),
        priority_flag: 1,
    }
    const [systemTypes, setSystemTypes] = useState([]);
    const [deviceTypes, setDeviceTypes] = useState([]);
    const [cutomers, setCustomers] = useState([]);
    const [areas, setAreas] = useState([]);
    const [managers, setManagers] = useState([]);
    const [projects, setProjects] = useState([]);
    const { t } = useTranslation();
    const [errorsValidate, setErrorsValidate] = useState([]);
    const [activeIndex, setActiveIndex] = useState(props.isActive);
    const [error, setError] = useState(null);
    const [customer, setCustomer] = useState([]);
    const [objects, setObjects] = useState([]);
    const [deviceGateway, setDeviceGateway] = useState([]);

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
        let resProjects = await ProjectService.getProjectByCustomerId(responseCustomer.data[0].customerId);
        setProjects(resProjects.data);
        let resObject = await ObjectService.getListObjectMst();
        setObjects(resObject.data);
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
                    text: "<i class=\"fa-solid fa-arrow-rotate-left\"></i> " + t('content.category.device.list.title'),
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

            } else if (res.status === 500) {
                setError(t('validate.customer.INSERT_FAILED'));

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
        formik.setFieldValue("manufacturer", $('#manufacturer').val());
        formik.setFieldValue("model", $('#model').val());
        formik.setFieldValue("p_max", $('#p_max').val());
        formik.setFieldValue("vmp", $('#vmp').val());
        formik.setFieldValue("imp", $('#imp').val());
        formik.setFieldValue("voc", $('#voc').val());
        formik.setFieldValue("isc", $('#isc').val());
        formik.setFieldValue("gstc", $('#gstc').val());
        formik.setFieldValue("tstc", $('#tstc').val());
        formik.setFieldValue("gnoct", $('#gnoct').val());
        formik.setFieldValue("tnoct", $('#tnoct').val());
        formik.setFieldValue("cp_max", $('#cp_max').val());
        formik.setFieldValue("cvoc", $('#cvoc').val());
        formik.setFieldValue("cisc", $('#cisc').val());
        formik.setFieldValue("ns", $('#ns').val());
        formik.setFieldValue("sensor_radiation_id", $('#sensor_radiation_id').val());
        formik.setFieldValue("sensor_temperature_id", $('#sensor_temperature_id').val());
        formik.setFieldValue("eff", $('#eff').val());
        formik.setFieldValue("reference_device_id", $('#reference_device_id').val());
        formik.setFieldValue("description", $('#description').val());
        formik.setFieldValue("work_date", $('#work_date').val());
        formik.setFieldValue("priority_flag", $('#priority_flag').val());
    }

    useEffect(() => {
        getInfoAdd();
        keepValueForm();
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
                <table className={`table table-input `}>
                    <tbody>
                        <tr className="deviceCode">
                            <th width="300px">{t('content.category.device.lable_device_id')} (SID)</th>
                            <td>
                                <input type="text" className="form-control" name="deviceCode" id="device-code" defaultValue={props.data.deviceCode} disabled />
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
                        <tr className="p_max">
                            <th>{t('content.category.device.power_max')}</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="p_max" id="p_max" defaultValue={formik.values.p_max} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="vmp">
                            <th id="lb-imccb">{t('content.category.device.rated_voltage')} Vmp [V]</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="vmp" id="vmp" defaultValue={formik.values.vmp} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="imp">
                            <th id="lb-imccb">{t('content.category.device.rated_voltage')} Imp [A]</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="imp" id="imp" defaultValue={formik.values.imp} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="voc">
                            <th id="lb-imccb">{t('content.category.device.open_circuit_voltage')} Voc [V]</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="voc" id="voc" defaultValue={formik.values.voc} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="isc">
                            <th id="lb-imccb">{t('content.category.device.short_circuit_voltage')} Isc [A]</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="isc" id="isc" defaultValue={formik.values.isc} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="eff string">
                            <th>{t('content.category.device.efficiency')} [%]</th>
                            <td>
                                <input type="number" className="form-control" name="eff" id="eff" defaultValue={formik.values.eff} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="gstc">
                            <th>{t('content.category.device.radiation')} STC [W/m2]</th>
                            <td>
                                <input type="number" className="form-control" name="gstc" id="gstc" defaultValue={formik.values.gstc} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="tstc">
                            <th>{t('content.category.device.temperature')} STC [oC]</th>
                            <td>
                                <input type="number" className="form-control" name="tstc" id="tstc" defaultValue={formik.values.tstc} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="gnoct">
                            <th>{t('content.category.device.radiation')} NOCT [W/m2]</th>
                            <td>
                                <input type="number" className="form-control" name="gnoct" id="gnoct" defaultValue={formik.values.gnoct} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="tnoct">
                            <th>{t('content.category.device.temperature')} NOCT [oC]</th>
                            <td>
                                <input type="number" className="form-control" name="tnoct" id="tnoct" defaultValue={formik.values.tnoct} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        {/* temp_coefficient */}
                        <tr className="cp_max">
                            <th>{t('content.category.device.temp_coefficient')} Pmax</th>
                            <td>
                                <input type="number" className="form-control" name="cp_max" id="cp_max" defaultValue={formik.values.cp_max} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="cvoc">
                            <th>{t('content.category.device.temp_coefficient')} Voc</th>
                            <td>
                                <input type="number" className="form-control" name="cvoc" id="cvoc" defaultValue={formik.values.cvoc} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="cisc">
                            <th>{t('content.category.device.temp_coefficient')} Isc</th>
                            <td>
                                <input type="number" className="form-control" name="cisc" id="cisc" defaultValue={formik.values.cisc} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="ns">
                            <th>{t('content.category.device.panels_in_series')}</th>
                            <td>
                                <input type="number" className="form-control" name="ns" id="ns" defaultValue={formik.values.ns} onChange={formik.handleChange} />
                            </td>
                        </tr>

                        <tr className="sensor_radiation_id">
                            <th>{t('content.category.device.data_radiation')}</th>
                            <td>
                                <input type="number" className="form-control" name="sensor_radiation_id" id="sensor_radiation_id" onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="sensor_temperature_id">
                            <th>{t('content.category.device.data_temp')}</th>
                            <td>
                                <input type="number" className="form-control" name="sensor_temperature_id" id="sensor_temperature_id" onChange={formik.handleChange} />
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
                                <input type="text" className="form-control" name="description" onChange={formik.handleChange} defaultValue={formik.values.description} disabled />
                            </td>
                        </tr>
                        <tr className="work_date string">
                            <th>{t('content.category.device.start_time')}</th>
                            <td>
                                <input type="text" className="form-control" name="work_date" value={moment(formik.values.work_date).format("YYYY-MM-DD")} disabled />
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            {
                props.isActive == 4 ?
                    <div className={`relative flex flex-col items-center justify-center mt-5 p-2
                bg-white rounded-md text-sm  ${props.isActive != 4 && "hidden"}`}>
                        <form onSubmit={formik.handleSubmit} id="form9">
                            <table className={`table table-input `}>
                                <tbody>
                                    <tr>
                                        <th width="180px">{t('content.customer')} </th>
                                        <td>
                                            <select className="custom-select  customer-id" id="customer-id" defaultValue={props.data.customerId} onChange={formik.handleChange} disabled>
                                                {customer?.length > 0 && customer?.map((c, index) => {
                                                    return <option key={index + 1} value={c.customerId}>{c.customerName}</option>
                                                })}
                                            </select>
                                        </td>
                                    </tr>

                                    <tr>
                                        <th width="180px">{t('content.project')} </th>
                                        <td>
                                            <select className="custom-select  project-id" id="project-id" defaultValue={props.data.projectId} onChange={formik.handleChange} disabled>
                                                {projects?.length > 0 && projects?.map((p, index) => {
                                                    return <option key={index + 1} value={p.projectId}>{p.projectName}</option>
                                                })}
                                            </select>
                                        </td>
                                    </tr>

                                    <tr>
                                        <th width="230px">{t('content.system')} </th>
                                        <td>
                                            <select className="custom-select  system-type-id" id="system-type-id" defaultValue={props.data.systemTypeId} onChange={formik.handleChange} disabled>
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
                                            <select className="custom-select  manager-id" id="manager-id" defaultValue={props.data.managerId} onChange={formik.handleChange} disabled>
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
                                            <select className="custom-select  area-id" id="area-id" defaultValue={props.data.areaId} onChange={formik.handleChange} disabled>
                                                {areas?.length > 0 && areas?.map((area, index) => {
                                                    return <option key={index + 1} value={area.areaId}>{area.areaName}</option>
                                                })}
                                            </select>
                                        </td>
                                    </tr>
                                    <tr className="address">
                                        <th>{t('content.address')}</th>
                                        <td>
                                            <input type="text" className="form-control" name="address" id="address" onChange={formik.handleChange} defaultValue={props.data.address} disabled />
                                        </td>
                                    </tr>
                                    <tr>
                                        <th width="300px">{t('content.super_manager')}</th>
                                        <td>
                                            <input type="text" className="form-control area" name="location" onChange={formik.handleChange} defaultValue={props.data.location} disabled />
                                        </td>
                                    </tr>
                                    <tr className="longitude">
                                        <th>{t('content.longitude')}</th>
                                        <td>
                                            <input type="number" step="0.0000000001" className="form-control input-number-m" name="longitude" id="longitude" onChange={formik.handleChange} defaultValue={props.data.longitude} disabled />
                                        </td>
                                    </tr>
                                    <tr className="latitude">
                                        <th>{t('content.latitude')}</th>
                                        <td>
                                            <input type="number" step="0.0000000001" className="form-control input-number-m" name="latitude" id="latitude" onChange={formik.handleChange} defaultValue={props.data.latitude} disabled />
                                        </td>
                                    </tr>
                                    <tr className="device-name" >
                                        <th width="180px">{t('content.category.device.lable_device_name')}<span className="required">※</span></th>
                                        <td>
                                            <input type="text" onChange={formik.handleChange} className="form-control" name="deviceName" id="deviceName" defaultValue={props.data.deviceName} disabled />
                                        </td>
                                    </tr>
                                    <tr>
                                        <th width="180px">{t('content.category.device.lable_device_type')}</th>
                                        <td>
                                            <select className="custom-select  device-type-id" id="device-type-id" onChange={formik.handleChange} defaultValue={props.data.deviceType} disabled>
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
                                            <select className="custom-select  object-type-id" id="object-type-id" onChange={formik.handleChange} defaultValue={props.data.objectId} disabled>
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
                                            <input type="text" className="form-control" name="deviceCode" id="device-code" defaultValue={props.data.deviceCode} disabled />
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>{t('content.category.device.lable_manufacture')}</th>
                                        <td>
                                            <input type="text" className="form-control" name="manufacturer" id="manufacturer" onChange={formik.handleChange} defaultValue={formik.values.manufacturer} disabled />
                                        </td>
                                    </tr>
                                    <tr className="model">
                                        <th>Model</th>
                                        <td>
                                            <input type="text" className="form-control" name="model" id="model" onChange={formik.handleChange} defaultValue={formik.values.model} disabled />
                                        </td>
                                    </tr>
                                    <tr className="p_max">
                                        <th>{t('content.category.device.power_max')}</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="p_max" id="p_max" onChange={formik.handleChange} defaultValue={formik.values.p_max} disabled />
                                        </td>
                                    </tr>
                                    <tr className="vmp">
                                        <th id="lb-imccb">{t('content.category.device.rated_voltage')} Vmp [V]</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="vmp" id="vmp" onChange={formik.handleChange} defaultValue={formik.values.vmp} disabled />
                                        </td>
                                    </tr>
                                    <tr className="imp">
                                        <th id="lb-imccb">{t('content.category.device.rated_voltage')} Imp [A]</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="imp" id="imp" onChange={formik.handleChange} defaultValue={formik.values.imp} disabled />
                                        </td>
                                    </tr>
                                    <tr className="voc">
                                        <th id="lb-imccb">{t('content.category.device.open_circuit_voltage')} Voc [V]</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="voc" id="voc" onChange={formik.handleChange} defaultValue={formik.values.voc} disabled />
                                        </td>
                                    </tr>
                                    <tr className="isc">
                                        <th id="lb-imccb">{t('content.category.device.short_circuit_voltage')} Isc [A]</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="isc" id="isc" onChange={formik.handleChange} defaultValue={formik.values.isc} disabled />
                                        </td>
                                    </tr>
                                    <tr className="eff string">
                                        <th>{t('content.category.device.efficiency')} [%]</th>
                                        <td>
                                            <input type="number" className="form-control" name="eff" id="eff" onChange={formik.handleChange} defaultValue={formik.values.eff} disabled />
                                        </td>
                                    </tr>
                                    <tr className="gstc">
                                        <th>{t('content.category.device.radiation')} STC [W/m2]</th>
                                        <td>
                                            <input type="number" className="form-control" name="gstc" id="gstc" onChange={formik.handleChange} defaultValue={formik.values.gstc} disabled />
                                        </td>
                                    </tr>
                                    <tr className="tstc">
                                        <th>{t('content.category.device.temperature')} STC [oC]</th>
                                        <td>
                                            <input type="number" className="form-control" name="tstc" id="tstc" onChange={formik.handleChange} defaultValue={formik.values.tstc} disabled />
                                        </td>
                                    </tr>
                                    <tr className="gnoct">
                                        <th>{t('content.category.device.radiation')} NOCT [W/m2]</th>
                                        <td>
                                            <input type="number" className="form-control" name="gnoct" id="gnoct" onChange={formik.handleChange} defaultValue={formik.values.gnoct} disabled />
                                        </td>
                                    </tr>
                                    <tr className="tnoct">
                                        <th>{t('content.category.device.temperature')} NOCT [oC]</th>
                                        <td>
                                            <input type="number" className="form-control" name="tnoct" id="tnoct" onChange={formik.handleChange} defaultValue={formik.values.tnoct} disabled />
                                        </td>
                                    </tr>
                                    <tr className="cp_max">
                                        <th>{t('content.category.device.temp_coefficient')} Pmax</th>
                                        <td>
                                            <input type="number" className="form-control" name="cp_max" id="cp_max" onChange={formik.handleChange} defaultValue={formik.values.cp_max} disabled />
                                        </td>
                                    </tr>
                                    <tr className="cvoc">
                                        <th>{t('content.category.device.temp_coefficient')} Voc</th>
                                        <td>
                                            <input type="number" className="form-control" name="cvoc" id="cvoc" onChange={formik.handleChange} defaultValue={formik.values.cvoc} disabled />
                                        </td>
                                    </tr>
                                    <tr className="cisc">
                                        <th>{t('content.category.device.temp_coefficient')} Isc</th>
                                        <td>
                                            <input type="number" className="form-control" name="cisc" id="cisc" onChange={formik.handleChange} defaultValue={formik.values.cisc} disabled />
                                        </td>
                                    </tr>
                                    <tr className="ns">
                                        <th>{t('content.category.device.panels_in_series')}</th>
                                        <td>
                                            <input type="number" className="form-control" name="ns" id="ns" onChange={formik.handleChange} defaultValue={formik.values.ns} disabled />
                                        </td>
                                    </tr>

                                    <tr className="sensor_radiation_id">
                                        <th>{t('content.category.device.data_radiation')}</th>
                                        <td>
                                            <input type="number" className="form-control" name="sensor_radiation_id" id="sensor_radiation_id" onChange={formik.handleChange} defaultValue={formik.values.sensor_radiation_id} disabled />
                                        </td>
                                    </tr>
                                    <tr className="sensor_temperature_id">
                                        <th>{t('content.category.device.data_temp')}</th>
                                        <td>
                                            <input type="number" className="form-control" name="sensor_temperature_id" id="sensor_temperature_id" onChange={formik.handleChange} defaultValue={formik.values.sensor_temperature_id} disabled />
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
                                            <input type="text" className="form-control" name="description" onChange={formik.handleChange} defaultValue={formik.values.description} disabled />
                                        </td>
                                    </tr>
                                    <tr className="work_date string">
                                        <th>{t('content.category.device.start_time')}</th>
                                        <td>
                                            <input type="text" className="form-control" name="work_date" value={moment(formik.values.work_date).format("YYYY-MM-DD")} disabled />
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

export default StringPV;