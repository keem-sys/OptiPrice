import { Link, useParams, useLocation, createSearchParams } from "react-router-dom";
import { useProductDetails } from "@/hooks/useProducts"; // Hook
import { ComparisonTable } from "@/components/products/ComparisonTable";
import { Button } from "@/components/ui/button";
import { ArrowLeft, Loader2, AlertCircle } from "lucide-react";

export default function ProductDetails() {
    const { id } = useParams<{ id: string }>();

    const location = useLocation();
    const previousSearch = location.state?.previousSearch;

    const backLink = previousSearch
        ? `/?${createSearchParams({ q: previousSearch }).toString()}`
        : "/";

    const backLabel = "Back to Search";

    const { data: product, isLoading, isError } = useProductDetails(id || "");

    if (isLoading) {
        return <div className="flex justify-center pt-20"><Loader2 className="animate-spin text-indigo-600" size={32} /></div>;
    }

    if (isError || !product) {
        return (
            <div className="flex flex-col items-center justify-center pt-20 text-slate-500">
                <AlertCircle size={48} className="mb-4 text-red-400" />
                <h2 className="text-xl font-bold">Product not found</h2>
                <Button variant="link" asChild className="mt-4"><Link to="/">Go Home</Link></Button>
            </div>
        );
    }

    return (
        <div className="container mx-auto max-w-5xl px-4 pt-8 pb-20">
            <div className="mb-8">
                <Button variant="ghost" asChild className="mb-4 pl-0 hover:bg-transparent hover:text-indigo-600">
                    <Link to={backLink} className="gap-2">
                        <ArrowLeft size={16} /> {backLabel}
                    </Link>
                </Button>

                <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                    <div>
            <span className="inline-block bg-indigo-100 text-indigo-700 text-xs font-bold px-2 py-1 rounded mb-2">
              {product.category}
            </span>
                        <h1 className="text-3xl md:text-4xl font-extrabold text-slate-900">
                            {product.genericName}
                        </h1>
                    </div>
                </div>
            </div>

            <div className="space-y-6">
                <h2 className="text-xl font-semibold text-slate-800">Live Price Comparison</h2>
                <ComparisonTable items={product.storeItems} />
            </div>
        </div>
    );
}