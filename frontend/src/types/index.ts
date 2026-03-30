export interface PagedResponse<T> {
    content: T[];
    currentPage: number;
    totalItems: number;
    totalPages: number;
}

export interface Store {
    id: number;
    name: string;
    logoUrl: string;
    websiteUrl: string;
}

export interface StoreItem {
    id: number;
    store: Store;
    brand: string;
    storeSpecificName: string;
    price: number;
    oldPrice: number;
    isOnPromotion: boolean;
    promotionText: string;
    barcode?: string;
    productUrl: string;
    imageUrl: string;
    lastUpdated: string;
}

export interface MasterProduct {
    id: number;
    genericName: string;
    category: string;
    storeItems: StoreItem[];
}