import type { MasterProduct } from "@/types";
import { ProductCard } from "./ProductCard";
import { ProductSkeleton } from "./ProductSkeleton";
import { useSwipeable } from "react-swipeable"; // <-- Import here

interface ProductGridProps {
    products: MasterProduct[];
    loading: boolean;
    // Add optional event handlers
    onSwipeNext?: () => void;
    onSwipePrev?: () => void;
}

export function ProductGrid({ products, loading, onSwipeNext, onSwipePrev }: ProductGridProps) {

    const swipeHandlers = useSwipeable({
        onSwipedLeft: () => onSwipeNext?.(),
        onSwipedRight: () => onSwipePrev?.(),
        preventScrollOnSwipe: false,
        trackMouse: false,
    });

    if (loading) {
        return (
            <div
                {...swipeHandlers}
                className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 touch-pan-y"
            >
                {Array.from({ length: 8 }).map((_, i) => (
                    <ProductSkeleton key={i} />
                ))}
            </div>
        );
    }

    if (products.length === 0) {
        return null;
    }

    return (
        <div
            {...swipeHandlers}
            className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 animate-in fade-in duration-700 slide-in-from-bottom-8 touch-pan-y"
        >
            {products.map((product) => (
                <ProductCard key={product.id} product={product} />
            ))}
        </div>
    );
}