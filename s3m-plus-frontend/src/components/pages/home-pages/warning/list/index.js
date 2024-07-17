import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import moment from "moment/moment";
import WarningService from "../../../../../services/WarningService";
import CONS from "../../../../../constants/constant";
import ReactModal from "react-modal";
import AuthService from "../../../../../services/AuthService";
import { useFormik } from 'formik';
import { Link, useLocation } from "react-router-dom";
import { Calendar } from 'primereact/calendar';
import Pagination from "react-js-pagination";
import converter from "../../../../../common/converter";
import authService from "../../../../../services/AuthService";
import { useTranslation } from "react-i18next";
import * as am5 from "@amcharts/amcharts5";
import * as am5xy from "@amcharts/amcharts5/xy";
import am5themes_Animated from "@amcharts/amcharts5/themes/Animated";
import { useRef } from "react";
import { useDownloadExcel } from "react-export-table-to-excel";
import UserService from "../../../../../services/UserService";
import WarningCarService from "../../../../../services/WarningCarService";
import { NotficationError, NotficationInfo, NotficationSuscces, NotficationWarning } from "../../notification/notification";
import { ToastContainer } from "react-toastify";
import SettingService from "../../../../../services/SettingService";
import CustomerService from "../../../../../services/CustomerService";
import ProjectService from "../../../../../services/ProjectService";
const $ = window.$;

const WarningLoad = ({ customerId, projectId, systemTypeId, deviceId, updateCountStatus }) => {

    const $ = window.$;
    const param = useParams();
    const [fromDate, setFromDate] = useState(new Date());
    const [toDate, setToDate] = useState(new Date());
    const [totalPage, setTotalPage] = useState(1);
    const [viewTypeModal, setViewTypeModal] = useState(null);
    const [settingValue, setSettingValue] = useState(null);
    const [isActiveButton, setIsActiveButton] = useState(true);

    const [display, setDisplay] = useState(false)
    const [valueTime, setValueTime] = useState(0);
    const { t } = useTranslation();
    const [warnedDevice, setWarnedDevice] = useState([]);
    const [searchWarnedDevice, setSearchWarnedDevice] = useState([]);
    const [tableOrChart, setTableOrChart] = useState(0);
    const [listWarning, setListWarning] = useState([]);
    const [listWarningFrame2, setListWarningFrame2] = useState([]);
    const [selectedWarningType, setSelectedWarningType] = useState();
    const [role] = useState(AuthService.getRoleName());
    const [userName] = useState(AuthService.getUserName());
    const [accessDenied, setAccessDenied] = useState(false)
    const [device, setDevice] = useState({
        deviceId: "",
        deviceType: "",
        warningType: "",
        inforWarning: ""
    });
    // total warning state
    const [warnings, setWarnings] = useState({
        nguongApCao: 0,
        nguongApThap: 0,
        quaTai: 0,
        heSoCongSuatThap: 0,
        tanSoCao: 0,
        tanSoThap: 0,
        lechPha: 0,
        songHai: 0,
        nguocPha: 0,
        matDienTong: 0,
        nhietDoCao: 0,
        nhietDoThap: 0,
        matKetNoiAC: 0,
        matKetNoiDC: 0,
        dienApCaoAC: 0,
        dienApThapAC: 0,
        tanSoThap: 0,
        tanSoCao: 0,
        matNguonLuoi: 0,
        chamDat: 0,
        hongCauChi: 0,
        dongMoCua: 0,
        dienApCaoDC: 0,
        matBoNho: 0,
        doAm: 0,
        FITuRMU: 0,
        khoangTonThat: 0,
        mucDauThap: 0,
        roleGas: 0,
        chamVo: 0,
        mucDauCao: 0,
        camBienHongNgoai: 0,
        apSuatNoiBoMBA: 0,
        roleNhietDoDau: 0,
        nhietDoCuonDay: 0,
        khiGasMBA: 0,
        phongDien: 0
    });
    const [typeNameDowload, setTypeNameDowload] = useState("tableEp");
    const tableRef = useRef(null);
    const { onDownload } = useDownloadExcel({
        currentTableRef: tableRef.current,
        filename: typeNameDowload,
        sheet: typeNameDowload
    });

    // Location
    const location = useLocation();

    // active warning state
    const [activeWarning, setActiveWaring] = useState("warning-all");

    // current page state
    const [page, setPage] = useState(1);

    // detail warning
    const [detailWarnings, setDetailWarnings] = useState([]);

    // active modal state
    const [isModalOpen, setIsModalOpen] = useState(false);

    // active modal update warning
    const [isModalUpdateOpen, setIsModalUpdateOpen] = useState(false);

    // update warning state
    const [updateWarning, setUpdateWarning] = useState(null);

    // warning type table
    const [warningType, setWarningType] = useState(0);

    const [createDate, setCreateDate] = useState(moment(new Date()).format("YYYY-MM-DD"));

    const [inforWarning, setInforWarning] = useState({
        deviceName: "",
        warningTypeName: "",
        warningInfo: "",
        value: "-",
        settingValue: "-",
        deviceLevel: "-",
        warningLevel: "-",
        fromDate: "",
        toDate: "",
        priorityFlag: "",
        settingValueHistory: ""

    });
    const [userId, setUserId] = useState();
    const [isModalAddCAROpen, setIsModalAddCAROpen] = useState(false);
    const [dataWarningCarAdd, setDataWarningCarAdd] = useState({
        id: "",
        systemTypeId: "",
        projectId: "",
        deviceId: "",
        status: "",
        createId: "",
        organizationCreate: "",
        content: ""
    });
    const [dataWarningCar, setDataWarningCar] = useState([]);
    const [dataWarningCarUpdate, setDataWarningCarUpdate] = useState([]);
    const [authorUpdateCar, setAuthorUpdateCar] = useState(true);
    const [query, setQuery] = useState("");
    const [isModalCAROpen, setIsModalCAROpen] = useState(false);

    const formik = useFormik({
        initialValues: updateWarning,
        enableReinitialize: true,
        onSubmit: async data => {
            let updateWarningData = {
                id: data.warningId,
                status: data.status,
                description: data.description,
                username: AuthService.getAuth().username
            }
            let res = await WarningService.updateWarningCache(updateWarningData, customerId);
            if (res.status === 200) {
                setIsModalUpdateOpen(false);
                deviceWarning(warningType, activeWarning);
            }
        }
    });

    // data load frame warning by warning type
    const [dataLoadFrameWarning, setDataLoadFrameWarning] = useState({
        page: 1,
        totalPage: 1,
        warningType: null,
        data: []
    });

    const getWarning = async (fromTime, toTime) => {
        let dvId = "";
        if (deviceId != undefined) {
            dvId = deviceId
        }

        let ids = ""
        if (role === "ROLE_ADMIN" || role === "ROLE_MOD") {

        }
        if (role === "ROLE_USER") {
            let re = await ProjectService.getProIds(userName)
            if (re.status === 200 && re.data !== '') {
                ids = re.data
            }
        }

        deviceWarning("ALL", "warning-all", fromTime, toTime, ids);
        let fDate = moment(fromTime).format("YYYY-MM-DD");
        let tDate = moment(toTime).format("YYYY-MM-DD");
        let res = await WarningService.getWarnings(fDate, tDate, projectId, customerId, systemTypeId, dvId, ids);
        if (res.status === 200) {
            setWarnings(res.data);
        }
    }
    const deviceWarning = async (type, idSelector, fromTime, toTime, ids) => {
        $('#warnedDevices').hide();
        $('#warning-loading').show();
        setActiveWaring(idSelector);
        setWarningType(type);
        let fDate = moment(fromTime).format("YYYY-MM-DD");
        let tDate = moment(toTime).format("YYYY-MM-DD");
        let res = await WarningService.getListWarnedDevice(fDate, tDate, projectId, customerId, systemTypeId, type, ids);
        if (res.status === 200) {
            setWarnedDevice(res.data);
            if (deviceId != undefined) {
                let dataR = res.data.filter(data => data.deviceId === deviceId);
                setSearchWarnedDevice(dataR);
            } else {
                setSearchWarnedDevice(res.data)
            }
            $('#warning-loading').hide();
            $('#warnedDevices').show();

        }
    }
    const [dataLostSignal, setDataLostSignal] = useState([]);
    const listDeviceLostSignal = async () => {
        let acustomerId = param.customerId;
        let aprojectId = param.projectId;
        let res = await WarningService.listDeviceLostSignal(acustomerId, aprojectId, systemTypeId);
        if (res.status === 200) {
            setDataLostSignal(res.data);
        }
    }

    const compareToListSetting = (array, warningType) => {
        if (typeof warningType === 'number' && isFinite(warningType)) {
            if (array != undefined) {
                let newArray = array.filter(ob => ob.warningType != warningType);
                let object = array.filter(ob => ob.warningType == warningType)[0];
                newArray.unshift(object)
                return newArray;
            }
        } else {
            let arrayOb = array.filter(ob => warningType.includes(ob.warningType))
            let arrayFind = array.filter(ob => !warningType.includes(ob.warningType));
            let newArray = [...arrayOb, ...arrayFind]
            return newArray;
        }
    }


    const handleChangeView = (isActive) => {
        setIsActiveButton(!isActive)
        setValueTime(() => 1)
        let fromTime = moment(new Date).format("YYYY-MM-DD") + " 00:00:00"
        let toTime = moment(new Date).format("YYYY-MM-DD") + " 23:59:59"
        setFromDate(fromTime)
        setToDate(toTime)
    }

    // const getDataByDate = () => {
    //     if (fromDate > toDate) {
    //         setDisplay(true)
    //     } else {
    //         setDisplay(false)
    //         let fromTime = moment(fromDate).format("YYYY-MM-DD") + " 00:00:00";
    //         let toTime = moment(toDate).format("YYYY-MM-DD") + " 23:59:59";
    //         setFromDate(fromTime)
    //         setToDate(toTime)
    //         //     funcGetWarnings(fromTime, toTime, warningLevel, 1, 1, projectId)
    //         //     funcGetWarningCar(fromTime, toTime, 1, 1, projectId)
    //         getWarning(fromTime, toTime);
    //     }
    // }

    const onChangeValue = async (e) => {
        let time = e.target.value;
        setValueTime(() => e.target.value)
        const today = new Date();
        let fromTime = "";
        let toTime = "";
        if (time == 2) {
            today.setDate(today.getDate() - 1);
            fromTime = moment(today).format("YYYY-MM-DD") + " 00:00:00";
            toTime = moment(today).format("YYYY-MM-DD") + " 23:59:59";
            setFromDate(fromTime)
            setToDate(toTime)

        } else if (time == 3) {
            fromTime = moment(today).format("YYYY-MM") + "-01" + " 00:00:00";
            toTime = moment(today).format("YYYY-MM-DD") + " 23:59:59";
            setFromDate(fromTime)
            setToDate(toTime)

        } else if (time == 4) {
            today.setMonth(today.getMonth() - 1);
            fromTime = moment(today).format("YYYY-MM") + "-01" + " 00:00:00";
            /**Xét ngày cuối tháng trước */
            today.setMonth(today.getMonth() + 1)
            let temp = new Date(today.getFullYear() + "-" + today.getMonth() + "-" + "01")
            today.setDate(temp.getDate() - 1);
            toTime = moment(today).format("YYYY-MM-DD") + " 23:59:59";
            setFromDate(fromTime)
            setToDate(toTime)

        } else if (time == 5) {
            today.setMonth(today.getMonth() - 3);
            fromTime = moment(today).format("YYYY-MM") + "-01" + " 00:00:00";
            /**Xét ngày cuối 3 tháng trước */
            today.setMonth(today.getMonth() + 3)
            let temp = new Date(today.getFullYear() + "-" + today.getMonth() + "-" + "01")
            today.setDate(temp.getDate() - 1);
            toTime = moment(today).format("YYYY-MM-DD") + " 23:59:59";
            setFromDate(fromTime)
            setToDate(toTime)

        } else if (time == 6) {
            today.setMonth(today.getMonth() - 6);
            fromTime = moment(today).format("YYYY-MM") + "-01" + " 00:00:00";
            /**Xét ngày cuối 6 tháng trước */
            today.setMonth(today.getMonth() + 6)
            let temp = new Date(today.getFullYear() + "-" + today.getMonth() + "-" + "01")
            today.setDate(temp.getDate() - 1);
            toTime = moment(today).format("YYYY-MM-DD") + " 23:59:59";
            setFromDate(fromTime)
            setToDate(toTime)

        } else if (time == 7) {
            fromTime = moment(today).format("YYYY") + "-01-01" + " 00:00:00";
            toTime = moment(today).format("YYYY-MM-DD") + " 23:59:59";
            setFromDate(fromTime)
            setToDate(toTime)

        } else if (time == 8) {
            today.setYear(today.getFullYear() - 1);
            fromTime = moment(today).format("YYYY") + "-01-01" + " 00:00:00";
            /**Xét ngày cuối năm ngoái */
            toTime = moment(today).format("YYYY") + "-12-31" + " 23:59:59";
            setFromDate(fromTime)
            setToDate(toTime)

        } else {
            fromTime = moment(today).format("YYYY-MM-DD") + " 00:00:00";
            toTime = moment(today).format("YYYY-MM-DD") + " 23:59:59";
            setFromDate(fromTime)
            setToDate(toTime)
        }
        getWarning(fromTime, toTime);
    }

    const searchDevice = async (e) => {
        let deviceName = e.target.value;
        if (deviceName === "") {
            setSearchWarnedDevice(warnedDevice);
        } else {
            let customerSearch = warnedDevice?.filter(d => d.deviceName.toLowerCase().includes(deviceName.toLowerCase()));
            setSearchWarnedDevice(customerSearch);
        }
    }

    const funcInforWarning = async (warning, _device) => {
        let data = warning;

        let inforDevice = { ...device, deviceId: _device.deviceId, deviceType: _device.deviceType, warningType: warning.warningType }
        data.priorityFlag = _device.priorityFlag;
        let res = await WarningService.getInfoWarnedDevice(customerId, systemTypeId, warning.deviceId, warning.warningType, warning.toDate);
        if (res.status == 200) {
            let dataWarning = res.data.data;
            let setting = res.data.setting;
            let warningType = warning.warningType;
            let settingValue = setting.split(",");
            console.log(dataWarning);
            if (settingValue.length > 1) {
                // thiết bị meter
                if (warningType == 104) {
                    settingValue = settingValue[1];
                } else {
                    settingValue = settingValue[0];
                }
            } else {
                if (warningType == 103) {
                    settingValue = parseFloat(settingValue[0]) * parseFloat(dataWarning.imccb);
                }
            }
            let value = "-";
            let nameWarnings = '';
            if (warningType == CONS.WARNING_TYPE.NGUONG_AP_CAO) {
                value = Math.max(dataWarning.uan, dataWarning.ubn, dataWarning.ucn);
                let selected = ['uan', 'ubn', 'ucn'];
                let filtered = Object.keys(dataWarning)
                    .filter(key => selected.includes(key))
                    .reduce((obj, key) => {
                        obj[key] = dataWarning[key];
                        return obj;
                    }, {});

                for (let key in filtered) {

                    if (filtered[key] > settingValue) {
                        if (nameWarnings == '') {
                            nameWarnings += key.charAt(0).toUpperCase() + key.slice(1);
                        } else {
                            nameWarnings += ' - ' + key.charAt(0).toUpperCase() + key.slice(1);
                        }
                    }
                }
                setDevice({ ...inforDevice, inforWarning: nameWarnings })
                //let nameKey = (Object.keys(filtered).reduce((a, b) => filtered[a] > filtered[b] ? a : b));
            } else if (warningType == CONS.WARNING_TYPE.NGUONG_AP_THAP) {
                value = Math.min(dataWarning.uan, dataWarning.ubn, dataWarning.ucn);
                let selected = ['uan', 'ubn', 'ucn'];
                let filtered = Object.keys(dataWarning)
                    .filter(key => selected.includes(key))
                    .reduce((obj, key) => {
                        obj[key] = dataWarning[key];
                        return obj;
                    }, {});

                for (let key in filtered) {

                    if (filtered[key] < settingValue) {
                        if (nameWarnings == '') {
                            nameWarnings += key.charAt(0).toUpperCase() + key.slice(1);
                        } else {
                            nameWarnings += ' - ' + key.charAt(0).toUpperCase() + key.slice(1);
                        }
                    }
                }
                setDevice({ ...inforDevice, inforWarning: nameWarnings })
            } else if (warningType == CONS.WARNING_TYPE.QUA_TAI) {
                value = Math.max(dataWarning.ia, dataWarning.ib, dataWarning.ic);
                let selected = ['ia', 'ib', 'ic'];
                let filtered = Object.keys(dataWarning)
                    .filter(key => selected.includes(key))
                    .reduce((obj, key) => {
                        obj[key] = dataWarning[key];
                        return obj;
                    }, {});

                for (let key in filtered) {

                    if (filtered[key] >= settingValue) {
                        if (nameWarnings == '') {
                            nameWarnings += key.charAt(0).toUpperCase() + key.slice(1);
                        } else {
                            nameWarnings += ' - ' + key.charAt(0).toUpperCase() + key.slice(1);
                        }
                    }
                }
                setDevice({ ...inforDevice, inforWarning: nameWarnings })
            } else if (warningType == CONS.WARNING_TYPE.HE_SO_CONG_SUAT_THAP) {
                // console.log("hi: ", (((dataWarning.ia + dataWarning.ib + dataWarning.ic) / 3) / dataWarning.in) * 100);
                // console.log("hihi: ", settingValue);
                let value = Math.abs(Math.min(dataWarning.pfa, dataWarning.pfb, dataWarning.pfc));
                if (value < settingValue) {
                    value = Math.abs(Math.min(dataWarning.pfa, dataWarning.pfb, dataWarning.pfc));
                } else {
                    value = ""
                }
                let selected = ['pfa', 'pfb', 'pfc'];
                let filtered = Object.keys(dataWarning)
                    .filter(key => selected.includes(key))
                    .reduce((obj, key) => {
                        obj[key] = dataWarning[key];
                        return obj;
                    }, {});

                for (let key in filtered) {

                    if (filtered[key] < settingValue) {
                        if (nameWarnings == '') {
                            nameWarnings += key.charAt(0).toUpperCase() + key.slice(1);
                        } else {
                            nameWarnings += ' - ' + key.charAt(0).toUpperCase() + key.slice(1);
                        }
                    }
                }
                setDevice({ ...inforDevice, inforWarning: nameWarnings })
            } else if (CONS.WARNING_TYPE.TAN_SO_THAP.includes(warningType)) {
                value = dataWarning.f
                let selected = ['f'];
                let filtered = Object.keys(dataWarning)
                    .filter(key => selected.includes(key))
                    .reduce((obj, key) => {
                        obj[key] = dataWarning[key];
                        return obj;
                    }, {});

                for (let key in filtered) {

                    if (filtered[key] < settingValue) {
                        if (nameWarnings == '') {
                            nameWarnings += key.charAt(0).toUpperCase() + key.slice(1);
                        } else {
                            nameWarnings += ' - ' + key.charAt(0).toUpperCase() + key.slice(1);
                        }
                    }
                }
                setDevice({ ...inforDevice, inforWarning: nameWarnings })
            } else if (CONS.WARNING_TYPE.TAN_SO_CAO.includes(warningType)) {
                value = dataWarning.f
                let selected = ['f'];
                let filtered = Object.keys(dataWarning)
                    .filter(key => selected.includes(key))
                    .reduce((obj, key) => {
                        obj[key] = dataWarning[key];
                        return obj;
                    }, {});

                for (let key in filtered) {

                    if (filtered[key] > settingValue) {
                        if (nameWarnings == '') {
                            nameWarnings += key.charAt(0).toUpperCase() + key.slice(1);
                        } else {
                            nameWarnings += ' - ' + key.charAt(0).toUpperCase() + key.slice(1);
                        }
                    }
                }
                setDevice({ ...inforDevice, inforWarning: nameWarnings })
            } else if (warningType == CONS.WARNING_TYPE.LECH_PHA) {
                value = Math.min(dataWarning.ia, dataWarning.ib, dataWarning.ic);
                let selected = ['ia', 'ib', 'ic'];
                let filtered = Object.keys(dataWarning)
                    .filter(key => selected.includes(key))
                    .reduce((obj, key) => {
                        obj[key] = dataWarning[key];
                        return obj;
                    }, {});

                for (let key in filtered) {

                    if (filtered[key] > settingValue) {
                        if (nameWarnings == '') {
                            nameWarnings += key.charAt(0).toUpperCase() + key.slice(1);
                        } else {
                            nameWarnings += ' - ' + key.charAt(0).toUpperCase() + key.slice(1);
                        }
                    }
                }
                setDevice({ ...inforDevice, inforWarning: nameWarnings })
            } else if (CONS.WARNING_TYPE.SONG_HAI.includes(warningType)) {
                if (warningType == 110) {
                    value = Math.max(dataWarning.thdVan, dataWarning.thdVbn, dataWarning.thdVcn);
                    let selected = ['thdVan', 'thdVbn', 'thdVcn'];
                    let filtered = Object.keys(dataWarning)
                        .filter(key => selected.includes(key))
                        .reduce((obj, key) => {
                            obj[key] = dataWarning[key];
                            return obj;
                        }, {});
                    for (let key in filtered) {

                        if (filtered[key] >= settingValue) {
                            if (nameWarnings == '') {
                                nameWarnings += key.charAt(0).toUpperCase() + key.slice(1);
                            } else {
                                nameWarnings += ' - ' + key.charAt(0).toUpperCase() + key.slice(1);
                            }
                        }
                    }
                    setDevice({ ...inforDevice, inforWarning: nameWarnings })
                } else if (warningType == 111) {
                    value = Math.max(dataWarning.thdIa, dataWarning.thdIb, dataWarning.thdIc);
                    let selected = ['thdIa', 'thdIb', 'thdIc'];
                    let filtered = Object.keys(dataWarning)
                        .filter(key => selected.includes(key))
                        .reduce((obj, key) => {
                            obj[key] = dataWarning[key];
                            return obj;
                        }, {});
                    for (let key in filtered) {

                        if (filtered[key] >= settingValue) {
                            if (nameWarnings == '') {
                                nameWarnings += key.charAt(0).toUpperCase() + key.slice(1);
                            } else {
                                nameWarnings += ' - ' + key.charAt(0).toUpperCase() + key.slice(1);
                            }
                        }
                    }
                    setDevice({ ...inforDevice, inforWarning: nameWarnings })
                }
            } else if (warningType == CONS.WARNING_TYPE.NGUOC_PHA) {
                let value = Math.abs(Math.min(dataWarning.pfa, dataWarning.pfb, dataWarning.pfc));
                if (value < settingValue) {
                    value = Math.abs(Math.min(dataWarning.pfa, dataWarning.pfb, dataWarning.pfc));
                } else {
                    value = ""
                }
                let selected = ['pfa', 'pfb', 'pfc'];
                let filtered = Object.keys(dataWarning)
                    .filter(key => selected.includes(key))
                    .reduce((obj, key) => {
                        obj[key] = dataWarning[key];
                        return obj;
                    }, {});
                for (let key in filtered) {

                    if (filtered[key] < settingValue) {
                        if (nameWarnings == '') {
                            nameWarnings += key.charAt(0).toUpperCase() + key.slice(1);
                        } else {
                            nameWarnings += ' - ' + key.charAt(0).toUpperCase() + key.slice(1);
                        }
                    }
                }

                setDevice({ ...inforDevice, inforWarning: nameWarnings })
            } else if (warningType == CONS.WARNING_TYPE.MAT_DIEN_TONG) {
                value = Math.min(dataWarning.uan, dataWarning.ubn, dataWarning.ucn);
                let selected = ['uan', 'ubn', 'ucn'];
                let filtered = Object.keys(dataWarning)
                    .filter(key => selected.includes(key))
                    .reduce((obj, key) => {
                        obj[key] = dataWarning[key];
                        return obj;
                    }, {});

                for (let key in filtered) {

                    if (filtered[key] <= settingValue) {
                        if (nameWarnings == '') {
                            nameWarnings += key.charAt(0).toUpperCase() + key.slice(1);
                        } else {
                            nameWarnings += ' - ' + key.charAt(0).toUpperCase() + key.slice(1);
                        }
                    }
                }
                setDevice({ ...inforDevice, inforWarning: nameWarnings })
            } else if (warningType == CONS.WARNING_TYPE.SONG_HAI_DIEN_AP_BAC_N) {
                value = 2
            }

            // thiết bị temp - hummidity sensor
            if (CONS.WARNING_TYPE.NHIET_DO_CAO.includes(warningType)) {
                value = dataWarning.t;
                let selected = ['t'];
                let filtered = Object.keys(dataWarning)
                    .filter(key => selected.includes(key))
                    .reduce((obj, key) => {
                        obj[key] = dataWarning[key];
                        return obj;
                    }, {});

                for (let key in filtered) {

                    if (filtered[key] >= settingValue) {
                        if (nameWarnings == '') {
                            nameWarnings += key.charAt(0).toUpperCase() + key.slice(1);
                        } else {
                            nameWarnings += ' - ' + key.charAt(0).toUpperCase() + key.slice(1);
                        }
                    }
                }
                setDevice({ ...inforDevice, inforWarning: nameWarnings })
            } else if (CONS.WARNING_TYPE.NHIET_DO_THAP.includes(warningType)) {
                value = dataWarning.t;
                let selected = ['t'];
                let filtered = Object.keys(dataWarning)
                    .filter(key => selected.includes(key))
                    .reduce((obj, key) => {
                        obj[key] = dataWarning[key];
                        return obj;
                    }, {});

                for (let key in filtered) {

                    if (filtered[key] <= settingValue) {
                        if (nameWarnings == '') {
                            nameWarnings += key.charAt(0).toUpperCase() + key.slice(1);
                        } else {
                            nameWarnings += ' - ' + key.charAt(0).toUpperCase() + key.slice(1);
                        }
                    }
                }
                setDevice({ ...inforDevice, inforWarning: nameWarnings })
            } else if (CONS.WARNING_TYPE.DO_AM.includes(warningType)) {
                value = dataWarning.h;
                let selected = ['h'];
                let filtered = Object.keys(dataWarning)
                    .filter(key => selected.includes(key))
                    .reduce((obj, key) => {
                        obj[key] = dataWarning[key];
                        return obj;
                    }, {});

                setDevice({ ...inforDevice, inforWarning: "H" })
            }
            // thiết bị inverter
            if (200 < warningType && 300 > warningType) {
                value = "-";
            }
            // Thiết bị phóng điện
            if (CONS.WARNING_TYPE.PHONG_DIEN.includes(warningType)) {
                value = dataWarning.indicator;
                let selected = ['indicator'];
                let filtered = Object.keys(dataWarning)
                    .filter(key => selected.includes(key))
                    .reduce((obj, key) => {
                        obj[key] = dataWarning[key];
                        return obj;
                    }, {});
                setDevice({ ...inforDevice, inforWarning: "indicator" })
            }

            // Thiết bị lưu lượng
            if (CONS.WARNING_TYPE.LUU_LUONG.includes(warningType)) {
                value = dataWarning.fs;
                let selected = ['fs'];
                let filtered = Object.keys(dataWarning)
                    .filter(key => selected.includes(key))
                    .reduce((obj, key) => {
                        obj[key] = dataWarning[key];
                        return obj;
                    }, {});
                setDevice({ ...inforDevice, inforWarning: "fs" })
            }

            // Thiết bị áp suất
            if (CONS.WARNING_TYPE.AP_SUAT.includes(warningType)) {
                value = dataWarning.p;
                let selected = ['p'];
                let filtered = Object.keys(dataWarning)
                    .filter(key => selected.includes(key))
                    .reduce((obj, key) => {
                        obj[key] = dataWarning[key];
                        return obj;
                    }, {});
                setDevice({ ...inforDevice, inforWarning: "p" })
            }

            settingValue = { settingValue: settingValue }
            value = { value: value }
            data = { ...data, ...settingValue, ...value }

            funcDrawChart(customerId, warning.deviceId, warning.warningType, warning.fromDate, warning.toDate, settingValue.settingValue)
        }
        setInforWarning(data)

    }

    const funcInforWarningFrame2 = async (warning, _device) => {
        let data = warning;
        let inforDevice = { ...device, deviceId: _device.deviceId, deviceType: _device.deviceType, warningType: warning.warningType }
        data.priorityFlag = _device.priorityFlag;
        let res = await WarningService.getInfoWarnedDeviceFrame2(customerId, systemTypeId, warning.deviceId, warning.warningType, warning.toDate);
        if (res.status == 200) {
            let dataWarning = res.data.dataFrame2;
            let setting = res.data.setting;

            let warningType = warning.warningType;
            let settingValue = setting.split(",");

            let value = "-"
            let nameWarnings = "";
            if (warningType == CONS.WARNING_TYPE.SONG_HAI_DIEN_AP_BAC_N) {

                let minValue = Infinity;
                let selected = ['van_H2', 'van_H3', 'van_H4', 'van_H5', 'van_H6', 'van_H7', 'van_H8', 'van_H9', 'van_H10', 'van_H11', 'van_H12', 'van_H13', 'van_H14', 'van_H15', 'van_H16', 'van_H17', 'van_H18', 'van_H19', 'van_H20', 'van_H21', 'van_H22', 'van_H23', 'van_H24', 'van_H25', 'van_H26', 'van_H27', 'van_H28', 'van_H29', 'van_H30', 'van_H31'
                    , 'vbn_H2', 'vbn_H3', 'vbn_H4', 'vbn_H5', 'vbn_H6', 'vbn_H7', 'vbn_H8', 'vbn_H9', 'vbn_H10', 'vbn_H11', 'vbn_H12', 'vbn_H13', 'vbn_H14', 'vbn_H15', 'vbn_H16', 'vbn_H17', 'vbn_H18', 'vbn_H19', 'vbn_H20', 'vbn_H21', 'vbn_H22', 'vbn_H23', 'vbn_H24', 'vbn_H25', 'vbn_H26', 'vbn_H27', 'vbn_H28', 'vbn_H29', 'vbn_H30', 'vbn_H31'
                    , 'vcn_H2', 'vcn_H3', 'vcn_H4', 'vcn_H5', 'vcn_H6', 'vcn_H7', 'vcn_H8', 'vcn_H9', 'vcn_H10', 'vcn_H11', 'vcn_H12', 'vcn_H13', 'vcn_H14', 'vcn_H15', 'vcn_H16', 'vcn_H17', 'vcn_H18', 'vcn_H19', 'vcn_H20', 'vcn_H21', 'vcn_H22', 'vcn_H23', 'vcn_H24', 'vcn_H25', 'vcn_H26', 'vcn_H27', 'vcn_H28', 'vcn_H29', 'vcn_H30', 'vcn_H31'];
                let filtered = Object.keys(dataWarning)
                    .filter(key => selected.includes(key))
                    .reduce((obj, key) => {
                        obj[key] = dataWarning[key];
                        return obj;
                    }, {});

                for (let key in filtered) {

                    if (filtered[key] >= settingValue) {
                        if (nameWarnings == '') {
                            nameWarnings += key.charAt(0).toUpperCase() + key.slice(1);
                        } else {
                            nameWarnings += ' - ' + key.charAt(0).toUpperCase() + key.slice(1);
                        }
                    }
                }
                setDevice({ ...inforDevice, inforWarning: nameWarnings })

                for (let i = 2; i <= 31; i++) {
                    const vanHValue = dataWarning["van_H" + i];
                    const vbnHValue = dataWarning["vbn_H" + i];
                    const vcnHValue = dataWarning["vcn_H" + i];

                    if (vanHValue >= settingValue[0] && vanHValue < minValue) {
                        minValue = vanHValue;
                    }

                    else if (vbnHValue >= settingValue[0] && vbnHValue < minValue) {
                        minValue = vbnHValue;
                    }

                    else if (vcnHValue >= settingValue[0] && vcnHValue < minValue) {
                        minValue = vcnHValue;
                    }
                }

                if (minValue !== Infinity) {
                    value = minValue;
                } else {
                    // Không có giá trị nào lớn hơn 3
                    // Thực hiện xử lý tương ứng nếu cần
                }

            } else if (warningType == CONS.WARNING_TYPE.SONG_HAI_DONG_DIEN_BAC_N) {
                // const settingValueString = "6,3,2.25,0.9,0.35";
                // const settingValueArray = settingValueString.toString() // Chuyển đổi chuỗi thành mảng số
                // settingValue = settingValueArray
                let minValue = Infinity;
                for (let j = 0; j < settingValue.length; j++) {
                    if (j == 0) {
                        let selected = ['ia_H2', 'ia_H3', 'ia_H4', 'ia_H5', 'ia_H6', 'ia_H7', 'ia_H8', 'ia_H9', 'ia_H10'
                            , 'ib_H2', 'ib_H3', 'ib_H4', 'ib_H5', 'ib_H6', 'ib_H7', 'ib_H8', 'ib_H9', 'ib_H10'
                            , 'ic_H2', 'ic_H3', 'ic_H4', 'ic_H5', 'ic_H6', 'ic_H7', 'ic_H8', 'ic_H9', 'ic_H10'];
                        let filtered = Object.keys(dataWarning)
                            .filter(key => selected.includes(key))
                            .reduce((obj, key) => {
                                obj[key] = dataWarning[key];
                                return obj;
                            }, {});
                        for (let key in filtered) {

                            if (filtered[key] >= settingValue[j]) {
                                if (nameWarnings == '') {
                                    nameWarnings += key.charAt(0).toUpperCase() + key.slice(1);
                                } else {
                                    nameWarnings += ' - ' + key.charAt(0).toUpperCase() + key.slice(1);
                                }
                            }
                        }
                        setDevice({ ...inforDevice, inforWarning: nameWarnings })
                        for (let i = 2; i <= 10; i++) {
                            const currentValueIA = dataWarning["ia_H" + i];
                            const currentValueIB = dataWarning["ib_H" + i];
                            const currentValueIC = dataWarning["ic_H" + i];
                            if (currentValueIA >= settingValue[j] || currentValueIB >= settingValue[j] || currentValueIC >= settingValue[j]) {
                                minValue = Math.max(currentValueIA, currentValueIB, currentValueIC);
                            }
                        }
                    } else if (j == 1) {
                        let selected = ['ia_H11', 'ia_H12', 'ia_H13', 'ia_H14', 'ia_H15', 'ia_H16'
                            , 'ib_H11', 'ib_H12', 'ib_H13', 'ib_H14', 'ib_H15', 'ib_H16'
                            , 'ic_H11', 'ic_H12', 'ic_H13', 'ic_H14', 'ic_H15', 'ic_H16'];
                        let filtered = Object.keys(dataWarning)
                            .filter(key => selected.includes(key))
                            .reduce((obj, key) => {
                                obj[key] = dataWarning[key];
                                return obj;
                            }, {});
                        for (let key in filtered) {

                            if (filtered[key] >= settingValue[j]) {
                                if (nameWarnings == '') {
                                    nameWarnings += key.charAt(0).toUpperCase() + key.slice(1);
                                } else {
                                    nameWarnings += ' - ' + key.charAt(0).toUpperCase() + key.slice(1);
                                }
                            }
                        }
                        setDevice({ ...inforDevice, inforWarning: nameWarnings })
                        for (let i = 11; i <= 16; i++) {
                            const currentValueIA = dataWarning["ia_H" + i];
                            const currentValueIB = dataWarning["ib_H" + i];
                            const currentValueIC = dataWarning["ic_H" + i];
                            if (currentValueIA >= settingValue[j] || currentValueIB >= settingValue[j] || currentValueIC >= settingValue[j]) {
                                minValue = Math.max(currentValueIA, currentValueIB, currentValueIC);

                            }
                        }
                    } else if (j == 2) {
                        let selected = ['ia_H17', 'ia_H18', 'ia_H19', 'ia_H20', 'ia_H21', 'ia_H22'
                            , 'ib_H17', 'ib_H18', 'ib_H19', 'ib_H20', 'ib_H21', 'ib_H22'
                            , 'ic_H17', 'ic_H18', 'ic_H19', 'ic_H20', 'ic_H21', 'ic_H22'];
                        let filtered = Object.keys(dataWarning)
                            .filter(key => selected.includes(key))
                            .reduce((obj, key) => {
                                obj[key] = dataWarning[key];
                                return obj;
                            }, {});
                        for (let key in filtered) {

                            if (filtered[key] >= settingValue[j]) {
                                if (nameWarnings == '') {
                                    nameWarnings += key.charAt(0).toUpperCase() + key.slice(1);
                                } else {
                                    nameWarnings += ' - ' + key.charAt(0).toUpperCase() + key.slice(1);
                                }
                            }
                        }
                        setDevice({ ...inforDevice, inforWarning: nameWarnings })
                        for (let i = 17; i <= 22; i++) {
                            const currentValueIA = dataWarning["ia_H" + i];
                            const currentValueIB = dataWarning["ib_H" + i];
                            const currentValueIC = dataWarning["ic_H" + i];
                            if (currentValueIA >= settingValue[j] || currentValueIB >= settingValue[j] || currentValueIC >= settingValue[j]) {
                                minValue = Math.max(currentValueIA, currentValueIB, currentValueIC);

                            }
                        }
                    } else if (j == 3) {
                        let selected = ['ia_H23', 'ia_H24', 'ia_H25', 'ia_H26', 'ia_H27', 'ia_H28', 'ia_H29', 'ia_H30', 'ia_H31'
                            , 'ib_H23', 'ib_H24', 'ib_H25', 'ib_H26', 'ib_H27', 'ib_H28', 'ib_H29', 'ib_H30', 'ib_H31'
                            , 'ic_H23', 'ic_H24', 'ic_H25', 'ic_H26', 'ic_H27', 'ic_H28', 'ic_H29', 'ic_H30', 'ic_H31'];
                        let filtered = Object.keys(dataWarning)
                            .filter(key => selected.includes(key))
                            .reduce((obj, key) => {
                                obj[key] = dataWarning[key];
                                return obj;
                            }, {});
                        for (let key in filtered) {

                            if (filtered[key] >= settingValue[j]) {
                                if (nameWarnings == '') {
                                    nameWarnings += key.charAt(0).toUpperCase() + key.slice(1);
                                } else {
                                    nameWarnings += ' - ' + key.charAt(0).toUpperCase() + key.slice(1);
                                }
                            }
                        }
                        setDevice({ ...inforDevice, inforWarning: nameWarnings })
                        for (let i = 23; i <= 34; i++) {
                            const currentValueIA = dataWarning["ia_H" + i];
                            const currentValueIB = dataWarning["ib_H" + i];
                            const currentValueIC = dataWarning["ic_H" + i];
                            if (currentValueIA >= settingValue[j] || currentValueIB >= settingValue[j] || currentValueIC >= settingValue[j]) {
                                minValue = Math.max(currentValueIA, currentValueIB, currentValueIC);

                            }
                        }
                    }
                }

                if (minValue !== Infinity) {
                    value = minValue;
                } else {
                    // Không có giá trị nào lớn hơn 3
                    // Thực hiện xử lý tương ứng nếu cần
                }
            }

            // thiết bị temp - hummidity sensor

            if (warning.warningType == 108) {
                settingValue = { settingValue: settingValue }
            } else {
                settingValue = { settingValue: settingValue }
            }

            value = { value: value }
            data = { ...data, ...settingValue, ...value }
            funcDrawChartFrame2(customerId, warning.deviceId, warning.warningType, warning.fromDate, warning.toDate, settingValue.settingValue)
        }
        setInforWarning(data)
    }

    const funcDrawChart = async (customerId, deviceId, warningType, fromDate, toDate, setting) => {
        let res = await WarningService.getListDataWarning(customerId, systemTypeId, deviceId, warningType, fromDate, toDate);
        if (res.status == 200) {
            setListWarning(res.data.data);
            const settingList = res.data.settingList;
            setSelectedWarningType(warningType);

            drawChart(res.data.data, settingList, setting, warningType);
        }
    }

    const funcDrawChartFrame2 = async (customerId, deviceId, warningType, fromDate, toDate, setting) => {

        let resFrame2 = await WarningService.getListDataWarningFrame2(customerId, systemTypeId, deviceId, warningType, fromDate, toDate);

        if (resFrame2.status == 200) {
            setListWarningFrame2(resFrame2.data.data)
            const settingList = resFrame2.data.settingList;
            setSelectedWarningType(warningType);
            drawChart(resFrame2.data.data, settingList, setting, warningType);
        }
    }



    const drawChart = (dataWarning, settingList, settingValue, warningType) => {
        const data = dataWarning.map((item, index) => {
            return {
                ...item,
                settingValue: parseFloat(settingValue),
                settingValueHistory: parseInt(item.settingValueHistory) || parseInt(settingValue)
            };
        });

        const extendedSettingList = settingList.map(item => {
            return {
                ...item,
                settingValue: parseFloat(settingValue),
                settingValueHistory: parseInt(item.settingValueHistory) || parseInt(settingValue)
            };
        });

        // Gộp hai mảng lại với nhau
        const mergedData = [...data, ...extendedSettingList];

        // Sắp xếp theo viewTime
        const sortedData = mergedData.sort((a, b) => new Date(a.viewTime) - new Date(b.viewTime));
        const data1 = dataWarning.map((item) => {
            return { ...item, settingValue: parseFloat(settingValue) };
        });

        am5.array.each(am5.registry.rootElements, function (root) {
            if (root) {
                if (root.dom.id == "chartdivWarning") {
                    root.dispose();
                }
            }
        });
        am5.ready(function () {
            let root = am5.Root.new("chartdivWarning");
            root._logo.dispose();
            // Set themes
            // https://www.amcharts.com/docs/v5/concepts/themes/
            root.setThemes([
                am5themes_Animated.new(root)
            ]);

            // Create chart
            // https://www.amcharts.com/docs/v5/charts/xy-chart/
            let chart = root.container.children.push(
                am5xy.XYChart.new(root, {
                    focusable: true,
                    panX: true,
                    panY: false,
                    heelX: "panX",
                    wheelY: "zoomX",
                    layout: root.verticalLayout

                })
            );

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

            let easing = am5.ease.linear;

            // Add cursor
            // https://www.amcharts.com/docs/v5/charts/xy-chart/cursor/
            let cursor = chart.set("cursor", am5xy.XYCursor.new(root, {
                behavior: "none"
            }));
            cursor.lineY.set("visible", false);

            // Create axes
            // https://www.amcharts.com/docs/v5/charts/xy-chart/axes/
            let xRenderer = am5xy.AxisRendererX.new(root, {
                minGridDistance: 50,
                strokeOpacity: 0.2,
            });
            xRenderer.labels.template.setAll({
                rotation: -30,
                paddingTop: 10,
                paddingRight: 10,
                fontSize: 10
            });
            xRenderer.grid.template.set("forceHidden", true);

            let xAxis = chart.xAxes.push(
                am5xy.CategoryAxis.new(root, {
                    categoryField: "viewTime",
                    renderer: xRenderer,
                    tooltip: am5.Tooltip.new(root, {})
                })
            );
            xAxis.data.setAll(sortedData);

            let yAxis = chart.yAxes.push(
                am5xy.ValueAxis.new(root, {
                    maxDeviation: 1000,
                    renderer: am5xy.AxisRendererY.new(root, {
                        inversed: false
                    })
                }),

            );

            // yAxis.set("min", -50);
            // yAxis.set("max", 300);


            if (warningType == 105 || warningType == 106) {
                yAxis.children.moveValue(am5.Label.new(root, { text: `Tần số [[Hz]]`, rotation: -90, y: am5.p50, centerX: am5.p50 }), 0);
            }
            if (warningType == 101 || warningType == 102) {
                yAxis.children.moveValue(am5.Label.new(root, { text: `Điện áp  [[V]]`, rotation: -90, y: am5.p50, centerX: am5.p50 }), 0);
            }
            if (warningType == 107 || warningType == 103) {
                yAxis.children.moveValue(am5.Label.new(root, { text: `Dòng diện [[A]]`, rotation: -90, y: am5.p50, centerX: am5.p50 }), 0);
            }
            if (warningType == 112) {
                yAxis.children.moveValue(am5.Label.new(root, { text: `Hệ số công suất`, rotation: -90, y: am5.p50, centerX: am5.p50 }), 0);
            }
            if (warningType == 108 || warningType == 109) {
                yAxis.children.moveValue(am5.Label.new(root, { text: `Biên độ sóng hài  [[%]]`, rotation: -90, y: am5.p50, centerX: am5.p50 }), 0);
            }
            // Add series
            // https://www.amcharts.com/docs/v5/charts/xy-chart/series/

            function createSeries(name, field, checked, unit) {
                let series = chart.series.push(
                    am5xy.LineSeries.new(root, {
                        name: name,
                        xAxis: xAxis,
                        yAxis: yAxis,
                        valueYField: field,
                        categoryXField: "viewTime",
                        tooltip: am5.Tooltip.new(root, {
                            pointerOrientation: "horizontal",
                            labelText: "[bold]{name}[/]\n{categoryX}: [bold]{valueY}" + " " + unit
                        })

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
                yAxis.set("renderer.labels.template.adapter.add", function (text) {
                    return text + " " + unit; // Thêm đơn vị vào nhãn của trục Y
                });

                // yAxis.set("min", -300);
                // yAxis.set("max", 300);
                series.set("setStateOnChildren", true);
                series.states.create("hover", {});

                series.mainContainer.set("setStateOnChildren", true);
                series.mainContainer.states.create("hover", {});

                series.strokes.template.states.create("hover", {
                    strokeWidth: 4
                });
                chart.yAxes.getIndex(0).set("separateStacks", true);
                series.data.setAll(sortedData);

                series.appear(1000);
                chart.appear(1000, 100);
            }

            function createSeriesHistory(name, field, unit) {
                let xAxisHistory = chart.xAxes.push(
                    am5xy.CategoryAxis.new(root, {
                        categoryField: "viewTime",
                        renderer: xRenderer,
                        // tooltip: am5.Tooltip.new(root, {})
                    })
                );
                xAxisHistory.data.setAll(sortedData);

                let series = chart.series.push(
                    am5xy.LineSeries.new(root, {
                        name: name,
                        xAxis: xAxis,
                        yAxis: yAxis,
                        valueYField: "settingValue",
                        categoryXField: "viewTime",
                        tooltip: am5.Tooltip.new(root, {
                            pointerOrientation: "horizontal",
                            labelText: "[bold]{name}[/]\n{categoryX}: [bold]{valueY}" + " " + unit
                        })

                    })
                );

                yAxis.set("renderer.labels.template.adapter.add", function (text) {
                    return text + " " + unit; // Thêm đơn vị vào nhãn của trục Y
                });

                series.set("setStateOnChildren", true);
                series.states.create("hover", {});
                series.mainContainer.set("setStateOnChildren", true);
                series.mainContainer.states.create("hover", {});

                series.strokes.template.states.create("hover", {
                    strokeWidth: 4
                });
                chart.yAxes.getIndex(0).set("separateStacks", true);
                series.data.setAll(sortedData);
                series.appear(1000);
                chart.appear(1000, 100);
            }

            function createSeriesN(name, field, checked, unit) {

                let series = chart.series.push(
                    am5xy.LineSeries.new(root, {
                        name: name,
                        xAxis: xAxis,
                        yAxis: yAxis,
                        valueYField: field,
                        categoryXField: "viewTime",
                        tooltip: am5.Tooltip.new(root, {
                            pointerOrientation: "horizontal",
                            labelText: "[bold]{name}[/]\n{categoryX}: [bold]{valueY}" + " " + unit
                        })
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
                let seriesRangeDataItem = yAxis.makeDataItem({ value: field, endValue: 0 });
                let seriesRange = series.createAxisRange(seriesRangeDataItem);
                seriesRange.fills.template.setAll({
                    visible: true,
                    opacity: 0.3
                });

                seriesRange.fills.template.set("fill", am5.color(0xFF0000));

                // Tạo một đối tượng Cursor

                seriesRangeDataItem.get("grid").setAll({
                    strokeOpacity: 1,
                    visible: true,
                    stroke: am5.color(0xFF0000),
                    strokeDasharray: [0, 0],
                    strokeWidth: 2,
                    step: 100
                })


                seriesRangeDataItem.get("label").setAll({
                    location: 0,
                    visible: true,
                    text: "",

                    inside: true,
                    centerX: 0,
                    centerY: am5.p100,
                    fontWeight: "bold"
                })
                yAxis.set("renderer.labels.template.adapter.add", function (text) {
                    return text + " " + unit; // Thêm đơn vị vào nhãn của trục Y
                });
                series.set("setStateOnChildren", true);
                series.states.create("hover", {});

                series.mainContainer.set("setStateOnChildren", true);
                series.mainContainer.states.create("hover", {});

                series.strokes.template.states.create("hover", {
                    strokeWidth: 4
                });

                series.data.setAll(sortedData);
                chart.appear(1000, 100);
                series.appear(1000);
            }



            if (warningType == CONS.WARNING_TYPE.SONG_HAI_DONG_DIEN_BAC_N) {
                createSeries(t('content.home_page.warning_tab.setting_value'), 'settingValue', false, "%")
                for (let x = 0; x < settingValue.length; x++) {
                    createSeriesN("Ngưỡng " + (x + 1), settingValue[x], false, "%")
                }
            } else {
                if (settingList.length > 1) {

                    if (warningType == CONS.WARNING_TYPE.NGUONG_AP_CAO || warningType == CONS.WARNING_TYPE.NGUONG_AP_THAP || warningType == CONS.WARNING_TYPE.MAT_DIEN_TONG) {
                        createSeriesHistory(t('content.home_page.warning_tab.setting_value'), 'settingValueHistory', "V");

                    } else if (CONS.WARNING_TYPE.TAN_SO_CAO.includes(warningType) || CONS.WARNING_TYPE.TAN_SO_THAP.includes(warningType)) {
                        createSeriesHistory(t('content.home_page.warning_tab.setting_value'), 'settingValueHistory', "Hz");

                    } else if (warningType == CONS.WARNING_TYPE.LECH_PHA || warningType == CONS.WARNING_TYPE.QUA_TAI) {
                        createSeriesHistory(t('content.home_page.warning_tab.setting_value'), 'settingValueHistory', "A");

                    } else if (warningType == CONS.WARNING_TYPE_MST.TONG_MEO_SONG_HAI_DIEN_AP || warningType == CONS.WARNING_TYPE_MST.TONG_MEO_SONG_HAI_DONG_DIEN || warningType == CONS.WARNING_TYPE_MST.SONG_HAI_DIEN_AP_BAC_N) {
                        createSeriesHistory(t('content.home_page.warning_tab.setting_value'), 'settingValueHistory', "%");

                    } else if (warningType == CONS.WARNING_TYPE.NHIET_DO_CAO || warningType == CONS.WARNING_TYPE.NHIET_DO_THAP) {
                        createSeriesHistory(t('content.home_page.warning_tab.setting_value'), 'settingValueHistory', "°C");

                    } else {
                        createSeriesHistory(t('content.home_page.warning_tab.setting_value'), 'settingValueHistory', "");
                    }
                }
                else if (warningType == CONS.WARNING_TYPE.NGUONG_AP_CAO || warningType == CONS.WARNING_TYPE.NGUONG_AP_THAP || warningType == CONS.WARNING_TYPE.MAT_DIEN_TONG) {
                    createSeries(t('content.home_page.warning_tab.setting_value'), 'settingValue', false, "V");

                } else if (CONS.WARNING_TYPE.TAN_SO_CAO.includes(warningType) || CONS.WARNING_TYPE.TAN_SO_THAP.includes(warningType)) {
                    createSeries(t('content.home_page.warning_tab.setting_value'), 'settingValue', false, "Hz");

                } else if (warningType == CONS.WARNING_TYPE.LECH_PHA || warningType == CONS.WARNING_TYPE.QUA_TAI) {
                    createSeries(t('content.home_page.warning_tab.setting_value'), 'settingValue', false, "A");

                } else if (warningType == CONS.WARNING_TYPE.NGUONG_AP_CAO || warningType == CONS.WARNING_TYPE.NGUONG_AP_THAP) {
                    createSeries(t('content.home_page.warning_tab.setting_value'), 'settingValue', false, "V");

                } else if (warningType == CONS.WARNING_TYPE_MST.TONG_MEO_SONG_HAI_DIEN_AP || warningType == CONS.WARNING_TYPE_MST.TONG_MEO_SONG_HAI_DONG_DIEN || warningType == CONS.WARNING_TYPE_MST.SONG_HAI_DIEN_AP_BAC_N) {
                    createSeries(t('content.home_page.warning_tab.setting_value'), 'settingValue', false, "%");

                } else if (warningType == CONS.WARNING_TYPE.NHIET_DO_CAO || warningType == CONS.WARNING_TYPE.NHIET_DO_THAP) {
                    createSeries(t('content.home_page.warning_tab.setting_value'), 'settingValue', false, "°C");

                } else if (CONS.WARNING_TYPE.LUU_LUONG.includes(warningType)) {
                    createSeries(t('content.home_page.warning_tab.setting_value'), 'settingValue', false, "m³/s");

                } else if (CONS.WARNING_TYPE.AP_SUAT.includes(warningType)) {
                    createSeries(t('content.home_page.warning_tab.setting_value'), 'settingValue', false, "bar");

                } else {
                    createSeries(t('content.home_page.warning_tab.setting_value'), 'settingValue', false, "");
                }
            }
            // for (let x = 0; x < settingValue.length; x++) {
            //     createSeriesN(t('content.home_page.warning_tab.setting_value') + x, x)

            // }

            // thiết bị meter
            if (warningType == CONS.WARNING_TYPE.NGUONG_AP_CAO) {
                yAxis.set("min", 0)
                createSeries("Uan", "uan", true, "V");
                createSeries("Ubn", "ubn", true, "V");
                createSeries("Ucn", "ucn", true, "V");

            } else if (warningType == CONS.WARNING_TYPE.NGUONG_AP_THAP) {
                createSeries("Uan", "uan", true, "V");
                createSeries("Ubn", "ubn", true, "V");
                createSeries("Ucn", "ucn", true, "V");
            } else if (warningType == CONS.WARNING_TYPE.LECH_PHA) {
                createSeries("Ia", "ia", true, "A");
                createSeries("Ib", "ib", true, "A");
                createSeries("Ic", "ic", true, "A");
            } else if (warningType == CONS.WARNING_TYPE.HE_SO_CONG_SUAT_THAP) {
                createSeries("PFa", "pfa", true, "");
                createSeries("PFb", "pfb", true, "");
                createSeries("PFc", "pfc", true, "");
            } else if (warningType == CONS.WARNING_TYPE.QUA_TAI) {

                createSeries("Ia", "ia", true, "A");
                createSeries("Ib", "ib", true, "A");
                createSeries("Ic", "ic", true, "A");
            } else if (CONS.WARNING_TYPE.TAN_SO_THAP.includes(warningType)) {
                createSeries("F", "f", true, "Hz");
            } else if (CONS.WARNING_TYPE.TAN_SO_CAO.includes(warningType)) {
                createSeries("F", "f", true, "Hz");
            } else if (warningType == CONS.WARNING_TYPE.MAT_DIEN_TONG) {
                createSeries("Uan", "uan", true, "V");
                createSeries("Ubn", "ubn", true, "V");
                createSeries("Ucn", "ucn", true, "V");
            } else if (warningType == CONS.WARNING_TYPE.NGUOC_PHA) {
                createSeries("PFa", "pfa", true, "");
                createSeries("PFb", "pfb", true, "");
                createSeries("PFc", "pfc", true, "");
            } else if (CONS.WARNING_TYPE.SONG_HAI.includes(warningType)) {
                if (warningType == 111) {
                    createSeries("THD_Ia", "thdIa", true, "%");
                    createSeries("THD_Ib", "thdIb", true, "%");
                    createSeries("THD_Ic", "thdIc", true, "%");
                }
                if (warningType == 109) {

                    // Thêm các phần tử vào container
                    for (let i = 2; i <= 31; i++) {
                        const baseName = `VAN_H${i}`;
                        const valueName = `van_H${i}`;
                        createSeries(baseName, valueName, true, "%");
                    }
                    for (let i = 2; i <= 31; i++) {
                        const baseName = `VBN_H${i}`;
                        const valueName = `vbn_H${i}`;
                        createSeries(baseName, valueName, true, "%");
                    }
                    for (let i = 2; i <= 31; i++) {
                        const baseName = `VCN_H${i}`;
                        const valueName = `vcn_H${i}`;
                        createSeries(baseName, valueName, true, "%");
                    }
                }
                if (warningType == 108) {

                    for (let i = 2; i <= 31; i++) {
                        const baseName = `IA_H${i}`;
                        const valueName = `ia_H${i}`;
                        createSeriesN(baseName, valueName, true, "%");
                    }
                    for (let i = 2; i <= 31; i++) {
                        const baseName = `IB_H${i}`;
                        const valueName = `ib_H${i}`;
                        createSeriesN(baseName, valueName, true, "%");
                    }
                    for (let i = 2; i <= 31; i++) {
                        const baseName = `IC_H${i}`;
                        const valueName = `ic_H${i}`;
                        createSeriesN(baseName, valueName, true, "%");
                    }
                }
                if (warningType == 110) {
                    createSeries("THD_Van", "thdVan", true, "%");
                    createSeries("THD_Vbn", "thdVbn", true, "%");
                    createSeries("THD_Vcn", "thdVcn", true, "%");
                }
            }

            //thiết bị cảm biến nhiệt độ - độ ẩm
            if (CONS.WARNING_TYPE.NHIET_DO_CAO.includes(warningType)) {
                createSeries("T", "t", true, "°C");
            } else if (CONS.WARNING_TYPE.NHIET_DO_THAP.includes(warningType)) {
                createSeries("T", "t", true, "°C");
            } else if (CONS.WARNING_TYPE.DO_AM.includes(warningType)) {
                createSeries("Humidity", "h", true, "%");
            }

            //thiết bị inverter
            // if (200 < warningType && 300 > warningType) {
            //     createSeries("Cảnh báo", 'settingValue', true);
            // }

            // Thiết bị phóng điện
            if (CONS.WARNING_TYPE.PHONG_DIEN.includes(warningType)) {
                createSeries("Indicator", "indicator", true);
            }

            // Thiết bị áp suất
            if (CONS.WARNING_TYPE.AP_SUAT.includes(warningType)) {
                createSeries("P", "p", true, "bar");
            }

            // Thiết bị lưu lượng
            if (CONS.WARNING_TYPE.LUU_LUONG.includes(warningType)) {
                createSeries("Fs", "fs", true, "m³/s");
            }

            let legend = chart.children.push(
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
    };
    // const thElements = document.querySelectorAll(".table-container table thead tr th");

    const funcTableOrChart = (e) => {
        setTableOrChart(e)
    }

    const handleClickAddCAR = async (item) => {
        let listWarning = item.listWarning;
        let content = "";
        for (let i = 0; i < listWarning.length; i++) {
            if (i == 0) {
                content += listWarning[i].warningTypeName
            } else {
                content += ", " + listWarning[i].warningTypeName
            }
        }
        const newDataWarningCar = { ...dataWarningCarAdd };
        newDataWarningCar.systemTypeId = systemTypeId;
        newDataWarningCar.projectId = item.projectId;
        newDataWarningCar.deviceId = item.deviceId;
        newDataWarningCar.createId = userId;
        newDataWarningCar.content = "Kiểm tra cảnh báo " + content;
        setDataWarningCarAdd(newDataWarningCar);
        setIsModalAddCAROpen(true);
    }

    const getUser = async () => {
        let res = await UserService.getUserByUsername();
        let cusId = param.customerId
        if (res.status === 200) {
            setUserId(res.data.id)
            let customerIds = res.data.customerIds
            if (customerIds != null) {
                let result = customerIds.includes(cusId);
                setAuthorUpdateCar(result)
            }
        }
    }

    const funcAddWarningCar = async () => {

        const emptyDataWarningCar = {
            id: "",
            systemTypeId: "",
            projectId: "",
            deviceId: "",
            status: "",
            createId: "",
            organizationCreate: "",
            content: ""
        };
        if (dataWarningCarAdd.organizationCreate == "" || dataWarningCarAdd.organizationCreate.trim() === "") {
            $.alert({
                title: t('content.notification'),
                content: t('content.home_page.plan.check_null') + " " + t('content.home_page.plan.organization_create'),
            });
        } else if (dataWarningCarAdd.content == "" || dataWarningCarAdd.content.trim() === "") {
            $.alert({
                title: t('content.notification'),
                content: t('content.home_page.plan.check_null') + " " + t('content.home_page.plan.content'),
            });
        }
        else {
            let response = await WarningCarService.addWarningCar(param.customerId, dataWarningCarAdd);
            if (response.status === 200) {
                $.alert({
                    title: t('content.notification'),
                    content: t('content.home_page.plan.add_success'),
                });
                saveModalCAR();

                setDataWarningCarAdd(
                    emptyDataWarningCar
                );
                if (updateCountStatus) {
                    updateCountStatus();
                }
            } else if (response.status === 400) {
                $.alert({
                    title: t('content.notification'),
                    content: t('content.home_page.plan.add_fail'),
                });
            } else if (response.status === 500) {
                $.alert({
                    title: t('content.notification'),
                    content: t('content.home_page.plan.add_fail'),
                });
            }
        }
    }

    const closeModalCAR = () => {
        setIsModalCAROpen(false);
        setIsModalAddCAROpen(false)
    };

    const saveModalCAR = () => {
        setIsModalCAROpen(false);
        setIsModalAddCAROpen(false)
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        const newDataWarningCar = { ...dataWarningCarAdd, [name]: value };
        setDataWarningCarAdd(newDataWarningCar);
    };


    const openModal = () => {
        setIsModalOpen(true);
    };

    const handleOpenModal = () => {
        openModal(true);
    };

    const closeModal = () => {
        setIsModalOpen(false);
    };

    useEffect(() => {
        document.title = "Cảnh báo"
        getWarning(fromDate, toDate);
        listDeviceLostSignal()
        getUser();
        // if (location.state) {
        //     setNotification(location.state);
        // };
    }, [customerId, projectId, systemTypeId, fromDate, toDate]);

    return (

        <div className="mt-2">
            <ToastContainer />
            <div>
                {/* <div className="project-infor" style={{ padding: "0px 10px", display: "block", marginTop: "10px" }}>
                    <span className="project-tree">{projectInfo}</span>
                </div> */}
                <div className="content-warning-calendar">
                    <div className="input-group p-1">
                        <div className="input-group-prepend float-left" style={{ zIndex: 0 }}>
                            <button className="btn btn-outline-secondary" title="Kiểu xem" type="button" style={{ backgroundColor: isActiveButton ? "#0a1a5c" : "#e9ecef" }} onClick={() => handleChangeView(isActiveButton)}>
                                <img src="/resources/image/icon-calendar.svg" style={{ height: "18px" }}></img>
                            </button>
                            <button className="btn btn-outline-secondary btn-time" title="Kiểu xem" type="button" style={{ backgroundColor: isActiveButton ? "#e9ecef" : "#0a1a5c" }} onClick={() => handleChangeView(isActiveButton)}>
                                <img src="/resources/image/icon-play.svg" style={{ height: "18px" }}></img>
                            </button>
                        </div>
                        {!isActiveButton && (
                            <div className="input-group float-left mr-1 select-calendar" style={{ width: "120px", marginLeft: 10, height: 34 }}>
                                <select className="form-control select-value"
                                    //onChange={(e) => handleChangeChartType(e.target.value)}
                                    style={{ backgroundColor: "#0a1a5c", borderRadius: 5, border: "1px solid #FFA87D", color: "white" }}
                                    title="Chi tiết"
                                    onChange={onChangeValue}
                                >
                                    <option className="value" key={1} value={1}>{t('content.home_page.today')}</option>
                                    <option className="value" key={2} value={2}>{t('content.home_page.yesterday')}</option>
                                    <option className="value" key={3} value={3}>{t('content.home_page.this_month')}</option>
                                    <option className="value" key={4} value={4}>{t('content.home_page.last_month')}</option>
                                    <option className="value" key={5} value={5}>{t('content.home_page.3_months_ago')}</option>
                                    <option className="value" key={6} value={6}>{t('content.home_page.6_months_ago')}</option>
                                    <option className="value" key={7} value={7}>{t('content.home_page.this_year')}</option>
                                    <option className="value" key={8} value={8}>{t('content.home_page.last_year')}</option>
                                </select>
                            </div>
                        )}
                        {isActiveButton && (
                            <div className="input-group float-left mr-1 select-time" title="Chi tiết" style={{ width: "300px", marginLeft: 10, height: 34 }}>
                                <button className="form-control button-calendar" readOnly data-toggle="modal" data-target={"#modal-calendar"} style={{ backgroundColor: "#ffffff", border: "1px solid #0A1A5C" }}>
                                    {moment(fromDate).format("YYYY-MM-DD") + " - " + moment(toDate).format("YYYY-MM-DD")}
                                </button>
                                <div className="input-group-append" style={{ zIndex: 0 }}>
                                    <button className="btn button-infor" type="button" data-toggle="modal" data-target={"#modal-calendar"} style={{ fontWeight: "bold", height: 34 }}>......</button>
                                </div>
                            </div>
                        )}
                        <div className="modal fade" id="modal-calendar" tabIndex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
                            <div className="modal-dialog" role="document">
                                <div className="modal-content">
                                    <div className="modal-header" style={{ backgroundColor: "#0a1a5c", height: "44px", color: "white" }}>
                                        <h5 style={{ color: "white" }}>CALENDAR</h5>
                                        <button style={{ color: "#fff" }} type="button" className="close" data-dismiss="modal" aria-label="Close">
                                            <span aria-hidden="true">&times;</span>
                                        </button>
                                    </div>
                                    <div className="modal-body">
                                        <div className="input-group float-left mr-1" style={{ width: "270px" }}>
                                            <h5>Từ ngày</h5>
                                            <Calendar
                                                id="from-value"
                                                className="celendar-picker"
                                                dateFormat="yy-mm-dd"
                                                maxDate={(new Date)}
                                                value={fromDate}
                                                onChange={e => setFromDate(e.value)}
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
                                                dateFormat="yy-mm-dd"
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
                                        <div className="input-group float-left mr-1">
                                        </div>
                                    </div>
                                    <div className="modal-footer">
                                        <button type="button" className="btn btn-secondary" data-dismiss="modal">Đóng</button>

                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>

                {/* Cảnh báo thiết bị mất tín hiệu */}
                {dataLostSignal.length > 0 && <div className="device-lost-signal alert alert-danger alert-dismissible fade show" role="alert" style={{ height: "50px", fontSize: "15px", paddingTop: "18px" }} onClick={() => handleOpenModal()}>
                    <strong style={{ color: "rgb(212, 17, 17)" }}>{t('content.home_page.warning')}!</strong> {t('content.home_page.warning_tab.lost_signal_device')}: {dataLostSignal.length}
                </div>}

                <div className={`content-warning-card mt-2 ${warnings.devicesWarning == 0 ? 'd-none' : ''}`} style={{ overflow: "auto" }}>
                    <div className="pl-1">
                        <div className="pl-1 pr-2 title-warning-type">
                            <i className="fa-solid fa-tag m-1" style={{ color: "var(--gray-800)" }}></i>
                            {t('content.home_page.overview.warning_statistics')}
                        </div>
                    </div>
                    <span className={warnings.nguongApCao == undefined ? 'd-none' : ''}>
                        <div className={`card warning-card float-left ${activeWarning === "warning-001" ? 'warning-active' : ''}`} id="warning-001"
                            onClick={() => {
                                if (warnings.nguongApCao <= 0) {
                                    return
                                }
                                deviceWarning(CONS.WARNING_TYPE.NGUONG_AP_CAO, "warning-001", fromDate, toDate)
                            }}>
                            <div className="card-header">
                                <h4 className="card-title"><img src="/resources/image/icon-dienapcao.png" alt="Điện áp cao" /></h4>
                            </div>
                            <div className="card-content">
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.nguongApCao > 0 ? 'numberWarning' : ''}`}>{warnings.nguongApCao ? warnings.nguongApCao : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.over_volt')}</p>
                                </div>
                            </div>
                        </div>
                    </span>
                    <span className={warnings.nguongApThap == undefined ? 'd-none' : ''}>
                        <div className={`card warning-card float-left ${activeWarning === "warning-002" ? 'warning-active' : ''}`} id="warning-002" onClick={() => {
                            if (warnings.nguongApThap <= 0) {
                                return
                            }
                            deviceWarning(CONS.WARNING_TYPE.NGUONG_AP_THAP, "warning-002", fromDate, toDate)
                        }}>
                            <div className="card-header">
                                <h4 className="card-title"><img src="/resources/image/icon-dienapthap.png" alt="Điện áp thấp" /></h4>
                            </div>
                            <div className="card-content">
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.nguongApThap > 0 ? 'numberWarning' : ''}`}>{warnings.nguongApThap ? warnings.nguongApThap : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.under_volt')}</p>
                                </div>
                            </div>
                        </div>
                    </span>
                    <span className={warnings.quaTai == undefined ? 'd-none' : ''}>
                        <div className={`card warning-card float-left ${activeWarning === "warning-003" ? 'warning-active' : ''}`} id="warning-003" onClick={() => {
                            if (warnings.quaTai <= 0) {
                                return
                            }
                            deviceWarning(CONS.WARNING_TYPE.QUA_TAI, "warning-003", fromDate, toDate)
                        }}>
                            <div className="card-header">
                                <h4 className="card-title"><img src="/resources/image/icon-quataitong.png" alt="Quá tải" /></h4>
                            </div>
                            <div className="card-content">
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.quaTai > 0 ? 'numberWarning' : ''}`}>{warnings.quaTai ? warnings.quaTai : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.over_load')}</p>
                                </div>
                            </div>
                        </div>
                    </span>
                    <span className={warnings.heSoCongSuatThap == undefined ? 'd-none' : ''}>
                        <div className={`card warning-card float-left ${activeWarning === "warning-004" ? 'warning-active' : ''}`} id="warning-004" onClick={() => {
                            if (warnings.heSoCongSuatThap <= 0) {
                                return
                            }
                            deviceWarning(CONS.WARNING_TYPE.HE_SO_CONG_SUAT_THAP, "warning-004", fromDate, toDate)
                        }}>
                            <div className="card-header">
                                <h4 className="card-title"><img src="/resources/image/icon-hesocongsuatthap.png" alt="Hệ số công suất thấp" /></h4>
                            </div>
                            <div className="card-content">
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.heSoCongSuatThap > 0 ? 'numberWarning' : ''}`}>{warnings.heSoCongSuatThap ? warnings.heSoCongSuatThap : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.under_power_factor')}</p>
                                </div>
                            </div>
                        </div>
                    </span>
                    <span className={warnings.tanSoCao == undefined ? 'd-none' : ''}>
                        <div className={`card warning-card float-left ${activeWarning === "warning-005" ? 'warning-active' : ''}`} id="warning-005" onClick={() => {
                            if (warnings.tanSoCao <= 0) {
                                return
                            }
                            deviceWarning(CONS.WARNING_TYPE.TAN_SO_CAO, "warning-005", fromDate, toDate)
                        }}>
                            <div className="card-header">
                                <h4 className="card-title"><img src="/resources/image/icon-tansocao.png" alt="Tần số cao" /></h4>
                            </div>
                            <div className="card-content">
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.tanSoCao > 0 ? 'numberWarning' : ''}`}>{warnings.tanSoCao ? warnings.tanSoCao : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.over_frequency')}</p>
                                </div>
                            </div>
                        </div>
                    </span>
                    <span className={warnings.tanSoThap == undefined ? 'd-none' : ''}>
                        <div className={`card warning-card float-left ${activeWarning === "warning-006" ? 'warning-active' : ''}`} id="warning-006" onClick={() => {
                            if (warnings.tanSoThap <= 0) {
                                return
                            }
                            deviceWarning(CONS.WARNING_TYPE.TAN_SO_THAP, "warning-006", fromDate, toDate)
                        }}>
                            <div className="card-header">
                                <h4 className="card-title"><img src="/resources/image/icon-tansothap.png" alt="Tần số thấp" /></h4>
                            </div>
                            <div className="card-content">
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.tanSoThap > 0 ? 'numberWarning' : ''}`}>{warnings.tanSoThap ? warnings.tanSoThap : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.under_frequency')}</p>
                                </div>
                            </div>
                        </div>
                    </span>
                    <span className={warnings.lechPha == undefined ? 'd-none' : ''}>
                        <div className={`card warning-card float-left ${activeWarning === "warning-007" ? 'warning-active' : ''}`} id="warning-007" onClick={() => {
                            if (warnings.lechPha <= 0) {
                                return
                            }
                            deviceWarning(CONS.WARNING_TYPE.LECH_PHA, "warning-007", fromDate, toDate)
                        }}>
                            <div className="card-header">
                                <h4 className="card-title"><img src="/resources/image/icon-lechphatong.png" alt="Lệch pha" /></h4>
                            </div>
                            <div className="card-content">
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.lechPha > 0 ? 'numberWarning' : ''}`}>{warnings.lechPha ? warnings.lechPha : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.phase_deviation')}</p>

                                </div>
                            </div>
                        </div>
                    </span>

                    <span className={warnings.nguocPha == undefined ? 'd-none' : ''}>
                        <div className={`card warning-card float-left ${activeWarning === "warning-036" ? 'warning-active' : ''}`} id="warning-036" onClick={() => {
                            if (warnings.nguocPha <= 0) {
                                return
                            }
                            deviceWarning(CONS.WARNING_TYPE.NGUOC_PHA, "warning-036", fromDate, toDate)
                        }}>
                            <div className="card-header">
                                <h4 className="card-title"><img src="/resources/image/icon-nguocpha.png" alt="Ngược pha" /></h4>
                            </div>
                            <div className="card-content">
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.nguocPha > 0 ? 'numberWarning' : ''}`}>{warnings.nguocPha ? warnings.nguocPha : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.phase_reverse')}</p>
                                </div>
                            </div>
                        </div>

                    </span>
                    <span className={warnings.songHai == undefined ? 'd-none' : ''}>
                        <div className={`card warning-card float-left ${activeWarning === "warning-008" ? 'warning-active' : ''}`} id="warning-008" onClick={() => {
                            if (warnings.songHai <= 0) {
                                return
                            }
                            deviceWarning(CONS.WARNING_TYPE.SONG_HAI, "warning-008", fromDate, toDate)
                        }}>
                            <div className="card-header">
                                <h4 className="card-title"><img src="/resources/image/icon-songhai.png" alt="Sóng hài" /></h4>
                            </div>
                            <div className="card-content">
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.songHai > 0 ? 'numberWarning' : ''}`}>{warnings.songHai ? warnings.songHai : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.harmonic')}</p>

                                </div>
                            </div>
                        </div>
                    </span>
                    <span className={warnings.matDienTong == undefined ? 'd-none' : ''}>
                        <div className={`card warning-card float-left ${activeWarning === "warning-009" ? 'warning-active' : ''}`} id="warning-009" onClick={() => {
                            if (warnings.matDienTong <= 0) {
                                return
                            }
                            deviceWarning(CONS.WARNING_TYPE.MAT_DIEN_TONG, "warning-009", fromDate, toDate)
                        }}>
                            <div className="card-header">
                                <h4 className="card-title"><img src="/resources/image/icon-matdientong.png" alt="Mất điện tổng" /></h4>
                            </div>
                            <div className="card-content">
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.matDienTong > 0 ? 'numberWarning' : ''}`}>{warnings.matDienTong ? warnings.matDienTong : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.lost_power')}</p>

                                </div>
                            </div>
                        </div>
                    </span>
                    <span className={warnings.chamDat == undefined ? 'd-none' : ''}>
                        <div className={`card warning-card float-left ${activeWarning === "warning-010" ? 'warning-active' : ''}`} id="warning-010" onClick={() => {
                            if (warnings.chamDat <= 0) {
                                return
                            }
                            deviceWarning(CONS.WARNING_TYPE.CHAM_DAT, "warning-010", fromDate, toDate)
                        }}>
                            <div className="card-header">
                                <h4 className="card-title"><img src="/resources/image/icon-chamdat.png" alt="Chạm đất" /></h4>
                            </div>
                            <div className="card-content">
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.chamDat > 0 ? 'numberWarning' : ''}`}>{warnings.chamDat ? warnings.chamDat : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.ground_fault')}</p>

                                </div>
                            </div>
                        </div>
                    </span>


                    <span className={warnings.matNguon == undefined ? 'd-none' : ''}>
                        <div className={`card warning-card float-left ${activeWarning === "warning-011" ? 'warning-active' : ''}`} id="warning-011" onClick={() => {
                            if (warnings.matNguon <= 0) {
                                return
                            }
                            deviceWarning(CONS.WARNING_TYPE.DIEN_AP_CAO_DC, "warning-011", fromDate, toDate)
                        }}>
                            <div className="card-header">
                                <h4 className="card-title"><img src="/resources/image/icon-dienapcao-dc.png" alt="Điện áp cao DC" /></h4>
                            </div>
                            <div className="card-content">
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.dienApCaoDC > 0 ? 'numberWarning' : ''}`}>{warnings.dienApCaoDC ? warnings.dienApCaoDC : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.dc_over_volt')}</p>

                                </div>
                            </div>
                        </div>
                    </span>

                    <span className={warnings.quaDongTrungTinh == undefined ? 'd-none' : ''}>
                        <div className={`card warning-card float-left ${activeWarning === "warning-012" ? 'warning-active' : ''}`} id="warning-012" onClick={() => {
                            if (warnings.quaDongTrungTinh <= 0) {
                                return
                            }
                            deviceWarning(CONS.WARNING_TYPE.MAT_KET_NOI_AC, "warning-012", fromDate, toDate)
                        }}>
                            <div className="card-header">
                                <h4 className="card-title"><img src="/resources/image/icon-matketnoi-ac.png" alt="Mất kết nối AC" /></h4>
                            </div>
                            <div className="card-content">
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.matKetNoiAC > 0 ? 'numberWarning' : ''}`}>{warnings.matKetNoiAC ? warnings.matKetNoiAC : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.ac_disconnect')}</p>
                                </div>
                            </div>
                        </div>
                    </span>
                    <span className={warnings.matKetNoiDC == undefined ? 'd-none' : ''}>
                        <div className={`card warning-card float-left ${activeWarning === "warning-013" ? 'warning-active' : ''}`} id="warning-013" onClick={() => {
                            if (warnings.matKetNoiDC <= 0) {
                                return
                            }
                            deviceWarning(CONS.WARNING_TYPE.MAT_KET_NOI_DC, "warning-013", fromDate, toDate)
                        }}>
                            <div className="card-header">
                                <h4 className="card-title"><img src="/resources/image/icon-matketnoi-dc.png" alt="Mất kết nối DC" /></h4>
                            </div>
                            <div className="card-content">
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.matKetNoiDC > 0 ? 'numberWarning' : ''}`}>{warnings.matKetNoiDC ? warnings.matKetNoiDC : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.dc_disconnect')}</p>
                                </div>
                            </div>
                        </div>
                    </span>
                    <span className={warnings.matNguonLuoi == undefined ? 'd-none' : ''}>
                        <div className={`card warning-card float-left ${activeWarning === "warning-014" ? 'warning-active' : ''}`} id="warning-014" onClick={() => {
                            if (warnings.matNguonLuoi <= 0) {
                                return
                            }
                            deviceWarning(CONS.WARNING_TYPE.MAT_NGUON_LUOI, "warning-014", fromDate, toDate)
                        }}>
                            <div className="card-header">
                                <h4 className="card-title"><img src="/resources/image/icon-matketnoi-luoi.png" alt="Mất kết nối lưới" /></h4>
                            </div>
                            <div className="card-content">
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.matNguonLuoi > 0 ? 'numberWarning' : ''}`}>{warnings.matNguonLuoi ? warnings.matNguonLuoi : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.grid_disconnect')}</p>
                                </div>
                            </div>
                        </div>
                    </span>
                    <span className={warnings.dongMoCua == undefined ? 'd-none' : ''}>
                        <div className={`card warning-card float-left ${activeWarning === "warning-015" ? 'warning-active' : ''}`} id="warning-015" onClick={() => {
                            if (warnings.dongMoCua <= 0) {
                                return
                            }
                            deviceWarning(CONS.WARNING_TYPE.DONG_MO_CUA, "warning-015", fromDate, toDate)
                        }}>
                            <div className="card-header">
                                <h4 className="card-title"><img src="/resources/image/icon-dongmocua.png" alt="Đóng mở cửa" /></h4>
                            </div>
                            <div className="card-content">
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.dongMoCua > 0 ? 'numberWarning' : ''}`}>{warnings.dongMoCua ? warnings.dongMoCua : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.door_operation')}</p>
                                </div>
                            </div>
                        </div>
                    </span>
                    <span className={warnings.nhietDoThap == undefined ? 'd-none' : ''}>
                        <div className={`card warning-card float-left ${activeWarning === "warning-021" ? 'warning-active' : ''}`} id="warning-021" onClick={() => {
                            if (warnings.nhietDoThap <= 0) {
                                return
                            }
                            deviceWarning(CONS.WARNING_TYPE.NHIET_DO_THAP, "warning-021", fromDate, toDate)
                        }}>
                            <div className="card-header">
                                <h4 className="card-title"><img src="/resources/image/icon-nhietdotiepxuc.png" alt="Nhiệt độ thấp" /></h4>
                            </div>
                            <div className="card-content">
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.nhietDoThap > 0 ? 'numberWarning' : ''}`}>{warnings.nhietDoThap ? warnings.nhietDoThap : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.under_temp')}</p>
                                </div>
                            </div>
                        </div>

                    </span>
                    <span className={warnings.nhietDoCao == undefined ? 'd-none' : ''}>
                        <div className={`card warning-card float-left ${activeWarning === "warning-022" ? 'warning-active' : ''}`} id="warning-022" onClick={() => {
                            if (warnings.nhietDoCao <= 0) {
                                return
                            }
                            deviceWarning(CONS.WARNING_TYPE.NHIET_DO_CAO, "warning-022", fromDate, toDate)
                        }}>
                            <div className="card-header">
                                <h4 className="card-title"><img src="/resources/image/icon-nhietdotiepxuc.png" alt="Nhiệt độ cao" /></h4>
                            </div>
                            <div className="card-content">
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.nhietDoCao > 0 ? 'numberWarning' : ''}`}>{warnings.nhietDoCao ? warnings.nhietDoCao : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.over_temp')}</p>
                                </div>
                            </div>
                        </div>

                    </span>
                    <span className={warnings.doAm == undefined ? 'd-none' : ''}>
                        <div className={`card warning-card float-left ${activeWarning === "warning-035" ? 'warning-active' : ''}`} id="warning-035" onClick={() => {
                            if (warnings.doAm <= 0) {
                                return
                            }
                            deviceWarning(CONS.WARNING_TYPE.DO_AM, "warning-035", fromDate, toDate)
                        }}>
                            <div className="card-header">
                                <h4 className="card-title"><img src="/resources/image/icon-doam.png" alt="Độ ẩm" /></h4>
                            </div>
                            <div className="card-content">
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.doAm > 0 ? 'numberWarning' : ''}`}>{warnings.doAm ? warnings.doAm : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.humidity')}</p>
                                </div>
                            </div>
                        </div>

                    </span>

                    <span className={warnings.FITuRMU == undefined ? 'd-none' : ''}>
                        <div className={`card warning-card float-left ${activeWarning === "warning-023" ? 'warning-active' : ''}`} id="warning-023" onClick={() => {
                            if (warnings.FITuRMU <= 0) {
                                return
                            }
                            deviceWarning(CONS.WARNING_TYPE.FI_TU_RMU, "warning-023", fromDate, toDate)
                        }}>
                            <div className="card-header">
                                <h4 className="card-title"><img src="/resources/image/icon-matdienrmu.png" alt="FI Tủ RMU" /></h4>
                            </div>
                            <div className="card-content">
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.FITuRMU > 0 ? 'numberWarning' : ''}`}>{warnings.FITuRMU ? warnings.FITuRMU : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.f1_rmu')}</p>
                                </div>
                            </div>
                        </div>
                    </span>
                    <span className={warnings.khoangTonThat == undefined ? 'd-none' : ''}>

                        <div className={`card warning-card float-left ${activeWarning === "warning-024" ? 'warning-active' : ''}`} id="warning-024" onClick={() => {
                            if (warnings.khoangTonThat <= 0) {
                                return
                            }
                            deviceWarning(CONS.WARNING_TYPE.KHOANG_TON_THAT, "warning-024", fromDate, toDate)
                        }}>
                            <div className="card-header">
                                <h4 className="card-title"><img src="/resources/image/icon-homtonthat.png" alt="Khoang tổn thất" /></h4>
                            </div>
                            <div className="card-content">
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.khoangTonThat > 0 ? 'numberWarning' : ''}`}>{warnings.khoangTonThat ? warnings.khoangTonThat : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.anti_loss_compartment')}</p>
                                </div>
                            </div>
                        </div>

                    </span>
                    <span className={warnings.mucDauThap == undefined ? 'd-none' : ''}>
                        <div className={`card warning-card float-left ${activeWarning === "warning-025" ? 'warning-active' : ''}`} id="warning-025" onClick={() => {
                            if (warnings.mucDauThap <= 0) {
                                return
                            }
                            deviceWarning(CONS.WARNING_TYPE.MUC_DAU_THAP, "warning-025", fromDate, toDate)
                        }}>
                            <div className="card-header">
                                <h4 className="card-title"><img src="/resources/image/icon-mucdauthap.png" alt="Mức dầu thấp" /></h4>
                            </div>
                            <div className="card-content">
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.mucDauThap > 0 ? 'numberWarning' : ''}`}>{warnings.mucDauThap ? warnings.mucDauThap : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.under_oil')}</p>
                                </div>
                            </div>
                        </div>

                    </span>
                    <span className={warnings.roleGas == undefined ? 'd-none' : ''}>
                        <div className={`card warning-card float-left ${activeWarning === "warning-026" ? 'warning-active' : ''}`} id="warning-026" onClick={() => {
                            if (warnings.roleGas <= 0) {
                                return
                            }
                            deviceWarning(CONS.WARNING_TYPE.ROLE_GAS, "warning-026", fromDate, toDate)
                        }}>
                            <div className="card-header">
                                <h4 className="card-title"><img src="/resources/image/icon-rolegas.png" alt="Role gas" /></h4>
                            </div>
                            <div className="card-content">
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.roleGas > 0 ? 'numberWarning' : ''}`}>{warnings.roleGas ? warnings.roleGas : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.gas_relay')}</p>
                                </div>
                            </div>
                        </div>
                    </span>
                    <span className={warnings.chamVo == undefined ? 'd-none' : ''}>
                        <div className={`card warning-card float-left ${activeWarning === "warning-027" ? 'warning-active' : ''}`} id="warning-027" onClick={() => {
                            if (warnings.chamVo <= 0) {
                                return
                            }
                            deviceWarning(CONS.WARNING_TYPE.CHAM_VO, "warning-027", fromDate, toDate)
                        }}>
                            <div className="card-header">
                                <h4 className="card-title"><img src="/resources/image/icon-apluckhi.png" alt="Chạm vỏ" /></h4>
                            </div>
                            <div className="card-content">
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.chamVo > 0 ? 'numberWarning' : ''}`}>{warnings.chamVo ? warnings.chamVo : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.touch_shell')}</p>
                                </div>
                            </div>
                        </div>
                    </span>
                    <span className={warnings.mucDauCao == undefined ? 'd-none' : ''}>
                        <div className={`card warning-card float-left ${activeWarning === "warning-028" ? 'warning-active' : ''}`} id="warning-028" onClick={() => {
                            if (warnings.mucDauCao <= 0) {
                                return
                            }
                            deviceWarning(CONS.WARNING_TYPE.MUC_DAU_CAO, "warning-028", fromDate, toDate)
                        }}>
                            <div className="card-header">
                                <h4 className="card-title"><img src="/resources/image/icon-mucdaucao.png" alt="Mức dầu cao" /></h4>
                            </div>
                            <div className="card-content">
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.mucDauCao > 0 ? 'numberWarning' : ''}`}>{warnings.mucDauCao ? warnings.mucDauCao : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.over_oil')}</p>
                                </div>
                            </div>
                        </div>
                    </span>
                    <span className={warnings.camBienHongNgoai == undefined ? 'd-none' : ''}>
                        <div className={`card warning-card float-left ${activeWarning === "warning-029" ? 'warning-active' : ''}`} id="warning-029" onClick={() => {
                            if (warnings.camBienHongNgoai <= 0) {
                                return
                            }
                            deviceWarning(CONS.WARNING_TYPE.CAM_BIEN_HONG_NGOAI, "warning-029", fromDate, toDate)
                        }}>
                            <div className="card-header">
                                <h4 className="card-title"><img src="/resources/image/icon-hongngoai.png" alt="Cảm biến hồng ngoại" /></h4>
                            </div>
                            <div className="card-content">
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.camBienHongNgoai > 0 ? 'numberWarning' : ''}`}>{warnings.camBienHongNgoai ? warnings.camBienHongNgoai : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.infrared_sensor')}</p>
                                </div>
                            </div>
                        </div>
                    </span>
                    <span className={warnings.apSuatNoiBoMBA == undefined ? 'd-none' : ''}>
                        <div className={`card warning-card float-left ${activeWarning === "warning-030" ? 'warning-active' : ''}`} id="warning-030" onClick={() => {
                            if (warnings.apSuatNoiBoMBA <= 0) {
                                return
                            }
                            deviceWarning(CONS.WARNING_TYPE.AP_SUAT_NOI_BO_MBA, "warning-030", fromDate, toDate)
                        }}>
                            <div className="card-header">
                                <h4 className="card-title"><img src="/resources/image/icon-apsuatnoimba.png" alt="Áp suất nội bộ MBA" /></h4>
                            </div>
                            <div className="card-content">
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.apSuatNoiBoMBA > 0 ? 'numberWarning' : ''}`}>{warnings.apSuatNoiBoMBA ? warnings.apSuatNoiBoMBA : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.internal_pressure_transformer')}</p>
                                </div>
                            </div>
                        </div>
                    </span>
                    <span className={warnings.roleNhietDoDau == undefined ? 'd-none' : ''}>
                        <div className={`card warning-card float-left ${activeWarning === "warning-031" ? 'warning-active' : ''}`} id="warning-031" onClick={() => {
                            if (warnings.roleNhietDoDau <= 0) {
                                return
                            }
                            deviceWarning(CONS.WARNING_TYPE.ROLE_NHIET_DO_DAU, "warning-031", fromDate, toDate)
                        }}>
                            <div className="card-header">
                                <h4 className="card-title"><img src="/resources/image/icon-nhietdodau.png" alt="Role Nhiệt độ dầu" /></h4>
                            </div>
                            <div className="card-content">
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.roleNhietDoDau > 0 ? 'numberWarning' : ''}`}>{warnings.roleNhietDoDau ? warnings.roleNhietDoDau : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.oil_temp_relay')}</p>
                                </div>
                            </div>
                        </div>
                    </span>
                    <span className={warnings.nhietDoCuonDay == undefined ? 'd-none' : ''}>
                        <div className={`card warning-card float-left ${activeWarning === "warning-032" ? 'warning-active' : ''}`} id="warning-032" onClick={() => {
                            if (warnings.nhietDoCuonDay <= 0) {
                                return
                            }
                            deviceWarning(CONS.WARNING_TYPE.NHIET_DO_CUON_DAY, "warning-032", fromDate, toDate)
                        }}>
                            <div className="card-header">
                                <h4 className="card-title"><img src="/resources/image/icon-nhietdocuonday.png" alt="Nhiệt độ cuộn dây" /></h4>
                            </div>
                            <div className="card-content">
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.nhietDoCuonDay > 0 ? 'numberWarning' : ''}`}>{warnings.nhietDoCuonDay ? warnings.nhietDoCuonDay : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.coil_temp')}</p>
                                </div>
                            </div>
                        </div>
                    </span>
                    <span className={warnings.khiGasMBA == undefined ? 'd-none' : ''}>
                        <div className={`card warning-card float-left ${activeWarning === "warning-033" ? 'warning-active' : ''}`} id="warning-033" onClick={() => {
                            if (warnings.khiGasMBA <= 0) {
                                return
                            }
                            deviceWarning(CONS.WARNING_TYPE.KHI_GAS_MBA, "warning-034", fromDate, toDate)
                        }}>
                            <div className="card-header">
                                <h4 className="card-title"><img src="/resources/image/icon-khigatrongmba.png" alt="Khí gas mba" /></h4>
                            </div>
                            <div className="card-content">
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.khiGasMBA > 0 ? 'numberWarning' : ''}`}>{warnings.khiGasMBA ? warnings.khiGasMBA : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.transformer_gas')}</p>
                                </div>
                            </div>
                        </div>
                    </span>
                    <span className={warnings.phongDien == undefined ? 'd-none' : ''}>
                        <div className={`card warning-card float-left ${activeWarning === "warning-034" ? 'warning-active' : ''}`} id="warning-034" onClick={() => {

                            deviceWarning(CONS.WARNING_TYPE.PHONG_DIEN, "warning-034", fromDate, toDate)
                        }}>
                            <div className="card-header">
                                <h4 className="card-title"><img src="/resources/image/icon-phongdien.png" alt="Phóng điện" /></h4>
                            </div>
                            <div className="card-content">
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.phongDien > 0 ? 'numberWarning' : ''}`}>{warnings.phongDien ? warnings.phongDien : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.energy_discharge')}</p>
                                </div>
                            </div>
                        </div>
                    </span>
                    <span className={warnings.devicesWarning == 0 ? 'd-none' : ''}>
                        <div className={`card warning-card float-left ${activeWarning === "warning-all" ? 'warning-active' : ''}`} id="warning-all" onClick={() => {
                            deviceWarning("ALL", "warning-all", fromDate, toDate)
                        }}>
                            <div className="card-header">
                                <h4 className="card-title"> </h4>
                            </div>
                            <div className="card-content" style={{ padding: 14 }}>
                                <div className="card-body">
                                    <div className={`numberCircle ${warnings.devicesWarning > 0 ? 'numberWarning' : ''}`}>{warnings.devicesWarning ? warnings.devicesWarning : 0}</div>
                                    <p className="text-uppercase">{t('content.home_page.warning_tab.all')}</p>
                                </div>
                            </div>
                        </div>
                    </span>


                </div>
                <div className="content-warning-device mt-2">
                    <div className="pl-1" style={{display: "flex", justifyContent: "space-between"}}>
                        <div className="pl-1 pr-2 title-warning-type">
                            <i className="fa-solid fa-tag m-1" style={{ color: "var(--gray-800)" }}></i>
                            {t('content.category.device.list.header')}
                        </div>
                        <div className="warning-search-device">
                        <input className="warning-search-device-input" type="text" placeholder={t('content.home_page.search')} onChange={searchDevice}></input>
                        <i className="fas fa-solid fa-search position-absolute" style={{ color: "#333", left: "90%", top: "35%" }}></i>
                    </div>
                    </div>
                    <div className="loading" id="warning-loading" style={{ marginTop: "", marginLeft: "45%" }}>
                        <img height="60px" src="/resources/image/loading2.gif" alt="loading" />
                    </div>
                    <div id="warnedDevices">
                        {searchWarnedDevice.length > 0 &&
                            <>
                                {searchWarnedDevice.map((item, index) => (
                                    <div className="p-1">
                                        <div key={index} className="polygon-warning-outside">
                                            <div className="polygon-warning-inside">
                                                <div>
                                                    <div className="title">{item.deviceName}</div>
                                                    <div className="priority pl-1">
                                                        <label>{t('content.home_page.warning_tab.importance_device')}: {item.priorityFlag == 1 ? t('content.home_page.warning_tab.low') : item.priorityFlag == 2 ? t('content.home_page.warning_tab.medium') : t('content.home_page.warning_tab.high')}</label>
                                                        <button className="warning-btn">
                                                            <i className="fas fa-solid fa-cube" style={{ color: "var(--ses-orange-100-color)" }} onClick={() => handleClickAddCAR(item)}></i>
                                                        </button>
                                                    </div>
                                                    <div className="content">
                                                        <table className="warning-table">
                                                            <thead className={item.listWarning.length > 2 ? "scroll" : ""}>

                                                                <tr height="40px">
                                                                    <th>{t('content.home_page.warning_tab.no')}</th>
                                                                    <th>
                                                                        {t('content.home_page.warning_tab.warning_type')}
                                                                    </th>
                                                                    <th>
                                                                        {t('content.home_page.warning_tab.time')}
                                                                    </th>
                                                                    <th >
                                                                        {t('content.home_page.warning_tab.times')}
                                                                    </th>
                                                                    <th>
                                                                        {t('content.home_page.warning_tab.warning_level')}
                                                                    </th>
                                                                </tr>
                                                            </thead>

                                                            <tbody style={{ lineHeight: 1 }} >
                                                                {warningType == "ALL" ?
                                                                    item.listWarning.map((warning, i) => (
                                                                        <tr key={i} height="30px" data-toggle="modal" data-target="#infor-warning-modal-lg" onClick={() => {
                                                                            if (warning.warningType === 108 || warning.warningType === 109) {
                                                                                funcInforWarningFrame2(warning, item);
                                                                            } else {
                                                                                funcInforWarning(warning, item);
                                                                            }
                                                                            // funcInforWarning(warning);
                                                                        }}>
                                                                            <td className="text-center">{i + 1}</td>
                                                                            <td className="text-center">{warning.warningTypeName}</td>
                                                                            <td className="text-center" >{warning.toDate}</td>
                                                                            <td className="text-center" >{warning.total}</td>
                                                                            <td className="text-center" >
                                                                                {warning.warningLevel == 1 && <div className="level1"></div>}
                                                                                {warning.warningLevel == 2 && <div className="level2"></div>}
                                                                                {warning.warningLevel == 3 && <div className="level3"></div>}
                                                                            </td>

                                                                        </tr>
                                                                    )) :
                                                                    compareToListSetting(item.listWarning, warningType).map((warning, i) => (
                                                                        <tr key={i} height="30px"
                                                                            style={
                                                                                typeof warningType === 'number' && isFinite(warningType) ?
                                                                                    (warning?.warningType == warningType ? { backgroundColor: '#F36F23' } : {})
                                                                                    :
                                                                                    (warningType.includes(warning?.warningType) ? { backgroundColor: '#F36F23' } : {})
                                                                            }
                                                                            data-toggle="modal" data-target="#infor-warning-modal-lg" onClick={() => {
                                                                                if (warning.warningType === 108 || warning.warningType === 109) {
                                                                                    funcInforWarningFrame2(warning, item);
                                                                                } else {
                                                                                    funcInforWarning(warning, item);
                                                                                }
                                                                            }}>
                                                                            <td className="text-center" style={
                                                                                typeof warningType === 'number' && isFinite(warningType) ?
                                                                                    (warning?.warningType == warningType ? { color: '#fff' } : {})
                                                                                    :
                                                                                    (warningType.includes(warning?.warningType) ? { color: '#fff' } : {})
                                                                            } >{i + 1}</td>
                                                                            <td className="text-center"
                                                                                style={
                                                                                    typeof warningType === 'number' && isFinite(warningType) ?
                                                                                        (warning?.warningType == warningType ? { color: '#fff' } : {})
                                                                                        :
                                                                                        (warningType.includes(warning?.warningType) ? { color: '#fff' } : {})
                                                                                } >{warning?.warningTypeName}</td>
                                                                            <td className="text-center" style={
                                                                                typeof warningType === 'number' && isFinite(warningType) ?
                                                                                    (warning?.warningType == warningType ? { color: '#fff' } : {})
                                                                                    :
                                                                                    (warningType.includes(warning?.warningType) ? { color: '#fff' } : {})
                                                                            } >{warning?.toDate}</td>
                                                                            <td className="text-center" style={
                                                                                typeof warningType === 'number' && isFinite(warningType) ?
                                                                                    (warning?.warningType == warningType ? { color: '#fff' } : {})
                                                                                    :
                                                                                    (warningType.includes(warning?.warningType) ? { color: '#fff' } : {})
                                                                            } >{warning?.total}</td>
                                                                            <td className="text-center" >
                                                                                {warning?.warningLevel == 1 && <div className="level1"></div>}
                                                                                {warning?.warningLevel == 2 && <div className="level2"></div>}
                                                                                {warning?.warningLevel == 3 && <div className="level3"></div>}
                                                                            </td>

                                                                        </tr>
                                                                    ))
                                                                }
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                ))
                                }
                            </>
                        }

                    </div>
                    {searchWarnedDevice.length == 0 &&
                        <div className="text-center loading-chart mt-1">{t('content.home_page.chart.active_device')}</div>
                    }

                </div>
            </div>
            <div className="modal fade bd-example-modal-lg" id="infor-warning-modal-lg" tabIndex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
                <div className="modal-dialog modal-dialog-centered modal-xl" style={{ maxWidth: "1500px", maxHeight: "800px" }}>
                    <div className="modal-content" style={{ height: '700px' }} >
                        <div className="left-warning">
                            <div className="infor" >
                                <i className="fa-solid fa-circle fa-2xs"> &nbsp;</i>    <label>{t('content.home_page.warning_tab.device_name')}: &nbsp;</label>{inforWarning.deviceName} <br />
                                <i className="fa-solid fa-circle fa-2xs"> &nbsp;</i>    <label>{t('content.home_page.warning_tab.warning_type')}: &nbsp;</label>{inforWarning.warningTypeName} <br />
                                <i className="fa-solid fa-circle fa-2xs"> &nbsp;</i>    <label>{t('content.home_page.warning_tab.warning_parameter')}: &nbsp;</label>{device.inforWarning} <br />

                                {
                                    device.warningType != 108 ?

                                        <>
                                            {(() => {
                                                let unit; // Khai báo biến unit ở ngoài JSX

                                                if (CONS.WARNING_TYPE.TAN_SO_CAO.includes(device.warningType) || CONS.WARNING_TYPE.TAN_SO_THAP.includes(device.warningType)) {
                                                    unit = "Hz";
                                                }
                                                else if (warningType == CONS.WARNING_TYPE.NGUONG_AP_CAO || warningType == CONS.WARNING_TYPE.NGUONG_AP_THAP || warningType == CONS.WARNING_TYPE.MAT_DIEN_TONG) {
                                                    unit = "V";

                                                } else if (warningType == CONS.WARNING_TYPE.LECH_PHA || warningType == CONS.WARNING_TYPE.QUA_TAI) {
                                                    unit = "A";

                                                } else if (warningType == CONS.WARNING_TYPE.NGUONG_AP_CAO || warningType == CONS.WARNING_TYPE.NGUONG_AP_THAP) {
                                                    unit = "%";

                                                } else if (warningType == CONS.WARNING_TYPE_MST.TONG_MEO_SONG_HAI_DIEN_AP || warningType == CONS.WARNING_TYPE_MST.TONG_MEO_SONG_HAI_DONG_DIEN || warningType == CONS.WARNING_TYPE_MST.SONG_HAI_DIEN_AP_BAC_N) {
                                                    unit = "%";
                                                } else if (warningType == CONS.WARNING_TYPE.NHIET_DO_CAO || warningType == CONS.WARNING_TYPE.NHIET_DO_THAP) {
                                                    unit = "°C";

                                                } else {
                                                    unit = "";
                                                }

                                                if (unit) { // Kiểm tra nếu biến unit đã được gán giá trị
                                                    return (
                                                        <>
                                                            <i className="fa-solid fa-circle fa-2xs"> &nbsp;</i>
                                                            <label>{t('content.home_page.warning_tab.setting_value')}: &nbsp;</label>
                                                            {inforWarning.settingValue.toString()}{unit}
                                                            <br />
                                                            <i className="fa-solid fa-circle fa-2xs"> &nbsp;</i>    <label>{t('content.home_page.warning_tab.warning_value')}: &nbsp;</label>{inforWarning.value}{unit} <br />
                                                        </>
                                                    );
                                                } else {
                                                    return (
                                                        <>
                                                            <i className="fa-solid fa-circle fa-2xs"> &nbsp;</i>
                                                            <label>{t('content.home_page.warning_tab.setting_value')}: &nbsp;</label>
                                                            {inforWarning.settingValue.toString()}
                                                            <br />
                                                            <i className="fa-solid fa-circle fa-2xs"> &nbsp;</i>    <label>{t('content.home_page.warning_tab.warning_value')}: &nbsp;</label>{inforWarning.value} <br />
                                                        </>
                                                    )
                                                }
                                            })()}
                                        </>

                                        : <>
                                            {
                                                inforWarning.settingValue.map((item, index) => (
                                                    <span key={index}>
                                                        {index == 0 && <>
                                                            <i className="fa-solid fa-circle fa-2xs"> &nbsp;</i>    <label>Ngưỡng cảnh báo (bậc 2- 10): &nbsp;</label>{item}% <br /></>
                                                        }
                                                        {index == 1 && <>
                                                            <i className="fa-solid fa-circle fa-2xs"> &nbsp;</i>    <label>Ngưỡng cảnh báo (bậc 11- 16): &nbsp;</label>{item}% <br /></>
                                                        }
                                                        {index == 2 && <>
                                                            <i className="fa-solid fa-circle fa-2xs"> &nbsp;</i>    <label>Ngưỡng cảnh báo (bậc 17- 22): &nbsp;</label>{item}% <br /></>
                                                        }
                                                        {index == 3 && <>
                                                            <i className="fa-solid fa-circle fa-2xs"> &nbsp;</i>    <label>Ngưỡng cảnh báo (bậc 23- 34): &nbsp;</label>{item}% <br /></>
                                                        }
                                                        {index == 4 && <>
                                                            <i className="fa-solid fa-circle fa-2xs"> &nbsp;</i>    <label>Ngưỡng cảnh báo (bậc từ 35 trở lên): &nbsp;</label>{item}% <br /></>
                                                        }
                                                    </span>
                                                ))
                                            }
                                        </>
                                }
                                <i className="fa-solid fa-circle fa-2xs"> &nbsp;</i>    <label>{t('content.home_page.warning_tab.importance_device')}:  &nbsp;</label>{inforWarning.priorityFlag == 1 ? t('content.home_page.warning_tab.low') : inforWarning.priorityFlag == 2 ? t('content.home_page.warning_tab.medium') : t('content.home_page.warning_tab.high')}<br />
                                <i className="fa-solid fa-circle fa-2xs"> &nbsp;</i>    <label>{t('content.home_page.warning_tab.priority_warning')}: &nbsp;</label>
                                {inforWarning.warningLevel == 1 && t('content.home_page.warning_tab.low')}
                                {inforWarning.warningLevel == 2 && t('content.home_page.warning_tab.medium')}
                                {inforWarning.warningLevel == 3 && t('content.home_page.warning_tab.high')}<br />
                                <i className="fa-solid fa-circle fa-2xs"> &nbsp;</i>    <label>{t('content.home_page.warning_tab.from_date')}: &nbsp;</label>{moment(inforWarning.fromDate).format("YYYY-MM-DD HH:mm:ss")} <br />
                                <i className="fa-solid fa-circle fa-2xs"> &nbsp;</i>    <label>{t('content.home_page.warning_tab.to_date')}: &nbsp;</label>{moment(inforWarning.toDate).format("YYYY-MM-DD HH:mm:ss")}  <br />
                                {/* <i className="fa-solid fa-circle fa-2xs"> &nbsp;</i>    <label>History: &nbsp;</label>
                                {inforWarning.settingValueHistory} <br /> */}
                                <i className="fa-solid fa-circle fa-2xs"> &nbsp;</i>    <label>{t('content.home_page.warning_tab.period')}: &nbsp;</label>

                                {(() => {
                                    const fromDate = moment(inforWarning.fromDate);
                                    const toDate = moment(inforWarning.toDate);
                                    const durationInHours = toDate.diff(fromDate, 'hours');

                                    if (durationInHours >= 24) {
                                        const days = Math.floor(durationInHours / 24);
                                        return `${days} ngày `;
                                    } else {
                                        const hours = Math.floor(durationInHours);
                                        const minutes = Math.floor((durationInHours % 1) * 60);
                                        return `${hours} giờ ${minutes} phút`;
                                    }
                                })()}

                            </div>
                        </div>
                        <div className="right-warning" style={{ height: '700px' }}>
                            <div className="chart" style={{ overflow: "auto" }}>
                                <div className="text-right p-1" style={{ height: "fit-content" }}>
                                    <button className="btn" onClick={() => funcTableOrChart(1)} hidden={tableOrChart == 0 ? false : true} style={{ border: "1px solid #F37021" }}>
                                        <i className="fas fa-solid fa-bars" style={{ color: "var(--ses-orange-100-color)" }} ></i>
                                    </button>
                                    <button className="btn" onClick={() => funcTableOrChart(0)} hidden={tableOrChart == 1 ? false : true} style={{ border: "1px solid #F37021" }}>
                                        <i className="fas fa-solid fa-chart-line" style={{ color: "var(--ses-orange-100-color)" }}></i>
                                    </button>
                                    <button type="button" className="btn btn-dashboard ml-1" style={{ border: "1px solid #F37021" }} onClick={onDownload}>
                                        <i className="fas fa-solid fa-download fa-1x" style={{ color: "var(--ses-orange-100-color)" }}></i>
                                    </button>

                                </div>
                                {(selectedWarningType == 109 || selectedWarningType == 108) ? <div id="chartdivWarning" style={{ height: "1000px", width: "100%" }} hidden={tableOrChart == 0 ? false : true}>
                                </div> :
                                    <div id="chartdivWarning" style={{ height: "95%", width: "100%" }} hidden={tableOrChart == 0 ? false : true}>
                                    </div>}


                                <div id="chartdivWarning" style={{ height: "95%", width: "100%" }} className="table-container" hidden={tableOrChart == 1 ? false : true}  >
                                    {(selectedWarningType !== 109 || selectedWarningType !== 108) &&
                                        <table id="myTable" className="table" style={{ width: "100%" }} ref={tableRef}>

                                            <thead>
                                                <tr>
                                                    {(selectedWarningType == 109 || selectedWarningType == 108) ?
                                                        null :
                                                        <>
                                                            <th width="50px">TT</th>
                                                            <th style={{ minWidth: "200px" }} >{t('content.home_page.warning_tab.time')}</th>
                                                        </>
                                                    }

                                                    {(selectedWarningType == 101 || selectedWarningType == 102 || selectedWarningType == 117) &&
                                                        <>
                                                            <th>UAn</th>
                                                            <th>UBn</th>
                                                            <th>Ucn</th>
                                                        </>
                                                    }
                                                    {selectedWarningType == 103 &&
                                                        <>
                                                            <th>T1</th>
                                                            <th>T2</th>
                                                            <th>T3</th>
                                                        </>
                                                    }
                                                    {(selectedWarningType == 110) &&
                                                        <>
                                                            <th>THD_Van</th>
                                                            <th>THD_Vbn</th>
                                                            <th>THD_Vcn</th>
                                                        </>
                                                    }
                                                    {selectedWarningType == 107 &&
                                                        <>
                                                            <th>Ia</th>
                                                            <th>Ib</th>
                                                            <th>Ic</th>
                                                        </>
                                                    }
                                                    {(selectedWarningType == 106 || selectedWarningType == 105) &&
                                                        <>
                                                            <th>F</th>
                                                        </>
                                                    }
                                                    {selectedWarningType == 111 &&
                                                        <>
                                                            <th>THD_Ia</th>
                                                            <th>THD_Ib</th>
                                                            <th>THD_Ic</th>
                                                        </>
                                                    }
                                                    {selectedWarningType == 104 || selectedWarningType == 112 &&
                                                        <>
                                                            <th>PFA</th>
                                                            <th>PFB</th>
                                                            <th>PFC</th>

                                                        </>
                                                    }
                                                    {selectedWarningType == 117 &&
                                                        <>
                                                            <th>In</th>
                                                        </>
                                                    }
                                                    {selectedWarningType == 117 &&
                                                        <>
                                                            <th>In</th>
                                                        </>
                                                    }
                                                    {selectedWarningType == 701 || selectedWarningType == 702 &&
                                                        <>
                                                            <th>P</th>
                                                        </>
                                                    }
                                                    {selectedWarningType == 1001 || selectedWarningType == 1002 &&
                                                        <>
                                                            <th>Fs</th>
                                                        </>
                                                    }

                                                </tr>
                                            </thead>

                                            <tbody>
                                                {(selectedWarningType !== 108 && selectedWarningType !== 109) &&
                                                    listWarning?.map((item, index) => (
                                                        <tr key={index}>
                                                            <td>{index + 1}</td>
                                                            <td style={{ textAlign: "center" }}>{item.viewTime}</td>

                                                            {(selectedWarningType == 101 || selectedWarningType == 102 || selectedWarningType == 117) &&
                                                                <>
                                                                    <td className="text-right">{item.uan}</td>
                                                                    <td className="text-right">{item.ubn}</td>
                                                                    <td className="text-right">{item.ucn}</td>
                                                                </>
                                                            }
                                                            {selectedWarningType == 103 &&
                                                                <>
                                                                    <td>{item.t1}</td>
                                                                    <td>{item.t2}</td>
                                                                    <td>{item.t3}</td>
                                                                </>
                                                            }
                                                            {(selectedWarningType == 104 || selectedWarningType == 110) &&
                                                                <>
                                                                    <td>{item.pfa}</td>
                                                                    <td>{item.pfb}</td>
                                                                    <td>{item.pfc}</td>
                                                                </>
                                                            }
                                                            {selectedWarningType == 107 &&
                                                                <>
                                                                    <td>{item.ia}</td>
                                                                    <td>{item.ib}</td>
                                                                    <td>{item.ic}</td>
                                                                </>
                                                            }
                                                            {(selectedWarningType == 106 || selectedWarningType == 105) &&
                                                                <>
                                                                    <td>{item.f}</td>
                                                                </>
                                                            }
                                                            {selectedWarningType == 111 &&
                                                                <>
                                                                    <td>{item.thdIa}</td>
                                                                    <td>{item.thdIb}</td>
                                                                    <td>{item.thdIb}</td>
                                                                </>
                                                            }
                                                            {selectedWarningType == 112 &&
                                                                <>
                                                                    <td>{item.thdVan}</td>
                                                                    <td>{item.thdVbn}</td>
                                                                    <td>{item.thdVcn}</td>
                                                                </>
                                                            }
                                                            {selectedWarningType == 117 &&
                                                                <>
                                                                    <td>{item.ia}</td>
                                                                </>
                                                            }
                                                            {selectedWarningType == 701 || selectedWarningType == 702 &&
                                                                <>
                                                                    <td>{item.p}</td>
                                                                </>
                                                            }
                                                            {selectedWarningType == 1001 || selectedWarningType == 1002 &&
                                                                <>
                                                                    <td>{item.fs}</td>
                                                                </>
                                                            }
                                                        </tr>

                                                    ))
                                                }
                                            </tbody>

                                        </table>

                                    }
                                    {(selectedWarningType == 109 || selectedWarningType == 108) &&
                                        <table id="myTable" className="table" style={{ width: "auto" }} ref={tableRef}>
                                            <thead>
                                                <tr>
                                                    <th width="50px">TT</th>
                                                    <th style={{ minWidth: "200px" }} >{t('content.home_page.warning_tab.time')}</th>

                                                    {selectedWarningType == 108 &&
                                                        <>
                                                            <th>IA_H1</th>
                                                            <th>IA_H2</th>
                                                            <th>IA_H3</th>
                                                            <th>IA_H4</th>
                                                            <th>IA_H5</th>
                                                            <th>IA_H6</th>
                                                            <th>IA_H7</th>
                                                            <th>IA_H8</th>
                                                            <th>IA_H9</th>
                                                            <th>IA_H10</th>
                                                            <th>IA_H11</th>
                                                            <th>IA_H12</th>
                                                            <th>IA_H13</th>
                                                            <th>IA_H14</th>
                                                            <th>IA_H15</th>
                                                            <th>IA_H16</th>
                                                            <th>IA_H17</th>
                                                            <th>IA_H18</th>
                                                            <th>IA_H19</th>
                                                            <th>IA_H20</th>
                                                            <th>IA_H21</th>
                                                            <th>IA_H22</th>
                                                            <th>IA_H23</th>
                                                            <th>IA_H24</th>
                                                            <th>IA_H25</th>
                                                            <th>IA_H26</th>
                                                            <th>IA_H27</th>
                                                            <th>IA_H28</th>
                                                            <th>IA_H29</th>
                                                            <th>IA_H30</th>
                                                            <th>IA_H31</th>
                                                            <th>IB_H1</th>
                                                            <th>IB_H2</th>
                                                            <th>IB_H3</th>
                                                            <th>IB_H4</th>
                                                            <th>IB_H5</th>
                                                            <th>IB_H6</th>
                                                            <th>IB_H7</th>
                                                            <th>IB_H8</th>
                                                            <th>IB_H9</th>
                                                            <th>IB_H10</th>
                                                            <th>IB_H11</th>
                                                            <th>IB_H12</th>
                                                            <th>IB_H13</th>
                                                            <th>IB_H14</th>
                                                            <th>IB_H15</th>
                                                            <th>IB_H16</th>
                                                            <th>IB_H17</th>
                                                            <th>IB_H18</th>
                                                            <th>IB_H19</th>
                                                            <th>IB_H20</th>
                                                            <th>IB_H21</th>
                                                            <th>IB_H22</th>
                                                            <th>IB_H23</th>
                                                            <th>IB_H24</th>
                                                            <th>IB_H25</th>
                                                            <th>IB_H26</th>
                                                            <th>IB_H27</th>
                                                            <th>IB_H28</th>
                                                            <th>IB_H29</th>
                                                            <th>IB_H30</th>
                                                            <th>IB_H31</th>
                                                            <th>IC_H1</th>
                                                            <th>IC_H2</th>
                                                            <th>IC_H3</th>
                                                            <th>IC_H4</th>
                                                            <th>IC_H5</th>
                                                            <th>IC_H6</th>
                                                            <th>IC_H7</th>
                                                            <th>IC_H8</th>
                                                            <th>IC_H9</th>
                                                            <th>IC_H10</th>
                                                            <th>IC_H11</th>
                                                            <th>IC_H12</th>
                                                            <th>IC_H13</th>
                                                            <th>IC_H14</th>
                                                            <th>IC_H15</th>
                                                            <th>IC_H16</th>
                                                            <th>IC_H17</th>
                                                            <th>IC_H18</th>
                                                            <th>IC_H19</th>
                                                            <th>IC_H20</th>
                                                            <th>IC_H21</th>
                                                            <th>IC_H22</th>
                                                            <th>IC_H23</th>
                                                            <th>IC_H24</th>
                                                            <th>IC_H25</th>
                                                            <th>IC_H26</th>
                                                            <th>IC_H27</th>
                                                            <th>IC_H28</th>
                                                            <th>IC_H29</th>
                                                            <th>IC_H30</th>
                                                            <th>IC_H31</th>

                                                        </>
                                                    }
                                                    {selectedWarningType == 109 &&
                                                        <>
                                                            <th>VAN_H1</th>
                                                            <th>VAN_H2</th>
                                                            <th>VAN_H3</th>
                                                            <th>VAN_H4</th>
                                                            <th>VAN_H5</th>
                                                            <th>VAN_H6</th>
                                                            <th>VAN_H7</th>
                                                            <th>VAN_H8</th>
                                                            <th>VAN_H9</th>
                                                            <th>VAN_H10</th>
                                                            <th>VAN_H11</th>
                                                            <th>VAN_H12</th>
                                                            <th>VAN_H13</th>
                                                            <th>VAN_H14</th>
                                                            <th>VAN_H15</th>
                                                            <th>VAN_H16</th>
                                                            <th>VAN_H17</th>
                                                            <th>VAN_H18</th>
                                                            <th>VAN_H19</th>
                                                            <th>VAN_H20</th>
                                                            <th>VAN_H21</th>
                                                            <th>VAN_H22</th>
                                                            <th>VAN_H23</th>
                                                            <th>VAN_H24</th>
                                                            <th>VAN_H25</th>
                                                            <th>VAN_H26</th>
                                                            <th>VAN_H27</th>
                                                            <th>VAN_H28</th>
                                                            <th>VAN_H29</th>
                                                            <th>VAN_H30</th>
                                                            <th>VAN_H31</th>
                                                            <th>VBN_H1</th>
                                                            <th>VBN_H2</th>
                                                            <th>VBN_H3</th>
                                                            <th>VBN_H4</th>
                                                            <th>VBN_H5</th>
                                                            <th>VBN_H6</th>
                                                            <th>VBN_H7</th>
                                                            <th>VBN_H8</th>
                                                            <th>VBN_H9</th>
                                                            <th>VBN_H10</th>
                                                            <th>VBN_H11</th>
                                                            <th>VBN_H12</th>
                                                            <th>VBN_H13</th>
                                                            <th>VBN_H14</th>
                                                            <th>VBN_H15</th>
                                                            <th>VBN_H16</th>
                                                            <th>VBN_H17</th>
                                                            <th>VBN_H18</th>
                                                            <th>VBN_H19</th>
                                                            <th>VBN_H20</th>
                                                            <th>VBN_H21</th>
                                                            <th>VBN_H22</th>
                                                            <th>VBN_H23</th>
                                                            <th>VBN_H24</th>
                                                            <th>VBN_H25</th>
                                                            <th>VBN_H26</th>
                                                            <th>VBN_H27</th>
                                                            <th>VBN_H28</th>
                                                            <th>VBN_H29</th>
                                                            <th>VBN_H30</th>
                                                            <th>VBN_H31</th>
                                                            <th>VCN_H1</th>
                                                            <th>VCN_H2</th>
                                                            <th>VCN_H3</th>
                                                            <th>VCN_H4</th>
                                                            <th>VCN_H5</th>
                                                            <th>VCN_H6</th>
                                                            <th>VCN_H7</th>
                                                            <th>VCN_H8</th>
                                                            <th>VCN_H9</th>
                                                            <th>VCN_H10</th>
                                                            <th>VCN_H11</th>
                                                            <th>VCN_H12</th>
                                                            <th>VCN_H13</th>
                                                            <th>VCN_H14</th>
                                                            <th>VCN_H15</th>
                                                            <th>VCN_H16</th>
                                                            <th>VCN_H17</th>
                                                            <th>VCN_H18</th>
                                                            <th>VCN_H19</th>
                                                            <th>VCN_H20</th>
                                                            <th>VCN_H21</th>
                                                            <th>VCN_H22</th>
                                                            <th>VCN_H23</th>
                                                            <th>VCN_H24</th>
                                                            <th>VCN_H25</th>
                                                            <th>VCN_H26</th>
                                                            <th>VCN_H27</th>
                                                            <th>VCN_H28</th>
                                                            <th>VCN_H29</th>
                                                            <th>VCN_H30</th>
                                                            <th>VCN_H31</th>
                                                        </>

                                                    }
                                                </tr>
                                            </thead>
                                            <tbody>

                                                {

                                                    listWarningFrame2?.map((item, index) => (
                                                        <tr key={index}>
                                                            <td>{index + 1}</td>

                                                            <td style={{ textAlign: "center" }}>{moment(item.viewTime).format("YYYY-MM-DD HH:mm:ss")}</td>

                                                            {selectedWarningType == 109 &&
                                                                <>
                                                                    <td>{item.van_H1}</td>
                                                                    <td>{item.van_H2}</td>
                                                                    <td>{item.van_H3}</td>
                                                                    <td>{item.van_H4}</td>
                                                                    <td>{item.van_H5}</td>
                                                                    <td>{item.van_H6}</td>
                                                                    <td>{item.van_H7}</td>
                                                                    <td>{item.van_H8}</td>
                                                                    <td>{item.van_H9}</td>
                                                                    <td>{item.van_H10}</td>
                                                                    <td>{item.van_H11}</td>
                                                                    <td>{item.van_H12}</td>
                                                                    <td>{item.van_H13}</td>
                                                                    <td>{item.van_H14}</td>
                                                                    <td>{item.van_H15}</td>
                                                                    <td>{item.van_H16}</td>
                                                                    <td>{item.van_H17}</td>
                                                                    <td>{item.van_H18}</td>
                                                                    <td>{item.van_H19}</td>
                                                                    <td>{item.van_H20}</td>
                                                                    <td>{item.van_H21}</td>
                                                                    <td>{item.van_H22}</td>
                                                                    <td>{item.van_H23}</td>
                                                                    <td>{item.van_H24}</td>
                                                                    <td>{item.van_H25}</td>
                                                                    <td>{item.van_H26}</td>
                                                                    <td>{item.van_H27}</td>
                                                                    <td>{item.van_H28}</td>
                                                                    <td>{item.van_H29}</td>
                                                                    <td>{item.van_H30}</td>
                                                                    <td>{item.van_H31}</td>
                                                                    <td>{item.vbn_H1}</td>
                                                                    <td>{item.vbn_H2}</td>
                                                                    <td>{item.vbn_H3}</td>
                                                                    <td>{item.vbn_H4}</td>
                                                                    <td>{item.vbn_H5}</td>
                                                                    <td>{item.vbn_H6}</td>
                                                                    <td>{item.vbn_H7}</td>
                                                                    <td>{item.vbn_H8}</td>
                                                                    <td>{item.vbn_H9}</td>
                                                                    <td>{item.vbn_H10}</td>
                                                                    <td>{item.vbn_H11}</td>
                                                                    <td>{item.vbn_H12}</td>
                                                                    <td>{item.vbn_H13}</td>
                                                                    <td>{item.vbn_H14}</td>
                                                                    <td>{item.vbn_H15}</td>
                                                                    <td>{item.vbn_H16}</td>
                                                                    <td>{item.vbn_H17}</td>
                                                                    <td>{item.vbn_H18}</td>
                                                                    <td>{item.vbn_H19}</td>
                                                                    <td>{item.vbn_H20}</td>
                                                                    <td>{item.vbn_H21}</td>
                                                                    <td>{item.vbn_H22}</td>
                                                                    <td>{item.vbn_H23}</td>
                                                                    <td>{item.vbn_H24}</td>
                                                                    <td>{item.vbn_H25}</td>
                                                                    <td>{item.vbn_H26}</td>
                                                                    <td>{item.vbn_H27}</td>
                                                                    <td>{item.vbn_H28}</td>
                                                                    <td>{item.vbn_H29}</td>
                                                                    <td>{item.vbn_H30}</td>
                                                                    <td>{item.vbn_H31}</td>
                                                                    <td>{item.vcn_H1}</td>
                                                                    <td>{item.vcn_H2}</td>
                                                                    <td>{item.vcn_H3}</td>
                                                                    <td>{item.vcn_H4}</td>
                                                                    <td>{item.vcn_H5}</td>
                                                                    <td>{item.vcn_H6}</td>
                                                                    <td>{item.vcn_H7}</td>
                                                                    <td>{item.vcn_H8}</td>
                                                                    <td>{item.vcn_H9}</td>
                                                                    <td>{item.vcn_H10}</td>
                                                                    <td>{item.vcn_H11}</td>
                                                                    <td>{item.vcn_H12}</td>
                                                                    <td>{item.vcn_H13}</td>
                                                                    <td>{item.vcn_H14}</td>
                                                                    <td>{item.vcn_H15}</td>
                                                                    <td>{item.vcn_H16}</td>
                                                                    <td>{item.vcn_H17}</td>
                                                                    <td>{item.vcn_H18}</td>
                                                                    <td>{item.vcn_H19}</td>
                                                                    <td>{item.vcn_H20}</td>
                                                                    <td>{item.vcn_H21}</td>
                                                                    <td>{item.vcn_H22}</td>
                                                                    <td>{item.vcn_H23}</td>
                                                                    <td>{item.vcn_H24}</td>
                                                                    <td>{item.vcn_H25}</td>
                                                                    <td>{item.vcn_H26}</td>
                                                                    <td>{item.vcn_H27}</td>
                                                                    <td>{item.vcn_H28}</td>
                                                                    <td>{item.vcn_H29}</td>
                                                                    <td>{item.vcn_H30}</td>
                                                                    <td>{item.vcn_H31}</td>

                                                                </>
                                                            }
                                                            {selectedWarningType == 108 &&
                                                                <>
                                                                    <td>{item.ia_H1}</td>
                                                                    <td>{item.ia_H2}</td>
                                                                    <td>{item.ia_H3}</td>
                                                                    <td>{item.ia_H4}</td>
                                                                    <td>{item.ia_H5}</td>
                                                                    <td>{item.ia_H6}</td>
                                                                    <td>{item.ia_H7}</td>
                                                                    <td>{item.ia_H8}</td>
                                                                    <td>{item.ia_H9}</td>
                                                                    <td>{item.ia_H10}</td>
                                                                    <td>{item.ia_H11}</td>
                                                                    <td>{item.ia_H12}</td>
                                                                    <td>{item.ia_H13}</td>
                                                                    <td>{item.ia_H14}</td>
                                                                    <td>{item.ia_H15}</td>
                                                                    <td>{item.ia_H16}</td>
                                                                    <td>{item.ia_H17}</td>
                                                                    <td>{item.ia_H18}</td>
                                                                    <td>{item.ia_H19}</td>
                                                                    <td>{item.ia_H20}</td>
                                                                    <td>{item.ia_H21}</td>
                                                                    <td>{item.ia_H22}</td>
                                                                    <td>{item.ia_H23}</td>
                                                                    <td>{item.ia_H24}</td>
                                                                    <td>{item.ia_H25}</td>
                                                                    <td>{item.ia_H26}</td>
                                                                    <td>{item.ia_H27}</td>
                                                                    <td>{item.ia_H28}</td>
                                                                    <td>{item.ia_H29}</td>
                                                                    <td>{item.ia_H30}</td>
                                                                    <td>{item.ia_H31}</td>
                                                                    <td>{item.ib_H1}</td>
                                                                    <td>{item.ib_H2}</td>
                                                                    <td>{item.ib_H3}</td>
                                                                    <td>{item.ib_H4}</td>
                                                                    <td>{item.ib_H5}</td>
                                                                    <td>{item.ib_H6}</td>
                                                                    <td>{item.ib_H7}</td>
                                                                    <td>{item.ib_H8}</td>
                                                                    <td>{item.ib_H9}</td>
                                                                    <td>{item.ib_H10}</td>
                                                                    <td>{item.ib_H11}</td>
                                                                    <td>{item.ib_H12}</td>
                                                                    <td>{item.ib_H13}</td>
                                                                    <td>{item.ib_H14}</td>
                                                                    <td>{item.ib_H15}</td>
                                                                    <td>{item.ib_H16}</td>
                                                                    <td>{item.ib_H17}</td>
                                                                    <td>{item.ib_H18}</td>
                                                                    <td>{item.ib_H19}</td>
                                                                    <td>{item.ib_H20}</td>
                                                                    <td>{item.ib_H21}</td>
                                                                    <td>{item.ib_H22}</td>
                                                                    <td>{item.ib_H23}</td>
                                                                    <td>{item.ib_H24}</td>
                                                                    <td>{item.ib_H25}</td>
                                                                    <td>{item.ib_H26}</td>
                                                                    <td>{item.ib_H27}</td>
                                                                    <td>{item.ib_H28}</td>
                                                                    <td>{item.ib_H29}</td>
                                                                    <td>{item.ib_H30}</td>
                                                                    <td>{item.ib_H31}</td>
                                                                    <td>{item.ic_H1}</td>
                                                                    <td>{item.ic_H2}</td>
                                                                    <td>{item.ic_H3}</td>
                                                                    <td>{item.ic_H4}</td>
                                                                    <td>{item.ic_H5}</td>
                                                                    <td>{item.ic_H6}</td>
                                                                    <td>{item.ic_H7}</td>
                                                                    <td>{item.ic_H8}</td>
                                                                    <td>{item.ic_H9}</td>
                                                                    <td>{item.ic_H10}</td>
                                                                    <td>{item.ic_H11}</td>
                                                                    <td>{item.ic_H12}</td>
                                                                    <td>{item.ic_H13}</td>
                                                                    <td>{item.ic_H14}</td>
                                                                    <td>{item.ic_H15}</td>
                                                                    <td>{item.ic_H16}</td>
                                                                    <td>{item.ic_H17}</td>
                                                                    <td>{item.ic_H18}</td>
                                                                    <td>{item.ic_H19}</td>
                                                                    <td>{item.ic_H20}</td>
                                                                    <td>{item.ic_H21}</td>
                                                                    <td>{item.ic_H22}</td>
                                                                    <td>{item.ic_H23}</td>
                                                                    <td>{item.ic_H24}</td>
                                                                    <td>{item.ic_H25}</td>
                                                                    <td>{item.ic_H26}</td>
                                                                    <td>{item.ic_H27}</td>
                                                                    <td>{item.ic_H28}</td>
                                                                    <td>{item.ic_H29}</td>
                                                                    <td>{item.ic_H30}</td>
                                                                    <td>{item.ic_H31}</td>

                                                                </>
                                                            }
                                                        </tr>
                                                    ))
                                                }

                                            </tbody>
                                        </table>
                                    }
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            {/* MODAL Add */}
            <ReactModal
                isOpen={isModalAddCAROpen}
                onRequestClose={() => {
                    setIsModalAddCAROpen(false);
                }}
                style={{
                    content: {
                        width: "50%", // Kích thước chiều rộng của modal (có thể điều chỉnh)
                        height: "90%", // Kích thước chiều cao của modal (có thể điều chỉnh)
                        margin: "auto", // Căn giữa modal
                        marginTop: "10px",
                    },
                }}
            >
                <h4
                    style={{
                        textAlign: "center",
                        backgroundColor: "#0A1A5C",
                        color: "#fff",
                        width: "100%",
                        padding: "5px", // Thay đổi kích thước màu nền bằng padding
                    }}
                    className="text-uppercase"
                >
                    {t('content.home_page.plan.add_title')}
                </h4>
                <br />
                <table className="table">
                    <tbody>
                        <tr>
                            <th scope="row">{t('content.create_date')}p</th>
                            <td className="col-10">
                                <input
                                    disabled
                                    type="text"
                                    className="form-control"
                                    name="createDate"
                                    value={createDate}
                                />
                            </td>
                        </tr>
                        <tr>
                            <th scope="row">ID</th>
                            <td>
                                <input
                                    disabled
                                    type="text"
                                    className="form-control"
                                    name="id"
                                />
                            </td>
                        </tr>
                        <tr>
                            <th scope="row">{t('content.home_page.plan.organization_create')}</th>
                            <td>
                                <input
                                    type="text"
                                    className="form-control"
                                    name="organizationCreate"
                                    onChange={handleInputChange}
                                />
                            </td>
                        </tr>
                        <tr>
                            <th>{t('content.home_page.plan.content')}</th>
                            <td>
                                <textarea
                                    className="form-control"
                                    name="content"
                                    rows="6"
                                    value={dataWarningCarAdd.content}
                                    onChange={handleInputChange}
                                    required={true}
                                    max={1000}
                                ></textarea>
                            </td>
                        </tr>
                        <tr>
                            <th>{t('content.home_page.plan.reason_and_measure')}</th>
                            <td>
                                <textarea
                                    disabled
                                    className="form-control"
                                    name="reasonMethod"
                                    rows="4"
                                ></textarea>
                            </td>
                        </tr>
                        <tr>
                            <th scope="row">{t('content.home_page.plan.organization_execution')}</th>
                            <td>
                                <input
                                    disabled
                                    type="text"
                                    className="form-control"
                                    name="organizationExecution"
                                />
                            </td>
                        </tr>
                        <tr>
                            <th scope="row">{t('content.home_page.plan.completion_time')}</th>
                            <td>
                                <input
                                    disabled
                                    type="text"
                                    className="form-control"
                                    name="completionTime"
                                />
                            </td>
                        </tr>
                        <tr>
                            <th scope="row">{t('content.home_page.plan.result_execution')}</th>
                            <td>
                                <input
                                    disabled
                                    type="text"
                                    className="form-control"
                                    name="resultExecution"
                                />
                            </td>
                        </tr>
                        <tr>
                            <th scope="row">{t('content.home_page.plan.organization_test')}</th>
                            <td>
                                <input
                                    disabled
                                    type="text"
                                    className="form-control"
                                    name="organizationTest"
                                />
                            </td>
                        </tr>
                    </tbody>
                </table>
                <div className="row">
                    <div style={{ marginLeft: "300px" }}>

                        <button
                            style={{
                                backgroundColor: "#0A1A5C",
                                color: "#fff",
                                width: "130px",
                                height: "40px",
                            }}
                            onClick={funcAddWarningCar}
                        >
                            {t('content.save')}
                        </button>

                        <button
                            style={{
                                backgroundColor: "#9DA3BE",
                                color: "#fff",
                                width: "130px",
                                height: "40px",
                                marginLeft: "15px"
                            }}
                            onClick={closeModalCAR}
                        >
                            {t('content.close')}
                        </button>

                    </div>
                </div>
            </ReactModal>




            <ReactModal
                isOpen={isModalOpen}
                onRequestClose={closeModal}
                contentLabel="Modal 1"
                style={{
                    content: {
                        width: "80%", // Kích thước chiều rộng của modal (có thể điều chỉnh)
                        height: "60%", // Kích thước chiều cao của modal (có thể điều chỉnh)
                        margin: "auto", // Căn giữa modal
                        marginTop: "10px",
                    },
                }}
            >
                <div className="table-container">
                    <table className="table" id="table">

                        <thead>
                            <tr height="40px">
                                <th style={{ width: "3%" }} className="text-uppercase"> {t('content.no')}</th>

                                <th style={{ width: "25%" }}>
                                    TÊN THIẾT BỊ

                                </th>
                                <th style={{ width: "12%" }}>
                                    NGÀY GỬI GẦN NHẤT
                                </th>



                            </tr>
                        </thead>

                        <tbody style={{ lineHeight: 1 }}>

                            {dataLostSignal?.map((item, index) => (
                                <tr
                                    key={index}
                                    height="30px"

                                >
                                    <td className="text-center">{index + 1}</td>
                                    <td className="text-left">{item.deviceName}</td>
                                    <td className="text-left">{moment(item.sentDateInstance).format('YYYY-MM-DD HH:mm:ss')}</td>

                                </tr>
                            )
                            )}

                        </tbody>

                    </table>
                </div>
            </ReactModal>
        </div >


    )
}

export default WarningLoad;