import React, { useEffect, useState } from 'react'
import { useHistory, useParams } from 'react-router-dom'
import areaService from '../../../../../services/AreaService';
import { useFormik } from 'formik';
import * as Yup from "yup";
import '../index.css';
import { useTranslation } from "react-i18next";
const EditArea = () => {
    const param = useParams();
    const regex = /^\d+\.{0,1}\d{0,}$/;
    const [area, setArea] = useState({
        areaName: "",
        managerId: "",
        latitude: "",
        longitude: "",
        description: ""

    });
    const [errorsValidate, setErrorsValidate] = useState([]);

    const [manager, setManager] = useState([]);
    const [error, setError] = useState(null);
    const getListManager = async () => {
        let response = await areaService.listArea();
        if (response.status === 200) {
            setManager(response.data.managers);

        }
    }
    const getArea = async () => {
        let response = await areaService.getArea(param.id);
        if (response.status === 200) {
            setArea(response.data);
        }
    }
    const { t } = useTranslation();
    const formik = useFormik({
        initialValues: area,
        enableReinitialize: true,
        validationSchema: Yup.object().shape({
            managerId: Yup.string().required(t('validate.area.MANAGER_ID_NOT_BLANK')),
            areaName: Yup.string().required(t('validate.area.AREA_NAME_NOT_BLANK')).max(100, t('validate.area.AREA_NAME_MAX')),
            longitude: Yup.number().min(-180, t('validate.area.LONGITUDE_MIN')).max(180, t('validate.area.LONGITUDE_MAX')).required(t('validate.area.LONGITUDE_NOT_BLANK')),
            latitude: Yup.number().min(-85.05112878, t('validate.area.LATIUDE_MIN')).max(85.05112878, t('validate.area.LATITUDE_MAX')).required(t('validate.area.LATITUDE_NOT_BLANK')),
            description: Yup.string().max(1000, t('validate.area.DESCRIPTION_MAX'))
        }),
        onSubmit: async (data) => {
            let response = await areaService.updateArea(param.id, data);
            if (response.data.errors && response.status === 400) {
                setErrorsValidate(response.data.errors)
            }
            if (response.status === 200) {
                history.push({
                    pathname: "/category/area",
                    state: {
                        status: 200,
                        message: "update_success"
                    }
                });
            } else {
                setError(t('validate.area.UPDATE_FAILED'));
            }
        }

    })

    useEffect(() => {
        document.title = t('content.category.area.edit.title')
        getListManager();
        getArea();
    }, [])
    const history = useHistory();
    return (
        <div>
            <div id="page-body">
                <div id="main-title">
                    <h5 className="d-block mb-0 float-left text-uppercase"><i className="far fa-clone"></i> &nbsp;{t('content.category.area.edit.title')}</h5>
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

                {(error != null) &&
                    <div className="alert alert-danger"><p>{t('content.category.area.edit.edit_error')}</p></div>}

                {(
                    (formik.errors.areaName && formik.touched.areaName) ||
                    (formik.errors.managerId && formik.touched.managerId) ||
                    (formik.errors.longitude && formik.touched.longitude) ||
                    (formik.errors.latitude && formik.touched.latitude) ||
                    (formik.errors.description && formik.touched.description))
                    &&
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
                        <table className="table table-input">
                            <tbody>
                                <tr>
                                    <th width="160px">{t('content.category.area.lable_area_name')}<span className="required">※</span></th>
                                    <td>
                                        <input type="text" className="form-control" name="areaName" style={{ wordWrap: "break-word" }}
                                            onChange={formik.handleChange} value={formik.values.areaName}
                                        />

                                    </td>
                                </tr>
                                <tr>
                                    <th width="160px">{t('content.category.manager.lable_manager_name')}</th>
                                    <td>
                                        <select id="manager-id" name="managerId" className="custom-select block"
                                            onChange={formik.handleChange} value={formik.values.managerId}>
                                            {manager?.map(m => {
                                                return <option key={m.managerId} value={m.managerId}> {m.managerName}</option>
                                            })}
                                        </select>

                                    </td>

                                </tr>
                                <tr>
                                    <th width="160px">{t('content.longitude')} <span className="required">※</span></th>
                                    <td>
                                        <input type="number" step="0.0000000001" className="form-control" name="longitude"
                                            value={formik.values.longitude} onChange={formik.handleChange}
                                        />

                                    </td>
                                </tr>
                                <tr>
                                    <th width="160px">{t('content.latitude')}<span className="required">※</span></th>
                                    <td>
                                        <input type="number" step="0.0000000001" className="form-control" name="latitude"
                                            value={formik.values.latitude} onChange={formik.handleChange}
                                        />

                                    </td>
                                </tr>
                                <tr>
                                    <th width="150px">{t('content.description')}</th>
                                    <td>
                                        <textarea type="text" className="form-control" name="description" rows="4"
                                            value={formik.values.description} onChange={formik.handleChange}
                                        />
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <div id="main-button">
                            <button type="submit" className="btn btn-outline-secondary btn-agree mr-1" >
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

export default EditArea;
