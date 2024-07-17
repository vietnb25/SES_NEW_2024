import React from "react";
import { Link, useHistory, useLocation, useParams } from 'react-router-dom';
import authService from "../../../services/AuthService";
import { useTranslation } from 'react-i18next';
import i18n from "i18next";
import { useEffect } from "react";
import { useState } from "react";
import "./index.css";
import "/node_modules/flag-icons/css/flag-icons.min.css";
import profileService from "../../../services/ProfileService";
import CategoryRouters from "../../pages/category/category.routes";
import useAppStore from "../../../applications/store/AppStore";
import moment from "moment";
import CONS from "../../../constants/constant";

const $ = window.$;

const Header = () => {
    const { t } = useTranslation();
    const location = useLocation();
    const [role, setRole] = useState("");
    const [urlImg, seturlImg] = useState(null);
    const [pathsByUser] = useState(useAppStore(state => state.categoryPath));
    const [image] = useState(useAppStore(state => state.imageProfile));
    const saveImage = useAppStore(state => state.saveImage);
    const history = useHistory();
    const [state, setState] = useState(0);
    const [minute, setMinute] = useState();
    const [hour, setHour] = useState();
    const [second, setSecond] = useState();
    const [flag] = useState(useAppStore(state => state.language));
    const setFlag = useAppStore(state => state.saveLanguage);
    const [language, setLanguage] = useState('');
    const param = useParams();
    const [customerId, setCustomerId] = useState();
    const onLogOut = () => {
        authService.logout();
    }

    const getRole = () => {
        let roleName = authService.getRoleName();
        setRole(roleName);
    }

    const profile = async () => {
        let res = await profileService.detailProfile();
        if (image === null || parseInt(image.length) === 0) {
            res = await profileService.detailProfile();
            if (res.status === 200) {
                seturlImg(res.data.img);
            }
        } else if (res.data.img == image) {
            seturlImg(image);
        } else {
            seturlImg(res.data.img);
        }
    }

    const Interval = async () => {
        setTimeout(() => {
            setState(state + 1);
        }, 1000);
    }

    const getCustomerId = async () => {
        let pathname = window.location.pathname;
        let arrPathName = pathname.split("/");
        setCustomerId(arrPathName[1])
    }
    function changeLanguage1(languageCode) {
        if (languageCode === 'vi') {
            i18n.changeLanguage('vi');
            setFlag('vi');
            setLanguage('vi');
        } else if (languageCode === 'en') {
            i18n.changeLanguage('en');
            setFlag('en');
            setLanguage('en');
        } else if (languageCode === 'kr') {
            i18n.changeLanguage('kr');
            setFlag('kr');
            setLanguage('kr');
        } else if (languageCode === 'cn') {
            i18n.changeLanguage('cn');
            setFlag('cn');
            setLanguage('cn');
        }

        // Lưu trữ giá trị ngôn ngữ đã chọn vào localStorage
        localStorage.setItem('selectedLanguage', languageCode);

        // Cập nhật dropdown
        updateDropdown(languageCode);
        window.location.reload();
    }

    function changeLanguageFromSelect() {
        var selectElement = document.getElementById("languageSelect");
        var selectedLanguage = selectElement.value;
        changeLanguage1(selectedLanguage);
    }

    document.addEventListener("DOMContentLoaded", function () {
        // Lấy giá trị đã lưu từ localStorage
        var selectedLanguage = localStorage.getItem('selectedLanguage');

        // Thiết lập lại giá trị cho dropdown
        if (selectedLanguage) {
            var languageSelect = document.getElementById("languageSelect");
            languageSelect.value = selectedLanguage;
            updateDropdown(selectedLanguage); // Cập nhật dropdown dựa trên giá trị đã chọn
        }
    });

    // Hàm cập nhật dropdown khi thay đổi ngôn ngữ
    function updateDropdown(selectedLanguage) {
        if (selectedLanguage === 'vi') {
            $('#language-flag').attr('src', '/resources/image/icon-vietnamese-flag.svg');
        } else if (selectedLanguage === 'en') {
            $('#language-flag').attr('src', '/resources/image/icon-american-flag.svg');
        } else if (selectedLanguage === 'kr') {
            $('#language-flag').attr('src', '/resources/image/south-korea-flag-icon.svg');
        } else if (selectedLanguage === 'kr') {
            $('#language-flag').attr('src', '/resources/image/china-flag-icon.svg');
        }
    }


    const changeLanguage = (e) => {
        if (language == null || language == '') {
            if (flag == null || flag == '') {
                setFlag('vi')
                setLanguage('vi')
                $('#language-flag').attr('src', '/resources/image/icon-vietnamese-flag.svg');
                i18n.changeLanguage('vi');
            } else {
                setLanguage(flag)
                if (flag == 'vi') {
                    i18n.changeLanguage('vi');
                    $('#language-flag').attr('src', '/resources/image/icon-vietnamese-flag.svg');
                    setLanguage('vi')
                } else if (flag == 'en') {
                    i18n.changeLanguage('en');
                    $('#language-flag').attr('src', '/resources/image/icon-american-flag.svg');
                    setLanguage('en')
                }
                else if (flag == 'kr') {
                    i18n.changeLanguage('kr');
                    $('#language-flag').attr('src', '/resources/image/south-korea-flag-icon.svg');
                    setLanguage('kr')
                }
                else if (flag == 'cn') {
                    i18n.changeLanguage('cn');
                    $('#language-flag').attr('src', '/resources/image/china-flag-icon.svg');
                    setLanguage('cn')
                }
            }
        }
        else {
            if (language == 'vi') {
                i18n.changeLanguage('en');
                $('#language-flag').attr('src', '/resources/image/icon-american-flag.svg');
                setFlag('en')
                setLanguage('en')
            } else if (language == 'en') {
                i18n.changeLanguage('vi');
                $('#language-flag').attr('src', '/resources/image/icon-vietnamese-flag.svg');
                setFlag('vi')
                setLanguage('vi')
            } else if (language == 'kr') {
                i18n.changeLanguage('kr');
                $('#language-flag').attr('src', '/resources/image/south-korea-flag-icon.svg');
                setFlag('kr')
                setLanguage('kr')
            } else if (language == 'cn') {
                i18n.changeLanguage('cn');
                $('#language-flag').attr('src', '/resources/image/china-flag-icon.svg');
                setFlag('cn')
                setLanguage('cn')
            }
        }
    }
    function toggleDropdown() {
        var dropdownContent = document.getElementById("languageDropdown");
        dropdownContent.classList.toggle("show");
    }
    useEffect(() => {

        profile();
        getRole();
        getCustomerId();
        changeLanguage();
        setInterval(() => {
            const date = new Date();
            setHour(moment(date).format("HH"));
            setMinute(moment(date).format("mm"));
            setSecond(moment(date).format("ss"));
        }, 1000);
    }, [])
    return (
        <div id="page-header">
            <div className="header-logo">

                <div className="logo">
                    <Link id="" to={"/"} onClick={e => {
                    }}>
                        <img id="id-logo" className="p-1" src="/resources/image/SES-logo-v2.svg" alt="S3M" />
                    </Link>
                </div>


                <div className="banner">
                    <img id="id-banner" className="" src="/resources/image/SES-banner.svg" alt="S3M" />
                </div>

                <div className="avatar-v2">
                    <div id="site-menu" className="dropdown show">
                        <Link to="/" id="dropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" onClick={e => e.preventDefault()}>
                            <div className="avatar avatar-online">
                                <img src={urlImg ? urlImg : "/resources/image/no_avatar.png"} alt="avatar" />
                            </div>
                            <span className="user-name"></span>
                        </Link>
                        <div className="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenuLink" style={{ fontSize: 13 }}>
                            <Link className="dropdown-item" to="/profile">
                                <i className="far fa-clipboard"></i> {t('content.profile')}
                            </Link>
                            <div id="language-dropdown" class="dropdown1">
                                <div id="dropdown-toggle" className="dropdown-item" style={{ cursor: "pointer" }}>
                                    <i className="fa fa-language"></i> {t('content.language')}
                                    <img id="language-flag" className="ml-2" height="15px" width="20px" src="/resources/image/icon-vietnamese-flag.svg"></img>
                                </div>
                                <div class="dropdown-content">
                                    <a href="#" onClick={(e) => changeLanguage1('vi')}>
                                        <img id="id-logo" className="flag-icon" src="/resources/image/icon-vietnamese-flag.svg" alt="S3M" /> VN
                                    </a>
                                    <a href="#" onClick={(e) => changeLanguage1('en')}>
                                        <img src="/resources/image/icon-american-flag.svg" alt="English Flag" className="flag-icon" /> US
                                    </a>
                                    <a href="#" onClick={(e) => changeLanguage1('cn')}>
                                        <img id="id-logo" className="flag-icon" src="/resources/image/china-flag-icon.svg" alt="S3M" /> CN
                                    </a>
                                    <a href="#" onClick={(e) => changeLanguage1('kr')}>
                                        <img id="id-logo" className="flag-icon" src="/resources/image/south-korea-flag-icon.svg" alt="S3M" /> KR
                                    </a>
                                </div>
                            </div>

                            <div className="dropdown-divider"></div>
                            {
                                role === "ROLE_ADMIN" ? CategoryRouters.map((route, i) => {
                                    return (
                                        <Link className="dropdown-item" to={route.path} key={i}>
                                            <i className={route.link.icon}></i> {route.link.title}
                                        </Link>
                                    )
                                }) : CategoryRouters.map((route, i) => {
                                    return (pathsByUser.some(p => p.path === route.path)) && (
                                        <Link className="dropdown-item" to={route.path} key={i}>
                                            <i className={route.link.icon}></i> {route.link.title}
                                        </Link>
                                    )
                                })
                            }

                            {/* <Link className="dropdown-item" to="/category/tool-page">
                        <i className="fas fa-pen-ruler"></i> Sơ đồ 1 sợi
                    </Link> */}

                            <Link className="dropdown-item" to="/category/help">
                                <i className="fas fa-question"></i> {t('content.help')}
                            </Link>

                            <div className="dropdown-divider"></div>

                            <Link className="dropdown-item" to="/" onClick={() => onLogOut()}>
                                <i className="fas fa-right-from-bracket"></i> {t('content.log_out')}
                            </Link>
                        </div>
                    </div>
                </div>
            </div>
            <div className="header-slogan">
                <div id="trapezium-left">
                    <p className="time">
                        {moment(new Date()).format("DD - MM - YYYY")} &ensp; {hour} : {minute} : {second}
                    </p>
                </div>
                <div id="trapezium-right">
                    <label className="time">
                        SMART ENERGY MONITOR SYSTEM
                    </label>
                </div>
            </div>

        </div>
    )
}

export default Header;