import AddDeviceType from "./add";
import editDeviceType from "./edit";
import ListDeviceType from "./list";

const DeviceTypeRouters = [
    {
        component: AddDeviceType,
        path: '/category/device-type/add',
        link: {
            title: "Thêm mới loại thiết bị",
            icon: "fas fa-user-tie"
        },
        action: "CREATE"
    },
    {
        component: editDeviceType,
        path: '/category/device-type/edit/:id',
        link: {
            title: "Cập nhật loại thiết bị",
            icon: "fas fa-user-tie"
        },
        action: "UPDATE"
    },
    {
        component: ListDeviceType,
        path: '/category/device-type',
        link: {
            title: "Danh sách loại thiết bị",
            icon: "fas fa-user-tie"
        },
        action: "READ"
    }
];

export default DeviceTypeRouters;