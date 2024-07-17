import moment from "moment";
import React, { useEffect, useState } from "react";
import { Link, useHistory, useLocation } from 'react-router-dom';
import CONS from "../../../../../constants/constant";
import ManagerService from "./../../../../../services/ManagerService";
import managerService from './../../../../../services/ManagerService';
import { useTranslation } from "react-i18next";

const $ = window.$;

const ListManager = ({ permissionActions }) => {
    const [managers, setManagers] = useState([]);
    const history = useHistory();
    const location = useLocation();
    const [status, setStatus] = useState(null);
    const { t } = useTranslation();
    const [error, setError] = useState(null);

    const listManager = async () => {
        let response = await managerService.listManager();
        if (response.status === 200) {
            setManagers(response.data);
        }
    }

    const searchManager = async () => {
        let keyword = document.getElementById("keyword").value;
        if (keyword.length > 100) {
            setError(t('validate.manager.DESCRIPTION_MAX_LENGTH'));
        } else if (keyword === "") {
            setError(null);
            listManager();
        } else {
            setError(null);
            let response = await managerService.searchManager(keyword);
            if (response.status === 200) {
                let manager = response.data;
                setManagers(manager);
            }
        }

    }

    const deleteManager = (managerId) => {
        $.confirm({
            type: 'red',
            typeAnimated: true,
            icon: 'fa fa-warning',
            title: t('content.accept'),
            content: t('content.category.manager.list.delete_confirm'),
            buttons: {
                confirm: {
                    text: 'Đồng ý',
                    action: async function () {
                        let { status } = await ManagerService.deleteManager(managerId);
                        if (status === 200) {
                            $.alert({
                                title: 'Thông báo!',
                                content: 'Xóa tỉnh thành thành công!',
                            });
                            listManager();
                        } else {
                            $.alert({
                                title: 'Thông báo!',
                                content: 'Không thể xóa tỉnh thành hiện tại!',
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
                message: t('content.category.manager.add.add_success')
            })
        }
        if (state?.message === "UPDATE_SUCCESS" && state?.status === 200) {
            setStatus({
                code: 200,
                message: t('content.category.manager.add.edit_success')
            })
        }
        // setTimeout(() => {
        //     setStatus(null)
        // }, 5000)
    }

    useEffect(() => {
        if (location.state) {
            setNotification(location.state);
        }
        document.title = t('content.category.manager.list.header');
        listManager();
    }, []);

    return (
        <div id="page-body">
            <div id="main-title">
                <h5 className="d-block mb-0 float-left text-uppercase"><i className="far fa-building"></i> &nbsp;{t('content.category.manager.list.header')}</h5>

                {
                    permissionActions.hasCreatePermission &&
                    <button type="button" className="btn btn-primary btn-rounded btn-new float-right hidden" onClick={() => {
                        history.push("/category/manager/add")
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

            {
                (error != null) &&
                <div className="alert alert-warning" role="alert">
                    <p className="m-0 p-0">{error}</p>
                </div>
            }

            <div id="main-search">
                <div className="input-group search-item mb-3 float-left">
                    <div className="input-group-prepend">
                        <span className="input-group-text" id="inputGroup-sizing-default">{t('content.description')}</span>
                    </div>
                    <input type="text" id="keyword" className="form-control" aria-label="Mô tả" aria-describedby="inputGroup-sizing-sm"
                        onKeyDown={e => e.key === 'Enter' && searchManager()} />
                </div>

                <div className="search-buttons float-left">
                    <button type="button" className="btn btn-outline-secondary" onClick={() => searchManager()}>
                        <i className="fa-solid fa-magnifying-glass"></i>
                    </button>
                </div>
            </div>

            <div id="main-content">
                <table className="table">
                    <thead>
                        <tr>
                            <th width="40px">{t('content.no')}</th>
                            <th width="150px" style={{ wordWrap: "break-word" }}>{t('content.manager')}</th>
                            <th width="150px" style={{ wordWrap: "break-word" }}>{t('content.super_manager')}</th>
                            <th>{t('content.description')}</th>
                            <th width="150px">{t('content.update_date')}</th>
                            <th width="55px"><i className="fa-regular fa-hand"></i></th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            managers.map((manager, index) => {
                                return (
                                    <tr key={manager.managerId}>
                                        <td className="text-center">{index + 1}</td>
                                        <td style={{ wordWrap: "break-word" }}>{manager.managerName}</td>
                                        <td style={{ wordWrap: "break-word" }}>{manager.superManagerName}</td>
                                        <td style={{ wordWrap: "break-word" }}>{manager.description}</td>
                                        <td className="text-center">{moment(manager.updateDate).format(CONS.DATE_FORMAT)}</td>
                                        <td className="text-center">
                                            {
                                                permissionActions.hasUpdatePermission &&
                                                <Link className="button-icon" to={`/category/manager/edit/` + manager.managerId} title="Chỉnh sửa">
                                                    <img height="16px" src="/resources/image/icon-edit.png" alt="Cập nhật" />
                                                </Link>
                                            }
                                            {
                                                permissionActions.hasDeletePermission &&
                                                <Link to="" className="button-icon hidden" title="Xóa" onClick={(e) => {
                                                    e.preventDefault();
                                                    deleteManager(manager.managerId);
                                                }}>
                                                    <img height="16px" src="/resources/image/icon-delete.png" alt="Xóa" />
                                                </Link>
                                            }
                                        </td>
                                    </tr>
                                )
                            })
                        }
                        <tr style={{ display: managers.length === 0 ? "contents" : "none" }}>
                            <td className="text-center" colSpan={6}>{t('content.no_data')}</td>
                        </tr>
                    </tbody>
                </table>

                <div id="main-button" className="text-left">
                    <button type="button" className="btn btn-outline-secondary btn-s3m w-120px" onClick={() => history.push("/")}>
                        <i className="fa-solid fa-house"></i> &nbsp;{t('content.home')}
                    </button>
                </div>
            </div>
        </div>
    )
}

export default ListManager;