import { useState } from "react";
import { useEffect } from "react";
import { useRef } from "react";
import { Link, Route, Switch, useHistory, useLocation, useParams } from "react-router-dom/cjs/react-router-dom.min";
import { Calendar } from 'primereact/calendar';
import ProjectService from "../../../../../services/ProjectService";
import DeviceService from "../../../../../services/DeviceService";
import CustomerService from "../../../../../services/CustomerService";
import AuthService from "../../../../../services/AuthService";
import moment from "moment";
import CONS from "../../../../../constants/constant";
import AccessDenied from "../../../access-denied/AccessDenied";
import "./../index.css";
import UserService from "../../../../../services/UserService";
import { t } from "i18next";


const DeviceInfor = () => {
    const [userName] = useState(AuthService.getUserName());
    const [role] = useState(AuthService.getRoleName());
    const [changeTab, setChangeTab] = useState(true);
    const [changeTabList, setChangeTabList] = useState(true);
    const [type, setType] = useState();
    const [typeName, setTypeName] = useState(t('content.all'));
    const [routeType, setRouteType] = useState(0);
    const [image, setImage] = useState(0);
    const [equipmentTab, setEquipmentTab] = useState(1);
    const [fromDate, setFromDate] = useState(new Date());
    const [toDate, setToDate] = useState(new Date());
    const [warningLevel, setWarningLevel] = useState(0);
    const location = useLocation();
    const [listProjects, setListProjects] = useState([])
    const [listDevice, setListDevice] = useState([])
    const [projectId, setProjectId] = useState(0)
    const [listDefault, setListDefault] = useState([])
    const [listType, setListType] = useState([])
    const [devices, setListDevices] = useState([])
    const [device, setDevice] = useState(null)
    const [infoDevice, setInfoDevice] = useState(null)
    const [listWarning, setListWarning] = useState([])
    const history = useHistory();
    const imgObjectGrid = "/resources/image/icon-khoangcap.svg";
    const [tabIndex, setTabIndex] = useState(true)
    const [listDeviceLevelTwo, setListDeviceLevelTwo] = useState([])
    const [routeTypeDefault, setRouteTypeDefault] = useState(0)
    const [customerName, setCustomerName] = useState()
    const [projectName, setProjectName] = useState()
    const [accessDenied, setAccessDenied] = useState(false)
    // const [projectIds, setProjectIds] = useState("")

    const param = useParams();

    const funcChangeTab = (objectTypeId, typeClass, img) => {
        //setRouteType(routeTypeDefault)
        let typeTab = changeTab;
        if (tabIndex === false) {
            setTabIndex(true)
        } else {
            if (typeTab === true) {
                history.push({
                    pathname: "",
                    search: `?opjectTypeId=${objectTypeId}`
                });
                setTabIndex(true)
                setChangeTab(false);
                if (projectId == null) {
                    if (typeClass == 0) {
                        getListDeviceOneLevelByCusSys(param.customerId, objectTypeId)
                    } else {
                        getListDeviceTwoLevelByCusSys(param.customerId, objectTypeId)
                    }
                } else {
                    if (typeClass == 0) {
                        getListDeviceOneLevelByProSys(param.customerId, projectId, objectTypeId)
                    } else {
                        getListDeviceTwoLevelByProSys(param.customerId, projectId, objectTypeId)
                    }
                }
            };
            if (typeTab === false) {
                setTabIndex(false)
                setChangeTab(true);
            };
        }
        setImage(img)
    }

    // const funcChangeTabList = async (device) => {
    //     let typeTab = changeTabList;
    //     if (typeTab === true) {
    //         setDevice(device)
    //         await getInforDeviceByDeviceId(param.customerId, device.deviceId)
    //         setChangeTabList(false);
    //     };
    //     if (typeTab === false) {
    //         setChangeTabList(true);
    //     };
    // }

    const handleChange = e => {
        let string = e.target.id;
        let number = string.charAt(5);

        let node = document.getElementById("input" + number);
        if (node.parentElement) {
            node.parentElement.style.backgroundColor = "#0a1a5c";
        }
        for (let i = 1; i < 9; i++) {
            if (i != number) {
                let a = document.getElementById("input" + i);
                if (a.parentElement) {
                    a.parentElement.style.backgroundColor = "#fff";
                }
            }
        }
    };

    const funcEquipment = (value) => {
        setEquipmentTab(() => value);
    }

    const [warningSolar, setWarningSolar] = useState([
        {
            warningType: 201,
            warningName: 'Chạm đất',

        },
        {
            warningType: 202,
            warningName: 'Điện áp cao DC',

        },
        {
            warningType: 203,
            warningName: 'Mất kết nối AC',

        },
        {
            warningType: 204,
            warningName: 'Mất kết nối DC',
        },
        {
            warningType: 205,
            warningName: 'Mất nguồn lưới',

        },
        {
            warningType: 206,
            warningName: 'Đóng mở cửa',

        },
        {
            warningType: 207,
            warningName: 'Ngắt thủ công',

        },
        {
            warningType: 208,
            warningName: 'Nhiệt độ cao',

        },
        {
            warningType: 209,
            warningName: 'Tần số cao',

        },
        {
            warningType: 210,
            warningName: 'Tần số thấp',

        },
        {
            warningType: 211,
            warningName: 'Điện áp cao AC',

        },
        {
            warningType: 212,
            warningName: 'Điện áp thấp AC',

        },
        {
            warningType: 213,
            warningName: 'Hỏng cầu chì',

        },
        {
            warningType: 214,
            warningName: 'Nhiệt độ thấp',

        },
        {
            warningType: 215,
            warningName: 'Mất bộ nhớ',

        },
    ]);
    const [warningLoad, setWarningLoad] = useState([
        {
            warningType: CONS.WARNING_TYPE.NGUONG_AP_CAO,
            warningName: 'Điện áp cao',

        },
        {
            warningType: CONS.WARNING_TYPE.NGUONG_AP_THAP,
            warningName: 'Điện áp thấp',

        },
        {
            warningType: CONS.WARNING_TYPE.NHIET_DO_TIEP_XUC,
            warningName: 'Nhiệt độ tiếp xúc',

        },
        {
            warningType: CONS.WARNING_TYPE.LECH_PHA,
            warningName: 'Lệch pha',
        },
        {
            warningType: CONS.WARNING_TYPE.QUA_TAI,
            warningName: 'Quá tải',

        },
        {
            warningType: CONS.WARNING_TYPE.TAN_SO_THAP,
            warningName: 'Tần số thấp',

        },
        {
            warningType: CONS.WARNING_TYPE.TAN_SO_CAO,
            warningName: 'Tần số cao',

        },
        {
            warningType: CONS.WARNING_TYPE.MAT_NGUON_PHA,
            warningName: 'Mất nguồn',

        },
        {
            warningType: CONS.WARNING_TYPE.NGUONG_TONG_HAI,
            warningName: 'Sóng hài',

        },
        {
            warningType: CONS.WARNING_TYPE.DONG_TRUNG_TINH,
            warningName: 'Dòng trung tính',

        },
        {
            warningType: CONS.WARNING_TYPE.DONG_MO_CUA,
            warningName: 'Đóng mở cửa',

        },
        {
            warningType: CONS.WARNING_TYPE.COS_THAP_TONG,
            warningName: 'Hệ số công suất thấp',

        }
    ]);


    const [warningGrid, setWarningGrid] = useState([
        {
            warningType: 501,
            warningName: 'Độ ẩm',

        },
        {
            warningType: 502,
            warningName: 'Phóng điện',

        },
        {
            warningType: 503,
            warningName: 'Tần số thấp',

        },
        {
            warningType: 504,
            warningName: 'Tần số cao',
        },
        {
            warningType: 505,
            warningName: 'Sóng hài',

        },
        {
            warningType: 506,
            warningName: 'Quá tải tổng',

        },
        {
            warningType: 507,
            warningName: 'Quá tải nhánh',

        },
        {
            warningType: 508,
            warningName: 'Lệch pha tổng',

        },
        {
            warningType: 509,
            warningName: 'Lệch pha nhánh',

        },
        {
            warningType: 510,
            warningName: 'FI tủ RMU',

        },
        {
            warningType: 511,
            warningName: 'Khoang tổn thất',

        },
        {
            warningType: 512,
            warningName: 'Đóng mở cửa',

        },
        {
            warningType: 513,
            warningName: 'Mức dầu thấp',

        },
        {
            warningType: 514,
            warningName: 'Nhiệt độ',

        },
        {
            warningType: 515,
            warningName: 'Nhiệt độ dầu',

        },
        {
            warningType: 516,
            warningName: 'Mất điện tổng',

        },
        {
            warningType: 517,
            warningName: 'Mất điện nhánh',

        },
        {
            warningType: 518,
            warningName: 'Role gas',

        },
        {
            warningType: 519,
            warningName: 'Chạm vỏ',

        },
        {
            warningType: 520,
            warningName: 'Mức dầu cao',

        },
        {
            warningType: 521,
            warningName: 'Cảm biến hồng ngoại',

        },
        {
            warningType: 522,
            warningName: 'Điện áp cao',

        },
        {
            warningType: 523,
            warningName: 'Điện áp thấp',

        },
        {
            warningType: 524,
            warningName: 'COSφ tổng Thấp',

        },
        {
            warningType: 525,
            warningName: 'COSφ nhánh Thấp',

        },
        {
            warningType: 526,
            warningName: 'Áp suất nội bộ MBA',

        },
        {
            warningType: 527,
            warningName: 'ROLE nhiệt độ dầu',

        },
        {
            warningType: 528,
            warningName: 'Nhiệt độ cuộn dây',

        },
        {
            warningType: 529,
            warningName: 'Khí gas MBA',

        },
        {
            warningType: 530,
            warningName: 'Hệ số công suất thấp',

        },

    ]);

    const warningType = [{
        name: "TẦN SỐ THẤP",
        warning: true
    },
    {
        name: " TẦN SỐ CAO",
        warning: false
    },
    {
        name: "NHIỆT ĐỘ CAO",
        warning: true
    },
    {
        name: "LỆCH PHA",
        warning: false
    },
    {
        name: "QUÁ TẢI",
        warning: false
    },
    {
        name: "MẤT NGUỒN",
        warning: false
    },
    {
        name: "SÓNG HÀI",
        warning: true
    },
    {
        name: "ĐÓNG MỞ CỬA",
        warning: false
    }, {
        name: "HỆ SỐ CÔNG SUẤT THẤP",
        warning: true
    },

    {
        name: "ĐIỆN ÁP THẤP",
        warning: false
    },
    {
        name: "ĐIỆN ÁP THẤP",
        warning: false
    },
    {
        name: "ĐIỆN ÁP THẤP",
        warning: false
    },
    {
        name: "ĐIỆN ÁP THẤP",
        warning: false
    }
    ]

    const funcSetType = async (e) => {
        setType(() => e.target.value)
        if (e.target.value == 1) {
            setTypeName(t('content.home_page.load'));
        } else if (e.target.value == 2) {
            setTypeName(t('content.home_page.solar'));
        } else if (e.target.value == 5) {
            setTypeName(t('content.home_page.grid'));
        } else if (e.target.value == 3) {
            setTypeName(t('content.home_page.battery'));
        } else if (e.target.value == 4) {
            setTypeName(t('content.home_page.wind'));
        } else if (e.target.value == 0) {
            setTypeName(t('content.all'));
        }
        setProjectId(param.projectId != undefined ? param.projectId : 0)
        let proJId = param.projectId != undefined ? param.projectId : null
        let res = await DeviceService.getListObjectType(param.customerId, proJId, e.target.value)
        if (res.status == 200) {
            setListDevice(res.data)
            setListType(res.data)
        }

    }

    const funcSetWarningLevel = (typeSelected) => {
        setWarningLevel(() => typeSelected)
    }

    const funcGetEquipmentByType = (equipmentName, img) => {
        let site = document.getElementById("selectSite");
        let text = site.options[site.selectedIndex].text;
        setRouteType(() => text + " / " + equipmentName);
        setRouteTypeDefault(() => " / " + text + " / " + equipmentName);
        setImage(img);
    }

    const funcGetEquipmentByTypeGrid = (equipmentName) => {
        setRouteType(() => routeTypeDefault + " / " + equipmentName)
    }

    const onChangeValueSite = (e) => {
        if (e === 0) {
            setProjectId(0)
            getListDevice(param.customerId)
        }
        if (e !== 0) {
            getlistDeviceByProjectId(param.customerId, e)
            setProjectId(e)
        }
    }

    const getListProject = async (customerId) => {
        let res = await ProjectService.getProjectByCustomerId(customerId);
        if (res.status === 200) {
            setListProjects(res.data)
        }
    }

    const getListDevice = async (customerId) => {
        setType(0)
        let res = await DeviceService.getDeviceByCustomerId(customerId);
        if (res.status === 200) {
            setListDevice(res.data)
            setListDefault(res.data)
            setListType(res.data)
        }
    }

    const getlistDeviceByProjectId = async (customerId, projectId) => {
        setType(0)
        let res = await DeviceService.getDeviceByProjectId(customerId, projectId);
        if (res.status === 200) {
            setListDevice(res.data)
            setListDefault(res.data)
            setListType(res.data)
        }
    }

    const getListDeviceOneLevelByCusSys = async (customerId, objectTypeId) => {
        let res = await DeviceService.getListDeviceOneLevelByCusSys(customerId, objectTypeId);
        if (res.status === 200) {
            setListDevices(res.data)
        }
    }

    const getListDeviceOneLevelByProSys = async (customerId, projectId, objectTypeId) => {
        let res = await DeviceService.getListDeviceOneLevelByProSys(customerId, projectId, objectTypeId)
        if (res.status === 200) {
            setListDevices(res.data)
        }
    }

    const getListDeviceTwoLevelByCusSys = async (customerId, objectTypeId) => {
        let res = await DeviceService.getListDeviceTwoLevelByCusSys(customerId, objectTypeId);
        if (res.status === 200) {
            setListDevices(res.data)
        }
    }

    const getListDeviceTwoLevelByProSys = async (customerId, projectId, objectTypeId) => {
        let res = await DeviceService.getListDeviceTwoLevelByProSys(customerId, projectId, objectTypeId);
        if (res.status === 200) {
            setListDevices(res.data)
        }
    }

    const getInforDeviceByDeviceId = async (customerId, deviceId) => {
        let res = await DeviceService.getInforDeviceByDeviceId(customerId, deviceId)
        if (res.status === 200) {
            setInfoDevice(res.data[1])
            setListWarning(res.data[0])
        }
    }

    const onValueImage = (name) => {
        let src = ""
        if (name != null) {
            if (name == "Tủ điện hạ thế") {
                src = "/resources/image/icon-tudienhathe.svg"
            }
            if (name == "Inverter") {
                src = "/resources/image/icon-inverter.svg"
            }
            if (name === "Combiner") {
                src = "/resources/image/icon-dccombinerbox.svg"
            }
            if (name === "String") {
                src = "/resources/image/icon-string.svg"
            }
            if (name === "Tủ trung thế") {
                src = "/resources/image/icon-khoangtutrungthe.svg"
            }
            if (name.includes("Khoang")) {
                src = "/resources/image/icon-khoangcap.svg"
            }
            if (name.includes("Tủ hạ")) {
                src = "/resources/image/icon-tudienhathe.svg"
            }
        }

        return src
    }

    const showListDeviceByObjectType = async (objectTypeId, systemTypeId, objectTypeName) => {
        let type = tabIndex
        if (type === true) {
            setTabIndex(false)
            history.push({
                pathname: "",
                search: `?objectTypeId=${objectTypeId}&systemTypeId=${systemTypeId}&opjectName=${objectTypeName}`
            });

            let res = await DeviceService.getDeviceByObjectTypeId(param.customerId, objectTypeId, systemTypeId, objectTypeName, projectId);
            if (res.status === 200) {
                setListDeviceLevelTwo(res.data)
            }

        } else {
            setTabIndex(true)
        }

    }

    const onFilterByStatusDevice = (typeStatus) => {
        if (typeStatus == 0) {
            if (type == 0) {
                setListDevice(listDefault)
            } else {
                setListDevice(listType)
            }
        }
        if (typeStatus == 1) {
            let list = listType.filter(device => device.status == "active")
            setListDevice(list);
        }
        if (typeStatus == 2) {
            let list = listType.filter(device => device.status == "warning")
            setListDevice(list);
        }
        if (typeStatus == 3) {
            let list = listType.filter(device => device.status == "offline")
            setListDevice(list);
        }

    }

    const onFilterDeviceByName = (e) => {
        let name = e.target.value
        if (name == "") {
            setListDevice(listDefault)
        } else {
            let deviceFilter = listDefault.filter(device => device.objectTypeName.toLowerCase().includes(name.toLowerCase()))
            setListDevice(deviceFilter)
        }
    }

    const getListObjectType = async (customerId, projectIds, typeSystem) => {
        setProjectId(param.projectId != undefined ? param.projectId : 0)
        let proJId = param.projectId != undefined ? param.projectId : null
        let res = await DeviceService.getListObjectType(param.customerId, proJId, typeSystem, projectIds)
        let resCus = await ProjectService.getProjectByCustomerId(param.customerId)
        if (res.status == 200) {
            if (resCus.data.length != 0) {
                setCustomerName(resCus.data[0].customerName)
                if (param.projectId != undefined) {
                    let resPro = await ProjectService.getProject(proJId)
                    setProjectName(resPro.data.projectName)
                } else {
                    setProjectName(t('content.home_page.all_site'))
                }
                setListDevice(res.data)
                setListDefault(res.data)
                setListType(res.data)
            }
        }
    }

    const checkAuthorization = async () => {
        let response = await UserService.getUserByUsername();
        let userData = null;
        if (response.status === 200) {
            userData = response.data;
            setType(userData.prioritySystem);
            if (userData.prioritySystem == 1) {
                setTypeName(t('content.home_page.load'));
            } else if (userData.prioritySystem == 2) {
                setTypeName(t('content.home_page.solar'));
            } else if (userData.prioritySystem == 5) {
                setTypeName(t('content.home_page.grid'));
            } else if (userData.prioritySystem == 3) {
                setTypeName(t('content.home_page.battery'));
            } else if (userData.prioritySystem == 4) {
                setTypeName(t('content.home_page.wind'));
            } else if (userData.prioritySystem == 0) {
                setTypeName(t('content.all'));
            }
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
                getListObjectType(param.customerId, "", userData.prioritySystem)
            }
            if (role === "ROLE_MOD") {
                let customerIds = ""

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
                        setAccessDenied(false)
                        getListObjectType(param.customerId, "", userData.prioritySystem)
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
                                getListObjectType(param.customerId, param.projectId, userData.prioritySystem)
                            }
                        } else {
                            setAccessDenied(false)
                            getListObjectType(param.customerId, projIds, userData.prioritySystem)
                        }
                    }
                }
            }
        }
    }

    useEffect(() => {
        checkAuthorization()
        setChangeTab(true)
        document.title = "Thông tin thiết bị";
    }, [param.customerId, param.projectId]);

    return (
        <>
            {!accessDenied ?
                changeTab === true ?
                    <>
                        <div className="position-block">
                            <div className="position-block">
                                <div className="float-left ml-2"><strong className="font-route">{customerName}/ {projectName} / {typeName}</strong></div>
                            </div>
                        </div>
                        <div className="div-list-equipment">
                            <div className="mt-2" style={{ width: "100%", height: "5px", backgroundColor: "#0a1a5c" }} ></div>
                            <div className="header-infor mt-1">
                                <div className="system-type">
                                    <div className="radio-tabs">
                                        <label className="radio-tabs__field text-uppercase" >
                                            <input type="radio" name="radio-tabs" value={0} className="radio-tabs__input" checked={type == 0 ? true : false} onChange={(e) => funcSetType(e)} />
                                            <span className="radio-tabs__text">
                                                <img src="/resources/image/icon-load-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                                {t('content.all')}</span>
                                        </label>
                                        <label className="radio-tabs__field text-uppercase" >
                                            <input type="radio" name="radio-tabs" value={1} className="radio-tabs__input" checked={type == 1 ? true : false} onChange={(e) => funcSetType(e)} />
                                            <span className="radio-tabs__text">
                                                <img src="/resources/image/icon-load-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                                {t('content.home_page.load')}</span>
                                        </label>
                                        <label className="radio-tabs__field text-uppercase" >
                                            <input type="radio" name="radio-tabs" value={2} className="radio-tabs__input" checked={type == 2 ? true : false} onChange={(e) => funcSetType(e)} />
                                            <span className="radio-tabs__text">
                                                <img src="/resources/image/icon-solar-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                                {t('content.home_page.solar')}</span>
                                        </label>
                                        <label className="radio-tabs__field text-uppercase" >
                                            <input type="radio" name="radio-tabs" value={5} className="radio-tabs__input" checked={type == 5 ? true : false} onChange={(e) => funcSetType(e)} />
                                            <span className="radio-tabs__text">
                                                <img src="/resources/image/icon-grid-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                                {t('content.home_page.grid')}</span>
                                        </label>
                                        <label className="radio-tabs__field text-uppercase" >
                                            <input type="radio" name="radio-tabs" value={3} className="radio-tabs__input" checked={type == 3 ? true : false} onChange={(e) => funcSetType(e)} />
                                            <span className="radio-tabs__text">
                                                <img src="/resources/image/icon-battery-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                                {t('content.home_page.battery')}</span>
                                        </label>
                                        <label className="radio-tabs__field text-uppercase" >
                                            <input type="radio" name="radio-tabs" value={4} className="radio-tabs__input" checked={type == 4 ? true : false} onChange={(e) => funcSetType(e)} />
                                            <span className="radio-tabs__text">
                                                <img src="/resources/image/icon-wind-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                                {t('content.home_page.wind')}</span>
                                        </label>
                                    </div>
                                    <div className="position-relative mt-1"><input type="text" className="" name="equipment-name" placeholder={t('content.home_page.search')} onChange={(e) => onFilterDeviceByName(e)} />
                                        <i className="fa fa-search position-absolute" style={{ left: "160px", top: "10px" }}></i>
                                        <div className="type-status">
                                            <div className="status">
                                                <button><div className="all" onClick={() => onFilterByStatusDevice(0)}><i className="icon fa-solid fa-arrows-up-down-left-right"></i></div></button>
                                                <span>{t('content.all')}</span>

                                            </div>
                                            <div className="status">
                                                <button><div className="online" onClick={() => onFilterByStatusDevice(1)}><i className="icon fa-solid fa-check" style={{ color: "white" }}></i></div></button>
                                                <span>{t('content.home_page.normal')}</span>
                                            </div>
                                            <div className="status">

                                                <button><div className="warning" onClick={() => onFilterByStatusDevice(2)}><i className="icon fa-solid fa-triangle-exclamation" style={{ color: "white" }}></i></div></button>
                                                <span>{t('content.warning')}</span>
                                            </div>
                                            <div className="status">
                                                <button><div className="offline" onClick={() => onFilterByStatusDevice(3)}><i className="icon fa-solid fa-xmark" style={{ color: "white" }}></i></div></button>
                                                <span>{t('content.home_page.lost_signal')}</span>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                            </div>
                        </div>

                        {/* <div className="header-infor">
                    <div className="system-type">
                        <div className="radio-tabs">
                            <label className="radio-tabs__field text-uppercase" >
                                <input type="radio" name="radio-tabs" value={0} className="radio-tabs__input" defaultChecked />
                                <span className="radio-tabs__text">
                                    <img src="/resources/image/icon-load-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                    ALL TYPE</span>
                            </label>
                            <label className="radio-tabs__field text-uppercase" >
                                <input type="radio" name="radio-tabs" value={1} className="radio-tabs__input" />
                                <span className="radio-tabs__text">
                                    <img src="/resources/image/icon-load-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                    LOAD</span>
                            </label>
                            <label className="radio-tabs__field text-uppercase" >
                                <input type="radio" name="radio-tabs" value={2} className="radio-tabs__input" />
                                <span className="radio-tabs__text">
                                    <img src="/resources/image/icon-solar-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                    SOLAR</span>
                            </label>
                            <label className="radio-tabs__field text-uppercase" >
                                <input type="radio" name="radio-tabs" value={5} className="radio-tabs__input" />
                                <span className="radio-tabs__text">
                                    <img src="/resources/image/icon-grid-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                    GRID</span>
                            </label>
                            <label className="radio-tabs__field text-uppercase" >
                                <input type="radio" name="radio-tabs" value={3} className="radio-tabs__input" />
                                <span className="radio-tabs__text">
                                    <img src="/resources/image/icon-battery-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                    BATTERY</span>
                            </label>
                            <label className="radio-tabs__field text-uppercase" >
                                <input type="radio" name="radio-tabs" value={4} className="radio-tabs__input" />
                                <span className="radio-tabs__text">
                                    <img src="/resources/image/icon-wind-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                    WIND</span>
                            </label>
                        </div>
                        <div className="position-relative mt-1"><input type="text" className="" name="equipment-name" placeholder="   Tìm kiếm...." onChange={(e) => onFilterDeviceByName(e)} />
                            <i className="fa fa-search position-absolute" style={{ left: "160px", top: "10px" }}></i>
                            <div className="type-status">
                                <div className="status">
                                    <button><div className="all" onClick={() => onFilterByStatusDevice(0)}><i className="icon fa-solid fa-arrows-up-down-left-right"></i></div></button>
                                    <span>TẤT CẢ</span>

                                </div>
                                <div className="status">
                                    <button><div className="online" onClick={() => onFilterByStatusDevice(1)}><i class="icon fa-solid fa-check" style={{ color: "white" }}></i></div></button>
                                    <span>BÌNH THƯỜNG</span>
                                </div>
                                <div className="status">

                                    <button><div className="warning" onClick={() => onFilterByStatusDevice(2)}><i class="icon fa-solid fa-triangle-exclamation" style={{ color: "white" }}></i></div></button>
                                    <span>CẢNH BÁO</span>
                                </div>
                                <div className="status">
                                    <button><div className="offline" onClick={() => onFilterByStatusDevice(3)}><i class="icon fa-solid fa-xmark"></i></div></button>
                                    <span>MẤT TÍN HIỆU</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div> */}
                        <div>
                            <div className="div-equipment mt-1 mb-1">
                                {
                                    listDevice?.map((item, index) => (
                                        <span key={index} className="p-2">
                                            <Link className="button-icon" to={`/${param.customerId}/${projectId}/device-information/objectTypeTwoLv1/${item.objectTypeId}/${type}`} title="Thông tin chi tiết" key={index}>
                                                <div className="list-equipment" key={index}>
                                                    <div className="header-equipment mb-4">
                                                        <div className="float-left">
                                                            <div className={item.status}></div>
                                                        </div>
                                                        <div className="float-right number">{t('content.home_page.device.no_device')}: {item.countDevice}</div>
                                                    </div>
                                                    <div className="body-equipment">
                                                        <img src={item.img != null ? item.img : "/resources/image/icon-khac.svg"} height="200px"></img>
                                                    </div>
                                                    <div className="footer-equipment">
                                                        <p>{item.objectTypeName}</p>
                                                    </div>
                                                </div>
                                            </Link>
                                        </span>
                                    ))

                                }
                            </div>
                        </div>
                    </>
                    : changeTabList === true &&
                    <>
                        <div className="position-block">
                            <div className="position-block">
                                <div className="float-left ml-2"><strong className="font-route">{routeType}</strong></div>
                                <div className="float-right">
                                    <button className="button-back" onClick={() => funcChangeTab()}>
                                        <img src="/resources/image/icon-back.svg" height="30px"></img>
                                    </button>
                                </div>
                            </div>
                        </div>
                        <div className="div-list-equipment">
                            <div className="mt-2" style={{ width: "100%", height: "5px", backgroundColor: "#0a1a5c" }} ></div>
                            <div className="position-relative mt-1"><input type="text" className="" name="equipment-name" placeholder={t('content.home_page.search')} />
                                <i className="fa fa-search position-absolute" style={{ left: "160px", top: "10px" }}></i>
                            </div>


                            <div className="div-list-equipment">

                                {tabIndex === true ?
                                    devices?.map((device, index) => (
                                        <span key={index}>
                                            {device.systemTypeId != 5 && device.systemTypeId != 1 || (device.systemTypeId == 1 && device.typeClass == 0) ?
                                                <Link className="button-icon" to={`/${param.customerId}/device-information/${device.deviceId}`} title="Thông tin chi tiết" key={index}>
                                                    <div className="item-equipment position-relative">
                                                        <div className="header-item-equipment mb-4">
                                                            <div className={device.operatingStatus}></div>
                                                            <div className="text-center number text-color-ses-2">{device.deviceName}</div>
                                                        </div>
                                                        {device.systemTypeId == 1 &&
                                                            <div className="body-equipment ml-5 d-flex">
                                                                <div className="float-left">
                                                                    <p>{t('content.project')}</p>
                                                                    <p>{t('content.home_page.device.total_active_power')}</p>
                                                                    <p>{t('content.home_page.device.total_reactive_power')}</p>
                                                                    <p>{t('content.home_page.device.voltage')}</p>
                                                                    <p>{t('content.home_page.device.current')}</p>
                                                                    <p>{t('content.home_page.device.cosphi')}</p>
                                                                </div>
                                                                {device.operatingStatus !== "offline" ?
                                                                    <div className="float-left ml-2">
                                                                        <p className="text-color-ses-1">{device.projectName}</p>
                                                                        <p className="text-color-ses-1">{device.ptotal != null ? device.ptotal : "-"} kW</p>
                                                                        <p className="text-color-ses-1">{device.qtotal >= 0 ? device.qtotal : "-"} kVAr</p>
                                                                        <p className="text-color-ses-1">{device.uab >= 0 ? device.uab : "-"} V</p>
                                                                        <p className="text-color-ses-1">{device.ia >= 0 ? device.ia : "-"} A</p>
                                                                        <p className="text-color-ses-1">{device.pfa >= 0 ? device.pfa : "-"}</p>
                                                                    </div>
                                                                    :
                                                                    <div className="float-left ml-2">
                                                                        <p className="text-color-ses-1">{device.projectName}</p>
                                                                        <p className="text-color-ses-1">- kW</p>
                                                                        <p className="text-color-ses-1">- kVAr</p>
                                                                        <p className="text-color-ses-1">- V</p>
                                                                        <p className="text-color-ses-1">- A</p>
                                                                        <p className="text-color-ses-1">-</p>
                                                                    </div>
                                                                }
                                                            </div>
                                                        }
                                                        {(device.deviceType === 1 && device.systemTypeId === 2) &&
                                                            <div className="body-equipment ml-5 d-flex">
                                                                <div className="float-left">
                                                                    <p>{t('content.project')}</p>
                                                                    <p>{t('content.home_page.device.total_active_power')}</p>
                                                                    <p>{t('content.home_page.device.total_reactive_power')}</p>
                                                                    <p>{t('content.home_page.device.voltage')}</p>
                                                                    <p>{t('content.home_page.device.current')}</p>
                                                                    <p>{t('content.home_page.device.cosphi')}</p>
                                                                </div>
                                                                {device.operatingStatus !== "offline" ?
                                                                    <div className="float-left ml-2">
                                                                        <p className="text-color-ses-1">{device.projectName}</p>
                                                                        <p className="text-color-ses-1">{device.ptotal != null ? device.ptotal : "-"} kW</p>
                                                                        <p className="text-color-ses-1">{device.qtotal > 0 ? device.qtotal : "-"} kVAr</p>
                                                                        <p className="text-color-ses-1">{device.uab > 0 ? device.uab : "-"} V</p>
                                                                        <p className="text-color-ses-1">{device.ia > 0 ? device.ia : "-"} A</p>
                                                                        <p className="text-color-ses-1">{device.pfa > 0 ? device.pfa : "-"}</p>
                                                                    </div>
                                                                    :
                                                                    <div className="float-left ml-2">
                                                                        <p className="text-color-ses-1">{device.projectName}</p>
                                                                        <p className="text-color-ses-1">- kW</p>
                                                                        <p className="text-color-ses-1">- kVAr</p>
                                                                        <p className="text-color-ses-1">- V</p>
                                                                        <p className="text-color-ses-1">- A</p>
                                                                        <p className="text-color-ses-1">-</p>
                                                                    </div>
                                                                }
                                                            </div>
                                                        }
                                                        {(device.deviceType === 3 && device.systemTypeId === 2) &&
                                                            <div className="body-equipment ml-5 d-flex">
                                                                <div className="float-left">
                                                                    <p>{t('content.project')}</p>
                                                                    <p>{t('content.home_page.device.total_active_power')}</p>
                                                                    <p>{t('content.home_page.device.current')}</p>
                                                                    <p>{t('content.home_page.device.voltage')}</p>


                                                                </div>
                                                                {device.operatingStatus !== "offline" ?
                                                                    <div className="float-left ml-2">
                                                                        <p className="text-color-ses-1">{device.projectName}</p>
                                                                        <p className="text-color-ses-1">{device.pdcCombiner != null ? device.pdcCombiner : "-"} kW</p>
                                                                        <p className="text-color-ses-1">{device.idcCombiner > 0 ? device.idcCombiner : "-"} A</p>
                                                                        <p className="text-color-ses-1">{device.vdcCombiner > 0 ? device.vdcCombiner : "-"} V</p>
                                                                    </div>
                                                                    :
                                                                    <div className="float-left ml-2">
                                                                        <p className="text-color-ses-1">{device.projectName}</p>
                                                                        <p className="text-color-ses-1">- kW</p>
                                                                        <p className="text-color-ses-1">- A</p>
                                                                        <p className="text-color-ses-1">- V</p>
                                                                    </div>
                                                                }
                                                            </div>
                                                        }
                                                        {(device.deviceType === 4 && device.systemTypeId === 2) &&
                                                            <div className="body-equipment ml-5 d-flex">
                                                                <div className="float-left">
                                                                    <p>{t('content.project')}</p>
                                                                    <p>{t('content.home_page.device.total_active_power')}</p>
                                                                    <p>{t('content.home_page.device.current')}</p>
                                                                    <p>{t('content.home_page.device.voltage')}</p>
                                                                    <p>T</p>
                                                                </div>
                                                                {device.operatingStatus !== "offline" ?
                                                                    <div className="float-left ml-2">
                                                                        <p className="text-color-ses-1">{device.projectName}</p>
                                                                        <p className="text-color-ses-1">{device.pdcStr != null ? device.pdcStr : "-"} kW</p>
                                                                        <p className="text-color-ses-1">{device.idcStr > 0 ? device.idcStr : "-"} A</p>
                                                                        <p className="text-color-ses-1">{device.vdcStr > 0 ? device.vdcStr : "-"} V</p>
                                                                        <p className="text-color-ses-1">{device.t > 0 ? device.t : "-"} °C</p>
                                                                    </div>
                                                                    :
                                                                    <div className="float-left ml-2">
                                                                        <p className="text-color-ses-1">{device.projectName}</p>
                                                                        <p className="text-color-ses-1">- kW</p>
                                                                        <p className="text-color-ses-1">- A</p>
                                                                        <p className="text-color-ses-1">- V</p>
                                                                        <p className="text-color-ses-1">- °C</p>
                                                                    </div>
                                                                }
                                                            </div>
                                                        }
                                                        <div className="position-absolute" style={{ top: "70%", left: "80%" }}>
                                                            <img src={image} height="50px"></img>
                                                        </div>
                                                    </div>
                                                </Link>
                                                :
                                                <div className="list-equipment-object-grid mt-1 mb-1" key={index}>
                                                    <div className="list-equipment" onClick={() => {
                                                        showListDeviceByObjectType(device.objectTypeId, device.systemTypeId, device.objectTypeName)
                                                        funcGetEquipmentByTypeGrid(device.objectTypeName)
                                                    }}>
                                                        <div className="header-equipment mb-4">
                                                            <div className="float-left">
                                                                <div className={device.status}></div>
                                                            </div>
                                                            <div className="float-right number">{device.countDevice}</div>
                                                        </div>
                                                        <div className="body-equipment">
                                                            {device.systemTypeId == 1 && device.typeClass == 1 &&
                                                                <img className="" alt="Ảnh" src="/resources/image/icon-tudienhathe.svg" height="200px"></img>
                                                            }
                                                            {device.systemTypeId == 5 &&
                                                                <img className="" alt="Ảnh" src="/resources/image/icon-khoangtutrungthe.svg" height="200px"></img>
                                                            }
                                                        </div>
                                                        <div className="footer-equipment">
                                                            <p>{device.objectTypeName}</p>
                                                        </div>
                                                    </div>
                                                </div>

                                            }

                                        </span>
                                    ))
                                    :
                                    listDeviceLevelTwo?.map((device, index) => (
                                        <span>

                                            <Link className="button-icon" to={`/${param.customerId}/device-information/${device.deviceId}`} title="Thông tin chi tiết">
                                                <div className="item-equipment position-relative"
                                                    key={index}>

                                                    <div className="header-item-equipment mb-4">
                                                        <div className={device.statusDevice}></div>
                                                        <div className="text-center number text-color-ses-2">{device.deviceName}</div>
                                                    </div>
                                                    {device.systemTypeId == 5 &&
                                                        <div className="body-equipment ml-5">
                                                            <div className="float-left">
                                                                <p>{t('content.project')}</p>
                                                                <p>T</p>
                                                                <p>H</p>
                                                                <p>Uab</p>
                                                                <p>Ia</p>
                                                                <p>PD Indicator</p>
                                                            </div>
                                                            {device.statusDevice !== "offline" ?
                                                                <div className="float-left ml-2">
                                                                    <p className="text-color-ses-1">{device.projectName}</p>
                                                                    <p className="text-color-ses-1">{device.t >= 0 ? device.t : "-"} °C</p>
                                                                    <p className="text-color-ses-1">{device.h >= 0 ? device.h : "-"} %</p>
                                                                    <p className="text-color-ses-1">{device.uab >= 0 ? device.uab : "-"} V</p>
                                                                    <p className="text-color-ses-1">{device.ia >= 0 ? device.ia : "-"} A</p>
                                                                    <p className="text-color-ses-1">{device.indicator >= 0 ? device.indicator : "-"}</p>
                                                                </div>
                                                                :
                                                                <div className="float-left ml-2">
                                                                    <p className="text-color-ses-1">{device.projectName}</p>
                                                                    <p className="text-color-ses-1">- °C</p>
                                                                    <p className="text-color-ses-1">- %</p>
                                                                    <p className="text-color-ses-1">- V</p>
                                                                    <p className="text-color-ses-1">- A</p>
                                                                    <p className="text-color-ses-1">- </p>
                                                                </div>
                                                            }
                                                            <div className="position-absolute" style={{ top: "70%", left: "80%" }}>
                                                                <img src="/resources/image/icon-khoangcap.svg" height="50px"></img>
                                                            </div>
                                                        </div>
                                                    }
                                                    {device.systemTypeId == 1 &&
                                                        <div className="body-equipment ml-5 d-flex">
                                                            <div className="float-left">
                                                                <p>{t('content.project')}</p>
                                                                <p>{t('content.home_page.device.total_active_power')}</p>
                                                                <p>{t('content.home_page.device.total_reactive_power')}</p>
                                                                <p>{t('content.home_page.device.voltage')}</p>
                                                                <p>{t('content.home_page.device.current')}</p>
                                                                <p>{t('content.home_page.device.cosphi')}</p>
                                                            </div>
                                                            {device.statusDevice !== "offline" ?
                                                                <div className="float-left ml-2">
                                                                    <p className="text-color-ses-1">{device.projectName}</p>
                                                                    <p className="text-color-ses-1">{device.ptotal != null ? device.ptotal : "-"} kW</p>
                                                                    <p className="text-color-ses-1">{device.qtotal >= 0 ? device.qtotal : "-"} kVAr</p>
                                                                    <p className="text-color-ses-1">{device.uab >= 0 ? device.uab : "-"} V</p>
                                                                    <p className="text-color-ses-1">{device.ia >= 0 ? device.ia : "-"} A</p>
                                                                    <p className="text-color-ses-1">{device.pfa >= 0 ? device.pfa : "-"}</p>
                                                                </div>
                                                                :
                                                                <div className="float-left ml-2">
                                                                    <p className="text-color-ses-1">{device.projectName}</p>
                                                                    <p className="text-color-ses-1">- kW</p>
                                                                    <p className="text-color-ses-1">- kVAr</p>
                                                                    <p className="text-color-ses-1">- V</p>
                                                                    <p className="text-color-ses-1">- A</p>
                                                                    <p className="text-color-ses-1">-</p>
                                                                </div>
                                                            }
                                                            <div className="position-absolute" style={{ top: "70%", left: "80%" }}>
                                                                <img src="/resources/image/icon-tudienhathe.svg" height="50px"></img>
                                                            </div>
                                                        </div>
                                                    }
                                                </div>
                                            </Link>
                                        </span>
                                    ))
                                }

                            </div>

                        </div>
                    </>
                :
                <AccessDenied></AccessDenied>
            }
        </>
    )
}

export default DeviceInfor;