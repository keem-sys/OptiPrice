import { useSearchParams } from "react-router-dom";
import { Hero } from "@/components/layout/Hero";
import { ProductGrid } from "@/components/products/ProductGrid";
import { useSearchProducts } from "@/hooks/useProducts";
import { useDebounce } from "@/hooks/useDebounce";
import { useCallback, useEffect, useState, useRef } from "react";
import { Search } from "lucide-react";
import { cn } from "@/lib/utils";
import {
    Pagination,
    PaginationContent,
    PaginationItem,
    PaginationNext,
    PaginationPrevious,
} from "@/components/ui/pagination";
import { SearchBar } from "@/components/search/SearchBar";

export default function Home() {
    const [searchParams, setSearchParams] = useSearchParams();

    const queryInUrl = searchParams.get("q") || "";
    const pageInUrl = Math.max(0, parseInt(searchParams.get("p") || "0"));

    const [searchTerm, setSearchTerm] = useState(queryInUrl);
    const debouncedSearchTerm = useDebounce(searchTerm, 500);

    const gridTopRef = useRef<HTMLDivElement>(null);

    const updateUrlParams = useCallback((query: string, page: number) => {
        const params: Record<string, string> = {};
        if (query) params.q = query;
        if (page > 0) params.p = page.toString();

        setSearchParams(params, { replace: true });
    }, [setSearchParams]);

    useEffect(() => {
        setSearchTerm(queryInUrl);
    }, [queryInUrl]);

    useEffect(() => {
        if (debouncedSearchTerm === queryInUrl) return;

        updateUrlParams(debouncedSearchTerm, 0);

        if (document.activeElement instanceof HTMLInputElement) {
            document.activeElement.blur();
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    },[debouncedSearchTerm]);

    const { data, isLoading, isFetching, isError, error } = useSearchProducts(
        queryInUrl,
        pageInUrl
    );

    const results = queryInUrl ? (data?.content || []) :[];
    const totalPages = data?.totalPages || 0;

    const handleSearchInput = (query: string) => {
        setSearchTerm(query);
    };

    const handlePageChange = useCallback((newPage: number) => {
        updateUrlParams(queryInUrl, newPage);

        if (gridTopRef.current) {
            const y = gridTopRef.current.getBoundingClientRect().top + window.scrollY - 80;
            window.scrollTo({ top: y, behavior: "smooth" });
        }
    },[queryInUrl, updateUrlParams]);

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
    },[pageInUrl, totalPages, handlePageChange]);

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

            <div className="container mx-auto px-4" ref={gridTopRef}>
                {isError && (
                    <div className="p-4 mb-8 text-sm text-red-700 bg-red-100 rounded-lg max-w-2xl mx-auto text-center border border-red-200">
                        <span className="font-semibold">Oops! Something went wrong:</span> {(error as Error).message}
                    </div>
                )}

                {queryInUrl && (
                    <div className={cn(
                        "transition-opacity duration-300",
                        (isFetching && !isLoading) ? "opacity-50 pointer-events-none" : "opacity-100"
                    )}>
                        <ProductGrid
                            products={results}
                            loading={isLoading || (isFetching && !data)}
                        />
                    </div>
                )}

                {!isLoading && !isError && results.length > 0 && totalPages > 1 && (
                    <div className="mt-12 animate-in fade-in slide-in-from-bottom-4">
                        <Pagination>
                            <PaginationContent className="flex flex-wrap justify-center gap-2">
                                <PaginationItem>
                                    <PaginationPrevious
                                        onClick={() => handlePageChange(Math.max(0, pageInUrl - 1))}
                                        className={cn(
                                            "transition-colors",
                                            pageInUrl === 0
                                                ? "pointer-events-none opacity-50 bg-transparent"
                                                : "cursor-pointer hover:bg-slate-200"
                                        )}
                                    />
                                </PaginationItem>

                                <PaginationItem>
                                    <span className="px-4 py-2 text-sm text-slate-600 font-medium bg-white rounded-md shadow-sm border border-slate-200">
                                        Page {pageInUrl + 1} of {totalPages}
                                    </span>
                                </PaginationItem>

                                <PaginationItem>
                                    <PaginationNext
                                        onClick={() => handlePageChange(Math.min(totalPages - 1, pageInUrl + 1))}
                                        className={cn(
                                            "transition-colors",
                                            pageInUrl === totalPages - 1
                                                ? "pointer-events-none opacity-50 bg-transparent"
                                                : "cursor-pointer hover:bg-slate-200"
                                        )}
                                    />
                                </PaginationItem>
                            </PaginationContent>
                        </Pagination>
                    </div>
                )}

                {!isLoading && !isFetching && debouncedSearchTerm && results.length === 0 && !isError && (
                    <div className="flex flex-col items-center justify-center py-20 text-slate-500 animate-in fade-in zoom-in duration-300">
                        <div className="bg-white p-6 rounded-full mb-6 shadow-sm border border-slate-100">
                            <Search size={48} className="text-slate-300" />
                        </div>
                        <h3 className="text-xl font-semibold text-slate-800 mb-2">No products found</h3>
                        <p className="text-center max-w-sm">
                            We couldn't find any groceries matching "<span className="font-semibold text-slate-700">{debouncedSearchTerm}</span>".
                            Try checking your spelling or using more general terms.
                        </p>
                    </div>
                )}
            </div>
        </div>
    );
}