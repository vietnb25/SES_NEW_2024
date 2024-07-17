import React, { useState } from "react";
import { Link } from "react-router-dom";
import { useFormik } from "formik";
import authService from "../../../../services/AuthService";
import { useTranslation } from "react-i18next";
import * as yup from "yup";
import CONS from "../../../../constants/constant";

const ForgotPassword = () => {
    const [status, setStatus] = useState(0);
    const [errorsValidate, setErrorsValidate] = useState([]);
    const [error, setError] = useState(null);
    const { t } = useTranslation();

    const formik = useFormik({
        initialValues: {
            email: ""
        },
        validationSchema: yup.object().shape({
            email: yup.string().required(t('validate.user.EMAIL_NOT_BLANK')).email(t('validate.user.EMAIL_IS_INVALID'))
        }),
        onSubmit: async data => {
            let button = document.getElementById("btn-submit-form");
            button.value = t('content.auth.forgot_password.processing');
            button.disabled = true;
            
            let res = await authService.forgotPassword(data);
            if (res.status === 400) {
                if (res.data === CONS.ERROR.NOT_FOUND_EMAIL) {
                    setError(t('error.login.NOT_FOUND_EMAIL'));
                }else if(res.data.errors){
                    setErrorsValidate(res.data.errors);
                }
            }else if(res.status === 200){
                setStatus(res.status);
            }else {
                setError(t('content.auth.forgot_password.error'));
            }
            button.disabled = false;
        }
    });

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
                                    (formik.errors.email && formik.touched.email) &&
                                    <div style={{ width: "280px", margin: "0 auto" }}>
                                        <div className="alert alert-warning" role="alert">
                                            <p className="m-0 p-0">{formik.errors.email}</p>
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
                                    <label htmlFor="login-password">
                                        <i className="fa-solid fa-envelope"></i>
                                        <span className="hidden">Email</span>
                                    </label>
                                    <input onChange={formik.handleChange} id="login-password" type="text" name="email" className="form-input" placeholder="Email" autoComplete="off" />
                                </div>
                                <div className="form-field">
                                    <input id="btn-submit-form" type="submit" value={t('content.auth.forgot_password.submit')} />
                                </div>
                            </> : 
                            <div className="alert alert-success text-center">
                                <i className="fa fa-check-circle text-success"></i> {t('content.auth.forgot_password.success_send_mail')}
                            </div>
                        }
                    </form>
                    <p className="text-center">
                        <Link to="/login">{t('content.auth.forgot_password.login')}</Link>
                    </p>
                </div>
            </div>
        </>
    )
}

export default ForgotPassword;