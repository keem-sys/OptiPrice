import React, { useState, useEffect } from "react";
import { Search, Sparkles } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";

interface HeroProps {
    onSearch: (query: string) => void;
    loading: boolean;
    initialValue?: string;
    compact?: boolean;
}

export function Hero({ onSearch, loading, initialValue = "", compact = false }: HeroProps) {
    const [query, setQuery] = useState(initialValue);

    useEffect(() => {
        setQuery(initialValue);
    }, [initialValue]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const newVal = e.target.value;
        setQuery(newVal);
        onSearch(newVal);
    };

    const handleQuickSearch = (term: string) => {
        setQuery(term);
        onSearch(term);
    };

    const handleSubmit = (e: React.SubmitEvent) => {
        e.preventDefault();
    };

    return (
        <section className={cn(
            "relative overflow-hidden transition-all duration-700 ease-in-out px-4",
            compact ? "pt-4 pb-6" : "pt-16 pb-12 md:pt-24 md:pb-20"
        )}>
            <div className="container mx-auto max-w-4xl text-center">

                <div className={cn(
                    "transition-all duration-500 ease-in-out overflow-hidden",
                    compact ? "max-h-0 opacity-0 mb-0" : "max-h-125 opacity-100 mb-8"
                )}>
                    {/* 1. FEATURE BADGE */}
                    <div className="inline-flex items-center rounded-full border border-indigo-100 bg-indigo-50 px-3 py-1 text-sm font-medium text-indigo-600 mb-8">
                        <Sparkles className="mr-2 h-3.5 w-3.5" />
                        <span className="mr-2">New: AI-Powered Matching</span>
                    </div>

                    {/* MAIN HEADLINE */}
                    <h1 className="text-4xl font-extrabold tracking-tight text-slate-900 sm:text-5xl md:text-6xl mb-6">
                        Compare Groceries with <br className="hidden sm:block" />
                        <span className="bg-linear-to-r from-indigo-600 to-violet-600 bg-clip-text text-transparent">
                Precision & Speed
              </span>
                    </h1>

                    <p className="mx-auto max-w-2xl text-lg text-slate-600 mb-10">
                        Stop overpaying. Instantly compare prices across Shoprite, Checkers, and Pick n Pay.
                    </p>
                </div>

                {/* SEARCH BAR */}
                <div className={cn(
                    "mx-auto transition-all duration-700",
                    compact ? "max-w-4xl" : "max-w-2xl"
                )}>
                    <form onSubmit={handleSubmit} className="relative flex items-center">
                        <Search className="absolute left-4 h-5 w-5 text-slate-400" />
                        <Input
                            type="text"
                            placeholder="Search for products (e.g. Milk, Bread, Clover)..."
                            className="h-14 w-full rounded-full border-slate-200 bg-white pl-12 pr-32 text-lg shadow-xl shadow-slate-200/40 focus-visible:ring-indigo-500 transition-all"
                            value={query}
                            onChange={handleChange}
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

                    {/* QUICK TAGS*/}
                    <div className={cn(
                        "mt-6 flex flex-wrap justify-center gap-2 text-sm text-slate-500 transition-opacity duration-300",
                        compact ? "hidden opacity-0" : "opacity-100"
                    )}>
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
        </section>
    );
}