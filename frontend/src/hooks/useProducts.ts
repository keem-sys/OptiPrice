import { useQuery } from '@tanstack/react-query';
import { searchProducts, getProductById } from '@/services/api';
import  type { MasterProduct } from '@/types';

export const useSearchProducts = (query: string) => {
    return useQuery<MasterProduct[]>({
        queryKey: ['products', 'search', query],
        queryFn: () => searchProducts(query),
        enabled: !!query,
        staleTime: 1000 * 60 * 10,
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