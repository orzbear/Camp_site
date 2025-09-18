// API Response Types
export interface SpotResponse {
  id: number;
  name: string;
  lat: number;
  lon: number;
  description?: string;
  feeAud?: number;
  petAllowed?: boolean;
  bookable?: boolean;
  park: ParkInfo;
  amenities: AmenitiesInfo;
  createdAt: string;
  updatedAt: string;
}

export interface ParkInfo {
  id: number;
  name: string;
  region?: string;
  authority?: string;
  websiteUrl?: string;
}

export interface AmenitiesInfo {
  hasBbq?: boolean;
  hasToilet?: boolean;
  hasWater?: boolean;
  hasShelter?: boolean;
  hasPower?: boolean;
}

export interface SpotSearchRequest {
  query?: string;
  region?: string;
  petAllowed?: boolean;
  bookable?: boolean;
  hasBbq?: boolean;
  hasToilet?: boolean;
  hasWater?: boolean;
  hasShelter?: boolean;
  hasPower?: boolean;
  page?: number;
  size?: number;
}

export interface PlanRequest {
  lat: number;
  lon: number;
  from: string;
  to: string;
  prefs?: PlanPreferences;
}

export interface PlanPreferences {
  maxWindMps?: number;
  minTempC?: number;
  maxTempC?: number;
  maxUvIndex?: number;
  maxPrecipProb?: number;
}

export interface PlanResponse {
  requestId: string;
  status: 'processing' | 'completed' | 'error';
  windows: WeatherWindow[];
  message: string;
  createdAt: string;
}

export interface WeatherWindow {
  startTime: string;
  endTime: string;
  score: number;
  explanation: string;
  conditions: WeatherConditions;
}

export interface WeatherConditions {
  tempC?: number;
  precipProb?: number;
  windMps?: number;
  uvIndex?: number;
}

export interface Alert {
  id: number;
  park?: ParkInfo;
  severity: string;
  title: string;
  summary?: string;
  startsAt?: string;
  endsAt?: string;
  source?: string;
  url?: string;
  createdAt: string;
  fetchedAt: string;
}

export interface Forecast {
  id: number;
  lat: number;
  lon: number;
  hourUtc: string;
  tempC?: number;
  precipMm?: number;
  precipProb?: number;
  windMps?: number;
  uvIndex?: number;
  source: string;
  fetchedAt: string;
}

// UI State Types
export interface SearchFilters {
  query: string;
  region: string;
  petAllowed: boolean | null;
  bookable: boolean | null;
  amenities: {
    hasBbq: boolean;
    hasToilet: boolean;
    hasWater: boolean;
    hasShelter: boolean;
    hasPower: boolean;
  };
}

export interface PaginationInfo {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}
