##### 上传代码遇到的问题
1. git push origin master 失败
- 原因: 本地git init 初始化后，先commit， 而后在github上创建了仓库，提交了许可证，导致两个分支是两个不同的版本，具有不同的提交历史
- 解决: git pull origin master --allow-unrelated-histories 可以允许不相关历史提，强制合并
