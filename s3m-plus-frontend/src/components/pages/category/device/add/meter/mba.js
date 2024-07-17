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



const MeterMBA = (props) => {
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
        load_type_id: '',
        vsc: 400,
        vpr: 22000,
        f: 22,
        delta_p0: '',
        delta_pk: '',
        i0: '',
        un: '',
        m_oil: '',
        m_all: '',
        exp_oil: 0.8,
        exp_wind: 1.6,
        hot_spot_factor: 1.1,
        loss_ratio: 5,
        const_k11: 1,
        const_k21: 1,
        const_k22: 1,
        hot_spot_temp: 98,
        hot_spot_gradient: 23,
        avg_oil_temp_rise: 44,
        top_oil_temp_rise: 44,
        bottom_oil_temp_rise: 1,
        const_time_oil: 98,
        const_time_winding: 98,
        pn: '',
        in: '',
        inc: '',
        work_date: moment(new Date()).format("YYYY-MM-DD"),
        description: '',
        reference_device_id: '',
        work_time: '',
        pdm: 0,
        priority_flag: 1,
    }
    const [systemTypes, setSystemTypes] = useState([]);
    const [deviceTypes, setDeviceTypes] = useState([]);
    const [deviceGateway, setDeviceGateway] = useState([]);
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
        formik.setFieldValue("uid", $('#uid').val());
        formik.setFieldValue("load_type_id", $('#load_type_id').val());
        formik.setFieldValue("manufacturer", $('#manufacturer').val());
        formik.setFieldValue("model", $('#model').val());
        formik.setFieldValue("vsc", $('#vsc').val());
        formik.setFieldValue("vpr", $('#vpr').val());
        formik.setFieldValue("f", $('#f').val());
        formik.setFieldValue("delta_p0", $('#delta_p0').val());
        formik.setFieldValue("delta_pk", $('#delta_pk').val());
        formik.setFieldValue("i0", $('#i0').val());
        formik.setFieldValue("un", $('#un').val());
        formik.setFieldValue("m_oil", $('#m_oil').val());
        formik.setFieldValue("m_all", $('#m_all').val());
        formik.setFieldValue("exp_oil", $('#exp_oil').val());
        formik.setFieldValue("exp_wind", $('#exp_wind').val());
        formik.setFieldValue("hot_spot_factor", $('#hot_spot_factor').val());
        formik.setFieldValue("loss_ratio", $('#loss_ratio').val());
        formik.setFieldValue("const_k11", $('#const_k11').val());
        formik.setFieldValue("const_k21", $('#const_k21').val());
        formik.setFieldValue("const_k22", $('#const_k22').val());
        formik.setFieldValue("hot_spot_temp", $('#hot_spot_temp').val());
        formik.setFieldValue("hot_spot_gradient", $('#hot_spot_gradient').val());
        formik.setFieldValue("avg_oil_temp_rise", $('#avg_oil_temp_rise').val());
        formik.setFieldValue("top_oil_temp_rise", $('#top_oil_temp_rise').val());
        formik.setFieldValue("bottom_oil_temp_rise", $('#bottom_oil_temp_rise').val());
        formik.setFieldValue("const_time_oil", $('#const_time_oil').val());
        formik.setFieldValue("const_time_winding", $('#const_time_winding').val());
        formik.setFieldValue("pn", $('#pn').val());
        formik.setFieldValue("in", $('#in').val());
        formik.setFieldValue("inc", $('#inc').val());
        formik.setFieldValue("description", $('#description').val());
        formik.setFieldValue("reference_device_id", $('#reference_device_id').val());
        formik.setFieldValue("work_date", $('#work_date').val());
        formik.setFieldValue("pdm", $('#pdm').val());
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
                        <tr className="pn">
                            <th>{t('content.category.device.lable_device_id')}</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="pn" id="pn" onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="in">
                            <th id="lb-imccb">{t('content.category.device.rated_current')}</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="in" id="in" onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="vsc">
                            <th>{t('content.category.device.secondary_voltage')}[V]</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="vsc" id="vsc" onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="vpr">
                            <th>{t('content.category.device.primary_voltage')}[V]</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="vpr" id="vpr" onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="f">
                            <th>{t('content.category.device.frequency')} [Hz]</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="f" id="f" onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="delta_p0">
                            <th>{t('content.category.device.no_load_loss')} [kW]</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="delta_p0" id="delta_p0" onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="delta_pk">
                            <th>{t('content.category.device.short_circuit_lost')} [kW]</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="delta_pk" id="delta_pk" onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="i0">
                            <th id="lb-imccb">{t('content.category.device.current_load_loss')}(%)</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="i0" id="i0" onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="un">
                            <th>{t('content.category.device.short_circuit_voltage')}[%]</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="un" id="un" onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="m_oil">
                            <th>{t('content.category.device.oil_weight')} [kg]</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="m_oil" id="m_oil" onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="m_all">
                            <th>{t('content.category.device.total_weight')} [kg]</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="m_all" id="m_all" onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="exp_oil stmv sgmv">
                            <th>Oil exponent</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="exp_oil" id="exp_oil" defaultValue={formik.values.exp_oil} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="exp_wind stmv sgmv">
                            <th>Winding exponent</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="exp_wind" id="exp_wind" defaultValue={formik.values.exp_wind} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="hot_spot_factor stmv sgmv">
                            <th>Hot-spot factor</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="hot_spot_factor" id="hot_spot_factor" defaultValue={formik.values.hot_spot_factor} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="loss_ratio stmv sgmv">
                            <th>Loss ratio</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="loss_ratio" id="loss_ratio" defaultValue={formik.values.loss_ratio} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="const_k11 stmv sgmv">
                            <th>{t('content.category.device.constant')} K11</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="const_k11" id="const_k11" defaultValue={formik.values.const_k11} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="const_k21 stmv sgmv">
                            <th>{t('content.category.device.constant')} K21</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="const_k21" id="const_k21" defaultValue={formik.values.const_k21} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="const_k22 stmv sgmv">
                            <th>{t('content.category.device.constant')} K22</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="const_k22" id="const_k22" defaultValue={formik.values.const_k22} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="hot_spot_temp stmv sgmv">
                            <th>Hot-spot temperature</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="hot_spot_temp" id="hot_spot_temp" defaultValue={formik.values.hot_spot_temp} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="hot_spot_gradient stmv sgmv">
                            <th>Hot-spot to top-oil gradient at rated current</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="hot_spot_gradient" id="hot_spot_gradient" defaultValue={formik.values.hot_spot_gradient} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="avg_oil_temp_rise stmv sgmv">
                            <th>Average oil temperature rise</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="avg_oil_temp_rise" id="avg_oil_temp_rise" defaultValue={formik.values.avg_oil_temp_rise} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="top_oil_temp_rise stmv sgmv">
                            <th>Top-oil temperature rise</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="top_oil_temp_rise" id="top_oil_temp_rise" defaultValue={formik.values.top_oil_temp_rise} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="bottom_oil_temp_rise stmv sgmv">
                            <th>Bottom oil temperature rise</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="bottom_oil_temp_rise" id="bottom_oil_temp_rise" defaultValue={formik.values.bottom_oil_temp_rise} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="const_time_oil stmv sgmv">
                            <th>{t('content.category.device.oil_time_constant')}</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="const_time_oil" id="const_time_oil" defaultValue={formik.values.const_time_oil} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="const_time_winding stmv sgmv">
                            <th>{t('content.category.device.coil_time_constant')}</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="const_time_winding" id="const_time_winding" defaultValue={formik.values.const_time_winding} onChange={formik.handleChange} />
                            </td>
                        </tr>

                        <tr className="inc">
                            <th id="lb-imccb">{t('content.category.device.neutral_rated_current')} [A]</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="inc" id="inc" onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="pdm">
                            <th>{t('content.category.device.rated_power')} [kW]</th>
                            <td>
                                <input type="number" className="form-control" name="pdm" id="pdm" defaultValue={formik.values.pdm} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="uid">
                            <th width="180px">UID<span className="required">※</span></th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="uid" id="uid" onChange={formik.handleChange} />

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
                                <input type="date" className="form-control" name="work_date" id="work_date" defaultValue={formik.values.work_date} onChange={formik.handleChange} />
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>


            {
                props.isActive == 4 ?
                    <div className={`relative flex flex-col items-center justify-center mt-5 p-2
                    bg-white rounded-md text-sm  ${props.isActive != 4 && "hidden"}`}>
                        <form onSubmit={formik.handleSubmit} id="form1">
                            <table className={`table table-input `}>
                                <tbody>
                                    <tr>
                                        <th width="180px">{t('content.customer')} </th>
                                        <td>
                                            <select className="custom-select  customer-id" id="customer-id" value={props.data.customerId} disabled>
                                                {customer?.length > 0 && customer?.map((c, index) => {
                                                    return <option key={index + 1} value={c.customerId}>{c.customerName}</option>
                                                })}
                                            </select>
                                        </td>
                                    </tr>

                                    <tr>
                                        <th width="180px">{t('content.project')} </th>
                                        <td>
                                            <select className="custom-select  project-id" id="project-id" value={props.data.projectId} disabled>
                                                {projects?.length > 0 && projects?.map((p, index) => {
                                                    return <option key={index + 1} value={p.projectId}>{p.projectName}</option>
                                                })}
                                            </select>
                                        </td>
                                    </tr>

                                    <tr>
                                        <th width="230px">{t('content.system')}</th>
                                        <td>
                                            <select className="custom-select  system-type-id" id="system-type-id" value={props.data.systemTypeId} disabled>
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
                                            <select className="custom-select  manager-id" id="manager-id" value={props.data.managerId} disabled>
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
                                            <select className="custom-select  area-id" id="area-id" value={props.data.areaId} disabled>
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
                                            <select className="custom-select  device-type-id" id="device-type-id" value={props.data.deviceType} disabled>
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
                                            <select className="custom-select  object-type-id" id="object-type-id" value={props.data.objectId} disabled>
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
                                        <th>{t('content.loadtype')}</th>
                                        <td>
                                            <select className="custom-select  system-type-id" name="load_type_id" id="load_type_id" value={formik.values.load_type_id} disabled>
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
                                    <tr className="powerDM">
                                        <th>{t('content.category.device.rated_power')}</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="pn" id="ac-power" value={formik.values.pn} disabled />
                                        </td>
                                    </tr>
                                    <tr className="in">
                                        <th id="lb-imccb">{t('content.category.device.rated_current')}</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="in" id="in" value={formik.values.in} disabled />
                                        </td>
                                    </tr>
                                    <tr className="vsc">
                                        <th>{t('content.category.device.secondary_voltage')}[V]</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="vsc" id="vsc" value={formik.values.vsc} disabled />
                                        </td>
                                    </tr>
                                    <tr className="vpr">
                                        <th>{t('content.category.device.primary_voltage')}[V]</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="vpr" id="vpr" value={formik.values.vpr} disabled />
                                        </td>
                                    </tr>
                                    <tr className="f">
                                        <th>{t('content.category.device.frequency')} [Hz]</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="f" id="f" value={formik.values.f} disabled />
                                        </td>
                                    </tr>
                                    <tr className="delta_p0">
                                        <th>{t('content.category.device.no_load_loss')} [kW]</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="delta_p0" id="delta_p0" value={formik.values.delta_p0} disabled />
                                        </td>
                                    </tr>
                                    <tr className="delta_pk">
                                        <th>{t('content.category.device.short_circuit_lost')} [kW]</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="delta_pk" id="delta_pk" value={formik.values.delta_pk} disabled />
                                        </td>
                                    </tr>
                                    <tr className="i0">
                                        <th id="lb-imccb">{t('content.category.device.current_load_loss')}(%)</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="i0" id="i0" value={formik.values.i0} disabled />
                                        </td>
                                    </tr>
                                    <tr className="un">
                                        <th>{t('content.category.device.short_circuit_voltage')}[%]</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="un" id="un" value={formik.values.un} disabled />
                                        </td>
                                    </tr>
                                    <tr className="m_oil">
                                        <th>{t('content.category.device.oil_weight')} [kg]</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="m_oil" id="m_oil" value={formik.values.m_oil} disabled />
                                        </td>
                                    </tr>
                                    <tr className="m_all">
                                        <th>{t('content.category.device.total_weight')} [kg]</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="m_all" id="m_all" value={formik.values.m_all} disabled />
                                        </td>
                                    </tr>
                                    <tr className="exp_oil stmv sgmv">
                                        <th>Oil exponent</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="exp_oil" id="exp_oil" value={formik.values.exp_oil} disabled />
                                        </td>
                                    </tr>
                                    <tr className="exp_wind stmv sgmv">
                                        <th>Winding exponent</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="exp_wind" id="exp_wind" value={formik.values.exp_wind} disabled />
                                        </td>
                                    </tr>
                                    <tr className="hot_spot_factor stmv sgmv">
                                        <th>Hot-spot factor</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="hot_spot_factor" id="hot_spot_factor" value={formik.values.hot_spot_factor} disabled />
                                        </td>
                                    </tr>
                                    <tr className="loss_ratio stmv sgmv">
                                        <th>Loss ratio</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="loss_ratio" id="loss_ratio" value={formik.values.loss_ratio} disabled />
                                        </td>
                                    </tr>
                                    <tr className="const_k11 stmv sgmv">
                                        <th>{t('content.category.device.constant')} K11</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="const_k11" d="const_k11" value={formik.values.const_k11} disabled />
                                        </td>
                                    </tr>
                                    <tr className="const_k21 stmv sgmv">
                                        <th>{t('content.category.device.constant')} K21</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="const_k21" d="const_k21" value={formik.values.const_k21} disabled />
                                        </td>
                                    </tr>
                                    <tr className="const_k22 stmv sgmv">
                                        <th>{t('content.category.device.constant')} K22</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="const_k22" d="const_k22" value={formik.values.const_k22} disabled />
                                        </td>
                                    </tr>
                                    <tr className="hot_spot_temp stmv sgmv">
                                        <th>Hot-spot temperature</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="hot_spot_temp" id="hot_spot_temp" value={formik.values.hot_spot_temp} disabled />
                                        </td>
                                    </tr>
                                    <tr className="hot_spot_gradient stmv sgmv">
                                        <th>Hot-spot to top-oil (in tank) gradient at rated current</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="hot_spot_gradient" id="hot_spot_gradient" value={formik.values.hot_spot_gradient} disabled />
                                        </td>
                                    </tr>
                                    <tr className="avg_oil_temp_rise stmv sgmv">
                                        <th>Average oil temperature rise</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="avg_oil_temp_rise" id="avg_oil_temp_rise" value={formik.values.avg_oil_temp_rise} disabled />
                                        </td>
                                    </tr>
                                    <tr className="top_oil_temp_rise stmv sgmv">
                                        <th>Top-oil (in tank) temperature rise</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="top_oil_temp_rise" id="top_oil_temp_rise" value={formik.values.top_oil_temp_rise} disabled />
                                        </td>
                                    </tr>
                                    <tr className="bottom_oil_temp_rise stmv sgmv">
                                        <th>Bottom oil temperature rise</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="bottom_oil_temp_rise" id="bottom_oil_temp_rise" value={formik.values.bottom_oil_temp_rise} disabled />
                                        </td>
                                    </tr>
                                    <tr className="const_time_oil stmv sgmv">
                                        <th>{t('content.category.device.oil_time_constant')}</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="const_time_oil" id="const_time_oil" value={formik.values.const_time_oil} disabled />
                                        </td>
                                    </tr>
                                    <tr className="const_time_winding stmv sgmv">
                                        <th>{t('content.category.device.coil_time_constant')}</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="const_time_winding" id="const_time_winding" value={formik.values.const_time_winding} disabled />
                                        </td>
                                    </tr>

                                    <tr className="inc">
                                        <th id="lb-inc">{t('content.category.device.neutral_rated_current')} [A]</th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="inc" id="inc" value={formik.values.inc} disabled />
                                        </td>
                                    </tr>
                                    <tr className="pdm">
                                        <th>{t('content.category.device.rated_power')} [kW]</th>
                                        <td>
                                            <input type="number" className="form-control" name="pdm" id="pdm" value={formik.values.pdm} disabled />
                                        </td>
                                    </tr>
                                    <tr className="uid">
                                        <th width="180px">UID<span className="required">※</span></th>
                                        <td>
                                            <input type="number" className="form-control input-number-m" name="uid" id="uid" value={formik.values.uid} disabled />

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
                                        <th>{t('content.start_time')}</th>
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

export default MeterMBA;