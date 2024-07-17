import { useState } from "react";
import { useEffect } from "react";
import { useRef } from "react";
import { useLocation, useParams, useHistory } from "react-router-dom/cjs/react-router-dom.min";
import { Route, Switch } from 'react-router-dom';
import AccessDenied from "../../access-denied/AccessDenied";
import overviewLoadService from "../../../../services/OverviewLoadService";
import customerService from "../../../../services/CustomerService";
import moment from "moment";
import SystemMapComponent from "./systemMap";

import HomeService from "../../../../services/HomeService";

import "./index.css"
import { async } from "q";
import ProjectService from "../../../../services/ProjectService";
import AuthService from "../../../../services/AuthService";
import CustomerService from "../../../../services/CustomerService";
import { t } from "i18next";
const SystemMap = () => {

    const $ = window.$;
    const SiteRef = useRef(null);
    const location = useLocation();
    const param = useParams();
    const history = useHistory();

    const [tabTable, setTabTable] = useState(true);
    const [statusInput, setStatusInput] = useState(true);
    const [sitesSearch, setSitesSearch] = useState([]);
    const [tableSite, setTableSite] = useState([]);
    const [cusId, setCusId] = useState(1);
    const [site, setSite] = useState(null);
    const [openOneline, setOpenOneline] = useState(false);
    const [customerName, setCustomerName] = useState();
    const [inforProject, setInforProject] = useState();
    const [customer, setCustomer] = useState();
    const [listOneLineNum, setListOneLineNum] = useState([]);
    const [role] = useState(AuthService.getRoleName());
    const [userName] = useState(AuthService.getUserName());
    const [accessDenied, setAccessDenied] = useState(false)


    const categorySite = [
        {
            name: t('content.home_page.load'),
            system_type: 1,
            src: "/resources/image/icon-load-black.svg",
            type: "load"
        },
        {
            name: t('content.home_page.solar'),
            system_type: 2,
            src: "/resources/image/icon-solar-black.svg",
            type: "pv"
        },
        {
            name: t('content.home_page.grid'),
            system_type: 5,
            src: "/resources/image/icon-grid-black.svg",
            type: "grid"
        },
        {
            name: t('content.home_page.battery'),
            system_type: 4,
            src: "/resources/image/icon-battery-black.svg"
        },
        {
            name: t('content.home_page.wind'),
            system_type: 3,
            src: "/resources/image/icon-wind-black.svg"
        }
    ];


    const getCustomerId = async () => {
        let response = await HomeService.getCustomerIdFirstTime();
        if (response.status === 200) {
            setCusId(response.data.customerId);
        }
        setCusId(response.data.customerId);
    }

    const checkAuthorization = async () => {
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
                showTable();
                getListOneLine(param.customerId);
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
                                setAccessDenied(false)
                                showTable();
                                getListOneLine(param.customerId);
                            }
                        }
                        else {
                            setAccessDenied(false)
                            showTable();
                            getListOneLine(param.customerId);
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
                                showTable();
                                getListOneLine(param.customerId);
                            }
                        } else {
                            setAccessDenied(false)
                            showTable();
                            getListOneLine(param.customerId);
                        }
                    }
                }
            }
        }
    }

    const getCustomer = async () => {
        let res = await customerService.getCustomer(param.id);
        if (res.status === 200) {
            setCustomer(res.data);
        }
    };

    const showTable = async () => {
        getCustomerId();
        let pathname = window.location.pathname;
        let arrPathName = pathname.split("/");
        let ids = ""
        if (role === "ROLE_USER") {
            let re = await ProjectService.getProIds(userName)
            if (re.status === 200 && re.data !== '') {
                ids = re.data
            }
        }
        if (arrPathName.length > 2) {

            for (let index = 0; index < arrPathName.length; index++) {
                if (arrPathName[index] !== "") {
                    let res = await HomeService.getProjectLocationsHomePageByCustomerId(param.customerId, 0, "", "", ids);
                    if (res.status === 200) {
                        var data = res.data;
                        if (typeof param.projectId !== "undefined") {
                            data = data.filter(d => d.projectId == param.projectId);
                        }
                        setTableSite(data);
                    }
                }
            }

            if (arrPathName.length >= 5) {
                setOpenOneline(true);
            }

        } else {
            if (location.search.length > 0) {
                let customerId = new URLSearchParams(location.search).get('customerId');
                let res = await HomeService.getProjectLocationsHomePageByCustomerId(customerId, 0, "", "");
                if (res.status === 200) {
                    var data = res.data;
                    if (typeof param.projectId !== "undefined") {
                        data = data.filter(d => d.projectId == param.projectId);
                    }
                    setTableSite(data);
                }
            } else {
                let res = await HomeService.getProjectLocationsHomePageByCustomerId(cusId, 0, "", "");
                if (res.status === 200) {
                    var data = res.data;
                    if (typeof param.projectId !== "undefined") {
                        data = data.filter(d => d.projectId == param.projectId);
                    }
                    setTableSite(data);
                }
            }
        }
    }

    const funcFocus = (projectId, type) => {
        for (let i = 0; i < tableSite.length; i++) {
            for (let j = 1; j < 6; j++) {
                if (projectId == tableSite[i].projectId && type == j) {
                    $('#' + projectId + type).css({ backgroundColor: "#9DA3BE" });
                } else {
                    $('#' + tableSite[i].projectId + j).css({ backgroundColor: "#FFF" });
                }
            }
        }
    }

    const settingOpenOneline = async (customerId, projectId, type, projectName) => {
        setOpenOneline(() => true);
        var name = ""
        funcFocus(projectId, type);
        let res = await overviewLoadService.getPower(param.customerId, projectId, "");
        if (res.status === 200) {
            let Array = res.data.projectInfor.split("/");
            name = Array[0];
            setCustomerName(Array[0]);

        }
        setSite(projectId);
        setCusId(customerId);
        setInforProject(name + " / " + projectName + " / " + type)
        let url = '/' + customerId + '/system-map/' + type + '/' + projectId;
        if (typeof param.projectId !== "undefined") {
            url = '/' + customerId + '/' + projectId + '/system-map/' + type + '/' + projectId;
        }

        history.push(url);
    }

    const changeTabOneline = () => {
        let typeTab = tabTable;
        if (typeTab === true) {
            setTabTable(false);
        };
        if (typeTab === false) {
            setTabTable(true);
        };
    }

    const searchSites = async (e) => {
        setStatusInput(false);
        let siteName = document.getElementById("tableSite-name").value;
        let selectSearch = document.getElementById("select-search").value;
        if (siteName === "" && selectSearch == 1) {
            setStatusInput(true);
            setSitesSearch([]);
        } else {
            if (siteName !== "") {
                let siteSearch = tableSite?.filter(d => d.projectName.includes(siteName));
                setSitesSearch(siteSearch);
            } else {
                let siteSearch = tableSite?.filter(d => d.projectName.includes(selectSearch));
                setSitesSearch(siteSearch);
            }
        }

    }
    const getListOneLine = async (customerId) => {
        let res = await ProjectService.getProjectByCustomerId(customerId)
        if (res.status === 200) {
            setListOneLineNum(res.data)
        }
    }

    useEffect(() => {
        checkAuthorization()
        document.title = "Sơ đồ 1 sợi";
    }, [param.customerId, cusId, tabTable, site, param.projectId]);

    return (
        <>
            {accessDenied ?
                <AccessDenied></AccessDenied>
                :
                <>
                    {/** ---------------------------TABLE SITE --------------------------------- */}
                    <div style={{ marginBottom: "10px" }} ref={SiteRef}>
                        {
                            tabTable === false &&
                            <button type="button" className="title-down text-left m-2" onClick={() => changeTabOneline()}><i className="fa-solid fa-diagram-project fa-bounce" style={{ color: "#fff" }}></i> {t('content.category.tool_page.list_project_diagram')}</button>
                        }
                        {
                            tabTable === true &&
                            <>
                                <div className="d-inline-block">
                                    <button type="button" className="btn btn-light btn-change-tab mt-1 mb-2 mr-2" onClick={() => changeTabOneline()}><i className="fas fa-bars" ></i></button>
                                    <span className="title-up text-left m-2"><div style={{ marginTop: "-21px", color: "white" }}><i className="fa-solid fa-diagram-project ml-1" style={{ color: "#fff" }}></i> {t('content.category.tool_page.list_project_diagram')}</div></span>
                                </div>
                                <div>
                                    <input type="text" id="tableSite-name" className="input mt-2 mb-1" name="tableSite-name" placeholder={t('content.home_page.search')} onChange={(e) => searchSites(e)} />
                                    <select id="select-search" className="" onChange={(e) => searchSites(e)}
                                        style={{ backgroundColor: "#cccccc", borderRadius: 5, color: "white", width: "200px", marginLeft: "30px" }}>
                                        <option className="value" value={1}>{t('content.all')}</option>
                                        {
                                            tableSite?.map((s, i) => (
                                                <option key={i} className="value" value={s.id}>{s.projectName}</option>
                                            ))}
                                    </select>
                                </div>
                                <div className="table-oneline">
                                    <table className="table">
                                        <thead>
                                            <tr className="tr-header">
                                                <th className="text-center text-uppercase" style={{ width: "50px" }}>{t('content.no')}</th>
                                                <th className="text-center text-uppercase">
                                                    <i className="fa-solid fa-file mr-1 ml-2" style={{ color: "#fff" }}></i>
                                                    {t('content.project')}
                                                </th>
                                                <th className="text-center text-uppercase">
                                                    <i className="fas fa-sharp fa-regular fa-location-dot mr-1" style={{ color: "#fff" }}></i>
                                                    {t('content.address')}
                                                </th>
                                                <th className="text-center text-uppercase" colSpan={5} style={{ width: "650px" }}>
                                                    <i className="fas fa-sharp fa-regular fa-list-alt mr-1" style={{ color: "#fff" }}></i>
                                                    {t('content.system_type')}
                                                </th>
                                            </tr>
                                        </thead>
                                        {statusInput === true && tableSite?.map((s, i) => (

                                            <tbody key={i} style={{ fontWeight: "bold", paddingLeft: "10px" }}>

                                                <tr style={{ height: "30px !important" }}>
                                                    <td rowSpan="" className="text-center">{i + 1}</td>
                                                    <td rowSpan="" title="Chi tiết" className="text-left text-uppercase">
                                                        <span>{s.projectName}</span>
                                                    </td>
                                                    <td className="text-left" title="Chi tiết" >{s.areaName}, {s.managerName}</td>
                                                    {
                                                        categorySite.map((kind, j) => {
                                                            var a = listOneLineNum?.filter(e => e.projectId == s.projectId)[0];
                                                            if (j == 0) {
                                                                if (a?.loadNum > 0) {
                                                                    return (
                                                                        <td id={s.projectId + "" + kind.system_type} className="text-center" onClick={() => {
                                                                            settingOpenOneline(s.customerId, s.projectId, kind.system_type, s.projectName);
                                                                        }} key={j}>
                                                                            <img className="fa-beat animation pt-1" src={kind.src} height="30px"></img>
                                                                            <br />
                                                                            <label className="text-uppercase mt-1" style={{ lineHeight: 1 }}> {kind.name} </label>

                                                                        </td>
                                                                    )
                                                                } else {
                                                                    return (
                                                                        <td id={s.projectId + "" + kind.system_type} className="text-center" style={{ opacity: 0.2 }} key={j}>
                                                                            <img className="fa-beat animation pt-1" src={kind.src} height="30px"></img>
                                                                            <br />
                                                                            <label className="text-uppercase mt-1" style={{ lineHeight: 1 }}> {kind.name} </label>
                                                                        </td>
                                                                    )
                                                                }
                                                            } else if (j == 1) {
                                                                if (a?.solarNum > 0) {
                                                                    return (
                                                                        <td id={s.projectId + "" + kind.system_type} className="text-center" onClick={() => {
                                                                            settingOpenOneline(s.customerId, s.projectId, kind.system_type, s.projectName);
                                                                        }} key={j}>
                                                                            <img className="fa-beat animation pt-1" src={kind.src} height="30px"></img>
                                                                            <br />
                                                                            <label className="text-uppercase mt-1" style={{ lineHeight: 1 }}> {kind.name} </label>

                                                                        </td>
                                                                    )
                                                                } else {
                                                                    return (
                                                                        <td id={s.projectId + "" + kind.system_type} className="text-center" style={{ opacity: 0.2 }} key={j}>
                                                                            <img className="fa-beat animation pt-1" src={kind.src} height="30px"></img>
                                                                            <br />
                                                                            <label className="text-uppercase mt-1" style={{ lineHeight: 1 }}> {kind.name} </label>
                                                                        </td>
                                                                    )
                                                                }
                                                            } else if (j == 2) {
                                                                if (a?.utilityNum > 0) {
                                                                    return (
                                                                        <td id={s.projectId + "" + kind.system_type} className="text-center" onClick={() => {
                                                                            settingOpenOneline(s.customerId, s.projectId, kind.system_type, s.projectName);
                                                                        }} key={j}>
                                                                            <img className="fa-beat animation pt-1" src={kind.src} height="30px"></img>
                                                                            <br />
                                                                            <label className="text-uppercase mt-1" style={{ lineHeight: 1 }}> {kind.name} </label>

                                                                        </td>
                                                                    )
                                                                } else {
                                                                    return (
                                                                        <td id={s.projectId + "" + kind.system_type} className="text-center" style={{ opacity: 0.2 }} key={j}>
                                                                            <img className="fa-beat animation pt-1" src={kind.src} height="30px"></img>
                                                                            <br />
                                                                            <label className="text-uppercase mt-1" style={{ lineHeight: 1 }}> {kind.name} </label>
                                                                        </td>
                                                                    )
                                                                }
                                                            } else if (j == 3) {
                                                                if (a?.evNum > 0) {
                                                                    return (
                                                                        <td id={s.projectId + "" + kind.system_type} className="text-center" onClick={() => {
                                                                            settingOpenOneline(s.customerId, s.projectId, kind.system_type, s.projectName);
                                                                        }} key={j}>
                                                                            <img className="fa-beat animation pt-1" src={kind.src} height="30px"></img>
                                                                            <br />
                                                                            <label className="text-uppercase mt-1" style={{ lineHeight: 1 }}> {kind.name} </label>

                                                                        </td>
                                                                    )
                                                                } else {
                                                                    return (
                                                                        <td id={s.projectId + "" + kind.system_type} className="text-center" style={{ opacity: 0.2 }} key={j}>
                                                                            <img className="fa-beat animation pt-1" src={kind.src} height="30px"></img>
                                                                            <br />
                                                                            <label className="text-uppercase mt-1" style={{ lineHeight: 1 }}> {kind.name} </label>
                                                                        </td>
                                                                    )
                                                                }
                                                            } else if (j == 4) {
                                                                if (a?.windNum > 0) {
                                                                    return (
                                                                        <td id={s.projectId + "" + kind.system_type} className="text-center" onClick={() => {
                                                                            settingOpenOneline(s.customerId, s.projectId, kind.system_type, s.projectName);
                                                                        }} key={j}>
                                                                            <img className="fa-beat animation pt-1" src={kind.src} height="30px"></img>
                                                                            <br />
                                                                            <label className="text-uppercase mt-1" style={{ lineHeight: 1 }}> {kind.name} </label>

                                                                        </td>
                                                                    )
                                                                } else {
                                                                    return (
                                                                        <td id={s.projectId + "" + kind.system_type} className="text-center" style={{ opacity: 0.2 }} key={j}>
                                                                            <img className="fa-beat animation pt-1" src={kind.src} height="30px"></img>
                                                                            <br />
                                                                            <label className="text-uppercase mt-1" style={{ lineHeight: 1 }}> {kind.name} </label>
                                                                        </td>
                                                                    )
                                                                }
                                                            }

                                                        })
                                                    }
                                                </tr>

                                            </tbody>
                                        ))}
                                        {statusInput === false && sitesSearch?.map((s, i) => (

                                            <tbody key={i} style={{ fontWeight: "bold", paddingLeft: "10px" }}>

                                                <tr style={{ height: "30px !important" }}>
                                                    <td rowSpan="" className="text-center">{i + 1}</td>
                                                    <td rowSpan="" title="Chi tiết" className="text-left text-uppercase">
                                                        <span>{s.projectName}</span>
                                                    </td>
                                                    <td className="text-left" title="Chi tiết" >{s.areaName}, {s.managerName}</td>
                                                    {
                                                        categorySite.map((kind, j) => {
                                                            var a = listOneLineNum?.filter(e => e.projectId == s.projectId)[0];

                                                            if (j == 0) {
                                                                if (a?.loadNum > 0) {
                                                                    return (
                                                                        <td id={s.projectId + "" + kind.system_type} className="text-center" onClick={() => {
                                                                            settingOpenOneline(s.customerId, s.projectId, kind.system_type, s.projectName);
                                                                        }} key={j}>
                                                                            <img className="fa-beat animation pt-1" src={kind.src} height="30px"></img>
                                                                            <br />
                                                                            <label className="text-uppercase mt-1" style={{ lineHeight: 1 }}> {kind.name} </label>

                                                                        </td>
                                                                    )
                                                                } else {
                                                                    return (
                                                                        <td id={s.projectId + "" + kind.system_type} className="text-center" style={{ opacity: 0.2 }}>
                                                                            <img className="fa-beat animation pt-1" src={kind.src} height="30px"></img>
                                                                            <br />
                                                                            <label className="text-uppercase mt-1" style={{ lineHeight: 1 }}> {kind.name} </label>
                                                                        </td>
                                                                    )
                                                                }
                                                            } else if (j == 1) {
                                                                if (a?.solarNum > 0) {
                                                                    return (
                                                                        <td id={s.projectId + "" + kind.system_type} className="text-center" onClick={() => {
                                                                            settingOpenOneline(s.customerId, s.projectId, kind.system_type, s.projectName);
                                                                        }} key={j}>
                                                                            <img className="fa-beat animation pt-1" src={kind.src} height="30px"></img>
                                                                            <br />
                                                                            <label className="text-uppercase mt-1" style={{ lineHeight: 1 }}> {kind.name} </label>

                                                                        </td>
                                                                    )
                                                                } else {
                                                                    return (
                                                                        <td id={s.projectId + "" + kind.system_type} className="text-center" style={{ opacity: 0.2 }}>
                                                                            <img className="fa-beat animation pt-1" src={kind.src} height="30px"></img>
                                                                            <br />
                                                                            <label className="text-uppercase mt-1" style={{ lineHeight: 1 }}> {kind.name} </label>
                                                                        </td>
                                                                    )
                                                                }
                                                            } else if (j == 2) {
                                                                if (a?.utilityNum > 0) {
                                                                    return (
                                                                        <td id={s.projectId + "" + kind.system_type} className="text-center" onClick={() => {
                                                                            settingOpenOneline(s.customerId, s.projectId, kind.system_type, s.projectName);
                                                                        }} key={j}>
                                                                            <img className="fa-beat animation pt-1" src={kind.src} height="30px"></img>
                                                                            <br />
                                                                            <label className="text-uppercase mt-1" style={{ lineHeight: 1 }}> {kind.name} </label>

                                                                        </td>
                                                                    )
                                                                } else {
                                                                    return (
                                                                        <td id={s.projectId + "" + kind.system_type} className="text-center" style={{ opacity: 0.2 }}>
                                                                            <img className="fa-beat animation pt-1" src={kind.src} height="30px"></img>
                                                                            <br />
                                                                            <label className="text-uppercase mt-1" style={{ lineHeight: 1 }}> {kind.name} </label>
                                                                        </td>
                                                                    )
                                                                }
                                                            } else if (j == 3) {
                                                                if (a?.evNum > 0) {
                                                                    return (
                                                                        <td id={s.projectId + "" + kind.system_type} className="text-center" onClick={() => {
                                                                            settingOpenOneline(s.customerId, s.projectId, kind.system_type, s.projectName);
                                                                        }} key={j}>
                                                                            <img className="fa-beat animation pt-1" src={kind.src} height="30px"></img>
                                                                            <br />
                                                                            <label className="text-uppercase mt-1" style={{ lineHeight: 1 }}> {kind.name} </label>

                                                                        </td>
                                                                    )
                                                                } else {
                                                                    return (
                                                                        <td id={s.projectId + "" + kind.system_type} className="text-center" style={{ opacity: 0.2 }}>
                                                                            <img className="fa-beat animation pt-1" src={kind.src} height="30px"></img>
                                                                            <br />
                                                                            <label className="text-uppercase mt-1" style={{ lineHeight: 1 }}> {kind.name} </label>
                                                                        </td>
                                                                    )
                                                                }
                                                            } else if (j == 4) {
                                                                if (a?.windNum > 0) {
                                                                    return (
                                                                        <td id={s.projectId + "" + kind.system_type} className="text-center" onClick={() => {
                                                                            settingOpenOneline(s.customerId, s.projectId, kind.system_type, s.projectName);
                                                                        }} key={j}>
                                                                            <img className="fa-beat animation pt-1" src={kind.src} height="30px"></img>
                                                                            <br />
                                                                            <label className="text-uppercase mt-1" style={{ lineHeight: 1 }}> {kind.name} </label>

                                                                        </td>
                                                                    )
                                                                } else {
                                                                    return (
                                                                        <td id={s.projectId + "" + kind.system_type} className="text-center" style={{ opacity: 0.2 }}>
                                                                            <img className="fa-beat animation pt-1" src={kind.src} height="30px"></img>
                                                                            <br />
                                                                            <label className="text-uppercase mt-1" style={{ lineHeight: 1 }}> {kind.name} </label>
                                                                        </td>
                                                                    )
                                                                }
                                                            }

                                                        })
                                                    }
                                                </tr>

                                            </tbody>
                                        ))}
                                    </table >
                                </div>
                            </>
                        }
                    </div >
                    {/** ---------------------------TABLE SITE --------------------------------- */}
                    <div style={{ width: "100%", height: "5px", backgroundColor: "#0a1a5c" }} ></div>
                    {/** ---------------------------ONELINE --------------------------------- */}
                    {
                        openOneline ? <>
                            <div id="oneline-info" className="position-relative">
                                <Switch>
                                    <Route path={"/:customerId/:projectSearchId/system-map/:type/:projectId/:systemMapId"}><SystemMapComponent projectInfo={inforProject}></SystemMapComponent></Route>
                                    <Route path={"/:customerId/:projectSearchId/system-map/:type/:projectId"}><SystemMapComponent projectInfo={inforProject}></SystemMapComponent></Route>
                                    <Route path={"/:customerId/system-map/:type/:projectId/:systemMapId"}><SystemMapComponent projectInfo={inforProject}></SystemMapComponent></Route>
                                    <Route path={"/:customerId/system-map/:type/:projectId"}><SystemMapComponent projectInfo={inforProject}></SystemMapComponent></Route>
                                </Switch>
                            </div>
                        </> : <></>
                    }
                </>}
        </>

    )
}

export default SystemMap;