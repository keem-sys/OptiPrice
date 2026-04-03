import { useSearchParams } from "react-router-dom";
import { Hero } from "@/components/layout/Hero";
import { ProductGrid } from "@/components/products/ProductGrid";
import { useSearchProducts } from "@/hooks/useProducts";
import { useDebounce } from "@/hooks/useDebounce";
import {useCallback, useEffect, useState} from "react";
import { Search } from "lucide-react";
import {
    Pagination,
    PaginationContent,
    PaginationItem,
    PaginationNext,
    PaginationPrevious,
} from "@/components/ui/pagination";
import {SearchBar} from "@/components/search/SearchBar.tsx";

export default function Home() {
    const [searchParams, setSearchParams] = useSearchParams();

    const queryInUrl = searchParams.get("q") || "";
    const pageInUrl = Math.max(0, parseInt(searchParams.get("p") || "0"));

    const [searchTerm, setSearchTerm] = useState(queryInUrl);
    const debouncedSearchTerm = useDebounce(searchTerm, 500);

    const updateUrlParams = useCallback((query: string, page: number) => {
        const params: Record<string, string> = {};
        if (query) params.q = query;
        if (page > 0) params.p = page.toString();

        setSearchParams(params, { replace: true });
    }, [setSearchParams]);

    useEffect(() => {
        if (debouncedSearchTerm !== queryInUrl) {
            updateUrlParams(debouncedSearchTerm, 0);
        }
    }, [debouncedSearchTerm, queryInUrl, updateUrlParams]);

    useEffect(() => {
        setSearchTerm(queryInUrl);
    }, [queryInUrl]);

    const { data, isLoading, isFetching, isError, error } = useSearchProducts(
        queryInUrl,
        pageInUrl
    );

    const results = queryInUrl ? (data?.content || []) : [];
    const totalPages = data?.totalPages || 0;

    const handleSearchInput = (query: string) => {
        setSearchTerm(query);
    };

    const handlePageChange = useCallback((newPage: number) => {
        updateUrlParams(queryInUrl, newPage);
    }, [queryInUrl, updateUrlParams]);

    useEffect(() => {
        const handleKeyDown = (e: KeyboardEvent) => {
            if (e.target instanceof HTMLInputElement || e.target instanceof HTMLTextAreaElement) return;

            if (e.key === "ArrowLeft" && pageInUrl > 0) {
                handlePageChange(pageInUrl - 1);
            } else if (e.key === "ArrowRight" && pageInUrl < totalPages - 1) {
                handlePageChange(pageInUrl + 1);
            }
        };

        window.addEventListener("keydown", handleKeyDown);

        return () => window.removeEventListener("keydown", handleKeyDown);
    }, [pageInUrl, totalPages, handlePageChange]);

    return (
        <div className="min-h-screen bg-slate-50 pb-20">
            <Hero compact={!!searchTerm}>
                <SearchBar
                    onSearch={handleSearchInput}
                    loading={isLoading || isFetching}
                    initialValue={searchTerm}
                    showTags={!searchTerm}
                />
            </Hero>

            <div className="container mx-auto px-4">
                {isError && (
                    <div className="p-4 mb-8 text-sm text-red-700 bg-red-100 rounded-lg max-w-2xl mx-auto text-center">
                        <span className="font-medium">Error:</span> {(error as Error).message}
                    </div>
                )}

                {queryInUrl && (
                    <ProductGrid
                        products={results}
                        loading={isLoading || (isFetching && !data)}
                    />
                )}

                {!isLoading && !isError && results.length > 0 && totalPages > 1 && (
                    <div className="mt-12 animate-in fade-in slide-in-from-bottom-4">
                        <Pagination>
                            <PaginationContent>
                                <PaginationItem>
                                    <PaginationPrevious
                                        onClick={() => handlePageChange(Math.max(0, pageInUrl - 1))}
                                        className={pageInUrl === 0 ? "pointer-events-none opacity-50" : "cursor-pointer"}
                                    />
                                </PaginationItem>

                                <PaginationItem>
                                    <span className="px-4 text-sm text-slate-500 font-medium">
                                        Page {pageInUrl + 1} of {totalPages}
                                    </span>
                                </PaginationItem>

                                <PaginationItem>
                                    <PaginationNext
                                        onClick={() => handlePageChange(Math.min(totalPages - 1, pageInUrl + 1))}
                                        className={pageInUrl === totalPages - 1 ? "pointer-events-none opacity-50" : "cursor-pointer"}
                                    />
                                </PaginationItem>
                            </PaginationContent>
                        </Pagination>
                    </div>
                )}

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