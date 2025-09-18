import React, { useState } from 'react';
import { planApi } from '../lib/api';
import { useSSE } from '../lib/useSSE';
import { PlanRequest, PlanResponse, WeatherWindow } from '../lib/types';
import { MapPin, Calendar, Thermometer, Droplets, Wind, Sun, Loader2, CheckCircle, AlertCircle } from 'lucide-react';

export function Planner() {
  const [formData, setFormData] = useState<PlanRequest>({
    lat: 0,
    lon: 0,
    from: '',
    to: '',
    prefs: {
      maxWindMps: 8,
      minTempC: 18,
      maxTempC: 28,
      maxUvIndex: 8,
      maxPrecipProb: 0.3,
    },
  });
  
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [planResult, setPlanResult] = useState<PlanResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [requestId, setRequestId] = useState<string | null>(null);

  const { isConnected, lastEvent } = useSSE(
    requestId ? `http://localhost:8080/api/plan/stream/${requestId}` : '',
    {
      onMessage: (event) => {
        if (event.type === 'update' && event.data) {
          setPlanResult(event.data);
        }
      },
      onError: (error) => {
        console.error('SSE Error:', error);
        setError('Connection lost. Please try again.');
      },
    }
  );

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    setError(null);
    setPlanResult(null);

    try {
      // Validate coordinates
      if (formData.lat === 0 && formData.lon === 0) {
        throw new Error('Please enter valid coordinates');
      }

      // Validate dates
      const fromDate = new Date(formData.from);
      const toDate = new Date(formData.to);
      if (fromDate >= toDate) {
        throw new Error('End date must be after start date');
      }

      const response = await planApi.create(formData);
      setRequestId(response.requestId);
      setPlanResult(response);
    } catch (err) {
      console.error('Error creating plan:', err);
      setError(err instanceof Error ? err.message : 'Failed to create plan');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleInputChange = (field: string, value: any) => {
    setFormData(prev => ({
      ...prev,
      [field]: value,
    }));
  };

  const handlePrefChange = (field: string, value: number) => {
    setFormData(prev => ({
      ...prev,
      prefs: {
        ...prev.prefs!,
        [field]: value,
      },
    }));
  };

  const getScoreColor = (score: number) => {
    if (score >= 80) return 'text-green-600 bg-green-100';
    if (score >= 60) return 'text-yellow-600 bg-yellow-100';
    return 'text-red-600 bg-red-100';
  };

  const formatDateTime = (dateTime: string) => {
    return new Date(dateTime).toLocaleString('en-AU', {
      weekday: 'short',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  return (
    <div className="max-w-4xl mx-auto">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Trip Planner</h1>
        <p className="text-gray-600">
          Plan your outdoor adventure with real-time weather analysis and optimal timing recommendations.
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* Form */}
        <div className="card">
          <h2 className="text-xl font-semibold text-gray-900 mb-6">Plan Your Trip</h2>
          
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Location */}
            <div>
              <label className="label">Location Coordinates</label>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <input
                    type="number"
                    step="any"
                    placeholder="Latitude"
                    value={formData.lat || ''}
                    onChange={(e) => handleInputChange('lat', parseFloat(e.target.value) || 0)}
                    className="input"
                    required
                  />
                </div>
                <div>
                  <input
                    type="number"
                    step="any"
                    placeholder="Longitude"
                    value={formData.lon || ''}
                    onChange={(e) => handleInputChange('lon', parseFloat(e.target.value) || 0)}
                    className="input"
                    required
                  />
                </div>
              </div>
              <p className="text-sm text-gray-500 mt-1">
                Enter the latitude and longitude of your destination
              </p>
            </div>

            {/* Date Range */}
            <div>
              <label className="label">Trip Duration</label>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <input
                    type="datetime-local"
                    value={formData.from}
                    onChange={(e) => handleInputChange('from', e.target.value)}
                    className="input"
                    required
                  />
                </div>
                <div>
                  <input
                    type="datetime-local"
                    value={formData.to}
                    onChange={(e) => handleInputChange('to', e.target.value)}
                    className="input"
                    required
                  />
                </div>
              </div>
            </div>

            {/* Preferences */}
            <div>
              <label className="label">Weather Preferences</label>
              <div className="space-y-4">
                <div>
                  <label className="text-sm text-gray-600">Temperature Range (°C)</label>
                  <div className="grid grid-cols-2 gap-2 mt-1">
                    <input
                      type="number"
                      placeholder="Min"
                      value={formData.prefs?.minTempC || ''}
                      onChange={(e) => handlePrefChange('minTempC', parseFloat(e.target.value) || 0)}
                      className="input"
                    />
                    <input
                      type="number"
                      placeholder="Max"
                      value={formData.prefs?.maxTempC || ''}
                      onChange={(e) => handlePrefChange('maxTempC', parseFloat(e.target.value) || 0)}
                      className="input"
                    />
                  </div>
                </div>
                
                <div>
                  <label className="text-sm text-gray-600">Max Wind Speed (m/s)</label>
                  <input
                    type="number"
                    step="0.1"
                    value={formData.prefs?.maxWindMps || ''}
                    onChange={(e) => handlePrefChange('maxWindMps', parseFloat(e.target.value) || 0)}
                    className="input"
                  />
                </div>
                
                <div>
                  <label className="text-sm text-gray-600">Max Precipitation Probability</label>
                  <input
                    type="number"
                    step="0.1"
                    min="0"
                    max="1"
                    value={formData.prefs?.maxPrecipProb || ''}
                    onChange={(e) => handlePrefChange('maxPrecipProb', parseFloat(e.target.value) || 0)}
                    className="input"
                  />
                </div>
                
                <div>
                  <label className="text-sm text-gray-600">Max UV Index</label>
                  <input
                    type="number"
                    step="0.1"
                    value={formData.prefs?.maxUvIndex || ''}
                    onChange={(e) => handlePrefChange('maxUvIndex', parseFloat(e.target.value) || 0)}
                    className="input"
                  />
                </div>
              </div>
            </div>

            <button
              type="submit"
              disabled={isSubmitting}
              className="btn btn-primary w-full"
            >
              {isSubmitting ? (
                <>
                  <Loader2 className="h-4 w-4 animate-spin mr-2" />
                  Creating Plan...
                </>
              ) : (
                'Create Plan'
              )}
            </button>
          </form>
        </div>

        {/* Results */}
        <div className="space-y-6">
          {/* Status */}
          {planResult && (
            <div className="card">
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-lg font-semibold text-gray-900">Plan Status</h3>
                {isConnected && (
                  <div className="flex items-center text-green-600">
                    <div className="w-2 h-2 bg-green-500 rounded-full mr-2 animate-pulse"></div>
                    <span className="text-sm">Live Updates</span>
                  </div>
                )}
              </div>
              
              <div className="space-y-2">
                <div className="flex items-center">
                  {planResult.status === 'completed' && <CheckCircle className="h-5 w-5 text-green-500 mr-2" />}
                  {planResult.status === 'error' && <AlertCircle className="h-5 w-5 text-red-500 mr-2" />}
                  {planResult.status === 'processing' && <Loader2 className="h-5 w-5 animate-spin text-blue-500 mr-2" />}
                  <span className="font-medium capitalize">{planResult.status}</span>
                </div>
                <p className="text-sm text-gray-600">{planResult.message}</p>
              </div>
            </div>
          )}

          {/* Weather Windows */}
          {planResult?.windows && planResult.windows.length > 0 && (
            <div className="card">
              <h3 className="text-lg font-semibold text-gray-900 mb-4">Recommended Weather Windows</h3>
              <div className="space-y-4">
                {planResult.windows.map((window: WeatherWindow, index: number) => (
                  <div key={index} className="border border-gray-200 rounded-lg p-4">
                    <div className="flex items-center justify-between mb-3">
                      <div className="flex items-center space-x-2">
                        <Calendar className="h-4 w-4 text-gray-400" />
                        <span className="text-sm font-medium text-gray-700">
                          {formatDateTime(window.startTime)} - {formatDateTime(window.endTime)}
                        </span>
                      </div>
                      <span className={`px-2 py-1 rounded-full text-sm font-medium ${getScoreColor(window.score)}`}>
                        {Math.round(window.score)}/100
                      </span>
                    </div>
                    
                    <div className="grid grid-cols-2 gap-4 mb-3">
                      <div className="flex items-center text-sm">
                        <Thermometer className="h-4 w-4 text-gray-400 mr-2" />
                        <span>{window.conditions.tempC?.toFixed(1)}°C</span>
                      </div>
                      <div className="flex items-center text-sm">
                        <Droplets className="h-4 w-4 text-gray-400 mr-2" />
                        <span>{(window.conditions.precipProb || 0) * 100}%</span>
                      </div>
                      <div className="flex items-center text-sm">
                        <Wind className="h-4 w-4 text-gray-400 mr-2" />
                        <span>{window.conditions.windMps?.toFixed(1)} m/s</span>
                      </div>
                      <div className="flex items-center text-sm">
                        <Sun className="h-4 w-4 text-gray-400 mr-2" />
                        <span>{window.conditions.uvIndex?.toFixed(1)}</span>
                      </div>
                    </div>
                    
                    <p className="text-sm text-gray-600">{window.explanation}</p>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Error */}
          {error && (
            <div className="card border-red-200 bg-red-50">
              <div className="flex items-center">
                <AlertCircle className="h-5 w-5 text-red-500 mr-2" />
                <p className="text-red-800">{error}</p>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
