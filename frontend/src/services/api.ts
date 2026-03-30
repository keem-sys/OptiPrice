import axios from "axios";
import type {MasterProduct, PagedResponse} from "@/types";

const BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

const api = axios.create({
    baseURL: BASE_URL,
    headers: {
        "Content-Type": "application/json",
    },
});

api.interceptors.response.use(
    (response) => response,
    (error) => {
        const backendMessage = error.response?.data?.message || error.message;
        
        console.error("Global API Error:", {
            url: error.config?.url,
            status: error.response?.status,
            message: backendMessage,
        });
        return Promise.reject(error);
    }
);

export const searchProducts =
    async (query: string, page = 0): Promise<PagedResponse<MasterProduct>> => {
        const response =
            await api.get<PagedResponse<MasterProduct>>(`/compare`, {
            params: {
                item: query,
                page: page,
                size: 12
            }
        });
        return response.data;
}

export const triggerScrape = async (query: string): Promise<string> => {
    const response = await api.post(`/scrape`, null, {
        params: { item: query }
    });
    return response.data;
}

export const getProductById = async (id: string): Promise<MasterProduct> => {
        const response = await api.get<MasterProduct>(`/product/${id}`);
        return response.data;
};


export const getDeals = async (page: number, size: number): Promise<PagedResponse<MasterProduct>> => {
    const response = await api.get<PagedResponse<MasterProduct>>('/products/deals', {
        params: { page, size }
    });
    return response.data;
};

export const getPriceDrops = async (page: number, size: number): Promise<PagedResponse<MasterProduct>> => {
    const response = await api.get<PagedResponse<MasterProduct>>('/products/deals/drops', {
        params: { page, size }
    });
    return response.data;
};

export interface PriceHistoryPoint {
    date: string;
    storeName: string;
    price: number;
}

export const getPriceHistory = async (id: string): Promise<PriceHistoryPoint[]> => {
    const response = await api.get<PriceHistoryPoint[]>(`/product/${id}/history`);
    return response.data;
};

export default api;