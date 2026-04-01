# 执照认证系统

执照认证系统是一个面向学员的在线考试与课程认证平台，包含 H5 移动端（考生端）和 Web 管理后台。

## 项目文档

- `执照认证系统一期规划.md` — 功能范围、技术设计（数据模型、API、状态机）
- `执照认证系统一期需求文档.md` — 每个页面的字段、交互、业务规则
- `执照认证系统UI规范.md` — 色彩、字体、间距、组件规范
- `prototype-admin.html` — 后台管理原型（浏览器打开可查看）
- `prototype-h5.html` — H5 移动端原型（浏览器打开可查看）

## 技术栈

| 层 | 技术 |
|---|---|
| 后端 | Python 3.11+ / FastAPI / SQLAlchemy / Alembic |
| 前端 H5 | Vue3 + Vant 4 + Vite + Pinia + Vue Router |
| 前端后台 | Vue3 + ElementPlus + Vite + Pinia + Vue Router |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis（验证码、token） |
| 文件存储 | OSS / MinIO |

## 项目目录结构

```
执照认证系统/
├── backend/                    # 后端 FastAPI 项目
│   ├── app/
│   │   ├── main.py             # FastAPI 入口
│   │   ├── core/               # 配置、安全、依赖注入
│   │   │   ├── config.py       # 环境配置
│   │   │   ├── security.py     # JWT Token、密码工具
│   │   │   └── deps.py         # 通用依赖（get_db、get_current_user）
│   │   ├── models/             # SQLAlchemy 模型（每个表一个文件）
│   │   ├── schemas/            # Pydantic 请求/响应模型
│   │   ├── api/                # 路由
│   │   │   ├── v1/             # API v1
│   │   │   │   ├── auth.py     # 登录注册
│   │   │   │   ├── user.py     # 用户信息
│   │   │   │   ├── practice.py # 练习题库
│   │   │   │   ├── enrollment.py # 报考
│   │   │   │   ├── exam.py     # 考试
│   │   │   │   └── admin/      # 后台管理接口
│   │   │   │       ├── category.py
│   │   │   │       ├── question.py
│   │   │   │       ├── course.py
│   │   │   │       ├── exam.py
│   │   │   │       ├── student.py
│   │   │   │       └── audit.py
│   │   │   └── router.py       # 路由汇总
│   │   ├── services/           # 业务逻辑层
│   │   └── utils/              # 工具函数（短信、OSS、Excel）
│   ├── alembic/                # 数据库迁移
│   ├── requirements.txt
│   └── .env
├── frontend-h5/                # H5 移动端
│   ├── src/
│   │   ├── api/                # 接口请求（按模块拆分）
│   │   ├── views/              # 页面组件（按模块目录）
│   │   │   ├── login/
│   │   │   ├── practice/
│   │   │   ├── enrollment/
│   │   │   ├── exam/
│   │   │   ├── record/
│   │   │   └── profile/
│   │   ├── components/         # 通用组件
│   │   ├── stores/             # Pinia 状态管理
│   │   ├── router/             # 路由配置
│   │   ├── utils/              # 工具函数
│   │   └── styles/             # 全局样式、变量
│   └── vite.config.js
├── frontend-admin/             # 管理后台
│   ├── src/
│   │   ├── api/
│   │   ├── views/
│   │   │   ├── question/       # 题库管理
│   │   │   ├── exam/           # 考试管理
│   │   │   ├── student/        # 考生管理
│   │   │   ├── course/         # 课程管理
│   │   │   └── audit/          # 认证审核
│   │   ├── components/
│   │   ├── stores/
│   │   ├── router/
│   │   ├── utils/
│   │   └── styles/
│   └── vite.config.js
└── docs/                       # 设计文档（已有）
```

## 编码规范

### 后端（Python / FastAPI）

- **命名**：文件名和变量用 snake_case，类名用 PascalCase
- **分层**：router（路由入参校验）→ service（业务逻辑）→ model（数据访问）
- **模型**：SQLAlchemy 模型放 `models/`，每个表一个文件；Pydantic schema 放 `schemas/`，按模块拆分
- **依赖注入**：数据库 session 通过 `Depends(get_db)` 注入，当前用户通过 `Depends(get_current_user)` 获取
- **异常处理**：业务异常统一抛 `HTTPException`，使用标准 HTTP 状态码
- **数据库迁移**：使用 Alembic 管理，不要手动改表结构

### 前端（Vue3）

- **命名**：组件文件用 PascalCase（如 `QuestionList.vue`），工具函数用 camelCase
- **组合式 API**：统一使用 `<script setup>` 语法
- **状态管理**：全局状态用 Pinia，组件局部状态用 `ref/reactive`
- **请求封装**：`api/` 目录下按模块拆分，使用 axios 统一封装，包含 token 注入和错误拦截
- **路由守卫**：需登录页面统一在路由守卫中拦截，未登录跳转登录页

## API 通用约定

### 命名风格
- **API 请求参数和响应字段统一使用 snake_case**（如 `page_size`、`question_type`），与 Python 后端一致
- **前端内部使用 camelCase**（如 `pageSize`、`questionType`），遵循 JS/Vue 社区惯例，与 Vant/ElementPlus 组件 props 风格一致
- **axios 封装层自动转换**：请求时 camelCase → snake_case，响应时 snake_case → camelCase，开发者无需手动转换

### 请求头
```
Authorization: Bearer <token>
Content-Type: application/json
```

### 响应格式
```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```
- `code: 0` 表示成功，非 0 表示业务错误
- `message` 为错误时的提示信息，前端可直接展示
- `data` 为实际数据

### 分页请求
```
GET /api/xxx?page=1&page_size=20&keyword=xxx
```

### 分页响应
```json
{
  "code": 0,
  "data": {
    "list": [],
    "total": 100,
    "page": 1,
    "page_size": 20
  }
}
```

### 错误码约定

| code | 说明 |
|------|------|
| 0 | 成功 |
| 400 | 参数错误 |
| 401 | 未登录或 token 过期 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |
| 1001 | 验证码错误 |
| 1002 | 验证码过期 |
| 1003 | 账号已被禁用 |
| 1004 | 手机号已注册（代报名场景） |
| 2001 | 题目已被考试引用，无法删除 |
| 2002 | 分类下存在题目，无法删除 |
| 3001 | 材料审核未通过，无法考试 |
| 3002 | 重考次数已用完 |
| 3003 | 考试已交卷，无法重复提交 |
| 3004 | 人脸验证失败次数超限 |
| 3005 | 切屏超限，自动交卷 |
| 4001 | 申诉已提交，请勿重复提交 |

## 状态枚举值

### 材料审核状态（audit_status）
- 0 = 待审核
- 1 = 已通过
- 2 = 不通过

### 报考考试状态（exam_status）
- 0 = 未开始
- 1 = 考试中
- 2 = 已通过
- 3 = 未通过

### 交卷类型（submit_type）
- 1 = 主动交卷
- 2 = 超时自动交卷
- 3 = 中途退出交卷
- 4 = 作弊交卷（切屏超限）

### 题目类型（question_type）
- 1 = 单选
- 2 = 多选
- 3 = 判断

### 组卷方式（paper_mode）
- 1 = 手动选题
- 2 = 随机抽题

### 题目媒体类型（media_type）
- 1 = 图片
- 2 = 视频
- 3 = GIF 动态图

### 练习模式（practice mode）
- 1 = 全部练习
- 2 = 分类练习
- 3 = 随机练习
- 4 = 错题回顾
- 5 = 模拟考试

### 申诉状态（appeal_status）
- 0 = 待审核
- 1 = 通过
- 2 = 驳回

### 认证状态（certification_status，前端展示用，由 enrollment 数据计算得出）
- 0 = 未认证（无 enrollment 记录）
- 1 = 认证中（有 enrollment 且 exam_status ≠ 2）
- 2 = 已认证（enrollment.exam_status = 2）

### 通用状态
- 1 = 启用/生效/有效
- 0 = 停用/失效/无效

## Git 规范

- 分支命名：`feature/模块名`，如 `feature/question-bank`、`feature/h5-practice`
- 提交信息格式：`feat: 题库管理-题目列表页`、`fix: 修复考试交卷计分错误`
- 每个模块开发完成后提 PR 合入 master
