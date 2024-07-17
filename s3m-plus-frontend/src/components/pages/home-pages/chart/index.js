import { useState } from "react";
import { useEffect } from "react";
import { useRef } from "react";
import { useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import "./index.css";
import * as am5 from "@amcharts/amcharts5";
import * as am5xy from "@amcharts/amcharts5/xy";
import * as am5flow from "@amcharts/amcharts5/flow";
import * as am5percent from "@amcharts/amcharts5/percent";
import am5themes_Animated from "@amcharts/amcharts5/themes/Animated";
import ProjectService from "../../../../services/ProjectService";
import ChartService from "../../../../services/ChartService";
import moment from "moment";
import dataTime from "../../home-pages/chart/time.json"
import { Calendar } from "primereact/calendar";
import SelectDevice from "../select-device-component/select-device-component";
import CustomerService from "../../../../services/CustomerService";
import AuthService from "../../../../services/AuthService";
import AccessDenied from "../../access-denied/AccessDenied";
import SelectDeviceSankey from "./select-device-sankey/select-device-sankey";
import SelectDeviceSankey1 from "./select-device-sankey-1/select-device-sankey-1";
import SelectDeviceSankey2 from "./select-device-sankey-2/select-device-sankey-2";
import SelectDeviceSankey3 from "./select-device-sankey-3/select-device-sankey-3";
import UserService from "../../../../services/UserService";
import { addLocale } from 'primereact/api';
import DeviceService from "../../../../services/DeviceService";

const Chart = () => {
    const { t } = useTranslation();
    addLocale('es', {
        firstDayOfWeek: 1,
        showMonthAfterYear: true,
        dayNames: ['domingo', 'lunes', 'martes', 'miércoles', 'jueves', 'viernes', 'sábado'],
        dayNamesShort: ['dom', 'lun', 'mar', 'mié', 'jue', 'vie', 'sáb'],
        dayNamesMin: ['D', 'L', 'M', 'X', 'J', 'V', 'S'],
        monthNames: ['enero', 'febrero', 'marzo', 'abril', 'mayo', 'junio', 'julio', 'agosto', 'septiembre', 'octubre', 'noviembre', 'diciembre'],
        monthNamesShort: ['ene', 'feb', 'mar', 'abr', 'may', 'jun', 'jul', 'ago', 'sep', 'oct', 'nov', 'dic'],
        today: 'Hoy',
        clear: 'Limpiar'
    });

    addLocale('vi', {
        firstDayOfWeek: 1,
        showMonthAfterYear: true,
        dayNames: ['Chủ nhật', 'Thứ hai', 'Thứ ba', 'Thứ tư', 'Thứ năm', 'Thứ sáu', 'Thứ bảy'],
        dayNamesShort: ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'],
        dayNamesMin: ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'],
        monthNames: ['Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4', 'Tháng 5', 'Tháng 6', 'Tháng 7', 'Tháng 8', 'Tháng 9', 'Tháng 10', 'Tháng 11', 'Tháng 12'],
        monthNamesShort: ['Thg 1', 'Thg 2', 'Thg 3', 'Thg 4', 'Thg 5', 'Thg 6', 'Thg 7', 'Thg 8', 'Thg 9', 'Thg 10', 'Thg 11', 'Thg 12'],
        today: t('content.today'),
        clear: t('content.title_icon_delete')
    });

    const $ = window.$;
    const param = useParams();

    const [type, setType] = useState(1);
    const [projectIdSelected, setProjectIdSelected] = useState(0);
    const [customerId, setCustomerId] = useState();
    const [projects, setProjects] = useState([]);
    const [dataNow, setDataNow] = useState([]);
    const [dataComp, setDataComp] = useState([]);
    const [fromDate, setFromDate] = useState(new Date());
    const [toDate, setToDate] = useState(new Date());
    const [fDate, setFDate] = useState(new Date());
    const [tDate, setTDate] = useState(new Date());
    const [typeTime, setTypeTime] = useState(1);
    const [zoom, setZoom] = useState(0);
    const [data, setData] = useState([]);
    const [projectId, setProjectId] = useState(0);
    const [systemTypeId, setSystemTypeId] = useState(1);
    const [dataLoadAllSite, setDataLoadAllSite] = useState([]);
    const [deviceId, setDeviceId] = useState("");
    const [projectName, setProjectName] = useState("");
    const [typeLoadName, setTypeLoadName] = useState("NĂNG LƯỢNG");
    const [showToDateTab1, setShowToDateTab1] = useState(false)
    const [activeButton, setActiveButton] = useState(1)
    const [typeFormat, setTypeFormat] = useState("yy-mm-dd")
    const [viewCalender, setViewCalender] = useState("")
    const [isLoading, setIsLoading] = useState(true);
    const [viewValue, setViewValue] = useState(1);
    const [typeView, setTypeView] = useState(true)
    const [dataTablePower, setDataTablePower] = useState([]);
    const [dataTablePowerType9, setDataTablePowerType9] = useState([]);
    const [dataTotalPower, setDataTotalPower] = useState([]);
    const [unit, setUnit] = useState("(kWh)")
    const [deviceType, setDeviceType] = useState(null);
    const [typeNameDowload, setTypeNameDowload] = useState("tableEp");
    const tableRef = useRef(null);
    const [role] = useState(AuthService.getRoleName());
    const [userName] = useState(AuthService.getUserName());
    const [accessDenied, setAccessDenied] = useState(false);
    const [deviceClass1, setDeviceClass1] = useState();
    const [deviceClass2, setDeviceClass2] = useState();
    const [deviceClass3, setDeviceClass3] = useState();
    const [dataSankey, setDataSankey] = useState(null);
    const [dataSankeyClass1, setDataSankeyClass1] = useState();
    const [dataSankeyClass2, setDataSankeyClass2] = useState();
    const [dataSankeyClass3, setDataSankeyClass3] = useState();
    const [dataSelectDevice1, setDataSelectDevice1] = useState();
    const [dataSelectDevice2, setDataSelectDevice2] = useState();
    const [typeSankey, setTypeSankey] = useState(0);
    const allowedTypes = [];
    // const allowedTypes = [4, 5, 6, 8, 9];
    const allowedTypeDate = [0, 1, 2, 3, 4, 5, 8];
    const allowedTypeDevice = [5, 6, 7, 8];
    const [dataDeviceName, setDataDeviceName] = useState(undefined);
    const [idDeviceSankey, setIdDeviceSankey] = useState([]);
    const [priorityLoad, setPriorityLoad] = useState([])
    const [prioritySolar, setPrioritySolar] = useState([])
    const [priorityGrid, setPriorityGrid] = useState([])
    const [priorityBattery, setPrioritBattery] = useState([])
    const [priorityWind, setPriorityWind] = useState([])
    const [priorityAll, setPriorityAll] = useState([])

    const callbackFunction = (childData, data) => {
        setIsLoading(true);
        const device = childData.join(', ');
        setDeviceId(device);
        let ProjectIdParam = param.projectId
        if (ProjectIdParam == undefined) {
            ProjectIdParam = 0;
        }
        let arr = [];
        if (data.length > 0) {
            data.forEach(element => {
                arr = [...arr, element.deviceName]
            });
        }
        const deviceName = arr.join(', ');
        setDataDeviceName(arr);
        funcGetDataLoad(ProjectIdParam, type, typeTime, fromDate, toDate, activeButton, systemTypeId, device, "", deviceName, arr, data)
    }

    const callbackFunctionSankey = (childData, data) => {
        if (childData.length == 0) {
            $.alert({
                title: t('content.notification'),
                content: t('content.home_page.device_select.device_null'),
            });
        } else {
            setTypeSankey(1);
            setDeviceClass1(data);
            const device = childData.join(', ');
            setDeviceId(device);
            let ProjectIdParam = param.projectId
            if (ProjectIdParam == undefined) {
                ProjectIdParam = 0;
            }
            setIdDeviceSankey(device)
            localStorage.setItem(param.customerId + param.projectId + "IdDeviceSankeyLocal", JSON.stringify(device));
            funcGetDataLoad(ProjectIdParam, type, typeTime, fromDate, toDate, activeButton, systemTypeId, device, data)
        }
    }

    const callbackFunctionSankey1 = (childData1, data, deviceClass2) => {
        if (childData1.length == 0) {
            $.alert({
                title: t('content.notification'),
                content: t('content.home_page.device_select.device_null'),
            });
        } else {
            setTypeSankey(2);
            const device = childData1.join(', ');
            setDeviceClass2(deviceClass2);
            setDataSelectDevice1(data)
            let ProjectIdParam = param.projectId
            if (ProjectIdParam == undefined) {
                ProjectIdParam = 0;
            }
            setIdDeviceSankey(idDeviceSankey + " ," + device)
            localStorage.setItem(param.customerId + param.projectId + "IdDeviceSankeyLocal", JSON.stringify(idDeviceSankey + " ," + device));
            funcGetDataLoad(ProjectIdParam, type, typeTime, fromDate, toDate, activeButton, systemTypeId, device, data)
        }
    }

    const callbackFunctionSankey2 = (childData2, data, deviceClass3) => {
        if (childData2.length == 0) {
            $.alert({
                title: t('content.notification'),
                content: t('content.home_page.device_select.device_null'),
            });
        } else {
            setTypeSankey(3);
            const device = childData2.join(', ');
            setDeviceClass3(deviceClass3);
            setDataSelectDevice2(data)
            let ProjectIdParam = param.projectId
            if (ProjectIdParam == undefined) {
                ProjectIdParam = 0;
            }
            setIdDeviceSankey(idDeviceSankey + " ," + device)
            localStorage.setItem(param.customerId + param.projectId + "IdDeviceSankeyLocal", JSON.stringify(idDeviceSankey + " ," + device));
            funcGetDataLoad(ProjectIdParam, type, typeTime, fromDate, toDate, activeButton, systemTypeId, device, data)
        }
    }

    const callbackFunctionSankey3 = (childData3, data, deviceClass4) => {
        if (childData3.length == 0) {
            $.alert({
                title: t('content.notification'),
                content: t('content.home_page.device_select.device_null'),
            });
        } else {
            const device = childData3.join(', ');
            let ProjectIdParam = param.projectId
            if (ProjectIdParam == undefined) {
                ProjectIdParam = 0;
            }
            setIdDeviceSankey(idDeviceSankey + " ," + device)
            localStorage.setItem(param.customerId + param.projectId + "IdDeviceSankeyLocal", JSON.stringify(idDeviceSankey + " ," + device));

            funcGetDataLoad(ProjectIdParam, type, typeTime, fromDate, toDate, activeButton, systemTypeId, device, data)
        }
    }
    const changeDataTablePower = (data) => {
        const filteredData = data.map(item => {
            const { time, total, viewTime, day, ...rest } = item;
            return rest;
        });

        const filteredDataTotal = data.map(item => {
            const { total, viewTime, day, ...rest } = item;
            return rest;
        });

        const totalData = filteredDataTotal.map(item => {
            const total = Object.keys(item)
                .filter(key => key !== "time")
                .reduce((acc, key) => acc + item[key], 0);
            return { ...item, total };
        });

        const columnNames = [];

        filteredData.forEach(item => {
            const keys = Object.keys(item);
            columnNames.push(...keys);
        });

        const uniqueColumnNames = Array.from(new Set(columnNames)).sort((a, b) => a.localeCompare(b, 'en', { sensitivity: 'base' }));
        setDataTablePower(uniqueColumnNames);
        setDataTotalPower(totalData);
    }

    const dataSankeyLocal = JSON.parse(localStorage.getItem(param.customerId + param.projectId + "DataSankeyLocal"));
    const idDeviceSankeyLocal = JSON.parse(localStorage.getItem(param.customerId + param.projectId + "IdDeviceSankeyLocal"));

    useEffect(() => {
        document.title = "Biểu đồ";
        $("#table-chart").hide()
        $("#chartdivChart").show()
        setViewValue(1)
        setTypeView(true)
        setIsLoading(true);
        funcGetSiteByCustomerId();
        let ProjectIdParam = param.projectId
        let customerId = param.customerId
        if (ProjectIdParam == undefined) {
            ProjectIdParam = 0;
        }
        let today = new Date();
        var fromTime = ""
        var toTime = ""
        // if (type != 8) {
        fromTime = moment(today).format("YYYY-MM-DD") + " 00:00:00";
        toTime = moment(today).format("YYYY-MM-DD") + " 23:59:59";
        // } else {
        //     fromTime = moment(today).format("YYYY-MM-DD");
        //     toTime = moment(today).format("YYYY-MM-DD");
        // }

        setFromDate(fromTime)
        setToDate(toTime)
        setActiveButton(1)
        setTypeFormat("yy-mm-dd")
        setFDate(new Date());
        setTDate(new Date());
        checkAuthorization(ProjectIdParam, type, 1, fromTime, toTime, 1, systemTypeId)
        setProjectId(ProjectIdParam)
        setCustomerId(customerId)
        setDataDeviceName(undefined)
        setDeviceId("");
    }, [param.projectId, param.customerId]);

    const funcSetSystemTypeId = (e) => {
        if (type != 6) {
            setIsLoading(true);
        }
        setViewValue(1)
        setTypeView(true)
        setTypeFormat("yy-mm-dd")
        let systemTypeId = e.target.value;
        let priorityAll = []
        let lastType = 0;

        if (systemTypeId == 1) {
            priorityAll = priorityLoad;
        } else if (systemTypeId == 2) {
            priorityAll = prioritySolar;
        } else if (systemTypeId == 3) {
            priorityAll = priorityWind;
        } else if (systemTypeId == 4) {
            priorityAll = priorityBattery;
        } else if (systemTypeId == 5) {
            priorityAll = priorityGrid;
        }
        if (priorityAll != null) {
            const numbersArray = priorityAll.trim() !== '' ? priorityAll.split(',') : [0];
            lastType = numbersArray.length > 0 ? parseInt(numbersArray[0]) : '';
            setType(lastType);
            setPriorityAll(priorityAll)
        } else {
            setType(0);
            setPriorityAll([])
        }
        setSystemTypeId(() => systemTypeId)
        funcGetDataLoad(projectId, lastType, typeTime, fromDate, toDate, activeButton, systemTypeId, deviceId, null, null, undefined)
    }


    const funcGetSiteByCustomerId = async () => {
        let cusId = param.customerId
        $('#site-list').hide();
        $('#site-loading').show();
        setCustomerId(() => cusId)
        let res = await ProjectService.getProjectByCustomerId(cusId);
        if (res.status === 200) {
            setProjects(() => res.data)
            setProjectIdSelected(() => 0)
            $('#site-loading').hide();
            $('#site-list').show();
        }
    }
    const reselectSankey = (e) => {
        setTypeSankey(0);
        setDeviceClass1();
        setDeviceClass2();
        setDeviceClass3();
        setDataSankey(null);
        setDataSankeyClass1();
        setDataSankeyClass2();
        setDataSankeyClass3();
        setDataSelectDevice1();
        setDataSelectDevice2();
        drawChartSankey(null, null);
    }

    const checkAuthorization = async (projectId, type, typeTime, fromDate, toDate, option, systemTypeId) => {
        let response = await UserService.getUserByUsername();
        let firstType = 0;
        if (response.status === 200) {
            const userData = response.data;
            systemTypeId = userData.prioritySystem;
            setSystemTypeId(systemTypeId);
            let priorityAll = []
            if (systemTypeId == 1) {
                priorityAll = userData.priorityLoad;
            } else if (systemTypeId == 2) {
                priorityAll = userData.prioritySolar;
            } else if (systemTypeId == 3) {
                priorityAll = userData.priorityWind;
            } else if (systemTypeId == 4) {
                priorityAll = userData.priorityBattery;
            } else if (systemTypeId == 5) {
                priorityAll = userData.priorityGrid;
            }
            const numbersArray = priorityAll.trim() !== '' ? priorityAll.split(',') : [0];
            firstType = numbersArray.length > 0 ? parseInt(numbersArray[0]) : '';
            setType(firstType);
            setPriorityAll(priorityAll)
            setPriorityLoad(userData.priorityLoad)
            setPrioritySolar(userData.prioritySolar)
            setPriorityGrid(userData.priorityGrid)
            setPrioritBattery(userData.priorityBattery)
            setPriorityWind(userData.priorityWind)
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
        if (firstType != 0) {
            if (check) {
                if (role === "ROLE_ADMIN") {
                    funcGetDataLoad(projectId, firstType, typeTime, fromDate, toDate, option, systemTypeId, "", null, null, undefined)
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
                                    funcGetDataLoad(projectId, firstType, typeTime, fromDate, toDate, option, systemTypeId, "", null, null, undefined)
                                    setAccessDenied(false)
                                }
                            } else {
                                funcGetDataLoad(projectId, firstType, typeTime, fromDate, toDate, option, systemTypeId, "", null, null, undefined)
                                setAccessDenied(false)
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
                                    funcGetDataLoad(projectId, firstType, typeTime, fromDate, toDate, option, systemTypeId, "", null, null, undefined)
                                    setAccessDenied(false)
                                }
                            } else {
                                funcGetDataLoad(projectId, firstType, typeTime, fromDate, toDate, option, systemTypeId, "", null, null, undefined)
                                setAccessDenied(false)
                            }
                        }
                    }
                }
            }
        }
    }

    const funcGetDataLoad = async (projectId, type, typeTime, fromDate, toDate, option, systemTypeId, deviceId, dataSankeyClass, deviceName, dataDeviceName, dataDeviceSelect) => {
        let ids = ""
        let resProject = [];
        let cusId = param.customerId;
        setDeviceType(null);
        setCustomerId(() => cusId)
        if (role === "ROLE_ADMIN" || role === "ROLE_MOD") {
            resProject = await ProjectService.getProjectByCustomerId(cusId);
        }
        if (role === "ROLE_USER") {
            let re = await ProjectService.getProIds(userName)
            if (re.status === 200 && re.data !== '') {
                ids = re.data
            }
            resProject = await ProjectService.getProjectIds(userName);
        }
        setProjects(resProject.data)

        if (projectId != 0) {
            let resProjectName = await ProjectService.getProject(projectId);
            setProjectName(resProjectName.data.projectName)
        }

        if (type == 1) {
            let resDevice = await DeviceService.getListDeviceCalculateFlag(projectId, systemTypeId, null, null, null);
            let res = await ChartService.getChartLoad(cusId, projectId, typeTime, fromDate, toDate, deviceId, systemTypeId, ids);
            if (res.status === 200) {
                setDataNow(() => res.data.dataNow)
                setDataComp(() => res.data.dataComp)
                setDataLoadAllSite(() => res.data.dataLoadAllSite)
                changeDataTablePower(res.data.dataLoadAllSite)
                setData(() => res.data.dataComp.concat(res.data.dataNow));
                setIsLoading(false);
                drawChartEnergy(res.data.dataLoadAllSite, res.data.dataComp, resProject.data, projectId, typeTime, resDevice.data, dataDeviceName, fromDate);
            }

        } else if (type == 2) {
            let res = await ChartService.getChartLoadPower(cusId, projectId, typeTime, fromDate, toDate, deviceId, systemTypeId, ids);
            if (res.status === 200) {
                setDataNow(() => res.data.dataNow)
                setDataComp(() => res.data.dataComp)
                setDataLoadAllSite(() => res.data.dataLoadAllSite)
                changeDataTablePower(res.data.dataLoadAllSite)
                setData(() => res.data.dataComp.concat(res.data.dataNow));
                setIsLoading(false);
                drawChartPower(res.data.dataLoadAllSite, resProject.data, projectId);
            }

        } else if (type == 3) {
            let res = await ChartService.getChartLoadCost(cusId, projectId, typeTime, fromDate, toDate, deviceId, systemTypeId, ids);
            if (res.status === 200) {
                setDataNow(() => res.data.data)
                setData(() => res.data.data);
                setIsLoading(false);
                setDataLoadAllSite(() => res.data.data)
                const filteredData = res.data.data.map(item => {
                    const filteredFields = Object.keys(item).filter(key => !key.endsWith('EpChartCostData'));
                    const filteredItem = filteredFields.reduce((acc, key) => {
                        acc[key] = item[key];
                        return acc;
                    }, {});
                    return filteredItem;
                });
                changeDataTablePower(filteredData)
                drawChartCost(res.data.data, resProject.data, projectId, option);
            }
        } else if (type == 4) {
            let res = await ChartService.getChartLoadHeat(cusId, projectId, typeTime, fromDate, toDate, deviceId, systemTypeId, ids);
            let resDevice = [];
            let data = []
            if (dataDeviceSelect != undefined) {
                if (dataDeviceSelect.length > 0) {
                    data = dataDeviceSelect;
                } else {
                    resDevice = await DeviceService.getListDeviceCalculateFlag(projectId, systemTypeId, null, null, null);
                    data = resDevice.data;
                }
            } else {
                resDevice = await DeviceService.getListDeviceCalculateFlag(projectId, systemTypeId, null, null, null);
                data = resDevice.data;
            }

            if (res.status === 200) {
                setDataNow(() => res.data.data)
                setData(() => res.data.data);
                setIsLoading(false);
                drawChartHeat(res.data.data, typeTime, systemTypeId == 1 ? "load" : (systemTypeId == 2 ? "solar" : "grid"), data);
            }
        } else if (type == 5) {
            if (projectId != 0) {
                let res = await ChartService.getDataTemperature(cusId, projectId, typeTime, fromDate, toDate, deviceId, systemTypeId, ids);
                if (res.status === 200) {
                    setIsLoading(false);
                    drawChartTemperature(res.data.dataTemperature);
                    changeDataTablePower(res.data.dataTemperature)
                }
            } else {
                setIsLoading(false);
                $.alert({
                    title: t('content.notification'),
                    content: t('content.home_page.chart.project_null'),
                });
            }
        } else if (type == 6) {
            if (projectId != 0) {
                if (deviceId != "") {
                    let res = await ChartService.getChartSankey(cusId, projectId, typeTime, fromDate, toDate, deviceId, systemTypeId, ids);
                    if (res.status === 200) {
                        if (typeSankey == 0) {
                            setDataSankey(res.data.data)
                        } if (typeSankey == 1) {
                            setDataSankeyClass1(res.data.data)
                        } if (typeSankey == 2) {
                            setDataSankeyClass2(res.data.data)
                        } if (typeSankey == 3) {
                            setDataSankeyClass3(res.data.data)
                        } if (typeSankey != 0) {
                            drawChartSankey(res.data.data, dataSankeyClass);
                            setIsLoading(false);
                        }
                        setDeviceId("");
                    }
                } else {
                    let checkIdSankeyLocal = JSON.parse(localStorage.getItem(param.customerId + param.projectId + "IdDeviceSankeyLocal"));
                    if (checkIdSankeyLocal == undefined) {
                        checkIdSankeyLocal = "";
                    }
                    let res = await ChartService.getChartSankey(cusId, projectId, typeTime, fromDate, toDate, checkIdSankeyLocal, systemTypeId, ids);
                    if (res.status === 200) {
                        let data = res.data.data;
                        let listNew = [];
                        if (data.length > 0 && dataSankeyLocal != undefined) {
                            listNew = dataSankeyLocal.list.slice();
                            for (let i = 0; i < data.length; i++) {
                                let x = data[i].deviceName;
                                let y = data[i].epIn;
                                listNew = listNew.map(item => {
                                    if (item.from.includes(x)) {
                                        return { ...item, from: x + ": " + y + " (kWh)" };
                                    }
                                    return item;
                                });
                            }

                            for (let i = 0; i < data.length; i++) {
                                let x = data[i].deviceName;
                                let y = data[i].epIn;
                                listNew = listNew.map(item => {
                                    if (item.to.includes(x)) {
                                        return { ...item, to: x + ": " + y + " (kWh)", value: y };
                                    }
                                    return item;
                                });
                            }
                            setIsLoading(false);
                            drawChartSankey(null, null, listNew);
                        } else {
                            setIsLoading(false);
                            drawChartSankey(null, null, dataSankeyLocal);
                        }
                    }
                }
            } else {
                setIsLoading(false);
                $.alert({
                    title: t('content.notification'),
                    content: t('content.home_page.chart.project_null'),
                });
            }
        } else if (type == 7) {
            if (projectId != 0) {
                let res = await ChartService.getDataDischargeIndicator(cusId, projectId, typeTime, fromDate, toDate, deviceId, systemTypeId, ids);
                if (res.status === 200) {
                    const dataHTR02 = res.data.dataHTR02;
                    const dataAMS01 = res.data.dataAMS01;
                    const mergedMap = new Map();
                    dataAMS01.forEach((amsItem) => {
                        mergedMap.set(amsItem.time, { ...amsItem });
                    });

                    dataHTR02.forEach((htrItem) => {
                        const existingItem = mergedMap.get(htrItem.time);
                        if (existingItem) {
                            mergedMap.set(htrItem.time, { ...existingItem, ...htrItem });
                        } else {
                            mergedMap.set(htrItem.time, { ...htrItem });
                        }
                    });
                    setIsLoading(false);
                    drawChartDischargeIndicator(Array.from(mergedMap.values()));
                    changeDataTablePower(Array.from(mergedMap.values()))
                }
            } else {
                setIsLoading(false);
                $.alert({
                    title: t('content.notification'),
                    content: t('content.home_page.chart.project_null'),
                });
            }
        } else if (type == 8) {
            if (projectId != 0) {
                let res = await ChartService.getChartEnergyPlan(cusId, projectId, typeTime - 1, systemTypeId, fromDate, toDate, deviceId, ids);
                let nameProDev = "";
                if (res.status === 200) {
                    if (res.data != null) {
                        if (deviceName != null && deviceName != "") {
                            nameProDev = deviceName;
                        } else {
                            nameProDev = res.data[0].name;
                        }
                        // if (typeTime == 1) {
                        if (res.data[0].listDataPower != null && res.data[0].listDataPower[0] != undefined) {
                            drawChartEnergyPlan(res.data, typeTime - 1, nameProDev);
                        } else {
                            drawChartEnergyPlan()
                        }
                        // } else {
                        //     if (res.data[0].listDataPower != null && res.data[0].listDataPower[0] != undefined) {
                        //         drawChartEnergyPlan(res.data, typeTime - 1, nameProDev);
                        //     } else {
                        //         drawChartEnergyPlan()
                        //     }
                        // }
                    }
                    setIsLoading(false);
                }
            } else {
                setIsLoading(false);
                $.alert({
                    title: t('content.notification'),
                    content: t('content.home_page.chart.project_null'),
                });
            }
        } else if (type == 9) {
            let res = await ChartService.getChartLoadCompare(cusId, projectId, typeTime, fromDate, toDate, deviceId, systemTypeId, ids);
            if (res.status === 200) {
                setIsLoading(false);
                drawChartEnergyComparison(res.data.dataNow);
                setDataTablePowerType9(res.data.dataNow);
            }
            setIsLoading(false);
        }
    }

    const exportDataChart = async () => {
        let ids = ""
        if (role === "ROLE_USER") {
            let re = await ProjectService.getProIds(userName)
            if (re.status === 200 && re.data !== '') {
                ids = re.data
            }
        }

        setIsLoading(true)
        let cusId = param.customerId;
        if (type == 1 || type == 0) {
            let res = await ChartService.exportDataChartLoad(cusId, projectId, typeTime, fromDate, toDate, deviceId, systemTypeId, ids);
            if (res.status == 200) {
                setIsLoading(false)
            }
        } else if (type == 2) {
            let res = await ChartService.exportDataChartPower(cusId, projectId, typeTime, fromDate, toDate, deviceId, systemTypeId, ids);
            if (res.status == 200) {
                setIsLoading(false)
            }
        } else if (type == 3) {
            let res = await ChartService.exportDataChartCost(cusId, projectId, typeTime, fromDate, toDate, deviceId, systemTypeId, ids);
            if (res.status == 200) {
                setIsLoading(false)
            }
        } else if (type == 4) {
            // let res = await ChartService.getChartLoadHeat(cusId, projectId, typeTime, fromDate, toDate, deviceId, systemTypeId);  
        }
    }

    const onChangeValueEnergy = async (option) => {
        if (type != 6) {
            setIsLoading(true);
        }
        setActiveButton(option)
        if (option == 5) {
            setTypeFormat("yy-mm-dd")
            setShowToDateTab1(!showToDateTab1)
        } else {
            setShowToDateTab1(false)
            setFDate(new Date());
            setTDate(new Date());
            if (option == 1) {
                setTypeFormat("yy-mm-dd")
            }
            if (option == 2) {
                setTypeFormat("yy-mm")
                setViewCalender("month")
            }
            if (option == 3 || option == 4) {
                setTypeFormat("yy")
                setViewCalender("year")
            }
            if (option == 6) {
                setTypeFormat("yy-mm-dd")
                setViewCalender("")
            }
        }
        let time = option
        const today = new Date();
        let fromTime = "";
        let toTime = "";
        setTypeTime(time)

        const selectedDay = today.getDay();
        const startDate = new Date();
        startDate.setDate(startDate.getDate() - selectedDay + 1);

        const endDate = new Date(startDate);
        endDate.setDate(endDate.getDate() + 6);

        if (time == 1) {
            //Hôm nay
            today.setDate(today.getDate());
            fromTime = moment(today).format("YYYY-MM-DD") + " 00:00:00";
            today.setDate(today.getDate());
            toTime = moment(today).format("YYYY-MM-DD") + " 23:59:59";
            setFromDate(fromTime)
            setToDate(toTime)

        } else if (time == 2) {
            //Tháng này
            today.setMonth(today.getMonth());
            fromTime = moment(today).format("YYYY-MM") + "-01" + " 00:00:00";
            today.setMonth(today.getMonth());
            toTime = moment(today).format("YYYY-MM-DD") + " 23:59:59";
            setFromDate(fromTime)
            setToDate(toTime)

        } else if (time == 3 || time == 4) {
            //Năm nay
            today.setYear(today.getFullYear());
            fromTime = moment(today).format("YYYY") + "-01-01" + " 00:00:00";
            today.setYear(today.getFullYear());
            toTime = moment(today).format("YYYY-MM-DD") + " 23:59:59";
            setFromDate(fromTime)
            setToDate(toTime)
        }
        else if (time == 6) {
            fromTime = moment(startDate).format("YYYY-MM-DD") + " 00:00:00";
            toTime = moment(endDate).format("YYYY-MM-DD") + " 23:59:59";
            setFromDate(fromTime)
            setToDate(toTime)
        }
        else {
            today.setDate(today.getDate());
            fromTime = moment(today).format("YYYY-MM-DD") + " 00:00:00";
            today.setDate(today.getDate());
            toTime = moment(today).format("YYYY-MM-DD") + " 23:59:59";
            setFromDate(fromTime)
            setToDate(toTime)
        }

        if (type == 8) {
            if (time == 6) {
                fromTime = moment(startDate).format("YYYY-MM-DD");
                toTime = moment(endDate).format("YYYY-MM-DD");
            } else if (time == 5) {
                fromTime = moment(startDate).format("YYYY-MM");
                toTime = moment(endDate).format("YYYY-MM");
                time = 2;
            } else {
                fromTime = null;
                toTime = null;
            }
            setFromDate(fromTime)
            setToDate(toTime)
        }

        funcGetDataLoad(projectId, type, time, fromTime, toTime, option, systemTypeId, deviceId, null, null, dataDeviceName)
    }

    const drawChartSankey = (dataTotal, dataSankeyClass, listNew) => {
        const findEpByIdClass = (deviceId) => {
            const dataFindEp = dataSankey.find(item => item.deviceId === deviceId);
            return dataFindEp ? dataFindEp.epIn : 1;
        };

        const findEpByIdClass1 = (deviceId) => {
            const dataFindEp = dataTotal.find(item => item.deviceId === deviceId);
            return dataFindEp ? dataFindEp.epIn : 1;
        };

        const findEpByIdClass2 = (deviceId) => {
            const dataFindEp = dataSankeyClass1.find(item => item.deviceId === deviceId);
            return dataFindEp ? dataFindEp.epIn : 1;
        };

        const findEpByIdClass3 = (deviceId) => {
            const dataFindEp = dataSankeyClass2.find(item => item.deviceId === deviceId);
            return dataFindEp ? dataFindEp.epIn : 1;
        };

        let data = [];
        let dataClass1 = [];
        let dataClass1x = [];
        let dataClass2y = [];

        if (dataTotal != null) {
            if (dataSelectDevice2 != null) {
                for (let i = 0; i < dataSelectDevice1.length; i++) {
                    for (let j = 0; j < dataSelectDevice1[i].list[0].length; j++) {
                        dataClass1.push({
                            from: dataSelectDevice1[i].objectTotal.selectedDevicesTotal[0].deviceName
                                + ": " + findEpByIdClass(dataSelectDevice1[i].objectTotal.selectedDevicesTotal[0].deviceId) + " (kWh)",
                            to: dataSelectDevice1[i].list[0][j].deviceName
                                + ": " + findEpByIdClass2(dataSelectDevice1[i].list[0][j].deviceId) + " (kWh)",
                            value: findEpByIdClass2(dataSelectDevice1[i].list[0][j].deviceId),
                        });
                    }
                }
                for (let i = 0; i < dataSelectDevice2.length; i++) {
                    for (let j = 0; j < dataSelectDevice2[i].list[0].length; j++) {
                        dataClass1x.push({
                            from: dataSelectDevice2[i].objectTotal.selectedDevicesTotal[0].deviceName
                                + ": " + findEpByIdClass2(dataSelectDevice2[i].objectTotal.selectedDevicesTotal[0].deviceId) + " (kWh)",
                            to: dataSelectDevice2[i].list[0][j].deviceName
                                + ": " + findEpByIdClass3(dataSelectDevice2[i].list[0][j].deviceId) + " (kWh)",
                            value: findEpByIdClass3(dataSelectDevice2[i].list[0][j].deviceId),
                        });
                    }
                }
                if (dataSankeyClass != null) {
                    for (let i = 0; i < dataSankeyClass.length; i++) {
                        for (let j = 0; j < dataSankeyClass[i].list[0].length; j++) {
                            dataClass2y.push({
                                from: dataSankeyClass[i].objectTotal.selectedDevicesTotal[0].deviceName
                                    + ": " + findEpByIdClass3(dataSankeyClass[i].objectTotal.selectedDevicesTotal[0].deviceId) + " (kWh)",
                                to: dataSankeyClass[i].list[0][j].deviceName
                                    + ": " + findEpByIdClass1(dataSankeyClass[i].list[0][j].deviceId) + " (kWh)",
                                value: findEpByIdClass1(dataSankeyClass[i].list[0][j].deviceId),
                            });
                        }
                    }
                }
                data = [...dataClass1, ...dataClass1x, ...dataClass2y];
            } else {

                if (dataSelectDevice1 != null) {
                    for (let i = 0; i < dataSelectDevice1.length; i++) {
                        for (let j = 0; j < dataSelectDevice1[i].list[0].length; j++) {
                            dataClass1.push({
                                from: dataSelectDevice1[i].objectTotal.selectedDevicesTotal[0].deviceName
                                    + ": " + findEpByIdClass(dataSelectDevice1[i].objectTotal.selectedDevicesTotal[0].deviceId) + " (kWh)",
                                to: dataSelectDevice1[i].list[0][j].deviceName
                                    + ": " + findEpByIdClass2(dataSelectDevice1[i].list[0][j].deviceId) + " (kWh)",
                                value: findEpByIdClass2(dataSelectDevice1[i].list[0][j].deviceId),
                            });
                        }
                    }
                    if (dataSankeyClass != null) {
                        for (let i = 0; i < dataSankeyClass.length; i++) {
                            for (let j = 0; j < dataSankeyClass[i].list[0].length; j++) {
                                dataClass1x.push({
                                    from: dataSankeyClass[i].objectTotal.selectedDevicesTotal[0].deviceName
                                        + ": " + findEpByIdClass2(dataSankeyClass[i].objectTotal.selectedDevicesTotal[0].deviceId) + " (kWh)",
                                    to: dataSankeyClass[i].list[0][j].deviceName
                                        + ": " + findEpByIdClass1(dataSankeyClass[i].list[0][j].deviceId) + " (kWh)",
                                    value: findEpByIdClass1(dataSankeyClass[i].list[0][j].deviceId),
                                });
                            }
                        }
                    }
                    data = [...dataClass1, ...dataClass1x];
                } else {
                    if (dataSankeyClass != null) {
                        for (let i = 0; i < dataSankeyClass.length; i++) {
                            for (let j = 0; j < dataSankeyClass[i].list[0].length; j++) {
                                data.push({
                                    from: dataSankeyClass[i].objectTotal.selectedDevicesTotal[0].deviceName
                                        + ": " + findEpByIdClass(dataSankeyClass[i].objectTotal.selectedDevicesTotal[0].deviceId) + " (kWh)",
                                    to: dataSankeyClass[i].list[0][j].deviceName
                                        + ": " + findEpByIdClass1(dataSankeyClass[i].list[0][j].deviceId) + " (kWh)",
                                    value: findEpByIdClass1(dataSankeyClass[i].list[0][j].deviceId),
                                });
                            }
                        }
                    }
                }
            }
        }
        else {
            if (listNew != undefined) {
                if (dataSankeyLocal.name == userName) {
                    data = listNew;
                }
            }
        }
        const dataLocal = {
            name: userName,
            list: data,
        };

        if (data.length > 0) {
            localStorage.setItem(param.customerId + param.projectId + "DataSankeyLocal", JSON.stringify(dataLocal));
        }

        am5.array.each(am5.registry.rootElements, function (root) {
            if (root) {
                if (root.dom.id == "chartdivChart") {
                    root.dispose();
                }
            }
        });

        let element = document.getElementById("chartdivChart")
        if (element != null) {

            am5.ready(function () {
                let root = am5.Root.new("chartdivChart");
                root._logo.dispose();
                root.setThemes([
                    am5themes_Animated.new(root)
                ]);
                var series = root.container.children.push(am5flow.Sankey.new(root, {
                    sourceIdField: "from",
                    targetIdField: "to",
                    valueField: "value",
                    paddingRight: 200,
                    paddingTop: 33
                }));

                series.children.unshift(am5.Label.new(root, {
                    text: t('content.home_page.chart.sankey_chart').toUpperCase(),
                    fontSize: 28,
                    fontWeight: "700",
                    textAlign: "center",
                    x: am5.percent(50),
                    y: am5.percent(-2),
                    centerX: am5.percent(50),
                    paddingTop: 0,
                    paddingBottom: 10
                }));

                series.links.template.setAll({
                    tooltipText: "[bold]{sourceId}[/]  --> [bold]{targetId}"

                });

                series.nodes.get("colors").set("colors", [
                    am5.color(0xe41a1c),
                    am5.color(0x377eb8),
                    am5.color(0x4daf4a),
                    am5.color(0x984ea3),
                    am5.color(0xff7f00),
                    am5.color(0xffff33),
                    am5.color(0xa65628),
                    am5.color(0x999999),
                    am5.color(0x66c2a5),
                    am5.color(0xfc8d62),
                    am5.color(0xe78ac3),
                    am5.color(0xa6d854),
                    am5.color(0xffd92f),
                ]);

                // series.nodes.get("colors").set("step", 2);


                // Set data
                // https://www.amcharts.com/docs/v5/charts/flow-charts/#Setting_data
                // console.log(data);
                // console.log(dataSankeyLocal);
                // if (data.length == 0) {
                //     // localStorage.setItem("dataSankeyLocal", JSON.stringify(data));
                //     series.data.setAll(data);
                // } else {
                //     series.data.setAll(dataSankeyLocal);
                // }
                series.data.setAll(data);

                // Make stuff animate on load
                series.appear(1000, 100);

            });
        }
    };

    const drawChartHeat = (data, type, typeModule, dataDevice) => {
        let viewTimeInDay = "";

        if (data.length > 0) {
            viewTimeInDay = [{ viewTime: data[0].viewTime }]
        }

        am5.array.each(am5.registry.rootElements, function (root) {
            if (root) {
                if (root.dom.id == "chartdivChart") {
                    root.dispose();
                }
            }
        });

        let element = document.getElementById("chartdivChart")
        if (element != null) {

            am5.ready(function () {
                let root = am5.Root.new("chartdivChart");
                root.setThemes([
                    am5themes_Animated.new(root)
                ]);
                root._logo.dispose();

                // Set themes
                // https://www.amcharts.com/docs/v5/concepts/themes/
                root.setThemes([am5themes_Animated.new(root)]);

                // Create chart
                // https://www.amcharts.com/docs/v5/charts/xy-chart/
                let chart = root.container.children.push(
                    am5xy.XYChart.new(root, {
                        panX: true,
                        panY: false,
                        wheelX: "none",
                        wheelY: "none",
                        layout: root.verticalLayout
                    })
                );

                chart.children.unshift(am5.Label.new(root, {
                    text: t('content.home_page.chart.load_level_chart').toUpperCase(),
                    fontSize: 28,
                    fontWeight: "700",
                    textAlign: "center",
                    x: am5.percent(50),
                    y: am5.percent(-2),
                    centerX: am5.percent(50),
                    paddingTop: 0,
                    paddingBottom: 0
                }));

                // Create axes and their renderers
                var yRenderer = am5xy.AxisRendererY.new(root, {
                    visible: false,
                    minGridDistance: 20,
                    rotation: 90,
                    fontSize: 10
                });


                yRenderer.grid.template.set("visible", false);

                var yAxis = chart.yAxes.push(am5xy.CategoryAxis.new(root, {
                    maxDeviation: 0,
                    renderer: yRenderer,
                    categoryField: "hour"
                }));

                var xRenderer = am5xy.AxisRendererX.new(root, {
                    visible: false,
                    minGridDistance: 30,
                    opposite: true
                });

                xRenderer.grid.template.set("visible", false);

                var xAxis = chart.xAxes.push(am5xy.CategoryAxis.new(root, {
                    renderer: xRenderer,
                    categoryField: type == 6 ? "day" : "viewTime",
                    fontSize: 10,
                }));

                yAxis.children.moveValue(am5.Label.new(root, { text: t('content.hour'), rotation: -0, y: am5.p50, centerX: am5.p50 }), 0);
                if (type != 6) {
                    xAxis.children.moveValue(am5.Label.new(root, { text: t('content.day'), rotation: 0, x: am5.p50, centerX: am5.p50 }), 0);
                } else {
                    xAxis.children.moveValue(am5.Label.new(root, { text: t('content.date_of_week'), rotation: 0, x: am5.p50, centerX: am5.p50 }), 0);
                }

                // xAxis.children.moveValue(am5.Label.new(root, { text: `Tiền điện tích lũy [[VND]]`, rotation: -90, y: am5.p50, centerX: am5.p50 }), 0);

                // Create series
                // https://www.amcharts.com/docs/v5/charts/xy-chart/#Adding_series
                var series = chart.series.push(am5xy.ColumnSeries.new(root, {
                    calculateAggregates: true,
                    stroke: am5.color(0xffffff),
                    clustered: false,
                    xAxis: xAxis,
                    yAxis: yAxis,
                    categoryXField: type == 6 ? "day" : "viewTime",
                    categoryYField: "hour",
                    valueField: "epIn"
                }));

                series.columns.template.setAll({
                    tooltipText: type == 6 ? "{viewTime}[/]\n[bold]{hour}h: [bold]{epIn} kWh" :
                        "[bold]{hour}h: [bold]{epIn} kWh",
                    strokeOpacity: 1,
                    strokeWidth: 2,
                    width: am5.percent(100),
                    height: am5.percent(100)
                });

                var heatLegend = chart.bottomAxesContainer.children.push(am5.HeatLegend.new(root, {
                    orientation: "horizontal",
                    endColor: am5.color(0xfe131a),
                    startColor: am5.color(0x00FF64),
                    stepCount: 200
                }));

                series.columns.template.events.on("pointerover", function (event) {
                    var di = event.target.dataItem;
                    if (di) {
                        heatLegend.showValue(di.get("value", 0));
                    }
                });

                series.events.on("datavalidated", function () {
                    heatLegend.set("startValue", series.getPrivate("valueLow"));
                    heatLegend.set("endValue", series.getPrivate("valueHigh"));
                });

                series.set("heatRules", [{
                    target: series.columns.template,
                    min: am5.color(0x00FF64),
                    max: am5.color(0xfe131a),
                    dataField: "value",
                    key: "fill"
                }]);
                // Add legend
                var legend = chart.children.push(am5.Legend.new(root, {
                    nameField: "deviceName",
                    fillField: am5.color(0x00FF64),
                    strokeField: am5.color(0x00FF64),
                    centerX: am5.percent(50),
                    x: am5.percent(50)
                }));

                legend.data.setAll(dataDevice);

                // Set data
                // https://www.amcharts.com/docs/v5/charts/xy-chart/#Setting_data
                series.data.setAll(data);
                yAxis.data.setAll(dataTime.hour);
                if (type < 2) {
                    xAxis.data.setAll(viewTimeInDay)
                } else if (type == 2) {
                    xAxis.data.setAll(dataTime.dates)
                } else if (type == 4) {
                    xAxis.data.setAll(dataTime.months)
                } else if (type == 5) {
                    xAxis.data.setAll(dataTime.dates)
                } else if (type == 6) {
                    xAxis.data.setAll(dataTime.weeks)
                    // xAxis.data.setAll(viewTimeInDay)
                } else {
                    xAxis.data.setAll(dataTime.months)
                }

                // Make stuff animate on load
                // https://www.amcharts.com/docs/v5/concepts/animations/#Initial_animation
                chart.appear(1000, 100);

            });
        }
    };

    const drawChartCost = (datax, projects, projectId, option) => {
        let data = [];
        if (option === 3) {
            const transformedData = datax.map(item => {
                const timeParts = item.time.split('-');
                const yearMonth = `${timeParts[0]}-${timeParts[1]}`;
                return {
                    ...item,
                    time: yearMonth,
                    total: 0
                };
            });
            const resultMap = {};

            for (const item of transformedData) {
                const { time, total, ...companies } = item;
                if (!resultMap[time]) {
                    resultMap[time] = { time, total, ...companies };
                } else {
                    resultMap[time].total += total;

                    for (const company in companies) {
                        if (resultMap[time][company]) {
                            resultMap[time][company] += companies[company];
                        } else {
                            resultMap[time][company] = companies[company];
                        }
                    }
                }
            }
            data = Object.values(resultMap);
            data.forEach(item => {
                let total = 0;
                for (const key in item) {
                    if (key !== 'time') {
                        total += item[key];
                    }
                }
                item.total = total;
            });

            let cumulativeTotal = 0;

            data.forEach(item => {
                if (item.total !== undefined) {
                    cumulativeTotal += item.total;
                }
                item.total = cumulativeTotal;
            });
        } else {
            data = datax;
        }

        am5.array.each(am5.registry.rootElements, function (root) {
            if (root) {
                if (root.dom.id == "chartdivChart") {
                    root.dispose();
                }
            }
        });
        let element = document.getElementById("chartdivChart")
        if (element != null) {

            let root = am5.Root.new("chartdivChart");
            root._logo.dispose();
            root.setThemes([
                am5themes_Animated.new(root)
            ]);


            // Create chart
            // https://www.amcharts.com/docs/v5/charts/xy-chart/
            var chart = root.container.children.push(am5xy.XYChart.new(root, {
                panX: true,
                panY: false,
                wheelX: "panX",
                wheelY: "zoomX",
                layout: root.verticalLayout
            }));

            chart.children.unshift(am5.Label.new(root, {
                text: t('content.home_page.chart.cost_chart').toUpperCase(),
                fontSize: 28,
                fontWeight: "700",
                textAlign: "center",
                x: am5.percent(50),
                y: am5.percent(-2),
                centerX: am5.percent(50),
                paddingTop: 0,
                paddingBottom: 0
            }));

            chart.get("colors").set("colors", [
                am5.color(0xe41a1c),
                am5.color(0x377eb8),
                am5.color(0x4daf4a),
                am5.color(0x984ea3),
                am5.color(0xff7f00),
                am5.color(0xffff33),
                am5.color(0xa65628),
                am5.color(0x999999),
                am5.color(0x66c2a5),
                am5.color(0xfc8d62),
                am5.color(0xe78ac3),
                am5.color(0xa6d854),
                am5.color(0xffd92f),
            ]);

            // Add scrollbar
            // https://www.amcharts.com/docs/v5/charts/xy-chart/scrollbars/
            // chart.set("scrollbarX", am5.Scrollbar.new(root, {
            //     orientation: "horizontal"
            // }));

            // Create axes
            // https://www.amcharts.com/docs/v5/charts/xy-chart/axes/
            var xRenderer = am5xy.AxisRendererX.new(root, {
                minGridDistance: 10,
                // cellStartLocation: 0.2,
                // cellEndLocation: 0.8
            });
            xRenderer.labels.template.setAll({
                rotation: -60,
                paddingTop: 0,
                paddingRight: 0,
                fontSize: 10
            });
            var xAxis = chart.xAxes.push(am5xy.CategoryAxis.new(root, {
                categoryField: "time",
                renderer: xRenderer,
                tooltip: am5.Tooltip.new(root, {})
            }));

            xRenderer.grid.template.setAll({
                location: 1
            })

            xAxis.data.setAll(data);

            var yAxis = chart.yAxes.push(am5xy.ValueAxis.new(root, {
                min: 0,
                renderer: am5xy.AxisRendererY.new(root, {
                    strokeOpacity: 0.1
                })
            }));
            var yAxis2 = chart.yAxes.push(
                am5xy.ValueAxis.new(root, {
                    maxDeviation: 0.3,
                    syncWithAxis: yAxis,
                    renderer: am5xy.AxisRendererY.new(root, { opposite: true })
                })
            );

            yAxis.children.moveValue(am5.Label.new(root, { text: t('content.home_page.chart.cost') + `[[VND]]`, rotation: -90, y: am5.p50, centerX: am5.p50 }), 0);
            yAxis2.children.moveValue(am5.Label.new(root, { text: t('content.home_page.chart.cost_accumulated') + `[[VND]]`, rotation: -90, y: am5.p50, centerX: am5.p50 }), 1);

            // Add legend
            // https://www.amcharts.com/docs/v5/charts/xy-chart/legend-xy-series/
            var legend = chart.children.push(am5.Legend.new(root, {
                centerX: am5.p50,
                x: am5.p50
            }));


            // Add series
            // https://www.amcharts.com/docs/v5/charts/xy-chart/series/
            function makeSeries(name, fieldName, ep) {
                var series = chart.series.push(am5xy.ColumnSeries.new(root, {
                    name: name,
                    stacked: true,
                    xAxis: xAxis,
                    yAxis: yAxis,
                    valueYField: fieldName,
                    categoryXField: "time",
                    valueXField: ep,
                }));

                series.columns.template.setAll({
                    // tooltipText: "[bold]{name}[/]\n{time}[/]\nTiền điện: [bold]{valueY} [[VND]]",
                    tooltipText: "[bold]{name}[/]\n{time}[/]\n" + t('content.home_page.chart.cost') + ": [bold]{valueY} [[VND]] [/]\n" + t('content.home_page.chart.energy_power') + ": [bold]{valueX} [[kWh]]",
                    tooltipY: am5.percent(10),
                    width: am5.percent(90),
                });
                series.data.setAll(data);

                // Make stuff animate on load
                // https://www.amcharts.com/docs/v5/concepts/animations/
                series.appear();

                series.bullets.push(function () {
                    return am5.Bullet.new(root, {
                        sprite: am5.Label.new(root, {
                            text: "{valueY}",
                            fill: root.interfaceColors.get("alternativeText"),
                            centerY: am5.p50,
                            centerX: am5.p50,
                            populateText: true
                        })
                    });
                });
            }
            //bỏ tên cột thừa
            // const filteredData = data.map(item => {
            //     const { time, total, ...rest } = item;
            //     return rest;
            // });

            const filteredData = data.map(item => {
                const { time, total, ...rest } = item;
                const filteredFields = Object.keys(rest).filter(key => !key.endsWith('EpChartCostData'));
                const filteredItem = filteredFields.reduce((acc, key) => {
                    acc[key] = rest[key];
                    return acc;
                }, {});
                return filteredItem;
            });


            //load tên cột
            const columnNames = [];

            filteredData.forEach(item => {
                const keys = Object.keys(item);
                columnNames.push(...keys);
            });

            const uniqueColumnNames = Array.from(new Set(columnNames)).sort((a, b) => a.localeCompare(b, 'en', { sensitivity: 'base' }));;

            if (projectId != 0) {
                uniqueColumnNames.forEach(name => {
                    makeSeries(name, name, name + "EpChartCostData");
                });
            } else {
                projects.forEach(name => {
                    makeSeries(name.projectName, name.projectName, name.projectName + "EpChartCostData");
                });
            }

            var series2 = chart.series.push(
                am5xy.LineSeries.new(root, {
                    name: t('content.home_page.chart.cost_accumulated'),
                    xAxis: xAxis,
                    yAxis: yAxis2,
                    valueYField: "total",
                    categoryXField: "time",
                    tooltip: am5.Tooltip.new(root, {
                        pointerOrientation: "horizontal",
                        labelText: "[bold]{name}[/]\n{time}[/]\n" + t('content.home_page.chart.cost') + ": [bold]{valueY} [[VND]]"
                    }),
                })
            );

            series2.strokes.template.setAll({
                strokeWidth: 3,
                templateField: "strokeSettings",
            });

            series2.data.setAll(data)

            series2.bullets.push(function () {
                return am5.Bullet.new(root, {
                    sprite: am5.Circle.new(root, {
                        strokeWidth: 3,
                        stroke: "blue",
                        radius: 5,
                        fill: root.interfaceColors.get("background"),

                    })
                });
            });
            chart.set("cursor", am5xy.XYCursor.new(root, {}));
            var legend = chart.children.push(
                am5.Legend.new(root, {
                    centerX: am5.p50,
                    x: am5.p50
                })
            );

            legend.data.setAll(chart.series.values);

            // Make stuff animate on load
            // https://www.amcharts.com/docs/v5/concepts/animations/
            chart.appear(1000, 100);
        }

    };

    const drawChartPower = (data, projects, projectId) => {
        am5.array.each(am5.registry.rootElements, function (root) {
            if (root) {
                if (root.dom.id == "chartdivChart") {
                    root.dispose();
                }
            }
        });

        let element = document.getElementById("chartdivChart")
        if (element != null) {

            am5.ready(function () {
                let root = am5.Root.new("chartdivChart");
                root._logo.dispose();
                root.setThemes([
                    am5themes_Animated.new(root)
                ]);

                var chart = root.container.children.push(
                    am5xy.XYChart.new(root, {
                        panX: true,
                        panY: true,
                        wheelX: "panX",
                        wheelY: "zoomX",
                        layout: root.verticalLayout,
                        pinchZoomX: true
                    })
                );

                chart.children.unshift(am5.Label.new(root, {
                    text: t('content.home_page.chart.power_chart').toUpperCase(),
                    fontSize: 28,
                    fontWeight: "700",
                    textAlign: "center",
                    x: am5.percent(50),
                    y: am5.percent(-2),
                    centerX: am5.percent(50),
                    paddingTop: 0,
                    paddingBottom: 0
                }));

                chart.get("colors").set("colors", [
                    am5.color(0xe41a1c),
                    am5.color(0x377eb8),
                    am5.color(0x4daf4a),
                    am5.color(0x984ea3),
                    am5.color(0xff7f00),
                    am5.color(0xffff33),
                    am5.color(0xa65628),
                    am5.color(0x999999),
                    am5.color(0x66c2a5),
                    am5.color(0xfc8d62),
                    am5.color(0xe78ac3),
                    am5.color(0xa6d854),
                    am5.color(0xffd92f),
                ]);

                var cursor = chart.set("cursor", am5xy.XYCursor.new(root, {
                    behavior: "none"
                }));
                cursor.lineY.set("visible", false);

                var xRenderer = am5xy.AxisRendererX.new(root, {
                    minGridDistance: 10,
                    cellStartLocation: 0.2,
                    cellEndLocation: 0.8
                });
                xRenderer.labels.template.setAll({
                    rotation: -60,
                    paddingTop: 0,
                    paddingRight: 0,
                    fontSize: 10,
                    location: 0.5,
                    multiLocation: 0.5
                });
                xRenderer.grid.template.set("location", 0.5);
                // xRenderer.labels.template.setAll({
                //     location: 0.5,
                //     multiLocation: 0.5
                // });

                var xAxis = chart.xAxes.push(
                    am5xy.CategoryAxis.new(root, {
                        categoryField: "time",
                        renderer: xRenderer,
                        tooltip: am5.Tooltip.new(root, {})
                    })
                );

                xAxis.data.setAll(data);

                var yAxis = chart.yAxes.push(
                    am5xy.ValueAxis.new(root, {
                        maxPrecision: 0,
                        renderer: am5xy.AxisRendererY.new(root, {

                        })
                    })
                );

                yAxis.children.moveValue(am5.Label.new(root, { text: t('content.home_page.chart.power') + `[[kW]]`, rotation: -90, y: am5.p50, centerX: am5.p50 }), 0);

                function createSeries(name, field) {
                    var series = chart.series.push(
                        am5xy.LineSeries.new(root, {
                            name: name,
                            xAxis: xAxis,
                            yAxis: yAxis,
                            valueYField: field,
                            categoryXField: "time",
                            tooltip: am5.Tooltip.new(root, {
                                pointerOrientation: "horizontal",
                                labelText: "[bold]{name}[/]\n{time}[/]\n" + t('content.home_page.chart.power') + ": [bold]{valueY} [[kW]]"
                            })
                        })
                    );


                    // series.bullets.push(function () {
                    //     return am5.Bullet.new(root, {
                    //         sprite: am5.Circle.new(root, {
                    //             radius: 5,
                    //             fill: series.get("fill")
                    //         })
                    //     });
                    // });

                    series.set("setStateOnChildren", true);
                    series.states.create("hover", {});

                    series.mainContainer.set("setStateOnChildren", true);
                    series.mainContainer.states.create("hover", {});

                    series.strokes.template.states.create("hover", {
                        strokeWidth: 4
                    });

                    series.data.setAll(data);
                    series.appear(1000);
                }

                //bỏ tên cột thừa
                const filteredData = data.map(item => {
                    const { time, total, ...rest } = item;
                    return rest;
                });

                const columnNames = [];

                filteredData.forEach(item => {
                    const keys = Object.keys(item);
                    columnNames.push(...keys);
                });

                const uniqueColumnNames = Array.from(new Set(columnNames)).sort((a, b) => a.localeCompare(b, 'en', { sensitivity: 'base' }));;

                if (projectId != 0) {
                    uniqueColumnNames.forEach(name => {
                        createSeries(name, name);
                    });
                } else {
                    projects.forEach(name => {
                        createSeries(name.projectName, name.projectName);
                    });
                }


                // chart.set("scrollbarX", am5.Scrollbar.new(root, {
                //     orientation: "horizontal",
                //     marginBottom: 20
                // }));

                var legend = chart.children.push(
                    am5.Legend.new(root, {
                        centerX: am5.p50,
                        x: am5.p50
                    })
                );

                // Make series change state when legend item is hovered
                legend.itemContainers.template.states.create("hover", {});

                legend.itemContainers.template.events.on("pointerover", function (e) {
                    e.target.dataItem.dataContext.hover();
                });
                legend.itemContainers.template.events.on("pointerout", function (e) {
                    e.target.dataItem.dataContext.unhover();
                });

                legend.data.setAll(chart.series.values);

                // Make stuff animate on load
                // https://www.amcharts.com/docs/v5/concepts/animations/
                chart.appear(1000, 100);
            });
        }
    };

    const drawChartEnergy = (data, dataComp, projects, projectId, typeTime, dataDevice, dataDeviceName, fromDate) => {
        let checkNowDay = true;
        let nowDay = "";

        if (typeTime == 6) {
            const startDate = new Date();
            const dayOfWeek = new Date().getDay();
            const diff = (dayOfWeek === 0 ? 6 : dayOfWeek - 1);
            startDate.setDate(startDate.getDate() - diff);
            nowDay = moment(startDate).format("YYYY-MM-DD") + " 00:00:00";
        }
        else if (typeTime == 2) {
            nowDay = moment(Date()).format("YYYY-MM") + "-01" + " 00:00:00";
        }
        else if (typeTime == 3) {
            nowDay = moment(Date()).format("YYYY") + "-01-01" + " 00:00:00";
        }
        else {
            nowDay = moment(Date()).format("YYYY-MM-DD") + " 00:00:00";
        }

        if (fromDate < nowDay) {
            checkNowDay = false;
        }

        const listCssColumn = [
            { width: 15, dx: -10 },
            { width: 30, dx: -15 },
            { width: 110, dx: -50 },
            { width: 200, dx: -100 },
            { width: 15, dx: -10 },
            { width: 80, dx: -45 },
        ];

        let dataTimeX = [];
        let nowDate = "";
        let beforeDate = "";
        console.log("typeTime", typeTime);
        switch (typeTime) {
            case 1:
                dataTimeX = dataTime.minutes
                nowDate = t('content.home_page.today')
                beforeDate = t('content.home_page.yesterday')
                break;
            case 2:
                dataTimeX = dataTime.dates
                nowDate = t('content.home_page.this_month')
                beforeDate = t('content.home_page.last_month')
                break;
            case 3:
                dataTimeX = dataTime.months
                nowDate = t('content.home_page.this_year')
                beforeDate = t('content.home_page.last_year')
                break;
            case 4:
                dataTimeX = data
                nowDate = t('content.total') + t('content.home_page.this_year')
                beforeDate = t('content.total') + t('content.home_page.last_year')
                break;
            case 5:
                dataTimeX = data
                nowDate = t('content.home_page.today')
                beforeDate = t('content.home_page.yesterday')
                break;
            case 6:
                dataTimeX = dataTime.weeks
                nowDate = t('content.home_page.this_week')
                beforeDate = t('content.home_page.last_week')
                break;
            default:
                dataTimeX = data
                nowDate = t('content.home_page.today')
                beforeDate = t('content.home_page.yesterday')
                break;
        }

        am5.array.each(am5.registry.rootElements, function (root) {
            if (root) {
                if (root.dom.id == "chartdivChart") {
                    root.dispose();
                }
            }
        });

        let element = document.getElementById("chartdivChart")
        if (element != null) {
            let root = am5.Root.new("chartdivChart");
            root._logo.dispose();
            root.setThemes([
                am5themes_Animated.new(root)
            ]);

            var chart = root.container.children.push(am5xy.XYChart.new(root, {
                panX: true,
                panY: false,
                wheelX: "panX",
                wheelY: "zoomX",
                layout: root.verticalLayout
            }));

            chart.children.unshift(am5.Label.new(root, {
                text: t('content.home_page.chart.energy_chart').toUpperCase(),
                fontSize: 28,
                fontWeight: "700",
                textAlign: "center",
                x: am5.percent(50),
                y: am5.percent(-2),
                centerX: am5.percent(50),
                paddingTop: 0,
                paddingBottom: 0
            }));

            // chart.get("colors").set("colors", [
            //     am5.color(0xe41a1c),
            //     am5.color(0x377eb8),
            //     am5.color(0x984ea3),
            //     am5.color(0xff7f00),
            //     am5.color(0xffff33),
            //     am5.color(0xa65628),
            //     am5.color(0x999999),
            //     am5.color(0x66c2a5),
            //     am5.color(0xfc8d62),
            //     am5.color(0xe78ac3),
            //     am5.color(0xa6d854),
            //     am5.color(0xffd92f),
            // ]);

            const colorChartNow = [
                "#0xe41a1c",
                "#0x16598c",
                "#0x6a2575",
                "#0xff7f00",
                "#0xffff33",
                "#0x4f2006",
                "#0x444444",
                "#0x18a579",
                "#0xed6f42",
                "#0xe248a5",
                "#0xa6d854",
                "#0xffd92f",
            ]

            // Add scrollbar
            // https://www.amcharts.com/docs/v5/charts/xy-chart/scrollbars/
            // chart.set("scrollbarX", am5.Scrollbar.new(root, {
            //     orientation: "horizontal"
            // }));

            // Create axes
            // https://www.amcharts.com/docs/v5/charts/xy-chart/axes/
            var xRenderer = am5xy.AxisRendererX.new(root, {
                minGridDistance: 10,
            });
            var xAxis = chart.xAxes.push(am5xy.CategoryAxis.new(root, {
                categoryField: typeTime == 6 ? "day" : "viewTime",
                renderer: xRenderer,
                tooltip: am5.Tooltip.new(root, {})
            }));

            var xAxis2 = chart.xAxes.push(am5xy.CategoryAxis.new(root, {
                categoryField: typeTime == 6 ? "day" : "viewTime",
                renderer: xRenderer,
                tooltip: am5.Tooltip.new(root, {})
            }));

            xRenderer.grid.template.setAll({
                location: 1
            })

            xRenderer.labels.template.setAll({
                rotation: -60,
                paddingTop: 0,
                paddingRight: -8,
                fontSize: 10
            });

            xAxis.data.setAll(dataTimeX);
            xAxis2.data.setAll(dataTimeX);

            var yAxis = chart.yAxes.push(am5xy.ValueAxis.new(root, {
                min: 0,
                renderer: am5xy.AxisRendererY.new(root, {
                    strokeOpacity: 0.1
                })
            }));
            var yAxis2 = chart.yAxes.push(
                am5xy.ValueAxis.new(root, {
                    maxDeviation: 0.3,
                    syncWithAxis: yAxis,
                    renderer: am5xy.AxisRendererY.new(root, { opposite: true })
                })
            );

            yAxis.children.moveValue(am5.Label.new(root, { text: t('content.home_page.chart.energy_power') + ` [[kWh]]`, rotation: -90, y: am5.p50, centerX: am5.p50 }), 0);
            yAxis2.children.moveValue(am5.Label.new(root, { text: t('content.home_page.chart.energy_power_accumulated') + ` [[kWh]]`, rotation: -90, y: am5.p50, centerX: am5.p50 }), 1);


            // Add legend
            // https://www.amcharts.com/docs/v5/charts/xy-chart/legend-xy-series/
            var legend = chart.children.push(am5.Legend.new(root, {
                centerX: am5.p50,
                x: am5.p50
            }));


            // Add series
            // https://www.amcharts.com/docs/v5/charts/xy-chart/series/


            function makeSeriesComp(name, fieldName, color) {
                var series2 = chart.series.push(am5xy.ColumnSeries.new(root, {
                    name: name,
                    stacked: true,
                    xAxis: xAxis2,
                    yAxis: yAxis,
                    valueYField: fieldName,
                    categoryXField: typeTime == 6 ? "day" : "viewTime",
                    fill: am5.color(color)
                }));

                series2.columns.template.setAll({
                    tooltipText: checkNowDay == true ? "[bold]{name}[/]\n" + beforeDate + "[/]\n{time}[/]\n" + t('content.home_page.chart.energy_power') + ": [bold]{valueY} [[kWh]]"
                        : "[bold]{name}[/]" + "[/]\n{time}\n[/]" + t('content.home_page.chart.energy_power') + ": [bold]{valueY} [[kWh]]",
                    tooltipY: am5.percent(10),
                    width: listCssColumn[typeTime - 1].width,
                    dx: listCssColumn[typeTime - 1].dx
                });


                series2.data.setAll(dataComp);


                series2.columns.template.set("fillOpacity", 0.5);

                // Make stuff animate on load
                // https://www.amcharts.com/docs/v5/concepts/animations/
                series2.appear();

                series2.bullets.push(function () {
                    return am5.Bullet.new(root, {
                        sprite: am5.Label.new(root, {
                            // text: "{valueY}",
                            fill: root.interfaceColors.get("alternativeText"),
                            centerY: am5.p50,
                            centerX: am5.p50,
                            populateText: true,
                            dx: listCssColumn[typeTime - 1].dx
                        })
                    });
                });
            }

            function makeSeries(name, fieldName, color) {
                var series = chart.series.push(am5xy.ColumnSeries.new(root, {
                    name: name,
                    stacked: true,
                    xAxis: xAxis,
                    yAxis: yAxis,
                    valueYField: fieldName,
                    categoryXField: typeTime == 6 ? "day" : "viewTime",
                    fill: am5.color(color),
                }));

                series.columns.template.setAll({
                    tooltipText: checkNowDay == true ? "[bold]{name}[/]\n" + nowDate + "[/]\n{time}[/]\n" + t('content.home_page.chart.energy_power') + ": [bold]{valueY} [[kWh]]"
                        : "[bold]{name}[/]" + "[/]\n{time}\n[/]" + t('content.home_page.chart.energy_power') + ": [bold]{valueY} [[kWh]]",
                    tooltipY: am5.percent(10),
                    width: listCssColumn[typeTime - 1].width,
                    dx: 0
                });

                series.data.setAll(data);

                // Make stuff animate on load
                // https://www.amcharts.com/docs/v5/concepts/animations/
                series.appear();

                series.bullets.push(function () {
                    return am5.Bullet.new(root, {
                        sprite: am5.Label.new(root, {
                            // text: "{valueY}",
                            fill: root.interfaceColors.get("alternativeText"),
                            centerY: am5.p50,
                            centerX: am5.p50,
                            populateText: true,
                            dx: 0
                        })
                    });
                });
            }

            // const filteredDataComp = dataComp.map(item => {
            //     const { time, total, day, viewTime, ...rest } = item;
            //     return rest;
            // });
            // const columnNamesComp = [];

            // filteredDataComp.forEach(item => {
            //     const keys = Object.keys(item);
            //     columnNamesComp.push(...keys);
            // });
            // const uniqueColumnNamesComp = Array.from(new Set(columnNamesComp)).sort((a, b) => a.localeCompare(b, 'en', { sensitivity: 'base' }));
            let x = 0;
            if (projectId != 0) {
                if (dataDeviceName != undefined) {
                    dataDeviceName.forEach(name => {
                        makeSeriesComp(name, name, colorChartNow[x]);
                        x++;
                    });
                } else {
                    dataDevice.forEach(name => {
                        makeSeriesComp(name.deviceName, name.deviceName, colorChartNow[x]);
                        x++;
                    });
                }

            } else {
                projects.forEach(name => {
                    makeSeriesComp(name.projectName, name.projectName, colorChartNow[x]);
                    x++
                });
            }

            // const filteredData = data.map(item => {
            //     const { time, total, day, viewTime, ...rest } = item;
            //     return rest;
            // });
            // const columnNames = [];

            // filteredData.forEach(item => {
            //     const keys = Object.keys(item);
            //     columnNames.push(...keys);
            // });
            // const uniqueColumnNames = Array.from(new Set(columnNames)).sort((a, b) => a.localeCompare(b, 'en', { sensitivity: 'base' }));
            let y = 0;

            if (projectId != 0) {
                if (dataDeviceName != undefined) {
                    dataDeviceName.forEach(name => {
                        makeSeries(name, name, colorChartNow[y]);
                        y++;
                    });
                } else {
                    dataDevice.forEach(name => {
                        makeSeries(name.deviceName, name.deviceName, colorChartNow[y]);
                        y++;
                    });
                }
            } else {
                projects.forEach(name => {
                    makeSeries(name.projectName, name.projectName, colorChartNow[y]);
                    y++;
                });
            }


            var series2 = chart.series.push(
                am5xy.LineSeries.new(root, {
                    name: t('content.home_page.chart.energy_power_accumulated'),
                    xAxis: xAxis,
                    yAxis: yAxis2,
                    valueYField: "total",
                    categoryXField: typeTime == 6 ? "day" : "viewTime",
                    tooltip: am5.Tooltip.new(root, {
                        pointerOrientation: "horizontal",
                        labelText: checkNowDay == true ? "[bold]{name}[/]\n" + nowDate + "[/]\n{time}[/]\n" + t('content.home_page.chart.energy_power') + ": [bold]{valueY} [[kWh]]" :
                            "[bold]{name}[/]" + "[/]\n{time}[/]\n" + t('content.home_page.chart.energy_power') + ": [bold]{valueY} [[kWh]]"
                    }),

                })
            );

            series2.strokes.template.setAll({
                strokeWidth: 3,
                templateField: "strokeSettings",
            });

            series2.data.setAll(data);

            series2.bullets.push(function () {
                return am5.Bullet.new(root, {
                    sprite: am5.Circle.new(root, {
                        strokeWidth: 3,
                        stroke: "blue",
                        radius: 5,
                        fill: root.interfaceColors.get("background"),

                    })
                });
            });
            chart.set("cursor", am5xy.XYCursor.new(root, {}));
            var legend = chart.children.push(
                am5.Legend.new(root, {
                    centerX: am5.p50,
                    x: am5.p50
                })
            );
            legend.data.setAll(chart.series.values);

            // Make stuff animate on load
            // https://www.amcharts.com/docs/v5/concepts/animations/
            chart.appear(1000, 100);

            // xAxis.events.once("datavalidated", function (ev) {
            //     ev.target.zoomToIndexes(data.length - 20, data.length);
            // });
        }
    }

    const drawChartTemperature = (data) => {
        am5.array.each(am5.registry.rootElements, function (root) {
            if (root) {
                if (root.dom.id == "chartdivChart") {
                    root.dispose();
                }
            }
        });

        let element = document.getElementById("chartdivChart")
        if (element != null) {

            am5.ready(function () {
                let root = am5.Root.new("chartdivChart");
                root._logo.dispose();
                root.setThemes([
                    am5themes_Animated.new(root)
                ]);

                var chart = root.container.children.push(
                    am5xy.XYChart.new(root, {
                        panX: true,
                        panY: true,
                        wheelX: "panX",
                        wheelY: "zoomX",
                        layout: root.verticalLayout,
                        pinchZoomX: true
                    })
                );

                chart.children.unshift(am5.Label.new(root, {
                    text: t('content.home_page.chart.temp_chart').toUpperCase(),
                    fontSize: 28,
                    fontWeight: "700",
                    textAlign: "center",
                    x: am5.percent(50),
                    y: am5.percent(-2),
                    centerX: am5.percent(50),
                    paddingTop: 0,
                    paddingBottom: 0
                }));

                chart.get("colors").set("colors", [
                    am5.color(0xe41a1c),
                    am5.color(0x377eb8),
                    am5.color(0x4daf4a),
                    am5.color(0x984ea3),
                    am5.color(0xff7f00),
                    am5.color(0xffff33),
                    am5.color(0xa65628),
                    am5.color(0x999999),
                    am5.color(0x66c2a5),
                    am5.color(0xfc8d62),
                    am5.color(0xe78ac3),
                    am5.color(0xa6d854),
                    am5.color(0xffd92f),
                ]);

                var cursor = chart.set("cursor", am5xy.XYCursor.new(root, {
                    behavior: "none"
                }));
                cursor.lineY.set("visible", false);

                var xRenderer = am5xy.AxisRendererX.new(root, {
                    minGridDistance: 10,
                    cellStartLocation: 0.2,
                    cellEndLocation: 0.8
                });
                xRenderer.grid.template.set("location", 0.5);
                xRenderer.labels.template.setAll({
                    rotation: -60,
                    paddingTop: 0,
                    paddingRight: 0,
                    fontSize: 10,
                    location: 0.5,
                    multiLocation: 0.5
                });

                var xAxis = chart.xAxes.push(
                    am5xy.CategoryAxis.new(root, {
                        categoryField: "time",
                        renderer: xRenderer,
                        tooltip: am5.Tooltip.new(root, {})
                    })
                );

                xAxis.data.setAll(data);

                var yAxis = chart.yAxes.push(
                    am5xy.ValueAxis.new(root, {
                        maxPrecision: 0,
                        renderer: am5xy.AxisRendererY.new(root, {

                        })
                    })
                );

                yAxis.children.moveValue(am5.Label.new(root, { text: t('content.temperature') + ` [[°C]]`, rotation: -90, y: am5.p50, centerX: am5.p50 }), 0);

                function createSeries(name, field) {
                    var series = chart.series.push(
                        am5xy.LineSeries.new(root, {
                            name: name,
                            xAxis: xAxis,
                            yAxis: yAxis,
                            valueYField: field,
                            categoryXField: "time",
                            tooltip: am5.Tooltip.new(root, {
                                pointerOrientation: "horizontal",
                                labelText: "[bold]{name}[/]\n{time}[/]\n" + t('content.temperature') + ": [bold]{valueY} [[°C]]"
                            })
                        })
                    );


                    // series.bullets.push(function () {
                    //     return am5.Bullet.new(root, {
                    //         sprite: am5.Circle.new(root, {
                    //             radius: 5,
                    //             fill: series.get("fill")
                    //         })
                    //     });
                    // });

                    series.set("setStateOnChildren", true);
                    series.states.create("hover", {});

                    series.mainContainer.set("setStateOnChildren", true);
                    series.mainContainer.states.create("hover", {});

                    series.strokes.template.states.create("hover", {
                        strokeWidth: 4
                    });

                    series.data.setAll(data);
                    series.appear(1000);
                }

                //bỏ tên cột thừa
                const filteredData = data.map(item => {
                    const { time, total, ...rest } = item;
                    return rest;
                });

                const columnNames = [];

                filteredData.forEach(item => {
                    const keys = Object.keys(item);
                    columnNames.push(...keys);
                });

                const uniqueColumnNames = Array.from(new Set(columnNames)).sort((a, b) => a.localeCompare(b, 'en', { sensitivity: 'base' }));;

                uniqueColumnNames.forEach(name => {
                    createSeries(name, name);
                });



                // chart.set("scrollbarX", am5.Scrollbar.new(root, {
                //     orientation: "horizontal",
                //     marginBottom: 20
                // }));

                var legend = chart.children.push(
                    am5.Legend.new(root, {
                        centerX: am5.p50,
                        x: am5.p50
                    })
                );

                // Make series change state when legend item is hovered
                legend.itemContainers.template.states.create("hover", {});

                legend.itemContainers.template.events.on("pointerover", function (e) {
                    e.target.dataItem.dataContext.hover();
                });
                legend.itemContainers.template.events.on("pointerout", function (e) {
                    e.target.dataItem.dataContext.unhover();
                });

                legend.data.setAll(chart.series.values);

                // Make stuff animate on load
                // https://www.amcharts.com/docs/v5/concepts/animations/
                chart.appear(1000, 100);
            });
        }
    };

    const drawChartDischargeIndicator = (data) => {

        am5.array.each(am5.registry.rootElements, function (root) {
            if (root) {
                if (root.dom.id == "chartdivChart") {
                    root.dispose();
                }
            }
        });

        let element = document.getElementById("chartdivChart")
        if (element != null) {

            am5.ready(function () {
                let root = am5.Root.new("chartdivChart");
                root._logo.dispose();
                root.setThemes([
                    am5themes_Animated.new(root)
                ]);

                var chart = root.container.children.push(
                    am5xy.XYChart.new(root, {
                        panX: true,
                        panY: true,
                        wheelX: "panX",
                        wheelY: "zoomX",
                        layout: root.verticalLayout,
                        pinchZoomX: true
                    })
                );

                chart.children.unshift(am5.Label.new(root, {
                    text: t('content.home_page.chart.discharge_indicator_chart').toUpperCase(),
                    fontSize: 28,
                    fontWeight: "700",
                    textAlign: "center",
                    x: am5.percent(50),
                    y: am5.percent(-2),
                    centerX: am5.percent(50),
                    paddingTop: 0,
                    paddingBottom: 0
                }));


                chart.get("colors").set("colors", [
                    am5.color(0xe41a1c),
                    am5.color(0x377eb8),
                    am5.color(0x4daf4a),
                    am5.color(0x984ea3),
                    am5.color(0xff7f00),
                    am5.color(0xffff33),
                    am5.color(0xa65628),
                    am5.color(0x999999),
                    am5.color(0x66c2a5),
                    am5.color(0xfc8d62),
                    am5.color(0xe78ac3),
                    am5.color(0xa6d854),
                    am5.color(0xffd92f),
                ]);

                var cursor = chart.set("cursor", am5xy.XYCursor.new(root, {
                    behavior: "none"
                }));
                cursor.lineY.set("visible", false);

                var xRenderer = am5xy.AxisRendererX.new(root, {
                    minGridDistance: 10,
                    cellStartLocation: 0.2,
                    cellEndLocation: 0.8
                });
                xRenderer.grid.template.set("location", 0.5);
                xRenderer.labels.template.setAll({
                    rotation: -60,
                    paddingTop: 0,
                    paddingRight: 0,
                    fontSize: 10,
                    location: 0.5,
                    multiLocation: 0.5
                });

                var xAxis = chart.xAxes.push(
                    am5xy.CategoryAxis.new(root, {
                        categoryField: "time",
                        renderer: xRenderer,
                        tooltip: am5.Tooltip.new(root, {})
                    })
                );

                xAxis.data.setAll(data);

                var yAxis = chart.yAxes.push(
                    am5xy.ValueAxis.new(root, {
                        maxPrecision: 0,
                        renderer: am5xy.AxisRendererY.new(root, {

                        })
                    })
                );

                yAxis.children.moveValue(am5.Label.new(root, { text: `Mức phóng điện`, rotation: -90, y: am5.p50, centerX: am5.p50 }), 0);

                function createSeries(name, field) {
                    var series = chart.series.push(
                        am5xy.LineSeries.new(root, {
                            name: name,
                            xAxis: xAxis,
                            yAxis: yAxis,
                            valueYField: field,
                            categoryXField: "time",
                            tooltip: am5.Tooltip.new(root, {
                                pointerOrientation: "horizontal",
                                labelText: "[bold]{name}[/]\n{time}[/]\nMức phóng điện: [bold]{valueY}"
                            })
                        })
                    );


                    series.bullets.push(function () {
                        return am5.Bullet.new(root, {
                            sprite: am5.Circle.new(root, {
                                radius: 2,
                                fill: series.get("fill")
                            })
                        });
                    });

                    series.set("setStateOnChildren", true);
                    series.states.create("hover", {});

                    series.mainContainer.set("setStateOnChildren", true);
                    series.mainContainer.states.create("hover", {});

                    series.strokes.template.states.create("hover", {
                        strokeWidth: 4
                    });

                    series.data.setAll(data);
                    series.appear(1000);
                }

                //bỏ tên cột thừa
                const filteredData = data.map(item => {
                    const { time, total, ...rest } = item;
                    return rest;
                });

                const columnNames = [];

                filteredData.forEach(item => {
                    const keys = Object.keys(item);
                    columnNames.push(...keys);
                });

                const uniqueColumnNames = Array.from(new Set(columnNames)).sort((a, b) => a.localeCompare(b, 'en', { sensitivity: 'base' }));;

                uniqueColumnNames.forEach(name => {
                    createSeries(name, name);
                });

                // chart.set("scrollbarX", am5.Scrollbar.new(root, {
                //     orientation: "horizontal",
                //     marginBottom: 20
                // }));

                var legend = chart.children.push(
                    am5.Legend.new(root, {
                        centerX: am5.p50,
                        x: am5.p50
                    })
                );

                // Make series change state when legend item is hovered
                legend.itemContainers.template.states.create("hover", {});

                legend.itemContainers.template.events.on("pointerover", function (e) {
                    e.target.dataItem.dataContext.hover();
                });
                legend.itemContainers.template.events.on("pointerout", function (e) {
                    e.target.dataItem.dataContext.unhover();
                });

                legend.data.setAll(chart.series.values);

                // Make stuff animate on load
                // https://www.amcharts.com/docs/v5/concepts/animations/
                chart.appear(1000, 100);
            });
        }
    };

    const drawChartEnergyPlan = (data, typeTime, nameProDev) => {
        am5.array.each(am5.registry.rootElements, function (root) {
            if (root) {
                if (root.dom.id == "chartdivChart") {
                    root.dispose();
                }
            }
        });
        if (data != null) {
            let element = document.getElementById("chartdivChart")
            if (element != null) {
                let root = am5.Root.new("chartdivChart");
                root._logo.dispose();
                root.setThemes([
                    am5themes_Animated.new(root)
                ]);

                // Create chart
                // https://www.amcharts.com/docs/v5/charts/xy-chart/
                var chart = root.container.children.push(
                    am5xy.XYChart.new(root, {
                        panX: true,
                        panY: true,
                        wheelX: "panX",
                        wheelY: "zoomX",
                        layout: root.verticalLayout,
                        pinchZoomX: true
                    })
                );

                chart.children.unshift(am5.Label.new(root, {
                    text: t('content.home_page.chart.energy_plan_chart').toUpperCase(),
                    fontSize: 28,
                    fontWeight: "700",
                    textAlign: "center",
                    x: am5.percent(50),
                    y: am5.percent(-2),
                    centerX: am5.percent(50),
                    paddingTop: 0,
                    paddingBottom: 0
                }));

                chart.get("colors").set("colors", [
                    am5.color(0x0a1a5c),
                    am5.color(0xFF0000),
                    am5.color(0x00FF00),
                ]);

                // Add cursor
                // https://www.amcharts.com/docs/v5/charts/xy-chart/cursor/
                var cursor = chart.set("cursor", am5xy.XYCursor.new(root, {
                    behavior: "none"
                }));
                cursor.lineY.set("visible", false);

                // Create axes
                // https://www.amcharts.com/docs/v5/charts/xy-chart/axes/

                var xRenderer = am5xy.AxisRendererX.new(root, {
                    minGridDistance: 10,
                });
                xRenderer.labels.template.setAll({
                    rotation: -60,
                    paddingTop: 0,
                    paddingRight: 0,
                    fontSize: 10
                });

                var xAxis = chart.xAxes.push(
                    am5xy.CategoryAxis.new(root, {
                        // categoryField: typeTime == 1 || typeTime == 5 ? "dateOfMonth" : "viewTime",
                        categoryField: "viewTime",
                        renderer: xRenderer,
                        tooltip: am5.Tooltip.new(root, {})
                    })
                );

                xAxis.data.setAll(data[0].listDataPower);


                var yAxis = chart.yAxes.push(
                    am5xy.ValueAxis.new(root, {
                        maxPrecision: 10,
                        renderer: am5xy.AxisRendererY.new(root, {
                        })
                    })
                );

                if (typeTime == 0) {
                    yAxis.children.moveValue(am5.Label.new(root, { text: t('content.home_page.chart.energy_power') + ` [[kWh]]`, rotation: -90, y: am5.p50, centerX: am5.p50 }), 0);
                } else {
                    yAxis.children.moveValue(am5.Label.new(root, { text: t('content.home_page.chart.energy_power') + ` [[kWh]]`, rotation: -90, y: am5.p50, centerX: am5.p50 }), 0);
                }

                // Add series
                // https://www.amcharts.com/docs/v5/charts/xy-chart/series/

                function createSeries(name, field, checked) {

                    let tooltip = am5.Tooltip.new(root, {
                        pointerOrientation: "horizontal",
                        labelText: typeTime == 5 ? `[bold]${nameProDev}[/]\n{day} [bold]{categoryX}[/]\n[bold]{name}: [bold]{valueY} [bold]kWh`
                            : `[bold]${nameProDev}[/]\n{categoryX}[/]\n[bold]{name}: [bold]{valueY} [bold]kWh`,
                    });
                    // tooltip: am5.Tooltip.new(root, {
                    //     pointerOrientation: "horizontal",
                    //     labelText: "[bold]{name}[/]\n{time}[/]\nMức phóng điện: [bold]{valueY}"
                    // })

                    var series = chart.series.push(
                        am5xy.LineSeries.new(root, {
                            name: name,
                            xAxis: xAxis,
                            yAxis: yAxis,
                            valueYField: field,
                            categoryXField: "viewTime",
                            tooltip: tooltip
                        })
                    );


                    if (checked) {
                        series.bullets.push(function () {
                            return am5.Bullet.new(root, {
                                sprite: am5.Circle.new(root, {
                                    radius: 2,
                                    fill: series.get("fill")
                                })
                            });
                        });
                    }

                    // create hover state for series and for mainContainer, so that when series is hovered,
                    // the state would be passed down to the strokes which are in mainContainer.
                    series.set("setStateOnChildren", true);
                    series.states.create("hover", {});

                    series.mainContainer.set("setStateOnChildren", true);
                    series.mainContainer.states.create("hover", {});

                    series.strokes.template.states.create("hover", {
                        strokeWidth: 0
                    });


                    series.data.setAll(data[0].listDataPower);


                    series.appear(1000);
                }

                if (typeTime == 0) {

                    for (let x = 0; x < data.length; x++) {
                        if (data[x].listDataPower[0] != undefined) {
                            let num = 0;
                            for (let i = 0; i < data[x].listDataPower.length; i++) {
                                if (data[x].listDataPower[i].power != null) {
                                    num += data[x].listDataPower[i].power
                                    data[x].listDataPower[i].power = num
                                    // data[x].listDataPower[i].power = data[x].listDataPower[i].power + 0
                                }
                            }

                            if (typeTime == 2) {
                                createSeries(t('content.home_page.chart.accumulated_energy'), "power", true)
                                createSeries(t('content.home_page.chart.base_line'), "sumLandmark")
                                createSeries(t('content.home_page.chart.target_line'), "sumEnergy")

                            } else {
                                createSeries(t('content.home_page.chart.accumulated_energy'), "power", true)
                                createSeries(t('content.home_page.chart.base_line'), "targetEnergy")
                                createSeries(t('content.home_page.chart.target_line'), "planEnergy")
                            }

                        }
                    }
                } else {
                    for (let x = 0; x < data.length; x++) {
                        if (data[x].listDataPower[0] != undefined) {
                            let num = 0;
                            for (let i = 0; i < data[x].listDataPower.length; i++) {
                                if (data[x].listDataPower[i].power != null) {
                                    num = data[x].listDataPower[i].power
                                    break;
                                }
                            }
                            for (let i = 0; i < data[x].listDataPower.length; i++) {
                                if (data[x].listDataPower[i].power != null && data[x].listDataPower[i].power != 0) {
                                    // data[x].listDataPower[i].power = data[x].listDataPower[i].power - num
                                    data[x].listDataPower[i].power = data[x].listDataPower[i].power + 0
                                }
                            }

                            if (typeTime == 2) {
                                createSeries(t('content.home_page.chart.accumulated_energy'), "power", true)
                                createSeries(t('content.home_page.chart.base_line'), "sumLandmark")
                                createSeries(t('content.home_page.chart.target_line'), "sumEnergy")

                            } else {
                                createSeries(t('content.home_page.chart.accumulated_energy'), "power", true)
                                createSeries(t('content.home_page.chart.base_line'), "targetEnergy")
                                createSeries(t('content.home_page.chart.target_line'), "planEnergy")
                            }
                        }
                    }
                }
                // Add scrollbar
                // https://www.amcharts.com/docs/v5/charts/xy-chart/scrollbars/

                var legend = chart.children.push(
                    am5.Legend.new(root, {
                        centerX: am5.p50,
                        x: am5.percent(60),
                        layout: root.horizontalLayout
                    })
                );

                // Make series change state when legend item is hovered
                legend.itemContainers.template.states.create("hover", {});

                legend.itemContainers.template.events.on("pointerover", function (e) {
                    e.target.dataItem.dataContext.hover();
                });
                legend.itemContainers.template.events.on("pointerout", function (e) {
                    e.target.dataItem.dataContext.unhover();
                });

                legend.labels.template.setAll({
                    fontSize: 10
                })

                legend.data.setAll(chart.series.values);

                // Make stuff animate on load
                // https://www.amcharts.com/docs/v5/concepts/animations/
                chart.appear(1000, 100);


            }
        }
    }

    const drawChartEnergyComparison = (data) => {

        am5.array.each(am5.registry.rootElements, function (root) {
            if (root) {
                if (root.dom.id == "chartdivChart") {
                    root.dispose();
                }
            }
        });

        let element = document.getElementById("chartdivChart")
        if (element != null) {


            // Create root element
            // https://www.amcharts.com/docs/v5/getting-started/#Root_element
            var root = am5.Root.new("chartdivChart");
            root._logo.dispose();

            // Set themes
            // https://www.amcharts.com/docs/v5/concepts/themes/
            root.setThemes([
                am5themes_Animated.new(root)
            ]);


            // Create chart
            // https://www.amcharts.com/docs/v5/charts/percent-charts/pie-chart/
            var chart = root.container.children.push(am5percent.PieChart.new(root, {
                layout: root.verticalLayout
            }));


            // Create series
            // https://www.amcharts.com/docs/v5/charts/percent-charts/pie-chart/#Series
            var series = chart.series.push(am5percent.PieSeries.new(root, {
                valueField: "ep",
                categoryField: "name"
            }));

            series.get("colors").set("colors", [
                am5.color(0xe41a1c),
                am5.color(0x377eb8),
                am5.color(0x984ea3),
                am5.color(0xff7f00),
                am5.color(0xffff33),
                am5.color(0xa65628),
                am5.color(0x999999),
                am5.color(0x66c2a5),
                am5.color(0xfc8d62),
                am5.color(0xe78ac3),
                am5.color(0xa6d854),
                am5.color(0xffd92f),
            ]);

            chart.children.unshift(am5.Label.new(root, {
                text: t('content.home_page.chart.energy_compare_chart').toUpperCase(),
                fontSize: 28,
                fontWeight: "700",
                textAlign: "center",
                x: am5.percent(50),
                y: am5.percent(-2),
                centerX: am5.percent(50),
                paddingTop: 20,
                paddingBottom: 0
            }));



            // Set data
            // https://www.amcharts.com/docs/v5/charts/percent-charts/pie-chart/#Setting_data
            series.data.setAll(data);

            // Create legend
            // https://www.amcharts.com/docs/v5/charts/percent-charts/legend-percent-series/
            var legend = chart.children.push(am5.Legend.new(root, {
                centerX: am5.percent(50),
                x: am5.percent(50),
                marginTop: 15,
                marginBottom: 15
            }));

            series.labels.template.set("text", "{name}: [bold]{ep} kWh [bold]({valuePercentTotal.formatNumber('0.0')}%)[/]");

            series.slices.template.set("tooltipText", "{name}: [bold]{ep} kWh [bold]({valuePercentTotal.formatNumber('0.0')}%) [/] ");

            legend.data.setAll(series.dataItems);


            // // Play initial series animation
            // // https://www.amcharts.com/docs/v5/concepts/animations/#Animation_of_series
            // series.appear(1000, 100);
            // var chart = root.container.children.push(
            //     am5percent.PieChart.new(root, {
            //         layout: root.horizontalLayout
            //     })
            // );

            // // Define data
            // var data = [{
            //     country: "France",
            //     sales: 100000
            // }, {
            //     country: "Spain",
            //     sales: 160000
            // }, {
            //     country: "United Kingdom",
            //     sales: 80000
            // }, {
            //     country: "Netherlands",
            //     sales: 90000
            // }, {
            //     country: "Portugal",
            //     sales: 25000
            // }, {
            //     country: "Germany",
            //     sales: 70000
            // }, {
            //     country: "Austria",
            //     sales: 75000
            // }, {
            //     country: "Belgium",
            //     sales: 40000
            // }, {
            //     country: "Poland",
            //     sales: 60000
            // }];

            // // Create series
            // var series = chart.series.push(
            //     am5percent.PieSeries.new(root, {
            //         name: "Series",
            //         valueField: "sales",
            //         categoryField: "country",
            //         legendLabelText: "[{fill}]{category}[/]",
            //         legendValueText: "[bold {fill}]{value}[/]"
            //     })
            // );
            // series.data.setAll(data);
            // series.labels.template.set("forceHidden", true);
            // series.ticks.template.set("forceHidden", true);

            // // Add legend
            // var legend = chart.children.push(
            //     am5.Legend.new(root, {
            //         centerY: am5.percent(50),
            //         y: am5.percent(50),
            //         layout: root.verticalLayout
            //     })
            // );

            // legend.data.setAll(series.dataItems);
        }
    }

    const funcSetType = (typeSelected) => {
        setDataTablePower([]);
        let check = false;
        if (projectId == 0) {
            if (allowedTypeDevice.includes(typeSelected)) {
                $.alert({
                    title: t('content.notification'),
                    content: t('content.home_page.chart.project_null'),
                });
                check = true;
            }
        }
        if (check == false) {
            setType(() => typeSelected)
            let today = new Date();

            if (typeSelected != 6) {
                setIsLoading(true);
            }
            setFDate(new Date());
            setTDate(new Date());
            setActiveButton(1)
            setViewValue(1)
            setTypeFormat("yy-mm-dd")

            switch (typeSelected) {
                case 1:
                    setTypeLoadName(" NĂNG LƯỢNG")
                    setUnit("(kWh)")
                    setTypeNameDowload("tableEp")
                    break;
                case 2:
                    setTypeLoadName(" CÔNG SUẤT")
                    setUnit("(kW)")
                    setTypeNameDowload("tablePower")
                    break;
                case 3:
                    setTypeLoadName(" TIỀN ĐIỆN")
                    setUnit("(VND)")
                    setTypeNameDowload("tableCost")
                    break;
                case 4:
                    setTypeLoadName(" MỨC MANG TẢI")
                    break;
                case 5:
                    setTypeLoadName(" NHIỆT ĐỘ")
                    break;
                case 6:
                    setDeviceId("");
                    setTypeLoadName(" SANKEY")
                    break;
                case 7:
                    setDeviceId("");
                    setTypeLoadName(" MỨC PHÓNG ĐIỆN")
                    break;
                case 8:
                    setTypeLoadName(" KẾ HOẠCH NĂNG LƯỢNG")
                    break;
                case 9:
                    setTypeLoadName(" SO SÁNH NĂNG LƯỢNG")
                    break;
                default:
                    break;
            }

            today.setDate(today.getDate());
            let fromTime = moment(today).format("YYYY-MM-DD") + " 00:00:00";
            let toTime = moment(today).format("YYYY-MM-DD") + " 23:59:59";
            if (typeSelected == 8) {
                fromTime = moment(today).format("YYYY-MM-DD");
                toTime = moment(today).format("YYYY-MM-DD");
            }
            setFromDate(fromTime)
            setToDate(toTime)

            if (allowedTypeDate.includes(typeSelected)) {
                funcGetDataLoad(projectId, typeSelected, 1, fromTime, toTime, activeButton, systemTypeId, deviceId, null, null, dataDeviceName)
            } else {
                funcGetDataLoad(projectId, typeSelected, 1, fromTime, toTime, activeButton, systemTypeId, "", null, null, dataDeviceName)
            }
        }
    }

    const onChangeValueTime = async (e) => {
        if (type == 6) {
            setIsLoading(true);
        }
        setTypeTime(activeButton)

        let fromDate = moment(e.value).format("YYYY-MM-DD") + " 00:00:00";
        let toDateYear = toDate;
        if (activeButton == 3) {
            toDateYear = moment(e.value).format("YYYY") + "-12-31" + " 23:59:59";
            setToDate(toDateYear)
            if (type == 8) {
                fromDate = moment(e.value).format("YYYY");
                toDateYear = null;
            }
        }
        if (activeButton == 2) {
            toDateYear = moment(e.value).format("YYYY-MM") + "-31" + " 23:59:59";
            setToDate(toDateYear)
            if (type == 8) {
                fromDate = moment(e.value).format("YYYY-MM");
                toDateYear = null;
            }
        }
        if (activeButton == 1) {
            toDateYear = moment(e.value).format("YYYY-MM-DD") + " 23:59:59";
            setToDate(toDateYear)
            if (type == 8) {
                fromDate = moment(e.value).format("YYYY-MM-DD");
                toDateYear = null;
            }
        }
        if (activeButton == 6) {

            const selectedDate = e.value; // Ngày được chọn
            // Tìm ngày đầu tiên của tuần (Thứ 2)
            const startDate = new Date(selectedDate);
            const dayOfWeek = startDate.getDay();
            const diff = (dayOfWeek === 0 ? 6 : dayOfWeek - 1); // Nếu là Chủ Nhật (0), lấy ngày đầu tiên là Thứ 2 (diff = 6)
            startDate.setDate(startDate.getDate() - diff);
            // Tìm ngày cuối cùng của tuần
            const endDate = new Date(startDate);
            endDate.setDate(startDate.getDate() + 6); // Lấy ngày cuối cùng của tuần

            fromDate = moment(startDate).format("YYYY-MM-DD") + " 00:00:00";
            toDateYear = moment(endDate).format("YYYY-MM-DD") + " 23:59:59";

            setToDate(toDateYear)
        }

        let checkDate = true;
        if (activeButton == 5) {
            let fromDateCheck = moment(e.value).startOf('day');
            let toDateCheck = moment(tDate).endOf('day').add(1, 'days');
            let diffInDays = toDateCheck.diff(fromDateCheck, 'days');

            if (diffInDays > 31) {
                checkDate = false;
                $.alert({
                    title: t('content.notification'),
                    content: "Lượng dữ liệu lớn, chỉ được xem trong khoảng 1 tháng !",
                });
                setIsLoading(false);
            } else {
                checkDate = true;
            }
        }

        if (checkDate == true) {
            setFromDate(fromDate)
            setFDate(e.value)
            funcGetDataLoad(projectId, type, typeTime, fromDate, toDateYear, activeButton, systemTypeId, deviceId, null, null, dataDeviceName)
        }
    }

    const onChangeValueToTime = async (e) => {
        if (type == 6) {
            setIsLoading(true);
        }
        setTypeTime(activeButton)
        let toDate = moment(e.value).format("YYYY-MM-DD") + " 23:59:59";
        let checkDate = true;
        if (activeButton == 5) {
            let fromDateCheck = moment(fDate).startOf('day');
            let toDateCheck = moment(e.value).endOf('day').add(1, 'days');
            let diffInDays = toDateCheck.diff(fromDateCheck, 'days');
            if (diffInDays > 31) {
                checkDate = false;
                $.alert({
                    title: t('content.notification'),
                    content: "Lượng dữ liệu lớn, chỉ được xem trong khoảng 1 tháng !",
                });
                setIsLoading(false);
            } else {
                checkDate = true;
            }
        }
        if (checkDate == true) {
            setToDate(toDate)
            setTDate(e.value)
            funcGetDataLoad(projectId, type, typeTime, fromDate, toDate, activeButton, systemTypeId, deviceId, null, null, dataDeviceName)
        }
    }
    const changeValueView = (value) => {
        setViewValue(value)
        if (value == 1) {
            if (typeView == false) {
                $("#table-chart").hide()
                $("#chartdivChart").show()
            }
            if (typeView == true) {
                $("#chartdivChart").hide()
                $("#table-chart").show()
            }
            setTypeView(!typeView)
        }
    }

    function formatCurrency(value) {
        if (type == 3) {
            if (value != null) {
                return value.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
            } else {
                return '0 VNĐ';
            }
        } else {
            if (value != null) {
                return new Intl.NumberFormat('vi-VN').format(value);
            } else {
                return value;
            }
        }
    }

    function formatNumber(number) {
        return new Intl.NumberFormat('vi-VN').format(number);
    }

    // function getCheckOptionSys(value) {
    //     switch (systemTypeId) {
    //         case 1:
    //             return priorityLoad.includes(value);
    //         case 2:
    //             return prioritySolar.includes(value);
    //         case 3:
    //             return priorityWind.includes(value);
    //         case 4:
    //             return priorityBattery.includes(value);
    //         case 5:
    //             return priorityGrid.includes(value);
    //         default:
    //             return '';
    //     }
    // }

    return (
        <>
            {!accessDenied ?
                <div className="div-chart">
                    <div id="chartSelect" className="chart-div-right">
                        <div className="system-type">
                            <div className="radio-tabs">
                                <label className="radio-tabs__field" >
                                    <input type="radio" name="radio-tabs" value={1} className="radio-tabs__input" checked={systemTypeId == 1 ? true : false} onChange={(e) => funcSetSystemTypeId(e)} />
                                    <span className="radio-tabs__text text-uppercase">
                                        <img src="/resources/image/icon-load-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                        {t('content.home_page.load')}</span>
                                </label>
                                <label className="radio-tabs__field" >
                                    <input type="radio" name="radio-tabs" value={2} className="radio-tabs__input" checked={systemTypeId == 2 ? true : false} onChange={(e) => funcSetSystemTypeId(e)} />
                                    <span className="radio-tabs__text text-uppercase">
                                        <img src="/resources/image/icon-solar-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                        {t('content.home_page.solar')}</span>
                                </label>
                                <label className="radio-tabs__field" >
                                    <input type="radio" name="radio-tabs" value={5} className="radio-tabs__input" checked={systemTypeId == 5 ? true : false} onChange={(e) => funcSetSystemTypeId(e)} />
                                    <span className="radio-tabs__text text-uppercase">
                                        <img src="/resources/image/icon-grid-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                        {t('content.home_page.grid')}</span>
                                </label>
                                <label className="radio-tabs__field" >
                                    <input type="radio" name="radio-tabs" value={4} className="radio-tabs__input" checked={systemTypeId == 4 ? true : false} onChange={(e) => funcSetSystemTypeId(e)} />
                                    <span className="radio-tabs__text text-uppercase">
                                        <img src="/resources/image/icon-battery-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                        {t('content.home_page.battery')}</span>
                                </label>
                                <label className="radio-tabs__field" >
                                    <input type="radio" name="radio-tabs" value={3} className="radio-tabs__input" checked={systemTypeId == 3 ? true : false} onChange={(e) => funcSetSystemTypeId(e)} />
                                    <span className="radio-tabs__text text-uppercase">
                                        <img src="/resources/image/icon-wind-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                        {t('content.home_page.wind')}</span>
                                </label>
                            </div>
                        </div>
                        <hr
                            style={{
                                background: "var(--ses-blue-80-color)",
                                color: "var(--ses-blue-80-color)",
                                borderColor: "var(--ses-blue-80-color)",
                                height: '2px',
                                borderRadius: "5px",
                                margin: "0em"
                            }}
                        />
                        <div className="warning-level ml-1">
                            <div className="select-type">
                                <div hidden={!priorityAll.includes(1) ? true : false} className="form-check form-check-inline mr-5">
                                    <input className="form-check-input" type="radio" name="levelWarning" id="inlineRadio1" value={1} style={{ transform: "scale(1.5)" }}
                                        checked={type == 1 ? true : false} onChange={() => funcSetType(1)} />
                                    <label className="form-check-label text-uppercase" htmlFor="inlineRadio1">{t('content.home_page.chart.energy')}</label>
                                </div>
                                <div hidden={!priorityAll.includes(2) ? true : false} className="form-check form-check-inline mr-5">
                                    <input className="form-check-input" type="radio" name="levelWarning" id="inlineRadio2" value={2} style={{ transform: "scale(1.5)" }}
                                        checked={type == 2 ? true : false} onChange={() => funcSetType(2)} />
                                    <label className="form-check-label text-uppercase" htmlFor="inlineRadio2">{t('content.home_page.chart.power')}</label>
                                </div>
                                <div hidden={!priorityAll.includes(3) ? true : false} className="form-check form-check-inline mr-5">
                                    <input className="form-check-input" type="radio" name="levelWarning" id="inlineRadio3" value={3} style={{ transform: "scale(1.5)" }}
                                        checked={type == 3 ? true : false} onChange={() => funcSetType(3)} />
                                    <label className="form-check-label text-uppercase" htmlFor="inlineRadio3">{t('content.home_page.chart.cost')}</label>
                                </div>
                                <div hidden={!priorityAll.includes(4) ? true : false} className="form-check form-check-inline mr-5">
                                    <input className="form-check-input" type="radio" name="levelWarning" id="inlineRadio4" value={4} style={{ transform: "scale(1.5)" }}
                                        checked={type == 4 ? true : false} onChange={() => funcSetType(4)} />
                                    <label className="form-check-label text-uppercase" htmlFor="inlineRadio4">{t('content.home_page.chart.load_level')}</label>
                                </div>
                                <div hidden={!priorityAll.includes(5) ? true : false} className="form-check form-check-inline mr-5">
                                    <input className="form-check-input" type="radio" name="levelWarning" id="inlineRadio5" value={5} style={{ transform: "scale(1.5)" }}
                                        checked={type == 5 ? true : false} onChange={() => funcSetType(5)} />
                                    <label className="form-check-label text-uppercase" htmlFor="inlineRadio5">{t('content.home_page.chart.temperature')}</label>
                                </div>
                                <div hidden={!priorityAll.includes(6) ? true : false} className="form-check form-check-inline mr-5">
                                    <input className="form-check-input" type="radio" name="levelWarning" id="inlineRadio6" value={6} style={{ transform: "scale(1.5)" }}
                                        checked={type == 6 ? true : false} onChange={() => funcSetType(6)} />
                                    <label className="form-check-label text-uppercase" htmlFor="inlineRadio6">{t('content.home_page.chart.sankey')}</label>
                                </div>
                                <div hidden={!priorityAll.includes(7) ? true : false} className="form-check form-check-inline mr-5">
                                    <input className="form-check-input" type="radio" name="levelWarning" id="inlineRadio7" value={7} style={{ transform: "scale(1.5)" }}
                                        checked={type == 7 ? true : false} onChange={() => funcSetType(7)} />
                                    <label className="form-check-label text-uppercase" htmlFor="inlineRadio7">{t('content.home_page.chart.discharge_indicator')}</label>
                                </div>
                                <div hidden={!priorityAll.includes(8) ? true : false} className="form-check form-check-inline mr-5">
                                    <input className="form-check-input" type="radio" name="levelWarning" id="inlineRadio8" value={8} style={{ transform: "scale(1.5)" }}
                                        checked={type == 8 ? true : false} onChange={() => funcSetType(8)} />
                                    <label className="form-check-label text-uppercase" htmlFor="inlineRadio8">{t('content.home_page.chart.energy_plan')}</label>
                                </div>
                                <div hidden={!priorityAll.includes(9) ? true : false} className="form-check form-check-inline">
                                    <input className="form-check-input" type="radio" name="levelWarning" id="inlineRadio9" value={9} style={{ transform: "scale(1.5)" }}
                                        checked={type == 9 ? true : false} onChange={() => funcSetType(9)} />
                                    <label className="form-check-label text-uppercase" htmlFor="inlineRadio9">{t('content.home_page.chart.energy_comparison')}</label>
                                </div>
                            </div>
                        </div>
                    </div>
                    {type != 0 && <>
                        <div id="chart" className="chart-div-right p-1">
                            {/**------------Chart----------------*/}
                            {projects.length > 0 ?
                                <>
                                    {
                                        <div className="name-site">
                                            {projectId == 0 ?
                                                <>
                                                    {
                                                        <h5>{projects[0].customerName}</h5>
                                                    }
                                                </> :
                                                <>
                                                    {
                                                        <h5>{projects[0].customerName} / {projectName} </h5>
                                                    }
                                                </>
                                            }

                                        </div>
                                    }
                                </> :
                                <>
                                    {
                                        <div className="name-site">

                                        </div>
                                    }
                                </>
                            }
                            <div id="chart-1" style={{ border: "2px solid #0a1a5c", borderRadius: "10px", padding: "5px", borderTopLeftRadius: "0", height: "100%" }} className="mb-2">
                                <div>
                                    <div className="float-left" style={{ with: "75%" }}>
                                        <div style={{ position: "relative" }}>
                                            <div className="option-time">
                                                <button className={activeButton == 1 ? "button-time" : "button-time-block"} onClick={() => onChangeValueEnergy(1)}>{t('content.day')}</button>
                                                <button className={activeButton == 6 ? "button-time" : "button-time-block"} onClick={() => onChangeValueEnergy(6)}>{t('content.week')}</button>
                                                <button className={activeButton == 2 ? "button-time" : "button-time-block"} onClick={() => onChangeValueEnergy(2)}>{t('content.month')}</button>
                                                <button hidden={type == 2 || type == 5 ? true : false} className={activeButton == 3 ? "button-time" : "button-time-block"} onClick={() => onChangeValueEnergy(3)}>{t('content.year')}</button>
                                                <button hidden={type == 4 ? true : false} className={activeButton == 4 ? "button-time" : "button-time-block"} onClick={() => onChangeValueEnergy(4)}>{t('content.total')}</button>
                                                <button hidden={type == 8 ? true : false} className={activeButton == 5 ? "button-time" : "button-time-block"} onClick={() => onChangeValueEnergy(5)}>{t('content.custom')}</button>
                                                <button className="button-time-from">
                                                    {activeButton == 2 || activeButton == 3 || activeButton == 4 ?
                                                        <Calendar
                                                            id="from-value"
                                                            className="celendar-picker "
                                                            // maxDate={(new Date)}
                                                            dateFormat={typeFormat}
                                                            view={viewCalender}
                                                            value={fDate}
                                                            onChange={e => onChangeValueTime(e)}
                                                            showWeek
                                                            locale="vi"
                                                        />
                                                        :
                                                        <Calendar
                                                            id="from-value"
                                                            className="celendar-picker"
                                                            // maxDate={(new Date)}
                                                            dateFormat={typeFormat}
                                                            value={fDate}
                                                            onChange={e => onChangeValueTime(e)}
                                                            showWeek
                                                            locale="vi"
                                                        />
                                                    }


                                                </button>
                                                {activeButton == 5 &&
                                                    <button className="button-time-to">
                                                        <Calendar
                                                            id="from-value"
                                                            className="celendar-picker"
                                                            maxDate={(new Date)}
                                                            value={tDate}
                                                            dateFormat={typeFormat}
                                                            onChange={e => onChangeValueToTime(e)}
                                                        />
                                                    </button>
                                                }
                                                {isLoading && (
                                                    <span id="load" style={{ left: "42vw", position: "absolute" }}>
                                                        <img height="50px" src="/resources/image/loading2.gif" alt="loading" />
                                                    </span>
                                                )}
                                            </div>
                                        </div>
                                        <div style={{ paddingLeft: "36vw" }}>
                                            {typeView == true ?
                                                <>
                                                </> :
                                                <>
                                                    <h3>
                                                        <b>BẢNG {typeLoadName}</b>
                                                    </h3>
                                                </>
                                            }

                                        </div>
                                    </div>
                                    <div className="float-right" style={{ with: "30%" }}>
                                        {projects.length > 0 && (projectId != 0) ?
                                            <>
                                                {type != 6 ?
                                                    <>
                                                        {
                                                            <SelectDevice customerId={customerId} projectId={projectId == 0 ? projects[0].projectId : projectId}
                                                                parentCallback={callbackFunction} systemTypeId={systemTypeId} deviceType={deviceType}
                                                                style={{ width: 160, hight: 20, backgroundColor: "#0A1A5C" }}
                                                                titleName={t('content.home_page.choose_device')}
                                                            >
                                                            </SelectDevice>
                                                        }
                                                    </>
                                                    : <>
                                                        {typeSankey == 0 && <>
                                                            {
                                                                <SelectDeviceSankey customerId={customerId} projectId={projectId == 0 ? projects[0].projectId : projectId}
                                                                    parentCallback={callbackFunctionSankey} systemTypeId={systemTypeId} deviceType={deviceType}
                                                                    titleName={t('content.home_page.device_select.choose_source_device')} style={{ width: "230px" }}
                                                                ></SelectDeviceSankey>
                                                            }
                                                        </>}
                                                        {typeSankey == 1 && <>
                                                            {
                                                                <SelectDeviceSankey1 customerId={customerId} projectId={projectId == 0 ? projects[0].projectId : projectId}
                                                                    parentCallback={callbackFunctionSankey1} systemTypeId={systemTypeId} deviceType={deviceType}
                                                                    titleName={t('content.home_page.device_select.choose_device_lv1')}
                                                                    style={{ width: "350px" }}
                                                                    deviceClass1={deviceClass1}
                                                                ></SelectDeviceSankey1>
                                                            }
                                                        </>}
                                                        {typeSankey == 2 && <>
                                                            {
                                                                <SelectDeviceSankey2 customerId={customerId} projectId={projectId == 0 ? projects[0].projectId : projectId}
                                                                    parentCallback={callbackFunctionSankey2} systemTypeId={systemTypeId} deviceType={deviceType}
                                                                    titleName={t('content.home_page.device_select.choose_device_lv2')}
                                                                    style={{ width: "350px" }}
                                                                    deviceClass1={deviceClass1} deviceClass2={deviceClass2}
                                                                ></SelectDeviceSankey2>
                                                            }
                                                        </>}
                                                        {typeSankey == 3 && <>
                                                            {
                                                                <SelectDeviceSankey3 customerId={customerId} projectId={projectId == 0 ? projects[0].projectId : projectId}
                                                                    parentCallback={callbackFunctionSankey3} systemTypeId={systemTypeId} deviceType={deviceType}
                                                                    titleName={t('content.home_page.device_select.choose_device_lv3')}
                                                                    deviceClass1={deviceClass1} deviceClass2={deviceClass2} deviceClass3={deviceClass3}
                                                                    style={{ width: "350px" }}
                                                                ></SelectDeviceSankey3>
                                                            }
                                                        </>}
                                                        {
                                                            <button className="btn btn-select-device" onClick={(e) => reselectSankey(e)} style={{ width: "150px" }}>{t('content.home_page.device_select.select_again')}</button>
                                                        }
                                                    </>

                                                }
                                            </> :
                                            <>
                                                {/* <div style={{ width: "700px", height: "40px" }}></div> */}
                                            </>
                                        }
                                        {
                                            !allowedTypes.includes(type) &&
                                            <button className="btn btn-dashboard ml-1" title="Chuyển đổi hiển thị bảng biểu" type="button" onClick={() => changeValueView(1)}>
                                                <i className={typeView == true ? "fa-solid fa-bars fa-2x" : "fa-solid fa-chart-line fa-2x"} style={viewValue == 1 ? { color: "#ff671f", fontSize: 15 } : { color: "", fontSize: 15 }}></i>
                                            </button>
                                        }
                                        {
                                            !allowedTypes.includes(type) &&
                                            <button type="button" className="btn btn-dashboard ml-1" style={{ border: "1px solid #F37021" }} onClick={exportDataChart}>
                                                <i className="fas fa-solid fa-download fa-1x" style={type === 0 ? { color: "#F37021", height: "10px" } : { color: "#F37021", height: "10px" }}></i>
                                            </button>
                                        }

                                    </div>
                                </div>
                                <div style={{}}>
                                    <div id="chartcontrols" className="mt-5"></div>
                                    <div id="chartdivChart" className="mt-5"></div>
                                    <div id="table-chart" style={{ height: "57vh", overflow: "auto", width: "100%" }}>
                                        {type != 9 && dataTablePower.length > 0 ? <>
                                            <table className="table-parameter" style={{ height: "400px", overflow: "auto", width: "100%" }} ref={tableRef}>
                                                <thead>
                                                    <tr>
                                                        <th className="th-view-time">THỜI GIAN</th>
                                                        {projectId != 0 && dataTablePower.length > 0 ?
                                                            <>
                                                                {dataTablePower?.map((data, i) => (
                                                                    <th className="th-content" key={i}>
                                                                        {data} {unit}
                                                                    </th>
                                                                ))
                                                                }
                                                            </> :
                                                            <>
                                                                {projects?.map((data, i) => (
                                                                    <th className="th-content" key={i}>
                                                                        {(data.projectName)} {unit}
                                                                    </th>
                                                                ))
                                                                }
                                                            </>
                                                        }
                                                        <th className={"th-content-last"}>{t('content.total')} {unit}</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    {dataLoadAllSite.length > 0 ? <>
                                                        {
                                                            dataTotalPower.map((dataTime, i) => (


                                                                <tr key={i}>
                                                                    <th className="td-view-time">{dataTime.time}</th>
                                                                    {projectId != 0 ?
                                                                        <>
                                                                            {dataTablePower?.map((data, x) => (

                                                                                <th key={x} className="td-content-en" >
                                                                                    {formatCurrency(dataTime[data])}
                                                                                </th>
                                                                            ))
                                                                            }
                                                                        </> :
                                                                        <>
                                                                            {projects?.map((data, x) => (

                                                                                <th key={x} className="td-content-en">
                                                                                    {formatCurrency(dataTime[data.projectName])}
                                                                                </th>
                                                                            ))
                                                                            }
                                                                        </>
                                                                    }
                                                                    <th className="td-content-en">
                                                                        {formatCurrency(dataTime.total)}
                                                                    </th>
                                                                </tr>
                                                            ))
                                                        }
                                                    </> : <>
                                                    </>}
                                                </tbody>
                                            </table>
                                        </> : <>
                                            <table className="table-parameter" style={{ height: "400px", overflow: "auto", width: "100%" }} ref={tableRef}>
                                                <thead>
                                                    <tr>
                                                        <th className="th-view-time">DỰ ÁN - ĐIỂM ĐO</th>
                                                        <th className="th-content">ĐIỆN NĂNG (kWh)</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    {dataTablePowerType9.length > 0 ? <>
                                                        {
                                                            dataTablePowerType9.map((data, i) => (
                                                                <tr key={i}>
                                                                    <th className="td-view-time">{data.name}</th>
                                                                    <th className="td-content-en">{data.ep}</th>
                                                                </tr>
                                                            ))
                                                        }
                                                    </> : <>
                                                    </>}
                                                </tbody>
                                            </table>
                                        </>}
                                    </div>
                                </div>
                            </div>

                            <div className="modal fade" id="modalLoad" tabIndex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                                <div className="modal-dialog modal-dialog-centered">
                                    <div className="modal-content">
                                        <div className="modal-header">
                                            <h5 className="modal-title" id="exampleModalLongTitle">CALENDAR</h5>
                                            <button type="button" className="close" data-dismiss="modal" aria-label="Close">
                                                <span aria-hidden="true">&times;</span>
                                            </button>
                                        </div>
                                        <div className="modal-body">
                                            <div className="input-group float-left mr-1" style={{ width: "270px" }}>
                                                <h5>Từ ngày</h5>
                                                <Calendar
                                                    id="from-value"
                                                    className="celendar-picker"
                                                    dateFormat={typeFormat}
                                                    maxDate={(new Date)}
                                                    value={fromDate}
                                                    view={viewCalender}
                                                    onChange={(e) => setFromDate(e.value)}
                                                />
                                                <div className="input-group-prepend background-ses">
                                                    <span className="input-group-text pickericon">
                                                        <span className="far fa-calendar"></span>
                                                    </span>
                                                </div>
                                            </div>
                                            <div className="input-group float-left mr-1" style={{ width: "270px" }}>
                                                <h5>Đến ngày</h5>
                                                <Calendar
                                                    id="to-value"
                                                    className="celendar-picker"
                                                    dateFormat={typeFormat}
                                                    maxDate={(new Date)}
                                                    value={toDate}
                                                    onChange={e => setToDate(e.value)}
                                                />
                                                <div className="input-group-prepend background-ses">
                                                    <span className="input-group-text pickericon" >
                                                        <span className="far fa-calendar"></span>
                                                    </span>
                                                </div>
                                            </div>
                                        </div>formatDefaultLocale
                                        <div className="modal-footer">
                                            <button type="button" className="btn btn-secondary" data-dismiss="modal">Đóng</button>
                                            <button type="button" className="btn btn-primary" onClick={() => {
                                                funcGetDataLoad(projectId, type, typeTime, fromDate, toDate, activeButton, systemTypeId, deviceId, null, null, dataDeviceName)
                                            }}>Chấp nhận</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </>}
                </div >
                :
                <AccessDenied></AccessDenied>
            }
        </>
    )
}

export default Chart;