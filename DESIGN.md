```md
# Event RSVP Manager — Design File

---

# Step 01 — Choose the Setup

## Existing Stack

### Backend
- Java 17
- Spring Boot 4
- Spring MVC
- Spring Data JPA

### Database
- PostgreSQL

### Frontend
- React 19
- TypeScript
- Vite

## Architectural Direction

Monolithic web application:
- React frontend
- Spring Boot REST API backend
- PostgreSQL database

---

# Step 02 — Capture the Raw Feature Brief

## Feature Summary

The system allows users to host events and collect RSVPs from invitees.

## Functional Requirements

- A user can create an event with:
  - title
  - description
  - date/time
  - location
  - optional max-capacity

- The creator becomes the host.

- A host must register with a name, email, and password before creating events.

- A registered host can log in and log out.

- The host can invite people by email.

- Each invitee receives a unique link.

- Invitees can respond:
  - Yes
  - No
  - Maybe

- The host can see:
  - attendance counts
  - attendee list
  - RSVP statuses

- If max-capacity is reached:
  - new Yes responses become waitlisted

- If a confirmed attendee changes RSVP to No:
  - a waitlisted attendee moves to confirmed

- The host can:
  - cancel the event
  - close the event to responses

- Invitees can change RSVP before event start time.

- After the event starts:
  - all RSVPs are locked

---

# Step 03 — Write the Problem Statement

## Problem Statement

Hosts need a simple way to manage event invitations and track attendee responses.

The system should:
- allow event creation
- allow invitees to respond through unique links
- enforce capacity limits
- support waitlists
- lock RSVP changes after event start

The system should keep RSVP information consistent and easy to manage.

---

# Step 04 — Define Goals and Non-Goals

## Goals

- Create and manage events
- Invite attendees
- Collect RSVPs
- Support waitlists
- Show attendance information
- Prevent RSVP changes after event start

## Non-Goals

- Payment systems
- Calendar integrations
- SMS notifications
- Social media sharing
- Recurring events
- Multiple hosts per event
- Mobile applications
- Real-time websocket updates

---

# Step 05 — Capture Context and Constraints

## Technical Constraints

- Must use Spring Boot and React
- Must use PostgreSQL
- Must work inside the provided scaffold

## Product Constraints

- RSVP changes stop after event start
- Capacity rules must remain correct
- Waitlist promotion must be automatic

## Operational Constraints

- Educational project
- Simplicity preferred over optimization

---

# Step 06 — Separate Facts, Assumptions, and Open Questions

## Facts

- Events have hosts
- Hosts must register and authenticate to create or manage events
- Invitees use unique links
- Invitees can RSVP Yes / No / Maybe
- Capacity is optional
- Waitlists exist
- RSVP changes lock after event start

## Assumptions

- Invite links are token-based
- Invitees do not need accounts
- One RSVP exists per invitee per event
- Hosts are trusted to register with their own email — no ownership verification is performed
- Host sessions are identified by a UUID token stored server-side and sent as a Bearer header

## Open Questions

- Can hosts edit events after creation?
- Should waitlist order always be FIFO?
- Should invite links expire?
- Are duplicate invite emails allowed?

---

# Step 07 — Identify Actors and Workflows

## Actors

### Host
Creates and manages events.

### Invitee
Responds to invitations.

### System
Enforces rules and updates RSVP state.

---

## Workflows

### Register Host

1. User submits name, email, and password
2. System checks email is not already registered
3. System stores host with BCrypt-hashed password
4. System issues a session token
5. User is logged in immediately

### Login Host

1. Host submits email and password
2. System verifies password against stored hash
3. System issues a new session token
4. Token returned to client and stored locally

### Logout Host

1. Host requests logout
2. System clears session token from database
3. Client removes token from local storage

### Create Event

1. Host sends request with Bearer token
2. System validates token and identifies host
3. System stores event with host's ID as owner
4. Host becomes owner

### Invite Attendees

1. Host submits emails
2. System creates invitations
3. System generates invite links

### RSVP Response

1. Invitee opens link
2. System validates link
3. Invitee submits RSVP
4. System checks:
   - event state
   - event time
   - capacity
5. RSVP stored

### Waitlist Promotion

1. Confirmed attendee changes RSVP to No
2. System checks waitlist
3. Next waitlisted attendee becomes confirmed

---

## Failure Flows

### Unauthenticated Request

1. Request arrives without a Bearer token
2. System rejects with 401 Unauthorized
3. Frontend redirects user to login page

### Unauthorized Event Action

1. Authenticated host attempts to close or cancel an event they do not own
2. System rejects with 403 Forbidden

### Invalid Invite Link

1. Invitee opens invalid link
2. System rejects request
3. Error message displayed

### RSVP After Event Start

1. Invitee submits RSVP after event start
2. System rejects update
3. Existing RSVP remains unchanged

---

# Step 08 — Define Invariants

## Business Invariants

### Capacity Limit

Confirmed attendees cannot exceed max-capacity.

### Waitlist Promotion

A waitlisted attendee becomes confirmed only when capacity becomes available.

---

## Data Integrity Invariants

### Single RSVP

An invitee can only have one RSVP per event.

---

## Authorization Invariants

### RSVP Lock

RSVP changes are not allowed after event start time.

### Authentication Required

Only authenticated hosts can create events, invite attendees, view the dashboard, or change event status.

### Event Ownership

A host can only close, cancel, invite, or view the dashboard of events they created.

---

# Step 09 — Generate First-Pass Architecture

## Frontend

- Login and registration pages
- Event management UI
- RSVP response UI
- Attendance dashboard
- Protected routes (redirect to login if unauthenticated)

## Backend

- REST API
- Host authentication (register, login, logout)
- Business rule handling
- RSVP and waitlist handling

## Database

Stores:
- hosts (with hashed passwords and session tokens)
- events
- invitations
- RSVP data

---

# Step 10 — Define Data Ownership and State Model

## Data Ownership

| Entity | Owner |
|---|---|
| Host | Auth Service |
| Event | Event Service |
| Invitation | Invitation Service |
| RSVP | RSVP Service |

The backend is the source of truth for all event and RSVP state changes.

---

## Event States

- OPEN
- CLOSED
- CANCELLED
- STARTED

---

## RSVP States

- YES_CONFIRMED
- YES_WAITLISTED
- NO
- MAYBE

---

# Step 11 — Add Trust Boundaries and Security Notes

## Host Permissions

Hosts must be authenticated (valid Bearer session token) to:
- create events
- invite attendees
- close events
- cancel events
- view attendance dashboard

Hosts may only manage events they own. Requests on another host's event are rejected with 403.

## Invitee Permissions

Invitees can:
- access invitation link
- submit RSVP
- change RSVP before lock

No authentication is required for invitees — the invite token acts as proof of invitation.

## Security Notes

- Invite links are UUID tokens — hard to guess
- Session tokens are UUID tokens stored server-side — invalidated on logout
- Host passwords are hashed with BCrypt before storage
- Backend validates authentication and ownership on every host-only endpoint
- Frontend validation alone is not enough
- Host email ownership is not verified — any email can be used to register (consciously deferred)

---

# Step 12 — Add Concurrency and Correctness Notes

## Concurrency Scenario

### Scenario

Capacity = 100  
Confirmed attendees = 99

Two invitees submit Yes at the same time.

### Risk

Both requests may see one remaining spot.

### Required Protection

Capacity validation and RSVP updates must happen atomically.

---

## Duplicate Request Scenario

### Scenario

An invitee submits the same RSVP request multiple times.

### Risk

Duplicate updates may create inconsistent RSVP state.

### Required Protection

The backend should ensure only one active RSVP state exists per invitee per event.

---

# Step 13 — Add Scalability and Multi-Tenancy Notes

## Expected Scale

Small educational application with limited traffic.

## Multi-Tenancy

Not required for this project.

---

# Step 14 — Add Risks and Failure Notes

## Risks

### Token Leakage

Invite links may be shared accidentally.

### Timezone Errors

Incorrect time handling may lock RSVPs incorrectly.

### Race Conditions

Concurrent RSVP updates may break capacity rules.

### No Email Verification

A host can register using any email address, including one they do not own. Email verification was considered and deliberately deferred as out of scope for this version.

---

# Step 15 — Generate Alternatives and Tradeoffs

## Alternative — Invitee Accounts

### Advantage

Better identity validation.

### Disadvantage

Adds complexity and user friction.

### Decision

Rejected for simplicity.

---

## Alternative — Real-Time Updates

### Advantage

Live dashboard updates.

### Disadvantage

Additional complexity.

### Decision

Not required for initial version.

---

# Step 16 — Add Rollout / Migration Notes

## Initial Rollout

Single deployment with:
- frontend
- backend
- PostgreSQL database

## Migration Notes

The feature can be added without changing existing application behavior outside the RSVP flow.

---

# Step 17 — Assemble the First Complete Design Draft

## Design Summary

The Event RSVP Manager is a simple web application for creating events and managing RSVP responses.

The backend is responsible for:
- host authentication (register, login, logout)
- event rules and ownership enforcement
- RSVP validation
- waitlist handling
- capacity enforcement

The frontend is responsible for:
- host login and registration screens
- protected routing (unauthenticated users redirected to login)
- event management screens
- RSVP interaction
- attendance display

The design prioritizes:
- simplicity
- correctness
- clear ownership

---

# Step 18 — Run the Pre-Review Weakness Check

## Weakness Check

### PASS
- clear problem statement
- explicit workflows (including auth flows)
- named invariants (including authentication and ownership)
- state models
- trust boundaries (with auth mechanism documented)
- concurrency scenario
- visible assumptions
- open questions
- known gaps explicitly documented (email verification deferred)

## Remaining Open Questions

- Should invite links expire?
- Can hosts edit events after creation?
- Should duplicate invite emails be allowed?
```
