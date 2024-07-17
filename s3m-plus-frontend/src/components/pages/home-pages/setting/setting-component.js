import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom/cjs/react-router-dom.min';
import "./setting-component.css"
import SettingWarning from "../setting-warning/list/index2"
import SettingShiftComponent from '../setting-shift';
import SettingCost from './setting-cost/setting-cost';
import LandmarksEnergyPlans from './landmarks-energy-plans/landmarks-energy-plans';
import DataSimulation from './data-simulation';
import ReceiverWarning from '../receiver-warning';
import ListSettingShift from '../setting-shift/list';
import ListSetting from '../setting-warning/list/index2';
import UserService from '../../../../services/UserService';
import { t } from 'i18next';
import SettingMaterial from './setting-material/setting-material';

const SettingComponent = (props) => {
    const [typeSystem, setTypeSystem] = useState(1);
    const [type, setType] = useState(1);
    const [loading, setLoading] = useState(false);

    const param = useParams();
    const reRenderComponent = () => {
        if (type == 6) {
            return <SettingCost typeSystem={typeSystem}></SettingCost>
        } else if (type == 5) {
            return <LandmarksEnergyPlans typeSystem={typeSystem}></LandmarksEnergyPlans>
        } else if (type == 4) {
            return <DataSimulation typeSystem={typeSystem}></DataSimulation>
        } else if (type == 3) {
            return <ReceiverWarning typeSystem={typeSystem}></ReceiverWarning>
        } else if (type == 2) {
            return <ListSettingShift typeSystem={typeSystem}></ListSettingShift>
        } else if (type == 1) {
            return <ListSetting typeSystem={typeSystem}></ListSetting>
        }else if (type == 7) {
            return <SettingMaterial typeSystem={typeSystem}></SettingMaterial>
        }
    }

    const checkPriority = async () => {
        let response = await UserService.getUserByUsername();
        if (response.status === 200) {
            const userData = response.data;
            setTypeSystem(userData.prioritySystem);
        }
    }

    useEffect(() => {
        checkPriority();
    }, [])
    return (
        <>
            <div className='row'>
                <div className='col-2 ' style={{ minHeight: '78vh' }}>
                    <div className="">
                        <div className={type == 1 ? "setting-option-active" : "setting-option"} onClick={type != 1 ? () => { setType(1); setLoading(true) } : () => { }} ><i className="setting-option-icon fa-solid fa-triangle-exclamation"></i><span className="setting-option-text">{t('content.home_page.setting.warning')}</span></div>
                        <div className={type == 2 ? "setting-option-active" : "setting-option"} onClick={type != 2 ? () => { setType(2); setLoading(true) } : () => { }} ><i className="setting-option-icon fa-solid fa-stopwatch"></i><span className="setting-option-text">{t('content.home_page.setting.shift')}</span></div>
                        <div className={type == 3 ? "setting-option-active" : "setting-option"} onClick={type != 3 ? () => { setType(3); setLoading(true) } : () => { }} ><i className="setting-option-icon fa-solid fa-user-tie"></i><span className="setting-option-text">{t('content.home_page.setting.receiver_warning')}</span></div>
                        <div className={type == 4 ? "setting-option-active" : "setting-option"} onClick={type != 4 ? () => { setType(4); setLoading(true) } : () => { }} ><i className="setting-option-icon fa-solid fa-coins"></i><span className="setting-option-text">{t('content.home_page.setting.data_simulation')}</span></div>
                        <div className={type == 5 ? "setting-option-active" : "setting-option"} onClick={type != 5 ? () => { setType(5); setLoading(true) } : () => { }} ><i className="setting-option-icon fa-solid fa-coins"></i><span className="setting-option-text">{t('content.home_page.setting.landmark_plan')}</span></div>
                        <div className={type == 6 ? "setting-option-active" : "setting-option"} onClick={type != 6 ? () => { setType(6); setLoading(true) } : () => { }} ><i className="setting-option-icon fa-solid fa-circle-dollar-to-slot"></i><span className="setting-option-text">{t('content.home_page.chart.price')}</span></div>
                        <div className={type == 7 ? "setting-option-active" : "setting-option"} onClick={type != 7 ? () => { setType(7); setLoading(true) } : () => { }} ><i className="setting-option-icon fa-solid fa-circle-dollar-to-slot"></i><span className="setting-option-text">{"Nguyên liệu"}</span></div>
                    </div>
                </div>
                <div className='col-10' style={{ minHeight: '80vh', borderLeft: 'solid 1px rgb(165, 161, 161)' }}>
                    <div className="system-type">
                        <div className="radio-tabs">
                            <label className="radio-tabs__field text-uppercase" >
                                <input type="radio" name="radio-tabs" value={1} className="radio-tabs__input" checked={typeSystem == 1 ? true : false} onChange={(event) => setTypeSystem(event.target.value)} />
                                <span className="radio-tabs__text">
                                    <img src="/resources/image/icon-load-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                    {t('content.home_page.load')}</span>
                            </label>
                            <label className="radio-tabs__field text-uppercase" >
                                <input type="radio" name="radio-tabs" value={2} className="radio-tabs__input" checked={typeSystem == 2 ? true : false} onChange={(event) => setTypeSystem(event.target.value)} />
                                <span className="radio-tabs__text">
                                    <img src="/resources/image/icon-solar-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                    {t('content.home_page.solar')}</span>
                            </label>
                            <label className="radio-tabs__field text-uppercase" >
                                <input type="radio" name="radio-tabs" value={5} className="radio-tabs__input" checked={typeSystem == 5 ? true : false} onChange={(event) => setTypeSystem(event.target.value)} />
                                <span className="radio-tabs__text">
                                    <img src="/resources/image/icon-grid-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                    {t('content.home_page.grid')}</span>
                            </label>
                            <label className="radio-tabs__field text-uppercase">
                                <input type="radio" name="radio-tabs" value={3} className="radio-tabs__input" checked={typeSystem == 3 ? true : false} onChange={(event) => setTypeSystem(event.target.value)} />
                                <span className="radio-tabs__text">
                                    <img src="/resources/image/icon-battery-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                    {t('content.home_page.battery')}</span>
                            </label>
                            <label className="radio-tabs__field text-uppercase">
                                <input type="radio" name="radio-tabs" value={4} className="radio-tabs__input" checked={typeSystem == 4 ? true : false} onChange={(event) => setTypeSystem(event.target.value)} />
                                <span className="radio-tabs__text">
                                    <img src="/resources/image/icon-wind-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                    {t('content.home_page.wind')}</span>
                            </label>
                        </div>
                    </div>
                    <hr></hr>
                    <div className='' style={{ minHeight: '300px', }}>
                        {reRenderComponent()}
                    </div>
                </div>
            </div >
        </>
    );
};



export default SettingComponent;