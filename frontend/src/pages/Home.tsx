import { Search } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";

export default function Home() {
    return (
        <div className="flex flex-col items-center justify-center pt-24 pb-12 px-4">
            <div className="text-center max-w-3xl">
                <h1 className="text-5xl md:text-6xl font-extrabold tracking-tight mb-6">
                    Compare Prices with <span className="text-primary">AI Precision</span>
                </h1>
                <p className="text-xl text-muted-foreground mb-10">
                    Real-time price aggregation across South Africa's leading retailers.
                </p>
            </div>

            <div className="flex w-full max-w-2xl items-center space-x-2">
                <div className="relative flex-grow">
                    <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground" size={18} />
                    <Input
                        type="text"
                        placeholder="Search for groceries (e.g. Milk, Bread, Clover)..."
                        className="pl-10 py-6 text-lg rounded-full shadow-lg border-slate-200 focus-visible:ring-primary"
                    />
                </div>
                <Button size="lg" className="rounded-full px-8 py-6 text-lg font-bold shadow-lg">
                    Search
                </Button>
            </div>
        </div>
    );
}