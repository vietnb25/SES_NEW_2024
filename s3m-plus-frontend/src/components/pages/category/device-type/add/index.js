import { useFormik } from "formik";
import React, { useEffect, useState } from "react";
import { useHistory } from "react-router-dom";
import $ from 'jquery';
import * as Yup from "yup";
import { useTranslation } from "react-i18next";
import ObjectTypeService from "../../../../../services/ObjectTypeService";
import ObjectService from "../../../../../services/ObjectService";
import { useParams } from "react-router-dom/cjs/react-router-dom.min";
import ProjectService from "../../../../../services/ProjectService";
import DeviceTypeService from "../../../../../services/DeviceTypeService";
import { Checkbox } from 'primereact/checkbox';

const initialValues = {
    name: "",
    objectTypeIds: "",
    img: ""
}

const AddDeviceType = () => {
    const param = useParams();
    const history = useHistory();
    const [errorsValidate, setErrorsValidate] = useState([]);
    const [error, setError] = useState(null);
    const { t } = useTranslation();
    const [objectType, setObjectType] = useState([]);
    const [objectTypeSelected, setObjectTypeSelected] = useState([]);


    const getObjectType = async () => {
        let resObjectType = await ObjectTypeService.listObjectType();
        if (resObjectType.status === 200) {
            setObjectType(resObjectType.data);
        }
    }

    const formik = useFormik({
        initialValues,
        onSubmit: async (data) => {
            let objectTypeIds = [];
            objectTypeSelected.forEach(objectType => {
                objectTypeIds.push(objectType.id);
            })
            data.objectTypeIds = objectTypeIds.toString();
            console.log("Data insert: ", data);
            let res = await DeviceTypeService.addDeviceType(data);
            if (res.status === 400) {
                setErrorsValidate(res.data.errors);
                return
            }
            if (res.status === 200) {
                $.alert({
                    title: t('content.title_notify'),
                    content: t('content.category.device_type.list.add_success')
                });
                history.push({
                    pathname: "/category/device-type",
                    state: {
                        status: 200,
                        message: "INSERT_SUCCESS"
                    }
                });
            } else {
                setError(t('content.category.device_type.add.error_add'))
            }

        }
    });

    const handleSelectAllObjectType = (e) => {
        if (e.checked) {
            setObjectTypeSelected(objectType);
        } else {
            setObjectTypeSelected([]);
        }
    }

    const handleSelectObjectType = (e) => {
        let objectTypeSelect = [...objectTypeSelected];
        if (e.checked) {
            objectTypeSelect.push(e.value);
        } else {
            objectTypeSelect = objectTypeSelect.filter(o => o.id !== e.value.id);
        }
        setObjectTypeSelected(objectTypeSelect);
    }

    function getBase64(event) {
        let file = event.target.files[0];
        var reader = new FileReader();
        reader.readAsDataURL(file);
        if (file.size < 10000000) {
            if (file.type === "image/jpeg" || file.type === "image/jpg" || file.type === "image/png" || file.type === "image/gif" || file.type === "svg" || file.type === "image/tiff" || file.type === "image/bmp" || file.type === "image/webp") {
                reader.onload = (e) => {
                    let img = e.target.result;
                    formik.setFieldValue("img", img);
                }
            } else {
                $.alert({
                    title: t('content.notification'),
                    content: t('content.error_format')

                });
            }

        } else {
            $.alert({
                title: t('content.notification'),
                content: t('content.10mb_img_size')

            });
        }
    }


    useEffect(() => {
        document.title = t('content.category.device_type.add.title');
        getObjectType();
    }, []);

    return (
        <div id="page-body">
            {
                (error != null) &&
                <div className="alert alert-danger" role="alert">
                    <p className="m-0 p-0">{t('content.category.device_type.add.error_add')}</p>
                </div>
            }
            {
                (errorsValidate.length > 0) &&
                <div className="alert alert-warning" role="alert">
                    {
                        errorsValidate.map((error, index) => {
                            return (<p key={index + 1} className="m-0 p-0">{error}</p>)
                        })
                    }
                </div>
            }
            <form onSubmit={formik.handleSubmit}>
                <div id="main-content">
                    <div id="main-title">
                        <h5 className="d-block mb-0 float-left text-uppercase"><i className="far fa-clone"></i> &nbsp;{t('content.category.device_type.add.title')}</h5>
                    </div>
                    <table className="table table-input">
                        <tbody>
                            <tr>
                                <th width="220px">{t('content.category.device_type.device_type_name')}<span className="required">※</span></th>
                                <td>
                                    <input type="text" name="name" className="form-control" onChange={formik.handleChange} />
                                </td>
                            </tr>

                            <tr>
                                <th width="220px">{t('content.object')}<span className="required">※</span></th>
                                <td>
                                    <div className="m-0 pb-2" >
                                        <Checkbox value={"objectTypeIds"} onChange={handleSelectAllObjectType} checked={objectType.length === objectTypeSelected.length} />
                                        <label className="ml-2 mr-3 mb-0">{t('content.all')}</label>
                                    </div>
                                    <div className="m-0 d-flex" style={{ flexWrap: "wrap" }}>
                                        {
                                            objectType.map((o, i) => {
                                                return (
                                                    <div key={i} className="mb-1">
                                                        <Checkbox checked={objectTypeSelected.some(ot => ot.id === o.id)} inputId={o.id} value={o} onChange={handleSelectObjectType} />
                                                        <label htmlFor={o.id} className="ml-2 mr-3 mb-2">{o.name}</label>
                                                    </div>
                                                )
                                            })
                                        }
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <th width="220px">{t('content.image')} <span className="required">※</span></th>
                                <td>
                                    <input type="file" name="img" id="file" onChange={(e) => getBase64(e)} />
                                </td>
                            </tr>
                            {
                                formik.values.img != "" ?
                                    <tr>
                                        <th width="150px">
                                            <p className="textSize"> - {t('content.img_size')}: {"<"} 10 mb<br></br>
                                                - {t('content.format')}: jpg, png, ... </p>
                                        </th>
                                        <td>
                                            {
                                                formik.values.img == "" ?
                                                    <></> :
                                                    <img id="blah" src={formik.values.img} alt="img" className="profileviewImage mt-2 mb-2" />
                                            }
                                        </td>
                                    </tr> : <></>
                            }
                        </tbody>
                    </table>

                    <div id="main-button">
                        <button type="submit" id="submitButton" className="btn btn-outline-secondary btn-agree mr-1" >
                            <i className="fa-solid fa-check"></i>
                        </button>
                        <button type="button" className="btn btn-outline-secondary btn-cancel" onClick={() => {
                            history.push("/category/device-type")
                        }}>
                            <i className="fa-solid fa-xmark"></i>
                        </button>
                    </div>
                </div>
            </form>
        </div>
    )
}

export default AddDeviceType;