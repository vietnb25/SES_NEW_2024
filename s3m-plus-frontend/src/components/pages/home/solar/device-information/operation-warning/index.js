import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import moment from "moment";
import ReactModal from "react-modal";
import { useFormik } from "formik";
import AuthService from "../../../../../../services/AuthService";
import CONS from './../../../../../../constants/constant';
import { Calendar } from 'primereact/calendar';
import converter from "../../../../../../common/converter";
import WarningSolarService from "../../../../../../services/WarningSolarService";
import Pagination from "react-js-pagination";


const $ = window.$;

const OperationWarning = () => {

    const param = useParams();
    const [page, setPage] = useState(1);

    const [fromDate, setFromDate] = useState(new Date());
    const [toDate, setToDate] = useState(new Date());
    const [viewTypeModal, setViewTypeModal] = useState(null);
    const [settingValue, setSettingValue] = useState(null);
    const deviceType = parseInt(param.deviceType);
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
            let res = await WarningSolarService.updateOperatingWarningCache(param.customerId, updateWarningData);
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

        let res = await WarningSolarService.getWarningsOperationInformationByType(fDate, tDate, param.customerId, param.deviceId, warningType, page);
        if (res.status === 200 && res.data !== null) {
            $('#loading').hide();
            $('#no-data').hide();
            setDetailWarnings(res.data.data);
            setTotalPage(res.data.totalPage);
            setPage(1);
        } else {
            $('#loading').hide();
            setDetailWarnings([]);
        }
    }

    const handlePagination = async page => {
        setPage(page);
        let fDate = moment(fromDate).format("YYYY-MM-DD");
        let tDate = moment(toDate).format("YYYY-MM-DD");
        let res = await WarningSolarService.getWarningsOperationInformationByType(fDate, tDate, param.customerId, param.deviceId, warningType, page);
        if (res.status === 200) {
            setDetailWarnings(res.data.data);
            setTotalPage(res.data.totalPage);
        }
    }

    const downloadWarning = async () => {
        setStatusDownload(true);
        let fDate = moment(fromDate).format("YYYY-MM-DD");
        let tDate = moment(toDate).format("YYYY-MM-DD");
        let warningType = document.getElementById("warning-type").value;
        await WarningSolarService.download(warningType, fDate, tDate, param.customerId, param.deviceId, AuthService.getAuth().username);
        setStatusDownload(false);
    }

    const getWarningName = (warningType, deviceType) => {
        let warningName = "";
        let WARNING_TYPE_PV = CONS.WARNING_TYPE_PV
        if (deviceType === 1) {
            switch (warningType) {
                case CONS.WARNING_TYPE_PV.NHIET_DO_CAO:
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

    // data pv frame warning by warning type
    const [dataWarning, setDataWarning] = useState({
        page: 1,
        totalPage: 1,
        warningType: null,
        data: []
    });

    const handleClickWarning = async (warningType, deviceId, fromDate, toDate) => {
        let res = await WarningSolarService.showDataWarningByDevice(warningType, fromDate, toDate, param.customerId, deviceId, page);
        if (res.status === 200) {
            if (warningType !== CONS.WARNING_TYPE.LECH_PHA) {
                setSettingValue(res.data.settingValue);
            } else {
                let values = res.data.settingValue.split(",");
                setSettingValue(values);
            }
            handleSetViewTypeTable(res.data.dataWarning);
            setDataWarning({ ...dataWarning, data: res.data.dataWarning })
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
        let res = await WarningSolarService.getDetailOperatingWarningCache(param.customerId, warningId);
        if (res.status === 200) {
            setUpdateWarning(res.data);
        }
        setIsModalUpdateOpen(true);
    }

    const handleDownloadData = async (warningType, deviceId, fromDate, toDate) => {
        let res = await WarningSolarService.download(warningType, fromDate, toDate, param.customerId, deviceId, AuthService.getAuth().username);
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
                    <select id="warning-type" name="dataType" defaultValue={CONS.WARNING_TYPE.NHIET_DO_CAO} className="custom-select block custom-select-sm" onChange={() => getOperationWarning()}>
                        <option value={CONS.WARNING_TYPE_PV.NHIET_DO_CAO}>Nhiệt độ cao</option>
                        <option value={CONS.WARNING_TYPE_PV.MAT_KET_NOI_AC}>Mất kết nối AC</option>
                        <option value={CONS.WARNING_TYPE_PV.MAT_KET_NOI_DC}>Mất kết nối DC</option>
                        <option value={CONS.WARNING_TYPE_PV.DIEN_AP_CAO_AC}>Điện áp cao AC</option>
                        <option value={CONS.WARNING_TYPE_PV.DIEN_AP_THAP_AC}>Điện áp thấp AC</option>
                        <option value={CONS.WARNING_TYPE_PV.TAN_SO_THAP}>Tần số thấp</option>
                        <option value={CONS.WARNING_TYPE_PV.TAN_SO_CAO}>Tần số cao</option>
                        <option value={CONS.WARNING_TYPE_PV.MAT_NGUON_LUOI}>Mất kết nối lưới</option>
                        <option value={CONS.WARNING_TYPE_PV.CHAM_DAT}>Chạm đất</option>
                        <option value={CONS.WARNING_TYPE_PV.HONG_CAU_CHI}>Cầu chì</option>
                        <option value={CONS.WARNING_TYPE_PV.DONG_MO_CUA}>Đóng mở cửa</option>
                        <option value={CONS.WARNING_TYPE_PV.DIEN_AP_CAO_DC}>Điện áp cao DC</option>
                        <option value={CONS.WARNING_TYPE_PV.MEMORY_LOSS}>Mất bộ nhớ</option>
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
            {
                detailWarnings && parseInt(detailWarnings.length) > 0 ?
                    <>
                        <table className="table">
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

                    : <></>
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
                                        <th >VA</th>
                                        <th >VAr</th>
                                        <th >PF</th>
                                        <th >W</th>
                                        <th >Wh</th>
                                        <th >DCA</th>
                                        <th >DCV</th>
                                        <th >DCW</th>
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
                                                            {warning.ppvphA}
                                                        </td>
                                                        <td >
                                                            {warning.aphaA}
                                                        </td>
                                                        <td rowSpan="3">{warning.hz}</td>
                                                        <td rowSpan="3">{warning.va}</td>
                                                        <td rowSpan="3">{warning.var}</td>
                                                        <td rowSpan="3">{warning.pf}</td>
                                                        <td rowSpan="3">{warning.w}</td>
                                                        <td rowSpan="3">{warning.wh}</td>
                                                        <td rowSpan="3">{warning.dca}</td>
                                                        <td rowSpan="3">{warning.dcv}</td>
                                                        <td rowSpan="3">{warning.dcw}</td>
                                                        <td rowSpan="3">{warning.tmpCab}</td>
                                                        <td rowSpan="3">{warning.tmpSnk}</td>
                                                        <td rowSpan="3">{warning.tmpTrns}</td>
                                                        <td rowSpan="3">{warning.tmpOt}</td>
                                                    </tr>
                                                    <tr className="text-center">
                                                        <td >B</td>
                                                        <td >
                                                            {warning.ppvphB}
                                                        </td>
                                                        <td >
                                                            {warning.aphaB}
                                                        </td>
                                                    </tr>
                                                    <tr className="text-center">
                                                        <td>C</td>
                                                        <td >
                                                            {warning.ppvphC}
                                                        </td>
                                                        <td >
                                                            {warning.aphaC}
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
                                                    <th width="50px">DCAMax</th>
                                                    <th width="100px">COMBINER FUSE FAULT</th>
                                                    <th >COMBINER CABINET OPEN</th>
                                                    <th >TEMP</th>
                                                    <th >GROUND FAULT</th>
                                                    <th >DCA</th>
                                                    <th >DCAh</th>
                                                    <th >DCV</th>
                                                    <th >T</th>
                                                    <th >DCW</th>
                                                    <th >PR</th>
                                                </tr>
                                                {
                                                    dataWarning.data.map((warning, index) => {
                                                        return (
                                                            <React.Fragment key={index}>
                                                                <tr className="text-center" >
                                                                    <td >{index + 1}</td>
                                                                    <td >{moment(warning.sentDate).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                    <td >{warning.dcaMax}</td>
                                                                    <td >{warning.combinerFuseFault}</td>
                                                                    <td >{warning.combinerCabinetOpen}</td>
                                                                    <td >{warning.temp}</td>
                                                                    <td >{warning.groundfault}</td>
                                                                    <td >{warning.dca}</td>
                                                                    <td >{warning.dcaH}</td>
                                                                    <td >{warning.dcv}</td>
                                                                    <td >{warning.t}</td>
                                                                    <td >{warning.dcw}</td>
                                                                    <td >{warning.pr}</td>

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
                                                    <th >InDCA</th>
                                                    <th >InDCAhr</th>
                                                    <th >InDCV</th>
                                                    <th >InDCW</th>
                                                    <th >InBCPR</th>
                                                </tr>
                                                {
                                                    dataWarning.data.map((warning, index) => {
                                                        return (
                                                            <React.Fragment key={index}>
                                                                <tr className="text-center" >
                                                                    <td >{index + 1}</td>
                                                                    <td >{moment(warning.sentDate).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                    <td >{warning.combinerFuseFault}</td>
                                                                    <td >{warning.combinerCabinetOpen}</td>
                                                                    <td >{warning.temp}</td>
                                                                    <td >{warning.groundfault}</td>
                                                                    <td >{warning.inDCA}</td>
                                                                    <td >{warning.inDCAhr}</td>
                                                                    <td >{warning.inDCV}</td>
                                                                    <td >{warning.inDCW}</td>
                                                                    <td >{warning.inDCPR}</td>

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
        </>

    )
}

export default OperationWarning;