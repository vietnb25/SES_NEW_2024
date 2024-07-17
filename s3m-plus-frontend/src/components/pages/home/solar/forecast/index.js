import { useFormik } from "formik";
import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { useHistory } from "react-router-dom";
import CONS from "../../../../../constants/constant";
import OverviewLoadService from "../../../../../services/OverviewLoadService";
import moment from 'moment';
import './index.css';
import $ from "jquery";
import AuthService from "../../../../../services/AuthService";
import ForecastSolarService from "../../../../../services/ForecastSolarService";
import Pagination from "react-js-pagination";

const Forecast = ({ projectInfo }) => {

    const [role, setRole] = useState("");
    const [page, setPage] = useState(1);
    const [totalPage, setTotalPage] = useState(1);
    const [forecasts, setForecasts] = useState([]);
    const getRole = () => {
        let roleName = AuthService.getRoleName();
        setRole(roleName);
    }

    const param = useParams();

    const [forecast, setForecast] = useState({
        id: "",
        projectId: "",
        systemTypeId: "",
        a0: 0,
        a1: 0,
        a2: 0,
        a3: 0,
        a4: 0,
        a5: 0,
        a6: 0,
        a7: 0,
        a8: 0,
        a9: 1,
        updateDate: ""
    });

    const getForecast = async () => {
        let res = await ForecastSolarService.getForecast(param.customerId, param.projectId, 2);
        if (res.status === 200) {
            setForecast(res.data);

        }
    }

    const getForecastss = async () => {
        let res = await ForecastSolarService.getForecasts(param.customerId, param.projectId, 2, page);
        if (res.status === 200) {
            setForecasts(res.data.data);
            setTotalPage(res.data.totalPage);
        } else {
            setForecasts([]);
        }
    }

    const handlePagination = async page => {
        setPage(page);
    }

    const resetValue = async () => {
        getForecast();
        formik.setFieldValue("a0", forecast.a0);
        formik.setFieldValue("a1", forecast.a1);
        formik.setFieldValue("a2", forecast.a2);
        formik.setFieldValue("a3", forecast.a3);
        formik.setFieldValue("a4", forecast.a4);
        formik.setFieldValue("a5", forecast.a5);
        formik.setFieldValue("a6", forecast.a6);
        formik.setFieldValue("a7", forecast.a7);
        formik.setFieldValue("a8", forecast.a8);
        formik.setFieldValue("a9", forecast.a9);
    }

    const formik = useFormik({
        enableReinitialize: true,
        initialValues: forecast,
        onSubmit: async (data) => {
            let res = await ForecastSolarService.saveForecast(param.customerId, data);
            if (res.status === 200) {
                getForecastss();
                $.alert({
                    title: 'Thông báo',
                    content: 'Lưu thành công!',
                });
            }
        }
    });

    useEffect(() => {
        getRole();
        getForecast();
        getForecastss();
    }, [param.projectId, page]);

    return (
        <div className="tab-content">
            <form onSubmit={formik.handleSubmit} id="form-forecast">
                <div className="project-infor" style={{ padding: "0px 10px", display: "block"}}>
                <span className="project-tree">{projectInfo}</span>
            </div>
                <table className="table tbl-overview table-bordered mt-2">
                    <tbody>
                        <tr>
                            <th>a0PV</th>
                            <th>a1PV</th>
                            <th>a2PV</th>
                            <th>a3PV</th>
                            <th>a4PV</th>
                            <th>a5PV</th>
                            <th>a6PV</th>
                            <th>a7PV</th>
                            <th>a8PV</th>
                            <th>a9PV</th>
                        </tr>
                        <tr>
                            <td>
                                <input type="hidden" name="id" value={formik.values.id} onChange={formik.handleChange} className="form-control" />
                                <input type="hidden" name="projectId" value={formik.values.projectId} onChange={formik.handleChange} className="form-control" />
                                <input type="hidden" name="systemTypeId" value={formik.values.systemTypeId} onChange={formik.handleChange} className="form-control" />
                                <input type="number" name="a0" disabled={role === "ROLE_ADMIN" ? false : true} value={formik.values.a0} onChange={formik.handleChange} className="form-control" />
                            </td>
                            <td>
                                <input type="number" name="a1" value={formik.values.a1} disabled={role === "ROLE_ADMIN" ? false : true} onChange={formik.handleChange} className="form-control" />
                            </td>
                            <td>
                                <input type="number" name="a2" value={formik.values.a2} disabled={role === "ROLE_ADMIN" ? false : true} onChange={formik.handleChange} className="form-control" />
                            </td>
                            <td>
                                <input type="number" name="a3" value={formik.values.a3} disabled={role === "ROLE_ADMIN" ? false : true} onChange={formik.handleChange} className="form-control" />
                            </td>
                            <td>
                                <input type="number" name="a4" value={formik.values.a4} disabled={role === "ROLE_ADMIN" ? false : true} onChange={formik.handleChange} className="form-control" />
                            </td>
                            <td>
                                <input type="number" name="a5" value={formik.values.a5} disabled={role === "ROLE_ADMIN" ? false : true} onChange={formik.handleChange} className="form-control" />
                            </td>
                            <td>
                                <input type="number" name="a6" value={formik.values.a6} disabled={role === "ROLE_ADMIN" ? false : true} onChange={formik.handleChange} className="form-control" />
                            </td>
                            <td>
                                <input type="number" name="a7" value={formik.values.a7} disabled={role === "ROLE_ADMIN" ? false : true} onChange={formik.handleChange} className="form-control" />
                            </td>
                            <td>
                                <input type="number" name="a8" value={formik.values.a8} disabled={role === "ROLE_ADMIN" ? false : true} onChange={formik.handleChange} className="form-control" />
                            </td>
                            <td>
                                <input type="number" name="a9" value={formik.values.a9} disabled={role === "ROLE_ADMIN" ? false : true} onChange={formik.handleChange} className="form-control" />
                            </td>
                        </tr>
                        {(role === "ROLE_ADMIN") &&
                            <tr>
                                <td colSpan={10}>
                                    <button type="submit" className="btn btn-s3m w-100px float-right" style={{ background: "rgb(255, 168, 125)", border: "none", padding: "0px", height: "35px", fontSize: "13px" }}>&nbsp;Lưu thay đổi</button>
                                    <button type="button" className="btn btn-s3m-m w-100px float-right mr-2" style={{ background: "white", color: "rgb(237, 80, 80)", border: "solid 1px rgb(237, 80, 80)", padding: "0px", height: "35px", fontSize: "13px" }} onClick={() => resetValue()}>&nbsp;Hủy</button>
                                </td>
                            </tr>
                        }
                    </tbody>
                </table>
            </form>
            <hr />
            <div style={{ overflow: "auto", height: "73.3%", position: "absolute", width: "-webkit-fill-available" }}>
                <div style={{ width: "-webkit-fill-available" }}>
                    <table className="table tbl-overview tbl-tsnd mt-1">
                        <thead>
                            <tr>
                                <th colSpan={10} className="tbl-title">Lịch sử cài đặt thông số dự báo</th>
                            </tr>
                        </thead>
                    </table>
                </div>

                {
                    forecasts?.length > 0 ?
                        <div>
                            <table className="table tbl-overview tbl-tsnd text-center">
                                <thead style={{ display: "table-header-group", width: "100%", tableLayout: "fixed" }}>
                                    <tr>
                                        <th width="40px">TT</th>
                                        <th width="150px">Thời gian</th>
                                        <th>a0PV</th>
                                        <th>a1PV</th>
                                        <th>a2PV</th>
                                        <th>a3PV</th>
                                        <th>a4PV</th>
                                        <th>a5PV</th>
                                        <th>a6PV</th>
                                        <th>a7PV</th>
                                        <th>a8PV</th>
                                        <th>a9PV</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {
                                        forecasts?.map((item, index) => (
                                            <tr key={index}>
                                                <td>{index + 1}</td>
                                                <td className="text-center">{moment(item.updateDate).format(CONS.DATE_FORMAT)}</td>
                                                <td>{item.a0}</td>
                                                <td>{item.a1}</td>
                                                <td>{item.a2}</td>
                                                <td>{item.a3}</td>
                                                <td>{item.a4}</td>
                                                <td>{item.a5}</td>
                                                <td>{item.a6}</td>
                                                <td>{item.a7}</td>
                                                <td>{item.a8}</td>
                                                <td>{item.a9}</td>
                                            </tr>
                                        ))
                                    }
                                </tbody>
                            </table>
                            <div id="pagination" style={{ marginLeft: "11px" }}>
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

                        <div style={{ width: "-webkit-fill-available" }}>
                            <table className="table tbl-overview tbl-tsnd mt-0">
                                <tbody>
                                    <tr className="w-100">
                                        <td height={30} className="text-center w-100" style={{ border: "none", background: "#D5D6D1" }}> Không có dữ liệu</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                }
            </div>
        </div>
    )
}
export default Forecast;
