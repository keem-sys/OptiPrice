import type {MasterProduct} from "@/types";
import { formatCurrency } from "@/lib/formatters";
import { Card, CardContent, CardFooter } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { ShoppingBag, ArrowRight } from "lucide-react";
import { Link, useSearchParams } from "react-router-dom";

interface ProductCardProps {
    product: MasterProduct;
}

export function ProductCard({ product }: ProductCardProps) {
    const [searchParams] = useSearchParams();
    const currentQuery = searchParams.get("q");

    const storeCount = product.storeItems.length;

    if (storeCount === 0) {
        return (
            <Card className="h-full bg-slate-50 opacity-60">
                <CardContent className="p-5 flex items-center justify-center h-full">
                    <span className="text-slate-400 font-medium">Currently Unavailable</span>
                </CardContent>
            </Card>
        );
    }

    /* if (!product.storeItems || product.storeItems.length === 0) {
        return null;
    } */

    const prices = product.storeItems.map((item) => item.price);
    const minPrice = prices.length > 0 ? Math.min(...prices) : 0;
    const maxPrice = prices.length > 0 ? Math.max(...prices) : 0;

    const image = product.storeItems.find((i) => i.imageUrl)?.imageUrl
        || "https://placehold.co/400x400?text=No+Image";


    return (
        <Card className="group relative h-full overflow-hidden border-slate-200 bg-white
        transition-all hover:-translate-y-1 hover:shadow-lg">
            {/* IMAGE SECTION */}
            <div className="relative aspect-square overflow-hidden bg-slate-100 p-8">
                <img
                    src={image}
                    alt={product.genericName}
                    className="h-full w-full object-contain mix-blend-multiply transition-transform
                    duration-500 group-hover:scale-105"
                    loading="lazy"
                />
                {/* Category Badge */}
                <Badge className="absolute top-3 left-3 bg-white/90 text-slate-700 hover:bg-white shadow-sm backdrop-blur-sm">
                    {product.category || "General"}
                </Badge>
            </div>

            {/* CONTENT SECTION */}
            <CardContent className="p-5">
                <h3 className="line-clamp-2 text-lg font-bold text-slate-900 group-hover:text-indigo-600 transition-colors h-14">
                    {product.genericName}
                </h3>

                <div className="mt-2 flex items-center justify-between">
                    <div className="flex flex-col">
                        <span className="text-xs text-slate-500 font-medium uppercase tracking-wide">
                          Best Price
                        </span>
                        <span className="text-xl font-extrabold text-emerald-600">
                            {formatCurrency(minPrice)}
                        </span>
                    </div>

                    {minPrice !== maxPrice && (
                        <div className="text-right">
                            <span className="text-xs text-slate-400">up to</span>
                            <div className="text-sm font-medium text-slate-500 line-through decoration-red-400 decoration-2">
                                {formatCurrency(maxPrice)}
                            </div>
                        </div>
                    )}
                </div>
            </CardContent>

            <CardFooter className="bg-slate-50 p-4 border-t border-slate-100">
                <div className="flex w-full items-center justify-between">
                    <div className="flex items-center gap-1 text-sm text-slate-600">
                        <ShoppingBag size={16} />
                        <span>{storeCount} stores</span>
                    </div>

                    <Link
                        to={`/product/${product.id}`}
                        state={{ previousSearch: currentQuery }}
                        className="inline-flex items-center gap-2 text-sm font-bold
                        text-indigo-600 hover:text-indigo-700 after:absolute after:inset-0"
                    >
                        Compare <ArrowRight size={14} />
                    </Link>
                </div>
            </CardFooter>
        </Card>
    );
}