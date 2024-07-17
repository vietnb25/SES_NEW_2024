import React from "react";
import { useHistory } from "react-router-dom";
import { useFormik } from "formik";
import customerService from "../../../../../services/CustomerService";
import areaService from "../../../../../services/AreaService";
import projectService from "../../../../../services/ProjectService";
import { useState } from "react";
import { useEffect } from "react";
import "./index.css";
import Checkbox from "react-custom-checkbox";
import * as Icon from "react-icons/fi";
import * as Yup from 'yup';
import { useTranslation } from "react-i18next";
import { PatternFormat } from 'react-number-format';


const $ = window.$;

const shift = /^([0-1]?[0-9]|2[0-3]):(00|15|30|45)\s-\s([0-1]?[0-9]|2[0-3]):(00|15|30|45)$/

const initialValues = {
    projectName: "",
    address: "",
    areaId: "",
    latitude: "",
    longitude: "",
    customerId: "",
    description: "",
    isViewForecast: "",
    isViewRadiation: "",
    areaOfFloor: 0,
    amountOfPeople: 0,
    emissionFactorCo2Electric: 0,
    emissionFactorCo2Gasoline: 0,
    emissionFactorCo2Charcoal: 0,
}

const AddProject = () => {
    const { t } = useTranslation();

    const [errorsValidate, setErrorsValidate] = useState([]);

    const [error, setError] = useState(null);

    const validationSchema = Yup.object().shape({
        projectName: Yup.string().required(t('validate.project.PROJECT_NAME_NOT_BLANK')).max(100, t('validate.project.PROJECT_NAME_MAX_SIZE_ERROR')),
        latitude: Yup.number().required(t('validate.project.PROJECT_LATITUDE_NOT_BLANK')).max(85.05112878, t('validate.project.PROJECT_LATITUDE_MAX_ERROR')).min(-85.05112878, t('validate.project.PROJECT_LATITUDE_MIN_ERROR')),
        longitude: Yup.number().required(t('validate.project.PROJECT_LONGITUDE_NOT_BLANK')).max(180, t('validate.project.PROJECT_LONGITUDE_MAX_ERROR')).min(-180, t('validate.project.PROJECT_LONGITUDE_MIN_ERROR'))
    })

    const history = useHistory();

    const [customers, setCustomer] = useState([]);

    const [areas, setArea] = useState([]);

    const listCustomer = async () => {
        let res = await customerService.getListCustomer();
        if (res.status === 200) {
            setCustomer(res.data);
        }
    };

    const listArea = async () => {
        let res = await areaService.listArea();
        if (res.status === 200) {
            setArea(res.data.areas);
        }
    };


    const formik = useFormik({
        initialValues,
        validationSchema,
        onSubmit: async (data) => {
            data.areaId = document.getElementById('area-id').value;
            data.customerId = document.getElementById('customer-id').value;
            let newProject = {
                projectName: data.projectName,
                address: data.address,
                areaId: data.areaId,
                latitude: data.latitude,
                longitude: data.longitude,
                customerId: data.customerId,
                description: data.description,
                areaOfFloor: data.areaOfFloor != null ? data.areaOfFloor : 0,
                amountOfPeople: data.amountOfPeople != null ? data.amountOfPeople : 0,
                emissionFactorCo2Electric: data.emissionFactorCo2Electric != null ? data.emissionFactorCo2Electric : 0,
                emissionFactorCo2Gasoline: data.emissionFactorCo2Gasoline != null ? data.emissionFactorCo2Gasoline : 0,
                emissionFactorCo2Charcoal: data.emissionFactorCo2Charcoal != null ? data.emissionFactorCo2Charcoal : 0,
                isViewForecast: data.isViewForecast ? 1 : 0,
                isViewRadiation: data.isViewRadiation ? 1 : 0
            };
            let response = await projectService.addProject(newProject);
            if (response.status === 200) {
                history.push({
                    pathname: "/category/project",
                    state: {
                        status: 200,
                        message: "INSERT_SUCCESS"
                    }
                });
            } else if (response.status === 500) {
                setError(t('validate.project.INSERT_FAILED'));
            } else if (response.status === 400) {
                setErrorsValidate(Array.from(new Set(response.data.errors)))
            }
        }
    });

    useEffect(() => {
        document.title = t('content.category.project.add.title');
        listCustomer();
        listArea();

    }, [])

    return (
        <div id="page-body">
            <div id="main-title">
                <h5 className="d-block mb-0 float-left text-uppercase"><i className="far fa-file-lines"></i> &nbsp;{t('content.category.project.add.title')}</h5>
            </div>

            <div id="main-content">

                {
                    (error != null) &&
                    <div className="alert alert-danger" role="alert">
                        <p className="m-0 p-0">{error}</p>
                    </div>
                }

                {
                    ((formik.errors.projectName && formik.touched.projectName) || (formik.errors.latitude && formik.touched.latitude === true) || (formik.errors.longitude && formik.touched.longitude)
                        || (formik.errors.shift1 && formik.touched.shift1) || (formik.errors.shift2 && formik.touched.shift2) || (formik.errors.shift3 && formik.touched.shift3)) &&
                    <div className="alert alert-warning" role="alert">
                        <p className="m-0 p-0">{formik.errors.projectName}</p>
                        <p className="m-0 p-0">{formik.errors.latitude}</p>
                        <p className="m-0 p-0">{formik.errors.longitude}</p>
                        <p className="m-0 p-0">{formik.errors.shift1}</p>
                        <p className="m-0 p-0">{formik.errors.shift2}</p>
                        <p className="m-0 p-0">{formik.errors.shift3}</p>
                    </div>
                }

                {
                    (errorsValidate.length > 0) &&
                    <div className="alert alert-warning" role="alert">
                        {
                            errorsValidate?.map((error, index) => {
                                return (<p key={index} className="m-0 p-0">{t('validate.project.' + error)}</p>)
                            })
                        }
                    </div>
                }
                <form onSubmit={formik.handleSubmit}>
                    <table className="table table-input">
                        <tbody>
                            <tr>
                                <th width="160px">{t('content.category.project.project_name')}<span className="required">※</span></th>
                                <td colSpan={3}>
                                    <input type="text" className="form-control" style={{ fontSize: "13px" }} name="projectName" maxLength={255} onChange={formik.handleChange} />
                                </td>
                            </tr>
                            <tr>
                                <th width="160px">{t('content.category.customer.customer_name')}</th>
                                <td colSpan={3}>
                                    <select id="customer-id" className="custom-select block" style={{ fontSize: "13px" }} name="customerId" onChange={formik.handleChange}>
                                        {
                                            customers?.map((customer) =>
                                                <option value={customer.customerId} key={customer.customerId}>{customer.customerName}</option>
                                            )
                                        }
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <th width="160px">{t('content.category.area.lable_area_name')}</th>
                                <td colSpan={3}>
                                    <select id="area-id" className="custom-select block" style={{ fontSize: "13px" }} name="areaId" onChange={formik.handleChange}>
                                        {
                                            areas?.map((area) =>
                                                <option value={area.areaId} key={area.areaId}>{area.areaName}</option>
                                            )
                                        }
                                    </select>
                                </td>
                            </tr>
                            <tr>

                                <th width="160px" >{t('content.forecast_capacity')}</th>
                                <td colSpan={3}>
                                    <div className="form-control" >
                                        <Checkbox id="project-is-view-forecast" icon={<Icon.FiCheck color="#174A41" size={20} />} className="custom-checkbox-project" name="isViewForecast" onChange={checked => formik.setFieldValue("isViewForecast", checked)} />
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <th width="160px">{t('content.radiation_power')}</th>
                                <td colSpan={3}>
                                    <div className="form-control">
                                        <Checkbox id="project-is-view-radiantion" icon={<Icon.FiCheck color="#174A41" size={20} />} name="isViewRadiation" className="custom-checkbox-project" onChange={checked => formik.setFieldValue("isViewRadiation", checked)} />
                                    </div>
                                </td>

                            </tr>
                            <tr>
                                <th width="160px">{t('content.longitude')}<span className="required">※</span></th>
                                <td colSpan={3}>
                                    <input type="number" step={"0.0000000001"} className="form-control" style={{ fontSize: "13px" }} name="longitude" onChange={formik.handleChange}></input>
                                </td>

                            </tr>
                            <tr>
                                <th width="160px">{t('content.latitude')}<span className="required">※</span></th>
                                <td colSpan={3}>
                                    <input type="number" step={"0.0000000001"} className="form-control" style={{ fontSize: "13px" }} name="latitude" onChange={formik.handleChange}></input>
                                </td>

                            </tr>
                            <tr>
                                <th width="160px">{"Diện tích mặt sàn"}</th>
                                <td colSpan={3}>
                                    <input type="number" className="form-control" style={{ fontSize: "13px" }} name="areaOfFloor" maxLength={1000} onChange={formik.handleChange} />
                                </td>
                            </tr>
                            <tr>
                                <th width="160px">{"Số người"}</th>
                                <td colSpan={3}>
                                    <input type="number" className="form-control" style={{ fontSize: "13px" }} name="amountOfPeople" maxLength={1000} onChange={formik.handleChange} />
                                </td>
                            </tr>
                            <tr>
                                <th width="160px">{"Hệ số phát thải CO2 điện"}</th>
                                <td colSpan={3}>
                                    <input type="number" className="form-control" style={{ fontSize: "13px" }} name="emissionFactorCo2Electric" maxLength={1000} onChange={formik.handleChange} />
                                </td>
                            </tr>
                            <tr>
                                <th width="160px">{"Hệ số phát thải CO2 xăng"}</th>
                                <td colSpan={3}>
                                    <input type="number" className="form-control" style={{ fontSize: "13px" }} name="emissionFactorCo2Gasoline" maxLength={1000} onChange={formik.handleChange} />
                                </td>
                            </tr>
                            <tr>
                                <th width="160px">{"Hệ số phát thải CO2 than"}</th>
                                <td colSpan={3}>
                                    <input type="number" className="form-control" style={{ fontSize: "13px" }} name="emissionFactorCo2Charcoal" maxLength={1000} onChange={formik.handleChange} />
                                </td>
                            </tr>

                            <tr>
                                <th width="160px">{t('content.address')}</th>
                                <td colSpan={3}>
                                    <input type="text" className="form-control" style={{ fontSize: "13px" }} name="address" maxLength={500} onChange={formik.handleChange} />
                                </td>
                            </tr>
                            <tr>
                                <th width="160px">{t('content.description')}</th>
                                <td colSpan={3}>
                                    <input type="text" className="form-control" style={{ fontSize: "13px" }} name="description" maxLength={1000} onChange={formik.handleChange} />
                                </td>
                            </tr>
                        </tbody>
                    </table>

                    <div id="main-button">
                        <button type="submit" className="btn btn-outline-secondary btn-agree mr-1">
                            <i className="fa-solid fa-check"></i>
                        </button>
                        <button type="button" className="btn btn-outline-secondary btn-cancel" onClick={() => history.push("/category/project")}>
                            <i className="fa-solid fa-xmark"></i>
                        </button>
                    </div>
                </form>

            </div>
        </div>
    )
}
export default AddProject;