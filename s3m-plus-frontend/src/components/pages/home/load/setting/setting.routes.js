
import EditSetting from "./edit";
import ListSetting from "./list";


const SettingRouters = [
    {
        component: EditSetting,
        path: '/home/load/:customerId/:projectId/setting/:id/edit',
        link: {
            title: "Cập nhật cài đặt",
            icon: "fas fa-user-tie"
        },
        action: "UPDATE"
    },
    {
        component: ListSetting,
        path: '/home/load/:customerId/:projectId/setting',
        link: {
            title: "Danh sách cài đặt",
            icon: "fas fa-user-tie"
        },
        action: "READ"
    }
];

export default SettingRouters;