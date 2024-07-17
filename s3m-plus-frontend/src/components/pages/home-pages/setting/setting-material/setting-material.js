import React, { useEffect, useState } from 'react';
import SettingLink from '../setting-link';
import { ToastContainer } from "react-toastify";
import ProjectService from '../../../../../services/ProjectService';
import { useParams } from 'react-router-dom/cjs/react-router-dom';
import SettingCostService from '../../../../../services/SettingCostService';
import { NotficationError, NotficationSuscces, NotficationWarning } from '../../notification/notification';
import moment from 'moment/moment';
import { t } from 'i18next';
import MaterialService from '../../../../../services/MaterialService';


const SettingMaterial = () => {
    const [peakHour, setPeakHour] = useState("");
    const [nonPeakHour, setNonPeakHour] = useState("");
    const [normalHour, setNormalHour] = useState("");
    const [vat, setVat] = useState("");
    const [edit, setEdit] = useState(true);
    const [projects, setProjects] = useState([]);
    const [materialTypes, setMaterialTypes] = useState([]);
    const [projectId, setProjectId] = useState();
    const [materialId, setMaterialId] = useState([]);
    const [updateDate, setUpdateDate] = useState();
    const param = useParams();
    const [loading, setLoading] = useState(false);

    const setInputPeakHour = (e) => {
        var char = e.target.value.slice(-1);
        const regex = new RegExp("^[0-9]*?$");
        if (regex.test(char) == true || e.target.value == "" || e.target.value.slice(-1) == "," || e.target.value.slice(-1) == ".") {
            setPeakHour(e.target.value)
        }
    }
    const setInputNonPeakHour = (e) => {
        var char = e.target.value.slice(-1);
        const regex = new RegExp("^[0-9]*?$");
        if (regex.test(char) == true || e.target.value == "" || e.target.value.slice(-1) == "," || e.target.value.slice(-1) == ".") {
            setNonPeakHour(e.target.value)
        }
    }
    const setInputNormalHour = (e) => {
        var char = e.target.value.slice(-1);
        const regex = new RegExp("^[0-9]*?$");
        if (regex.test(char) == true || e.target.value == "" || e.target.value.slice(-1) == "," || e.target.value.slice(-1) == ".") {
            setNormalHour(e.target.value)
        }
    }

    const setInputVAT = (e) => {
        var char = e.target.value.slice(-1);
        const regex = new RegExp("^[0-9]*?$");
        if (regex.test(char) == true || e.target.value == "" || e.target.value.slice(-1) == "," || e.target.value.slice(-1) == ".") {
            setVat(e.target.value)
        } else {
            setVat(null);
        }
    }
    const changeProject = (projectId) => {
        setProjectId(projectId);
        getListMaterialValue(projectId, materialId)
    }

    const changeMaterialType = (id) => {
        setMaterialId(id);
        getListMaterialValue(projectId, id)
    }


    const clickSave = async () => {
        const data = {
            projectId: projectId,
            materialId: materialId,
            peakHour: Number.parseInt(peakHour),
            nonPeakHour: Number.parseInt(nonPeakHour),
            normalHour: Number.parseInt(normalHour),
            vat: Number.parseInt(vat)
        }
        if (data.peakHour == 0 || data.nonPeakHour == 0 || data.normalHour == 0 || data.vat == 0) {
            NotficationWarning("Giá trị phải lớn hơn 0")
        } else if (peakHour == "" || nonPeakHour == "" || normalHour == "" || vat == "") {
            NotficationWarning("Giá trị không được để trống")
        } else {

            let res = await MaterialService.addOrUpdateMaterialValue(data);
            if (res.status == 200) {
                getListMaterialValue(projectId, materialId);
                NotficationSuscces("Cập nhật thành công!")
            } else {
                NotficationError("Cập nhật không thành công!")
            }
        }

    }
    const getListProject = async () => {
        let res = await ProjectService.getProjectByCustomerId(param.customerId);
        if (res.status == 200) {
            if (res.data.length > 0) {
                setProjects(res.data);
                setProjectId(res.data[0].projectId);
                getListMaterialType(res.data[0].projectId);
            }
        }

    }
    const getProject = async (id) => {
        let res = await ProjectService.getProject(id);
        setProjects([res.data]);
        setProjectId(id)
        getListMaterialType(id);
    }

    const getListMaterialType = async (projectId) => {
        let res = await MaterialService.getListMaterialType();
        if (res.status == 200) {
            setMaterialTypes(res.data)
            setMaterialId(res.data[0].id)
            getListMaterialValue(projectId, res.data[0].id)
        }
    }
    const getListMaterialValue = async (projectId, materialValue) => {
        setLoading(true)
        let res = await MaterialService.getListMaterialValue(projectId, materialValue);
        if (res.status == 200) {
            if (res.data.length > 0) {
                res.data.map(material => {
                    if (material.typeTime == 1) {
                        setPeakHour(material.materialPrice)
                    }
                    if (material.typeTime == 3) {
                        setNonPeakHour(material.materialPrice)
                    }
                    if (material.typeTime == 2) {
                        setNormalHour(material.materialPrice)
                    }
                    if (material.typeTime == 4) {
                        setVat(material.materialPrice)
                    }
                })
                setUpdateDate(res.data[0].updateDate)
            }
        } else {
            setPeakHour("")
            setNonPeakHour("")
            setNormalHour("")
            setVat("")
        }
        setLoading(false)
    }
    useEffect(() => {
        if (param.projectId != undefined) {
            getProject(param.projectId)
            setProjectId(param.projectId)
        } else {
            getListProject();
        }

    }, [param.projectId, param.customerId])

    return (<>
        <ToastContainer />
        <div className='d-flex'>
            <div className='' style={{ width: '20%', marginLeft: '0.6%' }}>
                <p
                    style={{
                        fontWeight: 'bold',
                        fontFamily: 'Arial, Helvetica, sans-serif',
                        fontSize: '14px',
                        marginBottom: '1%'
                    }}

                >
                    {t('content.project')}
                </p>
                <select id={"project"} className='form-select' style={{ width: '100%' }} disabled={param.projectId != undefined} onChange={(event) => changeProject(event.target.value)} >
                    {projects?.map((pro, index) => {
                        return <option key={index} value={pro.projectId}>{pro.projectName}</option>
                    })}
                </select>
            </div>
            <div className='' style={{ width: '20%', marginLeft: '2%' }}>
                <p
                    style={{
                        fontWeight: 'bold',
                        fontFamily: 'Arial, Helvetica, sans-serif',
                        fontSize: '14px',
                        marginBottom: '1%'
                    }}

                >
                    {"Nguyên liệu"}
                </p>
                <select id={"project"} className='form-select' style={{ width: '100%' }} onChange={(event) => changeMaterialType(event.target.value)} >
                    {materialTypes?.map((material, index) => {
                        return <option key={index} value={material.id}>{material.materialName}</option>
                    })}
                </select>
            </div>
        </div>
        <div id="main-content">
            <table className="table">
                <thead height="30px">
                    <tr>
                        <th>{t('content.home_page.chart.off_peak_hours')}</th>
                        <th>{t('content.home_page.chart.normal')}</th>
                        <th>{t('content.home_page.chart.peak_hours')}</th>
                        <th>{t('content.home_page.vat')}</th>
                        <th width="300px">{t('content.update_date')}</th>
                        <th width="40px"><i className="fa-regular fa-hand"></i></th>
                    </tr>
                </thead>
                {loading == true ?
                    <tbody>
                        <tr>
                            <td className="loading" style={{ marginTop: "10%", marginLeft: "700px" }}>
                                <img height="60px" src="/resources/image/loading2.gif" alt="loading" />
                            </td>
                        </tr>
                    </tbody>
                    :
                    <tbody>
                        <tr height="25px">
                            <td style={{ wordWrap: "break-word" }}>
                                <input type="text" style={{ cursor: "context-menu" }} value={nonPeakHour} onChange={(event) => setInputNonPeakHour(event)} className="input-shift-project" readOnly={edit === true} />
                            </td>
                            <td style={{ wordWrap: "break-word" }}>
                                <input type="text" style={{ cursor: "context-menu" }} value={normalHour} onChange={(event) => setInputNormalHour(event)} className="input-shift-project" readOnly={edit === true} />
                            </td>
                            <td style={{ wordWrap: "break-word" }}>
                                <input type="text" style={{ cursor: "context-menu" }} value={peakHour} onChange={(event) => setInputPeakHour(event)} className="input-shift-project" readOnly={edit === true} />
                            </td>
                            <td style={{ wordWrap: "break-word" }}>
                                <input type="text" style={{ cursor: "context-menu" }} value={vat} onChange={(event) => setInputVAT(event)} className="input-shift-project" readOnly={edit === true} />
                            </td>
                            <td className="text-center" style={{ cursor: "context-menu" }}>
                                {updateDate != undefined ? moment(updateDate).format("yyyy-MM-DD hh:mm:ss") : ""}
                            </td>
                            <td className="text-center">
                                {edit === false ? <a className="button-icon" title="Chỉnh sửa">
                                    <i className="fa-solid fa-circle-check" style={{ color: "#0A1A5C", fontSize: '18px' }} onClick={() => {
                                        setEdit(true)
                                        clickSave()
                                    }} ></i>
                                </a>
                                    : <a className="button-icon" title="Chỉnh sửa" >
                                        <i className="fas fa-edit" style={{ color: "#F37021", fontSize: '18px' }} onClick={() => setEdit(false)} ></i>
                                    </a>
                                }
                            </td>
                        </tr>
                    </tbody>
                }

            </table>
        </div>
    </>
    );
};

export default SettingMaterial;