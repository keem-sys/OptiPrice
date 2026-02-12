import { useState } from 'react';
import { Search, Loader2 } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { searchProducts } from "@/services/api";
import type {MasterProduct} from "@/types";
import {Link} from "react-router-dom";

export default function Home() {
    const [query, setQuery] = useState("");
    const [results, setResults] = useState<MasterProduct[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const handleSearch = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!query.trim()) return;

        setLoading(true);
        setError("");
        setResults([]);

        try {
            const data = await searchProducts(query);
            setResults(data);
            if (data.length === 0) {
                setError("No products found. Try a different term.");
            }
        } catch (err) {
            setError("Failed to connect to the server. Is Spring Boot running?");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="flex flex-col items-center pt-24 pb-12 px-4 min-h-[80vh]">
            <div className="text-center max-w-3xl animate-in fade-in slide-in-from-bottom-4 duration-700">
                <h1 className="text-5xl md:text-6xl font-extrabold tracking-tight mb-6 text-slate-900">
                    Compare Prices with <span className="text-indigo-600">AI Precision</span>
                </h1>
                <p className="text-xl text-slate-500 mb-10">
                    Real-time price aggregation across Shoprite, Checkers, and Pick n Pay.
                </p>
            </div>

            {/* SEARCH FORM */}
            <form onSubmit={handleSearch} className="flex w-full max-w-2xl items-center space-x-2 mb-12">
                <div className="relative flex-grow">
                    <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" size={20} />
                    <Input
                        type="text"
                        placeholder="Search for groceries (e.g. Milk, Bread)..."
                        className="pl-12 py-7 text-lg rounded-full shadow-lg border-slate-200 focus-visible:ring-indigo-600"
                        value={query}
                        onChange={(e) => setQuery(e.target.value)}
                    />
                </div>
                <Button size="lg" className="rounded-full px-8 py-7 text-lg font-bold shadow-lg bg-indigo-600 hover:bg-indigo-700 transition-all" disabled={loading}>
                    {loading ? <Loader2 className="animate-spin" /> : "Search"}
                </Button>
            </form>

            {/* ERROR STATE */}
            {error && (
                <div className="bg-red-50 text-red-600 px-6 py-4 rounded-xl mb-8 border border-red-100">
                    {error}
                </div>
            )}

            {/* RESULTS GRID*/}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 w-full max-w-7xl">
                {results.map((product) => (
                    <div key={product.id} className="bg-white p-6 rounded-2xl shadow-sm border border-slate-100 hover:shadow-md transition-shadow">
                        <div className="flex justify-between items-start mb-2">
                 <span className="bg-slate-100 text-slate-600 text-xs font-bold px-2 py-1 rounded uppercase tracking-wider">
                    {product.category || "General"}
                 </span>
                        </div>
                        <h3 className="font-bold text-lg mb-2 line-clamp-2">{product.genericName}</h3>
                        <p className="text-sm text-slate-500 mb-4">
                            Found in <span className="font-bold text-indigo-600">{(product.storeItems?.length || 0)} stores</span>
                        </p>
                        <Button variant="outline" className="w-full" asChild>
                            <Link to={`/product/${product.id}`}>
                                Compare Prices
                            </Link>
                        </Button>
                    </div>
                ))}
            </div>
        </div>
    );
}