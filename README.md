# Xperience Task — Event RSVP Manager

> **Xperience Educational Program — Task 01**
> Practice guide: [Building a Design File from Scratch with an AI Partner](https://xperience.works/educational-content/building-a-design-file-from-scratch-with-an-ai-partner)

---

## What is this task?

You are given an empty full-stack scaffold (Spring Boot + React).
Your job is **not** to just build features—it is to first produce a serious design file by following the 18-step guide above, using an AI partner (GitHub Copilot, ChatGPT, Claude, etc.).

Only after your design file is solid should you move on to implementation.

---

## The Feature: Event RSVP Manager

A small web application where users can host events and collect RSVPs from invitees.

### Raw feature brief

- A user can create an event with a title, description, date/time, location, and optional max-capacity.
- The creator becomes the **host** of that event.
- The host can invite people by email.
- Each invitee receives a unique link and can respond: **Yes / No / Maybe**.
- The host sees a live attendance dashboard with counts and a list of attendees.
- If the event has a max-capacity and it is reached, new "Yes" RSVPs go to a **waitlist**.
- A waitlisted attendee automatically moves to confirmed if a confirmed attendee changes their RSVP to No.
- The host can cancel the event or close it to further responses at any time.
- An invitee can change their RSVP at any point **before** the event starts.
- After the event start time, all RSVPs are locked.

Use this brief as input for **Step 02 – Capture the raw feature brief** in the guide.

---

## Tech Stack (already set up for you)

| Layer     | Technology                        |
|-----------|-----------------------------------|
| Backend   | Java 17, Spring Boot 4, Spring MVC, Spring Data JPA |
| Database  | PostgreSQL                        |
| Frontend  | React 19, TypeScript, Vite        |

---

## Prerequisites

Make sure the following are installed on your machine:

- [ ] Java 17+ (Java 18 recommended — see note below)
- [ ] Node.js 20+
- [ ] PostgreSQL (running locally on port 5432)
- [ ] pgAdmin (PostgreSQL GUI client)
- [ ] Git
- [ ] VS Code
- [ ] GitHub Copilot extension for VS Code

> **Java version note:** If your machine has multiple Java versions installed, make sure `JAVA_HOME` points to Java 17 or higher. Open `start-backend.ps1` and update the `JAVA_HOME` line if needed.

### Install VS Code

Download and install from [code.visualstudio.com](https://code.visualstudio.com/). Accept the defaults.

### Install pgAdmin

Download and install from [pgadmin.org](https://www.pgadmin.org/download/). During installation, **set the PostgreSQL superuser password to `1234`**. Use pgAdmin to create the database and run SQL in the Setup step below.

### Install GitHub Copilot

1. Open VS Code → press `Ctrl+Shift+X` to open the Extensions panel
2. Search for **GitHub Copilot** and click **Install**
3. When prompted, sign in with your GitHub account
4. Also install **GitHub Copilot Chat** for the chat interface

You'll use Copilot as your AI partner throughout the design phase.

---

## Setup

### 1. Clone the repository

```bash
git clone git@github.com:RamiY123/Xperience-Task-1-2026-04.git
cd Xperience-Task-1-2026-04
```

### 2. Create the database

Open pgAdmin and run:

```sql
CREATE DATABASE hero;
CREATE SCHEMA hero;
```

The default credentials expected by the app are `postgres / 1234`.
If you used a different password, update `hero-backend/src/main/resources/application.yml`.

### 3. Configure email (optional)

The app sends invitation emails via Gmail SMTP. To enable this:

1. Go to [myaccount.google.com](https://myaccount.google.com) → **Security** → **2-Step Verification** → **App passwords**
2. Generate an App Password and copy the 16-character code
3. Open `start-backend.ps1` and replace `YOUR_APP_PASSWORD_HERE` with your App Password

If you skip this step, invitations are still created and saved — you can copy the RSVP link from the dashboard manually.

### 4. Start the backend

Open PowerShell and run:

```powershell
.\start-backend.ps1
```

Wait until you see `Started HeroApplication` in the output. The backend runs on **http://localhost:8280**.

### 5. Start the frontend

Open a second PowerShell window and run:

```powershell
cd hero-frontend
npm install
npm run dev
```

The frontend runs on **http://localhost:5171** — open this in your browser.

---

## Your Task

### Phase 1 — Design (mandatory)

Open `DESIGN.md` at the root of the repo. This is your design file.

Work through all **18 steps** of the guide with your AI partner:

| # | Step |
|---|------|
| 01 | Choose the setup |
| 02 | Capture the raw feature brief |
| 03 | Write the problem statement |
| 04 | Define goals and non-goals |
| 05 | Capture context and constraints |
| 06 | Separate facts, assumptions, and open questions |
| 07 | Identify actors and workflows |
| 08 | Define invariants |
| 09 | Generate first-pass architecture |
| 10 | Define data ownership and state model |
| 11 | Add trust boundaries and security notes |
| 12 | Add concurrency and correctness notes |
| 13 | Add scalability and multi-tenancy notes |
| 14 | Add risks and failure notes |
| 15 | Generate alternatives and tradeoffs |
| 16 | Add rollout / migration notes |
| 17 | Assemble the first complete design draft |
| 18 | Run the pre-review weakness check |

Each step produces a section you copy into `DESIGN.md`.
Working notes and AI back-and-forth stay **out** of the file.

> A good design file for this feature should cover: a clear problem statement, explicit state machines for both Event and RSVP, named invariants (capacity, lock-after-start, waitlist promotion), trust boundaries between host and invitee, and at least one concurrency scenario (two simultaneous RSVPs filling the last spot).

### Phase 2 — Implementation (stretch goal)

Once your design file passes your own pre-review check (Step 18), implement the feature in the scaffold:

- Add JPA entities and repositories in `hero-backend/`
- Add REST endpoints in a new controller
- Wire up the React frontend in `hero-frontend/src/`

Commit your design file and code separately so reviewers can read the design before the code.

---

## Deliverables

| Deliverable | Where |
|-------------|-------|
| Completed design file | `DESIGN.md` |
| (Stretch) Working implementation | `hero-backend/` and `hero-frontend/` |

---

## Evaluation Criteria

Your design file will be reviewed against the **Definition of Success** from the guide:

- [ ] Clear problem statement
- [ ] Bounded scope (explicit non-goals)
- [ ] Visible assumptions, separated from facts
- [ ] Explicit workflows for all key actors
- [ ] Named invariants
- [ ] Real architecture boundaries
- [ ] Explicit state ownership
- [ ] First-pass trust / concurrency / scale treatment
- [ ] Visible risks and tradeoffs
- [ ] Unresolved open questions listed


---

## Implementation Complete ✅

The Event RSVP Manager has been fully implemented following Spring Boot best practices and the 18-step design.

### Backend Implementation (Spring Boot)

#### Architecture
Following Spring Boot best practices with separation of concerns:

```
com.xperience.hero/
├── model/           → JPA Entities (Event, Invitation, RSVP)
├── repository/      → Spring Data JPA Repositories
├── service/         → Business Logic (EventService, RSVPService, InvitationService, EmailService)
├── controller/      → REST Controllers (EventController, RSVPController)
├── dto/            → Data Transfer Objects
└── HeroApplication → Main Spring Boot Application
```

#### Key Components

**Models (JPA Entities)**
- `Event` — Event with title, location, date/time, capacity, status (OPEN/CLOSED/CANCELLED/STARTED_LOCKED)
- `Invitation` — Stores invitee email and unique invite token
- `RSVP` — Tracks RSVP status (YES_CONFIRMED, YES_WAITLISTED, NO, MAYBE), position in waitlist

**Services**
- `EventService` — Event lifecycle, capacity validation, state transitions
- `RSVPService` — RSVP submission, automatic waitlist promotion (transactional)
- `InvitationService` — Invitation generation, token management, email dispatch
- `EmailService` — Simple email sender for RSVP invitations

**Controllers**
- `EventController` — POST `/api/events`, GET `/api/events/{id}`, POST `/api/events/{id}/invite`, GET `/api/events/{id}/dashboard`
- `RSVPController` — POST `/api/rsvp/submit`, GET `/api/rsvp/{token}`, GET `/api/rsvp/invitation/{token}`

**Key Business Logic**
- Capacity enforcement: If max-capacity reached, new YES responses go to waitlist
- Automatic promotion: When a confirmed attendee changes to NO, first waitlisted person is promoted (atomic transaction)
- Lock after start: RSVPs rejected after event start time
- Event state transitions: OPEN → CLOSED/CANCELLED or auto-lock when event starts

#### Database (PostgreSQL)
Three core tables with proper schema:
```sql
-- hero.events (id, title, description, location, event_date_time, max_capacity, status, host_id, ...)
-- hero.invitations (id, event_id, invitee_email, invite_token, ...)
-- hero.rsvps (id, invitation_id, event_id, response_status, is_waitlisted, waitlist_position, ...)
```

### Frontend Implementation (React + TypeScript)

#### Architecture
```
src/
├── components/
│   ├── EventCreation.tsx     → Host event creation form
│   ├── Dashboard.tsx          → Host attendance dashboard
│   └── RSVPResponse.tsx        → Invitee RSVP response page
├── services/
│   └── api.ts                → Axios API client with typed endpoints
├── types.ts                  → TypeScript interfaces for all entities
├── App.tsx                   → React Router setup
├── App.css                   → App layout styles
└── index.css                 → Global styles + component styles
```

#### Routes
- `/` — Event creation
- `/event/:eventId` — Host dashboard
- `/rsvp/:token` — Invitee response page (unique link)

#### Features Implemented

**Event Creation** (`EventCreation.tsx`)
- Form to create event with title, description, location, date/time, max capacity
- Creates event via POST `/api/events`
- Redirects to dashboard after creation

**Host Dashboard** (`Dashboard.tsx`)
- Displays event details
- Shows RSVP counts: Confirmed, Maybe, Declined, Waitlist
- Displays capacity bar (if max-capacity set)
- Actions: Close Event, Cancel Event
- Lists all attendees with their RSVP status and response time
- Auto-fetches dashboard data from GET `/api/events/{id}/dashboard`

**Invitee Response** (`RSVPResponse.tsx`)
- Loads event details via unique invite token
- Shows current RSVP status (if already responded)
- Displays warning if event is locked or cancelled
- Three response buttons: ✓ Yes, ? Maybe, ✗ No
- Prevents changes after event start time
- Shows waitlist position if applicable

#### Styling
- Professional CSS with responsive layout
- Color-coded status badges (green=yes, yellow=maybe, red=no, blue=waitlist)
- Gradient navbar with navigation
- Stat cards for attendance overview
- Mobile-responsive forms and tables

### Dependencies Added

**Backend (pom.xml)**
- `spring-boot-starter-mail` — For email sending

**Frontend (package.json)**
- `react-router-dom@^6` — Client-side routing
- `axios@^1.7` — HTTP client with typed API service

### How to Run

#### Backend
```bash
cd hero-backend
mvnw clean install
mvnw spring-boot:run
# Backend runs on http://localhost:8280
```

#### Frontend
```bash
cd hero-frontend
npm install
npm run dev
# Frontend runs on http://localhost:5173
```

#### Database Setup
```sql
-- In PostgreSQL
CREATE DATABASE hero;
CREATE SCHEMA hero;

-- Tables auto-created by Hibernate (ddl-auto: update in application.yml)
```

### Key Architectural Decisions

1. **Transactional Consistency** — RSVPService uses `@Transactional` to ensure atomic capacity checks + promotion
2. **Token-Based Invitations** — Unique UUID tokens for secure, anonymous RSVP links (no user accounts required)
3. **Stateful Events** — Four event states (OPEN, CLOSED, CANCELLED, STARTED_LOCKED) prevent invalid state transitions
4. **FIFO Waitlist** — Position tracking ensures deterministic promotion order
5. **Separate DTOs** — EventDTO enriched with counts for dashboard; RSVPs isolated from UI
6. **CORS Enabled** — Controllers allow requests from React frontend on different port

### Testing Workflow

1. **Register** → Go to `http://localhost:5171/register` and create a host account
2. **Create Event** → Click "Create Event" and fill the form
3. **Send Invitations** → From the dashboard, enter invitee emails
4. **Submit RSVP** → Open the RSVP link from the dashboard (or from the invitation email) and respond
5. **View Dashboard** → Go to `/event/{eventId}` to see live counts
6. **Test Waitlist** → Set capacity=2, invite 3 people, confirm first 2, third goes to waitlist
7. **Test Promotion** → Change a confirmed to "No", watch waitlist person auto-promote

### Implementation Status
- ✅ Database schema with proper indexes
- ✅ JPA entities with lifecycle callbacks
- ✅ Spring Data repositories with custom queries
- ✅ Transactional services with waitlist logic
- ✅ REST controllers with CORS
- ✅ React components with TypeScript
- ✅ Routing and API integration
- ✅ Styling and responsive design
- ✅ Host authentication (register, login, logout, BCrypt passwords, session tokens)
- ✅ Protected routes and ownership enforcement
- ✅ Email delivery via Gmail SMTP (requires App Password — see setup)

---
