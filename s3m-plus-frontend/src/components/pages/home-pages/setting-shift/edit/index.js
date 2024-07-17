import React, { useEffect, useState } from "react";
import { useFormik } from "formik";
import { useHistory, useParams } from "react-router-dom";
import SettingShiftService from "../../../../../services/SettingShiftService";
import CONS from "../../../../../constants/constant";
import { PatternFormat } from 'react-number-format';
import * as Yup from 'yup';
import { NotficationError, NotficationSuscces } from "../../notification/notification";
import { ToastContainer } from "react-toastify";

const EditSettingShift = (props) => {
    console.log(props.id);
    const param = useParams();
    const history = useHistory();
    const customerId = param.customerId;
    const shift = /^([0-1]?[0-9]|2[0-3]):(00|15|30|45)\s-\s([0-1]?[0-9]|2[0-3]):(00|15|30|45)$/


    const  sendData = (status) => {
        props.callbackFunction(status);
    }
    const validationSchema = Yup.object().shape({
        shift1: Yup.string().matches(shift, 'Ca làm việc không đúng định dạng, phút (00, 15, 30, 45)'),
        shift2: Yup.string().matches(shift, 'Ca làm việc không đúng định dạng, phút (00, 15, 30, 45)'),
        shift3: Yup.string().matches(shift, 'Ca làm việc không đúng định dạng, phút (00, 15, 30, 45)')
    })

    const [settings, setSettings] = useState({
        customerId: "",
        projectId: "",
        systemTypeId: "",
        id: "",
        shift1: "",
        shift2: "",
        shift3: "",
        updateDate: "",
        fromDate: "",
        toDate: ""
    });

    const loadSetting = async () => {
        let res = await SettingShiftService.getSettingById(props.id);
        if (res.status === 200) {
            setSettings(res.data);
        }
    }

    const formik = useFormik({
        enableReinitialize: true,
        validationSchema,
        initialValues: settings,
        onSubmit: async (data) => {
            let response = await SettingShiftService.updateSetting(props.id, data);
            sendData(response.status)
            if (response.status === 200) {
                NotficationSuscces("Cập nhật thành công!")
            }else {
                NotficationError("Cập nhật không thành công")
            }
        }
    });

    useEffect(() => {
        document.title = "Chỉnh sửa cài đặt";
        loadSetting();
    }, [])

    return (
        <div id="">
            {/* <div className="title-up-warning text-left">
                <div style={{ marginTop: "-21px", color: "white" }}><i className="fa-solid fa-gear ml-1" style={{ color: "#fff" }}></i> CHỈNH SỬA CÀI ĐẶT
                </div>
            </div> */}
            <ToastContainer></ToastContainer>
            <div className="" style={{textAlign: 'left', marginLeft: '0.8%'}} ><p style={{fontWeight: 'bold', fontSize: '18px', fontFamily:'Arial, Helvetica, sans-serif' }}>Chỉnh sửa ca làm việc</p> </div>
            <form onSubmit={formik.handleSubmit}>
                <div id="main-content">
                {
                    ((formik.errors.projectName && formik.touched.projectName) || (formik.errors.latitude && formik.touched.latitude === true) || (formik.errors.longitude && formik.touched.longitude)
                    || (formik.errors.shift1 && formik.touched.shift1) || (formik.errors.shift2 && formik.touched.shift2) || (formik.errors.shift3 && formik.touched.shift3)) &&
                    <div className="alert alert-warning" role="alert">
                        <p className="m-0 p-0">{formik.errors.projectName}</p>
                        <p className="m-0 p-0">{formik.errors.latitude}</p>
                        <p className="m-0 p-0">{formik.errors.longitude}</p>
                        <p className="m-0 p-0">{formik.errors.shift1}</p>
                        <p className="m-0 p-0">{formik.errors.shift2}</p>
                        <p className="m-0 p-0">{formik.errors.shift3}</p>
                    </div>
                }
                    <table className="table">
                        <thead height="30px">
                            <tr>
                                <th width="100px">TT</th>
                                <th>Thời gian ca 1</th>
                                <th>Thời gian ca 2</th>
                                <th>Thời gian ca 3</th>
                                <th width="300px">Ngày cập nhật</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr  height="25px">
                                <td className="text-center">1</td>

                                <td style={{ wordWrap: "break-word" }}>
                                    <PatternFormat type="text" name="shift1" format="##:## - ##:##" mask="_" className="input-shift-project" value={formik.values.shift1} onChange={formik.handleChange}/>
                                </td>
                                <td style={{ wordWrap: "break-word" }}>
                                    <PatternFormat type="text" name="shift2" format="##:## - ##:##" mask="_" className="input-shift-project" value={formik.values.shift2} onChange={formik.handleChange}/>
                                </td>
                                <td style={{ wordWrap: "break-word" }}>
                                    <PatternFormat type="text" name="shift3" format="##:## - ##:##" mask="_" className="input-shift-project" value={formik.values.shift3} onChange={formik.handleChange}/>
                                </td>
                                <td className="text-center" style={{ cursor: "context-menu"}} >
                                    {formik.values.updateDate}
                                </td>
                            </tr>

                        </tbody>
                    </table>
                    <div id="main-button">
                        <button type="submit" className="btn btn-outline-secondary btn-agree mr-1">
                            <i className="fa-solid fa-check"></i>
                        </button>
                        {/* <button type="button" className="btn btn-outline-secondary btn-cancel" onClick={() => history.goBack()}>
                            <i className="fa-solid fa-xmark"></i>
                        </button> */}
                    </div>
                </div>
            </form>
        </div>
    )
}

export default EditSettingShift;