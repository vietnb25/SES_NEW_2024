import { useEffect } from "react";
import HomeRouters from "./home.routes";
import "./index.css";
import { Link, Route, Switch, useHistory, useParams, useLocation } from "react-router-dom/cjs/react-router-dom.min";
import { useState } from "react";
import CustomerService from "../../../services/CustomerService";
import AuthService from "../../../services/AuthService";
import CONS from "../../../constants/constant";
import { useTranslation } from 'react-i18next';
import i18n from "i18next";
import ProjectService from "../../../services/ProjectService";
import MainTain from "../maintain";

const $ = window.$;
const Home = () => {
    const { t } = useTranslation();
    const history = useHistory();
    const param = useParams();
    const location = useLocation();
    const [changeTabUser, setChangeTabUser] = useState(true);
    const [isLoading, setIsLoading] = useState(true);
    const [customers, setCustomers] = useState([]);
    const [customersSearch, setCustomersSearch] = useState([]);
    const [customerId, setCustomerId] = useState();
    const [projectId, setProjectId] = useState("");
    const [statusInput, setStatusInput] = useState(true);
    const [role] = useState(AuthService.getRoleName());
    const [userName] = useState(AuthService.getUserName());
    const [active, setActiveMenu] = useState(0);
    const [projects, setProjects] = useState([]);
    const [showListProject, setShowListProject] = useState();
    const [linkTo, setLinkTo] = useState(`/${customerId}`)
    const [customersDefault, setCustomersDefault] = useState([])
    const [accessDenied, setAccessDenied] = useState(false)



    const changeTab = async () => {
        let typeTab = changeTabUser;
        if (typeTab === true) {
            setChangeTabUser(false);
        };
        if (typeTab === false) {
            setChangeTabUser(true);
        };
    }

    const setActive = async () => {
        let path = window.location.pathname;
        if (path.includes("system-map")) {
            setActiveMenu(CONS.TAB_MENU.SYSTEM_MAP)
        } else if (path.includes("chart")) {
            setActiveMenu(CONS.TAB_MENU.CHART)
        } else if (path.includes("setting")) {
            setActiveMenu(CONS.TAB_MENU.SETTING_SHIFT)
        } else if (path.includes("receiver-warning")) {
            setActiveMenu(CONS.TAB_MENU.SETTING_SHIFT)
        } else if (path.includes("warning")) {
            setActiveMenu(CONS.TAB_MENU.WARNING)
        } else if (path.includes("setting-warning")) {
            setActiveMenu(CONS.TAB_MENU.SETTING_WARNING)
        } else if (path.includes("data-simulation")) {
            setActiveMenu(CONS.TAB_MENU.SETTING_SHIFT)
        } else if (path.includes("report")) {
            setActiveMenu(CONS.TAB_MENU.REPORT)
        } else if (path.includes("support")) {
            setActiveMenu(CONS.TAB_MENU.SUPPORT)
        } else if (path.includes("diagnose")) {
            setActiveMenu(CONS.TAB_MENU.DIAGNOSE)
        } else if (path.includes("manufacture")) {
            setActiveMenu(CONS.TAB_MENU.MANUFACTURE)
        } else if (path.includes("plan")) {
            setActiveMenu(CONS.TAB_MENU.PLAN)
        } else if (path.includes("device-information")) {
            setActiveMenu(CONS.TAB_MENU.DEVICE_INFORMATION)
        } else {
            setActiveMenu(CONS.TAB_MENU.OVERVIEW)
        }

        let cusId = path.split("/");
        if (cusId.length >= 2) {
            if (cusId[1] != "") {
                setCustomerId(cusId[1]);
            } else {
                if (customers.length > 0) {
                    setCustomerId(customers[0].customerId)
                }
            }
        } else {
            if (customers.length > 0) {
                setCustomerId(customers[0].customerId)
            }

        }

    }

    const getCustomers = async () => {
        let cId = location.pathname.split("/")[1]
        let pId = location.pathname.split("/")[2];
        setStatusInput(true);
        if (role === "ROLE_ADMIN") {
            let res = await CustomerService.getListCustomer();
            if (res.status === 200 && res.data !== '') {
                setCustomers(res.data);
                setCustomersDefault(res.data);
                if (cId !== "") {
                    if (pId == "" || pId == undefined || isNaN(pId)) {
                        setCustomerId(cId);
                        setLinkTo(`/${cId}`)
                    } else {
                        // setProjectId(cId);
                        await onLoadCustomer(cId, pId)
                    }
                } else {
                    setCustomerId(res.data[0].customerId);
                    setLinkTo(`/${res.data[0].customerId}`)
                }

            }
        }
        if (role === "ROLE_MOD") {
            let res = await CustomerService.getCustomerIds(userName)
            if (res.status === 200 && res.data !== '') {
                setCustomers(res.data);
                setCustomersDefault(res.data);
                if (cId !== "") {
                    if (pId == "") {
                        setCustomerId(cId);
                        setLinkTo(`/${cId}`)
                    } else {
                        await onLoadCustomer(cId)
                    }
                } else {
                    setCustomerId(res.data[0].customerId);
                    setLinkTo(`/${res.data[0].customerId}`)
                }
            }
        }
        if (role === "ROLE_USER") {
            let res = await CustomerService.getCustomerIds(userName)
            if (res.status === 200 && res.data !== '') {
                setCustomers(res.data);
                setCustomersDefault(res.data);
                if (cId !== "") {
                    if (pId == "") {
                        setCustomerId(cId);
                        setLinkTo(`/${cId}`)
                    } else {
                        await onLoadCustomer(cId)
                    }
                } else {
                    setCustomerId(res.data[0].customerId);
                    setLinkTo(`/${res.data[0].customerId}`)
                }
            }
        }
        // if (role !== "ROLE_ADMIN") {
        //     setAccessDenied(true);
        // }
        setIsLoading(false);
    }

    const onLoadCustomer = async (_customerId, projectId) => {
        let pId = location.pathname.split("/")[2];
        if (showListProject === _customerId) {
            setShowListProject(0)
            setProjects([])
        } else {
            setProjects([])
            setShowListProject(_customerId)
        }

        if (_customerId != customerId && customerId != undefined) {
            setLinkTo(`/${_customerId}`)
            setProjectId(0)
            pId = ""
        }


        setCustomerId(_customerId);
        let cusId = _customerId;
        let pathname = window.location.pathname;

        if (pId == "" || pId == undefined || isNaN(pId)) {
            setLinkTo(`/${_customerId}`)
            setProjectId(0)
        } else {
            if (projectId == undefined) {
                setLinkTo(`/${_customerId}`)
            } else {
                setLinkTo(`/${_customerId}/${pId}`)
            }
            setProjectId(pId)
        }

        let arrPathName = pathname.split("/");

        if (arrPathName.length >= 2 && arrPathName.length < 6) {
            let url = "/";
            if (arrPathName[1] === cusId) {
                if (arrPathName.length >= 6) {
                    for (let index = 0; index < 2; index++) {
                        url += arrPathName[index] + "/";
                    }
                } else {
                    arrPathName[1] = cusId
                    for (let index = 0; index < arrPathName.length; index++) {
                        if (arrPathName[index] !== "") {
                            if (index === 6) {
                                arrPathName[index] = "0";
                            }
                            url += arrPathName[index] + "/";
                        }
                        else {
                            url += arrPathName[index];
                        }

                        if (index == 2) {
                            if (arrPathName[2] == "system-map") {
                                break;
                            }
                        }
                    }
                }
            } else {
                arrPathName[1] = cusId

                if (arrPathName.length > 4) {
                    for (let index = 0; index < arrPathName.length; index++) {
                        if (arrPathName[index] !== "") {
                            if (index === 2) {
                                if (isNaN(arrPathName[index]) == false) {
                                    continue
                                }
                            }
                            if (index === 6) {
                                arrPathName[index] = "0";
                            }
                            url += arrPathName[index] + "/";
                        }
                        else {
                            url += arrPathName[index];
                        }

                        if (index == 2) {
                            if (arrPathName[2] == "system-map") {
                                break;
                            }
                        }
                    }
                }
                if (arrPathName.length <= 4) {
                    for (let index = 0; index < arrPathName.length; index++) {
                        if (arrPathName[index] !== "") {
                            if (index == 2) {
                                if (isNaN(arrPathName[index]) == false) {
                                    continue
                                }
                            }

                            if (index === 6) {
                                arrPathName[index] = "0";
                            }
                            url += arrPathName[index] + "/";
                        }
                        else {
                            url += arrPathName[index];
                        }

                        if (index == 2) {
                            if (arrPathName[2] == "system-map") {
                                break;
                            }
                            if (arrPathName[2] == "device-information") {
                                break;
                            }
                        }
                    }
                }
                history.push(url);
            }

        } else if (arrPathName.length >= 6) {
            let url = ""
            // arrPathName[1] = customerId
            // for (let index = 0; index < 3; index++) {
            //     url += arrPathName[index] + "/";
            // }
            if (arrPathName[3] == "device-information") {
                url = "/" + cusId + "/" + "device-information/";
            } else if ((arrPathName[3] == "system-map")) {
                url = "/" + cusId + "/" + "system-map";
            } else {
                for (let index = 0; index < 3; index++) {
                    url += arrPathName[index] + "/";
                }
            }
            history.push(url);

        }

        await getListProjectByCustomerId(cusId)

    }


    const searchCustomers = async (e) => {
        setStatusInput(false);
        let customerName = document.getElementById("keyword").value;
        if (customerName === "") {
            setStatusInput(true);
            setCustomers(customersDefault)
        } else {
            let customerSearch = customersDefault?.filter(d => d.customerName.toLowerCase().includes(customerName.toLowerCase()));
            setCustomers(customerSearch)
        }
    }

    const getListProjectByCustomerId = async (customerId) => {
        if (role === "ROLE_USER") {
            let res = await ProjectService.getProjectIds(userName)
            if (res.status === 200) {
                setProjects(res.data)
            }
        } else {
            let res = await ProjectService.getProjectByCustomerId(customerId)
            if (res.status === 200) {
                setProjects(res.data)
            }
        }
    }

    const onLoadProject = async (iProjectId) => {
        setProjectId(iProjectId);
        let pathname = window.location.pathname;
        let arrPathName = pathname.split("/");
        setLinkTo(`/${customerId}/${iProjectId}`)
        let url = "/";


        if (arrPathName.length >= 5 && arrPathName.length < 6) {
            arrPathName[1] = customerId;
            for (let index = 0; index < arrPathName.length; index++) {
                if (arrPathName[index] !== "") {
                    if (index === 2) {
                        if (arrPathName[index] === "system-map") {
                            url += iProjectId + "/" + "system-map"
                            break;
                        } else {
                            arrPathName[index] = iProjectId;
                        }
                    }
                    if (index === 6) {
                        arrPathName[index] = "0";
                    }
                    url += arrPathName[index] + "/";
                }
                else {
                    url += arrPathName[index];
                }

                if (index == 2) {
                    if (arrPathName[2] == "system-map") {
                        break;
                    }
                }
            }

        }
        else if (arrPathName.length == 3) {
            url = `/${customerId}/${iProjectId}/`
            if (arrPathName[2] == "system-map") {
                url = `/${customerId}/${iProjectId}/system-map/`
            }
        }
        else if (arrPathName.length == 4) {
            if (isNaN(arrPathName[2]) == true) {
                url = `/${customerId}/${iProjectId}/${arrPathName[2]}/`
            } else {
                url = `/${customerId}/${iProjectId}/`
            }
        } else if (arrPathName.length >= 6) {
            let urlNew = ""
            arrPathName[2] = iProjectId
            for (let i = 0; i < 4; i++) {
                urlNew += arrPathName[i] + "/";
            }
            url = urlNew
        }
        else if (arrPathName.length == 2) {
            url = `/${customerId}/${iProjectId}/`
        }
        history.push(url);
    }

    useEffect(() => {
        getCustomers();
        setActive();
        document.title = "Trang chủ";

    }, [changeTabUser]);

    return (
        <>
            {!accessDenied ?
                <>
                    <div id="page-body">
                        <div id="main-content" className={changeTabUser ? "main-content" : ""}>

                            {changeTabUser === true &&
                                <div id="project-list" className="tab-show">
                                    <button type="button" className="btn btn-light btn-change-tab" onClick={() => changeTab()}><i className="fas fa-bars"></i></button>
                                    <hr className="mt-1 mb-1" />
                                    <input type="text" id="keyword" name="keyword" className="input mt-0 mb-1 w-100" placeholder={t('content.home_page.search') + '.....'} onChange={(e) => searchCustomers(e)} />
                                    <i className="fa fa-search" id="icon-search"></i>
                                    {
                                        isLoading ?
                                            <div className="d-flex justify-content-center">
                                                <div className="spinner-border spinner-border-sm" role="status">
                                                    <span className="sr-only">Loading...</span>
                                                </div>
                                            </div> :
                                            <div className="customer-list">
                                                {
                                                    statusInput === true && customers?.map((item, index) => (
                                                        <div key={index}>
                                                            <button onClick={() => onLoadCustomer(item.customerId)} className={(customerId === index && index === 0) || (customerId == item.customerId) ? "btn btn-block text-left btn-cus btn-cus-active mt-1" : "btn btn-block text-left btn-cus mt-1"} data-bs-toggle="tooltip" data-bs-placement="right" title={item.customerName}>
                                                                <i className={(customerId === index && index === 0) || (customerId == item.customerId) ? "fa-solid fa-user" : "fa-solid fa-user"} style={{ height: 20 }}></i>&nbsp; <span>{item.customerName}</span>
                                                            </button>
                                                            {showListProject == item.customerId && projects?.map((project, i) => (
                                                                <div key={i} style={{ width: "170px", float: "right" }}>
                                                                    <button onClick={() => onLoadProject(project.projectId)} className={projectId == project.projectId ? "btn btn-block text-left btn-cus btn-cus-active mt-1" : "btn btn-block text-left btn-cus mt-1"} data-bs-toggle="tooltip" data-bs-placement="right" title={project.projectName}>
                                                                        <i className={projectId === project.projectId ? "fa-solid fa-user" : "fa-solid fa-user"} style={{ height: 20 }}></i>&nbsp; <span>{project.projectName}</span>
                                                                    </button>
                                                                </div>
                                                            ))
                                                            }
                                                        </div>
                                                    ))

                                                }
                                                {
                                                    statusInput === false && customers?.map((item, index) => (
                                                        <div key={index}>
                                                            <button onClick={() => onLoadCustomer(item.customerId)} className={(customerId === index && index === 0) || (customerId == item.customerId) ? "btn btn-block text-left btn-cus btn-cus-active mt-1" : "btn btn-block text-left btn-cus mt-1"} data-bs-toggle="tooltip" data-bs-placement="right" title={item.customerName}>
                                                                <i className={(customerId === index && index === 0) || (customerId == item.customerId) ? "fa-solid fa-user" : "fa-solid fa-user"} style={{ height: 20 }}></i>&nbsp; <span>{item.customerName}</span>
                                                            </button>
                                                            {showListProject === item.customerId && projects?.map((project, i) => (
                                                                <div key={i} style={{ width: "170px", float: "right" }}>
                                                                    <button onClick={() => onLoadProject(project.projectId)} className={projectId === project.projectId ? "btn btn-block text-left btn-cus btn-cus-active mt-1" : "btn btn-block text-left btn-cus mt-1"} data-bs-toggle="tooltip" data-bs-placement="right" title={i.projectName}>
                                                                        <i className={projectId === project.projectId ? "fa-solid fa-user" : "fa-solid fa-user"} style={{ height: 20 }}></i>&nbsp; <span>{project.projectName}</span>
                                                                    </button>
                                                                </div>
                                                            ))
                                                            }
                                                        </div>
                                                    ))
                                                }
                                            </div>
                                    }
                                </div>
                            }
                            {changeTabUser === false && <div id="project-list" className="tab-hide">
                                <button type="button" className="btn btn-light btn-change-tab" onClick={() => changeTab()}><i className="fas fa-circle-right"></i></button>
                            </div>
                            }


                            <div id="project-info" className={changeTabUser === true ?
                                "border-0 "
                                :
                                "border-0 tab-width"}>
                                <div className="tab-container">
                                    <nav className="menu-drop">
                                        <ul className="menu tab-menu d-flex flex-wrap">
                                            <li className={active === CONS.TAB_MENU.OVERVIEW ? "active" : "div"} data-bs-toggle="tooltip" data-bs-placement="right" title="TỔNG QUAN" onClick={() => setActiveMenu(CONS.TAB_MENU.OVERVIEW)}>
                                                <Link to={linkTo} className="primary-menu"
                                                ><i className="fas fa-home"></i>&nbsp; <span>{t('content.home_page.tab_overview')}</span></Link>
                                            </li>
                                            <li className={active === CONS.TAB_MENU.SYSTEM_MAP ? "active" : "div"} data-bs-toggle="tooltip" data-bs-placement="right" title="SƠ ĐỒ 1 SỢI" onClick={() => setActiveMenu(CONS.TAB_MENU.SYSTEM_MAP)}>
                                                <Link to={`${linkTo}/system-map/`} className="primary-menu"
                                                ><i className="fas fa-diagram-project"></i>&nbsp; <span>{t('content.home_page.tab_oneline')}</span></Link>
                                            </li>
                                            <li className={active === CONS.TAB_MENU.CHART ? "active" : "div"} data-bs-toggle="tooltip" data-bs-placement="right" title="BIỂU ĐỒ" onClick={() => setActiveMenu(CONS.TAB_MENU.CHART)}>
                                                <Link to={`${linkTo}/chart/`} className="primary-menu"
                                                ><i className="fas fa-chart-column"></i>&nbsp; <span>{t('content.home_page.tab_chart')}</span></Link>
                                            </li>
                                            <li className={active === CONS.TAB_MENU.WARNING ? "active" : "div"} data-bs-toggle="tooltip" data-bs-placement="right" title="CẢNH BÁO" onClick={() => setActiveMenu(CONS.TAB_MENU.WARNING)}>
                                                <Link to={`${linkTo}/warning/`} className="primary-menu"
                                                ><i className="fas fa-triangle-exclamation"></i>&nbsp; <span>{t('content.home_page.tab_warning')}</span></Link>
                                            </li>
                                            <li className={active === CONS.TAB_MENU.MANUFACTURE ? "active" : "div"} data-bs-toggle="tooltip" data-bs-placement="right" title="THEO DÕI SẢN XUẤT" onClick={() => setActiveMenu(CONS.TAB_MENU.MANUFACTURE)}>
                                                <Link to={`${linkTo}/manufacture/`} className="primary-menu"
                                                ><i className="fas fa-industry"></i>&nbsp; <span>{t('content.home_page.tab_manufacture')}</span></Link>
                                            </li>
                                            <li className={active === CONS.TAB_MENU.DEVICE_INFORMATION ? "active" : "div"} data-bs-toggle="tooltip" data-bs-placement="right" title="THÔNG TIN THIẾT BỊ" onClick={() => setActiveMenu(CONS.TAB_MENU.DEVICE_INFORMATION)}>
                                                <Link to={`${linkTo}/device-information/`} className="primary-menu">
                                                    <i className="fas fa-circle-info"></i>&nbsp; <span>{t('content.home_page.tab_device')}</span></Link>
                                            </li>
                                            <li className={active === CONS.TAB_MENU.REPORT ? "active" : "div"} data-bs-toggle="tooltip" data-bs-placement="right" title="BÁO CÁO" onClick={() => setActiveMenu(CONS.TAB_MENU.REPORT)}>
                                                <Link to={`${linkTo}/report/`} className="primary-menu"
                                                ><i className="fas fa-file-contract"></i>&nbsp; <span>{t('content.home_page.tab_report')}</span></Link>
                                            </li>
                                            <li className={active === CONS.TAB_MENU.SETTING_SHIFT ? "active" : "div"} data-bs-toggle="tooltip" data-bs-placement="right" title="CÀI ĐẶT" onClick={() => setActiveMenu(CONS.TAB_MENU.SETTING_SHIFT)}>
                                                <Link to={`${linkTo}/setting/`} className="primary-menu"
                                                ><i className="fas fa-gears"></i>&nbsp; <span>{t('content.home_page.tab_setting')}</span></Link>
                                            </li>
                                            <li className={active === CONS.TAB_MENU.SUPPORT ? "active" : "div"} data-bs-toggle="tooltip" data-bs-placement="right" title="TRỢ GIÚP" onClick={() => setActiveMenu(CONS.TAB_MENU.SUPPORT)}>
                                                <Link to={`${linkTo}/support/`} className="primary-menu"
                                                ><i className="fas fa-circle-question"></i>&nbsp; <span>{t('content.home_page.tab_support')}</span></Link>
                                            </li>
                                            <li className={active === CONS.TAB_MENU.PLAN ? "active" : "div"} data-bs-toggle="tooltip" data-bs-placement="right" title="TRỢ GIÚP" onClick={() => setActiveMenu(CONS.TAB_MENU.PLAN)}>
                                                <Link to={`${linkTo}/plan/`} className="primary-menu"
                                                ><i className="fas fa-bars"></i>&nbsp; <span>{t('content.home_page.tab_plan')}</span></Link>
                                            </li>
                                        </ul>
                                    </nav>
                                </div>

                                <div className="tab-content" id="overviewAll">
                                    <Switch>
                                        {
                                            HomeRouters.map((route, i) => {
                                                return <Route key={i} path={route.path} component={route.component} />
                                            })
                                        }
                                    </Switch>
                                </div>
                            </div>
                        </div>
                    </div>
                </>
                :
                <MainTain>

                </MainTain>
            }
        </>
    )
}

export default Home;