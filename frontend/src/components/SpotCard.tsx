import React from 'react';
import { Link } from 'react-router-dom';
import { MapPin, DollarSign, PawPrint, Calendar, Wifi, Droplets, Shield, Zap } from 'lucide-react';
import { SpotResponse } from '../lib/types';
import clsx from 'clsx';

interface SpotCardProps {
  spot: SpotResponse;
}

export function SpotCard({ spot }: SpotCardProps) {
  const amenities = [
    { key: 'hasBbq', label: 'BBQ', icon: Shield },
    { key: 'hasToilet', label: 'Toilets', icon: Shield },
    { key: 'hasWater', label: 'Water', icon: Droplets },
    { key: 'hasShelter', label: 'Shelter', icon: Shield },
    { key: 'hasPower', label: 'Power', icon: Zap },
  ];

  const availableAmenities = amenities.filter(amenity => 
    spot.amenities[amenity.key as keyof typeof spot.amenities]
  );

  return (
    <div className="card hover:shadow-md transition-shadow duration-200">
      <div className="flex justify-between items-start mb-4">
        <div>
          <h3 className="text-lg font-semibold text-gray-900 mb-1">
            <Link 
              to={`/spot/${spot.id}`}
              className="hover:text-primary-600 transition-colors"
            >
              {spot.name}
            </Link>
          </h3>
          <p className="text-sm text-gray-600 mb-2">{spot.park.name}</p>
          {spot.park.region && (
            <div className="flex items-center text-sm text-gray-500">
              <MapPin className="h-4 w-4 mr-1" />
              {spot.park.region}
            </div>
          )}
        </div>
        
        <div className="flex flex-col items-end space-y-1">
          {spot.feeAud && (
            <div className="flex items-center text-sm text-gray-600">
              <DollarSign className="h-4 w-4 mr-1" />
              ${spot.feeAud}
            </div>
          )}
          <div className="flex space-x-2">
            {spot.petAllowed && (
              <div className="flex items-center text-sm text-green-600">
                <PawPrint className="h-4 w-4" />
              </div>
            )}
            {spot.bookable && (
              <div className="flex items-center text-sm text-blue-600">
                <Calendar className="h-4 w-4" />
              </div>
            )}
          </div>
        </div>
      </div>

      {spot.description && (
        <p className="text-gray-700 text-sm mb-4 line-clamp-2">
          {spot.description}
        </p>
      )}

      {availableAmenities.length > 0 && (
        <div className="flex flex-wrap gap-2">
          {availableAmenities.map((amenity) => (
            <span
              key={amenity.key}
              className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-primary-100 text-primary-800"
            >
              <amenity.icon className="h-3 w-3 mr-1" />
              {amenity.label}
            </span>
          ))}
        </div>
      )}

      <div className="mt-4 pt-4 border-t border-gray-200">
        <Link
          to={`/spot/${spot.id}`}
          className="btn btn-outline w-full text-center"
        >
          View Details
        </Link>
      </div>
    </div>
  );
}
