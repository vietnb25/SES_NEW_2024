import React, { useEffect, useState } from "react";
import moment from "moment/moment";
import WarningGridService from "../../../../../services/WarningGridService";
import overviewGridService from "../../../../../services/OverviewGridService";
import CONS from "../../../../../constants/constant";
import ReactModal from "react-modal";
import AuthService from "../../../../../services/AuthService";
import { useFormik } from 'formik';
import { Link, useLocation } from "react-router-dom";
import { Calendar } from 'primereact/calendar';
import Pagination from "react-js-pagination";
import converter from "../../../../../common/converter";

const $ = window.$;

//time
let date = new Date();

let dateStr =
    ("00" + date.getDate()).slice(-2) + "-" +
    ("00" + (date.getMonth() + 1)).slice(-2) + "-" +
    date.getFullYear() + " " +
    ("00" + date.getHours()).slice(-2) + ":" +
    ("00" + date.getMinutes()).slice(-2)

const WarningGrid = ({ customerId, projectId, projectInfo }) => {
    const [fromDate, setFromDate] = useState(new Date());
    const [toDate, setToDate] = useState(new Date());
    const [totalPage, setTotalPage] = useState(1);
    const [viewTypeModal, setViewTypeModal] = useState(null);
    const [settingValue, setSettingValue] = useState(null);

    const [totalEnergy, setTotalEnergy] = useState(0);

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
            let res = await WarningGridService.updateWarningCache(updateWarningData);
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
        let res = await WarningGridService.getWarnings(fDate, tDate, customerId, projectId);
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
        let res = await WarningGridService.getWarningsByType(fDate, tDate, customerId, projectId, type, 1);
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
        let WARNING_TYPE_GRID = CONS.WARNING_TYPE_GRID
        switch (warningType) {
            case WARNING_TYPE_GRID.DO_AM:
                warningName = "Độ ẩm";
                break;
            case WARNING_TYPE_GRID.PHONG_DIEN:
                warningName = "Phóng điện";
                break;
            case WARNING_TYPE_GRID.TAN_SO_THAP:
                warningName = "Tần số thấp";
                break;
            case WARNING_TYPE_GRID.TAN_SO_CAO:
                warningName = "Tần số cao";
                break;
            case WARNING_TYPE_GRID.SONG_HAI:
                warningName = "Sóng hài";
                break;
            case WARNING_TYPE_GRID.QUA_TAI_TONG:
                warningName = "Quá tải tổng";
                break;
            case WARNING_TYPE_GRID.QUA_TAI_NHANH:
                warningName = "Quá tải nhánh";
                break;
            case WARNING_TYPE_GRID.LECH_PHA_TONG:
                warningName = "Lệch pha tổng";
                break;
            case WARNING_TYPE_GRID.LECH_PHA_NHANH:
                warningName = "Lệch pha nhánh";
                break;
            case WARNING_TYPE_GRID.QUA_TAI_TONG:
                warningName = "Quá tải tổng";
                break;
            case WARNING_TYPE_GRID.FI_TU_RMU:
                warningName = "FI Tủ RMU";
                break;
            case WARNING_TYPE_GRID.KHOANG_TON_THAT:
                warningName = "Khoang tổn thất";
                break;
            case WARNING_TYPE_GRID.DONG_MO_CUA:
                warningName = "Đóng mở cửa";
                break;
            case WARNING_TYPE_GRID.MUC_DAU_THAP:
                warningName = "Mức dầu thấp";
                break;
            case WARNING_TYPE_GRID.NHIET_DO:
                warningName = "Nhiệt độ cao";
                break;
            case WARNING_TYPE_GRID.NHIET_DO_DAU:
                warningName = "Nhiệt độ dầu";
                break;
            case WARNING_TYPE_GRID.MAT_DIEN_TONG:
                warningName = "Mất điện tổng";
                break;
            case WARNING_TYPE_GRID.MAT_DIEN_NHANH:
                warningName = "Mất điện nhánh";
                break;
            case WARNING_TYPE_GRID.ROLE_GAS:
                warningName = "Rơle gas";
                break;
            case WARNING_TYPE_GRID.CHAM_VO:
                warningName = "Chạm vỏ";
                break;
            case WARNING_TYPE_GRID.MUC_DAU_CAO:
                warningName = "Mức dầu cao";
                break;
            case WARNING_TYPE_GRID.CAM_BIEN_HONG_NGOAI:
                warningName = "Cảm biến hồng ngoại";
                break;
            case WARNING_TYPE_GRID.DIEN_AP_CAO:
                warningName = "Điện áp cao";
                break;
            case WARNING_TYPE_GRID.DIEN_AP_THAP:
                warningName = "Điện áp thấp";
                break;
            case WARNING_TYPE_GRID.COS_TONG_THAP:
                warningName = "Cos tổng thấp";
                break;
            case WARNING_TYPE_GRID.COS_NHANH_THAP:
                warningName = "Cos nhánh thấp";
                break;
            case WARNING_TYPE_GRID.AP_SUAT_NOI_BO_MBA:
                warningName = "Áp suất nội bộ MBA";
                break;
            case WARNING_TYPE_GRID.ROLE_NHIET_DO_DAU:
                warningName = "Rơle nhiệt độ dầu";
                break;
            case WARNING_TYPE_GRID.NHIET_DO_CUON_DAY:
                warningName = "Nhiệt độ cuộn dây";
                break;
            case WARNING_TYPE_GRID.KHI_GAS_MBA:
                warningName = "Khí gas MBA";
                break;
            case WARNING_TYPE_GRID.HE_SO_CONG_SUAT_THAP:
                warningName = "Hệ số công suất thấp";
                break;
            default:
                warningName = "Tất cả cảnh báo.";
                break;
        }
        return warningName;
    }

    const handleClickUpdate = async (warningType, deviceId, fromDate, toDate) => {
        let res = await WarningGridService.getDetailWarningCache(warningType, deviceId, fromDate, toDate, customerId);
        if (res.status === 200) {
            setUpdateWarning(res.data);
        }
        setIsModalUpdateOpen(true);
    }

    const handleDownloadData = async (warningType, deviceId, fromDate, toDate) => {
        let res = await WarningGridService.download(warningType, fromDate, toDate, customerId, deviceId, AuthService.getAuth().username);
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
        let res = await WarningGridService.getWarningsByType(fDate, tDate, customerId, projectId, warningType, page);
        if (res.status === 200) {
            setDetailWarnings(res.data.data);
            setTotalPage(res.data.totalPage);
            $('#loading').hide();
        $('#table').show();
        }
    }
    const handleClickWarning = async (warningType, deviceId, fromDate, toDate) => {
        let res = await WarningGridService.showDataWarningByDevice(warningType, fromDate, toDate, projectId, customerId, deviceId, page);
        if (res.status === 200) {
            if ((warningType !== CONS.WARNING_TYPE_GRID.LECH_PHA_NHANH) && (warningType !== CONS.WARNING_TYPE_GRID.NHIET_DO)) {
                setSettingValue(res.data.settingValue);
            } else {
                let values = res.data.settingValue.split(",");
                setSettingValue(values);
            }
            console.log(settingValue);
            handleSetViewTypeTable(res.data.dataWarning);
            setDataWarning({ ...dataWarning, data: res.data.dataWarning });
            setdeviceType(res.data.deviceType);
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
            if (item.pTotal && item.pTotal > 0) {
                values.push(item.pTotal);
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
            if (item.qTotal && item.qTotal > 0) {
                values.push(item.qTotal);
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
            if (item.sTotal && item.sTotal > 0) {
                values.push(item.sTotal);
            }
        });

        let min = Math.min(...values);

        setViewTypeModal(converter.setViewType(values.length > 0 ? min : 0));
    }

    const getTotalEnergy = async () => {
        let res = await overviewGridService.getTotalEnergy(customerId, projectId);
        if (res.status === 200) {
            setTotalEnergy(res.data);
        }
    }

    useEffect(() => {
        document.title = "Cảnh báo"
        loadWarning();
        getTotalEnergy();
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
                    <div className={`card warning-card float-left ${activeWarning === "warning-1" ? 'warning-active' : ''}`} id="warning-1" onClick={() => {
                        if (warnings.doAm <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_GRID.DO_AM, "warning-1")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-doam.png" alt="Độ ẩm" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.doAm > 0 ? 'numberWarning' : ''}`}>{warnings.doAm ? warnings.doAm : 0}</div>
                                <p>ĐỘ ẨM</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-2" ? 'warning-active' : ''}`} id="warning-2" onClick={() => {
                        if (warnings.phongDien <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_GRID.PHONG_DIEN, "warning-2")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-phongdien.png" alt="Phóng điện" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.phongDien > 0 ? 'numberWarning' : ''}`}>{warnings.phongDien ? warnings.phongDien : 0}</div>
                                <p>PHÓNG ĐIỆN</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-3" ? 'warning-active' : ''}`} id="warning-3" onClick={() => {
                        if (warnings.tanSoThap <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_GRID.TAN_SO_THAP, "warning-3")
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
                    <div className={`card warning-card float-left ${activeWarning === "warning-4" ? 'warning-active' : ''}`} id="warning-4"
                        onClick={() => {
                            if (warnings.tanSoCao <= 0) {
                                return
                            }
                            detailWarning(CONS.WARNING_TYPE_GRID.TAN_SO_CAO, "warning-4")
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
                        if (warnings.songHai <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_GRID.SONG_HAI, "warning-5")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-songhai.png" alt="Sóng hài" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.songHai > 0 ? 'numberWarning' : ''}`}>{warnings.songHai ? warnings.songHai : 0}</div>
                                <p>SÓNG HÀI</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-6" ? 'warning-active' : ''}`} id="warning-6" onClick={() => {
                        if (warnings.quaTaiTong <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_GRID.QUA_TAI_TONG, "warning-6")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-quataitong.png" alt="Quá tải tổng" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.quaTaiTong > 0 ? 'numberWarning' : ''}`}>{warnings.quaTaiTong ? warnings.quaTaiTong : 0}</div>
                                <p>QUÁ TẢI TỔNG</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-7" ? 'warning-active' : ''}`} id="warning-7" onClick={() => {
                        if (warnings.quaTaiNhanh <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_GRID.QUA_TAI_NHANH, "warning-7")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-quatainhanh.png" alt="Quá tải nhánh" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.quaTaiNhanh > 0 ? 'numberWarning' : ''}`}>{warnings.quaTaiNhanh ? warnings.quaTaiNhanh : 0}</div>
                                <p>QUÁ TẢI NHÁNH</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-8" ? 'warning-active' : ''}`} id="warning-8" onClick={() => {
                        if (warnings.matNguonLuoi <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_GRID.LECH_PHA_TONG, "warning-8")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-lechphatong.png" alt="Lệch pha tổng" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.matNguonLuoi > 0 ? 'numberWarning' : ''}`}>{warnings.matNguonLuoi ? warnings.matNguonLuoi : 0}</div>
                                <p>LỆCH PHA TỔNG</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-9" ? 'warning-active' : ''}`} id="warning-9" onClick={() => {
                        if (warnings.lechPha <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_GRID.LECH_PHA_NHANH, "warning-9")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-lechphanhanh.png" alt="Lệch pha nhánh" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.lechPha > 0 ? 'numberWarning' : ''}`}>{warnings.lechPha ? warnings.lechPha : 0}</div>
                                <p>LỆCH PHA NHÁNH</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-14" ? 'warning-active' : ''}`} id="warning-14" onClick={() => {
                        if (warnings.nhietDoCao <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_GRID.NHIET_DO, "warning-14")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-nhietdotiepxuc.png" alt="Nhiệt độ" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.nhietDoCao > 0 ? 'numberWarning' : ''}`}>{warnings.nhietDoCao ? warnings.nhietDoCao : 0}</div>
                                <p>NHIỆT ĐỘ CAO</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-15" ? 'warning-active' : ''}`} id="warning-15" onClick={() => {
                        if (warnings.dongMoCua <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_GRID.NHIET_DO_DAU, "warning-15")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-nhietdodau.png" alt="Nhiệt độ dầu" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.nhietDoDau > 0 ? 'numberWarning' : ''}`}>{warnings.nhietDoDau ? warnings.nhietDoDau : 0}</div>
                                <p>NHIỆT ĐỘ DẦU</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-16" ? 'warning-active' : ''}`} id="warning-16" onClick={() => {
                        if (warnings.dienApCaoDC <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_GRID.MAT_DIEN_TONG, "warning-16")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-matdientong.png" alt="Mật điện tổng" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.dienApCaoDC > 0 ? 'numberWarning' : ''}`}>{warnings.dienApCaoDC ? warnings.dienApCaoDC : 0}</div>
                                <p>MẤT ĐIỆN TỔNG</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-17" ? 'warning-active' : ''}`} id="warning-17" onClick={() => {
                        if (warnings.matDienNhanh <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_GRID.MAT_DIEN_NHANH, "warning-17")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-matdiennhanh.png" alt="Mất điện nhánh" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.matDienNhanh > 0 ? 'numberWarning' : ''}`}>{warnings.matDienNhanh ? warnings.matDienNhanh : 0}</div>
                                <p>MẤT ĐIỆN NHÁNH</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-22" ? 'warning-active' : ''}`} id="warning-22" onClick={() => {
                        if (warnings.dienApCao <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_GRID.DIEN_AP_CAO, "warning-22")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-dienapcao.png" alt="Điện áp cao" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.dienApCao > 0 ? 'numberWarning' : ''}`}>{warnings.dienApCao ? warnings.dienApCao : 0}</div>
                                <p>ĐIỆN ÁP CAO</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-23" ? 'warning-active' : ''}`} id="warning-23" onClick={() => {
                        if (warnings.dienApThap <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_GRID.DIEN_AP_THAP, "warning-23")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-dienapthap.png" alt="Điện áp thấp" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.dienApThap > 0 ? 'numberWarning' : ''}`}>{warnings.dienApThap ? warnings.dienApThap : 0}</div>
                                <p>ĐIỆN ÁP THẤP</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-24" ? 'warning-active' : ''}`} id="warning-24" onClick={() => {
                        if (warnings.matBoNho <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_GRID.COS_TONG_THAP, "warning-24")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-hesocongsuatthap.png" alt="COS tổng thấp" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.matBoNho > 0 ? 'numberWarning' : ''}`}>{warnings.matBoNho ? warnings.matBoNho : 0}</div>
                                <p>COSφ TỔNG THẤP</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-25" ? 'warning-active' : ''}`} id="warning-25" onClick={() => {
                        if (warnings.matBoNho <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_GRID.COS_NHANH_THAP, "warning-25")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-hesocongsuatthap.png" alt="COS nhánh thấp" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.matBoNho > 0 ? 'numberWarning' : ''}`}>{warnings.matBoNho ? warnings.matBoNho : 0}</div>
                                <p>COSφ NHÁNH THẤP</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-10" ? 'warning-active' : ''}`} id="warning-10" onClick={() => {
                        if (warnings.matBoNho <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_GRID.FI_TU_RMU, "warning-10")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-matdienrmu.png" alt="FI Tủ RMU" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.matBoNho > 0 ? 'numberWarning' : ''}`}>{warnings.matBoNho ? warnings.matBoNho : 0}</div>
                                <p>FI TỦ RMU</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-11" ? 'warning-active' : ''}`} id="warning-11" onClick={() => {
                        if (warnings.matBoNho <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_GRID.KHOANG_TON_THAT, "warning-11")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-homtonthat.png" alt="Khoang tổn thất" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.matBoNho > 0 ? 'numberWarning' : ''}`}>{warnings.matBoNho ? warnings.matBoNho : 0}</div>
                                <p>KHOANG TỔN THẤT</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-12" ? 'warning-active' : ''}`} id="warning-12" onClick={() => {
                        if (warnings.matBoNho <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_GRID.DONG_MO_CUA, "warning-12")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-dongmocua.png" alt="Đóng mở cửa" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.matBoNho > 0 ? 'numberWarning' : ''}`}>{warnings.matBoNho ? warnings.matBoNho : 0}</div>
                                <p>ĐÓNG MỞ CỬA</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-13" ? 'warning-active' : ''}`} id="warning-13" onClick={() => {
                        if (warnings.matBoNho <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_GRID.MUC_DAU_THAP, "warning-13")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-mucdauthap.png" alt="Mức dầu thấp" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.matBoNho > 0 ? 'numberWarning' : ''}`}>{warnings.matBoNho ? warnings.matBoNho : 0}</div>
                                <p>MỨC DẦU THẤP</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-18" ? 'warning-active' : ''}`} id="warning-18" onClick={() => {
                        if (warnings.matBoNho <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_GRID.ROLE_GAS, "warning-18")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-rolegas.png" alt="Role gas" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.matBoNho > 0 ? 'numberWarning' : ''}`}>{warnings.matBoNho ? warnings.matBoNho : 0}</div>
                                <p>RƠLE GAS</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-19" ? 'warning-active' : ''}`} id="warning-19" onClick={() => {
                        if (warnings.matBoNho <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_GRID.CHAM_VO, "warning-19")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-apluckhi.png" alt="Chạm vỏ" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.matBoNho > 0 ? 'numberWarning' : ''}`}>{warnings.matBoNho ? warnings.matBoNho : 0}</div>
                                <p>CHẠM VỎ</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-20" ? 'warning-active' : ''}`} id="warning-20" onClick={() => {
                        if (warnings.matBoNho <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_GRID.MUC_DAU_CAO, "warning-20")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-mucdaucao.png" alt="Mức dầu cao" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.matBoNho > 0 ? 'numberWarning' : ''}`}>{warnings.matBoNho ? warnings.matBoNho : 0}</div>
                                <p>MỨC DẦU CAO</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-21" ? 'warning-active' : ''}`} id="warning-21" onClick={() => {
                        if (warnings.matBoNho <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_GRID.CAM_BIEN_HONG_NGOAI, "warning-21")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-hongngoai.png" alt="Cảm biến hồng ngoại" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.matBoNho > 0 ? 'numberWarning' : ''}`}>{warnings.matBoNho ? warnings.matBoNho : 0}</div>
                                <p>CẢM BIẾN HỒNG NGOẠI</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-26" ? 'warning-active' : ''}`} id="warning-26" onClick={() => {
                        if (warnings.matBoNho <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_GRID.AP_SUAT_NOI_BO_MBA, "warning-26")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-apsuatnoimba.png" alt="Áp suất nội bộ MBA" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.matBoNho > 0 ? 'numberWarning' : ''}`}>{warnings.matBoNho ? warnings.matBoNho : 0}</div>
                                <p>ÁP SUẤT NỘI BỘ MBA</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-27" ? 'warning-active' : ''}`} id="warning-27" onClick={() => {
                        if (warnings.matBoNho <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_GRID.ROLE_NHIET_DO_DAU, "warning-27")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-nhietdodau.png" alt="Role Nhiệt độ dầu" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.matBoNho > 0 ? 'numberWarning' : ''}`}>{warnings.matBoNho ? warnings.matBoNho : 0}</div>
                                <p>RƠLE NHIỆT ĐỘ DẦU</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-28" ? 'warning-active' : ''}`} id="warning-28" onClick={() => {
                        if (warnings.matBoNho <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_GRID.NHIET_DO_CUON_DAY, "warning-28")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-nhietdocuonday.png" alt="Nhiệt độ cuộn dây" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.matBoNho > 0 ? 'numberWarning' : ''}`}>{warnings.matBoNho ? warnings.matBoNho : 0}</div>
                                <p>NHIỆT ĐỘ CUỘN DÂY</p>
                            </div>
                        </div>
                    </div>
                    <div className={`card warning-card float-left ${activeWarning === "warning-29" ? 'warning-active' : ''}`} id="warning-29" onClick={() => {
                        if (warnings.matBoNho <= 0) {
                            return
                        }
                        detailWarning(CONS.WARNING_TYPE_GRID.KHI_GAS_MBA, "warning-29")
                    }}>
                        <div className="card-header">
                            <h4 className="card-title"><img src="/resources/image/icon-khigatrongmba.png" alt="Khí gas mba" /></h4>
                        </div>
                        <div className="card-content">
                            <div className="card-body">
                                <div className={`numberCircle ${warnings.matBoNho > 0 ? 'numberWarning' : ''}`}>{warnings.matBoNho ? warnings.matBoNho : 0}</div>
                                <p>KHÍ GAS MBA</p>
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
                        warningType !== 103 ?
                            <tbody>
                                <tr>
                                    <th rowSpan="2" width="50px">TT</th>
                                    <th rowSpan="2" width="120px">THỜI GIAN</th>
                                    <th rowSpan="2" width="50px">PHA</th>
                                    <th rowSpan="2" width="100px">ĐIỆN ÁP [V]</th>
                                    <th rowSpan="2">DÒNG ĐIỆN [A]</th>
                                    <th colSpan="6">SAW</th>
                                    <th rowSpan="2">P </th>
                                    <th rowSpan="2">Q</th>
                                    <th rowSpan="2">S </th>
                                    <th rowSpan="2">PF</th>
                                    <th rowSpan="2">THD U [%]</th>
                                    <th rowSpan="2">THD I [%]</th>
                                    <th rowSpan="2">F</th>
                                    <th rowSpan="2">H</th>
                                    <th rowSpan="2">T</th>
                                    <th rowSpan="2">Indicator</th>
                                    <th rowSpan="2">ĐIỆN NĂNG</th>
                                </tr>
                                <tr>
                                    <th>ID1</th>
                                    <th>ID2</th>
                                    <th>ID3</th>
                                    <th>ID4</th>
                                    <th>ID5</th>
                                    <th>ID6</th>
                                </tr>
                                {
                                    dataWarning.data.map((warning, index) => {
                                        return (
                                            <React.Fragment key={index}>
                                                <tr className="text-center"
                                                    style={{
                                                        backgroundColor: (
                                                            (warningType === CONS.WARNING_TYPE_GRID.SONG_HAI && (warning.thdVan > settingValue || warning.thdVbn > settingValue || warning.thdVcn > settingValue))
                                                        ) ? "#FFA87D" : ""
                                                    }}
                                                >
                                                    <td rowSpan="3">{index + 1}</td>
                                                    <td rowSpan="3">{moment(warning.sentDate).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                    <td >A</td>
                                                    <td style={{
                                                        backgroundColor: ((warningType === CONS.WARNING_TYPE_GRID.DIEN_AP_CAO && warning.uan > settingValue)
                                                            || (warningType === CONS.WARNING_TYPE_GRID.DIEN_AP_THAP && warning.uan < settingValue)) ? "#FFA87D" : ""
                                                    }}>
                                                        {warning.uan != null && warning.uan >= 0 && warning.uan <= 50000 ? warning.uan : "-"}
                                                    </td>
                                                    <td style={{
                                                        backgroundColor:
                                                            ((warningType === CONS.WARNING_TYPE_GRID.QUA_TAI_TONG && ((warning.ia) > settingValue * warning.imccb))
                                                                || (warningType === CONS.WARNING_TYPE_GRID.LECH_PHA_NHANH && (((warning.ia + warning.ib + warning.ic) / 3) / (warning.power * 1.44) > settingValue[0]
                                                                    && (Math.max(warning.ia, warning.ib, warning.ic) - Math.min(warning.ia, warning.ib, warning.ic)) / Math.min(warning.ia, warning.ib, warning.ic) > settingValue[1])) ? "#FFA87D" : "")
                                                    }}>
                                                        {warning.ia != null && warning.ia >= 0 && warning.ia <= 10000 ? warning.ia : "-"}
                                                    </td>
                                                    <td rowSpan="3" style={{
                                                        backgroundColor:
                                                            (warningType === CONS.WARNING_TYPE_GRID.NHIET_DO && ((warning.sawId1 > settingValue[0])) ? "#FFA87D" : "")
                                                    }}>
                                                        {warning.sawId1 != null && warning.sawId1 >= -50 && warning.sawId1 <= 180 ? warning.sawId1 : "-"}</td>
                                                    <td rowSpan="3" style={{
                                                        backgroundColor:
                                                            (warningType === CONS.WARNING_TYPE_GRID.NHIET_DO && ((warning.sawId2 > settingValue[0])) ? "#FFA87D" : "")
                                                    }}>
                                                        {warning.sawId2 != null && warning.sawId2 >= -50 && warning.sawId2 <= 180 ? warning.sawId2 : "-"}</td>
                                                    <td rowSpan="3" style={{
                                                        backgroundColor:
                                                            (warningType === CONS.WARNING_TYPE_GRID.NHIET_DO && ((warning.sawId3 > settingValue[0])) ? "#FFA87D" : "")
                                                    }}>
                                                        {warning.sawId3 != null && warning.sawId3 >= -50 && warning.sawId3 <= 180 ? warning.sawId3 : "-"}</td>
                                                    <td rowSpan="3" style={{
                                                        backgroundColor:
                                                            (warningType === CONS.WARNING_TYPE_GRID.NHIET_DO && ((warning.sawId4 > settingValue[0])) ? "#FFA87D" : "")
                                                    }}>
                                                        {warning.sawId4 != null && warning.sawId4 >= -50 && warning.sawId4 <= 180 ? warning.sawId4 : "-"}</td>
                                                    <td rowSpan="3" style={{
                                                        backgroundColor:
                                                            (warningType === CONS.WARNING_TYPE_GRID.NHIET_DO && ((warning.sawId5 > settingValue[0])) ? "#FFA87D" : "")
                                                    }}>
                                                        {warning.sawId5 != null && warning.sawId5 >= -50 && warning.sawId5 <= 180 ? warning.sawId5 : "-"}</td>
                                                    <td rowSpan="3" style={{
                                                        backgroundColor:
                                                            (warningType === CONS.WARNING_TYPE_GRID.NHIET_DO && ((warning.sawId6 > settingValue[0])) ? "#FFA87D" : "")
                                                    }}>
                                                        {warning.sawId6 != null && warning.sawId6 >= -50 && warning.sawId6 <= 180 ? warning.sawId6 : "-"}</td>
                                                    <td>{warning.pa != null && warning.pa >= -2000000 && warning.pa <= 2000000 ? warning.pa : "-"}</td>
                                                    <td>{warning.qa != null && warning.qa >= -2000000 && warning.qa <= 2000000 ? warning.qa : "-"}</td>
                                                    <td>{warning.sa != null && warning.sa >= 0 && warning.sa <= 2000000 ? warning.sa : "-"}</td>
                                                    <td>{warning.pfa != null && warning.pfa >= -1 && warning.pfa <= 1 ? warning.pfa : "-"}</td>
                                                    <td>{warning.thdVab != null && warning.thdVab >= 0 && warning.thdVab <= 100 ? warning.thdVab : "-"}</td>
                                                    <td>{warning.thdIa != null && warning.thdIa >= 0 && warning.thdIa <= 100 ? warning.thdIa : "-"}</td>
                                                    <td rowSpan="3" style={{
                                                        backgroundColor: ((warningType === CONS.WARNING_TYPE_GRID.TAN_SO_THAP && warning.f < settingValue)
                                                            || (warningType === CONS.WARNING_TYPE_GRID.TAN_SO_CAO && warning.f > settingValue))
                                                            ? "#FFA87D" : ""
                                                    }}>
                                                        {warning.f != null && warning.f >= 45 && warning.f <= 65 ? warning.f : "-"}
                                                    </td>

                                                    <td rowSpan="3" style={{
                                                        backgroundColor: ((warningType === CONS.WARNING_TYPE_GRID.DO_AM && warning.h > settingValue))
                                                            ? "#FFA87D" : ""
                                                    }}>
                                                        {warning.h != null && warning.h >= 0 && warning.h <= 100 ? warning.h : "-"}
                                                    </td>
                                                    <td rowSpan="3" style={{
                                                        backgroundColor: ((warningType === CONS.WARNING_TYPE_GRID.NHIET_DO && warning.t > settingValue[1]))
                                                            ? "#FFA87D" : ""
                                                    }}>{warning.t != null && warning.t >= -50 && warning.t <= 180 ? warning.t : "-"}</td>
                                                    <td rowSpan="3" style={{
                                                        backgroundColor: ((warningType === CONS.WARNING_TYPE_GRID.PHONG_DIEN && warning.indicator > settingValue))
                                                            ? "#FFA87D" : ""
                                                    }}>  {warning.indicator != null && warning.indicator >= 0 && warning.indicator <= 3 ? warning.indicator : "-"}</td>
                                                    <td rowSpan="3">{warning.ep != null && warning.ep >= 0? warning.ep : "-"}</td>
                                                </tr>
                                                <tr className="text-center" style={{
                                                    backgroundColor: (
                                                        (warningType === CONS.WARNING_TYPE_GRID.SONG_HAI && (warning.thdVan > settingValue || warning.thdVbn > settingValue || warning.thdVcn > settingValue))
                                                    ) ? "#FFA87D" : ""
                                                }}>
                                                    <td >B</td>
                                                    <td style={{
                                                        backgroundColor: ((warningType === CONS.WARNING_TYPE_GRID.DIEN_AP_CAO && warning.ubn > settingValue)
                                                            || (warningType === CONS.WARNING_TYPE_GRID.DIEN_AP_THAP && warning.ubn < settingValue)) ? "#FFA87D" : ""
                                                    }}>
                                                        {warning.ubn != null && warning.ubn >= 0 && warning.ubn <= 50000 ? warning.ubn : "-"}
                                                    </td>
                                                    <td style={{
                                                        backgroundColor:
                                                            ((warningType === CONS.WARNING_TYPE_GRID.QUA_TAI_TONG && ((warning.ib) > settingValue * warning.imccb))
                                                                || (warningType === CONS.WARNING_TYPE_GRID.LECH_PHA_NHANH && (((warning.ia + warning.ib + warning.ic) / 3) / (warning.power * 1.44) > settingValue[0]
                                                                    && (Math.max(warning.ia, warning.ib, warning.ic) - Math.min(warning.ia, warning.ib, warning.ic)) / Math.min(warning.ia, warning.ib, warning.ic) > settingValue[1])) ? "#FFA87D" : "")
                                                    }}>
                                                        {warning.ib != null && warning.ib >= 0 && warning.ib <= 10000 ? warning.ib : "-"}
                                                    </td>
                                                    <td>{warning.pb != null && warning.pb >= -2000000 && warning.pb <= 2000000 ? warning.pb : "-"}</td>
                                                    <td>{warning.qb != null && warning.qb >= -2000000 && warning.qb <= 2000000 ? warning.qb : "-"}</td>
                                                    <td>{warning.sb != null && warning.sb >= 0 && warning.sb <= 2000000 ? warning.sb : "-"}</td>
                                                    <td>{warning.pfb != null && warning.pfb >= -1 && warning.pfb <= 1 ? warning.pfb : "-"}</td>
                                                    <td>{warning.thdVbc != null && warning.thdVbc >= 0 && warning.thdVbc <= 100 ? warning.thdVbc : "-"}</td>
                                                    <td>{warning.thdIb != null && warning.thdIb >= 0 && warning.thdIb <= 100 ? warning.thdIb : "-"}</td>
                                                </tr>
                                                <tr className="text-center" style={{
                                                    backgroundColor: (
                                                        (warningType === CONS.WARNING_TYPE_GRID.SONG_HAI && (warning.thdVan > settingValue || warning.thdVbn > settingValue || warning.thdVcn > settingValue))
                                                    ) ? "#FFA87D" : ""
                                                }}>
                                                    <td>C</td>
                                                    <td style={{
                                                        backgroundColor: ((warningType === CONS.WARNING_TYPE_GRID.DIEN_AP_CAO && warning.ucn > settingValue)
                                                            || (warningType === CONS.WARNING_TYPE_GRID.DIEN_AP_THAP && warning.ucn < settingValue)) ? "#FFA87D" : ""
                                                    }}>
                                                        {warning.ucn != null && warning.ucn >= 0 && warning.ucn <= 50000 ? warning.ucn : "-"}
                                                    </td>
                                                    <td style={{
                                                        backgroundColor:
                                                            ((warningType === CONS.WARNING_TYPE_GRID.QUA_TAI_TONG && ((warning.ic) > settingValue * warning.imccb))
                                                                || (warningType === CONS.WARNING_TYPE_GRID.LECH_PHA_NHANH && (((warning.ia + warning.ib + warning.ic) / 3) / (warning.power * 1.44) > settingValue[0]
                                                                    && (Math.max(warning.ia, warning.ib, warning.ic) - Math.min(warning.ia, warning.ib, warning.ic)) / Math.min(warning.ia, warning.ib, warning.ic) > settingValue[1])) ? "#FFA87D" : "")
                                                    }}>
                                                        {warning.ic != null && warning.ic >= 0 && warning.ic <= 10000 ? warning.ic : "-"}
                                                    </td>
                                                    <td>{warning.pc != null && warning.pc >= -2000000 && warning.pc <= 2000000 ? warning.pc : "-"}</td>
                                                    <td>{warning.qc != null && warning.qb >= -2000000 && warning.qb <= 2000000 ? warning.qb : "-"}</td>
                                                    <td>{warning.sc != null && warning.sc >= 0 && warning.sc <= 2000000 ? warning.sc : "-"}</td>
                                                    <td>{warning.pfc != null && warning.pfc >= -1 && warning.pfc <= 1 ? warning.pfc : "-"}</td>
                                                    <td>{warning.thdVca != null && warning.thdVca >= 0 && warning.thdVca <= 100 ? warning.thdVca : "-"}</td>
                                                    <td>{warning.thdIc != null && warning.thdIc >= 0 && warning.thdIc <= 100 ? warning.thdIc : "-"}</td>
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
                                    dataWarning.data.map((warning, index) => {
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
}

export default WarningGrid
