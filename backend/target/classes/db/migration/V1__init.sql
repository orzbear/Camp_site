-- OutScout Database Schema
-- Initial migration for parks, campsites, amenities, forecasts, and alerts

CREATE TABLE parks (
  id BIGSERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  region TEXT,
  authority TEXT,
  website_url TEXT,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE campsites (
  id BIGSERIAL PRIMARY KEY,
  park_id BIGINT REFERENCES parks(id) ON DELETE CASCADE,
  name TEXT NOT NULL,
  lat NUMERIC(9,6),
  lon NUMERIC(9,6),
  description TEXT,
  fee_aud NUMERIC(10,2),
  pet_allowed BOOLEAN,
  bookable BOOLEAN,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE amenities (
  campsite_id BIGINT PRIMARY KEY REFERENCES campsites(id) ON DELETE CASCADE,
  has_bbq BOOLEAN DEFAULT FALSE,
  has_toilet BOOLEAN DEFAULT FALSE,
  has_water BOOLEAN DEFAULT FALSE,
  has_shelter BOOLEAN DEFAULT FALSE,
  has_power BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE forecasts (
  id BIGSERIAL PRIMARY KEY,
  lat NUMERIC(9,6) NOT NULL,
  lon NUMERIC(9,6) NOT NULL,
  hour_utc TIMESTAMPTZ NOT NULL,
  temp_c DOUBLE PRECISION,
  precip_mm DOUBLE PRECISION,
  precip_prob DOUBLE PRECISION,
  wind_mps DOUBLE PRECISION,
  uv_index DOUBLE PRECISION,
  source VARCHAR(32) DEFAULT 'open-meteo',
  fetched_at TIMESTAMPTZ DEFAULT NOW(),
  UNIQUE(lat, lon, hour_utc)
);

CREATE TABLE alerts (
  id BIGSERIAL PRIMARY KEY,
  park_id BIGINT REFERENCES parks(id),
  severity VARCHAR(16) NOT NULL,
  title TEXT NOT NULL,
  summary TEXT,
  starts_at TIMESTAMPTZ,
  ends_at TIMESTAMPTZ,
  source VARCHAR(64),
  url TEXT,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  fetched_at TIMESTAMPTZ DEFAULT NOW()
);

-- Indexes for performance
CREATE INDEX idx_campsites_park ON campsites(park_id);
CREATE INDEX idx_campsites_flags ON campsites(pet_allowed, bookable);
CREATE INDEX idx_campsites_location ON campsites(lat, lon);
CREATE INDEX idx_forecasts_lookup ON forecasts(lat, lon, hour_utc);
CREATE INDEX idx_forecasts_time ON forecasts(hour_utc);
CREATE INDEX idx_alerts_park ON alerts(park_id);
CREATE INDEX idx_alerts_time ON alerts(starts_at, ends_at);
CREATE INDEX idx_alerts_severity ON alerts(severity);

-- Add updated_at triggers
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_parks_updated_at BEFORE UPDATE ON parks
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_campsites_updated_at BEFORE UPDATE ON campsites
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_amenities_updated_at BEFORE UPDATE ON amenities
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
