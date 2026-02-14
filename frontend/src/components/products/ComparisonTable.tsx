import { useMemo } from "react";
import type { StoreItem } from "@/types";
import { formatCurrency } from "@/lib/formatters";
import { StoreBadge } from "./StoreBadge";
import { ExternalLink, Trophy, Clock, Tag } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table";

interface ComparisonTableProps {
    items: StoreItem[];
}

export function ComparisonTable({ items }: ComparisonTableProps) {
    // 2. Optimization: Only re-sort if 'items' actually changes
    const sortedItems = useMemo(() => {
        return [...items].sort((a, b) => a.price - b.price);
    }, [items]);

    // Handle empty state gracefully
    if (!items.length) return null;

    const lowestPrice = sortedItems[0]?.price;

    return (
        <div className="rounded-xl border border-slate-200 bg-white shadow-sm overflow-hidden">
            <Table>
                <TableHeader className="bg-slate-50">
                    <TableRow>
                        <TableHead className="w-35">Store</TableHead>
                        <TableHead>Product</TableHead>
                        <TableHead className="text-right whitespace-nowrap">Price</TableHead>
                        <TableHead className="text-right w-25 hidden sm:table-cell">Action</TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    {sortedItems.map((item) => {
                        const isCheapest = item.price === lowestPrice;

                        return (
                            <TableRow
                                key={item.id}
                                className={isCheapest ? "bg-green-50/60 hover:bg-green-100/60 transition-colors" : "hover:bg-slate-50 transition-colors"}
                            >
                                {/* STORE COLUMN */}
                                <TableCell className="align-top py-4">
                                    <div className="flex flex-col gap-2">
                                        <StoreBadge storeName={item.store.name} />

                                        {isCheapest && (
                                            <span className="inline-flex items-center gap-1 text-[10px] font-bold text-green-700 uppercase tracking-wider bg-green-100 px-2 py-0.5 rounded-full w-fit">
                                                <Trophy size={10} /> Best Deal
                                            </span>
                                        )}
                                    </div>
                                </TableCell>

                                {/* DETAILS COLUMN */}
                                <TableCell className="align-top py-4">
                                    <div className="flex flex-col gap-1">
                                        <span className="font-semibold text-slate-700 line-clamp-2 leading-tight">
                                            {item.storeSpecificName}
                                        </span>

                                        {/* Meta info - Hidden on mobile to save space */}
                                        <div className="hidden sm:flex flex-col gap-1 mt-1">
                                            <span className="text-xs text-slate-500 flex items-center gap-1.5">
                                                <Tag size={12} /> {item.brand}
                                            </span>
                                            <span className="text-xs text-slate-400 flex items-center gap-1.5">
                                                <Clock size={12} /> {new Date(item.lastUpdated).toLocaleDateString()}
                                            </span>
                                        </div>
                                    </div>
                                </TableCell>

                                {/* PRICE COLUMN */}
                                <TableCell className="text-right align-top py-4">
                                    <div className="flex flex-col items-end">
                                        <span className={`text-lg font-bold tracking-tight ${isCheapest ? "text-green-700" : "text-slate-900"}`}>
                                            {formatCurrency(item.price)}
                                        </span>
                                        <a href={item.productUrl} target="_blank" rel="noreferrer" className="sm:hidden text-xs text-indigo-600 font-medium mt-2 underline">
                                            Visit Site
                                        </a>
                                    </div>
                                </TableCell>

                                <TableCell className="text-right align-top py-4 hidden sm:table-cell">
                                    <Button variant="outline" size="sm" asChild className="gap-2 h-9 text-xs">
                                        <a href={item.productUrl} target="_blank" rel="noreferrer">
                                            Visit Store <ExternalLink size={12} />
                                        </a>
                                    </Button>
                                </TableCell>
                            </TableRow>
                        );
                    })}
                </TableBody>
            </Table>
        </div>
    );
}