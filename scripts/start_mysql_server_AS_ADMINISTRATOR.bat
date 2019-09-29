@echo off
:: Start the service by using the Windows task manager. Go to the services tab and start MYSQL80.
echo Read the instructions in this script. The script does not start the service!
pause > nul

if 0=1 (
  :: Starts the MySQL service
  :: For this to be possible, the directory that contains the MySQL server must contain a folder called "data". Otherwise there will be errors and the server will not start.
  "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqld.exe"
  pause > nul
)