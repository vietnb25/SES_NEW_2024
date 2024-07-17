import EditSetting from "./edit";
import ListSetting from "./list";

const SettingRouters = [
    {
        component: EditSetting,
        path: '/category/setting/:id/edit',
        link: {
            title: "Cập nhật cài đặt",
            icon: "fas fa-user-tie"
        },
        action: "UPDATE"
    },
    {
        component: ListSetting,
        path: '/category/setting',
        link: {
            title: "Danh sách cài đặt",
            icon: "fas fa-user-tie"
        },
        action: "READ"
    }
];

export default SettingRouters;