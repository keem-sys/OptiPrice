import { useState } from "react";
import { Hero } from "@/components/layout/Hero";
import { searchProducts } from "@/services/api";
import type {MasterProduct} from "@/types";
import {ProductGrid} from "@/components/products/ProductGrid.tsx";
import {Search} from "lucide-react";
// import { ResultsGrid } from "@/components/products/ResultsGrid";

export default function Home() {
    const [results, setResults] = useState<MasterProduct[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [hasSearched, setHasSearched] = useState(false);

    const handleSearch = async (query: string) => {
        setLoading(true);
        setError("");
        setHasSearched(true);
        setResults([]);

        try {
            const data = await searchProducts(query);
            setResults(data);
        } catch (err) {
            setError("Connection failed. Ensure the backend is running.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-slate-50 pb-20">
            <Hero onSearch={handleSearch} loading={loading} />

            <div className="container mx-auto px-4 pb-20">
                {error && (
                    <div className="p-4 mb-8 text-sm text-red-700 bg-red-100 rounded-lg max-w-2xl mx-auto text-center">
                        <span className="font-medium">Error:</span> {error}
                    </div>
                )}

                <ProductGrid products={results} loading={loading} />

                {hasSearched && !loading && results.length === 0 && !error && (
                    <div className="flex flex-col items-center justify-center py-20 text-slate-400">
                        <div className="bg-slate-100 p-6 rounded-full mb-4">
                            <Search size={48} className="text-slate-300" />
                        </div>
                        <h3 className="text-xl font-semibold text-slate-700">No products found</h3>
                        <p>Try searching for "Milk" or "Bread"</p>
                    </div>
                )}
            </div>
        </div>
    );
}