@echo off
echo 正在创建npm修复批处理文件...

:: 创建npm.bat文件，正确指向Node.js目录中的npm.cmd
echo @echo off > %USERPROFILE%\npm.bat
echo D:\Downloads-1\nodejs\npm.cmd %%* >> %USERPROFILE%\npm.bat

:: 创建npx.bat文件，正确指向Node.js目录中的npx.cmd
echo @echo off > %USERPROFILE%\npx.bat
echo D:\Downloads-1\nodejs\npx.cmd %%* >> %USERPROFILE%\npx.bat

echo 完成！现在您可以在任何目录使用npm和npx命令
echo 请重新启动命令提示符或PowerShell以应用更改
pause
