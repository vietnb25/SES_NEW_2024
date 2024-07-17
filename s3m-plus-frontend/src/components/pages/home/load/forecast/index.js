import { useFormik } from "formik";
import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import CONS from "../../../../../constants/constant";
import OverviewLoadService from "../../../../../services/OverviewLoadService";
import moment from 'moment';
import './index.css';
import $ from "jquery";
import AuthService from "../../../../../services/AuthService";
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
        projectId: param.projectId,
        systemTypeId: 1,
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
        let res = await OverviewLoadService.getForecast(param.customerId, param.projectId, 1);
        if (res.status === 200 && res.data !== '') {
            setForecast(res.data);
            formik.setFieldValue("id", res.data.id);
            formik.setFieldValue("systemTypeId", res.data.systemTypeId);
            formik.setFieldValue("projectId", res.data.projectId);
            formik.setFieldValue("a0", res.data.a0);
            formik.setFieldValue("a1", res.data.a1);
            formik.setFieldValue("a2", res.data.a2);
            formik.setFieldValue("a3", res.data.a3);
            formik.setFieldValue("a4", res.data.a4);
            formik.setFieldValue("a5", res.data.a5);
            formik.setFieldValue("a6", res.data.a6);
            formik.setFieldValue("a7", res.data.a7);
            formik.setFieldValue("a8", res.data.a8);
            formik.setFieldValue("a9", res.data.a9);
        }
    }

    const getForecasts = async () => {
        let res = await OverviewLoadService.getForecasts(param.customerId, param.projectId, 1, page);
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
        formik.setFieldValue("id", forecast.id);
        formik.setFieldValue("systemTypeId", forecast.systemTypeId);
        formik.setFieldValue("projectId", forecast.projectId);
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
        initialValues: forecast,
        onSubmit: async (data) => {
            let res = await OverviewLoadService.saveForecast(param.customerId, data);
            if (res.status === 200) {
                getForecasts();
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
        getForecasts();
    }, [param.projectId, page]);

    return (
        <>
            <form onSubmit={formik.handleSubmit} id="form-forecast">
                <div className="tab-content">
                    <div className="project-infor" style={{ padding: "0px 10px", display: "block"}}>
                        <span className="project-tree">{projectInfo}</span>
                    </div>
                    <div>
                        <table className="table tbl-overview table-bordered mt-2">
                            <tbody>
                                <tr>
                                    <th>a0Load</th>
                                    <th>a1Load</th>
                                    <th>a2Load</th>
                                    <th>a3Load</th>
                                    <th>a4Load</th>
                                    <th>a5Load</th>
                                    <th>a6Load</th>
                                    <th>a7Load</th>
                                    <th>a8Load</th>
                                    <th>a9Load</th>
                                </tr>
                                <tr>
                                    <td>
                                        <input type="hidden" name="id" id="id" value={formik.values.id} onChange={formik.handleChange} className="form-control" />
                                        <input type="hidden" name="project-id" id="projectId" value={formik.values.projectId} onChange={formik.handleChange} className="form-control" />
                                        <input type="hidden" name="system-type-id" id="systemTypeId" value={formik.values.systemTypeId} onChange={formik.handleChange} className="form-control" />
                                        <input type="number" name="a0" id="a0" value={formik.values.a0} disabled={role === "ROLE_ADMIN" ? false : true} onChange={formik.handleChange} className="form-control" />
                                    </td>
                                    <td>
                                        <input type="number" name="a1" id="a1" value={formik.values.a1} disabled={role === "ROLE_ADMIN" ? false : true} onChange={formik.handleChange} className="form-control" />
                                    </td>
                                    <td>
                                        <input type="number" name="a2" id="a2" value={formik.values.a2} disabled={role === "ROLE_ADMIN" ? false : true} onChange={formik.handleChange} className="form-control" />
                                    </td>
                                    <td>
                                        <input type="number" name="a3" id="a3" value={formik.values.a3} disabled={role === "ROLE_ADMIN" ? false : true} onChange={formik.handleChange} className="form-control" />
                                    </td>
                                    <td>
                                        <input type="number" name="a4" id="a4" value={formik.values.a4} disabled={role === "ROLE_ADMIN" ? false : true} onChange={formik.handleChange} className="form-control" />
                                    </td>
                                    <td>
                                        <input type="number" name="a5" id="a5" value={formik.values.a5} disabled={role === "ROLE_ADMIN" ? false : true} onChange={formik.handleChange} className="form-control" />
                                    </td>
                                    <td>
                                        <input type="number" name="a6" id="a6" value={formik.values.a6} disabled={role === "ROLE_ADMIN" ? false : true} onChange={formik.handleChange} className="form-control" />
                                    </td>
                                    <td>
                                        <input type="number" name="a7" id="a7" value={formik.values.a7} disabled={role === "ROLE_ADMIN" ? false : true} onChange={formik.handleChange} className="form-control" />
                                    </td>
                                    <td>
                                        <input type="number" name="a8" id="a8" value={formik.values.a8} disabled={role === "ROLE_ADMIN" ? false : true} onChange={formik.handleChange} className="form-control" />
                                    </td>
                                    <td>
                                        <input type="number" name="a9" id="a9" value={formik.values.a9} disabled={role === "ROLE_ADMIN" ? false : true} onChange={formik.handleChange} className="form-control" />
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        {(role === "ROLE_ADMIN") &&
                            <div className="mt-2 mr-1" style={{ overflow: "auto" }}>
                                <button type="submit" className="btn btn-s3m w-100px float-right mr-2" style={{ background: "rgb(255, 168, 125)", border: "none", padding: "0px", height: "31.5px", fontSize: "13px" }}>&nbsp;Lưu thay đổi</button>
                                <button type="button" className="btn btn-s3m-m w-100px float-right mr-2" style={{ background: "white", color: "rgb(237, 80, 80)", border: "solid 1px rgb(237, 80, 80)", padding: "0px", height: "31.5px", fontSize: "13px" }} onClick={() => resetValue()}>&nbsp;Hủy</button>
                            </div>
                        }
                        <hr />
                    </div>

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
                                                <th>a0Load</th>
                                                <th>a1Load</th>
                                                <th>a2Load</th>
                                                <th>a3Load</th>
                                                <th>a4Load</th>
                                                <th>a5Load</th>
                                                <th>a6Load</th>
                                                <th>a7Load</th>
                                                <th>a8Load</th>
                                                <th>a9Load</th>
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
            </form>
        </>
    )
}
export default Forecast;