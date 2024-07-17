import React, { useState, useEffect } from "react";
import { useHistory, Link, useLocation } from "react-router-dom";
import customerService from "../../../../../services/CustomerService";
import ProjectService from "../../../../../services/ProjectService";
import authService from "../../../../../services/AuthService";
import "./index.css";
import moment from "moment/moment";
import $ from "jquery";
import CONS from "../../../../../constants/constant";
import { useFormik } from "formik";
import { t } from "i18next";



const ListCusomer = ({ permissionActions }) => {
    const history = useHistory();
    const location = useLocation();
    const [listCustomer, setListCustomer] = useState([]);
    const [status, setStatus] = useState(null);
    const [projects, setProject] = useState();
    const [minutes, setMinutes] = useState(5);
    const [seconds, setSeconds] = useState(0);
    const [otpData, setOtpData] = useState('');
    const [customerId, setCustomerId] = useState();
    const [errorOtp, setErrorOtp] = useState();

    const list = async () => {
        let res = await customerService.getListCustomer();
        if (res.status === 200) {
            setListCustomer(res.data);
        }
    };

    const searchCustomer = async () => {
        let keyword = document.getElementById("keyword").value;
        if (keyword === "") {
            list();
            setStatus(null);
        } else if (keyword.length > 100) {
            setStatus({
                message: "Từ khóa không quá 100 ký tự"
            });

        } else {
            let res = await customerService.searchCustomer(keyword);
            if (res.status === 200) {
                setListCustomer(res.data);
            }
            setStatus(null);
        }
    };

    const setNotification = state => {
        if (state?.status === 200 && state?.message === "INSERT_SUCCESS") {
            setStatus({
                code: 200,
                message: "Thêm mới khách hàng thành công"
            });
        } else if (state?.status === 200 && state?.message === "UPDATE_SUCCESS") {
            setStatus({
                code: 200,
                message: "Chỉnh sửa khách hàng thành công"
            });
        }
        setTimeout(() => {
            setStatus(null);
        }, 3000);
    };

    const getListProject = async (customerId) => {
        let res = await ProjectService.getProjectByCustomerId(customerId);
        if (res.status === 200) {
            setProject(res.data);
        }
    };

    const getOtp = async (customerId) => {
        let userName = authService.getUserName();
        let res = await customerService.getOtp(userName, customerId);
        return res.status;
    };

    const deleteCustomer = async (customerId) => {
        let userName = authService.getUserName();
        let otpData = {
            userName: userName,
            customerId: customerId,
            otpCode: document.getElementById("otp-code").value
        }
        let res = await customerService.checkOtp(otpData);
        if (res.status === 200) {
            $.alert({
                title: 'Thông báo!',
                content: 'Đã xóa khách hàng thành công!',
            });
        } else if (res.status === 500 && res.data === 400) {
            setErrorOtp("OTP không chính xác!")
        }
        list();
    };

    const expireOTP = async () => {
        let userName = authService.getUserName();
        let otpData = {
            userName: userName,
            customerId: customerId,
            otpCode: document.getElementById("otp-code").value
        }
        let res = await customerService.expireOtp(otpData);
        return res.data;
    }

    useEffect(() => {

        const interval = setInterval(() => {
            if (seconds > 0) {
                setSeconds(seconds - 1);
            }
            if (seconds === 0) {
                if (minutes === 0) {
                    expireOTP();
                    clearInterval(interval);
                } else {
                    setSeconds(59);
                    setMinutes(minutes - 1);
                }
            }
        }, 1000);

        return () => {
            clearInterval(interval);
        };
    }, [minutes, seconds]);

    useEffect(() => {
        if (location.state) {
            setNotification(location.state);
        };
        document.title = t('content.category.customer.customer_list');
        list();
    }, [location])

    return (
        <div id="page-body">
            <div id="main-title">
                <h5 className="d-block mb-0 float-left text-uppercase"><i className="fas fa-user-tie"></i> &nbsp;{t('content.category.customer.customer_list')}</h5>
                {
                    permissionActions.hasCreatePermission &&
                    <button type="button" className="btn btn-primary btn-rounded btn-new float-right" onClick={() => {
                        history.push("/category/customer/add")
                    }}>
                        <i className="fa-solid fa-plus"></i>
                    </button>
                }
            </div>

            {
                status != null &&
                <div>
                    {
                        status.code === 200 ?
                            <div className="alert alert-success alert-dismissible fade show" role="alert">
                                <p className="m-0 p-0">{status?.message}</p>
                            </div> :
                            <div className="alert alert-warning" role="alert">
                                <p className="m-0 p-0">{status?.message}</p>
                            </div>
                    }
                </div>

            }

            <div id="main-search">
                <div className="input-group search-item mb-3 float-left">
                    <div className="input-group-prepend">
                        <span className="input-group-text" id="inputGroup-sizing-default">{t('content.category.customer.description')}</span>
                    </div>
                    <input type="text" id="keyword" className="form-control" aria-label={t('content.category.customer.description')} aria-describedby="inputGroup-sizing-sm" onKeyDown={e => e.key === "Enter" && searchCustomer(e)} />
                </div>

                <div className="search-buttons float-left">
                    <button type="button" className="btn btn-outline-secondary" onClick={() => searchCustomer()}>
                        <i className="fa-solid fa-magnifying-glass"></i>
                    </button>
                </div>
            </div>

            <div id="main-content">
                <table className="table">
                    <thead>
                        <tr>
                            <th width="40px">{t('content.no')}</th>
                            <th width="150px">{t('content.category.customer.customer_id')}</th>
                            <th width="300px">{t('content.category.customer.customer_name')}</th>
                            <th>{t('content.category.customer.description')}</th>
                            <th width="150px">{t('content.category.customer.update_date')}</th>
                            <th width="55px"><i className="fa-regular fa-hand"></i></th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            listCustomer.map(
                                (customer, index) => <tr key={customer.customerId}>
                                    <td className="text-center">{index + 1}</td>
                                    <td style={{ wordWrap: "break-word" }}>{customer.customerCode}</td>
                                    <td style={{ wordWrap: "break-word" }}>{customer.customerName}</td>
                                    <td style={{ wordWrap: "break-word" }}>{customer.description}</td>
                                    <td className="text-center" style={{ wordWrap: "break-word" }}>{moment(customer.updateDate).format(CONS.DATE_FORMAT)}</td>
                                    <td className="text-center">
                                        {
                                            permissionActions.hasUpdatePermission &&
                                            <Link className="button-icon" to={'/category/customer/edit/' + customer.customerId} title="Chỉnh sửa">
                                                <img height="16px" src="/resources/image/icon-edit.png" alt="chỉnh sửa" />
                                            </Link>
                                        }
                                        {
                                            permissionActions.hasDeletePermission &&
                                            <Link to="/" className="button-icon" data-toggle="modal" data-target={"#model-" + (index + 1)} data-backdrop="static" data-keyboard="false" title="Xóa" onClick={(e) => {
                                                e.preventDefault();
                                                getListProject(customer.customerId);
                                                getOtp(customer.customerId);
                                                setCustomerId(customer.customerId);
                                                setMinutes(5);
                                                setSeconds(0);
                                                setOtpData(null);
                                            }}>
                                                <img height="16px" src="/resources/image/icon-delete.png" alt="xóa" />
                                            </Link>
                                        }
                                        <div className="modal fade" id={"model-" + (index + 1)} tabIndex="-1" role="dialog" aria-labelledby="exampleModalLabel"
                                            aria-hidden="true">
                                            <div className="modal-dialog modal-lg" role="document">
                                                <div className="modal-content">
                                                    <div className="modal-header" style={{ backgroundColor: "#052274", height: "44px" }}>
                                                        <h5 className="modal-title" id="myModalLabel" style={{ color: "white", fontSize: "15px", fontWeight: "bold" }}>{customer.customerName}</h5>
                                                    </div>
                                                    <div className="modal-body">
                                                        <table className="table text-center tbl-overview table-oper-info-tool">
                                                            <thead style={projects?.length >= 5 ? { display: "table", width: "calc(100% - 15px)", tableLayout: "fixed" } : { display: "table", width: "100%", tableLayout: "fixed" }}>
                                                                <tr>
                                                                    <th width="50px">{t('content.no')}</th>
                                                                    <th width="150px">{t('content.category.customer.project_name')}</th>
                                                                </tr>
                                                            </thead>
                                                            <tbody style={{ display: "block", maxHeight: "150px", overflow: "auto" }}>
                                                                {
                                                                    projects?.map(
                                                                        (project, tt) =>
                                                                            <tr key={project.projectId} style={{ display: "table", width: "100%", tableLayout: "fixed" }}>
                                                                                <td width="50px">{tt + 1}</td>
                                                                                <td width="150px">{project.projectName}</td>
                                                                            </tr >
                                                                    )
                                                                }
                                                            </tbody>
                                                        </table>
                                                        <div>
                                                            <span>
                                                                {t('content.category.customer.confirm_delete')}
                                                                <br />{t('content.category.customer.otp')}
                                                            </span>
                                                        </div>
                                                        <div className="time-countdown">
                                                            {seconds > 0 || minutes > 0 ? (
                                                                <h1>
                                                                    {minutes < 10 ? `0${minutes}` : minutes}:
                                                                    {seconds < 10 ? `0${seconds}` : seconds}
                                                                </h1>
                                                            ) : (
                                                                <h1>{t('content.category.customer.otp_expired')}</h1>
                                                            )}
                                                        </div>
                                                        <div className="insert-otp text-center">
                                                            <input type="text" id="otp-code" name="optData" className="form-control" style={{ width: "200px", display: "initial" }} value={otpData} onChange={e => { setOtpData(e.target.value); setErrorOtp(null) }}></input>
                                                        </div>
                                                        {
                                                            errorOtp != null &&
                                                            <div className="mt-3">
                                                                <span>{errorOtp}</span>
                                                            </div>
                                                        }
                                                    </div>
                                                    <div className="modal-footer">
                                                        <button type="button" className="btn btn-outline-primary" onClick={() => { $('#model-' + (index + 1)).hide(); expireOTP() }}>{t('content.close')}</button>
                                                        <button className="btn btn-primary" onClick={() => { deleteCustomer(customer.customerId) }}>{t('content.accept')}</button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                            )
                        }
                        <tr style={{ display: listCustomer.length === 0 ? "contents" : "none" }}>
                            <td className="text-center" colSpan={6}>{t('content.no_data')}</td>
                        </tr>

                    </tbody>

                </table>
                {/* <div className="text-center" style={{display: listCustomer.length === 0 ? 'block' : 'none'}}>
                    <p>No data</p>
                </div> */}
                <div id="main-button" className="text-left">
                    <button type="button" className="btn btn-outline-secondary btn-s3m w-120px" onClick={() => history.push("/")}>
                        <i className="fa-solid fa-house"></i> &nbsp;{t('content.home')}
                    </button>
                </div>
            </div>
        </div>
    )
}

export default ListCusomer;