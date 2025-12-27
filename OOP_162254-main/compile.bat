@echo off
REM Compile OOP Project with UTF-8 encoding
cd /d "%~dp0"

REM Remove old bin files
if exist QuanLyNganHang\bin rmdir /s /q QuanLyNganHang\bin
mkdir QuanLyNganHang\bin

REM Change to project directory
cd QuanLyNganHang

REM Compile with UTF-8
javac -encoding UTF-8 -d bin -cp "lib/flatlaf-3.2.5.jar" ^
    src/com/bank/Main.java ^
    src/com/bank/controller/BankController.java ^
    src/com/bank/model/*.java ^
    src/com/bank/repository/*.java ^
    src/com/bank/service/*.java ^
    src/com/bank/view/*.java

echo Compilation complete!
pause
