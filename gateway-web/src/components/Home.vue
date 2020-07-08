<template>
  <div>
    <div>
      欢迎你，{{currentUser.userName}}
      <el-link type="danger" @click="logOut">退出</el-link>
    </div>
  </div>
</template>

<script>
import userService from "@/api/user";
import {
  getUserData,
  setUserData,
  removeAccessToken,
  removeRefreshToken,
  removeUserData
} from "@/utils/auth";
import { Message } from "element-ui";
export default {
  data() {
    return {
      currentUser: { userName: "" }
    };
  },
  mounted: function() {
    this.getCurrentUserInfo();
  },

  methods: {
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
            }
          });
          return;
        }
      }
      this.logOut();
    },
    logOut() {
      removeAccessToken();
      removeRefreshToken();
      removeUserData();
      this.$router.push("Login");
    }
  }
};
</script>

<style>
</style>