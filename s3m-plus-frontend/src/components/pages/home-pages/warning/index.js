import React, { useState } from "react";
import { useEffect } from "react";
import { useParams } from "react-router-dom";
import WarningLoad from "./list";
import WarningCAR from "../../home-pages/warning/warning-car";
import "./index.css"
import AuthService from "../../../../services/AuthService";
import AccessDenied from "../../access-denied/AccessDenied";
import ProjectService from "../../../../services/ProjectService";
import CustomerService from "../../../../services/CustomerService";
import UserService from "../../../../services/UserService";
import WarningCarService from "../../../../services/WarningCarService";
import moment from "moment";
import { t } from "i18next";


const Warning = () => {
    const $ = window.$;
    const param = useParams();
    const [systemTypeId, setSystemTypeId] = useState(1);
    const [tableWarning, setTableWarning] = useState(true);
    const [role] = useState(AuthService.getRoleName());
    const [userName] = useState(AuthService.getUserName());
    const [accessDenied, setAccessDenied] = useState(false)

    const funcSetSystemType = (e) => {
        setSystemTypeId(() => e.target.value)
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
                            }
                        }
                        else {
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
                                setAccessDenied(false)
                            }
                        } else {
                            setAccessDenied(false)
                        }
                    }
                }
            }
        }
        let response = await UserService.getUserByUsername();
        if (response.status === 200) {
            const userData = response.data;
            setSystemTypeId(userData.prioritySystem);
        }
    }
    const [fromDate, setFromDate] = useState(moment(new Date()).format("YYYY-MM-DD") + " 00:00:00");
    const [toDate, setToDate] = useState(moment(new Date()).format("YYYY-MM-DD") + " 23:59:59");
    const [pageCAR, setPageCAR] = useState(1);
    const [dataWarningCar, setDataWarningCar] = useState([]);
    const [countStatus, setCountStatus] = useState([]);
    const updateCountStatus = async () => {
        // Thực hiện cuộc gọi API hoặc bất kỳ hành động cập nhật nào khác ở đây
        funcGetWarningCar(fromDate, toDate, pageCAR);
    };

    const funcGetWarningCar = async (fromDate, toDate, pageCAR) => {
        $('#tableCAR').hide();
        $('#loadingCAR').show();

        let res = await WarningCarService.getWarningCars(param.customerId, systemTypeId, param.projectId, fromDate, toDate, pageCAR)
        if (res.status === 200) {
            setDataWarningCar(() => res.data.data)
            setCountStatus(() => res.data.countStatus)
            $('#tableCAR').show();
            $('#loadingCAR').hide();
        }
    }

    useEffect(() => {
        checkAuthorization();
        document.title = "Cảnh báo";
        funcGetWarningCar(null, null, pageCAR);

    }, [tableWarning]);

    return (
        <>
            {accessDenied ?
                <AccessDenied></AccessDenied>
                :
                <>
                    <div className="div-warning">
                        <div className="system-type">
                            <div className="radio-tabs">
                                <label className="radio-tabs__field">
                                    <input type="radio" name="radio-tabs" value={1} className="radio-tabs__input" checked={systemTypeId == 1 ? true : false} onChange={(e) => funcSetSystemType(e)} />
                                    <span className="radio-tabs__text text-uppercase">
                                        <img src="/resources/image/icon-load-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                        {t('content.home_page.load')}</span>

                                </label>
                                <label className="radio-tabs__field" >
                                    <input type="radio" name="radio-tabs" value={2} className="radio-tabs__input" checked={systemTypeId == 2 ? true : false} onChange={(e) => funcSetSystemType(e)} />
                                    <span className="radio-tabs__text text-uppercase">
                                        <img src="/resources/image/icon-solar-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                        {t('content.home_page.solar')}</span>
                                </label>
                                <label className="radio-tabs__field" >
                                    <input type="radio" name="radio-tabs" value={5} className="radio-tabs__input" checked={systemTypeId == 5 ? true : false} onChange={(e) => funcSetSystemType(e)} />
                                    <span className="radio-tabs__text text-uppercase">
                                        <img src="/resources/image/icon-grid-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                        {t('content.home_page.grid')}</span>
                                </label>
                                <label className="radio-tabs__field" >
                                    <input type="radio" name="radio-tabs" value={3} className="radio-tabs__input" checked={systemTypeId == 3 ? true : false} onChange={(e) => funcSetSystemType(e)} />
                                    <span className="radio-tabs__text text-uppercase">
                                        <img src="/resources/image/icon-battery-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                        {t('content.home_page.battery')}</span>
                                </label>
                                <label className="radio-tabs__field" >
                                    <input type="radio" name="radio-tabs" value={4} className="radio-tabs__input" checked={systemTypeId == 4 ? true : false} onChange={(e) => funcSetSystemType(e)} />
                                    <span className="radio-tabs__text text-uppercase">
                                        <img src="/resources/image/icon-wind-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                        {t('content.home_page.wind')}</span>
                                </label>
                            </div>
                        </div>
                        <div style={{ flexGrow: "1", }} className="text-right" >
                            {tableWarning == true ?
                                <button className="btn-warning-car" onClick={() => { setTableWarning(false) }} style={{ position: "relative" }} title="Corrective Action Request">
                                    <div>
                                        <i className="fa-solid fa-file-pen" style={{ color: "#FFF" }}></i>
                                        {/* <label className="pt-1"> &nbsp; PHIẾU CAR <span className="circle-car">{countStatus > 0 ? countStatus : ""}</span>
                                    </label> */}
                                        <label className="pt-1">
                                            &nbsp; {t('content.car')}
                                            {countStatus > 0 ? <span className="circle">{countStatus}</span>
                                                : ""}
                                            {/* <span className="circle">{countStatus > 0 ? countStatus : ""}</span> */}
                                        </label>
                                    </div>

                                </button>

                                :
                                <button className="btn-warning-car" onClick={() => { setTableWarning(true) }}>
                                    <i className="fa-solid fa-triangle-exclamation" style={{ color: "#FFF" }}></i>
                                    <label className="pt-1 text-uppercase" > &nbsp; {t('content.warning')}</label>
                                </button>
                            }

                        </div>
                    </div>
                    <div>
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
                    </div>
                    {tableWarning == true ?
                        <>
                            <div>
                                <WarningLoad customerId={param.customerId} projectId={param.projectId} systemTypeId={systemTypeId} updateCountStatus={updateCountStatus}></WarningLoad>
                            </div>
                        </> :
                        <>
                            <div>
                                <WarningCAR customerId={param.customerId} projectId={param.projectId} systemTypeId={systemTypeId}></WarningCAR>
                            </div>
                        </>
                    }
                </>

            }
        </>

    )
}

export default Warning;