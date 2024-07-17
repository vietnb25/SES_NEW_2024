import CONS from "./constant";

const SETTING_HOST = CONS.LOCAL_HOST + "common/setting-cost";

const SETTING_COST_API = {
    LIST : SETTING_HOST + "/list-by-project" ,
    UPDATE : SETTING_HOST + "/update" ,
  
}

export default SETTING_COST_API;