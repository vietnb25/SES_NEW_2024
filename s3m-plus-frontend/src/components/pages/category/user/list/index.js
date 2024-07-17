import React, { useEffect, useState } from "react";
import { Link, useHistory } from "react-router-dom";
import userService from './../../../../../services/UserService';
import "./index.css";
import moment from 'moment';
import { useTranslation } from "react-i18next";
import AuthService from "../../../../../services/AuthService";
import useAppStore from "../../../../../applications/store/AppStore";
import { useLocation } from "react-router-dom/cjs/react-router-dom.min";

const $ = window.$;

const ListUsers = ({ permissionActions }) => {
    const history = useHistory();
    const [users, setUsers] = useState([]);
    const { t } = useTranslation();
    const [role] = useState(AuthService.getRoleName());
    const location = useLocation();
    const [status, setStatus] = useState(null);
    const appUserData = useAppStore(state => state.appUserData);
    const userTreeData = useAppStore(state => state.userTreeData);

    const unlockUser = (userId) => {
        $.confirm({
            type: 'red',
            typeAnimated: true,
            icon: 'fa fa-warning',
            title: t('content.confirm'),
            content: t('content.category.user.list.unlock_confirm'),
            buttons: {
                confirm: {
                    text: 'Đồng ý',
                    action: async function () {
                        let { status } = await userService.unlockUser(userId);
                        if (status === 200) {
                            $.alert({
                                title: t('content.notification'),
                                content: t('content.category.user.list.unlock_success')
                            });
                            listUser();
                        } else {
                            $.alert({
                                title: t('content.notification'),
                                content: t('content.category.user.list.error')
                            });
                        }
                    }
                },
                cancel: {
                    text: t('content.cancel'),
                    action: function () { }
                }
            }
        });
    }

    const lockUser = (userId) => {
        $.confirm({
            type: 'red',
            typeAnimated: true,
            icon: 'fa fa-warning',
            title: t('content.confirm'),
            content: t('content.category.user.list.lock_confirm'),
            buttons: {
                confirm: {
                    text: t('content.accept'),
                    action: async function () {
                        let { status } = await userService.lockUser(userId);
                        if (status === 200) {
                            $.alert({
                                title: t('content.notification'),
                                content: t('content.category.user.list.lock_success')
                            });
                            listUser();
                        } else {
                            $.alert({
                                title: t('content.notification'),
                                content: t('content.category.user.list.error')
                            });
                        }
                    }
                },
                cancel: {
                    text: t('content.cancel'),
                    action: function () { }
                }
            }
        });
    }

    const listUser = async () => {
        if (role === "ROLE_ADMIN") {
            let res = await userService.listUser();

            if (res.status === 200) {
                let users = res.data;
                setUsers(users);
            }
        } else {
            let customerIds = [];

            if (userTreeData && userTreeData.length > 0) {
                userTreeData.forEach(item => {
                    if (item.type === "customer") {
                        customerIds.push(item.data.customerId);
                    }
                });
            }

            let res = await userService.userByCustomerIds(customerIds.toString(), appUserData.userId);

            if (res.status === 200) {
                let users = res.data;
                setUsers(users);
            }
        }
    }

    const searchUser = async () => {
        let keyword = document.getElementById("keyword").value
        if (keyword === "") {
            listUser();
        } else {
            if (role === "ROLE_ADMIN") {
                let res = await userService.searchUser(keyword, null, appUserData.userId);
                if (res.status === 200) {
                    let users = (res.data && res.data.length) > 0 ? res.data : [];
                    setUsers(users);
                } else {
                    $.alert({
                        title: t('content.notification'),
                        content: t('content.category.user.list.error')
                    });
                }
            } else {
                let customerIds = [];

                if (userTreeData && userTreeData.length > 0) {
                    userTreeData.forEach(item => {
                        if (item.type === "customer") {
                            customerIds.push(item.data.customerId);
                        }
                    });
                }

                let res = await userService.searchUser(keyword, customerIds.toString(), appUserData.userId);

                if (res.status === 200) {
                    let users = (res.data && res.data.length) > 0 ? res.data : [];
                    setUsers(users);
                } else {
                    $.alert({
                        title: t('content.notification'),
                        content: t('content.category.user.list.error')
                    });
                }
            }

        }
    }

    const setNotification = state => {
        if (state?.status === 200 && state?.message === "INSERT_SUCCESS") {
            setStatus({
                code: 200,
                message: t('content.category.user.list.add_success')
            });
        } else if (state?.status === 200 && state?.message === "UPDATE_SUCCESS") {
            setStatus({
                code: 200,
                message: t('content.category.user.list.edit_success')
            });
        }
        setTimeout(() => {
            setStatus(null);
        }, 3000);
    };

    const deleteUser = (userId) => {
        $.confirm({
            type: 'red',
            typeAnimated: true,
            icon: 'fa fa-warning',
            title: t('content.confirm'),
            content: t('content.category.user.list.delete_confirm'),
            buttons: {
                confirm: {
                    text: t('content.accept'),
                    action: async function () {
                        let { status } = await userService.deleteUser(userId);
                        if (status === 200) {
                            $.alert({
                                title: t('content.notification'),
                                content: t('content.category.user.list.delete_success')
                            });
                            listUser();
                        } else {
                            $.alert({
                                title: t('content.notification'),
                                content: t('content.category.user.list.delete_error')
                            });
                        }
                    }
                },
                cancel: {
                    text: t('content.cancel'),
                    action: function () { }
                }
            }
        });
    }

    useEffect(() => {
        if (location.state) {
            setNotification(location.state);
        };
        document.title = t('content.list_account');
        listUser();
    }, [location]);

    return (
        <div id="page-body">
            <div id="main-title">
                <h5 className="d-block mb-0 float-left"><i className="far fa-circle-user"></i> &nbsp;{t('content.list_account')}</h5>

                {
                    permissionActions.hasCreatePermission &&
                    <button type="button" className="btn btn-primary btn-rounded btn-new float-right" onClick={() => history.push("/category/users/add")}>
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
                        <span className="input-group-text" id="inputGroup-sizing-default">{t('content.description')}</span>
                    </div>
                    <input type="text" id="keyword" className="form-control" aria-label="Mô tả" aria-describedby="inputGroup-sizing-sm"
                        onKeyDown={e => e.key === 'Enter' && searchUser()} />
                </div>

                <div className="search-buttons float-left">
                    <button type="button" className="btn btn-outline-secondary" onClick={() => searchUser()}>
                        <i className="fa-solid fa-magnifying-glass"></i>
                    </button>
                </div>
            </div>

            <div id="main-content">
                <table className="table">
                    <thead>
                        <tr>
                            <th width="40px">{t('content.no')}</th>
                            <th width="350px">{t('content.staff_name')}</th>
                            <th width="150px">{t('content.category.user.type')}</th>
                            <th width="">{t('content.category.user.manage')}</th>
                            <th width="150px">{t('content.username')}</th>
                            <th width="150px">{t('content.update_date')}</th>
                            <th width="120px"><i className="fa-regular fa-hand"></i></th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            users.length > 0 ? users.map((user, index) => {
                                return (user.id !== appUserData.userId) && (
                                    <tr key={user.id}>
                                        <td className="text-center">{index + 1}</td>
                                        <td>{user.staffName}</td>
                                        <td>{(user.userType === 1 || user.userType === 2) ? "Khách hàng" : user.userType === 3 ? "Dự án" : "EVN"}</td>
                                        <td>
                                            {((user.userType === 1 || user.userType === 2) && user.customerName) ? user.customerName.substring(0, user.customerName.length - 2) : (
                                                user.targetManager ? user.targetManager : "-"
                                            )}
                                        </td>
                                        <td>{user.username}</td>
                                        <td>{moment(user.updateDate).format("YYYY-MM-DD hh:mm:ss")}</td>
                                        <td className="text-center">
                                            {
                                                permissionActions.hasUpdatePermission &&
                                                <Link className="button-icon mr-1" to={`/category/users/` + user.id + `/edit`} title="Chỉnh sửa">
                                                    <img height="16px" src="/resources/image/icon-edit.png" alt="Cập nhật" />
                                                </Link>
                                            }
                                            {
                                                permissionActions.hasDeletePermission &&
                                                <Link to={"/"} className="button-icon mr-1" title="Xóa" onClick={(e) => {
                                                    e.preventDefault();
                                                    deleteUser(user.id);
                                                }}>
                                                    <img height="16px" src="/resources/image/icon-delete.png" alt="Xóa" />
                                                </Link>
                                            }
                                            {
                                                permissionActions.hasUpdatePermission && (
                                                    user.lockFlag === 1 ?
                                                        <Link className="button-icon mr-1" to="/" title="Mở khóa tài khoản" onClick={(e) => {
                                                            e.preventDefault();
                                                            unlockUser(user.id);
                                                        }}>
                                                            <img height="16px" src="/resources/image/icon-unlock.png" alt="Mở khóa tài khoản" />
                                                        </Link> :
                                                        <Link className="button-icon mr-1" to="/" title="Khóa tài khoản" onClick={(e) => {
                                                            e.preventDefault();
                                                            lockUser(user.id);
                                                        }}>
                                                            <img height="16px" src="/resources/image/icon-lock.png" alt="Khóa tài khoản" />
                                                        </Link>
                                                )
                                            }

                                            {/* {
                                                (role === "ROLE_ADMIN" || appUserData.authorized === 1) && (
                                                    <Link className="button-icon mr-1" to={`/permission/` + user.id} title="Phân quyền">
                                                        <i className="fa fa-wrench"></i>
                                                    </Link>
                                                )
                                            } */}

                                        </td>
                                    </tr>
                                )
                            }) :
                                <tr>
                                    <td className="text-center" colSpan={7}>{t('content.no_data')}</td>
                                </tr>
                        }
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

export default ListUsers;