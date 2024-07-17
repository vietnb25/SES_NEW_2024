import React, { useEffect, useState } from 'react'
import { useHistory, useParams } from 'react-router-dom'
import areaService from '../../../../../services/AreaService';
import { useFormik } from 'formik';
import * as Yup from "yup";
import { useTranslation } from "react-i18next";
import ObjectTypeService from '../../../../../services/ObjectTypeService';
import ProjectService from '../../../../../services/ProjectService';
const EditObjectTypeMst = () => {
    const param = useParams();
    // const regex = /^\d+\.{0,1}\d{0,}$/;
    const [data, setData] = useState([]);
    const [errorsValidate, setErrorsValidate] = useState([]);
    const $ = window.$;
    const [manager, setManager] = useState([]);
    const [error, setError] = useState(null);
    const [dataSelect, setDataSelect] = useState([]);
    const [project, setProject] = useState([]);


    const [dataObjectUpdate, setDataObjectUpdate] = useState({
        id: '',
        name: '',
        img: ''
    });

    const getObjectTypeSelect = async () => {
        let response = await ObjectTypeService.getObjectType();
        if (response.status === 200) {
            setData(response.data);
        }
    }
    const handleDetail = async () => {
        let res = await ObjectTypeService.getObjectTypeById(param.id)
        if (res.status === 200) {
            console.log(res.data)
            setDataObjectUpdate(() => res.data)
        };

    }

    const { t } = useTranslation();

    const onInputChange = (e) => {
        setDataObjectUpdate({ ...dataObjectUpdate, [e.target.name]: e.target.value });
    }

    const formik = useFormik({
        initialValues: dataObjectUpdate,
        enableReinitialize: true,
        onSubmit: async (data) => {
            let res = await ObjectTypeService.editObjectType(data);
            if (res.status === 400) {
                setErrorsValidate(res.data.errors);
                return
            }
            if (res.status === 200) {
                $.alert({
                    title: t('content.title_notify'),
                    content: t('content.category.object_type.list.edit_success')
                });
                history.push({
                    pathname: "/category/object-type-mst",
                    state: {
                        status: 200,
                        message: "update_success"
                    }
                });

            } else {
                console.log("that bai");
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
                    setDataObjectUpdate({
                        ...dataObjectUpdate,
                        img: img
                    })
                    //   dataObjectUpdate.img = img;
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
        handleDetail();
        document.title = t('content.category.object_type.edit.header');
    }, [])
    const history = useHistory();
    return (
        <div>
            <div id="page-body">
                <div id="main-title">
                    <h5 className="d-block mb-0 float-left"><i className="far fa-clone"></i> &nbsp;{t('content.category.object_type.edit.title')}</h5>
                </div>

                <div id="main-content">
                    <form onSubmit={formik.handleSubmit}>
                        <table className="table table-input">
                            <tbody>
                                <tr>
                                    <th width="230px">{t('content.category.object_type.lable_object_name')} <span className="required">※</span></th>
                                    <td>
                                        <input type="text" className="form-control" name="name" style={{ wordWrap: "break-word" }} defaultValue={dataObjectUpdate.name}
                                            onChange={(e) => onInputChange(e)}
                                        />

                                    </td>
                                </tr>

                                <tr>
                                    <th width="220px">{t('content.category.object_type.image')} <span className="required">※</span></th>
                                    <td>
                                        <input type="file" name="img" id="file" value={dataObjectUpdate.file} onChange={(e) => getBase64(e)} />
                                    </td>
                                </tr>

                                <tr>
                                    <th width="150px">
                                        <p className="textSize"> - {t('content.img_size')}: {"<"} 10 mb<br></br>
                                            - {t('content.format')}: jpg, png, ... </p>
                                    </th>
                                    <td>
                                        {
                                            dataObjectUpdate.img == "" ?
                                                <></> :
                                                <img id="blah" src={dataObjectUpdate.img} alt="img" className="profileviewImage mt-2 mb-2" />
                                        }
                                    </td>
                                </tr>

                            </tbody>
                        </table>

                        <div id="main-button">
                            <button type="submit" className="btn btn-outline-secondary btn-agree mr-1">
                                <i className="fa-solid fa-check"></i>
                            </button>
                            <button type="button" className="btn btn-outline-secondary btn-cancel" onClick={() => history.push("/category/object-type-mst")}>
                                <i className="fa-solid fa-xmark"></i>
                            </button>
                        </div>
                    </form>
                </div>


            </div>
        </div>
    )
}

export default EditObjectTypeMst;
