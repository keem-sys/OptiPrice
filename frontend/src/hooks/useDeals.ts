import { useQuery, keepPreviousData } from "@tanstack/react-query";
import { getDeals } from "@/services/api";

export const useDeals = (page: number = 0, size: number = 12) => {
    return useQuery({
        queryKey: ['deals', page, size],
        queryFn: () => getDeals(page, size),
        placeholderData: keepPreviousData,
        staleTime: 5 * 60 * 1000,
    });
};