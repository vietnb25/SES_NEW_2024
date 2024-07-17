import React, { useEffect, useState } from 'react'
import { useHistory, useParams } from 'react-router-dom'
import areaService from '../../../../../services/AreaService';
import { useFormik } from 'formik';
import * as Yup from "yup";
import { useTranslation } from "react-i18next";
import ObjectTypeService from '../../../../../services/ObjectTypeService';
import ProjectService from '../../../../../services/ProjectService';
const EditObjectType = () => {
    const param = useParams();
    // const regex = /^\d+\.{0,1}\d{0,}$/;
    const [data, setData] = useState([]);
    const [errorsValidate, setErrorsValidate] = useState([]);
    const $ = window.$;
    const [manager, setManager] = useState([]);
    const [error, setError] = useState(null);
    const [dataSelect, setDataSelect] = useState([]);
    const [project, setProject] = useState([]);


    const [dataObjectUpdate, setDataObjectUpdate] = useState({
        id: '',
        objectName: '',
        objectTypeId: '',
        objectTypeName: '',
        projectId: '',
        projectName: ''
    });

    const getObjectTypeSelect = async () => {
        let response = await ObjectTypeService.getObjectType();
        if (response.status === 200) {
            setData(response.data);
        }
    }
    const handleDetail = async () => {
        let res = await ObjectTypeService.getObjectById(param.id)
        if (res.status === 200) {
            console.log(res.data)
            setDataObjectUpdate(() => res.data)
        };

        funcGetObjectTypeSelect();
        getProjectId();
    }

    const { t } = useTranslation();

    const formik = useFormik({
        initialValues: dataObjectUpdate,
        enableReinitialize: true,
        onSubmit: async (data) => {
            let res = await ObjectTypeService.updateObjectType(data);
            if (res.status === 400) {
                setErrorsValidate(res.data.errors);
                return
            }
            if (res.status === 200) {
                $.alert({
                    title: t('content.title_notify'),
                    content: t('content.category.object_type.list.edit_success')
                });
                history.push({
                    pathname: "/category/object-type",
                    state: {
                        status: 200,
                        message: "update_success"
                    }
                });

            } else {
                console.log("that bai");
            }

        }
    });

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

    const getProjectId = async () => {
        const resProjects = await ProjectService.listProject();
        if (resProjects.status === 200) {
            setProject(resProjects.data);
        }
    }


    useEffect(() => {
        handleDetail();
        getObjectTypeSelect();
    }, [])
    const history = useHistory();
    return (
        <div>
            <div id="page-body">
                <div id="main-title">
                    <h5 className="d-block mb-0 float-left text-uppercase"><i className="far fa-clone"></i> &nbsp;{t('content.category.object_type.edit.title')}</h5>
                </div>

                <div id="main-content">
                    <form onSubmit={formik.handleSubmit}>
                        <table className="table table-input">
                            <tbody>
                                <tr>
                                    <th width="230px">{t('content.category.object_type.lable_object_name')} <span className="required">※</span></th>
                                    <td>
                                        <input type="text" className="form-control" name="objectName" style={{ wordWrap: "break-word" }} defaultValue={dataObjectUpdate.objectName}
                                            onChange={formik.handleChange}
                                        />

                                    </td>
                                </tr>

                                <tr>
                                    <th width="230px">{t('content.category.object_type.lable_object_type_name')} <span className="required">※</span></th>
                                    <td>
                                        <select className="form-label" style={{ wordWrap: "break-word", width: "100%" }} name="objectTypeId" value={dataObjectUpdate.objectTypeId} onChange={formik.handleChange} disabled >
                                            {dataSelect.map((m, index) => {
                                                return <option key={index} value={m.objectTypeId}> {m.objectTypeName}</option>
                                            })}
                                        </select>


                                    </td>
                                </tr>

                                <tr>
                                    <th width="230px">{t('content.choose_project')} <span className="required">※</span></th>
                                    <td>
                                        <select className="form-label" style={{ wordWrap: "break-word", width: "100%" }} name="projectId" value={dataObjectUpdate.projectId} onChange={formik.handleChange} disabled >
                                            {project.map((p, index) => {
                                                return <option key={index} value={p.projectId}> {p.projectName}</option>
                                            })}
                                        </select>


                                    </td>
                                </tr>

                            </tbody>
                        </table>

                        <div id="main-button">
                            <button type="submit" className="btn btn-outline-secondary btn-agree mr-1">
                                <i className="fa-solid fa-check"></i>
                            </button>
                            <button type="button" className="btn btn-outline-secondary btn-cancel" onClick={() => history.push("/category/object-type")}>
                                <i className="fa-solid fa-xmark"></i>
                            </button>
                        </div>
                    </form>
                </div>


            </div>
        </div>
    )
}

export default EditObjectType;
