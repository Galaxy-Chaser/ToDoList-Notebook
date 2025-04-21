new Vue({
    el: '#loginApp',
    data() {
        return {
            baseURL: 'http://localhost:8080',
            // baseURL: 'http://localhost:8088/toDoListAndNoteBook',
            // 登录
            loginLoading: false,
            loginForm: { username: '', password: '' },
            loginRules: {
                username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
                password: [
                    { required: true, message: '请输入密码', trigger: 'blur' },
                    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
                ]
            },
            // 注册
            showRegisterDialog: false,
            registerLoading: false,
            registerCodeLoading: false,
            registerForm: {
                username: '',
                email: '',
                verification: '',
                password: '',
                confirmPassword: ''
            },
            registerRules: {
                username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
                email: [
                    { required: true, message: '请输入邮箱', trigger: 'blur' },
                    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
                ],
                verification: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
                password: [
                    { required: true, message: '请输入密码', trigger: 'blur' },
                    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
                ],
                // ← 改成箭头函数，this 就指向 Vue 实例
                confirmPassword: [
                    {
                        validator: (rule, value, callback) => {
                            if (value !== this.registerForm.password) {
                                callback(new Error('两次输入密码不一致'));
                            } else {
                                callback();
                            }
                        },
                        trigger: 'blur'
                    }
                ]
            },
            // 重置密码
            showForgotDialog: false,
            forgotCodeLoading: false,
            forgotForm: { email: '', verification: '', password: '', confirmPassword: '' },
            forgotRules: {
                email: [
                    { required: true, message: '请输入邮箱', trigger: 'blur' },
                    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
                ],
                verification: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
                password: [
                    { required: true, message: '请输入新密码', trigger: 'blur' },
                    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
                ],
                // ← 这里也同理
                confirmPassword: [
                    {
                        validator: (rule, value, callback) => {
                            if (value !== this.forgotForm.password) {
                                callback(new Error('两次输入密码不一致'));
                            } else {
                                callback();
                            }
                        },
                        trigger: 'blur'
                    }
                ]
            }
        };
    },
    methods: {
        /** 登录 */
        handleLogin() {
            this.$refs.loginForm.validate(valid => {
                if (valid) {
                    this.loginLoading = true;
                    this.submitLogin();
                }
            });
        },
        async submitLogin() {
            try {
                const res = await axios.post(`${this.baseURL}/users/login`, {
                    username: this.loginForm.username,
                    password: this.loginForm.password
                });
                if (res.data.code === 200) {
                    localStorage.setItem('jwt_token', res.data.data);
                    window.location.href = '../main/main.html';
                } else {
                    this.$message.error(res.data.message || '登录失败');
                }
            } catch (err) {
                this.$message.error(err.response?.data?.message || '登录请求失败');
            } finally {
                this.loginLoading = false;
            }
        },

        /** 发送注册验证码 */
        async sendRegisterCode() {
            if (!this.registerForm.email) {
                this.$message.warning('请先填写邮箱');
                return;
            }
            this.registerCodeLoading = true;
            try {
                await axios.post(`${this.baseURL}/users/getVerificationCode`, {
                    email: this.registerForm.email
                });
                this.$message.success('验证码已发送，请注意查收');
            } catch (err) {
                this.$message.error(err.response?.data?.message || '发送验证码失败');
            } finally {
                this.registerCodeLoading = false;
            }
        },
        /** 注册 */
        handleRegister() {
            this.$refs.registerForm.validate(async valid => {
                if (!valid) return;
                this.registerLoading = true;
                try {
                    const { username, email, password, verification } = this.registerForm;
                    const res = await axios.post(
                        `${this.baseURL}/users/register?verification=${verification}`,
                        { username, email, password }
                    );
                    if (res.data.code === 200) {
                        this.$message.success('注册成功');
                        this.showRegisterDialog = false;
                    } else {
                        this.$message.error(res.data.message || '注册失败');
                    }
                } catch (err) {
                    this.$message.error(err.response?.data?.message || '注册请求失败');
                } finally {
                    this.registerLoading = false;
                }
            });
        },

        /** 发送重置验证码 */
        async sendForgotCode() {
            if (!this.forgotForm.email) {
                this.$message.warning('请先填写注册邮箱');
                return;
            }
            this.forgotCodeLoading = true;
            try {
                await axios.post(`${this.baseURL}/users/getVerificationCode`, {
                    email: this.forgotForm.email
                });
                this.$message.success('验证码已发送，请注意查收');
            } catch (err) {
                this.$message.error(err.response?.data?.message || '发送验证码失败');
            } finally {
                this.forgotCodeLoading = false;
            }
        },
        /** 重置密码 */
        handleReset() {
            this.$refs.forgotForm.validate(async valid => {
                if (!valid) return;
                try {
                    const { email, password, verification } = this.forgotForm;
                    const res = await axios.put(
                        `${this.baseURL}/users?verification=${verification}`,
                        { email, password }
                    );
                    if (res.data.code === 200) {
                        this.$message.success('密码重置成功');
                        this.showForgotDialog = false;
                    } else {
                        this.$message.error(res.data.message || '重置失败');
                    }
                } catch (err) {
                    this.$message.error(err.response?.data?.message || '重置请求失败');
                }
            });
        },

        openRegister() {
            this.showRegisterDialog = true;
        },
        handleCloseRegister(done) {
            this.$refs.registerForm.resetFields();
            done();
        },
        openForgot() {
            this.showForgotDialog = true;
        },
        handleCloseForgot(done) {
            this.$refs.forgotForm.resetFields();
            done();
        }
    }
});
