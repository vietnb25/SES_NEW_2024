
import React, { useState } from 'react';
import "../../select-device-component/select-device.css"
import { useParams } from 'react-router-dom/cjs/react-router-dom.min';
import DeviceService from '../../../../../services/DeviceService';
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

function SelectDeviceSetting(props) {
    const [checkedType, setCheckedType] = useState(1);
    const [oldDevices, setOldDevices] = useState([]);
    const [devices, setDevices] = useState([]);
    const [selectedDevices, setSelectedDevices] = useState([]);
    const param = useParams();
    const sendData = (selectedDevices) => {
        let arr = [];
        if (selectedDevices.length > 0) {
            selectedDevices.forEach(element => {
                arr = [...arr, element.deviceId]
            });
        }
        props.parentCallback(arr)
    }
    const searchByName = (name) => {
        let find = name.toUpperCase();
        setDevices(oldDevices.filter((item) => item.deviceName.toUpperCase().includes(find)));
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
    const checkLimits = () => {
        if (props.limit == selectedDevices.length) {
            return true;
        } else {
            return false;
        }
    }
    const renderListDevice = () => {
        if (devices.length > 0) {
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
    }
    const getListDevice = async (deviceType, systemType, project, customer) => {
        setSelectedDevices([]);
        let res = await DeviceService.getListDeviceByDeviceType(customer, project, systemType, deviceType);
        if (res.status == 200) {
            setDevices(res.data);
            setOldDevices(res.data);
        }
    }
    return (
        <>
            {/* Modal */}
            <button type="button" className={props.className === undefined || props.className === "" ? 'btn btn-select-device ' : props.className} data-toggle="modal" data-target="#exampleModalCenter1" onClick={() => {
                getListDevice(props.deviceTypeId, props.systemType, props.project, param.customerId)
            }
            } style={props.style} >
                {props.titleName}
            </button>
            <div className="modal fade" id="exampleModalCenter1" tabIndex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
                <div id={"select-device-dialog"} className="modal-dialog modal-dialog-centered" role="document">
                    <div id={"select-device-content"} className="modal-content modal-select-content">
                        <div className='box-select'>
                            <div className='' style={{ fontSize: '16px', fontWeight: 'bold' }}>{t('content.device').toUpperCase() + " " + props.deviceTypeName?.toUpperCase()}</div>
                            <p style={{ height: '1px', backgroundColor: 'rgb(202, 202, 202)', marginTop: '1.5%' }}></p>
                            <div className='modal-select-body row'>
                                <div className='body-left col-6'>
                                    <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                                        <p className='body-title'>{t('content.list_device')}</p>
                                        <div className="input-group" style={{ width: '45%', marginLeft: '5%', marginTop: '0' }}>
                                            <span className="input-group-addon" style={{ backgroundColor: '#eee', width: '18%', borderTopLeftRadius: '5px', borderBottomLeftRadius: '5px', border: '1px solid #ced4da' }}><i className="fa-solid fa-magnifying-glass" style={{ fontSize: '18px', marginTop: '23%', marginLeft: '25%' }} /></span>
                                            <input id="search-by-name" placeholder={checkedType === 3 ? t('content.super_manager') : checkedType === 4 ? t('content.device_type') : checkedType === 5 ? t('content.load_type') : t('content.device')} className='form-control' onChange={(event) => searchByName(event.target.value)} />
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

export default SelectDeviceSetting;