
import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import SuperManagerService from "../../../../../services/SuperManagerService";
import { useHistory } from "react-router-dom";
import { useFormik } from "formik";
import * as Yup from "yup";
import { useTranslation } from "react-i18next";

const EditSuperManager = () => {
    const param = useParams();
    let history = useHistory();
    const [errorsValidate, setErrorsValidate] = useState([]);
    const [error, setError] = useState(null);
    const { t } = useTranslation();

    const [superManager, setSuperManager] = useState({
        superManagerId: "",
        superManagerName: "",
        description: "",
        latitude: "",
        longitude: ""
    });

    const loadSuperManager = async () => {
        let res = await SuperManagerService.detailsSuperManager(param.id);
        if (res.status === 200) {
            setSuperManager(res.data);
        }
    }

    const formik = useFormik({
        enableReinitialize: true,
        initialValues: superManager, validationSchema: Yup.object({
            superManagerName: Yup.string().required(t('validate.super_manager.SUPER_MANAGER_NAME_NOT_BLANK')).max(100, t('validate.super_manager.SUPER_MANAGER_NAME_MAX_SIZE_ERROR')),
            longitude: Yup.number().min(-180.00000000, t('validate.super_manager.LONGITUDE_MIN_VALUE_ERROR')).max(180.00000000, t('validate.super_manager.LONGITUDE_MAX_VALUE_ERROR')).required(t('validate.super_manager.LONGITUDE_NOT_BLANK')),
            latitude: Yup.number().min(-85.05112878, t('validate.super_manager.LATITUDE_MIN_VALUE_ERROR')).max(85.05112878, t('validate.super_manager.LATITUDE_MAX_VALUE_ERROR')).required(t('validate.super_manager.LATITUDE_NOT_BLANK'))
        }),
        onSubmit: async (data) => {
            let res = await SuperManagerService.updateSuperManager(param.id, data);
            if (res.status === 400) {
                setErrorsValidate(res.data.errors);
                return
            }
            if (res.status === 200) {
                history.push({
                    pathname: "/category/super-manager",
                    state: {
                        status: 200,
                        message: "UPDATE_SUCCESS"
                    }
                });
            } else {
                setError(t('content.category.super_manager.edit.error_edit'))
            }
        }
    });

    useEffect(() => {
        document.title = t('content.category.super_manager.edit.title');
        loadSuperManager();
    }, []);
    return (
        <>
            <div id="page-body">
                <div id="main-title">
                    <h5 className="d-block mb-0 float-left text-uppercase"><i className="far fa-compass"></i> &nbsp;{t('content.category.super_manager.edit.header')}</h5>
                </div>
                <form onSubmit={formik.handleSubmit}>
                    {
                        (error != null) &&
                        <div className="alert alert-danger" role="alert">
                            <p className="m-0 p-0">{t('content.category.super_manager.edit.error_edit')}</p>
                        </div>
                    }
                    {
                        (errorsValidate.length > 0) &&
                        <div className="alert alert-warning" role="alert">
                            {
                                errorsValidate.map((error, index) => {
                                    return (<p key={index} className="m-0 p-0">{error}</p>)
                                })
                            }
                        </div>
                    }
                    {
                        ((formik.errors.superManagerName && formik.touched.superManagerName) ||
                            (formik.errors.longitude && formik.touched.longitude) ||
                            (formik.errors.latitude && formik.touched.latitude)) &&
                        <div className="alert alert-warning" role="alert">
                            <p className="m-0 p-0">{formik.errors.superManagerName}</p>
                            <p className="m-0 p-0">{formik.errors.longitude}</p>
                            <p className="m-0 p-0">{formik.errors.latitude}</p>
                        </div>
                    }
                    <div id="main-content">
                        {
                            <table className="table table-input">
                                <tbody>
                                    <tr>
                                        <th width="150px">{t('content.category.super_manager.lable_sm_id')}<span className="required">※</span></th>
                                        <td>
                                            <input type="text" className="form-control" name="superManagerId" value={formik.values.superManagerId} onChange={formik.handleChange} disabled />
                                        </td>
                                    </tr>
                                    <tr>
                                        <th width="150px">{t('content.category.super_manager.lable_sm_name')}<span className="required">※</span></th>
                                        <td>
                                            <input type="text" className="form-control" name="superManagerName" value={formik.values.superManagerName} onChange={formik.handleChange} />
                                        </td>
                                    </tr>
                                    <tr>
                                        <th width="150px">{t('content.longitude')}<span className="required">※</span></th>
                                        <td>
                                            <input type="number" step="0.0000000001" className="form-control input-number-m" name="longitude" value={formik.values.longitude} onChange={formik.handleChange} />
                                        </td>
                                    </tr>
                                    <tr>
                                        <th width="150px">{t('content.latitude')}<span className="required">※</span></th>
                                        <td>
                                            <input type="number" step="0.0000000001" className="form-control input-number-m" name="latitude" value={formik.values.latitude} onChange={formik.handleChange} />
                                        </td>
                                    </tr>
                                    <tr>
                                        <th width="150px">{t('content.description')}</th>
                                        <td>
                                            <input type="text" className="form-control" name="description" value={formik.values.description} onChange={formik.handleChange} />
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        }
                        <div id="main-button">
                            <button type="submit" className="btn btn-outline-secondary btn-agree mr-1">
                                <i className="fa-solid fa-check"></i>
                            </button>
                            <button type="button" className="btn btn-outline-secondary btn-cancel" onClick={() => {
                                history.push("/category/super-manager")
                            }}>
                                <i className="fa-solid fa-xmark"></i>
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </>
    )
}

export default EditSuperManager;