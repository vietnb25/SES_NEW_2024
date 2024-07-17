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
import SystemMapService from "../../../../../../services/SystemMapService";


const MeterInverter = (props) => {

    const initialValues = {
        deviceCode: props.device.deviceCode,
        deviceName: props.device.deviceName,
        deviceTypeId: props.device.deviceTypeId,
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
        model: props.device.model,
        dbId: props.device.dbId,
        description: props.device.description,
        manufacturer: props.device.manufacturer,
        pdc_max: props.device.pdc_max,
        vdc_max: props.device.vdc_max,
        vdc_rate: props.device.vdc_rate,
        vac_rate: props.device.vac_rate,
        idc_max: props.device.idc_max,
        iac_rate: props.device.iac_rate,
        iac_max: props.device.iac_max,
        pac: props.device.pac,
        eff: props.device.eff,
        reference_device_id: props.device.reference_device_id,
        work_date: props.device.work_date,
        priority_flag: props.device.priority_flag,
        pdm: props.device.pdm
    }
    const [systemTypes, setSystemTypes] = useState([]);
    const [deviceTypes, setDeviceTypes] = useState([]);
    const [cutomers, setCustomers] = useState([]);
    const [areas, setAreas] = useState([]);
    const [deviceGateway, setDeviceGateway] = useState([]);
    const [managers, setManagers] = useState([]);
    const [projects, setProjects] = useState([]);
    const { t } = useTranslation();
    const [errorsValidate, setErrorsValidate] = useState([]);
    const [error, setError] = useState(null);
    const [customer, setCustomer] = useState([]);
    const [objects, setObjects] = useState([]);
    const [isShowComponents, setIsShowComponents] = useState(false);

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
                            <th>{t('content.category.device.lable_manufacture')}</th>
                            <td>
                                <input type="text" className="form-control" name="manufacturer" id="manufacturer" onChange={formik.handleChange} defaultValue={formik.values.manufacturer} />
                            </td>
                        </tr>
                        <tr className="model">
                            <th>Model</th>
                            <td>
                                <input type="text" className="form-control" name="model" id="model" value={formik.values.model} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="pdc_max">
                            <th>{t('content.category.device.dc_capacity_max')}</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="pdc_max" id="dc-power" value={formik.values.pdc_max} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="vdc_max">
                            <th id="lb-imccb">{t('content.category.device.dc_input_voltage_max')}</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="vdc_max" id="vdc_max" value={formik.values.vdc_max} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="vdc_rate">
                            <th id="lb-imccb">{t('content.category.device.rated_dc_input_voltage')}</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="vdc_rate" id="vdc_rate" value={formik.values.vdc_rate} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="idc_max">
                            <th id="lb-imccb">{t('content.category.device.input_current_dc_max')}</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="idc_max" id="idc_max" value={formik.values.idc_max} onChange={formik.handleChange} />
                            </td>
                        </tr>

                        <tr className="pac">
                            <th>{t('content.category.device.rated_ac_output_power')}</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="pac" id="ac-power" value={formik.values.pac} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="iac_rate">
                            <th id="lb-imccb">{t('content.category.device.rated_ac_output_current')}</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="iac_rate" id="iac_rate" value={formik.values.iac_rate} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="iac_max">
                            <th id="lb-imccb">{t('content.category.device.max_ac_output_current')}</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="iac_max" id="iac_max" value={formik.values.iac_max} onChange={formik.handleChange} />
                            </td>
                        </tr>

                        <tr className="vac_rate">
                            <th id="lb-imccb">{t('content.category.device.rated_ac_voltage')}</th>
                            <td>
                                <input type="number" className="form-control input-number-m" name="vac_rate" id="vac_rate" value={formik.values.vac_rate} onChange={formik.handleChange} />
                            </td>
                        </tr>
                        <tr className="eff string">
                            <th>{t('content.category.device.efficiency')}</th>
                            <td>
                                <input type="number" className="form-control" name="eff" id="eff" value={formik.values.eff} onChange={formik.handleChange} />
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
                                <input type="number" className="form-control input-number-m" name="uid" id="uid" value={formik.values.uid} onChange={formik.handleChange} />

                            </td>
                        </tr>
                        <tr className="reference_device_id">
                            <th>{t('content.category.device.connect_device')}</th>
                            <td>
                                <select className="custom-select block system-type-id" name="reference_device_id" id="reference_device_id" defaultValue={formik.values.reference_device_id} onChange={formik.handleChange}>
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
                                <input type="text" className="form-control" name="description" value={formik.values.description} onChange={formik.handleChange} />
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

export default MeterInverter;