import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import moment from "moment/moment";
import SettingService from '../../../../../../services/SettingService';
import { NotficationError, NotficationInfo, NotficationSuscces, NotficationWarning } from "../../../notification/notification";
import { ToastContainer } from "react-toastify";
import "../../index.css"
import { DATA, createItem, setDecriptionByValue, setEmpty } from "../data"

const Meter = (props) => {
    const { t } = useTranslation();
    const [status, setStattus] = useState(true);
    const [list, setList] = useState();
    const [refresh, setRefresh] = useState(false);
    const $ = window.$;
    const [dataSettting, setDataSetting] = useState([]);

    let dataWarning = ([
        {
            warningType: 101,
            warningName: t('content.home_page.warning_tab.over_volt'),
            data: [{
                startDescription: "Uan || Ubn || Ucn > ",
                settingValue: "",
                endDescription: ""
            }],
            description: "",
            warningLevel: 3,
            updateDate: ''
        },
        {
            warningType: 102,
            warningName: t('content.home_page.warning_tab.under_volt'),
            data: [{
                startDescription: "Uan || Ubn || Ucn < ",
                settingValue: "",
                endDescription: ""
            }],
            description: "",
            warningLevel: 3,
            updateDate: ''
        },
        {
            warningType: 103,
            warningName: t('content.home_page.warning_tab.over_load'),
            data: [{
                startDescription: "Ia || Ib || Ic > ",
                settingValue: "",
                endDescription: " * Idm"
            }],
            description: "",
            warningLevel: 3,
            updateDate: ''
        },
        {
            warningType: 104,
            warningName: t('content.home_page.warning_tab.under_power_factor'),
            data: [{
                startDescription: "(((Ia+Ib+Ic)/3)/Idm) * 100 > ",
                settingValue: "",
                endDescription: ""
            },
            {
                startDescription: "& abs(PFa) || abs(PFb) || abs(PFc) < ",
                settingValue: "",
                endDescription: ""
            },
            ],
            description: "",
            warningLevel: 3,
            updateDate: ''
        },
        {
            warningType: 105,
            warningName: t('content.home_page.warning_tab.over_frequency'),
            data: [{
                startDescription: "F > ",
                settingValue: "",
                endDescription: ""
            }],
            description: "",
            warningLevel: 3,
            updateDate: ''
        },
        {
            warningType: 106,
            warningName: t('content.home_page.warning_tab.under_frequency'),
            data: [{
                startDescription: "F < ",
                settingValue: "",
                endDescription: ""
            }],
            description: "",
            warningLevel: 3,
            updateDate: ''
        },
        {
            warningType: 107,
            warningName: t('content.home_page.warning_tab.phase_deviation'),
            data: [{
                startDescription: "(((Ia+Ib+Ic)/3)/Idm) * 100 > ",
                settingValue: "",
                endDescription: ""
            },
            {
                startDescription: "& (Imax - Imin) / Imin > ",
                settingValue: "",
                endDescription: ""
            },],
            description: "",
            warningLevel: 3,
            updateDate: ''
        },
        {
            warningType: 108,
            warningName: t('content.home_page.warning_tab.current_harmonics_n'),
            data: [{
                startDescription: "IA_H1 || ... || IA_H10 || IB_H1 || ... || IB_H10 || IC_H1|| ...|| IC_H10 >= ",
                settingValue: "",
                endDescription: ""
            },
            {
                startDescription: "|| A_H11 || ... || IA_H16 || IB_H11 || ... || IB_H16 || IC_H11|| ...|| IC_H16 >= ",
                settingValue: "",
                endDescription: ""
            },
            {
                startDescription: "|| IA_H17 || ... || IA_H22 || IB_H17 || ... || IB_H22 || IC_H17|| ...|| IC_H22 >= ",
                settingValue: "",
                endDescription: ""
            },
            {
                startDescription: "|| IA_H23 || ... || IA_H34 || IB_H23 || ... || IB_H34 || IC_H23|| ...|| IC_H34 >= ",
                settingValue: "",
                endDescription: ""
            },
            {
                startDescription: "|| IA_H35 || ... || IA_HN || IB_H34 || ... || IB_HN || IC_H34|| ...|| IC_HN >= ",
                settingValue: "",
                endDescription: ""
            },
            {
                startDescription: "& (((Ia+Ib+Ic)/3)/Idm) * 100 >= ",
                settingValue: "",
                endDescription: ""
            },],
            description: "",
            warningLevel: 3,
            updateDate: ''
        },
        {
            warningType: 109,
            warningName: t('content.home_page.warning_tab.voltage_harmonics_n'),
            data: [{
                startDescription: "VAN_H(n) || VBN_H(n) || VCN_H(n) >= ",
                settingValue: "",
                endDescription: ""
            },
            {
                startDescription: "& (((Ia+Ib+Ic)/3)/Idm) * 100 >= ",
                settingValue: "",
                endDescription: ""
            },],
            description: "",
            warningLevel: 3,
            updateDate: ''
        },
        {
            warningType: 110,
            warningName: t('content.home_page.warning_tab.total_voltage_harmonics'),
            data: [{
                startDescription: "THD_Van || THD_Vbn || THD_Vcn >= ",
                settingValue: "",
                endDescription: ""
            },
            {
                startDescription: "& (((Ia+Ib+Ic)/3)/Idm) * 100 >= ",
                settingValue: "",
                endDescription: ""
            },],
            description: "",
            warningLevel: 3,
            updateDate: ''
        },
        {
            warningType: 111,
            warningName: t('content.home_page.warning_tab.total_current_harmonics'),
            data: [{
                startDescription: "THD_Ia || THD_Ib || THD_Ic >= ",
                settingValue: "",
                endDescription: ""
            },
            {
                startDescription: "& (((Ia+Ib+Ic)/3)/Idm) * 100 >= ",
                settingValue: "",
                endDescription: ""
            },],
            description: "",
            warningLevel: 3,
            updateDate: ''
        },
        {
            warningType: 112,
            warningName: t('content.home_page.warning_tab.phase_reverse'),
            data: [{
                startDescription: " abs(PFa) || abs(PFb) || abs(PFc) < ",
                settingValue: "",
                endDescription: ""
            }],
            description: "",
            warningLevel: 3,
            updateDate: ''
        },
        {
            warningType: 113,
            warningName: t('content.home_page.warning_tab.lost_power'),
            data: [{
                startDescription: "Ua || Ub || Uc <= ",
                settingValue: "",
                endDescription: ""
            }],
            description: "",
            warningLevel: 3,
            updateDate: ''
        },
    ])

    const funcGetSetting = async (deviceIds) => {
        let res = await SettingService.getSettingByDeviceIds(deviceIds);
        if (res.status == 200) {
            funcFillData(res.data)

        }
    }

    const funcFillData = async (dataInput) => {
        dataWarning.forEach(item => {
            let array = dataInput?.filter(d => d.warningType == item.warningType);

            if (array.length == 1) {
                let listSetting = array[0].settingValue.split(',');
                for (let i = 0; i < listSetting.length; i++) {
                    item.data[i].settingValue = listSetting[i]
                }
                item.description = array[0].description;
                item.warningLevel = array[0].warningLevel;
                item.updateDate = array[0].updateDate;
            }
        })
        setList(dataWarning)
    }

    const funcInputData = async (dataW, index, indexOfData, e, listRepair) => {
        let node = {
            "warningTypeId": "",
            "warningName": "",
            "settingValue": "",
            "warningLevel": "",
            "description": ""
        };

        node.warningTypeId = dataW.warningType;
        node.warningName = dataW.warningName;
        let setting = ""
        for (let i = 0; i < dataW.data.length; i++) {
            if (i == 0) {
                if (indexOfData == i) {
                    setting += e.target.value;
                    continue;
                }
                setting += dataW.data[i].settingValue
            } else {
                if (indexOfData == i) {
                    setting += "," + e.target.value;
                    continue;
                }
                setting += "," + dataW.data[i].settingValue
            }
        }
        node.settingValue = setting;
        node.warningLevel = $(`input[name=${"levelW" + index}]:checked`).val();
        node.description = setDecriptionByValue(dataW.warningType, setting)

        let haveWarningType = false;
        DATA.forEach(member => {
            if (member.warningTypeId == dataW.warningType) {
                haveWarningType = true
                member.settingValue = setting
                member.description = setDecriptionByValue(dataW.warningType, setting)
            }
        })
        if (!haveWarningType) {
            DATA.push(node)
        }

        listRepair.forEach(item => {
            if (item.warningType == dataW.warningType) {
                dataW.data[indexOfData].settingValue = e.target.value
            }

        })
        setList(listRepair)

        setDataSetting([])
    }

    const funcSetWarningLevel = (dataW, index, e, listRepair) => {
        $(`input[name=${"levelW" + index}]`).val(e.target.value);
        let node = {
            "warningTypeId": "",
            "warningName": "",
            "settingValue": "",
            "warningLevel": "",
            "description": ""
        };

        node.warningTypeId = dataW.warningType;
        node.warningName = dataW.warningName;
        let setting = ""
        for (let i = 0; i < dataW.data.length; i++) {
            if (i == 0) {
                setting += dataW.data[i].settingValue
            } else {
                setting += "," + dataW.data[i].settingValue
            }

        }
        node.settingValue = setting;
        node.warningLevel = e.target.value;
        node.description = setDecriptionByValue(dataW.warningType, setting)

        let haveWarningType = false;
        DATA.forEach(member => {
            if (member.warningTypeId == dataW.warningType) {
                haveWarningType = true
                member.warningLevel = e.target.value;
            }
        })

        if (!haveWarningType) {
            DATA.push(node)
        }

        listRepair.forEach(item => {
            if (item.warningType == dataW.warningType) {
                item.warningLevel = parseInt(e.target.value)
                item.description = setDecriptionByValue(dataW.warningType, setting)
            }

        })
        setList(listRepair)
        setDataSetting([])
    }

    const funcSave = async () => {
        DATA.forEach(item => {
            let res = SettingService.updateSettingByDevices(props.deviceIds.toString(), item);
            if (res == 200) {
                return;
            }
        })
        console.log(DATA);
        NotficationSuscces("Cập nhật thành công!");


    }

    const funcDisplayModal = async () => {
        console.log(DATA);
        setDataSetting(DATA)
    }

    useEffect(() => {
        setEmpty();
        funcGetSetting(props.deviceIds.toString())
    }, [props.deviceIds]);

    return (
        <div>
            <ToastContainer />
            <div style={{ backgroundColor: "#FFF" }}>
                <table id="table-setting-warning" className="table table-setting-warning">
                    <thead height="30px">
                        <tr >
                            <th width="40px">TT</th>
                            <th width="200px">{t('content.home_page.warning_setting.setting')}</th>
                            <th width="250px">{t('content.home_page.warning_setting.warning_level')}</th>
                            <th>{t('content.home_page.warning_setting.description')}</th>
                            <th width="150px">{t('content.home_page.warning_setting.update_date')}</th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            list?.map((item, index) => (

                                <tr key={index}>
                                    <td width="40px">{index + 1}</td>
                                    <td width="300px">{item.warningName}</td>
                                    <td width="400px">
                                        <div className="select-type">
                                            <span className="p-1" >
                                                <input className="radio-warning-level" type="radio" name={"levelW" + index} id={"inlineRadio1" + index} value={1} style={{ verticalAlign: "bottom", appearance: "none" }} defaultChecked={item.warningLevel == 1 ? true : false} onChange={(e) => funcSetWarningLevel(item, index, e, list)} />
                                                <span className='span-warning-level p-1'>
                                                    <label className="form-check-label text-white text-center" htmlFor={"inlineRadio1" + index}>{t('content.home_page.warning_setting.low')}</label>
                                                </span>

                                            </span>
                                            <span className="p-1" >
                                                <input className="radio-warning-level" type="radio" name={"levelW" + index} id={"inlineRadio2" + index} value={2} style={{ verticalAlign: "bottom", appearance: "none" }} defaultChecked={item.warningLevel == 2 ? true : false} onChange={(e) => funcSetWarningLevel(item, index, e, list)} />
                                                <span className='span-warning-level p-1'>
                                                    <label className="form-check-label text-white text-center" htmlFor={"inlineRadio2" + index}>{t('content.home_page.warning_setting.medium')}</label>
                                                </span>

                                            </span>
                                            <span className="p-1" >
                                                <input className="radio-warning-level" type="radio" name={"levelW" + index} id={"inlineRadio3" + index} value={3} style={{ verticalAlign: "bottom", appearance: "none" }} defaultChecked={item.warningLevel == 3 ? true : false} onChange={(e) => funcSetWarningLevel(item, index, e, list)} />
                                                <span className='span-warning-level p-1'>
                                                    <label className="form-check-label text-white text-center" htmlFor={"inlineRadio3" + index}>{t('content.home_page.warning_setting.high')}</label>
                                                </span>

                                            </span>
                                        </div>
                                    </td>
                                    <td style={{ lineHeight: 1.5 }}>{item.data?.map((i, no) => (
                                        <div className='p-1' key={no}>
                                            {
                                                no == 0 && <i className='fa-solid fa-circle fa-2xs p-1'></i>
                                            }

                                            {i.startDescription}
                                            <input className='text-center decimal' type="number" style={{ backgroundColor: "var(--surface-d)", width: "70px" }} defaultValue={i.settingValue} onChange={(e) => funcInputData(item, index, no, e, list)}></input>
                                            {i.endDescription}
                                            <br />
                                        </div>
                                    ))}</td>
                                    <td width="150px">{moment(item.updateDate ? item.updateDate : new Date()).format('YYYY-MM-DD HH:mm:ss')}</td>
                                </tr>
                            ))
                        }
                    </tbody>
                </table>
            </div>
            <div className='text-center'>
                <button style={{ width: "10%" }} type="button" className="btn btn-agree mr-1" data-toggle="modal" data-target="#exampleModalCenter" onClick={funcDisplayModal}>
                    <i className="fa-solid fa-check mr-1"></i>{t('content.category.setting.list.lable_update')}
                </button>
                {/* <button style={{ width: "10%" }} type="button" className="btn btn-outline-secondary btn-cancel text-white" onClick={() => funcGetSetting(props.deviceIds.toString())}>
                    <i className="fa-solid fa-xmark mr-1"></i>Làm mới
                </button> */}
            </div>


            <div className="modal fade bd-example-modal-lg" id="exampleModalCenter" tabIndex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true" style={{ backgroundColor: "rgb(120, 120, 120, 0.5)" }}>
                <div className="modal-dialog modal-lg modal-dialog-centered" role="document">
                    <div className="modal-content">
                        <div className="modal-header">
                            <h5 className="modal-title" id="exampleModalLongTitle"> {t('content.category.setting.edit.title')}</h5>
                            <button type="button" className="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div className="modal-body">
                            <table id="table-setting-warning" className="table table-setting-warning">
                                <thead height="30px">
                                    <tr >
                                        <th width="40px">TT</th>
                                        <th width="150px">{t('content.home_page.warning_setting.setting')}</th>
                                        <th width="250px">{t('content.home_page.warning_setting.warning_level')}</th>
                                        <th>{t('content.home_page.warning_setting.description')}</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {dataSettting.length > 0 &&
                                        dataSettting?.map((item, index) => (
                                            <tr key={index} height="30px" readOnly>
                                                <td width="40px">{index + 1}</td>
                                                <td width="300px">{item.warningName}</td>
                                                <td width="400px">
                                                    <div className="select-type">
                                                        <span className="p-1" >
                                                            <input className="radio-warning-level" type="radio" name={"level" + index} id={"inlineRadio" + index} value={1} style={{ verticalAlign: "bottom", appearance: "none" }} checked={item.warningLevel == 1 ? true : false} readOnly />
                                                            <span className='span-warning-level p-1'>
                                                                <label className="form-check-label text-white text-center" htmlFor={"inlineRadio" + index}>{t('content.home_page.warning_setting.low')}</label>
                                                            </span>

                                                        </span>
                                                        <span className="p-1" >
                                                            <input className="radio-warning-level" type="radio" name={"level" + index} id={"inlineRadio" + index} value={2} style={{ verticalAlign: "bottom", appearance: "none" }} checked={item.warningLevel == 2 ? true : false} readOnly />
                                                            <span className='span-warning-level p-1'>
                                                                <label className="form-check-label text-white text-center" htmlFor={"inlineRadio" + index}>{t('content.home_page.warning_setting.medium')}</label>
                                                            </span>

                                                        </span>
                                                        <span className="p-1" >
                                                            <input className="radio-warning-level" type="radio" name={"level" + index} id={"inlineRadio" + index} value={3} style={{ verticalAlign: "bottom", appearance: "none" }} checked={item.warningLevel == 3 ? true : false} readOnly />
                                                            <span className='span-warning-level p-1'>
                                                                <label className="form-check-label text-white text-center" htmlFor={"inlineRadio" + index}>{t('content.home_page.warning_setting.high')}</label>
                                                            </span>

                                                        </span>
                                                    </div>
                                                </td>
                                                <td>
                                                    {item.description}
                                                </td>
                                            </tr>
                                        ))
                                    }
                                </tbody>
                            </table>
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="btn btn-secondary" data-dismiss="modal">{t('content.close')}</button>
                            <button type="button" className="btn btn-primary" onClick={() => funcSave()}>{t('content.save')}</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};


export default Meter;
