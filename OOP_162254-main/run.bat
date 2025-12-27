@echo off
REM Script chạy ứng dụng Ngân Hàng OOP với FlatLaf theme

cd /d "%~dp0"

REM Thiết lập classpath bao gồm lib folder
set CLASSPATH=bin;lib/flatlaf-3.2.5.jar

REM Chạy ứng dụng với UTF-8 encoding
java -Dfile.encoding=UTF-8 -cp %CLASSPATH% com.bank.Main

pause
