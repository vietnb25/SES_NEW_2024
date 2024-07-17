import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import moment from "moment";
import ReactModal from "react-modal";
import { useFormik } from "formik";
import AuthService from "../../../../../../services/AuthService";
import CONS from './../../../../../../constants/constant';
import { Calendar } from 'primereact/calendar';
import converter from "../../../../../../common/converter";
import WarningGridService from "../../../../../../services/WarningGridService";
import { count } from "d3";
import Pagination from "react-js-pagination";

const $ = window.$;

const OperationWarning = () => {

    const param = useParams();
    const [page, setPage] = useState(1);

    const [fromDate, setFromDate] = useState(new Date());
    const [toDate, setToDate] = useState(new Date());
    const [viewTypeModal, setViewTypeModal] = useState(null);
    const [settingValue, setSettingValue] = useState(null);
    const [statusDownload, setStatusDownload] = useState(false);

    const [warningType, setWarningType] = useState({
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

    const [isModalOpen, setIsModalOpen] = useState(false);

    const [updateWarning, setUpdateWarning] = useState(null);

    const [isModalUpdateOpen, setIsModalUpdateOpen] = useState(false);
    // detail warning
    const [detailWarnings, setDetailWarnings] = useState([]);

    const [totalPage, setTotalPage] = useState(1);
    // data load frame warning by warning type
    const [dataWarning, setDataWarning] = useState({
        page: 1,
        totalPage: 1,
        warningType: null,
        data: []
    });

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
                customerId: param.customerId,
                fromDate: data.fromDate,
                toDate: data.toDate
            }
            let res = await WarningGridService.updateWarningCache(updateWarningData);
            if (res.status === 200) {
                setIsModalUpdateOpen(false);
                getOperationWarning();
            }
        }
    });

    const getOperationWarning = async () => {
        $('.table-hide-m').hide();
        $('#loading').show();
        let fDate = moment(fromDate).format("YYYY-MM-DD");
        let tDate = moment(toDate).format("YYYY-MM-DD");
        let warningType = document.getElementById("warning-type").value;

        let res = await WarningGridService.getWarningsOperationInformationByType(fDate, tDate, param.customerId, param.deviceId, warningType, page);
        if (res.status === 200 && res.data !== '') {
            $('#loading').hide();
            $('.table-hide-m').show();
            setDetailWarnings(res.data.data);
            setTotalPage(res.data.totalPage);
            setPage(1);
        } else {
            $('#loading').hide();
            setDetailWarnings([]);
        }
    }

    const downloadWarning = async () => {
        setStatusDownload(true);
        let fDate = moment(fromDate).format("YYYY-MM-DD");
        let tDate = moment(toDate).format("YYYY-MM-DD");
        let warningType = document.getElementById("warning-type").value;
        await WarningGridService.download(warningType, fDate, tDate, param.customerId, param.deviceId, AuthService.getAuth().username);
        setStatusDownload(false);
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

    const handleClickWarning = async (warningType, deviceId, fromDate, toDate) => {
        let res = await WarningGridService.showDataWarningByDevice(warningType, fromDate, toDate, param.projectId, param.customerId, deviceId, page);
        if (res.status === 200) {
            if ((warningType !== CONS.WARNING_TYPE_GRID.LECH_PHA_NHANH) && (warningType !== CONS.WARNING_TYPE_GRID.NHIET_DO)) {
                setSettingValue(res.data.settingValue);
            } else {
                let values = res.data.settingValue.split(",");
                setSettingValue(values);
            }
            handleSetViewTypeTable(res.data.dataWarning);
            setDataWarning({ ...dataWarning, data: res.data.dataWarning });
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

    const handleClickUpdate = async (warningType, deviceId, fromDate, toDate) => {
        let res = await WarningGridService.getDetailWarningCache(warningType, deviceId, fromDate, toDate, param.customerId);
        if (res.status === 200) {
            setUpdateWarning(res.data);
        }
        setIsModalUpdateOpen(true);
    }

    const handleDownloadData = async (warningType, deviceId, fromDate, toDate) => {
        let res = await WarningGridService.download(warningType, fromDate, toDate, param.customerId, deviceId, AuthService.getAuth().username);
        if (res.status !== 200)
            $.alert("Không có dữ liệu.");
    }

    const handlePagination = async page => {
        setPage(page);
        let fDate = moment(fromDate).format("YYYY-MM-DD");
        let tDate = moment(toDate).format("YYYY-MM-DD");
        let warningType = document.getElementById("warning-type").value;
        let res = await WarningGridService.getWarningsOperationInformationByType(fDate, tDate, param.customerId, param.deviceId, warningType, page);
        if (res.status === 200) {
            setDetailWarnings(res.data.data);
            setTotalPage(res.data.totalPage);
        }
    }

    useEffect(() => {
        document.title = "Thông tin thiết bị - Cảnh báo";

    }, [param.customerId, param.deviceId]);

    return (
        <>
            <div id="main-search" className="ml-1 mb-3" style={{ height: '32px' }}>
                <div className="form-group mt-2 mb-0 ml-2" >
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
                </div>
                <div className="input-group float-left mr-1" style={{ width: '200px' }}>
                    <div className="input-group-prepend">
                        <span className="input-group-text pickericon">
                            <span className="fas fa-exclamation-triangle" />
                        </span>
                    </div>
                    <select id="warning-type" name="dataType" defaultValue={CONS.WARNING_TYPE_GRID.DO_AM} className="custom-select block custom-select-sm" onChange={() => getOperationWarning()}>
                        <option value={CONS.WARNING_TYPE_GRID.DO_AM}>Độ ẩm</option>
                        <option value={CONS.WARNING_TYPE_GRID.PHONG_DIEN}>Phóng điện</option>
                        <option value={CONS.WARNING_TYPE_GRID.SONG_HAI}>Sóng hài</option>
                        <option value={CONS.WARNING_TYPE_GRID.QUA_TAI_TONG}>Quả tải tổng</option>
                        <option value={CONS.WARNING_TYPE_GRID.QUA_TAI_NHANH}>Quá tải nhánh</option>
                        <option value={CONS.WARNING_TYPE_GRID.LECH_PHA_TONG}>Lệch pha tổng</option>
                        <option value={CONS.WARNING_TYPE_GRID.LECH_PHA_NHANH}>Lệch pha nhánh</option>
                        <option value={CONS.WARNING_TYPE_GRID.FI_TU_RMU}>FI Tủ RMU</option>
                        <option value={CONS.WARNING_TYPE_GRID.KHOANG_TON_THAT}>Khoang tổn thất</option>
                        <option value={CONS.WARNING_TYPE_GRID.DONG_MO_CUA}>Đóng mở cửa</option>
                        <option value={CONS.WARNING_TYPE_GRID.MUC_DAU_THAP}>Mức dầu thấp</option>
                        <option value={CONS.WARNING_TYPE_GRID.NHIET_DO}>Nhiệt độ cao</option>
                        <option value={CONS.WARNING_TYPE_GRID.NHIET_DO_DAU}>Nhiệt độ dầu</option>
                        <option value={CONS.WARNING_TYPE_GRID.MAT_DIEN_TONG}>Mất điện tổng</option>
                        <option value={CONS.WARNING_TYPE_GRID.MAT_DIEN_NHANH}>Mất điện nhánh</option>
                        <option value={CONS.WARNING_TYPE_GRID.ROLE_GAS}>Role gas</option>
                        <option value={CONS.WARNING_TYPE_GRID.CHAM_VO}>Chạm Vỏ</option>
                        <option value={CONS.WARNING_TYPE_GRID.MUC_DAU_CAO}>Mức dầu cao</option>
                        <option value={CONS.WARNING_TYPE_GRID.CAM_BIEN_HONG_NGOAI}>Cảm biến hồng ngoại</option>
                        <option value={CONS.WARNING_TYPE_GRID.DIEN_AP_CAO}>Điện áp cao</option>
                        <option value={CONS.WARNING_TYPE_GRID.DIEN_AP_THAP}>Điện áp thấp</option>
                        <option value={CONS.WARNING_TYPE_GRID.COS_TONG_THAP}>Cos tổng thấp</option>
                        <option value={CONS.WARNING_TYPE_GRID.COS_NHANH_THAP}>Cos nhánh thấp</option>
                        <option value={CONS.WARNING_TYPE_GRID.AP_SUAT_NOI_BO_MBA}>Áp suất nội bộ MBA</option>
                        <option value={CONS.WARNING_TYPE_GRID.ROLE_NHIET_DO_DAU}>Role nhiệt độ dầu</option>
                        <option value={CONS.WARNING_TYPE_GRID.NHIET_DO_CUON_DAY}>Nhiệt độ cuộn dây</option>
                        <option value={CONS.WARNING_TYPE_GRID.KHI_GAS_MBA}>Khí gas MBA</option>
                        <option value={CONS.WARNING_TYPE_GRID.HE_SO_CONG_SUAT_THAP}>Hệ số công suất thấp</option>
                    </select>
                </div>
                <div className="search-buttons float-left">
                    <button type="button" className="btn btn-outline-secondary btn-sm mr-1" onClick={() => getOperationWarning()}>
                        <i className="fa-solid fa-search" />
                    </button>
                    <button type="button" className="btn btn-outline-secondary btn-sm" style={{ overflow: "hidden" }} onClick={() => downloadWarning()}>
                        {
                            !statusDownload &&
                            <i className="fa-solid fa-download" />
                        }
                        {
                            statusDownload &&
                            <i className="fa-solid fa-down-long icon-aniamation-download" />
                        }
                    </button>
                </div>
            </div>
            <div className="text-center loading" id="loading" style={{ display: "none" }}>
                <img height="60px" className="mt-0.5" src="/resources/image/loading.gif" alt="icon-loading" />
            </div>

            {parseInt(detailWarnings.length) > 0 ?
                <>
                    <table className="table tbl-overview table-hide-m mt-3">
                        <thead>
                            <tr>
                                <th width="40px">ID</th>
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
                                                {warning.systemMapName ? `Layer ` + warning.layer + ` > ` + warning.systemMapName : "-"}
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
                </>
                :
                <table className="table tbl-overview ml-0 mr-0" id="no-data" style={{ width: "-webkit-fill-available" }}>
                    <tbody>
                        <tr className="w-100">
                            <td height={30} className="text-center w-100" style={{ border: "none", background: "#D5D6D1" }}> Không có dữ liệu</td>
                        </tr>
                    </tbody>
                </table>
            }

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
                                    <th rowSpan="2">%</th>
                                    <th colSpan="6">SAW</th>
                                    <th rowSpan="2">P {converter.convertLabelElectricPower(viewTypeModal, "W")}</th>
                                    <th rowSpan="2">Q {converter.convertLabelElectricPower(viewTypeModal, "VAr")}</th>
                                    <th rowSpan="2">S {converter.convertLabelElectricPower(viewTypeModal, "VA")}</th>
                                    <th rowSpan="2">PF</th>
                                    <th rowSpan="2">THD U [%]</th>
                                    <th rowSpan="2">THD I [%]</th>
                                    <th rowSpan="2">F</th>
                                    <th rowSpan="2">H</th>
                                    <th rowSpan="2">T</th>
                                    <th rowSpan="2">Indicator</th>
                                    <th rowSpan="2">ĐIỆN NĂNG {converter.convertLabelElectricPower(viewTypeModal, "Wh")}</th>
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
                                                        {warning.uan}
                                                    </td>
                                                    <td style={{
                                                        backgroundColor:
                                                            ((warningType === CONS.WARNING_TYPE_GRID.QUA_TAI_TONG && ((warning.ia + warning.ib + warning.ic) / 3) / (warning.power * 1.44) > settingValue)
                                                                || (warningType === CONS.WARNING_TYPE_GRID.LECH_PHA_NHANH && (((warning.ia + warning.ib + warning.ic) / 3) / (warning.power * 1.44) > settingValue[0]
                                                                    && (Math.max(warning.ia, warning.ib, warning.ic) - Math.min(warning.ia, warning.ib, warning.ic)) / Math.min(warning.ia, warning.ib, warning.ic) > settingValue[1])) ? "#FFA87D" : "")
                                                    }}>
                                                        {warning.ia}
                                                    </td>
                                                    <td>-</td>
                                                    <td rowSpan="3" style={{
                                                        backgroundColor:
                                                            (warningType === CONS.WARNING_TYPE_GRID.NHIET_DO && ((warning.sawId1 > settingValue[0])) ? "#FFA87D" : "")
                                                    }}>
                                                        {warning.sawId1}</td>
                                                    <td rowSpan="3" style={{
                                                        backgroundColor:
                                                            (warningType === CONS.WARNING_TYPE_GRID.NHIET_DO && ((warning.sawId2 > settingValue[0])) ? "#FFA87D" : "")
                                                    }}>
                                                        {warning.sawId2}</td>
                                                    <td rowSpan="3" style={{
                                                        backgroundColor:
                                                            (warningType === CONS.WARNING_TYPE_GRID.NHIET_DO && ((warning.sawId3 > settingValue[0])) ? "#FFA87D" : "")
                                                    }}>
                                                        {warning.sawId3}</td>
                                                    <td rowSpan="3" style={{
                                                        backgroundColor:
                                                            (warningType === CONS.WARNING_TYPE_GRID.NHIET_DO && ((warning.sawId4 > settingValue[0])) ? "#FFA87D" : "")
                                                    }}>
                                                        {warning.sawId4}</td>
                                                    <td rowSpan="3" style={{
                                                        backgroundColor:
                                                            (warningType === CONS.WARNING_TYPE_GRID.NHIET_DO && ((warning.sawId5 > settingValue[0])) ? "#FFA87D" : "")
                                                    }}>
                                                        {warning.sawId5}</td>
                                                    <td rowSpan="3" style={{
                                                        backgroundColor:
                                                            (warningType === CONS.WARNING_TYPE_GRID.NHIET_DO && ((warning.sawId6 > settingValue[0])) ? "#FFA87D" : "")
                                                    }}>
                                                        {warning.sawId6}</td>
                                                    <td>{converter.convertElectricPower(viewTypeModal, warning.pa)}</td>
                                                    <td>{converter.convertElectricPower(viewTypeModal, warning.qa)}</td>
                                                    <td>{converter.convertElectricPower(viewTypeModal, warning.sa)}</td>
                                                    <td>{warning.pfa}</td>
                                                    <td>{warning.thdVab}</td>
                                                    <td>{warning.thdIa}</td>
                                                    <td rowSpan="3" style={{
                                                        backgroundColor: ((warningType === CONS.WARNING_TYPE_GRID.TAN_SO_THAP && warning.f < settingValue)
                                                            || (warningType === CONS.WARNING_TYPE_GRID.TAN_SO_CAO && warning.f > settingValue))
                                                            ? "#FFA87D" : ""
                                                    }}>
                                                        {warning.f}
                                                    </td>

                                                    <td rowSpan="3" style={{
                                                        backgroundColor: ((warningType === CONS.WARNING_TYPE_GRID.DO_AM && warning.h > settingValue))
                                                            ? "#FFA87D" : ""
                                                    }}>{warning.h}</td>
                                                    <td rowSpan="3" style={{
                                                        backgroundColor: ((warningType === CONS.WARNING_TYPE_GRID.NHIET_DO && warning.t > settingValue[1]))
                                                            ? "#FFA87D" : ""
                                                    }}>{warning.t}</td>
                                                    <td rowSpan="3" style={{
                                                        backgroundColor: ((warningType === CONS.WARNING_TYPE_GRID.PHONG_DIEN && warning.indicator > settingValue))
                                                            ? "#FFA87D" : ""
                                                    }}>  {warning.indicator}</td>
                                                    <td rowSpan="3">{converter.convertElectricPower(viewTypeModal, warning.ep)}</td>
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
                                                        {warning.ubn}
                                                    </td>
                                                    <td style={{
                                                        backgroundColor:
                                                            ((warningType === CONS.WARNING_TYPE_GRID.QUA_TAI_TONG && ((warning.ia + warning.ib + warning.ic) / 3) / (warning.power * 1.44) > settingValue)
                                                                || (warningType === CONS.WARNING_TYPE_GRID.LECH_PHA_NHANH && (((warning.ia + warning.ib + warning.ic) / 3) / (warning.power * 1.44) > settingValue[0]
                                                                    && (Math.max(warning.ia, warning.ib, warning.ic) - Math.min(warning.ia, warning.ib, warning.ic)) / Math.min(warning.ia, warning.ib, warning.ic) > settingValue[1])) ? "#FFA87D" : "")
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
                                                        {warning.ucn}
                                                    </td>
                                                    <td style={{
                                                        backgroundColor:
                                                            ((warningType === CONS.WARNING_TYPE_GRID.QUA_TAI_TONG && ((warning.ia + warning.ib + warning.ic) / 3) / (warning.power * 1.44) > settingValue)
                                                                || (warningType === CONS.WARNING_TYPE_GRID.LECH_PHA_NHANH && (((warning.ia + warning.ib + warning.ic) / 3) / (warning.power * 1.44) > settingValue[0]
                                                                    && (Math.max(warning.ia, warning.ib, warning.ic) - Math.min(warning.ia, warning.ib, warning.ic)) / Math.min(warning.ia, warning.ib, warning.ic) > settingValue[1])) ? "#FFA87D" : "")
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
        </>

    )
}

export default OperationWarning;