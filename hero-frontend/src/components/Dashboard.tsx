import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { EventAPI } from '../services/api';
import { EventDashboard, Invitation } from '../types';
import '../styles/Dashboard.css';

export default function Dashboard() {
  const { eventId } = useParams<{ eventId: string }>();
  const [event, setEvent] = useState<EventDashboard | null>(null);
  const [invitations, setInvitations] = useState<Invitation[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [inviteEmails, setInviteEmails] = useState('');
  const [inviting, setInviting] = useState(false);
  const [inviteError, setInviteError] = useState<string | null>(null);
  const [inviteSuccess, setInviteSuccess] = useState<string | null>(null);
  const [copiedToken, setCopiedToken] = useState<string | null>(null);

  useEffect(() => {
    if (!eventId) return;
    fetchDashboard();
  }, [eventId]);

  const fetchDashboard = async () => {
    try {
      const [data, invs] = await Promise.all([
        EventAPI.getDashboard(parseInt(eventId!)),
        EventAPI.getInvitations(parseInt(eventId!)),
      ]);
      setEvent(data);
      setInvitations(invs);
    } catch (err: any) {
      setError('Failed to load dashboard');
    } finally {
      setLoading(false);
    }
  };

  const handleSendInvitations = async (e: React.FormEvent) => {
    e.preventDefault();
    const emails = inviteEmails.split(/[\s,]+/).map(e => e.trim()).filter(Boolean);
    if (emails.length === 0) return;
    setInviting(true);
    setInviteError(null);
    setInviteSuccess(null);
    try {
      await EventAPI.sendInvitations(parseInt(eventId!), { inviteeEmails: emails, eventId: parseInt(eventId!) });
      const invs = await EventAPI.getInvitations(parseInt(eventId!));
      setInvitations(invs);
      setInviteEmails('');
      setInviteSuccess(`${emails.length} invitation(s) sent.`);
    } catch (err: any) {
      setInviteError('Failed to send invitations.');
    } finally {
      setInviting(false);
    }
  };

  const copyLink = (token: string) => {
    navigator.clipboard.writeText(`http://localhost:5171/rsvp/${token}`);
    setCopiedToken(token);
    setTimeout(() => setCopiedToken(null), 2000);
  };

  const handleCloseEvent = async () => {
    if (!event) return;
    try {
      await EventAPI.closeEvent(event.id);
      setEvent(prev => prev ? { ...prev, status: 'CLOSED' } : null);
    } catch (err) {
      setError('Failed to close event');
    }
  };

  const handleCancelEvent = async () => {
    if (!event || !window.confirm('Are you sure you want to cancel this event?')) return;
    try {
      await EventAPI.cancelEvent(event.id);
      setEvent(prev => prev ? { ...prev, status: 'CANCELLED' } : null);
    } catch (err) {
      setError('Failed to cancel event');
    }
  };

  if (loading) return <div className="loading">Loading dashboard...</div>;
  if (error) return <div className="alert alert-error">{error}</div>;
  if (!event) return <div className="alert alert-error">Event not found</div>;

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h1>{event.title}</h1>
        <p className="event-meta">{new Date(event.eventDateTime).toLocaleString()}</p>
        <p className="event-location">{event.location}</p>
      </div>

      {event.description && (
        <div className="event-description">{event.description}</div>
      )}

      <div className="dashboard-stats">
        <div className="stat-card confirmed">
          <div className="stat-value">{event.confirmCount}</div>
          <div className="stat-label">Confirmed</div>
        </div>
        <div className="stat-card maybe">
          <div className="stat-value">{event.maybeCount}</div>
          <div className="stat-label">Maybe</div>
        </div>
        <div className="stat-card declined">
          <div className="stat-value">{event.declinedCount}</div>
          <div className="stat-label">Declined</div>
        </div>
        <div className="stat-card waitlist">
          <div className="stat-value">{event.waitlistCount}</div>
          <div className="stat-label">Waitlist</div>
        </div>
      </div>

      {event.maxCapacity && (
        <div className="capacity-info">
          <p>Capacity: {event.confirmCount} / {event.maxCapacity}</p>
          <div className="capacity-bar">
            <div
              className="capacity-fill"
              style={{ width: `${(event.confirmCount / event.maxCapacity) * 100}%` }}
            />
          </div>
        </div>
      )}

      <div className="event-actions">
        <button
          className="btn btn-secondary"
          onClick={handleCloseEvent}
          disabled={event.status !== 'OPEN'}
        >
          Close Event
        </button>
        <button
          className="btn btn-danger"
          onClick={handleCancelEvent}
          disabled={event.status === 'CANCELLED'}
        >
          Cancel Event
        </button>
      </div>

      <div className="invite-section">
        <h2>Invite People</h2>
        <form onSubmit={handleSendInvitations} className="invite-form">
          <input
            type="text"
            className="invite-input"
            placeholder="Enter emails separated by commas"
            value={inviteEmails}
            onChange={e => setInviteEmails(e.target.value)}
            disabled={inviting || event.status !== 'OPEN'}
          />
          <button
            type="submit"
            className="btn btn-primary"
            disabled={inviting || !inviteEmails.trim() || event.status !== 'OPEN'}
          >
            {inviting ? 'Sending...' : 'Send Invitations'}
          </button>
        </form>
        {inviteError && <div className="alert alert-error">{inviteError}</div>}
        {inviteSuccess && <div className="alert alert-success">{inviteSuccess}</div>}

        {invitations.length > 0 && (
          <table className="attendees-table" style={{ marginTop: '1rem' }}>
            <thead>
              <tr><th>Email</th><th>Invite Link</th></tr>
            </thead>
            <tbody>
              {invitations.map(inv => (
                <tr key={inv.id}>
                  <td>{inv.inviteeEmail}</td>
                  <td>
                    <button
                      className="btn-copy"
                      onClick={() => copyLink(inv.inviteToken)}
                    >
                      {copiedToken === inv.inviteToken ? 'Copied!' : 'Copy Link'}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      <div className="attendees-section">
        <h2>Attendees</h2>
        {event.attendees && event.attendees.length > 0 ? (
          <table className="attendees-table">
            <thead>
              <tr>
                <th>Email</th>
                <th>Status</th>
                <th>Responded</th>
              </tr>
            </thead>
            <tbody>
              {event.attendees.map((attendee, idx) => (
                <tr key={idx}>
                  <td>{attendee.email}</td>
                  <td>
                    <span className={`status-badge ${attendee.status.toLowerCase()}`}>
                      {attendee.waitlisted ? `${attendee.status} (Waitlisted)` : attendee.status}
                    </span>
                  </td>
                  <td>{attendee.respondedAt ? new Date(attendee.respondedAt).toLocaleDateString() : '-'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p>No RSVPs yet</p>
        )}
      </div>
    </div>
  );
}
