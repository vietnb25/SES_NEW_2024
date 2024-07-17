import { React } from 'react';

const InfoWindow = ({project}) => {
    return <>
        <div id="project-info-window" className="">
            <div style={{ border: "1px solid #B3B3B3", borderEadius: "5px" }}>
                <div className="system-project-title">
                    <h4>{project.projectName}</h4>
                    <span><i className="fa-regular fa-clock"></i>&nbsp; 29-09-2022 17:02</span>
                </div>

                <div id="project-overview">
                    <div id="system-power">
                        <div>100KW</div>
                        <div>100KW</div>
                        <div>100KW</div>
                        <div>100KW</div>
                    </div>

                    <div id="system-icon">
                        <div className="active">
                            <img src="/resources/image/system-icon/system-solar.png" alt="Solar System" />
                        </div>
                        <div className="warning">
                            <img src="/resources/image/system-icon/system-wind.png" alt="Wind System" />
                        </div>
                        <div className="inactive">
                            <img src="/resources/image/system-icon/system-ev.png" alt="EV System" />
                        </div>
                        <div className="offline">
                            <img src="/resources/image/system-icon/system-utility.png" alt="Utility System" />
                        </div>
                    </div>

                    <div id="system-arrow">
                        <div>
                            <img src="/resources/image/system-icon/system-line-down.png" alt="system-load-line" />
                        </div>
                        <div>
                            <img src="/resources/image/system-icon/system-line-up.png" alt="system-load-line" />
                        </div>
                        <div>
                            <img src="/resources/image/system-icon/system-line.png" alt="system-load-line" />
                        </div>
                        <div>
                            <img src="/resources/image/system-icon/system-line-up.png" alt="system-load-line" />
                        </div>
                    </div>

                    <div id="system-arrow-load">
                        <img src="/resources/image/system-icon/system-line-load-down.png" alt="system-load-line" />
                    </div>

                    <div id="system-load">
                        <div className="warning">
                            <a href="/load/1"><img src="/resources/image/system-icon/system-load.png" alt="Load System"/></a>
                        </div>
                    </div>

                    <div id="system-power-load">
                        <div>200KW</div>
                    </div>
                </div>
            </div>
        </div>
    </>
}

export default InfoWindow;