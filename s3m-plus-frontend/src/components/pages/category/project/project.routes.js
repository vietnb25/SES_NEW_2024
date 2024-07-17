import AddProject from "./add";
import EditProject from "./edit";
import ListProject from "./list";

const ProjectRouters = [
    {
        component: AddProject,
        path: '/category/project/add',
        link: {
            title: "Thêm mới dự án",
            icon: "fas fa-user-tie"
        },
        action: "CREATE"
    },
    {
        component: EditProject,
        path: '/category/project/:id/edit',
        link: {
            title: "Cập nhật dự án",
            icon: "fas fa-user-tie"
        },
        action: "UPDATE"
    },
    {
        component: ListProject,
        path: '/category/project',
        link: {
            title: "Danh sách dự án",
            icon: "fas fa-user-tie"
        },
        action: "READ"
    }
];

export default ProjectRouters;