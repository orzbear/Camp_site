import axios, { AxiosResponse } from 'axios';
import { 
  SpotResponse, 
  SpotSearchRequest, 
  PlanRequest, 
  PlanResponse, 
  Alert, 
  Forecast 
} from './types';

// API Configuration
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor for logging
api.interceptors.request.use(
  (config) => {
    console.log(`API Request: ${config.method?.toUpperCase()} ${config.url}`);
    return config;
  },
  (error) => {
    console.error('API Request Error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    console.error('API Response Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

// Spots API
export const spotsApi = {
      search: async (request: SpotSearchRequest): Promise<{ 
    content: SpotResponse[]; totalElements: number; totalPages: number; page: number; size: number 
  }> => {
    const params = new URLSearchParams();

    // strings
    if (request.query && request.query.trim() !== "") params.append("q", request.query.trim());
    if (request.region && request.region.trim() !== "") params.append("region", request.region.trim());

    // booleans: only append when explicitly true/false (not null/undefined)
    if (request.petAllowed != null) params.append("pet", String(request.petAllowed));
    if (request.bookable != null) params.append("bookable", String(request.bookable));

    // amenities: only when true
    if (request.hasBbq === true) params.append("amenities", "bbq");
    if (request.hasToilet === true) params.append("amenities", "toilet");
    if (request.hasWater === true) params.append("amenities", "water");
    if (request.hasShelter === true) params.append("amenities", "shelter");
    if (request.hasPower === true) params.append("amenities", "power");

    // pagination (only when not null/undefined)
    if (request.page != null) params.append("page", String(request.page));
    if (request.size != null) params.append("size", String(request.size));

    const url = `/spots/search${params.toString() ? `?${params.toString()}` : ""}`;
    const response: AxiosResponse<{ content: SpotResponse[]; totalElements: number; totalPages: number; number: number; size: number }> =
      await api.get(url);

    return {
      content: response.data.content,
      totalElements: response.data.totalElements,
      totalPages: response.data.totalPages,
      page: response.data.number,
      size: response.data.size,
    };
  },

  getById: async (id: number): Promise<SpotResponse> => {
    const response: AxiosResponse<SpotResponse> = await api.get(`/spots/${id}`);
    return response.data;
  },
};

// Plan API
export const planApi = {
  create: async (request: PlanRequest): Promise<PlanResponse> => {
    const response: AxiosResponse<PlanResponse> = await api.post('/plan', request);
    return response.data;
  },

  getResult: async (requestId: string): Promise<PlanResponse> => {
    const response: AxiosResponse<PlanResponse> = await api.get(`/plan/${requestId}`);
    return response.data;
  },
};

// Alerts API
export const alertsApi = {
  getActive: async (): Promise<Alert[]> => {
    const response: AxiosResponse<Alert[]> = await api.get('/alerts');
    return response.data;
  },

  getByPark: async (parkId: number): Promise<Alert[]> => {
    const response: AxiosResponse<Alert[]> = await api.get(`/alerts/park/${parkId}`);
    return response.data;
  },

  getBySeverity: async (severity: string): Promise<Alert[]> => {
    const response: AxiosResponse<Alert[]> = await api.get(`/alerts/severity/${severity}`);
    return response.data;
  },
};

// Forecast API
export const forecastApi = {
  get: async (lat: number, lon: number, from: string, to: string): Promise<Forecast[]> => {
    const params = new URLSearchParams({
      lat: lat.toString(),
      lon: lon.toString(),
      from,
      to,
    });
    
    const response: AxiosResponse<Forecast[]> = await api.get(`/forecast?${params.toString()}`);
    return response.data;
  },
};

export default api;
