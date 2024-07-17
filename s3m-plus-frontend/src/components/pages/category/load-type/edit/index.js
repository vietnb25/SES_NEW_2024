import React, { useEffect, useState } from 'react'
import { useHistory, useParams } from 'react-router-dom'
import areaService from '../../../../../services/AreaService';
import { useFormik } from 'formik';
import * as Yup from "yup";
import { useTranslation } from "react-i18next";
import LoadTypeService from "../../../../../services/LoadTypeService";
const EditLoadType = () => {
    const param = useParams();
    // const regex = /^\d+\.{0,1}\d{0,}$/;

    const $ = window.$;
    const [dataLoadTypeUpdate, setDataLoadTypeUpdate] = useState([]);


    const handleDetail = async () => {
        let customerId = 1;
        let res = await LoadTypeService.getLoadTypeById(customerId, param.id)
        if (res.status === 200) {
            setDataLoadTypeUpdate(() => res.data)
        };
    }

    const { t } = useTranslation();

    const handleUpdateLoadType = async () => {
        let response = await LoadTypeService.updateLoadType(1, dataLoadTypeUpdate);
        if (response.status === 200) {
            $.alert({
                title: t('content.notification'),
                content: t('content.category.load_type.list.edit_success')
            });
            history.push({
                pathname: "/category/load-type",
                state: {
                    status: 200,
                    message: "update_success"
                }
            });

        } else {

        }
    }


    const handleInputChange = (e) => {
        const { name, value } = e.target;

        setDataLoadTypeUpdate((prevData) => ({
            ...prevData,
            [name]: value,
            [e.target.name]: e.target.value
        }));
    };
    document.title = t('content.update_load_type')
    useEffect(() => {
        handleDetail();
    }, [])
    const history = useHistory();
    return (
        <div>
            <div id="page-body">
                <div id="main-title">
                    <h5 className="d-block mb-0 float-left"><i className="far fa-clone"></i> &nbsp;{t('content.category.load_type.edit.header')}</h5>
                </div>

                <div id="main-content">

                    <table className="table table-input">
                        <tbody>
                            <tr>
                                <th width="160px">{t('content.category.load_type.lable_load_type_name')}<span className="required">※</span></th>
                                <td>
                                    <input type="text" className="form-control" name="loadTypeName" style={{ wordWrap: "break-word" }}
                                        onChange={handleInputChange} value={dataLoadTypeUpdate.loadTypeName}
                                    />
                                </td>
                            </tr>
                            <tr>
                                <th width="160px">{t('content.category.load_type.lable_description')}<span className="required">※</span></th>
                                <td>
                                    <input type="text" className="form-control" name="description" style={{ wordWrap: "break-word" }}
                                        onChange={handleInputChange} value={dataLoadTypeUpdate.description}
                                    />
                                </td>
                            </tr>

                        </tbody>
                    </table>

                    <div id="main-button">
                        <button type="submit" className="btn btn-outline-secondary btn-agree mr-1" onClick={handleUpdateLoadType}>
                            <i className="fa-solid fa-check"></i>
                        </button>
                        <button type="button" className="btn btn-outline-secondary btn-cancel" onClick={() => history.push("/category/load-type")}>
                            <i className="fa-solid fa-xmark"></i>
                        </button>
                    </div>

                </div>


            </div>
        </div>
    )
}

export default EditLoadType;
