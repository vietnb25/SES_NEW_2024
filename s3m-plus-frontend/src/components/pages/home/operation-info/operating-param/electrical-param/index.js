import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import OperationInformationService from "../../../../../../services/OperationInformationService";
import CONS from "../../../../../../constants/constant";
import moment from "moment";
import $ from "jquery";

const ElectricalParam = () => {

    const param = useParams();
    const [operationInfo, setOperationInfo] = useState([]);
    const [page, setPage] = useState(1);
    const [totalPage, setTotalPage] = useState(1);

    const getOperationInformation = async () => {
        $('#loading').show();
        let res = await OperationInformationService.getOperationInformation(param.deviceId, param.fromDate, param.toDate, page);
        if (res.status === 200 && parseInt(res.data.data.length) > 0) {
            $('#loading').hide();
            $('.table-data').show();
            let operationInfo = res.data.data;
            setOperationInfo(operationInfo);
            setTotalPage(res.data.totalPage);
        } else {
            $('#loading').hide();
            $('#no-data').show();
            $('.table-data').hide();
        }
    }

    const handlePagination = async page => {
        setPage(page);
    }

    document.title = "Thông tin thiết bị - Thông số vận hành";

    useEffect(() => {
        getOperationInformation();
    }, [param.deviceId, param.fromDate, param.toDate, page]);
    return (
        <>
            <div className="text-center loading" id="loading">
                <img height="60px" className="mt-0.5" src="/resources/image/loading.gif" alt="loading" />
            </div>
            {
                operationInfo.length > 0 ?
                    <div className="tab-content table-data">
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
                                    <th>Active Energy [kWh]</th>
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
                                                <td className="text-right">{item.uan === null ? "-" : item.uan}</td>
                                                <td className="text-right">{item.ia === null ? "-" : item.ia}</td>
                                                <td className="text-center">-</td>
                                                <td className="text-right">{item.pa === null ? "-" : item.pa}</td>
                                                <td className="text-right">{item.qa === null ? "-" : item.qa}</td>
                                                <td className="text-right">{item.sa === null ? "-" : item.sa}</td>
                                                <td className="text-right">{item.pfa === null ? "-" : item.pfa}</td>
                                                <td className="text-right">{item.thdVab === null ? "-" : item.thdVab}</td>
                                                <td className="text-right">{item.thdIa === null ? "-" : item.thdIa}</td>
                                                <td className="text-center">-</td>
                                                <td rowSpan={3} className="text-right">{item.f === null ? "-" : item.f}</td>
                                                <td rowSpan={3} className="text-center">-</td>
                                                <td rowSpan={3} className="text-center">-</td>
                                                <td rowSpan={3} className="text-right">{item.ep === null ? "-" : item.ep}</td>
                                            </tr>
                                            <tr>
                                                <td className="text-center">B</td>
                                                <td className="text-right">{item.ubn === null ? "-" : item.ubn}</td>
                                                <td className="text-right">{item.ib === null ? "-" : item.ib}</td>
                                                <td className="text-center">-</td>
                                                <td className="text-right">{item.pb === null ? "-" : item.pb}</td>
                                                <td className="text-right">{item.qb === null ? "-" : item.qb}</td>
                                                <td className="text-right">{item.sb === null ? "-" : item.sb}</td>
                                                <td className="text-right">{item.pfb === null ? "-" : item.pfb}</td>
                                                <td className="text-right">{item.thdVbc === null ? "-" : item.thdVbc}</td>
                                                <td className="text-right">{item.thdIb === null ? "-" : item.thdIb}</td>
                                                <td className="text-center">-</td>
                                            </tr>
                                            <tr>
                                                <td className="text-center">C</td>
                                                <td className="text-right">{item.ucn === null ? "-" : item.ucn}</td>
                                                <td className="text-right">{item.ic === null ? "-" : item.ic}</td>
                                                <td className="text-center">-</td>
                                                <td className="text-right">{item.pc === null ? "-" : item.pc}</td>
                                                <td className="text-right">{item.qc === null ? "-" : item.qc}</td>
                                                <td className="text-right">{item.sc === null ? "-" : item.sc}</td>
                                                <td className="text-right">{item.pfc === null ? "-" : item.pfc}</td>
                                                <td className="text-right">{item.thdVca === null ? "-" : item.thdVca}</td>
                                                <td className="text-right">{item.thdIc === null ? "-" : item.thdIc}</td>
                                                <td className="text-center">-</td>
                                            </tr>
                                        </React.Fragment>
                                    ))}
                            </tbody>
                        </table>
                        <div className="pagination">
                            {
                                totalPage > 0 &&
                                <span className={`pagelinks ${page === 1 && 'disabled'}`}>
                                    <Link to={"/"} onClick={(e) => {
                                        e.preventDefault();
                                        handlePagination(1)
                                    }}>Đầu</Link>
                                </span>
                            }
                            <span className={`pagelinks ${page === 1 && 'disabled'}`}>
                                <Link to={"/"} onClick={(e) => {
                                    e.preventDefault();
                                    handlePagination(page - 1)
                                }}>Trước</Link>
                            </span>
                            {
                                Array.from(Array(totalPage), (e, i) => {
                                    return <span key={i} className={`pagelinks ${page === i + 1 && 'active'}`}>
                                        <Link to={"/"} onClick={(e) => {
                                            e.preventDefault();
                                            handlePagination(i + 1)
                                        }}>{i + 1}</Link>
                                    </span>
                                })
                            }
                            <span className={`pagelinks ${page === totalPage && 'disabled'}`}>
                                <Link to={"/"} onClick={(e) => {
                                    e.preventDefault();
                                    handlePagination(page + 1)
                                }}>Sau</Link>
                            </span>
                            {
                                totalPage > 0 &&
                                <span className={`pagelinks ${page === totalPage && 'disabled'}`}>
                                    <Link to={"/"} onClick={(e) => {
                                        e.preventDefault();
                                        handlePagination(totalPage)
                                    }}>Cuối</Link>
                                </span>
                            }
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