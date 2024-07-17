import { data } from 'jquery';
import moment from 'moment';
import React, { useEffect, useState } from 'react';
import landmarkAndPlanService from '../../../../../services/landmark-and-plan-service';
import { useParams } from 'react-router-dom/cjs/react-router-dom.min';


const FormLanmarks = (props) => {
    const params = useParams();
    const [edit, setEdit] = useState(true);
    const [t1, setT1] = useState("");
    const [t2, setT2] = useState("");
    const [t3, setT3] = useState("");
    const [t4, setT4] = useState("");
    const [t5, setT5] = useState("");
    const [t6, setT6] = useState("");
    const [t7, setT7] = useState("");
    const [t8, setT8] = useState("");
    const [t9, setT9] = useState("");
    const [t10, setT10] = useState("");
    const [t11, setT11] = useState("");
    const [t12, setT12] = useState("");


    const sendData = (status) => {
        props.parentCallback(status)
    }
    const updateData = async (data) => {
        if (props.type == 1) {
            let res = await landmarkAndPlanService.updateLandmark(data, params.customerId);
            sendData(res.status);
        } else {
            let res = await landmarkAndPlanService.updatePlan(data, params.customerId);
            sendData(res.status);
        }
    }

    const clickSave = (id) => {
        const data = {
            "dateOfWeek": props.data.dateOfWeek,
            "id": id,
            "jan": t1,
            "feb": t2,
            "mar": t3,
            "apr": t4,
            "may": t5,
            "jun": t6,
            "jul": t7,
            "aug": t8,
            "sep": t9,
            "oct": t10,
            "nov": t11,
            "dec": t12,
            "status": 1,
        }
        updateData(data);
    }
    return (
        <>
            {/* {(props.length) > 0 && */}
            <tr height="25px" >
                <td style={{ wordWrap: "break-word", textAlign: 'center', paddingTop: '1%' }} >
                    <p style={{ width: '100%', fontWeight: 'bold', fontSize: '14px', fontFamily: 'arial' }}>{props.date}</p>
                </td>
                <td style={{ wordWrap: "break-word" }}>
                    <input type="number" style={{ cursor: "context-menu" }} value={t1 == "" ? props.data.jan != undefined ? props.data.jan : "" : t1} className="input-shift-project" readOnly={edit === true} onChange={(event) => setT1(event.target.value)} />
                </td>
                <td style={{ wordWrap: "break-word" }}>
                    {((props.year % 4 === 0 && props.year % 100 !== 0) || props.year % 400 === 0) ?
                        (props.date > 29 ? "" :
                            <input type="number" style={{ cursor: "context-menu" }} value={t2 == "" ? (props.data.feb != undefined ? props.data.feb : "") : t2} className="input-shift-project" readOnly={edit === true} onChange={(event) => setT2(event.target.value)} />
                        )
                        :
                        (props.date > 28 ? "" :
                            <input type="number" style={{ cursor: "context-menu" }} value={t2 == "" ? (props.data.feb != undefined ? props.data.feb : "") : t2} className="input-shift-project" readOnly={edit === true} onChange={(event) => setT2(event.target.value)} />
                        )

                    }



                </td>
                <td style={{ wordWrap: "break-word" }}>
                    <input type="number" style={{ cursor: "context-menu" }} value={t3 == "" ? (props.data.mar != undefined ? props.data.mar : "") : t3} className="input-shift-project" readOnly={edit === true} onChange={(event) => setT3(event.target.value)} />
                </td>
                <td style={{ wordWrap: "break-word" }}>
                    {props.date > 30 ? "" :
                        <input type="number" style={{ cursor: "context-menu" }} value={t4 == "" ? (props.data.apr != undefined ? props.data.apr : "") : t4} className="input-shift-project" readOnly={edit === true} onChange={(event) => setT4(event.target.value)} />}
                </td>
                <td style={{ wordWrap: "break-word" }}>
                    <input type="number" style={{ cursor: "context-menu" }} value={t5 == "" ? (props.data.may != undefined ? props.data.may : "") : t5} className="input-shift-project" readOnly={edit === true} onChange={(event) => setT5(event.target.value)} />
                </td>
                <td style={{ wordWrap: "break-word" }}>
                    {props.date > 30 ? "" :
                        <input type="number" style={{ cursor: "context-menu" }} value={t6 == "" ? (props.data.jun != undefined ? props.data.jun : "") : t6} className="input-shift-project" readOnly={edit === true} onChange={(event) => setT6(event.target.value)} />}
                </td>
                <td style={{ wordWrap: "break-word" }}>
                    <input type="number" style={{ cursor: "context-menu" }} value={t7 == "" ? (props.data.jul != undefined ? props.data.jul : "") : t7} className="input-shift-project" readOnly={edit === true} onChange={(event) => setT7(event.target.value)} />
                </td>
                <td style={{ wordWrap: "break-word" }}>
                    <input type="number" style={{ cursor: "context-menu" }} value={t8 == "" ? (props.data.aug != undefined ? props.data.aug : "") : t8} className="input-shift-project" readOnly={edit === true} onChange={(event) => setT8(event.target.value)} />
                </td>
                <td style={{ wordWrap: "break-word" }}>
                    {props.date > 30 ? "" :
                        <input type="number" style={{ cursor: "context-menu" }} value={t9 == "" ? (props.data.sep != undefined ? props.data.sep : "") : t9} className="input-shift-project" readOnly={edit === true} onChange={(event) => setT9(event.target.value)} />}
                </td>
                <td style={{ wordWrap: "break-word" }}>
                    <input type="number" style={{ cursor: "context-menu" }} value={t10 == "" ? (props.data.oct != undefined ? props.data.oct : "") : t10} className="input-shift-project" readOnly={edit === true} onChange={(event) => setT10(event.target.value)} />
                </td>
                <td style={{ wordWrap: "break-word" }}>
                    {props.date > 30 ? "" :
                        <input type="number" style={{ cursor: "context-menu" }} value={t11 == "" ? (props.data.nov != undefined ? props.data.nov : "") : t11} className="input-shift-project" readOnly={edit === true} onChange={(event) => setT11(event.target.value)} />}
                </td>
                <td style={{ wordWrap: "break-word" }}>
                    <input type="number" style={{ cursor: "context-menu" }} value={t12 == "" ? (props.data.dec != undefined ? props.data.dec : "") : t12} className="input-shift-project" readOnly={edit === true} onChange={(event) => setT12(event.target.value)} />
                </td>
                <td className="text-center" style={{ cursor: "context-menu" }}>
                    {props.data.updateDate}
                </td>
                <td className="text-center">
                    {edit === false ? <a className="button-icon" title="Chỉnh sửa">
                        <i className="fa-solid fa-circle-check" style={{ color: "#0A1A5C", fontSize: '18px' }} onClick={() => {
                            setEdit(true)
                            clickSave(props.data.id)
                        }} ></i>
                    </a>
                        : <a className="button-icon" title="Chỉnh sửa" >
                            <i className="fas fa-edit" style={{ color: "#F37021", fontSize: '18px' }} onClick={() => setEdit(false)} ></i>
                        </a>

                    }
                </td>
            </tr>
            {/* } */}

        </>
    );
};



export default FormLanmarks;
