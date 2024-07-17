import React, { useEffect, useState } from 'react';
import SettingLink from '../setting-link';
import { Link, useParams } from 'react-router-dom';
import "./index.css"
import ProjectService from '../../../../../services/ProjectService';
import SimulationDataService from '../../../../../services/SimulationDataService';
import { data, event } from 'jquery';
import { index } from 'd3';
import axios from 'axios';
import { ToastContainer } from 'react-toastify';
import { NotficationError, NotficationSuscces } from '../../notification/notification';
import { any } from '@amcharts/amcharts5/.internal/core/util/Array';
import { useTranslation } from 'react-i18next';
import { load } from '@amcharts/amcharts5/.internal/core/util/Net';

const DataSimulation = (props) => {
    const { t } = useTranslation();
    const param = useParams();
    const [render, setRender] = useState(false)
    const [projects, setProjects] = useState([]);
    const [projectId, setProjectId] = useState();
    const [checkElectricalPower, setCheckElectricalPower] = useState(true);
    const [edit, setEdit] = useState(true);
    const [editIndex, setEditIndex] = useState(0);
    const [btnEdit, setBtnEdit] = useState(true);
    const [epAndMoney, setEpAndMoney] = useState(true);
    const [listDataEp, setListDataEp] = useState([]);
    const [btnAdd, setBtnAdd] = useState(false);
    const [listDataMoney, setListDataMoney] = useState([]);
    const [year, setYear] = useState();
    const [t1, setT1] = useState();
    const [t2, setT2] = useState();
    const [t3, setT3] = useState();
    const [t4, setT4] = useState();
    const [t5, setT5] = useState();
    const [t6, setT6] = useState();
    const [t7, setT7] = useState();
    const [t8, setT8] = useState();
    const [t9, setT9] = useState();
    const [t10, setT10] = useState();
    const [t11, setT11] = useState();
    const [t12, setT12] = useState();
    const [dollar, setDollar] = useState(true);
    const [reRender, setReRender] = useState(true);
    const [loading, setLoading] = useState(false);

    const loadListProject = async (customerId) => {
        let resp = await ProjectService.getProjectByCustomerId(customerId);
        if (resp.status === 200) {
            setProjects(resp.data)
            setProjectId(resp.data[0].projectId)
            loadListData(param.customerId, resp.data[0].projectId, props.typeSystem);
        }
    }
    const getProject = async (id) => {
        let resp = await ProjectService.getProject(id);
        setProjects([resp.data])
        loadListData(param.customerId, resp.data.projectId, props.typeSystem);
    }
    const handleClickProject = (id) => {
        setLoading(false)
        setProjectId(id);
    }
    const loadListData = async (customer, project, systemType) => {
        setListDataEp([])
        setListDataMoney([])
        if (checkElectricalPower === true) {
            let res = await SimulationDataService.listDataEP(customer, project, systemType);
            setListDataEp(res.data)
            if (res.status == 200) {
                setLoading(true)
            }
        } else {
            let res = await SimulationDataService.listDataMoney(customer, project, systemType);
            setListDataMoney(res.data)
            if (res.status == 200) {
                setLoading(true)
            }
        }

    }
    const getListProject = async () => {
        let idProject = document.getElementById("project").value;
        let res = await ProjectService.getProjectByCustomerId(param.customerId);
        setProjects(res.data)
        loadListData(param.customerId, idProject, props.typeSystem);
    }

    useEffect(() => {
        if (param.projectId !== undefined) {
            getProject(param.projectId);
        }
        else if (projectId != undefined && param.projectId === undefined) {
            getListProject();
        } else {
            loadListProject(param.customerId);
        }

    }, [props.typeSystem, projectId, param.customerId, param.projectId, render, dollar])

    const onClickButonEdit = (index) => {
        setBtnEdit(!btnEdit)
        setEditIndex(index);
    }
    const onClickButonCheck = async (id) => {
        setBtnEdit(!btnEdit)
        setEditIndex(0);
        if (checkElectricalPower == true) {
            const data =
            {
                "year": year == undefined ? "" : year,
                "jan": t1 == undefined ? "" : t1,
                "mar": t3 == undefined ? "" : t3,
                "apr": t4 == undefined ? "" : t4,
                "may": t5 == undefined ? "" : t5,
                "jun": t6 == undefined ? "" : t6,
                "jul": t7 == undefined ? "" : t7,
                "aug": t8 == undefined ? "" : t8,
                "sep": t9 == undefined ? "" : t9,
                "oct": t10 == undefined ? "" : t10,
                "feb": t2 == undefined ? "" : t2,
                "nov": t11 == undefined ? "" : t11,
                "dec": t12 == undefined ? "" : t12,
                "updateDate": null,
                "projectId": projectId,
                "systemTypeId": props.typeSystem,
                "customer": param.customerId,
            }
            setRender(!render)
            const res = await SimulationDataService.updateDataEp(id, data);
            if (res.status == 200) {
                NotficationSuscces("Chỉnh sửa thành công!")
            } else {
                NotficationError("Chỉnh sửa không thành công!")
            }
        } else {
            const data =
            {
                "year": year == undefined ? "" : year,
                "jan": t1 == undefined ? "" : t1,
                "mar": t3 == undefined ? "" : t3,
                "apr": t4 == undefined ? "" : t4,
                "may": t5 == undefined ? "" : t5,
                "jun": t6 == undefined ? "" : t6,
                "jul": t7 == undefined ? "" : t7,
                "aug": t8 == undefined ? "" : t8,
                "sep": t9 == undefined ? "" : t9,
                "oct": t10 == undefined ? "" : t10,
                "feb": t2 == undefined ? "" : t2,
                "nov": t11 == undefined ? "" : t11,
                "dec": t12 == undefined ? "" : t12,
                "updateDate": null,
                "projectId": projectId,
                "systemTypeId": props.typeSystem,
                "customer": param.customerId,
            }
            setRender(!render)
            const res = await SimulationDataService.updateDataMoney(id, data);
            if (res.status == 200) {
                NotficationSuscces("Chỉnh sửa thành công!")
            } else {
                NotficationError("Chỉnh sửa không thành công!")
            }
        }
        clearData();
    }
    const newData = () => {
        setBtnAdd(true)
        const date = new Date();
        const data =
        {
            "year": "",
            "jan": "",
            "mar": "",
            "apr": "",
            "may": "",
            "jun": "",
            "jul": "",
            "aug": "",
            "sep": "",
            "oct": "",
            "feb": "",
            "nov": "",
            "dec": "",
            "updateDate": null,
            "projectId": projectId,
            "systemTypeId": props.typeSystem,
            "customer": param.customerId,
        }
        if (checkElectricalPower == true) {
            setListDataEp([...listDataEp, data])
            console.log(listDataEp);
        } else {
            setListDataMoney([...listDataMoney, data])
        }

    }
    const onClickButonAdd = async () => {
        setBtnEdit(false)
        setEditIndex(0);
        if (checkElectricalPower == true) {
            const data =
            {
                "year": year == undefined ? "" : year,
                "jan": t1 == undefined ? "" : t1,
                "mar": t3 == undefined ? "" : t3,
                "apr": t4 == undefined ? "" : t4,
                "may": t5 == undefined ? "" : t5,
                "jun": t6 == undefined ? "" : t6,
                "jul": t7 == undefined ? "" : t7,
                "aug": t8 == undefined ? "" : t8,
                "sep": t9 == undefined ? "" : t9,
                "oct": t10 == undefined ? "" : t10,
                "feb": t2 == undefined ? "" : t2,
                "nov": t11 == undefined ? "" : t11,
                "dec": t12 == undefined ? "" : t12,
                "updateDate": null,
                "projectId": projectId,
                "systemTypeId": props.typeSystem,
                "customer": param.customerId,
            }
            const res = await SimulationDataService.addDataEp(data);
            if (res.status == 200) {
                NotficationSuscces("Thêm mới thành công!")
            } else {
                NotficationError("Chỉnh sửa không thành công!")
            }
        } else {
            const data =
            {
                "year": year == undefined ? "" : year,
                "jan": t1 == undefined ? "" : t1,
                "mar": t3 == undefined ? "" : t3,
                "apr": t4 == undefined ? "" : t4,
                "may": t5 == undefined ? "" : t5,
                "jun": t6 == undefined ? "" : t6,
                "jul": t7 == undefined ? "" : t7,
                "aug": t8 == undefined ? "" : t8,
                "sep": t9 == undefined ? "" : t9,
                "oct": t10 == undefined ? "" : t10,
                "feb": t2 == undefined ? "" : t2,
                "nov": t11 == undefined ? "" : t11,
                "dec": t12 == undefined ? "" : t12,
                "updateDate": null,
                "projectId": projectId,
                "systemTypeId": props.typeSystem,
                "customer": param.customerId,
            }
            if (dollar === false) {
                data.year = data.year / 24000;
                data.jan = data.jan / 24000;
                data.feb = data.feb / 24000;
                data.mar = data.mar / 24000;
                data.apr = data.apr / 24000;
                data.may = data.may / 24000;
                data.jun = data.jun / 24000;
                data.jul = data.jul / 24000;
                data.aug = data.aug / 24000;
                data.sep = data.sep / 24000;
                data.oct = data.oct / 24000;
                data.nov = data.nov / 24000;
                data.dec = data.dec / 24000;
            }
            const res = await SimulationDataService.addDataMoney(data);
            if (res.status == 200) {
                NotficationSuscces("Thêm mới thành công!")
            } else {
                NotficationError("Chỉnh sửa không thành công!")
            }
        }
        loadListProject(param.customerId)
        clearData();
    }
    const clearData = () => {
        setYear(null);
        setT1(null);
        setT2(null);
        setT3(null);
        setT4(null);
        setT5(null);
        setT6(null);
        setT7(null);
        setT8(null);
        setT9(null);
        setT10(null);
        setT11(null);
        setT12(null);
    }


    return (<>
        {/* Select Project and Type Device */}
        <ToastContainer />
        <div className=''>
            <div className="">
                <div className='main-content-sim'>
                    <div className='content-btn-sim'>
                        <div style={{ width: '15%', marginRight: '1%' }}>
                            <select id={"project"} className='form-select' style={{ width: '100%' }} disabled={param.projectId !== undefined} onChange={(event) => handleClickProject(event.target.value)} >
                                {projects?.map((pro, index) => {
                                    return <option key={index} value={pro.projectId}>{pro.projectName}</option>
                                })}
                            </select>
                        </div>
                        <label className="content-btn-radio-sim" onClick={() => {
                            setLoading(false)
                            setCheckElectricalPower(true)
                            loadListData(param.customerId, projectId, props.typeSystem)
                        }
                        }>
                            <input type="radio" name="radio-content" value={1} className="content-btn-input-sim" defaultChecked />
                            <span className="content-btn-text-sim" style={checkElectricalPower == true ? { backgroundColor: 'var(--ses-orange-100-color)' } : {}} >Điện năng</span>
                        </label>
                        <label className="content-btn-radio-sim" onClick={() => {
                            setLoading(false)
                            setCheckElectricalPower(false)
                            loadListData(param.customerId, projectId, props.typeSystem)
                        }}>
                            <input type="radio" name="radio-content" value={2} className="content-btn-input-sim" />
                            <span className="content-btn-text-sim" style={checkElectricalPower == false ? { backgroundColor: 'var(--ses-orange-100-color)' } : {}}>Tiền Điện</span>
                        </label>
                        <div id='bar' hidden={checkElectricalPower === true}>
                            <input type='checkbox' id='slider' onChange={() => {
                                setDollar(!dollar)
                            }} />
                            <label htmlFor="slider" id='lable-silder' style={dollar === false ? { fontSize: '10px', paddingLeft: '5%', paddingTop: '10%' } : null}>{dollar === true ? "$" : "VNĐ"}</label>
                        </div>
                        <div className="float-right mr-1 btn-add-sim" data-toggle="modal" data-target={"#new-receiver"} onClick={() => newData()} ><i className="fas fa-solid fa-circle-plus fa-3x float-right add-user"></i></div>
                    </div>
                    <div className='content-table'>
                        <table className="table">
                            <thead height="30px">
                                <tr>
                                    <th>Năm</th>
                                    <th>T1</th>
                                    <th>T2</th>
                                    <th>T3</th>
                                    <th>T4</th>
                                    <th>T5</th>
                                    <th>T6</th>
                                    <th>T7</th>
                                    <th>T8</th>
                                    <th>T9</th>
                                    <th>T10</th>
                                    <th>T11</th>
                                    <th>T12</th>
                                    <th width="170px">Ngày cập nhật</th>
                                    <th width="60px"><i className="fa-regular fa-hand"></i></th>
                                </tr>
                            </thead>
                            <tbody style={{ width: '100%' }}>
                                {loading === true ? checkElectricalPower === true ?
                                    listDataEp.map((data, index) => {
                                        return (
                                            <>
                                                {/* <React.Fragment key={index}> */}
                                                <tr key={index} height="25px" id={index}>
                                                    <td style={{ wordWrap: "break-word" }}>
                                                        <input type="number" style={{ cursor: "context-menu" }} defaultValue={data.year} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1 && data.id !== undefined} onChange={(event) => setYear(event.target.value)} />
                                                    </td>
                                                    <td style={{ wordWrap: "break-word" }}>
                                                        <input type="number" style={{ cursor: "context-menu" }} defaultValue={data.jan} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1 && data.id !== undefined} onChange={(event) => setT1(event.target.value)} />
                                                    </td>
                                                    <td style={{ wordWrap: "break-word" }}>
                                                        <input type="number" style={{ cursor: "context-menu" }} defaultValue={data.feb} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1 && data.id !== undefined} onChange={(event) => setT2(event.target.value)} />
                                                    </td>
                                                    <td style={{ wordWrap: "break-word" }}>
                                                        <input type="number" style={{ cursor: "context-menu" }} defaultValue={data.mar} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1 && data.id !== undefined} onChange={(event) => setT3(event.target.value)} />
                                                    </td>
                                                    <td style={{ wordWrap: "break-word" }}>
                                                        <input type="number" style={{ cursor: "context-menu" }} defaultValue={data.apr} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1 && data.id !== undefined} onChange={(event) => setT4(event.target.value)} />
                                                    </td>
                                                    <td style={{ wordWrap: "break-word" }}>
                                                        <input type="number" style={{ cursor: "context-menu" }} defaultValue={data.may} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1 && data.id !== undefined} onChange={(event) => setT5(event.target.value)} />
                                                    </td>
                                                    <td style={{ wordWrap: "break-word" }}>
                                                        <input type="number" style={{ cursor: "context-menu" }} defaultValue={data.jun} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1 && data.id !== undefined} onChange={(event) => setT6(event.target.value)} />
                                                    </td>
                                                    <td style={{ wordWrap: "break-word" }}>
                                                        <input type="number" style={{ cursor: "context-menu" }} defaultValue={data.jul} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1 && data.id !== undefined} onChange={(event) => setT7(event.target.value)} />
                                                    </td>
                                                    <td style={{ wordWrap: "break-word" }}>
                                                        <input type="number" style={{ cursor: "context-menu" }} defaultValue={data.aug} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1 && data.id !== undefined} onChange={(event) => setT8(event.target.value)} />
                                                    </td>
                                                    <td style={{ wordWrap: "break-word" }}>
                                                        <input type="number" style={{ cursor: "context-menu" }} defaultValue={data.sep} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1 && data.id !== undefined} onChange={(event) => setT9(event.target.value)} />
                                                    </td>
                                                    <td style={{ wordWrap: "break-word" }}>
                                                        <input type="number" style={{ cursor: "context-menu" }} defaultValue={data.oct} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1 && data.id !== undefined} onChange={(event) => setT10(event.target.value)} />
                                                    </td>
                                                    <td style={{ wordWrap: "break-word" }}>
                                                        <input type="number" style={{ cursor: "context-menu" }} defaultValue={data.nov} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1 && data.id !== undefined} onChange={(event) => setT11(event.target.value)} />
                                                    </td>
                                                    <td style={{ wordWrap: "break-word" }}>
                                                        <input type="number" style={{ cursor: "context-menu" }} defaultValue={data.dec} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1 && data.id !== undefined} onChange={(event) => setT12(event.target.value)} />
                                                    </td>


                                                    <td className="text-center" style={{ cursor: "context-menu" }}>
                                                        {data.updateDate}
                                                    </td>
                                                    <td className="text-center">
                                                        {btnEdit === false && editIndex == index + 1 ? <a className="button-icon" title="Chỉnh sửa" onClick={() => onClickButonCheck(data.id)}>
                                                            <i className="fa-solid fa-circle-check" style={{ color: "#0A1A5C", fontSize: '18px' }}></i>
                                                        </a> :
                                                            btnAdd === true && data.id === undefined ? < a className="button-icon" title="Chỉnh sửa" onClick={() => onClickButonAdd()}>
                                                                <i className="fa-solid fa-circle-check" style={{ color: "#00CC33", fontSize: '18px' }}></i>
                                                            </a>

                                                                : <a className="button-icon" title="Chỉnh sửa" onClick={() => onClickButonEdit(index + 1)}>
                                                                    <i className="fas fa-edit" style={{ color: "#F37021", fontSize: '18px' }}></i>
                                                                </a>

                                                        }
                                                    </td>
                                                </tr>
                                                {/* </React.Fragment> */}
                                            </>
                                        )
                                    })
                                    :
                                    listDataMoney?.map((data, index) => {
                                        return (
                                            // <React.Fragment key={index}>
                                            <tr key={index} height="25px" id={index}>
                                                <td style={{ wordWrap: "break-word" }}>
                                                    <input type="number" style={{ cursor: "context-menu" }} defaultValue={dollar === true ? data.year : data.year * 24000} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1 && data.id !== undefined} onChange={(event) => setYear(event.target.value)} />
                                                </td>
                                                <td style={{ wordWrap: "break-word" }}>
                                                    <input type="number" style={{ cursor: "context-menu" }} defaultValue={dollar === true ? data.jan : data.jan * 24000} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1 && data.id !== undefined} onChange={(event) => setT1(event.target.value)} />
                                                </td>
                                                <td style={{ wordWrap: "break-word" }}>
                                                    <input type="number" style={{ cursor: "context-menu" }} defaultValue={dollar === true ? data.feb : data.feb * 24000} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1 && data.id !== undefined} onChange={(event) => setT2(event.target.value)} />
                                                </td>
                                                <td style={{ wordWrap: "break-word" }}>
                                                    <input type="number" style={{ cursor: "context-menu" }} defaultValue={dollar === true ? data.mar : data.mar * 24000} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1 && data.id !== undefined} onChange={(event) => setT3(event.target.value)} />
                                                </td>
                                                <td style={{ wordWrap: "break-word" }}>
                                                    <input type="number" style={{ cursor: "context-menu" }} defaultValue={dollar === true ? data.apr : data.apr * 24000} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1 && data.id !== undefined} onChange={(event) => setT4(event.target.value)} />
                                                </td>
                                                <td style={{ wordWrap: "break-word" }}>
                                                    <input type="number" style={{ cursor: "context-menu" }} defaultValue={dollar === true ? data.may : data.may * 24000} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1 && data.id !== undefined} onChange={(event) => setT5(event.target.value)} />
                                                </td>
                                                <td style={{ wordWrap: "break-word" }}>
                                                    <input type="number" style={{ cursor: "context-menu" }} defaultValue={dollar === true ? data.jun : data.jun * 24000} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1 && data.id !== undefined} onChange={(event) => setT6(event.target.value)} />
                                                </td>
                                                <td style={{ wordWrap: "break-word" }}>
                                                    <input type="number" style={{ cursor: "context-menu" }} defaultValue={dollar === true ? data.jul : data.jul * 24000} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1 && data.id !== undefined} onChange={(event) => setT7(event.target.value)} />
                                                </td>
                                                <td style={{ wordWrap: "break-word" }}>
                                                    <input type="number" style={{ cursor: "context-menu" }} defaultValue={dollar === true ? data.aug : data.aug * 24000} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1 && data.id !== undefined} onChange={(event) => setT8(event.target.value)} />
                                                </td>
                                                <td style={{ wordWrap: "break-word" }}>
                                                    <input type="number" style={{ cursor: "context-menu" }} defaultValue={dollar === true ? data.sep : data.sep * 24000} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1 && data.id !== undefined} onChange={(event) => setT9(event.target.value)} />
                                                </td>
                                                <td style={{ wordWrap: "break-word" }}>
                                                    <input type="number" style={{ cursor: "context-menu" }} defaultValue={dollar === true ? data.oct : data.oct * 24000} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1 && data.id !== undefined} onChange={(event) => setT10(event.target.value)} />
                                                </td>
                                                <td style={{ wordWrap: "break-word" }}>
                                                    <input type="number" style={{ cursor: "context-menu" }} defaultValue={dollar === true ? data.nov : data.nov * 24000} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1 && data.id !== undefined} onChange={(event) => setT11(event.target.value)} />
                                                </td>
                                                <td style={{ wordWrap: "break-word" }}>
                                                    <input type="number" style={{ cursor: "context-menu" }} defaultValue={dollar === true ? data.dec : data.dec * 24000} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1 && data.id !== undefined} onChange={(event) => setT12(event.target.value)} />
                                                </td>


                                                <td className="text-center" style={{ cursor: "context-menu" }}>
                                                    {data.updateDate}
                                                </td>
                                                <td className="text-center">
                                                    {btnEdit === false && editIndex == index + 1 ? <a className="button-icon" title="Chỉnh sửa" onClick={() => onClickButonCheck(data.id)}>
                                                        <i className="fa-solid fa-circle-check" style={{ color: "#0A1A5C", fontSize: '18px' }}></i>
                                                    </a> :
                                                        btnAdd === true && data.id === undefined ? < a className="button-icon" title="Chỉnh sửa" onClick={() => onClickButonAdd()}>
                                                            <i className="fa-solid fa-circle-check" style={{ color: "#00CC33", fontSize: '18px' }}></i>
                                                        </a>

                                                            : <a className="button-icon" title="Chỉnh sửa" onClick={() => onClickButonEdit(index + 1)}>
                                                                <i className="fas fa-edit" style={{ color: "#F37021", fontSize: '18px' }}></i>
                                                            </a>

                                                    }
                                                </td>
                                            </tr>
                                            // </React.Fragment>
                                        )
                                    })
                                    :
                                    <tr>
                                        <td>
                                            <div style={{ marginLeft: '750px' }}>
                                                <div className="loading" style={{ marginTop: "10%", marginLeft: "25%" }}>
                                                    <img height="60px" src="/resources/image/loading2.gif" alt="loading" />
                                                </div>
                                            </div>
                                        </td>
                                    </tr>
                                }
                            </tbody>
                        </table>
                    </div>
                </div >
            </div >
        </div >
    </>);
};


export default DataSimulation;