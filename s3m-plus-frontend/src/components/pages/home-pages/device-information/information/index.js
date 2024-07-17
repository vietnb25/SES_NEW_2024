import React, { useState } from "react";
import { useEffect } from "react";
import { useRef } from "react";
import { Link, Route, Switch, useHistory, useLocation, useParams } from "react-router-dom/cjs/react-router-dom.min";
import { Calendar } from 'primereact/calendar';
import ProjectService from "../../../../../services/ProjectService";
import DeviceService from "../../../../../services/DeviceService";
import moment from "moment";
import CONS from "../../../../../constants/constant";
import WarningService from "../../../../../services/WarningService";
import ReactModal from "react-modal";
import Pagination from "react-js-pagination";
import "./../index.css"
import { CSVLink } from "react-csv";
import * as am5 from "@amcharts/amcharts5";
import * as am5xy from "@amcharts/amcharts5/xy";
import am5themes_Animated from "@amcharts/amcharts5/themes/Animated";
import WarningLoad from "../../../home-pages/warning/list";
import { useDownloadExcel } from "react-export-table-to-excel";
import AccessDenied from "../../../access-denied/AccessDenied";
import Gateway from "./device/gateway";
import Htr02 from "./device/htr02";
import Ams01 from "./device/ams01";
import { t } from "i18next";
import Flow from "./device/flow";
import Pressure from "./device/pressure";

const Information = () => {
    const $ = window.$;

    const [equipmentTab, setEquipmentTab] = useState(1);
    const [fromDate, setFromDate] = useState(new Date());
    const [toDate, setToDate] = useState(new Date());
    const [fromDateWarning, setFromDateWarning] = useState(new Date());
    const [toDateWarning, setToDateWarning] = useState(new Date());
    const [warningLevel, setWarningLevel] = useState(0);
    const location = useLocation();
    const [infoDevice, setInfoDevice] = useState()
    const [listWarning, setListWarning] = useState([])
    const [accessDenied, setAccessDenied] = useState(false)
    const history = useHistory();
    const [listDataInstance, setListDataInstance] = useState([])
    const [valueRadio, setValueRadio] = useState(1)
    const [page, setPage] = useState(1);
    const [totalPage, setTotalPage] = useState(1);
    const [data, setData] = useState([]);
    const [settingValue, setSettingValue] = useState(null);
    const [warningType, setWarningType] = useState(0);
    const [systemTypeId, setSystemTypeId] = useState();
    const [warningCategory, setWarningCategory] = useState();
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isModalChartOpen, setIsModalChartOpen] = useState(false);
    const [valueRadioVol, setValueRadioVol] = useState(1);
    const [valueRadioA, setValueRadioA] = useState(1);
    const [isActiveButton, setIsActiveButton] = useState(1);
    const [dataWarning, setDataWarning] = useState({
        page: 1,
        totalPage: 1,
        warningType: null,
        data: []
    });
    const [statusMCCB, setStatusMCCB] = useState(true)
    const [unit, setUnit] = useState("V")
    const [typeFormat, setTypeFormat] = useState("yy-mm-dd")
    const [viewCalender, setViewCalender] = useState("")
    const [optionTime, setOptionTime] = useState(1)
    const [isLoading, setIsLoading] = useState(true);
    const [deviceType, setDeviceType] = useState();
    const [listDataInstanceFrame2, setListDataInstanceFrame2] = useState([])
    const [listDataInstanceFrame, setListDataInstanceFrame] = useState([])
    const [listDataInstanceFrame2Table, setListDataInstanceFrame2Table] = useState([])
    const [listDataInstanceFrame1Table, setListDataInstanceFrame1Table] = useState([])
    const allowedTypeDevice = [3, 5, 6, 7, 9, 10];
    const param = useParams();

    const tableRef = useRef(null);
    const { onDownload } = useDownloadExcel({
        currentTableRef: tableRef.current,
        filename: "dataDeviceInformation",
        sheet: "dataDeviceInformation"
    });

    const [paramHeader, setParamHeader] = useState();

    const [warningSolar, setWarningSolar] = useState([
        {
            warningType: 201,
            warningName: t('content.home_page.warning_tab.ground_fault'),
            active: "false"

        },
        {
            warningType: 202,
            warningName: t('content.home_page.warning_tab.dc_over_volt'),
            active: "false"

        },
        {
            warningType: 203,
            warningName: t('content.home_page.warning_tab.ac_disconnect'),
            active: "false"

        },
        {
            warningType: 204,
            warningName: t('content.home_page.warning_tab.dc_disconnect'),
            active: "false"
        },
        {
            warningType: 205,
            warningName: t('content.home_page.warning_tab.grid_disconnect'),
            active: "false"

        },
        {
            warningType: 206,
            warningName: t('content.home_page.warning_tab.door_operation'),
            active: "false"

        },
        {
            warningType: 207,
            warningName: t('content.home_page.warning_tab.manual_shutdown'),
            active: "false"

        },
        {
            warningType: 208,
            warningName: t('content.home_page.warning_tab.over_temp'),
            active: "false"

        },
        {
            warningType: 209,
            warningName: t('content.home_page.warning_tab.over_frequency'),
            active: "false"

        },
        {
            warningType: 210,
            warningName: t('content.home_page.warning_tab.under_frequency'),
            active: "false"

        },
        {
            warningType: 211,
            warningName: t('content.home_page.warning_tab.ac_over_volt'),
            active: "false"

        },
        {
            warningType: 212,
            warningName: t('content.home_page.warning_tab.ac_under_volt'),
            active: "false"

        },
        {
            warningType: 213,
            warningName: t('content.home_page.warning_tab.blown_fuse'),
            active: "false"

        },
        {
            warningType: 214,
            warningName: t('content.home_page.warning_tab.under_temp'),
            active: "false"

        },
        {
            warningType: 215,
            warningName: t('content.home_page.warning_tab.memory_loss'),
            active: "false"

        },
    ]);
    const [warningLoad, setWarningLoad] = useState([
        {
            warningType: CONS.WARNING_TYPE.NGUONG_AP_CAO,
            warningName: t('content.home_page.warning_tab.over_volt'),
            active: "false"

        },
        {
            warningType: CONS.WARNING_TYPE.NGUONG_AP_THAP,
            warningName: t('content.home_page.warning_tab.under_volt'),
            active: "false"

        },
        {
            warningType: CONS.WARNING_TYPE.NHIET_DO_TIEP_XUC,
            warningName: t('content.home_page.warning_tab.contact_temp'),
            active: "false"

        },
        {
            warningType: CONS.WARNING_TYPE.LECH_PHA,
            warningName: t('content.home_page.warning_tab.phase_deviation'),
            active: "false"
        },
        {
            warningType: CONS.WARNING_TYPE.QUA_TAI,
            warningName: t('content.home_page.warning_tab.over_load'),
            active: "false"

        },
        {
            warningType: 106,
            warningName: t('content.home_page.warning_tab.under_frequency'),
            active: "false"

        },
        {
            warningType: CONS.WARNING_TYPE.TAN_SO_CAO,
            warningName: t('content.home_page.warning_tab.over_frequency'),
            active: "false"

        },
        {
            warningType: CONS.WARNING_TYPE.MAT_NGUON_PHA,
            warningName: 'Mất nguồn',
            active: "false"

        },
        {
            warningType: CONS.WARNING_TYPE.NGUONG_TONG_HAI,
            warningName: t('content.home_page.warning_tab.harmonic'),
            active: "false"

        },
        {
            warningType: CONS.WARNING_TYPE.DONG_TRUNG_TINH,
            warningName: 'Dòng trung tính',
            active: "false"

        },
        {
            warningType: CONS.WARNING_TYPE.DONG_MO_CUA,
            warningName: t('content.home_page.warning_tab.door_operation'),
            active: "false"

        },
        {
            warningType: CONS.WARNING_TYPE.COS_THAP_TONG,
            warningName: t('content.home_page.warning_tab.under_power_factor'),
            active: "false"

        },
        {
            warningType: CONS.WARNING_TYPE.SONG_HAI_DONG_DIEN_BAC_N,
            warningName: t('content.home_page.warning_tab.current_harmonics_n'),
            active: "false"

        },
        {
            warningType: CONS.WARNING_TYPE.SONG_HAI_DIEN_AP_BAC_N,
            warningName: t('content.home_page.warning_tab.voltage_harmonics_n'),
            active: "false"

        },
        {
            warningType: 110,
            warningName: t('content.home_page.warning_tab.total_current_harmonics'),
            active: "false"

        },
        {
            warningType: 111,
            warningName: t('content.home_page.warning_tab.total_voltage_harmonics'),
            active: "false"

        }
    ]);


    const [warningGrid, setWarningGrid] = useState([
        {
            warningType: CONS.WARNING_TYPE.PHONG_DIEN,
            warningName: t('content.home_page.warning_tab.energy_discharge'),
            active: "false"

        }
    ]);

    const [warningTempHum, setWarningTempHum] = useState([
        {
            warningType: 301,
            warningName: t('content.home_page.warning_tab.over_temp'),
            active: "false"

        },
        {
            warningType: 302,
            warningName: t('content.home_page.warning_tab.under_temp'),
            active: "false"

        },
        {
            warningType: 303,
            warningName: t('content.home_page.warning_tab.over_humidity'),
            active: "false"

        },
        {
            warningType: 304,
            warningName: t('content.home_page.warning_tab.under_humidity'),
            active: "false"
        }
    ]);

    const [warningPressure, setWarningPressure] = useState([
        {
            warningType: "701",
            warningName: t('content.home_page.warning_tab.high_pressure'),
            active: "false"

        },
        {
            warningType: "702",
            warningName: t('content.home_page.warning_tab.low_pressure'),
            active: "false"

        },
    ]);

    const [warningFlow, setWarningFlow] = useState([
        {
            warningType: "1001",
            warningName: t('content.home_page.warning_tab.high_flow'),
            active: "false"

        },
        {
            warningType: "1002",
            warningName: t('content.home_page.warning_tab.low_flow'),
            active: "false"

        },
    ]);

    const [iDevice, setIDevice] = useState({
        deviceId: "",
        deviceName: "",
        createDate: "",
        systemTypeId: "",
        deviceTypeId: "",
        description: "",
        projectName: "",
    });

    const [device, setDevice] = useState({
        systemTypeId: "",
        deviceType: "",
        operatingStatus: "",
        uab: "",
        pfa: "",
        ubc: "",
        pfb: "",
        uca: "",
        pfc: "",
        uan: "",
        sa: "",
        ubn: "",
        sb: "",
        ucn: "",
        sc: "",
        ia: "",
        ep: "",
        ib: "",
        ic: "",
        f: "",
        pa: "",
        pb: "",
        pc: "",
        t: "",
        ptotal: "",
        qa: "",
        qb: "",
        qc: "",
        qtotal: "",
        vdcCombiner: "",
        idcCombiner: "",
        pdcCombiner: "",
        h: "",
        vdcStr: "",
        idcStr: "",
        pdcStr: "",
        sentDate: "",
    })

    const [chartName, setChartName] = useState("ĐIỆN ÁP");

    useEffect(() => {
        document.title = "Thông tin chi tiết thiết bị";
        getInfoDevice(param.customerId, param.deviceId);

    }, [param.deviceId])


    const funcSetWarningLevel = (typeSelected) => {
        setWarningLevel(() => typeSelected)
        functGetWarnings(fromDateWarning, toDateWarning, typeSelected, 1)
    }

    const funcEquipment = (value) => {
        setEquipmentTab(() => value);
        let type = ""
        if (value == 2) {
            if (iDevice.deviceType == 3) {
                setChartName("NHIỆT ĐỘ")
                setUnit("°C")
                type = 6;
            } else if (iDevice.deviceType == 5 || iDevice.deviceType == 6) {
                setChartName("PHÓNG ĐIỆN")
                setUnit("dB")
                type = 12;
            } else if (iDevice.deviceType == 9) {
                setChartName("TÌNH TRẠNG SERVER")
                setUnit("")
                type = 14;
            } else if (iDevice.deviceType == 7) {
                setChartName("ÁP SUẤT")
                setUnit("bar")
                type = 17;
            } else if (iDevice.deviceType == 10) {
                setChartName("LƯU LƯỢNG")
                setUnit("m3")
                type = 15;
            }
            else {
                type = 1;
            }
            setValueRadio(type)
            getListDataInstance(param.deviceId)
            getListDataInstanceFrame2(param.deviceId)
        }
        if (value === 3) {
            // functGetWarnings(fromDateWarning, toDateWarning, warningLevel, 1)
        }
    }

    const downloadDataInstance = async () => {
        setIsLoading(true)
        let fDate = null;
        let tDate = null;
        let optionNameChild = null;
        if (isActiveButton == 1) {
            fDate = moment(new Date(fromDate)).format("YYYY-MM-DD")
        }
        if (isActiveButton == 2) {
            fDate = moment(new Date(fromDate)).format("YYYY-MM")
        }
        if (isActiveButton == 3) {
            fDate = moment(new Date(fromDate)).format("YYYY")
        }
        if (isActiveButton == 4) {
            fDate = moment(new Date(fromDate)).format("YYYY-MM-DD")
            tDate = moment(new Date(toDate)).format("YYYY-MM-DD")
        }
        if (valueRadio == 8) {
            optionNameChild = valueRadioA;
        }

        if (valueRadio == 9) {
            optionNameChild = valueRadioVol;
        }

        let res = await DeviceService.exportDataInstance(param.customerId, param.deviceId, fDate, tDate, optionTime, iDevice.projectName, iDevice.deviceName, valueRadio, isActiveButton, optionNameChild)
        if (res.status == 200) {
            setIsLoading(false)
        }
    }

    const handleChange = e => {
        let string = e.target.id;
        let number = string.charAt(5) + "" + string.charAt(6);
        console.log(number);
        setValueRadio(number)
        setValueRadioA(1)
        setValueRadioVol(1)
        if (number == 5) {
            setOptionTime(1)
            document.getElementById("select-time").value = ""
        }

        let node = document.getElementById("input" + number);
        if (node.parentElement) {
            node.parentElement.style.backgroundColor = "#0a1a5c";
        }

        if (iDevice.deviceType == 3) {
            for (let i = 1; i < 11; i++) {
                if (i != number) {
                    let a = document.getElementById("input" + i);
                    if (a.parentElement) {
                        a.parentElement.style.backgroundColor = "#fff";
                    }
                }
            }

        }
        else if (iDevice.deviceType == 5 || iDevice.deviceType == 6) {
            for (let i = 12; i < 14; i++) {
                if (i != number) {
                    let a = document.getElementById("input" + i);
                    if (a.parentElement) {
                        a.parentElement.style.backgroundColor = "#fff";
                    }
                }
            }
        }
        else if (iDevice.deviceType == 10) {
            for (let i = 15; i < 17; i++) {
                if (i != number) {
                    let a = document.getElementById("input" + i);
                    if (a.parentElement) {
                        a.parentElement.style.backgroundColor = "#fff";
                    }
                }
            }
        }
        else {
            for (let i = 1; i < 10; i++) {
                if (i != number) {
                    let a = document.getElementById("input" + i);
                    if (a.parentElement) {
                        a.parentElement.style.backgroundColor = "#fff";
                    }
                }
            }
        }

    };

    const handleChangeValueVoltage = e => {
        let string = e.target.id;
        let number = string.charAt(1);
        setValueRadioVol(number)

        let node = document.getElementById("v" + number);
        if (node.parentElement) {
            node.parentElement.style.backgroundColor = "#0a1a5c";
        }
        for (let i = 1; i < 4; i++) {
            if (i != number) {
                let a = document.getElementById("v" + i);
                if (a.parentElement) {
                    a.parentElement.style.backgroundColor = "#fff";
                }
            }
        }
    }

    const handleChangeValueA = e => {
        let string = e.target.id;
        let number = string.charAt(1);
        setValueRadioA(number)

        let node = document.getElementById("i" + number);
        if (node.parentElement) {
            node.parentElement.style.backgroundColor = "#0a1a5c";
        }
        for (let i = 1; i < 4; i++) {
            if (i != number) {
                let a = document.getElementById("i" + i);
                if (a.parentElement) {
                    a.parentElement.style.backgroundColor = "#fff";
                }
            }
        }
    }

    const handleChangeValue = e => {
        let string = e.target.id;
        let number = string.charAt(1);
        setValueRadioA(number)

        let node = document.getElementById("i" + number);
        if (node.parentElement) {
            node.parentElement.style.backgroundColor = "#0a1a5c";
        }
        for (let i = 1; i < 4; i++) {
            if (i != number) {
                let a = document.getElementById("i" + i);
                if (a.parentElement) {
                    a.parentElement.style.backgroundColor = "#fff";
                }
            }
        }
    }

    const onValueImage = (device) => {
        let src = ""
        if (device.systemTypeId == 1) {
            if (device.deviceType == 1) {
                src = "/resources/image/icon-tudienhathe.svg"
            }
        }
        if (device.systemTypeId == 2) {
            if (device.deviceType == 1) {
                src = "/resources/image/icon-inverter.svg"
            }
            if (device.deviceType == 3) {
                src = "/resources/image/icon-dccombinerbox.svg"
            }
            if (device.deviceType == 4) {
                src = "/resources/image/icon-string.svg"
            }
        }
        if (device.systemTypeId == 5) {
            src = "/resources/image/icon-khoangtutrungthe.svg"
        }

        return src
    }

    const getInfoDevice = async (customerId, deviceId) => {
        let res = await DeviceService.getInforDeviceByDeviceId(customerId, deviceId)
        if (res.status === 200) {
            if (res.data.length > 0) {
                setAccessDenied(false)
                let info = res.data[1]
                let warning = res.data[0]
                setIDevice(info)
                if (info.systemTypeId == 1) {
                    for (let i = 0; i < warningLoad.length; i++) {
                        for (let j = 0; j < warning.length; j++) {
                            if (warningLoad[i].warningType == warning[j].warningType) {
                                warningLoad[i].active = true
                            }
                        }
                    }
                    let warningTrue = [];
                    let warningFalse = [];
                    for (let i = 0; i < warningLoad.length; i++) {
                        if (warningLoad[i].active == true) {
                            warningTrue.push(warningLoad[i])
                        } else {
                            warningFalse.push(warningLoad[i])
                        }
                    }
                    setWarningLoad(warningTrue.concat(warningFalse))
                }
                if (res.data[1].systemTypeId == 2) {
                    for (let i = 0; i < warningSolar.length; i++) {
                        for (let j = 0; j < warning.length; j++) {
                            if (warningSolar[i].warningType == warning[j].warningType) {
                                warningSolar[i].active = true
                            }
                        }
                    }
                }
                // if (res.data[1].systemTypeId == 5) {
                //     for (let i = 0; i < warningGrid.length; i++) {
                //         for (let j = 0; j < warning.length; j++) {
                //             if (warningGrid[i].warningType == warning[j].warningType) {
                //                 warningGrid[i].active = true
                //             }
                //         }
                //     }
                // }
                if (res.data[1].deviceType == 3) {
                    for (let i = 0; i < warningTempHum.length; i++) {
                        for (let j = 0; j < warning.length; j++) {
                            if (warningTempHum[i].warningType == warning[j].warningType) {
                                warningTempHum[i].active = true
                            }
                        }
                    }
                    let warningTrue = [];
                    let warningFalse = [];
                    for (let i = 0; i < warningTempHum.length; i++) {
                        if (warningTempHum[i].active == true) {
                            warningTrue.push(warningTempHum[i])
                        } else {
                            warningFalse.push(warningTempHum[i])
                        }
                    }
                    setWarningTempHum(warningTrue.concat(warningFalse))
                }

                if (res.data[1].deviceType == 6) {
                    for (let i = 0; i < warningGrid.length; i++) {
                        for (let j = 0; j < warning.length; j++) {
                            if (warningGrid[i].warningType.includes(warning[j].warningType)) {
                                warningGrid[i].active = true
                            }
                        }
                    }
                    let warningTrue = [];
                    let warningFalse = [];
                    for (let i = 0; i < warningGrid.length; i++) {
                        if (warningGrid[i].active == true) {
                            warningTrue.push(warningGrid[i])
                        } else {
                            warningFalse.push(warningGrid[i])
                        }
                    }
                    setWarningGrid(warningTrue.concat(warningFalse))
                }

                if (res.data[1].deviceType == 5) {
                    for (let i = 0; i < warningGrid.length; i++) {
                        for (let j = 0; j < warning.length; j++) {
                            if (warningGrid[i].warningType.includes(warning[j].warningType)) {
                                warningGrid[i].active = true
                            }
                        }
                    }
                    let warningTrue = [];
                    let warningFalse = [];
                    for (let i = 0; i < warningGrid.length; i++) {
                        if (warningGrid[i].active == true) {
                            warningTrue.push(warningGrid[i])
                        } else {
                            warningFalse.push(warningGrid[i])
                        }
                    }
                    setWarningGrid(warningTrue.concat(warningFalse))
                }

                if (res.data[1].deviceType == 7) {
                    for (let i = 0; i < warningPressure.length; i++) {
                        for (let j = 0; j < warning.length; j++) {
                            if (warningPressure[i].warningType.includes(warning[j].warningType)) {
                                warningPressure[i].active = true
                            }
                        }
                    }
                    let warningTrue = [];
                    let warningFalse = [];
                    for (let i = 0; i < warningPressure.length; i++) {
                        if (warningPressure[i].active == true) {
                            warningTrue.push(warningPressure[i])
                        } else {
                            warningFalse.push(warningPressure[i])
                        }
                    }
                    setWarningPressure(warningTrue.concat(warningFalse))
                }

                if (res.data[1].deviceType == 10) {
                    for (let i = 0; i < warningFlow.length; i++) {
                        for (let j = 0; j < warning.length; j++) {
                            if (warningFlow[i].warningType.includes(warning[j].warningType)) {
                                warningFlow[i].active = true
                            }
                        }
                    }
                    let warningTrue = [];
                    let warningFalse = [];
                    for (let i = 0; i < warningFlow.length; i++) {
                        if (warningFlow[i].active == true) {
                            warningTrue.push(warningFlow[i])
                        } else {
                            warningFalse.push(warningFlow[i])
                        }
                    }
                    setWarningFlow(warningTrue.concat(warningFalse))
                }


                await getDataInstance(customerId, res.data[1].systemTypeId, res.data[1].deviceType, deviceId)
            } else {
                setAccessDenied(true)
            }
        }
    }

    const getDataInstance = async (customerId, systemTypeId, deviceType, deviceId) => {
        let res = await DeviceService.getDataInstance(customerId, systemTypeId, deviceType, deviceId)
        if (res.status === 200) {
            setDevice(res.data)
            if (res.data.uab == 0 && res.data.ubc == 0 && res.data.uca == 0 && res.data.uan == 0 && res.data.ubn == 0 && res.data.ucn == 0) {
                setStatusMCCB(false)

            }
        }
    }

    const getDataByRadioValue = async (e) => {
        setValueRadio(e.target.value)
        if (e.target.value == 1) {
            setChartName(t('content.home_page.device.voltage'))
            setUnit("V")
        }
        if (e.target.value == 2) {
            setChartName(t('content.home_page.device.power'))
            setUnit("kW")
        }
        if (e.target.value == 3) {
            setChartName(t('content.home_page.device.current'))
            setUnit("A")
        }
        if (e.target.value == 4) {
            setChartName("COSPHI")
            setUnit("")
        }
        if (e.target.value == 5) {
            setChartName(t('content.home_page.device.energy'))
            setUnit("kWh")
        }
        if (e.target.value == 6) {
            if (iDevice.deviceType == 3) {
                setChartName(t('content.home_page.warning_tab.temperature'))
                setUnit("°C")
            } else {
                setChartName(t('content.frequency'))
                setUnit("Hz")
            }
        }

        if (e.target.value == 7) {
            setChartName("CHẤT LƯỢNG ĐIỆN NĂNG")
            setUnit("")
        }
        if (e.target.value == 8) {
            setChartName("SÓNG HÀI DÒNG ĐIỆN")
            setUnit("%")
        }
        if (e.target.value == 9) {
            setChartName("SÓNG HÀI ĐIỆN ÁP")
            setUnit("%")
        }
        if (e.target.value == 11) {
            setChartName("ĐỘ ẨM")
            setUnit("%")
        }
        if (e.target.value == 12) {
            setChartName("RATIO/EPPC")
            setUnit("dB")
        }
        if (e.target.value == 13) {
            setChartName("INDICATOR")
            setUnit("")
        }
        if (e.target.value == 14) {
            setChartName("STATUS SERVER")
            setUnit("")
        }

        if (e.target.value == 15) {
            setChartName("LƯU LƯỢNG")
            setUnit("m³")
        }
        if (e.target.value == 16) {
            setChartName("LƯU LƯỢNG TÍCH LŨY")
            setUnit("m³")
        }
        if (e.target.value == 17) {
            setChartName("ÁP SUẤT")
            setUnit("bar")
        }
    }

    const getListDataInstance = async (deviceId, fDate, tDate) => {
        // let res = await DeviceService.getListDataInstance(param.customerId, deviceId, fDate, tDate, optionTime);
        // if (res.status === 200) {
        //     let data = res.data;
        //     setListDataInstance(data.dataHumidity.length > 0 ? data.dataHumidity : data.dataInstance);
        //     setIsLoading(false)
        // }
    }

    const getListDataInstanceFrame2 = async (deviceId, fDate, tDate) => {
        let resDataFrame2 = await DeviceService.getListDataInstanceFrame2(param.customerId, deviceId, fDate, tDate, optionTime);
        let resDataFrame1 = await DeviceService.getListDataInstance(param.customerId, deviceId, fDate, tDate, optionTime);
        if (resDataFrame2.status === 200 && resDataFrame1.status === 200) {
            let dataFrame1 = (resDataFrame1.data.dataHumidity.length > 0 ? resDataFrame1.data.dataHumidity : resDataFrame1.data.dataInstance);
            let dataInstance = [...dataFrame1];
            let dataFrame2 = resDataFrame2.data;
            let dataFrameCombine = combineFrames(dataFrame1, dataFrame2);
            let dataInstanceFr2 = [...dataFrameCombine]
            setListDataInstance(dataInstance);
            setListDataInstanceFrame2(dataFrameCombine);
            setListDataInstanceFrame(dataFrame2)
            setListDataInstanceFrame1Table((resDataFrame1.data.dataHumidity.length > 0 ? resDataFrame1.data.dataHumidity : resDataFrame1.data.dataInstance).reverse())
            setListDataInstanceFrame2Table(dataInstanceFr2.reverse());
            setIsLoading(false)
        }
    }

    const getDataByFDateTDate = async () => {
        setIsLoading(true)
        let fDate = null;
        let tDate = null;
        if (isActiveButton == 1) {
            fDate = moment(new Date(fromDate)).format("YYYY-MM-DD")
        }
        if (isActiveButton == 2) {
            fDate = moment(new Date(fromDate)).format("YYYY-MM")
        }
        if (isActiveButton == 3) {
            fDate = moment(new Date(fromDate)).format("YYYY")
        }
        if (isActiveButton == 4) {
            fDate = moment(new Date(fromDate)).format("YYYY-MM-DD HH:mm")
            tDate = moment(new Date(toDate)).format("YYYY-MM-DD HH:mm")
        }

        getListDataInstance(param.deviceId, fDate, tDate)
        getListDataInstanceFrame2(param.deviceId, fDate, tDate)
    }

    const functGetWarnings = async (fromDate, toDate, level, page) => {
        $('#table-warning').hide();
        $('#loading-warning').show();
        let customerId = param.customerId;
        let deviceId = param.deviceId;
        let fDate = moment(new Date(fromDate)).format("YYYY-MM-DD 00:00:00");
        let tDate = moment(new Date(toDate)).format("YYYY-MM-DD 23:59:59")
        let res = await WarningService.getWarnings(customerId, iDevice.systemTypeId, 0, fDate, tDate, level, page, deviceId)
        if (res.status === 200) {
            setData(() => res.data.data)
            setTotalPage(() => res.data.totalPage);
            setPage(1);
            $('#loading-warning').hide();
            $('#table-warning').show();
        }
    }

    const handlePagination = async page => {
        $('#table').hide();
        $('#loading').show();
        setPage(page);

        let customerId = param.customerId;
        let deviceId = param.deviceId;
        let res = await WarningService.getWarnings(customerId, iDevice.systemTypeId, 0, fromDateWarning, toDateWarning, warningLevel, page, deviceId)
        if (res.status === 200) {
            setData(res.data.data);
            setTotalPage(res.data.totalPage);
            $('#loading').hide();
            $('#table').show();
        }

    }

    const handleClickWarning = async (warningType, systemTypeId, projectId, deviceId, category, fromDate, toDate) => {
        setSystemTypeId(() => systemTypeId)
        setWarningCategory(() => category)
        let customerId = param.customerId;
        let res = await WarningService.showDataWarningByDevice(systemTypeId, warningType, fromDate, toDate, projectId, customerId, deviceId, category, page);
        if (res.status === 200) {
            setDeviceType(res.data.deviceType)
            if (category == 1) {
                let setting = res.data.settingValue;
                if (res.data.settingValue.length == 1) {
                    setSettingValue(res.data.settingValue);
                } else {
                    let values = res.data.settingValue.split(",");
                    setSettingValue(values);
                }
            }
            setWarningType(() => warningType)
            setDataWarning({ ...dataWarning, data: res.data.dataWarning });
            setIsModalOpen(true);
        }
    }

    const getDataByDate = async () => {
        $('#table-warning').hide();
        $('#loading-warning').show();
        let customerId = param.customerId;
        let deviceId = param.deviceId;
        let fromDate = moment(new Date(fromDateWarning)).format("YYYY-MM-DD 00:00:00");
        let toDate = moment(new Date(toDateWarning)).format("YYYY-MM-DD 23:59:59");
        let res = await WarningService.getWarnings(customerId, iDevice.systemTypeId, 0, fromDate, toDate, 0, 1, deviceId)
        if (res.status === 200) {
            setData(() => res.data.data)
            setTotalPage(() => res.data.totalPage);
            setPage(1);
            $('#loading-warning').hide();
            $('#table-warning').show();
        }
    }


    let headers = [
        { label: "Loại cảnh báo", key: "warningTypeName" },
        { label: "Mức cảnh báo", key: "warningLevel" },
        { label: "Lần cuối xảy ra", key: "toDate" },
        { label: "Số lần xảy ra", key: "total" },
    ]

    function combineFrames(frame1, frame2) {
        const combinedFrame = [];
        const maxLength = Math.max(frame1.length, frame2.length);
        if (frame2.length > 0) {
            for (let i = 0; i < maxLength; i++) {
                const item1 = frame1[i] || {};
                const item2 = frame2[i] || {};
                const combinedItem = {};
                for (const key in item1) {
                    combinedItem[key] = item1[key] !== null ? item1[key] : item2[key];
                }
                for (const key in item2) {
                    if (!(key in combinedItem)) {
                        combinedItem[key] = item2[key];
                    }
                }

                combinedFrame.push(combinedItem);
            }
        } else {
            combinedFrame.push(frame1);
        }
        return combinedFrame;
    }

    const showDataChart = () => {

        $('#myModal').modal('show').one('shown.bs.modal', function () {
            let header = [];
            let unitChart = unit
            let tableInstance = document.getElementById("table-instance")
            for (let i = 0, row; row = tableInstance.rows[i]; i = 1) {
                if (i = 1) {
                    for (var j = 1, col; col = row.cells[j]; j++) {
                        header.push(col.innerText + "");
                    }
                    break;
                }
            }
            let data = [];
            if (listDataInstanceFrame.length > 0) {
                data = listDataInstanceFrame2;
            } else {
                data = listDataInstance;
            }

            let systemTypeId = iDevice.systemTypeId;
            let deviceTypeId = iDevice.deviceType;

            for (let i = 0; i < data.length; i++) {
                data[i].sendDate = moment(new Date(data[i].sendDate)).format("YYYY-MM-DD HH:mm:ss");
            }

            am5.array.each(am5.registry.rootElements, function (root) {
                if (root) {
                    if (root.dom.id == "chartdiv-device") {
                        root.dispose();
                    }
                }
            });

            var root = am5.Root.new("chartdiv-device");

            // Set themes
            // https://www.amcharts.com/docs/v5/concepts/themes/
            root.setThemes([
                am5themes_Animated.new(root)
            ]);

            if (valueRadio == 5) {
                var chart = root.container.children.push(am5xy.XYChart.new(root, {
                    panX: false,
                    panY: false,
                    wheelX: "panX",
                    wheelY: "zoomX",
                    layout: root.verticalLayout
                }));


                // Add legend
                // https://www.amcharts.com/docs/v5/charts/xy-chart/legend-xy-series/
                var legend = chart.children.push(
                    am5.Legend.new(root, {
                        centerX: am5.p50,
                        x: am5.p50
                    })
                );

                // Create axes
                // https://www.amcharts.com/docs/v5/charts/xy-chart/axes/
                var xRenderer = am5xy.AxisRendererX.new(root, {
                    cellStartLocation: 0.1,
                    cellEndLocation: 0.9
                })

                xRenderer.labels.template.setAll({
                    rotation: -70,
                    paddingTop: -20,
                    paddingRight: 10,
                    fontSize: 10
                });

                var xAxis = chart.xAxes.push(am5xy.CategoryAxis.new(root, {
                    categoryField: "sendDate",
                    renderer: xRenderer,
                    tooltip: am5.Tooltip.new(root, {}),
                }));

                xRenderer.grid.template.setAll({
                    location: 1
                })

                xAxis.data.setAll(data);

                var yAxis = chart.yAxes.push(am5xy.ValueAxis.new(root, {
                    renderer: am5xy.AxisRendererY.new(root, {
                        value: 0,
                        strokeOpacity: 0.1
                    })
                }));


                // Add series
                // https://www.amcharts.com/docs/v5/charts/xy-chart/series/
                function makeSeries(name, fieldName, data, color) {
                    var series = chart.series.push(am5xy.ColumnSeries.new(root, {
                        name: name,
                        xAxis: xAxis,
                        yAxis: yAxis,
                        valueYField: fieldName,
                        categoryXField: "sendDate"
                    }));

                    series.columns.template.setAll({
                        tooltipText: "{name}, {categoryX}: [bold]{valueY} kWh",
                        width: am5.percent(90),
                        tooltipY: 0,
                        strokeOpacity: 0
                    });

                    series.set("fill", am5.color(color));

                    series.data.setAll(data);

                    // Make stuff animate on load
                    // https://www.amcharts.com/docs/v5/concepts/animations/
                    series.appear();

                    series.bullets.push(function () {
                        return am5.Bullet.new(root, {
                            locationY: 0,
                            sprite: am5.Label.new(root, {
                                text: "{valueY}",
                                fill: root.interfaceColors.get("alternativeText"),
                                centerY: 0,
                                centerX: am5.p50,
                                populateText: true
                            })
                        });
                    });

                    legend.data.push(series);
                }

                makeSeries("Ep", "ep", data, 0xff671f);
                // makeSeries("Asia", "asia");
                // makeSeries("Latin America", "lamerica");
                // makeSeries("Middle East", "meast");
                // makeSeries("Africa", "africa");


                // Make stuff animate on load
                // https://www.amcharts.com/docs/v5/concepts/animations/
                chart.appear(1000, 100);
            } else {

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

                // Add cursor
                // https://www.amcharts.com/docs/v5/charts/xy-chart/cursor/
                var cursor = chart.set("cursor", am5xy.XYCursor.new(root, {
                    behavior: "none"
                }));
                cursor.lineY.set("visible", false);

                // The data


                // Create axes
                // https://www.amcharts.com/docs/v5/charts/xy-chart/axes/
                var xRenderer = am5xy.AxisRendererX.new(root, {});
                xRenderer.grid.template.set("location", 0.5);
                xRenderer.labels.template.setAll({
                    location: 0.5,
                    multiLocation: 0.5
                });

                xRenderer.labels.template.setAll({
                    rotation: -70,
                    paddingTop: -20,
                    paddingRight: 10,
                    fontSize: 10
                });

                var xAxis = chart.xAxes.push(
                    am5xy.CategoryAxis.new(root, {
                        categoryField: "sendDate",
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

                var yAxis2 = chart.yAxes.push(
                    am5xy.ValueAxis.new(root, {
                        maxPrecision: 0,
                        syncWithAxis: yAxis,
                        renderer: am5xy.AxisRendererY.new(root, {
                            opposite: true
                        })
                    })
                );

                if (unitChart == "db") {
                    yAxis.children.moveValue(am5.Label.new(root, { text: `RATIO [[dB]]`, rotation: -90, y: am5.p50, centerX: am5.p50 }), 0);
                    yAxis2.children.moveValue(am5.Label.new(root, { text: `EPPC [[peak/cycle]]`, rotation: -90, y: am5.p50, centerX: am5.p50 }), 1);
                }

                // Add series
                // https://www.amcharts.com/docs/v5/charts/xy-chart/series/

                function createSeries(name, field) {
                    let unit = unitChart
                    if (name == "Qa" || name == "Qb" || name == "Qc") {
                        unit = "kVar"
                    }
                    var series = chart.series.push(
                        am5xy.LineSeries.new(root, {
                            name: name,
                            xAxis: xAxis,
                            yAxis: yAxis,
                            valueYField: field,
                            categoryXField: "sendDate",
                            tooltip: am5.Tooltip.new(root, {
                                pointerOrientation: "horizontal",
                                labelText: "[bold]{name}[/]\n{categoryX}: [bold]{valueY} " + unit
                            })
                        })
                    );

                    // var series2 = chart.series.push(
                    //     am5xy.LineSeries.new(root, {
                    //         name: name,
                    //         xAxis: xAxis,
                    //         yAxis: yAxis2,
                    //         valueYField: field,
                    //         categoryXField: "sendDate",
                    //         tooltip: am5.Tooltip.new(root, {
                    //             pointerOrientation: "horizontal",
                    //             labelText: "[bold]{name}[/]\n{categoryX}: [bold]{valueY} " + unit
                    //         })
                    //     })
                    // );

                    // create hover state for series and for mainContainer, so that when series is hovered,
                    // the state would be passed down to the strokes whicH are in mainContainer.
                    series.set("setStateOnChildren", true);
                    series.states.create("hover", {});

                    series.mainContainer.set("setStateOnChildren", true);
                    series.mainContainer.states.create("hover", {});

                    series.strokes.template.states.create("hover", {
                        strokeWidth: 4
                    });

                    series.data.setAll(data);
                    series.appear(1000);

                    // series2.set("setStateOnChildren", true);
                    // series2.states.create("hover", {});

                    // series2.mainContainer.set("setStateOnChildren", true);
                    // series2.mainContainer.states.create("hover", {});

                    // series2.strokes.template.states.create("hover", {
                    //     strokeWidth: 4
                    // });

                    // series2.data.setAll(data);
                    // series2.appear(1000);
                }

                function createSeries2(name, field) {
                    // let unit = unitChart
                    // if (name == "Qa" || name == "Qb" || name == "Qc") {
                    //     unit = "kVar"
                    // }

                    var series2 = chart.series.push(
                        am5xy.LineSeries.new(root, {
                            name: name,
                            xAxis: xAxis,
                            yAxis: yAxis2,
                            valueYField: field,
                            categoryXField: "sendDate",
                            tooltip: am5.Tooltip.new(root, {
                                pointerOrientation: "horizontal",
                                labelText: "[bold]{name}[/]\n{categoryX}: [bold]{valueY} " + "peak/cycle"
                            })
                        })
                    );

                    series2.set("setStateOnChildren", true);
                    series2.states.create("hover", {});

                    series2.mainContainer.set("setStateOnChildren", true);
                    series2.mainContainer.states.create("hover", {});

                    series2.strokes.template.states.create("hover", {
                        strokeWidth: 4
                    });

                    series2.data.setAll(data);
                    series2.appear(1000);
                }

                for (let i = 0; i < header.length; i++) {
                    if (systemTypeId == 2) {
                        if (deviceTypeId == 1) {
                            if (header[i] == "Epac") {
                                header[i] = "ep"
                            }
                        }
                        if (deviceTypeId == 3) {
                            if (header[i] == "Udc") {
                                header[i] = "vdcCombiner"
                            }
                            if (header[i] == "Pdc") {
                                header[i] = "pdcCombiner"
                            }
                            if (header[i] == "Idc") {
                                header[i] = "idcCombiner"
                            }
                        }
                        if (deviceTypeId == 4) {
                            if (header[i] == "Udc") {
                                header[i] = "vdcStr"
                            }
                            if (header[i] == "Pdc") {
                                header[i] = "pdcStr"
                            }
                            if (header[i] == "Idc") {
                                header[i] = "idcStr"
                            }
                        }
                    }
                    if (systemTypeId == 5) {
                        if (deviceTypeId == 1) {
                            if (header[i] == "SAWID 1") {
                                header[i] = "sawId1"
                            }
                            if (header[i] == "SAWID 2") {
                                header[i] = "sawId2"
                            }
                            if (header[i] == "SAWID 3") {
                                header[i] = "sawId3"
                            }
                            if (header[i] == "SAWID 4") {
                                header[i] = "sawId4"
                            }
                            if (header[i] == "SAWID 5") {
                                header[i] = "sawId5"
                            }
                            if (header[i] == "SAWID 6") {
                                header[i] = "sawId6"
                            }
                        }
                    }

                    let checked = document.getElementById(convertName(header[i])).checked

                    if (valueRadio == 12) {
                        if (checked) {
                            if (header[i].includes("RATIO")) {
                                createSeries(header[i], convertName(header[i]))
                            }
                            if (header[i].includes("EPPC")) {
                                createSeries2(header[i], convertName(header[i]))
                            }
                        }
                    } else {
                        if (checked) {
                            createSeries(header[i], convertName(header[i]))
                        }
                    }
                }

                // Add scrollb ar
                // https://www.amcharts.com/docs/v5/charts/xy-chart/scrollbars/
                chart.set("scrollbarX", am5.Scrollbar.new(root, {
                    orientation: "horizontal",
                    marginBottom: 20
                }));

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
            }
        });
    }

    const convertName = (name) => {
        if (valueRadioA == 1) {
            if (name === "THD_Ia") {
                name = "thdIa";
            } else {
                name = name.replace("IA_H", "iaH");

            }
        }
        if (valueRadioA == 2) {
            if (name === "THD_Ib") {
                name = "thdIb";
            } else {
                name = name.replace("IB_H", "ibH");

            }
        }
        if (valueRadioA == 3) {
            if (name === "THD_Ic") {
                name = "thdIc";
            } else {
                name = name.replace("IC_H", "icH");
            }
        }

        // if (valueRadioVol == 1) {
        //     if (name === "THD_Uab") {
        //         name = "thdVab";
        //     } else {
        //         name = name.replace("UAB_H", "vabH");
        //     }
        // }
        // if (valueRadioVol == 2) {
        //     if (name === "THD_Ubc") {
        //         name = "thdVbc";
        //     } else {
        //         name = name.replace("UBC_H", "vbcH");

        //     }
        // }
        // if (valueRadioVol == 3) {
        //     if (name === "THD_Uca") {
        //         name = "thdVca";
        //     } else {
        //         name = name.replace("UCA_H", "vcaH");

        //     }
        // }
        if (valueRadioVol == 1) {
            if (name === "THD_Uan") {
                name = "thdVan";
            } else {
                name = name.replace("UAN_H", "vanH");

            }
        }
        if (valueRadioVol == 2) {
            if (name === "THD_Ubn") {
                name = "thdVbn";
            } else {
                name = name.replace("UBN_H", "vbnH");

            }
        }
        if (valueRadioVol == 3) {
            if (name === "THD_Ucn") {
                name = "thdVcn";
            } else {
                name = name.replace("UCN_H", "vcnH");
            }
        }
        if (valueRadio == 6) {
            if (name === "Tần số") {
                name = "f"
            }
            if (name === "Nhiệt độ cảm biến") {
                name = "t"
            }
        }
        if (valueRadio == 11) {
            name = name.replace("Độ ẩm", "h");
        }
        if (valueRadio == 12) {
            if (name === "RATIO") {
                name = "ratio"
            }
            if (name === "EPPC") {
                name = "eppc"
            }
            if (name === "LFB RATIO") {
                name = "lfbRatio"
            }
            if (name === "LFB EPPC") {
                name = "lfbEppc"
            }
            if (name === "MFB RATIO") {
                name = "mfbRatio"
            }
            if (name === "MFB EPPC") {
                name = "mfbEppc"
            }
            if (name === "HFB RATIO") {
                name = "hfbRatio"
            }
            if (name === "HFB EPPC") {
                name = "hfbEppc"
            }
            if (name === "MEAN RATIO") {
                name = "meanRatio"
            }
            if (name === "MEAN EPPC") {
                name = "meanEppc"
            }
            if (name === "RATIO EPPC HI") {
                name = "ratioEppcHi"
            }
            if (name === "RATIO EPPC LO") {
                name = "ratioEppcLo"
            }
        }
        if (valueRadio == 13) {
            name = name.replace("INDICATOR", "indicator");
        }
        if (valueRadioA == 1 && valueRadioVol == 1) {
            name = name.charAt(0).toLowerCase() + name.slice(1);
        }
        if (valueRadio == 14) {
            name = "status"
        }

        if (valueRadio == 15) {
            name = name.toUpperCase();
            if (name === "LƯU LƯỢNG") {
                name = "t"
            }

        }
        if (valueRadio == 16) {
            name = name.toUpperCase();
            if (name === "LƯU LƯỢNG TÍCH LŨY") {
                name = "taccumulationDay"
            }
        }

        if (valueRadio == 17) {
            name = name.toUpperCase();
            if (name === "ÁP SUẤT") {
                name = "p"
            }
        }

        return name;
    }

    function getFormatedStringFromDays(numberOfDays) {
        var years = Math.floor(numberOfDays / 365);
        var months = Math.floor(numberOfDays % 365 / 30);
        var days = Math.floor(numberOfDays % 365 % 30);

        var yearsDisplay = years > 0 ? years + (years == 1 ? "y" : "y") : "";
        var monthsDisplay = months > 0 ? months + (months == 1 ? "m" : "m") : "";
        var daysDisplay = days > 0 ? days + (days == 1 ? "d" : "d") : "";
        return yearsDisplay + monthsDisplay + daysDisplay;
    }

    const downloadDataDevice = () => {
        let header = [];
        let tableInstance = document.getElementById("table-instance")

        for (let i = 0; i < listDataInstance.length; i++) {
            listDataInstance[i].sendDate = moment(new Date(listDataInstance[i].sendDate)).format("YYYY-MM-DD HH:mm:ss");
        }

        for (let i = 0, row; row = tableInstance.rows[i]; i = 1) {
            if (i = 1) {
                let column = "";
                for (var j = 0, col; col = row.cells[j]; j++) {
                    if (col.innerText == "Thời Gian") {
                        column = "sendDate"
                    } else if (col.innerText == "Epac") {
                        column = "Ep"
                    } else if (col.innerText == "SAWID 1") {
                        column = "SawId1"
                    } else if (col.innerText == "SAWID 2") {
                        column = "SawId2"
                    } else if (col.innerText == "SAWID 3") {
                        column = "SawId3"
                    } else if (col.innerText == "SAWID 4") {
                        column = "SawId4"
                    } else if (col.innerText == "SAWID 5") {
                        column = "SawId5"
                    } else if (col.innerText == "SAWID 6") {
                        column = "SawId6"
                    } else {
                        column = col.innerText
                    }

                    let object = {
                        label: column,
                        key: convertName(column)
                    }
                    header.push(object);
                }
                break;
            }
        }
        setParamHeader(header)
    }

    const changeOptionTime = (value) => {
        setIsActiveButton(value)
        if (value == 1) {
            setTypeFormat("yy-mm-dd")
            setOptionTime(1)
            if (valueRadio == 5) {
                setOptionTime(1)
            }
        }
        if (value == 2) {
            setTypeFormat("yy-mm")
            setViewCalender("month")
            setOptionTime(1)
        }
        if (value == 3) {
            setTypeFormat("yy")
            setViewCalender("year")
            setOptionTime(1)
        }
        if (value == 4) {
            setTypeFormat("yy-mm-dd")
            setOptionTime(1)
            if (valueRadio == 5) {
                setOptionTime(1)
            }
        }
    }

    const onChangeValueOptionTime = (e) => {
        setOptionTime(e.target.value)
    }




    return (
        <>
            {!accessDenied ?
                <>
                    <div>
                        <div>
                            <button className="button-back float-right" style={{ marginRight: "1%" }}>
                                <img src="/resources/image/icon-back.svg" height="30px" onClick={() => history.goBack()}></img>
                            </button>
                        </div>
                        <div>
                            <div className="div-infor-equipment-header d-inline-block">
                                <div className="name d-inline-block">
                                    <i className="fas fa-tablet-alt mr-1" style={{ color: "#FFF" }}></i>
                                    {iDevice.deviceName}
                                </div>
                                {device.operatingStatus != "offline" ?
                                    // <div className="time d-inline-block"> {dataDeviceGateway?.map((item) => (
                                    //     <div className="time d-inline-block">{moment(item.sentDate).format("YYYY-MM-DD HH:mm:ss")}</div>
                                    // ))}</div>
                                    <div className="time d-inline-block">
                                        {iDevice.deviceType == 9 ? moment(device.sentDate).format("YYYY-MM-DD HH:mm:ss")
                                            : moment(device.sendDate).format("YYYY-MM-DD HH:mm:ss")}
                                    </div>

                                    :

                                    <div className="time-red d-inline-block">
                                        {iDevice.deviceType == 9 ?
                                            <div className="time-red d-inline-block">
                                                {device.sentDate != undefined ? "Mất tín hiệu " + moment(device.sentDate).format("YYYY-MM-DD HH:mm:ss")
                                                    : t('content.no_data')}
                                            </div>
                                            :
                                            <div className="time-red d-inline-block">
                                                {device.sendDate != undefined ? "Mất tín hiệu " + moment(device.sendDate).format("YYYY-MM-DD HH:mm:ss") : t('content.no_data')}
                                            </div>
                                        }
                                    </div>

                                }
                            </div>
                        </div>
                        <div className="div-infor-equipment-menu d-inline-block">
                            <div className={equipmentTab == 1 ? "d-inline-block active" : "d-inline-block list"} onClick={() => funcEquipment(1)}>
                                <i className="fas fa-solid fa-clock-rotate-left" style={{ color: "white" }}></i>
                                {t('content.home_page.device.latest_data')}
                            </div>
                            <div className={equipmentTab == 2 ? "d-inline-block active" : "d-inline-block list"} onClick={() => funcEquipment(2)}>
                                <i className="fas fa-solid fa-sliders" style={{ color: "white" }}></i>
                                {t('content.home_page.device.parameter')}
                            </div>
                            <div className={equipmentTab == 3 ? "d-inline-block active" : "d-inline-block list"} onClick={() => funcEquipment(3)}>
                                <i className="fas fa-sharp fa-regular fa-triangle-exclamation" style={{ color: "white" }}></i>
                                {t('content.home_page.device.warning')}
                            </div>
                        </div>
                        <div className="div-infor-equipment-content">
                            {equipmentTab === 1 &&
                                <div className="realtime">
                                    <div className="zone-1">
                                        {/* <img src={iDevice.img} height="500px" width={320} style={{ objectFit: "contain" }}></img> */}
                                        <img src={iDevice.img != null ? iDevice.img : "/resources/image/icon-khac.svg"} height="500px" width={320} style={{ objectFit: "contain" }}></img>
                                        <div style={{ padding: "0px 20px" }}>
                                            <div style={{ display: "grid", gridTemplateColumns: "60% 40%" }}>
                                                {iDevice.deviceType == 9 ? <div className="mccb-1">{t('content.home_page.device.gateway_status')}</div> : <div className="mccb-1">{t('content.home_page.device.status_cutting')}</div>}
                                                <div className={statusMCCB == true ? "mccb-2-active" : "mccb-2"}>{statusMCCB == true ? t('content.close') : t('content.open')}</div>
                                            </div>
                                        </div>
                                        <div style={{ padding: "0px 20px" }}>
                                            <div style={{ display: "grid", gridTemplateColumns: "60% 40%" }}>
                                                <div className="mccb-1" style={{ border: "solid 2px #0a1a5c" }}>{t('content.start_time')}</div>
                                                <div className="mccb-2" style={{ border: "solid 2px #0a1a5c", backgroundColor: "#FFF", color: "#0a1a5c" }}>
                                                    {iDevice.dayOnline != null && iDevice.dayOnline > 0 ? getFormatedStringFromDays(iDevice.dayOnline) : "-"}
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="zone-2">
                                        <div className="detail" style={{ backgroundColor: "#dddddd" }}>
                                            <>
                                                {iDevice.deviceType == 1 &&
                                                    <>
                                                        <div className="content-4">
                                                            <div className="content-1">
                                                                <div className="box">
                                                                    <table className="table-param" style={{ border: "2px solid", margin: "auto" }} ref={tableRef}>
                                                                        <tbody style={{ lineHeight: 0.5 }}>
                                                                            <tr>
                                                                                <th style={{ width: "60%" }} className="text-uppercase">{t('content.home_page.device.voltage')}</th>
                                                                                <th className="text-param">{t('content.home_page.device.value')}</th>
                                                                            </tr>
                                                                            <tr>
                                                                                <td className="text-param">Uab</td>
                                                                                <td className="text-param">
                                                                                    <div className="unit">{device.uab != null ? device.uab : "-"} V</div>
                                                                                </td>
                                                                            </tr>
                                                                            <tr>
                                                                                <td className="text-param">Ubc</td>
                                                                                <td className="text-param">
                                                                                    <div className="unit">{device.ubc != null ? device.ubc : "-"} V</div>
                                                                                </td>
                                                                            </tr>
                                                                            <tr>
                                                                                <td className="text-param">Uca</td>
                                                                                <td className="text-param">
                                                                                    <div className="unit">{device.uca != null ? device.uca : "-"} V</div>
                                                                                </td>
                                                                            </tr>
                                                                            <tr>
                                                                                <td className="text-param">Uan</td>
                                                                                <td className="text-param">
                                                                                    <div className="unit">{device.uan != null ? device.uan : "-"} V</div>
                                                                                </td>
                                                                            </tr>
                                                                            <tr>
                                                                                <td className="text-param">Ubn</td>
                                                                                <td className="text-param">
                                                                                    <div className="unit">{device.ubn != null ? device.ubn : "-"} V</div>
                                                                                </td>
                                                                            </tr>
                                                                            <tr>
                                                                                <td className="text-param">Ucn</td>
                                                                                <td className="text-param">
                                                                                    <div className="unit">{device.ucn != null ? device.ucn : "-"} V</div>
                                                                                </td>
                                                                            </tr>
                                                                        </tbody>
                                                                    </table>
                                                                </div>

                                                            </div>
                                                            <div className="content-2">
                                                                <div className="box">
                                                                    <table className="table-param" style={{ border: "2px solid", margin: "auto" }} ref={tableRef}>
                                                                        <tbody style={{ lineHeight: 0.5 }}>
                                                                            <tr>
                                                                                <th style={{ width: "60%" }} className="text-uppercase">{t('content.home_page.device.power')}</th>
                                                                                <th className="text-param">{t('content.home_page.device.value')}</th>
                                                                            </tr>
                                                                            <tr>
                                                                                <td className="text-param">P total</td>
                                                                                <td className="text-param">
                                                                                    <div className="unit">{device.ptotal != null ? device.ptotal : "-"} kW</div>
                                                                                </td>
                                                                            </tr>
                                                                            <tr>
                                                                                <td className="text-param">Q total</td>
                                                                                <td className="text-param">
                                                                                    <div className="unit">{device.qtotal != null ? device.qtotal : "-"} kVar</div>
                                                                                </td>
                                                                            </tr>
                                                                            <tr>
                                                                                <td className="text-param">S total</td>
                                                                                <td className="text-param">
                                                                                    <div className="unit">{device.stotal != null ? device.stotal : "-"} kVa</div>
                                                                                </td>
                                                                            </tr>
                                                                            <tr>
                                                                                <td className="text-param">{t('content.home_page.chart.accumulated_energy')}</td>
                                                                                <td className="text-param">
                                                                                    <div className="unit">{device.ep != null ? device.ep : "-"} kWh</div>
                                                                                </td>
                                                                            </tr>
                                                                            <tr>
                                                                                <td className="text-param">{t('content.home_page.chart.daily_energy')}</td>
                                                                                <td className="text-param">
                                                                                    <div className="unit">{device.epDay != null ? device.epDay : "-"} kWh</div>
                                                                                </td>
                                                                            </tr>
                                                                            <tr>
                                                                                <td className="text-param">{t('content.home_page.chart.monthly_energy')}</td>
                                                                                <td className="text-param">
                                                                                    <div className="unit">{device.epMonth != null ? device.epMonth : "-"} kWh</div>
                                                                                </td>
                                                                            </tr>
                                                                        </tbody>
                                                                    </table>
                                                                </div>
                                                            </div>
                                                        </div>
                                                        <div className="content-5">
                                                            <div className="content-1">
                                                                <div className="box">
                                                                    <table className="table-param-small" style={{ border: "2px solid", margin: "auto" }} ref={tableRef}>
                                                                        <tbody>
                                                                            <tr>
                                                                                <th style={{ width: "60%" }} className="text-uppercase">{t('content.home_page.device.current')}</th>
                                                                                <th className="text-param">{t('content.home_page.device.value')}</th>
                                                                            </tr>
                                                                            <tr>
                                                                                <td className="text-param">Ia</td>
                                                                                <td className="text-param">
                                                                                    <div className="unit">{device.ia != null ? device.ia : "-"} A</div>
                                                                                </td>
                                                                            </tr>
                                                                            <tr>
                                                                                <td className="text-param">Ib</td>
                                                                                <td className="text-param">
                                                                                    <div className="unit">{device.ib != null ? device.ib : "-"} A</div>
                                                                                </td>
                                                                            </tr>
                                                                            <tr>
                                                                                <td className="text-param">Ic</td>
                                                                                <td className="text-param">
                                                                                    <div className="unit">{device.ic != null ? device.ic : "-"} A</div>
                                                                                </td>
                                                                            </tr>
                                                                        </tbody>
                                                                    </table>
                                                                </div>
                                                                <div className="box">
                                                                    <table className="table-param-small" style={{ border: "2px solid", margin: "auto" }} ref={tableRef}>
                                                                        <tbody>
                                                                            <tr>
                                                                                <th style={{ width: "60%" }} className="text-uppercase">{t('content.home_page.device.power_factor')}</th>
                                                                                <th className="text-param">{t('content.home_page.device.value')}</th>
                                                                            </tr>
                                                                            <tr>
                                                                                <td className="text-param">PFa</td>
                                                                                <td className="text-param">
                                                                                    <div className="unit">{device.pfa != null ? device.pfa : "-"}</div>
                                                                                </td>
                                                                            </tr>
                                                                            <tr>
                                                                                <td className="text-param">PFb</td>
                                                                                <td className="text-param">
                                                                                    <div className="unit">{device.pfb != null ? device.pfb : "-"}</div>
                                                                                </td>
                                                                            </tr>
                                                                            <tr>
                                                                                <td className="text-param">PFc</td>
                                                                                <td className="text-param">
                                                                                    <div className="unit">{device.pfc != null ? device.pfc : "-"}</div>
                                                                                </td>
                                                                            </tr>
                                                                        </tbody>
                                                                    </table>
                                                                </div>
                                                            </div>
                                                            <div className="content-2">
                                                                <div className="box">
                                                                    <table className="table-param-small" style={{ border: "2px solid", margin: "auto" }} ref={tableRef}>
                                                                        <tbody>
                                                                            <tr>
                                                                                <th style={{ width: "60%" }} className="text-uppercase">{t('content.home_page.device.current_harmonics')}</th>
                                                                                <th className="text-param">{t('content.home_page.device.value')}</th>
                                                                            </tr>
                                                                            <tr>
                                                                                <td className="text-param">THD_Ia</td>
                                                                                <td className="text-param">
                                                                                    <div className="unit">{device.thdIa != null ? device.thdIa : "-"} %</div>
                                                                                </td>
                                                                            </tr>
                                                                            <tr>
                                                                                <td className="text-param">THD_Ib</td>
                                                                                <td className="text-param">
                                                                                    <div className="unit">{device.thdIb != null ? device.thdIb : "-"} %</div>
                                                                                </td>
                                                                            </tr>
                                                                            <tr>
                                                                                <td className="text-param">THD_Ic</td>
                                                                                <td className="text-param">
                                                                                    <div className="unit">{device.thdIc != null ? device.thdIc : "-"} %</div>
                                                                                </td>
                                                                            </tr>
                                                                        </tbody>
                                                                    </table>
                                                                </div>
                                                                <div className="box">
                                                                    <table className="table-param-small" style={{ border: "2px solid", margin: "auto" }} ref={tableRef}>
                                                                        <tbody>
                                                                            <tr>
                                                                                <th style={{ width: "60%" }} className="text-uppercase">{t('content.home_page.device.voltage_harmonics')}</th>
                                                                                <th className="text-param">{t('content.home_page.device.value')}</th>
                                                                            </tr>
                                                                            <tr>
                                                                                <td className="text-param">THD_Uan</td>
                                                                                <td className="text-param">
                                                                                    <div className="unit">{device.thdVan != null ? device.thdVan : "-"} %</div>
                                                                                </td>
                                                                            </tr>
                                                                            <tr>
                                                                                <td className="text-param">THD_Ubn</td>
                                                                                <td className="text-param">
                                                                                    <div className="unit">{device.thdVbn != null ? device.thdVbn : "-"} %</div>
                                                                                </td>
                                                                            </tr>
                                                                            <tr>
                                                                                <td className="text-param">THD_Ucn</td>
                                                                                <td className="text-param">
                                                                                    <div className="unit">{device.thdVcn != null ? device.thdVcn : "-"} %</div>
                                                                                </td>
                                                                            </tr>
                                                                        </tbody>
                                                                    </table>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </>
                                                }
                                                {iDevice.deviceType == 3 &&
                                                    <>
                                                        <div className="">
                                                            <div className="content-1">
                                                                <div className="box">
                                                                    <table className="table-param" style={{ border: "2px solid", margin: "auto" }}>
                                                                        <tbody style={{ lineHeight: 0.5 }}>
                                                                            <tr>
                                                                                <th style={{ width: "50%" }}>{t('content.home_page.device.parameter')}</th>
                                                                                <th className="text-param">{t('content.home_page.device.value')}</th>
                                                                            </tr>
                                                                            <tr>
                                                                                <td style={{ height: "220px" }} className="text-param">{t('content.home_page.warning_tab.temperature')}</td>
                                                                                <td className="text-param">
                                                                                    <div style={{ textAlign: "center" }} className="unit">{device.tsensor != null ? device.tsensor : "-"} °C</div>
                                                                                </td>
                                                                            </tr>
                                                                            <tr>
                                                                                <td style={{ height: "220px" }} className="text-param">{t('content.home_page.warning_tab.humidity')}</td>
                                                                                <td className="text-param">
                                                                                    <div style={{ textAlign: "center" }} className="unit">{device.h != null ? device.h : "-"} %</div>
                                                                                </td>
                                                                            </tr>
                                                                        </tbody>
                                                                    </table>
                                                                </div>

                                                            </div>
                                                        </div>
                                                    </>
                                                }
                                                {iDevice.deviceType == 4 &&
                                                    <>
                                                        <div className="content-4">
                                                            <div className="content-1">
                                                                <div className="box">
                                                                    <table className="table-param" style={{ border: "2px solid", margin: "auto" }}>
                                                                        <tbody style={{ lineHeight: 0.5 }}>
                                                                            <tr>
                                                                                <th style={{ width: "60%" }} className="text-uppercase">{t('content.home_page.device.parameter')}</th>
                                                                                <th className="text-param">{t('content.home_page.device.value')}</th>
                                                                            </tr>
                                                                            <tr>
                                                                                <td className="text-param">Trạng thái</td>
                                                                                <td className="text-param">
                                                                                    <div className="unit">{device.status == 1 ? "ON" : "OFF"}</div>
                                                                                </td>
                                                                            </tr>
                                                                        </tbody>
                                                                    </table>
                                                                </div>

                                                            </div>
                                                        </div></>
                                                }
                                                {iDevice.deviceType == 9 &&
                                                    <Gateway />
                                                }
                                                {iDevice.deviceType == 5 &&
                                                    <Htr02 />
                                                }
                                                {iDevice.deviceType == 6 &&
                                                    <Ams01 />
                                                }
                                                {iDevice.deviceType == 10 &&
                                                    <Flow />
                                                }
                                                {iDevice.deviceType == 7 &&
                                                    <Pressure />
                                                }
                                                {iDevice.systemTypeId == 2 &&
                                                    <>
                                                        {iDevice.deviceType == 2 &&
                                                            <>
                                                                {device.operatingStatus != null &&
                                                                    <>
                                                                        <div className="content-4">
                                                                            <div className="content-1">
                                                                                <div className="box">
                                                                                    <table className="table-param" style={{ border: "2px solid", margin: "auto" }} ref={tableRef}>
                                                                                        <tbody style={{ lineHeight: 0.5 }}>
                                                                                            <tr>
                                                                                                <th style={{ width: "60%" }}>ĐIỆN ÁP</th>
                                                                                                <th className="text-param">Giá trị</th>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">Uab</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.uab != null ? device.uab : "-"} V</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">Ubc</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.ubc != null ? device.ubc : "-"} V</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">Uca</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.uca != null ? device.uca : "-"} V</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">Uan</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.uan != null ? device.uan : "-"} V</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">Ubn</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.ubn != null ? device.ubn : "-"} V</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">Ucn</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.ucn != null ? device.ucn : "-"} V</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                        </tbody>
                                                                                    </table>
                                                                                </div>

                                                                            </div>
                                                                            <div className="content-2">
                                                                                <div className="box">
                                                                                    <table className="table-param" style={{ border: "2px solid", margin: "auto" }} ref={tableRef}>
                                                                                        <tbody style={{ lineHeight: 0.5 }}>
                                                                                            <tr>
                                                                                                <th>CÔNG SUẤT</th>
                                                                                                <th className="text-param">GIÁ TRỊ</th>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">P total</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.ptotal != null ? device.ptotal : "-"} kW</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">Q total</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.qtotal != null ? device.qtotal : "-"} kVar</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">S total</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.stotal != null ? device.stotal : "-"} kVa</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">Điện năng lũy kế</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.ep != null ? device.ep : "-"}</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">Điện năng trong ngày</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">kWh</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">Điện năng trong tháng</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">kWh</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                        </tbody>
                                                                                    </table>
                                                                                </div>
                                                                            </div>
                                                                        </div>
                                                                        <div className="content-5">
                                                                            <div className="content-1">
                                                                                <div className="box">
                                                                                    <table className="table-param-small" style={{ border: "2px solid", margin: "auto" }} ref={tableRef}>
                                                                                        <tbody>
                                                                                            <tr>
                                                                                                <th style={{ width: "60%" }}>DÒNG ĐIỆN</th>
                                                                                                <th className="text-param">GIÁ TRỊ</th>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">Ia</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.ia != null ? device.ia : "-"} A</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">Ib</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.ib != null ? device.ib : "-"} A</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">Ic</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.ic != null ? device.ic : "-"} A</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                        </tbody>
                                                                                    </table>
                                                                                </div>
                                                                                <div className="box">
                                                                                    <table className="table-param-small" style={{ border: "2px solid", margin: "auto" }} ref={tableRef}>
                                                                                        <tbody>
                                                                                            <tr>
                                                                                                <th style={{ width: "60%" }}>HỆ SỐ CÔNG SUẤT</th>
                                                                                                <th className="text-param">GIÁ TRỊ</th>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">PFa</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.pfa != null ? device.pfa : "-"}</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">PFb</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.pfb != null ? device.pfb : "-"}</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">PFc</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.pfc != null ? device.pfc : "-"}</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                        </tbody>
                                                                                    </table>
                                                                                </div>
                                                                                <div className="box">
                                                                                    <table className="table-param-small" style={{ border: "2px solid", margin: "auto" }} ref={tableRef}>
                                                                                        <tbody>
                                                                                            <tr>
                                                                                                <th style={{ width: "60%" }}>PHÍA DC</th>
                                                                                                <th className="text-param">GIÁ TRỊ</th>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">Vdc</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.udc != null ? device.udc : "-"} V</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">Idc</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.idc != null ? device.idc : "-"} A</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">Pdc</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.pdc != null ? device.pdc : "-"} kW</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">Epdc</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.epdc != null ? device.epdc : "-"} kWh</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                        </tbody>
                                                                                    </table>
                                                                                </div>
                                                                            </div>
                                                                            <div className="content-2">
                                                                                <div className="box">
                                                                                    <table className="table-param-small" style={{ border: "2px solid", margin: "auto" }} ref={tableRef}>
                                                                                        <tbody>
                                                                                            <tr>
                                                                                                <th style={{ width: "60%" }}>SÓNG HÀI DÒNG ĐIỆN</th>
                                                                                                <th className="text-param">GIÁ TRỊ</th>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">THD_Ia</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.thdIa != null ? device.thdIa : "-"} %</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">THD_Ib</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.thdIb != null ? device.thdIb : "-"} %</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">THD_Ic</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.thdIc != null ? device.thdIc : "-"} %</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                        </tbody>
                                                                                    </table>
                                                                                </div>
                                                                                <div className="box">
                                                                                    <table className="table-param-small" style={{ border: "2px solid", margin: "auto" }} ref={tableRef}>
                                                                                        <tbody>
                                                                                            <tr>
                                                                                                <th style={{ width: "60%" }}>SÓNG HÀI ĐIỆN ÁP</th>
                                                                                                <th className="text-param">GIÁ TRỊ</th>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">THD_Uan</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.thdVan != null ? device.thdVan : "-"} %</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">THD_Ubn</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.thdVbn != null ? device.thdVbn : "-"} %</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">THD_Ucn</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.thdVcn != null ? device.thdVcn : "-"} %</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                        </tbody>
                                                                                    </table>
                                                                                </div>
                                                                                <div className="box">
                                                                                    <table className="table-param-small" style={{ border: "2px solid", margin: "auto" }} ref={tableRef}>
                                                                                        <tbody>
                                                                                            <tr>
                                                                                                <th style={{ width: "60%" }}>NHIỆT ĐỘ</th>
                                                                                                <th className="text-param">GIÁ TRỊ</th>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">TmpCab</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.tmpCab != null ? device.tmpCab : "-"} °C</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">TmpSnk</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.tmpSnk != null ? device.tmpSnk : "-"} °C</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">TmpTrns</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.tmpTrns != null ? device.tmpTrns : "-"} °C</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">TmpOt</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.tmpOt != null ? device.tmpOt : "-"} °C</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                        </tbody>
                                                                                    </table>
                                                                                </div>
                                                                            </div>
                                                                        </div>
                                                                    </>
                                                                }
                                                            </>
                                                        }
                                                        {iDevice.deviceType == 3 &&
                                                            <>
                                                                {device.operatingStatus != null &&
                                                                    <>
                                                                        <div className="content-5">
                                                                            <div className="content-1">
                                                                                <div className="box">
                                                                                    <table className="table-param-small" style={{ border: "2px solid", margin: "auto" }} ref={tableRef}>
                                                                                        <tbody>
                                                                                            <tr>
                                                                                                <th style={{ width: "60%" }}>ĐIỆN ÁP</th>
                                                                                                <th className="text-param">GIÁ TRỊ</th>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">Vdc</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.vdcCombiner != null ? device.vdcCombiner : "-"} V</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                        </tbody>
                                                                                    </table>
                                                                                </div>
                                                                                <div className="box">
                                                                                    <table className="table-param-small" style={{ border: "2px solid", margin: "auto" }} ref={tableRef}>
                                                                                        <tbody>
                                                                                            <tr>
                                                                                                <th style={{ width: "60%" }}>DÒNG ĐIỆN</th>
                                                                                                <th className="text-param">GIÁ TRỊ</th>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">Idc</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.idcCombiner != null ? device.idcCombiner : "-"}</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                        </tbody>
                                                                                    </table>
                                                                                </div>
                                                                                <div className="box">
                                                                                    <table className="table-param-small" style={{ border: "2px solid", margin: "auto" }} ref={tableRef}>
                                                                                        <tbody>
                                                                                            <tr>
                                                                                                <th style={{ width: "60%" }}>HIỆU SUẤT</th>
                                                                                                <th className="text-param">GIÁ TRỊ</th>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">Eff</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.f != null ? device.f : "-"}</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                        </tbody>
                                                                                    </table>
                                                                                </div>
                                                                            </div>
                                                                            <div className="content-2">
                                                                                <div className="box">
                                                                                    <table className="table-param-small" style={{ border: "2px solid", margin: "auto" }} ref={tableRef}>
                                                                                        <tbody>
                                                                                            <tr>
                                                                                                <th style={{ width: "60%" }}>CÔNG SUẤT</th>
                                                                                                <th className="text-param">GIÁ TRỊ</th>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">Pdc</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.pdcCombiner != null ? device.pdcCombiner : "-"} °C</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                        </tbody>
                                                                                    </table>
                                                                                </div>
                                                                                <div className="box">
                                                                                    <table className="table-param-small" style={{ border: "2px solid", margin: "auto" }} ref={tableRef}>
                                                                                        <tbody>
                                                                                            <tr>
                                                                                                <th style={{ width: "60%" }}>NHIỆT ĐỘ</th>
                                                                                                <th className="text-param">GIÁ TRỊ</th>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">Temp</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.t != null ? device.t : "-"}</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                        </tbody>
                                                                                    </table>
                                                                                </div>
                                                                            </div>
                                                                        </div>
                                                                    </>
                                                                }
                                                            </>
                                                        }
                                                        {iDevice.deviceType == 4 &&
                                                            <>
                                                                {device.operatingStatus != null &&
                                                                    <>
                                                                        <div className="content-5">
                                                                            <div className="content-1">
                                                                                <div className="box">
                                                                                    <table className="table-param-small" style={{ border: "2px solid", margin: "auto" }} ref={tableRef}>
                                                                                        <tbody>
                                                                                            <tr>
                                                                                                <th style={{ width: "60%" }}>ĐIỆN ÁP</th>
                                                                                                <th className="text-param">GIÁ TRỊ</th>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">Vdc</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.vdcStr != null ? device.vdcStr : "-"} V</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                        </tbody>
                                                                                    </table>
                                                                                </div>
                                                                                <div className="box">
                                                                                    <table className="table-param-small" style={{ border: "2px solid", margin: "auto" }} ref={tableRef}>
                                                                                        <tbody>
                                                                                            <tr>
                                                                                                <th style={{ width: "60%" }}>DÒNG ĐIỆN</th>
                                                                                                <th className="text-param">GIÁ TRỊ</th>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">Idc</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.idcStr != null ? device.idcStr : "-"}</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                        </tbody>
                                                                                    </table>
                                                                                </div>
                                                                                <div className="box">
                                                                                    <table className="table-param-small" style={{ border: "2px solid", margin: "auto" }} ref={tableRef}>
                                                                                        <tbody>
                                                                                            <tr>
                                                                                                <th style={{ width: "60%" }}>HIỆU SUẤT</th>
                                                                                                <th className="text-param">GIÁ TRỊ</th>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">Eff</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.f != null ? device.f : "-"}</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                        </tbody>
                                                                                    </table>
                                                                                </div>
                                                                            </div>
                                                                            <div className="content-2">
                                                                                <div className="box">
                                                                                    <table className="table-param-small" style={{ border: "2px solid", margin: "auto" }} ref={tableRef}>
                                                                                        <tbody>
                                                                                            <tr>
                                                                                                <th style={{ width: "60%" }}>CÔNG SUẤT</th>
                                                                                                <th className="text-param">GIÁ TRỊ</th>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">Pdc</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.pdcStr != null ? device.pdcStr : "-"} °C</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                        </tbody>
                                                                                    </table>
                                                                                </div>
                                                                                <div className="box">
                                                                                    <table className="table-param-small" style={{ border: "2px solid", margin: "auto" }} ref={tableRef}>
                                                                                        <tbody>
                                                                                            <tr>
                                                                                                <th style={{ width: "60%" }}>NHIỆT ĐỘ</th>
                                                                                                <th className="text-param">GIÁ TRỊ</th>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td className="text-param">Temp</td>
                                                                                                <td className="text-param">
                                                                                                    <div className="unit">{device.t != null ? device.t : "-"}</div>
                                                                                                </td>
                                                                                            </tr>
                                                                                        </tbody>
                                                                                    </table>
                                                                                </div>
                                                                            </div>
                                                                        </div>
                                                                    </>
                                                                }
                                                            </>
                                                        }
                                                    </>
                                                }
                                            </>
                                        </div>
                                        <div className="describe">
                                            <div className="title text_uppercase">
                                                {t('content.description')}
                                            </div>
                                            <div className="content">
                                                <p>{iDevice.deviceType == 9 ? device.description : iDevice.description}</p>

                                            </div>
                                        </div>
                                    </div>
                                    <div className="zone-3">
                                        <div>
                                            <p className="header-warning" style={{ border: "solid 2px #0a1a5c" }}>{t('content.home_page.device.recent_warning')}</p>
                                        </div>
                                        <>
                                            {iDevice.deviceType == 1 &&
                                                warningLoad?.map((item, index) => (

                                                    <div key={index}>

                                                        <div className={item.active == true ? "list-warning" : "list-not-warning"}>
                                                            {item.active == true ?
                                                                <div className="warning"></div>
                                                                :
                                                                <div className="nwarning"></div>
                                                            }
                                                            <p>{item.warningName}</p>
                                                        </div>

                                                    </div>

                                                ))
                                            }
                                            {iDevice.systemTypeId == 2 &&
                                                warningSolar?.map((item, index) => (

                                                    <div key={index}>

                                                        <div className={item.active == true ? "list-warning" : "list-not-warning"}>
                                                            {item.active == true ?
                                                                <div className="warning"></div>
                                                                :
                                                                <div className="nwarning"></div>
                                                            }
                                                            <p>{item.warningName}</p>
                                                        </div>

                                                    </div>

                                                ))
                                            }
                                            {/* {iDevice.systemTypeId == 5 &&
                                                warningGrid?.map((item, index) => (

                                                    <div key={index}>

                                                        <div className={item.active == true ? "list-warning" : "list-not-warning"}>
                                                            {item.active == true ?
                                                                <div className="warning"></div>
                                                                :
                                                                <div className="nwarning"></div>
                                                            }
                                                            <p>{item.warningName}</p>
                                                        </div>

                                                    </div>
                                                ))
                                            } */}
                                            {iDevice.deviceType == 3 &&
                                                warningTempHum?.map((item, index) => (

                                                    <div key={index}>

                                                        <div className={item.active == true ? "list-warning" : "list-not-warning"}>
                                                            {item.active == true ?
                                                                <div className="warning"></div>
                                                                :
                                                                <div className="nwarning"></div>
                                                            }
                                                            <p>{item.warningName}</p>
                                                        </div>

                                                    </div>

                                                ))
                                            }
                                            {iDevice.deviceType == 9 &&
                                                <>
                                                    <div>
                                                        <p>{t('content.no_data')}</p>
                                                    </div>
                                                </>

                                            }
                                            {iDevice.deviceType == 6 &&
                                                warningGrid?.map((item, index) => (

                                                    <div key={index}>

                                                        <div className={item.active == true ? "list-warning" : "list-not-warning"}>
                                                            {item.active == true ?
                                                                <div className="warning"></div>
                                                                :
                                                                <div className="nwarning"></div>
                                                            }
                                                            <p>{item.warningName}</p>
                                                        </div>

                                                    </div>

                                                ))

                                            }
                                            {iDevice.deviceType == 5 &&
                                                warningGrid?.map((item, index) => (

                                                    <div key={index}>

                                                        <div className={item.active == true ? "list-warning" : "list-not-warning"}>
                                                            {item.active == true ?
                                                                <div className="warning"></div>
                                                                :
                                                                <div className="nwarning"></div>
                                                            }
                                                            <p>{item.warningName}</p>
                                                        </div>

                                                    </div>

                                                ))
                                            }
                                            {iDevice.deviceType == 7 &&
                                                warningPressure?.map((item, index) => (

                                                    <div key={index}>

                                                        <div className={item.active == true ? "list-warning" : "list-not-warning"}>
                                                            {item.active == true ?
                                                                <div className="warning"></div>
                                                                :
                                                                <div className="nwarning"></div>
                                                            }
                                                            <p>{item.warningName}</p>
                                                        </div>

                                                    </div>

                                                ))
                                            }
                                            {iDevice.deviceType == 10 &&
                                                warningFlow?.map((item, index) => (

                                                    <div key={index}>

                                                        <div className={item.active == true ? "list-warning" : "list-not-warning"}>
                                                            {item.active == true ?
                                                                <div className="warning"></div>
                                                                :
                                                                <div className="nwarning"></div>
                                                            }
                                                            <p>{item.warningName}</p>
                                                        </div>

                                                    </div>

                                                ))
                                            }
                                        </>
                                    </div>
                                </div>
                            }
                            {
                                equipmentTab === 2 &&
                                <>
                                    <div id="radios" onChange={(e) => {
                                        handleChange(e);
                                        getDataByRadioValue(e)
                                    }}>
                                        <span hidden={allowedTypeDevice.includes(iDevice.deviceType) ? true : false}>
                                            <div className="radio d-inline-block" style={{ backgroundColor: "#0a1a5c" }}>
                                                <input className="with-gap" id="input1" name="radio" type="radio" defaultChecked={iDevice.deviceType != 3 ? true : false} value={1} />
                                                <label htmlFor="input1">{t('content.home_page.device.voltage')}</label>
                                            </div>
                                            <span className="radio d-inline-block">
                                                <input className="with-gap" id="input2" name="radio" type="radio" value={2} />
                                                <label htmlFor="input2">{t('content.home_page.device.power')}</label>
                                            </span>
                                            <span className="radio d-inline-block">
                                                <input className="with-gap" id="input3" name="radio" type="radio" value={3} />
                                                <label htmlFor="input3">{t('content.home_page.device.current')}</label>
                                            </span>
                                            <span className="radio d-inline-block">
                                                <input className="with-gap" id="input4" name="radio" type="radio" value={4} />
                                                <label htmlFor="input4">COSPHI</label>
                                            </span>
                                            <span className="radio d-inline-block">
                                                <input className="with-gap" id="input5" name="radio" type="radio" value={5} />
                                                <label htmlFor="input5">{t('content.home_page.chart.energy_power')}</label>
                                            </span>
                                            <span className="radio d-inline-block">
                                                <input className="with-gap" id="input6" name="radio" type="radio" value={6} />
                                                <label htmlFor="input6">{t('content.category.device.frequency')}</label>
                                            </span>
                                            <span className="radio d-inline-block">
                                                <input className="with-gap" id="input8" name="radio" type="radio" value={8} />
                                                <label htmlFor="input8">{t('content.home_page.device.current_harmonics')}</label>
                                            </span>
                                            <span className="radio d-inline-block">
                                                <input className="with-gap" id="input9" name="radio" type="radio" value={9} />
                                                <label htmlFor="input9">{t('content.home_page.device.voltage_harmonics')}</label>
                                            </span>
                                            <span className="radio d-inline-block">
                                                <input className="with-gap" id="input7" name="radio" type="radio" value={7} />
                                                <label htmlFor="input7">{t('content.setting')}</label>
                                            </span>
                                            <span id="slider"></span>
                                        </span>


                                        {iDevice.deviceType == 3 ?
                                            <>
                                                <span className="radio d-inline-block" style={{ backgroundColor: valueRadio == 6 ? "#0a1a5c" : "" }}>
                                                    <input className="with-gap" id="input6" name="radio" type="radio" value={6} defaultChecked />
                                                    <label htmlFor="input6">{t('content.temperature')}</label>
                                                </span>
                                                <>
                                                    <span className="radio d-inline-block">
                                                        <input className="with-gap" id="input10" name="radio" type="radio" value={11} />
                                                        <label htmlFor="input10">{t('content.humidity')}</label>
                                                    </span>
                                                </>
                                            </> :
                                            <>
                                                {/* <span className="radio d-inline-block">
                                                    <input className="with-gap" id="input6" name="radio" type="radio" value={6} />
                                                    <label htmlFor="input6">TẦN SỐ</label>
                                                </span> */}
                                            </>
                                        }

                                        {iDevice.deviceType == 5 || iDevice.deviceType == 6 ?
                                            <>
                                                <span className="radio d-inline-block" style={{ backgroundColor: "#0a1a5c" }}>
                                                    <input className="with-gap" id="input12" name="radio" type="radio" value={12} defaultChecked />
                                                    <label htmlFor="input12">RATIO/EPPC</label>
                                                </span>
                                                <>
                                                    <span className="radio d-inline-block">
                                                        <input className="with-gap" id="input13" name="radio" type="radio" value={13} />
                                                        <label htmlFor="input13">INDICATOR</label>
                                                    </span>
                                                </>
                                            </> :
                                            <>

                                            </>
                                        }

                                        {iDevice.deviceType == 9 &&
                                            <>
                                                <span className="radio d-inline-block" style={{ backgroundColor: "#0a1a5c" }}>
                                                    <input className="with-gap" id="input14" name="radio" type="radio" value={14} defaultChecked />
                                                    <label htmlFor="input14">STATUS SERVER</label>
                                                </span>
                                            </>
                                        }

                                        {iDevice.deviceType == 10 &&
                                            <>
                                                <span className="radio d-inline-block" style={{ backgroundColor: "#0a1a5c" }}>
                                                    <input className="with-gap" id="input15" name="radio" type="radio" value={15} defaultChecked />
                                                    <label htmlFor="input15">Lưu lượng</label>
                                                </span>
                                                <>
                                                    <span className="radio d-inline-block">
                                                        <input className="with-gap" id="input16" name="radio" type="radio" value={16} />
                                                        <label htmlFor="input16">Lưu lượng tích lũy</label>
                                                    </span>
                                                </>
                                            </>
                                        }

                                        {iDevice.deviceType == 7 &&
                                            <>
                                                <span className="radio d-inline-block" style={{ backgroundColor: "#0a1a5c" }}>
                                                    <input className="with-gap" id="input17" name="radio" type="radio" value={17} defaultChecked />
                                                    <label htmlFor="input17">Áp suất</label>
                                                </span>
                                            </>
                                        }

                                    </div>
                                    <div className="form-group mt-2 mb-0 ml-2 calendar-equipment">
                                        <div className="input-group float-left" style={{ width: "370px" }}>
                                            <div className="input-group-prepend">
                                                <button className="btn btn-outline-secondary btn-time-left" title="Kiểu xem" type="button"
                                                    style={isActiveButton == 1 ? { color: "#fff", backgroundColor: "#0a1a5c" } : { color: "#6c757d", backgroundColor: "#e9ecef" }}
                                                    onClick={() => changeOptionTime(1)}
                                                >
                                                    {t('content.day')}
                                                </button>
                                                <button className="btn btn-outline-secondary btn-time-mid" title="Kiểu xem" type="button"
                                                    style={isActiveButton == 2 ? { color: "#fff", backgroundColor: "#0a1a5c" } : { color: "#6c757d", backgroundColor: "#e9ecef" }}
                                                    onClick={() => changeOptionTime(2)}
                                                >
                                                    {t('content.month')}
                                                </button>
                                                <button className="btn btn-outline-secondary btn-time-mid" title="Kiểu xem" type="button"
                                                    style={isActiveButton == 3 ? { color: "#fff", backgroundColor: "#0a1a5c" } : { color: "#6c757d", backgroundColor: "#e9ecef" }}
                                                    onClick={() => changeOptionTime(3)}
                                                >
                                                    {t('content.year')}
                                                </button>
                                                <button className="btn btn-outline-secondary btn-time-right" title="Kiểu xem" type="button"
                                                    style={isActiveButton == 4 ? { color: "#fff", backgroundColor: "#0a1a5c" } : { color: "#6c757d", backgroundColor: "#e9ecef" }}
                                                    onClick={() => changeOptionTime(4)}
                                                >
                                                    {t('content.custom')}
                                                </button>
                                            </div>
                                        </div>
                                        <div className="input-group float-left" style={{ width: "270px" }}>
                                            <div className="input-group-prepend">
                                                <span className="input-group-text pickericon">
                                                    <span className="far fa-calendar"></span>
                                                </span>
                                            </div>
                                            {isActiveButton == 3 &&
                                                <Calendar
                                                    id="from-value"
                                                    className="celendar-picker"
                                                    dateFormat={typeFormat}
                                                    value={fromDate}
                                                    view={viewCalender}
                                                    onChange={e => setFromDate(e.value)}
                                                />
                                            }
                                            {isActiveButton == 2 &&
                                                <Calendar
                                                    id="from-value"
                                                    className="celendar-picker"
                                                    dateFormat={typeFormat}
                                                    value={fromDate}
                                                    view={viewCalender}
                                                    onChange={e => setFromDate(e.value)}
                                                />
                                            }
                                            {isActiveButton == 1 &&
                                                <Calendar
                                                    id="from-value"
                                                    className="celendar-picker"
                                                    dateFormat={typeFormat}
                                                    value={fromDate}
                                                    onChange={e => setFromDate(e.value)}
                                                />
                                            }
                                            {isActiveButton == 4 &&
                                                <Calendar
                                                    id="from-value"
                                                    className="celendar-picker"
                                                    dateFormat={typeFormat}
                                                    value={fromDate}
                                                    onChange={e => setFromDate(e.value)}
                                                    showTime hourFormat="24"
                                                />
                                            }
                                        </div>
                                        {isActiveButton == 4 &&
                                            <div className="input-group float-left" style={{ width: "270px" }}>
                                                <div className="input-group-prepend">
                                                    <span className="input-group-text pickericon" style={{ backgroundColor: "F37021" }}>
                                                        <span className="far fa-calendar"></span>
                                                    </span>
                                                </div>
                                                <Calendar
                                                    id="to-value"
                                                    className="celendar-picker"
                                                    dateFormat={typeFormat}
                                                    value={toDate}
                                                    onChange={e => setToDate(e.value)}
                                                    showTime hourFormat="24"
                                                />
                                            </div>
                                        }
                                        <div className="input-group float-left" style={{ width: "100px" }}>
                                            <select onChange={onChangeValueOptionTime} defaultValue={1} id="select-time">
                                                {valueRadio == 5 || valueRadio == 15 || valueRadio == 16 ?
                                                    <>
                                                        <option value={1}>15 {t('content.minute')}</option>
                                                        <option value={3}>60 {t('content.minute')}</option>
                                                    </>
                                                    :
                                                    <>
                                                        {isActiveButton == 1 || isActiveButton == 4 ?
                                                            <option value={0}>5 {t('content.minute')}</option>
                                                            :
                                                            <></>
                                                        }
                                                        <option value={1}>15 {t('content.minute')}</option>
                                                        <option value={2}>30 {t('content.minute')}</option>
                                                        <option value={3}>60 {t('content.minute')}</option>
                                                    </>
                                                }
                                            </select>
                                        </div>
                                        <div>
                                            <button type="button" className="btn btn-outline-secondary" onClick={() => getDataByFDateTDate()}>
                                                <i className="fa-solid fa-magnifying-glass" style={{ color: "#F37021" }}></i>
                                            </button>
                                            {/* <CSVLink data={listDataInstance} headers={paramHeader}> */}
                                            <button type="button" className="btn btn-outline-secondary ml-1" onClick={downloadDataInstance}>
                                                <i className="fa-solid fa-download" style={{ color: "#F37021" }}></i>
                                            </button>
                                            {/* </CSVLink> */}
                                            <button type="button" className="btn btn-outline-secondary ml-1" style={{ width: "8%", backgroundColor: "#00B5B8", color: "#000" }} onClick={() => showDataChart()}>
                                                {t('content.view_chart')}
                                            </button>
                                        </div>
                                    </div>
                                    {valueRadio == 9 &&
                                        <div id="vol" onChange={(e) => {
                                            handleChangeValueVoltage(e);
                                        }} style={{ width: "100%" }}>
                                            <div className="radio d-inline-block" style={{ backgroundColor: "#0a1a5c" }}>
                                                <input className="with-gap" id="v1" name="radio-vol" type="radio" defaultChecked value={1} />
                                                <label htmlFor="v1">UAN</label>
                                            </div>
                                            <span className="radio d-inline-block" hidden>
                                                <input className="with-gap" id="v2" name="radio-vol" type="radio" value={2} />
                                                <label htmlFor="v2">UBN</label>
                                            </span>
                                            <span className="radio d-inline-block" hidden>
                                                <input className="with-gap" id="v3" name="radio-vol" type="radio" value={3} />
                                                <label htmlFor="v3">UCN</label>
                                            </span>
                                            {/* <span className="radio d-inline-block" hidden>
                                                <input className="with-gap" id="v4" name="radio-vol" type="radio" value={4} />
                                                <label htmlFor="v4">UAB</label>
                                            </span>
                                            <span className="radio d-inline-block">
                                                <input className="with-gap" id="v5" name="radio-vol" type="radio" value={5} />
                                                <label htmlFor="v5">UBC</label>
                                            </span>
                                            <span className="radio d-inline-block">
                                                <input className="with-gap" id="v6" name="radio-vol" type="radio" value={6} />
                                                <label htmlFor="v6">UCA</label>
                                            </span> */}
                                            <span id="slider"></span>
                                        </div>
                                    }
                                    {valueRadio == 8 &&
                                        <div id="vol" onChange={(e) => {
                                            handleChangeValueA(e);
                                        }} style={{ width: "100%" }}>
                                            <div className="radio d-inline-block" style={{ backgroundColor: "#0a1a5c" }}>
                                                <input className="with-gap" id="i1" name="radio-vol" type="radio" defaultChecked value={1} />
                                                <label htmlFor="i1">Ia</label>
                                            </div>
                                            <span className="radio d-inline-block">
                                                <input className="with-gap" id="i2" name="radio-vol" type="radio" value={2} />
                                                <label htmlFor="i2">Ib</label>
                                            </span>
                                            <span className="radio d-inline-block">
                                                <input className="with-gap" id="i3" name="radio-vol" type="radio" value={3} />
                                                <label htmlFor="i3">Ic</label>
                                            </span>
                                            <span id="slider"></span>
                                        </div>
                                    }
                                    <div className="fix-width Flipped">
                                        {isLoading ?
                                            <div className="loading" id="loading-instance" style={{ marginTop: "10%", marginLeft: "50%" }}>
                                                <img height="60px" src="/resources/image/loading2.gif" alt="loading" />
                                            </div>
                                            :
                                            <div className="table-instance" id="table-custom">
                                                <table id="table-instance" className="table-parameter" style={valueRadio != 8 && valueRadio != 9 ? { maxHeight: "700px", overflowY: "auto", width: "99%" } : { maxHeight: "700px", overflowY: "auto", width: "320%" }} >
                                                    <thead className={listDataInstance.length > 24 ? "headTable thead theadScroll" : "headTable thead"} >
                                                        {valueRadio == 8 || valueRadio == 9 ?


                                                            <tr>
                                                                {valueRadio == 8 &&
                                                                    <>
                                                                        {valueRadioA == 1 &&
                                                                            <>
                                                                                <th style={{ width: "10%" }}>{t('content.home_page.manufacture.time')}</th>
                                                                                <th style={{ width: "10%" }}>THD_Ia
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="thdIa" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IA_H1
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="iaH1" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IA_H2
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="iaH2" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IA_H3
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="iaH3" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IA_H4
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="iaH4" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IA_H5
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="iaH5" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IA_H6
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="iaH6" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IA_H7
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="iaH7" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IA_H8
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="iaH8" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IA_H9
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="iaH9" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IA_H10
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="iaH10" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IA_H11
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="iaH11" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IA_H12
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="iaH12" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IA_H13
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="iaH13" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IA_H14
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="iaH14" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IA_H15
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="iaH15" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IA_H16
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="iaH16" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IA_H17
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="iaH17" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IA_H18
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="iaH18" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IA_H19
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="iaH19" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IA_H20
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="iaH20" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IA_H21
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="iaH21" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IA_H22
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="iaH22" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IA_H23
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="iaH23" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IA_H24
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="iaH24" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IA_H25
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="iaH25" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IA_H26
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="iaH26" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IA_H27
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="iaH27" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IA_H28
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="iaH28" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IA_H29
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="iaH29" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IA_H30
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="iaH30" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IA_H31
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="iaH31" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                            </>
                                                                        }
                                                                        {valueRadioA == 2 &&
                                                                            <>
                                                                                <th style={{ width: "10%" }}>{t('content.home_page.manufacture.time')}</th>
                                                                                <th style={{ width: "10%" }}>THD_Ib
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="thdIb" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IB_H1
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ibH1" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IB_H2
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ibH2" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IB_H3
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ibH3" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IB_H4
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ibH4" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IB_H5
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ibH5" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IB_H6
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ibH6" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IB_H7
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ibH7" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IB_H8
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ibH8" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IB_H9
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ibH9" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IB_H10
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ibH10" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IB_H11
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ibH11" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IB_H12
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ibH12" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IB_H13
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ibH13" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IB_H14
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ibH14" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IB_H15
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ibH15" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IB_H16
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ibH16" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IB_H17
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ibH17" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IB_H18
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ibH18" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IB_H19
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ibH19" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IB_H20
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ibH20" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IB_H21
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ibH21" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IB_H22
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ibH22" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IB_H23
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ibH23" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IB_H24
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ibH24" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IB_H25
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ibH25" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IB_H26
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ibH26" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IB_H27
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ibH27" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IB_H28
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ibH28" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IB_H29
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ibH29" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IB_H30
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ibH30" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IB_H31
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ibH31" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                            </>
                                                                        }
                                                                        {valueRadioA == 3 &&
                                                                            <>
                                                                                <th style={{ width: "10%" }}>{t('content.home_page.manufacture.time')}</th>
                                                                                <th style={{ width: "10%" }}>THD_Ic
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="thdIc" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IC_H1
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="icH1" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IC_H2
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="icH2" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IC_H3
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="icH3" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IC_H4
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="icH4" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IC_H5
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="icH5" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IC_H6
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="icH6" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IC_H7
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="icH7" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IC_H8
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="icH8" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IC_H9
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="icH9" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IC_H10
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="icH10" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IC_H11
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="icH11" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IC_H12
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="icH12" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IC_H13
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="icH13" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IC_H14
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="icH14" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IC_H15
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="icH15" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IC_H16
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="icH16" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IC_H17
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="icH17" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IC_H18
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="icH18" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IC_H19
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="icH19" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IC_H20
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="icH20" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IC_H21
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="icH21" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IC_H22
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="icH22" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IC_H23
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="icH23" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IC_H24
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="icH24" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IC_H25
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="icH25" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IC_H26
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="icH26" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IC_H27
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="icH27" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IC_H28
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="icH28" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IC_H29
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="icH29" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IC_H30
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="icH30" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>IC_H31
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="icH31" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                            </>
                                                                        }
                                                                    </>
                                                                }
                                                                {valueRadio == 9 &&
                                                                    <>
                                                                        {valueRadioVol == 4 &&
                                                                            <>
                                                                                <th style={{ width: "10%" }}>{t('content.home_page.manufacture.time')}</th>
                                                                                <th style={{ width: "10%" }}>THD_Uab
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="thdVab" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAB_H1
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vabH1" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAB_H2
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vabH2" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAB_H3
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vabH3" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAB_H4
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vabH4" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAB_H5
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vabH5" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAB_H6
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vabH6" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAB_H7
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vabH7" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAB_H8
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vabH8" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAB_H9
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vabH9" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAB_H10
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vabH10" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAB_H11
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vabH11" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAB_H12
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vabH12" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAB_H13
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vabH13" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAB_H14
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vabH14" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAB_H15
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vabH15" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAB_H16
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vabH16" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAB_H17
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vabH17" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAB_H18
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vabH18" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAB_H19
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vabH19" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAB_H20
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vabH20" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAB_H21
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vabH21" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAB_H22
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vabH22" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAB_H23
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vabH23" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAB_H24
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vabH24" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAB_H25
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vabH25" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAB_H26
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vabH26" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAB_H27
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vabH27" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAB_H28
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vabH28" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAB_H29
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vabH29" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAB_H30
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vabH30" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAB_H31
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vabH31" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                            </>
                                                                        }
                                                                        {valueRadioVol == 5 &&
                                                                            <>
                                                                                <th style={{ width: "10%" }}>{t('content.home_page.manufacture.time')}</th>
                                                                                <th style={{ width: "10%" }}>THD_Ubc
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="thdVbc" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBC_H1
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbcH1" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBC_H2
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbcH2" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBC_H3
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbcH3" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBC_H4
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbcH4" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBC_H5
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbcH5" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBC_H6
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbcH6" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBC_H7
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbcH7" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBC_H8
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbcH8" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBC_H9
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbcH9" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBC_H10
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbcH10" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBC_H11
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbcH11" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBC_H12
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbcH12" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBC_H13
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbcH13" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBC_H14
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbcH14" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBC_H15
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbcH15" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBC_H16
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbcH16" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBC_H17
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbcH17" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBC_H18
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbcH18" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBC_H19
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbcH19" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBC_H20
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbcH20" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBC_H21
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbcH21" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBC_H22
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbcH22" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBC_H23
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbcH23" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBC_H24
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbcH24" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBC_H25
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbcH25" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBC_H26
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbcH26" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBC_H27
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbcH27" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBC_H28
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbcH28" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBC_H29
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbcH29" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBC_H30
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbcH30" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBC_H31
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbcH31" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                            </>
                                                                        }
                                                                        {valueRadioVol == 6 &&
                                                                            <>
                                                                                <th style={{ width: "10%" }}>{t('content.home_page.manufacture.time')}</th>
                                                                                <th style={{ width: "10%" }}>THD_Uca
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="thdVcac" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCA_H1
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcaH1" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCA_H2
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcaH2" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCA_H3
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcaH3" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCA_H4
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcaH4" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCA_H5
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcaH5" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCA_H6
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcaH6" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCA_H7
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcaH7" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCA_H8
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcaH8" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCA_H9
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcaH9" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCA_H10
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcaH10" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCA_H11
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcaH11" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCA_H12
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcaH12" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCA_H13
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcaH13" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCA_H14
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcaH14" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCA_H15
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcaH15" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCA_H16
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcaH16" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCA_H17
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcaH17" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCA_H18
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcaH18" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCA_H19
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcaH19" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCA_H20
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcaH20" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCA_H21
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcaH21" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCA_H22
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcaH22" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCA_H23
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcaH23" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCA_H24
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcaH24" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCA_H25
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcaH25" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCA_H26
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcaH26" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCA_H27
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcaH27" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCA_H28
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcaH28" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCA_H29
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcaH29" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCA_H30
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcaH30" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCA_H31
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcaH31" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                            </>
                                                                        }
                                                                        {valueRadioVol == 1 &&
                                                                            <>
                                                                                <th style={{ width: "10%" }}>{t('content.home_page.manufacture.time')}</th>
                                                                                <th style={{ width: "10%" }}>THD_Uan
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="thdVan" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAN_H1
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vanH1" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAN_H2
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vanH2" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAN_H3
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vanH3" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAN_H4
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vanH4" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAN_H5
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vanH5" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAN_H6
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vanH6" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAN_H7
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vanH7" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAN_H8
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vanH8" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAN_H9
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vanH9" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAN_H10
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vanH10" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAN_H11
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vanH11" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAN_H12
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vanH12" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAN_H13
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vanH13" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAN_H14
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vanH14" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAN_H15
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vanH15" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAN_H16
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vanH16" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAN_H17
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vanH17" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAN_H18
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vanH18" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAN_H19
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vanH19" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAN_H20
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vanH20" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAN_H21
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vanH21" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAN_H22
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vanH22" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAN_H23
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vanH23" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAN_H24
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vanH24" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAN_H25
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vanH25" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAN_H26
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vanH26" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAN_H27
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vanH27" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAN_H28
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vanH28" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAN_H29
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vanH29" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAN_H30
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vanH30" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UAN_H31
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vanH31" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                            </>
                                                                        }
                                                                        {valueRadioVol == 2 &&
                                                                            <>
                                                                                <th style={{ width: "10%" }}>{t('content.home_page.manufacture.time')}</th>
                                                                                <th style={{ width: "10%" }}>THD_Ubn
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="thdVbn" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBN_H1
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbnH1" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBN_H2
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbnH2" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBN_H3
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbnH3" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBN_H4
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbnH4" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBN_H5
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbnH5" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBN_H6
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbnH6" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBN_H7
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbnH7" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBN_H8
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbnH8" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBN_H9
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbnH9" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBN_H10
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbnH10" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBN_H11
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbnH11" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBN_H12
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbnH12" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBN_H13
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbnH13" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBN_H14
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbnH14" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBN_H15
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbnH15" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBN_H16
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbnH16" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBN_H17
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbnH17" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBN_H18
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbnH18" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBN_H19
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbnH19" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBN_H20
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbnH20" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBN_H21
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbnH21" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBN_H22
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbnH22" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBN_H23
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbnH23" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBN_H24
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbnH24" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBN_H25
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbnH25" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBN_H26
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbnH26" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBN_H27
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbnH27" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBN_H28
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbnH28" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBN_H29
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbnH29" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBN_H30
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbnH30" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UBN_H31
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vbnH31" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                            </>
                                                                        }
                                                                        {valueRadioVol == 3 &&
                                                                            <>
                                                                                <th style={{ width: "10%" }}>{t('content.home_page.manufacture.time')}</th>
                                                                                <th style={{ width: "10%" }}>THD_Ucn
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="thdVcn" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCN_H1
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcnH1" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCN_H2
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcnH2" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCN_H3
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcnH3" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCN_H4
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcnH4" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCN_H5
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcnH5" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCN_H6
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcnH6" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCN_H7
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcnH7" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCN_H8
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcnH8" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCN_H9
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcnH9" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCN_H10
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcnH10" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCN_H11
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcnH11" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCN_H12
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcnH12" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCN_H13
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcnH13" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCN_H14
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcnH14" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCN_H15
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcnH15" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCN_H16
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcnH16" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCN_H17
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcnH17" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCN_H18
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcnH18" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCN_H19
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcnH19" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCN_H20
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcnH20" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCN_H21
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcnH21" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCN_H22
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcnH22" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCN_H23
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcnH23" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCN_H24
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcnH24" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCN_H25
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcnH25" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCN_H26
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcnH26" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCN_H27
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcnH27" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCN_H28
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcnH28" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCN_H29
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcnH29" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCN_H30
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcnH30" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th style={{ width: "10%" }}>UCN_H31
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="vcnH31" type="checkbox" defaultChecked={true} style={{ paddingRight: "5px", paddingTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                            </>
                                                                        }
                                                                    </>
                                                                }
                                                            </tr>
                                                            :
                                                            <tr>
                                                                {valueRadio == 1 &&
                                                                    <>
                                                                        {iDevice.systemTypeId == 2 && (iDevice.deviceType == 3 || iDevice.deviceType == 4) ?
                                                                            <>
                                                                                <th style={{ width: "10%" }}>{t('content.home_page.manufacture.time')}</th>
                                                                                <th>
                                                                                    Udc
                                                                                </th>
                                                                            </>
                                                                            :
                                                                            <>
                                                                                <th style={{ width: "10%" }}>{t('content.home_page.manufacture.time')}</th>
                                                                                <th>
                                                                                    Uab
                                                                                    <div style={{ position: 'absolute', left: "85%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="uab" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th>
                                                                                    Ubc
                                                                                    <div style={{ position: 'absolute', left: "85%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ubc" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th>
                                                                                    Uca
                                                                                    <div style={{ position: 'absolute', left: "85%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="uca" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th >
                                                                                    Uan
                                                                                    <div style={{ position: 'absolute', left: "85%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="uan" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th>
                                                                                    Ubn
                                                                                    <div style={{ position: 'absolute', left: "85%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ubn" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th>
                                                                                    Ucn
                                                                                    <div style={{ position: 'absolute', left: "85%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ucn" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                            </>
                                                                        }
                                                                    </>
                                                                }
                                                                {valueRadio == 2 &&
                                                                    <>
                                                                        {iDevice.systemTypeId == 2 && (iDevice.deviceType == 3 || iDevice.deviceType == 4) ?
                                                                            <>
                                                                                <th style={{ width: "10%" }}>{t('content.home_page.manufacture.time')}</th>
                                                                                <th>
                                                                                    Pdc
                                                                                </th>
                                                                            </>
                                                                            :
                                                                            <>
                                                                                <th style={{ width: "10%" }}>{t('content.home_page.manufacture.time')}</th>
                                                                                <th>
                                                                                    Pa
                                                                                    <div style={{ position: 'absolute', left: "85%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="pa" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th>
                                                                                    Pb
                                                                                    <div style={{ position: 'absolute', left: "85%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="pb" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th>
                                                                                    Pc
                                                                                    <div style={{ position: 'absolute', left: "85%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="pc" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th>
                                                                                    Qa
                                                                                    <div style={{ position: 'absolute', left: "85%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="qa" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th>
                                                                                    Qb
                                                                                    <div style={{ position: 'absolute', left: "85%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="qb" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th>
                                                                                    Qc
                                                                                    <div style={{ position: 'absolute', left: "85%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="qc" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th>
                                                                                    Ptotal
                                                                                    <div style={{ position: 'absolute', left: "85%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ptotal" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                            </>
                                                                        }
                                                                    </>
                                                                }
                                                                {valueRadio == 3 &&
                                                                    <>
                                                                        {iDevice.systemTypeId == 2 && (iDevice.deviceType == 3 || iDevice.deviceType == 4) ?
                                                                            <>
                                                                                <th style={{ width: "10%" }}>{t('content.home_page.manufacture.time')}</th>
                                                                                <th>
                                                                                    Idc
                                                                                </th>
                                                                            </>
                                                                            :
                                                                            <>
                                                                                <th style={{ width: "10%" }}>{t('content.home_page.manufacture.time')}</th>
                                                                                <th>
                                                                                    Ia
                                                                                    <div style={{ position: 'absolute', left: "90%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ia" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th>
                                                                                    Ib
                                                                                    <div style={{ position: 'absolute', left: "90%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ib" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th>
                                                                                    Ic
                                                                                    <div style={{ position: 'absolute', left: "90%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ic" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th>
                                                                                    I
                                                                                    <div style={{ position: 'absolute', left: "90%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="i" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                            </>
                                                                        }
                                                                    </>
                                                                }
                                                                {valueRadio == 4 &&
                                                                    <>
                                                                        <th style={{ width: "10%" }}>{t('content.home_page.manufacture.time')}</th>
                                                                        <th>
                                                                            Pfa
                                                                            <div style={{ position: 'absolute', left: "90%", top: "20%" }}>
                                                                                <label className="input-checked float-right">
                                                                                    <input id="pfa" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                    <span className="checkmark"></span>
                                                                                </label>
                                                                            </div>
                                                                        </th>
                                                                        <th>
                                                                            Pfb
                                                                            <div style={{ position: 'absolute', left: "90%", top: "20%" }}>
                                                                                <label className="input-checked float-right">
                                                                                    <input id="pfb" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                    <span className="checkmark"></span>
                                                                                </label>
                                                                            </div>
                                                                        </th>
                                                                        <th>
                                                                            Pfc
                                                                            <div style={{ position: 'absolute', left: "90%", top: "20%" }}>
                                                                                <label className="input-checked float-right">
                                                                                    <input id="pfc" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                    <span className="checkmark"></span>
                                                                                </label>
                                                                            </div>
                                                                        </th>
                                                                    </>
                                                                }
                                                                {valueRadio == 5 &&
                                                                    <>
                                                                        {iDevice.systemTypeId == 2 && iDevice.deviceType == 2 ?
                                                                            <>
                                                                                <th style={{ width: "10%" }}>{t('content.home_page.manufacture.time')}</th>
                                                                                <th>
                                                                                    Epac
                                                                                    <div style={{ position: 'absolute', left: "95%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ep" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th>
                                                                                    Epdc
                                                                                    <div style={{ position: 'absolute', left: "95%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="epdc" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                            </>
                                                                            :
                                                                            <>
                                                                                <th style={{ width: "10%" }}>{t('content.home_page.manufacture.time')}</th>
                                                                                <th>
                                                                                    Điện năng
                                                                                    <div style={{ position: 'absolute', left: "98%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ep" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                            </>
                                                                        }
                                                                    </>
                                                                }
                                                                {valueRadio == 6 &&
                                                                    <>
                                                                        {iDevice.systemTypeId == 1 && iDevice.deviceType == 1 ?
                                                                            <>
                                                                                <th style={{ width: "10%" }}>{t('content.home_page.manufacture.time')}</th>
                                                                                <th>
                                                                                    Tần số
                                                                                    <div style={{ position: 'absolute', left: "98%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="f" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                            </>
                                                                            :
                                                                            <>
                                                                                <th style={{ width: "10%" }}>{t('content.home_page.manufacture.time')}</th>
                                                                                <th>
                                                                                    {t('content.home_page.warning_tab.temp_sensor')}
                                                                                    <div style={{ position: 'absolute', left: "98%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="t" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                            </>
                                                                        }
                                                                    </>
                                                                }
                                                                {valueRadio == 10 &&
                                                                    <>
                                                                        <th style={{ width: "10%" }}>{t('content.home_page.manufacture.time')}</th>
                                                                        <th>
                                                                            LFB RATIO
                                                                            <div style={{ position: 'absolute', left: "85%", top: "20%" }}>
                                                                                <label className="input-checked float-right">
                                                                                    <input id="thdVab" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                    <span className="checkmark"></span>
                                                                                </label>
                                                                            </div>
                                                                        </th>
                                                                        <th>
                                                                            LFB EPPC
                                                                            <div style={{ position: 'absolute', left: "85%", top: "20%" }}>
                                                                                <label className="input-checked float-right">
                                                                                    <input id="thdVbc" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                    <span className="checkmark"></span>
                                                                                </label>
                                                                            </div>
                                                                        </th>
                                                                        <th>
                                                                            MFB RATIO
                                                                            <div style={{ position: 'absolute', left: "85%", top: "20%" }}>
                                                                                <label className="input-checked float-right">
                                                                                    <input id="thdVca" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                    <span className="checkmark"></span>
                                                                                </label>
                                                                            </div>
                                                                        </th>
                                                                        <th >
                                                                            MFB EPPC
                                                                            <div style={{ position: 'absolute', left: "85%", top: "20%" }}>
                                                                                <label className="input-checked float-right">
                                                                                    <input id="thdVan" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                    <span className="checkmark"></span>
                                                                                </label>
                                                                            </div>
                                                                        </th>
                                                                        <th>
                                                                            HLFP RATIO
                                                                            <div style={{ position: 'absolute', left: "85%", top: "20%" }}>
                                                                                <label className="input-checked float-right">
                                                                                    <input id="thdVbn" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                    <span className="checkmark"></span>
                                                                                </label>
                                                                            </div>
                                                                        </th>
                                                                        <th >
                                                                            HLFB EPPC
                                                                            <div style={{ position: 'absolute', left: "85%", top: "20%" }}>
                                                                                <label className="input-checked float-right">
                                                                                    <input id="thdVcn" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                    <span className="checkmark"></span>
                                                                                </label>
                                                                            </div>
                                                                        </th>
                                                                        <th >
                                                                            INDICATOR
                                                                            <div style={{ position: 'absolute', left: "85%", top: "20%" }}>
                                                                                <label className="input-checked float-right">
                                                                                    <input id="thdVcn" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                    <span className="checkmark"></span>
                                                                                </label>
                                                                            </div>
                                                                        </th>
                                                                    </>
                                                                }
                                                                {valueRadio == 7 &&
                                                                    <th>

                                                                    </th>
                                                                }
                                                                {valueRadio == 11 &&
                                                                    <>
                                                                        <th style={{ width: "10%" }}>{t('content.home_page.manufacture.time')}</th>
                                                                        <th>
                                                                            Độ ẩm
                                                                            <div style={{ position: 'absolute', left: "98%", top: "20%" }}>
                                                                                <label className="input-checked float-right">
                                                                                    <input id="h" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                    <span className="checkmark"></span>
                                                                                </label>
                                                                            </div>
                                                                        </th>
                                                                    </>
                                                                }
                                                                {valueRadio == 12 &&
                                                                    <>
                                                                        <th style={{ width: "10%" }}>{t('content.home_page.manufacture.time')}</th>
                                                                        {iDevice.deviceType == 5 ?
                                                                            <>

                                                                                <th>
                                                                                    LFB RATIO
                                                                                    {/* <div style={{ position: 'absolute', left: "80%", top: "20%" }}> */}

                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="lfbRatio" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th>
                                                                                    LFB EPPC
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="lfbEppc" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th>
                                                                                    MFB RATIO
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="mfbRatio" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th >
                                                                                    MFB EPPC
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="mfbEppc" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th>
                                                                                    HFB RATIO
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="hfbRatio" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th >
                                                                                    HFB EPPC
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="hfbEppc" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th>
                                                                                    MEAN RATIO
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="meanRatio" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th >
                                                                                    MEAN EPPC
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="meanEppc" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                {/* <th>
                                                                                    RATIO EPPC HI
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ratioEppcHi" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th >
                                                                                    RATIO EPPC LO
                                                                                    <div style={{ position: 'absolute', left: "80%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ratioEppcLo" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th> */}
                                                                            </>
                                                                            :
                                                                            <>
                                                                                <th >
                                                                                    RATIO
                                                                                    <div style={{ position: 'absolute', left: "96%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="ratio" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                                <th >
                                                                                    EPPC
                                                                                    <div style={{ position: 'absolute', left: "96%", top: "20%" }}>
                                                                                        <label className="input-checked float-right">
                                                                                            <input id="eppc" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                            <span className="checkmark"></span>
                                                                                        </label>
                                                                                    </div>
                                                                                </th>
                                                                            </>}
                                                                    </>
                                                                }
                                                                {valueRadio == 13 &&
                                                                    <>
                                                                        <th style={{ width: "10%" }}>{t('content.home_page.manufacture.time')}</th>
                                                                        <th >
                                                                            INDICATOR
                                                                            <div style={{ position: 'absolute', left: "98%", top: "20%" }}>
                                                                                <label className="input-checked float-right">
                                                                                    <input id="indicator" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                    <span className="checkmark"></span>
                                                                                </label>
                                                                            </div>
                                                                        </th>
                                                                    </>
                                                                }
                                                                {valueRadio == 14 &&
                                                                    <>
                                                                        <th style={{ width: "10%" }}>{t('content.home_page.manufacture.time')}</th>
                                                                        <th >
                                                                            STATUS SERVER
                                                                            <div style={{ position: 'absolute', left: "98%", top: "20%" }}>
                                                                                <label className="input-checked float-right">
                                                                                    <input id="status" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                    <span className="checkmark"></span>
                                                                                </label>
                                                                            </div>
                                                                        </th>
                                                                    </>
                                                                }
                                                                {valueRadio == 15 &&
                                                                    <>
                                                                        <th style={{ width: "10%" }}>{t('content.home_page.manufacture.time')}</th>
                                                                        <th >
                                                                            LƯU LƯỢNG
                                                                            <div style={{ position: 'absolute', left: "98%", top: "20%" }}>
                                                                                <label className="input-checked float-right">
                                                                                    <input id="t" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                    <span className="checkmark"></span>
                                                                                </label>
                                                                            </div>
                                                                        </th>
                                                                    </>
                                                                }
                                                                {valueRadio == 16 &&
                                                                    <>
                                                                        <th style={{ width: "10%" }}>{t('content.home_page.manufacture.time')}</th>
                                                                        <th >
                                                                            LƯU LƯỢNG TÍCH LŨY
                                                                            <div style={{ position: 'absolute', left: "98%", top: "20%" }}>
                                                                                <label className="input-checked float-right">
                                                                                    <input id="taccumulationDay" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                    <span className="checkmark"></span>
                                                                                </label>
                                                                            </div>
                                                                        </th>
                                                                    </>
                                                                }
                                                                {valueRadio == 17 &&
                                                                    <>
                                                                        <th style={{ width: "10%" }}>{t('content.home_page.manufacture.time')}</th>
                                                                        <th >
                                                                            ÁP SUẤT
                                                                            <div style={{ position: 'absolute', left: "98%", top: "20%" }}>
                                                                                <label className="input-checked float-right">
                                                                                    <input id="p" type="checkbox" defaultChecked={true} style={{ marginRight: "10px", marginTop: "5px" }} />
                                                                                    <span className="checkmark"></span>
                                                                                </label>
                                                                            </div>
                                                                        </th>
                                                                    </>
                                                                }
                                                            </tr>
                                                        }
                                                    </thead>
                                                    <tbody style={{ lineHeight: 1.5 }} className="tbody">
                                                        {listDataInstanceFrame1Table.length > 0 &&
                                                            <>
                                                                {valueRadio == 1 &&
                                                                    <>
                                                                        {listDataInstanceFrame1Table?.map((item, index) => (
                                                                            <tr key={index}>
                                                                                {iDevice.systemTypeId == 2 ?
                                                                                    <>
                                                                                        {iDevice.deviceType == 3 &&
                                                                                            <>
                                                                                                <td style={{ width: "10%" }} className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                                <td style={{ width: "10%" }} className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>


                                                                                                <td>{item.vdcCombiner}</td>
                                                                                            </>
                                                                                        }
                                                                                        {iDevice.deviceType == 4 &&
                                                                                            <>
                                                                                                <td style={{ width: "10%" }} className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                                <td>{item.vdcStr}</td>
                                                                                            </>
                                                                                        }
                                                                                        {iDevice.deviceType == 2 &&
                                                                                            <>
                                                                                                <td style={{ width: "10%" }} className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                                <td>{item.uab}</td>
                                                                                                <td>{item.ubc}</td>
                                                                                                <td>{item.uca}</td>
                                                                                                <td>{item.uan}</td>
                                                                                                <td>{item.ubn}</td>
                                                                                                <td>{item.ucn}</td>
                                                                                            </>
                                                                                        }
                                                                                    </>
                                                                                    :
                                                                                    <>
                                                                                        <td style={{ width: "10%" }} className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                        <td>{item.uab != 0 ? item.uab : "-"}</td>
                                                                                        <td>{item.ubc != 0 ? item.ubc : "-"}</td>
                                                                                        <td>{item.uca != 0 ? item.uca : "-"}</td>
                                                                                        <td>{item.uan != 0 ? item.uan : "-"}</td>
                                                                                        <td>{item.ubn != 0 ? item.ubn : "-"}</td>
                                                                                        <td>{item.ucn != 0 ? item.ucn : "-"}</td>
                                                                                    </>
                                                                                }
                                                                            </tr>
                                                                        ))
                                                                        }
                                                                    </>
                                                                }
                                                                {valueRadio == 2 &&
                                                                    <>
                                                                        {listDataInstanceFrame1Table?.map((item, index) => (
                                                                            <tr key={index}>
                                                                                {iDevice.systemTypeId == 2 ?
                                                                                    <>
                                                                                        {iDevice.deviceType == 3 &&
                                                                                            <>
                                                                                                <td style={{ width: "10%" }} className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                                <td>{item.pdcCombiner}</td>
                                                                                            </>
                                                                                        }
                                                                                        {iDevice.deviceType == 4 &&
                                                                                            <>
                                                                                                <td style={{ width: "10%" }} className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                                <td>{item.pdcStr}</td>
                                                                                            </>
                                                                                        }
                                                                                        {iDevice.deviceType == 2 &&
                                                                                            <>
                                                                                                <td style={{ width: "10%" }} className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                                <td>{item.pa != 0 ? item.pa : "-"}</td>
                                                                                                <td>{item.pb != 0 ? item.pb : "-"}</td>
                                                                                                <td>{item.pc != 0 ? item.pc : "-"}</td>
                                                                                                <td>{item.qa != 0 ? item.qa : "-"}</td>
                                                                                                <td>{item.qb != 0 ? item.qb : "-"}</td>
                                                                                                <td>{item.qc != 0 ? item.qc : "-"}</td>
                                                                                                <td>{item.ptotal != 0 ? item.ptotal : "-"}</td>
                                                                                            </>
                                                                                        }
                                                                                    </>
                                                                                    :
                                                                                    <>
                                                                                        <td style={{ width: "10%" }} className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                        <td>{item.pa != 0 ? item.pa : "-"}</td>
                                                                                        <td>{item.pb != 0 ? item.pb : "-"}</td>
                                                                                        <td>{item.pc != 0 ? item.pc : "-"}</td>
                                                                                        <td>{item.qa != 0 ? item.qa : "-"}</td>
                                                                                        <td>{item.qb != 0 ? item.qb : "-"}</td>
                                                                                        <td>{item.qc != 0 ? item.qc : "-"}</td>
                                                                                        <td>{item.ptotal != 0 ? item.ptotal : "-"}</td>
                                                                                    </>
                                                                                }
                                                                            </tr>
                                                                        ))
                                                                        }
                                                                    </>
                                                                }
                                                                {valueRadio == 3 &&
                                                                    <>
                                                                        {listDataInstanceFrame1Table?.map((item, index) => (
                                                                            <tr key={index}>
                                                                                {iDevice.systemTypeId == 2 ?
                                                                                    <>
                                                                                        {iDevice.deviceType == 3 &&
                                                                                            <>
                                                                                                <td style={{ width: "10%" }} className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                                <td>{item.idcCombiner}</td>
                                                                                            </>
                                                                                        }
                                                                                        {iDevice.deviceType == 4 &&
                                                                                            <>
                                                                                                <td style={{ width: "10%" }} className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                                <td>{item.idcStr}</td>
                                                                                            </>
                                                                                        }
                                                                                        {iDevice.deviceType == 2 &&
                                                                                            <>
                                                                                                <td style={{ width: "10%" }} className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                                <td>{item.ia != 0 ? item.ia : "-"}</td>
                                                                                                <td>{item.ib != 0 ? item.ib : "-"}</td>
                                                                                                <td>{item.ic != 0 ? item.ic : "-"}</td>
                                                                                                <td>{item.i != 0 ? item.i : "-"}</td>
                                                                                            </>
                                                                                        }
                                                                                    </>
                                                                                    :
                                                                                    <>
                                                                                        <td style={{ width: "10%" }} className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                        <td>{item.ia != 0 ? item.ia : "-"}</td>
                                                                                        <td>{item.ib != 0 ? item.ib : "-"}</td>
                                                                                        <td>{item.ic != 0 ? item.ic : "-"}</td>
                                                                                        <td>{item.iavg != 0 ? item.iavg : "-"}</td>
                                                                                    </>
                                                                                }
                                                                            </tr>
                                                                        ))
                                                                        }
                                                                    </>
                                                                }
                                                                {valueRadio == 4 &&
                                                                    <>
                                                                        {listDataInstanceFrame1Table?.map((item, index) => (
                                                                            <tr key={index}>
                                                                                <td style={{ width: "10%" }} className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                <td>{item.pfa != 0 ? item.pfa : "-"}</td>
                                                                                <td>{item.pfb != 0 ? item.pfb : "-"}</td>
                                                                                <td>{item.pfc != 0 ? item.pfc : "-"}</td>
                                                                            </tr>
                                                                        ))
                                                                        }
                                                                    </>
                                                                }
                                                                {valueRadio == 5 &&
                                                                    <>
                                                                        {listDataInstanceFrame1Table?.map((item, index) => (
                                                                            <tr key={index}>
                                                                                {iDevice.systemTypeId == 2 && iDevice.deviceType == 2 ?
                                                                                    <>
                                                                                        {item.ep != null &&
                                                                                            <>
                                                                                                <td style={{ width: "10%" }} className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                                <td>{item.ep}</td>
                                                                                                <td>{item.epdc != 0 ? item.epdc : "-"}</td>
                                                                                            </>
                                                                                        }
                                                                                    </>
                                                                                    :
                                                                                    <>
                                                                                        {item.ep != null &&
                                                                                            <>
                                                                                                <td style={{ width: "10%" }} className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                                <td>{item.ep}</td>
                                                                                            </>
                                                                                        }
                                                                                    </>
                                                                                }
                                                                            </tr>
                                                                        ))
                                                                        }
                                                                    </>
                                                                }
                                                                {valueRadio == 6 &&
                                                                    <>
                                                                        {iDevice.systemTypeId == 1 && iDevice.deviceType == 1 ?
                                                                            <>
                                                                                {listDataInstanceFrame1Table?.map((item, index) => (
                                                                                    <tr key={index}>
                                                                                        <>
                                                                                            <td style={{ width: "10%" }} className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                            <td>{item.f != 0 ? item.f : "-"}</td>
                                                                                        </>

                                                                                    </tr>
                                                                                ))
                                                                                }
                                                                            </>
                                                                            :
                                                                            <>
                                                                                {listDataInstanceFrame1Table?.map((item, i) => (
                                                                                    <tr key={i}>
                                                                                        <>
                                                                                            <td style={{ width: "10%" }} className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                            <td>{item.t != null ? item.t : "-"}</td>
                                                                                        </>

                                                                                    </tr>
                                                                                ))
                                                                                }
                                                                            </>
                                                                        }
                                                                    </>
                                                                }
                                                                {valueRadio == 10 &&
                                                                    <>
                                                                        {listDataInstanceFrame1Table?.map((item, index) => (
                                                                            <tr key={index}>
                                                                                <td style={{ width: "10%" }} className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                <td>{item.lfbRatio != null ? item.lfbRatio : "-"}</td>
                                                                                <td>{item.lfbEppc != null ? item.lfbEppc : "-"}</td>
                                                                                <td>{item.mfbRatio != null ? item.mfbRatio : "-"}</td>
                                                                                <td>{item.mfbEppc != null ? item.mfbEppc : "-"}</td>
                                                                                <td>{item.hlfbRatio != null ? item.hlfbRatio : "-"}</td>
                                                                                <td>{item.hlfbEppc != null ? item.hlfbEppc : "-"}</td>
                                                                                <td>{item.indicator != null ? item.indicator : "-"}</td>
                                                                            </tr>
                                                                        ))
                                                                        }
                                                                    </>
                                                                }
                                                                {valueRadio == 11 &&
                                                                    <>
                                                                        {listDataInstanceFrame1Table?.map((item, index) => (
                                                                            <tr key={index}>
                                                                                <>
                                                                                    <td style={{ width: "10%" }} className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                    <td>{item.h != null ? item.h : "-"}</td>
                                                                                </>
                                                                            </tr>
                                                                        ))
                                                                        }
                                                                    </>
                                                                }
                                                                {valueRadio == 12 &&
                                                                    <>
                                                                        {listDataInstanceFrame1Table?.map((item, index) => (
                                                                            <tr key={index}>
                                                                                <>
                                                                                    <td style={{ width: "10%" }} className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                    {iDevice.deviceType == 5 ?
                                                                                        <>
                                                                                            <td>{item.lfbRatio != null ? item.lfbRatio : "-"}</td>
                                                                                            <td>{item.lfbEppc != null ? item.lfbEppc : "-"}</td>
                                                                                            <td>{item.mfbRatio != null ? item.mfbRatio : "-"}</td>
                                                                                            <td>{item.mfbEppc != null ? item.mfbEppc : "-"}</td>
                                                                                            <td>{item.hfbRatio != null ? item.hfbRatio : "-"}</td>
                                                                                            <td>{item.hfbEppc != null ? item.hfbEppc : "-"}</td>
                                                                                            <td>{item.meanRatio != null ? item.meanRatio : "-"}</td>
                                                                                            <td>{item.meanEppc != null ? item.meanEppc : "-"}</td>
                                                                                            {/* <td>{item.ratioEppcHi != 0 ? item.ratioEppcHi : "-"}</td>
                                                                                            <td>{item.ratioEppcLo != 0 ? item.ratioEppcLo : "-"}</td> */}
                                                                                        </> :
                                                                                        <>
                                                                                            <td>{item.ratio != null ? item.ratio : "-"}</td>
                                                                                            <td>{item.eppc != null ? item.eppc : "-"}</td>
                                                                                        </>}
                                                                                </>
                                                                            </tr>
                                                                        ))
                                                                        }
                                                                    </>
                                                                }
                                                                {valueRadio == 13 &&
                                                                    <>
                                                                        {/* {listDataInstanceFrame1Table?.map((item, index) => (
                                                                            <tr key={index}>
                                                                                <>
                                                                                    <td style={{ width: "10%" }} className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                    <td>{item.indicator != 0 ? item.indicator : "-"}</td>

                                                                                </>
                                                                            </tr>
                                                                        ))
                                                                        } */}

                                                                        {listDataInstanceFrame1Table?.map((item, index) => (
                                                                            <tr key={index}>
                                                                                <>
                                                                                    <td style={{ width: "10%" }} className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                    <td>{item.indicator != null ? item.indicator : "-"}</td>
                                                                                </>
                                                                            </tr>
                                                                        ))
                                                                        }
                                                                    </>
                                                                }
                                                                {valueRadio == 8 &&
                                                                    <>
                                                                        {listDataInstanceFrame2Table?.map((item, index) => (
                                                                            <tr key={index}>
                                                                                {valueRadioA == 1 &&
                                                                                    <>
                                                                                        <td className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                        <td >{item.thdIa != 0 ? item.thdIa : "-"}</td>
                                                                                        <td>{item.iaH1 != 0 ? item.iaH1 : "-"}</td>
                                                                                        <td>{item.iaH2 != 0 ? item.iaH2 : "-"}</td>
                                                                                        <td>{item.iaH3 != 0 ? item.iaH3 : "-"}</td>
                                                                                        <td>{item.iaH4 != 0 ? item.iaH4 : "-"}</td>
                                                                                        <td>{item.iaH5 != 0 ? item.iaH5 : "-"}</td>
                                                                                        <td>{item.iaH6 != 0 ? item.iaH6 : "-"}</td>
                                                                                        <td>{item.iaH7 != 0 ? item.iaH7 : "-"}</td>
                                                                                        <td>{item.iaH8 != 0 ? item.iaH8 : "-"}</td>
                                                                                        <td>{item.iaH9 != 0 ? item.iaH9 : "-"}</td>
                                                                                        <td>{item.iaH10 != 0 ? item.iaH10 : "-"}</td>
                                                                                        <td>{item.iaH11 != 0 ? item.iaH11 : "-"}</td>
                                                                                        <td>{item.iaH12 != 0 ? item.iaH12 : "-"}</td>
                                                                                        <td>{item.iaH13 != 0 ? item.iaH13 : "-"}</td>
                                                                                        <td>{item.iaH14 != 0 ? item.iaH14 : "-"}</td>
                                                                                        <td>{item.iaH15 != 0 ? item.iaH15 : "-"}</td>
                                                                                        <td>{item.iaH16 != 0 ? item.iaH16 : "-"}</td>
                                                                                        <td>{item.iaH17 != 0 ? item.iaH17 : "-"}</td>
                                                                                        <td>{item.iaH18 != 0 ? item.iaH18 : "-"}</td>
                                                                                        <td>{item.iaH19 != 0 ? item.iaH19 : "-"}</td>
                                                                                        <td>{item.iaH20 != 0 ? item.iaH20 : "-"}</td>
                                                                                        <td>{item.iaH21 != 0 ? item.iaH21 : "-"}</td>
                                                                                        <td>{item.iaH22 != 0 ? item.iaH22 : "-"}</td>
                                                                                        <td>{item.iaH23 != 0 ? item.iaH23 : "-"}</td>
                                                                                        <td>{item.iaH24 != 0 ? item.iaH24 : "-"}</td>
                                                                                        <td>{item.iaH25 != 0 ? item.iaH25 : "-"}</td>
                                                                                        <td>{item.iaH26 != 0 ? item.iaH26 : "-"}</td>
                                                                                        <td>{item.iaH27 != 0 ? item.iaH27 : "-"}</td>
                                                                                        <td>{item.iaH28 != 0 ? item.iaH28 : "-"}</td>
                                                                                        <td>{item.iaH29 != 0 ? item.iaH29 : "-"}</td>
                                                                                        <td>{item.iaH30 != 0 ? item.iaH30 : "-"}</td>
                                                                                        <td>{item.iaH31 != 0 ? item.iaH31 : "-"}</td>
                                                                                    </>
                                                                                }
                                                                                {valueRadioA == 2 &&
                                                                                    <>
                                                                                        <td className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                        <td >{item.thdIb != 0 ? item.thdIb : "-"}</td>
                                                                                        <td>{item.ibH1 != 0 ? item.ibH1 : "-"}</td>
                                                                                        <td>{item.ibH2 != 0 ? item.ibH2 : "-"}</td>
                                                                                        <td>{item.ibH3 != 0 ? item.ibH3 : "-"}</td>
                                                                                        <td>{item.ibH4 != 0 ? item.ibH4 : "-"}</td>
                                                                                        <td>{item.ibH5 != 0 ? item.ibH5 : "-"}</td>
                                                                                        <td>{item.ibH6 != 0 ? item.ibH6 : "-"}</td>
                                                                                        <td>{item.ibH7 != 0 ? item.ibH7 : "-"}</td>
                                                                                        <td>{item.ibH8 != 0 ? item.ibH8 : "-"}</td>
                                                                                        <td>{item.ibH9 != 0 ? item.ibH9 : "-"}</td>
                                                                                        <td>{item.ibH10 != 0 ? item.ibH10 : "-"}</td>
                                                                                        <td>{item.ibH11 != 0 ? item.ibH11 : "-"}</td>
                                                                                        <td>{item.ibH12 != 0 ? item.ibH12 : "-"}</td>
                                                                                        <td>{item.ibH13 != 0 ? item.ibH13 : "-"}</td>
                                                                                        <td>{item.ibH14 != 0 ? item.ibH14 : "-"}</td>
                                                                                        <td>{item.ibH15 != 0 ? item.ibH15 : "-"}</td>
                                                                                        <td>{item.ibH16 != 0 ? item.ibH16 : "-"}</td>
                                                                                        <td>{item.ibH17 != 0 ? item.ibH17 : "-"}</td>
                                                                                        <td>{item.ibH18 != 0 ? item.ibH18 : "-"}</td>
                                                                                        <td>{item.ibH19 != 0 ? item.ibH19 : "-"}</td>
                                                                                        <td>{item.ibH20 != 0 ? item.ibH20 : "-"}</td>
                                                                                        <td>{item.ibH21 != 0 ? item.ibH21 : "-"}</td>
                                                                                        <td>{item.ibH22 != 0 ? item.ibH22 : "-"}</td>
                                                                                        <td>{item.ibH23 != 0 ? item.ibH23 : "-"}</td>
                                                                                        <td>{item.ibH24 != 0 ? item.ibH24 : "-"}</td>
                                                                                        <td>{item.ibH25 != 0 ? item.ibH25 : "-"}</td>
                                                                                        <td>{item.ibH26 != 0 ? item.ibH26 : "-"}</td>
                                                                                        <td>{item.ibH27 != 0 ? item.ibH27 : "-"}</td>
                                                                                        <td>{item.ibH28 != 0 ? item.ibH28 : "-"}</td>
                                                                                        <td>{item.ibH29 != 0 ? item.ibH29 : "-"}</td>
                                                                                        <td>{item.ibH30 != 0 ? item.ibH30 : "-"}</td>
                                                                                        <td>{item.ibH31 != 0 ? item.ibH31 : "-"}</td>
                                                                                    </>
                                                                                }
                                                                                {valueRadioA == 3 &&
                                                                                    <>
                                                                                        <td className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                        <td >{item.thdIc != 0 ? item.thdIc : "-"}</td>
                                                                                        <td>{item.icH1 != 0 ? item.icH1 : "-"}</td>
                                                                                        <td>{item.icH2 != 0 ? item.icH2 : "-"}</td>
                                                                                        <td>{item.icH3 != 0 ? item.icH3 : "-"}</td>
                                                                                        <td>{item.icH4 != 0 ? item.icH4 : "-"}</td>
                                                                                        <td>{item.icH5 != 0 ? item.icH5 : "-"}</td>
                                                                                        <td>{item.icH6 != 0 ? item.icH6 : "-"}</td>
                                                                                        <td>{item.icH7 != 0 ? item.icH7 : "-"}</td>
                                                                                        <td>{item.icH8 != 0 ? item.icH8 : "-"}</td>
                                                                                        <td>{item.icH9 != 0 ? item.icH9 : "-"}</td>
                                                                                        <td>{item.icH10 != 0 ? item.icH10 : "-"}</td>
                                                                                        <td>{item.icH11 != 0 ? item.icH11 : "-"}</td>
                                                                                        <td>{item.icH12 != 0 ? item.icH12 : "-"}</td>
                                                                                        <td>{item.icH13 != 0 ? item.icH13 : "-"}</td>
                                                                                        <td>{item.icH14 != 0 ? item.icH14 : "-"}</td>
                                                                                        <td>{item.icH15 != 0 ? item.icH15 : "-"}</td>
                                                                                        <td>{item.icH16 != 0 ? item.icH16 : "-"}</td>
                                                                                        <td>{item.icH17 != 0 ? item.icH17 : "-"}</td>
                                                                                        <td>{item.icH18 != 0 ? item.icH18 : "-"}</td>
                                                                                        <td>{item.icH19 != 0 ? item.icH19 : "-"}</td>
                                                                                        <td>{item.icH20 != 0 ? item.icH20 : "-"}</td>
                                                                                        <td>{item.icH21 != 0 ? item.icH21 : "-"}</td>
                                                                                        <td>{item.icH22 != 0 ? item.icH22 : "-"}</td>
                                                                                        <td>{item.icH23 != 0 ? item.icH23 : "-"}</td>
                                                                                        <td>{item.icH24 != 0 ? item.icH24 : "-"}</td>
                                                                                        <td>{item.icH25 != 0 ? item.icH25 : "-"}</td>
                                                                                        <td>{item.icH26 != 0 ? item.icH26 : "-"}</td>
                                                                                        <td>{item.icH27 != 0 ? item.icH27 : "-"}</td>
                                                                                        <td>{item.icH28 != 0 ? item.icH28 : "-"}</td>
                                                                                        <td>{item.icH29 != 0 ? item.icH29 : "-"}</td>
                                                                                        <td>{item.icH30 != 0 ? item.icH30 : "-"}</td>
                                                                                        <td>{item.icH31 != 0 ? item.icH31 : "-"}</td>
                                                                                    </>
                                                                                }
                                                                            </tr>

                                                                        ))
                                                                        }
                                                                    </>
                                                                }
                                                                {valueRadio == 9 &&
                                                                    <>
                                                                        {listDataInstanceFrame2Table?.map((item, index) => (
                                                                            <tr key={index}>
                                                                                {valueRadioVol == 4 &&
                                                                                    <>
                                                                                        <td className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                        <td>{item.thdVab != 0 ? item.thdVab : "-"}</td>
                                                                                        <td>{item.vabH1 != 0 ? item.vabH1 : "-"}</td>
                                                                                        <td>{item.vabH2 != 0 ? item.vabH2 : "-"}</td>
                                                                                        <td>{item.vabH3 != 0 ? item.vabH3 : "-"}</td>
                                                                                        <td>{item.vabH4 != 0 ? item.vabH4 : "-"}</td>
                                                                                        <td>{item.vabH5 != 0 ? item.vabH5 : "-"}</td>
                                                                                        <td>{item.vabH6 != 0 ? item.vabH6 : "-"}</td>
                                                                                        <td>{item.vabH7 != 0 ? item.vabH7 : "-"}</td>
                                                                                        <td>{item.vabH8 != 0 ? item.vabH8 : "-"}</td>
                                                                                        <td>{item.vabH9 != 0 ? item.vabH9 : "-"}</td>
                                                                                        <td>{item.vabH10 != 0 ? item.vabH10 : "-"}</td>
                                                                                        <td>{item.vabH11 != 0 ? item.vabH11 : "-"}</td>
                                                                                        <td>{item.vabH12 != 0 ? item.vabH12 : "-"}</td>
                                                                                        <td>{item.vabH13 != 0 ? item.vabH13 : "-"}</td>
                                                                                        <td>{item.vabH14 != 0 ? item.vabH14 : "-"}</td>
                                                                                        <td>{item.vabH15 != 0 ? item.vabH15 : "-"}</td>
                                                                                        <td>{item.vabH16 != 0 ? item.vabH16 : "-"}</td>
                                                                                        <td>{item.vabH17 != 0 ? item.vabH17 : "-"}</td>
                                                                                        <td>{item.vabH18 != 0 ? item.vabH18 : "-"}</td>
                                                                                        <td>{item.vabH19 != 0 ? item.vabH19 : "-"}</td>
                                                                                        <td>{item.vabH20 != 0 ? item.vabH20 : "-"}</td>
                                                                                        <td>{item.vabH21 != 0 ? item.vabH21 : "-"}</td>
                                                                                        <td>{item.vabH22 != 0 ? item.vabH22 : "-"}</td>
                                                                                        <td>{item.vabH23 != 0 ? item.vabH23 : "-"}</td>
                                                                                        <td>{item.vabH24 != 0 ? item.vabH24 : "-"}</td>
                                                                                        <td>{item.vabH25 != 0 ? item.vabH25 : "-"}</td>
                                                                                        <td>{item.vabH26 != 0 ? item.vabH26 : "-"}</td>
                                                                                        <td>{item.vabH27 != 0 ? item.vabH27 : "-"}</td>
                                                                                        <td>{item.vabH28 != 0 ? item.vabH28 : "-"}</td>
                                                                                        <td>{item.vabH29 != 0 ? item.vabH29 : "-"}</td>
                                                                                        <td>{item.vabH30 != 0 ? item.vabH30 : "-"}</td>
                                                                                        <td>{item.vabH31 != 0 ? item.vabH31 : "-"}</td>
                                                                                    </>
                                                                                }
                                                                                {valueRadioVol == 5 &&
                                                                                    <>
                                                                                        <td className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                        <td>{item.thdVbc != 0 ? item.thdVbc : "-"}</td>
                                                                                        <td>{item.vbcH1 != 0 ? item.vbcH1 : "-"}</td>
                                                                                        <td>{item.vbcH2 != 0 ? item.vbcH2 : "-"}</td>
                                                                                        <td>{item.vbcH3 != 0 ? item.vbcH3 : "-"}</td>
                                                                                        <td>{item.vbcH4 != 0 ? item.vbcH4 : "-"}</td>
                                                                                        <td>{item.vbcH5 != 0 ? item.vbcH5 : "-"}</td>
                                                                                        <td>{item.vbcH6 != 0 ? item.vbcH6 : "-"}</td>
                                                                                        <td>{item.vbcH7 != 0 ? item.vbcH7 : "-"}</td>
                                                                                        <td>{item.vbcH8 != 0 ? item.vbcH8 : "-"}</td>
                                                                                        <td>{item.vbcH9 != 0 ? item.vbcH9 : "-"}</td>
                                                                                        <td>{item.vbcH10 != 0 ? item.vbcH10 : "-"}</td>
                                                                                        <td>{item.vbcH11 != 0 ? item.vbcH11 : "-"}</td>
                                                                                        <td>{item.vbcH12 != 0 ? item.vbcH12 : "-"}</td>
                                                                                        <td>{item.vbcH13 != 0 ? item.vbcH13 : "-"}</td>
                                                                                        <td>{item.vbcH14 != 0 ? item.vbcH14 : "-"}</td>
                                                                                        <td>{item.vbcH15 != 0 ? item.vbcH15 : "-"}</td>
                                                                                        <td>{item.vbcH16 != 0 ? item.vbcH16 : "-"}</td>
                                                                                        <td>{item.vbcH17 != 0 ? item.vbcH17 : "-"}</td>
                                                                                        <td>{item.vbcH18 != 0 ? item.vbcH18 : "-"}</td>
                                                                                        <td>{item.vbcH19 != 0 ? item.vbcH19 : "-"}</td>
                                                                                        <td>{item.vbcH20 != 0 ? item.vbcH20 : "-"}</td>
                                                                                        <td>{item.vbcH21 != 0 ? item.vbcH21 : "-"}</td>
                                                                                        <td>{item.vbcH22 != 0 ? item.vbcH22 : "-"}</td>
                                                                                        <td>{item.vbcH23 != 0 ? item.vbcH23 : "-"}</td>
                                                                                        <td>{item.vbcH24 != 0 ? item.vbcH24 : "-"}</td>
                                                                                        <td>{item.vbcH25 != 0 ? item.vbcH25 : "-"}</td>
                                                                                        <td>{item.vbcH26 != 0 ? item.vbcH26 : "-"}</td>
                                                                                        <td>{item.vbcH27 != 0 ? item.vbcH27 : "-"}</td>
                                                                                        <td>{item.vbcH28 != 0 ? item.vbcH28 : "-"}</td>
                                                                                        <td>{item.vbcH29 != 0 ? item.vbcH29 : "-"}</td>
                                                                                        <td>{item.vbcH30 != 0 ? item.vbcH30 : "-"}</td>
                                                                                        <td>{item.vbcH31 != 0 ? item.vbcH31 : "-"}</td>
                                                                                    </>
                                                                                }
                                                                                {valueRadioVol == 6 &&
                                                                                    <>
                                                                                        <td className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                        <td>{item.thdVca != 0 ? item.thdVca : "-"}</td>
                                                                                        <td>{item.vcaH1 != 0 ? item.vcaH1 : "-"}</td>
                                                                                        <td>{item.vcaH2 != 0 ? item.vcaH2 : "-"}</td>
                                                                                        <td>{item.vcaH3 != 0 ? item.vcaH3 : "-"}</td>
                                                                                        <td>{item.vcaH4 != 0 ? item.vcaH4 : "-"}</td>
                                                                                        <td>{item.vcaH5 != 0 ? item.vcaH5 : "-"}</td>
                                                                                        <td>{item.vcaH6 != 0 ? item.vcaH6 : "-"}</td>
                                                                                        <td>{item.vcaH7 != 0 ? item.vcaH7 : "-"}</td>
                                                                                        <td>{item.vcaH8 != 0 ? item.vcaH8 : "-"}</td>
                                                                                        <td>{item.vcaH9 != 0 ? item.vcaH9 : "-"}</td>
                                                                                        <td>{item.vcaH10 != 0 ? item.vcaH10 : "-"}</td>
                                                                                        <td>{item.vcaH11 != 0 ? item.vcaH11 : "-"}</td>
                                                                                        <td>{item.vcaH12 != 0 ? item.vcaH12 : "-"}</td>
                                                                                        <td>{item.vcaH13 != 0 ? item.vcaH13 : "-"}</td>
                                                                                        <td>{item.vcaH14 != 0 ? item.vcaH14 : "-"}</td>
                                                                                        <td>{item.vcaH15 != 0 ? item.vcaH15 : "-"}</td>
                                                                                        <td>{item.vcaH16 != 0 ? item.vcaH16 : "-"}</td>
                                                                                        <td>{item.vcaH17 != 0 ? item.vcaH17 : "-"}</td>
                                                                                        <td>{item.vcaH18 != 0 ? item.vcaH18 : "-"}</td>
                                                                                        <td>{item.vcaH19 != 0 ? item.vcaH19 : "-"}</td>
                                                                                        <td>{item.vcaH20 != 0 ? item.vcaH20 : "-"}</td>
                                                                                        <td>{item.vcaH21 != 0 ? item.vcaH21 : "-"}</td>
                                                                                        <td>{item.vcaH22 != 0 ? item.vcaH22 : "-"}</td>
                                                                                        <td>{item.vcaH23 != 0 ? item.vcaH23 : "-"}</td>
                                                                                        <td>{item.vcaH24 != 0 ? item.vcaH24 : "-"}</td>
                                                                                        <td>{item.vcaH25 != 0 ? item.vcaH25 : "-"}</td>
                                                                                        <td>{item.vcaH26 != 0 ? item.vcaH26 : "-"}</td>
                                                                                        <td>{item.vcaH27 != 0 ? item.vcaH27 : "-"}</td>
                                                                                        <td>{item.vcaH28 != 0 ? item.vcaH28 : "-"}</td>
                                                                                        <td>{item.vcaH29 != 0 ? item.vcaH29 : "-"}</td>
                                                                                        <td>{item.vcaH30 != 0 ? item.vcaH30 : "-"}</td>
                                                                                        <td>{item.vcaH31 != 0 ? item.vcaH31 : "-"}</td>
                                                                                    </>
                                                                                }
                                                                                {valueRadioVol == 1 &&
                                                                                    <>
                                                                                        <td className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                        <td>{item.thdVan != 0 ? item.thdVan : "-"}</td>
                                                                                        <td>{item.vanH1 != 0 ? item.vanH1 : "-"}</td>
                                                                                        <td>{item.vanH2 != 0 ? item.vanH2 : "-"}</td>
                                                                                        <td>{item.vanH3 != 0 ? item.vanH3 : "-"}</td>
                                                                                        <td>{item.vanH4 != 0 ? item.vanH4 : "-"}</td>
                                                                                        <td>{item.vanH5 != 0 ? item.vanH5 : "-"}</td>
                                                                                        <td>{item.vanH6 != 0 ? item.vanH6 : "-"}</td>
                                                                                        <td>{item.vanH7 != 0 ? item.vanH7 : "-"}</td>
                                                                                        <td>{item.vanH8 != 0 ? item.vanH8 : "-"}</td>
                                                                                        <td>{item.vanH9 != 0 ? item.vanH9 : "-"}</td>
                                                                                        <td>{item.vanH10 != 0 ? item.vanH10 : "-"}</td>
                                                                                        <td>{item.vanH11 != 0 ? item.vanH11 : "-"}</td>
                                                                                        <td>{item.vanH12 != 0 ? item.vanH12 : "-"}</td>
                                                                                        <td>{item.vanH13 != 0 ? item.vanH13 : "-"}</td>
                                                                                        <td>{item.vanH14 != 0 ? item.vanH14 : "-"}</td>
                                                                                        <td>{item.vanH15 != 0 ? item.vanH15 : "-"}</td>
                                                                                        <td>{item.vanH16 != 0 ? item.vanH16 : "-"}</td>
                                                                                        <td>{item.vanH17 != 0 ? item.vanH17 : "-"}</td>
                                                                                        <td>{item.vanH18 != 0 ? item.vanH18 : "-"}</td>
                                                                                        <td>{item.vanH19 != 0 ? item.vanH19 : "-"}</td>
                                                                                        <td>{item.vanH20 != 0 ? item.vanH20 : "-"}</td>
                                                                                        <td>{item.vanH21 != 0 ? item.vanH21 : "-"}</td>
                                                                                        <td>{item.vanH22 != 0 ? item.vanH22 : "-"}</td>
                                                                                        <td>{item.vanH23 != 0 ? item.vanH23 : "-"}</td>
                                                                                        <td>{item.vanH24 != 0 ? item.vanH24 : "-"}</td>
                                                                                        <td>{item.vanH25 != 0 ? item.vanH25 : "-"}</td>
                                                                                        <td>{item.vanH26 != 0 ? item.vanH26 : "-"}</td>
                                                                                        <td>{item.vanH27 != 0 ? item.vanH27 : "-"}</td>
                                                                                        <td>{item.vanH28 != 0 ? item.vanH28 : "-"}</td>
                                                                                        <td>{item.vanH29 != 0 ? item.vanH29 : "-"}</td>
                                                                                        <td>{item.vanH30 != 0 ? item.vanH30 : "-"}</td>
                                                                                        <td>{item.vanH31 != 0 ? item.vanH31 : "-"}</td>
                                                                                    </>
                                                                                }
                                                                                {valueRadioVol == 2 &&
                                                                                    <>
                                                                                        <td className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                        <td>{item.thdVbn != 0 ? item.thdVbn : "-"}</td>
                                                                                        <td>{item.vbnH1 != 0 ? item.vbnH1 : "-"}</td>
                                                                                        <td>{item.vbnH2 != 0 ? item.vbnH2 : "-"}</td>
                                                                                        <td>{item.vbnH3 != 0 ? item.vbnH3 : "-"}</td>
                                                                                        <td>{item.vbnH4 != 0 ? item.vbnH4 : "-"}</td>
                                                                                        <td>{item.vbnH5 != 0 ? item.vbnH5 : "-"}</td>
                                                                                        <td>{item.vbnH6 != 0 ? item.vbnH6 : "-"}</td>
                                                                                        <td>{item.vbnH7 != 0 ? item.vbnH7 : "-"}</td>
                                                                                        <td>{item.vbnH8 != 0 ? item.vbnH8 : "-"}</td>
                                                                                        <td>{item.vbnH9 != 0 ? item.vbnH9 : "-"}</td>
                                                                                        <td>{item.vbnH10 != 0 ? item.vbnH10 : "-"}</td>
                                                                                        <td>{item.vbnH11 != 0 ? item.vbnH11 : "-"}</td>
                                                                                        <td>{item.vbnH12 != 0 ? item.vbnH12 : "-"}</td>
                                                                                        <td>{item.vbnH13 != 0 ? item.vbnH13 : "-"}</td>
                                                                                        <td>{item.vbnH14 != 0 ? item.vbnH14 : "-"}</td>
                                                                                        <td>{item.vbnH15 != 0 ? item.vbnH15 : "-"}</td>
                                                                                        <td>{item.vbnH16 != 0 ? item.vbnH16 : "-"}</td>
                                                                                        <td>{item.vbnH17 != 0 ? item.vbnH17 : "-"}</td>
                                                                                        <td>{item.vbnH18 != 0 ? item.vbnH18 : "-"}</td>
                                                                                        <td>{item.vbnH19 != 0 ? item.vbnH19 : "-"}</td>
                                                                                        <td>{item.vbnH20 != 0 ? item.vbnH20 : "-"}</td>
                                                                                        <td>{item.vbnH21 != 0 ? item.vbnH21 : "-"}</td>
                                                                                        <td>{item.vbnH22 != 0 ? item.vbnH22 : "-"}</td>
                                                                                        <td>{item.vbnH23 != 0 ? item.vbnH23 : "-"}</td>
                                                                                        <td>{item.vbnH24 != 0 ? item.vbnH24 : "-"}</td>
                                                                                        <td>{item.vbnH25 != 0 ? item.vbnH25 : "-"}</td>
                                                                                        <td>{item.vbnH26 != 0 ? item.vbnH26 : "-"}</td>
                                                                                        <td>{item.vbnH27 != 0 ? item.vbnH27 : "-"}</td>
                                                                                        <td>{item.vbnH28 != 0 ? item.vbnH28 : "-"}</td>
                                                                                        <td>{item.vbnH29 != 0 ? item.vbnH29 : "-"}</td>
                                                                                        <td>{item.vbnH30 != 0 ? item.vbnH30 : "-"}</td>
                                                                                        <td>{item.vbnH31 != 0 ? item.vbnH31 : "-"}</td>
                                                                                    </>
                                                                                }
                                                                                {valueRadioVol == 3 &&
                                                                                    <>
                                                                                        <td className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                        <td>{item.thdVcn != 0 ? item.thdVcn : "-"}</td>
                                                                                        <td>{item.vcnH1 != 0 ? item.vcnH1 : "-"}</td>
                                                                                        <td>{item.vcnH2 != 0 ? item.vcnH2 : "-"}</td>
                                                                                        <td>{item.vcnH3 != 0 ? item.vcnH3 : "-"}</td>
                                                                                        <td>{item.vcnH4 != 0 ? item.vcnH4 : "-"}</td>
                                                                                        <td>{item.vcnH5 != 0 ? item.vcnH5 : "-"}</td>
                                                                                        <td>{item.vcnH6 != 0 ? item.vcnH6 : "-"}</td>
                                                                                        <td>{item.vcnH7 != 0 ? item.vcnH7 : "-"}</td>
                                                                                        <td>{item.vcnH8 != 0 ? item.vcnH8 : "-"}</td>
                                                                                        <td>{item.vcnH9 != 0 ? item.vcnH9 : "-"}</td>
                                                                                        <td>{item.vcnH10 != 0 ? item.vcnH10 : "-"}</td>
                                                                                        <td>{item.vcnH11 != 0 ? item.vcnH11 : "-"}</td>
                                                                                        <td>{item.vcnH12 != 0 ? item.vcnH12 : "-"}</td>
                                                                                        <td>{item.vcnH13 != 0 ? item.vcnH13 : "-"}</td>
                                                                                        <td>{item.vcnH14 != 0 ? item.vcnH14 : "-"}</td>
                                                                                        <td>{item.vcnH15 != 0 ? item.vcnH15 : "-"}</td>
                                                                                        <td>{item.vcnH16 != 0 ? item.vcnH16 : "-"}</td>
                                                                                        <td>{item.vcnH17 != 0 ? item.vcnH17 : "-"}</td>
                                                                                        <td>{item.vcnH18 != 0 ? item.vcnH18 : "-"}</td>
                                                                                        <td>{item.vcnH19 != 0 ? item.vcnH19 : "-"}</td>
                                                                                        <td>{item.vcnH20 != 0 ? item.vcnH20 : "-"}</td>
                                                                                        <td>{item.vcnH21 != 0 ? item.vcnH21 : "-"}</td>
                                                                                        <td>{item.vcnH22 != 0 ? item.vcnH22 : "-"}</td>
                                                                                        <td>{item.vcnH23 != 0 ? item.vcnH23 : "-"}</td>
                                                                                        <td>{item.vcnH24 != 0 ? item.vcnH24 : "-"}</td>
                                                                                        <td>{item.vcnH25 != 0 ? item.vcnH25 : "-"}</td>
                                                                                        <td>{item.vcnH26 != 0 ? item.vcnH26 : "-"}</td>
                                                                                        <td>{item.vcnH27 != 0 ? item.vcnH27 : "-"}</td>
                                                                                        <td>{item.vcnH28 != 0 ? item.vcnH28 : "-"}</td>
                                                                                        <td>{item.vcnH29 != 0 ? item.vcnH29 : "-"}</td>
                                                                                        <td>{item.vcnH30 != 0 ? item.vcnH30 : "-"}</td>
                                                                                        <td>{item.vcnH31 != 0 ? item.vcnH31 : "-"}</td>
                                                                                    </>
                                                                                }
                                                                            </tr>

                                                                        ))
                                                                        }
                                                                    </>
                                                                }
                                                                {valueRadio == 14 &&
                                                                    <>
                                                                        {listDataInstanceFrame1Table?.map((item, index) => (
                                                                            <tr key={index}>
                                                                                <>
                                                                                    <td style={{ width: "10%" }} className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                    <td>{item.status === 0 ? "ONLINE" : "OFFLINE"}</td>
                                                                                </>
                                                                            </tr>
                                                                        ))
                                                                        }
                                                                    </>
                                                                }
                                                                {valueRadio == 15 &&
                                                                    <>
                                                                        {listDataInstanceFrame1Table?.map((item, index) => (
                                                                            <tr key={index}>
                                                                                <td style={{ width: "10%" }} className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                <td>{item.t != null ? item.t : "-"}</td>
                                                                            </tr>
                                                                        ))
                                                                        }
                                                                    </>
                                                                }
                                                                {valueRadio == 16 &&
                                                                    <>
                                                                        {listDataInstanceFrame1Table?.map((item, index) => (
                                                                            <tr key={index}>
                                                                                <td style={{ width: "10%" }} className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                <td>{item.taccumulationDay != null ? item.taccumulationDay : "-"}</td>
                                                                            </tr>
                                                                        ))
                                                                        }
                                                                    </>
                                                                }
                                                                {valueRadio == 17 &&
                                                                    <>
                                                                        {listDataInstanceFrame1Table?.map((item, index) => (
                                                                            <tr key={index}>
                                                                                <td style={{ width: "10%" }} className="text-center">{moment(new Date(item.sendDate)).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                                <td>{item.p != null ? item.p : "-"}</td>
                                                                            </tr>
                                                                        ))
                                                                        }
                                                                    </>
                                                                }
                                                            </>
                                                        }
                                                    </tbody>
                                                </table>
                                            </div>
                                        }
                                    </div>
                                </>
                            }
                            {
                                equipmentTab === 3 &&
                                <div>
                                    {
                                        <WarningLoad customerId={param.customerId} projectId={param.projectId} systemTypeId={iDevice.systemTypeId} deviceId={iDevice.deviceId}></WarningLoad>
                                    }
                                </div>

                            }
                        </div >

                        <div className="modal fade" id="myModal" tabIndex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                            <div className="modal-dialog modal-xl">
                                <div className="modal-content">
                                    <div className="modal-header">
                                        <h4 className="modal-title" style={{ fontWeight: "bold" }} id="myModalLabel">BIỂU ĐỒ {chartName}
                                            <br /> {iDevice.deviceName}<br />
                                            <div style={{ fontSize: "13px", fontWeight: "none" }}>
                                                {isActiveButton == 1 &&
                                                    <>
                                                        Ngày {moment(new Date(fromDate)).format("DD-MM-YYYY")}
                                                    </>}
                                                {isActiveButton == 2 &&
                                                    <>
                                                        Tháng {moment(new Date(fromDate)).format("MM-YYYY")}
                                                    </>}
                                                {isActiveButton == 3 &&
                                                    <>
                                                        Năm {moment(new Date(fromDate)).format("YYYY")}
                                                    </>}
                                                {isActiveButton == 4 &&
                                                    <>
                                                        Từ {moment(new Date(fromDate)).format("DD-MM-YYYY")} tới ngày {moment(new Date(toDate)).format("DD-MM-DD")}
                                                    </>}
                                            </div></h4>
                                        <button type="button" className="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span className="sr-only">Close</span></button>
                                    </div>
                                    <div className="modal-body">
                                        <div id="chartdiv-device" style={{ width: "100%", height: "600px" }}></div>
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div >
                </>
                :
                <AccessDenied></AccessDenied>
            }
        </>
    )

}

export default Information;