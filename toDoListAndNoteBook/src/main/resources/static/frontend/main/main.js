// 检查登录状态
if (!localStorage.getItem('jwt_token')) {
    window.location.href = '../login/login.html';
}

// 添加请求拦截器
axios.interceptors.request.use(config => {
    const token = localStorage.getItem('jwt_token');
    console.log('[拦截器] Token:', token);
    console.log('[拦截器] 请求URL:', config.url);
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, error => {
    console.error('[拦截器] 请求错误:', error);
    return Promise.reject(error);
});

// 添加响应拦截器
axios.interceptors.response.use(response => {
    return response;
}, error => {
    if (error.response.status === 401) {
        localStorage.removeItem('jwt_token');
        window.location.href = '../2.html';
    }
    return Promise.reject(error);
});

new Vue({
    el: '#app',
    data() {
        return {
            // baseURL: 'http://localhost:8080',
            baseURL: 'http://localhost:8088/toDoListAndNoteBook',
            activeTab: 'todo',
            todos: [],
            currentTodo: {},
            todoDialogVisible: false,
            todoDialogTitle: '新建待办',
            todoQuery: { title: '', dateRange: [], status: '' },
            todoPager: { page: 1, total: 0 },
            pageSize: 5,
            notes: [],
            currentNote: {},
            noteDialogVisible: false,
            noteDialogTitle: '新建笔记',
            noteQuery: { title: '' },
            notePager: { page: 1, total: 0 },
            deletedFiles: [],
            userInfo: { id: null, username: '', email: '' },
            passwordDialogVisible: false,
            passwordForm: { newPassword: '', confirmPassword: '', verification: '' },
            deletionDialogVisible: false,
            stompClient: null
        };
    },
    computed: {
        // 为每个文件对象补充 fileType 信息，确保预览时正确判断文件类型
        fileList() {
            return (this.currentNote.attachedFiles || []).map(file => ({
                name: file.originalName,
                size: file.size,
                id: file.id,
                fileType: file.fileType,
                status: 'success'
            }));
        }
    },
    watch: {
        // 监听分页大小变化
        pageSize() {
            if (this.activeTab === 'todo') {
                this.todoPager.page = 1;
                this.fetchTodos();
            } else {
                this.notePager.page = 1;
                this.fetchNotes();
            }
        }
    },
    created() {
        const token = localStorage.getItem('jwt_token');
        if (token) {
            const payload = JSON.parse(atob(token.split('.')[1]));
            this.userInfo.id = payload.id;
            this.userInfo.username = payload.username;
            this.userInfo.email = payload.email;
        }
    },
    mounted() {
        this.fetchTodos();
        this.fetchNotes();
        this.connectWebSocket();
    },
    methods: {
        // WebSocket 连接与订阅提醒
        connectWebSocket() {
            const socket = new SockJS(this.baseURL + '/ws');
            this.stompClient = Stomp.over(socket);

            const _this = this;
            this.stompClient.connect({}, function(frame) {
                console.log('WebSocket 已连接:', frame);
                // 用 _this 而不是 this
                _this.stompClient.subscribe(
                    `/topic/reminders.${_this.userInfo.id}`,
                    msg => {
                        const text = msg.body;
                        _this.$message({ message: text, type: 'warning', duration: 10000 });
                    }
                );
            }, function(error) {
                console.error('WebSocket 连接出错:', error);
            });
        },

        // fetchTodos 方法
        async fetchTodos() {
            let url = `${this.baseURL}/todos/getAll`;
            const params = {
                pageNum: this.todoPager.page,
                pageSize: this.pageSize
            };

            if (this.todoQuery.title) {
                url = `${this.baseURL}/todos/getByTitle`;
                params.title = this.todoQuery.title;
            }

            if (this.todoQuery.dateRange?.length === 2) {
                const startDate = this.formatDate(this.todoQuery.dateRange[0]);
                const endDate = this.formatDate(this.todoQuery.dateRange[1]);
                params.startDate = startDate;
                params.endDate = endDate;

                if (this.todoQuery.status !== '') {
                    url = `${this.baseURL}/todos/getByDueDateAndStatus`;
                    params.status = this.todoQuery.status;
                } else {
                    url = `${this.baseURL}/todos/getByDueDate`;
                }
            } else if (this.todoQuery.status !== '') {
                url = `${this.baseURL}/todos/getByStatus`;
                params.status = this.todoQuery.status;
            }

            try {
                const res = await axios.get(url, { params });
                this.todos = res.data.data.todos.map(todo => ({
                    ...todo,
                    showFullContent: false,
                    dueDate: todo.dueDate ? todo.dueDate : null,
                    updatedAt: todo.updatedAt ? todo.updatedAt : null
                }));
                this.todoPager.total = res.data.data.total || 0;
            } catch (error) {
                this.$message.error('获取待办数据失败');
            }
        },

        // fetchNotes 方法
        async fetchNotes() {
            let url = `${this.baseURL}/notes/getAll`;
            const params = {
                pageNum: this.notePager.page,
                pageSize: this.pageSize
            };
            if (this.noteQuery.title) {
                url = `${this.baseURL}/notes/getByTitle`;
                params.title = this.noteQuery.title;
            }
            try {
                const res = await axios.get(url, { params });
                this.notes = res.data.data.notes.map(note => ({
                    ...note,
                    showFullContent: false,
                    updatedAt: note.updatedAt ? note.updatedAt : null
                }));
                this.notePager.total = res.data.data.total || 0;
            } catch (error) {
                this.$message.error('获取数据失败');
            }
        },

        showTodoDialog() {
            this.currentTodo = { status: 0 };
            this.todoDialogTitle = '新建待办';
            this.todoDialogVisible = true;
        },

        // 删除待办事项
        async deleteTodo(id) {
            try {
                await this.$confirm('确认删除该待办事项吗？', '提示', { type: 'warning' });
                await axios.delete(`${this.baseURL}/todos?id=${id}`);
                this.$message.success('删除成功');
                this.fetchTodos();
            } catch (error) {
                if (error !== 'cancel') {
                    this.$message.error('删除失败');
                }
            }
        },

        // 切换待办事项状态
        async toggleTodoStatus(todo) {
            try {
                const newStatus = todo.status === 0 ? 1 : 0;
                await axios.put(`${this.baseURL}/todos`, { ...todo, status: newStatus });
                this.fetchTodos();
            } catch (error) {
                this.$message.error('状态更新失败');
            }
        },

        // 编辑待办方法
        editTodo(todo) {
            this.currentTodo = {
                ...todo,
                dueDate: dayjs(this.currentTodo.dueDate).format('YYYY-MM-DD HH:mm:ss')
            };
            this.todoDialogTitle = '编辑待办';
            this.todoDialogVisible = true;
        },

        // 保存待办方法
        async saveTodo() {
            if (!this.currentTodo.title?.trim()) {
                this.$message.error('标题不能为空');
                return;
            }
            if (this.currentTodo.title.trim().length > 50) {
                this.$message.error('标题长度不能超过50字符');
                return;
            }
            if (!this.currentTodo.dueDate) {
                this.$message.error('请选择截止日期');
                return;
            }

            try {
                const todoData = {
                    ...this.currentTodo,
                    dueDate: dayjs(this.currentTodo.dueDate).format('YYYY-MM-DD HH:mm:ss')
                };

                const res = await (todoData.id ?
                    axios.put(`${this.baseURL}/todos`, todoData) :
                    axios.post(`${this.baseURL}/todos`, todoData));

                this.$message.success('保存成功');
                this.todoDialogVisible = false;

                if (todoData.id) {
                    const index = this.todos.findIndex(t => t.id === todoData.id);
                    if (index > -1) {
                        this.todos.splice(index, 1, res.data.data);
                    }
                } else {
                    this.todoPager.page = 1;
                    this.fetchTodos();
                }
            } catch (error) {
                this.$message.error(error.response?.data?.message || '保存失败');
            }
        },

        // 编辑笔记时初始化文件列表
        editNote(note) {
            this.currentNote = {
                ...note,
                attachedFiles: note.attachedFiles?.map(f => ({
                    name: f.originalName,
                    id: f.id,
                    fileType: f.fileType,
                    size: f.size,
                    status: 'success'
                })) || []
            };
            this.noteDialogTitle = '编辑笔记';
            this.noteDialogVisible = true;
        },

        async saveNote() {
            if (!this.currentNote.title?.trim()) {
                this.$message.error('标题不能为空');
                return;
            }
            if (this.currentNote.title.trim().length > 50) {
                this.$message.error('标题长度不能超过50字符');
                return;
            }
            if (!this.currentNote.content?.trim()) {
                this.$message.error('内容不能为空');
                return;
            }

            try {
                const formData = new FormData();
                const attachedFiles = this.fileList
                    .filter(f => f.id && !this.deletedFiles.includes(f.id))
                    .map(f => ({
                        id: f.id,
                        originalName: f.originalName || '',
                        storedName: f.storedName || '',
                        filePath: f.filePath || '',
                        fileType: f.fileType || '',
                        size: f.size || 0,
                        createdAt: f.createdAt || null,
                        noteId: f.noteId || null,
                        userId: f.userId || null,
                        fileHash: f.fileHash || ''
                    }));

                const noteBlob = new Blob([JSON.stringify({
                    id: this.currentNote.id,
                    title: this.currentNote.title,
                    content: this.currentNote.content,
                    attachedFiles: attachedFiles
                })], { type: "application/json" });

                formData.append('note', noteBlob, 'note.json');

                if (this.currentNote.uploadFiles) {
                    this.currentNote.uploadFiles.forEach(file => {
                        formData.append('files', file);
                    });
                }

                if (this.deletedFiles && this.deletedFiles.length > 0) {
                    this.deletedFiles.forEach(id => {
                        formData.append('deletedFiles', id);
                    });
                }

                this.$message.info('文件上传中，请稍候...');

                const url = `${this.baseURL}/notes`;
                const res = await (this.currentNote.id
                    ? axios.put(url, formData)
                    : axios.post(url, formData));

                this.$message.success('保存成功');
                this.noteDialogVisible = false;

                const updatedNote = res.data.data;
                if (this.currentNote.id) {
                    const index = this.notes.findIndex(n => n.id === this.currentNote.id);
                    if (index > -1) {
                        updatedNote.attachedFiles = res.data.data.attachedFiles || [];
                        this.notes.splice(index, 1, updatedNote);
                    }
                } else {
                    this.notePager.page = 1;
                    this.fetchNotes();
                }
            } catch (error) {
                if (error.response?.status === 413) {
                    this.$message.error('文件大小超过服务器限制');
                } else if (error.response?.data?.code === 3) {
                    this.$message.error(`文件类型不支持: ${error.response.data.message}`);
                } else if (error.response?.data?.code === 4) {
                    this.$message.error('文件大小超过10MB限制');
                } else {
                    this.$message.error(error.response?.data?.message || '保存失败');
                }
            }
        },

        // 显示笔记弹窗
        showNoteDialog() {
            this.currentNote = {};
            this.noteDialogTitle = '新建笔记';
            this.noteDialogVisible = true;
        },

        // 删除笔记
        async deleteNote(id) {
            try {
                await this.$confirm('确认删除该笔记吗？', '提示', { type: 'warning' });
                await axios.delete(`${this.baseURL}/notes?id=${id}`);
                this.$message.success('删除成功');
                this.fetchNotes();
            } catch (error) {
                if (error !== 'cancel') {
                    this.$message.error('删除失败');
                }
            }
        },

        // 文件预览方法
        // 修改后的文件预览方法
        previewFile(file) {
            axios.get(`${this.baseURL}/notes/getFiles?fileId=${file.id}`, {
                responseType: 'blob'
            }).then(response => {
                const blob = new Blob([response.data], { type: response.headers['content-type'] });
                const fileUrl = window.URL.createObjectURL(blob);

                const styles = `
                        <style>
                            @keyframes fadeIn {
                                from { opacity: 0; }
                                to { opacity: 1; }
                            }
                            .image-preview-modal {
                                width: auto !important;
                                max-width: 90vw;
                                max-height: 90vh;
                                padding: 0;
                                margin: 0;
                                background: transparent;
                            }
                            #preview-container {
                                width: 80vw;
                                height: 80vh;
                                background: #fff;
                                border-radius: 10px;
                                box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
                                display: flex;
                                align-items: center;
                                justify-content: center;
                                position: relative;
                                overflow: hidden;
                                touch-action: none;
                                animation: fadeIn 0.3s ease;
                                user-select: none;
                            }
                            #img-wrapper {
                                transition: transform 0.15s cubic-bezier(0.22, 1, 0.36, 1);
                                will-change: transform;
                            }
                            #preview-img {
                                max-width: 100%;
                                max-height: 100%;
                                object-fit: contain;
                                user-select: none;
                                -webkit-user-drag: none;
                                pointer-events: none;
                            }
                            #preview-controls {
                                position: absolute;
                                bottom: 12px;
                                left: 50%;
                                transform: translateX(-50%);
                                display: flex;
                                gap: 10px;
                                z-index: 10;
                            }
                            .ctrl-btn {
                                background: rgba(0, 0, 0, 0.7);
                                color: white;
                                border: none;
                                border-radius: 6px;
                                padding: 6px 12px;
                                cursor: pointer;
                                font-size: 14px;
                                transition: all 0.2s ease;
                            }
                            .ctrl-btn:hover {
                                background: rgba(0, 0, 0, 0.85);
                                transform: scale(1.05);
                            }
                            /* 预览框右上角的关闭按钮 */
                            #close-btn {
                                position: absolute;
                                top: 10px;
                                right: 10px;
                                z-index: 10;
                                font-size: 18px;
                                background: rgba(0, 0, 0, 0.6);
                                color: white;
                                border: none;
                                border-radius: 50%;
                                width: 32px;
                                height: 32px;
                                display: flex;
                                align-items: center;
                                justify-content: center;
                                cursor: pointer;
                                transition: background 0.2s ease;
                            }
                            #close-btn:hover {
                                background: rgba(0, 0, 0, 0.85);
                            }
                        </style>
                    `;

                if (file.fileType && file.fileType.startsWith('image/')) {
                    // 修改：取消关闭按钮注释，确保可以关闭预览窗口
                    const html = `
                            ${styles}
                            <div id="preview-container">
                                <button id="close-btn">&times;</button>
                                <div id="img-wrapper">
                                    <img id="preview-img" src="${fileUrl}" />
                                </div>
                                <div id="preview-controls">
                                    <button class="ctrl-btn" id="zoom-in">放大</button>
                                    <button class="ctrl-btn" id="zoom-out">缩小</button>
                                    <button class="ctrl-btn" id="reset">重置</button>
                                </div>
                            </div>
                        `;

                    let modal;
                    const cleanup = () => {
                        window.URL.revokeObjectURL(fileUrl);
                        if (modal) modal.close();
                    };

                    modal = this.$alert(html, {
                        dangerouslyUseHTMLString: true,
                        customClass: 'image-preview-modal',
                        title: '图片预览',
                        center: true,
                        showClose: false,
                        callback: cleanup
                    });

                    this.$nextTick(() => {
                        const container = document.getElementById('preview-container');
                        const wrapper = document.getElementById('img-wrapper');
                        const image = document.getElementById('preview-img');
                        const zoomInBtn = document.getElementById('zoom-in');
                        const zoomOutBtn = document.getElementById('zoom-out');
                        const resetBtn = document.getElementById('reset');
                        const closeBtn = document.getElementById('close-btn');

                        let scale = 1;
                        let translateX = 0;
                        let translateY = 0;
                        let isDragging = false;
                        let startX = 0;
                        let startY = 0;
                        let initialTranslateX = 0;
                        let initialTranslateY = 0;

                        const applyTransform = () => {
                            wrapper.style.transform = `translate3d(${translateX}px, ${translateY}px, 0) scale(${scale})`;
                        };

                        const smoothApplyTransform = () => {
                            wrapper.style.transition = 'transform 0.15s cubic-bezier(0.22, 1, 0.36, 1)';
                            applyTransform();
                            setTimeout(() => {
                                wrapper.style.transition = '';
                            }, 150);
                        };

                        const handleZoom = (delta, clientX, clientY) => {
                            const prevScale = scale;
                            scale = Math.max(0.5, Math.min(4, scale + delta));
                            const rect = container.getBoundingClientRect();
                            const offsetX = clientX - rect.left - translateX;
                            const offsetY = clientY - rect.top - translateY;
                            translateX -= offsetX * (scale / prevScale - 1);
                            translateY -= offsetY * (scale / prevScale - 1);
                            smoothApplyTransform();
                        };

                        const onWheel = (e) => {
                            e.preventDefault();
                            handleZoom(e.deltaY > 0 ? -0.1 : 0.1, e.clientX, e.clientY);
                        };

                        const reset = () => {
                            scale = 1;
                            translateX = 0;
                            translateY = 0;
                            smoothApplyTransform();
                        };

                        const onPointerDown = (e) => {
                            e.preventDefault();
                            isDragging = true;
                            startX = e.clientX;
                            startY = e.clientY;
                            initialTranslateX = translateX;
                            initialTranslateY = translateY;
                            container.style.cursor = 'grabbing';

                            const onPointerMove = (e) => {
                                if (!isDragging) return;
                                const dx = e.clientX - startX;
                                const dy = e.clientY - startY;
                                translateX = initialTranslateX + dx;
                                translateY = initialTranslateY + dy;
                                applyTransform();
                            };

                            const onPointerUp = () => {
                                isDragging = false;
                                container.style.cursor = 'grab';
                                container.removeEventListener('pointermove', onPointerMove);
                                container.removeEventListener('pointerup', onPointerUp);
                            };

                            container.addEventListener('pointermove', onPointerMove);
                            container.addEventListener('pointerup', onPointerUp);
                        };

                        container.addEventListener('wheel', onWheel, { passive: false });
                        container.addEventListener('pointerdown', onPointerDown);

                        zoomInBtn.addEventListener('click', () => handleZoom(0.2, container.clientWidth / 2, container.clientHeight / 2));
                        zoomOutBtn.addEventListener('click', () => handleZoom(-0.2, container.clientWidth / 2, container.clientHeight / 2));
                        resetBtn.addEventListener('click', reset);
                        closeBtn.addEventListener('click', cleanup);

                        container.style.cursor = 'grab';
                        image.onload = reset;
                    });
                } else if (file.fileType === 'application/pdf') {
                    window.open(fileUrl);
                } else {
                    this.$message.warning('不支持预览此文件类型');
                    window.URL.revokeObjectURL(fileUrl);
                }
            }).catch(() => {
                this.$message.error('预览文件失败');
            });
        },

        // 下载文件方法
        downloadFile(file) {
            axios.get(`${this.baseURL}/notes/getFiles?fileId=${file.id}&download=true`, {
                responseType: 'blob'
            }).then(response => {
                const blob = new Blob([response.data], { type: response.headers['content-type'] });
                const downloadUrl = window.URL.createObjectURL(blob);
                const link = document.createElement('a');
                link.href = downloadUrl;
                link.download = file.originalName;
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
                window.URL.revokeObjectURL(downloadUrl);
            }).catch(error => {
                this.$message.error('下载文件失败');
            });
        },

        // 获取文件URL
        getFileUrl(fileId) {
            return `${this.baseURL}/notes/getFiles?fileId=${fileId}`;
        },

        // 格式化日期
        formatDate(date) {
            return date.substring(0 , 10);
            // if (!date) return '';
            // return dayjs(date).format('YYYY-MM-DD');
        },

        // 格式化日期时间
        formatDateTime(datetime) {
            return datetime.substring(0 , 16);
            // const date = new Date(datetime);
            // return dayjs(date).format('YYYY-MM-DD HH:mm');
        },

        beforeFileRemove(file) {
            if (file.id) {
                return this.$confirm(`确定移除 ${file.name} 吗？`, '提示', { type: 'warning' });
            }
            return true;
        },

        handleExceed(files, fileList) {
            this.$message.warning(`最多选择5个文件，当前选择了 ${files.length + fileList.length} 个文件`);
        },

        formatFileSize(size) {
            if (size < 1024) return size + ' B';
            if (size < 1048576) return (size / 1024).toFixed(1) + ' KB';
            return (size / 1048576).toFixed(1) + ' MB';
        },

        // 处理文件变化
        handleFileChange(file, fileList) {
            const allowedTypes = ['image/jpeg', 'image/png', 'application/pdf'];
            if (!allowedTypes.includes(file.raw.type)) {
                this.$message.error('只支持 JPG/PNG/PDF 格式的文件');
                return false;
            }
            const maxSize = 10 * 1024 * 1024; // 10MB
            if (file.raw.size > maxSize) {
                this.$message.error(`文件大小不能超过${(maxSize/1024/1024).toFixed(0)}MB`);
                return false;
            }
            this.currentNote.uploadFiles = fileList
                .filter(f => f.raw)
                .map(f => f.raw);
        },

        handleFileRemove(file, fileList) {
            if (file.id) {
                this.deletedFiles.push(file.id);
                this.currentNote.attachedFiles = this.currentNote.attachedFiles.filter(
                    f => f.id !== file.id
                );
            }
            this.currentNote.uploadFiles = fileList
                .filter(f => f.raw)
                .map(f => f.raw);
        },

        // 退出登录
        logout() {
            localStorage.removeItem('jwt_token');
            window.location.href = '../login/login.html';
        },
        // 重置密码流程
        showPasswordDialog() {
            this.passwordForm = { newPassword: '', confirmPassword: '', verification: '' };
            this.passwordDialogVisible = true;
        },
        sendPasswordVerification() {
            axios.post(`${this.baseURL}/users/getVerificationCode`, { email: this.userInfo.email })
                .then(() => this.$message.success('验证码已发送'))
                .catch(() => this.$message.error('发送失败'));
        },
        saveNewPassword() {
            if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
                return this.$message.warning('两次密码不一致');
            }
            axios.put(`${this.baseURL}/users?verification=${this.passwordForm.verification}`, {
                id: this.userInfo.id,
                password: this.passwordForm.newPassword,
                email: this.userInfo.email
            }).then(() => {
                this.$message.success('密码已重置，请重新登录');
                this.logout();
            }).catch(() => this.$message.error('重置失败'));
        },
        // 注销账户流程
        showDeletionDialog() {
            this.deletionDialogVisible = true;
        },
        deleteAccount() {
            axios.delete(`${this.baseURL}/users?id=${this.userInfo.id}`)
                .then(() => {
                    this.$message.success('账户已注销');
                    this.logout();
                }).catch(() => this.$message.error('注销失败'));
        },

        // 获取文件图标
        getFileIcon(type) {
            if (!type) return 'el-icon-files';
            if (type.startsWith('image/')) return 'el-icon-picture';
            if (type === 'application/pdf') return 'el-icon-document';
            return 'el-icon-files';
        },

        // 分页处理方法
        handleTodoPageChange(page) {
            this.todoPager.page = page;
            this.fetchTodos();
        },

        handleNotePageChange(page) {
            this.notePager.page = page;
            this.fetchNotes();
        },

        // 切换展开和收起状态
        toggleExpand(todo) {
            todo.showFullContent = !todo.showFullContent;
        },

        toggleExpandNote(note) {
            note.showFullContent = !note.showFullContent;
        }
    }
});