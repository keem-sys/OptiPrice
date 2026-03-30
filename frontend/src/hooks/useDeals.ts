import { useQuery, keepPreviousData } from "@tanstack/react-query";
import { getDeals, getPriceDrops } from "@/services/api";

export const useDeals = (dealType: "arbitrage" | "drops",
                         page: number = 0, size: number = 12) => {
    return useQuery({
        queryKey: ['deals', dealType, page, size],
        queryFn: () => dealType === "arbitrage" ? getDeals(page, size) : getPriceDrops(page, size),
        placeholderData: keepPreviousData,
        staleTime: 5 * 60 * 1000,
    });
};