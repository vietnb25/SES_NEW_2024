import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import moment from "moment";
import $ from "jquery";
import OperationInformationService from "../../../../../../../services/OperationInformationService";
import CONS from './../../../../../../../constants/constant';
import converter from "../../../../../../../common/converter";
import Pagination from "react-js-pagination";

const ElectricalParam = () => {

    const param = useParams();
    const [operationInfo, setOperationInfo] = useState([]);
    const [page, setPage] = useState(1);
    const [totalPage, setTotalPage] = useState(1);
    const [viewTypeTable, setViewTypeTable] = useState(null);

    const getOperationInformation = async () => {
        $('#no-data').hide();
        $('.table-data').hide();
        $('#loading').show();
        let res = await OperationInformationService.getOperationInformation(param.customerId, param.deviceId, param.fromDate, param.toDate, page);
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
                                    <th colSpan="17" className="tbl-title">Thông số điện</th>
                                </tr>
                            </thead>
                        </table>
                        <table className="table tbl-overview tbl-tsd" style={{ marginLeft: "0" }}>
                            <thead style={{ display: "table-header-group", width: "100%", tableLayout: "fixed" }}>
                                <tr>
                                    <th width="40px">TT</th>
                                    <th width="100px">Thời gian</th>
                                    <th width="40px">Pha</th>
                                    <th width="100px">Điện áp [V]</th>
                                    <th width="100px">Dòng điện [A]</th>
                                    <th width="50px">%</th>
                                    <th width="80px">P [kW]</th>
                                    <th width="80px">Q [kVAr]</th>
                                    <th width="80px">S [kVA]</th>
                                    <th width="80px">PF</th>
                                    <th width="80px">THD U [%]</th>
                                    <th width="80px">THD I [%]</th>
                                    <th width="80px">Phase U</th>
                                    <th width="80px">F [Hz]</th>
                                    <th width="80px">Vu [%]</th>
                                    <th width="80px">Iu [%]</th>
                                    <th>Điện năng [kWh]</th>
                                </tr>
                            </thead>
                            <tbody className="tbody-border-none">
                                {
                                    operationInfo.map((item, index) => (
                                        <React.Fragment key={index + 1}>
                                            <tr >
                                                <td rowSpan={3} className="text-center">{index + 1}</td>
                                                <td rowSpan={3} className="text-center">{moment(item.sentDate).format(CONS.DATE_FORMAT)}</td>
                                                <td className="text-center">A</td>
                                                <td className="text-center">{item?.uan != null ? item?.uan : "-"}</td>
                                                <td className="text-center">{item?.ia != null ? item?.ia : "-"}</td>
                                                <td className="text-center">-</td>
                                                <td className="text-center">{item?.pa != null ? item?.pa : "-"}</td>
                                                <td className="text-center">{item?.qa != null ? item?.qa : "-"}</td>
                                                <td className="text-center">{item?.sa != null ? item?.sa : "-"}</td>
                                                <td className="text-center">{item?.pfa != null ? item?.pfa : "-"}</td>
                                                <td className="text-center">{item?.thdVab != null ? item?.thdVab : "-"}</td>
                                                <td className="text-center">{item?.thdIa != null ? item?.thdIa : "-"}</td>
                                                <td className="text-center">-</td>
                                                <td className="text-center" rowSpan={3}>{item?.f != null ? item?.f : "-"}</td>
                                                <td className="text-center" rowSpan={3}>-</td>
                                                <td className="text-center" rowSpan={3}>-</td>
                                                <td className="text-center" rowSpan={3}>{item?.ep != null && item?.ep > 0 ? item?.ep : "-"}</td>
                                            </tr>
                                            <tr>
                                                <td className="text-center">B</td>
                                                <td className="text-center">{item?.ubn != null ? item?.ubn : "-"}</td>
                                                <td className="text-center">{item?.ib != null ? item?.ib : "-"}</td>
                                                <td className="text-center">-</td>
                                                <td className="text-center">{item?.pb != null ? item?.pb : "-"}</td>
                                                <td className="text-center">{item?.qb != null ? item?.qb : "-"}</td>
                                                <td className="text-center">{item?.sb != null ? item?.sb : "-"}</td>
                                                <td className="text-center">{item?.pfb != null ? item?.pfb : "-"}</td>
                                                <td className="text-center">{item?.thdVbc != null ? item?.thdVbc : "-"}</td>
                                                <td className="text-center">{item?.thdIb != null ? item?.thdIb : "-"}</td>
                                                <td className="text-center">-</td>
                                            </tr>
                                            <tr>
                                                <td className="text-center">C</td>
                                                <td className="text-center">{item?.ucn != null ? item?.ucn : "-"}</td>
                                                <td className="text-center">{item?.ic != null ? item?.ic : "-"}</td>
                                                <td className="text-center">-</td>
                                                <td className="text-center">{item?.pc != null ? item?.pc : "-"}</td>
                                                <td className="text-center">{item?.qc != null ? item?.qc : "-"}</td>
                                                <td className="text-center">{item?.sc != null ? item?.sc : "-"}</td>
                                                <td className="text-center">{item?.pfc != null ? item?.pfc : "-"}</td>
                                                <td className="text-center">{item?.thdVca != null ? item?.thdVca : "-"}</td>
                                                <td className="text-center">{item?.thdIc != null ? item?.thdIc : "-"}</td>
                                                <td className="text-center">-</td>
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

export default ElectricalParam;