import React, { useEffect, useState } from "react";
import { useRef } from "react";
import { useParams } from "react-router-dom";
import "./index.css";
import { useTranslation } from "react-i18next";
import moment from "moment";
import { locale, addLocale } from 'primereact/api';
import "react-toastify/dist/ReactToastify.css";
import SelectDeviceComponent from "../../home-pages/select-device-component/select-device-component";
import ProductionService from "../../../../services/ProductionService";
import ProjectService from "../../../../services/ProjectService";
import { NotficationSuscces, NotficationWarning } from "../notification/notification";
import DeviceService from "../../../../services/DeviceService";
import ManufactureService from "../../../../services/ManufactureService";
import SettingShiftService from "../../../../services/SettingShiftService";
import ReactModal from "react-modal";


const AddProductionStep = (props) => {
    const [projectId, setProjectId] = useState();
    const [projects, setProjects] = useState([]);
    const [productions, setProductions] = useState([]);
    const [productionSteps, setProductionSteps] = useState([]);
    const [productionStepId, setProductionStepId] = useState([]);
    const [checkDate, setCheckDate] = useState();
    const [selectedDevices, setSelectedDevices] = useState([]);
    const [deviceIds, setDeviceIds] = useState("");
    const [productionId, setProductionId] = useState();
    const [systemTypeId, setSystemTypeId] = useState(1);
    const [type, setType] = useState(false);
    const { t } = useTranslation();
    addLocale('vn', {
        monthNames: ['Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4', 'Tháng 5', 'Tháng 6', 'Tháng 7', 'Tháng 8', 'Tháng 9', 'Tháng 10', 'Tháng 11', 'Tháng 12'],
        monthNamesShort: ['T1', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'T8', 'T9', 'T10', 'T11', 'T12',]
    });
    locale('vn');

    const param = useParams();
    const callbackFunction = (chillData) => {
        setDeviceIds(chillData);
        if (chillData.length > 0) {
            getDeviceByIds(convertArrayId(chillData))
        } else {
            setSelectedDevices(chillData)
        }
    }
    const convertArrayId = (ids) => {
        let strId = "";
        if (ids.length <= 0) {
            NotficationWarning(t('content.home_page.manufacture.noOptions'));
        } else {
            for (let i = 0; i < ids.length; i++) {
                if (i == 0) {
                    strId += ids[i];
                } else {
                    strId += "," + ids[i];
                }
            }
        }
        return strId;
    }

    const sendData = (status) => {
        props.parentCallback(status)
    }
    const getDeviceByIds = async (ids) => {
        let res = await DeviceService.listDeviceByIds(ids);
        if (res.status == 200) {
            setSelectedDevices(res.data)
        }
    }
    const closeDevice = async (dv) => {
        setSelectedDevices(selectedDevices.filter((d) => d.deviceId != dv.deviceId));
        setDeviceIds(deviceIds.filter((d) => d != dv.deviceId));
    }

    const checkMinDate = () => {
        const date2 = new Date(moment(new Date).format("YYYY-MM-DD"));
        var date = new Date(date2);
        date.setDate(date.getDate() - 31);
        setCheckDate(date);
    }

    const getProjects = async () => {
        let res = await ProjectService.getProjectByCustomerId(param.customerId, null);
        if (res.status == 200) {
            setProjects(res.data);
            if (res.data[0] != undefined) {
                setProjectId(res.data[0].projectId);
                listProduction(param.customerId, res.data[0].projectId)
            }

        }
    }
    const getProject = async (id) => {
        let res = await ProjectService.getProject(id);
        if (res.status === 200) {
            listProduction(param.customerId, param.projectId)
            setProjects([res.data]);
            setProjectId(res.data.projectId)
        }
    }
    const listProduction = async (customerId, projectId) => {
        let res = await ProductionService.getListProduction(customerId, projectId);
        if (res.data.length > 0) {
            setProductions(res.data);
            listProductionStep(param.customerId, res.data[0].productionId, projectId)
            setProductionId(res.data[0].productionId);
        } else {
            setProductions([])
            setProductionSteps([])
        }

    }

    const listProductionStep = async (customerId, productionId, projectId) => {
        let res = await ProductionService.getListProductionStep(customerId, productionId, projectId);
        if (res.data.length > 0) {
            if (res.status == 200) {
                setProductionSteps(res.data);
                setProductionStepId(res.data[0].productionStepId);
            }
        } else {
            setProductionSteps([])
        }

    }

    const changeProject = (id) => {
        setProjectId(id);
        setSelectedDevices([])
        listProduction(param.customerId, id);
    }
    const changeSystemType = (id) => {
        setSystemTypeId(id);
    }


    const changeProduction = (id) => {
        setProductionId(id);
        listProductionStep(param.customerId, id, projectId)
    }
    const changeProductionStep = (id) => {
        setProductionStepId(id);
    }


    useEffect(() => {
        document.title = "Theo dõi sản xuất";
        if (param.projectId != null) {
            getProject(param.projectId)
        } else {
            getProjects();
        }
        if (checkDate == undefined) {
            checkMinDate()
        }
    }, [param.customerId, param.projectId]);

    const renderSelectedDevices = () => {
        if (selectedDevices.length > 0) {
            return (
                selectedDevices.map((dv, index) => {
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
                            <i
                                id="ic-eye"
                                style={{ fontWeight: 'bold', marginLeft: '5%', fontSize: '14px', }}
                                onClick={() => closeDevice(dv)}
                                className="fa-solid fa-circle-xmark"></i>
                        </div>
                    )
                })
            )
        }
    }


    // Thêm sản phẩm và thêm công đoạn gia công
    const [isDrawerOpen, setIsDrawerOpen] = useState(false);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [typeAdd, setTypeAdd] = useState(0);

    const [productionAddName, setProductionAddName] = useState("");
    const [productionAddUnit, setProductionAddUnit] = useState("");
    const [productionStepAddName, setProductionStepAddName] = useState("");
    const closeModal = () => {
        clearForm();
        setIsModalOpen(false);
        setType(!type)
    };

    const clearForm = () => {
        setProductionAddName("");
        setProductionAddUnit("");
        setProductionStepAddName("");
    }

    const onSaveProduction = async () => {
        let data = {
            projectId: projectId,
            productionName: productionAddName,
            unit: productionAddUnit
        }
        let res = await ProductionService.addProduction(param.customerId, data);
        if (res.status == 200) {
            listProduction(param.customerId, projectId);
            NotficationSuscces("Thêm thành công");
            closeModal();
        }
    }
    const onSaveProductionStep = async () => {
        let data = {
            productionId: productionId,
            productionStepId: null,
            productionStepName: productionStepAddName,
        }
        if (productionStepAddName == "") {
            NotficationWarning("Chưa nhập tên công đoạn!");
            return
        }
        let res = await ManufactureService.addManufacturesAndProdutionStep(param.customerId, data);
        listProductionStep(param.customerId, productionId, projectId);
        if (res.status == 200) {
            NotficationSuscces("Thêm thành công");
            closeModal();
        }
    }


    const onSaveProductionStepAndDevice = async () => {
        let data = {};
        if (typeAdd == 0) {
            data = {
                productionId: productionId,
                productionStepId: productionStepId,
                productionStepName: null,
                deviceIds: convertArrayId(deviceIds)
            }
        } else {
            data = {
                productionId: productionId,
                productionStepId: null,
                productionStepName: productionStepAddName,
                deviceIds: convertArrayId(deviceIds)
            }
        }
        if (typeAdd == 1) {
            if (productionStepAddName == "") {
                NotficationWarning("Chưa nhập tên công đoạn!");
                return
            }
        }
        if (deviceIds.length <= 0) {
            NotficationWarning(t('content.home_page.manufacture.noOptions'));
            return
        }
        let res = await ManufactureService.addManufacturesAndProdutionStep(param.customerId, data);
        listProductionStep(param.customerId, productionId, projectId);
        setDeviceIds([]);
        if (res.status == 200) {
            sendData(200);
            NotficationSuscces("Thêm thành công");
            closeModal();
        }
    }
    return (<>
        <div className="row" style={{ width: '100%' }}>
            <div className="col-6" style={{ height: '950px' }}>
                <div className="manufacture-main-option-content" >
                    <div className="">
                        <div className="manufacture-main-option-detail-title">
                            <i className="fa-solid fa-tag"></i>
                            <span className="">{t('content.project')}</span>
                        </div>
                        <select className="mt-1 manufacture-main-option-detail-select" onChange={(event) => changeProject(event.target.value)} >
                            {projects.length > 0 ?
                                projects.map((pro, index) => { return <option key={index} value={pro.projectId}>{pro.projectName}</option> }
                                )
                                : <option>{t('content.no_data')}</option>}
                        </select>
                    </div>
                    <div className="mt-2">
                        <div className="manufacture-main-option-detail-title" style={{ width: '40%' }}>
                            <i className="fa-solid fa-tag"></i>
                            <span className="">{t('content.system')}</span>
                        </div>
                        <select className="mt-1 manufacture-main-option-detail-select" onChange={(event) => changeSystemType(event.target.value)} >
                            <option value={1}>{t('content.home_page.load')}</option>
                            <option value={2}>{t('content.home_page.solar')}</option>
                            <option value={3}>{t('content.home_page.wind')}</option>
                            <option value={4}>{t('content.home_page.battery')}</option>
                            <option value={5}>{t('content.home_page.grid')}</option>
                        </select>
                    </div>
                    <div className="mt-3">
                        <div className="d-flex justify-content-between">
                            <div className="manufacture-main-option-detail-title" style={{ width: '80%' }}>
                                <i className="fa-solid fa-tag"></i>
                                <span className="">{t('content.product_name')}</span>
                            </div>
                            <div className="addButtonProduction" onClick={() => {
                                setIsModalOpen(true);
                                setTypeAdd(0);
                                setType(false)
                            }}>
                                <i className="fa-solid fa-plus" style={{ fontWeight: 'bold', color: 'white' }}></i>
                            </div>
                        </div>
                        <select disabled={productions.length <= 0} className="mt-1 manufacture-main-option-detail-select" onChange={(event) => changeProduction(event.target.value)}>
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
                            <div className="addButtonProduction" onClick={() => {
                                setTypeAdd(0)
                                setIsModalOpen(true)
                                setType(true)
                            }}

                            // data-toggle="collapse" href="#collapseExample" role="button" aria-expanded="false" aria-controls="collapseExample"
                            >
                                <i className="fa-solid fa-plus" style={{ fontWeight: 'bold', color: 'white' }}></i>
                            </div>
                            {/* {typeAdd == 0 ?
                                <div className="addButtonProduction" onClick={() => {
                                    setTypeAdd(1)
                                    setIsModalOpen(true)
                                    setType(!type)
                                }}

                                    data-toggle="collapse" href="#collapseExample" role="button" aria-expanded="false" aria-controls="collapseExample"
                                >
                                    <i className="fa-solid fa-plus" style={{ fontWeight: 'bold', color: 'white' }}></i>
                                </div>
                                :
                                <div className="addButtonProduction" onClick={() => setTypeAdd(0)}
                                    data-toggle="collapse" href="#collapseExample" role="button" aria-expanded="false" aria-controls="collapseExample"
                                >
                                    <i className="fa-solid fa-xmark" style={{ fontWeight: 'bold', color: 'white', fontSize: '15px' }}></i>
                                </div>
                            } */}
                        </div>
                        <select disabled={productionSteps.length <= 0 || typeAdd == 1} className="mt-1 manufacture-main-option-detail-select" onChange={(e) => setProductionStepId(e.target.value)} >
                            {productionSteps.length > 0 ?
                                productionSteps.map((pro, index) => {
                                    return <option key={index} value={pro.productionStepId}>{pro.productionStepName}</option>
                                }
                                )
                                : <option>Không công đoạn sản xuất</option>}
                        </select>
                        <div className="collapse" id="collapseExample">
                            <div className="card card-body">
                                <div className="text-center">
                                    <input type="text" className="form-control" placeholder="Nhập tên công đoạn sản xuất" value={productionStepAddName} onChange={(e) => setProductionStepAddName(e.target.value)} />
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="mt-2" style={{ height: '250px' }}>
                        <div className="d-flex justify-content-between ">
                            <div className="manufacture-main-option-detail-title" style={{ width: '55%' }}>
                                <i className="fa-solid fa-tag"></i>
                                <span className="">{t('content.home_page.manufacture.selectSomeItems')}</span>
                            </div>
                            <div className="">
                                <SelectDeviceComponent
                                    style={{
                                        marginLeft: '2%',
                                        height: '30px', width: '30px',
                                        borderRadius: '15px', paddingLeft: '0',
                                        paddingTop: '0', paddingRight: '0',
                                        paddingBottom: '0', fontSize: '20px',
                                    }}
                                    projectId={projectId}
                                    systemTypeId={systemTypeId}
                                    parentCallback={callbackFunction}
                                    titleName={"+"} />
                            </div>
                        </div>
                        <div className="manufacture-main-option-detail-selected">
                            <div style={{ overflowY: 'scroll' }}>
                                {renderSelectedDevices()}
                            </div>
                        </div>
                        <div className="mt-2 text-center">
                            <button className="btn btn-primary" disabled={selectedDevices.length <= 0} style={{ width: '50%' }} onClick={() => onSaveProductionStepAndDevice()} >{t('content.save')}</button>
                        </div>
                    </div>
                </div>
            </div>
            <div className="col-6">
                <div className="manufacture-main-option-content" >

                </div>
            </div>
            <ReactModal
                isOpen={isModalOpen}
                onRequestClose={closeModal}
                contentLabel="Modal 1"
                style={{
                    content: {
                        width: "20%", // Kích thước chiều rộng của modal (có thể điều chỉnh)
                        height: type == false ? "200px" : "155px", // Kích thước chiều cao của modal (có thể điều chỉnh)
                        margin: "auto", // Căn giữa modal
                        marginTop: "5%",
                        // paddingTop: typeAdd == 1 ? "1%" : "1.5%",
                    },
                }}
            >
                {type == false ?
                    <div className="text-center">
                        <p style={{ fontFamily: 'Verdana, Geneva, Tahoma, sans-serif', fontSize: '14px', fontWeight: 'bold', textAlign: 'left' }}>Thêm sản phẩm</p>
                        <input type="text" className="form-control" placeholder="Nhập tên sản phẩm" value={productionAddName} onChange={(e) => setProductionAddName(e.target.value)} />
                        <input type="text" className="form-control mt-2" placeholder="Nhập đơn vị" value={productionAddUnit} onChange={(e) => setProductionAddUnit(e.target.value)} />
                        <button className="btn btn-primary mt-2" onClick={() => onSaveProduction()}>Lưu</button>
                        <button className="btn btn-secondary mt-2" onClick={() => closeModal()} style={{ marginLeft: '2%' }}>{t('content.close')}</button>
                    </div> :
                    <div className="text-center">
                        <p style={{ fontFamily: 'Verdana, Geneva, Tahoma, sans-serif', fontSize: '14px', fontWeight: 'bold', textAlign: 'left' }}>Thêm công đoạn sản xuất</p>
                        <input type="text" className="form-control" placeholder="Nhập công đoạn sản xuất" value={productionStepAddName} onChange={(e) => setProductionStepAddName(e.target.value)} />
                        <button className="btn btn-primary mt-2" onClick={() => onSaveProductionStep()}>Lưu</button>
                        <button className="btn btn-secondary mt-2" onClick={() => closeModal()} style={{ marginLeft: '2%' }}>{t('content.close')}</button>
                    </div>
                }
            </ReactModal>
        </div>

    </>)
}

export default AddProductionStep;