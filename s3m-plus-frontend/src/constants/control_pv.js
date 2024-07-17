import CONS from "./constant";

const CONTROL = CONS.LOCAL_HOST + "pv/control";

const CONTROL_API = {
    CONTROL : {
        GETSYSTEM : CONTROL + "/getSystem",
        CONTROL_PROJECT :   CONTROL,
        CONTROL_DEVICE : CONTROL + "/device",
        CONTROL_SAVE: CONTROL + "/save",
        CONTROL_SYSTEM: CONTROL + "/system/"
    }
};

export default CONTROL_API;