import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { spotsApi, forecastApi } from '../lib/api';
import { SpotResponse, Forecast } from '../lib/types';
import { ForecastChart } from '../components/ForecastChart';
import { MapPin, DollarSign, PawPrint, Calendar, ExternalLink, Loader2, AlertCircle } from 'lucide-react';

export function Spot() {
  const { id } = useParams<{ id: string }>();
  const [spot, setSpot] = useState<SpotResponse | null>(null);
  const [forecasts, setForecasts] = useState<Forecast[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isLoadingForecast, setIsLoadingForecast] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (id) {
      loadSpot(parseInt(id));
    }
  }, [id]);

  const loadSpot = async (spotId: number) => {
    setIsLoading(true);
    setError(null);
    
    try {
      const spotData = await spotsApi.getById(spotId);
      setSpot(spotData);
      
      // Load forecast if we have coordinates
      if (spotData.lat && spotData.lon) {
        loadForecast(spotData.lat, spotData.lon);
      }
    } catch (err) {
      console.error('Error loading spot:', err);
      setError('Failed to load campsite details.');
    } finally {
      setIsLoading(false);
    }
  };

  const loadForecast = async (lat: number, lon: number) => {
    setIsLoadingForecast(true);
    
    try {
      const now = new Date();
      const tomorrow = new Date(now.getTime() + 24 * 60 * 60 * 1000);
      
      const forecastData = await forecastApi.get(
        lat,
        lon,
        now.toISOString(),
        tomorrow.toISOString()
      );
      
      setForecasts(forecastData);
    } catch (err) {
      console.error('Error loading forecast:', err);
    } finally {
      setIsLoadingForecast(false);
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-12">
        <Loader2 className="h-8 w-8 animate-spin text-primary-600" />
        <span className="ml-2 text-gray-600">Loading campsite details...</span>
      </div>
    );
  }

  if (error || !spot) {
    return (
      <div className="text-center py-12">
        <AlertCircle className="h-12 w-12 text-red-400 mx-auto mb-4" />
        <h3 className="text-lg font-medium text-gray-900 mb-2">Campsite not found</h3>
        <p className="text-gray-600">{error || 'The requested campsite could not be found.'}</p>
      </div>
    );
  }

  const amenities = [
    { key: 'hasBbq', label: 'BBQ Facilities', value: spot.amenities.hasBbq },
    { key: 'hasToilet', label: 'Toilets', value: spot.amenities.hasToilet },
    { key: 'hasWater', label: 'Water Access', value: spot.amenities.hasWater },
    { key: 'hasShelter', label: 'Shelter', value: spot.amenities.hasShelter },
    { key: 'hasPower', label: 'Power Outlets', value: spot.amenities.hasPower },
  ];

  const availableAmenities = amenities.filter(amenity => amenity.value);

  return (
    <div className="max-w-4xl mx-auto">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-2">{spot.name}</h1>
        <div className="flex items-center text-gray-600 mb-4">
          <MapPin className="h-4 w-4 mr-1" />
          <span>{spot.park.name}</span>
          {spot.park.region && (
            <>
              <span className="mx-2">•</span>
              <span>{spot.park.region}</span>
            </>
          )}
        </div>
        
        {spot.description && (
          <p className="text-gray-700 text-lg leading-relaxed">{spot.description}</p>
        )}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Main Content */}
        <div className="lg:col-span-2 space-y-8">
          {/* Park Information */}
          <div className="card">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">Park Information</h2>
            <div className="space-y-3">
              <div>
                <span className="font-medium text-gray-700">Park:</span>
                <span className="ml-2 text-gray-600">{spot.park.name}</span>
              </div>
              {spot.park.authority && (
                <div>
                  <span className="font-medium text-gray-700">Authority:</span>
                  <span className="ml-2 text-gray-600">{spot.park.authority}</span>
                </div>
              )}
              {spot.park.region && (
                <div>
                  <span className="font-medium text-gray-700">Region:</span>
                  <span className="ml-2 text-gray-600">{spot.park.region}</span>
                </div>
              )}
              {spot.park.websiteUrl && (
                <div>
                  <span className="font-medium text-gray-700">Website:</span>
                  <a
                    href={spot.park.websiteUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="ml-2 text-primary-600 hover:text-primary-700 inline-flex items-center"
                  >
                    Visit Website
                    <ExternalLink className="h-3 w-3 ml-1" />
                  </a>
                </div>
              )}
            </div>
          </div>

          {/* Weather Forecast */}
          {spot.lat && spot.lon && (
            <div>
              {isLoadingForecast ? (
                <div className="card">
                  <div className="flex items-center justify-center py-8">
                    <Loader2 className="h-6 w-6 animate-spin text-primary-600" />
                    <span className="ml-2 text-gray-600">Loading weather forecast...</span>
                  </div>
                </div>
              ) : (
                <ForecastChart forecasts={forecasts} />
              )}
            </div>
          )}
        </div>

        {/* Sidebar */}
        <div className="space-y-6">
          {/* Pricing & Booking */}
          <div className="card">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">Pricing & Booking</h3>
            <div className="space-y-3">
              {spot.feeAud && (
                <div className="flex items-center">
                  <DollarSign className="h-5 w-5 text-gray-400 mr-2" />
                  <span className="text-gray-700">${spot.feeAud} AUD per night</span>
                </div>
              )}
              
              <div className="flex items-center">
                <Calendar className="h-5 w-5 text-gray-400 mr-2" />
                <span className="text-gray-700">
                  {spot.bookable ? 'Bookable online' : 'First come, first served'}
                </span>
              </div>
              
              <div className="flex items-center">
                <PawPrint className="h-5 w-5 text-gray-400 mr-2" />
                <span className="text-gray-700">
                  {spot.petAllowed ? 'Pet friendly' : 'No pets allowed'}
                </span>
              </div>
            </div>
          </div>

          {/* Amenities */}
          <div className="card">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">Amenities</h3>
            {availableAmenities.length > 0 ? (
              <div className="space-y-2">
                {availableAmenities.map((amenity) => (
                  <div key={amenity.key} className="flex items-center">
                    <div className="w-2 h-2 bg-green-500 rounded-full mr-3"></div>
                    <span className="text-gray-700">{amenity.label}</span>
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-gray-500 text-sm">No amenities listed</p>
            )}
          </div>

          {/* Location */}
          {spot.lat && spot.lon && (
            <div className="card">
              <h3 className="text-lg font-semibold text-gray-900 mb-4">Location</h3>
              <div className="space-y-2 text-sm text-gray-600">
                <div>Latitude: {spot.lat.toFixed(6)}</div>
                <div>Longitude: {spot.lon.toFixed(6)}</div>
              </div>
              <a
                href={`https://www.google.com/maps?q=${spot.lat},${spot.lon}`}
                target="_blank"
                rel="noopener noreferrer"
                className="btn btn-primary w-full mt-4 inline-flex items-center justify-center"
              >
                <MapPin className="h-4 w-4 mr-2" />
                View on Google Maps
              </a>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
