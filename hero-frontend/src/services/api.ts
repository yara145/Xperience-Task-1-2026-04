import axios from 'axios';
import {
  Event,
  CreateEventRequest,
  Invitation,
  SendInvitationRequest,
  RSVP,
  RSVPResponse,
  InvitationDetails,
  EventDashboard,
  RegisterRequest,
  LoginRequest,
  AuthResponse,
} from '../types';

const API_BASE_URL = 'http://localhost:8280/api';

const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

axiosInstance.interceptors.request.use(config => {
  const stored = localStorage.getItem('auth');
  if (stored) {
    const auth = JSON.parse(stored);
    config.headers.Authorization = `Bearer ${auth.token}`;
  }
  return config;
});

export const AuthAPI = {
  register: (data: RegisterRequest): Promise<AuthResponse> =>
    axiosInstance.post('/auth/register', data).then(res => res.data),

  login: (data: LoginRequest): Promise<AuthResponse> =>
    axiosInstance.post('/auth/login', data).then(res => res.data),

  logout: (): Promise<void> =>
    axiosInstance.post('/auth/logout').then(res => res.data),
};

export const EventAPI = {
  createEvent: (data: CreateEventRequest): Promise<Event> =>
    axiosInstance.post('/events', data).then(res => res.data),

  getEvent: (eventId: number): Promise<Event> =>
    axiosInstance.get(`/events/${eventId}`).then(res => res.data),

  getEventsByHost: (hostId: number): Promise<Event[]> =>
    axiosInstance.get(`/events/host/${hostId}`).then(res => res.data),

  getDashboard: (eventId: number): Promise<EventDashboard> =>
    axiosInstance.get(`/events/${eventId}/dashboard`).then(res => res.data),

  closeEvent: (eventId: number): Promise<Event> =>
    axiosInstance.post(`/events/${eventId}/close`).then(res => res.data),

  cancelEvent: (eventId: number): Promise<Event> =>
    axiosInstance.post(`/events/${eventId}/cancel`).then(res => res.data),

  sendInvitations: (eventId: number, data: SendInvitationRequest): Promise<any> =>
    axiosInstance.post(`/events/${eventId}/invite`, data).then(res => res.data),

  getInvitations: (eventId: number): Promise<Invitation[]> =>
    axiosInstance.get(`/events/${eventId}/invitations`).then(res => res.data),
};

export const RSVPApi = {
  submitRSVP: (data: RSVPResponse): Promise<RSVP> =>
    axiosInstance.post('/rsvp/submit', data).then(res => res.data),

  getRSVP: (inviteToken: string): Promise<RSVP> =>
    axiosInstance.get(`/rsvp/${inviteToken}`).then(res => res.data),

  getInvitationDetails: (inviteToken: string): Promise<InvitationDetails> =>
    axiosInstance.get(`/rsvp/invitation/${inviteToken}`).then(res => res.data),
};

export default axiosInstance;
