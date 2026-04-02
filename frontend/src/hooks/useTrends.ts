import { useQuery } from "@tanstack/react-query";
import {getBasketItems, getBasketTrends} from "@/services/api";

export const useBasketTrends = () => {
    return useQuery({
        queryKey: ['trends', 'basket'],
        queryFn: getBasketTrends,
        staleTime: 10 * 60 * 1000,
    });
};

export const useBasketItems = () => {
    return useQuery({
        queryKey: ['trends', 'basket-items'],
        queryFn: getBasketItems,
        staleTime: 60 * 60 * 1000,
    });
};