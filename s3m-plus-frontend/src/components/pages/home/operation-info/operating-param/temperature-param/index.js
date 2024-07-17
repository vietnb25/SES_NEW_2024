import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import OperationInformationService from "../../../../../../services/OperationInformationService";
import moment from "moment";
import CONS from "../../../../../../constants/constant";
import $ from "jquery";

const TemperatureParam = () => {

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
            $('.table-data').hide();
            $('#loading').hide();
            $('#no-data').show();
        }
    }

    const handlePagination = async page => {
        setPage(page);
    }

    document.title = "Thông tin thiết bị - Thông số vận hành";

    useEffect(() => {
        getOperationInformation();
    }, [param.deviceId, param.fromDate, param.toDate, page])
    return (
        <>
            <div className="text-center loading" id="loading">
                <img height="60px" className="mt-0.5" src="/resources/image/loading.gif" alt="loading" />
            </div>
            {
                operationInfo.length > 0 ?
                <div className="tab-content table-data">
                    <table className="table tbl-overview tbl-tsnd mt-3" style={{ marginLeft: "0" }}>
                        <thead>
                            <tr>
                                <th colSpan={6} className="tbl-title">Thông số nhiệt độ [°C]</th>
                            </tr>
                        </thead>
                    </table>
                    <table className="table tbl-overview tbl-tsnd" style={{ marginLeft: "0" }}>
                        <thead style={{ display: "table-header-group", width: "100%", tableLayout: "fixed" }}>
                            <tr>
                                <th width="40px">TT</th>
                                <th width="100px">Thời gian</th>
                                <th width="40px">Pha</th>
                                <th>Vị trí 1</th>
                                <th>Vị trí 2</th>
                                <th>Vị trí 3</th>
                            </tr>
                        </thead>
                        <tbody>
                            {
                                operationInfo.map((item, index) => (
                                    <>
                                        <tr key={index + 1}>
                                            <td rowspan={3} className="text-center">{index + 1}</td>
                                            <td rowspan={3} className="text-center">{moment(item.sentDate).format(CONS.DATE_FORMAT)}</td>
                                            <td className="text-center">A</td>
                                            <td className="text-right">{item.t1 === null ? "-" : item.t1}</td>
                                            <td className="text-center">-</td>
                                            <td className="text-center">-</td>
                                        </tr>
                                        <tr>
                                            <td className="text-center">B</td>
                                            <td className="text-right">{item.t2 === null ? "-" : item.t2}</td>
                                            <td className="text-center">-</td>
                                            <td className="text-center">-</td>
                                        </tr>
                                        <tr>
                                            <td className="text-center">C</td>
                                            <td className="text-right">{item.t2 === null ? "-" : item.t2}</td>
                                            <td className="text-center">-</td>
                                            <td className="text-center">-</td>
                                        </tr>
                                    </>
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
                                return  <span key={i} className={`pagelinks ${page === i+1 && 'active'}`}>
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

export default TemperatureParam;