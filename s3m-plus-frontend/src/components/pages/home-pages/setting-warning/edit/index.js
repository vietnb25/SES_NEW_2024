import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import SettingService from '../../../../../services/SettingService';
import moment from 'moment';
import CONS from '../../../../../constants/constant';


const FormUpdate = (props) => {
    const [svalue, setSvalue] = useState("");
    const { t } = useTranslation();
    const [edit, setEdit] = useState(true);
    const [sLevel, setSLevel] = useState(3);
    const [decription, setDecription] = useState("");
    const sendData = (status) => {
        props.parentCallback(status)
    }
    const [status, setStattus] = useState(true);

    const changSValueInput = (e) => {
        var char = e.target.value.slice(-1);
        const regex = new RegExp("^[0-9]*?$");
        if (regex.test(char) == true || e.target.value == "" || e.target.value.slice(-1) == "," || e.target.value.slice(-1) == ".") {
            setSvalue(e.target.value)
        }
    }
    const clickSave = async (id) => {
        const data = {
            "warningTypeId": id,
            "settingValue": svalue,
            "warningLevel": sLevel == null ? 3 : sLevel,
            "description": setDecriptionByValue(props.data.warningTypeId, svalue)
        }
        if (svalue != "") {
            console.log(props.devices);
            console.log(data);
            let res = await SettingService.updateSettingByDevices(props.devices, data);
            if (res == 200) {
                setStattus(!status);
            }
            sendData(res.status);
        } else {
            sendData(-1);
        }
    }

    const setDecriptionByValue = (warningType, value) => {
        var strs = value.split(",");
        
        if (warningType == 101) {
            return 'Uan || Ubn || Ucn > ' + strs[0]
        }
        if (warningType == 102) {
            return 'Uan || Ubn || Ucn < ' + strs[0]
        }
        if (warningType == 103) {
            return 'Ia || Ib || Ic >= ' + strs[0] + '* Imccb'
        }
        if (warningType == 104) {
            return '((Ia + Ib + Ic)/3) / (CongSuatThietBi*1.44) > ' + strs[0] + ' & cosA || cosB || cosC < ' + (strs[1] != undefined ? strs[1] : "-")
        }
        if (warningType == 105) {
            return 'F > ' + strs[0]
        }
        if (warningType == 106) {
            return 'F < ' + strs[0]
        }
        if (warningType == 107) {
            return '((Ia + Ib + Ic)/3) / (CongSuatThietBi*1.44) > ' + strs[0] + ' & (Imax – Imin)/Imin > ' + (strs[1] != undefined ? strs[1] : "-")
        }
        if (warningType == 108) {
            return 'IA_H1 || ... || IA_H10 || IB_H1 || ... || IB_H10 || IC_H1|| ...|| IC_H10 >= ' + strs[0] + '- IA_H11 || ... || IA_H16 || IB_H11 || ... || IB_H16 || IC_H11|| ...|| IC_H16 >= ' + (strs[1] != undefined ? strs[1] : "-") + '- IA_H17 || ... || IA_H22 || IB_H17 || ... || IB_H22 || IC_H17|| ...|| IC_H22 >= ' + (strs[2] != undefined ? strs[2] : "-") + '- IA_H23 || ... || IA_H34 || IB_H23 || ... || IB_H34 || IC_H23|| ...|| IC_H34 >= ' + (strs[3] != undefined ? strs[3] : "-")+ ' - IA_H35 || ... || IA_HN || IB_H34 || ... || IB_HN || IC_H34|| ...|| IC_HN >= ' + (strs[4] != undefined ? strs[4] : "-")
        }
        if (warningType == 109) {
            return 'VAN_H(n) || VBN_H(n) || VCN_H(n) >= ' + strs[0]
        }
        if (warningType == 110) {
            return 'THD_Van || THD_Vbn || THD_Vcn >= ' + strs[0]
        }
        if (warningType == 111) {
            return 'THD_Ia || THD_Ib || THD_Ic >= ' + strs[0]
        }
        if (warningType == 112) {
            return 'PFa|| PFb || PFc < ' + strs[0]
        }
        if (warningType == 113) {
            return 'Ua || Ub || Uc <= ' + strs[0] + '(V)'
        }
        if (warningType == 114) {
            return 'Uan || U1n || Ucn < ' + strs[0]
        }
        if (warningType == 115) {
            return 'Uan || U1n || Ucn < ' + strs[0]
        }
        if (warningType == 116) {
            return 'Uan || U1n || Ucn < ' + strs[0]
        }
        if (warningType == 117) {
            return 'Umax - Umin > 20 (V) & (UA,B,C> 90 (V))' + strs[0]
        }
        if (warningType == 201) {
            return 'Cảnh báo vận hành'
        }
        if (warningType == 202) {
            return 'Cảnh báo vận hành'
        }
        if (warningType == 203) {
            return 'Cảnh báo vận hành'
        }
        if (warningType == 204) {
            return 'Cảnh báo vận hành'
        }
        if (warningType == 205) {
            return 'Cảnh báo vận hành'
        }
        if (warningType == 206) {
            return 'Cảnh báo vận hành'
        }
        if (warningType == 207) {
            return 'Cảnh báo vận hành'
        }
        if (warningType == 208) {
            return 'Cảnh báo vận hành'
        }
        if (warningType == 209) {
            return 'Cảnh báo vận hành'
        }
        if (warningType == 210) {
            return 'Cảnh báo vận hành'
        }
        if (warningType == 211) {
            return 'Cảnh báo vận hành'
        }
        if (warningType == 212) {
            return 'Cảnh báo vận hành'
        }
        if (warningType == 213) {
            return 'Cảnh báo vận hành'
        }
        if (warningType == 214) {
            return 'Cảnh báo vận hành'
        }
        if (warningType == 215) {
            return 'Cảnh báo vận hành'
        }
        if (warningType == 216) {
            return 'Cảnh báo vận hành'
        }

        if (warningType == 301) {
            return 'Nhiệt độ (T) >= ' + strs[0] + '°C'
        }
        if (warningType == 302) {
            return 'Nhiệt độ (T) <= ' + strs[0] + '°C'
        }
        if (warningType == 303) {
            return 'Độ ẩm (Humidity) >= ' + strs[0] + '(%)'
        }
        if (warningType == 304) {
            return 'Độ ẩm (Humidity) <= ' + strs[0] + '(%)'
        }

        if (warningType == 401) {
            return 'Cảnh báo vận hành'
        }
        if (warningType == 402) {
            return 'Cảnh báo vận hành'
        }
        if (warningType == 403) {
            return 'Cảnh báo vận hành'
        }
        if (warningType == 404) {
            return 'Cảnh báo vận hành'
        }
        if (warningType == 405) {
            return 'Cảnh báo vận hành'
        }
        if (warningType == 406) {
            return 'Cảnh báo vận hành'
        }
        if (warningType == 407) {
            return 'Cảnh báo vận hành'
        }
        if (warningType == 408) {
            return 'Cảnh báo vận hành'
        }
        if (warningType == 409) {
            return 'Cảnh báo vận hành'
        }
        if (warningType == 410) {
            return 'Cảnh báo vận hành'
        }
        if (warningType == 411) {
            return 'Cảnh báo vận hành'
        }
        if (warningType == 412) {
            return 'Cảnh báo vận hành'
        }

        if (warningType == 501) {
            return 'Indicator  >=  ' + strs[0]
        }
        if (warningType == 601) {
            return 'Indicator  >=  ' + strs[0]
        }

    }
    useEffect(() => {
        setSvalue(props.lengthData > 1 ? "" : props.data.settingValue)
        setSLevel(props.lengthData > 1 ? "" : props.data.warningLevel)
    }, [])
    return (
        <tr height="25px">
            <td className="text-center">{props.stt}</td>
            <td style={{ wordWrap: "break-word" }}>{props.data.warningTypeName}</td>
            <td style={{ wordWrap: "break-word" }}>
                {edit == false ?
                    <div className="warning-level ml-1">
                        <div className="select-type">
                            <div className="form-check form-check-inline mr-5" >
                                <input className="form-check-input" type="radio" name={"levelW" + props.stt} id="inlineRadio1" value={1} defaultChecked={props.data.warningLevel == 1} style={{ transform: "scale(1.5)" }} onChange={((event) => setSLevel(event.target.value))} />
                                <label className="form-check-label " htmlFor="inlineRadio1">Thấp</label>
                            </div>
                            <div className="form-check form-check-inline mr-5" >
                                <input className="form-check-input" type="radio" name={"levelW" + props.stt} id="inlineRadio2" value={2} defaultChecked={props.data.warningLevel == 2} style={{ transform: "scale(1.5)" }} onChange={((event) => setSLevel(event.target.value))} />
                                <label className="form-check-label " htmlFor="inlineRadio2">Trung Bìnhh</label>
                            </div>
                            <div className="form-check form-check-inline mr-5" >
                                <input className="form-check-input" type="radio" name={"levelW" + props.stt} id="inlineRadio3" value={3} defaultChecked={props.data.warningLevel == 3} style={{ transform: "scale(1.5)" }} onChange={((event) => setSLevel(event.target.value))} />
                                <label className="form-check-label " htmlFor="inlineRadio3">Cao</label>
                            </div>
                        </div>
                    </div> : <div className={"level" + props.data.warningLevel}>
                    </div>}
            </td>
            <td style={{ wordWrap: "break-word" }}>
                <input type="text" style={{ cursor: "context-menu" }} value={svalue} onChange={(event) => changSValueInput(event)} className="input-shift-project" readOnly={edit === true} />
            </td>
            <td style={{ wordWrap: "break-word" }}>{props.lengthData > 1 ? props.data.descriptionMst : props.data.description}</td>
            <td className="text-center">{moment(props.data.updateDate).format(CONS.DATE_FORMAT)}</td>
            <td className="text-center">
                {edit === false ?
                    svalue == "" ? <a className="button-icon" title="Chỉnh sửa">
                        <i className="fa-solid fa-circle-check" style={{ color: "#808080", fontSize: '18px' }} onClick={() => {
                            setEdit(true)
                        }} ></i>
                    </a> :
                        <a className="button-icon" title="Chỉnh sửa">
                            <i className="fa-solid fa-circle-check" style={{ color: "#0A1A5C", fontSize: '18px' }} onClick={() => {
                                setEdit(true)
                                clickSave(props.data.warningTypeId)
                            }} ></i>
                        </a>
                    : <a className="button-icon" title="Chỉnh sửa" >
                        <i className="fas fa-edit" style={{ color: "#F37021", fontSize: '18px' }} onClick={() => setEdit(false)} ></i>
                    </a>

                }
            </td>
        </tr>
    );
};


export default FormUpdate;
