new Vue({
    el: '#loginApp',
    data() {
        return {
            baseURL: 'http://localhost:8088/toDoListAndNoteBook',
            loginLoading: false,
            registerLoading: false,
            showRegisterDialog: false,
            showForgotDialog: false,
            loginForm: {
                username: '',
                password: ''
            },
            loginRules: {
                username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
                password: [
                    { required: true, message: '请输入密码', trigger: 'blur' },
                    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
                ]
            },
            registerForm: {
                username: '',
                email: '',
                password: '',
                confirmPassword: ''
            },
            registerRules: {
                username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
                email: [
                    { required: true, message: '请输入邮箱', trigger: 'blur' },
                    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
                ],
                password: [
                    { required: true, message: '请输入密码', trigger: 'blur' },
                    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
                ],
                confirmPassword: [
                    { required: true, message: '请确认密码', trigger: 'blur' },
                    { validator: function(rule, value, callback) {
                            if (value !== this.registerForm.password) {
                                callback(new Error('两次输入密码不一致'));
                            } else {
                                callback();
                            }
                        }, trigger: 'blur'
                    }
                ]
            },
            forgotForm: {
                email: ''
            }
        };
    },
    methods: {
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
                const response = await axios.post(`${this.baseURL}/users/login`, {
                    username: this.loginForm.username,
                    password: this.loginForm.password
                });
                if (response.data.code === 200) {
                    localStorage.setItem('jwt_token', response.data.data);
                    window.location.href = '../main/main.html';
                } else {
                    this.$message.error(response.data.message || '登录失败');
                }
            } catch (error) {
                const message = error.response?.data?.message || '登录请求失败';
                this.$message.error(message);
            } finally {
                this.loginLoading = false;
            }
        },
        handleRegister() {
            this.$refs.registerForm.validate(async valid => {
                if (valid) {
                    this.registerLoading = true;
                    try {
                        const response = await axios.post(`${this.baseURL}/users`, {
                            username: this.registerForm.username,
                            email: this.registerForm.email,
                            password: this.registerForm.password
                        });
                        if (response.data.code === 200) {
                            this.$message.success(response.data.message || '注册成功');
                            this.showRegisterDialog = false;
                        } else {
                            this.$message.error(response.data.message || '注册失败');
                        }
                    } catch (error) {
                        const message = error.response?.data?.message || '注册请求失败';
                        this.$message.error(message);
                    } finally {
                        this.registerLoading = false;
                    }
                }
            });
        },
        handleForgot() {
            this.$message.info('重置密码功能暂未实现');
        },
        openRegister() {
            this.showRegisterDialog = true;
        },
        openForgot() {
            this.showForgotDialog = true;
        },
        handleCloseRegister(done) {
            this.$refs.registerForm.resetFields();
            done();
        },
        handleCloseForgot(done) {
            this.$refs.forgotForm.resetFields();
            done();
        }
    }
});
