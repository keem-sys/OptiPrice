import {useSearchParams} from "react-router-dom";
import {Hero} from "@/components/layout/Hero";
import {ProductGrid} from "@/components/products/ProductGrid";
import {useSearchProducts} from "@/hooks/useProducts";
import { useDebounce } from "@/hooks/useDebounce";
import {useEffect, useState} from "react";
import {Search} from "lucide-react";

export default function Home() {

    const [searchParams, setSearchParams] = useSearchParams();
    const initialQuery = searchParams.get("q") || "";
    const [searchTerm, setSearchTerm] = useState(initialQuery);

    const debouncedSearchTerm = useDebounce(searchTerm, 500);

    useEffect(() => {
        if (debouncedSearchTerm) {
            setSearchParams({ q: debouncedSearchTerm }, { replace: true, preventScrollReset: true });
        } else {
            setSearchParams({}, { replace: true, preventScrollReset: true });
        }
    }, [debouncedSearchTerm, setSearchParams]);

    const {
        data: results = [],
        isLoading,
        isFetching,
        isError,
        error
    } = useSearchProducts(debouncedSearchTerm);

    const handleSearchInput = (query: string) => {
        setSearchTerm(query);
    };

    return (
        <div className="min-h-screen bg-slate-50 pb-20">
            <Hero
                onSearch={handleSearchInput}
                loading={isLoading}
                initialValue={searchTerm}
                compact={!!searchTerm}
            />

            <div className="container mx-auto px-4">
                {isError && (
                    <div className="p-4 mb-8 text-sm text-red-700 bg-red-100 rounded-lg max-w-2xl mx-auto text-center">
                        <span className="font-medium">Error:</span> {(error as Error).message}
                    </div>
                )}

                <ProductGrid products={results} loading={isLoading && !!debouncedSearchTerm} />

                {!isLoading && !isFetching && debouncedSearchTerm && results.length === 0 && !isError && (
                    <div className="flex flex-col items-center justify-center py-20 text-slate-400 animate-in fade-in zoom-in duration-300">
                        <div className="bg-slate-100 p-6 rounded-full mb-4">
                            <Search size={48} className="text-slate-300" />
                        </div>
                        <h3 className="text-xl font-semibold text-slate-700">No products found</h3>
                        <p>We couldn't find matches for "{debouncedSearchTerm}".</p>
                    </div>
                )}
            </div>
        </div>
    );
}