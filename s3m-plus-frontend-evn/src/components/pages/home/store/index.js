import React, { useEffect } from "react";
import { useState } from "react";

import StorageService from "../../../../services/StorageService";
import './index.css';
import "react-datepicker/dist/react-datepicker.css";
import TableHistory from "./tablehistory";
import { Calendar } from 'primereact/calendar';
import moment from "moment/moment";




const Store = () => {

    const [stores, setStores] = useState([]);
    const [schedules, setScheDules] = useState([]);
    const [dateForm, setDateForm] = useState({
        fromDate: new Date(),
        toDate: new Date()
    });
    const [checkLoadingStore, setCheckLoadingStore] = useState(false);

    // Lay API full(ROLE_EVN)
    const getListStore = async () => {
        let response = await StorageService.listStore();
        if (response.status === 200) {
            setCheckLoadingStore(true);
            setStores(response.data.storage);
            setScheDules(response.data.schedules);
        }
    }
    const getListStoreSearch = async () => {
        let response = await StorageService.listStoreSearch(moment(dateForm.fromDate).format('YYYY-MM-DD'), moment(dateForm.toDate).format('YYYY-MM-DD'));
        if (response.status === 200) {
            setCheckLoadingStore(true);
            setStores(response.data.storage);
            setScheDules(response.data.schedules);
        }
    }
    //ROLE_EVN
    // Lấy API moi supermanager (ROLE_EVN)
    const getListStoreBySuperManagerId = async (superManagerId) => {
        let response = await StorageService.listStoreBySuperManagerId(superManagerId);
        if (response.status === 200) {
            setCheckLoadingStore(true);
            setStores(response.data.storage);
            setScheDules(response.data.schedules);
        }
    }
    const getListStoreBySuperManagerIdSearch = async (superManagerId) => {
        let response = await StorageService.listStoreBySuperManagerIdSearch(superManagerId, moment(dateForm.fromDate).format('YYYY-MM-DD'), moment(dateForm.toDate).format('YYYY-MM-DD'));
        if (response.status === 200) {
            setCheckLoadingStore(true);
            setStores(response.data.storage);
            setScheDules(response.data.schedules);
        }
    }
    // Lay API moi manager
    const getListStoreByManagerId = async (managerId) => {
        let response = await StorageService.listStoreByManagerId(managerId);
        if (response.status === 200) {
            setCheckLoadingStore(true);
            setStores(response.data.storage);
            setScheDules(response.data.schedules);
        }
    }
    const getListStoreByManagerIdSearch = async (managerId) => {
        let response = await StorageService.listStoreByManagerIdSearch(managerId, moment(dateForm.fromDate).format('YYYY-MM-DD'), moment(dateForm.toDate).format('YYYY-MM-DD'));
        if (response.status === 200) {
            setCheckLoadingStore(true);
            setStores(response.data.storage);
            setScheDules(response.data.schedules);
        }
    }
    // Lay API moi Area

    const getListStoreByAreaId = async (areaId) => {
        let response = await StorageService.listAllStoreByAreaID(areaId);
        if (response.status === 200) {
            setStores(response.data.storage);
            setScheDules(response.data.schedules);
        }
    }
    const getListStoreByAreaIdSearch = async (areaId) => {
        let response = await StorageService.listAllStoreByAreaIDSearch(areaId, moment(dateForm.fromDate).format('YYYY-MM-DD'), moment(dateForm.toDate).format('YYYY-MM-DD'));
        if (response.status === 200) {
            setStores(response.data.storage);
            setScheDules(response.data.schedules);
        }
    }
    //ROLE_TINHTHANH
    // Lay API moi vung cua tinh thanh (ROLE_TinhThanh)



    useEffect(() => {
        document.title = "Lưu Trữ";
        let urlSearchParam;

        if (!new URLSearchParams(new URL(window.location).search).get("superManagerId") &&
            !new URLSearchParams(new URL(window.location).search).get("managerId") &&
            !new URLSearchParams(new URL(window.location).search).get("areaId")) {
            getListStore();
        }
        if (new URLSearchParams(new URL(window.location).search).get("superManagerId")) {
            urlSearchParam = new URLSearchParams(new URL(window.location).search).get("superManagerId");
            getListStoreBySuperManagerId(urlSearchParam);
        } else if (new URLSearchParams(new URL(window.location).search).get("managerId")) {
            urlSearchParam = new URLSearchParams(new URL(window.location).search).get("managerId");

            getListStoreByManagerId(urlSearchParam);
        } else if (new URLSearchParams(new URL(window.location).search).get("areaId")) {
            urlSearchParam = new URLSearchParams(new URL(window.location).search).get("areaId");

            getListStoreByAreaId(urlSearchParam);
        };

    }, [window.location.search]);
    const btnSearchOnclick = async () => {
        let urlSearchParam;

        if (!new URLSearchParams(new URL(window.location).search).get("superManagerId") &&
            !new URLSearchParams(new URL(window.location).search).get("managerId") &&
            !new URLSearchParams(new URL(window.location).search).get("areaId")) {
            getListStoreSearch();
        }
        if (new URLSearchParams(new URL(window.location).search).get("superManagerId")) {
            urlSearchParam = new URLSearchParams(new URL(window.location).search).get("superManagerId");
            getListStoreBySuperManagerIdSearch(urlSearchParam);
        } else if (new URLSearchParams(new URL(window.location).search).get("managerId")) {
            urlSearchParam = new URLSearchParams(new URL(window.location).search).get("managerId");

            getListStoreByManagerIdSearch(urlSearchParam);

        } else if (new URLSearchParams(new URL(window.location).search).get("areaId")) {
            urlSearchParam = new URLSearchParams(new URL(window.location).search).get("areaId");

            getListStoreByAreaIdSearch(urlSearchParam);
        };

    }

    return (


        <div id="history-tb" className="containerx">

            <div className="form-group mt-2 mb-0 ml-2" style={{ height: "31.5px" }}>
                {/* <div className="input-group float-left mr-1" style={{ width: '250px' }}>
                    <div className="input-group-prepend">
                        <span className="input-group-text pickericon">
                            <span className="far fa-calendar"></span>
                        </span>
                    </div>
                    <div>
                        <Pickadate.InputPicker id="chart-date-from"
                            value={dateForm.fromDate}
                            className="form-control pickadate"
                            style={{ fontSize: "13px" }}
                            readOnly={true}
                            autoComplete="off"
                            initialState={{
                                selected: new Date(),
                                template: "YYYY-MM-DD"
                            }}
                            onChangeValue={({ value }) => {
                                setDateForm({...dateForm, fromDate: value })
                            }}
                        />
                    </div>


                </div> */}


                <div className="input-group float-left mr-1" style={{ width: "270px" }}>
                    <div className="input-group-prepend">
                        <span className="input-group-text pickericon">
                            <span className="far fa-calendar"></span>
                        </span>
                    </div>
                    <Calendar
                        value={dateForm.fromDate}
                        id="chart-date-from"
                        className="celendar-picker"
                        dateFormat="yy-mm-dd"
                        onChange={e => setDateForm({ ...dateForm, fromDate: e.value })}
                    />
                </div>

                <div className="input-group float-left mr-1" style={{ width: "270px" }}>
                    <div className="input-group-prepend">
                        <span className="input-group-text pickericon">
                            <span className="far fa-calendar"></span>
                        </span>
                    </div>
                    <Calendar
                        value={dateForm.toDate}
                        id="chart-date-to"
                        className="celendar-picker"
                        dateFormat="yy-mm-dd"
                        onChange={e => setDateForm({ ...dateForm, toDate: e.value })}
                    />
                </div>

                <div className="operation-btn float-left" style={{ height: '31.5px'}}>
                    <button type="button" id="btn-search-chart" className="btn btn-outline-warning" style={{ height: '100%' }} onClick={(state) => btnSearchOnclick()}>
                        <i className="fa fa-search"></i>
                    </button>
                </div>


            </div>
            {checkLoadingStore ?
                <TableHistory
                    history={stores}
                    schedules={schedules}
                />
                : <img src="/resources/image/loading.gif " alt="loading"></img>
            }

            {/* <h1>{parameter.URLsearchparam}</h1>
            <h2>{parameter.type}</h2>
            <button onClick={(state) => demoHistory()}>click me</button> */}
        </div>


    )
}

export default Store;