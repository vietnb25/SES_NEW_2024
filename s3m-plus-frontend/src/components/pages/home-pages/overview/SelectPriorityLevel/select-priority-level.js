
import React, { useState } from 'react';
import { useEffect } from "react";
import "../SelectPriorityLevel/select-priority-level.css";
import { useTranslation } from "react-i18next";
import UserService from '../../../../../services/UserService';

function SelectPriorityLevel(props) {
    const { t } = useTranslation();
    const [oldDevices, setOldDevices] = useState([]);
    const [module, setModule] = useState(
        [
            { label: t('content.home_page.overview.power_flow'), value: 1 },
            { label: t('content.home_page.overview.map'), value: 2 },
            { label: t('content.home_page.overview.energy_data'), value: 3 },
            { label: t('content.home_page.overview.energy_usage_plan'), value: 4 },
            { label: t('content.home_page.overview.energy_cost_revenue'), value: 5 },
            { label: t('content.home_page.overview.energy_statistics'), value: 6 },
            { label: t('content.home_page.overview.warning_statistics'), value: 7 },
            { label: t('content.home_page.overview.management_failure'), value: 8 },
            { label: t('content.home_page.overview.compressed_air_system_data'), value: 9 },
            { label: t('content.home_page.overview.liquid_system_data'), value: 10 },
        ]
    );

    const [selectedModule, setSelectedModule] = useState([]);
    const [listPriority, setListPriority] = useState([])

    const sendData = (selectedModule) => {
        let arr = [];
        if (selectedModule.length > 0) {
            selectedModule.forEach(element => {
                arr = [...arr, element.value]
            });
        }

        props.parentCallback(arr, selectedModule)
    }

    const getListModule = async () => {
        setSelectedModule([]);
        // document.getElementById("search-by-name").value = "";
    }

    const searchByName = (name) => {
        let find = name.toUpperCase();

        setModule(oldDevices.filter((item) => item.label.toUpperCase().includes(find)));

    }

    const onSelectDevice = (item) => {
        let ck = false;
        for (let i = 0; i < selectedModule.length; i++) {
            if (selectedModule[i].value == item.value) {
                ck = true;
            }
        }
        if (ck === false) {
            setSelectedModule(Array.from(new Set([...selectedModule, item])));
        } else {
            onUnSelectModule(item);
        }
        setChecked(item)
    }
    const setChecked = (item) => {
        let ck = false;
        selectedModule.forEach(element => {
            if (element.value == item.value) {
                ck = true;
            }
        })
        return ck;
    }
    const onUnSelectModule = (item) => {
        setSelectedModule(selectedModule.filter((dv) => item.value != dv.value));
    }

    const checkLimits = () => {
        if (props.limit == selectedModule.length) {
            return true;
        } else {
            return false;
        }
    }
    const renderModule = () => {
        return (
            module.map((item, index) => {
                return (
                    <div className="div-select" key={index} onClick={() => onSelectDevice(item)} style={checkLimits() == true ? { pointerEvents: 'none' } : null}>
                        <input type="checkbox" value={item.value} className="input-select" id={`ipcheck` + index} readOnly checked={setChecked(item)} />
                        <label className="label-select" >{item.label}</label>
                    </div>
                )
            }))
    }

    const getUser = async () => {
        let response = await UserService.getUserByUsername();
        if (response.status === 200) {
            const userData = response.data;

            if (userData.priorityIngredients != null || userData.priorityIngredients != undefined) {
                const priorityList = userData.priorityIngredients.split(',').map(item => parseInt(item));
                const newPriorityIngredients = priorityList.map((value) => {
                    return {
                        label: getLabelForValueIngredient(value),
                        value: value,
                    };
                });
                setModule(newPriorityIngredients)
            }
        }
    }

    function getLabelForValueIngredient(value) {
        switch (value) {
            case 1:
                return t('content.category.user.edit.power_flow');
            case 2:
                return t('content.category.user.edit.map');
            case 3:
                return t('content.category.user.edit.energy_data');
            case 4:
                return t('content.category.user.edit.energy_usage_plan');
            case 5:
                return t('content.category.user.edit.energy_cost_revenue');
            case 6:
                return t('content.category.user.edit.energy_statistics');
            case 7:
                return t('content.category.user.edit.warning_statistics');
            case 8:
                return t('content.category.user.edit.management_failure');
            case 9:
                return t('content.category.user.edit.compressed_air_system_data');
            case 10:
                return t('content.category.user.edit.liquid_system_data');
            default:
                return '';
        }
    }

    useEffect(() => {
        getUser();
    }, []);
    return (
        <>
            {/* Modal */}
            <span type="button" className={'btn btn-select-device-priority'} data-toggle="modal" data-target="#exampleModalCenter1" onClick={() => {
                getListModule()
            }
            } style={props.style} >
                {props.titleName}
                <i className="fa-solid fa-arrow-down-wide-short" style={{ color: "white" }}></i>
            </span>
            <div className="modal fade" id="exampleModalCenter1" tabIndex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
                <div id={"select-device-dialog"} className="modal-dialog modal-dialog-centered" role="document">
                    <div id={"select-device-content"} className="modal-content modal-select-content" >
                        <div className='box-select'>
                            <div className='modal-select-header'>
                            </div>
                            <p style={{ height: '1px', backgroundColor: 'rgb(202, 202, 202)', marginTop: '1.5%' }}></p>
                            <div className='modal-select-body row'>
                                <div className='body-left col-6'>
                                    <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                                        <p className='body-title-pri'>Thành phần ưu tiên</p>
                                        <div className="input-group" style={{ width: '45%', marginLeft: '5%' }}>
                                            {/* <span className="input-group-addon" style={{ backgroundColor: '#eee', width: '18%', borderTopLeftRadius: '5px', borderBottomLeftRadius: '5px', border: '1px solid #ced4da' }}><i className="fa-solid fa-magnifying-glass" style={{ fontSize: '18px', marginTop: '23%', marginLeft: '25%' }} /></span> */}
                                            {/* <input id="search-by-name" placeholder={"Tên thành phần"} className='form-control' onChange={(event) => searchByName(event.target.value)} /> */}
                                        </div>
                                    </div>
                                    <div className='body-content'>
                                        {renderModule()}
                                    </div>
                                </div>
                                <div className='body-right col-6'>
                                    <p className='body-title-pri'>Thành phần đã chọn</p>
                                    <div className='body-content'>
                                        {selectedModule?.map((item, index) => {
                                            return (
                                                <div className="div-select" key={`selected` + index} onClick={() => onUnSelectModule(item)}  >
                                                    <input type="checkbox" value={item.value} className="" id={`ip1che1ck` + index} checked={true} readOnly />
                                                    <label className="" htmlFor={`ip1che1ck1` + index}>{item.label}</label>
                                                </div>)

                                        })}

                                    </div>

                                </div>
                            </div>

                            <div className='modal-select-footer'>
                                <button type="button" className="btn btn-confirm" style={{ marginRight: '1.5%' }} data-dismiss="modal" onClick={() => sendData(selectedModule)}>Đồng ý</button>
                                <button type="button" className="btn btn-can" data-dismiss="modal" style={{ marginLeft: '1.5%' }} onClick={() => {
                                    setSelectedModule([])
                                    // setCheckedType(1);
                                }}>Hủy</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
}

export default SelectPriorityLevel;