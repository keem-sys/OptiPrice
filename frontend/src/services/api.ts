import axios from "axios";
import type {MasterProduct, PagedResponse} from "@/types";

const BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

const api = axios.create({
    baseURL: BASE_URL,
    headers: {
        "Content-Type": "application/json",
    },
});

export const searchProducts =
    async (query: string, page = 0): Promise<PagedResponse<MasterProduct>> => {
        try {
            const response =
                await api.get<PagedResponse<MasterProduct>>(`/compare`, {
                params: {
                    item: query,
                    page: page,
                    size: 12
                }
            });
            return response.data;
    } catch (error) {
        console.error("API error during search: ", error);
        throw error;
    }
}

export const triggerScrape = async (query: string): Promise<string> => {
    try {
        const response = await api.post(`/scrape`, null, {
            params: { item: query }
        });
        return response.data;
    } catch (error) {
        console.error("API Error triggering scrape:", error);
        throw error;
    }
}

export const getProductById = async (id: string): Promise<MasterProduct> => {
    try {
        const response = await api.get<MasterProduct>(`/product/${id}`);
        return response.data;
    } catch (error) {
        console.error("API Error fetching details:", error);
        throw error;
    }
};

export default api;