<template>
  <el-container>
    <el-header height="40px">
      <div class="logo">
        <img src="./../assets/aispeech-logo.png" />
      </div>
      <div class="user-info">
        欢迎你，{{currentUser.userName}}
        <el-link type="primary" @click="logOut(true)">退出</el-link>
      </div>
    </el-header>
    <el-container>
      <el-aside :width="asideMenuWidth">
        <el-menu
          default-active="1-4-1"
          class="el-menu-vertical-demo"
          :collapse="isCollapse"
          :default-openeds="defaultOpeneds"
          :unique-opened="true"
        >
          <el-submenu v-for="m in menuConfig" :key="m.name" :index="m.index">
            <template slot="title">
              <i :class="m.icon"></i>
              <span slot="title">{{m.name}}</span>
            </template>
            <el-menu-item-group>
              <el-menu-item
                v-for="sm in m.menuItems"
                :key="sm.name"
                :index="sm.index"
                @click="openMenuItem(m,sm)"
              >{{sm.name}}</el-menu-item>
            </el-menu-item-group>
          </el-submenu>
        </el-menu>

        <div @click="toggleMenuCollapse" :class="isCollapse?'menuCollapse':'menuExpand'">
          <div>{{isCollapse?'>>':'收起'}}</div>
        </div>
      </el-aside>
      <el-main id="main-container">
        <div class="default-div" v-show="!iframeShow">欢迎使用训练标注一体化平台</div>
        <iframe
          v-show="iframeShow"
          id="show-iframe"
          frameborder="0"
          name="showHere"
          scrolling="auto"
          src
        ></iframe>
      </el-main>
    </el-container>
    <el-footer height="30px">{{footerInfo}}</el-footer>
  </el-container>
</template>

<script>
import userService from "@/api/user";
import loginService from "@/api/auth";
import {
  getUserData,
  setUserData,
  removeAccessToken,
  removeRefreshToken,
  removeUserData,
  setAccessToken,
  setRefreshToken,
  getRefreshToken
} from "@/utils/auth";
import { Message } from "element-ui";
export default {
  data() {
    return {
      footerInfo: "XXXXXXXX信息科技有限公司 © 2017 苏ICP备00000000号-0",
      asideMenuCollapseWidth: "70px",
      asideMenuExpandWidth: "200px",
      asideMenuWidth: "200px",
      isCollapse: false,
      defaultOpeneds: ["1"],
      currentUser: { userName: "" },

      menuConfig: [],
      iframeShow: false
    };
  },
  mounted: function() {
    this.getCurrentUserInfo();

    setTimeout(() => {
      this.refreshIFrameSize();
    }, 200);

    window.addEventListener("resize", this.refreshIFrameSize);

    window.addEventListener("message", this.receiveMessage, false);
  },
  destroyed: function name(params) {
    window.removeEventListener("resize", this.refreshIFrameSize);
    window.removeEventListener("message", this.receiveMessage);
  },

  methods: {
    refreshIFrameSize() {
      console.log("refreshIFrameSize");

      const iframe = document.getElementById("show-iframe");
      const mainContainer = document.getElementById("main-container");

      const mainContainerWidth = mainContainer.clientWidth;
      const mainContainerHeight = mainContainer.clientHeight;
      console.log(
        "mainContainerWidth:",
        mainContainerWidth,
        "mainContainerHeight:",
        mainContainerHeight
      );

      iframe.style.width = mainContainerWidth - 5 + "px";
      iframe.style.height = mainContainerHeight - 5 + "px";
      console.log(
        "iframe.style.width:",
        iframe.style.width,
        "iframe.style.height:",
        iframe.style.height
      );
    },
    getCurrentUserInfo() {
      var d = getUserData();

      if (d) {
        d = JSON.parse(d);
        if (d && d.userId) {
          userService.getUser(d.userId).then(res => {
            console.log(" userService.getUserInfo:", res);
            if (res) {
              this.currentUser.userName = res.data.userName;
              let data = res.data;
              data.userId = res.data.id;
              setUserData(data);

              this.refreshMenuConfig(data, window.MENU_CONFIG);
            }
          });
          return;
        }
      }
      this.logOut(false);
    },
    logOut(prompt) {
      if (prompt == undefined) {
        prompt = true;
      }

      if (prompt) {
        this.$confirm("确定将退出系统吗, 是否继续?", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(() => {
          removeAccessToken();
          removeRefreshToken();
          removeUserData();
          this.$router.push("Login");
        });
      } else {
        removeAccessToken();
        removeRefreshToken();
        removeUserData();
        this.$router.push("Login");
      }
    },
    toggleMenuCollapse() {
      this.isCollapse = !this.isCollapse;
      if (this.isCollapse) {
        this.asideMenuWidth = this.asideMenuCollapseWidth;
      } else {
        this.asideMenuWidth = this.asideMenuExpandWidth;
      }
      setTimeout(() => {
        this.refreshIFrameSize();
      }, 200);
    },
    openMenuItem(m, sm) {
      console.log(m.name, "---", sm.name, "---", sm.path);

      let path = sm.path;
      if (path) {
        this.iframeShow = true;
        document.getElementById("show-iframe").src = path;
      }
    },

    refreshMenuConfig(userData, mc) {
      let filterMenuConfig = [];

      mc.forEach(m => {
        let smArray = [];
        for (let index = 0; index < m.menuItems.length; index++) {
          const sm = m.menuItems[index];
          if (sm.requirePermission == undefined || sm.requirePermission == "") {
            smArray.push(sm);
          } else {
            let checkPermission = sm.requirePermission;

            if (userData.permissionList) {
              let checkPassed = false;
              userData.permissionList.forEach(p => {
                if (p.permissionName == checkPermission) {
                  checkPassed = true;
                  return;
                }
              });
              if (checkPassed) {
                smArray.push(sm);
              }
            }
          }
        }
        if (smArray.length > 0) {
          filterMenuConfig.push({
            index: m.index,
            name: m.name,
            icon: m.icon,
            menuItems: smArray
          });
        }
      });

      this.menuConfig = filterMenuConfig;
    },

    receiveMessage(event) {
      console.log(event);
      var origin = event.origin;
      var data = event.data;
      if (data === "ezml-401-token-invalid") {
        //refreshToken
        var refreshToken = getRefreshToken();
        if (!refreshToken) {
          this.logOut(false);
          return;
        }
        refreshToken = refreshToken.replace('"', "");
        var self = this;
        let req = { refreshToken: refreshToken };

        loginService.refreshToken(req).then(res => {
          console.log("refreshToken:", res);
          if (res) {
            if (res.errorCode == 0) {
              let d = res.data;

              let accessToken = "Bearer " + d.accessToken.toString();
              setAccessToken(accessToken);
              let refreshToken = d.refreshToken.toString();
              setRefreshToken(refreshToken);
              console.log("刷新token成功");
            } else {
              Message({
                message: res.message,
                type: "error"
              });
            }
          }
        });
      }
    }
  }
};
</script>

<style>
.el-container {
  height: 100%;
}
.el-header {
  background-color: #b3c0d1;
  color: #333;
  text-align: center;
  line-height: 30px;
  font-size: 13px;
}

.el-footer {
  background-color: #b3c0d1;
  color: #333;
  text-align: center;
  line-height: 30px;
  font-size: 13px;
}

.el-aside {
  background-color: #fff;
  color: #333;
  text-align: center;
}

.el-main {
  background-color: #e9eef3;

  text-align: center;

  padding: 0px;
}

.logo {
  float: left;
}
.user-info {
  float: right;
  line-height: 20px;
  margin-top: 10px;
  font-size: 14px;
}

.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 195px;
  min-height: 400px;
}

.el-menu-vertical-demo.el-menu {
  height: calc(100% - 45px);
}

.menuExpand {
  width: 195px;
  border-right: solid 1px #eae6e6;
  cursor: pointer;
  color: #333;
  line-height: 40px;
  font-size: 14px;
}

.menuCollapse {
  width: 60px;
  border-right: solid 1px #eae6e6;
  cursor: pointer;
  color: #333;
  line-height: 40px;
  font-size: 14px;
}
.menuExpand:hover,
.menuCollapse:hover {
  background-color: #d9dbde;
}

.default-div {
  padding: 50px;
}
</style>