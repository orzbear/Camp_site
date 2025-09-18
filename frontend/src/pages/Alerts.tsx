import React, { useState, useEffect } from 'react';
import { alertsApi } from '../lib/api';
import { Alert } from '../lib/types';
import { AlertTriangle, MapPin, ExternalLink, Loader2, AlertCircle } from 'lucide-react';

export function Alerts() {
  const [alerts, setAlerts] = useState<Alert[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedSeverity, setSelectedSeverity] = useState<string>('all');

  useEffect(() => {
    loadAlerts();
  }, []);

  const loadAlerts = async () => {
    setIsLoading(true);
    setError(null);
    
    try {
      const alertsData = await alertsApi.getActive();
      setAlerts(alertsData);
    } catch (err) {
      console.error('Error loading alerts:', err);
      setError('Failed to load park alerts.');
    } finally {
      setIsLoading(false);
    }
  };

  const getSeverityColor = (severity: string) => {
    switch (severity.toLowerCase()) {
      case 'high':
      case 'severe':
        return 'text-red-600 bg-red-100 border-red-200';
      case 'medium':
      case 'moderate':
        return 'text-yellow-600 bg-yellow-100 border-yellow-200';
      case 'low':
      case 'minor':
        return 'text-blue-600 bg-blue-100 border-blue-200';
      default:
        return 'text-gray-600 bg-gray-100 border-gray-200';
    }
  };

  const getSeverityIcon = (severity: string) => {
    switch (severity.toLowerCase()) {
      case 'high':
      case 'severe':
        return <AlertTriangle className="h-5 w-5" />;
      case 'medium':
      case 'moderate':
        return <AlertTriangle className="h-5 w-5" />;
      case 'low':
      case 'minor':
        return <AlertTriangle className="h-5 w-5" />;
      default:
        return <AlertTriangle className="h-5 w-5" />;
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString('en-AU', {
      weekday: 'short',
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const filteredAlerts = selectedSeverity === 'all' 
    ? alerts 
    : alerts.filter(alert => alert.severity.toLowerCase() === selectedSeverity.toLowerCase());

  const severityOptions = [
    { value: 'all', label: 'All Alerts' },
    { value: 'high', label: 'High Priority' },
    { value: 'medium', label: 'Medium Priority' },
    { value: 'low', label: 'Low Priority' },
  ];

  return (
    <div className="max-w-4xl mx-auto">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Park Alerts</h1>
        <p className="text-gray-600">
          Stay informed about important updates, closures, and safety information for parks and campsites.
        </p>
      </div>

      {/* Filter */}
      <div className="card mb-6">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold text-gray-900">Filter Alerts</h2>
          <select
            value={selectedSeverity}
            onChange={(e) => setSelectedSeverity(e.target.value)}
            className="input w-auto"
          >
            {severityOptions.map(option => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* Loading State */}
      {isLoading && (
        <div className="flex items-center justify-center py-12">
          <Loader2 className="h-8 w-8 animate-spin text-primary-600" />
          <span className="ml-2 text-gray-600">Loading alerts...</span>
        </div>
      )}

      {/* Error State */}
      {error && (
        <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-6">
          <div className="flex items-center">
            <AlertCircle className="h-5 w-5 text-red-400 mr-2" />
            <p className="text-red-800">{error}</p>
          </div>
        </div>
      )}

      {/* Alerts List */}
      {!isLoading && filteredAlerts.length > 0 && (
        <div className="space-y-4">
          {filteredAlerts.map((alert) => (
            <div key={alert.id} className="card">
              <div className="flex items-start justify-between mb-4">
                <div className="flex items-start space-x-3">
                  <div className={`p-2 rounded-lg ${getSeverityColor(alert.severity)}`}>
                    {getSeverityIcon(alert.severity)}
                  </div>
                  <div className="flex-1">
                    <h3 className="text-lg font-semibold text-gray-900 mb-1">
                      {alert.title}
                    </h3>
                    {alert.park && (
                      <div className="flex items-center text-sm text-gray-600 mb-2">
                        <MapPin className="h-4 w-4 mr-1" />
                        {alert.park.name}
                        {alert.park.region && (
                          <>
                            <span className="mx-1">•</span>
                            {alert.park.region}
                          </>
                        )}
                      </div>
                    )}
                  </div>
                </div>
                
                <div className="text-right">
                  <span className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium ${getSeverityColor(alert.severity)}`}>
                    {alert.severity}
                  </span>
                </div>
              </div>

              {alert.summary && (
                <p className="text-gray-700 mb-4 leading-relaxed">
                  {alert.summary}
                </p>
              )}

              <div className="flex items-center justify-between text-sm text-gray-500">
                <div className="flex items-center space-x-4">
                  {alert.startsAt && (
                    <span>
                      Starts: {formatDate(alert.startsAt)}
                    </span>
                  )}
                  {alert.endsAt && (
                    <span>
                      Ends: {formatDate(alert.endsAt)}
                    </span>
                  )}
                  {alert.source && (
                    <span>
                      Source: {alert.source}
                    </span>
                  )}
                </div>
                
                {alert.url && (
                  <a
                    href={alert.url}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="inline-flex items-center text-primary-600 hover:text-primary-700"
                  >
                    More Info
                    <ExternalLink className="h-3 w-3 ml-1" />
                  </a>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Empty State */}
      {!isLoading && filteredAlerts.length === 0 && !error && (
        <div className="text-center py-12">
          <AlertTriangle className="h-12 w-12 text-gray-400 mx-auto mb-4" />
          <h3 className="text-lg font-medium text-gray-900 mb-2">No alerts found</h3>
          <p className="text-gray-600">
            {selectedSeverity === 'all' 
              ? 'There are currently no active park alerts.'
              : `No ${selectedSeverity} priority alerts found.`
            }
          </p>
        </div>
      )}
    </div>
  );
}
