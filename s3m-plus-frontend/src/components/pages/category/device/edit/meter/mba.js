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
import SystemMapService from "../../../../../../services/SystemMapService";



const MeterMBA = (props) => {
    const initialValues = {
        deviceCode: props.device.deviceCode,
        deviceName: props.device.deviceName,
        deviceType: props.device.deviceType,
        systemTypeId: props.device.systemTypeId,
        address: props.device.address,
        latitude: props.device.latitude,
        longitude: props.device.longitude,
        projectId: props.device.projectId,
        customerId: props.device.customerId,
        managerId: props.device.managerId,
        areaId: props.device.areaId,
        objectId: props.device.objectId,
        location: props.device.location,
        uid: props.device.uid,
        load_type_id: props.device.load_type_id,
        manufacturer: props.device.manufacturer,
        model: props.device.model,
        vsc: props.device.vsc,
        vpr: props.device.vpr,
        f: props.device.f,
        delta_p0: props.device.delta_p0,
        delta_pk: props.device.delta_pk,
        i0: props.device.i0,
        un: props.device.un,
        m_oil: props.device.m_oil,
        m_all: props.device.m_all,
        exp_oil: props.device.exp_oil,
        exp_wind: props.device.exp_wind,
        hot_spot_factor: props.device.hot_spot_factor,
        loss_ratio: props.device.loss_ratio,
        const_k11: props.device.const_k11,
        const_k21: props.device.const_k21,
        const_k22: props.device.const_k22,
        hot_spot_temp: props.device.hot_spot_temp,
        hot_spot_gradient: props.device.hot_spot_gradient,
        avg_oil_temp_rise: props.device.avg_oil_temp_rise,
        top_oil_temp_rise: props.device.top_oil_temp_rise,
        bottom_oil_temp_rise: props.device.bottom_oil_temp_rise,
        const_time_oil: props.device.const_time_oil,
        const_time_winding: props.device.const_time_winding,
        pn: props.device.pn,
        in: props.device.in,
        inc: props.device.inc,
        work_date: props.device.work_date,
        description: props.device.description,
        reference_device_id: props.device.reference_device_id,
        work_time: '',
        priority_flag: props.device.priority_flag,
        pdm: props.device.pdm
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
        // let resObject = await ObjectService.getListObjectMst();
        let resObject = await ObjectService.getListObjectByDeviceTypeId(props.device.projectId, props.device.deviceTypeId);
        setObjects(resObject.data);
        let resLoadTypes = await LoadTypeService.listLoadTypeMst();
        setLoadTypes(resLoadTypes.data);
        let resDevice = await DeviceService.getDeviceGateway();
        setDeviceGateway(resDevice.data);
    }

    const closeModal = () => {
        let isCloseModal = false;
        props.parentCallback(isCloseModal)
    }

    const addDevice = () => {
        $.confirm({
            type: 'green',
            typeAnimated: true,
            icon: 'fa fa-success',
            title: "Thông báo",
            content: t('content.category.device.list.edit_success'),
            buttons: {
                confirm: {
                    text: "<i class=\"fa-solid fa-arrow-rotate-left\"></i>" + t('content.category.device.list.title'),
                    action: async function () {
                        history.push({
                            pathname: "/category/device",
                            state: {
                                status: 200,
                                message: "UPDATE_SUCCESS"
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
            let res = await DeviceService.updateDevice(props.id, data);
            if (res.status === 200) {
                addDevice();
                let deviceRes = await DeviceService.detailsDevice(props.id);
                if (deviceRes.data.systemMapId != null) {
                    let data = await SystemMapService.getSystemMap(deviceRes.data.systemMapId);
                    let systemMapsResponse = data.data

                    let systemMapJsonData = JSON.parse(systemMapsResponse.jsonData);

                    if (systemMapJsonData != null && systemMapJsonData != "") {
                        for (let i = 0; i < systemMapJsonData.aCards.length; i++) {
                            if (typeof systemMapJsonData.aCards[i].deviceId !== "undefined" && systemMapJsonData.aCards[i].deviceId != "") {
                                if (Number(systemMapJsonData.aCards[i].deviceId) == Number(props.id)) {
                                    systemMapJsonData.aCards[i].deviceName = deviceRes.data.deviceName
                                }
                            }

                        }
                    }

                    systemMapsResponse.jsonData = JSON.stringify(systemMapJsonData);
                    await SystemMapService.updateSystemDevice(systemMapsResponse);
                }
            } else if (res.status === 400) {
                setErrorsValidate(res.data.errors);

            } else if (res.status === 500) {
                setError(t('validate.customer.INSERT_FAILED'));

            }
        }
    })


    useEffect(() => {
        getInfoAdd();
    }, [])

    return (
        <>
            {
                (error != null) &&
                <div className="alert alert-danger" role="alert">
                    <p className="m-0 p-0">{t('content.category.device.add.error_edit')}</p>
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

            <form onSubmit={formik.handleSubmit}>
                <table className={`table table-input `}>
                    <tbody>
                        <tr>
                            <th width="180px">{t('content.customer')} </th>
                            <td>
                                <select className="custom-select block customer-id" id="customer-id" value={props.device.customerId} disabled>
                                    {customer?.length > 0 && customer?.map((c, index) => {
                                        return <option key={index + 1} value={c.customerId}>{c.customerName}</option>
                                    })}
                                </select>
                            </td>
                        </tr>

                        <tr>
                            <th width="180px">{t('content.project')}  </th>
                            <td>
                                <select className="custom-select block project-id" id="project-id" value={props.device.projectId} disabled>
                                    {projects?.length > 0 && projects?.map((p, index) => {
                                        return <option key={index + 1} value={p.projectId}>{p.projectName}</option>
                                    })}
                                </select>
                            </td>
                        </tr>

                        <tr>
                            <th width="230px">{t('content.system')}  </th>
                            <td>
                                <select className="custom-select block system-type-id" id="system-type-id" value={props.device.systemTypeId} disabled>
                                    {
                                        systemTypes?.length > 0 && systemTypes?.map((systemType, index) => {
                                            return <option key={index + 1} value={systemType.systemTypeId}>{systemType.systemTypeName}</option>
                                        })
                                    }
                                </select>
                            </td>
                        </tr>
                        <tr>
                            <th width="180px">{t('content.country')}  </th>
                            <td>
                                <select className="custom-select block country-id" id="country-id" disabled>
                                    <option value="84">Việt Nam</option>
                                </select>
                            </td>
                        </tr>
                        <tr>
                            <th width="180px">{t('content.manager')}  </th>
                            <td>
                                <select className="custom-select block manager-id" id="manager-id" value={props.device.managerId} disabled>
                                    {
                                        managers?.length > 0 && managers?.map((manager, index) => {
                                            return <option key={index + 1} value={manager.managerId}>{manager.managerName}</option>
                                        })
                                    }
                                </select>
                            </td>
                        </tr>
                        <tr>
                            <th width="180px">{t('content.area')}  </th>
                            <td>
                                <select className="custom-select block area-id" id="area-id" value={props.device.areaId} disabled>
                                    {areas?.length > 0 && areas?.map((area, index) => {
                                        return <option key={index + 1} value={area.areaId}>{area.areaName}</option>
                                    })}
                                </select>
                            </td>
                        </tr>
                        <tr className="address">
                            <th>{t('content.address')} </th>
                            <td>
                                <input type="text" className="form-control" name="address" id="address" onChange={formik.handleChange} defaultValue={props.device.address} />
                            </td>
                        </tr>
                        <tr>
                            <th width="300px">{t('content.super_manager')} </th>
                            <td>
                                <input type="text" className="form-control area" name="location" onChange={formik.handleChange} defaultValue={props.device.location} />
                            </td>
                        </tr>
                        <tr className="longitude">
                            <th>{t('content.longitude')} </th>
                            <td>
                                <input type="number" step="0.0000000001" className="form-control input-number-m" name="longitude" id="longitude" onChange={formik.handleChange} defaultValue={props.device.longitude} />
                            </td>
                        </tr>
                        <tr className="latitude">
                            <th>{t('content.latitude')} </th>
                            <td>
                                <input type="number" step="0.0000000001" className="form-control input-number-m" name="latitude" id="latitude" onChange={formik.handleChange} defaultValue={props.device.latitude} />
                            </td>
                        </tr>
                        <tr className="device-name" >
                            <th width="180px">{t('content.category.device.lable_device_name')}<span className="required">※</span></th>
                            <td>
                                <input type="text" onChange={formik.handleChange} className="form-control" name="deviceName" id="deviceName" defaultValue={props.device.deviceName} />
                            </td>
                        </tr>
                        <tr>
                            <th width="180px">{t('content.category.device.lable_device_type')}</th>
                            <td>
                                <select className="custom-select block device-type-id" id="device-type-id" onChange={formik.handleChange} value={props.device.deviceTypeId} disabled>
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
                                <select className="custom-select block object-type-id" id="objectId" onChange={formik.handleChange} value={formik.values.objectId}>
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
                                <input type="text" className="form-control" name="deviceCode" id="device-code" value={props.device.deviceCode} disabled />
                            </td>
                        </tr>
                        <tr>
                            <th>{t('content.load_type')}</th>
                            <td>
                                <select className="custom-select block system-type-id" name="load_type_id" id="load_type_id" value={formik.values.load_type_id} onChange={formik.handleChange}>
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
                                <input type="text" className="form-control" name="manufacturer" id="manufacturer" defaultValue={formik.values.manufacturer} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="model">
                            <th>Model</th>
                            <td>
                                <input type="text" className="form-control" name="model" id="model" defaultValue={formik.values.model} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="powerDM">
                            <th>{t('content.category.device.rated_power')} [kVA]</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="pn" id="ac-power" onChange={formik.handleChange} defaultValue={formik.values.pn} />
                            </td>
                        </tr>
                        <tr className="in">
                            <th id="lb-imccb">{t('content.category.device.rated_current')} [A]</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="in" id="in" onChange={formik.handleChange} defaultValue={formik.values.in} />
                            </td>
                        </tr>
                        <tr className="vsc">
                            <th>{t('content.category.device.primary_voltage')}[V]</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="vsc" id="vsc" onChange={formik.handleChange} defaultValue={formik.values.vsc} />
                            </td>
                        </tr>
                        <tr className="vpr">
                            <th>{t('content.category.device.secondary_voltage')}[V]</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="vpr" id="vpr" onChange={formik.handleChange} defaultValue={formik.values.vpr} />
                            </td>
                        </tr>
                        <tr className="f">
                            <th>{t('content.category.device.frequency')} [Hz]</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="f" id="f" onChange={formik.handleChange} defaultValue={formik.values.f} />
                            </td>
                        </tr>
                        <tr className="delta_p0">
                            <th>{t('content.category.device.no_load_loss')} [kW]</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="delta_p0" id="delta_p0" onChange={formik.handleChange} defaultValue={formik.values.delta_p0} />
                            </td>
                        </tr>
                        <tr className="delta_pk">
                            <th>{t('content.category.device.short_circuit_loss')} [kW]</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="delta_pk" id="delta_pk" onChange={formik.handleChange} defaultValue={formik.values.delta_pk} />
                            </td>
                        </tr>
                        <tr className="i0">
                            <th id="lb-imccb">{t('content.category.device.current_load_loss')}(%)</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="i0" id="i0" onChange={formik.handleChange} defaultValue={formik.values.i0} />
                            </td>
                        </tr>
                        <tr className="un">
                            <th>{t('content.category.device.short_circuit_voltage')} [%]</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="un" id="un" onChange={formik.handleChange} defaultValue={formik.values.un} />
                            </td>
                        </tr>
                        <tr className="m_oil">
                            <th>{t('content.category.device.oil_weight')} [kg]</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="m_oil" id="m_oil" onChange={formik.handleChange} defaultValue={formik.values.m_oil} />
                            </td>
                        </tr>
                        <tr className="m_all">
                            <th>{t('content.category.device.total_weight')} [kg]</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="m_all" id="m_all" onChange={formik.handleChange} defaultValue={formik.values.m_all} />
                            </td>
                        </tr>
                        <tr className="exp_oil stmv sgmv">
                            <th>Oil exponent</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="exp_oil" id="exp_oil" onChange={formik.handleChange} defaultValue={formik.values.exp_oil} />
                            </td>
                        </tr>
                        <tr className="exp_wind stmv sgmv">
                            <th>Winding exponent</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="exp_wind" id="exp_wind" onChange={formik.handleChange} defaultValue={formik.values.exp_wind} />
                            </td>
                        </tr>
                        <tr className="hot_spot_factor stmv sgmv">
                            <th>Hot-spot factor</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="hot_spot_factor" id="hot_spot_factor" onChange={formik.handleChange} defaultValue={formik.values.hot_spot_factor} />
                            </td>
                        </tr>
                        <tr className="loss_ratio stmv sgmv">
                            <th>Loss ratio</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="loss_ratio" id="loss_ratio" onChange={formik.handleChange} defaultValue={formik.values.loss_ratio} />
                            </td>
                        </tr>
                        <tr className="const_k11 stmv sgmv">
                            <th>{t('content.category.device.constant')} K11</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="const_k11" d="const_k11" onChange={formik.handleChange} defaultValue={formik.values.const_k11} />
                            </td>
                        </tr>
                        <tr className="const_k21 stmv sgmv">
                            <th>{t('content.category.device.constant')} K21</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="const_k21" d="const_k21" onChange={formik.handleChange} defaultValue={formik.values.const_k21} />
                            </td>
                        </tr>
                        <tr className="const_k22 stmv sgmv">
                            <th>{t('content.category.device.constant')} K22</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="const_k22" d="const_k22" onChange={formik.handleChange} defaultValue={formik.values.const_k22} />
                            </td>
                        </tr>
                        <tr className="hot_spot_temp stmv sgmv">
                            <th>Hot-spot temperature</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="hot_spot_temp" id="hot_spot_temp" onChange={formik.handleChange} defaultValue={formik.values.hot_spot_temp} />
                            </td>
                        </tr>
                        <tr className="hot_spot_gradient stmv sgmv">
                            <th>Hot-spot to top-oil (in tank) gradient at rated current</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="hot_spot_gradient" id="hot_spot_gradient" onChange={formik.handleChange} defaultValue={formik.values.hot_spot_gradient} />
                            </td>
                        </tr>
                        <tr className="avg_oil_temp_rise stmv sgmv">
                            <th>Average oil temperature rise</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="avg_oil_temp_rise" id="avg_oil_temp_rise" onChange={formik.handleChange} defaultValue={formik.values.avg_oil_temp_rise} />
                            </td>
                        </tr>
                        <tr className="top_oil_temp_rise stmv sgmv">
                            <th>Top-oil (in tank) temperature rise</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="top_oil_temp_rise" id="top_oil_temp_rise" onChange={formik.handleChange} defaultValue={formik.values.top_oil_temp_rise} />
                            </td>
                        </tr>
                        <tr className="bottom_oil_temp_rise stmv sgmv">
                            <th>Bottom oil temperature rise</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="bottom_oil_temp_rise" id="bottom_oil_temp_rise" onChange={formik.handleChange} defaultValue={formik.values.bottom_oil_temp_rise} />
                            </td>
                        </tr>
                        <tr className="const_time_oil stmv sgmv">
                            <th>{t('content.category.device.oil_time_constant')}</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="const_time_oil" id="const_time_oil" onChange={formik.handleChange} defaultValue={formik.values.const_time_oil} />
                            </td>
                        </tr>
                        <tr className="const_time_winding stmv sgmv">
                            <th>{t('content.category.device.coil_time_constant')}</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="const_time_winding" id="const_time_winding" onChange={formik.handleChange} defaultValue={formik.values.const_time_winding} />
                            </td>
                        </tr>

                        <tr className="inc">
                            <th id="lb-inc">{t('content.category.device.neutral_rated_current')}[A]</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="inc" id="inc" onChange={formik.handleChange} defaultValue={formik.values.inc} />
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
                                <input type="number" className="form-control input-number-m" name="uid" id="uid" onChange={formik.handleChange} defaultValue={formik.values.uid} />

                            </td>
                        </tr>
                        <tr className="reference_device_id">
                            <th>{t('content.category.device.connect_device')}</th>
                            <td>
                                <select className="custom-select block system-type-id" name="reference_device_id" id="reference_device_id" defaultValue={formik.values.reference_device_id} >
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
                                <select className="custom-select  system-type-id" name="priority_flag" id="priority_flag" defaultValue={formik.values.priority_flag} onChange={formik.handleChange}>
                                    <option value={1}>{t('content.category.device.low')}</option>
                                    <option value={2}>{t('content.category.device.normal')}</option>
                                    <option value={3}>{t('content.category.device.high')}</option>
                                </select>
                            </td>
                        </tr>
                        <tr>
                            <th>{t('content.description')}</th>
                            <td>
                                <input type="text" className="form-control" name="description" onChange={formik.handleChange} defaultValue={formik.values.description} />
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
                <div id="main-button">
                    <button type="submit" className="btn btn-outline-secondary btn-agree mr-1 mb-3">
                        <i className="fa-solid fa-check"></i>
                    </button>
                    <button type="button" className="btn btn-outline-secondary btn-cancel mb-3" onClick={() => {
                        // history.push("/category/device")
                        closeModal();
                    }}>
                        <i className="fa-solid fa-xmark"></i>
                    </button>
                </div>
            </form>

        </>
    )
}

export default MeterMBA;