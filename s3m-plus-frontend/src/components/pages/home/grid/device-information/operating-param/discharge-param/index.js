import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import moment from "moment";
import $ from "jquery";
import OperationInformationService from "../../../../../../../services/OperationInformationService";
import CONS from './../../../../../../../constants/constant';
import converter from "../../../../../../../common/converter";
import Pagination from "react-js-pagination";

const DischargeParam = () => {

    const param = useParams();
    const [operationInfo, setOperationInfo] = useState([]);
    const [page, setPage] = useState(1);
    const [totalPage, setTotalPage] = useState(1);
    const [viewTypeTable, setViewTypeTable] = useState(null);

    const getOperationInformation = async () => {
        $('#no-data').hide();
        $('.table-data').hide();
        $('#loading').show();
        let res = await OperationInformationService.getOperationRmuDrawerGrid(param.customerId, param.deviceId, param.fromDate, param.toDate, page);
        if (res.status === 200 && res.data !== '') {
            $('#loading').hide();
            $('.table-data').show();
            let operationInfo = res.data.data;
            handleSetViewTypeTable(operationInfo);
            setOperationInfo(operationInfo);
            setTotalPage(res.data.totalPage);
        } else {
            $('#loading').hide();
            $('#no-data').show();
            $('.table-data').hide();
            setOperationInfo([]);
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

        setViewTypeTable(converter.setViewType(values.length > 0 ? min : 0));
    }

    const handlePagination = async page => {
        setPage(page);
    }

    document.title = "Thông tin thiết bị - Thông số vận hành";

    useEffect(() => {
        getOperationInformation();
    }, [param.customerId, param.deviceId, param.fromDate, param.toDate, page]);
    return (
        <>
            <div className="text-center loading" id="loading">
                <img height="60px" className="mt-0.5" src="/resources/image/loading.gif" alt="loading" />
            </div>
            {
                operationInfo.length > 0 ?
                    <div className="tab-content">
                        <table className="table tbl-overview tbl-tsd" style={{ marginLeft: "0" }}>
                            <thead>
                                <tr>
                                    <th colSpan="17" className="tbl-title">Thông số phóng điện</th>
                                </tr>
                            </thead>
                        </table>
                        <table className="table tbl-overview tbl-tsd" style={{ marginLeft: "0" }}>
                            <thead style={{ display: "table-header-group", width: "100%", tableLayout: "fixed" }}>
                                <tr>
                                    <th width="40px">TT</th>
                                    <th width="100px">Thời gian</th>
                                    <th></th>
                                    <th width="170px">LFB ratio [dB]</th>
                                    <th width="170px">LFB EPPC</th>
                                    <th width="170px">MFB Ratio [dB]</th>
                                    <th width="170px">MFB EPPC</th>
                                    <th width="170px">HFB ratio [dB]</th>
                                    <th width="170px">HFB EPPC</th>
                                </tr>
                            </thead>
                            <tbody className="tbody-border-none">
                                {
                                    operationInfo.map((item, index) => (
                                        <React.Fragment key={index + 1}>
                                            <tr>
                                                <td rowSpan={4} className="text-center">{index + 1}</td>
                                                <td rowSpan={4} className="text-center">{moment(item.sentDate).format(CONS.DATE_FORMAT)}</td>
                                                <td className="text-left">Giá trị</td>
                                                <td className="text-center">{item?.lfbRatio != null && item?.lfbRatio >= 0 ? item?.lfbRatio : "-"}</td>
                                                <td className="text-center">{item?.lfbEppc != null && item?.lfbRatio >= 0 ? item?.lfbEppc : "-"}</td>
                                                <td className="text-center">{item?.mfbRatio != null && item?.lfbRatio >= 0 ? item?.mfbRatio : "-"}</td>
                                                <td className="text-center">{item?.mlfbEppc != null && item?.lfbRatio >= 0 ? item?.mlfbEppc : "-"}</td>
                                                <td className="text-center">{item?.hlfbRatio != null && item?.lfbRatio >= 0 ? item?.hlfbRatio : "-"}</td>
                                                <td className="text-center">{item?.hlfbEppc != null && item?.lfbRatio >= 0 ? item?.hlfbEppc : "-"}</td>
                                            </tr>
                                            <tr>
                                                <td>Trung bình Ratio</td>
                                                <td className="text-center" colSpan={6}>{item?.meanRatio != null && item?.lfbRatio >= 0 ? item?.meanRatio : "-"}</td>
                                            </tr>
                                            <tr>
                                                <td>Trung bình EPPC</td>
                                                <td className="text-center" colSpan={6}>{item?.meanEppc != null && item?.lfbRatio >= 0 ? item?.meanEppc : "-"}</td>
                                            </tr>
                                            <tr>
                                                <td>Mức chỉ thị</td>
                                                <td className="text-center" colSpan={6}>{item?.indicator != null && item?.lfbRatio >= 0 && item?.lfbRatio <= 3 ? item?.indicator : "-"}</td>
                                            </tr>
                                        </React.Fragment>
                                    ))}
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
                                prevPageText="Trước"
                                nextPageText="Sau"
                                firstPageText="Đầu"
                                lastPageText="Cuối"
                            />
                        </div>
                    </div>
                    :
                    <table className="table tbl-overview ml-0 mr-0" id="no-data" style={{ width: "-webkit-fill-available" }}>
                        <tbody>
                            <tr className="w-100">
                                <td height={30} className="text-center w-100" style={{ border: "none", background: "#D5D6D1" }}> Không có dữ liệu</td>
                            </tr>
                        </tbody>
                    </table>
            }

        </>
    )
}

export default DischargeParam;