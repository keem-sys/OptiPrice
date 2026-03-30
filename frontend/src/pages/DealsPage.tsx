import { useState } from "react";
import { useDeals } from "@/hooks/useDeals";
import { ProductCard } from "@/components/products/ProductCard";
import { Flame, TrendingDown, ArrowLeft, ArrowRight, Loader2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";

export function DealsPage() {
    const[page, setPage] = useState(0);
    const [dealType, setDealType] = useState<"arbitrage" | "drops">("arbitrage");

    const { data, isLoading, isFetching } = useDeals(dealType, page, 12);

    const handleTabSwitch = (type: "arbitrage" | "drops") => {
        setDealType(type);
        setPage(0);
    };

    return (
        <div className="container mx-auto px-4 py-8 max-w-7xl">

            {/* HERO SECTION */}
            <div className={cn(
                "mb-8 rounded-2xl p-8 text-white shadow-lg transition-colors duration-500",
                dealType === "arbitrage" ? "bg-linear-to-r from-orange-500 to-red-600" : "bg-linear-to-r from-emerald-500 to-teal-600"
            )}>
                <div className="flex items-center gap-3 mb-4">
                    {dealType === "arbitrage" ? <Flame className="h-8 w-8 text-yellow-300" /> : <TrendingDown className="h-8 w-8 text-green-200" />}
                    <h1 className="text-3xl font-extrabold tracking-tight sm:text-4xl">
                        {dealType === "arbitrage" ? "Huge Store Gaps" : "Recent Price Drops"}
                    </h1>
                </div>
                <p className="max-w-2xl text-lg opacity-90">
                    {dealType === "arbitrage"
                        ? "We found products with a price difference of at least R10.00 between stores today. Don't overpay!"
                        : "These items are currently at least 15% cheaper than their recent historical high price. Time to stock up!"}
                </p>
            </div>

            {/* TABS CONTROLS */}
            <div className="flex justify-center gap-4 mb-10">
                <Button
                    variant={dealType === "arbitrage" ? "default" : "outline"}
                    onClick={() => handleTabSwitch("arbitrage")}
                    className={dealType === "arbitrage" ? "bg-orange-600 hover:bg-orange-700" : ""}
                >
                    <Flame className="mr-2 h-4 w-4" /> Store Gaps
                </Button>
                <Button
                    variant={dealType === "drops" ? "default" : "outline"}
                    onClick={() => handleTabSwitch("drops")}
                    className={dealType === "drops" ? "bg-emerald-600 hover:bg-emerald-700" : ""}
                >
                    <TrendingDown className="mr-2 h-4 w-4" /> Price Drops
                </Button>
            </div>

            {/* LOADING STATE */}
            {(isLoading || (isFetching && !data)) && (
                <div className="flex min-h-100 flex-col items-center justify-center text-slate-500">
                    <Loader2 className="h-10 w-10 animate-spin mb-4" />
                    <p className="text-lg font-medium">Hunting for the best deals...</p>
                </div>
            )}

            {/* DATA GRID */}
            {!isLoading && data && data.content.length > 0 && (
                <>
                    <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
                        {data.content.map((product) => (
                            <ProductCard key={product.id} product={product} />
                        ))}
                    </div>

                    {/* PAGINATION */}
                    {data.totalPages > 1 && (
                        <div className="mt-12 flex items-center justify-center gap-4">
                            <Button variant="outline" onClick={() => setPage((p) => Math.max(0, p - 1))} disabled={page === 0}>
                                <ArrowLeft size={16} className="mr-2" /> Previous
                            </Button>
                            <span className="text-sm font-medium text-slate-600">Page {page + 1} of {data.totalPages}</span>
                            <Button variant="outline" onClick={() => setPage((p) => p + 1)} disabled={page >= data.totalPages - 1}>
                                Next <ArrowRight size={16} className="ml-2" />
                            </Button>
                        </div>
                    )}
                </>
            )}

            {/* EMPTY STATE */}
            {!isLoading && !isFetching && data?.content.length === 0 && (
                <div className="flex min-h-75 flex-col items-center justify-center rounded-xl border border-dashed border-slate-300 bg-slate-50 p-8 text-center">
                    <TrendingDown className="h-12 w-12 text-slate-300 mb-4" />
                    <h3 className="text-xl font-bold text-slate-700">No {dealType === 'drops' ? 'Price Drops' : 'Store Gaps'} Today</h3>
                    <p className="text-slate-500 max-w-md mt-2">
                        {dealType === 'drops'
                            ? "No products have dropped by 15% recently. Check back tomorrow!"
                            : "Prices are very competitive across all stores right now."}
                    </p>
                </div>
            )}
        </div>
    );
}