@echo off
echo ===== 开始修复npm和npx命令 =====

:: 创建永久性的npm和npx批处理文件到用户目录
echo 创建npm和npx批处理文件...
echo @echo off > "%USERPROFILE%\npm.cmd"
echo D:\Downloads-1\nodejs\npm.cmd %%* >> "%USERPROFILE%\npm.cmd"

echo @echo off > "%USERPROFILE%\npx.cmd"
echo D:\Downloads-1\nodejs\npx.cmd %%* >> "%USERPROFILE%\npx.cmd"

:: 创建环境变量修改指南
echo.
echo ===== 环境变量设置指南 =====
echo 请按照以下步骤手动修改环境变量:
echo 1. 右键点击"此电脑"，选择"属性"
echo 2. 点击"高级系统设置"
echo 3. 点击"环境变量"按钮
echo 4. 在"用户变量"部分，选择"Path"，点击"编辑"
echo 5. 点击"新建"并添加以下路径:
echo    D:\Downloads-1\nodejs
echo    %USERPROFILE%
echo 6. 使用上下箭头按钮将这两个路径移到列表顶部
echo 7. 点击"确定"保存所有更改
echo.
echo ===== 管理员操作指南 =====
echo 请以管理员身份打开PowerShell并运行以下命令:
echo Rename-Item -Path "C:\WINDOWS\system32\npm" -NewName "npm.bak" -Force
echo.
echo 完成以上步骤后，打开新的命令提示符或PowerShell窗口并测试:
echo npm -v
echo.
echo ===== 如果您不想手动设置环境变量 =====
echo 可以使用以下命令临时修复当前会话:
echo set PATH=%USERPROFILE%;D:\Downloads-1\nodejs;%%PATH%%
echo.
pause
