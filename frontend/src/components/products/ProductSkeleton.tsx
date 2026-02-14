import { Skeleton } from "@/components/ui/skeleton";

export function ProductSkeleton() {
    return (
        <div className="flex flex-col space-y-3 rounded-xl border border-slate-200 bg-white p-4 shadow-sm">
            <Skeleton className="h-48 w-full rounded-lg bg-slate-200" />
            <div className="space-y-2 pt-4">
                <Skeleton className="h-4 w-3/4 bg-slate-200" />
                <Skeleton className="h-4 w-1/2 bg-slate-200" />
            </div>
            <div className="flex justify-between pt-4">
                <Skeleton className="h-10 w-20 bg-slate-200" />
                <Skeleton className="h-10 w-24 bg-slate-200" />
            </div>
        </div>
    );
}