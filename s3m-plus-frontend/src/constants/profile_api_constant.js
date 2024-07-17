import HOST from "./constant";


const BASE_API_PROFILE = {
    PROFILE:{
        LIST: HOST.LOCAL_HOST + "common/profile/",
        UPDATE : HOST.LOCAL_HOST+ "common/profile/updateProfile/",
        CHANGEPASSWORD : HOST.LOCAL_HOST +"common/profile/changePassword/"
    },
}

export default BASE_API_PROFILE;