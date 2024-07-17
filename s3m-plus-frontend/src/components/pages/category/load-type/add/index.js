import { useFormik } from "formik";
import React, { useEffect, useState } from "react";
import { useHistory } from "react-router-dom";
import $ from 'jquery';
import * as Yup from "yup";
import { useTranslation } from "react-i18next";
import LoadTypeService from "../../../../../services/LoadTypeService";
import { useParams } from "react-router-dom/cjs/react-router-dom.min";

const initialValues = {
    loadTypeName: "",
    description: "",
}

const AddLoadType = () => {
    const param = useParams();
    const history = useHistory();
    const { t } = useTranslation();
    const [data, setData] = useState([]);
    const [dataSelect, setDataSelect] = useState([]);
    const [errorsValidate, setErrorsValidate] = useState([]);
    const [error, setError] = useState(null);

    const formik = useFormik({
        initialValues,
        onSubmit: async (data) => {
            let cusId = 1
            let res = await LoadTypeService.addLoadType(cusId, data);
            if (res.status === 400) {
                setErrorsValidate(res.data.errors);
                return
            }
            if (res.status === 200) {
                $.alert({
                    title: t('content.notification'),
                    content: t('content.category.load_type.list.add_success')
                });
                history.push({
                    pathname: "/category/load-type",
                    state: {
                        status: 200,
                        message: "INSERT_SUCCESS"
                    }
                });
            } else {
                setError(t('validate.cable.INSERT_FAILED'))
            }

        }
    });
    document.title = t('content.add_new_load_type')

    useEffect(() => {

    }, []);

    return (
        <div id="page-body">
            <form onSubmit={formik.handleSubmit}>
                <div id="main-title">
                    <h5 className="d-block mb-0 float-left"><i className="far fa-clone"></i> &nbsp;{t('content.category.load_type.add.header')}</h5>
                </div>
                <div id="main-content">
                    <table className="table table-input">
                        <tbody>
                            <tr>
                                <th width="220px">{t('content.category.load_type.lable_load_type_name')}<span className="required">※</span></th>
                                <td>
                                    <input type="text" onChange={formik.handleChange} name="loadTypeName" className="form-control" />
                                </td>
                            </tr>
                            <tr>
                                <th width="220px">{t('content.category.load_type.lable_description')}<span className="required">※</span></th>
                                <td>
                                    <input type="text" onChange={formik.handleChange} name="description" className="form-control" />
                                </td>
                            </tr>

                        </tbody>
                    </table>

                    <div id="main-button">
                        <button type="submit" className="btn btn-outline-secondary btn-agree mr-1" >
                            <i className="fa-solid fa-check"></i>
                        </button>
                        <button type="button" className="btn btn-outline-secondary btn-cancel" onClick={() => {
                            history.push("/category/load-type")
                        }}>
                            <i className="fa-solid fa-xmark"></i>
                        </button>
                    </div>
                </div>
            </form>
        </div>
    )
}

export default AddLoadType;