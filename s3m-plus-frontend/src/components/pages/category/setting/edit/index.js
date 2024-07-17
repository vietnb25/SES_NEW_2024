import React, { useEffect, useState } from "react";
import { useFormik } from "formik";
import { useHistory, useParams } from "react-router-dom";
import SettingService from "../../../../../services/SettingService";
import CONS from "../../../../../constants/constant";
import { t } from "i18next";

const EditSetting = (props, { ids }) => {

    const param = useParams();
    const history = useHistory();

    const sendData = (ids) => {
        props.parentCallback(ids);
    }
    const [settings, setSettings] = useState({
        customerId: "",
        projectId: "",
        type: "",
        settingMstId: "",
        settingValue: "",
        settingValue2: "",
        settingMstName: "",
        description: ""
    });

    const loadSetting = async () => {
        let res = await SettingService.getSettingById(param.id);
        if (res.status === 200) {
            setSettings(res.data);
        }

    }

    const formik = useFormik({
        enableReinitialize: true,
        initialValues: settings,
        onSubmit: async (data) => {
            sendData({ idCustomer: data.customerId, idProject: data.projectId, typeSystem: data.type })
            let response = await SettingService.updateSetting(param.id, data);
            if (response.status === 200) {
                history.push({
                    pathname: `/category/setting`,
                    state: {
                        status: 200,
                        message: "UPDATE_SUCCESS"
                    }
                });
            }
        }
    });

    useEffect(() => {
        document.title = t('content.category.setting.list.title');
        loadSetting();
    }, [])

    return (
        <div id="page-body">
            <div id="main-title">
                <h5 className="d-block mb-0 float-left"><i className="fas fa-gear"></i> &nbsp;{t('content.category.setting.list.title')}</h5>
            </div>
            <form onSubmit={formik.handleSubmit}>
                <div id="main-content">

                    <table className="table table-input">
                        <tbody>
                            <tr>
                                <th width="150px">{t('content.category.setting.name')}</th>
                                <td>
                                    <input type="hidden" defaultValue={formik.values.type} name="type" onChange={formik.handleChange} className="form-control" />
                                    <input type="hidden" defaultValue={formik.values.settingMstId} name="settingMstId" onChange={formik.handleChange} className="form-control" />
                                    <input type="hidden" defaultValue={formik.values.customerId} name="customerId" onChange={formik.handleChange} className="form-control" />
                                    <input type="hidden" defaultValue={formik.values.projectId} name="projectId" onChange={formik.handleChange} className="form-control" />
                                    <input type="text" defaultValue={formik.values.settingMstName} name="settingMstName" onChange={formik.handleChange} className="form-control" disabled />
                                </td>
                            </tr>
                            <tr>
                                <th width="150px">{t('content.category.setting.value')}</th>
                                {
                                    (settings?.settingMstId === 1) &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}>  Ua || Ub || Uc {">"} <input type="number" step="0.0000000001" id="setting-value" onChange={formik.handleChange} name="settingValue" className="form-control d-inline-block" defaultValue={formik.values.settingValue} style={{ width: "60px" }} />&nbsp;V</span>
                                    </td>
                                }
                                {
                                    (settings?.settingMstId === 2) &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}>  Ua || Ub || Uc {"<"} <input type="number" step="0.0000000001" id="setting-value" onChange={formik.handleChange} name="settingValue" className="form-control d-inline-block" defaultValue={formik.values.settingValue} style={{ width: "60px" }} />&nbsp;V</span>
                                    </td>
                                }
                                {
                                    (settings?.settingMstId === 3) &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> Nhiệt độ ngoài trời || Nhiệt độ thiết bị {">"} <input type="number" step="0.0000000001" onChange={formik.handleChange} id="setting-value" name="settingValue" defaultValue={formik.values.settingValue} className="form-control d-inline-block" style={{ width: "60px" }} />&nbsp;°C</span>
                                    </td>
                                }
                                {
                                    (settings?.settingMstId === 4) &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> ((Ia + Ib + Ic)/3) / (CongSuatThietBi*1.44) {">"} <input type="number" step="0.0000000001" id="setting-value" onChange={formik.handleChange} name="settingValue" defaultValue={formik.values.settingValue} className="form-control d-inline-block" style={{ width: "60px" }} /> &nbsp;
                                            {"&"} cosA || cosB || cosC {"<"} <input type="number" step="0.0000000001" id="setting-value-2" onChange={formik.handleChange} defaultValue={formik.values.settingValue2} name="settingValue2" className="form-control d-inline-block" style={{ width: "60px" }} /></span>
                                    </td>
                                }
                                {
                                    (settings?.settingMstId === 5) &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> Ia || Ib || Ic {">="} <input type="number" step="0.0000000001" id="setting-value" name="settingValue" onChange={formik.handleChange} defaultValue={formik.values.settingValue} className="form-control d-inline-block" style={{ width: "60px" }} /> * Imccb </span>
                                    </td>
                                }
                                {
                                    (settings?.settingMstId === 6) &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> F {"<"} <input type="number" step="0.0000000001" id="setting-value" className="form-control d-inline-block" onChange={formik.handleChange} name="settingValue" defaultValue={formik.values.settingValue} style={{ width: "60px" }} /></span>
                                    </td>
                                }
                                {
                                    (settings?.settingMstId === 7) &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> F {">"} <input type="number" step="0.0000000001" id="setting-value" className="form-control d-inline-block" onChange={formik.handleChange} name="settingValue" defaultValue={formik.values.settingValue} style={{ width: "60px" }} /></span>
                                    </td>
                                }
                                {
                                    (settings?.settingMstId === 8) &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> Ua || Ub || Uc {"<"} <input type="number" step="0.0000000001" id="setting-value" className="form-control d-inline-block" onChange={formik.handleChange} name="settingValue" defaultValue={formik.values.settingValue} style={{ width: "60px" }} /></span>
                                    </td>
                                }
                                {
                                    (settings?.settingMstId === 9) &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> ((Ia + Ib + Ic)/3) / (CongSuatThietBi*1.44) {">"} <input type="number" step="0.0000000001" id="setting-value" onChange={formik.handleChange} defaultValue={formik.values.settingValue} name="settingValue" className="form-control d-inline-block" style={{ width: "60px" }} />
                                            {"&"} (Imax – Imin)/Imin {">"} <input type="number" step="0.0000000001" id="setting-value-2" onChange={formik.handleChange} defaultValue={formik.values.settingValue2} name="settingValue2" className="form-control d-inline-block" style={{ width: "60px" }} /></span>
                                    </td>
                                }
                                {
                                    (settings?.settingMstId === 10) &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> cosA || cosB || cosC {"<"} <input type="number" step="0.0000000001" id="setting-value" className="form-control d-inline-block" onChange={formik.handleChange} name="settingValue" defaultValue={formik.values.settingValue} style={{ width: "60px" }} /></span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 11 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> H_iA, iB, iC, uA-N, uB-N, uC-N {">"} <input type="number" step="0.0000000001" id="setting-value" name="settingValue" onChange={formik.handleChange} defaultValue={formik.values.settingValue} className="form-control d-inline-block" style={{ width: "60px" }} />&nbsp; (%)</span>
                                    </td>
                                }
                                {
                                    (settings?.settingMstId === 12) &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> THD_VA-N|| THD_VB-N || THD_VC-N {">"} <input type="number" step="0.0000000001" id="setting-value" name="settingValue" onChange={formik.handleChange} defaultValue={formik.values.settingValue} className="form-control d-inline-block" style={{ width: "60px" }} />&nbsp; (%)</span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 13 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> In {">"} Icap x <input type="number" step="0.0000000001" id="setting-value" defaultValue={formik.values.settingValue} name="settingValue" onChange={formik.handleChange} className="form-control d-inline-block" style={{ width: "60px" }} /></span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 14 &&
                                    <td>
                                        <input type="number" step="0.0000000001" className="form-control" disabled />
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 15 &&
                                    <td>
                                        <input type="number" step="0.0000000001" className="form-control" disabled />
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 16 &&
                                    <td>
                                        <input type="number" step="0.0000000001" className="form-control" disabled />
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 17 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> Umax - Umin {">"}  <input type="number" step="0.0000000001" id="setting-value" name="settingValue" onChange={formik.handleChange} defaultValue={formik.values.settingValue} className="form-control d-inline-block" style={{ width: "60px" }} /> (V)
                                            {"&&"} (UA,B,C {">"} <input type="number" step="0.0000000001" id="setting-value-2" onChange={formik.handleChange} defaultValue={formik.values.settingValue2} name="settingValue2" className="form-control d-inline-block" style={{ width: "60px" }} /> (V))</span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 18 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}><input type="number" step="0.0000000001" id="setting-value" defaultValue={formik.values.settingValue} name="settingValue" onChange={formik.handleChange} className="form-control d-inline-block" style={{ width: "60px" }} /></span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 19 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}><input type="number" step="0.0000000001" id="setting-value" defaultValue={formik.values.settingValue} name="settingValue" onChange={formik.handleChange} className="form-control d-inline-block" style={{ width: "60px" }} /></span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 20 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}><input type="number" step="0.0000000001" id="setting-value" defaultValue={formik.values.settingValue} name="settingValue" onChange={formik.handleChange} className="form-control d-inline-block" style={{ width: "60px" }} /></span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 21 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}><input type="number" step="0.0000000001" id="setting-value" defaultValue={formik.values.settingValue} name="settingValue" onChange={formik.handleChange} className="form-control d-inline-block" style={{ width: "60px" }} /></span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 22 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}><input type="number" step="0.0000000001" id="setting-value" defaultValue={formik.values.settingValue} name="settingValue" onChange={formik.handleChange} className="form-control d-inline-block" style={{ width: "60px" }} /></span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 23 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}><input type="number" step="0.0000000001" id="setting-value" defaultValue={formik.values.settingValue} name="settingValue" onChange={formik.handleChange} className="form-control d-inline-block" style={{ width: "60px" }} /></span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 24 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}><input type="number" step="0.0000000001" id="setting-value" defaultValue={formik.values.settingValue} name="settingValue" onChange={formik.handleChange} className="form-control d-inline-block" style={{ width: "60px" }} /></span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 25 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}><input type="number" step="0.0000000001" id="setting-value" defaultValue={formik.values.settingValue} name="settingValue" onChange={formik.handleChange} className="form-control d-inline-block" style={{ width: "60px" }} /></span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 26 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}><input type="number" step="0.0000000001" id="setting-value" defaultValue={formik.values.settingValue} name="settingValue" onChange={formik.handleChange} className="form-control d-inline-block" style={{ width: "60px" }} /></span>
                                    </td>
                                }{
                                    settings?.settingMstId === 27 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}><input type="number" step="0.0000000001" id="setting-value" defaultValue={formik.values.settingValue} name="settingValue" onChange={formik.handleChange} className="form-control d-inline-block" style={{ width: "60px" }} /></span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 28 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}><input type="number" step="0.0000000001" id="setting-value" defaultValue={formik.values.settingValue} name="settingValue" onChange={formik.handleChange} className="form-control d-inline-block" style={{ width: "60px" }} /></span>
                                    </td>
                                }

                                {
                                    settings?.settingMstId === 29 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}><input type="number" step="0.0000000001" id="setting-value" defaultValue={formik.values.settingValue} name="settingValue" onChange={formik.handleChange} className="form-control d-inline-block" style={{ width: "60px" }} /></span>
                                    </td>
                                }{
                                    settings?.settingMstId === 30 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}><input type="number" step="0.0000000001" id="setting-value" defaultValue={formik.values.settingValue} name="settingValue" onChange={formik.handleChange} className="form-control d-inline-block" style={{ width: "60px" }} /></span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 31 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}><input type="number" step="0.0000000001" id="setting-value" defaultValue={formik.values.settingValue} name="settingValue" onChange={formik.handleChange} className="form-control d-inline-block" style={{ width: "60px" }} /></span>
                                    </td>
                                }

                                {
                                    settings?.settingMstId === 32 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}><input type="number" step="0.0000000001" id="setting-value" defaultValue={formik.values.settingValue} name="settingValue" onChange={formik.handleChange} className="form-control d-inline-block" style={{ width: "60px" }} /></span>
                                    </td>
                                }{
                                    settings?.settingMstId === 33 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}><input type="number" step="0.0000000001" id="setting-value" defaultValue={formik.values.settingValue} name="settingValue" onChange={formik.handleChange} className="form-control d-inline-block" style={{ width: "60px" }} /></span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 34 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> Ua || Ub || Uc {">"} <input type="number" step="0.0000000001" id="setting-value" name="settingValue" onChange={formik.handleChange} defaultValue={formik.values.settingValue} className="form-control d-inline-block" style={{ width: "60px" }} /> </span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 35 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> Ua || Ub || Uc {"<"} <input type="number" step="0.0000000001" id="setting-value" name="settingValue" onChange={formik.handleChange} defaultValue={formik.values.settingValue} className="form-control d-inline-block" style={{ width: "60px" }} /> </span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 36 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> Nhiệt độ ngoài trời || Nhiệt độ thiết bị {">"} <input type="number" step="0.0000000001" id="setting-value" name="settingValue" onChange={formik.handleChange} defaultValue={formik.values.settingValue} className="form-control d-inline-block" style={{ width: "60px" }} /> °C </span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 37 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> SAW_ID1 || SAW_ID2...|| SAW_ID6 {">"} <input type="number" step="0.0000000001" id="setting-value" name="settingValue" onChange={formik.handleChange} defaultValue={formik.values.settingValue} className="form-control d-inline-block" style={{ width: "60px" }} /> °C </span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 38 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> ((Ia + Ib + Ic)/3) / (CongSuatThietBi*1.44) {">"}  <input type="number" step="0.0000000001" id="setting-value" name="settingValue" onChange={formik.handleChange} defaultValue={formik.values.settingValue} className="form-control d-inline-block" style={{ width: "60px" }} /> & cosA || cosB || cosC {"<"}
                                            <input type="number" step="0.0000000001" id="setting-value-2" onChange={formik.handleChange} defaultValue={formik.values.settingValue2} name="settingValue2" className="form-control d-inline-block" style={{ width: "60px" }} /></span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 39 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> Ia || Ib || Ic {">="} <input type="number" step="0.0000000001" id="setting-value" name="settingValue" onChange={formik.handleChange} defaultValue={formik.values.settingValue} className="form-control d-inline-block" style={{ width: "60px" }} /> * Imccb </span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 40 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> F {">"}  <input type="number" step="0.0000000001" id="setting-value" name="settingValue" onChange={formik.handleChange} defaultValue={formik.values.settingValue} className="form-control d-inline-block" style={{ width: "60px" }} /> </span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 41 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> F {"<"}  <input type="number" step="0.0000000001" id="setting-value" name="settingValue" onChange={formik.handleChange} defaultValue={formik.values.settingValue} className="form-control d-inline-block" style={{ width: "60px" }} /> </span>
                                    </td>
                                }{
                                    settings?.settingMstId === 42 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> Ua || Ub || Uc {"<"}  <input type="number" step="0.0000000001" id="setting-value" name="settingValue" onChange={formik.handleChange} defaultValue={formik.values.settingValue} className="form-control d-inline-block" style={{ width: "60px" }} /> </span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 43 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> ((Ia + Ib + Ic)/3) / (CongSuatThietBi*1.44) {">"}  <input type="number" step="0.0000000001" id="setting-value" name="settingValue" onChange={formik.handleChange} defaultValue={formik.values.settingValue} className="form-control d-inline-block" style={{ width: "60px" }} /> & (Imax – Imin)/Imin {">"}
                                            <input type="number" step="0.0000000001" id="setting-value-2" onChange={formik.handleChange} defaultValue={formik.values.settingValue2} name="settingValue2" className="form-control d-inline-block" style={{ width: "60px" }} /></span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 44 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> cosA || cosB || cosC {"<"}  <input type="number" step="0.0000000001" id="setting-value" name="settingValue" onChange={formik.handleChange} defaultValue={formik.values.settingValue} className="form-control d-inline-block" style={{ width: "60px" }} /> </span>
                                    </td>
                                }

                                {
                                    settings?.settingMstId === 45 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> THD_VA-N|| THD_VB-N || THD_VC-N {">"}  <input type="number" step="0.0000000001" id="setting-value" name="settingValue" onChange={formik.handleChange} defaultValue={formik.values.settingValue} className="form-control d-inline-block" style={{ width: "60px" }} /> % </span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 46 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}><input type="number" step="0.0000000001" id="setting-value" defaultValue={formik.values.settingValue} name="settingValue" onChange={formik.handleChange} className="form-control d-inline-block" style={{ width: "60px" }} /></span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 47 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> H {">"}  <input type="number" step="0.0000000001" id="setting-value" name="settingValue" onChange={formik.handleChange} defaultValue={formik.values.settingValue} className="form-control d-inline-block" style={{ width: "60px" }} /> % </span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 48 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> T {">"}  <input type="number" step="0.0000000001" id="setting-value" name="settingValue" onChange={formik.handleChange} defaultValue={formik.values.settingValue} className="form-control d-inline-block" style={{ width: "60px" }} /> °C </span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 49 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> Giá điện giờ thấp điểm {"="}  <input type="number" step="0.0000000001" id="setting-value" name="settingValue" onChange={formik.handleChange} defaultValue={formik.values.settingValue} className="form-control d-inline-block" style={{ width: "60px" }} /> VNĐ </span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 50 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> Giá điện giờ bình thường {"="}  <input type="number" step="0.0000000001" id="setting-value" name="settingValue" onChange={formik.handleChange} defaultValue={formik.values.settingValue} className="form-control d-inline-block" style={{ width: "60px" }} /> VNĐ </span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 51 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> Giá điện giờ cao điểm {"="}  <input type="number" step="0.0000000001" id="setting-value" name="settingValue" onChange={formik.handleChange} defaultValue={formik.values.settingValue} className="form-control d-inline-block" style={{ width: "60px" }} /> VNĐ </span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 52 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> Tiền bán điện giờ tháp điểm {"="}  <input type="number" step="0.0000000001" id="setting-value" name="settingValue" onChange={formik.handleChange} defaultValue={formik.values.settingValue} className="form-control d-inline-block" style={{ width: "60px" }} /> VNĐ </span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 53 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> Tiền bán điện giờ bình thường {"="}  <input type="number" step="0.0000000001" id="setting-value" name="settingValue" onChange={formik.handleChange} defaultValue={formik.values.settingValue} className="form-control d-inline-block" style={{ width: "60px" }} /> VNĐ </span>
                                    </td>
                                }
                                {
                                    settings?.settingMstId === 54 &&
                                    <td>
                                        <span className="form-control" style={{ backgroundColor: "#EBEBEB" }}> Tiền bán điện giờ cao điểm {"="}  <input type="number" step="0.0000000001" id="setting-value" name="settingValue" onChange={formik.handleChange} defaultValue={formik.values.settingValue} className="form-control d-inline-block" style={{ width: "60px" }} /> VNĐ </span>
                                    </td>
                                }
                            </tr>
                        </tbody>
                    </table>
                    <div id="main-button">
                        <button type="submit" className="btn btn-outline-secondary btn-agree mr-1" >
                            <i className="fa-solid fa-check"></i>
                        </button>
                        <button type="button" className="btn btn-outline-secondary btn-cancel" onClick={() => {
                            history.push({
                                pathname: `/category/setting`,
                                state: {
                                    status: -1,
                                    message: "NO UPDATE"
                                }
                            });
                            sendData({ idCustomer: settings.customerId, idProject: settings.projectId, typeSystem: settings.type });
                        }}>
                            <i className="fa-solid fa-xmark"></i>
                        </button>
                    </div>
                </div>
            </form>
        </div>
    )
}

export default EditSetting;