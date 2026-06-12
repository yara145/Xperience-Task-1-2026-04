import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { EventAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { Event } from '../types';
import '../styles/Home.css';

const STATUS_LABEL: Record<Event['status'], string> = {
  OPEN:           'Open',
  CLOSED:         'Closed',
  CANCELLED:      'Cancelled',
  STARTED: 'Started',
};

export default function Home() {
  const { user } = useAuth();
  const [events, setEvents] = useState<Event[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    EventAPI.getEventsByHost(user!.hostId)
      .then(setEvents)
      .catch(() => setError('Failed to load events'))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="home-container">
      <div className="home-header">
        <h1>My Events</h1>
        <Link to="/create" className="btn btn-primary">+ Create Event</Link>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      {loading ? (
        <div className="loading">Loading events...</div>
      ) : events.length === 0 ? (
        <div className="empty-state">
          <p>No events yet.</p>
          <Link to="/create" className="btn btn-primary">Create your first event</Link>
        </div>
      ) : (
        <div className="events-grid">
          {events.map(event => (
            <Link to={`/event/${event.id}`} key={event.id} className="event-card">
              <div className="event-card-header">
                <h2>{event.title}</h2>
                <span className={`status-badge ${event.status.toLowerCase()}`}>
                  {STATUS_LABEL[event.status]}
                </span>
              </div>
              <p className="event-card-date">
                {new Date(event.eventDateTime).toLocaleString()}
              </p>
              <p className="event-card-location">{event.location}</p>
              {event.description && (
                <p className="event-card-desc">{event.description}</p>
              )}
              {event.maxCapacity && (
                <p className="event-card-capacity">Capacity: {event.maxCapacity}</p>
              )}
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
