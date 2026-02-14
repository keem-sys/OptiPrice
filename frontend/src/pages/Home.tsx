import {useSearchParams} from "react-router-dom";
import {Hero} from "@/components/layout/Hero";
import {ProductGrid} from "@/components/products/ProductGrid";
import {useSearchProducts} from "@/hooks/useProducts";

export default function Home() {

    const [searchParams, setSearchParams] = useSearchParams();
    const currentSearchTerm = searchParams.get("q") || "";

    const {
        data: results = [],
        isLoading,
        isError,
        error
    } = useSearchProducts(currentSearchTerm);

    const handleSearch = (query: string) => {
        setSearchParams({q: query});
    };

    return (
        <div className="min-h-screen bg-slate-50 pb-20">
            <Hero
                onSearch={handleSearch}
                loading={isLoading}
                initialValue={currentSearchTerm}
            />

            <div className="container mx-auto px-4">
                {isError && (
                    <div className="p-4 mb-8 text-sm text-red-700 bg-red-100 rounded-lg max-w-2xl mx-auto text-center">
                        <span className="font-medium">Error:</span> {(error as Error).message}
                    </div>
                )}

                <ProductGrid products={results} loading={isLoading} />

                {!isLoading && !isError && currentSearchTerm && results.length === 0 && (
                    <div className="text-center py-10 text-slate-500">
                        <p className="text-lg">No products found for "{currentSearchTerm}".</p>
                    </div>
                )}
            </div>
        </div>
    );
}