# Script chạy ứng dụng Ngân Hàng OOP với FlatLaf theme

$scriptPath = Split-Path -Parent -Path $MyInvocation.MyCommand.Definition
Set-Location $scriptPath

# Thiết lập classpath bao gồm lib folder
$classpath = "bin;lib/flatlaf-3.2.5.jar"

# Chạy ứng dụng với UTF-8 encoding
& java -Dfile.encoding=UTF-8 -cp $classpath com.bank.Main

# Giữ cửa sổ PowerShell lại
Read-Host "Nhấn Enter để thoát"
