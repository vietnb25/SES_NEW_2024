import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import moment from "moment";
import $ from "jquery";
import OperationInformationService from "../../../../../../../services/OperationInformationService";
import CONS from './../../../../../../../constants/constant';
import converter from "../../../../../../../common/converter";
import Pagination from "react-js-pagination";

const TemperatureParam = () => {

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
                                    <th colSpan="17" className="tbl-title">Thông số nhiệt độ</th>
                                </tr>
                            </thead>
                        </table>
                        <table className="table tbl-overview tbl-tsd" style={{ marginLeft: "0" }}>
                            <thead style={{ display: "table-header-group", width: "100%", tableLayout: "fixed" }}>
                                <tr>
                                    <th width="40px">TT</th>
                                    <th width="100px">Thời gian</th>
                                    <th width="40px">Pha</th>
                                    <th>Nhiệt độ cực trên [°C]</th>
                                    <th>Nhiệt độ cực dưới [°C]</th>
                                    <th>Nhiệt độ khoang [°C]</th>
                                    <th>Độ ẩm [%]</th>
                                </tr>
                            </thead>
                            <tbody className="tbody-border-none">
                                {
                                    operationInfo.map((item, index) => (
                                        <React.Fragment key={index + 1}>
                                            <tr>
                                                <td className="text-center" rowSpan={3}>{index + 1}</td>
                                                <td className="text-center" rowSpan={3}>{moment(item.sentDate).format(CONS.DATE_FORMAT)}</td>
                                                <td className="text-center">A</td>
                                                <td className="text-center">{item?.sawId1 && parseInt(item?.sawId1) >= -50 && parseInt(item?.sawId1) <= 180 != null ? item?.sawId1 : "-"}</td>
                                                <td className="text-center">{item?.sawId4 && parseInt(item?.sawId4) >= -50 && parseInt(item?.sawId4) <= 180 != null ? item?.sawId4 : "-"}</td>
                                                <td rowSpan={3} className="text-center">{item?.t && parseInt(item?.t) >= -50 && item?.t <= 180 != null ? item?.t : "-"}</td>
                                                <td rowSpan={3} className="text-center">{item?.h && parseInt(item?.h) >= 0 && item?.h <= 100 != null ? item?.h : "-"}</td>
                                            </tr>
                                            <tr>
                                                <td className="text-center">B</td>
                                                <td className="text-center">{item?.sawId2 && parseInt(item?.sawId2) >= -50 && parseInt(item?.sawId2) <= 180 != null ? item?.sawId2 : "-"}</td>
                                                <td className="text-center">{item?.sawId5 && parseInt(item?.sawId5) >= -50 && parseInt(item?.sawId5) <= 180 != null ? item?.sawId5 : "-"}</td>
                                            </tr>
                                            <tr>
                                                <td className="text-center">C</td>
                                                <td className="text-center">{item?.sawId3 && parseInt(item?.sawId3) >= -50 && parseInt(item?.sawId3) <= 180 != null ? item?.sawId3 : "-"}</td>
                                                <td className="text-center">{item?.sawId6 && parseInt(item?.sawId6) >= -50 && parseInt(item?.sawId6) <= 180 != null ? item?.sawId6 : "-"}</td>
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

export default TemperatureParam;