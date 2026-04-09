import { Link, useParams, useLocation, useNavigate } from "react-router-dom";
import { useProductDetails } from "@/hooks/useProducts";
import { PriceHistoryChart } from "@/components/products/PriceHistoryChart.tsx";
import { Button } from "@/components/ui/button";
import {ArrowLeft, Loader2, AlertCircle, ExternalLink, Tag, TrendingDown, ZoomIn} from "lucide-react";
import { formatCurrency } from "@/lib/formatters";
import { cn } from "@/lib/utils";
import type { StoreItem } from "@/types";
import React from "react";

import {
    Dialog,
    DialogContent,
    DialogTrigger,
    DialogTitle,
} from "@/components/ui/dialog";

const STORE_STYLES: Record<string, { dot: string; chip: string; text: string }> = {
    Checkers: {
        dot: "bg-checkers",
        chip: "bg-checkers/10 border-checkers/20 text-checkers",
        text: "text-checkers",
    },
    Shoprite: {
        dot: "bg-shoprite",
        chip: "bg-shoprite/10 border-shoprite/20 text-shoprite",
        text: "text-shoprite",
    },
    "Pick n Pay": {
        dot: "bg-pnp",
        chip: "bg-pnp/10 border-pnp/20 text-pnp",
        text: "text-pnp",
    },
};

const defaultStyle = {
    dot: "bg-slate-400",
    chip: "bg-slate-50 border-slate-200 text-slate-700",
    text: "text-slate-600",
};

function storeStyle(name: string) {
    return STORE_STYLES[name] ?? defaultStyle;
}

// Price row card
function StoreRow({
                      item,
                      isBest,
                      minPrice,
                      maxPrice,
                  }: {
    item: StoreItem;
    isBest: boolean;
    minPrice: number;
    maxPrice: number;
}) {
    const style = storeStyle(item.store.name);
    const range = maxPrice - minPrice || 1;
    const barPct = Math.max(4, Math.round(((maxPrice - item.price) / range) * 100));
    const savings = item.oldPrice && item.oldPrice > item.price ? item.oldPrice - item.price : null;

    return (
        <div
            className={cn(
                "group relative flex items-start gap-3 sm:gap-4 rounded-xl border px-4 sm:px-5 py-4 transition-all",
                isBest
                    ? "border-emerald-200 bg-emerald-50/60 pt-8"
                    : "border-slate-100 bg-white hover:border-slate-200 hover:shadow-sm"
            )}
        >
            {/* Best-price ribbon */}
            {isBest && (
                <span className="absolute -top-px left-4 rounded-b-md bg-emerald-500 px-2.5 py-0.5 text-[10px] font-semibold uppercase tracking-widest text-white shadow-sm">
                    Best Price
                </span>
            )}

            {/* Store logo / fallback dot */}
            <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg border border-slate-100 bg-white shadow-sm mt-0.5">
                {item.store.logoUrl ? (
                    <img
                        src={item.store.logoUrl}
                        alt={item.store.name}
                        className="h-7 w-7 object-contain"
                    />
                ) : (
                    <span className={cn("h-3 w-3 rounded-full", style.dot)} />
                )}
            </div>

            {/* Store name + promo */}
            <div className="min-w-0 flex-1">
                <div className="flex flex-wrap items-center gap-1.5 sm:gap-2">
                    <span className="font-semibold text-slate-800 leading-none">{item.store.name}</span>
                    {item.isOnPromotion && item.promotionText && (
                        <span
                            className={cn(
                                "inline-flex items-center gap-1 rounded-full border px-2 py-0.5 text-[11px] font-medium whitespace-nowrap",
                                style.chip
                            )}
                        >
                            <Tag size={10} className="shrink-0" />
                            {item.promotionText}
                        </span>
                    )}
                </div>

                <p className="mt-1.5 text-sm text-slate-500 leading-snug pr-2 text-balance">
                    {item.storeSpecificName}
                </p>

                {/* Price bar */}
                <div className="mt-2.5 h-1.5 w-full overflow-hidden rounded-full bg-slate-100">
                    <div
                        className={cn(
                            "h-full rounded-full transition-all",
                            isBest ? "bg-emerald-400" : "bg-slate-300"
                        )}
                        style={{ width: `${barPct}%` }}
                    />
                </div>
            </div>

            {/* Price block */}
            <div className="flex shrink-0 flex-col items-end gap-1">
                <span
                    className={cn(
                        "text-xl sm:text-2xl font-bold tabular-nums leading-none",
                        isBest ? "text-emerald-600" : "text-slate-700"
                    )}
                >
                    {formatCurrency(item.price)}
                </span>

                {savings ? (
                    <span className="flex items-center gap-1 text-xs font-medium text-emerald-600 mt-1">
                        <TrendingDown size={11} className="shrink-0" />
                        Save {formatCurrency(savings)}
                    </span>
                ) : item.oldPrice && item.oldPrice > item.price ? null : (
                    <span className="text-xs text-slate-400 mt-1 text-right">
                        {item.lastUpdated
                            ? `Updated ${new Date(item.lastUpdated).toLocaleDateString("en-ZA", {
                                day: "numeric",
                                month: "short",
                            })}`
                            : ""}
                    </span>
                )}

                {item.productUrl && (
                    <a
                        href={item.productUrl}
                        target="_blank"
                        rel="noopener noreferrer"
                        className={cn(
                            "mt-1.5 inline-flex items-center gap-1 text-xs font-medium underline-offset-2 hover:underline",
                            style.text
                        )}
                    >
                        Visit store <ExternalLink size={10} className="shrink-0" />
                    </a>
                )}
            </div>
        </div>
    );
}

export default function ProductDetails() {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const location = useLocation();
    const fromUrl = location.state?.from || "/";

    const handleBack = (e: React.MouseEvent) => {
        if (location.state?.from) {
            e.preventDefault();
            navigate(-1);
        }
    };

    const { data: product, isLoading, isError } = useProductDetails(id ?? "");

    // Loading
    if (isLoading) {
        return (
            <div className="flex min-h-[60vh] items-center justify-center">
                <Loader2 className="animate-spin text-indigo-500" size={32} />
            </div>
        );
    }

    // Error
    if (isError || !product) {
        return (
            <div className="flex min-h-[60vh] flex-col items-center justify-center gap-3 text-slate-500">
                <AlertCircle size={44} className="text-red-400" />
                <h2 className="text-xl font-semibold text-slate-700">Product not found</h2>
                <Button variant="link" asChild>
                    <Link to="/">Go Home</Link>
                </Button>
            </div>
        );
    }

    // Derived data
    const prices = product.storeItems.map((i) => i.price);
    const minPrice = Math.min(...prices);
    const maxPrice = Math.max(...prices);
    const priceDiff = maxPrice - minPrice;
    const storeCount = product.storeItems.length;

    const sortedItems = [...product.storeItems].sort((a, b) => a.price - b.price);

    const image =
        product.storeItems.find((i) => i.imageUrl)?.imageUrl ||
        "https://placehold.co/400x400?text=No+Image";

    const promoItems = product.storeItems.filter((i) => i.isOnPromotion);

    return (
        <div className="container mx-auto max-w-5xl px-4 pb-24 pt-8">

            {/* Back button */}
            <Button
                variant="ghost"
                asChild
                className="mb-6 pl-0 text-slate-500 hover:bg-transparent hover:text-indigo-600"
            >
                <Link to={fromUrl} onClick={handleBack} className="flex items-center gap-1.5">
                    <ArrowLeft size={15} /> Back
                </Link>
            </Button>

            {/* Hero row */}
            <div className="mb-10 flex flex-col gap-8 md:flex-row md:items-start">

                {/* Product image */}
                <div className="relative w-full sm:w-auto sm:shrink-0 sm:self-start">
                    <Dialog>
                        <DialogTrigger asChild>
                            <div className="group relative flex h-48 w-full sm:h-56 sm:w-56 cursor-zoom-in items-center justify-center rounded-2xl border border-slate-100 bg-slate-50 p-6 shadow-sm transition-all hover:border-indigo-200 hover:shadow-md">
                                <img
                                    src={image}
                                    alt={product.genericName}
                                    className="h-full w-full object-contain mix-blend-multiply transition-transform duration-300 group-hover:scale-105"
                                />

                                <div className="absolute right-3 top-3 rounded-full bg-white/80 p-1.5 text-slate-600 opacity-0 backdrop-blur-sm transition-opacity group-hover:opacity-100 shadow-sm">
                                    <ZoomIn size={16} />
                                </div>
                            </div>
                        </DialogTrigger>

                        <DialogContent className="max-w-2xl w-[calc(100%-2rem)] sm:w-full border-none bg-transparent p-0 shadow-none">
                            <DialogTitle className="sr-only">
                                Fullscreen view of {product.genericName}
                            </DialogTitle>

                            <div className="relative flex h-[50vh] sm:h-[70vh] w-full items-center justify-center bg-white/95 rounded-2xl backdrop-blur-md p-6 sm:p-8">
                                <img
                                    src={image}
                                    alt={product.genericName}
                                    className="h-full w-full object-contain drop-shadow-2xl"
                                />
                            </div>
                        </DialogContent>
                    </Dialog>

                    {promoItems.length > 0 && (
                        <span className="absolute -right-2 -top-2 z-10 rounded-full bg-rose-500 px-2.5 py-1 text-[11px] font-bold uppercase tracking-wide text-white shadow pointer-events-none">
                            On Sale
                         </span>
                    )}
                </div>

                {/* Title + stat strip */}
                <div className="flex flex-1 flex-col">
                    <span className="mb-2 inline-block self-start rounded-md bg-indigo-50 px-2.5 py-1 text-xs font-semibold text-indigo-700">
                        {product.category}
                    </span>

                    <h1 className="text-3xl font-bold leading-tight text-slate-900 md:text-4xl">
                        {product.genericName}
                    </h1>

                    {/* Brand chips */}
                    {(() => {
                        const brands = [
                            ...new Set(product.storeItems.map((i) => i.brand).filter(Boolean)),
                        ];
                        return brands.length > 0 ? (
                            <div className="mt-2 flex flex-wrap gap-1.5">
                                {brands.map((b) => (
                                    <span
                                        key={b}
                                        className="rounded-full border border-slate-200 bg-white px-2.5 py-0.5 text-xs text-slate-500"
                                    >
                                        {b}
                                    </span>
                                ))}
                            </div>
                        ) : null;
                    })()}

                    {/* Stat strip */}
                    <div className="mt-6 grid grid-cols-3 divide-x divide-slate-100 overflow-hidden rounded-xl border border-slate-100 bg-white shadow-sm">
                        <div className="flex flex-col items-center px-4 py-4">
                            <span className="text-[11px] font-medium uppercase tracking-widest text-slate-400">
                                Best Price
                            </span>
                            <span className="mt-1 text-2xl font-bold text-emerald-600">
                                {formatCurrency(minPrice)}
                            </span>
                        </div>
                        <div className="flex flex-col items-center px-4 py-4">
                            <span className="text-[11px] font-medium uppercase tracking-widest text-slate-400">
                                Highest
                            </span>
                            <span className="mt-1 text-2xl font-bold text-slate-500">
                                {formatCurrency(maxPrice)}
                            </span>
                        </div>
                        <div className="flex flex-col items-center px-4 py-4">
                            <span className="text-[11px] font-medium uppercase tracking-widest text-slate-400">
                                You Save
                            </span>
                            <span
                                className={cn(
                                    "mt-1 text-2xl font-bold",
                                    priceDiff > 0 ? "text-rose-500" : "text-slate-300"
                                )}
                            >
                                {priceDiff > 0 ? formatCurrency(priceDiff) : "—"}
                            </span>
                        </div>
                    </div>

                    {/* Store chips */}
                    <div className="mt-4 flex flex-wrap gap-2">
                        {sortedItems.map((item) => {
                            const s = storeStyle(item.store.name);
                            const isBest = item.price === minPrice;
                            return (
                                <span
                                    key={item.id}
                                    className={cn(
                                        "flex items-center gap-1.5 rounded-full border px-3 py-1 text-xs font-medium",
                                        isBest
                                            ? "border-emerald-200 bg-emerald-50 text-emerald-700"
                                            : s.chip
                                    )}
                                >
                                    <span className={cn("h-2 w-2 rounded-full", isBest ? "bg-emerald-500" : s.dot)} />
                                    {item.store.name}
                                    <span className="font-semibold">{formatCurrency(item.price)}</span>
                                </span>
                            );
                        })}
                        <span className="flex items-center gap-1 rounded-full border border-slate-100 bg-slate-50 px-3 py-1 text-xs text-slate-400">
                            {storeCount} {storeCount === 1 ? "store" : "stores"}
                        </span>
                    </div>
                </div>
            </div>

            {/* Live price comparison */}
            <section className="mb-10">
                <h2 className="mb-4 flex items-center gap-2 text-lg font-semibold text-slate-800">
                    Live Price Comparison
                    <span className="rounded-full bg-slate-100 px-2 py-0.5 text-xs font-normal text-slate-500">
                        {storeCount} stores
                    </span>
                </h2>

                <div className="space-y-3">
                    {sortedItems.map((item) => (
                        <StoreRow
                            key={item.id}
                            item={item}
                            isBest={item.price === minPrice}
                            minPrice={minPrice}
                            maxPrice={maxPrice}
                        />
                    ))}
                </div>

                {priceDiff > 0 && (
                    <p className="mt-4 text-center text-sm text-slate-400">
                        Shop at{" "}
                        <span className="font-medium text-emerald-600">
                            {sortedItems[0].store.name}
                        </span>{" "}
                        and save{" "}
                        <span className="font-medium text-emerald-600">
                            {formatCurrency(priceDiff)}
                        </span>{" "}
                        vs the most expensive option.
                    </p>
                )}
            </section>

            {/* Price history chart */}
            {id && (
                <section>
                    <h2 className="mb-4 text-lg font-semibold text-slate-800">
                        Price History
                    </h2>
                    <div className="rounded-2xl border border-slate-100 bg-white p-4 shadow-sm">
                        <PriceHistoryChart masterId={id} />
                    </div>
                </section>
            )}
        </div>
    );
}