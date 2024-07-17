import { useEffect, useState } from "react";
import "./index.css";
import ImageSp from "./ImageSp";
import { t } from "i18next";

const Support = () => {
  const [type, setType] = useState(0);
  const clickTilte = (type) => {
    setType(type);
  };
  useEffect(() => {
    document.title = "Trợ giúp";
  }, []);

  const setContent = (type) => {
    if (type === 0) {
      return (
        <>
          <embed src="/resources/pdf/Thong_tin_phap_li.pdf" width="100%" style={{ height: "100%" }} />
        </>
      );
    } else if (type === 1) {
      return (
        <>
          <embed src="/resources/pdf/Thong_tin_an_toan.pdf" width="100%" height="750px" style={{ height: "100%" }} />
        </>
      );
    } else if (type === 2) {
      return (
        <>
          <embed src="/resources/pdf/Bien_phap_an_toan.pdf" width="100%" height="750px" style={{ height: "100%" }} />
        </>
      );
    } else if (type === 3) {
      return (
        <>
          <embed src="/resources/pdf/Huong_dan_su_dung_web.pdf" width="100%" height="750px" style={{ height: "100%" }} />
        </>
      );
    } else if (type === 4) {
      return (
        <>
          <embed src="/resources/pdf/Huong_dan_lap_dat_he_thong.pdf" width="100%" height="750px" style={{ height: "100%" }} />
        </>
      );
    } else if (type === 5) {
      return (
        <>
          <embed src="/resources/pdf/Huong_dan_van_hanh_he_thong_SEMS.pdf" width="100%" height="750px" style={{ height: "100%" }} />
        </>
      );
    }
  };
  const setTitle = (type) => {
  };
  const openPdf = () => {
    window.open("/resources/pdf/support.pdf");
  };

  return (
    <div className="support">
      {/* <div className="support-z1">
        <div
          className="z1"
          onClick={() => clickTilte(0)}
          style={
            type === 0
              ? { backgroundColor: "#0a1a5c" }
              : { backgroundColor: "#9da3be" }
          }
        >
          <i className="fa-solid ml-1 fa-circle-info"></i>
          <p className="ml-1">Thông tin pháp lý</p>
        </div>
        <div
          className="z1"
          onClick={() => clickTilte(1)}
          style={
            type === 1
              ? { backgroundColor: "#0a1a5c" }
              : { backgroundColor: "#9da3be" }
          }
        >
          <i className="fa-solid ml-1 fa-circle-info"></i>
          <p className="ml-1">Thông tin an toàn</p>
        </div>
        <div
          className="z1"
          onClick={() => clickTilte(2)}
          style={
            type === 2
              ? { backgroundColor: "#0a1a5c" }
              : { backgroundColor: "#9da3be" }
          }
        >
          <i
            className="fa-solid fa-triangle-exclamation"
            style={{ marginLeft: "1%" }}
          ></i>
          <p className="ml-1">Biện pháp an toàn</p>
        </div>
        <div
          className="z1"
          onClick={() => clickTilte(3)}
          style={
            type === 3
              ? { backgroundColor: "#0a1a5c" }
              : { backgroundColor: "#9da3be" }
          }
        >
          <i
            className="fa-solid fa-circle-question"
            style={{ marginLeft: "1%" }}
          ></i>
          <p className="ml-1">Hướng dẫn sử dụng web</p>
        </div>
        <div
          className="z1"
          onClick={() => clickTilte(4)}
          style={
            type === 4
              ? { backgroundColor: "#0a1a5c" }
              : { backgroundColor: "#9da3be" }
          }
        >
          <i
            className="fa-solid fa-circle-question"
            style={{ marginLeft: "1%" }}
          ></i>
          <p className="ml-1">Hướng dẫn lắp đặt hệ thống SEMS_L</p>
        </div>
      </div> */}
      <div className="submenu-report">
        <div className="main-submenu pr-1" id="reportType1" style={type === 0 ? { color: "#FFF", fontWeight: 'bold', backgroundColor: 'var(--ses-orange-80-color' } : { color: "#FFF", fontWeight: 'bold' }} onClick={() => {
          setType(0)
        }}>
          <i className="fa-solid ml-1 fa-circle-info" style={{ color: "#FFF", fontWeight: 'bold' }}></i> &nbsp;
          {t('content.home_page.support.legal_info')}
        </div>
        <div className="main-submenu pr-1" id="reportType1" style={type === 1 ? { color: "#FFF", fontWeight: 'bold', backgroundColor: 'var(--ses-orange-80-color' } : { color: "#FFF", fontWeight: 'bold' }} onClick={() => {
          setType(1)
        }}>
          <i className="fa-solid ml-1 fa-circle-info" style={{ color: "#FFF", fontWeight: 'bold' }}></i> &nbsp;
          {t('content.home_page.support.safety_info')}
        </div>
        <div className="main-submenu pr-1" id="reportType1" style={type === 2 ? { color: "#FFF", fontWeight: 'bold', backgroundColor: 'var(--ses-orange-80-color' } : { color: "#FFF", fontWeight: 'bold' }} onClick={() => {
          setType(2)

        }}>
          <i className="fa fa-money-check-dollar ml-1" style={{ color: "#FFF", fontWeight: 'bold' }}></i> &nbsp;
          {t('content.home_page.support.safety_measures')}
        </div>
        <div className="main-submenu pr-1" id="reportType1" style={type === 3 ? { color: "#FFF", fontWeight: 'bold', backgroundColor: 'var(--ses-orange-80-color' } : { color: "#FFF", fontWeight: 'bold' }} onClick={() => {
          setType(3)
        }}>
          <i className="fa fa-bars ml-1" style={{ color: "#FFF", fontWeight: 'bold' }}></i> &nbsp;
          {t('content.home_page.support.web_guide')}
        </div>
        <div className="main-submenu pr-1" id="reportType1" style={type === 4 ? { color: "#FFF", fontWeight: 'bold', backgroundColor: 'var(--ses-orange-80-color' } : { color: "#FFF", fontWeight: 'bold' }} onClick={() => {
          setType(4)
        }}>
          <i className="fa fa-plug-circle-exclamation ml-1" style={{ color: "#FFF", fontWeight: 'bold' }}></i> &nbsp;
          {t('content.home_page.support.instructions_install')}
        </div>
        <div className="main-submenu pr-1" id="reportType1" style={type === 5 ? { color: "#FFF", fontWeight: 'bold', backgroundColor: 'var(--ses-orange-80-color' } : { color: "#FFF", fontWeight: 'bold' }} onClick={() => {
          setType(5)
        }}>
          <i className="fa-brands fa-ubuntu ml-1" style={{ color: "#FFF", fontWeight: 'bold' }}></i> &nbsp;
          {t('content.home_page.support.instructions_operate')}
        </div>
      </div>
      <div className="support-z2">
        {/* <div className="z2-title">
          {setTitle(type)}
          <button
            className="btn btn-light"
            data-toggle="tooltip"
            title="Export"
            onClick={() => openPdf()}
          >
            {" "}
            <i
              className="fa-solid fa-file-export"
              style={{ fontSize: "16px" }}
            ></i>
          </button>
        </div> */}
        <div className="z2-content">{setContent(type)}</div>
      </div>
    </div>
  );
};

export default Support;
