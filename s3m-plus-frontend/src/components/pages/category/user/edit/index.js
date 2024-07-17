import { useFormik } from "formik";
import React, { useEffect, useState } from "react";
import { useHistory, useParams } from 'react-router-dom';
import UserService from "../../../../../services/UserService";
import * as Yup from "yup";
import { useTranslation } from "react-i18next";
import AuthService from "../../../../../services/AuthService";
import { Checkbox } from 'primereact/checkbox';
import SuperManagerService from "../../../../../services/SuperManagerService";
import ManagerService from "../../../../../services/ManagerService";
import AreaService from "../../../../../services/AreaService";
import Select from "react-select";
import ProjectService from "../../../../../services/ProjectService";
import CustomerService from "../../../../../services/CustomerService";

const EditUser = () => {
    const $ = window.$;
    const [authorize, setAuthorized] = useState("");
    const [role] = useState(AuthService.getRoleName());
    const [userName] = useState(AuthService.getUserName());
    const [user, setUser] = useState({
        staffName: "",
        userType: "",
        authorized: "",
        username: "",
        email: "",
        password: "",
        confirmPassword: "",
        lockFlag: "",
        customerName: ""
    });
    const [errorsValidate, setErrorsValidate] = useState([]);
    const [error, setError] = useState(null);
    const history = useHistory();
    const { id } = useParams();
    const { t } = useTranslation();
    const [userType, setUserType] = useState(0);

    const [superManagers, setSuperManagers] = useState([]);
    const [managers, setManagers] = useState([]);
    const [areas, setAreas] = useState([]);

    const [superManagerSelected, setSuperManagerSelected] = useState(null);
    const [managerSelected, setManagerSelected] = useState(null);
    const [areaSelected, setAreaSelected] = useState(null);
    const [customers, setCustomers] = useState([])
    const [customerIds, setCustomerIds] = useState(null);
    const [listCustomer, setListCustomer] = useState([]);
    const [projectIds, setProjectIds] = useState(null);
    const [listProject, setListProject] = useState([]);
    const [listPrioritySystem, setListPrioritySystem] = useState(
        [
            { label: t('content.category.user.edit.load'), value: 1 },
            { label: t('content.category.user.edit.solar'), value: 2 },
            { label: t('content.category.user.edit.grid'), value: 5 },
            { label: t('content.category.user.edit.wind'), value: 3 },
            { label: t('content.category.user.edit.battery'), value: 4 },
        ]
    )
    const [listPriorityIngredients, setListPriorityIngredients] = useState(
        [
            { label: t('content.category.user.edit.power_flow'), value: 1 },
            { label: t('content.category.user.edit.map'), value: 2 },
            { label: t('content.category.user.edit.energy_data'), value: 3 },
            { label: t('content.category.user.edit.energy_usage_plan'), value: 4 },
            { label: t('content.category.user.edit.energy_cost_revenue'), value: 5 },
            { label: t('content.category.user.edit.energy_statistics'), value: 6 },
            { label: t('content.category.user.edit.warning_statistics'), value: 7 },
            { label: t('content.category.user.edit.management_failure'), value: 8 },
            { label: t('content.category.user.edit.compressed_air_system_data'), value: 9 },
            { label: t('content.category.user.edit.liquid_system_data'), value: 10 },
        ]
    )

    const [listPriorityChartSystem, setListPriorityChartSystem] = useState(
        [
            { label: t('content.category.user.edit.energy'), value: 1 },
            { label: t('content.category.user.edit.power'), value: 2 },
            { label: t('content.category.user.edit.cost'), value: 3 },
            { label: t('content.category.user.edit.load_level'), value: 4 },
            { label: t('content.category.user.edit.temperature'), value: 5 },
            { label: t('content.category.user.edit.sankey'), value: 6 },
            { label: t('content.category.user.edit.discharge_indicator'), value: 7 },
            { label: t('content.category.user.edit.energy_usage_plan'), value: 8 },
            { label: t('content.category.user.edit.energy_comparison'), value: 9 },
        ]
    )

    const [prioritySystemId, setPrioritySystemId] = useState()
    const [priorityIngredientsId, setPriorityIngredientsId] = useState([])
    const [priorityChartSystemId, setPriorityChartSystemId] = useState([])
    const [selectedOptionA, setSelectedOptionA] = useState(null);
    const [selectedOptionsB, setSelectedOptionsB] = useState([]);

    const [priorityLoad, setPriorityLoad] = useState([])
    const [prioritySolar, setPrioritySolar] = useState([])
    const [priorityGrid, setPriorityGrid] = useState([])
    const [priorityBattery, setPrioritBattery] = useState([])
    const [priorityWind, setPriorityWind] = useState([])

    const handleSelectOptionA = (selectedOptionA) => {
        setSelectedOptionA(selectedOptionA);
        setSelectedOptionsB([]);
    };

    const formik = useFormik({
        initialValues: user,
        validationSchema: Yup.object().shape({
            staffName: Yup.string().required(t('validate.user.STAFF_NAME_NOT_BLANK')).max(100, t('validate.user.STAFF_NAME_MAX_SIZE_ERROR')),
            email: Yup.string().required(t('validate.user.EMAIL_NOT_BLANK')).email(t('validate.user.EMAIL_IS_INVALID')).max(100, t('validate.user.EMAIL_MAX_SIZE_ERROR')),
            password: Yup.string()
                .min(8, t('validate.user.PASSWORD_MIN_SIZE_ERROR'))
                .max(255, t('validate.user.PASSWORD_MAX_SIZE_ERROR'))
                .matches("^(?:(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*)", t('validate.user.PASSWORD_PATTERN_ERROR'))
                .nullable()
                .notRequired(),
            confirmPassword: Yup.string()
                .oneOf([Yup.ref('password')], t('validate.user.CONFIRM_PASSWORD_NOT_MATCH'))
        }),
        enableReinitialize: true,
        onSubmit: async userData => {
            // let _userType = document.getElementById("user-type-id").value;
            // userData.userType = _userType;
            userData.authorized = authorize;

            // if(userType === "7"){
            //     if(areaSelected){
            //         userData.userType = 6;
            //         userData.targetId = areaSelected.areaId;
            //     }else{
            //         if(managerSelected){
            //             userData.userType = 5;
            //             userData.targetId = managerSelected.managerId;
            //         }else{
            //             userData.userType = 4;
            //             userData.targetId = superManagerSelected.superManagerId;
            //         }
            //     }
            // }

            let list = []
            for (var i = 0; i < customerIds.length; i++) {
                var counter = customerIds[i];
                list.push(counter.value)
            }
            list.sort();
            userData.customerIds = list.toString()

            list = [];
            for (var i = 0; i < projectIds.length; i++) {
                var counter = projectIds[i];
                list.push(counter.value)
            }
            list.sort();
            userData.projectIds = list.toString()
            userData.prioritySystem = prioritySystemId.value;

            list = [];
            for (var i = 0; i < priorityIngredientsId.length; i++) {
                var counter = priorityIngredientsId[i];
                list.push(counter.value)
            }
            userData.priorityIngredients = list.toString();

            list = [];
            for (var i = 0; i < priorityLoad.length; i++) {
                var counter = priorityLoad[i];
                list.push(counter.value)
            }
            userData.priorityLoad = list.toString();

            list = [];
            for (var i = 0; i < prioritySolar.length; i++) {
                var counter = prioritySolar[i];
                list.push(counter.value)
            }
            userData.prioritySolar = list.toString();

            list = [];
            for (var i = 0; i < priorityWind.length; i++) {
                var counter = priorityWind[i];
                list.push(counter.value)
            }
            userData.priorityWind = list.toString();

            list = [];
            for (var i = 0; i < priorityBattery.length; i++) {
                var counter = priorityBattery[i];
                list.push(counter.value)
            }
            userData.priorityBattery = list.toString();

            list = [];
            for (var i = 0; i < priorityGrid.length; i++) {
                var counter = priorityGrid[i];
                list.push(counter.value)
            }
            userData.priorityGrid = list.toString();

            if (userData.userType == 2) {
                if (userData.customerIds == null || userData.customerIds == '') {
                    $.alert({
                        title: 'Thông báo!',
                        content: "Hãy chọn ít nhất 1 khách hàng!",
                    });
                    return
                }
            }
            if (userData.userType == 3) {
                if (userData.projectIds == null || userData.projectIds == '') {
                    $.alert({
                        title: 'Thông báo!',
                        content: "Hãy chọn ít nhất 1 dự án!",
                    });
                    return
                }
            }

            if (userData.prioritySystem == null || userData.prioritySystem == '') {
                $.alert({
                    title: 'Thông báo!',
                    content: "Hãy chọn ít nhất 1 hệ thống ưu tiên!",
                });
                return
            }

            if (userData.priorityIngredients == null || userData.priorityIngredients == '') {
                $.alert({
                    title: 'Thông báo!',
                    content: "Hãy chọn ít nhất 1 thành phần ưu tiên!",
                });
                return
            };

            let res = await UserService.updateUser(userData);
            if (res.status === 400) {
                setErrorsValidate(res.data.errors);
                return
            }
            if (res.status === 200) {
                history.push({
                    pathname: "/category/users",
                    state: {
                        status: 200,
                        message: "UPDATE_SUCCESS"
                    }
                });
            } else {
                setError(t('validate.user.UPDATE_FAILED'))
            }
        }
    });

    const getUser = async () => {
        let response = await UserService.getUser(id);
        if (response.status === 200) {
            const userData = response.data.user;
            setUser(userData);
            setAuthorized(userData.authorized);
            setUserType((userData.userType === 1 || userData.userType === 2) ? userData.userType : "7");
            getListCustomer(userData.userType, userData.username);

            const priorityIngredientsArray = userData.priorityIngredients.split(",").map(Number);
            let priorityLoad = [];
            let prioritySolar = [];
            let priorityGrid = [];
            let priorityBattery = [];
            let priorityWind = [];

            if (userData.priorityLoad) {
                priorityLoad = userData.priorityLoad?.split(",").map(Number);
            }
            if (userData.prioritySolar) {
                prioritySolar = userData.prioritySolar?.split(",").map(Number);
            }
            if (userData.priorityGrid) {
                priorityGrid = userData.priorityGrid?.split(",").map(Number);
            }
            if (userData.priorityBattery) {
                priorityBattery = userData.priorityBattery?.split(",").map(Number);
            }
            if (userData.priorityWind) {
                priorityWind = userData.priorityWind?.split(",").map(Number);
            }

            const newPriorityIngredients = priorityIngredientsArray.map((value) => {
                return {
                    label: getLabelForValueIngredient(value),
                    value: value,
                };
            });

            const newPriorityLoad = priorityLoad.map((value) => {
                return {
                    label: getLabelForValueIngredientChart(value),
                    value: value,
                };
            });

            const newPrioritySolar = prioritySolar.map((value) => {
                return {
                    label: getLabelForValueIngredientChart(value),
                    value: value,
                };
            });

            const newPriorityGrid = priorityGrid.map((value) => {
                return {
                    label: getLabelForValueIngredientChart(value),
                    value: value,
                };
            });

            const newPriorityBatterys = priorityBattery.map((value) => {
                return {
                    label: getLabelForValueIngredientChart(value),
                    value: value,
                };
            });

            const newPriorityWind = priorityWind.map((value) => {
                return {
                    label: getLabelForValueIngredientChart(value),
                    value: value,
                };
            });

            setPriorityIngredientsId(newPriorityIngredients);
            setPriorityLoad(newPriorityLoad)
            setPrioritySolar(newPrioritySolar)
            setPriorityGrid(newPriorityGrid)
            setPrioritBattery(newPriorityBatterys)
            setPriorityWind(newPriorityWind)

            setPrioritySystemId(
                { label: getLabelForValueSystem(userData.prioritySystem), value: userData.prioritySystem }
            );

            // let resSm = await SuperManagerService.listSuperManager();
            // let resManager = await ManagerService.listManager();
            // let resArea = await AreaService.listArea();

            // setSuperManagers(resSm.status === 200 ? resSm?.data : []);
            // setManagers(resManager.status === 200 ? resManager?.data : []);
            // setAreas(resArea.status === 200 ? resArea?.data.areas : []);

            // if(response.data.user.userType === 4){
            //     let superManagerId = response.data.user.targetId;
            //     let sm = resSm?.data.find(s => s.superManagerId === superManagerId);
            //     sm !== null ?  setSuperManagerSelected(sm) : setSuperManagerSelected(resSm?.data[0]);
            // }else if(response.data.user.userType === 5){
            //     let managerId = response.data.user.targetId;
            //     resSm?.data.forEach(sm => {
            //         resManager?.data.forEach(manager => {
            //             if(sm.superManagerId === manager.superManagerId && manager.managerId === managerId){
            //                 setSuperManagerSelected(sm);
            //                 setManagerSelected(manager);
            //             }
            //         });
            //     });
            // }else if(response.data.user.userType === 6){
            //     let areaId = response.data.user.targetId;
            //     resSm?.data.forEach(sm => {
            //         resManager?.data.forEach(manager => {
            //             resArea?.data.areas.forEach(area => {
            //                 if(sm.superManagerId === manager.superManagerId && manager.managerId === area.managerId && area.areaId === areaId){
            //                     setSuperManagerSelected(sm);
            //                     setManagerSelected(manager);
            //                     setAreaSelected(area);
            //                 }
            //             });
            //         });
            //     });
            // }
        }
    }

    function getLabelForValueIngredient(value) {
        switch (value) {
            case 1:
                return t('content.category.user.edit.power_flow');
            case 2:
                return t('content.category.user.edit.map');
            case 3:
                return t('content.category.user.edit.energy_data');
            case 4:
                return t('content.category.user.edit.energy_usage_plan');
            case 5:
                return t('content.category.user.edit.energy_cost_revenue');
            case 6:
                return t('content.category.user.edit.energy_statistics');
            case 7:
                return t('content.category.user.edit.warning_statistics');
            case 8:
                return t('content.category.user.edit.management_failure');
            case 9:
                return t('content.category.user.edit.compressed_air_system_data');
            case 10:
                return t('content.category.user.edit.liquid_system_data');
            default:
                return '';
        }
    }

    function getLabelForValueSystem(value) {
        switch (value) {
            case 1:
                return t('content.category.user.edit.load');
            case 2:
                return t('content.category.user.edit.solar');
            case 3:
                return t('content.category.user.edit.wind');
            case 4:
                return t('content.category.user.edit.battery');
            case 5:
                return t('content.category.user.edit.grid');
            default:
                return '';
        }
    }

    function getLabelForValueIngredientChart(value) {
        switch (value) {
            case 1:
                return t('content.category.user.edit.energy');
            case 2:
                return t('content.category.user.edit.power');
            case 3:
                return t('content.category.user.edit.cost');
            case 4:
                return t('content.category.user.edit.load_level');
            case 5:
                return t('content.category.user.edit.temperature');
            case 6:
                return t('content.category.user.edit.sankey');
            case 7:
                return t('content.category.user.edit.discharge_indicator');
            case 8:
                return t('content.category.user.edit.energy_usage_plan');
            case 9:
                return t('content.category.user.edit.energy_comparison');
            default:
                return '';
        }
    }

    function getDataOptionPriority(value) {
        switch (value) {
            case 1:
                return priorityLoad;
            case 2:
                return prioritySolar;
            case 3:
                return priorityWind;
            case 4:
                return priorityBattery;
            case 5:
                return priorityGrid;
            default:
                return '';
        }
    }

    function setDataOptionPriority(value) {
        switch (value) {
            case 1:
                return setPriorityLoad;
            case 2:
                return setPrioritySolar;
            case 3:
                return setPriorityWind;
            case 4:
                return setPrioritBattery;
            case 5:
                return setPriorityGrid;
            default:
                return '';
        }
    }

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
            setSuperManagerSelected(superManagers[0]);
        }
    }

    const changeSuperManager = (e) => {
        let value = e.target.value;

        let superManager = superManagers.find(sm => sm.superManagerId === parseInt(value));

        setSuperManagerSelected(superManager ? superManager : null);
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

    const getListCustomer = async (userTypeValue, userName) => {
        let resCustomer = await CustomerService.getListCustomer();
        let resCusIds = await CustomerService.getCustomerIds(userName)

        setCustomerIds(() => [])
        setProjectIds(() => [])


        if (resCusIds.status === 200 && resCusIds.data !== '') {
            const newListCusId = resCusIds.data.map(item => ({ value: item.customerId, label: item.customerName }));
            setCustomerIds(newListCusId)
        }

        setCustomers(resCustomer.data);

        while (listCustomer.length > 0) {
            listCustomer.pop();
        }
        if (resCustomer.status == 200) {
            if (resCustomer.data.length > 0) {
                for (let i = 0; i < resCustomer.data.length; i++) {
                    listCustomer.push({ label: resCustomer.data[i].customerName, value: resCustomer.data[i].customerId });
                }
                if (userTypeValue == 3) {
                    getListProject(resCusIds.data[0].customerId);
                    let resProIds = await ProjectService.getProjectIds(userName)
                    if (resProIds.status === 200 && resProIds.data !== '') {
                        const newListProId = resProIds.data.map(item => ({ value: item.projectId, label: item.projectName }));
                        setProjectIds(newListProId)
                    }
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
                for (let j = 0; j < resProject.data.length; j++) {
                    listProject.push({ label: resProject.data[j].projectName, value: resProject.data[j].projectId });
                }
            }
        }
    }

    useEffect(() => {
        document.title = t('content.update_account');
        getUser();
    }, [id]);

    return (
        <div id="page-body">
            <div id="main-title">
                <h5 className="d-block mb-0 float-left"><i className="far fa-circle-user"></i> &nbsp;{t('content.update_account')}</h5>
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
                                    <input type="text" className="form-control" name="staffName" value={formik.values.staffName} onChange={formik.handleChange} autoComplete="off" />
                                    {(formik.errors.staffName && formik.touched.staffName) && <p className="text-danger" style={{ margin: 0, fontSize: 14 }}>* {formik.errors.staffName}</p>}
                                </td>
                            </tr>
                            {/* {

                                <tr >
                                    <th width="150px">{user.userType === 1 || user.userType === 2 ? "Khách hàng" : "Quản lý"}</th>
                                    <td style={{height: 33.25, paddingLeft: 13, color: "#45484D"}}>
                                        {
                                            (user.userType === 1 || user.userType === 2) ? (
                                                user.customerName ? user.customerName.substring(0, user.customerName.length - 2) : ""
                                            ) : user.targetManager
                                        }
                                       
                                    </td>
                                </tr>
                                
                            } */}


                            <tr className="user-type-item">
                                <th width="150px">{t('content.user_type')}</th>
                                <td>
                                    <select disabled id="user-type-id" className="custom-select block" onChange={e => changeUserType(e)} value={(formik.values.userType === 1 || formik.values.userType === 2) ? "2" : "7"}>
                                        <option value="2" >Quản trị Khách hàng</option>
                                        <option value="3" >Quản trị Dự án</option>
                                        <option value="7" >Quản trị EVN</option>
                                    </select>
                                </td>
                            </tr>
                            {
                                (userType !== "7" && role === "ROLE_ADMIN") &&
                                <tr>
                                    <th width="150px">{t('content.category.user.edit.customer_name')}</th>
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
                                (userType == 7 && role === "ROLE_ADMIN") &&
                                <tr>
                                    <th width="150px">{t('content.category.user.edit.project_name')}</th>
                                    <td>
                                        {userType == 7 && <>
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
                            {/* {
                                userType === "7" && (
                                    <tr className="super-manager-item">
                                        <th width="150px">Khu vực</th>
                                        <td>
                                            <select id="super-manager-item-id" className="custom-select block" onChange={e => changeSuperManager(e)} value={superManagerSelected ? superManagerSelected.superManagerId : ""}>
                                                {
                                                    superManagers.map((sm, i) => {
                                                        return <option key={i} value={sm.superManagerId} >{sm.superManagerName}</option>
                                                    })
                                                }
                                            </select>
                                        </td>
                                    </tr>
                                )
                            }
                            {
                                userType === "7" &&
                                <tr className="manager-item">
                                    <th width="150px">Tỉnh thành</th>
                                    <td>
                                        <select id="manager-item-id" className="custom-select block" onChange={e => changeManager(e)} value={managerSelected ? managerSelected.managerId : ""}>
                                            <option value="">Chọn tỉnh thành</option>
                                            {
                                                superManagerSelected && managers.map((m, i) => {
                                                    return (
                                                        (superManagerSelected.superManagerId === m.superManagerId) &&
                                                        <option key={i} value={m.managerId} >{m.managerName}</option>
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
                                        <select id="area-item-id" className="custom-select block" onChange={e => changeArea(e)} value={areaSelected ? areaSelected.areaId : ""}>
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
                            } */}
                            {/* {
                                (userType !== "7" && role === "ROLE_ADMIN") &&
                                <tr>
                                    <th width="150px">Phân quyền</th>
                                    <td>
                                        <Checkbox onChange={onChangeSetAuthorize} checked={authorize === 1} value="1" label="Được phân quyền" name="authorize" />
                                        <label htmlFor="" className="p-checkbox-label mb-0 ml-2">Được phân quyền</label>
                                    </td>
                                </tr>
                            } */}
                            <tr>
                                <th width="150px">{t('content.category.user.edit.priority_system')}</th>
                                <td>
                                    <Select value={prioritySystemId} name="prioritysystemId"
                                        isMulti={false}
                                        onChange={(e) => setPrioritySystemId(e)}
                                        id="prioritysystemId" options={listPrioritySystem}
                                        className="basic-multi-select" classNamePrefix="select"
                                        placeholder="Chọn hệ thống ưu tiên" noOptionsMessage=
                                        {() => "Không có hệ thống ưu tiên"}
                                    />
                                </td>
                            </tr>
                            <tr>
                                <th width="150px">{t('content.category.user.edit.priority_ingredients')}</th>
                                <td>
                                    <Select value={priorityIngredientsId} name="priorityIngredientsId"
                                        isMulti
                                        onChange={(e) => setPriorityIngredientsId(e)}
                                        id="priorityIngredientsId" options={listPriorityIngredients}
                                        className="basic-multi-select" classNamePrefix="select"
                                        placeholder="Chọn thành phần ưu tiên" noOptionsMessage=
                                        {() => "Không có thàng phần ưu tiên"}

                                    />
                                </td>
                            </tr>
                            <tr>
                                <th width="150px">{t('content.category.user.edit.priority_chart_with_system')}</th>
                                <td>
                                    <Select
                                        value={selectedOptionA}
                                        onChange={handleSelectOptionA}
                                        options={listPrioritySystem}
                                        placeholder="Chọn hệ thống" noOptionsMessage=
                                        {() => "Không có hệ thống"}
                                        className="basic-multi-select" classNamePrefix="select"
                                    />
                                    {selectedOptionA && (
                                        <>
                                            <Select
                                                value={getDataOptionPriority(selectedOptionA.value)}
                                                onChange={setDataOptionPriority(selectedOptionA.value)}
                                                options={listPriorityChartSystem}
                                                className="basic-multi-select" classNamePrefix="select"
                                                isMulti
                                                placeholder="Chọn biểu đồ cho hệ thống" noOptionsMessage=
                                                {() => "Không có biểu đồ cho hệ thống"}
                                            />
                                        </>
                                    )}
                                </td>
                            </tr>

                            <tr>
                                <th width="150px">{t('content.username')} <span className="required">※</span></th>
                                <td>
                                    <input type="text" name="username"
                                        className="form-control"
                                        onChange={formik.handleChange} autoComplete="off"
                                        value={formik.values.username} readOnly disabled />
                                </td>
                            </tr>
                            <tr>
                                <th width="150px">{t('content.email')} <span className="required">※</span></th>
                                <td>
                                    <input type="text" name="email" className="form-control" value={formik.values.email} onChange={formik.handleChange} autoComplete="off" />
                                    {(formik.errors.email && formik.touched.email) && <p className="text-danger" style={{ margin: 0, fontSize: 14 }}>* {formik.errors.email}</p>}
                                </td>
                            </tr>
                            <tr>
                                <th width="150px">{t('content.password')}</th>
                                <td>
                                    <input type="password" name="password" className="form-control" onChange={formik.handleChange} autoComplete="off" />
                                    {(formik.errors.password && formik.touched.password) && <p className="text-danger" style={{ margin: 0, fontSize: 14 }}>* {formik.errors.password}</p>}
                                </td>
                            </tr>
                            <tr>
                                <th width="150px">{t('content.confirm_password')}</th>
                                <td>
                                    <input type="password" name="confirmPassword" className="form-control" onChange={formik.handleChange} autoComplete="off" />
                                    {(formik.errors.confirmPassword && formik.touched.confirmPassword) && <p className="text-danger" style={{ margin: 0, fontSize: 14 }}>* {formik.errors.confirmPassword}</p>}
                                </td>
                            </tr>
                            <tr>
                                <th width="150px">{t('content.status')}</th>
                                <td>
                                    <select value={formik.values.lockFlag} id="lock-flag" name="lockFlag" className="custom-select block" onChange={formik.handleChange}>
                                        <option value="0">{t('content.unlock')}</option>
                                        <option value="1">{t('content.lock')}</option>
                                    </select>
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

export default EditUser;