import React, { useEffect, useState } from "react";
import { Link, useHistory, useLocation } from 'react-router-dom';
import areaService from './../../../../../services/AreaService';
import $ from "jquery";
import moment from 'moment';
import CONS from "../../../../../constants/constant";
import { t } from "i18next";

const ListArea = ({ permissionActions }) => {
    const [areas, setAreas] = useState([]);
    const [managers, setManagers] = useState([]);
    const history = useHistory();
    const [status, setStatus] = useState(null);
    const location = useLocation();
    const [key, setKey] = useState(null);
    const getListArea = async () => {
        let response = await areaService.listArea();
        if (response.status === 200) {
            setAreas(response.data.areas);
            setManagers(response.data.managers);
        }
    }
    const searchArea = async () => {
        let keyword = document.getElementById("keyword").value;
        if (keyword.length > 100) {
            setKey("Mô tả không được quá 100 kí tự");
        } else {
            setKey(null);
            if (keyword === "") {
                getListArea();
            } else {
                let res = await areaService.searchArea(keyword);
                if (res.status === 200) {
                    let area = res.data;
                    setAreas(area);
                } else {
                    $.alert({
                        title: 'Thông báo!',
                        content: 'Có lỗi xảy ra. Vui lòng thử lại.',
                    });

                }
            }
        }

    }

    const setNotification = (state) => {
        if (state?.message === "insert_success" && state?.status === 200) {
            setStatus({
                code: 200,
                message: t('content.category.area.list.add_success')
            })
        }
        if (state?.message === "update_success" && state?.status === 200) {
            setStatus({
                code: 200,
                message: t('content.category.area.list.edit_success')
            })
        }
    }

    const deleteArea = (areaId) => {
        $.confirm({
            type: 'red',
            typeAnimated: true,
            icon: 'fa fa-warning',
            title: t('content.confirm'),
            content: t('content.category.area.list.delete_confirm'),
            buttons: {
                confirm: {
                    text: t('content.accept'),
                    action: async () => {
                        let response = await areaService.deleteArea(areaId);
                        if (response.status === 200) {
                            $.alert({
                                title: t('content.notification'),
                                content: t('content.category.area.list.delete_success')
                            });
                        } else {
                            $.alert({
                                type: 'red',
                                title: t('content.notification'),
                                content: t('content.category.area.list.delete_error')
                            });
                        }
                        getListArea();
                    }
                },
                cancel: {
                    text: t('content.cancel')
                }
            }

        })
    }

    useEffect(() => {
        document.title = t('content.category.area.list.header');
        if (location.state) {
            setNotification(location.state);
        };
        getListArea();
    }, [])

    return (
        <div id="page-body">
            <div id="main-title">
                <h5 className="d-block mb-0 float-left text-uppercase"><i className="far fa-clone"></i> &nbsp;{t('content.category.area.list.header')}</h5>

                {
                    permissionActions.hasCreatePermission &&
                    <button type="button" className="btn btn-primary btn-rounded btn-new float-right" onClick={() => history.push("/category/area/add")}>
                        <i className="fa-solid fa-plus"></i>
                    </button>
                }


            </div>
            {status != null &&
                <div>
                    {
                        status.code === 200 ?
                            <div className="alert alert-success">
                                <p className="m-0" role="alert"></p>{status?.message}
                            </div> :
                            <div className="alert alert-danger" role="alert">
                                <p className="m-0 p-0">{status?.message}</p>
                            </div>
                    }
                </div>
            }
            {(key != null) &&
                <div className="alert alert-warning">
                    <p className="m-0">{key}</p>
                </div>
            }
            <div id="main-search">
                <div className="input-group search-item mb-3 float-left">
                    <div className="input-group-prepend">
                        <span className="input-group-text" id="inputGroup-sizing-default">{t('content.description')}</span>
                    </div>
                    <input type="text" id="keyword" className="form-control" aria-label="Mô tả" aria-describedby="inputGroup-sizing-sm"
                        onKeyDown={e => e.key === 'Enter' && searchArea()} />
                </div>

                <div className="search-buttons float-left">
                    <button type="button" className="btn btn-outline-secondary" onClick={() => searchArea()}>
                        <i className="fa-solid fa-magnifying-glass"></i>
                    </button>
                </div>
            </div>

            <div id="main-content">
                <table className="table">
                    <thead>
                        <tr>
                            <th width="40px">{t('content.no')}</th>
                            <th width="150px">{t('content.category.area.lable_area_name')}</th>
                            <th width="150px">{t('content.manager')}</th>
                            <th width="150px">{t('content.update_date')}</th>
                            <th >{t('content.description')}</th>
                            <th width="55px"><i className="fa-regular fa-hand"></i></th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            areas.map((area, index) => {
                                return (
                                    <tr key={index}>
                                        <td className="text-center">{index + 1}</td>
                                        <td style={{ wordWrap: "break-word" }}>{area.areaName}</td>
                                        {
                                            managers.map(manager => {
                                                if (manager.managerId === area.managerId) {
                                                    return <td key={manager.managerId}>{manager.managerName}</td>
                                                }
                                                return null
                                            })
                                        }
                                        <td>{moment(area.updateDate).format(CONS.DATE_FORMAT)}</td>
                                        <td>{area.description}</td>
                                        <td className="text-center">
                                            {
                                                permissionActions.hasUpdatePermission &&
                                                <Link className="button-icon"
                                                    to={`/category/area/edit/` + area.areaId}
                                                    title="Chỉnh sửa">
                                                    <img height="16px" src="/resources/image/icon-edit.png" alt="Cập nhật" />
                                                </Link>
                                            }
                                            {
                                                permissionActions.hasDeletePermission &&
                                                <Link to="/" className="button-icon" title="Xóa" onClick={
                                                    (e) => {
                                                        deleteArea(area.areaId);
                                                        e.preventDefault();
                                                    }
                                                }>
                                                    <img height="16px" src="/resources/image/icon-delete.png" alt="Xóa" />
                                                </Link>
                                            }
                                        </td>
                                    </tr>
                                )
                            })

                        }
                        {(areas.length === 0) &&
                            <tr>
                                <td className=" text-center" colSpan={6}> Không có dữ liệu </td>
                            </tr>
                        }
                    </tbody>
                </table>

                <div id="main-button" className="text-left">
                    <button type="button" className="btn btn-outline-secondary btn-s3m w-120px" onClick={() => history.push("/")}>
                        <i className="fa-solid fa-house"></i> &nbsp;Trang chủ
                    </button>
                </div>
            </div>
        </div>
    )
}

export default ListArea;