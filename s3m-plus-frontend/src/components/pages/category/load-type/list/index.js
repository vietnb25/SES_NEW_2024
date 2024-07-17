import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, Link, useLocation } from "react-router-dom";
import ProjectService from "../../../../../services/ProjectService";
import CustomerService from "../../../../../services/CustomerService";
import { useEffect } from "react";
import './index.css'
import ReactModal from "react-modal";
import $ from "jquery";
import LoadTypeService from "../../../../../services/LoadTypeService";

const ListLoadType = (permissionActions) => {

    const history = useHistory();
    const [loadTypes, setLoadType] = useState([]);
    const [status, setStatus] = useState(null);
    const [errorsValidate, setErrorsValidate] = useState();
    const { t } = useTranslation();
    const location = useLocation();

    const setNotification = state => {
        if (state?.status === 200 && state?.message === "INSERT_SUCCESS" && state?.status !== -1) {
            setStatus({
                code: 200,
                message: t('content.category.load_type.list.add_success')
            });
        } else if (state?.status === 200 && state?.message === "UPDATE_SUCCESS" && state?.status !== -1) {
            setStatus({
                code: 200,
                message: t('content.category.load_type.list.edit_success')
            });
        }
        setTimeout(() => {
            setStatus(null);
        }, 3000);
    };

    const loadListTypeLoad = async () => {
        let res = await LoadTypeService.searchLoadType("");
        if (res.status === 200) {
            setLoadType(res.data);
        }
    }

    const searchDevice = async () => {
        let keyword = document.getElementById("keyword").value;
        if (keyword.length > 100) {
            setErrorsValidate(t('validate.super_manager.DESCRIPTION_MAX_SIZE_ERROR'));
        } else if (keyword === "") {
            setErrorsValidate(null);
            let res = await LoadTypeService.searchLoadType(keyword);
            if (res.status === 400) {
                setErrorsValidate(res.data.errors);
            }
            if (res.status === 200) {
                let loadTypes = res.data;
                setLoadType(loadTypes);
            }
        } else {
            setErrorsValidate(null);
            let res = await LoadTypeService.searchLoadType(keyword);
            if (res.status === 400) {
                setErrorsValidate(res.data.errors);
            }
            if (res.status === 200) {
                let loadTypes = res.data;
                setLoadType(loadTypes);
            }
        }
    }
    const sortDevice = (type, ls) => {
        if (type === 1) {
            loadTypes.sort((a, b) => {
                if (sort == false) {
                    return a.loadTypeId < b.loadTypeId ? -1 : a.loadTypeId > b.loadTypeId ? 1 : 0;
                } else {
                    return a.loadTypeId > b.loadTypeId ? -1 : a.loadTypeId < b.loadTypeId ? 1 : 0;
                }
            });
            setSort(!sort)
        }
        if (type === 2) {
            if (sort == false) {
                loadTypes.sort((a, b) => {
                    const nameA = a.loadTypeName.toUpperCase();
                    const nameB = b.loadTypeName.toUpperCase();
                    if (nameA < nameB) {
                        return -1;
                    }
                    if (nameA > nameB) {
                        return 1;
                    }
                });
            } else {
                loadTypes.sort((a, b) => {
                    const nameA = a.loadTypeName.toUpperCase();
                    const nameB = b.loadTypeName.toUpperCase();
                    if (nameA > nameB) {
                        return -1;
                    }
                    if (nameA < nameB) {
                        return 1;
                    }
                });
            }
            setSort(!sort);
        }
    }
    const [sort, setSort] = useState(false);

    const deleteLoadType = (id) => {
        $.confirm({
            type: 'red',
            typeAnimated: true,
            icon: 'fa fa-warning',
            title: t('content.category.load_type.list.label_confirm'),
            content: t('content.category.load_type.list.delete_confirm'),
            buttons: {
                confirm: {
                    text: t('content.category.load_type.list.label_ok'),
                    action: async () => {
                        let resCheck = await LoadTypeService.checkLoadTypeDevice(id);
                        console.log("resCheck", resCheck);
                        if (resCheck.data.length > 0) {
                            $.alert({
                                title: t('content.notification'),
                                content: t('content.category.load_type.list.delete_error'),
                            });
                        } else {
                            let response = await LoadTypeService.deleteLoadTypeById(id);
                            if (response.status === 200) {
                                $.alert({
                                    title: t('content.notification'),
                                    content: t('content.category.load_type.list.delete_success')
                                });
                            } else {
                                $.alert({
                                    type: 'red',
                                    title: t('content.notification'),
                                    content: 'Lỗi không xác định!'
                                });
                            }
                            loadListTypeLoad();
                        }
                    }
                },
                cancel: {
                    text: t('content.category.load_type.list.label_cancel')
                }
            }

        })
    }
    document.title = t('content.list_load_type')
    useEffect(() => {
        loadListTypeLoad()
        if (location.state) {
            setNotification(location.state);
        };
    }, [sort]);

    return (
        <div id="page-body" className="load_type">
            <div id="main-title">
                <h5 className="d-block mb-0 float-left"><i className="fas fa-server"></i> &nbsp;{t('content.category.load_type.list.header')}</h5>
                {
                    permissionActions.permissionActions.hasCreatePermission == true &&
                    <button type="button" className="btn btn-primary btn-rounded btn-new float-right" title={t('content.category.load_type.list.lable_add')} onClick={() => {
                        history.push("/category/load-type/add");
                    }}>
                        <i className="fa-solid fa-plus"></i>
                    </button>
                }
            </div>

            {
                errorsValidate != null &&
                <div className="alert alert-warning" role="alert">
                    <p className="m-0 p-0">{errorsValidate}</p>
                </div>
            }
            <div id="main-search">
                <div className="input-group search-item mb-3 float-left">
                    <div className="input-group-prepend" >
                        <span className="input-group-text" id="inputGroup-sizing-default">{t('content.description')}</span>
                    </div>
                    <input type="text" id="keyword" className="form-control mr-2" aria-label={t('content.description')} aria-describedby="inputGroup-sizing-sm" onKeyDown={e => e.key === 'Enter' && searchDevice()} />
                </div>

                <div className="search-buttons float-left">
                    <button type="button" className="btn btn-outline-secondary" onClick={() => searchDevice()}>
                        <i className="fa-solid fa-magnifying-glass"></i>
                    </button>
                </div>
            </div>

            <div id="main-content">
                <table className="table" >
                    <thead >
                        <tr>
                            <th width="40px">{t('content.no')}</th>

                            <th style={{ position: 'relative' }} width="535px" >{t('content.category.load_type.lable_load_type_name')}
                                <button
                                    onClick={() => sortDevice(2, loadTypes)}
                                    style={{ backgroundColor: 'transparent', border: 'none', color: 'white', position: 'absolute', left: '85%' }}
                                    className="fa-solid fa-sort"
                                />
                            </th>
                            <th style={{ position: 'relative' }} width="745px" >{t('content.category.load_type.lable_description')}
                                <button
                                    style={{ backgroundColor: 'transparent', border: 'none', color: 'white', position: 'absolute', left: '85%' }}
                                    className="fa-solid fa-sort"
                                />
                            </th>
                            <th style={{ position: 'relative' }} width="150px" >{t('content.category.load_type.lable_create_date')}
                                <button
                                    style={{ backgroundColor: 'transparent', border: 'none', color: 'white', position: 'absolute', left: '85%' }}
                                    className="fa-solid fa-sort"
                                />
                            </th>
                            <th style={{ position: 'relative' }} width="150px" >{t('content.category.load_type.lable_update_date')}
                                <button
                                    style={{ backgroundColor: 'transparent', border: 'none', color: 'white', position: 'absolute', left: '85%' }}
                                    className="fa-solid fa-sort"
                                />
                            </th>
                            <th style={{ position: 'relative' }} width="55px"><i className="fa-regular fa-hand"></i></th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            loadTypes?.map((item, index) => (
                                <tr key={index}>
                                    <td className="text-right">{index + 1}</td>
                                    <td className="text-left">{item.loadTypeName}</td>
                                    <td className="text-left">{item.description}</td>
                                    <td className="text-center">{item.createDate}</td>
                                    <td className="text-center">{item.updateDate}</td>
                                    <td className="text-center">
                                        {
                                            permissionActions.permissionActions.hasUpdatePermission &&
                                            <Link className="button-icon" to={'/category/load-type/edit/' + item.loadTypeId} title={t('content.category.load_type.list.lable_update')}>
                                                <img height="16px" src="/resources/image/icon-edit.png" alt="edit" />
                                            </Link>
                                        }

                                        {permissionActions.permissionActions.hasDeletePermission &&
                                            <Link to="/" className="button-icon" title={t('content.category.load_type.list.lable_delete')} onClick={(e) => {
                                                deleteLoadType(item.loadTypeId);
                                                e.preventDefault();
                                            }}>
                                                <img height="16px" src="/resources/image/icon-delete.png" alt="delete" />
                                            </Link>
                                        }
                                    </td>
                                </tr>
                            ))
                        }
                        <tr style={{ display: loadTypes.length === 0 ? "contents" : "none" }}>
                            <td className="text-center" colSpan={9}>{t('content.no_data')}</td>
                        </tr>
                    </tbody>
                </table>

                <div id="main-button" className="text-left">
                    <span type="button" className="btn btn-outline-secondary btn-s3m w-120px" onClick={() => history.push("/")}>
                        <i className="fa-solid fa-house"></i> &nbsp;{t('content.home')}
                    </span>
                </div>
            </div>
        </div>

    )
};

export default ListLoadType;
