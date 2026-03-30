import { useQuery, keepPreviousData } from "@tanstack/react-query";
import { getDeals, getPriceDrops, getPromotions } from "@/services/api";

type DealType = "arbitrage" | "drops" | "promotions";

export const useDeals = (dealType: DealType, page: number = 0, size: number = 12) => {
    return useQuery({
        queryKey: ['deals', dealType, page, size],
        queryFn: () => {
            if (dealType === "arbitrage") return getDeals(page, size);
            if (dealType === "drops") return getPriceDrops(page, size);
            return getPromotions(page, size);
        },
        placeholderData: keepPreviousData,
        staleTime: 5 * 60 * 1000,
    });
};