import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import moment from "moment";
import $ from "jquery";
import OperationInformationService from "../../../../../../../services/OperationInformationService";
import CONS from '../../../../../../../constants/constant';
import Pagination from "react-js-pagination";

const CombinerParam = () => {

    const param = useParams();
    const [operationInfo, setOperationInfo] = useState([]);
    const [page, setPage] = useState(1);
    const [totalPage, setTotalPage] = useState(1);

    const getOperationInformation = async () => {
        $('#no-data').hide();
        $('.table-data').hide();
        $('#loading').show();
        console.log(11);
        let res = await OperationInformationService.getOperationCombinerPV(param.customerId, param.deviceId, param.fromDate, param.toDate, page);
        if (res.status === 200 && res.data.data !== '') {
            $('#loading').hide();
            $('.table-data').show();
            let operationInfo = res.data.data;
            setOperationInfo(operationInfo);
            setTotalPage(res.data.totalPage);
        } else {
            $('#loading').hide();
            $('#no-data').show();
            $('.table-data').hide();
            setOperationInfo([]);
        }
    }

    const handlePagination = async page => {
        setPage(page);
    }

    document.title = "Thông tin thiết bị - Thông số vận hành";

    useEffect(() => {
        getOperationInformation();
    }, [param.customerId, param.deviceId, param.fromDate, param.toDate, page])
    return (
        <>
            <div className="text-center loading" id="loading">
                <img height="60px" className="mt-0.5" src="/resources/image/loading.gif" alt="loading" />
            </div>
            {
                operationInfo?.length > 0 ?
                    <div className="tab-content table-data">
                        <table className="table tbl-overview tbl-tsnd mt-3" style={{ marginLeft: "0" }}>
                            <thead>
                                <tr>
                                    <th colSpan={6} className="tbl-title">Thông số Combiner BOX</th>
                                </tr>
                            </thead>
                        </table>
                        <table className="table tbl-overview tbl-tsnd" style={{ marginLeft: "0" }}>
                            <thead style={{ display: "table-header-group", width: "100%", tableLayout: "fixed" }}>
                                <tr>
                                    <th colSpan={2} width="40px">TT</th>
                                    <th colSpan={2} width="100px">Thời gian</th>
                                    <th className="text-center">U<sub>DC</sub> [V]</th>
                                    <th className="text-center">I<sub>DC</sub> [A]</th>
                                    <th className="text-center">P [kW]</th>
                                    <th className="text-center">E [kWh]</th>
                                    <th className="text-center">Nhiệt độ [°C]</th>
                                    <th className="text-center">Hiệu suất [%]</th>
                                </tr>
                            </thead>
                            <tbody>
                                {
                                    operationInfo?.map((item, index) => (
                                        <React.Fragment key={index + 1}>
                                            <tr>
                                                <td className="text-center" colSpan={2} height="70px">{index + 1}</td>
                                                <td className="text-center" colSpan={2} height="70px">{moment(item.sentDate).format(CONS.DATE_FORMAT)}</td>
                                                <td className="text-center" height="70px">{item?.vdcCombiner != null ? item?.vdcCombiner : "-"}</td>
                                                <td className="text-center" height="70px">{item?.idcCombiner != null ? item?.idcCombiner : "-"}</td>
                                                <td className="text-center" height="70px">{item?.pdcCombiner != null ? item?.pdcCombiner : "-"}</td>
                                                <td className="text-center" height="70px">{item?.epCombiner != null ? item?.epCombiner : "-"}</td>
                                                <td className="text-center" height="70px">{item?.t != null ? item?.t : "-"}</td>
                                                <td className="text-center" height="70px">-</td>
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
                    </div> :
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

export default CombinerParam;