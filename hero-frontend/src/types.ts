// Event types
export interface Event {
  id: number;
  title: string;
  description: string;
  location: string;
  eventDateTime: string;
  maxCapacity: number | null;
  status: 'OPEN' | 'CLOSED' | 'CANCELLED' | 'STARTED';
  hostId: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateEventRequest {
  title: string;
  description: string;
  location: string;
  eventDateTime: string;
  maxCapacity: number | null;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  hostId: number;
  name: string;
  email: string;
  token: string;
}

// Invitation types
export interface Invitation {
  id: number;
  eventId: number;
  inviteeEmail: string;
  inviteToken: string;
  createdAt: string;
  updatedAt: string;
}

export interface SendInvitationRequest {
  inviteeEmails: string[];
  eventId: number;
}

// RSVP types
export interface RSVP {
  id: number;
  invitationId: number;
  eventId: number;
  responseStatus: 'YES_CONFIRMED' | 'YES_WAITLISTED' | 'NO' | 'MAYBE';
  isWaitlisted: boolean;
  waitlistPosition: number | null;
  respondedAt: string | null;
  updatedAt: string;
}

export interface RSVPResponse {
  responseStatus: 'YES_CONFIRMED' | 'NO' | 'MAYBE';
  inviteToken: string;
}

export interface InvitationDetails {
  invitationId: number;
  eventId: number;
  inviteeEmail: string;
  rsvpStatus: string | null;
}

// Dashboard types
export interface EventDashboard extends Event {
  confirmCount: number;
  maybeCount: number;
  declinedCount: number;
  waitlistCount: number;
  attendees: AttendeeInfo[];
}

export interface AttendeeInfo {
  email: string;
  status: string;
  waitlisted: boolean;
  respondedAt: string;
}
