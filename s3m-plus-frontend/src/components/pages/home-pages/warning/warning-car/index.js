import React, { useState } from "react";
import { useEffect } from "react";
import { useParams } from "react-router-dom";
import moment from "moment";
import { Calendar } from "primereact/calendar";
import ReactModal from "react-modal";
import "../../index.css"

import WarningService from "../../../../../services/WarningService";
import WarningCarService from "../../../../../services/WarningCarService";
import UserService from "../../../../../services/UserService";
import { t } from "i18next";


const WarningCAR = ({ customerId, projectId, systemTypeId, deviceId }) => {
    const $ = window.$;
    const param = useParams();

    const [userId, setUserId] = useState();
    const [isActiveButton, setIsActiveButton] = useState(true);
    const [fromDate, setFromDate] = useState(moment(new Date()).format("YYYY-MM-DD") + " 00:00:00");
    const [toDate, setToDate] = useState(moment(new Date()).format("YYYY-MM-DD") + " 23:59:59");
    const [display, setDisplay] = useState(false)
    const [warningLevel, setWarningLevel] = useState(0);
    const [valueTime, setValueTime] = useState(0);
    // active modal state
    const [isModalCAROpen, setIsModalCAROpen] = useState(false);
    const [isModalAddCAROpen, setIsModalAddCAROpen] = useState(false);


    const [totalPage, setTotalPage] = useState(1);
    const [totalPageCAR, setTotalPageCAR] = useState(1);
    // current page state
    const [page, setPage] = useState(1);
    const [pageCAR, setPageCAR] = useState(1);
    const [data, setData] = useState([]);
    const [order, setOrder] = useState();
    const [typeOrder, setTypeOrder] = useState();
    const [getWarningCar, setGetWarningCar] = useState(0);
    const [createDate, setCreateDate] = useState("");
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

    const closeModalCAR = () => {
        setIsModalCAROpen(false);
        setIsModalAddCAROpen(false)
    };

    const saveModalCAR = () => {
        setIsModalCAROpen(false);
        setIsModalAddCAROpen(false)
    };

    const handleChangeView = (isActive) => {
        setIsActiveButton(!isActive)
        setValueTime(() => 1)
        let fromTime = moment(new Date).format("YYYY-MM-DD") + " 00:00:00"
        let toTime = moment(new Date).format("YYYY-MM-DD") + " 23:59:59"

        funcGetWarningCar(fromTime, toTime, 1, systemTypeId, projectId)
        setGetWarningCar(1)
        setFromDate(fromTime)
        setToDate(toTime)
    }

    const getDataByDate = () => {
        if (fromDate > toDate) {
            setDisplay(true)
        } else {
            setDisplay(false)
            let fromTime = moment(fromDate).format("YYYY-MM-DD") + " 00:00:00";
            let toTime = moment(toDate).format("YYYY-MM-DD") + " 23:59:59";
            setFromDate(fromTime)
            setToDate(toTime)
            funcGetWarningCar(fromTime, toTime, 1, systemTypeId, projectId)
            setGetWarningCar(1)
        }
    }


    const funcSortCAR = (col) => {
        setTypeOrder(col);
        if (order === "ASC") {
            const sorted = [...dataWarningCar].sort((a, b) => {
                return a[col] > b[col] ? 1 : -1

            });
            setDataWarningCar(sorted);
            setOrder("DSC");
        }
        else {
            const sorted = [...dataWarningCar].sort((a, b) => {
                return a[col] < b[col] ? 1 : -1
            });
            setDataWarningCar(sorted);
            setOrder("ASC");
        }
    }

    const funcSortTimeCAR = (col) => {
        setTypeOrder(col);
        if (order === "ASC") {
            const sorted = [...dataWarningCar].sort((a, b) => {
                let date1 = new Date(a[col]);
                let date2 = new Date(b[col]);
                return date1 > date2 ? 1 : -1;
            });
            setDataWarningCar(sorted);
            setOrder("DSC");
        }
        else {
            const sorted = [...dataWarningCar].sort((a, b) => {
                let date1 = new Date(a[col]);
                let date2 = new Date(b[col]);
                return date1 < date2 ? 1 : -1;
            });
            setDataWarningCar(sorted);
            setOrder("ASC");
        }
    }


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
        funcGetWarningCar(fromTime, toTime, 1, systemTypeId, projectId);
        setGetWarningCar(1)

    }


    const funcGetWarningCar = async (fromDate, toDate, pageCAR) => {
        $('#tableCAR').hide();
        $('#loadingCAR').show();

        let res = await WarningCarService.getWarningCars(customerId, systemTypeId, projectId, fromDate ? fromDate : null,
            toDate ? toDate : null, pageCAR)
        if (res.status === 200) {
            setDataWarningCar(() => res.data.data)
            setTotalPageCAR(() => res.data.totalPage);
            setPageCAR(1);
            $('#tableCAR').show();
            $('#loadingCAR').hide();
        }
    }


    const handleClickCAR = async (warningCarId) => {
        let customerId = param.customerId;
        let res = await WarningCarService.getWarningCarById(customerId, warningCarId)
        if (res.status === 200) {
            setDataWarningCarUpdate(() => res.data)
            setIsModalCAROpen(true);
        }

    }

    const funcUpdateWarningCar = async () => {
        if (dataWarningCarUpdate.organizationCreate == "" || dataWarningCarUpdate.organizationCreate.trim() === "") {
            $.alert({
                title: t('content.notification'),
                content: t('content.home_page.plan.check_null') + " " + t('content.home_page.plan.organization_create'),
            });
        } else if (dataWarningCarUpdate.content == "" || dataWarningCarUpdate.content.trim() === "") {
            $.alert({
                title: t('content.notification'),
                content: t('content.home_page.plan.check_null') + " " + t('content.home_page.plan.content'),
            });
        } else if (dataWarningCarUpdate.organizationExecution == null || dataWarningCarUpdate.organizationExecution.trim() === "") {
            $.alert({
                title: t('content.notification'),
                content: t('content.home_page.plan.check_null') + " " + t('content.home_page.plan.organization_execution'),
            });
        }
        else {
            let response = await WarningCarService.updateWarningCar(param.customerId, dataWarningCarUpdate);
            if (response.status === 200) {
                $.alert({
                    title: t('content.notification'),
                    content: t('content.home_page.plan.update_success'),
                });
                saveModalCAR();
                if (getWarningCar == 0) {
                    funcGetWarningCar(null, null, 1, 1, projectId)
                } else {
                    funcGetWarningCar(fromDate, toDate, 1, 1, projectId)
                }
            } else if (response.status === 400) {
                $.alert({
                    title: t('content.notification'),
                    content: t('content.home_page.plan.update_fail'),
                });
            } else if (response.status === 500) {
                $.alert({
                    title: t('content.notification'),
                    content: t('content.home_page.plan.update_fail'),
                });
            }
        }
    }


    const handleInputChangeUpdate = (e) => {
        const { name, value } = e.target;
        const newDataWarningCar = { ...dataWarningCarUpdate, [name]: value };
        setDataWarningCarUpdate(newDataWarningCar);
    };
    const statusMapping = {
        1: t('content.home_page.plan.newly_created'),
        2: t('content.home_page.plan.detected'),
        3: t('content.home_page.plan.resolve'),
    };

    function removeDiacritics(text) {
        return text.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
    }

    const filteredData = dataWarningCar.filter((item) => {
        const statusText = statusMapping[item.status];
        return (
            (item.area !== null && removeDiacritics(item.area.toString().toLowerCase()).includes(removeDiacritics(query.toLowerCase()))) ||
            (item.deviceName !== null && removeDiacritics(item.deviceName.toString().toLowerCase()).includes(removeDiacritics(query.toLowerCase()))) ||
            (item.objectTypeName !== null && removeDiacritics(item.objectTypeName.toString().toLowerCase()).includes(removeDiacritics(query.toLowerCase()))) ||
            (item.loadTypeName !== null && removeDiacritics(item.loadTypeName.toString().toLowerCase()).includes(removeDiacritics(query.toLowerCase()))) ||
            (item.id !== null && removeDiacritics(item.id.toString().toLowerCase()).includes(removeDiacritics(query.toLowerCase()))) ||
            (item.status !== null && removeDiacritics(statusText.toString().toLowerCase()).includes(removeDiacritics(query.toLowerCase()))) ||
            (item.createDate !== null && removeDiacritics(item.createDate.toString().toLowerCase()).includes(removeDiacritics(query.toLowerCase()))) ||
            (item.updateDate !== null && removeDiacritics(item.updateDate.toString().toLowerCase()).includes(removeDiacritics(query.toLowerCase())))
        );
    });

    useEffect(() => {
        document.title = "Cảnh báo";
        getUser();

        // If fromDate and toDate are not defined, call funcGetWarningCar without them
        funcGetWarningCar(null, null, pageCAR);
        setGetWarningCar(0)

    }, [customerId, projectId, systemTypeId]);

    return (
        <>
            <div className="input-group p-1">
                <div className="content-warning-calendar">
                    <div className="input-group">
                        <div className="input-group-prepend float-left" style={{ zIndex: 0 }}>
                            <button className="btn btn-outline-secondary" title="Kiểu xem" type="button" style={{ backgroundColor: isActiveButton ? "#0a1a5c" : "#e9ecef" }} onClick={() => handleChangeView(isActiveButton)}>
                                <img src="/resources/image/icon-calendar.svg" style={{ height: "18px" }}></img>
                            </button>
                            <button className="btn btn-outline-secondary btn-time" title="Kiểu xem" type="button" style={{ backgroundColor: isActiveButton ? "#e9ecef" : "#0a1a5c" }} onClick={() => handleChangeView(isActiveButton)}>
                                <img src="/resources/image/icon-play.svg" style={{ height: "18px" }}></img>
                            </button>
                        </div>


                        {!isActiveButton && (
                            <div className="input-group float-left mr-1 select-calendar" style={{ width: "100px", marginLeft: 10, height: 34 }}>
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
                                        <button type="button" className="btn btn-primary" onClick={() => getDataByDate()} style={{ backgroundColor: "#0a1a5c", borderColor: "#fff" }}>Lấy dữ liệu</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div className="warning-nav ">
                <div className="position-relative">
                    <input
                        type="text"
                        placeholder={t('content.home_page.search') + ' .......'}
                        className="warning-search"
                        value={query}
                        onChange={(e) => setQuery(e.target.value)}
                    />
                    <i className="fa fa-search position-absolute" style={{ left: "230px", top: "10px" }}></i>

                </div>
            </div>
            <div className="loadingCAR" id="loadingCAR" style={{ marginLeft: "50%" }}>
                <img height="60px" src="/resources/image/loading2.gif" alt="loading" />
            </div>
            <div className="table-warning mt-2" >
                {display ? <h5>Giá trị từ ngày phải nhỏ hơn đến ngày</h5> : null}
                <div style={{ overflowY: "scroll" }}>
                    <table className="table" id="tableCAR">
                        <thead>
                            <tr height="40px">
                                <th>
                                    {t('content.no')}
                                </th>
                                <th>
                                    {t('content.device')}
                                    <i className={typeOrder == "deviceName" ? (order == "ASC" ? "fas fa-solid fa-sort-down ml-2 fa-lg" : "fas fa-solid fa-sort-up ml-2 fa-lg") : "fas fa-solid fa-sort ml-2 fa-lg"} style={{ color: "#FFF" }} onClick={() => funcSortCAR("deviceName")}></i>
                                </th>
                                <th>
                                    {t('content.device_type')}
                                    <i className={typeOrder == "objectTypeName" ? (order == "ASC" ? "fas fa-solid fa-sort-down ml-2 fa-lg" : "fas fa-solid fa-sort-up ml-2 fa-lg") : "fas fa-solid fa-sort ml-2 fa-lg"} style={{ color: "#FFF" }} onClick={() => funcSortCAR("objectTypeName")}></i>
                                </th>
                                <th >
                                    {t('content.load_type')}
                                    <i className={typeOrder == "loadTypeName" ? (order == "ASC" ? "fas fa-solid fa-sort-down ml-2 fa-lg" : "fas fa-solid fa-sort-up ml-2 fa-lg") : "fas fa-solid fa-sort ml-2 fa-lg"} style={{ color: "#FFF" }} onClick={() => funcSortCAR("loadTypeName")}></i>
                                </th>
                                <th >
                                    ID
                                    <i className={typeOrder == "id" ? (order == "ASC" ? "fas fa-solid fa-sort-down ml-2 fa-lg" : "fas fa-solid fa-sort-up ml-2 fa-lg") : "fas fa-solid fa-sort ml-2 fa-lg"} style={{ color: "#FFF" }} onClick={() => funcSortCAR("id")}></i>
                                </th>
                                <th>
                                    {t('content.status')}
                                    <i className={typeOrder == "status" ? (order == "ASC" ? "fas fa-solid fa-sort-down ml-2 fa-lg" : "fas fa-solid fa-sort-up ml-2 fa-lg") : "fas fa-solid fa-sort ml-2 fa-lg"} style={{ color: "#FFF" }} onClick={() => funcSortCAR("status")}></i>
                                </th>
                                <th>
                                    {t('content.create_date')}
                                    <i className={typeOrder == "createDate" ? (order == "ASC" ? "fas fa-solid fa-sort-down ml-2 fa-lg" : "fas fa-solid fa-sort-up ml-2 fa-lg") : "fas fa-solid fa-sort ml-2 fa-lg"} style={{ color: "#FFF" }} onClick={() => funcSortTimeCAR("createDate")}></i>
                                </th>
                                <th >
                                    {t('content.update_date')}
                                    <i className={typeOrder == "updateDate" ? (order == "ASC" ? "fas fa-solid fa-sort-down ml-2 fa-lg" : "fas fa-solid fa-sort-up ml-2 fa-lg") : "fas fa-solid fa-sort ml-2 fa-lg"} style={{ color: "#FFF" }} onClick={() => funcSortTimeCAR("updateDate")}></i>
                                </th>

                            </tr>
                        </thead>

                        <tbody style={{ lineHeight: 1 }} >
                            {filteredData?.map((item, index) => (

                                <tr key={index} height="30px" onClick={() => handleClickCAR(item.id)}>
                                    <td className="text-center">{index + 1}</td>
                                    <td className="text-center" >{item.deviceName}</td>
                                    <td className="text-center" >{item.objectTypeName}</td>
                                    <td className="text-center" >{item.loadTypeName}</td>
                                    <td className="text-center text-uppercase" >{item.id}</td>
                                    <td className="text-center" >{item.status == 1 ? t('content.home_page.plan.newly_created') : item.status == 2 ? t('content.home_page.plan.detected') : t('content.home_page.plan.resolve')}</td>
                                    <td className="text-center" >{item.createDate}</td>
                                    <td className="text-center" >{item.updateDate}</td>
                                </tr>
                            ))}
                            {filteredData.length < 1 &&

                                <tr height="30px">
                                    <td colSpan="8" className="text-center">Không có dữ liệu</td>
                                </tr>
                            }

                        </tbody>
                    </table>
                </div>
                {/* <div id="pagination">
                    <Pagination
                        activePage={pageCAR}
                        totalItemsCount={totalPageCAR}
                        pageRangeDisplayed={10}
                        itemsCountPerPage={1}
                        onChange={e => handlePaginationCAR(e)}
                        activeClass="active"
                        itemClass="pagelinks"
                        prevPageText="Truớc"
                        nextPageText="Sau"
                        firstPageText="Đầu"
                        lastPageText="Cuối"
                    />
                </div> */}
            </div>
            {/* MODAL Detail */}
            <ReactModal
                isOpen={isModalCAROpen}
                onRequestClose={() => {
                    setIsModalCAROpen(false);
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
                    {t('content.home_page.plan.update_title')}
                </h4>
                <br />
                <table className="table">
                    <tbody>
                        <tr>
                            <th scope="row">{t('content.create_date')}</th>
                            <td className="col-10">
                                <input
                                    disabled
                                    type="text"
                                    className="form-control"
                                    name="createDate"
                                    value={dataWarningCarUpdate.createDate != null ? dataWarningCarUpdate.createDate : createDate}
                                />
                            </td>
                        </tr>
                        <br />
                        <tr>
                            <th scope="row">{t('content.status')}</th>
                            <td className="col-10">
                                <input
                                    disabled
                                    type="text"
                                    className="form-control"
                                    name="createDate"
                                    value={dataWarningCarUpdate.status == 1 ? t('content.home_page.plan.newly_created') : dataWarningCarUpdate.status == 2 ? t('content.home_page.plan.detected') : t('content.home_page.plan.resolve')}
                                />

                            </td>
                        </tr>
                        <br />
                        <tr>
                            <th scope="row">ID</th>
                            <td>
                                <input
                                    disabled
                                    type="text"
                                    className="form-control"
                                    name="id"
                                    value={dataWarningCarUpdate.id}
                                />
                            </td>
                        </tr>
                        <br />
                        <tr>
                            <th scope="row">{t('content.home_page.plan.organization_create')}</th>
                            <td>
                                <input
                                    disabled={dataWarningCarUpdate.createId != userId ? true : (dataWarningCarUpdate.status == 3 ? true : false)}
                                    type="text"
                                    className="form-control"
                                    name="organizationCreate"
                                    value={dataWarningCarUpdate.organizationCreate}
                                    onChange={handleInputChangeUpdate}
                                />

                            </td>
                        </tr>
                        <br />
                        <tr>
                            <th>{t('content.home_page.plan.content')}</th>
                            <td>
                                <textarea
                                    disabled={dataWarningCarUpdate.createId != userId ? true : (dataWarningCarUpdate.status == 3 ? true : false)}
                                    className="form-control"
                                    name="content"
                                    rows="6"
                                    value={dataWarningCarUpdate.content}
                                    onChange={handleInputChangeUpdate}
                                ></textarea>
                            </td>
                        </tr>
                        <br />
                        <tr>
                            <th>{t('content.home_page.plan.reason_and_measure')}</th>
                            <td>
                                <textarea
                                    disabled={dataWarningCarUpdate.createId != userId ? (authorUpdateCar == true ? (dataWarningCarUpdate.status == 3 ? true : false) : true) : (authorUpdateCar == true ? (dataWarningCarUpdate.status == 3 ? true : false) : true)}
                                    className="form-control"
                                    name="reasonMethod"
                                    rows="4"
                                    value={dataWarningCarUpdate.reasonMethod}
                                    onChange={handleInputChangeUpdate}
                                ></textarea>
                            </td>
                        </tr>
                        <br />
                        <tr>
                            <th scope="row">{t('content.home_page.plan.organization_execution')}</th>
                            <td>
                                <input
                                    disabled={dataWarningCarUpdate.createId != userId ? (authorUpdateCar == true ? (dataWarningCarUpdate.status == 3 ? true : false) : true) : (authorUpdateCar == true ? (dataWarningCarUpdate.status == 3 ? true : false) : true)}
                                    type="text"
                                    className="form-control"
                                    name="organizationExecution"
                                    value={dataWarningCarUpdate.organizationExecution}
                                    onChange={handleInputChangeUpdate}
                                />
                            </td>
                        </tr>
                        <br />
                        <tr>
                            <th scope="row">{t('content.home_page.plan.completion_time')}</th>
                            <td>
                                <input
                                    disabled={dataWarningCarUpdate.createId != userId ? (authorUpdateCar == true ? (dataWarningCarUpdate.status == 3 || dataWarningCarUpdate.status == 1 ? true : false) : true) : (authorUpdateCar == true ? (dataWarningCarUpdate.status == 3 || dataWarningCarUpdate.status == 1 ? true : false) : true)}
                                    type="datetime-local"
                                    className="form-control"
                                    name="completionTime"
                                    value={dataWarningCarUpdate.completionTime}
                                    onChange={handleInputChangeUpdate}
                                />
                            </td>
                        </tr>
                        <br />
                        <tr>
                            <th scope="row">{t('content.home_page.plan.result_execution')}</th>
                            <td>
                                <input
                                    disabled={dataWarningCarUpdate.createId != userId ? (authorUpdateCar == true ? (dataWarningCarUpdate.status == 3 || dataWarningCarUpdate.status == 1 ? true : false) : true) : (authorUpdateCar == true ? (dataWarningCarUpdate.status == 3 || dataWarningCarUpdate.status == 1 ? true : false) : true)}
                                    type="text"
                                    className="form-control"
                                    name="resultExecution"
                                    value={dataWarningCarUpdate.resultExecution}
                                    onChange={handleInputChangeUpdate}
                                />
                            </td>
                        </tr>
                        <br />
                        <tr>
                            <th scope="row">{t('content.home_page.plan.organization_test')}</th>
                            <td>
                                <input
                                    disabled={dataWarningCarUpdate.createId != userId ? (authorUpdateCar == true ? (dataWarningCarUpdate.status == 3 || dataWarningCarUpdate.status == 1 ? true : false) : true) : (authorUpdateCar == true ? (dataWarningCarUpdate.status == 3 || dataWarningCarUpdate.status == 1 ? true : false) : true)}
                                    type="text"
                                    className="form-control"
                                    name="organizationTest"
                                    value={dataWarningCarUpdate.organizationTest}
                                    onChange={handleInputChangeUpdate}
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
                            onClick={funcUpdateWarningCar}
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

        </>

    )
}

export default WarningCAR;