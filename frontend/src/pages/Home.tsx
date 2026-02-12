import { useState } from "react";
import { Hero } from "@/components/layout/Hero";
import { searchProducts } from "@/services/api";
import type {MasterProduct} from "@/types";
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

            <div className="container mx-auto px-4">
                {error && (
                    <div className="p-4 mb-8 text-sm text-red-700 bg-red-100 rounded-lg max-w-2xl mx-auto text-center" role="alert">
                        <span className="font-medium">Error:</span> {error}
                    </div>
                )}

                {hasSearched && results.length === 0 && !loading && !error && (
                    <div className="text-center py-10 text-slate-500">
                        <p className="text-lg">No products found. Try a broader search term.</p>
                    </div>
                )}

                {results.length > 0 && (
                    <div className="max-w-4xl mx-auto">
                        <h2 className="text-2xl font-bold mb-4">Found {results.length} Results</h2>
                        <pre className="bg-white p-4 rounded-xl shadow-sm overflow-auto text-xs">
                     {JSON.stringify(results, null, 2)}
                 </pre>
                    </div>
                )}
            </div>
        </div>
    );
}