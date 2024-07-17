import React, { useEffect, useState } from "react";
import moment from "moment/moment";
import WarningLoadService from "../../../../../services/WarningLoadService";
import CONS from "../../../../../constants/constant";
import ReactModal from "react-modal";
import AuthService from "../../../../../services/AuthService";
import { useFormik } from 'formik';
import { Link, useLocation } from "react-router-dom";
import { Calendar } from 'primereact/calendar';
import Pagination from "react-js-pagination";
import converter from "../../../../../common/converter";
import authService from "../../../../../services/AuthService";

const $ = window.$;

const WarningLoad = ({ customerId, projectId, projectInfo }) => {
    const [fromDate, setFromDate] = useState(new Date());
    const [toDate, setToDate] = useState(new Date());
    const [totalPage, setTotalPage] = useState(1);
    const [viewTypeModal, setViewTypeModal] = useState(null);
    const [settingValue, setSettingValue] = useState(null);

    // total warning state
    const [warnings, setWarnings] = useState({
        canhBao1: 0,
        canhBao2: 0,
        heSoCongSuatThap: 0,
        lechApPha: 0,
        lechPha: 0,
        matNguon: 0,
        nguocPha: 0,
        nguongApCao: 0,
        nguongApThap: 0,
        nguongMeoSongN: 0,
        nguongTongMeoSongHai: 0,
        nhietDoTiepXuc: 0,
        quaDongTiepDia: 0,
        quaDongTrungTinh: 0,
        quaTai: 0,
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
            let res = await WarningLoadService.updateWarningCache(updateWarningData, customerId);
            if (res.status === 200) {
                setIsModalUpdateOpen(false);
                detailWarning(warningType, activeWarning);
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

    const loadWarning = async () => {
        detailWarning();
        let fDate = moment(fromDate).format("YYYY-MM-DD");
        let tDate = moment(toDate).format("YYYY-MM-DD");
        let res = await WarningLoadService.getWarnings(fDate, tDate, projectId, customerId);
        if (res.status === 200) {
            setWarnings(res.data);
        }
    }
    const detailWarning = async (type, idSelector) => {
        setActiveWaring(idSelector);
        setWarningType(type);
        let fDate = moment(fromDate).format("YYYY-MM-DD");
        let tDate = moment(toDate).format("YYYY-MM-DD");
        let res = await WarningLoadService.getWarningsByType(fDate, tDate, projectId, customerId, type, 1);
        if (res.status === 200) {
            setDetailWarnings(res.data.data);
            setTotalPage(res.data.totalPage);
            setPage(1);
        }
    }
    console.log("detailWarnings:", detailWarnings);

    const getWarningName = warningType => {
        let warningName = "";
        let WARNING_TYPE = CONS.WARNING_TYPE
        switch (warningType) {
            case WARNING_TYPE.NGUONG_AP_CAO:
                warningName = "Ngưỡng áp cao";
                break;
            case WARNING_TYPE.NGUONG_AP_THAP:
                warningName = "Ngưỡng áp thấp";
                break;
            case WARNING_TYPE.NHIET_DO_TIEP_XUC:
                warningName = "Nhiệt độ tiếp xúc";
                break;
            case WARNING_TYPE.LECH_PHA:
                warningName = "Lệch pha";
                break;
            case WARNING_TYPE.NGUOC_PHA:
                warningName = "Ngược pha";
                break;
            case WARNING_TYPE.LECH_AP_PHA:
                warningName = "Lệch áp pha";
                break;
            case WARNING_TYPE.COS_THAP_TONG:
                warningName = "Hệ số công suất thấp";
                break;
            case WARNING_TYPE.QUA_TAI:
                warningName = "Quá tải";
                break;
            case WARNING_TYPE.TAN_SO_THAP:
                warningName = "Tần số thấp";
                break;
            case WARNING_TYPE.TAN_SO_CAO:
                warningName = "Tần số cao";
                break;
            case WARNING_TYPE.MAT_NGUON_PHA:
                warningName = "Mất nguồn pha";
                break;
            case WARNING_TYPE.NGUONG_HAI_BAC_N:
                warningName = "Ngưỡng méo sóng hài bậc N";
                break;
            case WARNING_TYPE.NGUONG_TONG_HAI:
                warningName = "Ngưỡng tổng méo sóng hài";
                break;
            case WARNING_TYPE.DONG_TRUNG_TINH:
                warningName = "Quá dòng trung tính";
                break;
            case WARNING_TYPE.DONG_TIEP_DIA:
                warningName = "Quá dòng tiếp địa";
                break;
            case WARNING_TYPE.CANH_BAO_1:
                warningName = "Đầu vào số cảnh báo 1";
                break;
            case WARNING_TYPE.CANH_BAO_2:
                warningName = "Đầu vào số cảnh báo 2";
                break;
            default:
                warningName = "Tất cả cảnh báo.";
                break;
        }
        return warningName;
    }

    const handleClickWarning = async (warningType, deviceId, fromDate, toDate) => {
        let res = await WarningLoadService.showDataWarningByDevice(warningType, fromDate, toDate, deviceId, customerId, page);
        if (res.status === 200) {
            if (warningType !== CONS.WARNING_TYPE.LECH_PHA) {
                setSettingValue(res.data.settingValue);
            } else {
                let values = res.data.settingValue.split(",");
                setSettingValue(values);
            }
            handleSetViewTypeTable(res.data.dataWarning);
            setDataLoadFrameWarning({ ...dataLoadFrameWarning, data: res.data.dataWarning })
            setIsModalOpen(true);

        }
    }

    const handleSetViewTypeTable = (data) => {
        let values = [];

        data.forEach(item => {
            if (item.ep && item.ep > 0) {
                values.push(item.ep);
            }
            if (item.pa && item.pa > 0) {
                values.push(item.pa);
            }
            if (item.pb && item.pb > 0) {
                values.push(item.pb);
            }
            if (item.pc && item.pc > 0) {
                values.push(item.pc);
            }
            if (item.ptotal && item.ptotal > 0) {
                values.push(item.ptotal);
            }
            if (item.qa && item.qa > 0) {
                values.push(item.qa);
            }
            if (item.qb && item.qb > 0) {
                values.push(item.qb);
            }
            if (item.qc && item.qc > 0) {
                values.push(item.qc);
            }
            if (item.qtotal && item.qtotal > 0) {
                values.push(item.qtotal);
            }
            if (item.sa && item.sa > 0) {
                values.push(item.sa);
            }
            if (item.sb && item.sb > 0) {
                values.push(item.sb);
            }
            if (item.sc && item.sc > 0) {
                values.push(item.sc);
            }
            if (item.stotal && item.stotal > 0) {
                values.push(item.stotal);
            }
        });

        let min = Math.min(...values);

        setViewTypeModal(converter.setViewType(values.length > 0 ? min : 0));
    }

    const handleClickUpdate = async (warningId) => {
        let res = await WarningLoadService.getDetailWarningCache(warningId, customerId);
        if (res.status === 200) {
            setUpdateWarning(res.data);
        }
        setIsModalUpdateOpen(true);
    }

    const handleDownloadData = async (warningType, deviceId, fromDate, toDate) => {
        let res = await WarningLoadService.download(warningType, fromDate, toDate, deviceId, customerId, authService.getUserName());
        if (res.status !== 200)
            $.alert("Không có dữ liệu.");
    }

    const setNotification = (state) => {
        if (state?.message === "warning_all") {
            detailWarning("ALL", "warning-all");
        }
    }

    const handlePagination = async page => {
        setPage(page);
        let fDate = moment(fromDate).format("YYYY-MM-DD");
        let tDate = moment(toDate).format("YYYY-MM-DD");
        let res = await WarningLoadService.getWarningsByType(fDate, tDate, projectId, customerId, warningType, page);
        if (res.status === 200) {
            setDetailWarnings(res.data.data);
            setTotalPage(res.data.totalPage);
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
                    <div className={`card warning-card float-left ${activeWarning === "warning-101" ? 'warning-active' : ''}`} id="warning-101"
                        onClick={() => {
                            if (warnings.nguongApCao <= 0) {
                                return
                            }
                            detailWarning(CONS.WARNING_TYPE.NGUONG_AP_CAO, "warning-101")
                        }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-dienapcao.png" alt="Quá tải tổng" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.nguongApCao > 0 ? 'numberWarning' : ''}`}>{warnings.nguongApCao ? warnings.nguongApCao : 0}</div>
                                <p>ĐIỆN ÁP CAO</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-102" ? 'warning-active' : ''}`} id="warning-102" onClick={() => {
                        if (warnings.nguongApThap <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE.NGUONG_AP_THAP, "warning-102")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-dienapthap.png" alt="Quá tải nhánh" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.nguongApThap > 0 ? 'numberWarning' : ''}`}>{warnings.nguongApThap ? warnings.nguongApThap : 0}</div>
                                <p>ĐIỆN ÁP THẤP</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-103" ? 'warning-active' : ''}`} id="warning-103" onClick={() => {
                        if (warnings.nhietDoTiepXuc <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE.NHIET_DO_TIEP_XUC, "warning-103")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-nhietdotiepxuc.png" alt="Lệch pha tổng" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.nhietDoTiepXuc > 0 ? 'numberWarning' : ''}`}>{warnings.nhietDoTiepXuc ? warnings.nhietDoTiepXuc : 0}</div>
                                <p>NHIỆT ĐỘ TIẾP XÚC</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-109" ? 'warning-active' : ''}`} id="warning-109" onClick={() => {
                        if (warnings.lechApPha <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE.LECH_PHA, "warning-109")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-lechphatong.png" alt="Lệch pha nhánh" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.lechApPha > 0 ? 'numberWarning' : ''}`}>{warnings.lechApPha ? warnings.lechApPha : 0}</div>
                                <p>LỆCH PHA</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-105" ? 'warning-active' : ''}`} id="warning-105" onClick={() => {
                        if (warnings.quaTai <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE.QUA_TAI, "warning-105")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-quataitong.png" alt="Nhiệt độ dầu cao" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.quaTai > 0 ? 'numberWarning' : ''}`}>{warnings.quaTai ? warnings.quaTai : 0}</div>
                                <p>QUÁ TẢI</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-106" ? 'warning-active' : ''}`} id="warning-106" onClick={() => {
                        if (warnings.tanSoThap <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE.TAN_SO_THAP, "warning-106")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-tansothap.png" alt="Mất nguồn tổng" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.tanSoThap > 0 ? 'numberWarning' : ''}`}>{warnings.tanSoThap ? warnings.tanSoThap : 0}</div>
                                <p>TẦN SỐ THẤP</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-107" ? 'warning-active' : ''}`} id="warning-107" onClick={() => {
                        if (warnings.tanSoCao <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE.TAN_SO_CAO, "warning-107")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-tansocao.png" alt="Mất nguồn nhánh" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.tanSoCao > 0 ? 'numberWarning' : ''}`}>{warnings.tanSoCao ? warnings.tanSoCao : 0}</div>
                                <p>TẦN SỐ CAO</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-108" ? 'warning-active' : ''}`} id="warning-108" onClick={() => {
                        if (warnings.matNguon <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE.MAT_NGUON_PHA, "warning-108")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-matdientong.png" alt="Điện áp cao" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.matNguon > 0 ? 'numberWarning' : ''}`}>{warnings.matNguon ? warnings.matNguon : 0}</div>
                                <p>MẤT NGUỒN</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-112" ? 'warning-active' : ''}`} id="warning-112" onClick={() => {
                        if (warnings.nguongTongMeoSongHai <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE.NGUONG_TONG_HAI, "warning-112")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-songhai.png" alt="Mất điện RMU" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.nguongTongMeoSongHai > 0 ? 'numberWarning' : ''}`}>{warnings.nguongTongMeoSongHai ? warnings.nguongTongMeoSongHai : 0}</div>
                                <p>SÓNG HÀI</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-113" ? 'warning-active' : ''}`} id="warning-113" onClick={() => {
                        if (warnings.quaDongTrungTinh <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE.DONG_TRUNG_TINH, "warning-113")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-dongtrungtinh.png" alt="Hòm tổn thất" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.quaDongTrungTinh > 0 ? 'numberWarning' : ''}`}>{warnings.quaDongTrungTinh ? warnings.quaDongTrungTinh : 0}</div>
                                <p>DÒNG TRUNG TÍNH</p>
                            </div>
                        </div>
                    </div>

                    <div className={`card warning-card float-left ${activeWarning === "warning-120" ? 'warning-active' : ''}`} id="warning-120" onClick={() => {
                        if (warnings.dongMoCua <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE.DONG_TRUNG_TINH, "warning-120")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-dongmocua.png" alt="Hòm tổn thất" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.dongMoCua > 0 ? 'numberWarning' : ''}`}>{warnings.dongMoCua ? warnings.dongMoCua : 0}</div>
                                <p>ĐÓNG MỞ CỬA</p>
                            </div>
                        </div>
                    </div>

                    <div className={`card warning-card float-left ${activeWarning === "warning-104" ? 'warning-active' : ''}`} id="warning-104" onClick={() => {
                        if (warnings.dongMoCua <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE.COS_THAP_TONG, "warning-104")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-hesocongsuatthap.png" alt="Hòm tổn thất" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.heSoCongSuatThap > 0 ? 'numberWarning' : ''}`}>{warnings.heSoCongSuatThap ? warnings.heSoCongSuatThap : 0}</div>
                                <p>HỆ SỐ CÔNG SUẤT THẤP</p>
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
                                <table className="table">
                                    <thead>
                                        <tr>
                                            <th width="40px">TT</th>
                                            <th width="">Thiết bị</th>
                                            <th width="200px">Loại cảnh báo</th>
                                            <th width="150px">Bắt đầu</th>
                                            <th width="150px">Mới nhất</th>
                                            <th width="150px">Vị trí</th>
                                            <th width="100px">Trạng thái</th>
                                            <th width="200px">Người dùng</th>
                                            <th width="70px"><i className="fa-regular fa-hand"></i></th>
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
                                                        }}>{getWarningName(warning.warningType)}</td>
                                                        <td className="text-center" onClick={() => {
                                                            setWarningType(warning.warningType);
                                                            handleClickWarning(warning.warningType, warning.deviceId, moment(warning.fromDate).format("YYYY-MM-DD HH:mm:ss"), moment(warning.toDate).format("YYYY-MM-DD HH:mm:ss"));
                                                        }}>{moment(warning.fromDate).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                        <td className="text-center" onClick={() => {
                                                            setWarningType(warning.warningType);
                                                            handleClickWarning(warning.warningType, warning.deviceId, moment(warning.fromDate).format("YYYY-MM-DD HH:mm:ss"), moment(warning.toDate).format("YYYY-MM-DD HH:mm:ss"));
                                                        }}>{moment(warning.toDate).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                        <td>
                                                            <Link to={`systemMap/${warning.systemMapId}?deviceId=${warning.deviceId}`}>{warning.systemMapName ? `Layer ` + warning.layer + ` > ` + warning.systemMapName : "-"}</Link>
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
                                                                handleClickUpdate(warning.warningId)
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
                        warningType !== 103 ?
                            <tbody>
                                <tr>
                                    <th width="50px">TT</th>
                                    <th width="120px">THỜI GIAN</th>
                                    <th width="50px">PHA</th>
                                    <th width="100px">ĐIỆN ÁP [V]</th>
                                    <th >DÒNG ĐIỆN [A]</th>
                                    <th >%</th>
                                    <th >P {converter.convertLabelElectricPower(viewTypeModal, "W")}</th>
                                    <th >Q {converter.convertLabelElectricPower(viewTypeModal, "VAr")}</th>
                                    <th >S {converter.convertLabelElectricPower(viewTypeModal, "VA")}</th>
                                    <th >PF</th>
                                    <th >THD U [%]</th>
                                    <th >THD I [%]</th>
                                    <th >F</th>
                                    <th >VU [%]</th>
                                    <th >IU [%]</th>
                                    <th >ĐIỆN NĂNG {converter.convertLabelElectricPower(viewTypeModal, "Wh")}</th>
                                </tr>
                                {
                                    dataLoadFrameWarning.data.map((warning, index) => {
                                        return (
                                            <React.Fragment key={index}>
                                                <tr className="text-center"
                                                    style={{
                                                        backgroundColor: (
                                                            (warningType === CONS.WARNING_TYPE.NGUONG_TONG_HAI && (warning.thdVan > settingValue || warning.thdVbn > settingValue || warning.thdVcn > settingValue))
                                                        ) ? "#FFA87D" : ""
                                                    }}
                                                >
                                                    <td rowSpan="3">{index + 1}</td>
                                                    <td rowSpan="3">{moment(warning.sentDate).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                    <td >A</td>
                                                    <td style={{
                                                        backgroundColor: ((warningType === CONS.WARNING_TYPE.NGUONG_AP_CAO && warning.uan > settingValue)
                                                            || (warningType === CONS.WARNING_TYPE.NGUONG_AP_THAP && warning.uan < settingValue)
                                                            || (warningType === CONS.WARNING_TYPE.MAT_NGUON_PHA && warning.uan < settingValue)) ? "#FFA87D" : ""
                                                    }}>
                                                        {warning.uan}
                                                    </td>
                                                    <td style={{
                                                        backgroundColor:
                                                            ((warningType === CONS.WARNING_TYPE.QUA_TAI && (warning.ia >= settingValue * (warning.imccb ? warning.imccb : 0)))
                                                                || (warningType === CONS.WARNING_TYPE.LECH_PHA && (((warning.ia + warning.ib + warning.ic) / 3) / (warning.power * 1.44) > settingValue[0]
                                                                    && (Math.max(warning.ia + warning.ib + warning.ic) - Math.min(warning.ia + warning.ib + warning.ic)) / Math.min(warning.ia + warning.ib + warning.ic) > settingValue[1])) ? "#FFA87D" : "")
                                                    }}>
                                                        {warning.ia}
                                                    </td>
                                                    <td>-</td>
                                                    <td>{converter.convertElectricPower(viewTypeModal, warning.pa)}</td>
                                                    <td>{converter.convertElectricPower(viewTypeModal, warning.qa)}</td>
                                                    <td>{converter.convertElectricPower(viewTypeModal, warning.sa)}</td>
                                                    <td>{warning.pfa}</td>
                                                    <td>{warning.thdVab}</td>
                                                    <td>{warning.thdIa}</td>
                                                    <td rowSpan="3" style={{
                                                        backgroundColor: ((warningType === CONS.WARNING_TYPE.TAN_SO_CAO && warning.f > settingValue)
                                                            || (warningType === CONS.WARNING_TYPE.TAN_SO_THAP && warning.f < settingValue))
                                                            ? "#FFA87D" : ""
                                                    }}>
                                                        {warning.f}
                                                    </td>
                                                    <td>-</td>
                                                    <td>-</td>
                                                    <td rowSpan="3">{converter.convertElectricPower(viewTypeModal, warning.ep)}</td>
                                                </tr>
                                                <tr className="text-center" style={{
                                                    backgroundColor: (
                                                        (warningType === CONS.WARNING_TYPE.NGUONG_TONG_HAI && (warning.thdVan > settingValue || warning.thdVbn > settingValue || warning.thdVcn > settingValue))
                                                    ) ? "#FFA87D" : ""
                                                }}>
                                                    <td >B</td>
                                                    <td style={{
                                                        backgroundColor: ((warningType === CONS.WARNING_TYPE.NGUONG_AP_CAO && warning.ubn > settingValue)
                                                            || (warningType === CONS.WARNING_TYPE.NGUONG_AP_THAP && warning.ubn < settingValue))
                                                            || (warningType === CONS.WARNING_TYPE.MAT_NGUON_PHA && warning.ubn < settingValue) ? "#FFA87D" : ""
                                                    }}>
                                                        {warning.ubn}
                                                    </td>
                                                    <td style={{
                                                        backgroundColor:
                                                            ((warningType === CONS.WARNING_TYPE.QUA_TAI && (warning.ib >= settingValue * (warning.imccb ? warning.imccb : 0)))
                                                                || (warningType === CONS.WARNING_TYPE.LECH_PHA && (((warning.ia + warning.ib + warning.ic) / 3) / (warning.power * 1.44) > settingValue[0]
                                                                    && (Math.max(warning.ia + warning.ib + warning.ic) - Math.min(warning.ia + warning.ib + warning.ic)) / Math.min(warning.ia + warning.ib + warning.ic) > settingValue[1])) ? "#FFA87D" : "")
                                                    }}>
                                                        {warning.ib}
                                                    </td>
                                                    <td>-</td>
                                                    <td>{converter.convertElectricPower(viewTypeModal, warning.pb)}</td>
                                                    <td>{converter.convertElectricPower(viewTypeModal, warning.qb)}</td>
                                                    <td>{converter.convertElectricPower(viewTypeModal, warning.sb)}</td>
                                                    <td>{warning.pfb}</td>
                                                    <td>{warning.thdVbc}</td>
                                                    <td>{warning.thdIb}</td>
                                                    <td>-</td>
                                                    <td>-</td>
                                                </tr>
                                                <tr className="text-center" style={{
                                                    backgroundColor: (
                                                        (warningType === CONS.WARNING_TYPE.NGUONG_TONG_HAI && (warning.thdVan > settingValue || warning.thdVbn > settingValue || warning.thdVcn > settingValue))
                                                    ) ? "#FFA87D" : ""
                                                }}>
                                                    <td>C</td>
                                                    <td style={{
                                                        backgroundColor: ((warningType === CONS.WARNING_TYPE.NGUONG_AP_CAO && warning.ucn > settingValue)
                                                            || (warningType === CONS.WARNING_TYPE.NGUONG_AP_THAP && warning.ucn < settingValue))
                                                            || (warningType === CONS.WARNING_TYPE.MAT_NGUON_PHA && warning.ucn < settingValue) ? "#FFA87D" : ""
                                                    }}>
                                                        {warning.ucn}
                                                    </td>
                                                    <td style={{
                                                        backgroundColor:
                                                            ((warningType === CONS.WARNING_TYPE.QUA_TAI && (warning.ic >= settingValue * (warning.imccb ? warning.imccb : 0)))
                                                                || (warningType === CONS.WARNING_TYPE.LECH_PHA && (((warning.ia + warning.ib + warning.ic) / 3) / (warning.power * 1.44) > settingValue[0]
                                                                    && (Math.max(warning.ia + warning.ib + warning.ic) - Math.min(warning.ia + warning.ib + warning.ic)) / Math.min(warning.ia + warning.ib + warning.ic) > settingValue[1])) ? "#FFA87D" : "")
                                                    }}>
                                                        {warning.ic}
                                                    </td>
                                                    <td>-</td>
                                                    <td>{converter.convertElectricPower(viewTypeModal, warning.pc)}</td>
                                                    <td>{converter.convertElectricPower(viewTypeModal, warning.qc)}</td>
                                                    <td>{converter.convertElectricPower(viewTypeModal, warning.sc)}</td>
                                                    <td>{warning.pfc}</td>
                                                    <td>{warning.thdVca}</td>
                                                    <td>{warning.thdIc}</td>
                                                    <td>-</td>
                                                    <td>-</td>
                                                </tr>
                                            </React.Fragment>
                                        )
                                    })
                                }
                            </tbody> :
                            <tbody>
                                <tr>
                                    <th width="50px">TT</th>
                                    <th width="150px">Thời gian</th>
                                    <th width="50px">Pha</th>
                                    <th width="">Vị trí 1</th>
                                    <th width="">Vị trí 2</th>
                                    <th width="">Vị trí 3</th>
                                </tr>
                                {
                                    dataLoadFrameWarning.data.map((warning, index) => {
                                        return <React.Fragment key={index}>
                                            <tr >
                                                <td rowSpan={3} className="text-center">{index + 1}</td>
                                                <td rowSpan={3} className="text-center">{moment(warning.sentDate).format(CONS.DATE_FORMAT)}</td>
                                                <td className="text-center">A</td>
                                                <td className="text-right"
                                                    style={{ backgroundColor: ((warningType === CONS.WARNING_TYPE.NHIET_DO_TIEP_XUC && warning.t1 > settingValue)) ? "#FFA87D" : "" }}>
                                                    {warning.t1 === null ? "-" : warning.t1}
                                                </td>
                                                <td className="text-center">-</td>
                                                <td className="text-center">-</td>
                                            </tr>
                                            <tr>
                                                <td className="text-center">B</td>
                                                <td className="text-right"
                                                    style={{ backgroundColor: ((warningType === CONS.WARNING_TYPE.NHIET_DO_TIEP_XUC && warning.t2 > settingValue)) ? "#FFA87D" : "" }}>
                                                    {warning.t2 === null ? "-" : warning.t2}
                                                </td>
                                                <td className="text-center">-</td>
                                                <td className="text-center">-</td>
                                            </tr>
                                            <tr>
                                                <td className="text-center">C</td>
                                                <td className="text-right"
                                                    style={{ backgroundColor: ((warningType === CONS.WARNING_TYPE.NHIET_DO_TIEP_XUC && warning.t3 > settingValue)) ? "#FFA87D" : "" }}>
                                                    {warning.t3 === null ? "-" : warning.t3}
                                                </td>
                                                <td className="text-center">-</td>
                                                <td className="text-center">-</td>
                                            </tr>
                                        </React.Fragment>
                                    })
                                }
                            </tbody>
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
                                    <td width="">{getWarningName(updateWarning.warningType)}</td>
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
        </div>
    )
}

export default WarningLoad;