import React, { useEffect, useState } from "react";
import { Link, useHistory } from "react-router-dom";
import "./index.css";
import moment from 'moment';
import CONS from "../../../../../constants/constant";
import { useTranslation } from "react-i18next";
import ProjectService from "../../../../../services/ProjectService";
import CustomerService from "../../../../../services/CustomerService";
import { useLocation } from "react-router-dom/cjs/react-router-dom.min";
import { select } from "d3";
import ListObjectType from ".";
import ObjectTypeService from "../../../../../services/ObjectTypeService";


const $ = window.$;

const ListObjectTypeMst = ({ permissionActions, parentCallback, dataId }) => {

    const sendData = (data) => {
        let _customerId = document.getElementById("customerId").value;
        setCustomerId(_customerId);
        let _projectId = document.getElementById("projectId").value;
        parentCallback({
            customerId: _customerId,
            projectId: _projectId
        });
    }

    const history = useHistory();

    const [objectTypeMst, setObjectTypeMst] = useState([]);
    const [status, setStatus] = useState(null);
    const [errorsValidate, setErrorsValidate] = useState();
    const { t } = useTranslation();
    const [customerId, setCustomerId] = useState(1);
    const [projectId, setProjectId] = useState(0);
    const [projects, setProjects] = useState([]);
    const [project, setProject] = useState({});
    const [customers, setCustomers] = useState([]);
    const location = useLocation();
    const [customer, setCustomer] = useState({});
    const [searchKey, setSearchKey] = useState('');





    const setNotification = state => {
        if (state?.status === 200 && state?.message === "INSERT_SUCCESS" && state?.status !== -1) {
            setStatus({
                code: 200,
                message: t('content.category.object_type.list.add_success')
            });
        } else if (state?.status === 200 && state?.message === "UPDATE_SUCCESS" && state?.status !== -1) {
            setStatus({
                code: 200,
                message: t('content.category.object_type.list.edit_success')
            });
        }
        setTimeout(() => {
            setStatus(null);
        }, 3000);
    };



    const funcGetObjectType = async () => {
        $("#table").hide();
        $("#loading").show();
        let res = await ObjectTypeService.listObjectType();
        if (res.status === 200) {
            setObjectTypeMst(() => res.data);
            console.log(res.data);
            $("#loading").hide();
            $("#table").show();
        }
    }



    const sortDevice = (type, ls) => {
        if (type === 1) {
            objectTypeMst.sort((a, b) => {
                if (sort == false) {
                    return a.deviceCode < b.deviceCode ? -1 : a.deviceCode > b.deviceCode ? 1 : 0;
                } else {
                    return a.deviceCode > b.deviceCode ? -1 : a.deviceCode < b.deviceCode ? 1 : 0;
                }
            });
            setSort(!sort)
        }
        if (type === 2) {
            if (sort == false) {
                objectTypeMst.sort((a, b) => {
                    const nameA = a.deviceName.toUpperCase();
                    const nameB = b.deviceName.toUpperCase();
                    if (nameA < nameB) {
                        return -1;
                    }
                    if (nameA > nameB) {
                        return 1;
                    }
                });
            } else {
                objectTypeMst.sort((a, b) => {
                    const nameA = a.deviceName.toUpperCase();
                    const nameB = b.deviceName.toUpperCase();
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
        if (type === 3) {
            if (sort == false) {
                objectTypeMst.sort((a, b) => {
                    let a1;
                    let b1;
                    if (a.ip == null) {
                        a1 = '';
                    } else {
                        a1 = a.ip.toUpperCase();
                    }
                    if (b1 == null) {
                        b1 = ''
                    } else {
                        b.ip.toUpperCase();
                    }
                    if (a1 < b1) {
                        return -1;
                    }
                    if (a1 > b1) {
                        return 1;
                    }
                });
            } else {
                objectTypeMst.sort((a, b) => {
                    let a1;
                    let b1;
                    if (a.ip == null) {
                        a1 = '';
                    } else {
                        a1 = a.ip.toUpperCase();
                    }
                    if (b1 == null) {
                        b1 = ''
                    } else {
                        b.ip.toUpperCase();
                    }
                    if (a1 > b1) {
                        return -1;
                    }
                    if (a1 < b1) {
                        return 1;
                    }
                });
            }
            setSort(!sort);
        } if (type === 4) {
            objectTypeMst.sort((a, b) => {
                if (sort == false) {
                    return a.uid < b.uid ? -1 : a.uid > b.uid ? 1 : 0;
                } else {
                    return a.uid > b.uid ? -1 : a.uid < b.uid ? 1 : 0;
                }
            });
            setSort(!sort)
        } if (type === 7) {
            if (sort == false) {
                objectTypeMst.sort((a, b) => {
                    let a1;
                    let b1;
                    if (a.address == null) {
                        a1 = '';
                    } else {
                        a1 = a.address.toUpperCase();
                    }
                    if (b1 == null) {
                        b1 = ''
                    } else {
                        b.address.toUpperCase();
                    }
                    if (a1 < b1) {
                        return -1;
                    }
                    if (a1 > b1) {
                        return 1;
                    }
                });
            } else {
                objectTypeMst.sort((a, b) => {
                    let a1;
                    let b1;
                    if (a.address == null) {
                        a1 = '';
                    } else {
                        a1 = a.address.toUpperCase();
                    }
                    if (b1 == null) {
                        b1 = ''
                    } else {
                        b.address.toUpperCase();
                    }
                    if (a1 > b1) {
                        return -1;
                    }
                    if (a1 < b1) {
                        return 1;
                    }
                });
            }
            setSort(!sort);
        } if (type === 8) {
            if (sort == false) {
                objectTypeMst.sort((a, b) => {
                    let a1;
                    let b1;
                    if (a.area == null) {
                        a1 = '';
                    } else {
                        a1 = a.area.toUpperCase();
                    }
                    if (b1 == null) {
                        b1 = ''
                    } else {
                        b1 = b.area.toUpperCase();
                    }
                    if (a1 < b1) {
                        return -1;
                    }
                    if (a1 > b1) {
                        return 1;
                    }
                });
            } else {
                objectTypeMst.sort((a, b) => {
                    let a1;
                    let b1;
                    if (a.area == null) {
                        a1 = '';
                    } else {
                        a1 = a.area.toUpperCase();
                    }
                    if (b1 == null) {
                        b1 = ''
                    } else {
                        b1 = b.area.toUpperCase();
                    }
                    if (a1 > b1) {
                        return -1;
                    }
                    if (a1 < b1) {
                        return 1;
                    }
                });
            }
            setSort(!sort);
        } if (type === 9) {

            if (sort == false) {
                objectTypeMst.sort((a, b) => {
                    const dateA = new Date(a.updateDate)
                    const dateB = new Date(b.updateDate)
                    if (dateA - dateB > 0) {
                        return 1;
                    } else if (dateA - dateB < 0) {
                        return -1;
                    } else {
                        return 0;
                    }
                });
            } else {
                objectTypeMst.sort((a, b) => {
                    const dateA = new Date(a.updateDate)
                    const dateB = new Date(b.updateDate)
                    if (dateA - dateB < 0) {
                        return 1;
                    } else if (dateA - dateB > 0) {
                        return -1;
                    } else {
                        return 0;
                    }
                });
            }
            setSort(!sort)

        }
        if (type === 10) {
            console.log("sort by so do luoi");
        }

    }
    const [sort, setSort] = useState(false);


    const searchObjectType = async () => {
        let keyword = document.getElementById("keyword").value;
        if (keyword === "") {
            funcGetObjectType();
            setStatus(null);
        } else if (keyword.length > 100) {
            setStatus({
                message: "Từ khóa không quá 100 ký tự"
            });

        } else {
            let res = await ObjectTypeService.searchObjectType(keyword)
            if (res.status === 200) {
                setObjectTypeMst(res.data);
            }
            setStatus(null);
        }
    }

    function removeDiacritics(text) {
        return text.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
    }

    const deleteObject = (id) => {
        $.confirm({
            type: 'red',
            typeAnimated: true,
            icon: 'fa fa-warning',
            title: t('content.title_confirm'),
            content: t('content.category.object_type.delete.delete_confirm'),
            buttons: {
                confirm: {
                    text: t('content.accept'),
                    action: async () => {
                        let response = await ObjectTypeService.deleteObjectType(id);
                        // Xóa thành công
                        if (response.status === 200) {
                            $.alert({
                                title: t('content.title_notify'),
                                content: t('content.category.object_type.delete.alert')
                            });
                            funcGetObjectType();
                        } else {
                            $.alert({
                                title: t('content.title_notify'),
                                content: t('content.category.object_type.delete.error_delete')
                            });
                        }

                    }
                },
                cancel: {
                    text: t('content.cancel')
                }
            }

        })
    }


    useEffect(() => {
        funcGetObjectType()
        if (location.state) {
            setNotification(location.state);
        };
        document.title = t('content.category.object_type.list.title');
    }, [sort]);

    return (
        <div id="page-body">

            <div id="main-title">
                <h5 className="d-block mb-0 float-left text-uppercase"><i className="fas fa-server"></i> &nbsp;{t('content.category.object_type.list.title')}</h5>

                {
                    permissionActions.hasCreatePermission &&
                    <button type="button" className="btn btn-primary btn-rounded btn-new float-right" onClick={() => {
                        history.push("/category/object-type-mst/add");
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
                        <span className="input-group-text" id="inputGroup-sizing-default">{t('content.home_page.search')}</span>
                    </div>
                    <input type="text" id="keyword" className="form-control" aria-label="Mô tả" aria-describedby="inputGroup-sizing-sm" onKeyDown={e => e.key === "Enter" && searchObjectType(e)} />
                </div>
                <div className="search-buttons float-left">
                    <button type="button" className="btn btn-outline-secondary" onClick={() => searchObjectType()}>
                        <i className="fa-solid fa-magnifying-glass"></i>
                    </button>
                </div>


            </div>

            <div id="main-content">
                <table className="table">
                    <thead>
                        <tr>
                            <th width="10px">{t('content.no')}</th>
                            <th width="150px" style={{ position: 'relative' }}> {t('content.category.object_type.lable_object_name')}
                                <button
                                    onClick={() => sortDevice(3, objectTypeMst)}
                                    style={{ backgroundColor: 'transparent', border: 'none', color: 'white', position: 'absolute', left: '80%' }}

                                />
                            </th>
                            <th width="150px" style={{ position: 'relative' }} > {t('content.category.object_type.image')}
                                <button
                                    onClick={() => sortDevice(4, objectTypeMst)}
                                    style={{ backgroundColor: 'transparent', border: 'none', color: 'white', position: 'absolute', left: '70%' }}

                                />
                            </th>

                            <th width="40px" style={{ position: 'relative' }}>{t('content.category.object_type.lable_update_date')}
                                <button
                                    onClick={() => sortDevice(9, objectTypeMst)}
                                    style={{ backgroundColor: 'transparent', border: 'none', color: 'white', position: 'absolute', left: '85%' }}

                                />
                            </th>
                            <th width="20px"><i className="fa-regular fa-hand"></i></th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            objectTypeMst?.map((item, index) => (
                                <tr key={index}>
                                    <td className="text-center">{index + 1}</td>

                                    <td className="text-left">{item.name}</td>
                                    <td>
                                        {item.img != null ? <img src={item.img} width="150px" height="150px"></img> : ""}
                                    </td>
                                    <td className="text-center">{moment(item.updateDate).format(CONS.DATE_FORMAT)}</td>
                                    <td className="text-center">
                                        {
                                            permissionActions.hasUpdatePermission &&
                                            <Link className="button-icon" to={`/category/object-type-mst/edit/` + item.id} title={t('content.title_icon_edit')}>
                                                <img height="16px" src="/resources/image/icon-edit.png" alt="Chỉnh sửa" />
                                            </Link>
                                        }
                                        {
                                            permissionActions.hasDeletePermission &&
                                            <Link to="/" className="button-icon" title="Xóa" onClick={
                                                (e) => {
                                                    deleteObject(item.id);
                                                    e.preventDefault();
                                                }
                                            }>
                                                <img height="16px" src="/resources/image/icon-delete.png" alt="Xóa" />
                                            </Link>
                                        }
                                    </td>

                                </tr>
                            ))
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

export default ListObjectTypeMst;