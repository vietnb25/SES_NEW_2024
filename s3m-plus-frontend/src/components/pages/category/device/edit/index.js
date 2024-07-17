import { useFormik } from "formik";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from 'react-router-dom';
import DeviceService from "../../../../../services/DeviceService";
import CablesService from "../../../../../services/CablesService";
import * as Yup from "yup";
import "./index.css";
import CONS from "../../../../../constants/constant";
import { MultiSelect } from 'primereact/multiselect';
import moment from 'moment';
import { Calendar } from 'primereact/calendar';
import ObjectService from "../../../../../services/ObjectService";
import DeviceTypeService from "../../../../../services/DeviceTypeService";
import { logDOM } from "@testing-library/react";
import MeterMBA from "./meter/mba";
import MeterTDHT from "./meter/tdht";
import MeterAny from "./meter/any";
import Gateway from "./gateway";
import CambienAny from "./cambien";
import MeterTTT from "./meter/ttt";
import MeterInverter from "./meter/inverter";
import StringPV from "./string";
import ObjectTypeService from "../../../../../services/ObjectTypeService";
import SystemMapService from "../../../../../services/SystemMapService";
const $ = window.$;

const EditDevice = (props) => {
    const history = useHistory();
    const id = props.deviceId;
    const { t } = useTranslation();
    const [cables, setCables] = useState([]);
    const [errorsValidate, setErrorsValidate] = useState([]);
    const [error, setError] = useState(null);
    const [obj, setObj] = useState(true);
    const [objectTypeId, setObjectTypeId] = useState(0);

    const [device, setDevice] = useState({
        deviceId: 0,
        deviceCode: '',
        deviceName: '',
        deviceTypeId: 0,
        deviceTypeName: null,
        customerId: 0,
        systemMapId: 0,
        superManagerId: 0,
        systemTypeId: 0,
        managerId: 0,
        areaId: 0,
        projectId: 0,
        objectId: 0,
        address: '',
        latitude: '',
        longitude: '',
        priority_flag: '',
        location: '',
        load_type_id: 0,
        manufacturer: '',
        model: '',
        pn: 0,
        in: 0,
        vsc: 0,
        vpr: 0,
        f: 0,
        delta_p0: 0,
        delta_pk: 0,
        i0: 0,
        un: 0,
        m_oil: 0,
        m_all: 0,
        exp_oil: 0,
        exp_wind: 0,
        hot_spot_factor: 0,
        loss_ratio: 0,
        const_k11: 0,
        const_k21: 0,
        const_k22: 0,
        hot_spot_temp: 0,
        hot_spot_gradient: 0,
        avg_oil_temp_rise: 0,
        top_oil_temp_rise: 0,
        bottom_oil_temp_rise: 0,
        const_time_oil: 0,
        const_time_winding: 0,
        vn: 0,
        cable_length: 0,
        rho: 0,
        inc: 0,
        pdc_max: 0,
        vdc_max: 0,
        vdc_rate: 0,
        vac_rate: 0,
        idc_max: 0,
        iac_rate: 0,
        iac_max: 0,
        pac: 0,
        eff: 0,
        p_max: 0,
        vmp: 0,
        imp: 0,
        voc: 0,
        isc: 0,
        gstc: 0,
        tstc: 0,
        gnoct: 0,
        tnoct: 0,
        cp_max: 0,
        cvoc: 0,
        cisc: 0,
        ns: 0,
        sensor_radiation_id: 0,
        sensor_temperature_id: 0,
        sim_no: '',
        battery_capacity: 0,
        work_date: '',
        reference_device_id: 0,
        uid: '',
        db_id: '',
        delete_flag: 0,
        description: '',
        fuelTypeId: 0
    });


    const getDevice = async () => {
        let response = await DeviceService.detailsDevice(id);
        if (response.status === 200) {
            setDevice(response.data);
        }
    }

    const getObjectTypeIdById = async (id) => {
        let res = await ObjectTypeService.getObjectTypeIdById(id);
        if (res.status === 200) {
            setObjectTypeId(res.data.objectTypeId);
        }
    }

    const callbackFunction = (isCloseModal) => {
        props.parentCloseModal(isCloseModal)
    }

    const changeFormByCondition = () => {
        let systemType = device.systemTypeId;
        let deviceType = device.deviceTypeId;
        let typeObject = device.objectId;
        getObjectTypeIdById(typeObject);
        if (systemType == 1 || systemType == 3 || systemType == 4) {
            if (deviceType == 1) {
                if (objectTypeId == 1) {
                    // return props.isActive != undefined ? <MeterMBA data={data}></MeterMBA> : <></>
                    return <MeterMBA parentCallback={callbackFunction} id={id} device={device}></MeterMBA>
                } else if (objectTypeId == 2) {
                    return <MeterTDHT parentCallback={callbackFunction} id={id} device={device}></MeterTDHT>
                } else {
                    return <MeterAny parentCallback={callbackFunction} id={id} device={device}></MeterAny>
                }
            } else if (deviceType == 9) {
                return <Gateway parentCallback={callbackFunction} id={id} device={device}></Gateway>
            } else {
                return <CambienAny parentCallback={callbackFunction} id={id} device={device}></CambienAny>
            }
        } else if (systemType == 2) {
            if (deviceType == 1) {
                if (objectTypeId == 1) {
                    return <MeterMBA parentCallback={callbackFunction} id={id} device={device}></MeterMBA>
                } else if (objectTypeId == 2) {
                    return <MeterTDHT parentCallback={callbackFunction} id={id} device={device}></MeterTDHT>
                } else if (objectTypeId == 21) {
                    return <MeterInverter parentCallback={callbackFunction} id={id} device={device}></MeterInverter>
                } else {
                    return <MeterAny parentCallback={callbackFunction}></MeterAny>
                }
            } else if (deviceType == 2) {
                return <MeterInverter parentCallback={callbackFunction} id={id} device={device}></MeterInverter>
            } else if (deviceType == 9) {
                return <Gateway parentCallback={callbackFunction} id={id} device={device}></Gateway>
            } else if (deviceType == 16) {
                return <StringPV parentCallback={callbackFunction} id={id} device={device}></StringPV>
            } else {
                return <CambienAny parentCallback={callbackFunction} id={id} device={device}></CambienAny>
            }
        } else if (systemType == 5) {
            if (deviceType == 1) {
                if (objectTypeId == 1) {
                    return <MeterMBA parentCallback={callbackFunction} id={id} device={device}></MeterMBA>
                } else if (objectTypeId == 2) {
                    return <MeterTDHT parentCallback={callbackFunction} id={id} device={device}></MeterTDHT>
                } else if (objectTypeId == 20) {
                    return <MeterTTT parentCallback={callbackFunction} id={id} device={device}></MeterTTT>
                } else {
                    return <MeterAny parentCallback={callbackFunction} id={id} device={device}></MeterAny>
                }
            } else if (deviceType == 9) {
                return <Gateway parentCallback={callbackFunction} id={id} device={device}></Gateway>
            } else {
                return <CambienAny parentCallback={callbackFunction} id={id} device={device}></CambienAny>
            }
        }

    }
    const formik = useFormik({
        enableReinitialize: true,
        initialValues: device, validationSchema: Yup.object().shape({
            deviceName: Yup.string().required(t('validate.device.DEVICE_NAME_NOT_BLANK')).max(100, t('validate.device.DEVICE_NAME_MAX_SIZE_ERROR')),
            model: Yup.string().max(255, t('validate.device.MODEL_MAX_SIZE_ERROR')).nullable(),
            series_cell: Yup.number().min(1, t('validate.device.SERIES_CELL_MIN_VALUE_ERROR')).max(512, t('validate.device.SERIES_CELL_MAX_VALUE_ERROR')).nullable(true),
            parallel_cell: Yup.number().min(1, t('validate.device.PARALLEL_CELL_MIN_VALUE_ERROR')).max(512, t('validate.device.PARALLEL_CELL_MAX_VALUE_ERROR')).nullable(true),
            series_modul: Yup.number().min(1, t('validate.device.SERIES_MODUL_MIN_VALUE_ERROR')).max(512, t('validate.device.SERIES_MODUL_MAX_VALUE_ERROR')).nullable(true),
            parallel_modul: Yup.number().min(1, t('validate.device.PARALLEL_MODUL_MIN_VALUE_ERROR')).max(512, t('validate.device.PARALLEL_MODUL_MAX_VALUE_ERROR')).nullable(true),
            isc: Yup.number().min(0, t('validate.device.ISCO_MIN_VALUE_ERROR')).max(128, t('validate.device.ISCO_MAX_VALUE_ERROR')).nullable(true),
            voc: Yup.number().min(0, t('validate.device.VOCO_MIN_VALUE_ERROR')).max(128, t('validate.device.VOCO_MAX_VALUE_ERROR')).nullable(true),
            imp: Yup.number().min(0, t('validate.device.IMPO_MIN_VALUE_ERROR')).max(128, t('validate.device.IMPO_MAX_VALUE_ERROR')).nullable(true),
            aisc: Yup.number().min(-16, t('validate.device.ALPHA_ISC_MIN_VALUE_ERROR')).max(16, t('validate.device.ALPHA_ISC_MAX_VALUE_ERROR')).nullable(true),
            aimp: Yup.number().min(-16, t('validate.device.ISC_IMP_MIN_VALUE_ERROR')).max(16, t('validate.device.ISC_IMP_MAX_VALUE_ERROR')).nullable(true),
            c0: Yup.number().min(-16, t('validate.device.C0_MIN_VALUE_ERROR')).max(16, t('validate.device.C0_MAX_VALUE_ERROR')).nullable(true),
            c1: Yup.number().min(-16, t('validate.device.C1_MIN_VALUE_ERROR')).max(16, t('validate.device.C1_MAX_VALUE_ERROR')).nullable(true),
            bvoc: Yup.number().min(-16, t('validate.device.BETA_VOC_MIN_VALUE_ERROR')).max(16, t('validate.device.BETA_VOC_MAX_VALUE_ERROR')).nullable(true),
            mbvoc: Yup.number().min(-16, t('validate.device.MUYB_VOC_MIN_VALUE_ERROR')).max(16, t('validate.device.MUYB_VOC_MAX_VALUE_ERROR')).nullable(true),
            bvmp: Yup.number().min(-16, t('validate.device.BETA_VMP_MIN_VALUE_ERROR')).max(16, t('validate.device.BETA_VMP_MAX_VALUE_ERROR')).nullable(true),
            mbvmp: Yup.number().min(-16, t('validate.device.MUYB_VMP_MIN_VALUE_ERROR')).max(16, t('validate.device.MUYB_VMP_MAX_VALUE_ERROR')).nullable(true),
            n: Yup.number().min(0, t('validate.device.N_MIN_VALUE_ERROR')).max(8, t('validate.device.N_MAX_VALUE_ERROR')).nullable(true),
            c2: Yup.number().min(-16, t('validate.device.C2_MIN_VALUE_ERROR')).max(16, t('validate.device.C2_MAX_VALUE_ERROR')).nullable(true),
            c3: Yup.number().min(-16, t('validate.device.C3_MIN_VALUE_ERROR')).max(16, t('validate.device.C3_MAX_VALUE_ERROR')).nullable(true),
            dtc: Yup.number().min(-16, t('validate.device.DTC_MIN_VALUE_ERROR')).max(16, t('validate.device.ALPHA_ISC_MAXDTC_MAX_VALUE_ERROR_VALUE_ERROR')).nullable(true),
            fd: Yup.number().min(-16, t('validate.device.FD_MIN_VALUE_ERROR')).max(16, t('validate.device.FD_MAX_VALUE_ERROR')).nullable(true),
            a: Yup.number().min(-16, t('validate.device.A_MIN_VALUE_ERROR')).max(16, t('validate.device.A_MAX_VALUE_ERROR')).nullable(true),
            b: Yup.number().min(-16, t('validate.device.B_MIN_VALUE_ERROR')).max(16, t('validate.device.B_MAX_VALUE_ERROR')).nullable(true),
            c4: Yup.number().min(-16, t('validate.device.C4_MIN_VALUE_ERROR')).max(16, t('validate.device.C4_MAX_VALUE_ERROR')).nullable(true),
            c5: Yup.number().min(-16, t('validate.device.C5_MIN_VALUE_ERROR')).max(16, t('validate.device.C5_MAX_VALUE_ERROR')).nullable(true),
            ix: Yup.number().min(0, t('validate.device.IX_MIN_VALUE_ERROR')).max(128, t('validate.device.IX_MAX_VALUE_ERROR')).nullable(true),
            ixx: Yup.number().min(0, t('validate.device.IXX_MIN_VALUE_ERROR')).max(128, t('validate.device.IXX_MAX_VALUE_ERROR')).nullable(true),
            c6: Yup.number().min(-16, t('validate.device.C6_MIN_VALUE_ERROR')).max(16, t('validate.device.C6_MAX_VALUE_ERROR')).nullable(true),
            c7: Yup.number().min(-16, t('validate.device.C7_MIN_VALUE_ERROR')).max(16, t('validate.device.C7_MAX_VALUE_ERROR')).nullable(true),
            e0: Yup.number().min(0, t('validate.device.E0_MIN_VALUE_ERROR')).max(10000, t('validate.device.E0_MAX_VALUE_ERROR')).nullable(true),
            t0: Yup.number().min(-100, t('validate.device.T0_MIN_VALUE_ERROR')).max(100, t('validate.device.T0_MAX_VALUE_ERROR')).nullable(true),
            airmass: Yup.number().min(0, t('validate.device.AIRMASS_MIN_VALUE_ERROR')).max(10000, t('validate.device.AIRMASS_MAX_VALUE_ERROR')).nullable(true),
            aoi: Yup.number().min(0, t('validate.device.P_DIFFUSE_MIN_VALUE_ERROR')).max(100, t('validate.device.AOI_MAX_VALUE_ERROR')).nullable(true),
            p_diffuse: Yup.number().min(0, t('validate.device.IX_MIN_VALUE_ERROR')).max(100, t('validate.device.P_DIFFUSE_VALUE_ERROR')).nullable(true),
            adeg: Yup.number().min(0, t('validate.device.ADEG_MIN_VALUE_ERROR')).max(1, t('validate.device.ADEG_MAX_VALUE_ERROR')).nullable(true),
            pmpo: Yup.number().min(0, t('validate.device.PMPO_MIN_VALUE_ERROR')).max(2000, t('validate.device.PMPO_MAX_VALUE_ERROR')).nullable(true),
            apmp: Yup.number().min(-16, t('validate.device.APMP_MIN_VALUE_ERROR')).max(16, t('validate.device.APMP_MAX_VALUE_ERROR')).nullable(true),
            tempNOCT: Yup.number().min(-128, t('validate.device.TEMP_NOCT_MIN_VALUE_ERROR')).max(128, t('validate.device.TEMP_NOCT_MAX_VALUE_ERROR')).nullable(true),
            eff0: Yup.number().min(0, t('validate.device.EFF0_MIN_VALUE_ERROR')).max(100, t('validate.device.EFF0_MAX_VALUE_ERROR')).nullable(true),
            s: Yup.number().min(0, t('validate.device.S_MIN_VALUE_ERROR')).max(100, t('validate.device.S_MAX_VALUE_ERROR')).nullable(true)
        }),
        onSubmit: async data => {
            if (obj == false) {
                data.objectName = '';
            }
            let res = await DeviceService.updateDevice(id, data);
            // sendData({ project: data.projectId, customer: data.customerId })
            if (res.status === 400) {
                setErrorsValidate(res.data.errors);
                return
            }
            if (res.status === 200) {
                history.push({
                    pathname: "/category/device",
                    state: {
                        status: 200,
                        message: "UPDATE_SUCCESS"
                    }
                });
            } else {
                setError("Lỗi phát sinh khi thêm thiết bị mới!")
            }
        }
    });

    useEffect(() => {
        document.title = t('content.category.device.edit.title')
        getDevice();
    }, [id])

    return (
        <>
            <div id="page-body">
                <div id="main-title">
                    <h5 className="d-block mb-0 float-left"><i className="fas fa-server"></i> &nbsp;{t('content.category.device.edit.header')}</h5>
                </div>
                {/* {
                    (error != null) &&
                    <div className="alert alert-danger" role="alert">
                        <p className="m-0 p-0">Lỗi phát sinh khi chỉnh sửa thiết bị!</p>
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
                {
                    ((formik.errors.deviceName && formik.touched.deviceName) ||
                        (formik.errors.longitude && formik.touched.longitude) ||
                        (formik.errors.ip && formik.touched.ip) ||
                        (formik.errors.simNo && formik.touched.simNo) ||
                        (formik.errors.model && formik.touched.model) ||
                        (formik.errors.uid && formik.touched.uid) ||
                        (formik.errors.latitude && formik.touched.latitude) ||
                        (formik.errors.c0 && formik.touched.c0) ||
                        (formik.errors.c1 && formik.touched.c1) ||
                        (formik.errors.c2 && formik.touched.c2) ||
                        (formik.errors.c3 && formik.touched.c3) ||
                        (formik.errors.c4 && formik.touched.c4) ||
                        (formik.errors.c5 && formik.touched.c5) ||
                        (formik.errors.c6 && formik.touched.c6) ||
                        (formik.errors.c7 && formik.touched.c7) ||
                        (formik.errors.imp && formik.touched.imp) ||
                        (formik.errors.isc && formik.touched.isc) ||
                        (formik.errors.ix && formik.touched.ix) ||
                        (formik.errors.ixx && formik.touched.ixx) ||
                        (formik.errors.p_diffuse && formik.touched.p_diffuse) ||
                        (formik.errors.t0 && formik.touched.t0) ||
                        (formik.errors.voc && formik.touched.voc) ||
                        (formik.errors.a && formik.touched.a) ||
                        (formik.errors.aimp && formik.touched.aimp) ||
                        (formik.errors.aisc && formik.touched.aisc) ||
                        (formik.errors.aoi && formik.touched.aoi) ||
                        (formik.errors.b && formik.touched.b) ||
                        (formik.errors.bvmp && formik.touched.bvmp) ||
                        (formik.errors.bvoc && formik.touched.bvoc) ||
                        (formik.errors.dtc && formik.touched.dtc) ||
                        (formik.errors.e0 && formik.touched.e0) ||
                        (formik.errors.fd && formik.touched.fp) ||
                        (formik.errors.k && formik.touched.k) ||
                        (formik.errors.mbvmp && formik.touched.mbvmp) ||
                        (formik.errors.mbvoc && formik.touched.mbvoc) ||
                        (formik.errors.n && formik.touched.n) ||
                        (formik.errors.parallel_cell && formik.touched.parallel_cell) ||
                        (formik.errors.q && formik.touched.q) ||
                        (formik.errors.rpX && formik.touched.rpX) ||
                        (formik.errors.rpY && formik.touched.rpY) ||
                        (formik.errors.rul && formik.touched.rul) ||
                        (formik.errors.x && formik.touched.x) ||
                        (formik.errors.v && formik.touched.v) ||
                        (formik.errors.hs && formik.touched.hs) ||
                        (formik.errors.r && formik.touched.r) ||
                        (formik.errors.k11 && formik.touched.k11) ||
                        (formik.errors.k21 && formik.touched.k21) ||
                        (formik.errors.k22 && formik.touched.k22) ||
                        (formik.errors.deltaH && formik.touched.deltaH) ||
                        (formik.errors.deltaHR && formik.touched.deltaHR) ||
                        (formik.errors.deltaAOMR && formik.touched.deltaAOMR) ||
                        (formik.errors.deltaTOMR && formik.touched.deltaTOMR) ||
                        (formik.errors.deltaBR && formik.touched.deltaBR) ||
                        (formik.errors.tauO && formik.touched.tauO) ||
                        (formik.errors.tauW && formik.touched.tauW) ||
                        (formik.errors.l && formik.touched.l) ||
                        (formik.errors.r0 && formik.touched.r0) ||
                        (formik.errors.pfMax && formik.touched.pfMax) ||
                        (formik.errors.series_cell && formik.touched.series_cell) ||
                        (formik.errors.series_modul && formik.touched.series_modul) ||
                        (formik.errors.parallel_modul && formik.touched.parallel_modul) ||
                        (formik.errors.adeg && formik.touched.adeg) ||
                        (formik.errors.pmpo && formik.touched.pmpo) ||
                        (formik.errors.apmp && formik.touched.apmp) ||
                        (formik.errors.tempNOCT && formik.touched.tempNOCT) ||
                        (formik.errors.eff0 && formik.touched.eff0) ||
                        (formik.errors.s && formik.touched.s)
                    )
                    &&
                    <div className="alert alert-warning" role="alert">
                        <p className="m-0 p-0">{formik.errors.deviceName}</p>
                        <p className="m-0 p-0">{formik.errors.longitude}</p>
                        <p className="m-0 p-0">{formik.errors.latitude}</p>
                        <p className="m-0 p-0">{formik.errors.ip}</p>
                        <p className="m-0 p-0">{formik.errors.uid}</p>
                        <p className="m-0 p-0">{formik.errors.simNo}</p>
                        <p className="m-0 p-0">{formik.errors.model}</p>
                        <p className="m-0 p-0">{formik.errors.c0}</p>
                        <p className="m-0 p-0">{formik.errors.series_cell}</p>
                        <p className="m-0 p-0">{formik.errors.parallel_cell}</p>
                        <p className="m-0 p-0">{formik.errors.parallel_modul}</p>
                        <p className="m-0 p-0">{formik.errors.series_modul}</p>
                        <p className="m-0 p-0">{formik.errors.isc}</p>
                        <p className="m-0 p-0">{formik.errors.voc}</p>
                        <p className="m-0 p-0">{formik.errors.imp}</p>
                        <p className="m-0 p-0">{formik.errors.aisc}</p>
                        <p className="m-0 p-0">{formik.errors.aimp}</p>
                        <p className="m-0 p-0">{formik.errors.c1}</p>
                        <p className="m-0 p-0">{formik.errors.bvoc}</p>
                        <p className="m-0 p-0">{formik.errors.mbvoc}</p>
                        <p className="m-0 p-0">{formik.errors.bvmp}</p>
                        <p className="m-0 p-0">{formik.errors.mbvmp}</p>
                        <p className="m-0 p-0">{formik.errors.n}</p>
                        <p className="m-0 p-0">{formik.errors.c2}</p>
                        <p className="m-0 p-0">{formik.errors.c3}</p>
                        <p className="m-0 p-0">{formik.errors.dtc}</p>
                        <p className="m-0 p-0">{formik.errors.fd}</p>
                        <p className="m-0 p-0">{formik.errors.a}</p>
                        <p className="m-0 p-0">{formik.errors.b}</p>
                        <p className="m-0 p-0">{formik.errors.c4}</p>
                        <p className="m-0 p-0">{formik.errors.c5}</p>
                        <p className="m-0 p-0">{formik.errors.Ix}</p>
                        <p className="m-0 p-0">{formik.errors.Ixx}</p>
                        <p className="m-0 p-0">{formik.errors.c6}</p>
                        <p className="m-0 p-0">{formik.errors.c7}</p>
                        <p className="m-0 p-0">{formik.errors.e0}</p>
                        <p className="m-0 p-0">{formik.errors.c0}</p>
                        <p className="m-0 p-0">{formik.errors.k}</p>
                        <p className="m-0 p-0">{formik.errors.q}</p>
                        <p className="m-0 p-0">{formik.errors.airmass}</p>
                        <p className="m-0 p-0">{formik.errors.aoi}</p>
                        <p className="m-0 p-0">{formik.errors.p_diffuse}</p>
                        <p className="m-0 p-0">{formik.errors.rpX}</p>
                        <p className="m-0 p-0">{formik.errors.rpY}</p>
                        <p className="m-0 p-0">{formik.errors.rul}</p>
                        <p className="m-0 p-0">{formik.errors.x}</p>
                        <p className="m-0 p-0">{formik.errors.v}</p>
                        <p className="m-0 p-0">{formik.errors.hs}</p>
                        <p className="m-0 p-0">{formik.errors.r}</p>
                        <p className="m-0 p-0">{formik.errors.k11}</p>
                        <p className="m-0 p-0">{formik.errors.k21}</p>
                        <p className="m-0 p-0">{formik.errors.k22}</p>
                        <p className="m-0 p-0">{formik.errors.deltaH}</p>
                        <p className="m-0 p-0">{formik.errors.deltaHR}</p>
                        <p className="m-0 p-0">{formik.errors.deltaAOMR}</p>
                        <p className="m-0 p-0">{formik.errors.deltaTOMR}</p>
                        <p className="m-0 p-0">{formik.errors.deltaBR}</p>
                        <p className="m-0 p-0">{formik.errors.tauO}</p>
                        <p className="m-0 p-0">{formik.errors.tauW}</p>
                        <p className="m-0 p-0">{formik.errors.l}</p>
                        <p className="m-0 p-0">{formik.errors.r0}</p>
                        <p className="m-0 p-0">{formik.errors.pfMax}</p>
                        <p className="m-0 p-0">{formik.errors.adeg}</p>
                        <p className="m-0 p-0">{formik.errors.pmpo}</p>
                        <p className="m-0 p-0">{formik.errors.apmp}</p>
                        <p className="m-0 p-0">{formik.errors.tempNOCT}</p>
                        <p className="m-0 p-0">{formik.errors.eff0}</p>
                        <p className="m-0 p-0">{formik.errors.s}</p>
                    </div>
                } */}
                <div id="main-content">
                    {changeFormByCondition()}
                </div>
            </div>
        </>
    )

}

export default EditDevice;