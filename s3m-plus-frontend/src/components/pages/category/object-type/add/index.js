import { useFormik } from "formik";
import React, { useEffect, useState } from "react";
import { useHistory } from "react-router-dom";
import $ from 'jquery';
import * as Yup from "yup";
import { useTranslation } from "react-i18next";
import ObjectTypeService from "../../../../../services/ObjectTypeService";
import ObjectService from "../../../../../services/ObjectService";
import { useParams } from "react-router-dom/cjs/react-router-dom.min";
import ProjectService from "../../../../../services/ProjectService";



const AddObjectType = () => {
    const param = useParams();
    const history = useHistory();
    const [errorsValidate, setErrorsValidate] = useState([]);
    const [error, setError] = useState(null);
    const { t } = useTranslation();
    const [data, setData] = useState([]);
    const [dataSelect, setDataSelect] = useState([]);
    const [project, setProject] = useState([]);

    const initialValues = {
        objectName: "",
        objectTypeId: "",
        projectId: ""
    }

    const formik = useFormik({
        initialValues,
        onSubmit: async (data) => {
            let datas;
            if (data.projectId == "" && data.objectTypeId == "") {
                datas = {
                    objectName: data.objectName,
                    objectTypeId: dataSelect[0].objectTypeId,
                    projectId: project[0].projectId
                }
                console.log("data: ", datas);
            } else if (data.objectTypeId == "") {
                datas = {
                    objectName: data.objectName,
                    objectTypeId: dataSelect[0].objectTypeId,
                    projectId: data.projectId
                }
                console.log("data: ", datas);
            } else if (data.projectId == "") {
                datas = {
                    objectName: data.objectName,
                    objectTypeId: data.objectTypeId,
                    projectId: project[0].projectId
                }
                console.log("data: ", datas);
            } else {
                datas = data;
            }

            let res = await ObjectTypeService.addObjectType(datas);
            if (res.status === 400) {
                setErrorsValidate(res.data.errors);
                return
            }
            if (res.status === 200) {
                $.alert({
                    title: t('content.title_notify'),
                    content: t('content.category.object_type.list.add_success')
                });
                history.push({
                    pathname: "/category/object-type",
                    state: {
                        status: 200,
                        message: "INSERT_SUCCESS"
                    }
                });
            } else {
                setError(t('validate.cable.INSERT_FAILED'))
            }

        }
    });

    const getProjectId = async () => {
        const resProjects = await ProjectService.listProject();
        if (resProjects.status === 200) {
            setProject(resProjects.data);
        }
    }

    const funcGetObjectType = async () => {
        $("#table").hide();
        $("#loading").show();
        let res = await ObjectTypeService.getObjectType(1);
        if (res.status === 200) {
            setData(() => res.data);

            $("#loading").hide();
            $("#table").show();
        }
    }

    const funcGetObjectTypeSelect = async () => {
        $("#table").hide();
        $("#loading").show();
        let res = await ObjectTypeService.getObjectTypeSelect(1);
        if (res.status === 200) {
            setDataSelect(() => res.data);
            $("#loading").hide();
            $("#table").show();
        }
    }
    const uniqueNames = Array.from(new Set(data));
    const uniqueManagerNames = Array.from(new Set(data.map(m => m.objectType)));
    useEffect(() => {
        document.title = t('content.category.object_type.add.title')
        // funcGetObjectType();
        funcGetObjectTypeSelect();
        getProjectId();
    }, []);

    return (
        <div id="page-body">
            {
                (error != null) &&
                <div className="alert alert-danger" role="alert">
                    <p className="m-0 p-0">{t('content.category.object_type.add.error_add')}</p>
                </div>
            }
            {
                (errorsValidate.length > 0) &&
                <div className="alert alert-warning" role="alert">
                    {
                        errorsValidate.map((error, index) => {
                            return (<p key={index + 1} className="m-0 p-0">{error}</p>)
                        })
                    }
                </div>
            }
            <form onSubmit={formik.handleSubmit}>
                <div id="main-content">
                    <div id="main-title">
                        <h5 className="d-block mb-0 float-left text-uppercase"><i className="far fa-clone"></i> &nbsp;{t('content.category.object_type.add.title')}</h5>
                    </div>
                    <table className="table table-input">
                        <tbody>
                            <tr>
                                <th width="220px">{t('content.category.object_type.lable_object_name')} <span className="required">※</span></th>
                                <td>
                                    <input type="text" onChange={formik.handleChange} name="objectName" className="form-control" />
                                </td>
                            </tr>
                            <tr>
                                <th width="220px">{t('content.category.object_type.lable_object_type_name')} <span className="required">※</span></th>
                                <td>

                                    <select className="form-select" name="objectTypeId" onChange={formik.handleChange} required>
                                        <option value="" selected hidden>{t('content.choose_object')}</option>
                                        {dataSelect.map((m, index) => {
                                            return <option key={index} value={m.objectTypeId} > {m.objectTypeName}</option>
                                        })}
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <th width="220px">{t('content.project')}<span className="required">※</span></th>
                                <td>

                                    <select className="form-select" name="projectId" onChange={formik.handleChange} required>
                                        {project.map((p, index) => {
                                            return <option key={index} value={p.projectId}> {p.projectName}</option>
                                        })}
                                    </select>
                                </td>
                            </tr>

                        </tbody>
                    </table>

                    <div id="main-button">
                        <button type="submit" id="submitButton" className="btn btn-outline-secondary btn-agree mr-1" >
                            <i className="fa-solid fa-check"></i>
                        </button>
                        <button type="button" className="btn btn-outline-secondary btn-cancel" onClick={() => {
                            history.push("/category/object-type")
                        }}>
                            <i className="fa-solid fa-xmark"></i>
                        </button>
                    </div>
                </div>
            </form>
        </div>
    )
}

export default AddObjectType;