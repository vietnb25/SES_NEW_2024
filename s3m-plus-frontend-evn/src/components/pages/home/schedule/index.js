import React from "react";
import { useEffect } from "react";
import { useState } from "react";
import AuthService from "../../../../services/AuthService";
import UserService from "../../../../services/UserService";
import ScheduleService from "../../../../services/ScheduleService";
import Pickadate from 'pickadate/builds/react-dom';
import moment from "moment/moment";
import TimePicker from 'rc-time-picker';
import 'rc-time-picker/assets/index.css';
import $ from "jquery";
import 'jquery-confirm';
import { useLocation, useHistory, Link } from 'react-router-dom';
import TableSchedule from "./tableSchedule";

import "./index.css";

const Schedule = () => {

    const location = useLocation();

    const [id, setId] = useState(null)

    const [tongCSDM, setTongCSDM] = useState()

    const [schedules, setSchedules] = useState([])

    const [historySettings, setHistorySettings] = useState([])

    const [type, setType,] = useState("")

    const [users, setUser] = useState(null)

    const [checkLoadingSchedule, setCheckLoadingSchedule] = useState(false)

    const [checkLoadingHistory, setCheckLoadingHistory] = useState(false)

    const getUser = async (userName) => {
        console.log("userName22: ", userName);
        let res = await UserService.getUser(userName);
        console.log("res: ", res);
        return res.data;
    }

    const getName = async () => {
        let res = await AuthService.getAuth()
        console.log(res.username);
        return res.username;
    }

    const listSchedule = async (data) => {
        let res;
        let userName = await getName();
        let user = await getUser(userName);
        console.log("userType: ", user.userType);
        if (user.userType === 3) {
            res = await ScheduleService.getSchedule();

            let tong = 0;
            if (res.status === 200) {
                let schedules = res.data;
                setSchedules(schedules)

                for (let schedule of schedules) {
                    tong += schedule.acPower;
                }
                setTongCSDM(tong)
                setCheckLoadingSchedule(true);
            }
        } else if (user.userType === 4) {
            res = await ScheduleService.getScheduleLowerLevel(data);
            let tong = 0;
            if (res.status === 200) {
                let historySetting = res.data.historySetting;
                setHistorySettings(historySetting)
                for (let schedule of schedules) {
                    tong += schedule.acPower;
                }
                setTongCSDM(tong)
                setCheckLoadingSchedule(true);
            }
        } else if (user.userType === 5) {
            res = await ScheduleService.getScheduleLowerLevel(data);
            let tong = 0;
            if (res.status === 200) {
                let historySetting = res.data.historySetting;
                setHistorySettings(historySetting)
                for (let schedule of schedules) {
                    tong += schedule.acPower;
                }
                setTongCSDM(tong)
                setCheckLoadingSchedule(true);
            }
        }  else if (user.userType === 6) {
            res = await ScheduleService.getScheduleLowerLevel(data);
            let tong = 0;
            if (res.status === 200) {
                let historySetting = res.data.historySetting;
                setHistorySettings(historySetting)
                for (let schedule of schedules) {
                    tong += schedule.acPower;
                }
                setTongCSDM(tong)
                setCheckLoadingSchedule(true);
            }
        }


    }

    const getHistory = async (data) => {
        let res = await ScheduleService.getHistory(data);
        
        if (res.status === 200) {
            console.log("getHistory", res.data);
            setHistorySettings(res.data)
            setCheckLoadingHistory(true);
        }
    }

    const getId = async () => {
        let param = "";
        console.log("paramm: ", location.search);
        if (location.search) {
            if (location.search.includes("superManager")) {
                param = "superManagerId";
            } else if (location.search.includes("manager")) {
                param = "managerId";
            } else if (location.search.includes("area")) {
                param = "areaId";
            }
            setId(new URLSearchParams(location.search).get(param))
            return new URLSearchParams(location.search).get(param)
        } else {
            setId(null)
            return null
        }

    }

    const initial = async () => {
        let userName = await getName();
        let user = await getUser(userName);
        setUser(user);
        let idType = await getId();

        let dataSend = {
            type: user.userType,
            id: idType,
            superManagerId: user.targetId,
            managerId: user.managerId,
            areaId: user.areaId,
            userName: userName,
        }
        //await getHistory(dataSend);

        if (user.userType === 3) {
            if (idType != null) {
                setType(user.userType)
                setCheckLoadingSchedule(true);
                await getHistory(dataSend);
            } else {
                setType(user.userType)
                await getHistory(dataSend);
                listSchedule();
            }
        } else if (user.userType === 4) {
            if (idType != null) {
                setType(user.userType)
                await getHistory(dataSend);
                setCheckLoadingSchedule(true);
            } else {
                listSchedule(dataSend);
                setType(user.userType)
                setCheckLoadingHistory(true);
            }
        } else if (user.userType === 5) {
            if (idType != null) {
                setType(user.userType)
                await getHistory(dataSend);
                setCheckLoadingSchedule(true);
            } else {
                listSchedule(dataSend);
                setType(user.userType)
                setCheckLoadingHistory(true);
            }
        }
        else if (user.userType === 6) {
            if (idType != null) {
                setType(user.userType)
                await getHistory(dataSend);
                setCheckLoadingSchedule(true);
            } else {
                listSchedule(dataSend);
                setType(user.userType)
                setCheckLoadingHistory(true);
            }
        }

    }

    useEffect(() => {
        document.title = "Schedule";
        initial();
    }, [location])

    return (<>
        {(checkLoadingSchedule && checkLoadingHistory) ?
            <TableSchedule
            id={id}
            type={type}
            schedules={schedules}
            historySettings={historySettings}
            users={users}
            tongCSDM={tongCSDM}
        />
        : <img src="/resources/image/loading.gif " alt="loading"></img>
        }
    </>
        
    )
}

export default Schedule;