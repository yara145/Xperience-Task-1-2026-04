@echo off
cd C:\Users\97250\Xperience-Task-1-2026-04\hero-backend\src\main\java\com\xperience\hero

REM Create subdirectories
mkdir model
mkdir service
mkdir repository
mkdir controller
mkdir dto

echo Directories created

REM Move model files
move Event.java model\
move Invitation.java model\
move RSVP.java model\

echo Model files moved

REM Move service files
move EmailService.java service\
move EventService.java service\
move RSVPService.java service\
move InvitationService.java service\

echo Service files moved

REM Move repository files
move EventRepository.java repository\
move InvitationRepository.java repository\
move RSVPRepository.java repository\

echo Repository files moved

REM Move controller files
move EventController.java controller\
move RSVPController.java controller\

echo Controller files moved

REM Move DTO files
move EventDTO.java dto\

echo DTO files moved

echo.
echo All files organized successfully!
echo.
dir /s
