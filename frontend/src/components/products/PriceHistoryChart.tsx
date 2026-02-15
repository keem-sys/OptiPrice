import { usePriceHistory } from "@/hooks/useProducts";
import { formatCurrency } from "@/lib/formatters";
import { format } from "date-fns";
import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
    ResponsiveContainer,
} from "recharts";
import { Loader2 } from "lucide-react";

interface Props {
    masterId: string;
}

type ChartDataPoint = {
    date: string;
    [storeName: string]: string | number;
};


const STORE_COLORS: Record<string, string> = {
    "Checkers": "#38A8AE",
    "Shoprite": "#ea212d",
    "Pick n Pay": "#003359",
    "default": "#64748b"
};

export function PriceHistoryChart({ masterId }: Props) {
    const { data, isLoading } = usePriceHistory(masterId);

    if (isLoading) return <div className="flex justify-center p-10"><Loader2 className="animate-spin" /></div>;
    if (!data || data.length === 0) return <div className="text-center p-10 text-slate-500">No history data available yet.</div>;


    const chartData = data.reduce<ChartDataPoint[]>((acc, curr) => {
        const dateKey = format(new Date(curr.date), "d MMM");

        const existingEntry = acc.find((e) => e.date === dateKey);

        if (existingEntry) {
            existingEntry[curr.storeName] = curr.price;
        } else {
            acc.push({ date: dateKey, [curr.storeName]: curr.price });
        }
        return acc;
    }, []);
    const stores = Array.from(new Set(data.map(d => d.storeName)));

    return (
        <div className="h-100 w-full bg-white p-4 rounded-xl border border-slate-200 shadow-sm">
            <h3 className="text-lg font-semibold mb-6 text-slate-800">Price Trend History</h3>

            <ResponsiveContainer width="100%" height="100%">
                <LineChart data={chartData} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                    <XAxis
                        dataKey="date"
                        stroke="#64748b"
                        fontSize={12}
                        tickMargin={10}
                    />
                    <YAxis
                        stroke="#64748b"
                        fontSize={12}
                        tickFormatter={(val) => `R${val}`}
                    />
                    <Tooltip
                        contentStyle={{ borderRadius: "8px", border: "none", boxShadow: "0 4px 6px -1px rgb(0 0 0 / 0.1)" }}
                        formatter={(value: number | undefined) => formatCurrency(value ?? 0)}
                    />
                    <Legend wrapperStyle={{ paddingTop: "20px" }} />

                    {stores.map((store) => (
                        <Line
                            key={store}
                            type="monotone"
                            dataKey={store}
                            stroke={STORE_COLORS[store] || STORE_COLORS["default"]}
                            strokeWidth={3}
                            dot={{ r: 4, strokeWidth: 2 }}
                            activeDot={{ r: 6 }}
                            connectNulls={true}
                        />
                    ))}
                </LineChart>
            </ResponsiveContainer>
        </div>
    );
}