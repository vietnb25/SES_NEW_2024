import React, { useEffect, useState } from "react";
import { useRef } from "react";
import { useParams } from "react-router-dom";
import "./index.css";
import { ToastContainer, toast } from "react-toastify";
import { useTranslation } from "react-i18next";
import moment from "moment";
import { locale, addLocale } from 'primereact/api';
import "react-toastify/dist/ReactToastify.css";
import AccessDenied from "../../access-denied/AccessDenied";
import { Calendar } from "primereact/calendar";
import ProductionService from "../../../../services/ProductionService";
import ProjectService from "../../../../services/ProjectService";
import { NotficationSuscces, NotficationWarning } from "../notification/notification";
import DeviceService from "../../../../services/DeviceService";
import ManufactureService from "../../../../services/ManufactureService";
import SettingShiftService from "../../../../services/SettingShiftService";
import ManufactureTableData from "./manufacture-table-data";
import Drawer from "./drawer-add/drawer";
import ReactModal from "react-modal";
import AddProductionStep from "./add-production";
import UserService from "../../../../services/UserService";

const Manufacture2 = () => {
    const [projectId, setProjectId] = useState();
    const [projects, setProjects] = useState([]);
    const [loading, setLoading] = useState(false);
    const [productions, setProductions] = useState([]);
    const [productionSteps, setProductionSteps] = useState([]);
    const [fromDate, setFromDate] = useState(new Date());
    const [toDate, setToDate] = useState(new Date());
    const [checkDate, setCheckDate] = useState();
    const [selectedDevices, setSelectedDevices] = useState([]);
    const [deviceIds, setDeviceIds] = useState("");
    const [productionId, setProductionId] = useState();
    const [productionStepId, setProductionStepId] = useState();
    const [settingShifts, setSettingShifts] = useState([]);
    const [manufactureShifts, setManufactureShifts] = useState([]);
    const [dates, setDates] = useState([]);
    const [dataTab1, setDataTab1] = useState([]);
    const [type, setType] = useState(false);
    const [add, setAdd] = useState(false);
    const [manufature, setManufature] = useState({});

    addLocale('vn', {
        monthNames: ['Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4', 'Tháng 5', 'Tháng 6', 'Tháng 7', 'Tháng 8', 'Tháng 9', 'Tháng 10', 'Tháng 11', 'Tháng 12'],
        monthNamesShort: ['T1', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'T8', 'T9', 'T10', 'T11', 'T12',]
    });
    locale('vn');

    const param = useParams();
    const { t } = useTranslation();
    const [accessDenied, setAccessDenied] = useState(false)
    const [systemTypeId, setSystemTypeId] = useState(1)


    const getStatusAddManufacture = (status) => {
        if (status == 200) {
            getListManufacture(param.customerId, projectId, productionId, productionStepId);
        }
    }

    const getProjects = async () => {
        let res = await ProjectService.getProjectByCustomerId(param.customerId, null);
        if (res.status == 200) {
            setProjects(res.data);
           if(res.data[0] != undefined) {
            setProjectId(res.data[0].projectId);
            listProduction(param.customerId, res.data[0].projectId)
            getSettingShiftByPoject(res.data[0].projectId);
            getListManufacture(param.customerId, res.data[0].projectId);
           }
        }
        let response = await UserService.getUserByUsername();
        if (response.status === 200) {
            const userData = response.data;
            setSystemTypeId(userData.prioritySystem);
        }
    }
    const getProject = async (id) => {
        let res = await ProjectService.getProject(id);
        if (res.status === 200) {
            listProduction(param.customerId, param.projectId)
            setProjects([res.data]);
            setProjectId(res.data.projectId)
            getSettingShiftByPoject(res.data.projectId);
            getListManufacture(param.customerId, res.data.projectId);
        }
    }
    const listProduction = async (customerId, projectId) => {
        let res = await ProductionService.getListProduction(customerId, projectId);
        if (res.data.length > 0) {
            setProductions(res.data);
            // listProductionStep(param.customerId, res.data[0].productionId, projectId)
            // setProductionId(res.data[0].productionId);
        } else {
            setProductions([])
        }
        setProductionSteps([])

    }

    const listProductionStep = async (customerId, productionId, projectId) => {
        let res = await ProductionService.getListProductionStep(customerId, productionId, projectId);
        if (res.data.length > 0) {
            if (res.status == 200) {
                setProductionSteps(res.data);
                // setProductionStepId(res.data[0].productionStepId);
            }
        } else {
            setProductionSteps([])
        }

    }

    const changeProject = (id) => {
        setProjectId(id);
        setSelectedDevices([])
        listProduction(param.customerId, id);
        getSettingShiftByPoject(id);
        getListManufacture(param.customerId, id)
    }
    const changeProduction = (id) => {
        setProductionId(id);
        listProductionStep(param.customerId, id, projectId)
        if (id == 0) {
            getListManufacture(param.customerId, projectId)
        } else {
            getListManufacture(param.customerId, projectId, id)
        }
    }
    const changeProductionStep = (id) => {
        setProductionStepId(id);
        if (id == 0) {
            getListManufacture(param.customerId, projectId, productionId)
        } else {
            getListManufacture(param.customerId, projectId, productionId, id)
        }

    }


    const clickDataTable = (item) => {
        let a = document.getElementById("manufacture-main-show-content-tab-1")
        let b = document.getElementById("manufacture-main-show-content-tab-2")
        a.checked = false;
        b.checked = true;
        setType(!type)
        setManufature(item);
    }
    useEffect(() => {
        document.title = "Theo dõi sản xuất";
        if (param.projectId != null) {
            getProject(param.projectId)
        } else {
            getProjects();
        }
    }, [param.customerId, param.projectId]);

    const getListManufacture = async (customer, project, production, productionStep) => {
        let res = await ManufactureService.getListManufacturesShift(customer, project, production != 0 ? production : null, productionStep != 0 ? productionStep : null);
        if (res.status == 200) {
            setDataTab1(res.data)
        }
    }


    const getSettingShiftByPoject = async (projectId) => {
        let res = await SettingShiftService.listSetting(projectId);
        if (res.status == 200) {
            setSettingShifts(res.data)
        }
    }



    const getDeviceName = (devices) => {
        let name = "";
        for (let i = 0; i < devices.length; i++) {
            if (i == 0) {
                name = devices[i].deviceName
            } else {
                name = name + ",  " + devices[i].deviceName
            }
        }
        return name;
    }

    // Thêm sản phẩm và thêm công đoạn gia công
    const [isDrawerOpen, setIsDrawerOpen] = useState(false);
    const handleToggleDrawer = () => {
        setIsDrawerOpen(!isDrawerOpen);
    };

    const renderDevices = (arr) => {
        if (arr.length > 0) {
            return (
                arr.map((dv, index) => {
                    return (
                        <div id="ic-eyes" className="mt-2" key={index} >
                            <span className="span3cham"
                                style={{
                                    marginLeft: '2%',
                                    fontSize: '12px',
                                    fontWeight: 'bold',
                                    fontFamily: 'Verdana, Geneva, Tahoma, sans-serif'
                                }}>
                                {dv.deviceName}
                            </span>
                            {/* <i
                                id="ic-eye"
                                style={{ fontWeight: 'bold', marginLeft: '5%', fontSize: '14px', }}
                                onClick={() => closeDevice(dv)}
                                className="fa-solid fa-circle-xmark"></i> */}
                        </div>
                    )
                })
            )
        }
    }

    const openModalDelete = (id) => {
        setIdDelete(id);
        setIsModalOpen(true);
    }

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [idDelete, setIdDelete] = useState(0);
    const closeModal = () => {
        setIsModalOpen(false);
    };

    const deleteManufacture = async (id) => {
        let res = await ManufactureService.deleteManufacture(param.customerId, id);
        if (res.status == 200) {
            NotficationSuscces("Xóa thành công!");
        }
        getListManufacture(param.customerId, projectId, productionId, productionStepId);
        closeModal();
    }

    return (
        <>
            {accessDenied ?
                <AccessDenied></AccessDenied>
                :
                <>
                    <ToastContainer></ToastContainer>
                   
                    <div className="manufacture-main" >
                        <div className="manufacture-main-option">
                            <div className="manufacture-main-option-content" >
                                <div className="">
                                    <div className="manufacture-main-option-detail-title">
                                        <i className="fa-solid fa-tag"></i>
                                        <span className="">{t('content.project')}</span>
                                    </div>
                                    <select disabled={param.projectId != null || type === true} className="mt-1 manufacture-main-option-detail-select" onChange={(event) => changeProject(event.target.value)} >
                                        {projects.length > 0 ?
                                            projects.map((pro, index) => { return <option key={index} value={pro.projectId}>{pro.projectName}</option> }
                                            )
                                            : <option>{t('content.no_data')}</option>}
                                    </select>
                                </div>
                                <div className="mt-3">
                                    <div className="d-flex justify-content-between">
                                        <div className="manufacture-main-option-detail-title" style={{ width: '55%' }}>
                                            <i className="fa-solid fa-tag"></i>
                                            <span className="">{t('content.product_name')}</span>
                                        </div>

                                    </div>
                                    <select disabled={productions.length <= 0 || type === true} className="mt-1 manufacture-main-option-detail-select" onChange={(event) => changeProduction(event.target.value)}>
                                        {type === false ? <option value={0}>{t('content.home_page.manufacture.choose_product')}</option> : <option value={0}>{manufature.productionName}</option>}
                                        {productions.length > 0 ?
                                            productions.map((pro, index) => { return <option key={index} value={pro.productionId}>{pro.productionName}</option> }
                                            )
                                            : <option>{t('content.no_data')}</option>}
                                    </select>
                                </div>
                                <div className="mt-3">
                                    <div className="d-flex justify-content-between">
                                        <div className="manufacture-main-option-detail-title" style={{ width: '80%' }}>
                                            <i className="fa-solid fa-tag"></i>
                                            <span className="">{t('content.production_step')}</span>
                                        </div>

                                    </div>
                                    <select disabled={productionSteps.length <= 0 || productions.length <= 0 || type === true}
                                        onChange={(e) => changeProductionStep(e.target.value)}
                                        className="mt-1 manufacture-main-option-detail-select" >
                                        {type === false ? <option value={0}>{t('content.home_page.manufacture.choose_production_step')}</option> : <option value={0}>{manufature.productionStepName}</option>}

                                        {productionSteps.length > 0 ?
                                            productionSteps.map((pro, index) => { return <option key={index} value={pro.productionStepId}>{pro.productionStepName}</option> }
                                            )
                                            : <option>{t('content.no_data')}</option>}
                                    </select>
                                </div>
                                <div className="mt-3" hidden={type === false}>
                                    <div className="manufacture-main-option-detail-title" style={{ width: '40%' }}>
                                        <i className="fa-solid fa-tag"></i>
                                        <span className="">{t('content.device')}</span>
                                    </div>
                                    <div className="manufacture-main-option-detail-selected">
                                        <div style={{ overflowY: 'scroll', height: '250px' }} >
                                            {renderDevices((manufature.devices != undefined ? manufature.devices : []))}
                                        </div>
                                    </div>
                                </div>
                            </div>

                        </div>
                        <div className="manufacture-main-show">
                            <div className="manufacture-main-show-content">
                                <div className="manufacture-main-show-content-container">
                                    <input type="radio" name="tabs" id="manufacture-main-show-content-tab-1" defaultChecked={true} />
                                    <label htmlFor="manufacture-main-show-content-tab-1" onClick={() => {
                                        setType(false);
                                    }}>{t('content.list')}</label>
                                    <input type="radio" name="tabs" id="manufacture-main-show-content-tab-2" defaultChecked={false} />
                                    <label htmlFor="manufacture-main-show-content-tab-2" onClick={() => {
                                        setType(true);
                                    }}>{t('content.data')}</label>

                                    <div hidden={type === true} className="float-right mr-4" onClick={() => {
                                        setAdd(!add);
                                        handleToggleDrawer();
                                    }}>
                                        {add == true ?
                                            <i className="fa-solid fa-circle-xmark fa-3x float-right add-user"></i> : <i className="fas fa-solid fa-circle-plus fa-3x float-right add-user"></i>
                                        }
                                    </div>
                                    <div className="manufacture-main-show-content-tabs" >
                                        <div hidden={type == true}>
                                            <table className="table-list-manu mt-2" width={"100%"}  >
                                                <thead >
                                                    <tr height="50px">
                                                        <th width="100px">{t('content.no')}</th>
                                                        <th width="300px">
                                                            {t('content.project')}
                                                        </th>
                                                        <th width="400">
                                                            {t('content.product_name')}
                                                        </th >
                                                        <th width="250PX">
                                                            {t('content.unit')}
                                                        </th>
                                                        <th width="300PX">
                                                            {t('content.production_step')}
                                                        </th>
                                                        <th width="300px">
                                                            {t('content.device')}
                                                        </th>
                                                        {/* <th width="300px">
                                                            NGÀY CẬP NHẬT
                                                        </th> */}
                                                        <th width="200PX">
                                                            ####
                                                        </th>
                                                    </tr>
                                                </thead>
                                                <tbody className="">
                                                    {dataTab1.length > 0 ?
                                                        dataTab1.map((item, index) => {
                                                            return (
                                                                <tr key={index + "keytab1"} height="40px" className="tr-hover" >
                                                                    <td width={'50px'} onClick={() => clickDataTable(item)}>{index + 1}</td>
                                                                    <td width={'200px'} onClick={() => clickDataTable(item)}>{item.projectName}</td>
                                                                    <td width={'200px'} onClick={() => clickDataTable(item)}>{item.productionName}</td>
                                                                    <td width={'200px'} onClick={() => clickDataTable(item)}>{item.unit}</td>
                                                                    <td width={'200px'} onClick={() => clickDataTable(item)}>{item.productionStepName}</td>
                                                                    <td width={'200px'} onClick={() => clickDataTable(item)}>{item.devices.length > 0 ? getDeviceName(item.devices) : 'không có điểm đo'}</td>
                                                                    {/* <td width={'200px'}>22-12-2023</td> */}
                                                                    <td width={'200px'}>
                                                                        <a className="button-icon text-left ml-2" data-toggle="modal" data-target={`#my-modal-` + (index + 1)}>
                                                                            <i className="fas fa-trash" style={{ color: "#ff0000" }}
                                                                                onClick={(e) => {
                                                                                    openModalDelete(item.id)
                                                                                }}></i>
                                                                        </a>
                                                                    </td>
                                                                </tr>
                                                            )
                                                        })
                                                        :
                                                        <tr height="40px">
                                                            <td colSpan={7} width={'50px'}>{t('content.no_data')}</td>
                                                        </tr>
                                                    }
                                                </tbody>

                                            </table>
                                        </div>
                                        <div style={{ width: '1250px' }} hidden={type == false} >
                                            <ManufactureTableData
                                                settingShift={settingShifts}
                                                manufature={manufature}
                                            />
                                        </div>

                                    </div>
                                    <Drawer isOpen={isDrawerOpen} onClose={() => {
                                        setIsDrawerOpen(false);
                                        setAdd(!add);
                                    }}
                                        style={{ right: '-400px' }}
                                    >
                                        <AddProductionStep parentCallback={getStatusAddManufacture} systemTypeId={systemTypeId} />
                                    </Drawer>

                                </div>

                            </div>
                        </div>
                        <ReactModal
                            isOpen={isModalOpen}
                            onRequestClose={closeModal}
                            contentLabel="Modal 1"
                            style={{
                                content: {
                                    width: "20%", // Kích thước chiều rộng của modal (có thể điều chỉnh)
                                    height: "20%", // Kích thước chiều cao của modal (có thể điều chỉnh)
                                    margin: "auto", // Căn giữa modal
                                    marginTop: "5%",

                                },
                            }}
                        >
                            <div>
                                <p style={{ fontSize: '16px', fontWeight: 'bold' }}> {t('content.delete_confirm')}</p>
                            </div>

                            <div className="text-center mt-4">
                                <button className="btn btn-primary mt-2" onClick={() => deleteManufacture(idDelete)}>{t('content.accept')}</button>
                                <button className="btn btn-secondary mt-2" onClick={() => closeModal()} style={{ marginLeft: '2%' }}>{t('content.cancel')}</button>
                            </div>
                        </ReactModal>
                    </div>

                </>}
        </>

    )
}

export default Manufacture2;