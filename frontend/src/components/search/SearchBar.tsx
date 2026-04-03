import React, { useState, useEffect } from "react";
import { Search } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
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

    // Sync with URL changes
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

    return (
        <div className={cn("w-full transition-all duration-700", className)}>
            <form onSubmit={(e) => e.preventDefault()} className="relative flex items-center">
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

            {showTags && (
                <div className="mt-6 flex flex-wrap justify-center gap-2 text-sm text-slate-500">
                    <span>Trending:</span>
                    {["Full Cream Milk", "Brown Bread", "Coca Cola", "Eggs"].map((tag) => (
                        <Badge
                            key={tag}
                            variant="secondary"
                            className="cursor-pointer hover:bg-indigo-100 hover:text-indigo-700 px-3 py-1"
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