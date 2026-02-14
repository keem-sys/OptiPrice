import React, { useState } from "react";
import { Search, Sparkles, ArrowRight } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";


interface HeroProps {
    onSearch: (query: string) => void;
    loading: boolean;
    initialValue?: string;
}

export function Hero({ onSearch, loading, initialValue = "" }: HeroProps) {
    const [query, setQuery] = useState(initialValue);

    const handleSubmit = (e: React.SubmitEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (query.trim()) onSearch(query);
    };

    const handleQuickSearch = (term: string) => {
        setQuery(term);
        onSearch(term);
    };

    return (
        <section className="relative overflow-hidden pt-16 pb-12 md:pt-24 md:pb-20 px-4">
            <div className="container mx-auto max-w-4xl text-center">

                <div className="inline-flex items-center rounded-full border border-indigo-100 bg-indigo-50 px-3 py-1 text-sm font-medium text-indigo-600 mb-8 animate-in fade-in slide-in-from-bottom-4 duration-500">
                    <Sparkles className="mr-2 h-3.5 w-3.5" />
                    <span className="mr-2">New: AI-Powered Matching</span>
                    <span className="h-3.5 w-px bg-indigo-200 mx-2"></span>
                    <span className="flex items-center cursor-pointer hover:underline">
            Try it now <ArrowRight className="ml-1 h-3 w-3" />
          </span>
                </div>

                <h1 className="text-4xl font-extrabold tracking-tight text-slate-900 sm:text-5xl md:text-6xl mb-6 animate-in fade-in slide-in-from-bottom-5 duration-700">
                    Compare Groceries with <br className="hidden sm:block" />
                    <span className="bg-linear-to-r from-indigo-600 to-violet-600 bg-clip-text text-transparent">
            Precision & Speed
          </span>
                </h1>

                <p className="mx-auto max-w-2xl text-lg text-slate-600 mb-10 animate-in fade-in slide-in-from-bottom-6 duration-1000">
                    Stop overpaying. Instantly compare prices across Shoprite, Checkers, and Pick n Pay to find the best deals near you.
                </p>

                {/* SEARCH BAR */}
                <div className="mx-auto max-w-2xl animate-in fade-in slide-in-from-bottom-8 duration-1000">
                    <form onSubmit={handleSubmit} className="relative flex items-center">
                        <Search className="absolute left-4 h-5 w-5 text-slate-400" />
                        <Input
                            type="text"
                            placeholder="Search for products (e.g. Milk, Bread, Clover)..."
                            className="h-14 w-full rounded-full border-slate-200 bg-white pl-12 pr-32 text-lg shadow-xl shadow-slate-200/40 focus-visible:ring-indigo-500 transition-all"
                            value={query}
                            onChange={(e) => setQuery(e.target.value)}
                        />
                        <div className="absolute right-2">
                            <Button
                                size="lg"
                                type="submit"
                                disabled={loading}
                                className="rounded-full bg-indigo-600 hover:bg-indigo-700 font-semibold px-6 cursor-pointer"
                            >
                                {loading ? "Scanning..." : "Search"}
                            </Button>
                        </div>
                    </form>

                    {/* QUICK TAGS */}
                    <div className="mt-6 flex flex-wrap justify-center gap-2 text-sm text-slate-500">
                        <span>Trending:</span>
                        {["Full Cream Milk", "Brown Bread", "Coca Cola", "Eggs 18"].map((tag) => (
                            <Badge
                                key={tag}
                                variant="secondary"
                                className="cursor-pointer hover:bg-indigo-100 hover:text-indigo-700 transition-colors px-3 py-1"
                                onClick={() => handleQuickSearch(tag)}
                            >
                                {tag}
                            </Badge>
                        ))}
                    </div>
                </div>

            </div>

            <div className="absolute top-0 left-1/2 -z-10 h-125 w-200 -translate-x-1/2 opacity-20 bg-linear-to-tr from-indigo-400 to-violet-400 blur-[100px]" />
        </section>
    );
}