import React, { useEffect, useState } from "react";
import moment from "moment/moment";
import WarningSolarService from "../../../../../services/WarningSolarService";
import CONS from "../../../../../constants/constant";
import ReactModal from "react-modal";
import AuthService from "../../../../../services/AuthService";
import { useFormik } from 'formik';
import { Link, useLocation } from "react-router-dom";
import { Calendar } from 'primereact/calendar';
import Pagination from "react-js-pagination";
import converter from "../../../../../common/converter";

const $ = window.$;

const WarningPv = ({ customerId, projectId, projectInfo }) => {
    const [fromDate, setFromDate] = useState(new Date());
    const [toDate, setToDate] = useState(new Date());
    const [totalPage, setTotalPage] = useState(1);
    const [viewTypeModal, setViewTypeModal] = useState(null);
    const [settingValue, setSettingValue] = useState(null);

    // total warning state
    const [warnings, setWarnings] = useState({
        chamDat: 0,
        dienApCaoAC: 0,
        dienApCaoDC: 0,
        dienApThapAC: 0,
        dongMoCua: 0,
        hongCauChi: 0,
        nhietDoCao: 0,
        matBoNho: 0,
        matKetNoiAC: 0,
        matKetNoiDC: 0,
        matNguonLuoi: 0,
        tanSoCao: 0,
        tanSoThap: 0
    });

    // Location
    const location = useLocation();

    // active warning state
    const [activeWarning, setActiveWaring] = useState("");

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

    // warning type table
    const [deviceType, setdeviceType] = useState(0);

    const formik = useFormik({
        initialValues: updateWarning,
        enableReinitialize: true,
        onSubmit: async data => {
            let updateWarningData = {
                id: data.warningId,
                warningType: data.warningType,
                deviceId: data.deviceId,
                status: data.status,
                description: data.description,
                username: AuthService.getAuth().username,
                customerId: customerId,
                fromDate: data.fromDate,
                toDate: data.toDate
            }
            let res = await WarningSolarService.updateWarningCache(updateWarningData);
            if (res.status === 200) {
                setIsModalUpdateOpen(false);
                detailWarning(warningType, activeWarning);
            }
        }
    });

    // data pv frame warning by warning type
    const [dataWarning, setDataWarning] = useState({
        page: 1,
        totalPage: 1,
        warningType: null,
        data: []
    });

    const loadWarning = async () => {
        detailWarning();
        let fDate = moment(fromDate).format("YYYY-MM-DD");
        let tDate = moment(toDate).format("YYYY-MM-DD");
        let res = await WarningSolarService.getWarnings(fDate, tDate, customerId, projectId);
        if (res.status === 200) {
            console.log(res.data);
            setWarnings(res.data);
        }
    }

    const detailWarning = async (type, idSelector) => {
        $('#table').hide();
        $('#loading').show();
        setActiveWaring(idSelector);
        setWarningType(type);
        let fDate = moment(fromDate).format("YYYY-MM-DD");
        let tDate = moment(toDate).format("YYYY-MM-DD");
        let res = await WarningSolarService.getWarningsByType(fDate, tDate, customerId, projectId, type, 1);
        if (res.status === 200) {
            setDetailWarnings(res.data.data);
            setTotalPage(res.data.totalPage);
            setPage(1);
            $('#loading').hide();
            $('#table').show();
        }
    }

    const getWarningName = (warningType, deviceType) => {
        let warningName = "";
        let WARNING_TYPE_PV = CONS.WARNING_TYPE_PV
        if (deviceType === 1) {
            switch (warningType) {
                case WARNING_TYPE_PV.NHIET_DO_CAO:
                    warningName = "Nhiệt độ cao";
                    break;
                case WARNING_TYPE_PV.MAT_KET_NOI_AC:
                    warningName = "Mất kết nối AC";
                    break;
                case WARNING_TYPE_PV.MAT_KET_NOI_DC:
                    warningName = "Mất kết nối DC";
                    break;
                case WARNING_TYPE_PV.DIEN_AP_CAO_AC:
                    warningName = "Điện áp cao AC";
                    break;
                case WARNING_TYPE_PV.DIEN_AP_THAP_AC:
                    warningName = "Điện áp thấp AC";
                    break;
                case WARNING_TYPE_PV.DIEN_AP_CAO_DC:
                    warningName = "Điện áp cao DC";
                    break;
                case WARNING_TYPE_PV.TAN_SO_THAP:
                    warningName = "Tần số thấp";
                    break;
                case WARNING_TYPE_PV.TAN_SO_CAO:
                    warningName = "Tần số cao";
                    break;
                case WARNING_TYPE_PV.MAT_NGUON_LUOI:
                    warningName = "Mất kết nối lưới";
                    break;
                case WARNING_TYPE_PV.CHAM_DAT:
                    warningName = "Chạm đất";
                    break;
                case WARNING_TYPE_PV.HONG_CAU_CHI:
                    warningName = "Hỏng cầu chì";
                    break;
                case WARNING_TYPE_PV.DONG_MO_CUA:
                    warningName = "Đóng mở cửa";
                    break;
                case WARNING_TYPE_PV.MEMORY_LOSS:
                    warningName = "Mất bộ nhớ";
                    break;
                default:
                    warningName = "Tất cả cảnh báo.";
                    break;
            }
        } else {
            switch (warningType) {
                case WARNING_TYPE_PV.NHIET_DO_CAO:
                    warningName = "Nhiệt độ cao";
                    break;
                case WARNING_TYPE_PV.CHAM_DAT:
                    warningName = "Chạm đất";
                    break;
                case WARNING_TYPE_PV.HONG_CAU_CHI:
                    warningName = "Hỏng cầu chì";
                    break;
                case WARNING_TYPE_PV.DONG_MO_CUA:
                    warningName = "Đóng mở cửa";
                    break;
                default:
                    warningName = "Tất cả cảnh báo.";
                    break;

            }
        }
        return warningName;
    }

    const handleClickWarning = async (warningType, deviceId, fromDate, toDate) => {
        let res = await WarningSolarService.showDataWarningByDevice(warningType, fromDate, toDate, customerId, deviceId, page);
        if (res.status === 200) {
            console.log(res.data);
            handleSetViewTypeTable(res.data.dataWarning);
            setDataWarning({ ...dataWarning, data: res.data.dataWarning });
            setdeviceType(res.data.deviceType);
            setIsModalOpen(true);

        }
    }

    const handleSetViewTypeTable = (data) => {
        let values = [];

        data.forEach(item => {
            if (item.a && item.a > 0) {
                values.push(item.a);
            }
            if (item.aphaA && item.aphaA > 0) {
                values.push(item.aphaA);
            }
            if (item.aphaB && item.aphaB > 0) {
                values.push(item.aphaB);
            }
            if (item.aphaC && item.aphaC > 0) {
                values.push(item.aphaC);
            }
            if (item.ppvphAB && item.ppvphAB > 0) {
                values.push(item.ppvphAB);
            }
            if (item.ppvphBC && item.ppvphBC > 0) {
                values.push(item.ppvphBC);
            }
            if (item.ppvphCA && item.ppvphCA > 0) {
                values.push(item.ppvphCA);
            }
            if (item.ppvphA && item.ppvphA > 0) {
                values.push(item.ppvphA);
            }
            if (item.ppvphB && item.ppvphB > 0) {
                values.push(item.ppvphB);
            }
            if (item.ppvphC && item.ppvphC > 0) {
                values.push(item.ppvphC);
            }
            if (item.va && item.va > 0) {
                values.push(item.va);
            }
            if (item.var && item.var > 0) {
                values.push(item.var);
            }
            if (item.w && item.w > 0) {
                values.push(item.w);
            }
            if (item.hz && item.hz > 0) {
                values.push(item.hz);
            }
            if (item.wh && item.wh > 0) {
                values.push(item.wh);
            }
            if (item.dca && item.dca > 0) {
                values.push(item.dca);
            }
            if (item.dcv && item.dcv > 0) {
                values.push(item.dcv);
            }
            if (item.dcw && item.dcw > 0) {
                values.push(item.dcw);
            }
            if (item.tmpCab && item.tmpCab > 0) {
                values.push(item.tmpCab);
            }
        });

        let min = Math.min(...values);

        setViewTypeModal(converter.setViewType(values.length > 0 ? min : 0));
    }

    const handleClickUpdate = async (warningType, deviceId, fromDate, toDate) => {
        let res = await WarningSolarService.getDetailWarningCache(warningType, deviceId, fromDate, toDate, customerId);
        if (res.status === 200) {
            setUpdateWarning(res.data);
        }
        setIsModalUpdateOpen(true);
    }

    const handleDownloadData = async (warningType, deviceId, fromDate, toDate) => {
        let res = await WarningSolarService.download(warningType, fromDate, toDate, customerId, deviceId, AuthService.getAuth().username);
        if (res.status !== 200)
            $.alert("Không có dữ liệu.");
    }

    const setNotification = (state) => {
        if (state?.message === "warning_all") {
            detailWarning("ALL", "warning-all");
        }
    }

    const handlePagination = async page => {
        $('#table').hide();
        $('#loading').show();
        setPage(page);
        let fDate = moment(fromDate).format("YYYY-MM-DD");
        let tDate = moment(toDate).format("YYYY-MM-DD");
        let res = await WarningSolarService.getWarningsByType(fDate, tDate, customerId, projectId, warningType, page);
        if (res.status === 200) {
            setDetailWarnings(res.data.data);
            setTotalPage(res.data.totalPage);
            $('#loading').hide();
        $('#table').show();
        }
    }

    useEffect(() => {
        document.title = "Cảnh báo"
        loadWarning();
        if (location.state) {
            setNotification(location.state);
        };
    }, [projectId]);


    return (
        <div className="tab-content">
            <div className="tab-alarm">
                <div className="project-infor" style={{ padding: "0px 10px", display: "block", marginTop: "10px" }}>
                    <span className="project-tree">{projectInfo}</span>
                </div>
                <div className="form-group mt-2 mb-0 ml-2">
                    <div className="input-group float-left" style={{ width: "270px" }}>
                        <div className="input-group-prepend">
                            <span className="input-group-text pickericon">
                                <span className="far fa-calendar"></span>
                            </span>
                        </div>
                        <Calendar
                            id="from-value"
                            className="celendar-picker"
                            dateFormat="yy-mm-dd"
                            value={fromDate}
                            onChange={e => setFromDate(e.value)}
                        />
                    </div>
                    <div className="input-group float-left" style={{ width: "270px" }}>
                        <div className="input-group-prepend">
                            <span className="input-group-text pickericon">
                                <span className="far fa-calendar"></span>
                            </span>
                        </div>
                        <Calendar
                            id="to-value"
                            className="celendar-picker"
                            dateFormat="yy-mm-dd"
                            value={toDate}
                            onChange={e => setToDate(e.value)}
                        />
                    </div>
                    <div>
                        <button type="button" className="btn btn-outline-secondary" onClick={loadWarning}>
                            <i className="fa-solid fa-magnifying-glass"></i>
                        </button>
                    </div>
                </div>

                <div id="alarm-notification" className="mt-4" style={{ height: "calc(100% - 110px)", overflow: "auto", width: "calc(100% - 30px)" }}>
                    <div className={`card warning-card float-left ${activeWarning === "warning-8" ? 'warning-active' : ''}`} id="warning-8" onClick={() => {
                        if (warnings.nhietDoCao <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_PV.NHIET_DO_CAO, "warning-8")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-nhietdotiepxuc.png" alt="Nhiệt độ cao" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.nhietDoCao > 0 ? 'numberWarning' : ''}`}>{warnings.nhietDoCao ? warnings.nhietDoCao : 0}</div>
                                <p>NHIỆT ĐỘ CAO</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-3" ? 'warning-active' : ''}`} id="warning-3" onClick={() => {
                        if (warnings.matKetNoiAC <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_PV.MAT_KET_NOI_AC, "warning-3")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-matketnoi-ac.png" alt="Mất kết nối AC" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.matKetNoiAC > 0 ? 'numberWarning' : ''}`}>{warnings.matKetNoiAC ? warnings.matKetNoiAC : 0}</div>
                                <p>MẤT KẾT NỐI AC</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-4" ? 'warning-active' : ''}`} id="warning-4" onClick={() => {
                        if (warnings.matKetNoiDC <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_PV.MAT_KET_NOI_DC, "warning-4")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-matketnoi-dc.png" alt="Mất kết nối DC" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.matKetNoiDC > 0 ? 'numberWarning' : ''}`}>{warnings.matKetNoiDC ? warnings.matKetNoiDC : 0}</div>
                                <p>MẤT KẾT NỐI DC</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-11" ? 'warning-active' : ''}`} id="warning-11"
                        onClick={() => {
                            if (warnings.dienApCaoAC <= 0) {
                                return
                            }
                            detailWarning(CONS.WARNING_TYPE_PV.DIEN_AP_CAO_AC, "warning-11")
                        }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-dienapcao-ac.png" alt="Điện áp cao" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.dienApCaoAC > 0 ? 'numberWarning' : ''}`}>{warnings.dienApCaoAC ? warnings.dienApCaoAC : 0}</div>
                                <p>ĐIỆN ÁP CAO AC</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-12" ? 'warning-active' : ''}`} id="warning-12" onClick={() => {
                        if (warnings.dienApThapAC <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_PV.DIEN_AP_THAP_AC, "warning-12")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-dienapthap-ac.png" alt="Điện áp thấp" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.dienApThapAC > 0 ? 'numberWarning' : ''}`}>{warnings.dienApThapAC ? warnings.dienApThapAC : 0}</div>
                                <p>ĐIỆN ÁP THẤP AC</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-11" ? 'warning-active' : ''}`} id="warning-11" onClick={() => {
                        if (warnings.tanSoThap <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_PV.TAN_SO_THAP, "warning-11")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-tansothap.png" alt="Tần số thấp" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.tanSoThap > 0 ? 'numberWarning' : ''}`}>{warnings.tanSoThap ? warnings.tanSoThap : 0}</div>
                                <p>TẦN SỐ THẤP</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-9" ? 'warning-active' : ''}`} id="warning-9" onClick={() => {
                        if (warnings.tanSoCao <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_PV.TAN_SO_CAO, "warning-9")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-tansocao.png" alt="Tần số cao" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.tanSoCao > 0 ? 'numberWarning' : ''}`}>{warnings.tanSoCao ? warnings.tanSoCao : 0}</div>
                                <p>TẦN SỐ CAO</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-5" ? 'warning-active' : ''}`} id="warning-5" onClick={() => {
                        if (warnings.matNguonLuoi <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_PV.MAT_NGUON_LUOI, "warning-5")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-matketnoi-luoi.png" alt="Mất kết nối lưới" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.matNguonLuoi > 0 ? 'numberWarning' : ''}`}>{warnings.matNguonLuoi ? warnings.matNguonLuoi : 0}</div>
                                <p>MẤT KẾT NỐI LƯỚI</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-1" ? 'warning-active' : ''}`} id="warning-1" onClick={() => {
                        if (warnings.chamDat <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_PV.CHAM_DAT, "warning-1")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-chamdat.png" alt="Chạm đất" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.chamDat > 0 ? 'numberWarning' : ''}`}>{warnings.chamDat ? warnings.chamDat : 0}</div>
                                <p>CHẠM ĐẤT </p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-13" ? 'warning-active' : ''}`} id="warning-13" onClick={() => {
                        if (warnings.hongCauChi <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_PV.HONG_CAU_CHI, "warning-13")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-cauchi.png" alt="Hỏng cầu chì" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.hongCauChi > 0 ? 'numberWarning' : ''}`}>{warnings.hongCauChi ? warnings.hongCauChi : 0}</div>
                                <p>CẦU CHÌ</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-6" ? 'warning-active' : ''}`} id="warning-6" onClick={() => {
                        if (warnings.dongMoCua <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_PV.DONG_MO_CUA, "warning-6")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-dongmocua.png" alt="Đóng mở cửa" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.dongMoCua > 0 ? 'numberWarning' : ''}`}>{warnings.dongMoCua ? warnings.dongMoCua : 0}</div>
                                <p>ĐÓNG MỞ CỬA</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-2" ? 'warning-active' : ''}`} id="warning-2" onClick={() => {
                        if (warnings.dienApCaoDC <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_PV.DIEN_AP_CAO_DC, "warning-2")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-dienapcao-dc.png" alt="Điện áp cao DC" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.dienApCaoDC > 0 ? 'numberWarning' : ''}`}>{warnings.dienApCaoDC ? warnings.dienApCaoDC : 0}</div>
                                <p>ĐIỆN ÁP CAO DC</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-15" ? 'warning-active' : ''}`} id="warning-15" onClick={() => {
                        if (warnings.matBoNho <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_PV.MEMORY_LOSS, "warning-15")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-matbonho.png" alt="Mất bộ nhớ" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.matBoNho > 0 ? 'numberWarning' : ''}`}>{warnings.matBoNho ? warnings.matBoNho : 0}</div>
                                <p>MẤT BỘ NHỚ</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-31" ? 'warning-active' : ''}`} id="warning-31" onClick={() => {
                        if (warnings.shading <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_GRID.SHADING, "warning-31")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-shading.png" alt="Shading" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.shading > 0 ? 'numberWarning' : ''}`}>{warnings.shading ? warnings.shading : 0}</div>
                                <p>SHADING</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-32" ? 'warning-active' : ''}`} id="warning-32" onClick={() => {
                        if (warnings.hieuSuatThap <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_GRID.HIEU_SUAT_THAP, "warning-32")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-hieusuatthap.png" alt="Hiệu suất thấp" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.hieuSuatThap > 0 ? 'numberWarning' : ''}`}>{warnings.hieuSuatThap ? warnings.hieuSuatThap : 0}</div>
                                <p>HIỆU SUẤT THẤP</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-33" ? 'warning-active' : ''}`} id="warning-33" onClick={() => {
                        if (warnings.loiGateway <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_GRID.LOI_GATEWAY, "warning-33")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-loigateway.png" alt="Lỗi gateway"/></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.loiGateway > 0 ? 'numberWarning' : ''}`}>{warnings.loiGateway ? warnings.loiGateway : 0}</div>
                                <p>LỖI GATEWAY</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-all" ? 'warning-active' : ''}`} id="warning-all" onClick={() => {
                        detailWarning("ALL", "warning-all")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"> </h4>
                        </div>
                        <div className="card-content" style={{ padding: 14 }}>
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.devicesWarning > 0 ? 'numberWarning' : ''}`}>{warnings.devicesWarning ? warnings.devicesWarning : 0}</div>
                                <p>ALL</p>
                            </div>
                        </div>
                    </div>

                    {
                        detailWarnings && detailWarnings.length > 0 ?
                            <>
                                <div id="table">
                                <table className="table">
                                    <thead>
                                        <tr>
                                            <th width="40px">STT</th>
                                            <th width="200px">Tên thành phần</th>
                                            <th width="150px">Loại cảnh báo</th>
                                            <th width="150px">Thời gian bắt đầu</th>
                                            <th width="150px">Thời gian kết thúc</th>
                                            <th width="150px">Số lần xuất hiện</th>
                                            <th width="150px">Vị trí</th>
                                            <th width="100px">Trạng thái</th>
                                            <th width="100px">Người dùng</th>
                                            <th width="100px"><i className="fa-regular fa-hand"></i></th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {
                                            detailWarnings.map((warning, index) => {
                                                return (
                                                    <tr key={index}>
                                                        <td className="text-center">{index + 1}</td>
                                                        <td onClick={() => {
                                                            setWarningType(warning.warningType);
                                                            handleClickWarning(warning.warningType, warning.deviceId, moment(warning.fromDate).format("YYYY-MM-DD HH:mm:ss"), moment(warning.toDate).format("YYYY-MM-DD HH:mm:ss"));
                                                        }}>
                                                            {warning.deviceName}
                                                        </td>
                                                        <td onClick={() => {
                                                            setWarningType(warning.warningType);
                                                            handleClickWarning(warning.warningType, warning.deviceId, moment(warning.fromDate).format("YYYY-MM-DD HH:mm:ss"), moment(warning.toDate).format("YYYY-MM-DD HH:mm:ss"));
                                                        }}>{getWarningName(warning.warningType, warning.deviceType)}</td>
                                                        <td className="text-center" onClick={() => {
                                                            setWarningType(warning.warningType);
                                                            handleClickWarning(warning.warningType, warning.deviceId, moment(warning.fromDate).format("YYYY-MM-DD HH:mm:ss"), moment(warning.toDate).format("YYYY-MM-DD HH:mm:ss"));
                                                        }}>{moment(warning.fromDate).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                        <td className="text-center" onClick={() => {
                                                            setWarningType(warning.warningType);
                                                            handleClickWarning(warning.warningType, warning.deviceId, moment(warning.fromDate).format("YYYY-MM-DD HH:mm:ss"), moment(warning.toDate).format("YYYY-MM-DD HH:mm:ss"));
                                                        }}>{moment(warning.toDate).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                        <td onClick={() => {
                                                            setWarningType(warning.warningType);
                                                            handleClickWarning(warning.warningType, warning.deviceId, moment(warning.fromDate).format("YYYY-MM-DD HH:mm:ss"), moment(warning.toDate).format("YYYY-MM-DD HH:mm:ss"));
                                                        }}>{warning.total}</td>
                                                        <td>
                                                            <Link to={`systemMap/${warning.systemMapId}`}>{warning.systemMapName ? `Layer ` + warning.layer + ` > ` + warning.systemMapName : "-"}</Link>
                                                        </td>
                                                        <td className="text-center" onClick={() => {
                                                            setWarningType(warning.warningType);
                                                            handleClickWarning(warning.warningType, warning.deviceId, moment(warning.fromDate).format("YYYY-MM-DD HH:mm:ss"), moment(warning.toDate).format("YYYY-MM-DD HH:mm:ss"));
                                                        }}>
                                                            {warning.handleFlag === 0 && "Mới"}
                                                            {warning.handleFlag === 1 && "Đã xác nhận"}
                                                            {warning.handleFlag === 2 && "Đang sửa"}
                                                            {warning.handleFlag === 3 && "Đã sửa"}
                                                            {warning.handleFlag === 4 && "Đã hủy"}
                                                        </td>
                                                        <td className={`${warning.staffName ? '' : 'text-center'}`}>
                                                            {warning.staffName ? warning.staffName : "-"}
                                                        </td>
                                                        <td className="text-center">
                                                            <img className="mr-2" src="/resources/image/icon-edit.png" alt="edit warning" style={{ width: 16 }} onClick={() => {
                                                                handleClickUpdate(warning.warningType, warning.deviceId, moment(warning.fromDate).format("YYYY-MM-DD HH:mm:ss"), moment(warning.toDate).format("YYYY-MM-DD HH:mm:ss"))
                                                            }} />
                                                            <img src="/resources/image/icon-download.png" alt="Tải bảng thông số" style={{ width: 16 }} onClick={() =>
                                                                handleDownloadData(warning.warningType, warning.deviceId, moment(warning.fromDate).format("YYYY-MM-DD HH:mm:ss"), moment(warning.toDate).format("YYYY-MM-DD HH:mm:ss"))}
                                                            />
                                                        </td>
                                                    </tr>
                                                )
                                            })
                                        }
                                    </tbody>
                                </table>
                                <div id="pagination">
                                    <Pagination
                                        activePage={page}
                                        totalItemsCount={totalPage}
                                        pageRangeDisplayed={10}
                                        itemsCountPerPage={1}
                                        onChange={e => handlePagination(e)}
                                        activeClass="active"
                                        itemClass="pagelinks"
                                        prevPageText="Truớc"
                                        nextPageText="Sau"
                                        firstPageText="Đầu"
                                        lastPageText="Cuối"
                                    />
                                </div>
                                </div>
                            </>

                            : <></>
                    }
                </div>
            </div>
            <ReactModal
                isOpen={isModalOpen}
                onRequestClose={() => {
                    setIsModalOpen(false)
                }}
                style={{
                    content: {
                        top: '50%',
                        left: '50%',
                        right: 'auto',
                        bottom: 'auto',
                        marginRight: '-50%',
                        transform: 'translate(-50%, -50%)',
                        width: '90%',
                        height: '800px',
                    },
                }}
            >
                <table className="table">
                    {
                        deviceType && deviceType === 1 ?
                            <>
                                <tbody>
                                    <tr>
                                        <th width="50px">TT</th>
                                        <th width="120px">THỜI GIAN</th>
                                        <th width="50px">PHA</th>
                                        <th width="100px">ĐIỆN ÁP [V]</th>
                                        <th >DÒNG ĐIỆN [A]</th>
                                        <th >Tần số</th>
                                        <th >Công suất tác dụng</th>
                                        <th >Ptotal</th>
                                        <th >Công suất toàn phần</th>
                                        <th >Stotal</th>
                                        <th >Công suất phản kháng</th>
                                        <th >Qtotal</th>
                                        <th >PF</th>
                                        <th >Idc</th>
                                        <th >Udc</th>
                                        <th >Pdc</th>
                                        <th >TmpCab</th>
                                        <th >TmpSnk</th>
                                        <th >TmpTrns</th>
                                        <th >TmpOt</th>
                                    </tr>
                                    {
                                        dataWarning.data.map((warning, index) => {
                                            return (
                                                <React.Fragment key={index}>
                                                    <tr className="text-center" >
                                                        <td rowSpan="3">{index + 1}</td>
                                                        <td rowSpan="3">{moment(warning.sentDate).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                        <td >A</td>
                                                        <td >
                                                            {warning.va != null  ? warning.va : "-"}
                                                        </td>
                                                        <td >
                                                            {warning.ia != null  ? warning.ia : "-"}
                                                        </td>
                                                        <td rowSpan="3">{warning.f != null  ? warning.f : "-"}</td>
                                                        <td >
                                                            {warning.pa != null  ? warning.pa : "-"}
                                                        </td>
                                                        <td rowSpan="3">{warning.ptotal != null  ? warning.ptotal : "-"}</td>
                                                        <td >
                                                            {warning.sa != null  ? warning.sa : "-"}
                                                        </td>
                                                        <td rowSpan="3">{warning.stotal != null  ? warning.stotal : "-"}</td>
                                                        <td >
                                                            {warning.qa != null  ? warning.qa : "-"}
                                                        </td>
                                                        <td rowSpan="3">{warning.qtotal != null  ? warning.qtotal : "-"}</td>
                                                        <td rowSpan="3"> {warning.pf != null  ? warning.pf : "-"}</td>
                                                        <td rowSpan="3">{warning.idc != null  ? warning.idc : "-"}</td>
                                                        <td rowSpan="3">{warning.udc != null  ? warning.udc : "-"}</td>
                                                        <td rowSpan="3">{warning.pdc != null  ? warning.pdc : "-"}</td>
                                                        <td rowSpan="3">{warning.tmpCab != null  ? warning.tmpCab : "-"}</td>
                                                        <td rowSpan="3">{warning.tmpSnk != null  ? warning.tmpSnk : "-"}</td>
                                                        <td rowSpan="3">{warning.tmpTrns != null  ? warning.tmpTrns : "-"}</td>
                                                        <td rowSpan="3">{warning.tmpOt != null  ? warning.tmpOt : "-"}</td>
                                                    </tr>
                                                    <tr className="text-center">
                                                        <td >B</td>
                                                        <td >
                                                            {warning.vb != null  ? warning.vb : "-"}
                                                        </td>
                                                        <td >
                                                            {warning.ib != null  ? warning.ib : "-"}
                                                        </td>
                                                        <td >
                                                            {warning.pb != null  ? warning.pb : "-"}
                                                        </td>
                                                        <td >
                                                            {warning.sb != null  ? warning.sb : "-"}
                                                        </td>
                                                        <td >
                                                            {warning.qb != null  ? warning.qb : "-"}
                                                        </td>
                                                    </tr>
                                                    <tr className="text-center">
                                                        <td>C</td>
                                                        <td >
                                                            {warning.vc != null  ? warning.vc : "-"}
                                                        </td>
                                                        <td >
                                                            {warning.ic != null  ? warning.ic : "-"}
                                                        </td>
                                                        <td >
                                                            {warning.pc != null  ? warning.pc : "-"}
                                                        </td>
                                                        <td >
                                                            {warning.sc != null  ? warning.sc : "-"}
                                                        </td>
                                                        <td >
                                                            {warning.qc != null  ? warning.qc : "-"}
                                                        </td>
                                                    </tr>
                                                </React.Fragment>
                                            )
                                        })
                                    }
                                </tbody>
                            </>
                            :
                            <>
                                {
                                    deviceType && deviceType === 3 ?
                                        <>
                                            <tbody>
                                                <tr>
                                                    <th width="50px">TT</th>
                                                    <th width="120px">THỜI GIAN</th>
                                                    <th width="100px">DCAMax</th>
                                                    <th >COMBINER FUSE FAULT</th>
                                                    <th >COMBINER CABINET OPEN</th>
                                                    <th >TEMP</th>
                                                    <th >GROUND FAULT</th>
                                                    <th >IdcCombiner</th>
                                                    <th >DCAh</th>
                                                    <th >VdcCombiner</th>
                                                    <th >T</th>
                                                    <th >PdcCombiner</th>
                                                    <th >PR</th>
                                                </tr>
                                                {
                                                    dataWarning.data.map((warning, index) => {
                                                        return (
                                                            <React.Fragment key={index}>
                                                                <tr className="text-center" >
                                                                    <td >{index + 1}</td>
                                                                    <td >{moment(warning.sentDate).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                    <td >{warning.dcamax != null  ? warning.dcamax : "-"}</td>
                                                                    <td >{warning.combinerFuseFault != null  ? warning.combinerFuseFault : "-"}</td>
                                                                    <td >{warning.combinerCabinetOpen != null  ? warning.combinerCabinetOpen : "-"}</td>
                                                                    <td >{warning.temp != null  ? warning.temp : "-"}</td>
                                                                    <td >{warning.groundfault != null  ? warning.groundfault : "-"}</td>
                                                                    <td >{warning.idcCombiner != null  ? warning.idcCombiner : "-"}</td>
                                                                    <td >{warning.dcah != null  ? warning.dcah : "-"}</td>
                                                                    <td >{warning.vdcCombiner != null  ? warning.vdcCombiner : "-"}</td>
                                                                    <td >{warning.t != null  ? warning.t : "-"}</td>
                                                                    <td >{warning.pdcCombiner != null  ? warning.pdcCombiner : "-"}</td>
                                                                    <td >{warning.pr != null  ? warning.pr : "-"}</td>

                                                                </tr>
                                                            </React.Fragment>
                                                        )
                                                    })
                                                }
                                            </tbody>
                                        </> :
                                        <>
                                            <tbody>
                                                <tr>
                                                    <th width="50px">TT</th>
                                                    <th width="120px">THỜI GIAN</th>
                                                    <th width="100px">COMBINER FUSE FAULT</th>
                                                    <th >COMBINER CABINET OPEN</th>
                                                    <th >TEMP</th>
                                                    <th >GROUND FAULT</th>
                                                    <th >IDCStr</th>
                                                    <th >InDCAhr</th>
                                                    <th >VdcStr</th>
                                                    <th >EpStr</th>
                                                    <th >InDCPR</th>
                                                </tr>
                                                {
                                                    dataWarning.data.map((warning, index) => {
                                                        return (
                                                            <React.Fragment key={index}>
                                                                <tr className="text-center" >
                                                                    <td >{index + 1}</td>
                                                                    <td >{moment(warning.sentDate).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                    <td >{warning.combinerFuseFault != null  ? warning.combinerFuseFault : "-"}</td>
                                                                    <td >{warning.combinerCabinetOpen != null  ? warning.combinerCabinetOpen : "-"}</td>
                                                                    <td >{warning.temp != null  ? warning.temp : "-"}</td>
                                                                    <td >{warning.groundfault != null  ? warning.groundfault : "-"}</td>
                                                                    <td >{warning.idcStr != null  ? warning.idcStr : "-"}</td>
                                                                    <td >{warning.inDCAhr != null  ? warning.inDCAhr : "-"}</td>
                                                                    <td >{warning.vdcStr != null  ? warning.vdcStr : "-"}</td>
                                                                    <td >{warning.epStr != null  ? warning.epStr : "-"}</td>
                                                                    <td >{warning.inDCPR != null  ? warning.inDCPR : "-"}</td>

                                                                </tr>
                                                            </React.Fragment>
                                                        )
                                                    })
                                                }
                                            </tbody>
                                        </>
                                }
                            </>
                    }

                </table>
                <div className="float-right">
                    <button className="button" style={{ backgroundColor: "#0A1A5C", width: "100px" }} onClick={() => setIsModalOpen(false)}>Đóng</button>
                </div>
            </ReactModal>
            <ReactModal
                isOpen={isModalUpdateOpen}
                onRequestClose={() => setIsModalUpdateOpen(false)}
                style={{
                    content: {
                        top: '35%',
                        left: '50%',
                        right: 'auto',
                        bottom: 'auto',
                        marginRight: '-50%',
                        transform: 'translate(-50%, -50%)',
                        width: '50%',
                        height: 'auto',
                    },
                }}>
                {
                    updateWarning !== null &&
                    <>
                        <table className="table">
                            <thead>
                                <tr>
                                    <th width="">Trạng thái</th>
                                    <th width="">Khởi tạo</th>
                                    <th width="">Thiết bị</th>
                                    <th width="">Loại cảnh báo</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr className="text-center">
                                    <td width="">
                                        {updateWarning.handleFlag === 0 && "Mới"}
                                        {updateWarning.handleFlag === 1 && "Đã xác nhận"}
                                        {updateWarning.handleFlag === 2 && "Đang sửa"}
                                        {updateWarning.handleFlag === 3 && "Đã sửa"}
                                        {updateWarning.handleFlag === 4 && "Đã hủy"}
                                    </td>
                                    <td width="">{updateWarning.fromDate}</td>
                                    <td width="">{updateWarning.deviceName}</td>
                                    <td width="">{getWarningName(updateWarning.warningType, updateWarning.deviceType)}</td>
                                </tr>
                            </tbody>
                        </table>

                        <form onSubmit={formik.handleSubmit}>
                            <div id="update-warning-form">
                                <div className="input-group mr-1 mb-1">
                                    <div className="input-group-prepend">
                                        <span className="input-group-text pickericon" style={{ width: "110px" }}>
                                            Mô tả
                                        </span>
                                    </div>
                                    <input className="form-control" name="description" maxLength="100"
                                        defaultValue={updateWarning.description}
                                        onChange={formik.handleChange}
                                    />
                                </div>

                                <div className="input-group mr-1 mb-1" style={{ marginTop: "15px" }}>
                                    <div className="input-group-prepend">
                                        <span className="input-group-text pickericon" style={{ width: "110px" }}>
                                            Người dùng
                                        </span>
                                    </div>
                                    <label className="form-control">{updateWarning.staffName ? updateWarning.staffName : "-"}</label>
                                </div>

                                <div className="pqs-type-search-item input-group float-left mb-1" style={{ marginTop: 10 }}>
                                    <div className="input-group-prepend">
                                        <span className="input-group-text pickericon" style={{ width: "110px" }}>
                                            Trạng thái
                                        </span>
                                    </div>
                                    <div className="form-control p-0">
                                        <div className="dropdown text-left">
                                            <select id="status" name="status" className="custom-select block" style={{ borderRadius: 0 }}
                                                defaultValue={updateWarning.handleFlag}
                                                onChange={e => formik.setFieldValue("status", e.target.value)}
                                            >
                                                <option value="0">Mới</option>
                                                <option value="1">Đã xác nhận</option>
                                                <option value="2">Đang sửa</option>
                                                <option value="3">Đã sửa</option>
                                                <option value="4">Đã hủy</option>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div className="float-right mt-2">
                                <button type="submit" className="button mr-1" style={{ backgroundColor: "#16d39a", width: "100px", borderColor: "#16d39a" }}>Cập nhật</button>
                                <button className="button" style={{ backgroundColor: "#0A1A5C", width: "100px" }} onClick={() => setIsModalUpdateOpen(false)}>Đóng</button>
                            </div>
                        </form>


                    </>
                }

            </ReactModal>
            <div className="loading" id="loading" style={{ marginTop: "30%", marginLeft: "50%"}}>
                <img height="60px" src="/resources/image/loading.gif" alt="loading" />
            </div>
        </div>
    )
};

export default WarningPv;