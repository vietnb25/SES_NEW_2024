import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import moment from "moment";
import $ from "jquery";
import OperationInformationService from "../../../../../../../services/OperationInformationService";
import CONS from './../../../../../../../constants/constant';
import Pagination from "react-js-pagination";

const PowerQuality = () => {

    const param = useParams();

    const [powerQualities, setPowerQuality] = useState([]);
    const [page, setPage] = useState(1);
    const [totalPage, setTotalPage] = useState(1);

    const getPowerQualities = async () => {
        $('#loading').show();
        $('#no-data').hide();
        $('.table-data').hide();
        let res = await OperationInformationService.getPowerQuality(param.customerId, param.deviceId, param.fromDate, param.toDate, page);
        if (res.status === 200 && res.data !== '') {
            $('#loading').hide();
            $('.table-data').show();
            let powerQualities = res.data.data;
            setPowerQuality(powerQualities);
            setTotalPage(res.data.totalPage);
        } else {
            $('.table-data').hide();
            $('#loading').hide();
            $('#no-data').show();
            setPowerQuality([]);
        }
    }

    const handlePagination = page => {
        setPage(page);
    }

    useEffect(() => {
        getPowerQualities();
        document.title = "Thông tin thiết bị - Thông số vận hành";
    }, [param.customerId, param.deviceId, param.fromDate, param.toDate, page])
    return (
        <>
            <div className="text-center loading" id="loading">
                <img height="60px" className="mt-0.5" src="/resources/image/loading.gif" alt="icon-loading" />
            </div>
            {
                powerQualities.length > 0 ?
                    <div className="tab-content">
                        <table className="table tbl-overview tbl-tsd mt-3" style={{ marginLeft: "0" }}>
                            <thead>
                                <tr>
                                    <th colSpan={34} className="tbl-title">Thông số sóng hài [%]</th>
                                </tr>
                            </thead>
                        </table>
                        <table className="table tbl-overview tbl-tsd" style={{ marginLeft: "0" }}>
                            <thead style={{ display: "table-header-group", width: "100%", tableLayout: "fixed" }}>
                                <tr>
                                    <th width="40px">TT</th>
                                    <th width="80px">Thời gian</th>
                                    <th width="40px">Pha</th>
                                    <th>H1</th>
                                    <th>H2</th>
                                    <th>H3</th>
                                    <th>H4</th>
                                    <th>H5</th>
                                    <th>H6</th>
                                    <th>H7</th>
                                    <th>H8</th>
                                    <th>H9</th>
                                    <th>H10</th>
                                    <th>H11</th>
                                    <th>H12</th>
                                    <th>H13</th>
                                    <th>H14</th>
                                    <th>H15</th>
                                    <th>H16</th>
                                    <th>H17</th>
                                    <th>H18</th>
                                    <th>H19</th>
                                    <th>H20</th>
                                    <th>H21</th>
                                    <th>H22</th>
                                    <th>H23</th>
                                    <th>H24</th>
                                    <th>H25</th>
                                    <th>H26</th>
                                    <th>H27</th>
                                    <th>H28</th>
                                    <th>H29</th>
                                    <th>H30</th>
                                    <th>H31</th>
                                </tr>
                            </thead>
                            <tbody>
                                {
                                    powerQualities.map((item, index) => (
                                        <React.Fragment key={index + 1}>
                                            <tr>
                                                <td rowSpan={6} className="text-center">{index + 1}</td>
                                                <td rowSpan={6} className="text-center">{moment(item.sentDate).format(CONS.DATE_FORMAT)}</td>
                                                <td className="text-center">UA</td>
                                                <td className="text-right">{item.vanH1 === null ? "-" : item.vanH1}</td>
                                                <td className="text-right">{item.vanH2 === null ? "-" : item.vanH2}</td>
                                                <td className="text-right">{item.vanH3 === null ? "-" : item.vanH3}</td>
                                                <td className="text-right">{item.vanH4 === null ? "-" : item.vanH4}</td>
                                                <td className="text-right">{item.vanH5 === null ? "-" : item.vanH5}</td>
                                                <td className="text-right">{item.vanH6 === null ? "-" : item.vanH6}</td>
                                                <td className="text-right">{item.vanH7 === null ? "-" : item.vanH7}</td>
                                                <td className="text-right">{item.vanH8 === null ? "-" : item.vanH8}</td>
                                                <td className="text-right">{item.vanH9 === null ? "-" : item.vanH9}</td>
                                                <td className="text-right">{item.vanH10 === null ? "-" : item.vanH10}</td>
                                                <td className="text-right">{item.vanH11 === null ? "-" : item.vanH11}</td>
                                                <td className="text-right">{item.vanH12 === null ? "-" : item.vanH12}</td>
                                                <td className="text-right">{item.vanH13 === null ? "-" : item.vanH13}</td>
                                                <td className="text-right">{item.vanH14 === null ? "-" : item.vanH14}</td>
                                                <td className="text-right">{item.vanH15 === null ? "-" : item.vanH15}</td>
                                                <td className="text-right">{item.vanH16 === null ? "-" : item.vanH16}</td>
                                                <td className="text-right">{item.vanH17 === null ? "-" : item.vanH17}</td>
                                                <td className="text-right">{item.vanH18 === null ? "-" : item.vanH18}</td>
                                                <td className="text-right">{item.vanH19 === null ? "-" : item.vanH19}</td>
                                                <td className="text-right">{item.vanH20 === null ? "-" : item.vanH20}</td>
                                                <td className="text-right">{item.vanH21 === null ? "-" : item.vanH21}</td>
                                                <td className="text-right">{item.vanH22 === null ? "-" : item.vanH22}</td>
                                                <td className="text-right">{item.vanH23 === null ? "-" : item.vanH23}</td>
                                                <td className="text-right">{item.vanH24 === null ? "-" : item.vanH24}</td>
                                                <td className="text-right">{item.vanH25 === null ? "-" : item.vanH25}</td>
                                                <td className="text-right">{item.vanH26 === null ? "-" : item.vanH26}</td>
                                                <td className="text-right">{item.vanH27 === null ? "-" : item.vanH27}</td>
                                                <td className="text-right">{item.vanH28 === null ? "-" : item.vanH28}</td>
                                                <td className="text-right">{item.vanH29 === null ? "-" : item.vanH29}</td>
                                                <td className="text-right">{item.vanH30 === null ? "-" : item.vanH30}</td>
                                                <td className="text-right">{item.vanH31 === null ? "-" : item.vanH31}</td>
                                            </tr>
                                            <tr>
                                                <td className="text-center">UB</td>
                                                <td className="text-right">{item.vbnH1 === null ? "-" : item.vanH1}</td>
                                                <td className="text-right">{item.vbnH2 === null ? "-" : item.vanH2}</td>
                                                <td className="text-right">{item.vbnH3 === null ? "-" : item.vanH3}</td>
                                                <td className="text-right">{item.vbnH4 === null ? "-" : item.vanH4}</td>
                                                <td className="text-right">{item.vbnH5 === null ? "-" : item.vanH5}</td>
                                                <td className="text-right">{item.vbnH6 === null ? "-" : item.vanH6}</td>
                                                <td className="text-right">{item.vbnH7 === null ? "-" : item.vanH7}</td>
                                                <td className="text-right">{item.vbnH8 === null ? "-" : item.vanH8}</td>
                                                <td className="text-right">{item.vbnH9 === null ? "-" : item.vanH9}</td>
                                                <td className="text-right">{item.vbnH10 === null ? "-" : item.vanH10}</td>
                                                <td className="text-right">{item.vbnH11 === null ? "-" : item.vanH11}</td>
                                                <td className="text-right">{item.vbnH12 === null ? "-" : item.vanH12}</td>
                                                <td className="text-right">{item.vbnH13 === null ? "-" : item.vanH13}</td>
                                                <td className="text-right">{item.vbnH14 === null ? "-" : item.vanH14}</td>
                                                <td className="text-right">{item.vbnH15 === null ? "-" : item.vanH15}</td>
                                                <td className="text-right">{item.vbnH16 === null ? "-" : item.vanH16}</td>
                                                <td className="text-right">{item.vbnH17 === null ? "-" : item.vanH17}</td>
                                                <td className="text-right">{item.vbnH18 === null ? "-" : item.vanH18}</td>
                                                <td className="text-right">{item.vbnH19 === null ? "-" : item.vanH19}</td>
                                                <td className="text-right">{item.vbnH20 === null ? "-" : item.vanH20}</td>
                                                <td className="text-right">{item.vbnH21 === null ? "-" : item.vanH21}</td>
                                                <td className="text-right">{item.vbnH22 === null ? "-" : item.vanH22}</td>
                                                <td className="text-right">{item.vbnH23 === null ? "-" : item.vanH23}</td>
                                                <td className="text-right">{item.vbnH24 === null ? "-" : item.vanH24}</td>
                                                <td className="text-right">{item.vbnH25 === null ? "-" : item.vanH25}</td>
                                                <td className="text-right">{item.vbnH26 === null ? "-" : item.vanH26}</td>
                                                <td className="text-right">{item.vbnH27 === null ? "-" : item.vanH27}</td>
                                                <td className="text-right">{item.vbnH28 === null ? "-" : item.vanH28}</td>
                                                <td className="text-right">{item.vbnH29 === null ? "-" : item.vanH29}</td>
                                                <td className="text-right">{item.vbnH30 === null ? "-" : item.vanH30}</td>
                                                <td className="text-right">{item.vbnH31 === null ? "-" : item.vanH31}</td>
                                            </tr>
                                            <tr>
                                                <td className="text-center">UC</td>
                                                <td className="text-right">{item.vcnH1 === null ? "-" : item.vcnH1}</td>
                                                <td className="text-right">{item.vcnH2 === null ? "-" : item.vcnH2}</td>
                                                <td className="text-right">{item.vcnH3 === null ? "-" : item.vcnH3}</td>
                                                <td className="text-right">{item.vcnH4 === null ? "-" : item.vcnH4}</td>
                                                <td className="text-right">{item.vcnH5 === null ? "-" : item.vcnH5}</td>
                                                <td className="text-right">{item.vcnH6 === null ? "-" : item.vcnH6}</td>
                                                <td className="text-right">{item.vcnH7 === null ? "-" : item.vcnH7}</td>
                                                <td className="text-right">{item.vcnH8 === null ? "-" : item.vcnH8}</td>
                                                <td className="text-right">{item.vcnH9 === null ? "-" : item.vcnH9}</td>
                                                <td className="text-right">{item.vcnH10 === null ? "-" : item.vcnH10}</td>
                                                <td className="text-right">{item.vcnH11 === null ? "-" : item.vcnH11}</td>
                                                <td className="text-right">{item.vcnH12 === null ? "-" : item.vcnH12}</td>
                                                <td className="text-right">{item.vcnH13 === null ? "-" : item.vcnH13}</td>
                                                <td className="text-right">{item.vcnH14 === null ? "-" : item.vcnH14}</td>
                                                <td className="text-right">{item.vcnH15 === null ? "-" : item.vcnH15}</td>
                                                <td className="text-right">{item.vcnH16 === null ? "-" : item.vcnH16}</td>
                                                <td className="text-right">{item.vcnH17 === null ? "-" : item.vcnH17}</td>
                                                <td className="text-right">{item.vcnH18 === null ? "-" : item.vcnH19}</td>
                                                <td className="text-right">{item.vcnH19 === null ? "-" : item.vcnH19}</td>
                                                <td className="text-right">{item.vcnH20 === null ? "-" : item.vcnH20}</td>
                                                <td className="text-right">{item.vcnH21 === null ? "-" : item.vcnH21}</td>
                                                <td className="text-right">{item.vcnH22 === null ? "-" : item.vcnH22}</td>
                                                <td className="text-right">{item.vcnH23 === null ? "-" : item.vcnH23}</td>
                                                <td className="text-right">{item.vcnH24 === null ? "-" : item.vcnH24}</td>
                                                <td className="text-right">{item.vcnH25 === null ? "-" : item.vcnH25}</td>
                                                <td className="text-right">{item.vcnH26 === null ? "-" : item.vcnH26}</td>
                                                <td className="text-right">{item.vcnH27 === null ? "-" : item.vcnH27}</td>
                                                <td className="text-right">{item.vcnH28 === null ? "-" : item.vcnH28}</td>
                                                <td className="text-right">{item.vcnH29 === null ? "-" : item.vcnH29}</td>
                                                <td className="text-right">{item.vcnH30 === null ? "-" : item.vcnH30}</td>
                                                <td className="text-right">{item.vcnH31 === null ? "-" : item.vcnH31}</td>
                                            </tr>
                                            <tr>
                                                <td className="text-center">IA</td>
                                                <td className="text-right">{item.iaH1 === null ? "-" : item.iaH1}</td>
                                                <td className="text-right">{item.iaH2 === null ? "-" : item.iaH2}</td>
                                                <td className="text-right">{item.iaH3 === null ? "-" : item.iaH3}</td>
                                                <td className="text-right">{item.iaH4 === null ? "-" : item.iaH4}</td>
                                                <td className="text-right">{item.iaH5 === null ? "-" : item.iaH5}</td>
                                                <td className="text-right">{item.iaH6 === null ? "-" : item.iaH6}</td>
                                                <td className="text-right">{item.iaH7 === null ? "-" : item.iaH7}</td>
                                                <td className="text-right">{item.iaH8 === null ? "-" : item.iaH8}</td>
                                                <td className="text-right">{item.iaH9 === null ? "-" : item.iaH9}</td>
                                                <td className="text-right">{item.iaH10 === null ? "-" : item.iaH10}</td>
                                                <td className="text-right">{item.iaH11 === null ? "-" : item.iaH11}</td>
                                                <td className="text-right">{item.iaH12 === null ? "-" : item.iaH12}</td>
                                                <td className="text-right">{item.iaH13 === null ? "-" : item.iaH13}</td>
                                                <td className="text-right">{item.iaH14 === null ? "-" : item.iaH14}</td>
                                                <td className="text-right">{item.iaH15 === null ? "-" : item.iaH15}</td>
                                                <td className="text-right">{item.iaH16 === null ? "-" : item.iaH16}</td>
                                                <td className="text-right">{item.iaH17 === null ? "-" : item.iaH17}</td>
                                                <td className="text-right">{item.iaH18 === null ? "-" : item.iaH18}</td>
                                                <td className="text-right">{item.iaH19 === null ? "-" : item.iaH19}</td>
                                                <td className="text-right">{item.iaH20 === null ? "-" : item.iaH20}</td>
                                                <td className="text-right">{item.iaH21 === null ? "-" : item.iaH21}</td>
                                                <td className="text-right">{item.iaH22 === null ? "-" : item.iaH22}</td>
                                                <td className="text-right">{item.iaH23 === null ? "-" : item.iaH23}</td>
                                                <td className="text-right">{item.iaH24 === null ? "-" : item.iaH24}</td>
                                                <td className="text-right">{item.iaH25 === null ? "-" : item.iaH25}</td>
                                                <td className="text-right">{item.iaH26 === null ? "-" : item.iaH26}</td>
                                                <td className="text-right">{item.iaH27 === null ? "-" : item.iaH27}</td>
                                                <td className="text-right">{item.iaH28 === null ? "-" : item.iaH28}</td>
                                                <td className="text-right">{item.iaH29 === null ? "-" : item.iaH29}</td>
                                                <td className="text-right">{item.iaH30 === null ? "-" : item.iaH30}</td>
                                                <td className="text-right">{item.iaH31 === null ? "-" : item.iaH31}</td>
                                            </tr>
                                            <tr>
                                                <td className="text-center">IB</td>
                                                <td className="text-right">{item.ibH1 === null ? "-" : item.ibH1}</td>
                                                <td className="text-right">{item.ibH2 === null ? "-" : item.ibH2}</td>
                                                <td className="text-right">{item.ibH3 === null ? "-" : item.ibH3}</td>
                                                <td className="text-right">{item.ibH4 === null ? "-" : item.ibH4}</td>
                                                <td className="text-right">{item.ibH5 === null ? "-" : item.ibH5}</td>
                                                <td className="text-right">{item.ibH6 === null ? "-" : item.ibH6}</td>
                                                <td className="text-right">{item.ibH7 === null ? "-" : item.ibH7}</td>
                                                <td className="text-right">{item.ibH8 === null ? "-" : item.ibH8}</td>
                                                <td className="text-right">{item.ibH9 === null ? "-" : item.ibH9}</td>
                                                <td className="text-right">{item.ibH10 === null ? "-" : item.ibH10}</td>
                                                <td className="text-right">{item.ibH11 === null ? "-" : item.ibH11}</td>
                                                <td className="text-right">{item.ibH12 === null ? "-" : item.ibH12}</td>
                                                <td className="text-right">{item.ibH13 === null ? "-" : item.ibH13}</td>
                                                <td className="text-right">{item.ibH14 === null ? "-" : item.ibH14}</td>
                                                <td className="text-right">{item.ibH15 === null ? "-" : item.ibH15}</td>
                                                <td className="text-right">{item.ibH16 === null ? "-" : item.ibH16}</td>
                                                <td className="text-right">{item.ibH17 === null ? "-" : item.ibH17}</td>
                                                <td className="text-right">{item.ibH18 === null ? "-" : item.ibH18}</td>
                                                <td className="text-right">{item.ibH19 === null ? "-" : item.ibH19}</td>
                                                <td className="text-right">{item.ibH20 === null ? "-" : item.ibH20}</td>
                                                <td className="text-right">{item.ibH21 === null ? "-" : item.ibH21}</td>
                                                <td className="text-right">{item.ibH22 === null ? "-" : item.ibH22}</td>
                                                <td className="text-right">{item.ibH23 === null ? "-" : item.ibH23}</td>
                                                <td className="text-right">{item.ibH24 === null ? "-" : item.ibH24}</td>
                                                <td className="text-right">{item.ibH25 === null ? "-" : item.ibH25}</td>
                                                <td className="text-right">{item.ibH26 === null ? "-" : item.ibH26}</td>
                                                <td className="text-right">{item.ibH27 === null ? "-" : item.ibH27}</td>
                                                <td className="text-right">{item.ibH28 === null ? "-" : item.ibH28}</td>
                                                <td className="text-right">{item.ibH29 === null ? "-" : item.ibH29}</td>
                                                <td className="text-right">{item.ibH30 === null ? "-" : item.ibH30}</td>
                                                <td className="text-right">{item.ibH31 === null ? "-" : item.ibH31}</td>
                                            </tr>
                                            <tr>
                                                <td className="text-center">IC</td>
                                                <td className="text-right">{item.icH1 === null ? "-" : item.icH1}</td>
                                                <td className="text-right">{item.icH2 === null ? "-" : item.icH2}</td>
                                                <td className="text-right">{item.icH3 === null ? "-" : item.icH3}</td>
                                                <td className="text-right">{item.icH4 === null ? "-" : item.icH4}</td>
                                                <td className="text-right">{item.icH5 === null ? "-" : item.icH5}</td>
                                                <td className="text-right">{item.icH6 === null ? "-" : item.icH6}</td>
                                                <td className="text-right">{item.icH7 === null ? "-" : item.icH7}</td>
                                                <td className="text-right">{item.icH8 === null ? "-" : item.icH8}</td>
                                                <td className="text-right">{item.icH9 === null ? "-" : item.icH9}</td>
                                                <td className="text-right">{item.icH10 === null ? "-" : item.icH10}</td>
                                                <td className="text-right">{item.icH11 === null ? "-" : item.icH11}</td>
                                                <td className="text-right">{item.icH12 === null ? "-" : item.icH12}</td>
                                                <td className="text-right">{item.icH13 === null ? "-" : item.icH13}</td>
                                                <td className="text-right">{item.icH14 === null ? "-" : item.icH14}</td>
                                                <td className="text-right">{item.icH15 === null ? "-" : item.icH15}</td>
                                                <td className="text-right">{item.icH16 === null ? "-" : item.icH16}</td>
                                                <td className="text-right">{item.icH17 === null ? "-" : item.icH17}</td>
                                                <td className="text-right">{item.icH18 === null ? "-" : item.icH18}</td>
                                                <td className="text-right">{item.icH19 === null ? "-" : item.icH19}</td>
                                                <td className="text-right">{item.icH20 === null ? "-" : item.icH20}</td>
                                                <td className="text-right">{item.icH21 === null ? "-" : item.icH21}</td>
                                                <td className="text-right">{item.icH22 === null ? "-" : item.icH22}</td>
                                                <td className="text-right">{item.icH23 === null ? "-" : item.icH23}</td>
                                                <td className="text-right">{item.icH24 === null ? "-" : item.icH24}</td>
                                                <td className="text-right">{item.icH25 === null ? "-" : item.icH25}</td>
                                                <td className="text-right">{item.icH26 === null ? "-" : item.icH26}</td>
                                                <td className="text-right">{item.icH27 === null ? "-" : item.icH27}</td>
                                                <td className="text-right">{item.icH28 === null ? "-" : item.icH28}</td>
                                                <td className="text-right">{item.icH29 === null ? "-" : item.icH29}</td>
                                                <td className="text-right">{item.icH30 === null ? "-" : item.icH30}</td>
                                                <td className="text-right">{item.icH31 === null ? "-" : item.icH31}</td>
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
                    <table className="table tbl-overview ml-0 mr-0" id="no-data" style={{ width: "-webkit-fill-available", display: "none" }}>
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

export default PowerQuality;