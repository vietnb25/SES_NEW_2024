import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import moment from "moment";
import ReactModal from "react-modal";
import { useFormik } from "formik";
import AuthService from "../../../../../../services/AuthService";
import OperationInformationService from "../../../../../../services/OperationInformationService";
import CONS from './../../../../../../constants/constant';
import { Calendar } from 'primereact/calendar';
import converter from "../../../../../../common/converter";

const $ = window.$;

const OperationWarning = () => {

    const param = useParams();
    const [page, setPage] = useState(1);
    // total warning state
    const [warnings, setWarnings] = useState({});
    const [fromDate, setFromDate] = useState(new Date());
    const [toDate, setToDate] = useState(new Date());
    const [viewTypeModal, setViewTypeModal] = useState(null);
    const [settingValue, setSettingValue] = useState(null);
    let userName = AuthService.getUserName();
    const [statusDownload, setStatusDownload] = useState(false);

    const [warningType, setWarningType] = useState({
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

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [updateWarning, setUpdateWarning] = useState(null);
    const [isModalUpdateOpen, setIsModalUpdateOpen] = useState(false);

    // data load frame warning by warning type
    const [dataLoadFrameWarning, setDataLoadFrameWarning] = useState({
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
                status: data.status,
                description: data.description,
                username: AuthService.getAuth().username
            }
            let res = await OperationInformationService.updateOperatingWarningCache(param.customerId, updateWarningData);
            if (res.status === 200) {
                setIsModalUpdateOpen(false);
                getOperationWarning();
            }
        }
    });

    const getOperationWarning = async () => {
        $('#loading').show();
        let fDate = moment(fromDate).format("YYYY-MM-DD");
        let tDate = moment(toDate).format("YYYY-MM-DD");
        let warningType = document.getElementById("warning-type").value;
        let res = await OperationInformationService.getWarningOperation(param.customerId, param.deviceId, warningType, fDate, tDate);
        if (res.status === 200 && parseInt(res.data.length) > 0) {
            $('#loading').hide();
            $('#no-data').hide();
            setWarnings(res.data);
        } else {
            $('#loading').hide();
            setWarnings([]);
            $('#no-data').show();
        }
    }

    const downloadWarning = async () => {
        setStatusDownload(true);
        let fDate = moment(fromDate).format("YYYY-MM-DD");
        let tDate = moment(toDate).format("YYYY-MM-DD");
        let warningType = document.getElementById("warning-type").value;
        await OperationInformationService.downloadWarning(param.customerId, param.deviceId, warningType, fDate, tDate, userName);
        setStatusDownload(false);
    }

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

    const handleClickWarning = async (warningType, fromDate, toDate) => {
        let res = await OperationInformationService.showDataWarning(param.customerId, warningType, fromDate, toDate, param.deviceId, page);
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
        let res = await OperationInformationService.getDetailOperatingWarningCache(param.customerId, warningId);
        if (res.status === 200) {
            setUpdateWarning(res.data);
        }
        setIsModalUpdateOpen(true);
    }

    const handleDownloadData = async (warningType, fromDate, toDate) => {
        let res = await OperationInformationService.download(param.customerId, warningType, fromDate, toDate, param.deviceId, userName);
        if (res.status !== 200)
            $.alert("Không có dữ liệu.");
    }

    useEffect(() => {
        document.title = "Thông tin thiết bị - Cảnh báo";
    }, [param.customerId, param.deviceId])

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
                    <select id="warning-type" name="dataType" defaultValue={CONS.WARNING_TYPE.QUA_TAI} className="custom-select block custom-select-sm" onChange={() => getOperationWarning()}>
                        <option value={CONS.WARNING_TYPE.QUA_TAI}>Quá tải</option>
                        <option value={CONS.WARNING_TYPE.LECH_PHA}>Lệch pha</option>
                        <option value={CONS.WARNING_TYPE.NHIET_DO_TIEP_XUC}>Nhiệt độ tiếp xúc</option>
                        <option value={CONS.WARNING_TYPE.MAT_NGUON_PHA}>Mất nguồn</option>
                        <option value={CONS.WARNING_TYPE.NGUONG_AP_CAO}>Điện áp cao</option>
                        <option value={CONS.WARNING_TYPE.NGUONG_AP_THAP}>Điện áp thấp</option>
                        <option value={CONS.WARNING_TYPE.TAN_SO_CAO}>Tần số cao</option>
                        <option value={CONS.WARNING_TYPE.TAN_SO_THAP}>Tần số thấp</option>
                        <option value={CONS.WARNING_TYPE.DONG_TRUNG_TINH}>Dòng trung tính</option>
                        <option value={CONS.WARNING_TYPE.LECH_AP_PHA}>Lệch áp pha</option>
                        <option value={CONS.WARNING_TYPE.NGUONG_TONG_HAI}>Sóng hài</option>
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
            {warnings.length > 0 &&
                <table className="table tbl-overview mt-3">
                    <thead>
                        <tr>
                            <th className="header-m" style={{ width: "55px" }}>TT</th>
                            <th className="header-m text-center" style={{ width: "150px" }}>Loại cảnh báo</th>
                            <th className="header-m text-center" style={{ width: "150px" }}>Bắt đầu</th>
                            <th className="header-m text-center" style={{ width: "150px" }}>Mới nhất</th>
                            <th className="header-m text-center">Vị trí</th>
                            <th className="header-m text-center" style={{ width: "100px" }}>Trạng thái</th>
                            <th className="header-m text-center" style={{ width: "150px" }}>Người dùng</th>
                            <th className="header-m text-center" style={{ width: "55px" }}><i className="fa-regular fa-hand"></i></th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            warnings.map((warning, index) => (
                                <tr key={index}>
                                    <td className="text-center" onClick={() => {
                                        setWarningType(warning.warningType);
                                        handleClickWarning(warning.warningType, moment(warning.fromDate).format("YYYY-MM-DD HH:mm:ss"), moment(warning.toDate).format("YYYY-MM-DD HH:mm:ss"));
                                    }}>{index + 1}</td>
                                    <td className="text-center" onClick={() => {
                                        setWarningType(warning.warningType);
                                        handleClickWarning(warning.warningType, moment(warning.fromDate).format("YYYY-MM-DD HH:mm:ss"), moment(warning.toDate).format("YYYY-MM-DD HH:mm:ss"));
                                    }}>{getWarningName(warning.warningType)}</td>
                                    <td className="text-center" onClick={() => {
                                        setWarningType(warning.warningType);
                                        handleClickWarning(warning.warningType, moment(warning.fromDate).format("YYYY-MM-DD HH:mm:ss"), moment(warning.toDate).format("YYYY-MM-DD HH:mm:ss"));
                                    }}>{moment(warning.fromDate).format("YYYY-MM-DD hh:mm:ss")}</td>
                                    <td className="text-center" onClick={() => {
                                        setWarningType(warning.warningType);
                                        handleClickWarning(warning.warningType, moment(warning.fromDate).format("YYYY-MM-DD HH:mm:ss"), moment(warning.toDate).format("YYYY-MM-DD HH:mm:ss"));
                                    }}>{moment(warning.toDate).format("YYYY-MM-DD hh:mm:ss")}</td>
                                    <td>
                                        {`Layer ${warning.layer} > ` + warning.systemMapName}
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
                                    <td>
                                        {warning.staffName ? warning.staffName : "-"}
                                    </td>
                                    <td>
                                        <Link to={"/home/"} className="button-icon text-left" onClick={(e) => {
                                            e.preventDefault();
                                            handleDownloadData(warning.warningType, moment(warning.fromDate).format("YYYY-MM-DD HH:mm:ss"), moment(warning.toDate).format("YYYY-MM-DD HH:mm:ss"))
                                        }}>
                                            <img src="/resources/image/icon-download.png" alt="Tải bảng thông số" style={{ width: 16 }} />
                                        </Link>
                                        <Link to={"/home/"} className="button-icon float-right" onClick={(e) => {
                                            e.preventDefault();
                                            handleClickUpdate(warning.warningId)
                                        }}>
                                            <img src="/resources/image/icon-edit.png" alt="edit warning" style={{ width: 16 }} />
                                        </Link>
                                    </td>
                                </tr>
                            ))}
                    </tbody>
                </table>
            }
            <table className="table tbl-overview ml-0 mr-0" id="no-data" style={{ width: "-webkit-fill-available", display: "none" }}>
                <tbody>
                    <tr className="w-100">
                        <td height={30} className="text-center w-100" style={{ border: "none", background: "#D5D6D1" }}> Không có dữ liệu</td>
                    </tr>
                </tbody>
            </table>
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
                                    <th >F [Hz]</th>
                                    <th >VU [%]</th>
                                    <th >IU [%]</th>
                                    <th >ĐIỆN NĂNG {converter.convertLabelElectricPower(viewTypeModal, "Wh")}</th>
                                </tr>
                                {
                                    dataLoadFrameWarning.data.map((warning, index) => {
                                        return (
                                            <React.Fragment key={index}>
                                                <tr className="text-center" style={{
                                                    backgroundColor: (
                                                        (warningType === CONS.WARNING_TYPE.NGUONG_TONG_HAI && (warning.thdVan > settingValue || warning.thdVbn > settingValue || warning.thdVcn > settingValue))
                                                    ) ? "#FFA87D" : ""
                                                }}>
                                                    <td rowSpan="3">{index + 1}</td>
                                                    <td rowSpan="3">{moment(warning.sentDate).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                    <td >A</td>
                                                    <td style={{
                                                        backgroundColor: ((warningType === CONS.WARNING_TYPE.NGUONG_AP_CAO && warning.uan > settingValue)
                                                            || (warningType === CONS.WARNING_TYPE.NGUONG_AP_THAP && warning.uan < settingValue)
                                                            || (warningType === CONS.WARNING_TYPE.MAT_NGUON_PHA && warning.uan < settingValue)) ? "#FFA87D" : ""
                                                    }}>{warning.uan}</td>
                                                    <td style={{
                                                        backgroundColor:
                                                            ((warningType === CONS.WARNING_TYPE.QUA_TAI && (warning.ia >= settingValue * (warning.imccb ? warning.imccb : 0)))
                                                                || (warningType === CONS.WARNING_TYPE.LECH_PHA && (((warning.ia + warning.ib + warning.ic) / 3) / (warning.power * 1.44) > settingValue[0]
                                                                    && (Math.max(warning.ia + warning.ib + warning.ic) - Math.min(warning.ia + warning.ib + warning.ic)) / Math.min(warning.ia + warning.ib + warning.ic) > settingValue[1])) ? "#FFA87D" : "")
                                                    }}>{warning.ia}</td>
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
                                                    }}>{warning.f}</td>
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
                                                    }}>{warning.ubn}</td>
                                                    <td style={{
                                                        backgroundColor:
                                                            ((warningType === CONS.WARNING_TYPE.QUA_TAI && (warning.ib >= settingValue * (warning.imccb ? warning.imccb : 0)))
                                                                || (warningType === CONS.WARNING_TYPE.LECH_PHA && (((warning.ia + warning.ib + warning.ic) / 3) / (warning.power * 1.44) > settingValue[0]
                                                                    && (Math.max(warning.ia + warning.ib + warning.ic) - Math.min(warning.ia + warning.ib + warning.ic)) / Math.min(warning.ia + warning.ib + warning.ic) > settingValue[1])) ? "#FFA87D" : "")
                                                    }}>{warning.ib}</td>
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
                                                    }}>{warning.ucn}</td>
                                                    <td style={{
                                                        backgroundColor:
                                                            ((warningType === CONS.WARNING_TYPE.QUA_TAI && (warning.ic >= settingValue * (warning.imccb ? warning.imccb : 0)))
                                                                || (warningType === CONS.WARNING_TYPE.LECH_PHA && (((warning.ia + warning.ib + warning.ic) / 3) / (warning.power * 1.44) > settingValue[0]
                                                                    && (Math.max(warning.ia + warning.ib + warning.ic) - Math.min(warning.ia + warning.ib + warning.ic)) / Math.min(warning.ia + warning.ib + warning.ic) > settingValue[1])) ? "#FFA87D" : "")
                                                    }}>{warning.ic}</td>
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
                                    <th width="">Hoạt động</th>
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
                                    <td width="">{updateWarning.deleteFlag === 0 ? "Không" : "Có"}</td>
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