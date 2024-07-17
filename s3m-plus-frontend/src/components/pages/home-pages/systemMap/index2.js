import { useState } from "react";
import { useEffect } from "react";
import { useRef } from "react";
import { useLocation, useParams, useHistory } from "react-router-dom/cjs/react-router-dom.min";
import { Route, Switch } from 'react-router-dom';
import AccessDenied from "../../access-denied/AccessDenied";
import overviewLoadService from "../../../../services/OverviewLoadService";
import customerService from "../../../../services/CustomerService";
import moment from "moment";
import SystemMapComponent from "./systemMap2";

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
    const [selectedProject, setSelectedProject] = useState(0);
    const [systemTypeId, setSystemTypeId] = useState(0);


    const categorySite = [
        {
            name: "tải điện",
            system_type: 1,
            // chọn module
            act: "/resources/image/module-icon/act_load.gif",
            // module hoạt động
            sys: "/resources/image/module-icon/sys_load.svg",
            // không hoạt động
            not: "/resources/image/module-icon/not_load.svg",
            type: "load"
        },
        {
            name: "điện mặt trời",
            system_type: 2,
            act: "/resources/image/module-icon/act_solar.gif",
            sys: "/resources/image/module-icon/sys_solar.svg",
            not: "/resources/image/module-icon/not_solar.svg",
            type: "pv"
        },
        {
            name: "lưới điện",
            system_type: 5,
            act: "/resources/image/module-icon/act_grid.gif",
            sys: "/resources/image/module-icon/sys_grid.svg",
            not: "/resources/image/module-icon/not_grid.svg",
            type: "grid"
        },
        {
            name: "pin lưu trữ",
            system_type: 4,
            act: "/resources/image/module-icon/act_battery.gif",
            sys: "/resources/image/module-icon/sys_battery.svg",
            not: "/resources/image/module-icon/not_battery.svg",
        },
        {
            name: "điện gió",
            system_type: 3,
            act: "/resources/image/module-icon/act_wind.gif",
            sys: "/resources/image/module-icon/sys_wind.svg",
            not: "/resources/image/module-icon/not_wind.svg",
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
                        setSitesSearch(data);
                        if (data.length > 0) {
                            setSelectedProject(data[0].projectId)
                        }
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
                    setSitesSearch(data);
                    if (data.length > 0) {
                        setSelectedProject(data[0].projectId)
                    }
                }
            } else {
                let res = await HomeService.getProjectLocationsHomePageByCustomerId(cusId, 0, "", "");
                if (res.status === 200) {
                    var data = res.data;
                    if (typeof param.projectId !== "undefined") {
                        data = data.filter(d => d.projectId == param.projectId);
                    }
                    setTableSite(data);
                    setSitesSearch(data);
                    if (data.length > 0) {
                        setSelectedProject(data[0].projectId)
                    } if (data.length > 0) {
                        setSelectedProject(data[0].projectId)
                    }
                }
            }
        }
    }

    const funcFocus = (projectId, type) => {
        for (let i = 0; i < tableSite.length; i++) {
            for (let j = 1; j < 6; j++) {
                if (projectId == tableSite[i].projectId && type == j) {
                    $('#' + projectId + type).css({ backgroundColor: "#fff0e3" });
                } else {
                    $('#' + tableSite[i].projectId + j).css({ backgroundColor: "#FFF" });
                }
            }
        }
    }

    const settingOpenOneline = async (customerId, projectId, inforModule, projectName) => {
        setOpenOneline(() => true);
        let type = inforModule.system_type;
        setSystemTypeId(type);
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
        let siteName = document.getElementById("tableSite-name").value;
        let selectSearch = document.getElementById("select-search").value;
        if (siteName === "" && selectSearch == 0) {
            setSitesSearch(tableSite);
            if (tableSite.length > 0) {
                setSelectedProject(tableSite[0].projectId)
            }
        } else {
            if (selectSearch != 0) {
                let siteSearch = tableSite?.filter(d => d.projectName.toLowerCase().includes(selectSearch.toLowerCase()));
                siteSearch = siteSearch?.filter(d => d.projectName.toLowerCase().includes(siteName.toLowerCase()));
                setSitesSearch(siteSearch);
                if (siteSearch.length > 0) {
                    setSelectedProject(siteSearch[0].projectId)
                }
            } else {
                let siteSearch = tableSite?.filter(d => d.projectName.toLowerCase().includes(siteName.toLowerCase()));
                setSitesSearch(siteSearch);
                if (siteSearch.length > 0) {
                    setSelectedProject(siteSearch[0].projectId)
                }
            }
        }

    }
    const getListOneLine = async (customerId) => {
        let res = await ProjectService.getProjectByCustomerId(customerId)
        if (res.status === 200) {
            setListOneLineNum(res.data)
        }
    }

    const funcSelectProject = async (item, index) => {
        sitesSearch.splice(index, 1);
        sitesSearch.unshift(item)
        if (sitesSearch.length > 0) {
            setSelectedProject(sitesSearch[0].projectId)
        }
    }

    useEffect(() => {
        checkAuthorization()
        document.title = "Sơ đồ 1 sợi";
    }, [param.customerId, param.projectId]);

    return (
        <>
            {accessDenied ?
                <AccessDenied></AccessDenied>
                :
                <div className="d-flex h-100">
                    {/** ---------------------------TABLE SITE --------------------------------- */}
                    <div className="border-system-map rounded p-1" style={{ maxWidth: "300px" }} ref={SiteRef}>

                        <div className="p-1">
                            <div className="title-system-map">
                                <span className="title-up text-left w-100"><div style={{ marginTop: "-21px", color: "white" }}><i className="fa-solid fa-diagram-project ml-1" style={{ color: "#fff" }}></i> {t('content.category.tool_page.list_project_diagram')}</div></span>
                            </div>
                            <div className="search-system-map pt-1 d-flex">
                                <div>
                                    <input type="text" id="tableSite-name" className="input" name="tableSite-name" placeholder="  Tìm kiếm " onChange={(e) => searchSites(e)} />
                                </div>
                                <div className="pl-1" style={{ flexGrow: "1" }}>
                                    <select id="select-search" onChange={(e) => searchSites(e)}
                                        style={{ backgroundColor: "var(--ses-blue-60-color)", borderRadius: 5, color: "white", width: "95px", }}>
                                        <option className="value" value={0}>Tất cả</option>
                                        {
                                            tableSite?.map((s, i) => (
                                                <option key={i} className="value" value={s.id}>{s.projectName}</option>
                                            ))}
                                    </select>
                                </div>
                            </div>

                            <div className="system-map-select-project">
                                {sitesSearch.map((item, index) => (
                                    <div key={index}>
                                        <div className={((selectedProject == 0 && index == 0) || (item.projectId == selectedProject)) ? "sys-map-project w-100 pl-2 active" : "sys-map-project w-100 pl-2"} onClick={() => { funcSelectProject(item, index) }}>
                                            <div className="d-inline-flex">
                                                <i className="fa-brands fa-edge pt-2" style={{ color: "#FFF" }}></i>
                                                <span className="text-uppercase pl-2" title={item.projectName}>{item.projectName}</span>
                                            </div>
                                            {
                                                (selectedProject == 0 && index == 0) || (item.projectId == selectedProject) &&
                                                <>
                                                    <div>
                                                        <div className="text-white pl-3 font-italic hidden-long-text" title={item.areaName + "," + item.managerName}>{item.areaName}, {item.managerName}</div>
                                                    </div>
                                                </>
                                            }
                                        </div>
                                        {
                                            (selectedProject == 0 && index == 0) || (item.projectId == selectedProject) &&
                                            <>
                                                <div className="sys-map-list-module pt-1">
                                                    {categorySite.map((itemC, indexC) => {
                                                        var a = listOneLineNum?.filter(e => e.projectId == selectedProject)[0];
                                                        if (indexC == 0) {
                                                            if (a?.loadNum > 0) {
                                                                return (
                                                                    <div id={item.projectId + "" + itemC.system_type} className="system-map-item-module p-1" key={indexC} onClick={() => {
                                                                        settingOpenOneline(item.customerId, item.projectId, itemC, item.projectName);
                                                                    }}>
                                                                        <div className="d-flex">
                                                                            <div className="p-1">
                                                                                <img src={itemC.sys} style={{ height: 75 }}></img>
                                                                            </div>
                                                                            <div className="p-1 text-right" style={{ flexGrow: "1" }}>
                                                                                <div className="sys-map-module">

                                                                                </div>
                                                                            </div>
                                                                        </div>
                                                                        <div className="text-uppercase text-center" style={{ fontWeight: "700" }}>
                                                                            {itemC.name}
                                                                        </div>
                                                                    </div>
                                                                )
                                                            } else {
                                                                return (
                                                                    <div id={item.projectId + "" + itemC.system_type} className="system-map-item-module p-1" key={indexC}>
                                                                        <div className="d-flex">
                                                                            <div className="p-1">
                                                                                <img src={itemC.not} style={{ height: 75 }}></img>
                                                                            </div>
                                                                            <div className="p-1 text-right" style={{ flexGrow: "1" }}>
                                                                                <div className="not-sys-map-module">

                                                                                </div>
                                                                            </div>
                                                                        </div>
                                                                        <div className="text-uppercase text-center" style={{ fontWeight: "700" }}>
                                                                            {itemC.name}
                                                                        </div>
                                                                    </div>
                                                                )
                                                            }
                                                        } else if (indexC == 1) {
                                                            if (a?.solarNum > 0) {
                                                                return (
                                                                    <div id={item.projectId + "" + itemC.system_type} className="system-map-item-module p-1" key={indexC} onClick={() => {
                                                                        settingOpenOneline(item.customerId, item.projectId, itemC, item.projectName);
                                                                    }}>
                                                                        <div className="d-flex">
                                                                            <div className="p-1">
                                                                                <img src={itemC.sys} style={{ height: 75 }}></img>
                                                                            </div>
                                                                            <div className="p-1 text-right" style={{ flexGrow: "1" }}>
                                                                                <div className="sys-map-module">

                                                                                </div>
                                                                            </div>
                                                                        </div>
                                                                        <div className="text-uppercase text-center" style={{ fontWeight: "700" }}>
                                                                            {itemC.name}
                                                                        </div>
                                                                    </div>
                                                                )
                                                            } else {
                                                                return (
                                                                    <div id={item.projectId + "" + itemC.system_type} className="system-map-item-module p-1" key={indexC}>
                                                                        <div className="d-flex">
                                                                            <div className="p-1">
                                                                                <img src={itemC.not} style={{ height: 75 }}></img>
                                                                            </div>
                                                                            <div className="p-1 text-right" style={{ flexGrow: "1" }}>
                                                                                <div className="not-sys-map-module">

                                                                                </div>
                                                                            </div>
                                                                        </div>
                                                                        <div className="text-uppercase text-center" style={{ fontWeight: "700" }}>
                                                                            {itemC.name}
                                                                        </div>
                                                                    </div>
                                                                )
                                                            }
                                                        } else if (indexC == 2) {
                                                            if (a?.utilityNum > 0) {
                                                                return (
                                                                    <div id={item.projectId + "" + itemC.system_type} className="system-map-item-module p-1" key={indexC} onClick={() => {
                                                                        settingOpenOneline(item.customerId, item.projectId, itemC, item.projectName);
                                                                    }}>
                                                                        <div className="d-flex">
                                                                            <div className="p-1">
                                                                                <img src={itemC.sys} style={{ height: 75 }}></img>
                                                                            </div>
                                                                            <div className="p-1 text-right" style={{ flexGrow: "1" }}>
                                                                                <div className="sys-map-module">

                                                                                </div>
                                                                            </div>
                                                                        </div>
                                                                        <div className="text-uppercase text-center" style={{ fontWeight: "700" }}>
                                                                            {itemC.name}
                                                                        </div>
                                                                    </div>
                                                                )
                                                            } else {
                                                                return (
                                                                    <div id={item.projectId + "" + itemC.system_type} className="system-map-item-module p-1" key={indexC}>
                                                                        <div className="d-flex">
                                                                            <div className="p-1">
                                                                                <img src={itemC.not} style={{ height: 75 }}></img>
                                                                            </div>
                                                                            <div className="p-1 text-right" style={{ flexGrow: "1" }}>
                                                                                <div className="not-sys-map-module">

                                                                                </div>
                                                                            </div>
                                                                        </div>
                                                                        <div className="text-uppercase text-center" style={{ fontWeight: "700" }}>
                                                                            {itemC.name}
                                                                        </div>
                                                                    </div>
                                                                )
                                                            }
                                                        } else if (indexC == 3) {
                                                            if (a?.evNum > 0) {
                                                                return (
                                                                    <div id={item.projectId + "" + itemC.system_type} className="system-map-item-module p-1" key={indexC} onClick={() => {
                                                                        settingOpenOneline(item.customerId, item.projectId, itemC, item.projectName);
                                                                    }}>
                                                                        <div className="d-flex">
                                                                            <div className="p-1">
                                                                                <img src={itemC.sys} style={{ height: 75 }}></img>
                                                                            </div>
                                                                            <div className="p-1 text-right" style={{ flexGrow: "1" }}>
                                                                                <div className="sys-map-module">

                                                                                </div>
                                                                            </div>
                                                                        </div>
                                                                        <div className="text-uppercase text-center" style={{ fontWeight: "700" }}>
                                                                            {itemC.name}
                                                                        </div>
                                                                    </div>
                                                                )
                                                            } else {
                                                                return (
                                                                    <div id={item.projectId + "" + itemC.system_type} className="system-map-item-module p-1" key={indexC}>
                                                                        <div className="d-flex">
                                                                            <div className="p-1">
                                                                                <img src={itemC.not} style={{ height: 75 }}></img>
                                                                            </div>
                                                                            <div className="p-1 text-right" style={{ flexGrow: "1" }}>
                                                                                <div className="not-sys-map-module">

                                                                                </div>
                                                                            </div>
                                                                        </div>
                                                                        <div className="text-uppercase text-center" style={{ fontWeight: "700" }}>
                                                                            {itemC.name}
                                                                        </div>
                                                                    </div>
                                                                )
                                                            }
                                                        } else if (indexC == 4) {
                                                            if (a?.windNum > 0) {
                                                                return (
                                                                    <div id={item.projectId + "" + itemC.system_type} className="system-map-item-module p-1" key={indexC} onClick={() => {
                                                                        settingOpenOneline(item.customerId, item.projectId, itemC, item.projectName);
                                                                    }}>
                                                                        <div className="d-flex">
                                                                            <div className="p-1">
                                                                                <img src={itemC.sys} style={{ height: 75 }}></img>
                                                                            </div>
                                                                            <div className="p-1 text-right" style={{ flexGrow: "1" }}>
                                                                                <div className="sys-map-module">

                                                                                </div>
                                                                            </div>
                                                                        </div>
                                                                        <div className="text-uppercase text-center" style={{ fontWeight: "700" }}>
                                                                            {itemC.name}
                                                                        </div>
                                                                    </div>
                                                                )
                                                            } else {
                                                                return (
                                                                    <div id={item.projectId + "" + itemC.system_type} className="system-map-item-module p-1" key={indexC}>
                                                                        <div className="d-flex">
                                                                            <div className="p-1">
                                                                                <img src={itemC.not} style={{ height: 75 }}></img>
                                                                            </div>
                                                                            <div className="p-1 text-right" style={{ flexGrow: "1" }}>
                                                                                <div className="not-sys-map-module">

                                                                                </div>
                                                                            </div>
                                                                        </div>
                                                                        <div className="text-uppercase text-center" style={{ fontWeight: "700" }}>
                                                                            {itemC.name}
                                                                        </div>
                                                                    </div>
                                                                )
                                                            }
                                                        }
                                                    })}
                                                </div>
                                            </>
                                        }
                                    </div>
                                ))}

                            </div>
                        </div>

                    </div >
                    {/** ---------------------------TABLE SITE --------------------------------- */}
                    {/* <div style={{ width: "100%", height: "5px", backgroundColor: "#0a1a5c" }} ></div> */}
                    {/** ---------------------------ONELINE --------------------------------- */}
                    {
                        openOneline ? <>
                            <div id="oneline-info" className="position-relative pl-1">
                                <Switch>
                                    <Route path={"/:customerId/:projectSearchId/system-map/:type/:projectId/:systemMapId"}><SystemMapComponent projectInfo={inforProject}></SystemMapComponent></Route>
                                    <Route path={"/:customerId/:projectSearchId/system-map/:type/:projectId"}><SystemMapComponent projectInfo={inforProject}></SystemMapComponent></Route>
                                    <Route path={"/:customerId/system-map/:type/:projectId/:systemMapId"}><SystemMapComponent projectInfo={inforProject}></SystemMapComponent></Route>
                                    <Route path={"/:customerId/system-map/:type/:projectId"}><SystemMapComponent projectInfo={inforProject}></SystemMapComponent></Route>
                                </Switch>
                            </div>
                        </> : <></>
                    }
                </div>}
        </>

    )
}

export default SystemMap;