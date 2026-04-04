import React, { useState, useEffect } from "react";
import { Search, X, Loader2 } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";

interface SearchBarProps {
    onSearch: (query: string) => void;
    loading: boolean;
    initialValue?: string;
    showTags?: boolean;
    className?: string;
}

export function SearchBar({
                              onSearch,
                              loading,
                              initialValue = "",
                              showTags = false,
                              className
                          }: SearchBarProps) {
    const [query, setQuery] = useState(initialValue);

    useEffect(() => {
        setQuery(initialValue);
    },[initialValue]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const newVal = e.target.value;
        setQuery(newVal);
        onSearch(newVal);
    };

    const handleClear = () => {
        setQuery("");
        onSearch("");
    };

    const handleQuickSearch = (term: string) => {
        setQuery(term);
        onSearch(term);
    };

    return (
        <div className={cn("w-full max-w-3xl mx-auto transition-all duration-700", className)}>
            <form onSubmit={(e) => e.preventDefault()} className="relative flex items-center group">

                {/* Left Status Icon: Search Glass*/}
                <div className="absolute left-5 z-10 pointer-events-none flex items-center justify-center text-slate-500 group-focus-within:text-indigo-600 transition-colors duration-300">
                    {loading ? (
                        <Loader2 className="h-5 w-5 animate-spin text-indigo-600" />
                    ) : (
                        <Search className="h-5 w-5" />
                    )}
                </div>

                {/* The Input */}
                <Input
                    type="search"
                    inputMode="search"
                    enterKeyHint="search"
                    placeholder="Search groceries (e.g. Milk, Bread, Clover)..."
                    className="h-14 sm:h-16 w-full rounded-full border border-slate-200/60 bg-white/95 backdrop-blur-sm pl-14 pr-14 text-base sm:text-lg shadow-lg shadow-slate-200/50 focus-visible:ring-2 focus-visible:ring-indigo-500 focus-visible:border-transparent transition-all duration-300 placeholder:text-slate-400"
                    value={query}
                    onChange={handleChange}
                />

                {/* Right Action: Clear Button */}
                <div className="absolute right-3 z-10 flex items-center">
                    <button
                        type="button"
                        onClick={handleClear}
                        disabled={!query}
                        className={cn(
                            "p-2 text-slate-500 hover:text-slate-700 hover:bg-slate-100 rounded-full transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-indigo-500",
                            query ? "opacity-100 scale-100" : "opacity-0 scale-95 pointer-events-none"
                        )}
                        aria-label="Clear search"
                    >
                        <X className="h-5 w-5" />
                    </button>
                </div>
            </form>

            {/* Trending Tags */}
            {showTags && (
                <div className="mt-6 flex flex-wrap justify-center items-center gap-2 text-sm text-slate-500 animate-in fade-in duration-500">
                    <span className="hidden sm:inline-block font-medium">Trending:</span>
                    {["Full Cream Milk", "Brown Bread", "Coca Cola", "Eggs"].map((tag) => (
                        <Badge
                            key={tag}
                            variant="secondary"
                            className="cursor-pointer bg-white/60 border-slate-200 hover:bg-indigo-50 hover:border-indigo-200 hover:text-indigo-700 px-4 py-1.5 text-xs sm:text-sm rounded-full shadow-sm transition-all duration-200"
                            onClick={() => handleQuickSearch(tag)}
                        >
                            {tag}
                        </Badge>
                    ))}
                </div>
            )}
        </div>
    );
}