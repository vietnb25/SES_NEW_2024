import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useLocation, useParams } from "react-router-dom/cjs/react-router-dom.min";
import AuthService from "../../../../../services/AuthService";
import ProjectService from "../../../../../services/ProjectService";
import CustomerService from "../../../../../services/CustomerService";
import { useEffect } from "react";
import './index.css'
import ObjectTypeService from "../../../../../services/ObjectTypeService";
import ReactModal from "react-modal";
import * as Yup from "yup";
import $ from "jquery";
import { isAllOf } from "@reduxjs/toolkit";
const ListObjectType = () => {
    const $ = window.$;
    const param = useParams();
    const { t } = useTranslation();
    const history = useHistory();
    const [error, setError] = useState(null);
    const location = useLocation();
    const [changeTabUser, setChangeTabUser] = useState(true);
    const [isLoading, setIsLoading] = useState(true);
    const [customers, setCustomers] = useState([]);
    const [customersSearch, setCustomersSearch] = useState([]);
    const [customerId, setCustomerId] = useState(1);
    const [projectId, setProjectId] = useState("");
    const [statusInput, setStatusInput] = useState(true);
    const [role] = useState(AuthService.getRoleName());
    const [active, setActiveMenu] = useState(0);
    const [projects, setProjects] = useState([]);
    const [showListProject, setShowListProject] = useState();
    const [linkTo, setLinkTo] = useState(`/${customerId}`)
    const [customersDefault, setCustomersDefault] = useState([])
    const [systemTypeId, setSystemTypeId] = useState(1);
    const [profiledata, setProfiledata] = useState({
        img: null,
        objectTypeName: "",
        objectTypeId: "",
        projectId: ""
    });
    const onInputChange = (e) => {
        setProfiledata({ ...profiledata, [e.target.name]: e.target.value });
    }
    const getCustomers = async () => {
        setStatusInput(true);
        let res = await CustomerService.getListCustomer();
        if (res.status === 200 && res.data !== '') {
            setCustomers(res.data);
            setCustomersDefault(res.data);
        }
        setIsLoading(false);
    }

    const onLoadCustomer = async (_customerId) => {
        console.log(_customerId);
        if (showListProject === _customerId) {
            setShowListProject(0)
            setProjects([])
        } else {
            setProjects([])
            setShowListProject(_customerId)
        }
        setProjectId(0)
        setCustomerId(_customerId);
        let customerId = _customerId;
        let pathname = window.location.pathname;
        console.log(pathname);
        await getListProjectByCustomerId(customerId)

    }

    const getListProjectByCustomerId = async (customerId) => {
        let res = await ProjectService.getProjectByCustomerId(customerId)
        if (res.status === 200) {
            setProjects(res.data)
            console.log(res.data);
        }
    }

    const onLoadProject = async (iProjectId) => {
        setProjectId(iProjectId);
        console.log(iProjectId);
        let pathname = window.location.pathname;
        let arrPathName = pathname.split("/");
        console.log(arrPathName);
        setLinkTo(`/${customerId}/${iProjectId}`)
    }

    const funcSetSystemType = (e) => {
        setSystemTypeId(() => e.target.value)
        console.log("SystemType", e.target.value)
    }

    const [projectIdSelected, setProjectIdSelected] = useState();
    //LIST DATA
    const funcGetSiteByCustomerId = async () => {
        let cusId = param.customerId;

        let projectId = null;
        console.log("cusId", cusId);
        setCustomerId(() => cusId);
        let res = await ProjectService.getProjectByCustomerId(cusId);
        if (res.status === 200) {
            setProjects(() => res.data);
            if (res.data.length > 0) {
                setProjectIdSelected(() => res.data[0].projectId);
                projectId = res.data[0].projectId;
            }
        }
        funcGetObjectType(projectId);
    };



    //LIST DATA
    const [data, setData] = useState([]);
    const funcGetObjectType = async () => {
        $("#table").hide();
        $("#loading").show();
        let res = await ObjectTypeService.getObjectType(1);
        if (res.status === 200) {
            setData(() => res.data);
            console.log(res.data);
            $("#loading").hide();
            $("#table").show();
        }
    }

    //MODAL DETAIL
    const [isModalDetailOpen, setIsModalDetailOpen] = useState(false);
    const openModalDetail = () => {
        setIsModalDetailOpen(true);
    };

    const closeModalDetail = () => {
        setIsModalDetailOpen(false);
    };
    const [dataObjectTypeUpdate, setDataObjectTypeUpdate] = useState([]);
    const handleClickObjectType = async (objectTypeId) => {
        let customerId = 1;
        let res = await ObjectTypeService.getObjectTypeById(customerId, objectTypeId)
        console.log(objectTypeId);
        if (res.status === 200) {
            console.log(res.data)
            console.log("ngu")
            setDataObjectTypeUpdate(() => res.data)
            openModalDetail()
        };
    }

    //MODAL ADD
    const [isModalAddOpen, setIsModalAddOpen] = useState(false);
    const openModalAdd = () => {
        setIsModalAddOpen(true);
    };

    const closeModalAdd = () => {
        setIsModalAddOpen(false);
    };
    const handleClickAdd = () => {
        openModalAdd(true);
    };

    function getBase64(event) {
        let file = event.target.files[0];
        var reader = new FileReader();
        reader.readAsDataURL(file);
        if (file.size < 10000000) {
            if (
                file.type === "image/jpeg" ||
                file.type === "image/jpg" ||
                file.type === "image/png" ||
                file.type === "image/gif" ||
                file.type === "image/svg" ||
                file.type === "image/tiff" ||
                file.type === "image/bmp" ||
                file.type === "image/webp"
            ) {
                reader.onload = (e) => {
                    let img = e.target.result;
                    setProfiledata({
                        ...profiledata,
                        img: img
                    });
                };
            } else {
                $.alert({
                    title: 'Thông báo',
                    content: 'Định dạng ảnh không hợp lệ (vd: image.jpg, image.png, ...)'
                });
            }
        } else {
            $.alert({
                title: 'Thông báo',
                content: 'Size ảnh không được quá 10mb'
            });
        }
    }

    function getBase64Update(event) {
        let file = event.target.files[0];
        var reader = new FileReader();
        reader.readAsDataURL(file);
        if (file.size < 10000000) {
            if (
                file.type === "image/jpeg" ||
                file.type === "image/jpg" ||
                file.type === "image/png" ||
                file.type === "image/gif" ||
                file.type === "image/svg" ||
                file.type === "image/tiff" ||
                file.type === "image/bmp" ||
                file.type === "image/webp"
            ) {
                reader.onload = (e) => {
                    let img = e.target.result;
                    setDataObjectTypeUpdate({
                        ...dataObjectTypeUpdate,
                        img: img
                    });
                };
            } else {
                $.alert({
                    title: 'Thông báo',
                    content: 'Định dạng ảnh không hợp lệ (vd: image.jpg, image.png, ...)'
                });
            }
        } else {
            $.alert({
                title: 'Thông báo',
                content: 'Size ảnh không được quá 10mb'
            });
        }
    }


    const initialValues = {
        objectTypeName: "",
        img: "",
        projectId: "",
        systemTypeId: 2,
        typeDefault: "0"
    }




    const addObjectType = async (e) => {
        let cusId = param.customerId
        let res = await ObjectTypeService.addObjectType(1, profiledata);
        console.log(data);
        if (res.status === 200) {
            $.alert({
                title: 'Thông báo',
                content: 'Đã thêm thành công'
            });
            console.log("HIHIHIHI them thanh cong");
            funcGetObjectType()
            closeModalAdd()
        } else {
            setError(t('validate.area.INSERT_FAILED'));
        }
    }

    const updateObjectType = async (e) => {
        let cusId = param.customerId
        let res = await ObjectTypeService.updateObjectType(1, dataObjectTypeUpdate);
        console.log(data);
        console.log(dataObjectTypeUpdate.objectTypeId);
        if (res.status === 200) {
            $.alert({
                title: 'Thông báo',
                content: 'Đã update thành công'
            });
            console.log("HIHIHIHI update thanh cong");

            funcGetObjectType()
            closeModalDetail()
        } else {
            setError(t('validate.area.UPDATE_FAILED'));
        }
    }

    //UPDATE
    const handleInputChange = (e) => {
        console.log('handleInputChange called'); // Thêm dòng này
        const { name, value } = e.target;

        setDataObjectTypeUpdate((prevData) => ({
            ...prevData,
            [name]: value,
            [e.target.name]: e.target.value
        }));
    };

    //DELETE
    // const deleteObjectType = async (e) => {
    //     let res = await ObjectTypeService.deleteObjectTypeById(1, dataObjectTypeUpdate);
    //     if (res.status === 200) {
    //         $.alert({
    //             title: 'Thông báo',
    //             content: 'Đã xóa thành công'
    //         });
    //         console.log("HIHIHIHI xóa thanh cong");

    //         funcGetObjectType()
    //         closeModalDetail()
    //     } else {
    //         setError(t('validate.area.UPDATE_FAILED'));
    //     }
    // }

    const handleDeletePlanById = () => {
        $.confirm({
            type: 'red',
            typeAnimated: true,
            icon: 'fa fa-warning',
            title: 'Xác nhận!',
            content: 'Bạn có chắc chắn muốn xóa?',
            buttons: {
                confirm: {
                    text: 'Đồng ý',
                    action: async () => {
                        let customerId = param.customerId;
                        let res = await ObjectTypeService.deleteObjectTypeById(1, dataObjectTypeUpdate);
                        if (res.status === 200) {
                            $.alert({
                                title: 'Thông báo',
                                content: 'Đã xóa thành công!'
                            });
                            console.log("Xóa thành công");

                        } else {
                            $.alert({
                                type: 'red',
                                title: 'Thông báo',
                                content: 'Lỗi không xác định!(có thể quận huyện đang được sử dụng ở nơi khác)'
                            });
                        }
                        funcGetObjectType()
                        closeModalDetail()
                    }
                },
                cancel: {
                    text: 'Hủy bỏ'
                }
            }
        })

    };


    useEffect(() => {
        document.title = "Cảnh báo";
        funcGetSiteByCustomerId()
        console.log(param.customerId);
        // console.log(param.projectId);
    }, [param.customerId]);

    useEffect(() => {
        getCustomers();

        document.title = "Trang chủ";

    }, [changeTabUser]);


    //     <div className="customer-list">
    //     {
    //         customers?.map((item, index) => (
    //             <div key={index}>
    //                 <button onClick={() => onLoadCustomer(item.customerId)} className={(customerId === index && index === 0) || (customerId == item.customerId) ? "btn btn-block text-left btn-cus btn-cus-active mt-1" : "btn btn-block text-left btn-cus mt-1"} data-bs-toggle="tooltip" data-bs-placement="right" title={item.customerName}>
    //                     <i className={(customerId === index && index === 0) || (customerId == item.customerId) ? "fa-solid fa-user" : "fa-solid fa-user"} style={{ height: 20 }}></i>&nbsp; <span>{item.customerName}</span>
    //                 </button>

    //             </div>
    //         ))

    //     }

    // </div>


    const handleSystemTypeClick = (id) => {
        setSystemTypeId(id);
    };

    const handleShowAll = () => {
        setSystemTypeId(0);

    };


    return (
        <>
            <div id="page-body" style={{ display: "flex", width: "100%" }} >
                <div id="main-content"  >
                    {role !== "ROLE_MOD" && <>
                        {changeTabUser === true && <div id="project-list" className="tab-show">
                            <button type="button" className="btn btn-light btn-change-tab" ><i className="fas fa-bars"></i></button>
                            <hr className="mt-1 mb-1" />
                            <input type="text" id="keyword" name="keyword" className="input mt-0 mb-1 w-100" placeholder="Tìm kiếm ........." />
                            <i className="fa fa-search" id="icon-search"></i>
                            {
                                isLoading ?
                                    <div className="d-flex justify-content-center">
                                        <div className="spinner-border spinner-border-sm" role="status">
                                            <span className="sr-only">Loading...</span>
                                        </div>
                                    </div> :
                                    <div className="customer-list">
                                        {
                                            customers?.map((item, index) => (
                                                <div key={index}>
                                                    <button onClick={() => onLoadCustomer(item.customerId)} className={(customerId === index && index === 0) || (customerId == item.customerId) ? "btn btn-block text-left btn-cus btn-cus-active mt-1" : "btn btn-block text-left btn-cus mt-1"} data-bs-toggle="tooltip" data-bs-placement="right" title={item.customerName}>
                                                        <i className={(customerId === index && index === 0) || (customerId == item.customerId) ? "fa-solid fa-user" : "fa-solid fa-user"} style={{ height: 20 }}></i>&nbsp; <span>{item.customerName}</span>
                                                    </button>
                                                    {showListProject === item.customerId && projects?.map((project, i) => (
                                                        <div key={i} style={{ width: "170px", float: "right" }}>
                                                            <button onClick={() => onLoadProject(project.projectId)} className={projectId === project.projectId ? "btn btn-block text-left btn-cus btn-cus-active mt-1" : "btn btn-block text-left btn-cus mt-1"} data-bs-toggle="tooltip" data-bs-placement="right" title={i.projectName}>
                                                                <i className={projectId === project.projectId ? "fa-solid fa-user" : "fa-solid fa-user"} style={{ height: 20 }}></i>&nbsp; <span>{project.projectName}</span>
                                                            </button>
                                                        </div>
                                                    ))
                                                    }
                                                </div>
                                            ))
                                        }

                                    </div>
                            }
                        </div>
                        }

                    </>
                    }

                </div>
                <div className="tab-container tab-overview" >
                    <div className="div-container-right" >

                        <div className="content-header">
                            <div className="header-left">
                                <div className="title" style={{ width: "270px" }} >
                                    <div>ĐỐI TƯỢNG GIÁM SÁT</div>
                                </div>
                            </div>

                        </div>
                        <div className="content" >
                            <div className="div-content-left" style={{ width: "1437px", minHeight: "1200px" }}>
                                <>
                                    <div>
                                        <div className="input-group float-left mr-1" style={{ width: "100%", marginLeft: "8px" }}>
                                            <div className="radio-tabs">
                                                <label className="radio-tabs__field" style={{ width: "170px" }} onClick={handleShowAll}>
                                                    <input type="radio" name="radio-tabs-1" value={1} className="radio-tabs__input-1" defaultChecked />
                                                    <span className="radio-tabs__text" style={{ width: "160px" }}>
                                                        <img src="/resources/image/icon-load-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                                        Tất cả</span>
                                                </label>

                                                <label className="radio-tabs__field" style={{ width: "170px" }} onClick={() => handleSystemTypeClick(3)}>
                                                    <input type="radio" name="radio-tabs-1" value={1} className="radio-tabs__input-1" />
                                                    <span className="radio-tabs__text" style={{ width: "160px" }}>
                                                        <img src="/resources/image/icon-load-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                                        {t('content.home_page.load')}</span>
                                                </label>
                                                <label className="radio-tabs__field" style={{ width: "170px" }} onClick={() => handleSystemTypeClick(2)}>
                                                    <input type="radio" name="radio-tabs-1" value={2} className="radio-tabs__input-1" />
                                                    <span className="radio-tabs__text" style={{ width: "160px" }}>
                                                        <img src="/resources/image/icon-solar-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                                        {t('content.home_page.solar')}</span>
                                                </label>
                                                <label className="radio-tabs__field" style={{ width: "170px" }} onClick={() => handleSystemTypeClick(1)}>
                                                    <input type="radio" name="radio-tabs-1" value={5} className="radio-tabs__input-1" />
                                                    <span className="radio-tabs__text" style={{ width: "160px" }}>
                                                        <img src="/resources/image/icon-grid-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                                        {t('content.home_page.grid')}</span>
                                                </label>
                                                <label className="radio-tabs__field" style={{ width: "170px" }} onChange={(e) => funcSetSystemType(e)}>
                                                    <input type="radio" name="radio-tabs-1" value={3} className="radio-tabs__input-1" />
                                                    <span className="radio-tabs__text" style={{ width: "160px" }}>
                                                        <img src="/resources/image/icon-battery-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                                        {t('content.home_page.battery')}</span>
                                                </label>
                                                <label className="radio-tabs__field" style={{ width: "170px" }} onChange={(e) => funcSetSystemType(e)}>
                                                    <input type="radio" name="radio-tabs-1" value={4} className="radio-tabs__input-1" />
                                                    <span className="radio-tabs__text" style={{ width: "160px" }}>
                                                        <img src="/resources/image/icon-wind-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                                        {t('content.home_page.wind')}</span>
                                                </label>
                                                <label className="radio-tabs__field" style={{ width: "170px" }} onChange={(e) => funcSetSystemType(e)}>
                                                    <input type="radio" name="radio-tabs-1" value={6} className="radio-tabs__input-1" />
                                                    <span className="radio-tabs__text" style={{ width: "160px" }}>
                                                        <img src="/resources/image/icon-wind-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                                                        Máy phát điện</span>
                                                </label>
                                            </div>
                                        </div>
                                    </div>
                                    <div>
                                        <div className="title-up-warning text-left" style={{ backgroundColor: "rgb(226, 223, 223)", marginLeft: "8px" }}>
                                            <div style={{ marginTop: "-32px", color: "white" }}><i className="fa-solid fa-triangle-exclamation ml-1" style={{ color: "#fff" }}></i> ĐỐI TƯỢNG MẶC ĐỊNH
                                            </div>
                                        </div>
                                    </div>

                                    <div className="content">

                                        <div className="box-row">
                                            {data?.map((item, index) => (
                                                <div hidden={item.typeDefault !== 1 ? true : false} className="box-object" onClick={() => handleClickObjectType(item.objectTypeId)}>
                                                    <div className="image-object">
                                                        <img src={item.img} alt="Object Image" />
                                                    </div>
                                                    <div className="title-object">
                                                        <p className="title-main">{item.objectTypeName}</p>
                                                    </div>
                                                </div>


                                                // Không render phần tử nếu không thoả điều kiện
                                            ))}
                                        </div>

                                        <div id="chartdiv" style={{ height: "100%" }}></div>


                                    </div>

                                    <div>
                                        <div className="title-up-warning text-left" style={{ backgroundColor: "rgb(226, 223, 223)", marginLeft: "8px" }}>
                                            <div style={{ marginTop: "-32px", color: "white" }}><i className="fa-solid fa-triangle-exclamation ml-1" style={{ color: "#fff" }}></i> ĐỐI TƯỢNG THÊM
                                            </div>
                                        </div>
                                    </div>
                                    <div className="content" >


                                        <div className="box-row">
                                            <div className="box-object" style={{ backgroundColor: "#b5b5b5" }}>
                                                <div className="image-object" onClick={handleClickAdd}>
                                                    <i className="fa-solid fa-plus fa-10x" style={{ color: "#fff", marginTop: "50px" }}>
                                                    </i>
                                                </div>
                                            </div>
                                            {/* <div className="image-add" onClick={handleClickAdd}> */}
                                            {/* <svg width="256px" height="256px" viewBox="0 0 24.00 24.00" fill="none" xmlns="http://www.w3.org/2000/svg" stroke="#a60707" transform="matrix(-1, 0, 0, -1, 0, 0)" stroke-width="0.00024000000000000003"><g id="SVGRepo_bgCarrier" stroke-width="0"></g><g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g><g id="SVGRepo_iconCarrier"> <path fill-rule="evenodd" clip-rule="evenodd" d="M12 22C7.28595 22 4.92893 22 3.46447 20.5355C2 19.0711 2 16.714 2 12C2 7.28595 2 4.92893 3.46447 3.46447C4.92893 2 7.28595 2 12 2C16.714 2 19.0711 2 20.5355 3.46447C22 4.92893 22 7.28595 22 12C22 16.714 22 19.0711 20.5355 20.5355C19.0711 22 16.714 22 12 22ZM12 8.25C12.4142 8.25 12.75 8.58579 12.75 9V11.25H15C15.4142 11.25 15.75 11.5858 15.75 12C15.75 12.4142 15.4142 12.75 15 12.75H12.75L12.75 15C12.75 15.4142 12.4142 15.75 12 15.75C11.5858 15.75 11.25 15.4142 11.25 15V12.75H9C8.58579 12.75 8.25 12.4142 8.25 12C8.25 11.5858 8.58579 11.25 9 11.25H11.25L11.25 9C11.25 8.58579 11.5858 8.25 12 8.25Z" fill="#aaadb6"></path> </g></svg> */}
                                            {/* </div> */}
                                            {data?.map((item, index) => {
                                                if (item.systemTypeId === systemTypeId || systemTypeId === 0) {
                                                    return (
                                                        <div hidden={item.typeDefault === 1 ? true : false} className="box-object" onClick={() => handleClickObjectType(item.objectTypeId)}>
                                                            <div className="image-object">
                                                                <img src={item.img} />
                                                            </div>

                                                            <div className="title-object">
                                                                <p className="title-main">{item.objectTypeName}</p>
                                                                {/* <p className="title-number">9</p> */}
                                                            </div>
                                                        </div>
                                                    )
                                                }
                                                return null;
                                            })}
                                        </div>
                                        <div id="chartdiv" style={{ height: "100%" }}></div>
                                    </div>
                                </>
                            </div>

                        </div>
                    </div>
                </div >
            </div>

            {/* MODAL ADD */}
            <ReactModal
                isOpen={isModalAddOpen}
                onRequestClose={closeModalAdd}

                style={{
                    content: {
                        width: "50%", // Kích thước chiều rộng của modal (có thể điều chỉnh)
                        height: "80%", // Kích thước chiều cao của modal (có thể điều chỉnh)
                        margin: "auto", // Căn giữa modal
                        marginTop: "20px",
                    },
                }}
            >

                <h2
                    style={{
                        textAlign: "center",
                        backgroundColor: "#0A1A5C",
                        color: "#fff",
                        width: "100%",
                        padding: "5px", // Thay đổi kích thước màu nền bằng padding

                    }}
                >
                    THÊM ĐỐI TƯỢNG
                </h2>
                <br />

                <table className="table">
                    <tbody>
                        <tr>
                            <th scope="row">ID</th>
                            <td className="col-10">
                                <input
                                    type="text"
                                    name="objectTypeId"
                                    placeholder="ID: "
                                    style={{ width: "100%" }}
                                    onChange={(e) => onInputChange(e)}
                                    disabled
                                />
                            </td>
                        </tr>
                        <br />
                        <tr>
                            <th>Dự án</th>
                            <td>
                                <select className="form-select" style={{ width: "100%" }} name="projectId" required onChange={(e) => onInputChange(e)}>

                                    <option value="">Chọn một dự án</option>
                                    {projects.map((item, index) => (
                                        <option key={index} value={item.projectId}>
                                            {item.projectName}
                                        </option>
                                    ))}
                                </select>
                            </td>
                        </tr>
                        <br />

                        <tr>
                            <th scope="row">Tên đối đượng</th>
                            <td className="col-10">
                                <input
                                    type="text"
                                    name="objectTypeName"
                                    className="form-control"
                                    placeholder="Tên đối tượng: "
                                    style={{ width: "100%" }}
                                    onChange={(e) => onInputChange(e)}

                                />
                            </td>
                        </tr>
                        <tr hidden={true}>
                            <th scope="row">System Type Id</th>
                            <td className="col-10">
                                <input
                                    type="text"
                                    name="systemTypeId"
                                    className="form-control"
                                    placeholder="Tên đối tượng: "
                                    style={{ width: "100%" }}
                                    value="2"

                                />
                            </td>
                        </tr>
                        <tr hidden={true}>
                            <th scope="row">Type Default</th>
                            <td className="col-10">
                                <input
                                    type="text"
                                    name="typeDefault"
                                    className="form-control"
                                    placeholder="Tên đối tượng: "
                                    style={{ width: "100%" }}
                                    value="0"

                                />
                            </td>
                        </tr>
                        <tr hidden={true}>
                            <th scope="row">Delete Flag</th>
                            <td className="col-10">
                                <input
                                    type="text"
                                    name="deleteFlag"
                                    className="form-control"
                                    placeholder="Tên đối tượng: "
                                    style={{ width: "100%" }}
                                    value="0"

                                />
                            </td>
                        </tr>
                        <br />
                        <tr>
                            <th scope="row">Ảnh</th>
                            <td className="col-10">
                                <input type="file" name="file" id="file" value={profiledata.file} onChange={(e) => getBase64(e)} />
                                <br />
                                {
                                    profiledata.img == null ?
                                        <img id="blah" src="/resources/image/no_avatar.png" alt="avt" className="profileviewImage mt-2 mb-2" style={{ maxHeight: "200px" }} /> :
                                        <img id="blah" src={profiledata.img} alt="avt" className="profileviewImage mt-2 mb-2" style={{ width: "100%", height: "300px" }} />
                                }

                            </td>
                        </tr>
                    </tbody>
                </table>
                <br />
                <div className="row">
                    <div className="col-4 mr-5"></div>
                    <div className="col-0">
                        <button
                            type="submit"
                            style={{
                                backgroundColor: "#0A1A5C",
                                color: "#fff",
                                width: "70px",
                                height: "40px",
                            }}
                            onClick={addObjectType}
                        >
                            Lưu OK
                        </button>
                    </div>
                    <div className="col-1"></div>
                    <div className="col-0">
                        <button
                            style={{
                                backgroundColor: "#9DA3BE",
                                color: "#fff",
                                width: "70px",
                                height: "40px",
                            }}
                            onClick={closeModalAdd}
                        >
                            Đóng
                        </button>
                    </div>
                    <div className="col-5"></div>
                </div>

            </ReactModal >

            {/* MODAL DETAIL */}
            <ReactModal
                isOpen={isModalDetailOpen}
                onRequestClose={closeModalDetail}

                style={{
                    content: {
                        width: "50%", // Kích thước chiều rộng của modal (có thể điều chỉnh)
                        height: "80%", // Kích thước chiều cao của modal (có thể điều chỉnh)
                        margin: "auto", // Căn giữa modal
                        marginTop: "50px",
                    },
                }}
            >

                <h2
                    style={{
                        textAlign: "center",
                        backgroundColor: "#0A1A5C",
                        color: "#fff",
                        width: "100%",
                        padding: "5px", // Thay đổi kích thước màu nền bằng padding

                    }}
                >
                    THÔNG TIN ĐỐI TƯỢNG
                </h2>
                <br />

                <table className="table">
                    <tbody>
                        <tr>
                            <th scope="row">ID</th>
                            <td className="col-10">
                                <input
                                    type="text"
                                    name="objectTypeId"
                                    placeholder="ID: "
                                    style={{ width: "100%" }}
                                    onChange={(e) => onInputChange(e)}
                                    value={dataObjectTypeUpdate.objectTypeId}
                                    disabled
                                />
                            </td>
                        </tr>
                        <br />
                        <tr>
                            <th>Dự án</th>
                            <td>
                                <select className="form-select" style={{ width: "100%" }} name="projectId" required onChange={(e) => onInputChange(e)}>

                                    <option value="">Chọn một dự án</option>
                                    {projects.map((item, index) => (
                                        <option key={index} value={item.projectId}>
                                            {item.projectName}
                                        </option>
                                    ))}
                                </select>
                            </td>
                        </tr>
                        <br />

                        <tr>
                            <th scope="row">Tên đối đượng</th>
                            <td className="col-10">
                                <input
                                    type="text"
                                    name="objectTypeName"
                                    className="form-control"
                                    placeholder="Tên đối tượng: "
                                    style={{ width: "100%" }}
                                    onChange={handleInputChange}
                                    value={dataObjectTypeUpdate.objectTypeName}

                                />
                            </td>
                        </tr>
                        <br />
                        <tr>
                            <th scope="row">Ảnh</th>
                            <td className="col-10">
                                <input type="file" name="file" id="file" value={profiledata.file} onChange={(e) => getBase64Update(e)} />

                                <br />

                                <img id="blah" src={dataObjectTypeUpdate.img} alt="avt" className="profileviewImage mt-2 mb-2" style={{ width: "100%", height: "300px" }} />




                            </td>
                        </tr>
                    </tbody>
                </table>
                <br />
                <div className="row justify-content-center">
                    <div className="col-2 mb-4"></div>

                    <div className="col-2 mb-4">
                        <button
                            type="submit"
                            className="btn btn-primary"
                            onClick={updateObjectType}
                            style={{
                                width: "70px",
                                backgroundColor: "#0A1A5C"
                            }}
                        >
                            Lưu
                        </button>
                    </div>

                    <div className="col-2 mb-4">
                        <button
                            type="submit"
                            className="btn btn-danger"
                            style={{
                                width: "70px",
                                backgroundColor: "orangered"
                            }}
                            onClick={handleDeletePlanById}
                        >
                            Xóa
                        </button>
                    </div>

                    <div className="col-2 mb-4">
                        <button
                            className="btn btn-secondary"
                            onClick={closeModalDetail}
                        >
                            Đóng
                        </button>
                    </div>

                    <div className="col-2 mb-4"></div>
                </div>



            </ReactModal >

        </>
    )
};

export default ListObjectType;
