<template>
  <el-container>
    <el-header height="70px">
      <div class="logo">
        <img src="./../assets/aispeech-logo.png" />
      </div>
      <div class="logo-title">思必驰标注训练一体化平台</div>
    </el-header>

    <el-main id="main-container" class="flex-center">
      <div class="loginDiv">
        <el-row>
          <el-col :span="12">
            <div class="adDiv">
              <div class="title">思必驰标注训练一体化平台</div>
              <div class="sub-title-icon" style="margin-left: 80px;"></div>
              <div class="sub-title">更专业</div>
              <div class="sub-title-icon"></div>
              <div class="sub-title">更简单</div>
              <div class="sub-title-icon"></div>
              <div class="sub-title">更强大</div>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="loginFormDiv">
              <div class="login-form-title">用户登录</div>
              <br />
              <el-form ref="loginform" :model="form" label-width="0px" :rules="rules">
                <el-form-item style="margin-bottom: 5px;">
                  <div class="form-item-label">账户</div>
                </el-form-item>
                <el-form-item label prop="userName">
                  <el-input v-model="form.userName" placeholder="请输入邮箱"></el-input>
                </el-form-item>
                <el-form-item style="margin-bottom: 5px;">
                  <div class="form-item-label">密码</div>
                </el-form-item>
                <el-form-item label prop="password">
                  <el-input v-model="form.password" type="password" placeholder="请输入密码"></el-input>
                </el-form-item>

                <el-form-item>
                  <el-button class="login-button" type="primary" @click="submitForm">登录</el-button>
                </el-form-item>
              </el-form>
            </div>
          </el-col>
        </el-row>
      </div>
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
            var errorMsg = "登录失败，请检查用户名与密码";
            if (res.message.indexOf("disabled")) {
              errorMsg = "登录失败，该用户已经被停用";
            }
            Message({
              message: errorMsg,
              type: "error"
            });
          }
        }
      });
    }
  }
};
</script>

<style scoped>
.el-container {
  height: 100%;

  background: rgba(246, 248, 250, 1);
}
.el-header {
  /* background-color: #b3c0d1; */
  color: #333;
  text-align: center;
  line-height: 30px;
  font-size: 13px;
  /* border-bottom: solid 1px #eee; */
}

.el-main {
  text-align: center;
  /* margin: 20px; */
  padding: 0px;
  border-radius: 4px;
}

.logo {
  float: left;
}
.logo img {
  width: 130px;
  margin-top: 20px;
}
.logo-title {
  float: left;
  font-size: 16px;
  font-family: NotoSansHans-Medium, NotoSansHans;
  font-weight: 500;
  color: rgba(74, 80, 94, 1);
  line-height: 70px;
}

.flex-center {
  display: flex;
  display: -moz-box;
  display: -ms-flexbox;
  display: -webkit-flex;
  align-items:center;
  -webkit-align-items: center;
  -moz-align-items: center;
  -ms-align-items: center;
  -o-align-items: center;
  justify-content:center;
  -webkit-justify-content: center;
  -moz-justify-content: center;
  -ms-justify-content: center;
  -o-justify-content: center;
  height:100%;
}

.loginDiv {
  margin: 0 auto;
  /* margin-top: 100px; */
  width: 940px;
  height: 500px;
  background: rgba(255, 255, 255, 1);
  box-shadow: 0px 2px 25px 0px rgba(220, 228, 238, 1);
  border-radius: 4px;
}

.adDiv {
  height: 500px;
  text-align: center;
  line-height: 35px;
  background-image: url("./../assets/bg_login_tiny.png");
  background-repeat: round;
}
.adDiv .title {
  font-size: 26px;
  font-family: NotoSansHans-Medium, NotoSansHans;
  font-weight: 500;
  color: rgba(255, 255, 255, 1);
  line-height: 39px;
  padding-top: 70px;
  padding-bottom: 20px;
}

.adDiv .sub-title-icon {
  float: left;
  background-image: url("./../assets/icon_check.png");
  background-repeat: round;
  width: 16px;
  height: 13px;
  margin-right: 5px;
  margin-top: 5px;
}
.adDiv .sub-title {
  float: left;
  font-size: 16px;
  font-family: NotoSansHans-Regular, NotoSansHans;
  font-weight: 400;
  color: rgba(255, 255, 255, 1);
  line-height: 24px;
  margin-right: 50px;
}

.loginFormDiv {
  text-align: center;
  padding: 0px 70px;
}

.login-form-title {
  text-align: center;
  height: 24px;
  font-size: 24px;
  font-family: NotoSansHans-Medium, NotoSansHans;
  font-weight: 500;
  color: rgba(74, 80, 94, 1);
  line-height: 36px;
  margin-top: 75px;
}

.login-button {
  width: 330px;
  background: rgba(64, 158, 255, 1);
  border-radius: 4px;
  margin-top: 50px;
}
.form-item-label {
  text-align: left;
  font-size: 14px;
  font-family: NotoSansHans-Regular, NotoSansHans;
  font-weight: 400;
  color: rgba(116, 130, 146, 1);
  line-height: 21px;
}
</style>

<style  >
.el-input__inner {
  border-radius: 0px;
  border-top-width: 0px;
  border-left-width: 0px;
  border-right-width: 0px;
  border-bottom-width: 1px;
  padding-left: 2px;
  padding-right: 2px;
}
</style>