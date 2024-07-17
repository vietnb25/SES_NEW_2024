import React from "react";
import { Link } from 'react-router-dom';
import authService from "../../../services/AuthService";
import { useTranslation } from 'react-i18next';

const Header = () => {
    const { t } = useTranslation();
    const onLogOut = () => {
        authService.logout();
    }

    return (
        <div id="page-header">
            <Link id="site-logo" className="float-left" to={"/"}>
                <img id="site-logo-pc" src="/resources/image/s3m-logo-pc.png" alt="S3M" />
                <img id="site-logo-mobile" src="/resources/image/s3m-logo-mobile.png" alt="S3M" />
            </Link>

            <div id="site-menu" className="dropdown show float-right">
                <a href="#" id="dropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <div className="avatar avatar-online">
                        <img src="/resources/image/no_avatar.png" alt="avatar" />
                    </div>
                    <span className="user-name"></span>
                </a>

                <div className="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenuLink">
                    <Link className="dropdown-item" to="/profile">
                        <i className="far fa-clipboard"></i> {t('content.profile')}
                    </Link>
                    <Link className="dropdown-item" to="/category/help">
                        <i className="fas fa-question"></i> Trợ giúp
                    </Link>
                    <div className="dropdown-divider"></div>

                    <Link className="dropdown-item" to="/" onClick={() => onLogOut()}>
                        <i className="fas fa-right-from-bracket"></i> Đăng xuất
                    </Link>
                </div>
            </div>
        </div>
    )
}

export default Header;