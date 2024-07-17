import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import controlPVService from '../../../../../services/ControlPVService';
import ControlSystemMap from './control_system_map';
import ControlProject from './control_project';


const ControlSolar = ({ projectInfo }) => {

  const [systemSearch, setSystemSearch] = useState([]);
  const [selectedInputSystem, setSelectedInputSystem] = useState(true);
  const [systems, setSystem] = useState([]);
  const [systemSelected, setSystemSelected] = useState('all');
  const param = useParams();

  const changeSearch = () => {
    setSelectedInputSystem(!selectedInputSystem);
    setSystemSearch([]);
  };

  const getListSystem = async () => {
    let res = await controlPVService.getListSystemMap(param.projectId);
    if (res.status === 200) {
      setSystem(res.data);
    }
  };

  const searchSystem = (e) => {
    let system_name = document.getElementById("keyword").value;
    if (system_name === "") {
      setSystemSearch([]);
    } else {
      let systemSearch = systems?.filter(item => item.name.includes(system_name));
      setSystemSearch(systemSearch);
    }
  };

  const changeSystem = (systemId, systemName) => {
    document.getElementById("keyword").value = systemName;
    onLoadSystem(systemId);
    setSystemSearch([]);
  }


  const onLoadSystem = (systemId) => {
    console.log("idddd: ", systemId);
    setSystemSelected(systemId);
    // let url = window.location.pathname;
    // let deviceStr = "" + param.deviceId;
    // let projectStr = "" + param.projectId;

    // let _href = url.slice(0, 0) + url.slice(27 + deviceStr.length + projectStr.length);

    // history.push(`/load/` + param.projectId + `/device-information/` + _deviceId + `/` + _href);
  }

  useEffect(() => {
    getListSystem();
    onLoadSystem();
    setSystemSelected("all");
    document.title = "Điều khiển";
  }, [param.projectId])

  return (
    <div className="tab-content">
      <div className="tab-title">
        <div className="project-infor" style={{ padding: "0px 10px", display: "block", marginTop: "10px" }}>
          <span className="project-tree">{projectInfo}</span>
        </div>
        <div className="form-group mt-2 mb-5 ml-2 mr-2 " >
          <div className="input-group mr-1" style={{marginTop: "10px"}}>

            {
              selectedInputSystem ?
                <>
                  <div className="input-group-prepend" onClick={() => changeSearch()}>
                    <span className="input-group-text pickericon">
                      <span className="fas fa-list-check"></span>
                    </span>
                  </div>
                  <select id="cbo-device-id" value={systemSelected} className="custom-select block custom-select-sm" onChange={(e) => onLoadSystem(e.target.value)}>
                    <option value={'all'}>All</option>
                    {
                      systems?.map((item, index) => (
                        <option value={item.id} key={index}>{item.name}</option>
                      ))
                    }
                  </select>
                </>
                :
                <>
                  <div className="input-group-prepend" onClick={() => changeSearch()}>
                    <span className="input-group-text pickericon">
                      <span className="fas fa-magnifying-glass"></span>
                    </span>
                  </div>
                  <input type="text" id="keyword" className="form-control" aria-label="Tìm kiếm" aria-describedby="inputGroup-sizing-sm" placeholder="Nhập tên thiết bị" onChange={() => searchSystem()} />
                </>
            }
          </div>
          <div style={{ position: "relative", zIndex: "99", border: "none" }}>
            {
              systemSearch?.map((m, index) => {
                return <div className="autocomplete" key={index} style={{ border: "none" }}>
                  <div className="form-control hover-device" style={{ border: "none" }} onClick={() => changeSystem(m.id, m.name)}><i className="fas fa-server pr-3 pl-1"></i>{m.name}</div>
                </div>
              })
            }
          </div>
        </div>
      </div>

      <div className="tab-content">
        {
          systemSelected === 'all' ? <ControlProject projectId={param.projectId}></ControlProject> : <ControlSystemMap systemMapId={systemSelected}></ControlSystemMap>
        }
      </div>
    </div>


  )
}

export default ControlSolar
