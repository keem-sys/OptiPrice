export interface Store {
    id: number;
    name: string;
    logoUrl: string;
    websiteUrl: string;
}

export interface StoreItem {
    id: number;
    storeSpecificName: string;
    price: number;
    brand: string;
    imageUrl: string;
    productUrl: string;
    lastUpdated: string;
    store: Store;
}

export interface MasterProduct {
    id: number;
    genericName: string;
    category: string;
    storeItems: StoreItem[];
}