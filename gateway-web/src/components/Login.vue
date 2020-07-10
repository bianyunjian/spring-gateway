<template>
  <el-container>
    <el-header height="40px">
      <div class="logo">
        <img src="./../assets/aispeech-logo.png" />
      </div>
    </el-header>

    <el-main id="main-container">
      <el-row>
        <el-col :span="14">
          <div class="adDiv">
            <div>思必驰标注训练一体化平台</div>
            <div>• 更专业的模型训练平台，大幅度提升识别性能</div>
            <div>• 更简单的一体化平台，让工作更高效</div>
            <div>• 更强大的功能支持，打通数据壁垒</div>
          </div>
        </el-col>
        <el-col :span="10">
          <div class="loginFormDiv">
            <div>登录</div>
            <br />
            <el-form ref="loginform" :model="form" label-width="0px" :rules="rules">
              <el-form-item label prop="userName">
                <el-input v-model="form.userName" placeholder="请输入邮箱"></el-input>
              </el-form-item>
              <el-form-item label prop="password">
                <el-input v-model="form.password" type="password" placeholder="请输入密码"></el-input>
              </el-form-item>

              <el-form-item>
                <el-button type="primary" style="width:300px;" @click="submitForm">立即登录</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>

<script>
import loginService from "@/api/auth";
import { setAccessToken, setRefreshToken, setUserData } from "@/utils/auth";
import { Message } from "element-ui";
export default {
  data() {
    var checkPassword = (rule, value, callback) => {
      if (!value) {
        return callback(new Error("密码不能为空"));
      } else {
        callback();
      }
    };
    var checkUserName = (rule, value, callback) => {
      if (!value) {
        return callback(new Error("邮箱不能为空"));
      } else {
        callback();
      }
    };

    return {
      formName: "loginform",
      form: {
        userName: "admin@aispeech.com",
        password: "123456"
      },
      rules: {
        password: [{ validator: checkPassword, trigger: "blur" }],
        userName: [{ validator: checkUserName, trigger: "blur" }]
      }
    };
  },
  mounted() {},
  computed: {},
  methods: {
    submitForm() {
      this.$refs[this.formName].validate(valid => {
        if (valid) {
          this.doLogin();
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    },
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
              message: "登录失败，请检查用户名与密码",
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
.el-container {
  height: 100%;
  background: #f5f7fa;
}
.el-header {
  background-color: #b3c0d1;
  color: #333;
  text-align: center;
  line-height: 30px;
  font-size: 13px;
}

.el-main {
  background: #fff;
  text-align: center;
  margin: 20px;
  padding: 0px;
  border-radius: 4px;
  box-shadow: rgba(0, 0, 0, 0.05) 1px 0px 4px;
}

.logo {
  float: left;
}

.adDiv {
  margin-top: 30%;
  text-align: center;
  line-height: 35px;
}

.loginFormDiv {
  margin-top: 30%;
  text-align: center;
  width: 300px;
  border: solid 1px #ddd;
  border-radius: 5px;
  padding: 25px;
}
</style>