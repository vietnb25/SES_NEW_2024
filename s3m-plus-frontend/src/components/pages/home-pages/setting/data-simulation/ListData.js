import { data } from 'jquery';
import React, { useEffect, useState } from 'react';

const ListData = props => {
    const [edit, setEdit] = useState(true);
    const [editIndex, setEditIndex] = useState(0);
    const [btnEdit, setBtnEdit] = useState(true);
    const [year, setYear] = useState();
    const [t1, setT1] = useState();
    const [t2, setT2] = useState();
    const [t3, setT3] = useState();
    const [t4, setT4] = useState();
    const [t5, setT5] = useState();
    const [t6, setT6] = useState();
    const [t7, setT7] = useState();
    const [t8, setT8] = useState();
    const [t9, setT9] = useState();
    const [t10, setT10] = useState();
    const [t11, setT11] = useState();
    const [t12, setT12] = useState();
    const onClickButonEdit = (index) => {
        setBtnEdit(!btnEdit)
        setEditIndex(index);
    }
    const onClickButonCheck = (id) => {
        setBtnEdit(!btnEdit)
        setEditIndex(0);
        const data =
            {
                "year": year == undefined ? "" : year,
                "jan": t1 == undefined ? "" : t1,
                "mar": t3 == undefined ? "" : t2,
                "apr": t4 == undefined ? "" : t3,
                "may": t5 == undefined ? "" : t4,
                "jun": t6 == undefined ? "" : t5,
                "jul": t7 == undefined ? "" : t6,
                "aug": t8 == undefined ? "" : t7,
                "sep": t9 == undefined ? "" : t8,
                "oct": t10 == undefined ? "" : t9,
                "feb": t2 == undefined ? "" : t10,
                "nov": t11 == undefined ? "" : t11,
                "dec": t12 == undefined ? "" : t12,
                "updateDate": null,
                // "projectId": projectId,
                // "systemTypeId": systemTypeId,
                // "customer": param.customerId,
            }       
    }
    return (<>
        {props.listData.map((data, index) => {
            <tr height="25px" key={index + 1} id={index}>
                <td style={{ wordWrap: "break-word" }}>
                    <input type="number" style={{ cursor: "context-menu" }} defaultValue={data.year} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1} onChange={(event) => setYear(event.target.value)} />
                </td>
                <td style={{ wordWrap: "break-word" }}>
                    <input type="number" style={{ cursor: "context-menu" }} defaultValue={data.jan} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1} onChange={(event) => setT1(event.target.value)} />
                </td>
                <td style={{ wordWrap: "break-word" }}>
                    <input type="number" style={{ cursor: "context-menu" }} defaultValue={data.feb} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1} onChange={(event) => setT2(event.target.value)} />
                </td>
                <td style={{ wordWrap: "break-word" }}>
                    <input type="number" style={{ cursor: "context-menu" }} defaultValue={data.mar} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1} onChange={(event) => setT3(event.target.value)} />
                </td>
                <td style={{ wordWrap: "break-word" }}>
                    <input type="number" style={{ cursor: "context-menu" }} defaultValue={data.apr} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1} onChange={(event) => setT4(event.target.value)} />
                </td>
                <td style={{ wordWrap: "break-word" }}>
                    <input type="number" style={{ cursor: "context-menu" }} defaultValue={data.may} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1} onChange={(event) => setT5(event.target.value)} />
                </td>
                <td style={{ wordWrap: "break-word" }}>
                    <input type="number" style={{ cursor: "context-menu" }} defaultValue={data.jun} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1} onChange={(event) => setT6(event.target.value)} />
                </td>
                <td style={{ wordWrap: "break-word" }}>
                    <input type="number" style={{ cursor: "context-menu" }} defaultValue={data.jul} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1} onChange={(event) => setT7(event.target.value)} />
                </td>
                <td style={{ wordWrap: "break-word" }}>
                    <input type="number" style={{ cursor: "context-menu" }} defaultValue={data.aug} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1} onChange={(event) => setT8(event.target.value)} />
                </td>
                <td style={{ wordWrap: "break-word" }}>
                    <input type="number" style={{ cursor: "context-menu" }} defaultValue={data.sep} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1} onChange={(event) => setT9(event.target.value)} />
                </td>
                <td style={{ wordWrap: "break-word" }}>
                    <input type="number" style={{ cursor: "context-menu" }} defaultValue={data.oct} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1} onChange={(event) => setT10(event.target.value)} />
                </td>
                <td style={{ wordWrap: "break-word" }}>
                    <input type="number" style={{ cursor: "context-menu" }} defaultValue={data.nov} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1} onChange={(event) => setT11(event.target.value)} />
                </td>
                <td style={{ wordWrap: "break-word" }}>
                    <input type="number" style={{ cursor: "context-menu" }} defaultValue={data.dec} className="input-shift-project" readOnly={edit !== false && editIndex !== index + 1} onChange={(event) => setT12(event.target.value)} />
                </td>

                <td className="text-center" style={{ cursor: "context-menu" }}>
                    {data.updateDate}
                </td>
                <td className="text-center">
                    {props.btnEdit === false && editIndex == index + 1 ? <a className="button-icon" title="Chỉnh sửa" onClick={() => onClickButonCheck(data.id)}>
                        <i className="fa-solid fa-circle-check" style={{ color: "#0A1A5C", fontSize: '18px' }}></i>
                    </a> : <a className="button-icon" title="Chỉnh sửa" onClick={() => onClickButonEdit(index + 1)}>
                        <i className="fas fa-edit" style={{ color: "#F37021", fontSize: '18px' }}></i>
                    </a>}
                </td>
            </tr>

        })}

    </>);
};


export default ListData;