import React, { useState, useEffect } from 'react';
import { spotsApi } from '../lib/api';
import { SpotResponse, SearchFilters, PaginationInfo } from '../lib/types';
import { SpotCard } from '../components/SpotCard';
import { Filters } from '../components/Filters';
import { Loader2, MapPin, AlertCircle } from 'lucide-react';

export function Search() {
  const [spots, setSpots] = useState<SpotResponse[]>([]);
  const [pagination, setPagination] = useState<PaginationInfo>({
    page: 0,
    size: 20,
    totalElements: 0,
    totalPages: 0,
  });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  
  const [filters, setFilters] = useState<SearchFilters>({
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

  const searchSpots = async (page = 0) => {
    setIsLoading(true);
    setError(null);
    
    try {
      const searchRequest = {
        query: filters.query || undefined,
        region: filters.region || undefined,
        petAllowed: filters.petAllowed,
        bookable: filters.bookable,
        hasBbq: filters.amenities.hasBbq || undefined,
        hasToilet: filters.amenities.hasToilet || undefined,
        hasWater: filters.amenities.hasWater || undefined,
        hasShelter: filters.amenities.hasShelter || undefined,
        hasPower: filters.amenities.hasPower || undefined,
        page,
        size: pagination.size,
      };

      const result = await spotsApi.search(searchRequest);
      
      setSpots(result.content);
      setPagination({
        page: result.page,
        size: result.size,
        totalElements: result.totalElements,
        totalPages: result.totalPages,
      });
    } catch (err) {
      console.error('Error searching spots:', err);
      setError('Failed to search campsites. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleSearch = () => {
    searchSpots(0);
  };

  const handlePageChange = (newPage: number) => {
    searchSpots(newPage);
  };

  // Load initial data
  useEffect(() => {
    searchSpots();
  }, []);

  return (
    <div>
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Find Your Perfect Campsite</h1>
        <p className="text-gray-600">
          Discover amazing camping spots across Australia with real-time weather insights.
        </p>
      </div>

      <Filters
        filters={filters}
        onFiltersChange={setFilters}
        onSearch={handleSearch}
        isLoading={isLoading}
      />

      {/* Results */}
      <div className="mb-6">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold text-gray-900">
            {isLoading ? 'Searching...' : `${pagination.totalElements} campsites found`}
          </h2>
          
          {pagination.totalPages > 1 && (
            <div className="flex items-center space-x-2">
              <span className="text-sm text-gray-600">
                Page {pagination.page + 1} of {pagination.totalPages}
              </span>
            </div>
          )}
        </div>
      </div>

      {/* Error State */}
      {error && (
        <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-6">
          <div className="flex items-center">
            <AlertCircle className="h-5 w-5 text-red-400 mr-2" />
            <p className="text-red-800">{error}</p>
          </div>
        </div>
      )}

      {/* Loading State */}
      {isLoading && spots.length === 0 && (
        <div className="flex items-center justify-center py-12">
          <Loader2 className="h-8 w-8 animate-spin text-primary-600" />
          <span className="ml-2 text-gray-600">Searching campsites...</span>
        </div>
      )}

      {/* Results Grid */}
      {!isLoading && spots.length > 0 && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
          {spots.map((spot) => (
            <SpotCard key={spot.id} spot={spot} />
          ))}
        </div>
      )}

      {/* Empty State */}
      {!isLoading && spots.length === 0 && !error && (
        <div className="text-center py-12">
          <MapPin className="h-12 w-12 text-gray-400 mx-auto mb-4" />
          <h3 className="text-lg font-medium text-gray-900 mb-2">No campsites found</h3>
          <p className="text-gray-600 mb-4">
            Try adjusting your search criteria or browse all available campsites.
          </p>
          <button
            onClick={() => {
              setFilters({
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
              searchSpots();
            }}
            className="btn btn-primary"
          >
            Show All Campsites
          </button>
        </div>
      )}

      {/* Pagination */}
      {pagination.totalPages > 1 && (
        <div className="flex items-center justify-center space-x-2">
          <button
            onClick={() => handlePageChange(pagination.page - 1)}
            disabled={pagination.page === 0 || isLoading}
            className="btn btn-outline disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Previous
          </button>
          
          <span className="px-4 py-2 text-sm text-gray-600">
            Page {pagination.page + 1} of {pagination.totalPages}
          </span>
          
          <button
            onClick={() => handlePageChange(pagination.page + 1)}
            disabled={pagination.page >= pagination.totalPages - 1 || isLoading}
            className="btn btn-outline disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
}
