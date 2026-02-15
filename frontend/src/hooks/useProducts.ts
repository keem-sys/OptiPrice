import { useQuery } from '@tanstack/react-query';
import {searchProducts, getProductById, getPriceHistory} from '@/services/api';
import  type { MasterProduct } from '@/types';

export const useSearchProducts = (query: string, page: number) => {
    return useQuery({
        queryKey: ['products', 'search', query, page],
        queryFn: () => searchProducts(query, page),
        enabled: !!query,
        staleTime: 1000 * 60 * 10,
        placeholderData:
            (previousData) =>
                previousData,
    });
};

export const useProductDetails = (id: string) => {
    return useQuery<MasterProduct>({
        queryKey: ['product', id],
        queryFn: () => getProductById(id),
        enabled: !!id,
        retry: false,
    });
};

export const usePriceHistory = (id: string) => {
    return useQuery({
        queryKey: ['product', id, 'history'],
        queryFn: () => getPriceHistory(id),
        enabled: !!id,
    });
};