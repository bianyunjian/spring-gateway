<template>
  <div>
    <div class="title-div">欢迎使用训练标注一体化平台</div>
    <div class="loginFormDiv">
      <el-form ref="form" :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.userName"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="doLogin">立即登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import loginService from "@/api/auth";
import { setAccessToken, setRefreshToken, setUserData } from "@/utils/auth";
import { Message } from "element-ui";
export default {
  data() {
    return {
      form: {
        userName: "admin@aispeech.com",
        password: "123456"
      }
    };
  },
  mounted() {},
  computed: {},
  methods: {
    doLogin() {
      var self = this;
      let req = { userName: this.form.userName, password: this.form.password };

      loginService.login(req).then(res => {
        console.log("login:", res);
        if (res) {
          if (res.errorCode == 0) {
            let d = res.data;

            let accessToken = "Bearer " + d.accessToken.toString();
            setAccessToken(accessToken);
            let refreshToken = d.refreshToken.toString();
            setRefreshToken(refreshToken);
            setUserData(d);
            Message({
              message: "登录成功",
              type: "success"
            });
            self.$router.push("Home");
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
};
</script>

<style>
.title-div {
  padding: 50px;
  font-size: 20px;
}
.loginFormDiv {
  width: 300px;
  margin: 20px auto;
  border: solid 1px;
  padding: 15px;
}
</style>