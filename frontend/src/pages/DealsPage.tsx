import { useState } from "react";
import { useDeals } from "@/hooks/useDeals";
import { ProductCard } from "@/components/products/ProductCard";
import { Flame, AlertCircle, ArrowLeft, ArrowRight, Loader2 } from "lucide-react";
import { Button } from "@/components/ui/button";

export function DealsPage() {
    const [page, setPage] = useState(0);
    const { data, isLoading, isError } = useDeals(page, 12);

    return (
        <div className="container mx-auto px-4 py-8 max-w-7xl">

            {/* HERO SECTION */}
            <div className="mb-10 rounded-2xl bg-linear-to-r from-orange-500 to-red-600 p-8 text-white shadow-lg">
                <div className="flex items-center gap-3 mb-4">
                    <Flame className="h-8 w-8 text-yellow-300" />
                    <h1 className="text-3xl font-extrabold tracking-tight sm:text-4xl">
                        Arbitrage Deals
                    </h1>
                </div>
                <p className="max-w-2xl text-lg text-red-50">
                    We found the biggest price gaps in the country today.
                    Every product below has a price difference of <strong>at least R10.00</strong> between Checkers, Shoprite, and Pick n Pay. Don't overpay!
                </p>
            </div>

            {/* LOADING STATE */}
            {isLoading && (
                <div className="flex min-h-100 flex-col items-center justify-center text-slate-500">
                    <Loader2 className="h-10 w-10 animate-spin text-orange-500 mb-4" />
                    <p className="text-lg font-medium">Hunting for the best deals...</p>
                </div>
            )}

            {/* ERROR STATE */}
            {isError && (
                <div className="flex min-h-100 flex-col items-center justify-center text-red-500">
                    <AlertCircle className="h-12 w-12 mb-4" />
                    <p className="text-lg font-medium">Oops! Failed to load deals.</p>
                    <Button variant="outline" className="mt-4" onClick={() => window.location.reload()}>
                        Try Again
                    </Button>
                </div>
            )}

            {/* SUCCESS / DATA GRID */}
            {data && data.content.length > 0 ? (
                <>
                    <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
                        {data.content.map((product) => (
                            <ProductCard key={product.id} product={product} />
                        ))}
                    </div>

                    {/* PAGINATION CONTROLS */}
                    {data.totalPages > 1 && (
                        <div className="mt-12 flex items-center justify-center gap-4">
                            <Button
                                variant="outline"
                                onClick={() => setPage((p) => Math.max(0, p - 1))}
                                disabled={page === 0}
                                className="gap-2"
                            >
                                <ArrowLeft size={16} /> Previous
                            </Button>

                            <span className="text-sm font-medium text-slate-600">
                                Page {page + 1} of {data.totalPages}
                            </span>

                            <Button
                                variant="outline"
                                onClick={() => setPage((p) => p + 1)}
                                disabled={page >= data.totalPages - 1}
                                className="gap-2"
                            >
                                Next <ArrowRight size={16} />
                            </Button>
                        </div>
                    )}
                </>
            ) : (
                !isLoading && !isError && (
                    <div className="flex min-h-75 flex-col items-center justify-center rounded-xl border border-dashed border-slate-300 bg-slate-50 p-8 text-center">
                        <Flame className="h-12 w-12 text-slate-300 mb-4" />
                        <h3 className="text-xl font-bold text-slate-700">No Huge Gaps Today</h3>
                        <p className="text-slate-500 max-w-md mt-2">
                            Prices are currently very competitive across all stores. We couldn't find any products with a gap larger than R10.00 right now.
                        </p>
                    </div>
                )
            )}
        </div>
    );
}