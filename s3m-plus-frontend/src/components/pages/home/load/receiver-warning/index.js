import React from 'react';
import { useState, useRef } from "react";
import { useEffect } from "react";
import { useParams } from "react-router";
import receiverService from '../../../../../services/ReceiverService';
import DeviceService from '../../../../../services/DeviceService';
import $ from "jquery";
import * as Yup from "yup";
import { Checkbox } from 'primereact/checkbox';
import { useHistory } from "react-router-dom";
import { useFormik } from 'formik';
import CONS from '../../../../../constants/constant';
import moment from 'moment';
import { Toast } from 'primereact/toast';


const ReceiverWarning = ({ projectInfo, projectId }) => {
    const [visible, setVisible] = useState(false);
    const toast = useRef(null);

    const history = useHistory();
    const param = useParams();
    const [receiver, setReceiver] = useState({
        receiverId: "",
        name: "",
        phone: "",
        email: "",
        description: ""
    });
    const [receivers, setReceivers] = useState([]);
    const [devices, setDevices] = useState();
    const [devicesSelected, setDevicesSelected] = useState([]);
    const [deviceSearch, setDeviceSearch] = useState([]);
    const [checkAll, setCheckAll] = useState(false);
    const [selectedInputDevice, setSelectedInputDevice] = useState(true);
    const [deviceId, setDeviceId] = useState(0);
    const [warning, setWarning] = useState([
        {
            warningType: CONS.WARNING_TYPE.NGUONG_AP_CAO,
            warningName: 'Điện áp cao',

        },
        {
            warningType: CONS.WARNING_TYPE.NGUONG_AP_THAP,
            warningName: 'Điện áp thấp',

        },
        {
            warningType: CONS.WARNING_TYPE.NHIET_DO_TIEP_XUC,
            warningName: 'Nhiệt độ tiếp xúc',

        },
        {
            warningType: CONS.WARNING_TYPE.LECH_PHA,
            warningName: 'Lệch pha',
        },
        {
            warningType: CONS.WARNING_TYPE.QUA_TAI,
            warningName: 'Quá tải',

        },
        {
            warningType: CONS.WARNING_TYPE.TAN_SO_THAP,
            warningName: 'Tần số thấp',

        },
        {
            warningType: CONS.WARNING_TYPE.TAN_SO_CAO,
            warningName: 'Tần số cao',

        },
        {
            warningType: CONS.WARNING_TYPE.MAT_NGUON_PHA,
            warningName: 'Mất nguồn',

        },
        {
            warningType: CONS.WARNING_TYPE.NGUONG_TONG_HAI,
            warningName: 'Sóng hài',

        },
        {
            warningType: CONS.WARNING_TYPE.DONG_TRUNG_TINH,
            warningName: 'Dòng trung tính',

        },
        {
            warningType: CONS.WARNING_TYPE.DONG_MO_CUA,
            warningName: 'Đóng mở cửa',

        },
        {
            warningType: CONS.WARNING_TYPE.COS_THAP_TONG,
            warningName: 'Hệ số công suất thấp',

        }
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
            let res = await receiverService.addNewReceiverLoad(data, projectId, CONS.SYSTEM_TYPE.LOAD);
            if (res.status === 200) {
                $('#new-receiver').hide();
                getResetReceiver();
                getListReceiver();
                toast.current.show({ severity: 'success', summary: 'Success', detail: 'Thêm người nhận thành công', life: 3000 });
            } else {
                $('#new-receiver').show();
                toast.current.show({ severity: 'error', summary: 'Error', detail: 'Lỗi khi thêm người nhận', life: 3000 });
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
            let res = await receiverService.updateReceiverLoad(data);
            if (res.status === 200) {
                $('#update-receiver-' + data.receiverId).hide();
                getListReceiver();
                toast.current.show({ severity: 'success', summary: 'Success', detail: 'Cập nhật thông tin thành công', life: 3000 });
            } else {
                $('#update-receiver-' + data.receiverId).show();
                toast.current.show({ severity: 'error', summary: 'Error', detail: 'Cập nhật thông tin thất bại', life: 3000 });
            }
        }
    });

    const [warningSelected, setWarningSelected] = useState([]);

    const getListDevice = async (receiverId) => {
        let res = await DeviceService.listDevice(projectId, 1);
        if (res.status === 200) {
            setDevices(res.data)
        }
        setDeviceId(res.data[0].deviceId);
        onLoadDevice(res.data[0].deviceId, receiverId);
    };

    const getListReceiver = async () => {
        let res = await receiverService.listReceiverLoad(projectId, CONS.SYSTEM_TYPE.LOAD);
        if (res.status === 200) {
            setReceivers(res.data);
        }
    };

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

    const onWarningChange = (e) => {

        let _warning = [...warningSelected];
        if (e.checked)
            _warning.push(e.value);
        else {
            _warning = _warning?.filter(item => item.warningType !== e.value.warningType);
        }
        setWarningSelected(_warning);
    };

    const changeSearch = () => {
        setSelectedInputDevice(!selectedInputDevice);
        setDeviceSearch([]);
    }

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

    const onLoadDevice = async (_deviceId, receiverId) => {
        setDeviceId(_deviceId);

        let res = await receiverService.getWarningInforLoad(receiverId, _deviceId);
        let _warningRes = [];
        warning?.forEach(e => {
            if (res.data?.some(item => item == e.warningType)) {
                _warningRes.push(e);
            }
        })
        setWarningSelected(_warningRes);
        if (_warningRes.length < warning.length) {
            setCheckAll(false);
        } else {
            setCheckAll(true);
        }

        getListReceiver();
    };

    const save = async (receiverId) => {
        let res = await receiverService.saveWarningInforLoad(warningSelected.map(item => item.warningType), CONS.SYSTEM_TYPE.LOAD, param.customerId, param.projectId, deviceId, receiverId);
        if (res.status === 200) {
            toast.current.show({ severity: 'success', summary: 'Success', detail: 'Thêm thông tin thành công', life: 3000 });
        }
        else {
            toast.current.show({ severity: 'error', summary: 'Error', detail: 'Thêm thông tin thất bại', life: 3000 });
        }
    };

    const accept = async (receiverId) => {
        let res = await receiverService.deleteReceiverLoad(receiverId);
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
            setWarningSelected(warning);
            setCheckAll(true);
        }
        else {
            setWarningSelected([]);
            setCheckAll(false);
        }

    };

    useEffect(() => {
        document.title = "Danh sách nhận cảnh báo PV";
        getListReceiver();
    }, [projectId])

    return (
        <div className='tab-content'>

            <div className="project-infor" style={{ padding: "0px 10px", display: "block", marginTop: "10px" }}>
                <span className="project-tree">{projectInfo}</span>
            </div>

            <Toast ref={toast} />

            <table className="table tbl-overview tbl-power mt-3">
                <thead style={{ display: "table", width: "100%", tableLayout: "fixed" }}>
                    <tr>
                        <th width="40px">TT</th>
                        <th width="150px">Người nhận</th>
                        <th width="150px">Số điện thoại</th>
                        <th>Email</th>
                        <th>Mô tả</th>
                        <th>Thời gian cập nhật</th>
                        <th width="70px"><i className="fa-regular fa-hand"></i></th>
                    </tr>
                </thead>
                <tbody style={{ display: "table", maxHeight: "115px", overflow: "auto" }}>
                    {
                        receivers?.length > 0 ? receivers?.map((item, index) =>
                            <tr key={index} style={{ display: "table", width: "100%", tableLayout: "fixed" }}>
                                <td width={'40px'} className='text-center'>{index + 1}</td>
                                <td width="150px">{item.name}</td>
                                <td width="150px">{item.phone}</td>
                                <td >{item.email}</td>
                                <td >{item.description}</td>
                                <td >{moment(item.updateDate).format(CONS.DATE_FORMAT)}</td>
                                <td width="70px">
                                    <a className="button-icon text-left" data-toggle="modal" data-target={"#update-receiver-" + item.receiverId} onClick={() => setReceiver(item)}>
                                        <img height="16px" src="/resources/image/icon-edit.png" alt="Chỉnh sửa" />
                                    </a>
                                    <div className="modal fade" id={"update-receiver-" + item.receiverId} tabIndex="-1" role="dialog" aria-labelledby="exampleModalLabel"
                                        aria-hidden="true">
                                        <div className="modal-dialog modal-lg" role="document">
                                            <div className="modal-content">
                                                <form onSubmit={formikUpdate.handleSubmit}>
                                                    <div className="modal-header" style={{ backgroundColor: "#052274", height: "44px" }}>
                                                        <h5 className="modal-title" id="myModalLabel" style={{ color: "white", fontSize: "15px", fontWeight: "bold" }}>Chỉnh sửa thông tin người nhận</h5>
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
                                                                        <th width="150px">Người nhận<span className="required">※</span></th>
                                                                        <td>
                                                                            <input type="text" hidden className="form-control" name="receiverId" defaultValue={receiver.receiverId} disabled />
                                                                            <input type="text" className="form-control" name="name" maxLength={100} defaultValue={receiver.name} disabled />
                                                                        </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th width="150px">Số điện thoại<span className="required">※</span></th>
                                                                        <td>
                                                                            <input type="text" pattern="[0-9]{0,}" className="form-control" name="phone" defaultValue={receiver.phone} onChange={formikUpdate.handleChange} />
                                                                        </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th width="150px">Email<span className="required">※</span></th>
                                                                        <td>
                                                                            <input type="text" className="form-control" name="email" maxLength={255} defaultValue={receiver.email} onChange={formikUpdate.handleChange} />
                                                                        </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th width="150px">Mô tả</th>
                                                                        <td>
                                                                            <input type="text" className="form-control" name="description" maxLength={1000} defaultValue={receiver.description} onChange={formikUpdate.handleChange} />
                                                                        </td>
                                                                    </tr>
                                                                </tbody>
                                                            </table>
                                                        </div>
                                                    </div>
                                                    <div className="modal-footer">
                                                        <button type="button" className="btn btn-outline-primary" onClick={() => $('#update-receiver-' + item.receiverId).hide()}>Đóng</button>
                                                        <button type="submit" className="btn btn-primary">Lưu</button>
                                                    </div>
                                                </form>
                                            </div>
                                        </div>
                                    </div>
                                    <a className="button-icon text-left ml-1" data-toggle="modal" data-target={`#my-modal-` + (index + 1)}>
                                        <img height="16px" src="/resources/image/icon-delete.png" alt="Xóa" />
                                    </a>
                                    <div id={`my-modal-` + (index + 1)} class="modal fade" tabindex="-1" role="dialog" aria-hidden="true">
                                        <div class="modal-dialog" role="document">
                                            <div class="modal-content border-0">
                                                <div class="modal-body p-0">
                                                    <div class="card border-0 p-sm-3 p-2 justify-content-center" style={{ boxShadow: "0 1px 3px rgba(0, 0, 0, 0.3)" }}>
                                                        <div class="card-header pb-0 bg-white border-0 "><div class="row"><div class="col ml-auto"><button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true">&times;</span> </button></div> </div>
                                                            <p class="font-weight-bold mb-2"> Bạn có chắc chắn muốn xóa người nhận này ?</p>
                                                        </div>
                                                        <div class="card-body px-sm-4 mb-2 pt-1 pb-0">
                                                            <div class="row justify-content-end no-gutters"><div class="col-auto">
                                                                <button type="button" class="btn btn-light text-muted mr-2" data-dismiss="modal">No</button>
                                                            </div>
                                                                <div class="col-auto">
                                                                    <button type="button" class="btn btn-danger px-4" data-dismiss="modal" onClick={() => accept(item.receiverId)}>Yes</button>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <a className="button-icon text-left ml-1" data-toggle="modal" data-target={"#model-" + (index + 1)} onClick={() => getListDevice(item.receiverId)}>
                                        <i className="fa fa-wrench"></i>
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
                                                                <th>Người nhận</th>
                                                                <td>{item.name}</td>
                                                            </tr>
                                                            <tr>
                                                                <th>Số điện thoại</th>
                                                                <td>{item.phone}</td>
                                                            </tr>
                                                            <tr>
                                                                <th>Email</th>
                                                                <td>{item.email}</td>
                                                            </tr>
                                                            <tr>
                                                                <th>Mô tả</th>
                                                                <td>{item.description}</td>
                                                            </tr>
                                                        </thead>
                                                    </table>
                                                    <div className="form-group mt-2 mb-0 ml-2 mr-2">
                                                        <div className="input-group mr-1">
                                                            {
                                                                selectedInputDevice ?
                                                                    <>
                                                                        <div className="input-group-prepend" onClick={() => changeSearch()}>
                                                                            <span className="input-group-text pickericon">
                                                                                <span className="fas fa-list-check"></span>
                                                                            </span>
                                                                        </div>
                                                                        <select id="cbo-device-id" name="deviceId" value={param.deviceId} className="custom-select block custom-select-sm lable-device-info" onChange={(e) => onLoadDevice(e.target.value, item.receiverId)}>
                                                                            {
                                                                                devices?.map((item, index) => (
                                                                                    <option value={item.deviceId} key={index}>{item.deviceName}</option>
                                                                                ))
                                                                            }
                                                                        </select>
                                                                    </>
                                                                    :
                                                                    <>
                                                                        <div className="input-group-prepend" onClick={() => changeSearch()}>
                                                                            <span className="input-group-text pickericon">
                                                                                <span className="fas fa-magnifying-glass"></span>
                                                                            </span>
                                                                        </div>
                                                                        <input type="text" id="keyword" className="form-control lable-device-info" aria-label="Tìm kiếm" aria-describedby="inputGroup-sizing-sm" placeholder="Nhập tên thiết bị" onChange={() => searchDevice()} />
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
                                                    <div className="flex align-items-center mt-3 w-50 mb-2" style={{ margin: "0 10px" }}>
                                                        <Checkbox inputId={"check-all"} onChange={checkAlls} checked={checkAll} />
                                                        <label htmlFor={"check-all"} className="ml-2">Chọn tất cả</label>
                                                    </div>
                                                    <div className='list-warning' style={{ display: "flex", flexWrap: "wrap", margin: "0 10px" }}>
                                                        {warning.map(infor => {
                                                            return (
                                                                <div key={infor.warningType} className="flex align-items-center mt-3 w-50" >
                                                                    <Checkbox inputId={infor.warningType} name="infor" value={infor} onChange={onWarningChange} checked={warningSelected.some((element) => element.warningType === infor.warningType)} />
                                                                    <label htmlFor={infor.warningType} className="ml-2">{infor.warningName}</label>
                                                                </div>
                                                            )
                                                        })}
                                                    </div>
                                                </div>
                                                <div className="modal-footer">
                                                    <button type="button" className="btn btn-outline-primary" onClick={() => $('#model-' + (index + 1)).hide()}>Đóng</button>
                                                    <button className="btn btn-primary" onClick={() => save(item.receiverId)}>Lưu</button>
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
            <button type="button" data-toggle="modal" data-target={"#new-receiver"} className="btn btn-s3m w-150px float-right" style={{ background: "rgb(255, 168, 125)", border: "none", padding: "0px", height: "35px", fontSize: "13px", margin: "10px 10px" }}>&nbsp;Thêm người nhận</button>
            <div className="modal fade" id="new-receiver" tabIndex="-1" role="dialog" aria-labelledby="exampleModalLabel"
                aria-hidden="true">
                <div className="modal-dialog modal-lg" role="document">
                    <div className="modal-content">
                        <form onSubmit={formik.handleSubmit}>
                            <div className="modal-header" style={{ backgroundColor: "#052274", height: "44px" }}>
                                <h5 className="modal-title" id="myModalLabel" style={{ color: "white", fontSize: "15px", fontWeight: "bold" }}>Thêm người nhận</h5>
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
                                                <th width="150px">Người nhận<span className="required">※</span></th>
                                                <td>
                                                    <input type="text" className="form-control" id="name" name="name" maxLength={100} onChange={formik.handleChange} />
                                                </td>
                                            </tr>
                                            <tr>
                                                <th width="150px">Số điện thoại<span className="required">※</span></th>
                                                <td>
                                                    <input type="text" pattern="[0-9]{0,}" className="form-control" id="phone" name="phone" onChange={formik.handleChange} />
                                                </td>
                                            </tr>
                                            <tr>
                                                <th width="150px">Email<span className="required">※</span></th>
                                                <td>
                                                    <input type="text" className="form-control" id="email" name="email" maxLength={255} onChange={formik.handleChange} />
                                                </td>
                                            </tr>
                                            <tr>
                                                <th width="150px">Mô tả</th>
                                                <td>
                                                    <input type="text" className="form-control" id="description" name="description" maxLength={1000} onChange={formik.handleChange} />
                                                </td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-outline-primary" onClick={() => $('#new-receiver').hide()}>Đóng</button>
                                <button type="submit" className="btn btn-primary">Lưu</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div >
    )
}

export default ReceiverWarning;
