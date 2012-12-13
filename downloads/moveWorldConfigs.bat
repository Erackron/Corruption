@echo off
if not exist "%~dp0\Worlds" md "%~dp0\Worlds"
attrib +h "%~dp0\Worlds" /s /d
for /f %%F in ('dir "%~dp0" /a:d-h /b') do (
	copy "%~dp0%%~nF\config.yml" "%~dp0\Worlds\%%~nF.yml" /y
	cls
	rmdir "%~dp0\%%~nF" /s /q
	cls
)
attrib -h "%~dp0\Worlds" /s /d
cls
for /f %%F in ('dir "%~dp0\Worlds" /b') do echo %%~nF\config.yml -^> Worlds\%%~nxF
echo.
pause
cls