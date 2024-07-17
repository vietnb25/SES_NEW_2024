import React, { useEffect, useState, useRef } from "react";
import CustomerService from "../../../../services/CustomerService";
import ProjectService from "../../../../services/ProjectService";
import $ from "jquery";
import SettingService from "../../../../services/SettingService";
import CONS from "../../../../constants/constant";
import moment from "moment";
import { Link, useHistory, useParams } from "react-router-dom";
import { useLocation } from "react-router-dom/cjs/react-router-dom.min";
import receiverService from '../../../../services/ReceiverService';
import DeviceService from '../../../../services/DeviceService';
import DeviceTypeService from "../../../../services/DeviceTypeService";
import { Checkbox } from 'primereact/checkbox';
import { MultiSelect } from 'primereact/multiselect';
import { useFormik } from 'formik';
import * as Yup from "yup";
import { ConfirmPopup, confirmPopup } from 'primereact/confirmpopup';
import { Toast } from 'primereact/toast';
import "./index.css"
import SettingLink from "../setting/setting-link";
import { t } from "i18next";

const ReceiverWarning = (props) => {

    const param = useParams();

    const [systemtype, setSystemType] = useState([]);
    const [customerId, setCustomerId] = useState(0);
    const [projectId, setProjectId] = useState(0);
    const [status, setStatus] = useState(null);
    const location = useLocation();
    const history = useHistory();
    const [receivers, setReceivers] = useState([]);
    const [devices, setDevices] = useState([]);
    const [devicesSelected, setDevicesSelected] = useState([]);
    const [deviceSearch, setDeviceSearch] = useState([]);
    const [selectedInputDevice, setSelectedInputDevice] = useState(true);
    const [deviceId, setDeviceId] = useState(0);
    const [checkAll, setCheckAll] = useState(false);

    const [visible, setVisible] = useState(false);
    const toast = useRef(null);
    const [deviceTypeId, setDeviceTypeId] = useState();
    const [receiver, setReceiver] = useState({
        receiverId: "",
        name: "",
        phone: "",
        email: "",
        description: ""
    });
    const [deviceTypeName, setDeviceTypeName] = useState();
    const [deviceTypes, setDeviceTypes] = useState([]);

    const getInfoAdd = async () => {
        listProject();
    }

    const listProject = async () => {
        let customerId = param.customerId;
        if (customerId != null && parseInt(customerId) > 0) {
            setCustomerId(customerId);
            $(".input-project-m").show();
            let res = await ProjectService.getProjectByCustomerId(customerId);
            $('#projectId').html("");
            if (res.data.length > 0) {
                $('#projectId').prop('disable', false);
                let data = res.data;
                $.each(data, function (index, value) {
                    if (param.projectId == null || param.projectId == undefined) {
                        $('#projectId').append('<option value="' + value.projectId + '">' + value.projectName + '</option>')
                    } else {
                        if (param.projectId == value.projectId) {
                            $('#projectId').append('<option value="' + value.projectId + '">' + value.projectName + '</option>')
                        }
                    }

                });
            } else {
                $('#projectId').prop('disable', true);
            }
        } else {
            $(".input-project-m").hide();
        }
        getListReceiver();
    }

    const getListReceiver = async () => {

        let customerId = param.customerId;
        setCustomerId(customerId);
        let projectId = document.getElementById("projectId").value;
        setProjectId(projectId);
        let typeSystem = props.typeSystem;
        setSystemType(typeSystem);
        let res = await receiverService.listReceiver(projectId, typeSystem);
        if (res.status === 200) {
            setReceivers(res.data);
        }

    }

    const setNotification = state => {
        if (state?.status === 200 && state?.message === "INSERT_SUCCESS") {
            setStatus({
                code: 200,
                message: "Thêm mới cài đặt thành công"
            });
        } else if (state?.status === 200 && state?.message === "UPDATE_SUCCESS") {
            setStatus({
                code: 200,
                message: "Chỉnh sửa cài đặt thành công"
            });
        }
        setTimeout(() => {
            setStatus(null);
        }, 3000);
    };

    const [warningInverter, setWarningInverter] = useState([
        {
            warningType: CONS.WARNING_TYPE_MST.CHAM_DAT,
            warningName: t('content.home_page.warning_tab.ground_fault'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.DIEN_AP_CAO_DC,
            warningName: t('content.home_page.warning_tab.dc_over_volt'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.MAT_KET_NOI_AC,
            warningName: t('content.home_page.warning_tab.ac_disconnect'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.MAT_KET_NOI_DC,
            warningName: t('content.home_page.warning_tab.dc_disconnect'),
        },
        {
            warningType: CONS.WARNING_TYPE_MST.MAT_NGUON_LUOI,
            warningName: t('content.home_page.warning_tab.grid_disconnect'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.DONG_MO_CUA_INVERTER,
            warningName: t('content.home_page.warning_tab.door_operation'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.NGAT_THU_CONG,
            warningName: t('content.home_page.warning_tab.manual_shutdown'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.NHIET_DO_CAO_INVERTER,
            warningName: t('content.home_page.warning_tab.over_temp'),
        },
        {
            warningType: CONS.WARNING_TYPE_MST.TAN_SO_CAO_INVERTER,
            warningName: t('content.home_page.warning_tab.over_frequency'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.TAN_SO_THAP_INVERTER,
            warningName: t('content.home_page.warning_tab.under_frequency'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.DIEN_AP_CAO_AC,
            warningName: t('content.home_page.warning_tab.ac_over_volt'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.DIEN_AP_THAP_AC,
            warningName: t('content.home_page.warning_tab.ac_under_volt'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.HONG_CAU_CHI,
            warningName: t('content.home_page.warning_tab.blown_fuse'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.NHIET_DO_THAP_INVERTER,
            warningName: t('content.home_page.warning_tab.under_temp'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.MEMORY_LOSS,
            warningName: t('content.home_page.warning_tab.memory_loss'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.HW_TEST_FAILURE,
            warningName: t('content.home_page.warning_tab.hw_test_failure'),
        },
    ]);
    const [warningMeter, setWarningMeter] = useState([
        {
            warningType: CONS.WARNING_TYPE_MST.NGUONG_AP_CAO,
            warningName: t('content.home_page.warning_tab.over_volt'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.NGUONG_AP_THAP,
            warningName: t('content.home_page.warning_tab.under_volt'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.QUA_TAI,
            warningName: t('content.home_page.warning_tab.over_load'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.HE_SO_CONG_SUAT_THAP,
            warningName: t('content.home_page.warning_tab.under_power_factor'),
        },
        {
            warningType: CONS.WARNING_TYPE_MST.TAN_SO_THAP_METER,
            warningName: t('content.home_page.warning_tab.under_frequency'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.TAN_SO_CAO_METER,
            warningName: t('content.home_page.warning_tab.over_frequency'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.LECH_PHA,
            warningName: t('content.home_page.warning_tab.phase_deviation'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.SONG_HAI_DONG_DIEN_BAC_N,
            warningName: t('content.home_page.warning_tab.total_current_harmonics'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.SONG_HAI_DIEN_AP_BAC_N,
            warningName: t('content.home_page.warning_tab.voltage_harmonics_n'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.TONG_MEO_SONG_HAI_DIEN_AP,
            warningName: t('content.home_page.warning_tab.total_voltage_harmonics'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.TONG_MEO_SONG_HAI_DONG_DIEN,
            warningName: t('content.home_page.warning_tab.total_current_harmonics'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.NGUOC_PHA,
            warningName: t('content.home_page.warning_tab.phase_reverse'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.MAT_DIEN_TONG,
            warningName: t('content.home_page.warning_tab.lost_power'),

        }

    ]);

    const [warningTempHumidity, setWarningTempHumidity] = useState([
        {
            warningType: CONS.WARNING_TYPE_MST.NHIET_DO_CA0_TEMP,
            warningName: 'Nhiệt độ cao',

        },
        {
            warningType: CONS.WARNING_TYPE_MST.NHIET_DO_THAP_TEMP,
            warningName: 'Nhiệt độ thấp',

        },
        {
            warningType: CONS.WARNING_TYPE_MST.DO_AM_CAO,
            warningName: 'Độ ẩm cao',

        },
        {
            warningType: CONS.WARNING_TYPE_MST.DO_AM_THAP,
            warningName: 'Đổ ẩm cao',

        }
    ]);


    const [warningStatus, setWarningStatus] = useState([
        {
            warningType: CONS.WARNING_TYPE_MST.FI_TU_RMU,
            warningName: t('content.home_page.warning_tab.f1_rmu'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.KHOANG_TON_THAT,
            warningName: t('content.home_page.warning_tab.anti_loss_compartment'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.DONG_MO_CUA_STATUS,
            warningName: t('content.home_page.warning_tab.door_operation'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.MUC_DAU_THAP,
            warningName: t('content.home_page.warning_tab.under_oil'),
        },
        {
            warningType: CONS.WARNING_TYPE_MST.ROLE_GAS,
            warningName: t('content.home_page.warning_tab.gas_relay'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.CHAM_VO,
            warningName: t('content.home_page.warning_tab.touch_shell'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.MUC_DAU_CAO,
            warningName: t('content.home_page.warning_tab.over_oil'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.CAM_BIEN_HONG_NGOAI,
            warningName: t('content.home_page.warning_tab.infrared_sensor'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.AP_SUAT_NOI_BO_MBA,
            warningName: t('content.home_page.warning_tab.internal_pressure_transformer'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.ROLE_NHIET_DO_DAU,
            warningName: t('content.home_page.warning_tab.oil_temp_relay'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.NHIET_DO_CUON_DAY,
            warningName: t('content.home_page.warning_tab.coil_temp'),

        },
        {
            warningType: CONS.WARNING_TYPE_MST.KHI_GAS_MBA,
            warningName: t('content.home_page.warning_tab.transformer_gas'),

        }
    ]);

    const [warningHTR02, setWarningHTR02] = useState([
        {
            warningType: CONS.WARNING_TYPE_MST.PHONG_DIEN_HTR02,
            warningName: t('content.home_page.warning_tab.energy_discharge'),

        },
    ]);

    const [warningAMS01, setWarningAMS01] = useState([
        {
            warningType: CONS.WARNING_TYPE_MST.PHONG_DIEN_AMS01,
            warningName: t('content.home_page.warning_tab.energy_discharge'),

        },
    ]);

    const initialValues = {
        receiverId: "",
        name: "",
        phone: "",
        email: "",
        description: ""
    };

    const formik = useFormik({
        initialValues,
        validationSchema: Yup.object().shape({
            name: Yup.string().required("Tên người nhận không được để trống."),
            phone: Yup.string().required("Số điện thoại không được để trống.").max(20, "Số điện thoại không quá 20 kí tự."),
            email: Yup.string()
                .email("Email không đúng định dạng.")
                .matches(/.+@.+\.[A-Za-z]+$/, "Email không đúng định dạng.")
                .required("Email không được để trống!")
        }),
        onSubmit: async (data) => {
            let res = await receiverService.addNewReceiver(data, projectId, systemtype);
            if (res.status === 200) {
                $('#new-receiver').hide();
                getResetReceiver();
                getListReceiver();
                toast.current.show({ severity: 'success', summary: 'Success', detail: t('content.home_page.receiver_warning.add_success'), life: 3000 });
            } else {
                $('#new-receiver').show();
                toast.current.show({ severity: 'error', summary: 'Error', detail: t('content.home_page.receiver_warning.add_error'), life: 3000 });
            }
        }
    });

    const formikUpdate = useFormik({
        initialValues: receiver,
        validationSchema: Yup.object().shape({
            name: Yup.string().required("Tên người nhận không được để trống."),
            phone: Yup.string().required("Số điện thoại không được để trống.").max(20, "Số điện thoại không quá 20 kí tự."),
            email: Yup.string()
                .email("Email không đúng định dạng.")
                .matches(/.+@.+\.[A-Za-z]+$/, "Email không đúng định dạng.")
                .required("Email không được để trống!")
        }),
        enableReinitialize: true,
        onSubmit: async (data) => {
            let res = await receiverService.updateReceiver(data);


            if (res.status === 200) {
                $('#update-receiver-' + data.receiverId).hide();
                getListReceiver();
                toast.current.show({ severity: 'success', summary: 'Success', detail: t('content.home_page.receiver_warning.edit_success'), life: 3000 });
            } else {
                $('#update-receiver-' + data.receiverId).show();
                toast.current.show({ severity: 'error', summary: 'Error', detail: t('content.home_page.receiver_warning.edit_error'), life: 3000 });
            }
        }
    });

    const getResetReceiver = () => {
        $('#name').val("");
        $('#email').val("");
        $('#phone').val("");
        $('#description').val("");
        formik.setFieldValue("name", "");
        formik.setFieldValue("email", "");
        formik.setFieldValue("phone", "");
        formik.setFieldValue("description", "");
    }

    const [warningSelected, setWarningSelected] = useState([]);

    const getListDevice = async (receiverId, deviceTypeId) => {

        let select = document.getElementById("cbo-device-id");
        select.value = "0";

        setDeviceTypeId(deviceTypeId);
        let res = await DeviceService.listDevice(projectId, systemtype, deviceTypeId);
        if (res.status === 200) {
            if (res.data.length > 0) {
                setDevices(res.data)
                onLoadDevice(0, receiverId, res.data)
            }
        }
    };

    const onWarningChange = (e) => {

        let _warning = [...warningSelected];
        if (e.checked)
            _warning.push(e.value);
        else {
            _warning = _warning?.filter(item => item.warningType !== e.value.warningType);
        }
        setWarningSelected(_warning);
    };

    const changeDevice = (deviceId, deviceName, receiverId) => {
        document.getElementById("keyword").value = deviceName;
        onLoadDevice(deviceId, receiverId);
        setDeviceSearch([]);
    };

    const searchDevice = (e) => {
        let device_name = document.getElementById("keyword").value;
        if (device_name === "") {
            setDeviceSearch([]);
        } else {
            let deviceSearch = devices?.filter(d => d.deviceName.includes(device_name));
            setDeviceSearch(deviceSearch);
        }
    };

    const onLoadDevice = async (_deviceId, receiverId, listDevice) => {
        if(_deviceId != 0) {
            setDeviceId(_deviceId);
        } else {
            let deviceTemp = "";
            listDevice.forEach((element, i) => {
                if(i == 0) {
                    deviceTemp += element.deviceId
                } else {
                     deviceTemp += "," + element.deviceId
                }            
            });
            setDeviceId(deviceTemp)
        }
        
        let deviceType = null;
        if (_deviceId != null && _deviceId != 0) {
            let resInfo = await DeviceService.detailsDevice(_deviceId);

            if (resInfo.status == 200) {
                deviceType = resInfo.data.deviceTypeId;
                setDeviceTypeId(resInfo.data.deviceTypeId);
                funcOnLoadWarning(deviceType, _deviceId, receiverId)
            }
            getListReceiver();
        } else {
            funcOnLoadWarning(deviceTypeId, _deviceId, receiverId)
            getListReceiver();
        }
    };

    const funcOnLoadWarning = async (deviceType, _deviceId, receiverId) => {
        let res = await receiverService.getWarningByDeviceId(receiverId, _deviceId);
        let _warningRes = [];
        if (deviceType == 4) {
            warningStatus?.forEach(e => {
                if (res.data?.some(item => item == e.warningType)) {
                    _warningRes.push(e);
                }
            })
            setWarningSelected(_warningRes);
            if (_warningRes.length < warningStatus.length) {
                setCheckAll(false);
            } else {
                setCheckAll(true);
            }
        } else if (deviceType == 3) {
            warningTempHumidity?.forEach(e => {
                if (res.data?.some(item => item == e.warningType)) {
                    _warningRes.push(e);
                }
            })
            setWarningSelected(_warningRes);
            if (_warningRes.length < warningTempHumidity.length) {
                setCheckAll(false);
            } else {
                setCheckAll(true);
            }
        } else if (deviceType == 2) {
            warningInverter?.forEach(e => {
                if (res.data?.some(item => item == e.warningType)) {
                    _warningRes.push(e);
                }
            })
            setWarningSelected(_warningRes);
            if (_warningRes.length < warningInverter.length) {
                setCheckAll(false);
            } else {
                setCheckAll(true);
            }
        } else if (deviceType == 1) {
            warningMeter?.forEach(e => {
                if (res.data?.some(item => item == e.warningType)) {
                    _warningRes.push(e);
                }
            })
            setWarningSelected(_warningRes);
            if (_warningRes.length < warningMeter.length) {
                setCheckAll(false);
            } else {
                setCheckAll(true);
            }
        } else if (deviceType == 5) {
            warningHTR02?.forEach(e => {
                if (res.data?.some(item => item == e.warningType)) {
                    _warningRes.push(e);
                }
            })
            setWarningSelected(_warningRes);
            if (_warningRes.length < warningHTR02.length) {
                setCheckAll(false);
            } else {
                setCheckAll(true);
            }
        } else if (deviceType == 6) {
            warningAMS01?.forEach(e => {
                if (res.data?.some(item => item == e.warningType)) {
                    _warningRes.push(e);
                }
            })
            setWarningSelected(_warningRes);
            if (_warningRes.length < warningAMS01.length) {
                setCheckAll(false);
            } else {
                setCheckAll(true);
            }
        }
    }

    const save = async (receiverId) => {
        let res = await receiverService.saveWarningInfor(warningSelected.map(item => item.warningType), systemtype, param.customerId, param.projectId, deviceId, receiverId);
        if (res.status === 200) {
            toast.current.show({ severity: 'success', summary: 'Success', detail: 'Thêm thông tin thành công', life: 3000 });
        }
        else {
            toast.current.show({ severity: 'error', summary: 'Error', detail: 'Thêm thông tin thất bại', life: 3000 });
        }
    };

    const accept = async (receiverId) => {
        let res = await receiverService.deleteReceiver(receiverId);
        if (res.status === 200) {
            toast.current.show({ severity: 'success', summary: 'Success', detail: 'Xoá thành công', life: 3000 });
            getListReceiver();
        }
        else {
            toast.current.show({ severity: 'error', summary: 'Error', detail: 'Không thể xóa thành công', life: 3000 });
        }

    };

    const checkAlls = (e) => {
        if (e.checked) {
            if (deviceTypeId == 1) {
                setWarningSelected(warningMeter);
                setCheckAll(true);
            } else if (deviceTypeId == 2) {
                setWarningSelected(warningInverter);
                setCheckAll(true);
            } else if (deviceTypeId == 3) {
                setWarningSelected(warningTempHumidity);
                setCheckAll(true);
            } else if (deviceTypeId == 4) {
                setWarningSelected(warningStatus);
                setCheckAll(true);
            } else if (deviceTypeId == 5) {
                setWarningSelected(warningHTR02);
                setCheckAll(true);
            } else if (deviceTypeId == 6) {
                setWarningSelected(warningAMS01);
                setCheckAll(true);
            }

        }
        else {
            setWarningSelected([]);
            setCheckAll(false);
        }
    };

    const getListDeviceTypeMst = async (projectId, receiverId) => {
        setProjectId(projectId)
        let res = await DeviceTypeService.listDeviceTypeMst(props.typeSystem, param.customerId, projectId);
        if (res.status == 200 && res.data.length > 0) {
            setDeviceTypes(res.data)
            setDeviceTypeId(res.data[0].id)
            setDeviceTypeName(res.data[0].name)
            getListDevice(receiverId, res.data[0].id)
            // getSettings(param.customerId, param.projectId, typeSystem, res.data[0].id, deviceIds)
        } else {
            setDeviceTypes([])
            setDeviceTypeId("")
            setDeviceTypeName("")
            getListDevice(receiverId)
        }

    }

    useEffect(() => {
        document.title = "Người nhận cảnh báo";
        if (location.state) {
            setNotification(location.state);
        };
        const fetchData = async () => {
            await getInfoAdd();
            await getListReceiver();
        }
        fetchData().catch(console.error);

    }, [param.customerId, param.projectId, props.typeSystem]);

    return (
        <div>
            {/* <div className="title-up-warning text-left">
                <div style={{ marginTop: "-21px", color: "white" }}><i className="fa-solid fa-triangle-exclamation ml-1" style={{ color: "#fff" }}></i> NGƯỜI NHẬN CẢNH BÁO
                </div>
            </div> */}
            <div className="float-right mr-1" data-toggle="modal" data-target={"#new-receiver"} ><i className="fas fa-solid fa-circle-plus fa-3x float-right add-user"></i></div>
            <div id="" className="mt-1">
                <div id="" style={{ height: 32 }}>
                    <div className="input-group float-left mr-1 input-project-m" style={{ width: 270 }}>
                        <div className="input-group-prepend background-ses">
                            <span className="input-group-text pickericon">
                                <span className="fas fa-user-group"></span>
                            </span>
                        </div>
                        <select id="projectId" className="custom-select block custom-select-sm projectId" defaultValue={projectId} onChange={() => getListReceiver()} >
                        </select>
                    </div>
                    {/* <div className="input-group float-left mr-1" style={{ width: 270 }}>
                        <div className="input-group-prepend background-ses">
                            <span className="input-group-text pickericon">
                                <span className="fas fa-gears"></span>
                            </span>
                        </div>
                        <select id="typeSystem" defaultValue={1} className="custom-select block custom-select-sm" onChange={() => getListReceiver()} >
                            <option value={1}>LOAD</option>
                            <option value={2}>SOLAR</option>
                            <option value={3}>WIND</option>
                            <option value={4}>BATTERY</option>
                            <option value={5}>GRID</option>
                        </select>
                    </div>
                    <div className="search-buttons float-left">
                        <button systemtype="button" className="btn btn-outline-secondary btn-sm mr-1 border" onClick={() => getListReceiver()}>
                            <i className="fa-solid fa-search" style={{ color: "#F37021" }} />
                        </button>
                    </div> */}
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
                <div id="main-content">
                    <Toast ref={toast} />
                    <table className="table tbl-power mt-1">
                        <thead style={{ display: "table", width: "100%", tableLayout: "fixed", height: "30px" }}>
                            <tr>
                                <th width="40px">{t('content.no')}</th>
                                <th width="150px">{t('content.home_page.receiver_warning.name')}</th>
                                <th width="150px">{t('content.home_page.receiver_warning.phone_number')}</th>
                                <th>Email</th>
                                <th>{t('content.description')}</th>
                                <th>{t('content.update_date')}</th>
                                <th width="150px"><i className="fa-regular fa-hand"></i></th>
                            </tr>
                        </thead>
                        <tbody style={{ display: "table", maxHeight: "115px", overflow: "auto" }}>
                            {
                                receivers?.length > 0 ? receivers?.map((item, index) =>
                                    <tr key={index} style={{ display: "table", width: "100%", tableLayout: "fixed", height: "25px" }}>
                                        <td width={'40px'} className='text-center'>{index + 1}</td>
                                        <td width="150px">{item.name}</td>
                                        <td width="150px">{item.phone}</td>
                                        <td >{item.email}</td>
                                        <td >{item.description}</td>
                                        <td >{moment(item.updateDate).format(CONS.DATE_FORMAT)}</td>
                                        <td width="150px" className="text-center">
                                            <a className="button-icon text-left" data-toggle="modal" data-target={"#update-receiver-" + item.receiverId} onClick={() => setReceiver(item)}>
                                                <i className="fas fa-edit" style={{ color: "#F37021" }}></i>
                                            </a>
                                            <div className="modal fade" id={"update-receiver-" + item.receiverId} tabIndex="-1" role="dialog" aria-labelledby="exampleModalLabel"
                                                aria-hidden="true">
                                                <div className="modal-dialog modal-lg" role="document">
                                                    <div className="modal-content">
                                                        <form onSubmit={formikUpdate.handleSubmit}>
                                                            <div className="modal-header" style={{ backgroundColor: "#052274", height: "44px" }}>
                                                                <h5 className="modal-title" id="myModalLabel" style={{ color: "white", fontSize: "15px", fontWeight: "bold" }}>{t('content.home_page.receiver_warning.edit_receiver')}</h5>
                                                            </div>
                                                            <div className="modal-body">
                                                                {
                                                                    ((formikUpdate.errors.name && formikUpdate.touched.name) || (formikUpdate.errors.phone && formikUpdate.touched.phone) || (formikUpdate.errors.email && formikUpdate.touched.email)) &&
                                                                    <div className="alert alert-warning" role="alert">
                                                                        <p className="m-0 p-0">{formikUpdate.errors.name}</p>
                                                                        <p className="m-0 p-0">{formikUpdate.errors.phone}</p>
                                                                        <p className="m-0 p-0">{formikUpdate.errors.email}</p>
                                                                    </div>
                                                                }
                                                                <div id="main-content">
                                                                    <table className="table table-input">
                                                                        <tbody>
                                                                            <tr>
                                                                                <th width="150px">{t('content.home_page.receiver_warning.name')}<span className="required">※</span></th>
                                                                                <td>
                                                                                    <input systemtype="text" hidden className="form-control" name="receiverId" defaultValue={receiver.receiverId} disabled />
                                                                                    <input systemtype="text" className="form-control" name="name" maxLength={100} defaultValue={receiver.name} disabled />
                                                                                </td>
                                                                            </tr>
                                                                            <tr>
                                                                                <th width="150px">{t('content.home_page.receiver_warning.phone_number')}<span className="required">※</span></th>
                                                                                <td>
                                                                                    <input systemtype="text" pattern="[0-9]{0,}" className="form-control" name="phone" defaultValue={receiver.phone} onChange={formikUpdate.handleChange} />
                                                                                </td>
                                                                            </tr>
                                                                            <tr>
                                                                                <th width="150px">Email<span className="required">※</span></th>
                                                                                <td>
                                                                                    <input systemtype="text" className="form-control" name="email" maxLength={255} defaultValue={receiver.email} onChange={formikUpdate.handleChange} />
                                                                                </td>
                                                                            </tr>
                                                                            <tr>
                                                                                <th width="150px">{t('content.description')}</th>
                                                                                <td>
                                                                                    <input systemtype="text" className="form-control" name="description" maxLength={1000} defaultValue={receiver.description} onChange={formikUpdate.handleChange} />
                                                                                </td>
                                                                            </tr>
                                                                        </tbody>
                                                                    </table>
                                                                </div>
                                                            </div>
                                                            <div className="modal-footer">
                                                                <button systemtype="button" className="btn btn-outline-primary" data-dismiss="modal">{t('content.close')}</button>
                                                                <button systemtype="submit" className="btn btn-primary">{t('content.save')}</button>
                                                            </div>
                                                        </form>
                                                    </div>
                                                </div>
                                            </div>
                                            <a className="button-icon text-left ml-2" data-toggle="modal" data-target={`#my-modal-` + (index + 1)}>
                                                <i className="fas fa-trash" style={{ color: "#FF0000" }}></i>
                                            </a>
                                            <div id={`my-modal-` + (index + 1)} className="modal fade" tabIndex="-1" role="dialog" aria-hidden="true">
                                                <div className="modal-dialog" role="document">
                                                    <div className="modal-content border-0">
                                                        <div className="modal-body p-0">
                                                            <div className="card border-0 p-sm-3 p-2 justify-content-center" style={{ boxShadow: "0 1px 3px rgba(0, 0, 0, 0.3)" }}>
                                                                <div className="card-header pb-0 bg-white border-0 "><div className="row"><div className="col ml-auto"><button systemtype="button" className="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true">&times;</span> </button></div> </div>
                                                                    <p className="font-weight-bold mb-2"> Bạn có chắc chắn muốn xóa người nhận này ?</p>
                                                                </div>
                                                                <div className="card-body px-sm-4 mb-2 pt-1 pb-0">
                                                                    <div className="row justify-content-end no-gutters"><div className="col-auto">
                                                                        <button systemtype="button" className="btn btn-light text-muted mr-2" data-dismiss="modal">No</button>
                                                                    </div>
                                                                        <div className="col-auto">
                                                                            <button systemtype="button" className="btn btn-danger px-4" data-dismiss="modal" onClick={() => accept(item.receiverId)}>Yes</button>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>

                                            <a className="button-icon text-left ml-2" data-toggle="modal" data-target={"#model-" + (index + 1)} onClick={() => getListDeviceTypeMst(projectId, item.receiverId)}>
                                                <i className="fas fa-wrench" style={{ color: "#0a1a5c" }}></i>
                                            </a>
                                            <div className="modal fade" id={"model-" + (index + 1)} tabIndex="-1" role="dialog" aria-labelledby="exampleModalLabel"
                                                aria-hidden="true">
                                                <div className="modal-dialog modal-lg" role="document">
                                                    <div className="modal-content">
                                                        <div className="modal-header" style={{ backgroundColor: "#052274", height: "44px" }}>
                                                            <h5 className="modal-title" id="myModalLabel" style={{ color: "white", fontSize: "15px", fontWeight: "bold" }}></h5>
                                                        </div>
                                                        <div className="modal-body">
                                                            <table className="table text-center tbl-overview table-oper-info-tool">
                                                                <thead>
                                                                    <tr>
                                                                        <th>{t('content.home_page.receiver_warning.name')}</th>
                                                                        <td>{item.name}</td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th>{t('content.home_page.receiver_warning.phone_number')}</th>
                                                                        <td>{item.phone}</td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th>Email</th>
                                                                        <td>{item.email}</td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th>{t('content.description')}</th>
                                                                        <td>{item.description}</td>
                                                                    </tr>
                                                                </thead>
                                                            </table>
                                                            <div className="form-group mt-2 mb-0 ml-2 mr-2">
                                                                <div style={{ display: "grid", gridTemplateColumns: "50% 50%" }}>
                                                                    {
                                                                        selectedInputDevice &&
                                                                        <>

                                                                            <div className="input-group-prepend p-1">
                                                                                <span className="input-group-text p-0" style={{ lineHeight: 1 }}>
                                                                                    <span className="fas fa-list-check"></span>
                                                                                    {t('content.device_type')}
                                                                                </span>
                                                                                <select id="cbo-device-type-id" className="custom-select block custom-select-sm" onChange={(e) => {
                                                                                    getListDevice(item.receiverId, e.target.value)
                                                                                    }}>
                                                                                    {deviceTypes?.map((pro, index) => {
                                                                                        return <option key={index} value={pro.id}>{pro.name}</option>
                                                                                    })}
                                                                                </select>
                                                                            </div>

                                                                            <div className="input-group-prepend p-1">
                                                                                <span className="input-group-text pl-1 pr-1" style={{ lineHeight: 1 }}>
                                                                                    <span className="fas fa-list-check"></span>
                                                                                    {t('content.device')}
                                                                                </span>
                                                                                <select id="cbo-device-id" name="deviceId" className="custom-select block custom-select-sm " onChange={(e) => onLoadDevice(e.target.value, item.receiverId, devices)}>
                                                                                    {
                                                                                        devices?.length > 0 &&
                                                                                        <option value="0">All</option>
                                                                                    }
                                                                                    {
                                                                                        devices?.map((item, index) => (
                                                                                            <option value={item.deviceId} key={index}>{item.deviceName}</option>
                                                                                        ))
                                                                                    }
                                                                                </select>
                                                                            </div>

                                                                        </>

                                                                    }
                                                                </div>
                                                                <div style={{ position: "relative", zIndex: "99", border: "none" }}>
                                                                    {
                                                                        deviceSearch?.map((m, index) => {
                                                                            return <div className="autocomplete" key={index} style={{ border: "none" }}>
                                                                                <div className="form-control hover-device" style={{ border: "none" }} onClick={() => changeDevice(m.deviceId, m.deviceName, item.receiverId)}><i className="fas fa-server pr-3 pl-1"></i>{m.deviceName}</div>
                                                                            </div>
                                                                        })
                                                                    }
                                                                </div>
                                                            </div>
                                                            <div className="flex align-items-center mt-3 mb-2" style={{ margin: "0 10px", textAlign: "left" }}>
                                                                <Checkbox inputId={"check-all"} onChange={checkAlls} checked={checkAll} />
                                                                <label htmlFor={"check-all"} className="ml-2">{t('content.home_page.manufacture.select_all')}</label>
                                                            </div>
                                                            <div className='list-warning' style={{ display: "flex", flexWrap: "wrap", margin: "0 10px", textAlign: "left" }}>
                                                                {
                                                                    deviceTypeId == 1 &&
                                                                    <>
                                                                        {warningMeter.map(infor => {
                                                                            return (
                                                                                <div key={infor.warningType} className="flex align-items-center mt-3 w-50" >
                                                                                    <Checkbox inputId={infor.warningType} name="infor" value={infor} onChange={onWarningChange} checked={warningSelected.some((element) => element.warningType === infor.warningType)} />
                                                                                    <label htmlFor={infor.warningType} className="ml-2">{infor.warningName}</label>
                                                                                </div>
                                                                            )
                                                                        })}
                                                                    </>
                                                                }
                                                                {
                                                                    deviceTypeId == 2 &&
                                                                    <>
                                                                        {warningInverter.map(infor => {
                                                                            return (
                                                                                <div key={infor.warningType} className="flex align-items-center mt-3 w-50" >
                                                                                    <Checkbox inputId={infor.warningType} name="infor" value={infor} onChange={onWarningChange} checked={warningSelected.some((element) => element.warningType === infor.warningType)} />
                                                                                    <label htmlFor={infor.warningType} className="ml-2">{infor.warningName}</label>
                                                                                </div>
                                                                            )
                                                                        })}
                                                                    </>
                                                                }
                                                                {
                                                                    deviceTypeId == 3 &&
                                                                    <>
                                                                        {warningTempHumidity.map(infor => {
                                                                            return (
                                                                                <div key={infor.warningType} className="flex align-items-center mt-3 w-50" >
                                                                                    <Checkbox inputId={infor.warningType} name="infor" value={infor} onChange={onWarningChange} checked={warningSelected.some((element) => element.warningType === infor.warningType)} />
                                                                                    <label htmlFor={infor.warningType} className="ml-2">{infor.warningName}</label>
                                                                                </div>
                                                                            )
                                                                        })}
                                                                    </>
                                                                }
                                                                {
                                                                    deviceTypeId == 4 &&
                                                                    <>
                                                                        {warningStatus.map(infor => {
                                                                            return (
                                                                                <div key={infor.warningType} className="flex align-items-center mt-3 w-50" >
                                                                                    <Checkbox inputId={infor.warningType} name="infor" value={infor} onChange={onWarningChange} checked={warningSelected.some((element) => element.warningType === infor.warningType)} />
                                                                                    <label htmlFor={infor.warningType} className="ml-2">{infor.warningName}</label>
                                                                                </div>
                                                                            )
                                                                        })}
                                                                    </>
                                                                }
                                                                {
                                                                    deviceTypeId == 5 &&
                                                                    <>
                                                                        {warningHTR02.map(infor => {
                                                                            return (
                                                                                <div key={infor.warningType} className="flex align-items-center mt-3 w-50" >
                                                                                    <Checkbox inputId={infor.warningType} name="infor" value={infor} onChange={onWarningChange} checked={warningSelected.some((element) => element.warningType === infor.warningType)} />
                                                                                    <label htmlFor={infor.warningType} className="ml-2">{infor.warningName}</label>
                                                                                </div>
                                                                            )
                                                                        })}
                                                                    </>
                                                                }
                                                                {
                                                                    deviceTypeId == 6 &&
                                                                    <>
                                                                        {warningAMS01.map(infor => {
                                                                            return (
                                                                                <div key={infor.warningType} className="flex align-items-center mt-3 w-50" >
                                                                                    <Checkbox inputId={infor.warningType} name="infor" value={infor} onChange={onWarningChange} checked={warningSelected.some((element) => element.warningType === infor.warningType)} />
                                                                                    <label htmlFor={infor.warningType} className="ml-2">{infor.warningName}</label>
                                                                                </div>
                                                                            )
                                                                        })}
                                                                    </>
                                                                }
                                                            </div>
                                                        </div>
                                                        <div className="modal-footer">
                                                            <button systemtype="button" className="btn btn-outline-primary" data-dismiss="modal">{t('content.close')}</button>
                                                            <button className="btn btn-primary" onClick={() => save(item.receiverId)}>{t('content.save')}</button>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </td>
                                    </tr>
                                )
                                    :
                                    <tr style={{ display: "table", width: "100%", tableLayout: "fixed" }}>
                                        <td className="text-center"> Không có dữ liệu</td>
                                    </tr>
                            }
                        </tbody>
                    </table>
                    <div className="modal fade" id="new-receiver" tabIndex="-1" role="dialog" aria-labelledby="exampleModalLabel"
                        aria-hidden="true">
                        <div className="modal-dialog modal-lg" role="document">
                            <div className="modal-content">
                                <form onSubmit={formik.handleSubmit}>
                                    <div className="modal-header" style={{ backgroundColor: "#052274", height: "44px" }}>
                                        <h5 className="modal-title" id="myModalLabel" style={{ color: "white", fontSize: "15px", fontWeight: "bold" }}>{t('content.home_page.receiver_warning.add_receiver')}</h5>
                                    </div>
                                    <div className="modal-body">
                                        {
                                            ((formik.errors.name && formik.touched.name) || (formik.errors.phone && formik.touched.phone) || (formik.errors.email && formik.touched.email)) &&
                                            <div className="alert alert-warning" role="alert">
                                                <p className="m-0 p-0">{formik.errors.name}</p>
                                                <p className="m-0 p-0">{formik.errors.phone}</p>
                                                <p className="m-0 p-0">{formik.errors.email}</p>
                                            </div>
                                        }
                                        <div id="main-content">
                                            <table className="table table-input">
                                                <tbody>
                                                    <tr>
                                                        <th width="150px">{t('content.home_page.receiver_warning.name')}<span className="required">※</span></th>
                                                        <td>
                                                            <input systemtype="text" className="form-control" name="name" maxLength={100} onChange={formik.handleChange} />
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <th width="150px">{t('content.home_page.receiver_warning.phone_number')}<span className="required">※</span></th>
                                                        <td>
                                                            <input systemtype="text" pattern="[0-9]{0,}" className="form-control" id="phone" name="phone" maxLength={255} onChange={formik.handleChange} />
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <th width="150px">Email<span className="required">※</span></th>
                                                        <td>
                                                            <input systemtype="text" className="form-control" id="email" name="email" maxLength={255} onChange={formik.handleChange} />
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <th width="150px">{t('content.description')}</th>
                                                        <td>
                                                            <input systemtype="text" className="form-control" id="description" name="description" maxLength={1000} onChange={formik.handleChange} />
                                                        </td>
                                                    </tr>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                    <div className="modal-footer">
                                        <button systemtype="button" className="btn btn-outline-primary" data-dismiss="modal">{t('content.close')}</button>
                                        <button systemtype="submit" className="btn btn-primary">{t('content.save')}</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default ReceiverWarning;