import React, { useEffect, useState } from 'react';
import SettingLink from '../setting-link';
import { ToastContainer } from "react-toastify";
import ProjectService from '../../../../../services/ProjectService';
import { useParams } from 'react-router-dom/cjs/react-router-dom';
import SettingCostService from '../../../../../services/SettingCostService';
import { NotficationSuscces } from '../../notification/notification';
import moment from 'moment/moment';
import { t } from 'i18next';


const SettingCost = () => {
    const [peakHour, setPeakHour] = useState("");
    const [nonPeakHour, setNonPeakHour] = useState("");
    const [normalHour, setNormalHour] = useState("");
    const [vat, setVat] = useState("");
    const [edit, setEdit] = useState(true);
    const [projects, setProjects] = useState([]);
    const [projectId, setProjectId] = useState([]);
    const [updateDate, setUpdateDate] = useState();
    const param = useParams();

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
    const changeProject = async (projectId) => {
        getSettingCost(projectId)
        setProjectId(projectId);
    }
    const getSettingCost = async (projectId) => {
        let res = await SettingCostService.listSettingCostByProject(projectId);
        if (res.status == 200) {
            if (res.data[0] != undefined) {
                res.data.map(st => {
                    if (st.settingCostMstId == 1) {
                        setPeakHour(st.settingValue)
                    }
                    if (st.settingCostMstId == 3) {
                        setNonPeakHour(st.settingValue)
                    }
                    if (st.settingCostMstId == 2) {
                        setNormalHour(st.settingValue)
                    }
                    if (st.settingCostMstId == 4) {
                        setVat(st.settingValue)
                    }
                })

                setUpdateDate(res.data[0].updateDate)
            }
        }
    }
    const clickSave = async () => {
        const data = {
            projectId: projectId,
            peakHour: peakHour,
            nonPeakHour: nonPeakHour,
            normalHour: normalHour,
            vat: vat
        }
        let res = await SettingCostService.updateSettingCost(data);
        if (res.status == 200) {
            NotficationSuscces(t('content.update_success'));
            getSettingCost(projectId);
        }
    }
    const getListProject = async () => {
        let res = await ProjectService.getProjectByCustomerId(param.customerId);
        if (res.status == 200) {
            if (res.data.length > 0) {
                setProjects(res.data)
                setProjectId(res.data[0].projectId)
                getSettingCost(res.data[0].projectId)
            }
        }

    }
    const getProject = async (id) => {
        let res = await ProjectService.getProject(id);
        setProjects([res.data]);

        setProjectId(id)
        getSettingCost(id)
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
        {/* <div>
            <SettingLink active={6} /><hr />
        </div> */}
        <ToastContainer />
        <div className='' style={{ width: '20%', marginLeft: '0.6%' }}>
            <select id={"project"} className='form-select' style={{ width: '100%' }} disabled={param.projectId != undefined} onChange={(event) => changeProject(event.target.value)} >
                {projects?.map((pro, index) => {
                    return <option key={index} value={pro.projectId}>{pro.projectName}</option>
                })}
            </select>
        </div>
        <div id="main-content">
            <table className="table">
                <thead height="30px">
                    <tr>
                        <th width="100px">{t('content.no')}</th>
                        <th>{t('content.home_page.chart.off_peak_hours')}</th>
                        <th>{t('content.home_page.chart.normal')}</th>
                        <th>{t('content.home_page.chart.peak_hours')}</th>
                        <th>{t('content.home_page.vat')}</th>
                        <th width="300px">{t('content.update_date')}</th>
                        <th width="40px"><i className="fa-regular fa-hand"></i></th>
                    </tr>
                </thead>
                <tbody>
                    <tr height="25px">
                        <td className="text-center">1</td>

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

            </table>
        </div>
    </>
    );
};

export default SettingCost;