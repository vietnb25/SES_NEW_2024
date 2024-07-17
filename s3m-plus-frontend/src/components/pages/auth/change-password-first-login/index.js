import { useFormik } from "formik";
import React from "react";
import { useEffect } from "react";
import { Link, useHistory } from "react-router-dom";
import AUTH_API from './../../../../constants/auth_api';
import axios from "axios";
import { useState } from "react";
import * as Yup from 'yup';
import { useTranslation } from "react-i18next";
import CONS from "../../../../constants/constant";

const ChangePasswordFirstLogin = (props) => {
    const history = useHistory();
    const [status, setStatus] = useState(0);
    const [message, setMessage] = useState(0);
    const { t } = useTranslation();
    const [errorsValidate, setErrorsValidate] = useState([]);
    const [error, setError] = useState(null);

    const formik = useFormik({
        initialValues: {
            username: "",
            currentPassword: "",
            password: "",
            confirmPassword: ""
        },
        validationSchema: Yup.object().shape({
            currentPassword: Yup.string().required(t('validate.first_login.CURRENT_PASSWORD_NOT_BLANK')),
            password: Yup.string()
                .required(t('validate.first_login.PASSWORD_NOT_BLANK'))
                .min(8, t('validate.first_login.PASSWORD_MIN_SIZE_ERROR'))
                .max(255, t('validate.first_login.PASSWORD_MAX_SIZE_ERROR'))
                .matches("^(?:(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*)", t('validate.first_login.PASSWORD_PATTERN_ERROR')),
            confirmPassword: Yup.string().required(t('validate.first_login.CONFIRM_PASSWORD_NOT_BLANK')).oneOf([Yup.ref('password')], t('validate.first_login.CONFIRM_PASSWORD_NOT_MATCH')),
        }),
        onSubmit: async data => {
            data.username = props.history.location.state?.username;
            await axios.post(AUTH_API.CHANGE_PASSWORD_FIRST_LOGIN, data).then(response => {
                if (response.status === 200) {
                    setStatus(response.status);
                    setMessage(t('content.auth.first_login.change_password_success'));
                }
            }).catch(error => {
                if (error.response.data.errors && error.response.data.status === 400) {
                    setErrorsValidate(error.response.data.errors);
                    return
                }

                if (error.response.data === CONS.ERROR.CURRENT_PASSWORD_NOT_MATCH) {
                    setError(t('validate.first_login.CURRENT_PASSWORD_NOT_MATCH'));
                }else if(error.response.data === CONS.ERROR.ERROR_UPDATE_PASSWORD){
                    setError(t('validate.first_login.ERROR_UPDATE_PASSWORD'));
                }else if(error.response.data === CONS.ERROR.NEW_PASSWORD_SAME_CURRENT_PASSWORD){
                    setError(t('validate.first_login.NEW_PASSWORD_SAME_CURRENT_PASSWORD'));
                }
            })
        }
    })

    const checkUserIsFirstLogin = () => {
        let isFirstLogin = props.history.location.state?.isFirstLogin;
        let username = props.history.location.state?.username;
        if (!isFirstLogin && !username) {
            history.push("/login");
        }
    }

    useEffect(() => {
        checkUserIsFirstLogin();
    }, [])

    return (
        <>
            <div className="align">
                <div className="section-login">
                    <form className="form login" onSubmit={formik.handleSubmit}>
                        <div className="form-field text-center login-logo">
                            <img src="/resources/image/s3m-logo-dark.png" alt="S3M" style={{ height: "32px" }} />
                        </div>
                        {
                            status !== 200 ? 
                            <>
                                {
                                    (error != null) &&
                                    <div style={{ width: "280px", margin: "0 auto" }}>
                                        <div className="alert alert-danger" role="alert">
                                            <p className="m-0 p-0">{error}</p>
                                        </div>
                                    </div>
                                }
                                {
                                    (errorsValidate.length > 0) &&
                                    <div style={{ width: "280px", margin: "0 auto" }}>
                                        <div className="alert alert-warning" role="alert">
                                            {
                                                errorsValidate.map((error, index) => {
                                                    return (<p key={index} className="m-0 p-0">{t('validate.first_login.' + error)}</p>)
                                                })
                                            }
                                        </div>
                                    </div>
                                }
                                {
                                    ((formik.errors.currentPassword && formik.touched.currentPassword) ||
                                        (formik.errors.password && formik.touched.password) || 
                                        (formik.errors.confirmPassword && formik.touched.confirmPassword)) &&
                                    <div style={{ width: "280px", margin: "0 auto" }}>
                                        <div className="alert alert-warning" role="alert">
                                            <p className="m-0 p-0">{formik.errors.currentPassword}</p>
                                            <p className="m-0 p-0">{formik.errors.password}</p>
                                            <p className="m-0 p-0">{formik.errors.confirmPassword}</p>
                                        </div>
                                    </div>
                                }
                                <div className="form-field">
                                    <label htmlFor="login-username">
                                        <i className="fa-solid fa-key"></i>
                                        <span className="hidden">{t('content.auth.first_login.current_password')}</span>
                                    </label>
                                    <input onChange={formik.handleChange} type="password" name="currentPassword" className="form-input" placeholder={t('content.auth.first_login.current_password')} autoComplete="off" />
                                </div>
                                <div className="form-field">
                                    <label htmlFor="login-username">
                                        <i className="fa-solid fa-key"></i>
                                        <span className="hidden">{t('content.auth.first_login.new_password')}</span>
                                    </label>
                                    <input onChange={formik.handleChange} type="password" name="password" className="form-input" placeholder={t('content.auth.first_login.new_password')} autoComplete="off" />
                                </div>
                                <div className="form-field">
                                    <label htmlFor="login-password">
                                        <i className="fa-solid fa-key"></i>
                                        <span className="hidden">{t('content.auth.first_login.confirm_password')}</span>
                                    </label>
                                    <input onChange={formik.handleChange} type="password" name="confirmPassword" className="form-input" placeholder={t('content.auth.first_login.confirm_password')} autoComplete="off" />
                                </div>
                                <div className="form-field">
                                    <input type="submit" value={t('content.auth.first_login.confirm')} />
                                </div>
                            </> : 
                            <>
                                <div style={{ width: "280px", margin: "0 auto" }}>
                                    <div className="alert alert-success text-center" role="alert">
                                        <p className="m-0 p-0">{message}</p>
                                    </div>
                                </div>
                                <p className="text-center pb-3">
                                    <Link to="/login">{t('content.auth.first_login.login')}</Link>
                                </p>
                            </>
                        }
                        
                    </form>
                </div>
            </div>
        </>
    )
}

export default ChangePasswordFirstLogin;