
import React, { useState } from 'react';
import "../select-device-sankey/select-device-sankey.css"
import { useParams } from 'react-router-dom/cjs/react-router-dom.min';
import DeviceTypeService from '../../../../../services/DeviceTypeService';
import ObjectTypeService from '../../../../../services/ObjectTypeService';
import AreaService from '../../../../../services/AreaService';
import DeviceService from '../../../../../services/DeviceService';
import LoadTypeService from '../../../../../services/LoadTypeService';
import ObjectService from '../../../../../services/ObjectService';
import { t } from 'i18next';

/**
 * Component hiển thị modal
 * cho phép chọn điểm đo và trả về 1 mảng chứa các id của điểm đo
 * Các props cần truyền vào:
 *  projectId: Các dự án thuộc customer -> Bắt buộc phải truyền props
 *  systemTypeId: id hệ thống bao gồm LOAD, GRID, SOLAR.... -> Bắt buộc phải truyền props
 *  limit: Giới hạn điểm đo chọn
 *  titleName: Tên Button hiển thị modal
 *  className: className để chỉnh các thuộc tính của button hiển thị modall
 *  style: chỉnh css inline
 *  Để lấy giá trị trả về thì s(ử dụng: parentCallback  
 *  VD: parentCallback(callbackFunction). Viết 1 hàm callbackFunction trên Component có chứa Component chọn điểm đo
 * const callbackFunction = (childData) => {console.log(childData))} 
 *  
 */

function SelectDeviceSankey(props) {
    const [checkedType, setCheckedType] = useState(1);
    const [oldDevices, setOldDevices] = useState([]);
    const [oldAreas, setOldAreas] = useState([]);
    const [devices, setDevices] = useState([]);
    const [devicesByArea, setDevicesByArea] = useState([]);
    const [devicesByObjectType, setDevicesByObjectType] = useState([]);
    const [checkedHiden, setCheckedHiden] = useState(0)
    const [selectedDevices, setSelectedDevices] = useState([]);
    const [areas, setAreas] = useState([]);
    const [objectTypes, setObjectTypes] = useState([]);
    const [oldObjectTypes, setOldObjectTypes] = useState([]);
    const [loadTypes, setLoadTypes] = useState([]);
    const [oldLoadTypes, setOldLoadTypes] = useState([]);
    const [devicesByLoadType, setDevicesByLoadType] = useState([]);
    const [on, setOn] = useState(false);
    const param = useParams();

    const sendData = (selectedDevices) => {
        let arr = [];
        if (selectedDevices.length > 0) {
            selectedDevices.forEach(element => {
                arr = [...arr, element.deviceId]
            });
        }
        props.parentCallback(arr, selectedDevices)
    }
    const clickBtnFind = (type) => {
        document.getElementById("search-by-name").value = "";
        setCheckedType(type)
        if (type === 3) {
            getListArea();
        } else if (type === 4) {
            getListObjectType();
        } else if (type === 5) {
            getListLoadType()
        }
        else if (type === 7) {
            getListDeviceTypeMst();
        }
        getListDevice(type);
        setCheckedHiden(0);
    }
    const getListDevice = async (type, area) => {
        setSelectedDevices([]);
        document.getElementById("search-by-name").value = "";
        let res;
        if (type === undefined || type === 0) {
            res = await DeviceService.getListDeviceAllFlag(props.projectId, props.systemTypeId, null, null, null, props.deviceType == null ? null : props.deviceType);
            setDevices(res.data);
            setOldDevices(res.data);
        }
        if (type === 1) {
            res = await DeviceService.getListDeviceAllFlag(props.projectId, props.systemTypeId, null, null, null, props.deviceType == null ? null : props.deviceType);
            setDevices(res.data);
            setOldDevices(res.data);
        }

        if (type === 2) {
            res = await DeviceService.getListDeviceCalculateFlag(props.projectId, props.systemTypeId, null, null, null);
            setDevices(res.data);
            setOldDevices(res.data);
        }
        if (type === 3) {
            res = await DeviceService.getListDeviceAllFlag(props.projectId, props.systemTypeId, area, null, null);
            setDevicesByArea(res.data);
            setOldDevices(res.data);
        }
        if (type === 4) {
            res = await ObjectService.getListObjectTypeBySystemTypeIdAndProjectId(props.systemTypeId, props.projectId);
        }
        if (type === 5) {
            res = await LoadTypeService.getListLoadBySystemTypeIdAndProjectId(props.systemTypeId, props.projectId);
        }
        if (type === 6) {
            res = await DeviceService.getListDeviceCalculateFlag(props.projectId, props.systemTypeId, null, null, null);
        }

    }
    const getDeviceByArea = async (area) => {
        let res = await DeviceService.getDeviceByAreaSelectDevice(props.systemTypeId, props.projectId, area);
        setDevicesByArea(res.data);
        setOldDevices(res.data);
    }
    const getListArea = async () => {
        let res = await AreaService.getAreaSelectDevice(props.systemTypeId, props.projectId);
        setAreas(res.data);
        setOldAreas(res.data);
    }
    const getListObjectType = async () => {
        let res = await ObjectTypeService.getObjectTypeSelectDevice(props.systemTypeId, props.projectId);
        setObjectTypes(res.data);
        setOldObjectTypes(res.data);
    }
    const getListDeviceTypeMst = async () => {
        let res = await DeviceTypeService.listDeviceTypeMst(props.systemTypeId, param.customerId, props.projectId);
        setObjectTypes(res.data);
        setOldObjectTypes(res.data);
    }
    const searchByName = (name) => {
        let find = name.toUpperCase();

        if (checkedType === 3) {
            setDevicesByArea([])
            setAreas(oldAreas.filter((area) => area.area.toUpperCase().includes(find)));
        }
        if (checkedType === 4) {
            setDevicesByObjectType([])
            setObjectTypes(oldObjectTypes.filter((objT) => objT.objectTypeName.toUpperCase().includes(find)));
        }
        if (checkedType === 5) {
            setDevicesByLoadType([])
            setLoadTypes(oldLoadTypes.filter((l) => l.loadTypeName.toUpperCase().includes(find)));
        }
        else {
            setDevices(oldDevices.filter((item) => item.deviceName.toUpperCase().includes(find)));
        }
    }
    const onSelectDevice = (item) => {
        let ck = false;
        for (let i = 0; i < selectedDevices.length; i++) {
            if (selectedDevices[i].deviceId == item.deviceId) {
                ck = true;
            }
        }
        if (ck === false) {
            setSelectedDevices(Array.from(new Set([...selectedDevices, item])));
        } else {
            onUnSelectDevice(item);
        }
        setChecked(item)
    }
    const getListLoadType = async () => {
        let res = await LoadTypeService.getListLoadBySystemTypeIdAndProjectId(props.systemTypeId, props.projectId);
        setLoadTypes(res.data)
        setOldLoadTypes(res.data)
    }
    const setChecked = (item) => {
        let ck = false;
        selectedDevices.forEach(element => {
            if (element.deviceId == item.deviceId) {
                ck = true;
            }
        })
        return ck;
    }
    const onUnSelectDevice = (item) => {
        setSelectedDevices(selectedDevices.filter((dv) => item.deviceId != dv.deviceId));
    }
    const getListDeviceByObjectType = async (objectType) => {
        let res = await DeviceService.selectDeviceByObjectType(props.systemTypeId, props.projectId, objectType);
        setDevicesByObjectType(res.data);
    }
    const getListDeviceByDeviceTypeMst = async (deviceType) => {
        let res = await DeviceService.getListDeviceByDeviceType(props.projectId, props.systemTypeId, deviceType);
        setDevicesByObjectType(res.data);
    }
    const getListDeviceByLoadTypee = async (loadType) => {
        let res = await DeviceService.getDeviceByLoadTypeSelectDevice(props.systemTypeId, props.projectId, loadType);
        setDevicesByLoadType(res.data)
    }
    const hiddenData = (index) => {
        if (checkedHiden === index) {
            setOn(!on);
        } else {
            setOn(true);
        }

    }
    const checkLimits = () => {
        if (props.limit == selectedDevices.length) {
            return true;
        } else {
            return false;
        }
    }
    const renderListDevice = () => {
        if (checkedType === undefined || checkedType === 0 || checkedType === 1 || checkedType === 2) {
            return (
                devices.map((item, index) => {
                    return (
                        <div className="div-select" key={index} onClick={() => onSelectDevice(item)} style={checkLimits() == true ? { pointerEvents: 'none' } : null}>
                            <input type="checkbox" value={item.deviceId} className="input-select" id={`ipcheck` + index} readOnly checked={setChecked(item)} />
                            <label className="label-select" >{item.deviceName}</label>
                        </div>
                    )
                }))
        }
        if (checkedType === 3) {
            return (
                areas.map((area, index) => {
                    return (
                        <div key={index}>
                            <div className="mt-2">
                                <button type="button" className="btn btn-select mb-2" onClick={() => {
                                    setCheckedHiden(index + 1)
                                    setDevicesByArea([])
                                    hiddenData(index + 1);
                                    getDeviceByArea(area == null ? "null" : area.location)
                                }} >{area == null ? "Không có tên khu vực" : area.location}</button>
                            </div>
                            <div style={(checkedHiden === index + 1 && on === true) == false ? { display: 'none', transition: '5s ease-in' } : null} >
                                {
                                    devicesByArea.map((item, idx) => {
                                        return (
                                            <div className="div-select" key={idx} onClick={() => onSelectDevice(item)} >
                                                <input type="checkbox" className="input-select" id={`ipcheck` + idx} readOnly checked={setChecked(item)} />
                                                <label className="label-select" >{item.deviceName}</label>
                                            </div>
                                        )
                                    })
                                }
                            </div>
                        </div>
                    )
                })
            )
        }
        if (checkedType === 4) {
            return (
                objectTypes.map((item, index) => {
                    return (
                        <div key={index}>
                            <div className="mt-2">
                                <button type="button" className="btn btn-select mb-2" onClick={() => {
                                    setCheckedHiden(index + 1)
                                    setDevicesByObjectType([])
                                    hiddenData(index + 1);
                                    getListDeviceByObjectType(item.objectTypeId)
                                }} >{item.objectTypeName == "" ? '' : item.objectTypeName}</button>
                            </div>
                            <div style={(checkedHiden === index + 1 && on === true) == false ? { display: 'none', transition: '5s ease-in' } : null} >
                                {
                                    devicesByObjectType.map((item, idx) => {
                                        return (
                                            <div className="div-select" key={idx} onClick={() => onSelectDevice(item)} >
                                                <input type="checkbox" className="input-select" id={`ipcheck` + idx} readOnly checked={setChecked(item)} />
                                                <label className="label-select" >{item.deviceName}</label>
                                            </div>
                                        )
                                    })
                                }
                            </div>
                        </div>
                    )
                })
            )
        }
        if (checkedType === 5) {
            return (
                loadTypes.map((item, index) => {
                    return (
                        <div key={index}>
                            <div className="mt-2">
                                <button type="button" className="btn btn-select mb-2" onClick={() => {
                                    setCheckedHiden(index + 1)
                                    setDevicesByLoadType([])
                                    hiddenData(index + 1);
                                    getListDeviceByLoadTypee(item.loadTypeId)
                                }} >{item.loadTypeName == "" ? '' : item.loadTypeName}</button>
                            </div>
                            <div style={(checkedHiden === index + 1 && on === true) == false ? { display: 'none', transition: '5s ease-in' } : null} >
                                {
                                    devicesByLoadType.map((item, idx) => {
                                        return (
                                            <div className="div-select" key={idx} onClick={() => onSelectDevice(item)} >
                                                <input type="checkbox" className="input-select" id={`ipcheck` + idx} readOnly checked={setChecked(item)} />
                                                <label className="label-select" >{item.deviceName}</label>
                                            </div>
                                        )
                                    })
                                }
                            </div>
                        </div>
                    )
                })
            )
        }
        if (checkedType === 7) {
            return (
                objectTypes.map((item, index) => {
                    return (
                        <div key={index}>
                            <div className="mt-2">
                                <button type="button" className="btn btn-select mb-2" style={{ width: '70%' }} onClick={() => {
                                    setCheckedHiden(index + 1)
                                    setDevicesByObjectType([])
                                    hiddenData(index + 1);
                                    getListDeviceByDeviceTypeMst(item.id)
                                }} >{item.id == "" ? '' : item.name}</button>
                            </div>
                            <div style={(checkedHiden === index + 1 && on === true) == false ? { display: 'none', transition: '5s ease-in' } : null} >
                                {
                                    devicesByObjectType.map((item, idx) => {
                                        return (
                                            <div className="div-select" key={idx} onClick={() => onSelectDevice(item)} >
                                                <input type="checkbox" className="input-select" id={`ipcheck` + idx} readOnly checked={setChecked(item)} />
                                                <label className="label-select" >{item.deviceName}</label>
                                            </div>
                                        )
                                    })
                                }
                            </div>
                        </div>
                    )
                })
            )
        }
    }
    return (
        <>
            {/* Modal */}
            <button type="button" className={'btn btn-select-device-sankey'} data-toggle="modal" data-target="#exampleModalCenter1" onClick={() => {
                getListDevice()
            }
            } style={props.style} >
                {props.titleName}
            </button>
            <div className="modal fade" id="exampleModalCenter1" tabIndex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
                <div id={"select-device-dialog"} className="modal-dialog modal-dialog-centered" role="document">
                    <div id={"select-device-content"} className="modal-content modal-select-content" >
                        <div className='box-select'>
                            <div className='modal-select-header'>
                                <button type="button" className={checkedType === 1 ? "btn btn-find-checked" : 'btn btn-find'} onClick={() => clickBtnFind(1)}>{t('content.all')}</button>
                                <button type="button" className={checkedType === 2 ? "btn btn-find-checked" : 'btn btn-find'} onClick={() => clickBtnFind(2)}>{t('content.home_page.device_select.source_device')}</button>
                                <button hidden={props.new == undefined ? false : true} type="button" className={checkedType === 3 ? "btn btn-find-checked" : 'btn btn-find'} onClick={() => clickBtnFind(3)}>{t('content.super_manager')}</button>
                                <button hidden={props.new == undefined ? false : true} type="button" className={checkedType === 4 ? "btn btn-find-checked" : 'btn btn-find'} onClick={() => clickBtnFind(4)}>{t('content.device_type')}</button>
                                <button hidden={props.new != undefined ? false : true} type="button" className={checkedType === 7 ? "btn btn-find-checked" : 'btn btn-find'} onClick={() => clickBtnFind(7)}>{t('content.device_type')}</button>
                                <button type="button" className={checkedType === 5 ? "btn btn-find-checked" : 'btn btn-find'} onClick={() => clickBtnFind(5)} hidden={props.systemTypeId == 1 ? false : true}>{t('content.load_type')}</button>
                                <button type="button" className={checkedType === 6 ? "btn btn-find-checked" : 'btn btn-find'} onClick={() => clickBtnFind(6)}>{t('content.diagram')}</button>
                            </div>
                            <p style={{ height: '1px', backgroundColor: 'rgb(202, 202, 202)', marginTop: '1.5%' }}></p>
                            <div className='modal-select-body row'>
                                <div className='body-left col-6'>
                                    <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                                        <p className='body-title'>{t('content.list_device')}</p>
                                        <div className="input-group" style={{ width: '45%', marginLeft: '5%', marginTop: '0' }}>
                                            <span className="input-group-addon" style={{ backgroundColor: '#eee', width: '18%', borderTopLeftRadius: '5px', borderBottomLeftRadius: '5px', border: '1px solid #ced4da' }}><i className="fa-solid fa-magnifying-glass" style={{ fontSize: '18px', marginTop: '23%', marginLeft: '25%' }} /></span>
                                            <input id="search-by-name" placeholder={checkedType === 3 ? "Tên khu vực" : checkedType === 4 ? "Tên loại thiết bị" : checkedType === 5 ? "Tên loại phụ tải" : "Tên thiết bị"} className='form-control' onChange={(event) => searchByName(event.target.value)} />
                                        </div>
                                    </div>
                                    <div className='body-content'>
                                        {renderListDevice()}
                                    </div>
                                </div>
                                <div className='body-right col-6'>
                                    <p className='body-title'>{t('content.device_selected')}</p>
                                    <div className='body-content'>
                                        {selectedDevices?.map((item, index) => {
                                            return (
                                                <div className="div-select" key={`selected` + index} onClick={() => onUnSelectDevice(item)}  >
                                                    <input type="checkbox" value={item.deviceId} className="" id={`ip1che1ck` + index} checked={true} readOnly />
                                                    <label className="" htmlFor={`ip1che1ck1` + index}>{item.deviceName}</label>
                                                </div>)

                                        })}

                                    </div>

                                </div>
                            </div>

                            <div className='modal-select-footer'>
                                <button type="button" className="btn btn-confirm" style={{ marginRight: '1.5%' }} data-dismiss="modal" onClick={() => sendData(selectedDevices)}>{t('content.accept')}</button>
                                <button type="button" className="btn btn-can" data-dismiss="modal" style={{ marginLeft: '1.5%' }} onClick={() => {
                                    setSelectedDevices([])
                                    setCheckedType(1);
                                }}>{t('content.cancel')}</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
}

export default SelectDeviceSankey;