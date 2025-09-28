# 贡献指南

感谢您对 DDL Task Manager 项目的关注！我们欢迎各种形式的贡献。

## 🤝 如何贡献

### 报告 Bug

如果您发现了 Bug，请：

1. 检查 [Issues](https://github.com/Soupcpu/DDL-Task-Manager/issues) 确认该问题尚未被报告
2. 创建新的 Issue，并提供以下信息：
   - Bug 的详细描述
   - 复现步骤
   - 预期行为 vs 实际行为
   - 设备信息（Android 版本、设备型号等）
   - 应用版本号
   - 相关截图（如果有）

### 功能建议

如果您有新功能的想法：

1. 检查现有 Issues 确认该功能尚未被提议
2. 创建 Feature Request Issue，描述：
   - 功能的详细说明
   - 使用场景和用户价值
   - 可能的实现方案（可选）

### 提交代码

#### 开发环境设置

1. Fork 本仓库
2. 克隆您的 Fork 到本地：
   ```bash
   git clone https://github.com/YOUR_USERNAME/DDL-Task-Manager.git
   ```
3. 创建开发分支：
   ```bash
   git checkout -b feature/your-feature-name
   ```
4. 安装开发依赖并确保项目能正常构建

#### 开发规范

**代码风格**
- 使用 Kotlin 官方代码风格
- 函数和变量命名使用 camelCase
- 类名使用 PascalCase
- 常量使用 UPPER_SNAKE_CASE

**提交规范**
- 使用清晰的提交信息
- 提交信息格式：
  ```
  type(scope): description

  详细说明（可选）
  ```
- 类型包括：
  - `feat`: 新功能
  - `fix`: Bug 修复
  - `docs`: 文档更新
  - `style`: 代码格式（不影响功能）
  - `refactor`: 重构
  - `test`: 测试相关
  - `chore`: 构建过程或辅助工具的变动

**代码质量**
- 新代码应包含适当的注释
- 复杂逻辑需要添加文档说明
- 确保新功能有对应的测试
- 运行现有测试确保没有破坏性变更

#### 提交 Pull Request

1. 确保代码已经过测试
2. 更新相关文档
3. 推送到您的 Fork：
   ```bash
   git push origin feature/your-feature-name
   ```
4. 创建 Pull Request，并填写 PR 模板中的所有信息

## 📋 开发指南

### 项目结构

```
app/src/main/java/com/example/myapplication/
├── adapter/          # RecyclerView 适配器
├── data/            # 数据模型和数据库
├── repository/      # 数据仓库层
├── utils/           # 工具类
├── viewmodel/       # ViewModel 层
├── MainActivity.kt  # 主活动
├── FirstFragment.kt # 主页面片段
├── SecondFragment.kt # 任务详情页面
└── SettingsActivity.kt # 设置页面
```

### 架构说明

本项目采用 MVVM 架构模式：

- **Model**: 数据层，包括 Room 数据库和 Repository
- **View**: UI 层，包括 Activity 和 Fragment
- **ViewModel**: 业务逻辑层，连接 View 和 Model

### 数据流

1. UI 层触发用户操作
2. ViewModel 处理业务逻辑
3. Repository 管理数据源
4. Room 数据库持久化数据
5. 数据变更通过 LiveData 通知 UI 更新

## 🧪 测试

### 运行测试

```bash
# 运行单元测试
./gradlew test

# 运行 UI 测试
./gradlew connectedAndroidTest
```

### 测试覆盖率

我们建议新功能的测试覆盖率应达到 80% 以上。

## 📝 文档

### 更新文档

如果您的贡献涉及：
- 新功能：更新 README.md 和用户文档
- API 变更：更新相关技术文档
- 构建过程变更：更新构建说明

### 文档风格

- 使用清晰、简洁的语言
- 提供代码示例
- 包含必要的截图
- 支持中英双语（优先中文）

## 🏷️ 版本发布

### 版本号规则

采用语义化版本号 (Semantic Versioning)：

- **主版本号**：不兼容的 API 修改
- **次版本号**：向下兼容的功能性新增
- **修订号**：向下兼容的问题修正

### 发布流程

1. 更新 CHANGELOG.md
2. 更新版本号
3. 创建 Release Tag
4. 生成发布包
5. 发布到 GitHub Releases

## ❓ 获得帮助

如果您在贡献过程中遇到问题：

1. 查看现有的 Issues 和 Discussions
2. 创建新的 Discussion 询问问题
3. 联系项目维护者

## 📜 行为准则

请遵守我们的行为准则：

- 尊重所有贡献者
- 使用包容性语言
- 专注于对项目和社区最有利的事情
- 对不同观点保持开放态度

---

再次感谢您的贡献！您的每一份努力都让这个项目变得更好。