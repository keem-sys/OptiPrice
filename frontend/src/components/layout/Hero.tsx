import {cn} from "@/lib/utils.ts";
import {Sparkles} from "lucide-react";
import React from "react";

interface HeroProps {
    compact?: boolean;
    children: React.ReactNode;
}

export function Hero({ compact = false, children }: HeroProps) {
    return (
        <section className={cn(
            "relative overflow-hidden transition-all duration-700 px-4",
            compact ? "pt-4 pb-6" : "pt-16 pb-12 md:pt-24 md:pb-20"
        )}>
            <div className="container mx-auto max-w-4xl text-center">

                {!compact && (
                    <div className="animate-in fade-in slide-in-from-top-4 duration-500">
                        <div className="inline-flex items-center rounded-full border border-indigo-100 bg-indigo-50 px-3 py-1 text-sm font-medium text-indigo-600 mb-8">
                            <Sparkles className="mr-2 h-3.5 w-3.5" />
                            <span>New Optimized Matching</span>
                        </div>
                        <h1 className="text-4xl font-extrabold text-slate-900 sm:text-6xl mb-6">
                            Compare Groceries with <br />
                            <span className="text-indigo-600">Precision & Speed</span>
                        </h1>
                        <p className="mx-auto max-w-2xl text-lg text-slate-600 mb-10">
                            Stop overpaying. Instantly compare prices across Shoprite, Checkers, and Pick n Pay.
                        </p>
                    </div>
                )}

                <div className={cn("mx-auto", compact ? "max-w-4xl" : "max-w-2xl")}>
                    {children}
                </div>
            </div>
        </section>
    );
}