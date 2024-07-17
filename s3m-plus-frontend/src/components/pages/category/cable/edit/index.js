import React, { useEffect, useState } from "react";
import { useHistory, useParams } from 'react-router-dom';
import CablesService from "../../../../../services/CablesService";
import $ from 'jquery';
import * as Yup from "yup";
// import isEmpty from "validator/lib/isEmpty"
import { useFormik } from "formik";
import { useTranslation } from "react-i18next";

const EditCable = () => {
    const param = useParams();
    let history = useHistory();
    const [cable, setCable] = useState({
        cableId: "",
        cableName: "",
        current: "",
        description: ""
    });
    const [errorsValidate, setErrorsValidate] = useState([]);
    const [error, setError] = useState(null);
    const { cableId, cableName, current, description } = cable;
    const {t} = useTranslation();

    const onInputChange = (e) => {
        setCable({ ...cable, [e.target.name]: e.target.value });
    };

    const loadCable = async () => {
        let res = await CablesService.detailCable(param.id);
        if (res.status === 200) {
            setCable(res.data);
        }
    };

    const formik = useFormik({
        initialValues:cable,
        validationSchema: Yup.object().shape({
            cableName: Yup.string().required(t('validate.cable.CABLE_NAME_NOT_BLANK')).max(255, t('validate.cable.CABLE_NAME_MAX_LENGTH')),
            current: Yup.number().required(t('validate.cable.CURRENT_NOT_BLANK')).max(8388607, t('validate.cable.CURRENT_MAX_VALUE')),
            description: Yup.string().max(100,t('validate.cable.DESCRIPTION_MAX_LENGTH'))
        }),
        enableReinitialize: true,
        onSubmit: async cableData =>{
            let res = await CablesService.updateCable(param.id, cableData);
            if (res.status === 400) {
                setErrorsValidate(res.data.errors);
                return
            }
            if (res.status === 200) {
                history.push({
                    pathname: "/category/cables",
                    state: {
                        status: 200,
                        message: "UPDATE_SUCCESS"
                    }
                });
            }else{
                setError(t('validate.cable.UPDATE_FAILED'))
            }
        }
    })

    useEffect(() => {
        document.title = t('content.update_cable');
        loadCable();
    }, []);

    return (

        <div id="page-body">
            <div id="main-title">
                <h5 className="d-block mb-0 float-left"><i className="fas fa-lines-leaning"></i> &nbsp;{t('content.update_cable')}</h5>
            </div>
            {
                (error != null) &&
                <div className="alert alert-danger" role="alert">
                    <p className="m-0 p-0">{t('validate.cable.UPDATE_FAILED')}</p>
                </div>
            }
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
                ((formik.errors.cableName && formik.touched.cableName) || 
                    (formik.errors.current && formik.touched.current) || 
                    (formik.errors.description && formik.touched.description)) &&
                <div className="alert alert-warning" role="alert">
                    <p className="m-0 p-0">{formik.errors.cableName}</p>
                    <p className="m-0 p-0">{formik.errors.current}</p>
                    <p className="m-0 p-0">{formik.errors.description}</p>
                </div>
            }
            <form onSubmit={formik.handleSubmit}>
                <div id="main-content">
                    {
                        <table className="table table-input">
                            <tbody>
                                <tr>
                                    <th width="150px">{t('content.cable_id')}</th>
                                    <td>
                                        <input type="text" className="form-control" name="cableId" value={cableId} onChange={(e) => onInputChange(e)} disabled />
                                    </td>
                                </tr>
                                <tr>
                                    <th width="150px">{t('content.cable_name')}<span className="required">※</span></th>
                                    <td>
                                        <input type="text" className="form-control" name="cableName" value={cableName} onChange={(e) => onInputChange(e)} />
                                    </td>
                                </tr>
                                <tr>
                                    <th width="150px">{t('content.current')}<span className="required">※</span></th>
                                    <td>
                                        <input type="number" className="form-control" name="current" value={current} onChange={(e) => onInputChange(e)} />
                                    </td>
                                </tr>
                                <tr>
                                    <th width="150px">{t('content.description')}</th>
                                    <td>
                                        <input type="text" className="form-control" name="description" value={description} onChange={(e) => onInputChange(e)} />
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
                            history.push("/category/cables")
                        }}>
                            <i className="fa-solid fa-xmark"></i>
                        </button>
                    </div>
                </div>
            </form>
        </div>
    )
}

export default EditCable;