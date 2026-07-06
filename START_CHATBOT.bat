@echo off
cd /d C:\Users\KING\Downloads\chatbot\chatbot

echo Starting Chatbot...

start mvnw spring-boot:run

timeout /t 6 > nul

start http://localhost:8081

exit