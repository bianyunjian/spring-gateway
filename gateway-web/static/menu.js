
window.MENU_CONFIG = [
    {
        index: "1",
        name: "训练平台",
        icon: "el-icon-cpu",
        menuItems: [
            {
                index: "1-1",
                name: "语言模型",
                path: "/lmtrain/#/languageModel",
                activePath: "languageModel",
                requirePermission: "语言模型训练"
            },
            {
                index: "1-2",
                name: "声学模型",
                path: "/amtrain/#/acousticModel",
                activePath: "acousticModel",
                requirePermission: "声学模型训练"
            }
        ]
    },
    {
        index: "2",
        name: "标注平台",
        icon: "el-icon-edit",
        menuItems: [
            {
                index: "2-1",
                name: "标注大厅",
                path: "/label/#/process",
                activePath: "process",
                requirePermission: "人工标注"
            },
            {
                index: "2-2",
                name: "审核大厅",
                path: "/label/#/verify",
                activePath: "verify",
                requirePermission: "标注任务审核"
            },
            {
                index: "2-3",
                name: "我的发布",
                path: "/label/#/myPublish",
                activePath: "myPublish",
                requirePermission: "发布标注任务"
            },
            {
                index: "2-4",
                name: "我的标注",
                path: "/label/#/myProcess",
                activePath: "myProcess",
                requirePermission: "人工标注"
            },
            {
                index: "2-5",
                name: "我的审核",
                path: "/label/#/myVerify",
                activePath: "myVerify",
                requirePermission: "标注任务审核"
            }
        ]
    },
    {
        index: "3",
        name: "测试工具",
        icon: "el-icon-aim",
        menuItems: [
            {
                index: "3-1",
                name: "模型测试",
                path: "/eval/#/modelTest",
                activePath: "modelTest",
                requirePermission: "模型测试"
            },
            {
                index: "3-2",
                name: "测试记录",
                path: "/eval/#/testRecord",
                activePath: "testRecord",
                requirePermission: "模型测试"
            }
        ]
    },
    {
        index: "4",
        name: "团队管理",
        icon: "el-icon-user",
        menuItems: [
            {
                index: "4-1",
                name: "成员管理",
                activePath: "UserManage",
                path: "/user/#/UserManage",
                requirePermission: "团队成员管理"
            },
            {
                index: "4-2",
                name: "权限管理",
                activePath: "RoleManage",
                path: "/user/#/RoleManage",
                requirePermission: "团队权限管理"
            },
            {
                index: "4-3",
                name: "权限配置",
                activePath: "PermissionManage",
                path: "/user/#/PermissionManage",
                requirePermission: "权限配置"
            }
        ]
    }
];
console.log("window.MENU_CONFIG=", window.MENU_CONFIG);
