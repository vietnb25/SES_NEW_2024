import React, { useEffect, useState } from "react";
import { Link, useHistory } from "react-router-dom";
import "./index.css";
import moment from 'moment';
import DeviceService from "../../../../../services/DeviceService";
import CONS from "../../../../../constants/constant";
import { useTranslation } from "react-i18next";
import ProjectService from "../../../../../services/ProjectService";
import CustomerService from "../../../../../services/CustomerService";
import { useLocation } from "react-router-dom/cjs/react-router-dom.min";
import { select } from "d3";
import ReactModal from "react-modal";
import EditDevice from "../edit";
import AddDevice from "../add";

const $ = window.$;

const ListDevice = ({ permissionActions, dataId }) => {

    const sendData = (data) => {
        let _customerId = document.getElementById("customerId").value;
        setCustomerId(_customerId);
        let _projectId = document.getElementById("projectId").value;
        // parentCallback({
        //     customerId: _customerId,
        //     projectId: _projectId
        // });
    }

    const history = useHistory();

    const [devices, setDevices] = useState([]);
    const [status, setStatus] = useState(null);
    const [errorsValidate, setErrorsValidate] = useState();
    const { t } = useTranslation();
    const [customerId, setCustomerId] = useState(1);
    const [projectId, setProjectId] = useState(0);
    const [projects, setProjects] = useState([]);
    const [project, setProject] = useState({});
    const [customers, setCustomers] = useState([]);
    const location = useLocation();
    const [customer, setCustomer] = useState({});
    const [searchKey, setSearchKey] = useState('');
    const [isOpenModalUpdateDevice, setIsModalUpdateDevice] = useState(false);
    const [isOpenModalAddDevice, setIsModalAddDevice] = useState(false);
    const [deviceId, setDeviceId] = useState();

    const handeChangeCustomer = (event) => {
        loadlistproject(event.target.value)
        setCustomerId(event.target.value)
        setProject({})
    }
    const handeChangeProject = (event) => {
        setProjectId(event.target.value)
        loadListDevice("", customerId, event.target.value)
    }
    const listDevice = async () => {
        let res = await DeviceService.listDevice();
        if (res.status === 200) {
            let devices = res.data;
            setDevices(devices);
        }
    }

    const setNotification = state => {
        if (state?.status === 200 && state?.message === "INSERT_SUCCESS" && state?.status !== -1) {
            setStatus({
                code: 200,
                message: t('content.category.device.list.add_success')
            });
        } else if (state?.status === 200 && state?.message === "UPDATE_SUCCESS" && state?.status !== -1) {
            setStatus({
                code: 200,
                message: t('content.category.device.list.edit_success')
            });
        }
        setTimeout(() => {
            setStatus(null);
        }, 3000);
    };

    const deleteDevice = (deviceId) => {
        $.confirm({
            type: 'red',
            typeAnimated: true,
            icon: 'fa fa-warning',
            title: t('content.title_confirm'),
            content: t('content.category.device.list.delete_confirm'),
            buttons: {
                confirm: {
                    text: t('content.accept'),
                    action: async function () {
                        let { status } = await DeviceService.deleteDevice(deviceId);
                        if (status === 200) {
                            $.alert({
                                title: t('content.title_notify'),
                                content: t('content.category.device.list.delete_success'),
                            });
                            listDevice();
                        } else {
                            $.alert({
                                title: t('content.title_notify'),
                                content: t('content.category.device.list.delete_error'),
                            });
                        }
                    }
                },
                cancel: {
                    text: t('content.cancel'),
                    action: function () {

                    }
                }
            }
        });
    }
    const loadlistCustomer = async () => {
        let res = await CustomerService.getListCustomer();
        if (res.status == 200) {
            setCustomers(res.data);
        }
        if (dataId != undefined && dataId.customerId != "" && dataId.projectId != "") {
            let res = await CustomerService.getCustomer(dataId.customer);
            setCustomer(res.data)
            let resp = await ProjectService.getProject(dataId.project);
            setProject(resp.data)
            loadlistproject(dataId.customer);

        } else {
            if (res.status === 200) {
                setCustomerId([]);
                loadlistproject("");
                loadListDevice("", "", "");
            }
        }
    }
    const loadListDevice = async (key, customerId, projectId) => {
        // let key = $('#keyword').val();
        // let customerid = $('#customerId').val();
        // let projectId = $('#projectId').val();
        let res = await DeviceService.searchDevice(key, customerId, projectId);
        setDevices(res.data);
    }

    const loadlistproject = async (idCustomer, idProject) => {
        if (idCustomer == "") {
            setProjects([]);
        } else {
            let resp = await ProjectService.getProjectByCustomerId(idCustomer);
            setProjects(resp.data);
            $('#projectId').html("");
            if (resp.data.length > 0) {
                $('#projectId').prop('disabled', false);
                let data = resp.data;
                $('#projectId').append('<option value="">All</option>');
                // $.each(data, function (index, value) {
                //     $('#projectId').append('<option value="' + value.projectId + '">' + value.projectName + '</option>')
                // });
                $.each(data, function (index, value) {
                    let option = $('<option>', {
                        value: value.projectId,
                        text: value.projectName,
                        selected: value.projectId == idProject // Check if the projectId matches idProject
                    });
                    $('#projectId').append(option);
                });
            } else {
                $('#projectId').prop('disabled', true);
            }
        }


    }
    const searchDevice = async () => {
        let keyword = document.getElementById("keyword").value;
        let _customerId = document.getElementById("customerId").value;
        setCustomerId(_customerId);
        let _projectId = document.getElementById("projectId").value;
        setProjectId(_projectId);
        if (keyword.length > 100) {
            setErrorsValidate(t('validate.super_manager.DESCRIPTION_MAX_SIZE_ERROR'));
        } else if (keyword === "" && _customerId == "" && _projectId == "") {
            setErrorsValidate(null);
            let res = await DeviceService.searchDevice(keyword, _customerId, _projectId);
            if (res.status === 400) {
                setErrorsValidate(res.data.errors);
            }
            if (res.status === 200) {
                let devices = res.data;
                setDevices(devices);
            }
        } else {
            setErrorsValidate(null);
            let res = await DeviceService.searchDevice(keyword, _customerId, _projectId);
            if (res.status === 400) {
                setErrorsValidate(res.data.errors);
            }
            if (res.status === 200) {
                let devices = res.data;
                setDevices(devices);
            }
        }
    }
    const sortDevice = (type, ls) => {
        if (type === 1) {
            devices.sort((a, b) => {
                if (sort == false) {
                    return a.deviceCode < b.deviceCode ? -1 : a.deviceCode > b.deviceCode ? 1 : 0;
                } else {
                    return a.deviceCode > b.deviceCode ? -1 : a.deviceCode < b.deviceCode ? 1 : 0;
                }
            });
            setSort(!sort)
        }
        if (type === 2) {
            if (sort == false) {
                devices.sort((a, b) => {
                    const nameA = a.deviceName.toUpperCase();
                    const nameB = b.deviceName.toUpperCase();
                    if (nameA < nameB) {
                        return -1;
                    }
                    if (nameA > nameB) {
                        return 1;
                    }
                });
            } else {
                devices.sort((a, b) => {
                    const nameA = a.deviceName.toUpperCase();
                    const nameB = b.deviceName.toUpperCase();
                    if (nameA > nameB) {
                        return -1;
                    }
                    if (nameA < nameB) {
                        return 1;
                    }
                });
            }
            setSort(!sort);
        }
        if (type === 3) {
            if (sort == false) {
                devices.sort((a, b) => {
                    let a1;
                    let b1;
                    if (a.ip == null) {
                        a1 = '';
                    } else {
                        a1 = a.ip.toUpperCase();
                    }
                    if (b1 == null) {
                        b1 = ''
                    } else {
                        b.ip.toUpperCase();
                    }
                    if (a1 < b1) {
                        return -1;
                    }
                    if (a1 > b1) {
                        return 1;
                    }
                });
            } else {
                devices.sort((a, b) => {
                    let a1;
                    let b1;
                    if (a.ip == null) {
                        a1 = '';
                    } else {
                        a1 = a.ip.toUpperCase();
                    }
                    if (b1 == null) {
                        b1 = ''
                    } else {
                        b.ip.toUpperCase();
                    }
                    if (a1 > b1) {
                        return -1;
                    }
                    if (a1 < b1) {
                        return 1;
                    }
                });
            }
            setSort(!sort);
        } if (type === 4) {
            devices.sort((a, b) => {
                if (sort == false) {
                    return a.uid < b.uid ? -1 : a.uid > b.uid ? 1 : 0;
                } else {
                    return a.uid > b.uid ? -1 : a.uid < b.uid ? 1 : 0;
                }
            });
            setSort(!sort)
        } if (type == 5) {
            devices.sort((a, b) => {
                const nameA = a.customerName.toUpperCase();
                const nameB = b.customerName.toUpperCase();
                if (nameA < nameB) {
                    return -1;
                }
                if (nameA > nameB) {
                    return 1;
                }
            });
            setSort(!sort);
        } if (type == 6) {
            devices.sort((a, b) => {
                const nameA = a.projectName.toUpperCase();
                const nameB = b.projectName.toUpperCase();
                if (nameA < nameB) {
                    return -1;
                }
                if (nameA > nameB) {
                    return 1;
                }
            });
            setSort(!sort);
        } if (type === 7) {
            if (sort == false) {
                devices.sort((a, b) => {
                    let a1;
                    let b1;
                    if (a.address == null) {
                        a1 = '';
                    } else {
                        a1 = a.address.toUpperCase();
                    }
                    if (b1 == null) {
                        b1 = ''
                    } else {
                        b.address.toUpperCase();
                    }
                    if (a1 < b1) {
                        return -1;
                    }
                    if (a1 > b1) {
                        return 1;
                    }
                });
            } else {
                devices.sort((a, b) => {
                    let a1;
                    let b1;
                    if (a.address == null) {
                        a1 = '';
                    } else {
                        a1 = a.address.toUpperCase();
                    }
                    if (b1 == null) {
                        b1 = ''
                    } else {
                        b.address.toUpperCase();
                    }
                    if (a1 > b1) {
                        return -1;
                    }
                    if (a1 < b1) {
                        return 1;
                    }
                });
            }
            setSort(!sort);
        } if (type === 8) {
            if (sort == false) {
                devices.sort((a, b) => {
                    let a1;
                    let b1;
                    if (a.location == null) {
                        a1 = '';
                    } else {
                        a1 = a.location.toUpperCase();
                    }
                    if (b1 == null) {
                        b1 = ''
                    } else {
                        b1 = b.location.toUpperCase();
                    }
                    if (a1 < b1) {
                        return -1;
                    }
                    if (a1 > b1) {
                        return 1;
                    }
                });
            } else {
                devices.sort((a, b) => {
                    let a1;
                    let b1;
                    if (a.location == null) {
                        a1 = '';
                    } else {
                        a1 = a.location.toUpperCase();
                    }
                    if (b1 == null) {
                        b1 = ''
                    } else {
                        b1 = b.location.toUpperCase();
                    }
                    if (a1 > b1) {
                        return -1;
                    }
                    if (a1 < b1) {
                        return 1;
                    }
                });
            }
            setSort(!sort);
        } if (type === 9) {
            if (sort == false) {
                devices.sort((a, b) => {
                    const dateA = new Date(a.updateDate)
                    const dateB = new Date(b.updateDate)
                    if (dateA - dateB > 0) {
                        return 1;
                    } else if (dateA - dateB < 0) {
                        return -1;
                    } else {
                        return 0;
                    }
                });
            } else {
                devices.sort((a, b) => {
                    const dateA = new Date(a.updateDate)
                    const dateB = new Date(b.updateDate)
                    if (dateA - dateB < 0) {
                        return 1;
                    } else if (dateA - dateB > 0) {
                        return -1;
                    } else {
                        return 0;
                    }
                });
            }
            setSort(!sort)
        }
        if (type === 10) {
            if (sort == false) {
                devices.sort((a, b) => {
                    let a1;
                    let b1;
                    if (a.systemMapName == null) {
                        a1 = '';
                    } else {
                        a1 = a.systemMapName.toUpperCase();
                    }
                    if (b1 == null) {
                        b1 = ''
                    } else {
                        b.systemMapName.toUpperCase();
                    }
                    if (a1 < b1) {
                        return -1;
                    }
                    if (a1 > b1) {
                        return 1;
                    }
                });
            } else {
                devices.sort((a, b) => {
                    let a1;
                    let b1;
                    if (a.systemMapName == null) {
                        a1 = '';
                    } else {
                        a1 = a.systemMapName.toUpperCase();
                    }
                    if (b1 == null) {
                        b1 = ''
                    } else {
                        b.systemMapName.toUpperCase();
                    }
                    if (a1 > b1) {
                        return -1;
                    }
                    if (a1 < b1) {
                        return 1;
                    }
                });
            }
            setSort(!sort);
        }
        if (type === 11) {
            if (sort == false) {
                devices.sort((a, b) => {
                    const nameA = a.deviceTypeName.toUpperCase();
                    const nameB = b.deviceTypeName.toUpperCase();
                    if (nameA < nameB) {
                        return -1;
                    }
                    if (nameA > nameB) {
                        return 1;
                    }
                });
            } else {
                devices.sort((a, b) => {
                    const nameA = a.deviceTypeName.toUpperCase();
                    const nameB = b.deviceTypeName.toUpperCase();
                    if (nameA > nameB) {
                        return -1;
                    }
                    if (nameA < nameB) {
                        return 1;
                    }
                });
            }
            setSort(!sort);
        }

    }
    const [sort, setSort] = useState(false);

    const handleDetailDevice = (deviceId) => {
        setIsModalUpdateDevice(true)
        setDeviceId(deviceId)
    }

    const handleAddDevice = () => {
        setIsModalAddDevice(true)
    }

    const callbackFunction = (isCloseModal, customerId, projectId) => {
        let cusId = customerId != undefined ? customerId : "";
        let proId = projectId != undefined ? projectId : "";
        loadListDevice("", cusId, proId)
        setCustomerId(cusId)
        setProjectId(proId)
        loadlistproject(cusId, proId)
        setIsModalUpdateDevice(isCloseModal)
        setIsModalAddDevice(isCloseModal)
    }

    // const changeFormByCondition = () => {
    //     return <EditDevice parentCloseModal={callbackFunction} deviceId={deviceId} ></EditDevice>
    // }

    const handleCancelModaleUpdate = async () => {
        searchDevice();
        setIsModalUpdateDevice(false);
        setIsModalAddDevice(false)
    }
    useEffect(() => {
        document.title = t('content.category.device.list.title')
        loadlistCustomer();
        // loadListDevice();
        if (location.state) {
            setNotification(location.state);
        };
    }, []);

    useEffect(() => {

    }, [deviceId]);

    return (
        <>
            <div id="page-body">
                <div id="main-title">
                    <h5 className="d-block mb-0 float-left text-uppercase"><i className="fas fa-server"></i> &nbsp;{t('content.category.device.list.header')}</h5>

                    {
                        permissionActions.hasCreatePermission &&
                        <button type="button" className="btn btn-primary btn-rounded btn-new float-right" style={{ zIndex: "0" }} onClick={() => {
                            sendData({
                                customerId: customerId,
                                projectId: projectId
                            })
                            // history.push("/category/device/add");
                            handleAddDevice();
                        }}>
                            <i className="fa-solid fa-plus"></i>
                        </button>
                    }

                </div>
                {
                    status != null &&
                    <div>
                        {
                            status.code === 200 ?
                                <div className="alert alert-success alert-dismissible fade show" role="alert">
                                    <p className="m-0 p-0">{status?.message}</p>
                                </div> :
                                <div className="alert alert-warning" role="alert">
                                    <p className="m-0 p-0">{status?.message}</p>
                                </div>
                        }
                    </div>

                }
                {
                    errorsValidate != null &&
                    <div className="alert alert-warning" role="alert">
                        <p className="m-0 p-0">{errorsValidate}</p>
                    </div>
                }
                <div id="main-search">
                    <div className="input-group search-item mb-3 float-left">

                        <div className="input-group float-left mr-1" style={{ width: 420 }}>
                            <div className="input-group-prepend">
                                <span className="input-group-text pickericon">
                                    <span className="fas fa-user-tie"></span>
                                </span>
                            </div>

                            <select
                                name="customerId"
                                id="customerId"
                                defaultValue={customerId}
                                className="custom-select block custom-select-sm"
                                onChange={(event) => { handeChangeCustomer(event) }}
                            >
                                <option value="">All</option>
                                {customers?.map((cus, index) => {
                                    return (
                                        <option selected={cus.customerId == customerId ? true : false} key={index + 1} value={cus.customerId}>
                                            {cus.customerName}
                                        </option>
                                    );
                                })}
                            </select>

                        </div>
                        <div className="input-group float-left mr-1 input-project-m" style={{ width: 300 }}>
                            <div className="input-group-prepend">
                                <span className="input-group-text pickericon">
                                    <span className="far fa-file-lines"></span>
                                </span>
                            </div>

                            <select
                                id="projectId"
                                className="custom-select block custom-select-sm projectId"
                            >
                                <option value="">All</option>

                            </select>

                        </div>
                        <div className="input-group-prepend">
                            <span className="input-group-text" id="inputGroup-sizing-default">{t('content.description')}</span>
                        </div>
                        <input type="text" id="keyword" className="form-control mr-2" aria-label={t('content.description')} aria-describedby="inputGroup-sizing-sm" onKeyDown={e => e.key === 'Enter' && searchDevice()} />
                    </div>

                    <div className="search-buttons float-left">
                        <button type="button" className="btn btn-outline-secondary" onClick={() => searchDevice()}>
                            <i className="fa-solid fa-magnifying-glass"></i>
                        </button>
                    </div>
                </div>

                <div id="main-content">
                    <table className="table">
                        <thead>
                            <tr>
                                <th width="40px">{t('content.no')}</th>
                                <th width="120px" style={{ position: 'relative' }} >
                                    {t('content.category.device.lable_device_id')}
                                    <button
                                        onClick={() => sortDevice(1, devices)}
                                        style={{ backgroundColor: 'transparent', border: 'none', color: 'white', position: 'absolute', left: '85%' }}
                                        className="fa-solid fa-sort"
                                    />
                                </th>
                                <th width="60px" style={{ position: 'relative' }} >UID
                                    <button
                                        onClick={() => sortDevice(4, devices)}
                                        style={{ backgroundColor: 'transparent', border: 'none', color: 'white', position: 'absolute', left: '70%' }}
                                        className="fa-solid fa-sort"
                                    />
                                </th>
                                <th style={{ position: 'relative' }} >{t('content.category.device.lable_device_name')}
                                    <button
                                        onClick={() => sortDevice(2, devices)}
                                        style={{ backgroundColor: 'transparent', border: 'none', color: 'white', position: 'absolute', left: '85%' }}
                                        className="fa-solid fa-sort"
                                    />
                                </th>
                                <th width="200px" style={{ position: 'relative' }}>{t('content.category.device.lable_device_type_name')}
                                    <button
                                        onClick={() => sortDevice(11, devices)}
                                        style={{ backgroundColor: 'transparent', border: 'none', color: 'white', position: 'absolute', left: '90%' }}
                                        className="fa-solid fa-sort"
                                    /></th>
                                <th style={{ position: 'relative' }}>{t('content.customer')}
                                    <button
                                        onClick={() => sortDevice(5, devices)}
                                        style={{ backgroundColor: 'transparent', border: 'none', color: 'white', position: 'absolute', left: '90%' }}
                                        className="fa-solid fa-sort"
                                    />
                                </th>
                                <th style={{ position: 'relative' }}>{t('content.project')}
                                    <button
                                        onClick={() => sortDevice(6, devices)}
                                        style={{ backgroundColor: 'transparent', border: 'none', color: 'white', position: 'absolute', left: '90%' }}
                                        className="fa-solid fa-sort"
                                    /> </th>
                                <th style={{ position: 'relative' }}> {t('content.system_map')}
                                    <button
                                        onClick={() => sortDevice(10, devices)}
                                        style={{ backgroundColor: 'transparent', border: 'none', color: 'white', position: 'absolute', left: '90%' }}
                                        className="fa-solid fa-sort"
                                    />
                                </th>
                                <th style={{ position: 'relative' }}>{t('content.address')}
                                    <button
                                        onClick={() => sortDevice(7, devices)}
                                        style={{ backgroundColor: 'transparent', border: 'none', color: 'white', position: 'absolute', left: '90%' }}
                                        className="fa-solid fa-sort"
                                    />
                                </th>
                                <th width="150px" style={{ position: 'relative' }}>{t('content.super_manager')}
                                    <button
                                        onClick={() => sortDevice(8, devices)}
                                        style={{ backgroundColor: 'transparent', border: 'none', color: 'white', position: 'absolute', left: '80%' }}
                                        className="fa-solid fa-sort"
                                    />
                                </th>
                                <th width="150px" style={{ position: 'relative' }}>{t('content.update_date')}
                                    <button
                                        onClick={() => sortDevice(9, devices)}
                                        style={{ backgroundColor: 'transparent', border: 'none', color: 'white', position: 'absolute', left: '85%' }}
                                        className="fa-solid fa-sort"
                                    />
                                </th>
                                <th width="55px"><i className="fa-regular fa-hand"></i></th>
                            </tr>
                        </thead>
                        <tbody>
                            {
                                devices?.map((item, index) => (
                                    <tr key={index}>
                                        <td className="text-center">{index + 1}</td>
                                        <td>{item.deviceCode}</td>
                                        <td className="text-right">{item.uid}</td>
                                        <td className="text-left">{item.deviceName}</td>
                                        <td className="text-left">{item.deviceTypeName}</td>
                                        <td className="text-left">{item.customerName}</td>
                                        <td className="text-left">{item.projectName}</td>
                                        <td className="text-left">{item.systemMapName}</td>
                                        <td className="text-left">{item.address}</td>
                                        <td className="text-left">{item.location}</td>
                                        <td className="text-center">{moment(item.updateDate).format(CONS.DATE_FORMAT)}</td>
                                        <td className="text-center">
                                            {
                                                permissionActions.hasUpdatePermission &&
                                                // <Link className="button-icon" to={`/category/device/edit/` + item.deviceId} title={t('content.title_icon_edit')}>
                                                //     <img height="16px" src="/resources/image/icon-edit.png" alt="Chỉnh sửa" />
                                                // </Link>
                                                <span className="button-icon" onClick={() => handleDetailDevice(item.deviceId)} title={t('content.title_icon_edit')}>
                                                    <img height="16px" src="/resources/image/icon-edit.png" alt="Chỉnh sửa" />
                                                </span>
                                            }
                                            {
                                                permissionActions.hasDeletePermission &&
                                                <Link to={``} className="button-icon" title={t('content.title_icon_delete')} onClick={(e) => {
                                                    e.preventDefault();
                                                    deleteDevice(item.deviceId);
                                                }}>
                                                    <img height="16px" src="/resources/image/icon-delete.png" alt="Xóa" />
                                                </Link>
                                            }
                                        </td>
                                    </tr>
                                ))
                            }
                            <tr style={{ display: devices.length === 0 ? "contents" : "none" }}>
                                <td className="text-center" colSpan={12}>{t('content.no_data')}</td>
                            </tr>
                        </tbody>
                    </table>

                    <div id="main-button">
                        <button type="button" className="btn btn-outline-secondary btn-s3m w-120px float-left" onClick={() => history.push("/")}>
                            <i className="fa-solid fa-house"></i> &nbsp;{t('content.home')}
                        </button>
                    </div>
                </div>
            </div>
            <ReactModal
                isOpen={isOpenModalUpdateDevice}
                onRequestClose={() => {
                    handleCancelModaleUpdate();
                }}
                style={{
                    content: {
                        width: "90%",
                        height: "90%",
                        margin: "auto",
                        marginTop: "10px",
                    },
                }}
            >
                <EditDevice deviceId={deviceId} parentCloseModal={callbackFunction}></EditDevice>
            </ReactModal>
            <ReactModal
                isOpen={isOpenModalAddDevice}
                onRequestClose={() => {
                    handleCancelModaleUpdate();
                }}
                style={{
                    content: {
                        width: "90%",
                        height: "90%",
                        margin: "auto",
                        marginTop: "10px",
                    },
                }}
            >
                <AddDevice customerId={customerId} projectId={projectId} parentCloseModal={callbackFunction}></AddDevice>
            </ReactModal>
        </>
    )
}

export default ListDevice;