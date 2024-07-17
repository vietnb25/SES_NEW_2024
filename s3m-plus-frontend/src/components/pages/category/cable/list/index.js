import moment from "moment";
import React, { useEffect, useState } from "react";
import { useHistory, Link, useLocation } from "react-router-dom";
import cablesService from "../../../../../services/CablesService";
import $ from 'jquery';
import 'jquery-confirm';
import CONS from "../../../../../constants/constant";
import { useTranslation } from "react-i18next";
import authService from "../../../../../services/AuthService";

const ListCable = ({ permissionActions }) => {
    let history = useHistory();
    const location = useLocation();
    const [cables, setCables] = useState([]);
    const [status, setStatus] = useState(null);
    const { t } = useTranslation();
    const [error, setError] = useState(null);

    const listCable = async () => {
        let res = await cablesService.listCable();
        if (res.status === 200) {
            let cables = res.data;
            setCables(cables);
        }
    }

    const searchCables = async () => {
        let keyword = document.getElementById("keyword").value;
        // console.log(keyword);
        if (keyword.length > 100) {
            setError(t('validate.cable.DESCRIPTION_MAX_LENGTH'));
        } else if (keyword === "") {
            setError(null);
            listCable();
        } else {
            setError(null);
            let response = await cablesService.searchCable(keyword);
            if (response.status === 200) {
                let cables = response.data;
                setCables(cables);
            }
        }
    }

    const deleteCable = async (cableId) => {
        $.confirm({
            type: 'red',
            typeAnimated: true,
            icon: 'fa fa-warning',
            title: 'Xác nhận!',
            content: 'Bạn có chắc chắn muốn xóa cáp này không?',
            buttons: {
                confirm: {
                    text: 'Đồng ý',
                    action: async function () {
                        let { status } = await cablesService.deleteCable(cableId);
                        if (status === 200) {
                            $.alert({
                                title: 'Thông báo!',
                                content: 'Xóa cáp thành công!',
                            });
                            listCable();
                        } else {
                            $.alert({
                                title: 'Thông báo!',
                                content: 'Không thể xóa cáp hiện tại!',
                            });
                        }
                    }
                },
                cancel: {
                    text: 'Hủy bỏ',
                    action: function () {

                    }
                }
            }
        });
    }

    const setNotification = (state) => {
        if (state?.message === "INSERT_SUCCESS" && state?.status === 200) {
            setStatus({
                code: 200,
                message: "Thêm mới cáp thành công!"
            })
        }
        if (state?.message === "UPDATE_SUCCESS" && state?.status === 200) {
            setStatus({
                code: 200,
                message: "Chỉnh sửa cáp thành công!"
            })
        }
        // setTimeout(() => {
        //     setStatus(null)
        // }, 5000)
    }
    const [role, setRole] = useState("");
    const getRole = () => {
        let roleName = authService.getRoleName();
        setRole(roleName);

    }

    useEffect(() => {
        if (location.state) {
            setNotification(location.state);
        }
        document.title = t('content.list_cable');
        listCable();
        getRole();
    }, [])

    return (
        <div id="page-body">
            <div id="main-title">
                <h5 className="d-block mb-0 float-left"><i className="fas fa-lines-leaning"></i> &nbsp;{t('content.list_cable')}</h5>
                {
                    permissionActions.hasCreatePermission &&
                    <button type="button" className="btn btn-primary btn-rounded btn-new float-right" onClick={() => {
                        history.push("/category/cables/add")
                    }}>
                        <i className="fa-solid fa-plus"></i>
                    </button>
                }
            </div>

            {
                (error != null) &&
                <div className="alert alert-warning" role="alert">
                    <p className="m-0 p-0">{error}</p>
                </div>
            }
            {
                status != null &&
                <div>
                    {
                        status.code === 200 ?
                            <div className="alert alert-success alert-dismissible fade show" role="alert">
                                <p className="m-0 p-0">{status?.message}</p>
                                {/* <button type="button" className="close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button> */}
                            </div> :
                            <div className="alert alert-danger" role="alert">
                                <p className="m-0 p-0">{status?.message}</p>
                            </div>
                    }
                </div>
            }

            <div id="main-search">

                <div className="input-group search-item mb-3 float-left">
                    <div className="input-group-prepend">
                        <span className="input-group-text" id="inputGroup-sizing-default">{t('content.description')}</span>
                    </div>
                    <input type="text" id="keyword" className="form-control" aria-label="Mô tả" aria-describedby="inputGroup-sizing-sm"
                        onKeyDown={e => e.key === 'Enter' && searchCables()} />
                </div>

                <div className="search-buttons float-left">
                    <button type="button" className="btn btn-outline-secondary" onClick={() => searchCables()}>
                        <i className="fa-solid fa-magnifying-glass"></i>
                    </button>
                </div>
            </div>

            <div id="main-content">
                <table className="table">
                    <tbody>
                        <tr>
                            <th width="40px">{t('content.no')}</th>
                            <th width="300px" style={{ wordWrap: "break-word" }}>{t('content.cable_name')} </th>
                            <th width="150px">{t('content.current')} </th>
                            <th>{t('content.description')}</th>
                            <th width="150px">{t('content.update_date')}</th>
                            {(role === "ROLE_ADMIN" || role === "ROLE_MOD") &&
                                <th width="55px"><i className="fa-regular fa-hand"></i></th>
                            }
                        </tr>
                        {
                            cables.map((cable, index) => {
                                return (
                                    <tr key={cable.cableId}>
                                        <td className="text-center">{index + 1}</td>
                                        <td>{cable.cableName}</td>
                                        <td>{cable.current}</td>
                                        <td>{cable.description}</td>
                                        <td className="text-center">{moment(cable.updateDate).format(CONS.DATE_FORMAT)}</td>
                                        <td className="text-center">
                                            {
                                                permissionActions.hasUpdatePermission &&
                                                <Link className="button-icon" to={'/category/cables/edit/' + cable.cableId} title="Chỉnh sửa">
                                                    <img height="16px" src="/resources/image/icon-edit.png" alt="edit" />
                                                </Link>
                                            }
                                            {
                                                permissionActions.hasDeletePermission &&
                                                <Link to="/" className="button-icon" title="Xóa" onClick={(e) => {
                                                    e.preventDefault();
                                                    deleteCable(cable.cableId);
                                                }}>
                                                    <img height="16px" src="/resources/image/icon-delete.png" alt="delete" />
                                                </Link>
                                            }
                                        </td>
                                    </tr>
                                )
                            })
                        }
                        <tr style={{ display: cables.length === 0 ? "contents" : "none" }}>
                            <td className="text-center" colSpan={6}>{t('content.no_data')}</td>
                        </tr>
                    </tbody>
                </table>

                <div id="main-button" className="text-left">
                    <button type="button" className="btn btn-outline-secondary btn-s3m w-150px" onClick={() => {
                        history.push("/")
                    }}>
                        <i className="fa-solid fa-house"></i> &nbsp;{t('content.home')}
                    </button>
                </div>
            </div>
        </div>
    )
}

export default ListCable;