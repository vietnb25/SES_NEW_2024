import React, { useEffect, useState } from "react";
import { useFormik } from "formik";
import { useHistory } from "react-router-dom";
import * as Yup from "yup";
import { useTranslation } from "react-i18next";
import userService from "../../../../../services/UserService";
import { Checkbox } from 'primereact/checkbox';
import AuthService from "../../../../../services/AuthService";
import SuperManagerService from "../../../../../services/SuperManagerService";
import ManagerService from "../../../../../services/ManagerService";
import AreaService from "../../../../../services/AreaService";
import useAppStore from './../../../../../applications/store/AppStore';
import CustomerService from "../../../../../services/CustomerService";
import ProjectService from "../../../../../services/ProjectService";
import { NotficationError, NotficationWarning } from "../../../home-pages/notification/notification";
import Select from "react-select";
const initialValues = {
    staffName: "",
    userType: "",
    authorized: "",
    username: "",
    email: "",
    customerId: "",
    password: "",
    confirmPassword: "",
    createId: "",
    targetId: null,
    customerIds: null,
    projectIds: null,
}

const AddUser = () => {
    const $ = window.$;
    const [errorsValidate, setErrorsValidate] = useState([]);
    const [error, setError] = useState(null);
    const [authorize, setAuthorized] = useState("");
    const [role] = useState(AuthService.getRoleName());

    const appUserData = useAppStore(state => state.appUserData);

    const [customers, setCustomers] = useState([])
    const [superManagers, setSuperManagers] = useState([]);
    const [managers, setManagers] = useState([]);
    const [areas, setAreas] = useState([]);
    const [userType, setUserType] = useState(2);
    const [targetId, setTargetId] = useState(null);

    const [customerSelected, setCustomerSelected] = useState(null);
    const [superManagerSelected, setSuperManagerSelected] = useState(null);
    const [managerSelected, setManagerSelected] = useState(null);
    const [areaSelected, setAreaSelected] = useState(null);
    const [customerIds, setCustomerIds] = useState(null);
    const [listCustomer, setListCustomer] = useState([]);

    const [projectIds, setProjectIds] = useState(null);
    const [listProject, setListProject] = useState([]);

    const history = useHistory();
    const { t } = useTranslation();

    const formik = useFormik({
        initialValues,
        validationSchema: Yup.object().shape({
            staffName: Yup.string().required(t('validate.user.STAFF_NAME_NOT_BLANK')).max(100, t('validate.user.STAFF_NAME_MAX_SIZE_ERROR')),
            username: Yup.string().required(t('validate.user.USERNAME_NOT_BLANK')).max(20, t('validate.user.USERNAME_MAX_SIZE_ERROR')),
            email: Yup.string().required(t('validate.user.EMAIL_NOT_BLANK')).email(t('validate.user.EMAIL_IS_INVALID')).max(100, t('validate.user.EMAIL_MAX_SIZE_ERROR'))
                .matches("^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$", t('validate.user.EMAIL_IS_INVALID')),
            password: Yup.string()
                .required(t('validate.user.PASSWORD_NOT_BLANK'))
                .min(8, t('validate.user.PASSWORD_MIN_SIZE_ERROR'))
                .max(255, t('validate.user.PASSWORD_MAX_SIZE_ERROR'))
                .matches("^(?:(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*)", t('validate.user.PASSWORD_PATTERN_ERROR')),
            confirmPassword: Yup.string()
                .required(t('validate.user.CONFIRM_PASSWORD_NOT_BLANK'))
                .oneOf([Yup.ref('password')], t('validate.user.CONFIRM_PASSWORD_NOT_MATCH')),

        }),
        enableReinitialize: true,
        onSubmit: async user => {
            let userType = document.getElementById("user-type-id").value;
            user.userType = userType;
            user.authorized = authorize;
            user.createId = appUserData.userId;
            let list = []
            for (var i = 0; i < customerIds.length; i++) {
                var counter = customerIds[i];
                list.push(counter.value)
            }

            list.sort();
            // danh sách khách hàng
            user.customerIds = list.toString()

            list = [];
            for (var i = 0; i < projectIds.length; i++) {
                var counter = projectIds[i];
                list.push(counter.value)
            }
            list.sort();
            user.projectIds = list.toString()

            if (userType === "7") {
                if (areaSelected) {
                    user.userType = 6;
                    user.targetId = areaSelected.areaId;
                } else {
                    if (managerSelected) {
                        user.userType = 5;
                        user.targetId = managerSelected.managerId;
                    } else {
                        user.userType = 4;
                        user.targetId = superManagerSelected.superManagerId;
                    }
                }
            } else {
                if (customerSelected) {
                    user.customerId = customerSelected.customerId
                }
                if (userType == 2) {
                    if (user.customerIds == null || user.customerIds == '') {
                        $.alert({
                            title: 'Thông báo!',
                            content: "Hãy chọn ít nhất 1 khách hàng!",
                        });
                        return
                    }
                }
                if (userType == 3) {
                    if (user.projectIds == null || user.projectIds == '') {
                        $.alert({
                            title: 'Thông báo!',
                            content: "Hãy chọn ít nhất 1 dự án!",
                        });
                        return
                    }
                }
            }

            let res = await userService.addUser(user);
            if (res.status === 400) {
                setErrorsValidate(res.data.errors);
                return
            }
            if (res.status === 200) {
                history.push({
                    pathname: "/category/users",
                    state: {
                        status: 200,
                        message: "INSERT_SUCCESS"
                    }
                });
            }else{
                setError(t('validate.user.INSERT_FAILED'))
            }
        },
    });

    const onChangeSetAuthorize = (e) => {
        if (e.checked) {
            setAuthorized(e.value);
        } else {
            setAuthorized("");
        }
    }

    const changeUserType = async (e) => {
        let value = e.target.value;

        setUserType(e.target.value);

        if (value === "7") {

            let resSm = await SuperManagerService.listSuperManager();
            let resManager = await ManagerService.listManager();
            let resArea = await AreaService.listArea();

            setSuperManagers(resSm.status === 200 ? resSm?.data : []);
            setManagers(resManager.status === 200 ? resManager?.data : []);
            setAreas(resArea.status === 200 ? resArea?.data.areas : []);

            setSuperManagerSelected(resSm?.data[0]);
        } else {
            getListCustomer(value);
        }
    }

    const getListCustomer = async (userTypeValue) => {
        let resCustomer = await CustomerService.getListCustomer();
        setCustomers(resCustomer.data);

        setCustomerIds(() => [])
        setProjectIds(() => [])
        while (listCustomer.length > 0) {
            listCustomer.pop();
        }
        if (resCustomer.status == 200) {
            if (resCustomer.data.length > 0) {
                let id = resCustomer.data[0].customerId;
                let name = resCustomer.data[0].customerName;
                setCustomerIds([{ label: name, value: id }])
                for (let i = 0; i < resCustomer.data.length; i++) {
                    listCustomer.push({ label: resCustomer.data[i].customerName, value: resCustomer.data[i].customerId });
                }
                if (userTypeValue == 3) {
                    getListProject(id);
                }
            }
        }
    }

    const getListProject = async (id) => {
        setProjectIds(() => [])
        while (listProject.length > 0) {
            listProject.pop();
        }
        let resProject = await ProjectService.getProjectByCustomerId(id);
        if (resProject.status == 200) {
            if (resProject.data.length > 0) {
                setProjectIds([{ label: resProject.data[0].projectName, value: resProject.data[0].projectId }])
                for (let j = 0; j < resProject.data.length; j++) {
                    listProject.push({ label: resProject.data[j].projectName, value: resProject.data[j].projectId });
                }
            }
        }
    }

    const changeSuperManager = (e) => {
        let value = e.target.value;

        let superManager = superManagers.find(sm => sm.superManagerId === parseInt(value));

        setSuperManagerSelected(superManager ? superManager : null);

        let _managers = managers.filter(m => m.superManagerId === superManager.superManagerId);

        setManagerSelected(_managers.length > 0 && _managers[0]);
    }

    const changeManager = (e) => {
        let value = e.target.value;

        let manager = managers.find(m => m.managerId === parseInt(value));

        setManagerSelected(manager ? manager : null);
    }

    const changeArea = (e) => {
        let value = e.target.value;

        let area = areas.find(a => a.areaId === parseInt(value));

        setAreaSelected(area ? area : null);
    }

    const changeCustomer = (e) => {
        let value = e.target.value;
        console.log("value", value);

        let customer = customers.find(a => a.customerId === parseInt(value));

        console.log("customer", customer);

        setCustomerSelected(customer ? customer : null);
    }

    useEffect(() => {
        getListCustomer();
        document.title = t('content.add_new_account');
    }, []);

    return (
        <div id="page-body">
            <div id="main-title">
                <h5 className="d-block mb-0 float-left"><i className="far fa-circle-user"></i> &nbsp;{t('content.add_new_account')}</h5>
            </div>
            {
                (error != null) &&
                <div className="alert alert-danger" role="alert">
                    <p className="m-0 p-0">{error}</p>
                </div>
            }
            {
                (errorsValidate.length > 0) &&
                <div className="alert alert-warning" role="alert">
                    {
                        errorsValidate.map((error, index) => {
                            return (<p key={index} className="m-0 p-0">{t('validate.user.' + error)}</p>)
                        })
                    }
                </div>
            }
            {
                ((formik.errors.staffName && formik.touched.staffName) ||
                    (formik.errors.username && formik.touched.username) ||
                    (formik.errors.email && formik.touched.email) ||
                    (formik.errors.password && formik.touched.password) ||
                    (formik.errors.confirmPassword && formik.touched.confirmPassword)) &&
                <div className="alert alert-warning" role="alert">
                    <p className="m-0 p-0">{formik.errors.staffName}</p>
                    <p className="m-0 p-0">{formik.errors.username}</p>
                    <p className="m-0 p-0">{formik.errors.email}</p>
                    <p className="m-0 p-0">{formik.errors.password}</p>
                    <p className="m-0 p-0">{formik.errors.confirmPassword}</p>
                </div>
            }

            <div id="main-content">
                <form onSubmit={formik.handleSubmit}>
                    <table className="table table-input">
                        <tbody>
                            <tr>
                                <th width="150px">{t('content.staff_name')} <span className="required">※</span></th>
                                <td>
                                    <input type="text" className="form-control" name="staffName" onChange={formik.handleChange} autoComplete="off" />
                                </td>
                            </tr>
                            <tr className="user-type-item">
                                <th width="150px">{t('content.user_type')}</th>
                                <td>
                                    <select id="user-type-id" className="custom-select block" onChange={e => changeUserType(e)}>
                                        <option value="2">Quản trị Khách hàng</option>
                                        <option value="3">Quản trị Dự án</option>
                                        <option value="7">Quản trị EVN</option>
                                        {/* <option value="10">EVN Khu vực</option>
                                        <option value="11">EVN Tỉnh thành</option>
                                        <option value="12">EVN Quận huyện</option> */}
                                    </select>
                                </td>
                            </tr>
                            {
                                userType === "7" &&
                                <tr className="super-manager-item">
                                    <th width="150px">Khu vực</th>
                                    <td>
                                        <select id="super-manager-item-id" className="custom-select block" onChange={e => changeSuperManager(e)}>
                                            {
                                                superManagers.map((sm, i) => {
                                                    return <option key={i} value={sm.superManagerId}>{sm.superManagerName}</option>
                                                })
                                            }
                                        </select>
                                    </td>
                                </tr>
                            }
                            {
                                userType === "7" &&
                                <tr className="manager-item">
                                    <th width="150px">Tỉnh thành</th>
                                    <td>
                                        <select id="manager-item-id" className="custom-select block" onChange={e => changeManager(e)}>
                                            <option value="">Chọn tỉnh thành</option>
                                            {
                                                superManagerSelected && managers.map((m, i) => {
                                                    return (
                                                        (superManagerSelected.superManagerId === m.superManagerId) &&
                                                        <option key={i} value={m.managerId}>{m.managerName}</option>
                                                    )
                                                })
                                            }
                                        </select>
                                    </td>
                                </tr>
                            }
                            {
                                userType === "7" &&
                                <tr className="area-item">
                                    <th width="150px">Quận huyện</th>
                                    <td>
                                        <select id="area-item-id" className="custom-select block" onChange={e => changeArea(e)}>
                                            <option value="">Chọn quận huyện</option>
                                            {
                                                managerSelected && areas.map((a, i) => {
                                                    return (
                                                        (managerSelected.managerId === a.managerId) &&
                                                        <option key={i} value={a.areaId}>{a.areaName}</option>
                                                    )
                                                })
                                            }

                                        </select>
                                    </td>
                                </tr>
                            }
                            {
                                (userType !== "7" && role === "ROLE_ADMIN") &&
                                <tr>
                                    <th width="150px">Tên khách hàng</th>
                                    <td>
                                        {userType == 2 && <>
                                            <Select value={customerIds} name="customerIds"
                                                isMulti
                                                onChange={(e) => setCustomerIds(e)}
                                                id="customerIds" options={listCustomer}
                                                className="basic-multi-select" classNamePrefix="select"
                                                placeholder="Chọn tên khách hàng" noOptionsMessage=
                                                {() => "Không có tên khách hàng"}
                                            />
                                        </>
                                        }
                                        {userType == 3 && <>
                                            <Select value={customerIds} name="customerIds"
                                                onChange={(e) => {
                                                    setCustomerIds([e]);
                                                    getListProject(e.value);
                                                }}
                                                id="customerIds" options={listCustomer}
                                                className="basic-multi-select" classNamePrefix="select"
                                                placeholder="Chọn tên khách hàng" noOptionsMessage=
                                                {() => "Không có tên khách hàng"}
                                            />

                                        </>}

                                    </td>
                                </tr>
                            }
                            {
                                (userType == 3 && role === "ROLE_ADMIN") &&
                                <tr>
                                    <th width="150px">Tên dự án</th>
                                    <td>
                                        {userType == 3 && <>
                                            <Select value={projectIds} name="projectIds"
                                                isMulti
                                                onChange={(e) => {
                                                    setProjectIds(e);
                                                }}
                                                id="production" options={listProject}
                                                className="basic-multi-select" classNamePrefix="select"
                                                placeholder="Chọn tên dự án" noOptionsMessage=
                                                {() => "Không có tên dự án"}
                                            />

                                        </>}

                                    </td>
                                </tr>
                            }
                            <tr>
                                <th width="150px">{t('content.username')} <span className="required">※</span></th>
                                <td>
                                    <input type="text" name="username"
                                        className="form-control"
                                        onChange={formik.handleChange} autoComplete="off" />
                                </td>
                            </tr>
                            <tr>
                                <th width="150px">{t('content.email')} <span className="required">※</span></th>
                                <td>
                                    <input type="text" name="email" className="form-control" onChange={formik.handleChange} autoComplete="off" />
                                </td>
                            </tr>
                            <tr>
                                <th width="150px">{t('content.password')} <span className="required">※</span></th>
                                <td>
                                    <input type="password" name="password" className="form-control" onChange={formik.handleChange} autoComplete="off" />
                                </td>
                            </tr>
                            <tr>
                                <th width="150px">{t('content.confirm_password')} <span className="required">※</span></th>
                                <td>
                                    <input type="password" name="confirmPassword" className="form-control" onChange={formik.handleChange} autoComplete="off" />
                                </td>
                            </tr>
                        </tbody>
                    </table>

                    <div id="main-button">
                        <button type="submit" className="btn btn-outline-secondary btn-agree mr-1">
                            <i className="fa-solid fa-check"></i>
                        </button>
                        <button type="button" className="btn btn-outline-secondary btn-cancel" onClick={() => {
                            history.push("/category/users")
                        }}>
                            <i className="fa-solid fa-xmark"></i>
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}

export default AddUser;