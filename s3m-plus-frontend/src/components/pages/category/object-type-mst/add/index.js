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

const initialValues = {
    name: "",
    img: ""
}

const AddObjectTypeMst = () => {
    const param = useParams();
    const history = useHistory();
    const [errorsValidate, setErrorsValidate] = useState([]);
    const [error, setError] = useState(null);
    const { t } = useTranslation();
    const [data, setData] = useState([]);
    const [dataSelect, setDataSelect] = useState([]);
    const [project, setProject] = useState([]);


    const formik = useFormik({
        initialValues,
        onSubmit: async (data) => {
            let res = await ObjectTypeService.insertObjectType(data);
            if (res.status === 400) {
                setErrorsValidate(res.data.errors);
                return
            }
            if (res.status === 200) {
                $.alert({
                    title: t('content.title_notify'),
                    content: t('content.category.object_type.list.add_success')
                });
                history.push({
                    pathname: "/category/object-type-mst",
                    state: {
                        status: 200,
                        message: "INSERT_SUCCESS"
                    }
                });
            } else {
                setError(t('validate.cable.INSERT_FAILED'))
            }

        }
    });



    function getBase64(event) {
        let file = event.target.files[0];
        var reader = new FileReader();
        reader.readAsDataURL(file);
        if (file.size < 10000000) {
            if (file.type === "image/jpeg" || file.type === "image/jpg" || file.type === "image/png" || file.type === "image/gif" || file.type === "image/svg+xml" || file.type === "image/tiff" || file.type === "image/bmp" || file.type === "image/webp") {
                reader.onload = (e) => {
                    let img = e.target.result;
                    formik.setFieldValue("img", img);
                }
            } else {
                $.alert({
                    title: t('content.title_notify'),
                    content: t('content.error_format')
                });
            }

        } else {
            $.alert({
                title: t('content.title_notify'),
                content: t('content.10mb_img_size')

            });
        }
    }


    useEffect(() => {
        document.title = "Thêm mới loại đối tượng giám sát"
    }, []);

    return (
        <div id="page-body">
            {
                (error != null) &&
                <div className="alert alert-danger" role="alert">
                    <p className="m-0 p-0">{t('content.category.object_type.add.error_add')}</p>
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
                        <h5 className="d-block mb-0 float-left"><i className="far fa-clone"></i> &nbsp;{t('content.category.object_type.add.title')}</h5>
                    </div>
                    <table className="table table-input">
                        <tbody>
                            <tr>
                                <th width="220px">{t('content.category.object_type.lable_object_type_name')}  <span className="required">※</span></th>
                                <td>
                                    <input type="text" name="name" className="form-control" onChange={formik.handleChange} />
                                </td>
                            </tr>
                            <tr>
                                <th width="220px">{t('content.category.object_type.image')}  <span className="required">※</span></th>
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
                            history.push("/category/object-type-mst")
                        }}>
                            <i className="fa-solid fa-xmark"></i>
                        </button>
                    </div>
                </div>
            </form>
        </div>
    )
}

export default AddObjectTypeMst;