import type { MasterProduct } from "@/types";
import { formatCurrency } from "@/lib/formatters";
import { Card, CardContent, CardFooter } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { ArrowRight, Tag } from "lucide-react";
import { Link, useLocation } from "react-router-dom";
import { cn } from "@/lib/utils";

interface ProductCardProps {
    product: MasterProduct;
}

const STORE_COLORS: Record<string, string> = {
    "Checkers": "#38A8AE",
    "Shoprite": "#ea212d",
    "Pick n Pay": "#003359",
    "default": "bg-slate-400"
};

export function ProductCard({ product }: ProductCardProps) {
    const location = useLocation();
    const storeCount = product.storeItems?.length || 0;

    if (storeCount === 0) {
        return (
            <Card className="h-full bg-slate-50 opacity-60">
                <CardContent className="p-5 flex items-center justify-center h-full">
                    <span className="text-slate-400 font-medium">Currently Unavailable</span>
                </CardContent>
            </Card>
        );
    }

    const sortedItems = [...product.storeItems].sort((a, b) => a.price - b.price);
    const cheapestItem = sortedItems[0];
    const highestPriceItem = sortedItems[sortedItems.length - 1];

    const minPrice = cheapestItem.price;
    const maxPrice = highestPriceItem.price;
    const savings = maxPrice - minPrice;

    const promoItem = [...product.storeItems]
        .filter(i => i.isOnPromotion)
        .sort((a, b) => a.price - b.price)[0];

    const image = cheapestItem.imageUrl || "https://placehold.co/400x400?text=No+Image";
    const brandName = cheapestItem.brand || "Unknown Brand";

    return (
        <Card className="group relative flex flex-col h-full overflow-hidden border-slate-200 bg-white transition-all hover:-translate-y-1 hover:shadow-xl">

            {/* --- IMAGE SECTION --- */}
            <div className="relative aspect-square overflow-hidden bg-slate-50 p-6 sm:p-8">
                <img
                    src={image}
                    alt={product.genericName}
                    className="h-full w-full object-contain mix-blend-multiply transition-transform duration-500 group-hover:scale-105"
                    loading="lazy"
                />

                {/* Category Badge */}
                <Badge className="absolute top-3 left-3 bg-white/90 text-slate-600 hover:bg-white shadow-sm backdrop-blur-md border-slate-200/50">
                    {product.category || "General"}
                </Badge>

                {/* Official Store Promotion Badge */}
                {promoItem && (
                    <Badge
                        className={cn(
                            "absolute top-3 right-3 shadow-md border-none px-3 py-1 font-bold text-white",
                            STORE_COLORS[promoItem.store.name] || STORE_COLORS.default
                        )}
                    >
                        <span className="flex items-center gap-1.5">
                            {promoItem.promotionText || "Sale"}
                        </span>
                    </Badge>
                )}
            </div>

            {/* --- CONTENT SECTION --- */}
            <CardContent className="p-5 grow flex flex-col">

                {/* Brand Name */}
                <div className="flex items-center gap-1.5 mb-1.5 text-slate-400">
                    <Tag size={12} />
                    <span className="text-xs font-bold uppercase tracking-wider line-clamp-1">
                        {brandName}
                    </span>
                </div>

                {/* Product Title */}
                <h3 className="line-clamp-2 text-[1.05rem] font-bold leading-tight text-slate-900 group-hover:text-indigo-600 transition-colors mb-4">
                    {product.genericName}
                </h3>

                <div className="mt-auto pt-2 border-t border-slate-100 border-dashed">
                    {/* The "Cheapest At" Indicator */}
                    <div className="flex items-center gap-1.5 mb-2 text-xs font-medium text-slate-500">
                        <span>Cheapest at</span>
                        <div className="flex items-center gap-1.5 bg-slate-100 px-2 py-1 rounded-md">
                            <img
                                src={cheapestItem.store.logoUrl}
                                alt={cheapestItem.store.name}
                                className="w-3.5 h-3.5 object-contain rounded-sm"
                            />
                            <span className="text-slate-700 font-bold tracking-tight">
                                {cheapestItem.store.name}
                            </span>
                        </div>
                    </div>

                    <div className="flex items-end justify-between">
                        {/* Price Display */}
                        <div className="flex items-baseline gap-2">
                            <span className="text-2xl font-extrabold tracking-tight text-slate-900">
                                {formatCurrency(minPrice)}
                            </span>
                            {savings > 0 && (
                                <span className="text-sm font-semibold text-slate-400 line-through mb-0.5">
                                    {formatCurrency(maxPrice)}
                                </span>
                            )}
                        </div>

                        {savings > 0 && (
                            <Badge className="bg-emerald-100 text-emerald-700 hover:bg-emerald-200 border-none font-bold pointer-events-none">
                                Save {formatCurrency(savings)}
                            </Badge>
                        )}
                    </div>
                </div>
            </CardContent>

            {/* --- FOOTER SECTION --- */}
            <CardFooter className="bg-slate-50 p-4 border-t border-slate-100 mt-auto">
                <div className="flex w-full items-center justify-between">

                    <div className="flex items-center gap-2">
                        <div className="flex -space-x-2">
                            {sortedItems.map((item) => (
                                <div
                                    key={item.store.name}
                                    title={`Available at ${item.store.name}`}
                                    className="w-7 h-7 rounded-full border-2 border-white bg-white shadow-sm flex items-center justify-center overflow-hidden z-10 hover:z-20 transition-all hover:scale-110"
                                >
                                    <img
                                        src={item.store.logoUrl}
                                        alt={item.store.name}
                                        className="w-full h-full object-contain p-0.5"
                                    />
                                </div>
                            ))}
                        </div>
                        <span className="text-xs font-medium text-slate-500 ml-1">
                            {storeCount} {storeCount === 1 ? 'store' : 'stores'}
                        </span>
                    </div>

                    <Link
                        to={`/product/${product.id}`}
                        state={{ from: location.pathname + location.search }}
                        className="inline-flex items-center gap-1.5 text-sm font-bold text-indigo-600 hover:text-indigo-700 after:absolute after:inset-0"
                    >
                        Compare <ArrowRight size={14} />
                    </Link>
                </div>
            </CardFooter>
        </Card>
    );
}