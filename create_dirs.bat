@echo off
REM Create directory structure for Spring Boot project
setlocal enabledelayedexpansion

set basedir=C:\Users\97250\Xperience-Task-1-2026-04\hero-backend\src\main\java\com\xperience\hero

echo Creating directories...
md "%basedir%\model" 2>nul
md "%basedir%\service" 2>nul
md "%basedir%\repository" 2>nul
md "%basedir%\controller" 2>nul
md "%basedir%\dto" 2>nul

echo.
echo ===== DIRECTORY STRUCTURE CREATED =====
echo.
echo Base directory: %basedir%
echo.
echo Contents:
echo.

REM List directories
for /d %%D in ("%basedir%\*") do (
    echo 📁 %%~nxD\
    dir "%%D" /b /a:-d 2>nul | findstr . >nul
    if !errorlevel! equ 0 (
        for /f "tokens=*" %%F in ('dir "%%D" /b /a:-d') do (
            echo    └─ %%F
        )
    ) else (
        echo    (empty directory^)
    )
    echo.
)

echo ===== VERIFICATION =====
echo.
if exist "%basedir%\model" echo ✓ model directory exists
if exist "%basedir%\service" echo ✓ service directory exists
if exist "%basedir%\repository" echo ✓ repository directory exists
if exist "%basedir%\controller" echo ✓ controller directory exists
if exist "%basedir%\dto" echo ✓ dto directory exists

echo.
echo ===== FULL TREE VIEW =====
tree "%basedir%" /a 2>nul

pause
