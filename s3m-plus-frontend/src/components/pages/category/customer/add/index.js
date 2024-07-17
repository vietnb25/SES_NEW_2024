import React, { useState, useEffect } from "react";
import { useHistory } from "react-router-dom";
import { useFormik } from 'formik';
import customerService from "../../../../../services/CustomerService";
import $ from "jquery";
import * as Yup from 'yup';
import { useTranslation } from "react-i18next";
import "./index.css";
import { Dialog } from 'primereact/dialog';
import { ProgressSpinner } from 'primereact/progressspinner';


const initialValues = {
    customerName: "",
    description: "",
    customerCode: ""
};

const AddCustomer = () => {
    const { t } = useTranslation();
    const [errorsValidate, setErrorsValidate] = useState([]);
    const [error, setError] = useState(null);
    const [status, setStatus] = useState(false);
    const [information, setInformation] = useState({
        customerCode: ''
    });

    const validationSchema = Yup.object().shape({
        customerName: Yup.string().max(100, t('validate.customer.CUSTOMER_NAME_MAX_SIZE_ERROR')).required(t('validate.customer.CUSTOMER_NAME_NOT_BLANK')),
        customerCode: Yup.string().max(8, t('validate.customer.CUSTOMER_CODE_MAX_SIZE_ERROR')).test('Existed Code', t("validate.customer.CUSTOMER_CODE_EXISTED"), function (customerCode) {
            return checkExistedCode(customerCode);
        }).required(t("validate.customer.CUSTOMER_CODE_NOT_BLANK"))
    });


    const checkExistedCode = async (code) => {
        setInformation({
            customerCode: code
        });
        let response = await customerService.checkCustomerCode(information);
        if (response.status === 200) {
            return !response.data;
        }

    };

    const history = useHistory();

    const formik = useFormik({
        initialValues,
        validationSchema,
        onSubmit: async (data) => {
            setStatus(true);
            let response = await customerService.addCustomer(data);
            if (response.status === 200) {
                history.push({
                    pathname: "/category/customer",
                    state: {
                        status: 200,
                        message: "INSERT_SUCCESS"
                    }
                });
            } else if (response.status === 400) {
                setErrorsValidate(response.data.errors);
            } else if (response.status === 500) {
                setError(t('validate.customer.INSERT_FAILED'));
            }
        }
    });

    useEffect(() => {
        document.title = t('content.category.customer.customer_add');
    }, []);

    return (
        <>
            <div id="page-body">
                <div id="main-title">
                    <h5 className="d-block mb-0 float-left text-uppercase"><i className="fas fa-user-tie"></i> &nbsp;{t('content.category.customer.customer_add')}</h5>
                </div>
                {
                    (error != null) &&
                    <div className="alert alert-danger" role="alert">
                        <p className="m-0 p-0">{error}</p>
                    </div>
                }
                {
                    (errorsValidate?.length > 0) &&
                    <div className="alert alert-warning" role="alert">
                        {
                            errorsValidate?.map((error, index) => {
                                return (<p key={index} className="m-0 p-0">{t('validate.customer.' + error)}</p>)
                            })
                        }
                    </div>
                }
                {
                    ((formik.errors.customerName && formik.touched.customerName) || (formik.errors.customerCode && formik.touched.customerCode === true)) &&
                    <div className="alert alert-warning" role="alert">
                        <p className="m-0 p-0">{formik.errors.customerName}</p>
                        <p className="m-0 p-0">{formik.errors.customerCode}</p>
                    </div>
                }
                <div id="main-content" >
                    <form onSubmit={formik.handleSubmit}>
                        <table className="table table-input">
                            <tbody>
                                <tr>
                                    <th width="150px">{t('content.category.customer.customer_id')}<span className="required">※</span></th>
                                    <td>
                                        <input type="text" className="form-control" name="customerCode" maxLength={100} onChange={formik.handleChange} />
                                    </td>
                                </tr>
                                <tr>
                                    <th width="150px">{t('content.category.customer.customer_name')}<span className="required">※</span></th>
                                    <td>
                                        <input type="text" className="form-control" name="customerName" maxLength={255} onChange={formik.handleChange} />
                                    </td>
                                </tr>
                                <tr>
                                    <th width="150px">{t('content.category.customer.description')}</th>
                                    <td>
                                        <input type="text" className="form-control" name="description" maxLength={1000} onChange={formik.handleChange} />
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <div id="main-button">
                            {
                                status === false && <>
                                    <button type="submit" className="btn btn-outline-secondary btn-agree mr-1">
                                        <i className="fa-solid fa-check"></i>
                                    </button>
                                    <button type="button" className="btn btn-outline-secondary btn-cancel" onClick={() => history.goBack()}>
                                        <i className="fa-solid fa-xmark"></i>
                                    </button>
                                </>
                            }
                            <Dialog visible={status} closable={false} className="text-center">
                                <div className="text-center loading" id="loading" style={{ fontSize: "16px" }}>
                                    <img height="60px" className="mt-0.5" src="/resources/image/loading.gif" alt="loading" />
                                </div><p className="mt-0.5">{t('content.category.customer.configure')}</p>
                            </Dialog>
                        </div>
                    </form>

                </div>
            </div>
        </>
    )
}

export default AddCustomer;