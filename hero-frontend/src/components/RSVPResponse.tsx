import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { RSVPApi, EventAPI } from '../services/api';
import { Event, RSVP, InvitationDetails } from '../types';
import '../styles/RSVPResponse.css';

export default function RSVPResponse() {
  const { token } = useParams<{ token: string }>();
  const [event, setEvent] = useState<Event | null>(null);
  const [invitationDetails, setInvitationDetails] = useState<InvitationDetails | null>(null);
  const [currentRSVP, setCurrentRSVP] = useState<RSVP | null>(null);
  const [selectedResponse, setSelectedResponse] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    if (!token) return;
    fetchData();
  }, [token]);

  const fetchData = async () => {
    try {
      const details = await RSVPApi.getInvitationDetails(token!);
      setInvitationDetails(details);

      const eventData = await EventAPI.getEvent(details.eventId);
      setEvent(eventData);

      try {
        const rsvpData = await RSVPApi.getRSVP(token!);
        setCurrentRSVP(rsvpData);
        setSelectedResponse(rsvpData?.responseStatus || null);
      } catch {
        // 404 means no RSVP submitted yet — that's fine
      }
    } catch (err: any) {
      setError('Invalid or expired invitation link');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (response: string) => {
    if (!token) return;
    setSubmitting(true);
    setError(null);

    try {
      const rsvp = await RSVPApi.submitRSVP({
        inviteToken: token,
        responseStatus: response,
      });
      setCurrentRSVP(rsvp);
      setSelectedResponse(response);
      setSuccess(true);
      setTimeout(() => setSuccess(false), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to submit RSVP');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <div className="loading">Loading invitation...</div>;
  if (error) return <div className="alert alert-error">{error}</div>;
  if (!event || !invitationDetails) return <div className="alert alert-error">Invitation not found</div>;

  const eventStarted = new Date(event.eventDateTime) < new Date();
  const isEventLocked = ['STARTED', 'CLOSED', 'CANCELLED'].includes(event.status) || eventStarted;

  return (
    <div className="rsvp-response-container">
      <div className="rsvp-card">
        <div className="rsvp-header">
          <h1>You're Invited!</h1>
          <p className="invite-email">Invited as: {invitationDetails.inviteeEmail}</p>
        </div>

        <div className="event-details">
          <h2>{event.title}</h2>
          <p className="event-date">{new Date(event.eventDateTime).toLocaleString()}</p>
          <p className="event-location">{event.location}</p>
          {event.description && <p className="event-desc">{event.description}</p>}
          {event.maxCapacity && (
            <p className="event-capacity">Max Capacity: {event.maxCapacity}</p>
          )}
        </div>

        {currentRSVP?.isWaitlisted && (
          <div className="alert alert-info">
            You are on the waitlist (Position: {currentRSVP.waitlistPosition})
          </div>
        )}

        {isEventLocked && (
          <div className="alert alert-warning">
            {eventStarted
              ? 'This event has started - RSVP changes are no longer allowed.'
              : 'This event is closed to further responses.'}
          </div>
        )}

        {success && (
          <div className="alert alert-success">Your RSVP has been recorded!</div>
        )}

        {error && <div className="alert alert-error">{error}</div>}

        <div className="rsvp-options">
          <p className="rsvp-prompt">How will you attend?</p>
          <div className="rsvp-buttons">
            <button
              className={`rsvp-btn yes ${selectedResponse === 'YES_CONFIRMED' ? 'active' : ''}`}
              onClick={() => handleSubmit('YES_CONFIRMED')}
              disabled={submitting || isEventLocked}
            >
              ✓ Yes
            </button>
            <button
              className={`rsvp-btn maybe ${selectedResponse === 'MAYBE' ? 'active' : ''}`}
              onClick={() => handleSubmit('MAYBE')}
              disabled={submitting || isEventLocked}
            >
              ? Maybe
            </button>
            <button
              className={`rsvp-btn no ${selectedResponse === 'NO' ? 'active' : ''}`}
              onClick={() => handleSubmit('NO')}
              disabled={submitting || isEventLocked}
            >
              ✗ No
            </button>
          </div>
        </div>

        {currentRSVP && (
          <div className="current-response">
            <p>
              Your current response: <strong>{currentRSVP.responseStatus}</strong>
            </p>
            <p className="response-time">
              Responded on: {new Date(currentRSVP.updatedAt).toLocaleString()}
            </p>
          </div>
        )}
      </div>
    </div>
  );
}
