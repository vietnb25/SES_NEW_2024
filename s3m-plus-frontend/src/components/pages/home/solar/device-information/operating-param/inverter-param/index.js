import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import moment from "moment";
import $ from "jquery";
import OperationInformationService from "../../../../../../../services/OperationInformationService";
import CONS from '../../../../../../../constants/constant';
import converter from "../../../../../../../common/converter";
import Pagination from "react-js-pagination";

const InverterParam = () => {

    const param = useParams();
    const [operationInfo, setOperationInfo] = useState([]);
    const [page, setPage] = useState(1);
    const [totalPage, setTotalPage] = useState(1);
    const [viewTypeTable, setViewTypeTable] = useState(null);

    const getOperationInformation = async () => {
        $('#no-data').hide();
        $('.table-data').hide();
        $('#loading').show();
        let res = await OperationInformationService.getOperationInverterPV(param.customerId, param.deviceId, param.fromDate, param.toDate, page);
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
            if (item.w && item.w > 0) {
                values.push(item.w);
            }
            if (item.wh && item.wh > 0) {
                values.push(item.wh);
            }
            if (item.dcw && item.dcw > 0) {
                values.push(item.dcw);
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
                                    <th colSpan="17" className="tbl-title">Thông số Inverter</th>
                                </tr>
                            </thead>
                        </table>
                        <table className="table tbl-overview tbl-tsd" style={{ marginLeft: "0" }}>
                            <thead style={{ display: "table-header-group", width: "100%", tableLayout: "fixed" }}>
                                <tr>
                                    <th width="40px">TT</th>
                                    <th width="100px">Thời gian</th>
                                    <th width="40px">Pha</th>
                                    <th width="60px">U<sub>AC</sub> [V]</th>
                                    <th width="60px">I<sub>AC</sub> [A]</th>
                                    <th width="50px">PF</th>
                                    <th width="80px">P<sub>AC</sub> [KW]</th>
                                    <th width="80px">Q<sub>AC</sub> [KW]</th>
                                    <th width="120px">P<sub>AC_Total</sub> [KW]</th>
                                    <th width="120px">Q<sub>AC_Total</sub> [KW]</th>
                                    <th width="60px">U<sub>DC</sub> [V]</th>
                                    <th width="60px">I<sub>DC</sub> [A]</th>
                                    <th width="80px">P<sub>DC</sub> [KW]</th>
                                    <th width="120px">Hiệu suất [%]</th>
                                    <th width="50px">F [Hz]</th>
                                    <th>Yield [kWh]</th>
                                    <th width="120px">T<sub>Cabnet</sub> [°C]</th>
                                    <th width="120px">T<sub>Heatsink</sub> [°C]</th>
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
                                                <td className="text-center">{item?.va != null ? item?.va : "-"}</td>
                                                <td className="text-center">{item?.ia != null ? item?.ia : "-"}</td>
                                                <td className="text-center">{item?.pfa != null ? item?.pfa : "-"}</td>
                                                <td className="text-center">{item?.pa != null ? item?.pa : "-"}</td>
                                                <td className="text-center">{item?.qa != null ? item?.qa : "-"}</td>
                                                <td className="text-center" rowSpan={3}>{item?.ptotal != null ? item?.ptotal : "-"}</td>
                                                <td className="text-center" rowSpan={3}>{item?.qtotal != null ? item?.qtotal : "-"}</td>
                                                <td className="text-center" rowSpan={3}>{item?.udc != null ? item?.udc : "-"}</td>
                                                <td className="text-center" rowSpan={3}>{item?.idc != null ? item?.idc : "-"}</td>
                                                <td className="text-center" rowSpan={3}>{item?.pdc != null ? item?.pdc : "-"}</td>
                                                <td className="text-center" rowSpan={3}>-</td>
                                                <td className="text-center" rowSpan={3}>{item?.f != null ? item?.f : "-"}</td>
                                                <td className="text-center" rowSpan={3}>{item?.ep != null ? item?.ep : "-"}</td>
                                                <td className="text-center" rowSpan={3}>{item?.tmpCab != null ? item?.tmpCab : "-"}</td>
                                                <td className="text-center" rowSpan={3}>{item?.tmpSnk != null ? item?.tmpSnk : "-"}</td>
                                            </tr>
                                            <tr>
                                                <td className="text-center">B</td>
                                                <td className="text-center">{item?.vb != null ? item?.vb : "-"}</td>
                                                <td className="text-center">{item?.ib != null ? item?.ib : "-"}</td>
                                                <td className="text-center">{item?.pfb != null ? item?.pfb : "-"}</td>
                                                <td className="text-center">{item?.pb != null ? item?.pb : "-"}</td>
                                                <td className="text-center">{item?.qb != null ? item?.qb : "-"}</td>
                                            </tr>
                                            <tr>
                                                <td className="text-center">C</td>
                                                <td className="text-center">{item?.vc != null ? item?.vc : "-"}</td>
                                                <td className="text-center">{item?.ic != null ? item?.ic : "-"}</td>
                                                <td className="text-center">{item?.pfc != null ? item?.pfc : "-"}</td>
                                                <td className="text-center">{item?.pc != null ? item?.pc : "-"}</td>
                                                <td className="text-center">{item?.qc != null ? item?.qc : "-"}</td>
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

export default InverterParam;