import { useEffect, useState } from "react";
import { useParams } from "react-router-dom/cjs/react-router-dom.min"
import ProductionService from "../../../../services/ProductionService";
import SelectDevice from "./select-device-component";

const ModalAddProductionStep = (props) => {
    const [productions, setProductions] = useState([]);
    const [productionSteps, setProductionSteps] = useState([]);
    const [selectedDevices, setSelectedDevices] = useState([]);
    const param = useParams();
    const listProduction = async (customerId, projectId) => {
        let res = await ProductionService.getListProduction(customerId, projectId);
        if (res.data.length > 0) {
            setProductions(res.data);
            listProductionStep(param.customerId, res.data[0].productionId, projectId)
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
            }
        } else {
            setProductionSteps([])
        }
    }
    const changeProduction = (id) => {
        listProductionStep(param.customerId, id, props.projectId)
    }
    const changeProductionStep = (id) => {

    }
    const closeDevice = async (dv) => {
        setSelectedDevices(selectedDevices.filter((d) => d.deviceId != dv.deviceId));
    }
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
    useEffect(() => {
        listProduction(param.customerId, props.projectId)
    }, [props.projectId])
    return (<>
        <div className="" style={{ width: '100%', height: '100%', backgroundColor: 'white', border: '2px #1B3281 solid', borderRadius: '5px', boxShadow: "rgba(0, 0, 0, 0.35) 0px 5px 15px" }}>
            <div className="row">
                <div className="col-6" style={{paddingLeft: '3%'}}>
                    <div className="mt-2">
                        <div className="d-flex">
                            <div className="manufacture-main-option-detail-title" style={{ width: '50%', height: '30px' }}>
                                <i className="fa-solid fa-tag"></i>
                                <span className="">Tên sản phẩm</span>
                            </div>
                            <div className="addButtonProduction">
                                <i className="fa-solid fa-plus" style={{ color: 'white' }}></i>
                            </div>
                        </div>
                        <select disabled={productions.length <= 0} style={{ width: '70%' }} className="mt-1 manufacture-main-option-detail-select" onChange={(event) => changeProduction(event.target.value)}>
                            {productions.length > 0 ?
                                productions.map((pro, index) => { return <option key={index} value={pro.productionId}>{pro.productionName}</option> }
                                )
                                : <option>Không có sản phẩm</option>}
                        </select>
                    </div>
                    <div className="mt-3">
                        <div className="d-flex">
                            <div className="manufacture-main-option-detail-title" style={{ width: '50%', height: '30px' }}>
                                <i className="fa-solid fa-tag"></i>
                                <span className="">Tên công đoạn sản xuất</span>
                            </div>
                            <div className="addButtonProduction">
                                <i className="fa-solid fa-plus" style={{ color: 'white' }}></i>
                            </div>
                        </div>
                        <select disabled={productionSteps.length <= 0 || productionSteps.length <= 0} style={{ width: '70%' }} className="mt-1 manufacture-main-option-detail-select" >
                            {productionSteps.length > 0 ?
                                productionSteps.map((pro, index) => { return <option key={index} value={pro.productionStepId}>{pro.productionStepName}</option> }
                                )
                                : <option>Không công đoạn sản xuất</option>}
                        </select>
                    </div>
                </div>
                <div className="col-6">
                    kienamsjdf
                </div>
            </div>
        </div>
    </>)
}

export default ModalAddProductionStep;