$env:JAVA_HOME = "C:\Program Files\Java\jdk-18.0.1.1"
$env:PATH = "C:\Program Files\Java\jdk-18.0.1.1\bin;" + $env:PATH

# Paste your 16-character Gmail App Password below (no spaces).
# Generate one at: myaccount.google.com -> Security -> 2-Step Verification -> App passwords
$env:MAIL_PASSWORD = "gwdqtdyfniuvgoet"

Set-Location "C:\Users\97250\Xperience-Task-1-2026-04\hero-backend"
& "C:\Users\97250\Xperience-Task-1-2026-04\hero-backend\mvnw.cmd" "spring-boot:run"
