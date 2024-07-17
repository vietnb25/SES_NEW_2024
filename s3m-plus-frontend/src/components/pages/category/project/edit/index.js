import React from "react";
import { useFormik } from "formik";
import customerService from "../../../../../services/CustomerService";
import areaService from "../../../../../services/AreaService";
import projectService from "../../../../../services/ProjectService";
import { useState } from "react";
import { useEffect } from "react";
import { useHistory, useParams } from "react-router-dom";
import Checkbox from "react-custom-checkbox";
import * as Icon from "react-icons/fi";
import * as Yup from 'yup';
import { useTranslation } from "react-i18next";


const $ = window.$;

const EditProject = () => {
    const { t } = useTranslation();

    const validationSchema = Yup.object().shape({
        projectName: Yup.string().required(t('validate.project.PROJECT_NAME_NOT_BLANK')).max(100, t('validate.project.PROJECT_NAME_MAX_SIZE_ERROR')),
        latitude: Yup.number().required(t('validate.project.PROJECT_LATITUDE_NOT_BLANK')).max(85.05112878, t('validate.project.PROJECT_LATITUDE_MAX_ERROR')).min(-85.05112878, t('validate.project.PROJECT_LATITUDE_MIN_ERROR')),
        longitude: Yup.number().required(t('validate.project.PROJECT_LONGITUDE_NOT_BLANK')).max(180, t('validate.project.PROJECT_LONGITUDE_MAX_ERROR')).min(-180, t('validate.project.PROJECT_LONGITUDE_MIN_ERROR')),
        description: Yup.string().required(t('validate.project.PROJECT_DESCRIPTION_NOT_BLANK')).max(225, t('validate.project.PROJECT_DESCRIPTION_MAX_SIZE_ERROR')),
    })
    const [error, setError] = useState(null);

    const history = useHistory();

    const [errorsValidate, setErrorsValidate] = useState([]);

    const [customers, setCustomer] = useState([]);

    const [areas, setArea] = useState([]);

    const param = useParams();

    const projectId = param.id;

    const [project, setProject] = useState({
        projectName: "",
        address: "",
        latitude: "",
        longitude: "",
        description: "",
        isViewRadiation: "",
        isViewForecast: "",
        customerId: "",
        areaId: "",
        amountOfPeople: 0,
        emissionFactorCo2Electric: 0.0,
        emissionFactorCo2Gasoline: 0.0,
        emissionFactorCo2Charcoal: 0.0,
        areaOfFloor: 0.0,
        projectId: projectId
    });

    const getProject = async () => {
        let res = await projectService.getProject(projectId);
        if (res.status === 200) {
            setProject(res.data);
        }
    };

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
        initialValues: project,
        enableReinitialize: true,
        validationSchema,
        onSubmit: async (data) => {
            let newProject = {
                projectId: data.projectId,
                projectName: data.projectName,
                address: data.address,
                areaId: data.areaId,
                latitude: data.latitude,
                longitude: data.longitude,
                customerId: data.customerId,
                areaOfFloor: data.areaOfFloor != null ? data.areaOfFloor : 0,
                amountOfPeople: data.amountOfPeople != null ? data.amountOfPeople : 0,
                emissionFactorCo2Electric: data.emissionFactorCo2Electric != null ? data.emissionFactorCo2Electric : 0,
                emissionFactorCo2Gasoline: data.emissionFactorCo2Gasoline != null ? data.emissionFactorCo2Gasoline : 0,
                emissionFactorCo2Charcoal: data.emissionFactorCo2Charcoal != null ? data.emissionFactorCo2Charcoal : 0,
                description: data.description,
                isViewForecast: data.isViewForecast ? 1 : 0,
                isViewRadiation: data.isViewRadiation ? 1 : 0
            };
            let response = await projectService.updateProject(newProject);
            if (response.status === 200) {
                history.push({
                    pathname: "/category/project",
                    state: {
                        status: 200,
                        message: "UPDATE_SUCCESS"
                    }
                });
            } else if (response.data === 500) {
                setError(t('validate.project.UPDATE_FAILED'));
            } else if (response.status === 400) {
                setErrorsValidate(Array.from(new Set(response.data.errors)))
            }
        }
    });
    useEffect(() => {
        document.title = t('content.category.project.edit.title');
        listCustomer();
        listArea();
        getProject();
    }, [])

    return (
        <div id="page-body">
            <div id="main-title">
                <h5 className="d-block mb-0 float-left text-uppercase"><i className="far fa-file-lines"></i> &nbsp;{t('content.category.project.edit.title')}</h5>
            </div>

            <div id="main-content">
                {
                    (error != null) &&
                    <div className="alert alert-danger" role={"alert"}>
                        <p className="m-0 p-0">{error}</p>
                    </div>
                }

                {
                    ((formik.errors.projectName && formik.touched.projectName) || (formik.errors.latitude && formik.touched.latitude === true) || (formik.errors.longitude && formik.touched.longitude) || (formik.errors.description && formik.touched.projectName) ) &&
                    <div className="alert alert-warning" role="alert">
                        <p className="m-0 p-0">{formik.errors.projectName}</p>
                        <p className="m-0 p-0">{formik.errors.latitude}</p>
                        <p className="m-0 p-0">{formik.errors.longitude}</p>
                        <p className="m-0 p-0">{formik.errors.description}</p>
                    </div>
                }
                {
                    (errorsValidate.length > 0) &&
                    <div className="alert alert-warning" role="alert">
                        {
                            errorsValidate?.map((error, index) => {
                                console.log(error);
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
                                <td>
                                    <input type="text" className="form-control" style={{ fontSize: "13px" }} name="projectName" value={formik.values.projectName} onChange={formik.handleChange} />
                                </td>
                            </tr>
                            <tr>
                                <th width="160px">{t('content.category.customer.customer_name')}</th>
                                <td>
                                    <select id="customer-type-id" className="custom-select block" style={{ fontSize: "13px" }} name="customerId" value={formik.values.customerId} onChange={formik.handleChange}>
                                        {
                                            customers.map((customer) =>
                                                <option value={customer.customerId} key={customer.customerId} >{customer.customerName}</option>
                                            )
                                        }
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <th width="160px">{t('content.category.area.lable_area_name')}</th>
                                <td>
                                    <select id="area-type-id" className="custom-select block" style={{ fontSize: "13px" }} name="areaId" value={formik.values.areaId} onChange={formik.handleChange}>
                                        {
                                            areas.map((area) =>
                                                <option value={area.areaId} key={area.areaId} >{area.areaName}</option>
                                            )
                                        }
                                    </select>
                                </td>
                            </tr>
                            <tr>

                                <th width="160px" >{t('content.forecast_capacity')}</th>
                                <td>
                                    <div className="form-control" >
                                        <Checkbox id="project-is-view-forecast" icon={<Icon.FiCheck color="#174A41" size={20} />} className="custom-checkbox-project " name="isViewForecast" value={formik.values.isViewForecast} checked={formik.values.isViewForecast == 1 ? true : false} onChange={checked => formik.setFieldValue("isViewForecast", checked)} />
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <th width="160px">{t('content.radiation_power')}</th>
                                <td>
                                    <div className="form-control">
                                        <Checkbox id="project-is-view-radian" icon={<Icon.FiCheck color="#174A41" size={20} />} className="custom-checkbox-project " name="isViewRadiation" value={formik.values.isViewRadiation} checked={formik.values.isViewRadiation == 1 ? true : false} onChange={checked => formik.setFieldValue("isViewRadiation", checked)} />
                                    </div>
                                </td>

                            </tr>
                            <tr>
                                <th width="160px">{t('content.longitude')}<span className="required">※</span></th>
                                <td>
                                    <input type="number" step={"0.0000000001"} className="form-control" style={{ fontSize: "13px" }} name="longitude" value={formik.values.longitude || ""} onChange={formik.handleChange}></input>
                                </td>

                            </tr>
                            <tr>
                                <th width="160px">{t('content.latitude')}<span className="required">※</span></th>
                                <td>
                                    <input type="number" step={"0.0000000001"} className="form-control" style={{ fontSize: "13px" }} name="latitude" value={formik.values.latitude || ""} onChange={formik.handleChange}></input>
                                </td>

                            </tr>
                            <tr>
                                <th width="160px">{"Diện tích mặt sàn"}</th>
                                <td>
                                    <input type="number" className="form-control" style={{ fontSize: "13px" }} name="areaOfFloor" value={formik.values.areaOfFloor} maxLength={1000} onChange={formik.handleChange} />
                                </td>
                            </tr>
                            <tr>
                                <th width="160px">{"Số người"}</th>
                                <td>
                                    <input type="number" className="form-control" style={{ fontSize: "13px" }} name="amountOfPeople" value={formik.values.amountOfPeople} maxLength={1000} onChange={formik.handleChange} />
                                </td>
                            </tr>
                            <tr>
                                <th width="160px">{"Hệ số phát thải CO2 điện"}</th>
                                <td>
                                    <input type="number" className="form-control" style={{ fontSize: "13px" }} name="emissionFactorCo2Electric" value={formik.values.emissionFactorCo2Electric} maxLength={1000} onChange={formik.handleChange} />
                                </td>
                            </tr>
                            <tr>
                                <th width="160px">{"Hệ số phát thải CO2 xăng"}</th>
                                <td>
                                    <input type="number" className="form-control" style={{ fontSize: "13px" }} name="emissionFactorCo2Gasoline" value={formik.values.emissionFactorCo2Gasoline} maxLength={1000} onChange={formik.handleChange} />
                                </td>
                            </tr>
                            <tr>
                                <th width="160px">{"Hệ số phát thải CO2 than"}</th>
                                <td>
                                    <input type="number" className="form-control" style={{ fontSize: "13px" }} name="emissionFactorCo2Charcoal" value={formik.values.emissionFactorCo2Charcoal} maxLength={1000} onChange={formik.handleChange} />
                                </td>
                            </tr>
                            <tr>
                                <th width="160px">{t('content.address')}</th>
                                <td>
                                    <input type="text" className="form-control" name="address" style={{ fontSize: "13px" }} value={formik.values.address || ""} maxLength={500} onChange={formik.handleChange} />
                                </td>
                            </tr>
                            <tr>
                                <th width="160px">{t('content.description')}</th>
                                <td>
                                    <input type="text" className="form-control" name="description" style={{ fontSize: "13px" }} value={formik.values.description || ""} maxLength={1000} onChange={formik.handleChange} />
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

export default EditProject;