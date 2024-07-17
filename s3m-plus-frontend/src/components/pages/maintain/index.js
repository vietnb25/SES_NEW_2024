import React from "react";
import { Link } from "react-router-dom";

const Maintain = () => {
    return (
        <div className="page-danied" style={{ height: "calc(100vh - 88px)", margin: "0 auto", textAlign: "center", paddingTop: "20px" }}>
            <div className="alert alert-warning alert-dismissible fade show" style={{ width: "calc(100vw - 30px)", margin: "0 auto", marginBottom: "20px" }}>
                <strong>Chúng tôi đang bảo trì</strong> <br></br> Xin lỗi bạn vì sự bất tiện này. Bạn vui lòng quay lại sau.
            </div>
            <img src="/resources/image/maintain.svg" style={{height:"300px"}} alt="Mất điện tổng" />
            {/* <Link className="btn btn-outline-warning" to={{ pathname: "/" }}>HOME</Link> */}
        </div>
    )
}

export default Maintain;