# 更新日志

本文档记录了 DDL Task Manager 的所有重要变更。

格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/)，
版本号遵循 [语义化版本](https://semver.org/lang/zh-CN/)。

## [未发布]

### 计划中
- 任务统计和数据可视化
- 导入/导出功能
- 主题自定义
- 桌面小部件
- 云同步功能

## [1.0.0] - 2025-09-28

### 新增
- 🎉 **初版发布**
- ✅ **核心功能**
  - 任务创建、编辑、删除
  - 截止日期设置和提醒
  - 四级优先级系统（低、中、高、紧急）
  - 任务分类管理
  - 任务完成状态切换

- 🌍 **多语言支持**
  - 简体中文
  - English

- 🎨 **用户界面**
  - Material Design 设计语言
  - 现代化卡片式布局
  - 响应式UI设计
  - 优雅的动画效果

- 💾 **数据存储**
  - Room 数据库本地存储
  - 数据持久化保存
  - 高效的数据访问

- ⚙️ **设置功能**
  - 语言切换
  - 应用主题设置

- 🏗️ **技术架构**
  - MVVM 架构模式
  - Kotlin 协程异步处理
  - ViewBinding 视图绑定
  - Navigation Component 导航
  - LiveData 数据观察

### 技术规格
- **最低支持版本**: Android 5.0 (API level 21)
- **目标版本**: Android 14 (API level 34)
- **编译版本**: Android 14 (API level 34)
- **应用大小**: 约 10MB

### 已知问题
- 暂无已知问题

---

## 版本说明

### 版本号格式
采用语义化版本号: `主版本号.次版本号.修订号`

- **主版本号**: 不兼容的 API 修改
- **次版本号**: 向下兼容的功能性新增
- **修订号**: 向下兼容的问题修正

### 变更类型
- `新增`: 新功能
- `变更`: 对现有功能的变更
- `弃用`: 即将移除的功能
- `移除`: 已移除的功能
- `修复`: 错误修复
- `安全`: 安全相关的修复

### 链接格式
- [未发布]: https://github.com/Soupcpu/DDL-Task-Manager/compare/v1.0.0...HEAD
- [1.0.0]: https://github.com/Soupcpu/DDL-Task-Manager/releases/tag/v1.0.0