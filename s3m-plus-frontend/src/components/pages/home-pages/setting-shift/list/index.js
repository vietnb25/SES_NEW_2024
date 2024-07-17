import React, { useEffect, useRef, useState } from "react";
import CustomerService from "../../../../../services/CustomerService";
import ProjectService from "../../../../../services/ProjectService";
import $ from "jquery";
import SettingShiftService from "../../../../../services/SettingShiftService";
import CONS from "../../../../../constants/constant";
import moment from "moment";
import { Link, useHistory, useParams } from "react-router-dom";
import { useLocation } from "react-router-dom/cjs/react-router-dom.min";
import authService from "../../../../../services/AuthService";
import { PatternFormat } from 'react-number-format';
import "./index.css"
import EditSettingShift from "../edit";
import { useFormik } from "formik";
import * as Yup from "yup";
import ReactModal from "react-modal";
import DatePicker from 'react-datepicker';
import { ToastContainer } from "react-toastify";
import { NotficationError, NotficationSuscces } from "../../notification/notification";
import { t } from "i18next";

const ListSettingShift = (props) => {
    const toast = useRef(null);
    const param = useParams();
    const [projects, setProjects] = useState([]);
    const [customers, setCustomers] = useState([]);
    const [settings, setSettings] = useState([]);
    const [customerId, setCustomerId] = useState(0);
    const [projectId, setProjectId] = useState(0);
    const [status, setStatus] = useState(null);
    const location = useLocation();
    const history = useHistory();
    const [role, setRole] = useState("");
    const [page, setPage] = useState(1);
    const [edit, setEdit] = useState(false);
    const [idEdit, setIdEdit] = useState();
    const [projectsAdd, setProjectsAdd] = useState([]);
    const [projectIdAdd, setProjectIdAdd] = useState();

    const getRole = () => {
        let roleName = authService.getRoleName();
        setRole(roleName);
    }

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
            setProjectsAdd(res.data)
            if (res.data.length > 0) {
                $('#projectId').prop('disable', false);
                let data = res.data;
                let projectId = res.data[0].projectId;
                setProjectIdAdd(projectId)
                $.each(data, function (index, value) {
                    $('#projectId').append('<option value="' + value.projectId + '">' + value.projectName + '</option>')
                });
            } else {
                $('#projectId').prop('disable', true);
            }
        } else {
            $(".input-project-m").hide();
        }
        listSetting();
    }

    const listSetting = async (projectId) => {

        let res = await SettingShiftService.listSetting(projectId);
        if (res.status === 200 && parseInt(res.data.length) > 0) {
            setSettings(res.data);
        } else {
            setSettings([]);
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

    //VALIDATE
    function validateFormData(data) {
        const fields = ['shiftName', 'startTime', 'endTime'];

        for (const field of fields) {
            if (!data[field]) {
                $.alert({
                    title: 'Thông báo!',
                    content: `Xin vui lòng điền đầy đủ thông tin cho ${field}!`,
                });
                return false; // Thoát khỏi hàm nếu trường không hợp lệ
            }

            if (typeof data[field] === 'string' && data[field].trim() === "") {
                $.alert({
                    title: 'Thông báo!',
                    content: `Xin vui lòng điền đầy đủ thông tin cho ${field}!`,
                });
                return false; // Thoát khỏi hàm nếu trường là chuỗi trống
            }
        }

        return true;
    }

    // ADD SETTING SHIFT
    const initialValues = {
        shiftName: "",
        startTime: "",
        endTime: "",
    };

    const formik = useFormik({
        initialValues,

        onSubmit: async (data) => {
            if (validateFormData(data)) {
                let res = await SettingShiftService.addSettingShift(data, projectId);
                if (res.status === 200) {
                    $('#new-shift-setting').hide();
                    getResetSettingShift();
                    listSetting(projectId);
                    NotficationSuscces("Thêm thành công!")
                } else {
                    $('#new-shift-setting').show();
                    NotficationError("Thêm thất bại!")
                }
            }
        }
    });

    const getResetSettingShift = () => {
        $('#shiftName').val("");
        $('#startTime').val("");
        $('#endTime').val("");
        formik.setFieldValue("shiftName", "");
        formik.setFieldValue("startTime", "");
        formik.setFieldValue("endTime", "");
    }

    const handleDeleteSettingShift = async (id) => {
        $.confirm({
            type: 'red',
            typeAnimated: true,
            icon: 'fa fa-warning',
            title: 'Xác nhận!',
            content: 'Bạn có chắc chắn muốn xóa?',
            buttons: {
                confirm: {
                    text: 'Đồng ý',
                    action: async () => {
                        let res = await SettingShiftService.deleteSetting(id);

                        if (res.status === 200) {
                            $.alert({
                                title: 'Thông báo',
                                content: 'Đã xóa thành công!'
                            });

                        } else {
                            $.alert({
                                type: 'red',
                                title: 'Thông báo',
                                content: 'Khóa thất bại'
                            });
                        }
                        listSetting(projectId);
                    }
                },
                cancel: {
                    text: 'Hủy bỏ'
                }
            }
        })


    };
    const params = useParams();
    const handleInputChange = (e) => {
        const { name, value } = e.target;

        setShiftSetting((prevData) => ({
            ...prevData,
            [name]: value,
            [e.target.name]: e.target.value
        }));
    };

    const handleRadioChange = (e) => {
        const { name, value } = e.target;
        setShiftSetting((prevData) => ({
            ...prevData,
            [name]: parseInt(value),
        }));
    };

    const [shiftSetting, setShiftSetting] = useState([]);


    //UPDATE
    const getSettingShiftById = async (id) => {
        console.log(id);
        let res = await SettingShiftService.getSettingShiftByIds(id)
        if (res.status === 200) {
            setShiftSetting(() => res.data)
            setIsModalOpen(true);
        };
    }

    const handleUpdateSettingShift = async () => {
        let response = await SettingShiftService.updateSetting(shiftSetting);

        if (response.status === 200) {
            $.alert({
                title: 'Thông báo',
                content: 'Đã chỉnh sửa thành công'
            });
            closeModal()

            listSetting(projectId)
        } else {
        }
    }
    const [isModalOpen, setIsModalOpen] = useState(false);

    const openModal = () => {
        setIsModalOpen(true);
    };

    const closeModal = () => {
        setIsModalOpen(false);
    };
    const getProject = async (id) => {
        let res = await ProjectService.getProject(id);
        setProjects([res.data]);

        setProjectId(id)
        listSetting(id)
    }
    const getListProject = async () => {
        let res = await ProjectService.getProjectByCustomerId(param.customerId);
        if (res.status == 200) {
            if (res.data.length > 0) {
                setProjects(res.data)
                setProjectId(res.data[0].projectId)
                listSetting(res.data[0].projectId)
            }
        }

    }

    useEffect(() => {
        document.title = t('content.home_page.setting.shift_tab');
        if (location.state) {
            setNotification(location.state);
        };
        if (param.projectId != undefined) {
            getProject(param.projectId)
            setProjectId(param.projectId)
        } else {
            getListProject();
        }
    }, [param.customerId, param.projectId]);

    const [query, setQuery] = useState("");
    function removeDiacritics(text) {
        return text.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
    }
    const filteredData = settings.filter((item) =>
        (item.shiftName !== null && removeDiacritics(item.shiftName.toString().toLowerCase()).includes(removeDiacritics(query.toLowerCase())))
    );

    //Debounce cho Search Input
    let timeout;
    const handleSearchChange = (e) => {
        const value = e.target.value;

        if (timeout) {
            clearTimeout(timeout); // Xóa bất kỳ timeout nào còn đang chạy
        }

        timeout = setTimeout(() => {
            setQuery(value);
        }, 400); // Sau 1000ms (1 giây), gọi hàm setQuery
    };

    const renderFormEdit = (id) => {
        return <EditSettingShift id={id} callbackFunction={statusUpdate} />
    }
    const statusUpdate = (child) => {
        if (child == 200) {
            setEdit(!edit);
            listSetting();
        }
    }

    const changeProject = async (projectId) => {
        setProjectId(projectId)
        listSetting(projectId);
    }
    const hoursOptions = [{ label: t('content.home_page.setting.choose_time'), value: '' }];
    for (let hour = 0; hour < 24; hour++) {
        for (let minute = 0; minute < 60; minute += 5) {
            const formattedHour = String(hour).padStart(2, '0');
            const formattedMinute = String(minute).padStart(2, '0');
            const timeValue = `${formattedHour}:${formattedMinute}`;
            hoursOptions.push({ label: timeValue, value: timeValue });
        }
    }
    const hoursOptionsUpdate = [];
    for (let hour = 0; hour < 24; hour++) {
        for (let minute = 0; minute < 60; minute += 5) {
            const formattedHour = String(hour).padStart(2, '0');
            const formattedMinute = String(minute).padStart(2, '0');
            const timeValue = `${formattedHour}:${formattedMinute}`;
            hoursOptionsUpdate.push({ label: timeValue, value: timeValue });
        }
    }
    return (
        <div>
            <ToastContainer />
            {page == 1 &&
                <>
                    {/* <div className="title-up-warning text-left">
                        <div style={{ marginTop: "-21px", color: "white" }}><i className="fa-solid fa-gears ml-1" style={{ color: "#fff" }}></i> CÀI ĐẶT CA LÀM VIỆC
                        </div>fa-regular fa-circle-xmark
                    </div> */}
                    <div id="" style={{ height: 32, width: "100%" }}>
                        <div className="float-right mr-1" data-toggle="modal" data-target={"#new-shift-setting"} >
                            <i className="fas fa-solid fa-circle-plus fa-2x float-right add-user" style={{ width: "30px" }}></i>
                        </div>

                        <div className="input-group float-left input-project-m" style={{ width: "200px", marginLeft: '0.75555%', marginRight: 0 }}>

                            <div className="input-group-prepend background-ses">
                                <span className="input-group-text pickericon">
                                    <span className="fas fa-user-group"></span>
                                </span>
                            </div>
                            <div className='' style={{ width: '70%' }}>
                                <select id={"project"} className='form-select' style={{ width: '100%' }} onChange={(event) => changeProject(event.target.value)} disabled={params.projectId !== undefined} >
                                    {projects?.map((pro, index) => {
                                        return <option key={index} value={pro.projectId}>{pro.projectName}</option>
                                    })}
                                </select>
                            </div>

                        </div>

                        <div className="float-left">
                            <input type="text" style={{ width: "250px", height: "" }} className="" name="equipment-name" placeholder={t('content.home_page.search')}
                                onChange={handleSearchChange} />
                            <i className="fa fa-search position-absolute" style={{ left: "450px", top: "85px", }}></i>
                        </div>

                        {/* <div className="" style={{ width: "50%", height: "" }}>
                            <input type="text" style={{ width: "390px", height: "" }} className="" name="equipment-name" placeholder="   Tìm kiếm...."
                                onChange={handleSearchChange} />
                         
                            <i className="fa fa-search position-absolute" style={{ left: "360px", top: "10px", }}></i>
                        </div> */}



                    </div>

                    {/* TABLE CHÍNH */}
                    <div id="main-content">
                        <table className="table">
                            <thead height="40px">
                                <tr >
                                    <th width="50px">{t('content.no')}</th>
                                    <th width="250px">{t('content.home_page.setting.shift')} </th>
                                    <th width="200px">{t('content.home_page.setting.start_time')} </th>
                                    <th width="200px">{t('content.home_page.setting.end_time')} </th>
                                    <th>{t('content.create_date')}</th>
                                    <th>{t('content.update_date')}</th>
                                    <th>{t('content.status')}</th>
                                    <th width="140px"><i className="fa-regular fa-hand"></i></th>
                                </tr>
                            </thead>
                            <tbody>
                                {
                                    parseInt(settings?.length) > 0 && filteredData?.map((setting, index) => {
                                        const formattedEndTime = setting.endTime.slice(0, 5);
                                        const formattedStartTime = setting.startTime.slice(0, 5);
                                        return (
                                            <tr key={index} height="30px"
                                                onClick={() => getSettingShiftById(setting.id)}>
                                                <td className="text-center">{index + 1}</td>

                                                <td className="text-left" style={{ cursor: "context-menu" }}>
                                                    {setting.shiftName}
                                                </td>

                                                <td className="text-center" style={{ cursor: "context-menu" }}>

                                                    {(formattedStartTime)}
                                                </td>

                                                <td className="text-center" style={{ cursor: "context-menu" }}>
                                                    {formattedEndTime}
                                                </td>

                                                <td className="text-center" style={{ cursor: "context-menu" }}>
                                                    {moment(setting.createDate).format("DD-MM-YYYY HH:mm:ss")}
                                                </td>

                                                <td className="text-center" style={{ cursor: "context-menu" }}>
                                                    {moment(setting.updateDate).format("DD-MM-YYYY HH:mm:ss")}
                                                </td>

                                                <td className="text-center" style={{ cursor: "context-menu" }}>
                                                    {setting.status === 0 ? (
                                                        <i className="fa-solid fa-lock"
                                                            style={{ color: "#f7aa02" }}></i>
                                                    )
                                                        :
                                                        (
                                                            <i className="fa-solid fa-unlock" style={{ color: "#29d67d" }}></i>
                                                        )}
                                                    {/* {setting.status} */}
                                                </td>

                                                <td width="150px" className="text-center">
                                                    <a className="button-icon text-left ml-2" data-toggle="modal" data-target={`#my-modal-` + (index + 1)}>
                                                        <i className="fas fa-trash" style={{ color: "#ff0000" }}
                                                            onClick={(e) => {
                                                                e.stopPropagation()
                                                                handleDeleteSettingShift(setting.id);
                                                            }}></i>
                                                    </a>
                                                </td>
                                            </tr>
                                        )
                                    })
                                }
                                {
                                    parseInt(settings?.length) === 0 &&
                                    <tr height="30px">
                                        <td colSpan={8} className="text-center">Không có dữ liệu</td>
                                    </tr>
                                }
                            </tbody>

                        </table>
                        <ReactModal
                            isOpen={isModalOpen}
                            onRequestClose={() => {
                                setIsModalOpen(false);
                            }}
                            style={{
                                content: {
                                    width: "50%", // Kích thước chiều rộng của modal (có thể điều chỉnh)
                                    height: "40%", // Kích thước chiều cao của modal (có thể điều chỉnh)
                                    margin: "auto", // Căn giữa modal
                                    marginTop: "10px",
                                },
                            }}
                        >
                            <div className="modal-content" >
                                <div className="modal-header" style={{ backgroundColor: "#052274", height: "44px" }}>
                                    <h5 className="modal-title" id="myModalLabel" style={{ color: "white", fontSize: "15px", fontWeight: "bold" }}>{t('content.home_page.setting.edit_shift')}</h5>
                                </div>
                                <div className="modal-body">
                                    <div id="main-content">
                                        <table className="table table-input">
                                            <tbody>
                                                <tr>
                                                    <th width="150px">{t('content.home_page.setting.shift')}<span className="required">※</span></th>
                                                    <td>
                                                        <input type="text" hidden className="form-control" name="id" value={shiftSetting.id} disabled />

                                                        <input type="text" className="form-control" name="shiftName" value={shiftSetting.shiftName}
                                                            onChange={handleInputChange} />
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <th width="150px">{t('content.home_page.setting.start_time')}<span className="required">※</span></th>
                                                    <td>
                                                        <select
                                                            id="startTime"
                                                            name="startTime"
                                                            onChange={handleInputChange}
                                                            value={shiftSetting.startTime}
                                                            style={{ height: "80px", width: "100%" }}
                                                        >
                                                            <option selected hidden>{shiftSetting.startTime}</option>
                                                            {hoursOptionsUpdate.map((option) => (
                                                                <option key={option.value} value={option.value}>
                                                                    {option.label}
                                                                </option>
                                                            ))}
                                                        </select>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <th width="150px">{t('content.home_page.setting.end_time')}<span className="required">※</span></th>
                                                    <td>
                                                        <select

                                                            id="endTime"
                                                            name="endTime"
                                                            onChange={handleInputChange}
                                                            value={shiftSetting.endTime}
                                                            style={{ height: "100px", width: "100%" }}
                                                        >
                                                            <option selected hidden>{shiftSetting.endTime}</option>
                                                            {hoursOptionsUpdate.map((option) => (
                                                                <option key={option.value} value={option.value}>
                                                                    {option.label}
                                                                </option>
                                                            ))}
                                                            {/* Thêm tùy chọn chỉ hiển thị shiftSetting.endTime */}


                                                        </select>
                                                        {/* <input type="text" className="form-control" name="endTime" value={shiftSetting.endTime} onChange={handleInputChange} /> */}
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <th width="150px">{t('content.status')}</th>
                                                    <td>
                                                        <label>
                                                            <input
                                                                type="radio"
                                                                name="status"
                                                                value="0"
                                                                checked={shiftSetting.status === 0 ? "checked" : ""}
                                                                onChange={handleRadioChange}
                                                            /> {t('content.lock')}
                                                        </label>
                                                        <div style={{ backgroundColor: "white", height: "0px" }}></div>
                                                        <label>
                                                            <input
                                                                type="radio"
                                                                name="status"
                                                                value="1"
                                                                checked={shiftSetting.status === 1 ? "checked" : ""}
                                                                onChange={handleRadioChange}
                                                            />  {t('content.unlock')}
                                                        </label>

                                                    </td>
                                                </tr>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                                <div className="modal-footer">
                                    <button type="button" className="btn btn-outline-primary" onClick={closeModal}> {t('content.close')}</button>
                                    <button type="submit" className="btn btn-primary" onClick={handleUpdateSettingShift}> {t('content.save')}</button>
                                </div>

                            </div>
                        </ReactModal>
                        <div className="modal fade" id="new-shift-setting" tabIndex="-1" role="dialog" aria-labelledby="exampleModalLabel"
                            aria-hidden="true">
                            <div className="modal-dialog modal-lg" role="document">
                                <div className="modal-content">
                                    <form onSubmit={formik.handleSubmit}>
                                        <div className="modal-header" style={{ backgroundColor: "#052274", height: "44px" }}>
                                            <h5 className="modal-title" id="myModalLabel" style={{ color: "white", fontSize: "15px", fontWeight: "bold" }}>{t('content.home_page.setting.add_shift')}</h5>
                                        </div>
                                        <div className="modal-body">
                                            <div id="main-content">
                                                <table className="table table-input">
                                                    <tbody>
                                                        <tr>
                                                            <th width="150px">{t('content.home_page.setting.shift')}<span className="required">※</span></th>
                                                            <td>
                                                                <input type="text" className="form-control"
                                                                    id="shiftName" name="shiftName" maxLength={100} onChange={formik.handleChange}
                                                                />
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <th width="150px">{t('content.home_page.setting.start_time')}<span className="required">※</span></th>
                                                            <td>
                                                                <select

                                                                    id="startTime"
                                                                    name="startTime"
                                                                    onChange={formik.handleChange}
                                                                    value={formik.values.startTime}
                                                                    style={{ height: "80px", width: "100%" }}
                                                                >
                                                                    {hoursOptions.map((option) => (
                                                                        <option key={option.value} value={option.value}>
                                                                            {option.label}
                                                                        </option>
                                                                    ))}
                                                                </select>
                                                            </td>
                                                        </tr>

                                                        <tr>
                                                            <th width="150px">{t('content.home_page.setting.end_time')}<span className="required">※</span></th>
                                                            <td>
                                                                <select

                                                                    id="endTime"
                                                                    name="endTime"
                                                                    onChange={formik.handleChange}
                                                                    value={formik.values.endTime}
                                                                    style={{ height: "80px", width: "100%" }}
                                                                >
                                                                    {hoursOptions.map((option) => (
                                                                        <option key={option.value} value={option.value}>
                                                                            {option.label}
                                                                        </option>
                                                                    ))}
                                                                </select>
                                                            </td>
                                                        </tr>

                                                    </tbody>
                                                </table>
                                            </div>
                                        </div>
                                        <div className="modal-footer">
                                            <button type="button" className="btn btn-outline-primary" data-dismiss="modal">{t('content.close')}</button>
                                            <button type="submit" className="btn btn-primary">{t('content.save')}</button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                        {edit == true ?
                            <div className="collapse" id="collapseExample">
                                <div className="card card-body">
                                    {renderFormEdit(idEdit)}
                                </div>
                            </div>
                            : null}
                    </div>

                </>}

        </div >
    )
}

export default ListSettingShift;