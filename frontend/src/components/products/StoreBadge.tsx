import { Badge } from "@/components/ui/badge";

interface StoreBadgeProps {
    storeName: string;
}

export function StoreBadge({ storeName }: StoreBadgeProps) {
    const name = storeName.toLowerCase();

    let bgClass = "bg-slate-500 hover:bg-slate-600";

    if (name.includes("checkers")) {
        bgClass = "bg-brand-checkers hover:bg-[#2d868b]";
    } else if (name.includes("shoprite")) {
        bgClass = "bg-brand-shoprite hover:bg-[#c41b25]";
    } else if (name.includes("pick")) {
        bgClass = "bg-brand-pnp hover:bg-[#00223d]";
    }

    return (
        <Badge className={`${bgClass} text-white border-0 px-3 py-1 text-xs font-bold uppercase tracking-wide`}>
            {storeName}
        </Badge>
    );
}