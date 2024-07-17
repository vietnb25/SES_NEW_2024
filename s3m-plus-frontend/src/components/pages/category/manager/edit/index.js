import React, { useEffect, useState } from "react";
import { useHistory, useParams } from "react-router-dom";
import ManagerService from "../../../../../services/ManagerService";
import $ from 'jquery';
import SuperManagerService from "../../../../../services/SuperManagerService";
import { useFormik } from "formik";
import * as Yup from "yup";
import { useTranslation } from "react-i18next";
const EditManager = () => {

    const param = useParams();
    const history = useHistory();
    const [superManagers, setSupperManager] = useState([]);
    const [manager, setManager] = useState({
        managerId: "",
        managerName: "",
        superManagerId: "",
        latitude: "",
        longitude: "",
        description: ""
    });
    const { t } = useTranslation();
    const [error, setError] = useState(null);
    const { managerId, managerName, superManagerId, latitude, longitude, description } = manager;
    // const [errorsValidate, setErrorsValidate] = useState([]);


    const onInputChange = (e) => {
        setManager({ ...manager, [e.target.name]: e.target.value });
    };

    const loadManager = async () => {
        let res = await ManagerService.detailManager(param.id);
        if (res.status === 200) {
            setManager(res.data);
        }
    };

    const listsuperManager = async () => {
        let res = await SuperManagerService.listSuperManager();
        if (res.status === 777) {
            $.alert("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
            window.location.href = "/login";
        }
        setSupperManager(res.data)
    }

    const formik = useFormik({
        initialValues: manager,
        // validationSchema: Yup.object().shape({
        //     managerName: Yup.string().required(t('validate.manager.MANAGER_NAME_NOT_BLANK')).max(100, t('validate.manager.MANAGER_NAME_MAX_LENTH')),
        //     description: Yup.string().max(100, t('validate.manager.DESCRIPTION_MAX_LENGTH')),
        //     latitude: Yup.number().required(t('validate.manager.LATITUDE_NOT_BLANK')).min(-85.05112878, t('validate.manager.LATITUDE_MIN_VALUE')).max(85.05112878, t('validate.manager.LATITUDE_MAX_VALUE')),
        //     longitude: Yup.number().required(t('validate.manager.LONGTITUDE_NOT_BLANK')).min(-180.00000000, t('validate.manager.LONGTITUDE_MIN_VALUE')).max(180.00000000, t('validate.manager.LONGTITUDE_MAX_VALUE'))
        // }),
        enableReinitialize: true,
        onSubmit: async managerData => {
            let res = await ManagerService.updateManager(param.id, managerData);
            // if (res.status === 400) {
            //     setErrorsValidate(res.data.errors);
            //     return
            // }
            if (res.status === 200) {
                history.push({
                    pathname: "/category/manager",
                    state: {
                        status: 200,
                        message: "UPDATE_SUCCESS"
                    }
                });
            } else {
                setError(t('validate.manager.UPDATE_FAILED'))
            }

        }

    });

    useEffect(() => {
        document.title = t('content.category.manager.edit.header');
        loadManager();
        listsuperManager();
    }, []);

    return (
        <div id="page-body">
            <div id="main-title">
                <h5 className="d-block mb-0 float-left text-uppercase"><i className="far fa-building"></i> &nbsp;{t('content.category.manager.edit.header')}</h5>
            </div>
            {/* {
                (errorsValidate.length > 0) &&
                <div class="alert alert-warning" role="alert">
                    {
                            errorsValidate.map((error, index) => {
                            return (<p key={index} className="m-0 p-0">{error}</p>)
                        })
                    }
                </div>
            }
            {
                ((formik.errors.managerName && formik.touched.managerName) || 
                    (formik.errors.description && formik.touched.description) ||
                    (formik.errors.longitude && formik.touched.longitude) ||
                    (formik.errors.latitude && formik.touched.latitude)) &&
                <div className="alert alert-warning" role="alert">
                    <p className="m-0 p-0">{formik.errors.managerName}</p>
                    <p className="m-0 p-0">{formik.errors.description}</p>
                    <p className="m-0 p-0">{formik.errors.longitude}</p>
                    <p className="m-0 p-0">{formik.errors.latitude}</p>
                </div>
            } */}
            <form onSubmit={formik.handleSubmit}>
                <div id="main-content">
                    <table className="table table-input">
                        <tbody>
                            <tr>
                                <th width="150px">{t('content.manager')}</th>
                                <td>
                                    <input type="text" name="managerName" value={managerName} className="form-control" onChange={(e) => onInputChange(e)} disabled />
                                </td>
                            </tr>
                            <tr>
                                <th width="150px">{t('content.super_manager')}</th>
                                <td>
                                    <select value={formik.values.superManagerId} id="super-manager-id" name="superManagerId" className="custom-select block"
                                        onChange={(e) => onInputChange(e)}>
                                        <option value="-1">{t('content.choose_super_manger')}</option>
                                        {
                                            superManagers.map(superManager => {
                                                return <option key={superManager.superManagerId} value={`${superManager.superManagerId}`}>{superManager.superManagerName}</option>
                                            })
                                        }
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <th width="150px">{t('content.longtitude')}</th>
                                <td>
                                    <input type="number" name="longitude" value={longitude} className="form-control" onChange={(e) => onInputChange(e)} disabled />
                                </td>
                            </tr>
                            <tr>
                                <th width="150px">{t('content.latitude')}</th>
                                <td>
                                    <input type="number" name="latitude" value={latitude} className="form-control" onChange={(e) => onInputChange(e)} disabled />
                                </td>
                            </tr>
                            <tr>
                                <th width="150px">{t('content.description')}</th>
                                <td>
                                    <input type="text" name="description" value={description} className="form-control" onChange={(e) => onInputChange(e)} disabled />
                                </td>
                            </tr>
                        </tbody>
                    </table>

                    <div id="main-button">
                        <button type="submit" className="btn btn-outline-secondary btn-agree mr-1">
                            <i className="fa-solid fa-check"></i>
                        </button>
                        <button type="button" className="btn btn-outline-secondary btn-cancel" onClick={() => {
                            history.push("/category/manager")
                        }}>
                            <i className="fa-solid fa-xmark"></i>
                        </button>
                    </div>
                </div>
            </form>
        </div>
    )
}

export default EditManager;