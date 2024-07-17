

import AddObjectType from "./add";
import EditObjectType from "./edit";

import ListObjectType1 from "./list/index2";

const ObjectTypeRouters = [
    {
        component: AddObjectType,
        path: '/category/object-type/add',
        link: {
            title: "Thêm đối tượng",
            icon: "fas fa-user-tie"
        },
        action: "ADD"
    },
    {
        component: EditObjectType,
        path: '/category/object-type/edit/:id',
        link: {
            title: "Cập nhật thiết bị",
            icon: "fas fa-user-tie"
        },
        action: "UPDATE"
    },
    {
        component: ListObjectType1,
        path: '/category/object-type',
        link: {
            title: "Danh sách Đối tượng giám sát",
            icon: "fas fa-user-tie"
        },
        action: "READ"
    },
];

export default ObjectTypeRouters;