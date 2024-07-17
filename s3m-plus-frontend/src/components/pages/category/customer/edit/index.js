import React, { useState, useEffect } from "react";
import { useHistory, useParams } from "react-router-dom";
import customerService from "../../../../../services/CustomerService";
import $ from 'jquery';
import { useFormik } from "formik";
import * as Yup from 'yup';
import { useTranslation } from "react-i18next";

const EditCustomer = () => {
    const { t } = useTranslation();
    const history = useHistory();
    const [errorsValidate, setErrorsValidate] = useState([]);
    const [error, setError] = useState(null);
    const [customer, setCustomer] = useState({
        customerCode: '',
        customerName: '',
        description: ''
    });

    const [information, setInformation] = useState({
        customerCode: '',
        customerId: ''
    });

    const param = useParams();
    const getCustomer = async () => {
        let res = await customerService.getCustomer(param.id);
        if (res.status === 200) {
            setCustomer(res.data);
        }
    };

    const checkExistedCode = async (code) => {
        setInformation({
            customerCode: code,
            customerId: param.id
        })
        let response = await customerService.checkCustomerCode(information);
        if (response.status === 200) {
            return !response.data;
        }

    };

    const validationSchema = Yup.object().shape({
        customerName: Yup.string().max(100, 'Tên khách hàng chỉ có tối đa 100 ký tự').required('Tên khách hàng không bỏ trống'),
        customerCode: Yup.string().max(8, 'Mã khách hàng chỉ có tối đa 8 ký tự').required('Existed Code', 'Mã khách hàng không bỏ trống', function (customerCode) {
            return checkExistedCode(customerCode);
        }).required('Mã khách hàng không bỏ trống')
    });

    const formik = useFormik({
        initialValues: customer,
        enableReinitialize: true,
        validationSchema,
        onSubmit: async (data) => {
            console.log(data);
            let res = await customerService.updateCustomer(param.id, data);
            if (res.status === 200) {
                history.push({
                    pathname: "/category/customer",
                    state: {
                        status: 200,
                        message: "UPDATE_SUCCESS"
                    }
                });
            } else if (res.status === 400) {
                setErrorsValidate(res.data.errors);
            } else {
                setError(t('validate.customer.UPDATE_FAILED'));
            }
        }
    });

    useEffect(() => {
        document.title = t('content.category.customer.customer_update');
        getCustomer();
    }, []);

    return (
        <>
            <div id="page-body">
                <div id="main-title">
                    <h5 className="d-block mb-0 float-left"><i className="fas fa-user-tie"></i> &nbsp;{t('content.category.customer.customer_update')}</h5>
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
                <div className={(formik.errors.customerName && formik.touched.customerName === true) || (formik.errors.customerCode && formik.touched.customerCode === true) ? "alert alert-warning" : "hidden"} role="alert">
                    <p className="m-0 p-0">{formik.errors.customerName}</p>
                    <p className="m-0 p-0">{formik.errors.customerCode}</p>
                </div>

                <div id="main-content">
                    <form onSubmit={formik.handleSubmit}>

                        <table className="table table-input">
                            <tbody>
                                <tr>
                                    <th width="150px">{t('content.category.customer.customer_id')}<span className="required">※</span></th>
                                    <td>
                                        <input type="text" className="form-control" name="customerCode" maxLength={100} value={formik.values.customerCode} onChange={formik.handleChange} />
                                    </td>
                                </tr>
                                <tr>
                                    <th width="150px">{t('content.category.customer.customer_name')}<span className="required">※</span></th>
                                    <td>
                                        <input type="text" className="form-control" name="customerName" maxLength={255} value={formik.values.customerName} onChange={formik.handleChange} />
                                    </td>
                                </tr>
                                <tr>
                                    <th width="150px">{t('content.category.customer.description')}</th>
                                    <td>
                                        <input type="text" className="form-control" name="description" maxLength={1000} value={formik.values.description} onChange={formik.handleChange} />
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <div id="main-button">
                            <button type="submit" className="btn btn-outline-secondary btn-agree mr-1" >
                                <i className="fa-solid fa-check"></i>
                            </button>
                            <button type="button" className="btn btn-outline-secondary btn-cancel" onClick={() => history.push("/category/customer")}>
                                <i className="fa-solid fa-xmark"></i>
                            </button>
                        </div>
                    </form>
                </div>


            </div>
        </>
    )
}

export default EditCustomer;