import React, { useEffect, useState } from "react";
import { Link, useHistory } from "react-router-dom";
import superManagerService from './../../../../../services/SuperManagerService';
import "./index.css";
import moment from 'moment';
import CONS from "../../../../../constants/constant";
import { useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";


const $ = window.$;

const ListSuperManagers = ({ permissionActions }) => {
    const history = useHistory();
    const [status, setStatus] = useState(null);
    const location = useLocation();
    const [superManagers, setSuperManagers] = useState([]);
    const [errorsValidate, setErrorsValidate] = useState();
    const { t } = useTranslation();

    const listSuperManager = async () => {
        let res = await superManagerService.listSuperManager();
        if (res.status === 200) {
            let superManagers = res.data;
            setSuperManagers(superManagers);
        }
    }

    const searchSuperManager = async () => {

        let keyword = document.getElementById("keyword").value;
        if (keyword.length > 100) {
            setErrorsValidate(t('validate.super_manager.DESCRIPTION_MAX_SIZE_ERROR'));
        } else if (keyword === "") {
            setErrorsValidate(null);
            listSuperManager();
        } else {
            setErrorsValidate(null);
            let res = await superManagerService.searchSuperManager(keyword);
            if (res.status === 400) {
                setErrorsValidate(res.data.errors);
            }
            if (res.status === 200) {
                let superManagers = res.data;
                setSuperManagers(superManagers);
            }
        }
    }

    const deleteSuperManager = (superManagerId) => {
        $.confirm({
            type: 'red',
            typeAnimated: true,
            icon: 'fa fa-warning',
            title: t('content.title_confirm'),
            content: t('content.category.super_manager.list.delete_confirm'),
            buttons: {
                confirm: {
                    text: t('content.yes'),
                    action: async function () {
                        let { status } = await superManagerService.deleteSuperManager(superManagerId);
                        if (status === 200) {
                            $.alert({
                                title: t('content.title_notify'),
                                content: t('content.category.super_manager.list.delete_success'),
                            });
                            listSuperManager();
                        } else {
                            $.alert({
                                title: t('content.title_notify'),
                                content: t('content.category.super_manager.list.delete_error'),
                            });
                        }
                    }
                },
                cancel: {
                    text: t('content.cancel'),
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
                message: t('content.category.super_manager.list.add_success')
            })
        }
        if (state?.message === "UPDATE_SUCCESS" && state?.status === 200) {
            setStatus({
                code: 200,
                message: t('content.category.super_manager.list.edit_success')
            })
        }
    }
    useEffect(() => {
        if (location.state) {
            setNotification(location.state);
        }
        document.title = t('content.category.super_manager.list.title');
        listSuperManager();
    }, [])

    return (
        <div id="page-body">
            <div id="main-title">
                <h5 className="d-block mb-0 float-left text-uppercase"><i className="far fa-compass"></i> &nbsp;{t('content.category.super_manager.list.header')}</h5>

                {
                    permissionActions.hasCreatePermission &&
                    <button type="button" className="btn btn-primary btn-rounded btn-new float-right" onClick={() => {
                        history.push("/category/super-manager/add")
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
                            <div className="alert alert-danger" role="alert">
                                <p className="m-0 p-0">{status?.message}</p>
                            </div>
                    }
                </div>
            }
            {
                errorsValidate != null &&
                <div className="alert alert-warning" role="alert">
                    <p className="m-0 p-0">{errorsValidate}</p>
                </div>
            }
            <div id="main-search">
                <div className="input-group search-item mb-3 float-left">
                    <div className="input-group-prepend">
                        <span className="input-group-text" id="inputGroup-sizing-default">{t('content.description')}</span>
                    </div>
                    <input type="text" id="keyword" className="form-control" aria-label={t('content.description')} aria-describedby="inputGroup-sizing-sm" onKeyDown={e => e.key === 'Enter' && searchSuperManager()} />
                </div>

                <div className="search-buttons float-left">
                    <button type="button" className="btn btn-outline-secondary" onClick={() => searchSuperManager()}>
                        <i className="fa-solid fa-magnifying-glass"></i>
                    </button>
                </div>
            </div>

            <div id="main-content">
                <table className="table">
                    <thead>
                        <tr>
                            <th width="40px">{t('content.no')}</th>
                            <th width="200px">{t('content.category.super_manager.lable_sm_name')}</th>
                            <th>{t('content.description')}</th>
                            <th width="150px">{t('content.update_date')}</th>
                            <th width="55px"><i className="fa-regular fa-hand"></i></th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            superManagers.map((item, index) => (
                                <tr key={index}>
                                    <td className="text-center">{index + 1}</td>
                                    <td>{item.superManagerName}</td>
                                    <td>{item.description}</td>
                                    <td className="text-center">{moment(item.updateDate).format(CONS.DATE_FORMAT)}</td>
                                    <td className="text-center">
                                        {
                                            permissionActions.hasUpdatePermission &&
                                            <Link className="button-icon" to={`/category/super-manager/edit/` + item.superManagerId} title={t('content.title_icon_edit')}>
                                                <img height="16px" src="/resources/image/icon-edit.png" alt="" />
                                            </Link>
                                        }
                                        {
                                            permissionActions.hasDeletePermission &&
                                            <Link className="button-icon" title={t('content.title_icon_delete')} onClick={(e) => {
                                                e.preventDefault();
                                                deleteSuperManager(item.superManagerId)
                                            }}>
                                                <img height="16px" src="/resources/image/icon-delete.png" alt="" />
                                            </Link>
                                        }
                                    </td>
                                </tr>
                            ))
                        }
                        <tr style={{ display: superManagers.length === 0 ? "contents" : "none" }}>
                            <td className="text-center" colSpan={5}>{t('content.no_data')}</td>
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

export default ListSuperManagers;