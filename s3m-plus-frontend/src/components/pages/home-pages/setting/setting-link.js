import React from 'react';
import "./setting.css";
import { Link } from 'react-router-dom';
import { useParams } from 'react-router-dom/cjs/react-router-dom';

const SettingLink = (props) => {
    
    const param = useParams();

    return (<>
        <div className="bg">
            <div className={ props.active ===1 ? 'opt-active': 'opt'}><Link to={'/' + param.customerId + "/setting-warning"} className='opt-link'><i className="opt-icon fa-solid fa-triangle-exclamation"></i> <span className="opt-text">Cài đặt cảnh báo</span></Link></div>
            <div className={props.active ===2 ? 'opt-active': 'opt'}><Link to={'/' + param.customerId + "/setting-shift"} className='opt-link'><i className="opt-icon fa-solid fa-stopwatch"></i> <span className='opt-text'>Cài đặt ca làm việc</span></Link></div>
            <div className={props.active ===3 ? 'opt-active': 'opt'}><Link to={'/' + param.customerId + "/receiver-warning"} className="opt-link"><i className="opt-icon fa-solid fa-user-tie"></i><span className='opt-text'>Người nhận cảnh báo</span></Link></div>
            <div className={props.active ===4 ? 'opt-active': 'opt'}><Link to={'/' + param.customerId + "/data-simulation"} className="opt-link"><i className="opt-icon fa-solid fa-coins"></i><span className="opt-text">Dữ Liệu Mô Phỏng</span></Link></div>
            <div style={props.active ===5 ? {width: '25%'}:{} } className={props.active ===5 ? 'opt-active': 'opt'} ><Link to={'/' + param.customerId + "/landmarks-energy-plans"} className="opt-link"><i className="opt-icon fa-solid fa-coins"></i><span style={{width:'80%', whiteSpace: 'nowrap', overflow:'hidden',textOverflow: 'ellipsis'}} className="opt-text">Điểm mốc & kế hoạch năng lượng</span></Link></div>
            <div  className={props.active ===6 ? 'opt-active': 'opt'} ><Link to={'/' + param.customerId + "/setting-cost"} className="opt-link"><i className="opt-icon fa-solid fa-circle-dollar-to-slot"></i><span style={{width:'80%', whiteSpace: 'nowrap', overflow:'hidden',textOverflow: 'ellipsis'}} className="opt-text">Cài đặt tiền điện</span></Link></div>
        </div>
    </>);
};



export default SettingLink;