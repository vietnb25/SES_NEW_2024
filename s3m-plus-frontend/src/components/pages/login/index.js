import React, { Fragment, useEffect, useState } from "react";
import { useFormik } from 'formik';
import authService from "../../../services/AuthService";
import { useHistory, useLocation, Link } from 'react-router-dom';
import * as yup from "yup";
import { useTranslation } from "react-i18next";
import CONS from "../../../constants/constant";
import jwtDecode from "jwt-decode";

import { Dialog } from 'primereact/dialog';
import { ProgressSpinner } from 'primereact/progressspinner';

import "./index.css";

import useAppStore from "../../../applications/store/AppStore";

const initialValues = {
    username: "",
    password: ""
}

const Login = () => {
    const history = useHistory();
    const location = useLocation();
    const { t } = useTranslation();
    const [errorsValidate, setErrorsValidate] = useState([]);
    const [error, setError] = useState(null);
    const [visible, setVisible] = useState(false);
    const saveTreeData = useAppStore(state => state.saveTreeData);
    const saveMapData = useAppStore(state => state.saveMapData);
    const saveCategoryPath = useAppStore(state => state.saveCategoryPath);
    const saveUserData = useAppStore(state => state.saveUserData);
    const token = authService.getToken();

    const formik = useFormik({
        initialValues,
        validationSchema: yup.object().shape({
            username: yup.string().required(t('validate.user.USERNAME_NOT_BLANK')),
            password: yup.string().required(t('validate.user.PASSWORD_NOT_BLANK'))
        }),
        onSubmit: async (data) => {
            setVisible(true);
            let res = await authService.login(data);
            setVisible(false);
            if (res.status === 200) {
                let jwt = jwtDecode(res.data.jwt);
                if (jwt.roles[0] === "ROLE_EVN") {
                    setError("Tài khoản không có quyền truy cập.");
                    return;
                }
                if (res.data.firstLoginFlag && res.data.firstLoginFlag === 1) {
                    history.push({
                        pathname: "/change-password-first-login",
                        state: {
                            username: data.username,
                            isFirstLogin: res.data.firstLoginFlag === 1 ? true : false
                        }
                    });
                    return;
                }

                authService.saveToken(res.data.jwt);
                
                    let urlSearchParam = new URLSearchParams(location.search).get("next");
                    if (urlSearchParam) {
                        history.push({
                            pathname: urlSearchParam
                        });
                    } else {
                        history.push("/")
                    }
               
            } else {
                handleErrorLogin(res);
            }
        }
    });

    const handleErrorLogin = (res) => {
        if (res.status === 401) {
            if (res.data.error === CONS.ERROR.BAD_CREDITIAL) {
                let failedRemains = res.data.failedRemains;
                setError(t('error.login.' + res.data.error).replace("{failedAttempRemain}", failedRemains));
            }
        } else if (res.status === 403) {
            if (res.data.error === CONS.ERROR.ACCOUNT_IS_LOCKED) {
                let maxFailedAttempts = res.data.maxFailedAttempts;
                setError(t('error.login.' + res.data.error).replace("{maxFailedAttempts}", maxFailedAttempts));
            } else if (res.data.error === CONS.ERROR.ACCOUNT_IS_LOCKED_BY_ADMIN) {
                setError(t('error.login.' + res.data.error));
            }
        } else if (res.status === 503) {
            if (res.data.error === CONS.ERROR.SERVICE_UNAVAILABLE) {
                setError(t('error.login.' + res.data.error));
            }
        } else if (res.status === 400) {
            if (res.data === CONS.ERROR.BAD_REQUEST) {
                setError(t('error.login.LOGIN_FAILED'));
            } else {
                setErrorsValidate(res.data.errors);
            }
        } else {
            setError(t('error.login.LOGIN_FAILED'));
        }
    }

    const handleCreateMarkers = (mapData) => {
        let markers = [];
        mapData.forEach(m => {
            let marker = {
                projectId: m.projectId,
                projectName: m.projectName,
                longitude: m.longitude,
                latitude: m.latitude
            }
            markers.push(marker);
        });
        return markers;
    }

    useEffect(() => {
        document.title = t('content.auth.login.login');
    }, []);

    return (
        <Fragment>
            {
                (!token || token == null) ?
                    <div className="align">
                        <div className="section-login">
                            <form className="form login" onSubmit={formik.handleSubmit}>
                                <div className="form-field text-center login-logo">
                                    <img src="/resources/image/s3m-logo-dark.png" alt="S3M" style={{ height: "32px" }} />
                                </div>
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
                                {
                                    ((formik.errors.username && formik.touched.username) ||
                                        (formik.errors.password && formik.touched.password)) &&
                                    <div style={{ width: "280px", margin: "0 auto" }}>
                                        <div className="alert alert-warning" role="alert">
                                            <p className="m-0 p-0">{formik.errors.username}</p>
                                            <p className="m-0 p-0">{formik.errors.password}</p>
                                        </div>
                                    </div>
                                }
                                <div className="form-field">
                                    <label htmlFor="login-username">
                                        <i className="fa-regular fa-user"></i>
                                        <span className="hidden">{t('content.username')}</span>
                                    </label>
                                    <input onChange={formik.handleChange} id="login-username" type="text" name="username" className="form-input" placeholder={t('content.username')} autoComplete="off" />
                                </div>
                                <div className="form-field">
                                    <label htmlFor="login-password">
                                        <i className="fa-solid fa-key"></i>
                                        <span className="hidden">{t('content.password')}</span>
                                    </label>
                                    <input onChange={formik.handleChange} id="login-password" type="password" name="password" className="form-input" placeholder={t('content.password')} autoComplete="off" />
                                </div>
                                <div className="form-field">
                                    <input type="submit" value={t('content.auth.login.login')} />
                                </div>
                            </form>
                            <p className="text-center">
                                <Link to="/forgot-password">{t('content.auth.login.forgot_password')}</Link>
                            </p>
                        </div>
                    </div> :
                    history.push("/category")
            }
            <Dialog visible={visible} style={{ width: '15vw', height: '15vh' }} closable={false} className="text-center">
                <ProgressSpinner style={{ width: '30px', height: '30px' }} strokeWidth="8" animationDuration="1s" />
                <p className="m-0 text-center">
                    Đang đăng nhập
                </p>
            </Dialog>
        </Fragment>
    )
}

export default Login;