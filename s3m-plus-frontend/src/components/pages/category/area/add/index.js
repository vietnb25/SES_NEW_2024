import React from 'react'
import { useHistory } from "react-router-dom";
import { useFormik } from 'formik';
import $ from "jquery";
import { useState } from 'react';
import areaService from '../../../../../services/AreaService';
import { useEffect } from 'react';
import '../index.css';
import * as Yup from "yup";
import { useTranslation } from "react-i18next";

const AddArea = () => {
    const history = useHistory();
    const [manager, setManager] = useState([]);
    const [error, setError] = useState(null);
    const [errorsValidate, setErrorsValidate] = useState([]);
    const regex = /^\d+\.{0,1}\d{0,}$/;
    const getListManager = async () => {
        let response = await areaService.listArea();
        if (response.status === 200) {
            setManager(response.data.managers);
        }
    }

    const initialValues = {
        areaName: "",
        managerId: "",
        longitude: "",
        latitude: "",
        description: ""
    }
    const { t } = useTranslation();

    const formik = useFormik({
        initialValues,
        validationSchema: Yup.object().shape({
            managerId: Yup.string().required(t('validate.area.MANAGER_ID_NOT_BLANK')),
            areaName: Yup.string().required(t('validate.area.AREA_NAME_NOT_BLANK')).max(100, t('validate.area.AREA_NAME_MAX')),
            longitude: Yup.number().min(-180, t('validate.area.LONGITUDE_MIN')).max(180, t('validate.area.LONGITUDE_MAX')).required(t('validate.area.LONGITUDE_NOT_BLANK')),
            latitude: Yup.number().min(-85.05112878, t('validate.area.LATIUDE_MIN')).max(85.05112878, t('validate.area.LATITUDE_MAX')).required(t('validate.area.LATITUDE_NOT_BLANK')),
            description: Yup.string().max(1000, t('validate.area.DESCRIPTION_MAX'))
        }),
        onSubmit: async (data) => {
            let res = await areaService.addArea(data);
            if (res.data.errors && res.status === 400) {
                setErrorsValidate(res.data.errors)
            }
            if (res.status === 200) {
                history.push({
                    pathname: "/category/area",
                    state: {
                        status: 200,
                        message: "insert_success"
                    }
                }
                );
            } else {
                setError(t('validate.area.INSERT_FAILED'));
            }
        }

    })

    useEffect(() => {
        document.title = t('content.category.area.add.title');
        getListManager();

    }, []);
    return (
        <div>
            <div id="page-body">
                <div id="main-title">
                    <h5 className="d-block mb-0 float-left text-uppercase"><i className="fas fa-user-tie"></i> &nbsp;{t('content.category.area.add.title')}</h5>
                </div>
                {
                    (errorsValidate.length > 0) &&
                    <div className="alert alert-warning" role="alert">
                        {errorsValidate.map((error, index) => {
                            return (<p key={index} className="m-0 p-0">{error}</p>)
                        })
                        }
                    </div>
                }

                {
                    (error != null) &&
                    <div className="alert alert-danger">
                        <p className="m-0 p-0">{error}</p>
                    </div>
                }

                {
                    ((formik.errors.areaName && formik.touched.areaName) ||
                        (formik.errors.managerId && formik.touched.managerId) ||
                        (formik.errors.longitude && formik.touched.longitude) ||
                        (formik.errors.latitude && formik.touched.latitude) ||
                        (formik.errors.description && formik.touched.description)
                    ) &&
                    <div className='alert alert-warning' role="alert">
                        <p className='m-0'>{formik.errors.areaName}</p>
                        <p className='m-0'>{formik.errors.managerId}</p>
                        <p className='m-0'>{formik.errors.longitude}</p>
                        <p className='m-0'>{formik.errors.latitude}</p>
                        <p className='m-0'>{formik.errors.description}</p>
                    </div>
                }
                <div id="main-content">
                    <form onSubmit={formik.handleSubmit}>
                        <table className="table table-input mt-3">
                            <tbody>
                                <tr>
                                    <th width="160px">{t('content.category.area.lable_area_name')}<span className="required">※</span></th>
                                    <td>
                                        <input type="text" className="form-control" name="areaName" onChange={formik.handleChange} />
                                    </td>
                                </tr>
                                <tr>
                                    <th width="160px">{t('content.category.manager.lable_manager_name')}</th>
                                    <td>
                                        <select id="manager-id" name="managerId" className="custom-select block " onChange={formik.handleChange}>
                                            <option value="-1">{t('content.category.manager.choose_manager')}</option>
                                            {manager.map(m => {
                                                return <option key={m.managerId} value={m.managerId}> {m.managerName}</option>
                                            })}
                                        </select>

                                    </td>
                                </tr>
                                <tr>
                                    <th width="160px">{t('content.longitude')} <span className="required">※</span></th>
                                    <td>
                                        <input type="number" step="0.0000000001" className="form-control" name="longitude" onChange={formik.handleChange} />
                                    </td>
                                </tr>
                                <tr>
                                    <th width="160px">{t('content.latitude')}<span className="required">※</span></th>
                                    <td>
                                        <input type="number" step="0.0000000001" className="form-control" name="latitude" onChange={formik.handleChange} />
                                    </td>
                                </tr>
                                <tr>
                                    <th width="160px">{t('content.description')}</th>
                                    <td>
                                        <textarea type="text" className="form-control" name="description" onChange={formik.handleChange} />
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <div id="main-button">
                            <button type="submit" className="btn btn-outline-secondary btn-agree mr-1">
                                <i className="fa-solid fa-check"></i>
                            </button>
                            <button type="button" className="btn btn-outline-secondary btn-cancel" onClick={() => history.push("/category/area")}>
                                <i className="fa-solid fa-xmark"></i>
                            </button>
                        </div>
                    </form>

                </div>
            </div>
        </div>
    )
}

export default AddArea
