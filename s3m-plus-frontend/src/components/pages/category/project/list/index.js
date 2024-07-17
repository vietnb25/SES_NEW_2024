import React, { useEffect, useState } from "react";
import { Link, useHistory, useLocation } from "react-router-dom";
import projectService from './../../../../../services/ProjectService';
import moment from "moment";
import CONS from "../../../../../constants/constant";
import { t } from "i18next";

const $ = window.$;

const ListProject = ({ permissionActions }) => {
    const [projects, setProjects] = useState([]);
    const history = useHistory();
    const location = useLocation();

    const [status, setStatus] = useState(null);


    const getListProject = async () => {
        let response = await projectService.listProject();
        if (response.status === 200) {
            setProjects(response.data);
        }

    };
    const searchProject = async (e) => {
        let keyword = document.getElementById("keyword").value;
        if (keyword === "") {
            setStatus(null);
            getListProject();
        } else if (keyword.length > 100) {
            setStatus({
                code: 400,
                message: "Từ khóa không quá 100 ký tự"
            });
        } else {
            let res = await projectService.searchProject(keyword);
            if (res.status === 200) {
                setProjects(res.data);
            }
            setStatus(null);
        }
    };

    const setNotification = state => {
        if (state?.status === 200 && state?.message === "INSERT_SUCCESS") {
            setStatus({
                code: 200,
                message: t('content.category.project.list.add_success')
            });
        } else if (state?.status === 200 && state?.message === "UPDATE_SUCCESS") {
            setStatus({
                code: 200,
                message: t('content.category.project.list.edit_success')
            });
        }
        setTimeout(() => {
            setStatus(null);
        }, 3000);
    };


    const deleteProject = (projectId) => {
        $.confirm({
            type: 'red',
            typeAnimated: true,
            icon: 'fa fa-warning',
            title: t('content.notification'),
            content: t('content.category.project.list.delete_confirm'),
            buttons: {
                confirm: {
                    text: t('content.accept'),
                    action: async () => {
                        let res = await projectService.deleteProject(projectId);
                        if (res.status === 200) {
                            $.alert({
                                title: t('content.notification'),
                                content: t('content.category.project.list.delete_success'),
                            });
                        } else {
                            $.alert({
                                type: 'red',
                                title: t('content.notification'),
                                content: t('content.category.project.list.delete_error'),
                            });
                        }
                        getListProject();
                    }
                },
                cancel: {
                    text: t('content.cancel'),
                    action: function () {
                        // $.alert('Hủy bỏ!');
                    }
                }
            }
        });
    }

    useEffect(() => {
        if (location.state) {
            setNotification(location.state);
        };
        document.title = t('content.category.project.list.title');
        getListProject();
    }, [location])

    return (
        <div id="page-body">
            <div id="main-title">
                <h5 className="d-block mb-0 float-left text-uppercase"><i className="far fa-file-lines"></i> &nbsp;{t('content.category.project.list.title')}</h5>

                {
                    permissionActions.hasCreatePermission &&
                    <button type="button" className="btn btn-primary btn-rounded btn-new float-right" onClick={() => history.push("/category/project/add")}>
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
                        onKeyDown={e => e.key === 'Enter' && searchProject(e)} />
                </div>

                <div className="search-buttons float-left">
                    <button type="button" className="btn btn-outline-secondary" onClick={() => searchProject()}>
                        <i className="fa-solid fa-magnifying-glass"></i>
                    </button>
                </div>
            </div>

            <div id="main-content">
                <table className="table">
                    <thead>
                        <tr>
                            <th width="40px">{t('content.no')}</th>
                            <th width="150px">{t('content.category.project.project_name')}</th>
                            <th width="200px">{t('content.category.area.lable_area_name')}</th>
                            <th width="200px">{t('content.category.customer.customer_name')}</th>
                            <th width="">{t('content.address')}</th>
                            <th width="250px">{t('content.update_date')}</th>
                            <th width="55px"><i className="fa-regular fa-hand"></i></th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            projects?.map((project, index) => {
                                return (
                                    <tr key={project.projectId}>
                                        <td className="text-center">{index + 1}</td>
                                        <td style={{ wordWrap: "break-word" }}>{project.projectName}</td>
                                        <td style={{ wordWrap: "break-word" }}>{project.areaName}</td>
                                        <td style={{ wordWrap: "break-word" }}>{project.customerName}</td>
                                        <td>{project.address}</td>
                                        <td className="text-center" style={{ wordWrap: "break-word" }}>{moment(project.updateDate).format(CONS.DATE_FORMAT)}</td>
                                        <td className="text-center">
                                            {
                                                permissionActions.hasUpdatePermission &&
                                                <Link className="button-icon" to={`/category/project/` + project.projectId + `/edit`} title="Chỉnh sửa">
                                                    <img height="16px" src="/resources/image/icon-edit.png" alt="Cập nhật" />
                                                </Link>
                                            }
                                            {
                                                permissionActions.hasDeletePermission &&
                                                <Link to="/" className="button-icon" title="Xóa" onClick={
                                                    (e) => {
                                                        e.preventDefault();
                                                        deleteProject(project.projectId);
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
                        <tr style={{ display: projects.length === 0 ? "contents" : "none" }}>
                            <td className="text-center" colSpan={7}>{t('content.no_data')}</td>
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

export default ListProject;
