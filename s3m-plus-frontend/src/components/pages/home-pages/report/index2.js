import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import "./index.css";
import moment from "moment";
import { Calendar } from "primereact/calendar";
import { locale, addLocale } from "primereact/api";
import SelectDevice from "../select-device-component/select-device-component";
import "./index.css";
import ReportService from "../../../../services/ReportService";
import { NotficationError, NotficationWarning } from "../notification/notification";
import { ToastContainer, toast } from "react-toastify";
import { any } from "@amcharts/amcharts5/.internal/core/util/Array";
import { async } from "q";
import ProjectService from "../../../../services/ProjectService";
import ProductionService from "../../../../services/ProductionService";
import AuthService from "../../../../services/AuthService";
import CustomerService from "../../../../services/CustomerService";
import AccessDenied from "../../access-denied/AccessDenied";
import LoadTypeService from "../../../../services/LoadTypeService";
import ReactModal from "react-modal";
import UserService from "../../../../services/UserService";
import ManufactureService from "../../../../services/ManufactureService";
import { t } from "i18next";

const Report2 = () => {
    addLocale("vn", {
        monthNames: [
            "Tháng 1",
            "Tháng 2",
            "Tháng 3",
            "Tháng 4",
            "Tháng 5",
            "Tháng 6",
            "Tháng 7",
            "Tháng 8",
            "Tháng 9",
            "Tháng 10",
            "Tháng 11",
            "Tháng 12",
        ],
        monthNamesShort: [
            "T1",
            "T2",
            "T3",
            "T4",
            "T5",
            "T6",
            "T7",
            "T8",
            "T9",
            "T10",
            "T11",
            "T12",
        ],
    });

    locale("vn");
    const param = useParams();
    const [systemTypeId, setSystemTypeId] = useState(1);
    const [reportName, setReportName] = useState(t('content.home_page.report.report_energy_time'));
    const [type, setType] = useState(1);
    const [typeTime, setTypeTime] = useState(1);
    const [fromDate, setFromDate] = useState(new Date());
    const [toDate, setToDate] = useState(new Date());
    const [checkDate, setCheckDate] = useState(new Date());
    const [selectedDevicesId, setSelectedDevicesId] = useState([]);
    const [projectId, setProjectId] = useState();
    const [projects, setProjects] = useState([]);
    const [loading, setLoading] = useState(false);
    const [productions, setProductions] = useState([]);
    const [productionSteps, setProductionSteps] = useState([]);
    const [selectedDevices, setSelectedDevices] = useState([]);
    const [role] = useState(AuthService.getRoleName());
    const [userName] = useState(AuthService.getUserName());
    const [accessDenied, setAccessDenied] = useState(false)
    const [loadTypes, setLoadTypes] = useState([]);
    const [selectedLoadType, setSelectedLoadType] = useState([]);
    const [loadTypeIds, setLoadTypeIds] = useState([]);
    const [manufactureShifts, setManufactureShifts] = useState([]);
    const [manufactureId, setManufactureId] = useState([]);
    const [productionId, setProductionId] = useState();
    const [productionStepId, setProductionStepId] = useState();
    const [intensity, setIntensity] = useState("")

    const callbackFunction = (childData, arr) => {
        setSelectedDevicesId(childData)
        setSelectedDevices(arr);
    }
    const onchangeProject = (pro) => {
        setProjectId(pro);
        listProduction(param.customerId, pro)
        setSelectedDevices([]);
        getLoadTypeByProjectAndSystemType(param.customerId, pro, systemTypeId)
        setSelectedLoadType([]);
    }
    const onchangeProduction = (event) => {
        listProductionStep(param.customerId, event.target.value, projectId)
    }
    const onChangeSystemType = (id) => {
        getLoadTypeByProjectAndSystemType(param.customerId, projectId, id)
    }

    const funcHandleChange = (event) => {
        setReportName(() => event.target.value);
    };
    const funcSetTypeTime = (e) => {
        setTypeTime(() => e.target.value);
        setFromDate(new Date());
        setToDate(new Date());
        if (e.target.value == 1) {
            let date = new Date();
            date.setDate(date.getDate() - 31);
            setCheckDate(date);
        } else if (e.target.value == 2) {
            let date = new Date();
            date.setMonth(date.getMonth() - 12);
            setCheckDate(date);
        } else if (e.target.value == 3) {
            let date = new Date();
            date.setFullYear(date.getFullYear() - 5);
            setCheckDate(date);
        }
    };
    const funcCheck = (e) => {
        if (typeTime == 1) {
            const date1 = new Date(moment(fromDate).format("YYYY-MM-DD"));
            const date2 = new Date(moment(e.target.value).format("YYYY-MM-DD"));

            var date = new Date(date2);
            date.setDate(date.getDate() - 31);
            setCheckDate(date);
            const diffTime = date2 - date1;
            let diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
            if (diffDays < 0) {
                setFromDate(e.value);
            } else if (diffDays > 31) {
                setFromDate(date);
            }
        } else if (typeTime == 2) {
            const date1 = new Date(moment(fromDate).format("YYYY-MM"));
            const date2 = new Date(moment(e.target.value).format("YYYY-MM"));

            var date = new Date(date2);
            date.setMonth(date.getMonth() - 12);
            setCheckDate(date);
            const diffTime = date2 - date1;
            let diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24 * 30));
            if (diffDays < 0) {
                setFromDate(e.value);
            } else if (diffDays > 12) {
                setFromDate(date);
            }
        } else if (typeTime == 3) {
            const date1 = new Date(moment(fromDate).format("YYYY-MM"));
            const date2 = new Date(moment(e.target.value).format("YYYY-MM"));

            var date = new Date(date2);
            date.setFullYear(date.getFullYear() - 5);
            setCheckDate(date);
            const diffTime = date2 - date1;
            let diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24 * 365));
            if (diffDays < 0) {
                setFromDate(e.value);
            } else if (diffDays > 5) {
                setFromDate(date);
            }
        }
    };
    const funcDownload = async () => {
        setLoading(true);
        const prefix = param.customerId + "@" + projectId + "@" + systemTypeId + "@" + type + "@" + typeTime + "@" + reportName
            + "@" + document.getElementById("sp").options[document.getElementById("sp").selectedIndex].text
            + "@" + document.getElementById("step").options[document.getElementById("step").selectedIndex].text
            + "@" + manufactureId + "@" + intensity;
        let devices = "";
        let loadType = "";
        if (type != 8) {
            if (type != 4) {
                if (selectedDevicesId.length <= 0) {
                    NotficationWarning(t('content.home_page.report.validate.device_null'));
                    setLoading(false);
                    return;
                }
            } else {
                if (productionSteps.length < 0 || productions.length < 0 || manufactureId == 0) {
                    NotficationError(t('content.home_page.report.validate.download'));
                    setLoading(false);
                    return;
                } else if (intensity == "") {
                    NotficationWarning(t('content.home_page.report.validate.base_intensity_null'));
                    setLoading(false);
                    return;
                }
            }
        } else {
            if (selectedLoadType.length <= 0) {
                NotficationWarning(t('content.home_page.report.validate.load_type_null'));
                setLoading(false);
                return;
            }
        }
        if (type == 7) {
            if (selectedDevicesId.length > 1) {
                NotficationWarning(t('content.home_page.report.validate.device_1_only'));
                setLoading(false);
                return;
            }
        }
        if (type != 8) {
            for (let i = 0; i < selectedDevicesId.length; i++) {
                if (i === 0) {
                    devices = selectedDevicesId[0].toString();
                } else {
                    devices += devices = "," + selectedDevicesId[i];
                }
            }
        }
        else {
            for (let i = 0; i < selectedLoadType.length; i++) {
                if (i === 0) {
                    loadType = selectedLoadType[0].loadTypeId.toString();
                } else {
                    loadType += loadType = "," + selectedLoadType[i].loadTypeId;
                }
            }
        }
        let time = any;
        if (typeTime == 1) {
            if (type == 7) {
                if (daysdifference(moment(fromDate).format(`YYYY-MM-DD`), moment(toDate).format(`YYYY-MM-DD`)) < 7) {
                    time = moment(fromDate).format(`YYYY-MM-DD`) + "@" + moment(toDate).format(`YYYY-MM-DD`) + "@" + 1
                } else
                    if (daysdifference(moment(fromDate).format(`YYYY-MM-DD`), moment(toDate).format(`YYYY-MM-DD`)) >= 7 &&
                        daysdifference(moment(fromDate).format(`YYYY-MM-DD`), moment(toDate).format(`YYYY-MM-DD`)) <= 30
                    ) {
                        time = moment(fromDate).format(`YYYY-MM-DD`) + "@" + moment(toDate).format(`YYYY-MM-DD`) + "@" + 2
                    } else {
                        time = moment(fromDate).format(`YYYY-MM-DD`) + "@" + moment(toDate).format(`YYYY-MM-DD`) + "@" + 3
                    }
            } else {
                time = moment(fromDate).format(`YYYY-MM-DD`) + "@" + moment(toDate).format(`YYYY-MM-DD`)
            }
        } else if (typeTime == 2) {
            if (type == 7) {
                time = moment(fromDate).format(`YYYY-MM`) + "@" + moment(fromDate).format(`YYYY-MM`)
            } else {
                time = moment(fromDate).format(`YYYY-MM`) + "@" + moment(toDate).format(`YYYY-MM`)
            }
        } else if (typeTime == 3) {
            time = moment(fromDate).format(`YYYY`) + "@" + moment(toDate).format(`YYYY`)
        }
        let response;
        response = await ReportService.exportReport(prefix, devices == "" ? undefined : devices, time, loadType == "" ? undefined : loadType);
        setLoading(false);
        // Thông báo
        if (response.status == 200) {
        } else {
            NotficationError(t('content.no_data'))
        }
    }
    const daysdifference = (firstDate, secondDate) => {
        var startDay = new Date(firstDate);
        var endDay = new Date(secondDate);

        var millisBetween = startDay.getTime() - endDay.getTime();
        var days = millisBetween / (1000 * 3600 * 24);

        return Math.round(Math.abs(days));
    }
    const getProjects = async () => {
        let ids = null;
        if (role === "ROLE_ADMIN" || role === "ROLE_MOD") {

        }
        if (role === "ROLE_USER") {
            let re = await ProjectService.getProIds(userName)
            if (re.status === 200 && re.data !== '') {
                ids = re.data
            }
        }
        let res = await ProjectService.getProjectByCustomerId(param.customerId, ids);
        setProjects(res.data);
        setProjectId(res.data[0].projectId);
        listProduction(param.customerId, res.data[0].projectId);
        getLoadTypeByProjectAndSystemType(param.customerId, res.data[0].projectId, systemTypeId)
    }
    const getProject = async (id) => {
        let res = await ProjectService.getProject(id);
        listProduction(param.customerId, param.projectId)
        setProjects([res.data]);
        getLoadTypeByProjectAndSystemType(param.customerId, param.projectId, systemTypeId)
    }
    const listProduction = async (customerId, projectId) => {
        let res = await ProductionService.getListProduction(customerId, projectId);
        if (res.data.length > 0) {
            setProductions(res.data);
            listProductionStep(param.customerId, res.data[0].productionId, projectId)
            setProductionId(res.data[0].productionId)
        } else {
            setProductions([])
            setProductionSteps([])
            setProductionStepId(0)
            setManufactureShifts([]);
            setManufactureId(0)
        }
    }

    const listProductionStep = async (customerId, productionId, projectId) => {
        let res = await ProductionService.getListProductionStep(customerId, productionId, projectId);
        if (res.data.length > 0) {
            if (res.status == 200) {
                setProductionSteps(res.data);
                setProductionStepId(res.data[0].productionStepId)
                if (type == 4) {
                    getListManufacture(customerId, projectId, productionId, res.data[0].productionStepId);
                }
            }
        } else {
            setProductionSteps([])
            setProductionStepId(0)
            setManufactureShifts([]);
            setManufactureId(0)
        }
    }

    const onChangeType = (number) => {
        if (number == 1) {
            setType(1)
            setReportName(t('content.home_page.report.report_energy_time'));
        }
        if (number == 2) {
            setType(2)
            setReportName(t('content.home_page.report.report_energy_work_shift'));
        }
        if (number == 3) {
            setType(3)
            setReportName(t('content.home_page.report.report_cost'));
        }
        if (number == 4) {
            setType(4)
            setReportName(t('content.home_page.report.report_consump_energy'));
            getListManufacture(param.customerId, projectId, productionId, productionStepId);
        }
        if (number == 5) {
            setType(5)
            setReportName(t('content.home_page.report.report_warning'));
        }
        if (number == 6) {
            setType(6)
            setReportName(t('content.home_page.report.report_compare_energy'));
        }
        if (number == 7) {
            setType(7)
            setReportName((t('content.home_page.report.report_energy_quality')));
        }
        if (number == 8) {
            setType(8)
            setReportName(t('content.home_page.report.report_energy_load_type'));
        }
    }

    const getListManufacture = async (customer, project, production, productionStep) => {
        let res = await ManufactureService.getListManufacturesShift(customer, project, production != 0 ? production : null, productionStep != 0 ? productionStep : null);
        if (res.status == 200) {
            if (res.data.length > 0) {
                setManufactureId(res.data[0].id)
                setManufactureShifts(res.data)
                getDeviceIdByManufactureShift(res.data[0]);
            } else {
                setManufactureId(0)
                setManufactureShifts([])
            }
        }
    }
    const onChangeManufactureShift = (id) => {
        setManufactureId(id);
        getDeviceIdByManufactureShift(manufactureShifts.filter(ob => ob.id == id)[0])
    }

    const getDeviceIdByManufactureShift = (manufacture) => {
        let arr = [];
        manufacture.devices.forEach(element => {
            arr = [...arr, element.deviceId]
        });
        setSelectedDevicesId(arr);
    }

    const getDeviceNamesByManufacture = (devices) => {
        let name = "";
        for (let i = 0; i < devices.length; i++) {
            if (i == 0) {
                name = devices[i].deviceName
            } else {
                name = name + ",  " + devices[i].deviceName
            }
        }
        return name;
    }

    const renderSelectedDevices = () => {
        if (type != 8) {
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
        } else {
            if (selectedLoadType.length > 0) {
                return (
                    selectedLoadType.map((load, index) => {
                        return (
                            <div id="ic-eyes" key={index} className="mt-2" >
                                <i className="fa-solid fa-circle fa-2xs" style={{ color: "#ff23a4" }}> &nbsp;</i>
                                <span style={{ fontWeight: 'bold', color: "black", fontSize: '16px' }}>{load.loadTypeName}</span>
                                <i
                                    id="ic-eye"
                                    style={{ fontWeight: 'bold', marginLeft: '5%', fontSize: '20px', }}
                                    onClick={() => {
                                        setSelectedLoadType(selectedLoadType.filter((d) => d.loadTypeId != load.loadTypeId))
                                        // setSelectedDevicesId(selectedDevicesId.filter((a) => a != dv.deviceId))
                                    }
                                    }
                                    className="fa-solid fa-circle-xmark"></i>
                            </div>
                        )
                    })
                )
            }
        }
    }

    const checkAuthorization = async () => {
        let response = await UserService.getUserByUsername();
        if (response.status === 200) {
            const userData = response.data;
            setSystemTypeId(userData.prioritySystem);
        }
        let cusId = 0;
        if (param.customerId !== undefined) {
            cusId = param.customerId;
        }
        let check = true;
        let resCheckProjectInCus = await ProjectService.getProjectByCustomerId(cusId);
        if (param.projectId != null) {
            check = resCheckProjectInCus.data.some(project => parseInt(project.projectId) === parseInt(param.projectId));
            if (!check) {
                setAccessDenied(true);
            }
        }
        if (check) {
            if (role === "ROLE_ADMIN") {
                setAccessDenied(false)
                if (param.projectId != undefined) {
                    getProject(param.projectId)
                    setProjectId(param.projectId)
                } else {
                    getProjects();
                }
            }
            if (role === "ROLE_MOD") {
                let customerIds = ""
                let projIds = ""
                let res = await CustomerService.getCustomerIds(userName)
                if (res.status === 200 && res.data !== '') {
                    for (let i = 0; i < res.data.length; i++) {
                        customerIds += res.data[i].customerId + ","
                    }
                }

                if (param.customerId != undefined) {
                    if (!customerIds.includes(param.customerId)) {
                        setAccessDenied(true)
                    } else {
                        let re = await ProjectService.getProIds(userName)
                        if (re.status === 200 && re.data !== "") {
                            projIds = "" + re.data
                        }
                        if (param.projectId != null) {
                            if (!projIds.includes(param.projectId)) {
                                setAccessDenied(true)
                            } else {
                                setAccessDenied(false);
                                if (param.projectId != undefined) {
                                    getProject(param.projectId)
                                    setProjectId(param.projectId)
                                } else {
                                    getProjects();
                                }
                            }
                        }
                        else {
                            setAccessDenied(false);
                            if (param.projectId != undefined) {
                                getProject(param.projectId)
                                setProjectId(param.projectId)
                            } else {
                                getProjects();
                            }
                        }
                    }
                }
            }
            if (role == "ROLE_USER") {
                let customerIds = ""
                let projIds = ""

                let res = await CustomerService.getCustomerIds(userName)
                if (res.status === 200 && res.data !== '') {
                    for (let i = 0; i < res.data.length; i++) {
                        customerIds += res.data[i].customerId + ","
                    }
                }

                if (param.customerId != undefined) {
                    if (!customerIds.includes(param.customerId)) {
                        setAccessDenied(true)
                    } else {
                        let re = await ProjectService.getProIds(userName)
                        if (re.status === 200 && re.data !== "") {
                            projIds = "" + re.data
                        }
                        if (param.projectId != null) {
                            if (!projIds.includes(param.projectId)) {
                                setAccessDenied(true)
                            } else {
                                setAccessDenied(false)
                                if (param.projectId != undefined) {
                                    getProject(param.projectId)
                                    setProjectId(param.projectId)
                                } else {
                                    getProjects();
                                }
                            }
                        } else {
                            setAccessDenied(false)
                            if (param.projectId != undefined) {
                                getProject(param.projectId)
                                setProjectId(param.projectId)
                            } else {
                                getProjects();
                            }
                        }
                    }
                }
            }
        }
    }
    const getLoadTypeByProjectAndSystemType = async (customer, project, systemType) => {
        let res = await LoadTypeService.getLoadTypeByProjectAndSystemType(customer, project, systemType);
        if (res.status == 200) {
            setLoadTypes(res.data)
        }
    }

    const [isModalOpen, setIsModalOpen] = useState(false);
    const closeModal = () => {
        setIsModalOpen(false);
    };
    const setChecked = (item) => {
        let ck = false;
        selectedLoadType.forEach(element => {
            if (element.loadTypeId == item.loadTypeId) {
                ck = true;
            }
        })
        return ck;
    }
    const onSelectLoadType = (item) => {
        let ck = false;
        for (let i = 0; i < selectedLoadType.length; i++) {
            if (selectedLoadType[i].loadTypeId == item.loadTypeId) {
                ck = true;
            }
        }
        if (ck === false) {
            setSelectedLoadType(Array.from(new Set([...selectedLoadType, item])));
        } else {
            onUnSelectLoadType(item);
        }
        setChecked(item)
    }
    const onUnSelectLoadType = (item) => {
        setSelectedLoadType(selectedLoadType.filter((dv) => item.loadTypeId != dv.loadTypeId));
    }

    const onSaveModalSelectLoadType = () => {
        closeModal();
    }

    useEffect(() => {
        document.title = (t('content.home_page.report.report'));
        checkAuthorization();
    }, [param.projectId, param.customerId])

    return (
        <>
            {accessDenied ?
                <AccessDenied></AccessDenied>
                :
                <>
                    <div>
                        <div>
                            <ToastContainer />
                            <div className="report-zone1">
                                <div className="submenu-report">
                                    <div className="main-submenu" id="reportType1" title={t('content.home_page.report.report_energy_time')} style={type === 1 ? { color: "#FFF", fontWeight: 'bold', backgroundColor: 'var(--ses-orange-80-color' } : { color: "#FFF", fontWeight: 'bold' }} onClick={() => {
                                        onChangeType(1);
                                    }}>
                                        <i className="fa fa-bolt ml-1" style={{ color: "#FFF", fontWeight: 'bold' }}></i> &nbsp;
                                        {t('content.home_page.report.report_energy_time')}
                                    </div>
                                    <div className="main-submenu" id="reportType1" title={t('content.home_page.report.report_energy_work_shift')} style={type === 2 ? { color: "#FFF", fontWeight: 'bold', backgroundColor: 'var(--ses-orange-80-color' } : { color: "#FFF", fontWeight: 'bold' }} onClick={() => {
                                        onChangeType(2);
                                    }}>
                                        <i className="fa fa-puzzle-piece ml-1" style={{ color: "#FFF", fontWeight: 'bold' }}></i> &nbsp;
                                        {t('content.home_page.report.report_energy_work_shift')}
                                    </div>
                                    <div className="main-submenu" id="reportType1" title={t('content.home_page.report.report_cost')} style={type === 3 ? { color: "#FFF", fontWeight: 'bold', backgroundColor: 'var(--ses-orange-80-color' } : { color: "#FFF", fontWeight: 'bold' }} onClick={() => {
                                        onChangeType(3);
                                    }}>
                                        <i className="fa fa-money-check-dollar ml-1" style={{ color: "#FFF", fontWeight: 'bold' }}></i> &nbsp;
                                        {t('content.home_page.report.report_cost')}
                                    </div>
                                    <div className="main-submenu" id="reportType1" title={t('content.home_page.report.report_consump_energy')} style={type === 4 ? { color: "#FFF", fontWeight: 'bold', backgroundColor: 'var(--ses-orange-80-color' } : { color: "#FFF", fontWeight: 'bold' }} onClick={() => {
                                        onChangeType(4)
                                    }}>
                                        <i className="fa fa-bars ml-1" style={{ color: "#FFF", fontWeight: 'bold' }}></i> &nbsp;
                                        {t('content.home_page.report.report_consump_energy')}
                                    </div>
                                    <div className="main-submenu" id="reportType1" title={t('content.home_page.report.report_warning')} style={type === 5 ? { color: "#FFF", fontWeight: 'bold', backgroundColor: 'var(--ses-orange-80-color' } : { color: "#FFF", fontWeight: 'bold' }} onClick={() => {
                                        onChangeType(5)
                                    }}>
                                        <i className="fa fa-plug-circle-exclamation ml-1" style={{ color: "#FFF", fontWeight: 'bold' }}></i> &nbsp;
                                        {t('content.home_page.report.report_warning')}
                                    </div>
                                    <div className="main-submenu" id="reportType1" title={t('content.home_page.report.report_compare_energy')} style={type === 6 ? { color: "#FFF", fontWeight: 'bold', backgroundColor: 'var(--ses-orange-80-color' } : { color: "#FFF", fontWeight: 'bold' }} onClick={() => {
                                        onChangeType(6)
                                    }}>
                                        <i className="fa fa-magnifying-glass-chart ml-1" style={{ color: "#FFF", fontWeight: 'bold' }}></i> &nbsp;
                                        {t('content.home_page.report.report_compare_energy')}
                                    </div>
                                    <div className="main-submenu" id="reportType1" title={t('content.home_page.report.report_energy_quality')} style={type === 7 ? { color: "#FFF", fontWeight: 'bold', backgroundColor: 'var(--ses-orange-80-color' } : { color: "#FFF", fontWeight: 'bold' }} onClick={() => {
                                        onChangeType(7);
                                    }}>
                                        <i className="fa-solid fa-bug" style={{ color: "#FFF", fontWeight: 'bold' }}></i> &nbsp;
                                        {t('content.home_page.report.report_energy_quality')}
                                    </div>
                                    <div hidden={systemTypeId != 1} title={t('content.home_page.report.report_energy_load_type')} className="main-submenu" id="reportType1" style={type === 8 ? { color: "#FFF", fontWeight: 'bold', backgroundColor: 'var(--ses-orange-80-color' } : { color: "#FFF", fontWeight: 'bold' }} onClick={() => {
                                        onChangeType(8);
                                    }}>
                                        <i className="fa-solid fa-gears" style={{ color: "#FFF", fontWeight: 'bold' }}></i> &nbsp;
                                        {t('content.home_page.report.report_energy_load_type')}
                                    </div>
                                </div>
                            </div>
                            <div className="report-zone2">
                                {loading == true ?
                                    <div style={{ position: 'absolute' }}>
                                        <div className="loading" style={{ marginTop: "10%", marginLeft: "700px" }}>
                                            <img height="60px" src="/resources/image/loading2.gif" alt="loading" />
                                        </div>
                                    </div> : null
                                }
                                <div className="system-type">
                                    <div className="radio-tabs">
                                        <label className="radio-tabs__field" >
                                            <input type="radio" name="radio-tabs" value={1} className="radio-tabs__input" checked={systemTypeId == 1 ? true : false} onChange={(event) => {
                                                setSystemTypeId(event.target.value)
                                                onChangeSystemType(event.target.value)
                                            }} />
                                            <span className="radio-tabs__text text-uppercase">
                                                <img src="/resources/image/icon-load-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                                {t('content.home_page.load')}</span>
                                        </label>
                                        <label className="radio-tabs__field" >
                                            <input type="radio" name="radio-tabs" value={2} className="radio-tabs__input" checked={systemTypeId == 2 ? true : false} onChange={(event) => {
                                                setSystemTypeId(event.target.value)
                                                onChangeSystemType(event.target.value)
                                            }} />
                                            <span className="radio-tabs__text text-uppercase">
                                                <img src="/resources/image/icon-solar-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                                {t('content.home_page.solar')}</span>
                                        </label>
                                        <label className="radio-tabs__field" >
                                            <input type="radio" name="radio-tabs" value={5} className="radio-tabs__input" checked={systemTypeId == 5 ? true : false} onChange={(event) => {
                                                setSystemTypeId(event.target.value)
                                                onChangeSystemType(event.target.value)
                                            }} />
                                            <span className="radio-tabs__text text-uppercase">
                                                <img src="/resources/image/icon-grid-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                                {t('content.home_page.grid')}</span>
                                        </label>
                                        <label className="radio-tabs__field">
                                            <input type="radio" name="radio-tabs" value={3} className="radio-tabs__input" checked={systemTypeId == 3 ? true : false} onChange={(event) => {
                                                setSystemTypeId(event.target.value)
                                                onChangeSystemType(event.target.value)
                                            }} />
                                            <span className="radio-tabs__text text-uppercase">
                                                <img src="/resources/image/icon-battery-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                                {t('content.home_page.battery')}</span>
                                        </label>
                                        <label className="radio-tabs__field">
                                            <input type="radio" name="radio-tabs" value={4} className="radio-tabs__input" checked={systemTypeId == 4 ? true : false} onChange={(event) => {
                                                setSystemTypeId(event.target.value)
                                                onChangeSystemType(event.target.value)
                                            }} />
                                            <span className="radio-tabs__text text-uppercase">
                                                <img src="/resources/image/icon-wind-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                                {t('content.home_page.wind')}</span>
                                        </label>
                                    </div>
                                </div>



                                <table className="table table-input">
                                    <tbody className="report-tbody">
                                        <tr>
                                            <th width="250px" className="text-uppercase">
                                                <i className="fas fa-solid fa-file mr-1" style={{ color: "#333" }}></i>
                                                {t('content.home_page.report.name')}<span className="required">※</span>
                                            </th>
                                            <td colSpan={3}>
                                                <input id="reportName" type="text" className="form-control" style={{ fontSize: "13px" }} maxLength={255} onChange={(e) => {
                                                    funcHandleChange(e);
                                                }} value={reportName} />
                                            </td>
                                        </tr>
                                        <tr>
                                            <th width="250px" className="text-uppercase">
                                                <i className="fas fa-solid fa-file mr-1" style={{ color: "#333" }}></i>
                                                {t('content.project')}<span className="required">※</span>
                                            </th>
                                            <td colSpan={3}>
                                                <select disabled={param.projectId != undefined} id="siteManufacture" className="custom-select block" onChange={(event) => { onchangeProject(event.target.value) }} style={{ fontSize: "13px" }}>
                                                    {projects.map((pro, index) => {
                                                        return (
                                                            <option key={index} value={pro.projectId}>{pro.projectName}</option>
                                                        )
                                                    })}
                                                </select>
                                            </td>
                                        </tr>
                                        <tr hidden={type != 4}>
                                            <th width="250px" className="text-uppercase">
                                                <i className="fas fa-solid fa-file mr-1" style={{ color: "#333" }}></i>
                                                {t('content.product')}<span className="required">※</span>
                                            </th>
                                            <td colSpan={3}>
                                                <select id="sp" className="custom-select block" onChange={(event) => onchangeProduction(event)} style={{ fontSize: "13px" }}>
                                                    {productions.length > 0 ? productions.map((pro, index) => {
                                                        return (
                                                            <option key={index} value={pro.productionId}>{pro.productionName}</option>
                                                        )
                                                    }) : <option value={0}>{t('content.no_data')}</option>}
                                                </select>
                                            </td>
                                        </tr>
                                        <tr hidden={type != 4}>
                                            <th width="250px" className="text-uppercase">
                                                <i className="fas fa-solid fa-file mr-1" style={{ color: "#333" }}></i>
                                                {t('content.production_step')}<span className="required">※</span>
                                            </th>
                                            <td colSpan={1}>
                                                <select id="step" className="custom-select block" style={{ fontSize: "13px" }}>
                                                    {productionSteps.length > 0 ? productionSteps.map((pro, index) => {
                                                        return (
                                                            <option key={index} value={pro.productionStepId}>{pro.productionStepName}</option>
                                                        )
                                                    }) : <option value={0}>{t('content.no_data')}</option>}
                                                </select>
                                            </td>
                                            <th width="250px" className="text-uppercase">
                                                <i className="fas fa-solid fa-file mr-1" style={{ color: "#333" }}></i> {t('content.home_page.report.base_intensity')}
                                                <span className="required"></span>
                                            </th>
                                            <td colSpan={1}>
                                                <input className="form-control" placeholder={t('content.home_page.report.choose_base_intensity')} type="number" onChange={(e) => setIntensity(e.target.value)} ></input>
                                            </td>
                                        </tr>

                                        <tr hidden={type === 8 || type == 4}>
                                            <th width="250px" className="text-uppercase">
                                                <i className="fas fa-solid fa-layer-group mr-1" style={{ color: "#333" }}></i>{t('content.device')}</th>
                                            <td colSpan={1}>
                                                {(type == 6) === false ?
                                                    <SelectDevice style={{ marginLeft: '2%', width: '200px' }} projectId={projectId} systemTypeId={systemTypeId} parentCallback={callbackFunction} titleName={t('content.home_page.report.choose_device')} limit={type == 7 ? 1 : null} />
                                                    :
                                                    <SelectDevice style={{ marginLeft: '2%', width: '200px' }} projectId={projectId} systemTypeId={systemTypeId} parentCallback={callbackFunction} titleName={t('content.home_page.report.choose_device')} limit={5} />
                                                }
                                            </td>
                                            <td colSpan={2} style={{ height: '60px' }} >
                                                <div style={{ height: '70px', display: 'grid', gridTemplateColumns: 'auto auto', overflowY: 'scroll' }}>{renderSelectedDevices()}</div>
                                            </td>
                                        </tr>
                                        <tr hidden={type != 4}>
                                            <th width="250px" className="text-uppercase">
                                                <i className="fas fa-solid fa-layer-group mr-1" style={{ color: "#333" }}></i>{t('content.home_page.report.device_production')}</th>
                                            <td colSpan={3}>
                                                <select disabled={manufactureShifts < 1} id="siteManufacture" className="custom-select block" onChange={(event) => { onChangeManufactureShift(event.target.value) }} style={{ fontSize: "13px" }}>
                                                    {manufactureShifts.length > 0 ? manufactureShifts.map((manu, index) => {
                                                        return (
                                                            <option key={index} value={manu.id}>{getDeviceNamesByManufacture(manu.devices)}</option>
                                                        )
                                                    }) :
                                                        <option value={0}>{t('content.no_data')}</option>
                                                    }
                                                </select>
                                            </td>

                                        </tr>
                                        <tr hidden={type !== 8}>
                                            <th width="250px" className="text-uppercase">
                                                <i className="fas fa-solid fa-layer-group mr-1" style={{ color: "#333" }}></i>{t('content.load_type')}</th>
                                            <td colSpan={1}>
                                                <button className="btn-select-load-type btn" onClick={() => { setIsModalOpen(true) }}>{t('content.home_page.report.choose_load_type')}</button>
                                            </td>
                                            <td colSpan={2} style={{ height: '60px' }} >
                                                <div style={{ height: '70px', display: 'grid', gridTemplateColumns: 'auto auto', overflowY: 'scroll' }}>{renderSelectedDevices()}</div>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th width="250px" className="text-uppercase">
                                                <i className="fas fa-solid fa-clone mr-1" style={{ color: "#333" }}></i>{t('content.home_page.report.template')}</th>
                                            <td colSpan={3}>
                                                <select id="reportTemplate" className="custom-select block" style={{ fontSize: "13px" }}>
                                                    <option value={1}>{t('content.home_page.report.default_template')}</option>
                                                </select>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th width="250px" className="text-uppercase">
                                                <i className="fas fa-solid fa-calendar-days mr-1" style={{ color: "#333" }}></i>
                                                {t('content.home_page.manufacture.time')}
                                            </th>
                                            <td>
                                                <select id="typeTimeReport" className="custom-select block" style={{ fontSize: "13px" }}
                                                    onChange={e => {
                                                        funcSetTypeTime(e);
                                                        // formik.handleChange(e)
                                                    }}>
                                                    <option value={1}>{t('content.day')}</option>
                                                    <option value={2} hidden={type == 2}>{t('content.month')}</option>
                                                    <option value={3} hidden={type == 7 || type == 3 || type == 2}>{t('content.year')}</option>
                                                </select>
                                            </td>
                                            <td>
                                                <label hidden={type == 7}>{t('content.home_page.warning_tab.from_date')}:</label>
                                                <Calendar locale="vn"
                                                    value={fromDate}
                                                    onChange={(e) => setFromDate(e.value)}
                                                    view={typeTime == 2 ? "month" : typeTime == 3 ? "year" : "date"}
                                                    dateFormat={typeTime == 2 ? "yy-mm" : typeTime == 3 ? "yy" : "yy-mm-dd"}
                                                    maxDate={toDate}
                                                // minDate={type == 7 ? toDate : null} 
                                                />
                                            </td>
                                            <td hidden={type == 7 && typeTime == 2}>
                                                {t('content.home_page.warning_tab.to_date')}:
                                                <Calendar locale="vn"
                                                    value={toDate}
                                                    onChange={(e) => {
                                                        setToDate(e.value)
                                                        funcCheck(e)
                                                    }}
                                                    view={typeTime == 2 ? "month" : typeTime == 3 ? "year" : "date"}
                                                    dateFormat={typeTime == 2 ? "yy-mm" : typeTime == 3 ? "yy" : "yy-mm-dd"}
                                                    maxDate={new Date()} />
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>

                                <div id="main-button">
                                    <button id="submitReport" type="submit" className="btn btn-outline-secondary btn-agree text-uppercase" style={{ width: "200px" }} onClick={() => funcDownload()}>
                                        <i className="fa-solid fa-check"></i> {t('content.home_page.report.report')}
                                    </button>
                                </div>
                            </div>

                            <ReactModal
                                isOpen={isModalOpen}
                                onRequestClose={closeModal}
                                contentLabel="Modal 1"
                                style={{
                                    content: {
                                        width: "680px", // Kích thước chiều rộng của modal (có thể điều chỉnh)
                                        height: "500px", // Kích thước chiều cao của modal (có thể điều chỉnh)
                                        margin: "auto", // Căn giữa modal
                                        marginTop: "5%",
                                        border: "2px solid #0a1a5c",
                                        borderRadius: '5px',
                                        padding: "8px",
                                        backgroundColor: 'rgba(224, 224, 224,0.1)'
                                    },
                                }}
                            >
                                <div className="modal-list-load-type">
                                    <div className="modal-list-load-type-header">
                                        <p>{t('content.home_page.report.ch')}</p>
                                    </div>
                                    <div className="modal-list-load-type-body">
                                        <div className="modal-list-load-type-body-left">
                                            <p className="modal-list-load-type-body-title">
                                                Danh sách phụ tải
                                            </p>
                                            <hr />
                                            <div className="modal-list-load-type-body-main">
                                                {
                                                    loadTypes.map((item, index) => {
                                                        return (
                                                            <div className="div-select" key={index} onClick={() => onSelectLoadType(item)}>
                                                                <input type="checkbox" value={item.loadTypeId} className="input-select" id={`ipcheck` + index} readOnly checked={setChecked(item)} />
                                                                <label className="label-select" >{item.loadTypeName}</label>
                                                            </div>
                                                        )
                                                    })
                                                }
                                            </div>
                                        </div>
                                        <div className="modal-list-load-type-body-right">
                                            <p className="modal-list-load-type-body-title">
                                                Danh sách đã chọn
                                            </p>
                                            <hr />
                                            <div className="modal-list-load-type-body-main">
                                                {selectedLoadType?.map((item, index) => {
                                                    return (
                                                        <div className="div-select" key={`selected` + index} onClick={() => onUnSelectLoadType(item)}  >
                                                            <input type="checkbox" value={item.loadTypeId} className="input-select" id={`ip1che1ck` + index} checked={true} readOnly />
                                                            <label className="div-select label-select">{item.loadTypeName}</label>
                                                        </div>
                                                    )

                                                })}
                                            </div>
                                        </div>
                                    </div>
                                    <div className="modal-list-load-type-footer">
                                        <button type="button" className="btn btn-confirm" style={{ marginRight: '1.5%' }} data-dismiss="modal" onClick={() => onSaveModalSelectLoadType()}>Đồng ý</button>
                                        <button type="button" className="btn btn-can" data-dismiss="modal" style={{ marginLeft: '1.5%' }} onClick={() => {
                                            setSelectedLoadType([]);
                                            closeModal();
                                        }}>Hủy</button>
                                    </div>
                                </div>

                            </ReactModal>
                        </div>
                    </div>
                </>}
        </>
    );
}


export default Report2;
