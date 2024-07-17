import React, { useState } from "react";
import { useFormik } from "formik";
import { useHistory } from "react-router-dom";
import ManagerService from "../../../../../services/ManagerService";
import $ from 'jquery';
import SuperManagerService from "../../../../../services/SuperManagerService";
import { useEffect } from "react";
import * as Yup from "yup";
import { useTranslation } from "react-i18next";

const initialValues = {
    managerCode: "",
    managerName: "",
    superManagerId: null,
    description: "",
    latitude: "",
    longitude: ""

}

const AddManager = () => {
    const [superManagers, setSupperManager] = useState([]);
    const history = useHistory();
    const [errorsValidate, setErrorsValidate] = useState([]);
    const { t } = useTranslation();

    const formik = useFormik({
        initialValues,
        validationSchema: Yup.object().shape({
            managerName: Yup.string().required(t('validate.manager.MANAGER_NAME_NOT_BLANK')).max(100, t('validate.manager.MANAGER_NAME_MAX_LENTH')),
            description: Yup.string().max(100, t('validate.manager.DESCRIPTION_MAX_LENGTH')),
            latitude: Yup.number().required(t('validate.manager.LATITUDE_NOT_BLANK')).min(-85.05112878, t('validate.manager.LATITUDE_MIN_VALUE')).max(85.05112878, t('validate.manager.LATITUDE_MAX_VALUE')),
            longitude: Yup.number().required(t('validate.manager.LONGTITUDE_NOT_BLANK')).min(-180.00000000, t('validate.manager.LONGTITUDE_MIN_VALUE')).max(180.00000000, t('validate.manager.LONGTITUDE_MAX_VALUE'))
        }),
        onSubmit: async (manager) => {
            let res = await ManagerService.addManager(manager);
            if (res.status === 400) {
                setErrorsValidate(res.data.errors);
                return
            }
            if (res.status === 200) {
                history.push({
                    pathname: "/category/manager",
                    state: {
                        status: 200,
                        message: "INSERT_SUCCESS"
                    }
                });
            }

        }
    });

    const listsuperManager = async () => {
        let res = await SuperManagerService.listSuperManager();
        if (res.status === 777) {
            $.alert("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
            window.location.href = "/login";
        }
        setSupperManager(res.data)
    }


    useEffect(() => {
        document.title = t('content.category.manager.add.header');
        listsuperManager();
    }, []);




    return (
        <div id="page-body">
            <div id="main-title">
                <h5 className="d-block mb-0 float-left"><i className="far fa-building"></i> &nbsp;{t('content.category.manager.add.header')}</h5>
            </div>
            {
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
            }
            <form onSubmit={formik.handleSubmit}>
                <div id="main-content">
                    <table className="table table-input">
                        <tbody>
                            <tr>
                                <th width="150px">{t('content.manager_code')}</th>
                                <td>
                                    <input type="text" onChange={formik.handleChange} name="managerCode" className="form-control" />
                                </td>
                            </tr>
                            <tr>
                                <th width="150px">{t('content.manager')}</th>
                                <td>
                                    <input type="text" onChange={formik.handleChange} name="managerName" className="form-control" />
                                </td>
                            </tr>
                            <tr>
                                <th width="150px">{t('content.super_manager')}</th>
                                <td>
                                    <select id="super-manager-id" name="superManagerId" className="custom-select block" onChange={formik.handleChange}>
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
                                <th width="150px">{t('content.longitude')}</th>
                                <td>
                                    <input type="number" onChange={formik.handleChange} name="longitude" className="form-control" />
                                </td>
                            </tr>
                            <tr>
                                <th width="150px">{t('content.latitude')}</th>
                                <td>
                                    <input type="number" onChange={formik.handleChange} name="latitude" className="form-control" />
                                </td>
                            </tr>
                            <tr>
                                <th width="150px">{t('content.description')}</th>
                                <td>
                                    <input type="text" onChange={formik.handleChange} name="description" className="form-control" />
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

export default AddManager;