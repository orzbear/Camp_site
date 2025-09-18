import React from 'react';
import { Search, Filter, X } from 'lucide-react';
import { SearchFilters } from '../lib/types';
import clsx from 'clsx';

interface FiltersProps {
  filters: SearchFilters;
  onFiltersChange: (filters: SearchFilters) => void;
  onSearch: () => void;
  isLoading?: boolean;
}

export function Filters({ filters, onFiltersChange, onSearch, isLoading }: FiltersProps) {
  const [isExpanded, setIsExpanded] = React.useState(false);

  const handleFilterChange = (key: keyof SearchFilters, value: any) => {
    onFiltersChange({
      ...filters,
      [key]: value,
    });
  };

  const handleAmenityChange = (key: keyof SearchFilters['amenities'], value: boolean) => {
    onFiltersChange({
      ...filters,
      amenities: {
        ...filters.amenities,
        [key]: value,
      },
    });
  };

  const clearFilters = () => {
    onFiltersChange({
      query: '',
      region: '',
      petAllowed: null,
      bookable: null,
      amenities: {
        hasBbq: false,
        hasToilet: false,
        hasWater: false,
        hasShelter: false,
        hasPower: false,
      },
    });
  };

  const hasActiveFilters = filters.query || 
    filters.region || 
    filters.petAllowed !== null || 
    filters.bookable !== null ||
    Object.values(filters.amenities).some(Boolean);

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 mb-6">
      {/* Search Bar */}
      <div className="flex gap-4 mb-4">
        <div className="flex-1">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
            <input
              type="text"
              placeholder="Search campsites or parks..."
              value={filters.query}
              onChange={(e) => handleFilterChange('query', e.target.value)}
              className="input pl-10"
              onKeyPress={(e) => e.key === 'Enter' && onSearch()}
            />
          </div>
        </div>
        <button
          onClick={onSearch}
          disabled={isLoading}
          className="btn btn-primary px-6"
        >
          {isLoading ? 'Searching...' : 'Search'}
        </button>
      </div>

      {/* Filter Toggle */}
      <div className="flex items-center justify-between">
        <button
          onClick={() => setIsExpanded(!isExpanded)}
          className="flex items-center space-x-2 text-sm font-medium text-gray-700 hover:text-gray-900"
        >
          <Filter className="h-4 w-4" />
          <span>Filters</span>
          {hasActiveFilters && (
            <span className="bg-primary-100 text-primary-800 text-xs px-2 py-1 rounded-full">
              Active
            </span>
          )}
        </button>
        
        {hasActiveFilters && (
          <button
            onClick={clearFilters}
            className="flex items-center space-x-1 text-sm text-gray-500 hover:text-gray-700"
          >
            <X className="h-4 w-4" />
            <span>Clear</span>
          </button>
        )}
      </div>

      {/* Expanded Filters */}
      {isExpanded && (
        <div className="mt-4 pt-4 border-t border-gray-200">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {/* Region */}
            <div>
              <label className="label">Region</label>
              <select
                value={filters.region}
                onChange={(e) => handleFilterChange('region', e.target.value)}
                className="input"
              >
                <option value="">All Regions</option>
                <option value="NSW">New South Wales</option>
                <option value="VIC">Victoria</option>
                <option value="QLD">Queensland</option>
                <option value="WA">Western Australia</option>
                <option value="SA">South Australia</option>
                <option value="TAS">Tasmania</option>
                <option value="NT">Northern Territory</option>
                <option value="ACT">Australian Capital Territory</option>
              </select>
            </div>

            {/* Pet Allowed */}
            <div>
              <label className="label">Pet Policy</label>
              <select
                value={filters.petAllowed === null ? '' : filters.petAllowed.toString()}
                onChange={(e) => handleFilterChange('petAllowed', e.target.value === '' ? null : e.target.value === 'true')}
                className="input"
              >
                <option value="">Any</option>
                <option value="true">Pet Friendly</option>
                <option value="false">No Pets</option>
              </select>
            </div>

            {/* Bookable */}
            <div>
              <label className="label">Booking</label>
              <select
                value={filters.bookable === null ? '' : filters.bookable.toString()}
                onChange={(e) => handleFilterChange('bookable', e.target.value === '' ? null : e.target.value === 'true')}
                className="input"
              >
                <option value="">Any</option>
                <option value="true">Bookable</option>
                <option value="false">First Come</option>
              </select>
            </div>
          </div>

          {/* Amenities */}
          <div className="mt-4">
            <label className="label">Amenities</label>
            <div className="grid grid-cols-2 md:grid-cols-5 gap-3">
              {Object.entries(filters.amenities).map(([key, value]) => (
                <label key={key} className="flex items-center space-x-2 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={value}
                    onChange={(e) => handleAmenityChange(key as keyof SearchFilters['amenities'], e.target.checked)}
                    className="rounded border-gray-300 text-primary-600 focus:ring-primary-500"
                  />
                  <span className="text-sm text-gray-700 capitalize">
                    {key.replace('has', '').toLowerCase()}
                  </span>
                </label>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
