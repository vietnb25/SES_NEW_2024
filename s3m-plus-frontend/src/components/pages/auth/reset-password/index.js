import React from "react";
import { useEffect } from "react";
import { Link, useHistory, useParams } from "react-router-dom";
import axios from "axios";
import { useState } from "react";
import AUTH_API from "../../../../constants/auth_api";
import { useFormik } from "formik";
import * as Yup from "yup";
import { useTranslation } from "react-i18next";
import CONS from "../../../../constants/constant";

const ResetPassword = () => {
    const history = useHistory();
    const { token } = useParams();
    const [status, setStatus] = useState(0);
    const [statusResetSuccess, setStatusResetSuccess] = useState();
    const [message, setMessage] = useState("");
    const [errorsValidate, setErrorsValidate] = useState([]);
    const [error, setError] = useState(null);
    const { t } = useTranslation();

    const formik = useFormik({
        initialValues: {
            userId: "",
            password: "",
            confirm_password: ""
        },
        validationSchema: Yup.object().shape({
            password: Yup.string()
                .required(t('validate.user.PASSWORD_NOT_BLANK'))
                .min(8, t('validate.user.PASSWORD_MIN_SIZE_ERROR'))
                .max(255, t('validate.user.PASSWORD_MAX_SIZE_ERROR'))
                .matches("^(?:(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*)", t('validate.user.PASSWORD_PATTERN_ERROR')),
            confirm_password: Yup.string().required(t('validate.user.CONFIRM_PASSWORD_NOT_BLANK')).oneOf([Yup.ref('password'), null], t('validate.user.CONFIRM_PASSWORD_NOT_MATCH'))
        }),
        onSubmit: async data => {
            await axios.post(AUTH_API.RESET_PASSWORD, data).then(res => {
                if (res.status === 200) {
                    setStatusResetSuccess(res.status);
                    setMessage(t('content.auth.reset_password.change_password_success'));
                }else{
                    setError(t('content.auth.reset_password.change_password_failed'));
                }
            }).catch(error => {
                if(error.response.status === 400){
                    if (error.response.data.errors) {
                        setErrorsValidate(error.response.data.errors);
                    }
                }else{
                    setError(t('content.auth.reset_password.change_password_failed'));
                }
            });
        }
    });

    const checkExistToken = async () => {
        if (token && token.length === CONS.RESET_PASSWORD_TOKEN_LENGTH) {
            let url = AUTH_API.RESET_PASSWORD + "/" + token;
            await axios.get(url).then(res => {
                if (res.status === 200) {
                    setStatus(res.status);
                    formik.setFieldValue("userId", res.data.id);
                }
            }).catch(error => {
                setError(t('content.auth.reset_password.token_is_expired'));
            });
        } else {
            history.push('/login');
        }
    }

    useEffect(() => {
        document.title = t('content.auth.reset_password.change_password');
        checkExistToken();
    }, [token]);

    return (
        <>
            <div className="align">
                <div className="section-login">
                    {
                        status === 200 ?
                            <form className="form login" onSubmit={formik.handleSubmit}>
                                <div className="form-field text-center login-logo">
                                    <img src="/resources/image/s3m-logo-dark.png" alt="S3M" style={{ height: "32px" }} />
                                </div>
                                {
                                    statusResetSuccess !== 200 ?
                                        <>
                                            {
                                                ((formik.errors.password && formik.touched.password) ||
                                                    (formik.errors.confirm_password && formik.touched.confirm_password)) &&
                                                <div style={{ width: "280px", margin: "0 auto" }}>
                                                    <div className="alert alert-warning" role="alert">
                                                        <p className="m-0 p-0">{formik.errors.password}</p>
                                                        <p className="m-0 p-0">{formik.errors.confirm_password}</p>
                                                    </div>
                                                </div>
                                            }
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
                                                                return (<p key={index} className="m-0 p-0">{t('validate.user.' + error)}</p>)
                                                            })
                                                        }
                                                    </div>
                                                </div>
                                            }
                                            <div className="form-field">
                                                <label htmlFor="login-username">
                                                    <i className="fa-solid fa-key"></i>
                                                    <span className="hidden">{t('content.auth.reset_password.new_password')}</span>
                                                </label>
                                                <input onChange={formik.handleChange} id="login-username" type="password" name="password" className="form-input" placeholder={t('content.auth.reset_password.new_password')} autoComplete="off" />
                                            </div>
                                            <div className="form-field">
                                                <label htmlFor="login-password">
                                                    <i className="fa-solid fa-key"></i>
                                                    <span className="hidden">{t('content.auth.reset_password.confirm_password')}</span>
                                                </label>
                                                <input onChange={formik.handleChange} id="login-password" type="password" name="confirm_password" className="form-input" placeholder={t('content.auth.reset_password.confirm_password')} autoComplete="off" />
                                            </div>
                                            <div className="form-field">
                                                <input type="submit" value={t('content.auth.reset_password.confirm')} />
                                            </div>
                                        </> :
                                        <>
                                            <div style={{ width: "280px", margin: "0 auto" }}>
                                                <div className="alert alert-success" role="alert">
                                                    <p className="m-0 p-0">{message}</p>
                                                </div>
                                            </div>
                                            <div>
                                                <p className="text-center pb-2">
                                                    <Link to="/login">{t('content.auth.reset_password.login')}</Link>
                                                </p>
                                            </div>
                                        </>
                                }
                            </form> :
                            <div>
                                <div style={{ width: "280px", margin: "0 auto" }}>
                                    <div className="alert alert-danger" role="alert">
                                        <p className="m-0 p-0">{error}</p>
                                    </div>
                                </div>
                                <p className="text-center">
                                    <Link to="/login">{t('content.auth.reset_password.login')}</Link>
                                </p>
                                <p className="text-center">
                                    <Link to="/forgot-password">{t('content.auth.reset_password.forgot_password')}</Link>
                                </p>
                            </div>
                    }
                </div>
            </div>
        </>
    )
}

export default ResetPassword;